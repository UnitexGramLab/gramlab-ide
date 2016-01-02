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
package fr.umlv.unitex.tfst;

import java.util.ArrayList;

public class TokenTags {
	/*
	 * Bounds of the token sequence in the sentence (a MWU can be represented by
	 * several tokens)
	 */
	private final int start;
	private final int end;
	/* The token itself */
	private final String content;

	public TokenTags(int start, int end, String content) {
		this.start = start;
		this.end = end;
		this.content = content;
	}

	/* Unfiltered interpretation list */
	private final ArrayList<ArrayList<Tag>> interpretations = new ArrayList<ArrayList<Tag>>();

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public String getContent() {
		return content;
	}

	private final ArrayList<String> filteredInterpretations = new ArrayList<String>();

	public int getInterpretationCount() {
		return filteredInterpretations.size();
	}

	public String getInterpretation(int i) {
		return filteredInterpretations.get(i);
	}

	public void refreshFilter(TagFilter f, boolean delafStyle) {
		filteredInterpretations.clear();
		for (final ArrayList<Tag> interpretation : interpretations) {
			final String s = getFilteredInterpretation(interpretation, f,
					delafStyle);
			if (s != null && !"".equals(s)
					&& !filteredInterpretations.contains(s)) {
				filteredInterpretations.add(s);
			}
		}
	}

	private String getFilteredInterpretation(ArrayList<Tag> l, TagFilter f,
			boolean delafStyle) {
		final StringBuilder b = new StringBuilder();
		for (int i = 0; i < l.size(); i++) {
			final Tag tag = l.get(i);
			final String s = tag.toString(f, delafStyle);
			if (s == null)
				return null;
			final boolean isTag = (s.contains(",") && s.contains("."));
			if (i > 0)
				b.append(delafStyle ? " " : "+");
			if (delafStyle && isTag)
				b.append("{");
			b.append(s);
			if (delafStyle && isTag)
				b.append("}");
		}
		return b.toString();
	}

	public void addInterpretation(ArrayList<Tag> interpretation) {
		interpretations.add(interpretation);
	}

	@Override
	public String toString() {
		final StringBuilder b = new StringBuilder();
		final int n = getInterpretationCount();
		if (n > 1)
			b.append("(");
		for (int i = 0; i < n; i++) {
			if (i > 0)
				b.append("|");
			b.append(getInterpretation(i));
		}
		if (n > 1)
			b.append(")");
		return b.toString();
	}
}
