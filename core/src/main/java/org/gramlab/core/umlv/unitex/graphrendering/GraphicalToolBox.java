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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;

/**
 * @author Sébastien Paumier
 */
class GraphicalToolBox {
	private static final Ellipse2D.Double ellipse = new Ellipse2D.Double();
	private static final Line2D.Double line = new Line2D.Double();
	private static final Rectangle rectangle = new Rectangle();
	private static final QuadCurve2D.Double curve = new QuadCurve2D.Double();
	private static final Arc2D.Double arc = new Arc2D.Double();
	public static final Stroke frameStroke = new BasicStroke(2);

	public static void drawLine(Graphics2D g, int x1, int y1, int x2, int y2) {
		line.setLine(x1, y1, x2, y2);
		g.draw(line);
	}

	public static void drawRect(Graphics2D g, int x1, int y1, int w, int h) {
		// Note: the following code produced a bug, because it drawed:
		//
		// *******
		// *
		// *
		//
		// instead of:
		//
		// *******
		// * *
		// * *
		// *******
		//
		// This bug was dued to a bug in my graphic controller pilot.
		// S. Paumier
		rectangle.setBounds(x1, y1, w, h);
		g.draw(rectangle);
	}

	public static void fillRect(Graphics2D g, int x1, int y1, int w, int h) {
		rectangle.setBounds(x1, y1, w, h);
		g.fill(rectangle);
	}

	public static void drawArc(Graphics2D g, int x1, int y1, int w, int h,
			int startAngle, int arctAngle) {
		arc.setArc(x1, y1, w, h, startAngle, arctAngle, Arc2D.OPEN);
		g.draw(arc);
	}

	public static void drawCurve(Graphics2D g, int x1, int y1, int x2, int y2,
			int x3, int y3) {
		curve.setCurve(x1, y1, x2, y2, x3, y3);
		g.draw(curve);
	}

	public static void drawEllipse(Graphics2D g, int x1, int y1, int w, int h) {
		ellipse.setFrame(x1, y1, w, h);
		g.draw(ellipse);
	}

	public static void fillEllipse(Graphics2D g, int x1, int y1, int w, int h) {
		ellipse.setFrame(x1, y1, w, h);
		g.fill(ellipse);
	}
}
