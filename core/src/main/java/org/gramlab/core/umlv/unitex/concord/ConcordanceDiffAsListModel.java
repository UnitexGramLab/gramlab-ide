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
package org.gramlab.core.umlv.unitex.concord;

import java.awt.Color;

import org.gramlab.core.umlv.unitex.text.Interval;

/**
 * This is a model for representing an HTML concordance file as the list of its
 * paragraphs. Paragraphs are delimited by new lines. It uses a mapped file to
 * avoid to store large data in memory.
 * 
 * @author Sébastien Paumier
 */
public class ConcordanceDiffAsListModel extends ConcordanceAsListModel {
	/**
	 * An HTML concordance diff file always starts with a header of
	 * HTML_START_LINES lines, then there are the real concordance lines, then
	 * there are HTML_END_LINES that close open HTML tags.
	 */
	private static final int DIFF_HTML_START_LINES = 17;
	private static final int DIFF_HTML_END_LINES = 3;

	public ConcordanceDiffAsListModel() {
		super(DIFF_HTML_START_LINES, DIFF_HTML_END_LINES);
	}

	/**
	 * Returns the text corresponding to the diff line #i.
	 */
	@Override
	public Object getElementAt(int i) {
		final Interval interval = getInterval(i + HTML_START_LINES);
		final int start = interval.getStartInBytes() + 55; // we don't want
															// neither the
															// <tr><td nowrap
															// bgcolor="#90EE90"><font
															// color="#008000">
		final int end = interval.getEndInBytes() - 19; // nor the
														// </font></td></tr>
		final byte[] tmp = new byte[end - start + 1];
		int z = 0;
		buffer.position(start);
		for (int pos = start; pos <= end; pos++) {
			tmp[z++] = buffer.get();
		}
		return new String(tmp, utf8);
	}

	static class DiffLine {
		String line1;
		String line2;
		Color color1;
		Color color2;

		DiffLine(String s1, Color c1, String s2, Color c2) {
			line1 = s1;
			color1 = c1;
			line2 = s2;
			color2 = c2;
		}

		static DiffLine buildDiffLine(String line) {
			final DiffLine result = new DiffLine(null, null, null, null);
			int pos = line.indexOf('"');
			String tmp = line.substring(0, pos);
			if ("green".equals(tmp))
				result.color1 = Color.GREEN;
			else if ("red".equals(tmp))
				result.color1 = Color.RED;
			else if ("blue".equals(tmp))
				result.color1 = Color.BLUE;
			else if ("orange".equals(tmp))
				result.color1 = Color.ORANGE;
			else
				return null;
			int start = line.indexOf('>', pos) + 1;
			pos = line.indexOf('<', start) + 1;
			if (line.charAt(pos) == 'u') {
				/* If there is a non empty line */
				pos = line.indexOf('<', pos) + 1;
				pos = line.indexOf('<', pos);
				result.line1 = line.substring(start, pos);
			}
			pos = line.indexOf('=', pos) + 1;
			start = line.indexOf('=', pos) + 2;
			pos = line.indexOf('"', start);
			tmp = line.substring(start, pos);
			if ("green".equals(tmp))
				result.color2 = Color.GREEN;
			else if ("red".equals(tmp))
				result.color2 = Color.RED;
			else if ("blue".equals(tmp))
				result.color2 = Color.BLUE;
			else if ("orange".equals(tmp))
				result.color2 = Color.ORANGE;
			else
				return null;
			start = line.indexOf('>', pos) + 1;
			pos = line.indexOf('<', start) + 1;
			if (line.charAt(pos) == 'u') {
				/* If there is a non empty line */
				pos = line.indexOf('<', pos) + 1;
				pos = line.indexOf('<', pos);
				result.line2 = line.substring(start, pos);
			}
			return result;
		}
	}
}
