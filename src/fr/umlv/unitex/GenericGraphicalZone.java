 /*
  * Unitex
  *
  * Copyright (C) 2001-2007 Université de Marne-la-Vallée <unitex@univ-mlv.fr>
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
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import fr.umlv.unitex.undo.*;

/**
 * This class describes a component on which a graph can be drawn.
 * 
 * @author Sébastien Paumier
 *  
 */
public abstract class GenericGraphicalZone extends JComponent {

	/**
	 * Indicates if the graph contains unsaved modifications.
	 */
	public boolean modified = false;

	/**
	 * Width of the drawing area.
	 */
	public int Width;

	/**
	 * Height of the drawing area.
	 */
	public int Height;

	/**
	 * Text field in which the content of boxes can be edited.
	 */
	public JTextField texte;

	/**
	 * ArrayList containing the current selected boxes
	 */
	public ArrayList selectedBoxes;

	/**
	 * ArrayList containing all the graph's boxes
	 */
	public ArrayList graphBoxes;

	protected boolean is_initialised = false;

	/**
	 * Indicates if a grid be drawn in backend
	 */
	public boolean isGrid = false;

	/**
	 * Indicates the size of the grid's cells
	 */
	public int nPixels = 30;

	/**
	 * Graph's rendering properties
	 */
	public Preferences pref = new Preferences();

	/**
	 * <code>JInternalFrame</code> that contains this component
	 */
	public JInternalFrame parentFrame;

	/**
	 * Undo/redo Manager
	 */
	//private UndoManager manager;
	protected UndoableEditSupport support = new UndoableEditSupport();

	/**
	 * Zoom factor
	 */
	public double scaleFactor = 1.0;
	protected int Xmouse, Ymouse;
	protected boolean mouseInGraphicalZone = false;
  private Rectangle clipZone;
    
	/**
	 * Indicates mouse's editing mode
	 */
	public int EDITING_MODE = MyCursors.NORMAL;

