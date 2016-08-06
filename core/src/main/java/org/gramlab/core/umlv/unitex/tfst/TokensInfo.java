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
package org.gramlab.core.umlv.unitex.tfst;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import org.gramlab.core.umlv.unitex.io.Encoding;

public class TokensInfo {
	private static int[] info;
	private static ArrayList<String> tokens = new ArrayList<String>();

	public static int getToken(int n) {
		return info[2 * n];
	}

	public static int getTokenLength(int n) {
		return info[2 * n + 1];
	}

	public static void loadTokensInfo(File f, String sentence) {
		final Scanner scanner = Encoding.getScanner(f);
		final ArrayList<Integer> l = new ArrayList<Integer>();
		tokens.clear();
		int currentPos = 0;
		while (scanner.hasNextInt()) {
			int n = scanner.nextInt();
			if (n < 0) {
				throw new AssertionError("Negative token number: " + n);
			}
			/* Reading token number */
			l.add(n);
			if (!scanner.hasNextInt()) {
				throw new AssertionError("Invalid token info file");
			}
			/* Reading token length */
			n = scanner.nextInt();
			if (n < -1) {
				throw new AssertionError("Invalid token bound: " + n
						+ " ; should be >=-1");
			}
			l.add(n);
			/* Then we compute the token itself */
			tokens.add(sentence.substring(currentPos, currentPos + n));
			currentPos = currentPos + n;
		}
		if (scanner.hasNext()) {
			throw new AssertionError(
					"Invalid token info file: unexpected remaining token <"
							+ scanner.next() + ">");
		}
		scanner.close();
		final int size = l.size();
		info = new int[size];
		for (int i = 0; i < size; i++) {
			info[i] = l.get(i);
		}
		if (currentPos != sentence.length()) {
			throw new IllegalStateException("Inconsistency in sentence tokens");
		}
	}

	public static String getTokenSequence(int start, int end) {
		final StringBuilder b = new StringBuilder();
		for (int i = start; i <= end; i++) {
			b.append(tokens.get(i));
		}
		return b.toString();
	}

	public static int getTokenCount() {
		return tokens.size();
	}

	public static String getTokenAsString(int n) {
		return tokens.get(n);
	}

	private static int[] infoBackup;
	private static ArrayList<String> tokensBackup;

	public static void save() {
		infoBackup = info;
		info = null;
		tokensBackup = tokens;
		tokens = new ArrayList<String>();
	}

	public static void restore() {
		info = infoBackup;
		infoBackup = null;
		tokens = tokensBackup;
		tokensBackup = null;
	}
}
