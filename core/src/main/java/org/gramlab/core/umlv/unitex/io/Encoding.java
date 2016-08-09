/*
 * Unitex
 *
 * Copyright (C) 2001-2016 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 *
 */
package org.gramlab.core.umlv.unitex.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Scanner;

import org.gramlab.core.umlv.unitex.config.Preferences;

public enum Encoding {
	UTF16LE {
		@Override
		public String getCharset() {
			return "UTF-16LE";
		}

		@Override
		public char readChar(ByteBuffer parseBuffer) {
			final int a = 0xFF & parseBuffer.get();
			final int b = 0xFF & parseBuffer.get();
			final char c = (char) (b << 8 | a);
			return c;
		}
	},
	UTF16BE {
		@Override
		public String getCharset() {
			return "UTF-16BE";
		}

		@Override
		public char readChar(ByteBuffer parseBuffer) {
			final int b = 0xFF & parseBuffer.get();
			final int a = 0xFF & parseBuffer.get();
			final char c = (char) (b << 8 | a);
			return c;
		}
	},
	UTF8 {
		@Override
		public String getCharset() {
			return "UTF-8";
		}

		@Override
		public boolean isValidEncoding(File f) {
			FileInputStream stream;
			try {
				stream = new FileInputStream(f);
			} catch (final FileNotFoundException e) {
				return false;
			}
			InputStreamReader reader;
			try {
				reader = new InputStreamReader(stream, getCharset());
			} catch (final UnsupportedEncodingException e) {
				return false;
			}
			int c = 0;
			int i = 0;
			try {
				while ((c = reader.read()) != -1 && ++i < 4096) {
					if (c == 65533) {
						/* Decoding error */
						reader.close();
						stream.close();
						return false;
					}
					if (c > 128) {
						/* We stop on the first valid utf8 multi-byte char */
						break;
					}
				}
				reader.close();
				stream.close();
			} catch (final IOException e) {
				return false;
			}
			return true;
		}

		@Override
		public char readChar(ByteBuffer buffer) {
			int c = buffer.get() & 0xFF;
			if (c <= 0x7F) {
				/* Case of a 1 byte character 0XXX XXXX */
				return (char) c;
			}
			/* Case of a character encoded on several bytes */
			int number_of_bytes;
			int value;
			if ((c & 0xE0) == 0xC0) {
				/* 2 bytes 110X XXXX */
				value = c & 31;
				number_of_bytes = 2;
			} else if ((c & 0xF0) == 0xE0) {
				/* 3 bytes 1110X XXXX */
				value = c & 15;
				number_of_bytes = 3;
			} else if ((c & 0xF8) == 0xF0) {
				/* 4 bytes 1111 0XXX */
				value = c & 7;
				number_of_bytes = 4;
			} else if ((c & 0xFC) == 0xF8) {
				/* 5 bytes 1111 10XX */
				value = c & 3;
				number_of_bytes = 5;
			} else if ((c & 0xFE) == 0xFC) {
				/* 6 bytes 1111 110X */
				value = c & 1;
				number_of_bytes = 6;
			} else {
				System.err
						.println("Encoding error in first byte of a unicode sequence\n");
				return '?';
			}
			/*
			 * If there are several bytes, we read them and compute the unicode
			 * number of the character
			 */
			for (int i = 1; i < number_of_bytes; i++) {
				c = buffer.get() & 0xFF;
				/* Following bytes should be of the form 10XX XXXX */
				if ((c & 0xC0) != 0x80) {
					System.err.println("Encoding error in byte " + (i + 1)
							+ " of a " + number_of_bytes
							+ " byte unicode sequence\n");
					return '?';
				}
				value = (value << 6) | (c & 0x3F);
			}
			return (char) value;
		}
	};
	public abstract String getCharset();

	/**
	 * Returns a Scanner, ready to work, the BOM having been read if any.
	 */
	public static Scanner getScanner(File f) {
		return new Scanner(getInputStreamReader(f));
	}

	/**
	 * 
	 * Valid UTF16LE and UTF16BE encodings should include the BOM. UTF8 files
	 * should not.
	 */
	public boolean isValidEncoding(File f) {
		if (f.length() < 2 || f.length() % 2 != 0)
			return false;
		FileInputStream stream;
		try {
			stream = new FileInputStream(f);
		} catch (final FileNotFoundException e) {
			return false;
		}
		InputStreamReader reader;
		try {
			reader = new InputStreamReader(stream, getCharset());
		} catch (final UnsupportedEncodingException e) {
			return false;
		}
		int c = 0;
		try {
			c = reader.read();
			reader.close();
			stream.close();
		} catch (final IOException e) {
			return false;
		}
		return c == 0xFEFF;
	}

	public static Encoding getEncoding(File f) {
		if (UTF16LE.isValidEncoding(f))
			return UTF16LE;
		if (UTF16BE.isValidEncoding(f))
			return UTF16BE;
		if (UTF8.isValidEncoding(f))
			return UTF8;
		return null;
	}

	/**
	 * Returns a reader for the given file. For UTF16 encodings, we skip the
	 * BOM.
	 */
	public static InputStreamReader getInputStreamReader(File f) {
		final Encoding e = getEncoding(f);
		if (e == null)
			return null;
		try {
			final InputStreamReader r = new InputStreamReader(
					new FileInputStream(f), e.getCharset());
			if (e == UTF16LE || e == UTF16BE) {
				if (r.read() != 0xFEFF) {
					/* Should not happen */
					return null;
				}
			}
			return r;
		} catch (final UnsupportedEncodingException e1) {
			return null;
		} catch (final FileNotFoundException e1) {
			return null;
		} catch (final IOException e1) {
			return null;
		}
	}

	/**
	 * Returns a writer for the given file. For UTF16 encodings, we write the
	 * BOM.
	 */
	public OutputStreamWriter getOutputStreamWriter(File f) {
		try {
			final OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(f), getCharset());
			if (this == UTF16LE || this == UTF16BE) {
				try {
					writer.write(0xFEFF);
				} catch (final IOException e1) {
					return null;
				}
			}
			return writer;
		} catch (final UnsupportedEncodingException e1) {
			return null;
		} catch (final FileNotFoundException e1) {
			return null;
		}
	}

	public abstract char readChar(ByteBuffer parseBuffer);

	/**
	 * Returns the content of the given file as a String, or null if the size is
	 * >=Preferences.MAX_TEXT_FILE_SIZE
	 */
	public static String getContent(File file) {
		if (file.length() >= Preferences.MAX_TEXT_FILE_SIZE) {
			return null;
		}
		final StringBuilder builder = new StringBuilder();
		final InputStreamReader reader = getInputStreamReader(file);
		if (reader == null) {
			return null;
		}
		int c;
		try {
			while ((c = reader.read()) != -1) {
				builder.append((char) c);
			}
			reader.close();
		} catch (final IOException e) {
			return null;
		} catch (final OutOfMemoryError e) {
			return null;
		}
		return builder.toString();
	}
}
