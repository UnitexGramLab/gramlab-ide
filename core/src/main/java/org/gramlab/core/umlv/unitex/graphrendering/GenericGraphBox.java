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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.TextLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.diff.GraphDecoratorConfig;
import org.gramlab.core.umlv.unitex.frames.GraphFrame;

/**
 * This class describes a box of a graph or a sentence graph.
 * 
 * @author Sébastien Paumier
 */
public class GenericGraphBox {
	/**
	 * Box X coordinate
	 */
	public int X;
	/**
	 * Box Y coordinate
	 */
	public int Y;
	/**
	 * Box type (initial, final or normal)
	 */
	public int type;
	public final static int INITIAL = 0;
	public final static int FINAL = 1;
	public final static int NORMAL = 2;
	/**
	 * Indicates if the box contains a start or end of a variable
	 */
	public boolean variable;
	/**
	 * If 'variable' is true, this field is used to know whether the variable is
	 * a normal one or an output one
	 */
	boolean outputVariable;
	/**
	 * Indicates if the box contains a context limit
	 */
	public boolean contextMark;
	/**
	 * Indicates if the box contains a morphological mode tag
	 */
	public boolean morphologicalModeMark;
        /**
        * Indicates if the box contains generic graph indicator
        */
        public boolean genericGrfMark;
	/**
	 * Indicates if the box is currently selected
	 */
	private boolean selected;
	/**
	 * Indicates if the box is isolated (no input/output transition)
	 */
	public boolean standaloneBox;
	/**
	 * Comment boxes start with / and have their content tokenized in a
	 * different way than normal boxes: + is used to separate lines, and \ is
	 * used to protect + and \. The initial / is not displayed, neither are the
	 * protecting \
	 */
	public boolean commentBox;
	/**
	 * Indicates if there is at least one transition that goes out this box
	 */
	public boolean hasOutgoingTransitions;
	/**
	 * Indicates if there is at least one transition that comes in this box
	 */
	private int hasIncomingTransitions;
	/**
	 * Indicates if the box is being dragged alone
	 */
	boolean singleDragging;
	/**
	 * X coordinate of the box input point
	 */
	public int X_in;
	/**
	 * Y coordinate of the box input point
	 */
	public int Y_in;
	/**
	 * X coordinate of the box output point
	 */
	public int X_out;
	/**
	 * Y coordinate of the box output point
	 */
	public int Y_out;
	/**
	 * Text contained in the box, as it can be viewed when the box is edited
	 */
	String content;
	/**
	 * Tokenized box lines
	 */
	public final ArrayList<String> lines;
	/**
	 * greyed[i]==true if the line i is a subgraph call
	 */
	public final ArrayList<Boolean> greyed;
	/**
	 * output of the box, if any
	 */
	public String transduction;
	/**
	 * Number of visible lines in the box: 0 if the box contains only the
	 * epsilon symbol, the number of lines otherwise
	 */
	public int n_lines; // number of visible lines in the box
	/**
	 * X coordinate of the upper left corner of the box frame
	 */
	public int X1;
	/**
	 * Y coordinate of the upper left corner of the box frame
	 */
	public int Y1;
	/**
	 * Width of the box frame
	 */
	public int Width;
	/**
	 * Height of the box frame
	 */
	public int Height;
	/**
	 * Height of a box line
	 */
	private int h_ligne;
	/**
	 * Heigth of the bottom of letters like j, q or g
	 */
	private int descent;
	/**
	 * <code>GenericGraphicalZone</code> object that contains the box
	 */
	GenericGraphicalZone parentGraphicalZone;
	public ArrayList<GenericGraphBox> transitions;
	public static final Font variableFont = new Font("Times New Roman",
			Font.BOLD, 30);
	Graphics2D context;
	/**
	 * Number of the box
	 */
	int identificationNumber; // number used to numerote the state

	/**
	 * Constructs a new box
	 * 
	 * @param x
	 *            X coordinate of the input point of the box
	 * @param y
	 *            Y coordinate of the input point of the box
	 * @param type
	 *            indicates if the box is initial, final or normal
	 * @param p
	 *            component on which the box will be drawn
	 */
	GenericGraphBox(int x, int y, int type, GenericGraphicalZone p) {
		this.X = x;
		Y = y;
		parentGraphicalZone = p;
		this.type = type;
		content = "<E>";
		transduction = "";
		variable = false;
		n_lines = 0;
		h_ligne = 15;
		lines = new ArrayList<String>();
		greyed = new ArrayList<Boolean>();
		transitions = new ArrayList<GenericGraphBox>();
		Width = 15;
		Height = 20;
		if (type == FINAL) {
			X_in = x;
			Y_in = Y;
			X1 = x;
			Y1 = Y - 10;
			Y_out = Y_in;
			X_out = X_in + 25;
		} else {
			X1 = x;
			Y1 = Y;
			X_in = x;
			Y_in = Y /*-Height/2*/;
			X_out = x + Width + 5;
			Y_out = Y_in;
		}
		selected = false;
		singleDragging = false;
		standaloneBox = true;
		commentBox = false;
		hasOutgoingTransitions = false;
		hasIncomingTransitions = 0;
		identificationNumber = -1;
	}

	public int getBoxNumber() {
		return parentGraphicalZone.graphBoxes.indexOf(this);
	}

	/**
	 * Returns a String corresponding to the graph call, after repository
	 * resolution and replacement of ':' by the system separator char.
	 */
	public static String getNormalizeGraphCall(String s) {
		if (s.startsWith(":")) {
			// if the graph is located in a package repository
			String name = null;
			if (s.startsWith(":$")) {
				int pos = s.indexOf(':', 2);
				if (pos == -1
						|| (s.indexOf('/', 2) != -1 && pos > s.indexOf('/', 2))) {
					pos = s.indexOf('/', 2);
				}
				if (pos == -1
						|| (s.indexOf('\\', 2) != -1 && pos > s
								.indexOf('\\', 2))) {
					pos = s.indexOf('\\', 2);
				}
				if (pos != -1) {
					name = s.substring(2, pos);
					s = s.substring(pos);
				}
			} else {
				s = s.substring(1);
			}
			final File repositoryDir = ConfigManager.getManager()
					.getGraphRepositoryPath(null, name);
			if (repositoryDir != null) {
				s = repositoryDir.getAbsolutePath() + File.separatorChar
						+ s.replace(':', File.separatorChar);
				return s;
			}
		}
		return s.replace(':', File.separatorChar);
	}

