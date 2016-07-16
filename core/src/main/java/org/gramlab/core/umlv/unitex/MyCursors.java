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
package org.gramlab.core.umlv.unitex;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

/**
 * This class defines personal mouse cursors and icons.
 * 
 * @author Sébastien Paumier
 * 
 */
public class MyCursors {
	public static final int NORMAL = 0;
	public static final int CREATE_BOXES = 3;
	public static final int KILL_BOXES = 4;
	public static final int LINK_BOXES = 5;
	public static final int REVERSE_LINK_BOXES = 6;
	public static final int OPEN_SUBGRAPH = 8;
	/**
	 * Initializes all cursors and icons.
	 * 
	 */
	public static ImageIcon diffIcon = new ImageIcon(
			MyCursors.class.getResource("diff.png"));
	public static ImageIcon calledGrfIcon = new ImageIcon(
			MyCursors.class.getResource("calledGrf.png"));
	public static ImageIcon callersGrfIcon = new ImageIcon(
			MyCursors.class.getResource("callersGrf.png"));
	public static ImageIcon closeIcon = new ImageIcon(
			MyCursors.class.getResource("close.png"));
	public static ImageIcon refreshIcon = new ImageIcon(
			MyCursors.class.getResource("refresh.png"));
	public static ImageIcon undoIcon = new ImageIcon(
			MyCursors.class.getResource("undo.gif"));
	public static ImageIcon redoIcon = new ImageIcon(
			MyCursors.class.getResource("redo.gif"));
	public static ImageIcon findIcon = new ImageIcon(
			MyCursors.class.getResource("find.gif"));
	public static ImageIcon saveIcon = new ImageIcon(
			MyCursors.class.getResource("saveIcon.gif"));
	public static ImageIcon compilationIcon = new ImageIcon(
			MyCursors.class.getResource("compilationIcon.gif"));
	public static ImageIcon cutIcon = new ImageIcon(
			MyCursors.class.getResource("cutIcon.gif"));
	public static ImageIcon pasteIcon = new ImageIcon(
			MyCursors.class.getResource("pasteIcon.gif"));
	public static ImageIcon copyIcon = new ImageIcon(
			MyCursors.class.getResource("copyIcon.gif"));
	public static ImageIcon arrowIcon = new ImageIcon(
			MyCursors.class.getResource("arrowIcon.gif"));
	public static ImageIcon createBoxesIcon = new ImageIcon(
			MyCursors.class.getResource("createBoxesIcon.gif"));
	public static ImageIcon killBoxesIcon = new ImageIcon(
			MyCursors.class.getResource("killBoxesIcon.gif"));
	public static ImageIcon linkBoxesIcon = new ImageIcon(
			MyCursors.class.getResource("linkBoxesIcon.gif"));
	public static ImageIcon reverseLinkBoxesIcon = new ImageIcon(
			MyCursors.class.getResource("reverseLinkBoxesIcon.gif"));
	public static ImageIcon openSubgraphIcon = new ImageIcon(
			MyCursors.class.getResource("openSubgraphIcon.gif"));
	public static ImageIcon configurationIcon = new ImageIcon(
			MyCursors.class.getResource("configurationIcon.gif"));
	public static Cursor normalCursor = Cursor.getDefaultCursor();
	public static Cursor createBoxesCursor = Toolkit
			.getDefaultToolkit()
			.createCustomCursor(
					new ImageIcon(MyCursors.class
							.getResource("createBoxesCursor.gif")).getImage(),
					new Point(0, 0), "createBoxesCursor");
	public static Cursor killBoxesCursor = Toolkit
			.getDefaultToolkit()
			.createCustomCursor(
					new ImageIcon(MyCursors.class
							.getResource("killBoxesCursor.gif")).getImage(),
					new Point(0, 0), "killBoxesCursor");
	public static Cursor linkBoxesCursor = Toolkit
			.getDefaultToolkit()
			.createCustomCursor(
					new ImageIcon(MyCursors.class
							.getResource("linkBoxesCursor.gif")).getImage(),
					new Point(0, 0), "linkBoxesCursor");
	public static Cursor reverseLinkBoxesCursor = Toolkit
			.getDefaultToolkit()
			.createCustomCursor(
					new ImageIcon(MyCursors.class
							.getResource("reverseLinkBoxesCursor.gif"))
							.getImage(),
					new Point(0, 0), "reverseLinkBoxesCursor");
	public static Cursor openSubgraphCursor = Toolkit
			.getDefaultToolkit()
			.createCustomCursor(
					new ImageIcon(MyCursors.class
							.getResource("openSubgraphCursor.gif")).getImage(),
					new Point(0, 0), "openSubgraphCursor");
}
