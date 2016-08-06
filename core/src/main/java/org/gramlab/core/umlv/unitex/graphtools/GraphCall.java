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
package org.gramlab.core.umlv.unitex.graphtools;

import java.io.File;

public class GraphCall implements Comparable<GraphCall> {

	private final File grf;
	/*
	 * useful is true when the graph call is in a graph box that is both
	 * accessible and coaccessible
	 */
	private boolean useful;
	/*
	 * direct is true when the graph is called from the main graph on which the
	 * dependency request was made
	 */
	private final boolean direct;

	public GraphCall(File grf, boolean useful, boolean direct) {
		this.grf = grf;
		this.useful = useful;
		this.direct = direct;
	}

	public GraphCall(File grf) {
		this(grf, true, true);
	}

	public File getGrf() {
		return grf;
	}

	public boolean isUseful() {
		return useful;
	}

	public boolean isDirect() {
		return direct;
	}

	@Override
	public int compareTo(GraphCall c) {
		if (c == null)
			return -1;
		final int n = grf.compareTo(c.getGrf());
		if (n != 0)
			return n;
		if (useful && !c.isUseful())
			return -1;
		if (!useful && c.isUseful())
			return 1;
		if (direct && !c.isDirect())
			return -1;
		if (!direct && c.isDirect())
			return 1;
		return 0;
	}

	public void setUseful(boolean b) {
		useful = b;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GraphCall))
			return false;
		final GraphCall c = (GraphCall) obj;
		return 0 == compareTo(c);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