	private boolean existsGraph(int n) {
		if (parentGraphicalZone.parentFrame == null) {
			/*
			 * If this method is called from a graph diff frame, we answer true
			 * to avoid problems
			 */
			return true;
		}
		if (!greyed.get(n))
			throw new IllegalArgumentException(
					"Should not be called with a normal line");
		String s = getNormalizeGraphCall(lines.get(n));
		boolean endWithGrf = true;
		if (!s.endsWith(".grf")) {
			s = s + ".grf";
			endWithGrf = false;
		}
		if (s.startsWith(":")) {
			if (new File(s).exists()) {
				return true;
			}
			if (!endWithGrf) {
				/*
				 * If there was no explicit .grf extension, we try to look for a
				 * .fst2
				 */
				s = s + ".fst2";
				return new File(s).exists();
			}
			return false;
		}
		// otherwise
		final File f = new File(s);
		final File fst2 = new File(s.substring(0, s.lastIndexOf('.')) + ".fst2");
		if (Config.getCurrentSystem() == Config.WINDOWS_SYSTEM
				&& f.isAbsolute()) {
			// first we test if we have an absolute windows pathname,
			// in order to avoid wrong transformations like:
			//
			// C:\\foo\foo.grf => C\\\foo\foo.grf
			//
			if (f.exists())
				return true;
			if (!endWithGrf) {
				return fst2.exists();
			}
			return false;
		}
		s = s.replace(':', File.separatorChar);
		if (!f.isAbsolute()) {
			final File currentGraph = ((GraphFrame) parentGraphicalZone.parentFrame)
					.getGraph();
			if (currentGraph == null) {
				// if we try to open a subgraph inside a newly created graph
				// with no name
				return false;
			}
			if (new File(currentGraph.getParentFile(), s).exists())
				return true;
			if (!endWithGrf) {
				s = s.substring(0, s.lastIndexOf('.')) + ".fst2";
				return new File(currentGraph.getParentFile(), s).exists();
			}
			return false;
		}
		if (f.exists())
			return true;
		if (!endWithGrf) {
			s = s.substring(0, s.lastIndexOf('.')) + ".fst2";
			return new File(s).exists();
		}
		return false;
	}

	/**
	 * Tests if the click point was in a sub-graph call area. In that case, it
	 * returns the sub-graph's name
	 * 
	 * @param y
	 *            Y coordinate of the click point
	 * @return the sub-graph's name, or the empty string if no graph was pointed
	 *         out by the click
	 */
	public File getGraphClicked(int y) {
		int n;
		String s;
		Boolean b = false;
		n = (y - Y1 - 4) / (h_ligne);
		if (n >= greyed.size())
			n = greyed.size() - 1;
		b = (n >= 0) && greyed.get(n);
		if (b) {
			s = lines.get(n);
			if (!s.endsWith(".grf")) {
				s = s + ".grf";
			}
			/* replace ':' by '/' resp. '\\' */
			if (s.startsWith(":")) {
				// if the graph is located in the package repository
				return new File(getNormalizeGraphCall(s));
			}
			// otherwise
			File f = new File(s);
			if (Config.getCurrentSystem() == Config.WINDOWS_SYSTEM
					&& f.isAbsolute()) {
				// first we test if we have an absolute windows pathname,
				// in order to avoid wrong transformations like:
				//
				// C:\\foo\foo.grf => C\\\foo\foo.grf
				//
				return f;
			}
			s = s.replace(':', File.separatorChar);
			if (!f.isAbsolute()) {
				final File currentGraph = ((GraphFrame) parentGraphicalZone.parentFrame)
						.getGraph();
				if (currentGraph == null) {
					// if we try to open a subgraph inside a newly created graph
					// with no name
					f = null;
					JOptionPane
							.showMessageDialog(
									null,
									"Cannot resolve relative graph path:\n\n"
											+ s
											+ "\n\nbecause the location of the current graph is\n"
											+ "not defined (the graph has never been saved).",
									"Error", JOptionPane.ERROR_MESSAGE);
				} else {
					f = new File(currentGraph.getParentFile(), s);
				}
			}
			return f;
		}
		return null;
	}

