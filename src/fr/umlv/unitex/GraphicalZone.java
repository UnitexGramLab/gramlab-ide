/*
 * Unitex
 *
 * Copyright (C) 2001-2007 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import java.util.*;

import javax.swing.undo.*;

import fr.umlv.unitex.undo.*;

/**
 * This class describes a component on which a graph can be drawn.
 * 
 * @author Sébastien Paumier
 *  
 */
public class GraphicalZone extends GenericGraphicalZone implements Printable {

	boolean dragBegin = true;
	int dX, dY;

	/**
	 * Constructs a new <code>GraphicalZone</code>.
	 * 
	 * @param w
	 *            width of the drawing area
	 * @param h
	 *            height of the drawing area
	 * @param t
	 *            text field to edit box contents
	 * @param p
	 *            frame that contains the component
	 */
	public GraphicalZone(int w, int h, TextField t, GraphFrame p) {
		super(w, h, t, p);
		addMouseListener(new MyMouseListener());
		addMouseMotionListener(new MyMouseMotionListener());
	}

	protected GenericGraphBox newBox(int x, int y, int type, GenericGraphicalZone p) {
		return new GraphBox(x, y, type, (GraphicalZone) p);
	}

	protected void init() {
		if (!((GraphFrame) parentFrame).nonEmptyGraph) {
			GraphBox g, g2;
			// creating the final state
			g = new GraphBox(300, 200, 1, this);
			g.setContent("<E>");
			// and the initial state
			g2 = new GraphBox(70, 200, 0, this);
			g2.n_lignes = 0;
			g2.setContent("<E>");
			addBox(g2);
			addBox(g);
			initText("");
			text.setEditable(false);
		} else {
			for (int i = 0; i < graphBoxes.size(); i++) {
				GraphBox g = (GraphBox) graphBoxes.get(i);
				g.context = (Graphics2D) this.getGraphics();
				g.parentGraphicalZone = this;
				g.update();
			}
		}
	}

	protected GenericGraphBox createBox(int x, int y) {
		GraphBox g = new GraphBox(x, y, 2, this);
		g.setContent("<E>");
		addBox(g);
		return g;
	}

	class MyMouseListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			int boxSelected;
			GraphBox b;
			int x_tmp, y_tmp;

