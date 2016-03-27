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
package fr.umlv.unitex.diff;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Stroke;

public class GraphDecoratorConfig {
	public static final Color ADDED = Color.GREEN;
	public static final Color REMOVED = Color.RED;
	public static final Color MOVED = new Color(0xC4, 0x4F, 0xD0);
	public static final Color CONTENT_CHANGED = Color.ORANGE;
	public static final Color DEBUG_HIGHLIGHT = Color.GREEN;
	public static final Color OUTPUT_HIGHLIGHTED = DEBUG_HIGHLIGHT;
	/* Values used for tagging the text automaton */
	public static final float SHADE_ALPHA = 0.2f;
	public static final Color SHADED = new Color(0f, 0f, 0f, SHADE_ALPHA);
	public static final Composite SHADE_COMPOSITE = AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, SHADE_ALPHA);
	public static final Color LINEAR_TFST = new Color(0xFC, 0xE4, 0x00);
	public static final Stroke STROKE = new BasicStroke(3);
}