	/**
	 * Adds a transition to a box. If there is already a transition to this box,
	 * it is removed.
	 * 
	 * @param g
	 *            the destination box
	 */
	public boolean addTransitionTo(GenericGraphBox g) {
		if (this.type == FINAL) {
			// if it is the final, we don't allow parent.pref.output transitions
			return false;
		}
		if (commentBox || g.commentBox) {
			/* We never add any transition from or to a comment box */
			return false;
		}
		final int i = transitions.indexOf(g);
		if (i == -1) {
			// if the transition to g does not exist, we create it
			// but we check first if it is a loop transition on a box containing
			// an interval loop
			if (g == this && !getRangeOutput(transduction).equals("")
					&& parentGraphicalZone != null) {
				JOptionPane
						.showMessageDialog(
								null,
								"Setting a loop on a box containing a range definition is not allowed.",
								"Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			transitions.add(g);
			g.hasIncomingTransitions++;
		} else {
			// else, we remove it
			transitions.remove(i);
			g.hasIncomingTransitions--;
		}
		hasOutgoingTransitions = !transitions.isEmpty();
		standaloneBox = (!hasOutgoingTransitions && hasIncomingTransitions == 0);
		g.standaloneBox = (!g.hasOutgoingTransitions && g.hasIncomingTransitions == 0);
		return true;
	}

	/**
	 * Adds a transition to a box. If there is already a transition to this box,
	 * it is not removed.
	 * 
	 * @param g
	 *            the destination box
	 */
	public void onlyAddTransitionTo(GenericGraphBox g) {
		if (this.type == FINAL) {
			// if it is the final, we don't allow parent.pref.output transitions
			return;
		}
		if (commentBox || g.commentBox) {
			/* We never add any transition from or to a comment box */
			return;
		}
		final int i = transitions.indexOf(g);
		if (i == -1) {
			// if the transition to g does not exist, we create it
			transitions.add(g);
			g.hasIncomingTransitions++;
		}
		hasOutgoingTransitions = !transitions.isEmpty();
		standaloneBox = (!hasOutgoingTransitions && hasIncomingTransitions == 0);
		g.standaloneBox = (!g.hasOutgoingTransitions && g.hasIncomingTransitions == 0);
	}

	/**
	 * removes a box transition
	 * 
	 * @param g
	 *            the transition's destination box
	 */
	public void removeTransitionTo(GenericGraphBox g) {
		final int i = transitions.indexOf(g);
		if (i != -1) {
			transitions.remove(i);
			g.hasIncomingTransitions--;
		}
		hasOutgoingTransitions = !transitions.isEmpty();
		standaloneBox = (!hasOutgoingTransitions && hasIncomingTransitions == 0);
		g.standaloneBox = (!g.hasOutgoingTransitions && g.hasIncomingTransitions == 0);
	}

	public void removeAllOutgoingTransitions() {
		for (int i = transitions.size() - 1; i >= 0; i--) {
			removeTransitionTo(transitions.get(i));
		}
	}

	public void removeAllIncomingTransitions() {
		if (parentGraphicalZone == null) {
			/* This happens when the graph is loaded */
			return;
		}
		for (final GenericGraphBox g : parentGraphicalZone.graphBoxes) {
			g.removeTransitionTo(this);
		}
	}

	/**
	 * Translates the box
	 * 
	 * @param dx
	 *            length of X shift in pixels
	 * @param dy
	 *            length of Y shift in pixels
	 */
	public void translate(int dx, int dy) {
		X = X + dx;
		Y = Y + dy;
		X_in = X_in + dx;
		Y_in = Y_in + dy;
		X_out = X_out + dx;
		Y_out = Y_out + dy;
		X1 = X1 + dx;
		Y1 = Y1 + dy;
	}

	/**
	 * Tests if the box is selected by a rectangle
	 * 
	 * @param Xr
	 *            X coordinate of the upper left corner of the rectangle
	 * @param Yr
	 *            Y coordinate of the upper left corner of the rectangle
	 * @param Wr
	 *            width of the rectangle
	 * @param Hr
	 *            height of the rectangle
	 * @return <code>true</code> if the intersection between the box and the
	 *         rectangle is not empty, <code>false</code> otherwise
	 */
	public boolean isSelectedByRectangle(int Xr, int Yr, int Wr, int Hr) {
		return !((X1 > (Xr + Wr)) || ((X1 + Width) < Xr) || (Y1 > (Yr + Hr)) || ((Y1 + Height) < Yr));
	}

	/**
	 * Draws a transition to a box
	 * 
	 * @param g
	 *            the graphical context
	 * @param dest
	 *            the destination box
	 */
	public void drawTransition(Graphics2D g, GenericGraphBox dest, DrawGraphParams params) {
		final int boxNumber = getBoxNumber();
		final int destNumber = dest.getBoxNumber();
		final Stroke oldStroke = g.getStroke();
		if (parentGraphicalZone.decorator == null) {
			g.setColor(params.getForegroundColor());
		} else {
			g.setColor(parentGraphicalZone.decorator.getTransitionColor(
					boxNumber, destNumber, params.getForegroundColor()));
			g.setStroke(parentGraphicalZone.decorator.getTransitionStroke(
					boxNumber, destNumber, oldStroke));
		}
		if (!parentGraphicalZone.getGraphPresentationInfo().isRightToLeft()) {
			if (dest.X_in > this.X_out) {
				// easiest case: drawing a line
				GraphicalToolBox.drawLine(g, this.X_out, this.Y_out, dest.X_in,
						dest.Y_in);
				g.setStroke(oldStroke);
				return;
			}
			if (this.equals(dest)) {
				// if the box is relied to itself
				final int diametre1 = 10 + Height / 2;
				GraphicalToolBox.drawArc(g, X_out - diametre1 / 2, Y_out
						- diametre1, diametre1, diametre1, 90, -180);
				GraphicalToolBox.drawArc(g, dest.X_in - diametre1 / 2,
						dest.Y_in - diametre1, diametre1, diametre1, 90, 180);
				GraphicalToolBox.drawLine(g, X_in, Y_in - diametre1, X_out,
						Y_out - diametre1);
				g.setStroke(oldStroke);
				return;
			}
			if ((Y1 < (dest.Y1 + dest.Height)) && ((Y1 + Height) > dest.Y1)) {
				final int diametre1 = 10 + Height / 2;
				GraphicalToolBox.drawArc(g, X_out - diametre1 / 2, Y_out
						- diametre1, diametre1, diametre1, 90, -180);
				final int diametre2 = 10 + dest.Height / 2;
				GraphicalToolBox.drawArc(g, dest.X_in - diametre2 / 2,
						dest.Y_in - diametre2, diametre2, diametre2, 90, 180);
				int Xpoint1, Ypoint1, Xpoint2, Ypoint2;
				Xpoint1 = dest.X_in;
				Ypoint1 = dest.Y_in - diametre2;
				Xpoint2 = X_out;
				Ypoint2 = Y_out - diametre1;
				int Xmilieu, Ymilieu, largeurLimite;
				Xmilieu = (Xpoint2 + Xpoint1) / 2;
				Ymilieu = (Ypoint2 + Ypoint1) / 2;
				largeurLimite = diametre1 + diametre2;
				GraphicalToolBox.drawCurve(g, Xpoint1, Ypoint1, Xpoint1
						+ largeurLimite, Ypoint1, Xmilieu, Ymilieu);
				GraphicalToolBox.drawCurve(g, Xpoint2, Ypoint2, Xpoint2
						- largeurLimite, Ypoint2, Xmilieu, Ymilieu);
				g.setStroke(oldStroke);
				return;
			}
			if (Y1 < (dest.Y1 + dest.Height)) {
				final int diametre1 = 10 + Height / 2;
				GraphicalToolBox.drawArc(g, X_out - diametre1 / 2, Y_out,
						diametre1, diametre1, 0, 90);
				final int diametre2 = 10 + dest.Height / 2;
				GraphicalToolBox.drawArc(g, dest.X_in - diametre2 / 2,
						dest.Y_in - diametre2, diametre2, diametre2, 180, 90);
				int Xpoint1, Ypoint1, Xpoint2, Ypoint2;
				Xpoint1 = dest.X_in - diametre2 / 2;
				Ypoint1 = dest.Y_in - diametre2 / 2;
				Xpoint2 = X_out + 1 + diametre1 / 2;
				Ypoint2 = Y_out + 1 + diametre1 / 2;
				final int hauteurLimite = diametre1 + diametre2 - 20;
				int Xmilieu, Ymilieu;
				Xmilieu = (Xpoint2 + Xpoint1) / 2;
				Ymilieu = (Ypoint2 + Ypoint1) / 2;
				GraphicalToolBox.drawCurve(g, Xpoint1, Ypoint1, Xpoint1,
						Ypoint1 - hauteurLimite, Xmilieu, Ymilieu);
				GraphicalToolBox.drawCurve(g, Xpoint2, Ypoint2, Xpoint2,
						Ypoint2 + hauteurLimite, Xmilieu, Ymilieu);
				g.setStroke(oldStroke);
				return;
			}
			final int diametre1 = 10 + Height / 2;
			GraphicalToolBox.drawArc(g, X_out - diametre1 / 2, Y_out
					- diametre1, diametre1, diametre1, 270, 90);
			final int diametre2 = 10 + dest.Height / 2;
			GraphicalToolBox.drawArc(g, dest.X_in - diametre2 / 2, dest.Y_in,
					diametre2, diametre2, 90, 90);
			int Xpoint1, Ypoint1, Xpoint2, Ypoint2;
			Xpoint1 = dest.X_in - diametre2 / 2;
			Ypoint1 = dest.Y_in + diametre2 / 2;
			Xpoint2 = X_out + 1 + diametre1 / 2;
			Ypoint2 = Y_out + 1 - diametre1 / 2;
			final int hauteurLimite = diametre1 + diametre2 - 20;
			int Xmilieu, Ymilieu;
			Xmilieu = (Xpoint2 + Xpoint1) / 2;
			Ymilieu = (Ypoint2 + Ypoint1) / 2;
			GraphicalToolBox.drawCurve(g, Xpoint1, Ypoint1, Xpoint1, Ypoint1
					+ hauteurLimite, Xmilieu, Ymilieu);
			GraphicalToolBox.drawCurve(g, Xpoint2, Ypoint2, Xpoint2, Ypoint2
					- hauteurLimite, Xmilieu, Ymilieu);
			g.setStroke(oldStroke);
			return;
		}
		// end of the left to right mode
		if (dest.X_out - 5 < this.X_in - 5) {
			// easiest case: drawing a line
			GraphicalToolBox.drawLine(g, this.X_in - 5, this.Y_in,
					dest.X_out - 5, dest.Y_out);
			g.setStroke(oldStroke);
			return;
		}
		if (this.equals(dest)) {
			// if the box is relied to itself
			final int diametre1 = 10 + Height / 2;
			GraphicalToolBox.drawArc(g, X_in - 5 - diametre1 / 2, Y_in
					- diametre1, diametre1, diametre1, 90, 180);
			GraphicalToolBox.drawArc(g, dest.X_out - 5 - diametre1 / 2,
					dest.Y_out - diametre1, diametre1, diametre1, 90, -180);
			GraphicalToolBox.drawLine(g, X_out - 5, Y_out - diametre1,
					X_in - 5, Y_in - diametre1);
			g.setStroke(oldStroke);
			return;
		}
		if ((Y1 < (dest.Y1 + dest.Height)) && ((Y1 + Height) > dest.Y1)) {
			final int diametre1 = 10 + Height / 2;
			GraphicalToolBox.drawArc(g, X_in - 5 - diametre1 / 2, Y_in
					- diametre1, diametre1, diametre1, 90, 180);
			final int diametre2 = 10 + dest.Height / 2;
			GraphicalToolBox.drawArc(g, dest.X_out - 5 - diametre2 / 2,
					dest.Y_out - diametre2, diametre2, diametre2, 90, -180);
			int Xpoint1, Ypoint1, Xpoint2, Ypoint2;
			Xpoint2 = dest.X_out - 5;
			Ypoint2 = dest.Y_out - diametre2;
			Xpoint1 = X_in - 5;
			Ypoint1 = Y_in - diametre1;
			int Xmilieu, Ymilieu, largeurLimite;
			Xmilieu = (Xpoint2 + Xpoint1) / 2;
			Ymilieu = (Ypoint2 + Ypoint1) / 2;
			largeurLimite = diametre1 + diametre2;
			GraphicalToolBox.drawCurve(g, Xpoint1, Ypoint1, Xpoint1
					+ largeurLimite, Ypoint1, Xmilieu, Ymilieu);
			GraphicalToolBox.drawCurve(g, Xpoint2, Ypoint2, Xpoint2
					- largeurLimite, Ypoint2, Xmilieu, Ymilieu);
			g.setStroke(oldStroke);
			return;
		}
		if (Y1 < (dest.Y1 + dest.Height)) {
			final int diametre1 = 10 + Height / 2;
			GraphicalToolBox.drawArc(g, X_in - 5 - diametre1 / 2, Y_in,
					diametre1, diametre1, 90, 90);
			final int diametre2 = 10 + dest.Height / 2;
			GraphicalToolBox.drawArc(g, dest.X_out - 5 - diametre2 / 2,
					dest.Y_out - diametre2, diametre2, diametre2, 270, 90);
			int Xpoint1, Ypoint1, Xpoint2, Ypoint2;
			Xpoint1 = dest.X_out + 1 - 5 + diametre2 / 2;
			Ypoint1 = dest.Y_out - diametre2 / 2;
			Xpoint2 = X_in - 5 - diametre1 / 2;
			Ypoint2 = Y_in + diametre1 / 2;
			final int hauteurLimite = diametre1 + diametre2 - 20;
			int Xmilieu, Ymilieu;
			Xmilieu = (Xpoint2 + Xpoint1) / 2;
			Ymilieu = (Ypoint2 + Ypoint1) / 2;
			GraphicalToolBox.drawCurve(g, Xpoint1, Ypoint1, Xpoint1, Ypoint1
					- hauteurLimite, Xmilieu, Ymilieu);
			GraphicalToolBox.drawCurve(g, Xpoint2, Ypoint2, Xpoint2, Ypoint2
					+ hauteurLimite, Xmilieu, Ymilieu);
			g.setStroke(oldStroke);
			return;
		}
		final int diametre1 = 10 + Height / 2;
		GraphicalToolBox.drawArc(g, X_in - 5 - diametre1 / 2, Y_in - diametre1,
				diametre1, diametre1, 270, -90);
		final int diametre2 = 10 + dest.Height / 2;
		GraphicalToolBox.drawArc(g, dest.X_out - 5 - diametre2 / 2, dest.Y_out,
				diametre2, diametre2, 90, -90);
		int Xpoint1, Ypoint1, Xpoint2, Ypoint2;
		Xpoint1 = dest.X_out - 5 + diametre2 / 2;
		Ypoint1 = dest.Y_out + 1 + diametre2 / 2;
		Xpoint2 = X_in - 5 - diametre1 / 2;
		Ypoint2 = Y_in - diametre1 / 2;
		final int hauteurLimite = diametre1 + diametre2 - 20;
		int Xmilieu, Ymilieu;
		Xmilieu = (Xpoint2 + Xpoint1) / 2;
		Ymilieu = (Ypoint2 + Ypoint1) / 2;
		GraphicalToolBox.drawCurve(g, Xpoint1, Ypoint1, Xpoint1, Ypoint1
				+ hauteurLimite, Xmilieu, Ymilieu);
		GraphicalToolBox.drawCurve(g, Xpoint2, Ypoint2, Xpoint2, Ypoint2
				- hauteurLimite, Xmilieu, Ymilieu);
		g.setStroke(oldStroke);
	}

	/**
	 * Draws all transitions that go out of the box
	 * 
	 * @param gr
	 *            the graphical context
	 */
	public void drawTransitions(Graphics2D gr, DrawGraphParams params) {
		updateWithContext(gr);
		int i, L;
		GenericGraphBox g;
		if (transitions.isEmpty())
			return;
		L = transitions.size();
		for (i = 0; i < L; i++) {
			g = transitions.get(i);
			drawTransition(gr, g, params);
		}
	}

	void drawOtherSingleDrag(Graphics2D g, DrawGraphParams params) {
		if (standaloneBox)
			g.setColor(params.getCommentColor());
		else
			g.setColor(params.getForegroundColor());
		// drawing the box
		if (n_lines == 0) {
			GraphicalToolBox.drawLine(g, X_in, Y_in, X_in + 15, Y_in);
			if (!parentGraphicalZone.getGraphPresentationInfo().isRightToLeft())
				GraphicalToolBox.drawLine(g, X_in + 15, Y1, X_in + 15, Y1
						+ Height);
			else
				GraphicalToolBox.drawLine(g, X_in, Y1, X_in, Y1 + Height);
		} else {
			GraphicalToolBox.drawRect(g, X1, Y1, Width, Height);
		}
		// and the triangle if necessary
		if (hasOutgoingTransitions || type == INITIAL) {
			if (!parentGraphicalZone.getGraphPresentationInfo().isRightToLeft()) {
				GraphicalToolBox.drawLine(g, X_out, Y_out, X1 + Width, Y1);
				GraphicalToolBox.drawLine(g, X1 + Width, Y1, X1 + Width, Y1
						+ Height);
				GraphicalToolBox.drawLine(g, X1 + Width, Y1 + Height, X_out,
						Y_out);
			} else {
				GraphicalToolBox.drawLine(g, X_in - 5, Y_in, X1, Y1);
				GraphicalToolBox.drawLine(g, X1, Y1, X1, Y1 + Height);
				GraphicalToolBox.drawLine(g, X1, Y1 + Height, X_in - 5, Y_in);
			}
		}
	}

	private void drawInitialSingleDrag(Graphics2D g, DrawGraphParams params) {
		g.setColor(params.getForegroundColor());
		// drawing the box
		if (n_lines == 0) {
			GraphicalToolBox.drawLine(g, X_in, Y_in, X_in + 15, Y_in);
		} else {
			GraphicalToolBox.drawRect(g, X1, Y1, Width, Height);
		}
		// drawing the entry line
		if (!parentGraphicalZone.getGraphPresentationInfo().isRightToLeft())
			GraphicalToolBox.drawLine(g, X_in, Y_in, X_in - 10, Y_in);
		else
			GraphicalToolBox.drawLine(g, X_out - 5, Y_out, X_out + 5, Y_out);
		// and the triangle if necessary
		if (hasOutgoingTransitions || type == INITIAL) {
			if (!parentGraphicalZone.getGraphPresentationInfo().isRightToLeft()) {
				GraphicalToolBox.drawLine(g, X_out, Y_out, X1 + Width, Y1);
				GraphicalToolBox.drawLine(g, X1 + Width, Y1, X1 + Width, Y1
						+ Height);
				GraphicalToolBox.drawLine(g, X1 + Width, Y1 + Height, X_out,
						Y_out);
			} else {
				GraphicalToolBox.drawLine(g, X_in - 5, Y_in, X1, Y1);
				GraphicalToolBox.drawLine(g, X1, Y1, X1, Y1 + Height);
				GraphicalToolBox.drawLine(g, X1, Y1 + Height, X_in - 5, Y_in);
			}
		}
	}

	private void drawFinalSingleDrag(Graphics2D g, DrawGraphParams params) {
		drawFinal(g, params);
	}

	private static Font rangeFont = new JLabel().getFont().deriveFont(0.8f);
	private static Color rangeColor = new Color(0xF8, 0x72, 0x17);

	private void drawRange(Graphics2D g) {
		final String range = getRangeOutput(transduction);
		if (range.equals(""))
			return;
		final TextLayout textlayout = new TextLayout(range, parentGraphicalZone
				.getGraphPresentationInfo().getInput().getFont(),
				g.getFontRenderContext());
		g.setColor(rangeColor);
		g.setFont(rangeFont);
		textlayout.draw(g, X1 + 2, Y1 - 5);
	}

	private int getRangeShift(Graphics2D g) {
		final String range = getRangeOutput(transduction);
		if (range.equals(""))
			return 0;
		final TextLayout textlayout = new TextLayout(range, parentGraphicalZone
				.getGraphPresentationInfo().getInput().getFont(),
				g.getFontRenderContext());
		return (int) (textlayout.getBounds().getWidth() + 10);
	}

	void drawOtherStandalone(Graphics2D g, DrawGraphParams params) {
		int i;
		Boolean is_greyed;
		String l;
		final int boxNumber = getBoxNumber();
		if (parentGraphicalZone.decorator != null
				&& parentGraphicalZone.decorator
						.requiresSpecialLineDrawing(boxNumber)) {
			g.setColor(parentGraphicalZone.decorator.getBoxShapeColor(
					boxNumber, params.getForegroundColor()));
			final Stroke old = g.getStroke();
			g.setStroke(parentGraphicalZone.decorator.getBoxStroke(boxNumber,
					old));
			GraphicalToolBox.drawRect(g, X1, Y1, Width, Height);
			if (hasOutgoingTransitions || type == INITIAL) {
				if (!parentGraphicalZone.getGraphPresentationInfo()
						.isRightToLeft()) {
					final int a = X1 + Width;
					final int b = Y1 + Height;
					GraphicalToolBox.drawLine(g, X_out, Y_out, a, Y1);
					GraphicalToolBox.drawLine(g, a, Y1, a, b);
					GraphicalToolBox.drawLine(g, a, b, X_out, Y_out);
				} else {
					GraphicalToolBox.drawLine(g, X_in - 5, Y_in, X1, Y1);
					GraphicalToolBox.drawLine(g, X1, Y1, X1, Y1 + Height);
					GraphicalToolBox.drawLine(g, X1, Y1 + Height, X_in - 5,
							Y_in);
				}
			}
			g.setStroke(old);
			g.setColor(params.getForegroundColor());
		}
		if (variable) {
			drawVariableStandalone(g, params);
			return;
		}
		if (contextMark) {
			drawContextMarkStandalone(g, params);
			return;
		}

		g.setColor(params.getCommentColor());
		if (commentBox) {
			g.setColor(Color.GREEN.darker());
		}
		// print lines if the box is empty
		if (n_lines == 0) {
			GraphicalToolBox.drawLine(g, X_in, Y_in, X_in + 15, Y_in);
			if (!parentGraphicalZone.getGraphPresentationInfo().isRightToLeft())
				GraphicalToolBox.drawLine(g, X_in + 15, Y1, X_in + 15, Y1
						+ Height);
			else
				GraphicalToolBox.drawLine(g, X_in, Y1, X_in, Y1 + Height);
		} else {
			g.setColor(params.getBackgroundColor());
			GraphicalToolBox.fillRect(g, X1 + 1, Y1 + 1, Width - 2, Height - 2);
			g.setColor(params.getCommentColor());
			if (commentBox) {
				g.setColor(Color.GREEN.darker());
			}
		}
		// prints the lines of the box
		for (i = 0; i < n_lines; i++) {
			is_greyed = greyed.get(i);
			l = lines.get(i);

			Color c = null;

			if (n_lines > 1
					&& parentGraphicalZone.decorator != null
					&& parentGraphicalZone.decorator.isBoxLineHighlighted(
							boxNumber, i)) {
				c = GraphDecoratorConfig.DEBUG_HIGHLIGHT;
			} else if (is_greyed) {
				c = params.getSubgraphColor();
				if (l.startsWith(":")) {
					// if we have a subgraph within a package
					c = params.getPackageColor();
				}
				if (!existsGraph(i)) {
					/* We use a special color to mark non existing graphs */
					c = params.getUnreachableGraphColor();
				}
			}
			if (c != null) {
				g.setColor(c);
				GraphicalToolBox.fillRect(g, X1 + 3, Y1 + 4 + (i) * h_ligne,
						Width - 4, h_ligne);
			}
			g.setColor(params.getCommentColor());
			if (commentBox) {
				g.setColor(Color.GREEN.darker());
			}
			if (!l.equals("")) {
				final TextLayout textlayout = new TextLayout(l,
						parentGraphicalZone.getGraphPresentationInfo()
								.getInput().getFont(), g.getFontRenderContext());
				textlayout
						.draw(g, X1 + 5, Y1 - descent + 3 + (i + 1) * h_ligne);
			}
		}
		// prints the transduction, if exists
		if (parentGraphicalZone.decorator != null) {
			g.setColor(parentGraphicalZone.decorator.getBoxOutputColor(
					boxNumber, params.getForegroundColor()));
		}
		g.setColor(params.getForegroundColor());
		final String output = getNonRangeOutput(transduction);
		if (!output.equals("")) {
			g.setFont(parentGraphicalZone.getGraphPresentationInfo()
					.getOutput().getFont());
			g.drawString(output, X1 + 5, Y1 + Height
					+ g.getFontMetrics().getHeight());
		}
		drawRange(g);
	}

	/**
	 * If the output string starts with $[...]$, the function returns the range
	 * expression or "" if there is not;
	 */
	private String getRangeOutput(String output) {
		if (!output.startsWith("$["))
			return "";
		final int pos = output.indexOf("]$");
		if (pos == -1)
			return "";
		return output.substring(0, pos + 2);
	}

	/**
	 * If the output string starts with $[...]$, the function returns the string
	 * without the range expression.
	 */
	private String getNonRangeOutput(String output) {
		if (!output.startsWith("$["))
			return output;
		final int pos = output.indexOf("]$");
		if (pos == -1)
			return output;
		return output.substring(pos + 2);
	}

	private void drawFinal(Graphics2D g, DrawGraphParams params) {
		g.setColor(params.getBackgroundColor());
		GraphicalToolBox.fillEllipse(g, X, Y - 10, 21, 21);
		final Stroke old = g.getStroke();
		if (parentGraphicalZone.decorator == null) {
			g.setColor(params.getForegroundColor());
		} else {
			final int boxNumber = getBoxNumber();
			final JLabel r = parentGraphicalZone.decorator
					.getCoverageInfoLabel(boxNumber);
			if (r != null) {
				final TextLayout textlayout = new TextLayout(r.getText(),
						parentGraphicalZone.getGraphPresentationInfo()
								.getInput().getFont(), g.getFontRenderContext());
				g.setColor(r.getForeground());
				g.setFont(r.getFont());
				textlayout.draw(g, X1 + 2 + getRangeShift(g), Y1 - 5);
			}
			g.setColor(parentGraphicalZone.decorator.getBoxShapeColor(
					boxNumber, params.getForegroundColor()));
			g.setStroke(parentGraphicalZone.decorator.getBoxStroke(boxNumber,
					old));
		}
		GraphicalToolBox.drawEllipse(g, X, Y - 10, 21, 21);
		GraphicalToolBox.drawRect(g, X + 5, Y - 5, 10, 10);
		g.setStroke(old);
	}

	private void drawFinalSelected(Graphics2D g, DrawGraphParams params) {
		g.setColor(params.getSelectedColor());
		GraphicalToolBox.fillEllipse(g, X, Y - 10, 21, 21);
		g.setColor(params.getBackgroundColor());
		GraphicalToolBox.drawRect(g, X + 5, Y - 5, 10, 10);
	}

	private void drawVariable(Graphics2D g, DrawGraphParams params) {
		Color c;
		if (!outputVariable) {
			c = params.getCommentColor();
		} else {
			c = params.getOutputVariableColor();
		}
		if (parentGraphicalZone.decorator != null) {
			c = parentGraphicalZone.decorator.getBoxOutputColor(getBoxNumber(),
					c);
		}
		g.setColor(c);
		g.setFont(variableFont);
		g.drawString(lines.get(0), X1 + 5, Y1 - g.getFontMetrics().getDescent()
				+ get_h_variable_ligne());
		g.setFont(parentGraphicalZone.getGraphPresentationInfo().getOutput()
				.getFont());
		g.drawString(transduction, X1 + 10, Y1 + Height
				+ g.getFontMetrics().getHeight());
	}

	private void drawVariableSelected(Graphics2D g, DrawGraphParams params) {
		Color c = params.getSelectedColor();
		if (parentGraphicalZone.decorator != null) {
			c = parentGraphicalZone.decorator.getBoxOutputColor(getBoxNumber(),
					c);
		}
		g.setColor(c);
		GraphicalToolBox.fillRect(g, X1, Y1, Width, Height);
		g.setColor(params.getCommentColor());
		g.setFont(variableFont);
		g.drawString(lines.get(0), X1 + 5, Y1 - g.getFontMetrics().getDescent()
				+ get_h_variable_ligne());
		g.setColor(params.getSelectedColor());
		GraphicalToolBox.fillRect(
				g,
				X1 + 5,
				Y1 + Height + g.getFontMetrics().getDescent(),
				g.getFontMetrics(
						parentGraphicalZone.getGraphPresentationInfo()
								.getOutput().getFont()).stringWidth(
						transduction),
				g.getFontMetrics(
						parentGraphicalZone.getGraphPresentationInfo()
								.getOutput().getFont()).getHeight() + 1);
		g.setColor(params.getBackgroundColor());
		g.setFont(parentGraphicalZone.getGraphPresentationInfo().getOutput()
				.getFont());
		g.drawString(transduction, X1 + 5, Y1 + Height
				+ g.getFontMetrics().getHeight());
	}

	private void drawVariableStandalone(Graphics2D g, DrawGraphParams params) {
		drawVariable(g, params);
	}

	private void drawContextMark(Graphics2D g, DrawGraphParams params) {
		g.setColor(params.getContextColor());
		g.setFont(variableFont);
		g.drawString(lines.get(0), X1 + 5, Y1 - g.getFontMetrics().getDescent()
				+ get_h_variable_ligne());
	}
        
        private void drawGenericGrfMark(Graphics2D g, DrawGraphParams params) {
		g.setColor(params.getGenericGrfColor());
		g.setFont(variableFont);
		g.drawString(lines.get(0), X1 + 5, Y1 - g.getFontMetrics().getDescent()
				+ get_h_variable_ligne());
                if(!"".equals(transduction)) {
                    g.drawString(transduction, X1 + 10, Y1 + Height
				+ g.getFontMetrics().getHeight());
                }
	}

	private void drawContextMarkSelected(Graphics2D g, DrawGraphParams params) {
		drawVariableSelected(g, params);
	}

	private void drawContextMarkStandalone(Graphics2D g, DrawGraphParams params) {
		drawContextMark(g, params);
	}

	private void drawMorphologicalModeMark(Graphics2D g, DrawGraphParams params) {
		g.setColor(params.getMorphologicalModeColor());
		g.setFont(variableFont);
		g.drawString(lines.get(0), X1 + 5, Y1 - g.getFontMetrics().getDescent()
				+ get_h_variable_ligne());
	}

	private void drawMorphologicalModeMarkSelected(Graphics2D g, DrawGraphParams params) {
		drawVariableSelected(g, params);
	}

        private void drawGenericGrfMarkSelected(Graphics2D g, DrawGraphParams params) {
		drawVariableSelected(g, params);
	}
	void drawOther(Graphics2D g, DrawGraphParams params) {
		final int boxNumber = getBoxNumber();
		int i;
		Boolean is_greyed;
		String l;
		boolean boxDrawn = false;
		if (parentGraphicalZone.decorator != null
				&& parentGraphicalZone.decorator
						.requiresSpecialLineDrawing(boxNumber)) {
			g.setColor(parentGraphicalZone.decorator.getBoxShapeColor(
					boxNumber, params.getForegroundColor()));
			final Stroke old = g.getStroke();
			g.setStroke(parentGraphicalZone.decorator.getBoxStroke(boxNumber,
					old));
			GraphicalToolBox.drawRect(g, X1, Y1, Width, Height);
			if (hasOutgoingTransitions || type == INITIAL) {
				if (!parentGraphicalZone.getGraphPresentationInfo()
						.isRightToLeft()) {
					final int a = X1 + Width;
					final int b = Y1 + Height;
					GraphicalToolBox.drawLine(g, X_out, Y_out, a, Y1);
					GraphicalToolBox.drawLine(g, a, Y1, a, b);
					GraphicalToolBox.drawLine(g, a, b, X_out, Y_out);
				} else {
					GraphicalToolBox.drawLine(g, X_in - 5, Y_in, X1, Y1);
					GraphicalToolBox.drawLine(g, X1, Y1, X1, Y1 + Height);
					GraphicalToolBox.drawLine(g, X1, Y1 + Height, X_in - 5,
							Y_in);
				}
			}
			g.setStroke(old);
			g.setColor(params.getForegroundColor());
			boxDrawn = true;
		}
		if (parentGraphicalZone.decorator != null) {
			final JLabel r = parentGraphicalZone.decorator
					.getCoverageInfoLabel(boxNumber);
			if (r != null) {
				final TextLayout textlayout = new TextLayout(r.getText(),
						parentGraphicalZone.getGraphPresentationInfo()
								.getInput().getFont(), g.getFontRenderContext());
				g.setColor(r.getForeground());
				g.setFont(r.getFont());
				textlayout.draw(g, X1 + 2 + getRangeShift(g), Y1 - 5);
			}
		}
		if (variable) {
			drawVariable(g, params);
			return;
		}
		if (contextMark) {
			drawContextMark(g, params);
			return;
		}
                if(genericGrfMark) { 
                    drawGenericGrfMark(g,params);
                    return;
                }
                    
		if (morphologicalModeMark) {
			drawMorphologicalModeMark(g, params);
			return;
		}
		g.setColor(params.getForegroundColor());
		// drawing the box
		if (n_lines == 0) {
			if (parentGraphicalZone.decorator != null
					&& parentGraphicalZone.decorator.isLinearTfst()) {
				g.setColor(GraphDecoratorConfig.LINEAR_TFST);
			}
			GraphicalToolBox.drawLine(g, X_in, Y_in, X_in + 15, Y_in);
			if (!parentGraphicalZone.getGraphPresentationInfo().isRightToLeft())
				GraphicalToolBox.drawLine(g, X_in + 15, Y1, X_in + 15, Y1
						+ Height);
			else
				GraphicalToolBox.drawLine(g, X_in, Y1, X_in, Y1 + Height);
		} else {
			if (parentGraphicalZone.decorator == null) {
				g.setColor(params.getBackgroundColor());
			} else {
				g.setColor(parentGraphicalZone.decorator.getBoxFillColor(
						boxNumber, params.getBackgroundColor()));
			}
			GraphicalToolBox.fillRect(g, X1 + 1, Y1 + 1, Width - 2, Height - 2);
			if (!boxDrawn) {
				g.setColor(params.getForegroundColor());
				GraphicalToolBox.drawRect(g, X1, Y1, Width, Height);
			}
		}
		// and the triangle if necessary
		if (parentGraphicalZone.decorator != null
				&& parentGraphicalZone.decorator.isLinearTfst()) {
			g.setColor(GraphDecoratorConfig.LINEAR_TFST);
		} else {
			g.setColor(params.getForegroundColor());
		}
		if (!boxDrawn && (hasOutgoingTransitions || type == INITIAL)) {
			if (!parentGraphicalZone.getGraphPresentationInfo().isRightToLeft()) {
				final int a = X1 + Width;
				final int b = Y1 + Height;
				GraphicalToolBox.drawLine(g, X_out, Y_out, a, Y1);
				GraphicalToolBox.drawLine(g, a, Y1, a, b);
				GraphicalToolBox.drawLine(g, a, b, X_out, Y_out);
			} else {
				GraphicalToolBox.drawLine(g, X_in - 5, Y_in, X1, Y1);
				GraphicalToolBox.drawLine(g, X1, Y1, X1, Y1 + Height);
				GraphicalToolBox.drawLine(g, X1, Y1 + Height, X_in - 5, Y_in);
			}
		}
		// prints the lines of the box
		for (i = 0; i < n_lines; i++) {
			is_greyed = greyed.get(i);
			l = lines.get(i);
			if (is_greyed) {
				g.setColor(params.getSubgraphColor());
				if (l.startsWith(":")) {
					// if we have a subgraph within a package
					g.setColor(params.getPackageColor());
				}
				if (!existsGraph(i)) {
					g.setColor(params.getUnreachableGraphColor());
				}
				GraphicalToolBox.fillRect(g, X1 + 3, Y1 + 4 + (i) * h_ligne,
						Width - 4, h_ligne);
			}
			if (n_lines > 1
					&& parentGraphicalZone.decorator != null
					&& parentGraphicalZone.decorator.isBoxLineHighlighted(
							boxNumber, i)) {
				g.setColor(GraphDecoratorConfig.DEBUG_HIGHLIGHT);
				GraphicalToolBox.fillRect(g, X1 + 3, Y1 + 4 + (i) * h_ligne,
						Width - 4, h_ligne);
			}
			g.setColor(params.getForegroundColor());
			final TextLayout textlayout = new TextLayout(l, parentGraphicalZone
					.getGraphPresentationInfo().getInput().getFont(),
					g.getFontRenderContext());
			textlayout.draw(g, X1 + 5, Y1 - descent + 3 + (i + 1) * h_ligne);
		}
		// prints the output, if any
		if (parentGraphicalZone.decorator == null) {
			g.setColor(params.getForegroundColor());
		} else {
			g.setColor(parentGraphicalZone.decorator.getBoxOutputColor(
					boxNumber, params.getForegroundColor()));
		}
		final String output = getNonRangeOutput(transduction);
		if (!output.equals("")) {
			g.setFont(parentGraphicalZone.getGraphPresentationInfo()
					.getOutput().getFont());
			g.drawString(output, X1 + 5, Y1 + Height
					+ g.getFontMetrics().getHeight());
		}
		drawRange(g);
	}

	void drawOtherSelected(Graphics2D g, DrawGraphParams params) {
		int i;
		String l;
		if (variable) {
			drawVariableSelected(g, params);
			return;
		}
		if (contextMark) {
			drawContextMarkSelected(g, params);
			return;
		}
		if (morphologicalModeMark) {
			drawMorphologicalModeMarkSelected(g, params);
			return;
		}
                
                if(genericGrfMark) {
                    drawGenericGrfMarkSelected(g,params);
                    return;
                }
                
		g.setColor(params.getForegroundColor());
		// drawing the box
		if (n_lines == 0) {
			g.setColor(params.getSelectedColor());
			GraphicalToolBox.fillRect(g, X_in, Y_in - 10, 15, 20);
			g.setColor(params.getBackgroundColor());
			GraphicalToolBox.drawLine(g, X_in, Y_in, X_in + 15, Y_in);
		} else {
			g.setColor(params.getSelectedColor());
			GraphicalToolBox.fillRect(g, X1, Y1, Width, Height);
		}
		// and the triangle if necessary
		if (hasOutgoingTransitions || type == INITIAL) {
			g.setColor(params.getForegroundColor());
			if (!parentGraphicalZone.getGraphPresentationInfo().isRightToLeft()) {
				GraphicalToolBox.drawLine(g, X_out, Y_out, X1 + Width, Y1);
				GraphicalToolBox.drawLine(g, X1 + Width, Y1, X1 + Width, Y1
						+ Height);
				GraphicalToolBox.drawLine(g, X1 + Width, Y1 + Height, X_out,
						Y_out);
			} else {
				GraphicalToolBox.drawLine(g, X_in - 5, Y_in, X1, Y1);
				GraphicalToolBox.drawLine(g, X1, Y1, X1, Y1 + Height);
				GraphicalToolBox.drawLine(g, X1, Y1 + Height, X_in - 5, Y_in);
			}
		}
		// prints the lines of the box
		g.setColor(params.getBackgroundColor());
		for (i = 0; i < n_lines; i++) {
			l = lines.get(i);
			if (!l.equals("")) {
				final TextLayout textlayout = new TextLayout(l,
						parentGraphicalZone.getGraphPresentationInfo()
								.getInput().getFont(), g.getFontRenderContext());
				textlayout
						.draw(g, X1 + 5, Y1 - descent + 3 + (i + 1) * h_ligne);
			}
		}
		// prints the transduction, if exists
		final String output = getNonRangeOutput(transduction);
		if (!output.equals("")) {
			g.setColor(params.getSelectedColor());
			GraphicalToolBox
					.fillRect(
							g,
							X1 + 5,
							Y1 + Height + g.getFontMetrics().getDescent(),
							g.getFontMetrics(
									parentGraphicalZone
											.getGraphPresentationInfo()
											.getOutput().getFont())
									.stringWidth(output),
							g.getFontMetrics(
									parentGraphicalZone
											.getGraphPresentationInfo()
											.getOutput().getFont()).getHeight() + 1);
			g.setColor(params.getBackgroundColor());
			g.setFont(parentGraphicalZone.getGraphPresentationInfo()
					.getOutput().getFont());
			g.drawString(output, X1 + 5, Y1 + Height
					+ g.getFontMetrics().getHeight());
		}
		drawRange(g);
	}

	private void drawInitial(Graphics2D g, DrawGraphParams params) {
		drawOther(g, params);
		final Color old = g.getColor();
		if (parentGraphicalZone.decorator != null
				&& parentGraphicalZone.decorator.isLinearTfst()) {
			g.setColor(GraphDecoratorConfig.LINEAR_TFST);
		}
		if (!parentGraphicalZone.getGraphPresentationInfo().isRightToLeft())
			GraphicalToolBox.drawLine(g, X_in, Y_in, X_in - 10, Y_in);
		else
			GraphicalToolBox.drawLine(g, X_out - 5, Y_out, X_out + 5, Y_out);
		g.setColor(old);
	}

	private void drawInitialSelected(Graphics2D g, DrawGraphParams params) {
		drawOtherSelected(g, params);
		g.setColor(params.getForegroundColor());
		if (!parentGraphicalZone.getGraphPresentationInfo().isRightToLeft())
			GraphicalToolBox.drawLine(g, X_in - 1, Y_in, X_in - 10, Y_in);
		else
			GraphicalToolBox.drawLine(g, X_out - 5, Y_out, X_out + 5, Y_out);
	}

	/**
	 * Draws the box
	 * 
	 * @param g
	 *            the graphical context
	 */
	public void draw(Graphics2D g, DrawGraphParams params) {
		updateWithContext(g);
		g.setFont(parentGraphicalZone.getGraphPresentationInfo().getInput()
				.getFont());
		h_ligne = g.getFontMetrics().getHeight();
		descent = g.getFontMetrics().getDescent();
		if (singleDragging) {
			// if the box is being dragged just under the mouse,
			// we just draw its frame
			if (type == FINAL)
				drawFinalSingleDrag(g, params);
			else if (type == NORMAL)
				drawOtherSingleDrag(g, params);
			else
				drawInitialSingleDrag(g, params);
		} else if (selected) {
			// if the box was selected before (blue box)
			if (type == FINAL)
				drawFinalSelected(g, params);
			else if (type == NORMAL)
				drawOtherSelected(g, params);
			else
				drawInitialSelected(g, params);
		} else if (standaloneBox) {
			// if the box is in comment and not selected
			if (type == FINAL)
				drawFinal(g, params);
			else if (type == NORMAL)
				drawOtherStandalone(g, params);
			else
				drawInitial(g, params);
		} else {
			// the box is normal
			if (type == FINAL)
				drawFinal(g, params);
			else if (type == NORMAL)
				drawOther(g, params);
			else
				drawInitial(g, params);
		}
	}

	/**
	 * Returns the height of a line of the box.
	 * 
	 * @return the height
	 */
	int get_h_ligne() {
		if (context == null) {
			return 0;
		}
		context.setFont(parentGraphicalZone.getGraphPresentationInfo()
				.getInput().getFont());
		return context.getFontMetrics().getHeight();
	}

	/**
	 * Returns the height of a variable definition line like <code>$a(</code>.
	 * 
	 * @return the height
	 */
	int get_h_variable_ligne() {
		if (context == null) {
			return 0;
		}
		context.setFont(variableFont);
		return context.getFontMetrics().getHeight();
	}

	/**
	 * Returns the width of the box's largest line.
	 * 
	 * @return the width
	 */
	int maxLineWidth() {
		if (context == null) {
			return 0;
		}
		int i, max = 0;
		String s;
		max = 0;
		final FontMetrics f = context.getFontMetrics(parentGraphicalZone
				.getGraphPresentationInfo().getInput().getFont());
		for (i = 0; i < n_lines; i++) {
			s = lines.get(i);
			if (max < f.stringWidth(s))
				max = f.stringWidth(s);
		}
		return max;
	}

	/**
	 * Sets the content of the box
	 * 
	 * @param s
	 *            the content
	 */
	public void setContent(String s) {
		throw new UnsupportedOperationException(
				"setContent should have been overriden!");
	}

	/**
	 * Updates the box by calling the <code>setContent</code> method with the
	 * current box content, which refresh the box properties. It used to
	 * recompute box properties, for example when the user has changed font
	 * sizes.
	 */
	public void update() {
		// this method is used to resize the box after a font change
		setContent(content);
	}

	public ArrayList<GenericGraphBox> getTransitions() {
		return transitions;
	}

	public int getX() {
		return X;
	}

	public int getY() {
		return Y;
	}

	/**
	 * @return the box content as it appears in the text edition field
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Translate a box to the x,y position
	 * 
	 * @param xPos
	 * @param yPos
	 */
	public void translateToPosition(int xPos, int yPos) {
		final int dx = xPos - X;
		final int dy = yPos - Y;
		translate(dx, dy);
	}

	public boolean hasTransitionToItself() {
		// self transition checking
		final Iterator<GenericGraphBox> it = getTransitions().iterator();
		GenericGraphBox g;
		while (it.hasNext()) {
			g = it.next();
			if (g == this)
				return true;
		}
		return false;
	}

	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param b
	 */
	public void setSelected(boolean b) {
		selected = b;
	}

	public void setX(int x1) {
		X = x1;
	}

	public void setY(int y) {
		Y = y;
	}

	public int getWidth() {
		return Width;
	}

	public int getX_in() {
		return X_in;
	}

	public void setX_in(int x_in) {
		X_in = x_in;
	}

	public int getX_out() {
		return X_out;
	}

	public void setX_out(int x_out) {
		X_out = x_out;
	}

	public int getX1() {
		return X1;
	}

	public void setX1(int x1) {
		X1 = x1;
	}

	public int getY1() {
		return Y1;
	}

	public void setY1(int y1) {
		Y1 = y1;
	}

	public int getY_in() {
		return Y_in;
	}

	public void setY_in(int y_in) {
		Y_in = y_in;
	}

	public int getY_out() {
		return Y_out;
	}

	public void setY_out(int y_out) {
		Y_out = y_out;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setTransitions(ArrayList<GenericGraphBox> transitions) {
		this.transitions = transitions;
	}

	void updateWithContext(Graphics2D g) {
		if (context != null) {
			return;
		}
		context = g;
		update();
	}
}
