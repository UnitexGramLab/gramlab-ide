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
package org.gramlab.core.umlv.unitex.graphrendering;

import java.util.Vector;

/**
 * This class is used to store information on a box. It is used for doing
 * multiple box selections.
 * 
 * @author Sébastien Paumier
 */
class GraphBoxInfo {
	/**
	 * X coordinate of the box
	 */
	public int X;
	/**
	 * Y coordinate of the box
	 */
	public int Y;
	/**
	 * Text content of the box
	 */
	public String content;
	/**
	 * Vector containing integers that are reference to boxes that can be
	 * reached from this box
	 */
	public Vector<Integer> reachableBoxes;
}
