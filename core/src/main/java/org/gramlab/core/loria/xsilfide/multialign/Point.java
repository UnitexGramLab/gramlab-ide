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
 * @(#)       Point.java
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
package fr.loria.xsilfide.multialign;

public class Point {
	public int x;
	public int y;

	public Point() {
		this(0, 0);
	}

	public Point(Point p) {
		this(p.x, p.y);
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "[x=" + x + ",y=" + y + "]";
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int a) {
		x = a;
	}

	public void setY(int b) {
		y = b;
	}
}
// EOF
