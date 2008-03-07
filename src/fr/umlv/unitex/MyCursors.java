 /*
  * Unitex
  *
  * Copyright (C) 2001-2008 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

package fr.umlv.unitex;

import java.awt.*;

import javax.swing.*;

/**
 * This class defines personal mouse cursors and icons.
 * @author Sébastien Paumier
 *
 */
public class MyCursors {

	/**
	 * Normal mouse cursor 
	 */
	public static Cursor normalCursor;

	/**
	 * Cursor used to create boxes 
	 */
	public static Cursor createBoxesCursor;

	/**
	 *  Cursor used to kill boxes
	 */
	public static Cursor killBoxesCursor;

	/**
	 *  Cursor used to link boxes
	 */
	public static Cursor linkBoxesCursor;

	/**
	 *  Cursor used to create reverse transitions
	 */
	public static Cursor reverseLinkBoxesCursor;

	/**
	 *  Cursor used to open sub-graphs
	 */
	public static Cursor openSubgraphCursor;

	/**
	 *  Save icon
	 */
	public static ImageIcon saveIcon;

	/**
	 *  Graph compilation icon
	 */
	public static ImageIcon compilationIcon;

	/**
	 *  Cut icon
	 */
	public static ImageIcon cutIcon;

	/**
	 *  Paste icon
	 */
	public static ImageIcon pasteIcon;

	/**
	 *  Copy icon
	 */
	public static ImageIcon copyIcon;

	/**
	 *  Arrow icon used to return in normal mouse mode
	 */
	public static ImageIcon arrowIcon;

	/**
	 *  Creating boxes icon
	 */
	public static ImageIcon createBoxesIcon;

	/**
	 *  Killing boxes icon
	 */
	public static ImageIcon killBoxesIcon;

	/**
	 *  Linking boxes icon
	 */
	public static ImageIcon linkBoxesIcon;

	/**
	 *  Creating reverse transitions icon
	 */
	public static ImageIcon reverseLinkBoxesIcon;

	/**
	 *  Opening sub-graphs icon
	 */
	public static ImageIcon openSubgraphIcon;

	/**
	 * Undo graph icon
	 */
	public static ImageIcon undoIcon;

	/**
	 * Redo graph icon
	 */	
	public static ImageIcon redoIcon;
	
	/** find icon */
	public static ImageIcon findIcon;
	
	/**
	 *  Configuration icon
	 */	
	public static ImageIcon configurationIcon;

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
	public static void initCursorsAndIcons() {
		
		undoIcon = new ImageIcon(MyCursors.class.getResource("undo.gif"));
		redoIcon = new ImageIcon(MyCursors.class.getResource("redo.gif"));
		findIcon = new ImageIcon(MyCursors.class.getResource("find.gif"));
		
		saveIcon = new ImageIcon(MyCursors.class.getResource("saveIcon.gif"));
		compilationIcon =
			new ImageIcon(MyCursors.class.getResource("compilationIcon.gif"));
		cutIcon = new ImageIcon(MyCursors.class.getResource("cutIcon.gif"));
		pasteIcon = new ImageIcon(MyCursors.class.getResource("pasteIcon.gif"));
		copyIcon = new ImageIcon(MyCursors.class.getResource("copyIcon.gif"));
		arrowIcon = new ImageIcon(MyCursors.class.getResource("arrowIcon.gif"));
		createBoxesIcon =
			new ImageIcon(MyCursors.class.getResource("createBoxesIcon.gif"));
		killBoxesIcon =
			new ImageIcon(MyCursors.class.getResource("killBoxesIcon.gif"));
		linkBoxesIcon =
			new ImageIcon(MyCursors.class.getResource("linkBoxesIcon.gif"));
		reverseLinkBoxesIcon =
			new ImageIcon(
				MyCursors.class.getResource("reverseLinkBoxesIcon.gif"));
		openSubgraphIcon =
			new ImageIcon(MyCursors.class.getResource("openSubgraphIcon.gif"));
		configurationIcon =
			new ImageIcon(MyCursors.class.getResource("configurationIcon.gif"));
		normalCursor = Cursor.getDefaultCursor();
		createBoxesCursor =
			Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon(
					MyCursors.class.getResource("createBoxesCursor.gif"))
					.getImage(),
				new Point(0, 0),
				"createBoxesCursor");
		killBoxesCursor =
			Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon(
					MyCursors.class.getResource("killBoxesCursor.gif"))
					.getImage(),
				new Point(0, 0),
				"killBoxesCursor");
		linkBoxesCursor =
			Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon(
					MyCursors.class.getResource("linkBoxesCursor.gif"))
					.getImage(),
				new Point(0, 0),
				"linkBoxesCursor");
		reverseLinkBoxesCursor =
			Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon(
					MyCursors.class.getResource("reverseLinkBoxesCursor.gif"))
					.getImage(),
				new Point(0, 0),
				"reverseLinkBoxesCursor");
		openSubgraphCursor =
			Toolkit.getDefaultToolkit().createCustomCursor(
				new ImageIcon(
					MyCursors.class.getResource("openSubgraphCursor.gif"))
					.getImage(),
				new Point(0, 0),
				"openSubgraphCursor");
	}
}