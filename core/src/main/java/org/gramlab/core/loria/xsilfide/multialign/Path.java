/*
 * XAlign
 *
 * Copyright (C) LORIA
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
/*
 * @(#)       Path.java
 * 
 * 
 * 
 * 
 * @version   
 * @author    Patrice Bonhomme
 * Copyright  1997 (C) PATRICE BONHOMME
 *            CRIN/CNRS & INRIA Lorraine
 *
 */
package org.gramlab.core.loria.xsilfide.multialign;

import java.util.Vector;

public class Path {
	private final Vector<Object> path; // set of points

	public Path(@SuppressWarnings("unused") int x,
			@SuppressWarnings("unused") int y, int size) {
		path = new Vector<Object>(size);
	}

	public int getNumberOfPoint() {
		return (this.path.size());
	}

	public void setPointAt(Point p, int at) {
		path.insertElementAt(p, at);
	}

	public void setPointAt(int i, int j, int at) {
		Point point;
		point = new Point(i, j);
		this.setPointAt(point, at);
	}

	public Point getPointAt(int at) {
		return ((Point) path.elementAt(at));
	}

	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < this.path.size(); i++) {
			s = s + this.getPointAt(i).toString();
		}
		return (s);
	}
}
// EOF
