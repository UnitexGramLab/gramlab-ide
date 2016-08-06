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
package fr.umlv.unitex.graphrendering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

import fr.umlv.unitex.diff.GraphDecorator;
import fr.umlv.unitex.frames.TfstFrame;
import fr.umlv.unitex.io.GraphIO;
import fr.umlv.unitex.tfst.Bounds;
import fr.umlv.unitex.tfst.tagging.TaggingModel;
import fr.umlv.unitex.tfst.tagging.TaggingState;
import fr.umlv.unitex.undo.SelectEdit;

/**
 * This class describes a component on which a sentence graph can be drawn.
 * 
 * @author Sébastien Paumier
 */
public class TfstGraphicalZone extends GenericGraphicalZone implements
		Printable {
	
	TaggingModel model;
	int sentence=-1;

	/**
	 * Constructs a new <code>TfstGraphicalZone</code>.
	 * 
	 * @param t
	 *            text field to edit box contents
	 * @param p
	 *            frame that contains the component
	 * @param listeners
	 *            indicates if mouse listeners must be added to the component
	 */
	public TfstGraphicalZone(GraphIO gio, TfstTextField t,
			TfstFrame p, boolean listeners) {
		super(gio, t, p, null);
		model = new TaggingModel(this);
		setDecorator(new GraphDecorator(model));
		if (listeners) {
			addMouseListener(new FstGraphMouseListener());
		}
	}

	@Override
	protected GenericGraphBox createBox(int x, int y) {
		final TfstGraphBox g = new TfstGraphBox(x, y, 2, this);
		g.setContent("<E>");
		addBox(g);
		return g;
	}

	@Override
	protected GenericGraphBox newBox(int x, int y, int type,
			GenericGraphicalZone p) {
		return new TfstGraphBox(x, y, type, (TfstGraphicalZone) p);
	}

	boolean hasMoved = false;
	boolean scrollingWhileTagging = false;
	int X_start_scrolling;
	int Y_start_scrolling;
	Point originalViewPoint;

	class FstGraphMouseListener extends MouseAdapter {
		final MouseMotionListener motionListener = new FstGraphMouseMotionListener();

		@Override
		public void mouseClicked(MouseEvent e) {
			int boxSelected;
			TfstGraphBox b;
			if (e.isShiftDown()) {
				// Shift+click
				// reverse transitions
				boxSelected = getSelectedBox((int) (e.getX() / scaleFactor),
						(int) (e.getY() / scaleFactor));
				if (boxSelected != -1) {
					// if we click on a box
					b = (TfstGraphBox) graphBoxes.get(boxSelected);
					if (!selectedBoxes.isEmpty()) {
						// if there are selected boxes, we rely them to the
						// current
						addReverseTransitionsFromSelectedBoxes(b);
						unSelectAllBoxes();
					}
				} else {
					// simple click not on a box
					unSelectAllBoxes();
				}
			} else if (e.isControlDown() || e.getButton() == MouseEvent.BUTTON3) {
				/*
				 * In the text automaton, Ctrl+click is used to select a box for
				 * tagging
				 */
				boxSelected = getSelectedBox((int) (e.getX() / scaleFactor),
						(int) (e.getY() / scaleFactor));
				if (boxSelected != -1) {
					// if we click on a box
					b = (TfstGraphBox) graphBoxes.get(boxSelected);
					model.selectBox(b);
				}
			} else {
				boxSelected = getSelectedBox((int) (e.getX() / scaleFactor),
						(int) (e.getY() / scaleFactor));
				if (boxSelected != -1) {
					// if we click on a box
					b = (TfstGraphBox) graphBoxes.get(boxSelected);
					if (!selectedBoxes.isEmpty()) {
						// if there are selected boxes, we rely them to the
						// current
						addTransitionsFromSelectedBoxes(b, true);
						unSelectAllBoxes();
					} else {
						// if not, we just select this one
						b.setSelected(true);
						postEdit(new SelectEdit(selectedBoxes));
						selectedBoxes.add(b);
						fireGraphTextChanged(b.getContent());
					}
				} else {
					// simple click not on a box
					unSelectAllBoxes();
				}
			}
			fireGraphChanged(false);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			int selectedBox;
			addMouseMotionListener(motionListener);
			if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {
				scrollingWhileTagging = true;
				X_start_scrolling = (int) (e.getX() / scaleFactor);
				Y_start_scrolling = (int) (e.getY() / scaleFactor);
				final JScrollPane scroll = ((TfstFrame) parentFrame).getTfstScrollPane();
				final JViewport view = scroll.getViewport();
				originalViewPoint = view.getViewPosition();
				return;
			}
			if (e.isShiftDown() || e.isAltDown()) {
				return;
			}
			validateContent();
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
			if (selectedBox != -1) {
				// if we start dragging a box
				singleDraggedBox = graphBoxes.get(selectedBox);
				fireGraphTextChanged(singleDraggedBox.content);
				if (!singleDraggedBox.isSelected()) {
					dragging = true;
					singleDragging = true;
					singleDraggedBox.singleDragging = true;
				}
			}
			if (!selectedBoxes.isEmpty()) {
				dragging = true;
			}
			fireGraphChanged(false);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			removeMouseMotionListener(motionListener);
			if (e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) {
				scrollingWhileTagging = false;
				X_start_scrolling = -1;
				Y_start_scrolling = -1;
				originalViewPoint = null;
				return;
			}
			if (e.isShiftDown() || e.isAltDown()) {
				return;
			}
			dragging = false;
			if (singleDragging) {
				singleDragging = false;
				singleDraggedBox.singleDragging = false;
				if (hasMoved) {
					fireGraphTextChanged(null);
				}
			}
			fireGraphChanged(false);
			hasMoved = false;
		}
	}

	class FstGraphMouseMotionListener extends MouseMotionAdapter {
		@Override
		public void mouseDragged(MouseEvent e) {
			if (scrollingWhileTagging) {
				/* We want to scroll while tagging the tfst */
				final int X_current_scrolling = (int) (e.getX() / scaleFactor);
				final int Y_current_scrolling = (int) (e.getY() / scaleFactor);
				final int shiftX = X_current_scrolling - X_start_scrolling;
				final int shiftY = Y_current_scrolling - Y_start_scrolling;
				final JScrollPane scroll = ((TfstFrame) parentFrame).getTfstScrollPane();
				final JViewport view = scroll.getViewport();
				int newX = view.getViewPosition().x - shiftX;
				int newY = view.getViewPosition().y - shiftY;
				if (newX < 0)
					newX = 0;
				if (newY < 0)
					newY = 0;
				final int maxX = getWidth() - view.getWidth();
				final int maxY = getHeight() - view.getHeight();
				if (newX > maxX)
					newX = maxX;
				if (newY > maxY)
					newY = maxY;
				view.setViewPosition(new Point(newX, newY));
				X_start_scrolling = X_current_scrolling - shiftX;
				Y_start_scrolling = Y_current_scrolling - shiftY;
				return;
			}
			hasMoved = true;
			final int Xtmp = X_end_drag;
			final int Ytmp = Y_end_drag;
			X_end_drag = (int) (e.getX() / scaleFactor);
			Y_end_drag = (int) (e.getY() / scaleFactor);
			final int dx = X_end_drag - Xtmp;
			final int dy = Y_end_drag - Ytmp;
			if (singleDragging) {
				// translates the single dragged box
				singleDraggedBox.translate(dx, dy);
				fireGraphChanged(true);
			}
			if (dragging) {
				// translates all the selected boxes
				translateAllSelectedBoxes(dx, dy);
				// if we were dragging, we have nothing else to do
			}
		}
	}

	/**
	 * Draws the graph. This method should only be called by the virtual
	 * machine.
	 * 
	 * @param f_old
	 *            the graphical context
	 */
	@Override
	public void paintComponent(Graphics f_old) {
		final Graphics2D f = (Graphics2D) f_old;
		DrawGraphParams params = defaultDrawParams();
		drawGraph(f,params);
	}
		
	@Override
	public void drawGraph(Graphics2D f, DrawGraphParams params) {
		f.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				getGraphPresentationInfo().isAntialiasing() ? RenderingHints.VALUE_ANTIALIAS_ON
						: RenderingHints.VALUE_ANTIALIAS_OFF);
		f.setColor(new Color(205, 205, 205));
		f.fillRect(0, 0, getWidth(), getHeight());
		f.setColor(params.getBackgroundColor());
		f.fillRect(0, 0, getWidth(), getHeight());
		f.setColor(params.getForegroundColor());
		f.drawRect(10, 10, getWidth() - 20, getHeight() - 20);
		f.drawRect(9, 9, getWidth() - 18, getHeight() - 18);
		f.setColor(params.getForegroundColor());
		if (graphBoxes.size() == 0 || graphBoxes.isEmpty()) {
			return;
		}
		f.setColor(new Color(205, 205, 205));
		f.fillRect(0, 0, getWidth(), getHeight());
		f.setColor(params.getBackgroundColor());
		f.fillRect(0, 0, getWidth(), getHeight());
		f.setColor(params.getForegroundColor());
		final Stroke oldStroke = f.getStroke();
		f.setStroke(GraphicalToolBox.frameStroke);
		f.drawRect(10, 10, getWidth() - 20, getHeight() - 20);
		f.setStroke(oldStroke);
		f.setColor(params.getForegroundColor());
		drawAllTransitions(f, params);
		drawAllBoxes(f, params);
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
	@Override
	public int print(Graphics g, PageFormat p, int pageIndex) {
		if (pageIndex != 0)
			return Printable.NO_SUCH_PAGE;
		final Graphics2D f = (Graphics2D) g;
		DrawGraphParams params = defaultDrawParams();
		final double DPI = 96.0;
		// (double)Toolkit.getDefaultToolkit().getScreenResolution();
		final double WidthInInches = p.getImageableWidth() / 72;
		final double realWidthInInches = (getWidth() / DPI);
		final double HeightInInches = p.getImageableHeight() / 72;
		final double realHeightInInches = (getHeight() / DPI);
		final double scale_x = WidthInInches / realWidthInInches;
		final double scale_y = HeightInInches / realHeightInInches;
		f.translate(p.getImageableX(), p.getImageableY());
		if (scale_x < scale_y)
			f.scale(0.99 * 0.72 * scale_x, 0.99 * 0.72 * scale_x);
		else
			f.scale(0.99 * 0.72 * scale_y, 0.99 * 0.72 * scale_y);
		f.setColor(params.getBackgroundColor());
		f.fillRect(0, 0, getWidth(), getHeight());
		f.setColor(params.getForegroundColor());
		f.drawRect(10, 10, getWidth() - 20, getHeight() - 20);
		f.drawRect(9, 9, getWidth() - 18, getHeight() - 18);
		f.setColor(params.getForegroundColor());
		drawAllTransitions(f,params);
		drawAllBoxes(f,params);
		return Printable.PAGE_EXISTS;
	}

	public void setBoundsForSelected(Bounds b) {
		int i, L;
		TfstGraphBox g;
		if (selectedBoxes.isEmpty())
			return;
		L = selectedBoxes.size();
		for (i = 0; i < L; i++) {
			g = (TfstGraphBox) selectedBoxes.get(i);
			g.setBounds(b);
		}
	}

	@Override
	protected void initializeEmptyGraph() {
		final Dimension d = new Dimension(1188, 840);
		setSize(d);
		setPreferredSize(new Dimension(d));
	}

	private final ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();
	private final boolean firing = false;

	public void addActionListner(ActionListener l) {
		listeners.add(l);
	}

	public void removeActionListner(ActionListener l) {
		if (firing) {
			throw new IllegalStateException(
					"Cannot remove a listener while firing");
		}
		listeners.remove(l);
	}

	protected void fireActionPerformed() {
		final ActionEvent e = new ActionEvent(this, 0, null);
		for (final ActionListener l : listeners) {
			l.actionPerformed(e);
		}
	}

	
	private HashMap<Integer,TaggingState[]> stateSelection=new HashMap<Integer,TaggingState[]>();
	
	public void setup(GraphIO g,int sentence) {
		/* First, we save the previous state selection if any */
		if (this.sentence!=-1) {
			if (model.getTaggingStates()!=null && model.getTaggingStates().length!=0) {
				stateSelection.put(this.sentence,model.getTaggingStates());
			}
		}
		final Dimension d = new Dimension(g.getWidth(), g.getHeight());
		setSize(d);
		setPreferredSize(d);
		graphBoxes = g.getBoxes();
		for (final GenericGraphBox b : graphBoxes) {
			b.context = (Graphics2D) this.getGraphics();
			b.parentGraphicalZone = this;
			b.update();
		}
		this.sentence=sentence;
		setGraphPresentationInfo(g.getInfo());
		fireActionPerformed();
		/* Now that anyone is aware of the change, we just have to replace
		 * the state selection by the previous one, if any
		 */
		if (sentence!=-1) {
			TaggingState[] selection=stateSelection.get(sentence);
			if (selection!=null) {
				model.setTaggingStates(selection);
				model.updateAutomatonLinearity();
			}
		}
		revalidate();
		repaint();
	}

	public boolean isBoxToBeRemoved(TfstGraphBox box) {
		return model.isToBeRemoved(box);
	}

	public void clearStateSelection(int n) {
		stateSelection.put(n,null);
		model.resetModel();
	}
	
	public void saveStateSelection(int n) {
		stateSelection.put(n,model.getTaggingStates());
	}
	
	public TaggingModel getTaggingModel() {
		return model;
	}

	public void unsureBoxIsVisible(int index) {
		final TfstGraphBox b = (TfstGraphBox) graphBoxes.get(index);
		final JViewport viewport = ((TfstFrame) parentFrame).getTfstScrollPane()
				.getViewport();
		Rectangle visibleRect = viewport.getViewRect();
		if (visibleRect.width == 0 && visibleRect.height == 0) {
			/*
			 * If the view port has not been given a size, we consider the panel
			 * area as default
			 */
			visibleRect = new Rectangle(0, 0, getWidth(), getHeight());
		}
		/*
		 * If necessary, we adjust the scrolling so that the middle of the box
		 * will be visible
		 */
		int newX = visibleRect.x;
		if (b.X < visibleRect.x + 50) {
			newX = b.X1 - 50;
		} else if ((b.X1 + b.Width) > (visibleRect.x + visibleRect.width)) {
			newX = b.X1 - 50;
		}
		int newY = visibleRect.y;
		if (b.Y < visibleRect.y + 50) {
			newY = b.Y1 - 50;
		} else if ((b.Y1 + b.Height) > (visibleRect.y + visibleRect.height)) {
			newY = b.Y1 - 50;
		}
		viewport.setViewPosition(new Point(newX, newY));
	}

	public void resetAllStateSelections() {
		stateSelection.clear();
	}
	
	public Integer[] getModifiedSentenceIndices() {
		Integer[] tab=new Integer[stateSelection.keySet().size()];
		stateSelection.keySet().toArray(tab);
		Arrays.sort(tab);
		return tab;
	}
	
}
