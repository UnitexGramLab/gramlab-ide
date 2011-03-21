/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

package fr.umlv.unitex.diff;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

public class DiffColors {

	public static final Color ADDED=Color.GREEN;
	public static final Color REMOVED=Color.RED;
	public static final Color MOVED=new Color(0xC4, 0x4F, 0xD0);
	public static final Color CONTENT_CHANGED=Color.ORANGE;
	
	public static final Stroke DIFF_STROKE=new BasicStroke(3);

}
