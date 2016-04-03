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
import java.util.regex.Pattern;

/**
 * This class represent a regex filter to be applied on tfst tags.
 * 
 * @author paumier
 */
public class TagFilter {
	private Pattern pattern;
	private boolean alwaysShowGramCode;
	private boolean onlyShowGramCode;

	public void setFilter(Pattern pattern, boolean alwaysShowGramCode,
			boolean onlyShowGramCode) {
		this.pattern = pattern;
		this.alwaysShowGramCode = alwaysShowGramCode;
		this.onlyShowGramCode = onlyShowGramCode;
		if (onlyShowGramCode && pattern != null) {
			throw new IllegalArgumentException(
					"Should not a non null pattern when onlyShowGramCode is true");
		}
		fireFilterChanged();
	}

	public Pattern getPattern() {
		return pattern;
	}

	public boolean alwaysShowGramCode() {
		return alwaysShowGramCode;
	}

	public boolean onlyShowGramCode() {
		return onlyShowGramCode;
	}

	private final ArrayList<FilterListener> listeners = new ArrayList<FilterListener>();
	private boolean firing = false;

	public void addFilterListener(FilterListener l) {
		listeners.add(l);
	}

	public void removeFilterListener(FilterListener l) {
		if (firing) {
			throw new IllegalStateException(
					"Should not remove a listener while firing");
		}
		listeners.remove(l);
	}

	void fireFilterChanged() {
		firing = true;
		try {
			for (final FilterListener l : listeners) {
				l.filterChanged();
			}
		} finally {
			firing = false;
		}
	}
}
