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
package fr.umlv.unitex.graphrendering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

import fr.umlv.unitex.diff.GraphDecorator;
import fr.umlv.unitex.frames.TextAutomatonFrame;
import fr.umlv.unitex.io.GraphIO;
import fr.umlv.unitex.tfst.Bounds;
import fr.umlv.unitex.tfst.tagging.TaggingModel;

/**
 * This class describes a component on which a sentence graph can be drawn.
 *
 * @author Sébastien Paumier
 */
public class TfstGraphicalZone extends GenericGraphicalZone implements
        Printable {

	TaggingModel model;
	
    /**
     * Constructs a new <code>TfstGraphicalZone</code>.
     *
     * @param t         text field to edit box contents
     * @param p         frame that contains the component
     * @param listeners indicates if mouse listeners must be added to the component
     */
    public TfstGraphicalZone(GraphIO gio, TfstTextField t,
                             TextAutomatonFrame p, boolean listeners) {
        super(gio, t, p, null);
        model=new TaggingModel(this);
        setDecorator(new GraphDecorator(model));
        if (listeners) {
            addMouseListener(new FstGraphMouseListener());
        }
    }

    @Override
    protected GenericGraphBox createBox(int x, int y) {
        TfstGraphBox g = new TfstGraphBox(x, y, 2, this);
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
            } else if (e.isControlDown()) {
                /* In the text automaton, Ctrl+click is used to select
                 * a box for tagging */
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
            if (e.isShiftDown() || e.isAltDown() || e.isControlDown()) {
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
            if (e.isShiftDown() || e.isAltDown() || e.isControlDown())
                return;
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
            hasMoved = true;
            int Xtmp = X_end_drag;
            int Ytmp = Y_end_drag;
            X_end_drag = (int) (e.getX() / scaleFactor);
            Y_end_drag = (int) (e.getY() / scaleFactor);
            int dx = X_end_drag - Xtmp;
            int dy = Y_end_drag - Ytmp;
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
     * @param f_old the graphical context
     */
    @Override
    public void paintComponent(Graphics f_old) {
        Graphics2D f = (Graphics2D) f_old;
        f.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                info.antialiasing ? RenderingHints.VALUE_ANTIALIAS_ON
                        : RenderingHints.VALUE_ANTIALIAS_OFF);
        f.setColor(new Color(205, 205, 205));
        f.fillRect(0, 0, getWidth(), getHeight());
        f.setColor(info.backgroundColor);
        f.fillRect(0, 0, getWidth(), getHeight());
        f.setColor(info.foregroundColor);
        f.drawRect(10, 10, getWidth() - 20, getHeight() - 20);
        f.drawRect(9, 9, getWidth() - 18, getHeight() - 18);
        f.setColor(info.foregroundColor);
        if (graphBoxes.size() == 0 || graphBoxes.isEmpty()) {
            return;
        }
        f.setColor(new Color(205, 205, 205));
        f.fillRect(0, 0, getWidth(), getHeight());
        f.setColor(info.backgroundColor);
        f.fillRect(0, 0, getWidth(), getHeight());
        f.setColor(info.foregroundColor);
        Stroke oldStroke = f.getStroke();
        f.setStroke(GraphicalToolBox.frameStroke);
        f.drawRect(10, 10, getWidth() - 20, getHeight() - 20);
        f.setStroke(oldStroke);
        f.setColor(info.foregroundColor);
        drawAllTransitions(f);
        drawAllBoxes(f);
    }

    /**
     * Prints the graph.
     *
     * @param g         the graphical context
     * @param p         the page format
     * @param pageIndex the page index
     */
    public int print(Graphics g, PageFormat p, int pageIndex) {
        if (pageIndex != 0)
            return Printable.NO_SUCH_PAGE;
        Graphics2D f = (Graphics2D) g;
        double DPI = 96.0;
        // (double)Toolkit.getDefaultToolkit().getScreenResolution();
        double WidthInInches = p.getImageableWidth() / 72;
        double realWidthInInches = (getWidth() / DPI);
        double HeightInInches = p.getImageableHeight() / 72;
        double realHeightInInches = (getHeight() / DPI);
        double scale_x = WidthInInches / realWidthInInches;
        double scale_y = HeightInInches / realHeightInInches;
        f.translate(p.getImageableX(), p.getImageableY());
        if (scale_x < scale_y)
            f.scale(0.99 * 0.72 * scale_x, 0.99 * 0.72 * scale_x);
        else
            f.scale(0.99 * 0.72 * scale_y, 0.99 * 0.72 * scale_y);
        f.setColor(info.backgroundColor);
        f.fillRect(0, 0, getWidth(), getHeight());
        f.setColor(info.foregroundColor);
        f.drawRect(10, 10, getWidth() - 20, getHeight() - 20);
        f.drawRect(9, 9, getWidth() - 18, getHeight() - 18);
        f.setColor(info.foregroundColor);
        drawAllTransitions(f);
        drawAllBoxes(f);
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
        Dimension d = new Dimension(1188, 840);
        setSize(d);
        setPreferredSize(new Dimension(d));
    }

    
    private ArrayList<ActionListener> listeners=new ArrayList<ActionListener>();
    private boolean firing=false;
    public void addActionListner(ActionListener l) {
    	listeners.add(l);
    }
    public void removeActionListner(ActionListener l) {
    	if (firing) {
    		throw new IllegalStateException("Cannot remove a listener while firing");
    	}
    	listeners.remove(l);
    }
    protected void fireActionPerformed() {
    	ActionEvent e=new ActionEvent(this,0,null);
    	for (ActionListener l:listeners) {
    		l.actionPerformed(e);
    	}
    }

    
    public void setup(GraphIO g) {
        Dimension d = new Dimension(g.width, g.height);
        setSize(d);
        setPreferredSize(d);
        graphBoxes = g.boxes;
        for (GenericGraphBox b : graphBoxes) {
            b.context = (Graphics2D) this.getGraphics();
            b.parentGraphicalZone = this;
            b.update();
        }
        setGraphPresentationInfo(g.info);
        revalidate();
        repaint();
        fireActionPerformed();
    }

    public boolean isBoxToBeRemoved(TfstGraphBox box) {
    	return model.isToBeRemoved(box);
    }
    
}
