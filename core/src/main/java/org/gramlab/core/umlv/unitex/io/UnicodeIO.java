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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * This class provides methods for loading and saving UTF-16LE texts.
 * 
 * @author Sébastien Paumier
 */
public class UnicodeIO {
	/**
	 * Reads a character from a file, considering <code>0x0D</code> and
	 * <code>0x0A</code> as two distinct characters
	 */
	private static int readCharRawly(InputStreamReader r) {
		try {
			return r.read();
		} catch (final IOException e) {
			return -1;
		}
	}

	/**
	 * Reads a character from a file. If the character is <code>0x0D</code>,
	 * then the following <code>0x0A</code> character is skipped.
	 */
	public static int readChar(InputStreamReader r) {
		int c = readCharRawly(r);
		if (c == -1)
			return -1;
		if (c == 0x0d) {
			readCharRawly(r);
			if (c == -1)
				return -1;
			c = '\n';
		}
		return c;
	}

	/**
	 * Reads a line from a file. The \n, if any, is not put in the result.
	 * Returns null at the end of file.
	 */
	public static String readLine(InputStreamReader r) {
		int c;
		String s = "";
		while ((c = readChar(r)) != '\n' && c != -1) {
			s = s + (char) c;
		}
		if (c == -1 && s.equals(""))
			return null;
		return s;
	}

	/**
	 * Reads the first line of a file
	 */
	public static String readFirstLine(File f) {
		if (!f.exists()) {
			return null;
		}
		if (!f.canRead()) {
			return null;
		}
		final InputStreamReader r = Encoding.getInputStreamReader(f);
		if (r == null)
			return null;
		final String line = readLine(r);
		try {
			r.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return line;
	}

	/**
	 * Skips characters from a file
	 */
	public static void skipChars(InputStreamReader r, int n) {
		for (int i = 0; i < n; i++)
			readChar(r);
	}

	/**
	 * Skips a line in a file
	 */
	public static void skipLine(InputStreamReader r) {
		int c;
		while ((c = readChar(r)) != '\n' && c != -1) {/**/
		}
	}

	/**
	 * Tests if a character is a digit
	 * 
	 * @param c
	 *            the character to test
	 * @return <code>true</code> if <code>c</code> is a digit,
	 *         <code>false</code> otherwise
	 */
	public static boolean isDigit(char c) {
		return (c >= '0' && c <= '9');
	}

	/**
	 * Writes a character to a file
	 */
	public static void writeChar(OutputStreamWriter w, char c) {
		try {
			if (c == '\n') {
				w.write(0x0D);
			}
			w.write(c);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes a string to a file
	 */
	public static void writeString(OutputStreamWriter w, String s) {
		final int L = s.length();
		for (int i = 0; i < L; i++)
			writeChar(w, s.charAt(i));
	}
	/**
	 * @param f
	 *            a file
	 * @return true if f is a Unicode Little-Endian file
	 * @throws FileNotFoundException
	 *             if the file cannot be opened
	 */
	/*
	 * public static boolean isAUnicodeLittleEndianFile(File f) throws
	 * FileNotFoundException { FileInputStream stream = new FileInputStream(f);
	 * boolean res = (UnicodeIO.readChar(stream) == 0xFEFF); try {
	 * stream.close(); } catch (IOException e) { e.printStackTrace(); } return
	 * res; }
	 */
	/**
	 * Opens and returns a file input stream.
	 * 
	 * @param f
	 *            the file
	 * @return the input stream
	 * @throws NotAUnicodeLittleEndianFileException
	 *             if the file is not a Unicode Little Endian one
	 * @throws FileNotFoundException
	 *             if the file cannot be opened
	 */
	/*
	 * public static FileInputStream openUnicodeLittleEndianFileInputStream(File
	 * f) throws NotAUnicodeLittleEndianFileException, FileNotFoundException {
	 * FileInputStream stream = new FileInputStream(f); if
	 * (UnicodeIO.readChar(stream) != 0xFEFF) { try { stream.close(); } catch
	 * (IOException e) { e.printStackTrace(); } throw new
	 * NotAUnicodeLittleEndianFileException(f); } return stream; }
	 * 
	 * /** Opens and returns a file output stream.
	 * 
	 * @param f the file
	 * 
	 * @return the output stream
	 * 
	 * @throws FileNotFoundException if the file cannot be opened
	 */
	/*
	 * public static FileOutputStream openUnicodeLittleEndianFileOutputStream(
	 * File f) throws FileNotFoundException { FileOutputStream stream = new
	 * FileOutputStream(f); UnicodeIO.writeChar(stream, (char) 0xFEFF); return
	 * stream; }
	 */
}