	/**
	 * Constructs a new <code>GenericGraphicalZone</code>.
	 * 
	 * @param w
	 *            width of the drawing area
	 * @param h
	 *            heig ht of the drawing area
	 * @param t
	 *            text field to edit box contents
	 * @param p
	 *            frame that contains the component
	 */
	public GenericGraphicalZone(int w, int h, JTextField t,
			final JInternalFrame p) {
		super();
		Width = w;
		Height = h;
		texte = t;
		texte.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent arg0) {
				// nothing to do
			}
			public void insertUpdate(DocumentEvent arg0) {
				((GraphFrame) p).setRedoEnabled(false);
				((GraphFrame) p).setUndoEnabled(false);
			}
			public void removeUpdate(DocumentEvent arg0) {
				 // nothing to do
			}
		});
		parentFrame = p;
		selectedBoxes = new ArrayList();
		graphBoxes = new ArrayList();
		setBackground(Color.white);
		setLayout(new BorderLayout());
		texte.setEditable(false);
		pref = Preferences.getCloneOfPreferences();
	}

	/**
	 * Constructs a new <code>GenericGraphicalZone</code>.
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
	public GenericGraphicalZone(int w, int h, JTextField t,
			final TextAutomatonFrame p) {
		super();
		Width = w;
		Height = h;
		texte = t;
		parentFrame = p;
		selectedBoxes = new ArrayList();
		graphBoxes = new ArrayList();
		setBackground(Color.white);
		setLayout(new BorderLayout());
		texte.setEditable(false);
		pref = Preferences.getCloneOfPreferences();
	}

	protected abstract void init();


    
  /*
	 * Methods for adding and creating boxes
	 *  
	 */

	/**
	 * Adds a graph box to the graph
	 * 
	 * @param g the graph box
	 */
	public void addBox(GenericGraphBox g) {
		// intital an terminal state must not edit
		if (graphBoxes.size() >= 2) {
			UndoableEdit edit = new AddBoxeEdit(g, graphBoxes, this);
			postEdit(edit);
		}
		graphBoxes.add(g);
	}

	protected abstract GenericGraphBox createBox(int x, int y);

	protected abstract GenericGraphBox newBox(int x, int y, int type, GenericGraphicalZone p);

    
	/**
	 * Paste a graph box selection in the graph. The created boxes will be the
	 * new box selection.
	 * 
	 * @param m the graph box selection
	 */
	public void pasteSelection(MultipleSelection m) {
		ArrayList v = m.elem;
		GenericGraphBox g;
		GraphBoxInfo tmp;
		unSelectAllBoxes();
		for (int i = 0; i < v.size(); i++) {
			tmp = (GraphBoxInfo) v.get(i);
			g = createBox(tmp.X + 20 * m.n, tmp.Y + 20 * m.n);
			g.setContent(tmp.content);
			g.selected = true;
			selectedBoxes.add(g);
		}
		for (int i = 0; i < selectedBoxes.size(); i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			tmp = tmp = (GraphBoxInfo) v.get(i);
			Vector vec = tmp.reachableBoxes;
			int taille = vec.size();
			for (int j = 0; j < taille; j++) {
				Integer n = (Integer) vec.get(j);
				g.addTransitionTo((GenericGraphBox) selectedBoxes.get(n
						.intValue()));
			}
		}
		setModified(true);
		initText("");
		repaint();
	}

	/**
	 * Sets the text field content
	 * 
	 * @param s the new content
	 */
	public void initText(String s) {
		if (texte instanceof TextField) {
			((TextField) texte).initText(s);
		} else if (texte instanceof FstTextField) {
			((FstTextField) texte).initText(s);
		}
	}

	/**
	 * Validates the text field content
	 *  
	 */
	public void validateTextField() {
		if (texte instanceof TextField) {
			((TextField) texte).validateTextField();
		} else if (texte instanceof FstTextField) {
			((FstTextField) texte).validateTextField();
		}
	}

	/**
	 * Indicates if the graph must be marked as modified are not
	 * 
	 * @param b <code>true</code> if the graph must be marked as modified,
	 *          <code>false</code> otherwise
	 */
	public void setModified(boolean b) {
		if (parentFrame instanceof GraphFrame) {
			((GraphFrame) parentFrame).setModified(b);
		} else if (parentFrame instanceof TextAutomatonFrame) {
			((TextAutomatonFrame) parentFrame).setModified(b);
		}
	}

	/**
	 * Removes all transitions that go to a specified graph box
	 * 
	 * @param dest the target graph box
	 */
	public void removeTransitionTo(GenericGraphBox dest) {
		int i, L, pos;
		GenericGraphBox g;
		if (graphBoxes.isEmpty())
			return;
		L = graphBoxes.size();
		for (i = 0; i < L; i++) {
			g = (GenericGraphBox) graphBoxes.get(i);
			pos = g.transitions.indexOf(dest);
			if (pos != -1) {
				g.transitions.remove(pos);
			}
		}
	}

	/**
	 * Removes all transitions that go to a specified graph box
	 * 
	 * @param dest the target graph box
	 * @return the boxes which had transition to dest
	 */
	public ArrayList getTransitionTo(GenericGraphBox dest) {
		int i, L, pos;
		ArrayList list = new ArrayList();
		GenericGraphBox g;
		if (graphBoxes.isEmpty())
			return null;
		L = graphBoxes.size();
		for (i = 0; i < L; i++) {
			g = (GenericGraphBox) graphBoxes.get(i);
			pos = g.transitions.indexOf(dest);
			if (pos != -1) {
				list.add(g);
			}
		}
		return list;
	}

	/**
	 * Removes all transitions that go selected graph boxes
	 *  
	 */
	public void removeTransitionsToSelected() {
		int i, L;
		GenericGraphBox g;
		if (selectedBoxes.isEmpty())
			return;
		L = selectedBoxes.size();
		for (i = 0; i < L; i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			if (g.type == 2) {
				removeTransitionTo(g);
			}
		}
	}

	/**
	 * Remove all selected graph boxes
	 *  
	 */
	public void removeSelected() {
		int i, L;
		GenericGraphBox g;
		if (selectedBoxes.isEmpty())
			return;
		L = selectedBoxes.size();
		UndoableEdit edit = new DeleteBoxGroupEdit(selectedBoxes, graphBoxes,
				this);
		postEdit(edit);
		for (i = 0; i < L; i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			if (g.type == 2) {
				graphBoxes.remove(g);
			}
			removeTransitionsToSelected();
		}
		unSelectAllBoxes();
		repaint();
	}

	/**
	 * Sets the content for all selected graph boxes
	 * 
	 * @param s the new content
	 */
	public void setTextForSelected(String s) {
		int i, L;
		GenericGraphBox g;
		if (selectedBoxes.isEmpty())
			return;
		if (s.equals("")) {
			// if we want to destroy the boxes
			removeSelected();
			return;
		}
		L = selectedBoxes.size();
		if (L == 1) {
			g = (GenericGraphBox) selectedBoxes.get(0);
			AbstractUndoableEdit edit = new BoxeTextEdit(g, s, this);
			postEdit(edit);
			g.setContent(s);
		} else {
			BoxeGroupTextEdit edit = new BoxeGroupTextEdit(selectedBoxes, s,
					this);
			postEdit(edit);
			for (i = 0; i < L; i++) {
				g = (GenericGraphBox) selectedBoxes.get(i);
				g.setContent(s);
			}
		}
	}

	/**
	 * Finds the box that is selected by a click
	 * 
	 * @param x X coordinate of the click
	 * @param y Y coordinate of the click
	 * @return the position of the box in the <code>graphBoxes</code> vector,
	 *         or -1 if no box was selected by the click
	 */
	public int getSelectedBox(int x, int y) {
		int i, L;
		GenericGraphBox g;
		L = graphBoxes.size();
		for (i = 0; i < L; i++) {
			g = (GenericGraphBox) graphBoxes.get(i);
			if (x >= g.x && x <= g.x + g.Width && y >= g.Y1
					&& y <= g.Y1 + g.Height)
				return i;
		}
		return -1;
	}

	/**
	 * Unselect all selected graph boxes
	 *  
	 */
	public void unSelectAllBoxes() {
		GenericGraphBox g;
		while (!selectedBoxes.isEmpty()) {
			g = (GenericGraphBox) selectedBoxes.get(0);
			g.selected = false;
			selectedBoxes.remove(0);
		}
		texte.setEditable(false);
	}

	/**
	 * Select all graph boxes
	 *  
	 */
	public void selectAllBoxes() {
		unSelectAllBoxes();
		int L = L = graphBoxes.size();
		for (int i = 0; i < L; i++) {
			GenericGraphBox g = (GenericGraphBox) graphBoxes.get(i);
			g.selected = true;
			selectedBoxes.add(g);
			initText(g.content);
		}
		texte.setEditable(false);
		repaint();
	}

	/**
	 * Selects all graph boxes that have a non empty intersection with a
	 * selection rectangle
	 * 
	 * @param x X coordinate of the selection rectangle
	 * @param y Y coordinate of the selection rectangle
	 * @param w width of the selection rectangle
	 * @param h height of the selection rectangle
	 */
	public void selectByRectangle(int x, int y, int w, int h) {
		int i, L;
		GenericGraphBox g;
		L = graphBoxes.size();
		for (i = 0; i < L; i++) {
			g = (GenericGraphBox) graphBoxes.get(i);
			if (g.isSelectedByRectangle(x, y, w, h)) {
				g.selected = true;
				selectedBoxes.add(g);
				initText(g.content);
			}
		}
		if (!selectedBoxes.isEmpty()) {
			UndoableEdit edit = new SelectEdit(selectedBoxes);
			postEdit(edit);
		}
	}

	/**
	 * Translates all selected graph boxes
	 * 
	 * @param dx value to be added to X coordinates
	 * @param dy value to be added to Y coordinates
	 */
	public void translateAllSelectedBoxes(int dx, int dy) {
		int i, L;
		GenericGraphBox g;
		if (selectedBoxes.isEmpty())
			return;
		L = selectedBoxes.size();
		for (i = 0; i < L; i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			g.translate(dx, dy);
		}
	}

	protected int X_start_drag, Y_start_drag;
	protected int X_end_drag, Y_end_drag;
	protected int X_drag, Y_drag, dragWidth, dragHeight;
	protected boolean selecting = false;
	protected boolean dragging = false;
	protected boolean singleDragging = false;
	protected GenericGraphBox singleDraggedBox;

	/**
	 * Adds transitions from all selected boxes to a specified graph box
	 * 
	 * @param dest the target graph box
	 * @param save True if we went to save the state for do undo action
	 */
	public void addTransitionsFromSelectedBoxes(GenericGraphBox dest,
			boolean save) {
		int i, L;
		GenericGraphBox g;
		if (selectedBoxes.isEmpty())
			return;
		L = selectedBoxes.size();
		if (save) {
			UndoableEdit edit = new TransitionGroupEdit(selectedBoxes, dest,
					this);
			postEdit(edit);
		}
		for (i = 0; i < L; i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			g.addTransitionTo(dest);
		}
	}

	/**
	 * Adds transitions from a specified graph box to all selected boxes
	 * 
	 * @param src the source graph box
	 */
	public void addReverseTransitionsFromSelectedBoxes(GenericGraphBox src) {
		int i, L;
		GenericGraphBox g;
		if (selectedBoxes.isEmpty())
			return;
		L = selectedBoxes.size();
		for (i = 0; i < L; i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			UndoableEdit edit = new TransitionEdit(src, g);
			postEdit(edit);
			src.addTransitionTo(g);
		}
	}

	/**
	 * Draws all graph's transitions
	 * 
	 * @param gr the graphical context
	 */
	public void drawAllTransitions(Graphics2D gr) {
		int i, L;
		GenericGraphBox g;
		if (graphBoxes.isEmpty())
			return;
		L = graphBoxes.size();
		for (i = 0; i < L; i++) {
			g = (GenericGraphBox) graphBoxes.get(i);
			g.drawTransitions(gr);
		}
	}

	/**
	 * Adds transitions from the last mouse click position to all selected boxes
	 * 
	 * @param gr the graphical context
	 */
	public void drawTransitionsFromMousePointerToSelectedBoxes(Graphics2D gr) {
		GenericGraphBox temp = newBox(Xmouse, Ymouse, 2, this);
		GenericGraphBox g;
		temp.X_out = Xmouse;
		temp.Y_out = Ymouse;
		int L = selectedBoxes.size();
		for (int i = 0; i < L; i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			temp.drawTransition(gr, g);
		}
	}

	/**
	 * Adds transitions from all selected boxes to the last mouse click position
	 * 
	 * @param gr the graphical context
	 */
	public void drawTransitionsFromSelectedBoxesToMousePointer(Graphics2D gr) {
		GenericGraphBox temp = newBox(Xmouse, Ymouse, 2, this);
		GenericGraphBox g;
		temp.X_out = Xmouse;
		temp.Y_out = Ymouse;
		int L = selectedBoxes.size();
		for (int i = 0; i < L; i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			g.drawTransition(gr, temp);
		}
	}

	/**
	 * Draws all boxes of the graph
	 * 
	 * @param gr the graphical context
	 */
	public void drawAllBoxes(Graphics2D gr) {
		int i, L;
		GenericGraphBox g;
		if (graphBoxes.isEmpty())
			return;
		L = graphBoxes.size();
		for (i = 0; i < L; i++) {
			g = (GenericGraphBox) graphBoxes.get(i);
			g.draw(gr);
		}
	}

	/**
	 * Draws the grid of the graph if the <code>isGrid</code> field is set to
	 * <code>true</code>
	 * 
	 * @param f the graphical context
	 */
	public void drawGrid(Graphics2D f) {
		if (!isGrid)
			return;
		int x, y;
		f.setColor(pref.foregroundColor);
		for (x = 10; x < Width - 20; x = x + nPixels)
			for (y = 10; y < Height - 20; y = y + nPixels)
				f.drawLine(x, y, x + 1, y);
	}


	/*
	 * Box alignment methods
	 *  
	 */

	/**
	 * Aligns horizontally all selected boxes on the upper box
	 */
	public void HTopAlign() {
		int y1, i;
		GenericGraphBox g;
		if (selectedBoxes.isEmpty())
			return;
		g = (GenericGraphBox) selectedBoxes.get(0);
		y1 = g.Y1;
		for (i = 1; i < selectedBoxes.size(); i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			if (g.Y1 < y1)
				y1 = g.Y1;
		}
		// now, y1 is the value that will be common to the selected boxes
		for (i = 0; i < selectedBoxes.size(); i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			int dy = y1 - g.Y1;
			UndoableEdit edit = new TranslationEdit(g, 0, dy);
			postEdit(edit);
			g.translate(0, dy);
		}
		repaint();
	}

	/**
	 * Aligns horizontally all selected boxes on the average Y coordinate of
	 * these boxes
	 */
	public void HCenterAlign() {
		if (selectedBoxes.isEmpty())
			return;
		int y1, i;
		GenericGraphBox g;
		if (selectedBoxes.isEmpty())
			return;
		y1 = 0;
		for (i = 0; i < selectedBoxes.size(); i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			y1 = y1 + g.Y;
		}
		y1 = y1 / selectedBoxes.size();
		// now, y1 is the value that will be common to the selected boxes
		for (i = 0; i < selectedBoxes.size(); i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			int dy = y1 - g.Y;
			UndoableEdit edit = new TranslationEdit(g, 0, dy);
			postEdit(edit);
			g.translate(0, dy);
		}
		repaint();
	}

	/**
	 * Aligns horizontally all selected boxes on the lower box
	 */
	public void HBottomAlign() {
		int y1, i;
		GenericGraphBox g;
		if (selectedBoxes.isEmpty())
			return;
		g = (GenericGraphBox) selectedBoxes.get(0);
		y1 = g.Y1 + g.Height;
		for (i = 1; i < selectedBoxes.size(); i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			if ((g.Y1 + g.Height) > y1)
				y1 = g.Y1 + g.Height;
		}
		// now, y1 is the value that will be common to the selected boxes
		for (i = 0; i < selectedBoxes.size(); i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			int dy = y1 - (g.Y1 + g.Height);
			UndoableEdit edit = new TranslationEdit(g, 0, dy);
			postEdit(edit);
			g.translate(0, dy);
		}
		repaint();
	}

	/**
	 * Aligns vertically all selected boxes on the leftmost box
	 */
	public void VLeftAlign() {
		int x1, i;
		GenericGraphBox g;
		if (selectedBoxes.isEmpty())
			return;
		g = (GenericGraphBox) selectedBoxes.get(0);
		x1 = g.X1;
		for (i = 1; i < selectedBoxes.size(); i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			if (g.X1 < x1)
				x1 = g.X1;
		}
		// now, x1 is the value that will be common to the selected boxes
		for (i = 0; i < selectedBoxes.size(); i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			int dx = x1 - g.X1;
			UndoableEdit edit = new TranslationEdit(g, dx, 0);
			postEdit(edit);
			g.translate(dx, 0);
		}
		repaint();
	}

	/**
	 * Aligns vertically all selected boxes on the average X coordinate of these
	 * boxes
	 */
	public void VCenterAlign() {
		int x1, i;
		GenericGraphBox g;
		if (selectedBoxes.isEmpty())
			return;
		g = (GenericGraphBox) selectedBoxes.get(0);
		x1 = 0;
		for (i = 0; i < selectedBoxes.size(); i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			x1 = x1 + (g.X1 + g.Width / 2);
		}
		x1 = x1 / selectedBoxes.size();
		// now, x1 is the value that will be common to the selected boxes
		for (i = 0; i < selectedBoxes.size(); i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			int dx = x1 - (g.X1 + g.Width / 2);
			UndoableEdit edit = new TranslationEdit(g, dx, 0);
			postEdit(edit);
			g.translate(dx, 0);
		}
		repaint();
	}

	/**
	 * Aligns vertically all selected boxes on the rightmost box
	 */
	public void VRightAlign() {
		int x1, i;
		GenericGraphBox g;
		if (selectedBoxes.isEmpty())
			return;
		g = (GenericGraphBox) selectedBoxes.get(0);
		x1 = g.X1 + g.Width;
		for (i = 1; i < selectedBoxes.size(); i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			if ((g.X1 + g.Width) > x1)
				x1 = g.X1 + g.Width;
		}
		// now, x1 is the value that will be common to the selected boxes
		for (i = 0; i < selectedBoxes.size(); i++) {
			g = (GenericGraphBox) selectedBoxes.get(i);
			int dx = x1 - (g.X1 + g.Width);
			UndoableEdit edit = new TranslationEdit(g, dx, 0);
			postEdit(edit);
			g.translate(dx, 0);
		}
		repaint();
	}

	/**
	 * Sets the <code>isGrid</code> field
	 * 
	 * @param b <code>true</code> if the graph must be marked as modified,
	 *          <code>false</code> otherwise
	 */
	public void setGrid(boolean b) {
		isGrid = b;
		repaint();
	}

	/**
	 * Sets the <code>isGrid</code> field
	 * 
	 * @param b <code>true</code> if the graph must be marked as modified,
	 *          <code>false</code> otherwise
	 * @param n size of grid's cells
	 */
	public void setGrid(boolean b, int n) {
		isGrid = b;
		nPixels = n;
		repaint();
	}

	/**
	 * Updates all graph's boxes
	 *  
	 */
	public void updateAllBoxes() {
		GenericGraphBox g;
		int i;
		for (i = 0; i < graphBoxes.size(); i++) {
			g = (GenericGraphBox) graphBoxes.get(i);
			g.update();
		}
		repaint();
	}

	/**
	 * Indicates if the graph must be drawn from right to left or not.
	 * 
	 * @param b <code>true</code> if the graph must be drawn from right to
	 *          left, <code>false</code> otherwise
	 */
	public void setRightToLeft(boolean b) {
		pref.rightToLeft = b;
	}

	public void addUndoableEditListener(UndoableEditListener listener) {
		support.addUndoableEditListener(listener);
	}

	public void removeUndoableEditListener(UndoableEditListener listener) {
		support.removeUndoableEditListener(listener);
	}

	public void postEdit(UndoableEdit e) { // le fireUndoableEditUpdate....
		support.postEdit(e);
		parentFrame.repaint();
	}

	/**
	 * @return selected boxes of the graph
	 */
	public ArrayList getSelectedBoxes() {
		return selectedBoxes;
	}

   public Rectangle getClipZone() {
    return clipZone;
   }

   public void setClipZone(Rectangle r) {
    clipZone=r;
   }

}