			if (EDITING_MODE == MyCursors.REVERSE_LINK_BOXES
					|| (EDITING_MODE == MyCursors.NORMAL && e.isShiftDown())) {

				// Shift+click
				// reverse transitions
				boxSelected = getSelectedBox((int) (e.getX() / scaleFactor),
						(int) (e.getY() / scaleFactor));
				if (boxSelected != -1) {
					// if we click on a box
					b = (GraphBox) graphBoxes.get(boxSelected);
					if (!selectedBoxes.isEmpty()) {
						// if there are selected boxes, we rely them to the
						// current
						addReverseTransitionsFromSelectedBoxes(b);
						unSelectAllBoxes();
						setModified(true);
					} else {
						if (EDITING_MODE == MyCursors.REVERSE_LINK_BOXES) {
							// if we click on a box while there is no box
							// selected in REVERSE_LINK_BOXES mode,
							// we select it
							b.selected = true;
							selectedBoxes.add(b);
							initText(b.content);
						}
					}
				} else {
					// simple click not on a box
					initText("");
					unSelectAllBoxes();
					//					parentFrame.paintImmediately();
					paintImmediately();
				}
			} else if (EDITING_MODE == MyCursors.CREATE_BOXES
					|| (EDITING_MODE == MyCursors.NORMAL && e.isControlDown())) {
				// Control+click
				// creation of a new box
				b = (GraphBox) createBox((int) (e.getX() / scaleFactor),
						(int) (e.getY() / scaleFactor));
				setModified(true);
				// if some boxes are selected, we rely them to the new one
				if (!selectedBoxes.isEmpty()) {
					addTransitionsFromSelectedBoxes(b, false);
				}
				// then, the only selected box is the new one
				unSelectAllBoxes();
				b.selected = true;
				selectedBoxes.add(b);
				initText("<E>");
			} else if (EDITING_MODE == MyCursors.OPEN_SUBGRAPH
					|| (EDITING_MODE == MyCursors.NORMAL && e.isAltDown())) {
				// Alt+click
				// opening of a sub-graph
				x_tmp = (int) (e.getX() / scaleFactor);
				y_tmp = (int) (e.getY() / scaleFactor);
				boxSelected = getSelectedBox(x_tmp, y_tmp);
				if (boxSelected != -1) {
					// if we click on a box
					b = (GraphBox) graphBoxes.get(boxSelected);
					File file = b.getGraphClicked(y_tmp);
					if (file != null) {
						UnitexFrame.mainFrame.loadGraph(file);
					}
				}
			} else if (EDITING_MODE == MyCursors.KILL_BOXES) {
				// killing a box
				if (!selectedBoxes.isEmpty()) {
					// if boxes are selected, we remove them
					removeSelected();
				} else {
					// else, we check if we clicked on a box
					x_tmp = (int) (e.getX() / scaleFactor);
					y_tmp = (int) (e.getY() / scaleFactor);
					boxSelected = getSelectedBox(x_tmp, y_tmp);
					if (boxSelected != -1) {
						b = (GraphBox) graphBoxes.get(boxSelected);
						b.selected = true;
						selectedBoxes.add(b);
						removeSelected();
					}
				}
			} else {
				boxSelected = getSelectedBox((int) (e.getX() / scaleFactor),
						(int) (e.getY() / scaleFactor));
				if (boxSelected != -1) {
					// if we click on a box
					b = (GraphBox) graphBoxes.get(boxSelected);
					if (!selectedBoxes.isEmpty()) {
						// if there are selected boxes, we rely them to the
						// current one
						addTransitionsFromSelectedBoxes(b, true);
						unSelectAllBoxes();
						setModified(true);
					} else {
						if (!((EDITING_MODE == MyCursors.LINK_BOXES) && (b.type == 1))) {
							// if not, we just select this one, but only if we
							// are not clicking
							// on final state in LINK_BOXES mode
							b.selected = true;
							selectedBoxes.add(b);
							initText(b.content);
						}
					}
				} else {
					// simple click not on a box
					unSelectAllBoxes();
					initText("");
					text.setEditable(false);
				}
			}
			paintImmediately();
			return;
		}

		public void mousePressed(MouseEvent e) {
			int selectedBox;
			if ((EDITING_MODE == MyCursors.NORMAL && (e.isShiftDown()
					|| e.isAltDown() || e.isControlDown()))
					|| (EDITING_MODE == MyCursors.OPEN_SUBGRAPH)
					|| (EDITING_MODE == MyCursors.KILL_BOXES)) {
				return;
			}
			validateTextField();
			X_start_drag = (int) (e.getX() / scaleFactor);
			Y_start_drag = (int) (e.getY() / scaleFactor);
			X_end_drag = X_start_drag;
			Y_end_drag = Y_start_drag;
			X_drag = X_start_drag;
			Y_drag = Y_start_drag;
			dragWidth = 0;
			dragHeight = 0;
			selectedBox = getSelectedBox(X_start_drag, Y_start_drag);
			singleDragging = false;
			dragging = false;
			selecting = false;
			if (selectedBox != -1) {
				// if we start dragging a box
				singleDraggedBox = (GraphBox) graphBoxes.get(selectedBox);
				initText(singleDraggedBox.content);
				if (!singleDraggedBox.selected) {
					dragging = true;
					singleDragging = true;
					singleDraggedBox.singleDragging = true;
				}
			}
			if (!selectedBoxes.isEmpty()) {
				dragging = true;
			}
			if ((selectedBox == -1) && selectedBoxes.isEmpty()) {
				// being drawing a selection rectangle
				dragging = false;
				selecting = true;
				initText("");
			}
			//parentFrame.repaint();
			paintImmediately();
			e.consume();
		}

		public void mouseReleased(MouseEvent e) {
			if (e.isShiftDown() || e.isAltDown() || e.isControlDown())
				return;
			int dx = X_end_drag - X_start_drag;
			int dy = Y_end_drag - Y_start_drag;
			if (singleDragging && dx != 0 && dy != 0) {
				// save position after the dragging
				selectedBoxes.add(singleDraggedBox);
				UndoableEdit edit = new TranslationGroupEdit(selectedBoxes, dx,
						dy);
				postEdit(edit);
				selectedBoxes.remove(singleDraggedBox);
				dragging = false;
				setModified(true);
			}
			if (dragging && EDITING_MODE == MyCursors.NORMAL) {
				// save the position of all the translated boxes
				if (dx != 0 && dy != 0) {
					UndoableEdit edit = new TranslationGroupEdit(selectedBoxes,
							dx, dy);
					postEdit(edit);
				}
			}
			dragging = false;
			initText("");
			text.setEditable(false);
			if (singleDragging) {
				singleDragging = false;
				singleDraggedBox.singleDragging = false;
			} else if (selecting == true) {
				selectByRectangle(X_drag, Y_drag, dragWidth, dragHeight);
				text.setEditable(true);
				selecting = false;
			}
			paintImmediately();
		}

		public void mouseEntered(MouseEvent e) {
			mouseInGraphicalZone = true;
			paintImmediately();
		}

		public void mouseExited(MouseEvent e) {
			mouseInGraphicalZone = false;
			paintImmediately();
		}
	}

	class MyMouseMotionListener implements MouseMotionListener {
		public void mouseDragged(MouseEvent e) {
			int Xtmp = X_end_drag;
			int Ytmp = Y_end_drag;
			X_end_drag = (int) (e.getX() / scaleFactor);
			Y_end_drag = (int) (e.getY() / scaleFactor);
			int dx = X_end_drag - Xtmp;
			int dy = Y_end_drag - Ytmp;
			dX += dx;
			dY += dy;
			if (singleDragging) {
				// translates the single dragged box
				singleDraggedBox.translate(dx, dy);
				setModified(true);
				paintImmediately();
				return;
			}
			if (dragging && EDITING_MODE == MyCursors.NORMAL) {
				// translates all the selected boxes
				setModified(true);
				translateAllSelectedBoxes(dx, dy);
				// if we were dragging, we have nothing else to do
				paintImmediately();
				return;
			}
			if (X_start_drag < X_end_drag) {
				X_drag = X_start_drag;
				dragWidth = X_end_drag - X_start_drag;
			} else {
				X_drag = X_end_drag;
				dragWidth = X_start_drag - X_end_drag;
			}
			if (Y_start_drag < Y_end_drag) {
				Y_drag = Y_start_drag;
				dragHeight = Y_end_drag - Y_start_drag;
			} else {
				Y_drag = Y_end_drag;
				dragHeight = Y_start_drag - Y_end_drag;
			}
			paintImmediately();
		}

		public void mouseMoved(MouseEvent e) {
			Xmouse = (int) (e.getX() / scaleFactor);
			Ymouse = (int) (e.getY() / scaleFactor);
			if ((EDITING_MODE == MyCursors.REVERSE_LINK_BOXES || EDITING_MODE == MyCursors.LINK_BOXES)
					&& !selectedBoxes.isEmpty()) {
				paintImmediately();
			}
		}
	}

	void paintImmediately() {
		paintImmediately(0, 0, getWidth(), getHeight());
	}

	/**
	 * Draws the graph. This method should only be called by the virtual
	 * machine.
	 * 
	 * @param f_old
	 *            the graphical context
	 */
	public void paintComponent(Graphics f_old) {
		setClipZone(f_old.getClipBounds());
		Graphics2D f = (Graphics2D) f_old;
		f.scale(scaleFactor, scaleFactor);
		if (!is_initialised) {
			this.init();
			is_initialised = true;
		}
		if (pref.antialiasing) {
			f.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			f.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		f.setColor(new Color(205, 205, 205));
		f.fillRect(0, 0, getWidth(), getHeight());
		f.setColor(pref.backgroundColor);
		f.fillRect(0, 0, Width, Height);
		if (pref.frame) {
			f.setColor(pref.foregroundColor);
			f.drawRect(10, 10, Width - 20, Height - 20);
			f.drawRect(9, 9, Width - 18, Height - 18);
		}
		f.setColor(pref.foregroundColor);
		File file = ((GraphFrame) parentFrame).getGraph();
		if (pref.filename) {
			if (pref.pathname)
				f.drawString((file != null) ? file.getAbsolutePath() : "", 20,
						Height - 45);
			else
				f.drawString((file != null) ? file.getName() : "", 20,
						Height - 45);
		}
		if (pref.date)
			f.drawString(new Date().toString(), 20, Height - 25);
		drawGrid(f);
		if (mouseInGraphicalZone && !selectedBoxes.isEmpty()) {
			if (EDITING_MODE == MyCursors.REVERSE_LINK_BOXES) {
				drawTransitionsFromMousePointerToSelectedBoxes(f);
			} else if (EDITING_MODE == MyCursors.LINK_BOXES) {
				drawTransitionsFromSelectedBoxesToMousePointer(f);
			}
		}
		drawAllTransitions(f);
		drawAllBoxes(f);
		if (selecting) {
			// here we draw the selection rectangle
			f.setColor(pref.foregroundColor);
			f.drawRect(X_drag, Y_drag, dragWidth, dragHeight);
		}
	}

	/**
	 * Prints the graph.
	 * 
	 * @param g
	 *            the graphical context
	 * @param p
	 *            the page format
	 * @param pageIndex
	 *            the page index
	 */
	public int print(Graphics g, PageFormat p, int pageIndex) {
		if (pageIndex != 0)
			return Printable.NO_SUCH_PAGE;
		Graphics2D f = (Graphics2D) g;
		double DPI = 96.0;
		double WidthInInches = p.getImageableWidth() / 72;
		double realWidthInInches = (Width / DPI);
		double HeightInInches = p.getImageableHeight() / 72;
		double realHeightInInches = (Height / DPI);
		double scale_x = WidthInInches / realWidthInInches;
		double scale_y = HeightInInches / realHeightInInches;
		f.translate(p.getImageableX(), p.getImageableY());
		if (scale_x < scale_y)
			f.scale(0.99 * 0.72 * scale_x, 0.99 * 0.72 * scale_x);
		else
			f.scale(0.99 * 0.72 * scale_y, 0.99 * 0.72 * scale_y);
		f.setColor(pref.backgroundColor);
		f.fillRect(0, 0, Width, Height);
		if (pref.frame) {
			f.setColor(pref.foregroundColor);
			f.drawRect(10, 10, Width - 20, Height - 20);
			f.drawRect(9, 9, Width - 18, Height - 18);
		}
		f.setColor(pref.foregroundColor);
		File file = ((GraphFrame) parentFrame).getGraph();
		if (pref.filename) {
			if (pref.pathname)
				f.drawString((file != null) ? file.getAbsolutePath() : "", 20,
						Height - 45);
			else
				f.drawString((file != null) ? file.getName() : "", 20,
						Height - 45);
		}
		if (pref.date)
			f.drawString(new Date().toString(), 20, Height - 25);
		drawGrid(f);
		drawAllTransitions(f);
		drawAllBoxes(f);
		if (selecting) {
			// here we draw the selection rectangle
			f.drawRect(X_drag, Y_drag, dragWidth, dragHeight);
		}
		return Printable.PAGE_EXISTS;
	}

	/**
	 * @return the preferences for this graphical zone
	 */
	public Preferences getPreferences() {
		return pref;
	}

}