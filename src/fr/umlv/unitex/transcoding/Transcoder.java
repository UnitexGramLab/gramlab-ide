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
package fr.umlv.unitex.transcoding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import fr.umlv.unitex.process.commands.ConvertCommand;

public class Transcoder {
	/**
	 * Gives an encoding value that can be used as a parameter for the
	 * <code>Convert</code> program.
	 * <p/>
	 * Example: "Russian" => "windows-1251"
	 * 
	 * @param language
	 * @return a <code>String</code> that represents the encoding value for the
	 *         given language
	 */
	public static String getEncodingForLanguage(String language) {
		if (language.equals("Portuguese (Brazil)")
				|| language.equals("Portuguese (Portugal)"))
			return "PORTUGUESE";
		else if (language.equals("English"))
			return "ENGLISH";
		else if (language.equals("Finnish"))
			return "iso-8859-1";
		else if (language.equals("French"))
			return "FRENCH";
		else if (language.equals("German"))
			return "GERMAN";
		else if (language.equals("Greek (Modern)"))
			return "GREEK";
		else if (language.equals("Italian"))
			return "ITALIAN";
		else if (language.equals("Norwegian"))
			return "NORWEGIAN";
		else if (language.equals("Russian"))
			return "windows-1251";
		else if (language.equals("Spanish"))
			return "SPANISH";
		else if (language.equals("Thai"))
			return "THAI";
		else {
			// by default, we chose the latin1 codepage
			return "LATIN1";
		}
	}

	public static boolean isValidEncoding(String s) {
		final String[] tab = getAvailableEncodings();
		for (final String aTab : tab) {
			if (aTab.equalsIgnoreCase(s))
				return true;
		}
		return false;
	}

	/**
	 * @return a String array containing all the encodings supported by the
	 *         Convert program.
	 */
	private static String[] encodings;

	public static String[] getAvailableEncodings() {
		if (encodings != null) {
			return encodings;
		}
		final ConvertCommand cmd = new ConvertCommand().getEncodings();
		final String[] comm = cmd.getCommandArguments(true);
		final ArrayList<String> lines = new ArrayList<String>();
		try {
			final Process p = Runtime.getRuntime().exec(comm);
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(p.getInputStream(), "UTF8"));
			String s;
			while ((s = myReadLine(reader)) != null) {
				while (s.endsWith("\n") || s.endsWith("\r")) {
					s = s.substring(0, s.length() - 1);
				}
				if ("".equals(s)) {
					continue;
				}
				lines.add(s.toUpperCase());
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		Collections.sort(lines);
		encodings = new String[lines.size()];
		encodings = lines.toArray(encodings);
		return encodings;
	}

	private static char nextChar = '\0';

	private static String myReadLine(BufferedReader reader) {
		int c;
		String result = "";
		if (nextChar != '\0') {
			result = "" + nextChar;
			nextChar = '\0';
		}
		try {
			while ((c = reader.read()) != -1) {
				char ch = (char) c;
				if (ch == '\r') {
					// result=result+ch;
					if ((c = reader.read()) != -1) {
						ch = (char) c;
						if (ch == '\n') {
							/* If we have a \r\n sequence, we return it */
							return result;
						}
						/* Otherwise, we stock the character */
						nextChar = ch;
					}
					return result;
				} else if (ch == '\n') {
					/* If we have a single \n, we return it */
					// result=result+ch;
					nextChar = '\0';
					return result;
				} else {
					nextChar = '\0';
					result = result + ch;
				}
			}
			return null;
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
