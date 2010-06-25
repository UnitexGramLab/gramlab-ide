/*
 * Unitex
 *
 * Copyright (C) 2001-2010 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import fr.umlv.unitex.Config;
import fr.umlv.unitex.Preferences;
import fr.umlv.unitex.frames.GraphFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class describes a box of a graph or a sentence graph.
 *
 * @author Sébastien Paumier
 */
public class GenericGraphBox {

    /**
     * Box X coordinate
     */
    public int x;

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
     * Indicates if the box contains a context limit
     */
    public boolean contextMark;

    /**
     * Indicates if the box contains a morphological mode tag
     */
    public boolean morphologicalModeMark;

    /**
     * Indicates if the box is currently selected
     */
    protected boolean selected;

    /**
     * Indicates if the box is in comment (no input/output transition)
     */
    public boolean comment;

    /**
     * Indicates if there is at least one transition that goes out this box
     */
    public boolean hasOutgoingTransitions;

    /**
     * Indicates if there is at least one transition that comes in this box
     */
    protected int hasIncomingTransitions;

    /**
     * Indicates if the box is being dragged alone
     */
    protected boolean singleDragging;

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
    protected String content;

    /**
     * Tokenized box lines
     */
    public final ArrayList<String> lines;

    /**
     * greyed[i]==true if the line i is a subgraph call
     */
    public final ArrayList<Boolean> greyed;

    /**
     * Transduction text, if exists
     */
    public String transduction;

    /**
     * Number of visible lines in the box: 0 if the box contains only the
     * espilon symbol, the number of lines otherwise
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
    protected int h_ligne;

    /**
     * Heigth of the bottom of letters like j, q or g
     */
    protected int descent;

    /**
     * <code>GenericGraphicalZone</code> object that contains the box
     */
    protected GenericGraphicalZone parentGraphicalZone;

    /**
     * Numbers of boxes that can be reached with transitions going out this box
     */
    protected ArrayList<GenericGraphBox> transitions;

    public static final Font variableFont = new Font("Times New Roman", Font.BOLD, 30);

    protected Graphics2D context;

    /**
     * Number of the box
     */
    protected int identificationNumber; // number used to numerote the state

    /**
     * Constructs a new box
     *
     * @param x    X coordinate of the input point of the box
     * @param y    Y coordinate of the input point of the box
     * @param type indicates if the box is initial, final or normal
     * @param p    component on which the box will be drawn
     */
    public GenericGraphBox(int x, int y, int type, GenericGraphicalZone p) {
        this.x = x;
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
        comment = true;
        hasOutgoingTransitions = false;
        hasIncomingTransitions = 0;
        identificationNumber = -1;
    }

    /**
     * Tests if the click point was in a sub-graph call area. In that case, it
     * returns the sub-graph's name
     *
     * @param y Y coordinate of the click point
     * @return the sub-graph's name, or the empty string if no graph was pointed
     *         out by the click
     */
    public File getGraphClicked(int y) {
        int n;
        String s;
        Boolean b;
        n = (y - Y1 - 4) / (h_ligne);
        b = greyed.get(n);
        if (b.booleanValue()) {
            s = lines.get(n);
            if (!s.endsWith(".grf")) {
                s = s + ".grf";
            }
            /* replace ':' by '/' resp. '\\' */
            if (s.startsWith(":")) {
                // if the graph is located in the package repository
                s = s.replace(':', File.separatorChar);
                return new File(Preferences.packagePath(), s.substring(1));
            }
            // otherwise
            File f = new File(s);
            if (Config.getCurrentSystem() == Config.WINDOWS_SYSTEM &&
                    f.isAbsolute()) {
                // first we test if we have an absolute windows pathname,
                // in order to avoid wrong transformations like:
                //
                // C:\\foo\foo.grf  =>  C\\\foo\foo.grf
                //
                return f;
            }
            s = s.replace(':', File.separatorChar);
            if (!f.isAbsolute()) {
                System.out.println();
                File currentGraph = ((GraphFrame) parentGraphicalZone.parentFrame).getGraph();
                if (currentGraph == null) {
                    // if we try to open a subgraph inside a newly created graph with no name
                    f = null;
                    JOptionPane
                            .showMessageDialog(null, "Cannot resolve relative graph path:\n\n"
                                    + s + "\n\nbecause the location of the current graph is\n" +
                                    "not defined (the graph has never been saved).", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                } else {
                    f = new File(currentGraph.getParentFile(), s);
                }
            }
            return f;
        }
        return null;
    }

    /**
     * Adds a transition to a box. If there is allready a transition to this
     * box, it is removed.
     *
     * @param g the destination box
     */
    public void addTransitionTo(GenericGraphBox g) {
        int i;
        if (this.type == FINAL) {
            // if it is the final, we don't allow parent.pref.output transitions
            return;
        }
        i = transitions.indexOf(g);
        if (i == -1) {
            // if the transition to g does not exist, we create it
            transitions.add(g);
            g.hasIncomingTransitions++;
        } else {
            // else, we remove it
            transitions.remove(i);
            g.hasIncomingTransitions--;
        }
        hasOutgoingTransitions = !transitions.isEmpty();
        comment = (!hasOutgoingTransitions && hasIncomingTransitions == 0);
        g.comment = (!g.hasOutgoingTransitions && g.hasIncomingTransitions == 0);
    }

    /**
     * Adds a transition to a box. If there is allready a transition to this
     * box, it is not removed.
     *
     * @param g the destination box
     */

    public void onlyAddTransitionTo(GenericGraphBox g) {
        int i;
        if (this.type == FINAL) {
            // if it is the final, we don't allow parent.pref.output transitions
            return;
        }
        i = transitions.indexOf(g);
        if (i == -1) {
            // if the transition to g does not exist, we create it
            transitions.add(g);
            g.hasIncomingTransitions++;
        }
        hasOutgoingTransitions = !transitions.isEmpty();
        comment = (!hasOutgoingTransitions && hasIncomingTransitions == 0);
        g.comment = (!g.hasOutgoingTransitions && g.hasIncomingTransitions == 0);
    }

    /**
     * removes a box transition
     *
     * @param g the transition's destination box
     */
    public void removeTransitionTo(GenericGraphBox g) {

        int i = transitions.indexOf(g);
        if (i != -1) {
            transitions.remove(i);
            g.hasIncomingTransitions--;
        }
        hasOutgoingTransitions = !transitions.isEmpty();

        comment = (!hasOutgoingTransitions && hasIncomingTransitions == 0);
        g.comment = (!g.hasOutgoingTransitions && g.hasIncomingTransitions == 0);
    }

    /**
     * Translates the box
     *
     * @param dx length of X shift in pixels
     * @param dy length of Y shift in pixels
     */
    public void translate(int dx, int dy) {
        x = x + dx;
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
     * @param Xr X coordinate of the upper left corner of the rectangle
     * @param Yr Y coordinate of the upper left corner of the rectangle
     * @param Wr width of the rectangle
     * @param Hr height of the rectangle
     * @return <code>true</code> if the intersection between the box and the
     *         rectangle is not empty, <code>false</code> otherwise
     */
    public boolean isSelectedByRectangle(int Xr, int Yr, int Wr, int Hr) {
        return !((X1 > (Xr + Wr)) || ((X1 + Width) < Xr) || (Y1 > (Yr + Hr)) || ((Y1 + Height) < Yr));
    }

    /**
     * Draws a transition to a box
     *
     * @param g    the graphical context
     * @param dest the destination box
     */
    public void drawTransition(Graphics2D g, GenericGraphBox dest) {
        g.setColor(parentGraphicalZone.info.foregroundColor);
        if (!parentGraphicalZone.info.rightToLeft) {
            if (dest.X_in > this.X_out) {
                // easiest case: drawing a line
                GraphicalToolBox.drawLine(g, this.X_out, this.Y_out, dest.X_in, dest.Y_in);
                return;
            }
            if (this.equals(dest)) {
                // if the box is relied to itself
                int diametre1 = 10 + Height / 2;
                GraphicalToolBox.drawArc(g, X_out - diametre1 / 2, Y_out - diametre1, diametre1,
                        diametre1, 90, -180);
                GraphicalToolBox.drawArc(g, dest.X_in - diametre1 / 2, dest.Y_in - diametre1,
                        diametre1, diametre1, 90, 180);
                GraphicalToolBox.drawLine(g, X_in, Y_in - diametre1, X_out, Y_out - diametre1);
                return;
            }
            if ((Y1 < (dest.Y1 + dest.Height)) && ((Y1 + Height) > dest.Y1)) {
                int diametre1 = 10 + Height / 2;
                GraphicalToolBox.drawArc(g, X_out - diametre1 / 2, Y_out - diametre1, diametre1,
                        diametre1, 90, -180);
                int diametre2 = 10 + dest.Height / 2;
                GraphicalToolBox.drawArc(g, dest.X_in - diametre2 / 2, dest.Y_in - diametre2,
                        diametre2, diametre2, 90, 180);
                int Xpoint1, Ypoint1, Xpoint2, Ypoint2;
                Xpoint1 = dest.X_in;
                Ypoint1 = dest.Y_in - diametre2;
                Xpoint2 = X_out;
                Ypoint2 = Y_out - diametre1;
                int Xmilieu, Ymilieu, largeurLimite;
                Xmilieu = (Xpoint2 + Xpoint1) / 2;
                Ymilieu = (Ypoint2 + Ypoint1) / 2;
                largeurLimite = diametre1 + diametre2;
                GraphicalToolBox.drawCurve(g, Xpoint1, Ypoint1, Xpoint1 + largeurLimite,
                        Ypoint1, Xmilieu, Ymilieu);
                GraphicalToolBox.drawCurve(g, Xpoint2, Ypoint2, Xpoint2 - largeurLimite,
                        Ypoint2, Xmilieu, Ymilieu);
                return;
            }
            if (Y1 < (dest.Y1 + dest.Height)) {
                int diametre1 = 10 + Height / 2;
                GraphicalToolBox.drawArc(g, X_out - diametre1 / 2, Y_out, diametre1, diametre1,
                        0, 90);
                int diametre2 = 10 + dest.Height / 2;
                GraphicalToolBox.drawArc(g, dest.X_in - diametre2 / 2, dest.Y_in - diametre2,
                        diametre2, diametre2, 180, 90);
                int Xpoint1, Ypoint1, Xpoint2, Ypoint2;
                Xpoint1 = dest.X_in - diametre2 / 2;
                Ypoint1 = dest.Y_in - diametre2 / 2;
                Xpoint2 = X_out + 1 + diametre1 / 2;
                Ypoint2 = Y_out + 1 + diametre1 / 2;
                int hauteurLimite = diametre1 + diametre2 - 20;
                int Xmilieu, Ymilieu;
                Xmilieu = (Xpoint2 + Xpoint1) / 2;
                Ymilieu = (Ypoint2 + Ypoint1) / 2;
                GraphicalToolBox.drawCurve(g, Xpoint1, Ypoint1, Xpoint1,
                        Ypoint1 - hauteurLimite, Xmilieu, Ymilieu);
                GraphicalToolBox.drawCurve(g, Xpoint2, Ypoint2, Xpoint2,
                        Ypoint2 + hauteurLimite, Xmilieu, Ymilieu);
                return;
            }
            int diametre1 = 10 + Height / 2;
            GraphicalToolBox.drawArc(g, X_out - diametre1 / 2, Y_out - diametre1, diametre1,
                    diametre1, 270, 90);
            int diametre2 = 10 + dest.Height / 2;
            GraphicalToolBox.drawArc(g, dest.X_in - diametre2 / 2, dest.Y_in, diametre2,
                    diametre2, 90, 90);
            int Xpoint1, Ypoint1, Xpoint2, Ypoint2;
            Xpoint1 = dest.X_in - diametre2 / 2;
            Ypoint1 = dest.Y_in + diametre2 / 2;
            Xpoint2 = X_out + 1 + diametre1 / 2;
            Ypoint2 = Y_out + 1 - diametre1 / 2;
            int hauteurLimite = diametre1 + diametre2 - 20;
            int Xmilieu, Ymilieu;
            Xmilieu = (Xpoint2 + Xpoint1) / 2;
            Ymilieu = (Ypoint2 + Ypoint1) / 2;
            GraphicalToolBox.drawCurve(g, Xpoint1, Ypoint1, Xpoint1, Ypoint1 + hauteurLimite,
                    Xmilieu, Ymilieu);
            GraphicalToolBox.drawCurve(g, Xpoint2, Ypoint2, Xpoint2, Ypoint2 - hauteurLimite,
                    Xmilieu, Ymilieu);
            return;
        }
        // end of the left to right mode
        if (dest.X_out - 5 < this.X_in - 5) {
            // easiest case: drawing a line
            GraphicalToolBox.drawLine(g, this.X_in - 5, this.Y_in, dest.X_out - 5, dest.Y_out);
            return;
        }
        if (this.equals(dest)) {
            // if the box is relied to itself
            int diametre1 = 10 + Height / 2;
            GraphicalToolBox.drawArc(g, X_in - 5 - diametre1 / 2, Y_in - diametre1, diametre1,
                    diametre1, 90, 180);
            GraphicalToolBox.drawArc(g, dest.X_out - 5 - diametre1 / 2, dest.Y_out - diametre1,
                    diametre1, diametre1, 90, -180);
            GraphicalToolBox.drawLine(g, X_out - 5, Y_out - diametre1, X_in - 5, Y_in
                    - diametre1);
            return;
        }
        if ((Y1 < (dest.Y1 + dest.Height)) && ((Y1 + Height) > dest.Y1)) {
            int diametre1 = 10 + Height / 2;
            GraphicalToolBox.drawArc(g, X_in - 5 - diametre1 / 2, Y_in - diametre1, diametre1,
                    diametre1, 90, 180);
            int diametre2 = 10 + dest.Height / 2;
            GraphicalToolBox.drawArc(g, dest.X_out - 5 - diametre2 / 2, dest.Y_out - diametre2,
                    diametre2, diametre2, 90, -180);
            int Xpoint1, Ypoint1, Xpoint2, Ypoint2;
            Xpoint2 = dest.X_out - 5;
            Ypoint2 = dest.Y_out - diametre2;
            Xpoint1 = X_in - 5;
            Ypoint1 = Y_in - diametre1;
            int Xmilieu, Ymilieu, largeurLimite;
            Xmilieu = (Xpoint2 + Xpoint1) / 2;
            Ymilieu = (Ypoint2 + Ypoint1) / 2;
            largeurLimite = diametre1 + diametre2;
            GraphicalToolBox.drawCurve(g, Xpoint1, Ypoint1, Xpoint1 + largeurLimite, Ypoint1,
                    Xmilieu, Ymilieu);
            GraphicalToolBox.drawCurve(g, Xpoint2, Ypoint2, Xpoint2 - largeurLimite, Ypoint2,
                    Xmilieu, Ymilieu);
            return;
        }
        if (Y1 < (dest.Y1 + dest.Height)) {
            int diametre1 = 10 + Height / 2;
            GraphicalToolBox.drawArc(g, X_in - 5 - diametre1 / 2, Y_in, diametre1, diametre1,
                    90, 90);
            int diametre2 = 10 + dest.Height / 2;
            GraphicalToolBox.drawArc(g, dest.X_out - 5 - diametre2 / 2, dest.Y_out - diametre2,
                    diametre2, diametre2, 270, 90);
            int Xpoint1, Ypoint1, Xpoint2, Ypoint2;
            Xpoint1 = dest.X_out + 1 - 5 + diametre2 / 2;
            Ypoint1 = dest.Y_out - diametre2 / 2;
            Xpoint2 = X_in - 5 - diametre1 / 2;
            Ypoint2 = Y_in + diametre1 / 2;
            int hauteurLimite = diametre1 + diametre2 - 20;
            int Xmilieu, Ymilieu;
            Xmilieu = (Xpoint2 + Xpoint1) / 2;
            Ymilieu = (Ypoint2 + Ypoint1) / 2;
            GraphicalToolBox.drawCurve(g, Xpoint1, Ypoint1, Xpoint1, Ypoint1 - hauteurLimite,
                    Xmilieu, Ymilieu);
            GraphicalToolBox.drawCurve(g, Xpoint2, Ypoint2, Xpoint2, Ypoint2 + hauteurLimite,
                    Xmilieu, Ymilieu);
            return;
        }
        int diametre1 = 10 + Height / 2;
        GraphicalToolBox.drawArc(g, X_in - 5 - diametre1 / 2, Y_in - diametre1, diametre1,
                diametre1, 270, -90);
        int diametre2 = 10 + dest.Height / 2;
        GraphicalToolBox.drawArc(g, dest.X_out - 5 - diametre2 / 2, dest.Y_out, diametre2,
                diametre2, 90, -90);
        int Xpoint1, Ypoint1, Xpoint2, Ypoint2;
        Xpoint1 = dest.X_out - 5 + diametre2 / 2;
        Ypoint1 = dest.Y_out + 1 + diametre2 / 2;
        Xpoint2 = X_in - 5 - diametre1 / 2;
        Ypoint2 = Y_in - diametre1 / 2;
        int hauteurLimite = diametre1 + diametre2 - 20;
        int Xmilieu, Ymilieu;
        Xmilieu = (Xpoint2 + Xpoint1) / 2;
        Ymilieu = (Ypoint2 + Ypoint1) / 2;
        GraphicalToolBox.drawCurve(g, Xpoint1, Ypoint1, Xpoint1, Ypoint1 + hauteurLimite,
                Xmilieu, Ymilieu);
        GraphicalToolBox.drawCurve(g, Xpoint2, Ypoint2, Xpoint2, Ypoint2 - hauteurLimite,
                Xmilieu, Ymilieu);
    }

    /**
     * Draws all transitions that go out of the box
     *
     * @param gr the graphical context
     */
    public void drawTransitions(Graphics2D gr) {
        updateWithContext(gr);
        int i, L;
        GenericGraphBox g;
        if (transitions.isEmpty())
            return;
        L = transitions.size();
        for (i = 0; i < L; i++) {
            g = transitions.get(i);
            drawTransition(gr, g);
        }
    }

    void drawOtherSingleDrag(Graphics2D g) {
        if (comment)
            g.setColor(parentGraphicalZone.info.commentColor);
        else
            g.setColor(parentGraphicalZone.info.foregroundColor);
        // drawing the box
        if (n_lines == 0) {
            GraphicalToolBox.drawLine(g, X_in, Y_in, X_in + 15, Y_in);
            if (!parentGraphicalZone.info.rightToLeft)
                GraphicalToolBox.drawLine(g, X_in + 15, Y1, X_in + 15, Y1 + Height);
            else
                GraphicalToolBox.drawLine(g, X_in, Y1, X_in, Y1 + Height);
        } else {
            GraphicalToolBox.drawRect(g, X1, Y1, Width, Height);
        }
        // and the triangle if necessary
        if (hasOutgoingTransitions || type == INITIAL) {
            if (!parentGraphicalZone.info.rightToLeft) {
                GraphicalToolBox.drawLine(g, X_out, Y_out, X1 + Width, Y1);
                GraphicalToolBox.drawLine(g, X1 + Width, Y1, X1 + Width, Y1 + Height);
                GraphicalToolBox.drawLine(g, X1 + Width, Y1 + Height, X_out, Y_out);
            } else {
                GraphicalToolBox.drawLine(g, X_in - 5, Y_in, X1, Y1);
                GraphicalToolBox.drawLine(g, X1, Y1, X1, Y1 + Height);
                GraphicalToolBox.drawLine(g, X1, Y1 + Height, X_in - 5, Y_in);
            }
        }
    }

    private void drawInitialSingleDrag(Graphics2D g) {
        g.setColor(parentGraphicalZone.info.foregroundColor);
        // drawing the box
        if (n_lines == 0) {
            GraphicalToolBox.drawLine(g, X_in, Y_in, X_in + 15, Y_in);
        } else {
            GraphicalToolBox.drawRect(g, X1, Y1, Width, Height);
        }
        // drawing the entry line
        if (!parentGraphicalZone.info.rightToLeft)
            GraphicalToolBox.drawLine(g, X_in, Y_in, X_in - 10, Y_in);
        else
            GraphicalToolBox.drawLine(g, X_out - 5, Y_out, X_out + 5, Y_out);
        // and the triangle if necessary
        if (hasOutgoingTransitions || type == INITIAL) {
            if (!parentGraphicalZone.info.rightToLeft) {
                GraphicalToolBox.drawLine(g, X_out, Y_out, X1 + Width, Y1);
                GraphicalToolBox.drawLine(g, X1 + Width, Y1, X1 + Width, Y1 + Height);
                GraphicalToolBox.drawLine(g, X1 + Width, Y1 + Height, X_out, Y_out);
            } else {
                GraphicalToolBox.drawLine(g, X_in - 5, Y_in, X1, Y1);
                GraphicalToolBox.drawLine(g, X1, Y1, X1, Y1 + Height);
                GraphicalToolBox.drawLine(g, X1, Y1 + Height, X_in - 5, Y_in);
            }
        }
    }

    private void drawFinalSingleDrag(Graphics2D g) {
        drawFinal(g);
    }

    void drawOtherComment(Graphics2D g) {
        int i;
        Boolean is_greyed;
        String l;
        if (variable) {
            drawVariableComment(g);
            return;
        }
        if (contextMark) {
            drawContextMarkComment(g);
            return;
        }
        g.setColor(parentGraphicalZone.info.commentColor);
        // print lines if the box is empty
        if (n_lines == 0) {
            GraphicalToolBox.drawLine(g, X_in, Y_in, X_in + 15, Y_in);
            if (!parentGraphicalZone.info.rightToLeft)
                GraphicalToolBox.drawLine(g, X_in + 15, Y1, X_in + 15, Y1 + Height);
            else
                GraphicalToolBox.drawLine(g, X_in, Y1, X_in, Y1 + Height);
        } else {
            g.setColor(parentGraphicalZone.info.backgroundColor);
            GraphicalToolBox.fillRect(g, X1 + 1, Y1 + 1, Width - 2, Height - 2);
            g.setColor(parentGraphicalZone.info.commentColor);
        }
        // prints the lines of the box
        for (i = 0; i < n_lines; i++) {
            is_greyed = greyed.get(i);
            l = lines.get(i);
            if (is_greyed.booleanValue()) {
                g.setColor(parentGraphicalZone.info.subgraphColor);
                if (l.startsWith(":")) {
                    // if we have a subgraph within a package
                    g.setColor(parentGraphicalZone.info.packageColor);
                }
                GraphicalToolBox.fillRect(g, X1 + 3, Y1 + 4 + (i) * h_ligne, Width - 4, h_ligne);
                g.setColor(parentGraphicalZone.info.commentColor);

                TextLayout textlayout = new TextLayout(l, parentGraphicalZone.info.input.font, g.getFontRenderContext());
                textlayout.draw(g, X1 + 5, Y1 - descent + 3 + (i + 1) * h_ligne);
            } else {
                TextLayout textlayout = new TextLayout(l, parentGraphicalZone.info.input.font, g.getFontRenderContext());
                textlayout.draw(g, X1 + 5, Y1 - descent + 3 + (i + 1) * h_ligne);
            }
        }
        // prints the transduction, if exists
        g.setColor(parentGraphicalZone.info.foregroundColor);
        if (!transduction.equals("")) {
            g.setFont(parentGraphicalZone.info.output.font);
            g.drawString(transduction, X1 + 5, Y1 + Height
                    + g.getFontMetrics().getHeight());
        }
    }

    private void drawFinal(Graphics2D g) {
        g.setColor(parentGraphicalZone.info.backgroundColor);
        GraphicalToolBox.fillEllipse(g, x, Y - 10, 21, 21);
        g.setColor(parentGraphicalZone.info.foregroundColor);
        GraphicalToolBox.drawEllipse(g, x, Y - 10, 21, 21);
        GraphicalToolBox.drawRect(g, x + 5, Y - 5, 10, 10);
    }

    private void drawFinalSelected(Graphics2D g) {
        g.setColor(parentGraphicalZone.info.selectedColor);
        GraphicalToolBox.fillEllipse(g, x, Y - 10, 21, 21);
        g.setColor(parentGraphicalZone.info.backgroundColor);
        GraphicalToolBox.drawRect(g, x + 5, Y - 5, 10, 10);
    }

    private void drawVariable(Graphics2D g) {
        g.setColor(parentGraphicalZone.info.commentColor);
        g.setFont(variableFont);
        g.drawString(lines.get(0), X1 + 5, Y1
                - g.getFontMetrics().getDescent() + get_h_variable_ligne());
        g.setFont(parentGraphicalZone.info.output.font);
        g.drawString(transduction, X1 + 10, Y1 + Height
                + g.getFontMetrics().getHeight());
    }

    private void drawVariableSelected(Graphics2D g) {
        g.setColor(parentGraphicalZone.info.selectedColor);
        GraphicalToolBox.fillRect(g, X1, Y1, Width, Height);
        g.setColor(parentGraphicalZone.info.commentColor);
        g.setFont(variableFont);
        g.drawString(lines.get(0), X1 + 5, Y1
                - g.getFontMetrics().getDescent() + get_h_variable_ligne());

        g.setColor(parentGraphicalZone.info.selectedColor);
        GraphicalToolBox.fillRect(g, X1 + 5, Y1 + Height + g.getFontMetrics().getDescent(), g
                .getFontMetrics(parentGraphicalZone.info.output.font).stringWidth(
                transduction), g.getFontMetrics(
                parentGraphicalZone.info.output.font).getHeight() + 1);
        g.setColor(parentGraphicalZone.info.backgroundColor);
        g.setFont(parentGraphicalZone.info.output.font);
        g.drawString(transduction, X1 + 5, Y1 + Height
                + g.getFontMetrics().getHeight());
    }

    private void drawVariableComment(Graphics2D g) {
        drawVariable(g);
    }

    private void drawContextMark(Graphics2D g) {
        g.setColor(parentGraphicalZone.info.contextColor);
        g.setFont(variableFont);
        g.drawString(lines.get(0), X1 + 5, Y1
                - g.getFontMetrics().getDescent() + get_h_variable_ligne());
    }

    private void drawContextMarkSelected(Graphics2D g) {
        drawVariableSelected(g);
    }

    private void drawContextMarkComment(Graphics2D g) {
        drawContextMark(g);
    }

    private void drawMorphologicalModeMark(Graphics2D g) {
        g.setColor(parentGraphicalZone.info.morphologicalModeColor);
        g.setFont(variableFont);
        g.drawString(lines.get(0), X1 + 5, Y1
                - g.getFontMetrics().getDescent() + get_h_variable_ligne());
    }

    private void drawMorphologicalModeMarkSelected(Graphics2D g) {
        drawVariableSelected(g);
    }

    void drawOther(Graphics2D g) {
        int i;
        Boolean is_greyed;
        String l;
        if (variable) {
            drawVariable(g);
            return;
        }
        if (contextMark) {
            drawContextMark(g);
            return;
        }
        if (morphologicalModeMark) {
            drawMorphologicalModeMark(g);
            return;
        }
        g.setColor(parentGraphicalZone.info.foregroundColor);
        // drawing the box
        if (n_lines == 0) {
            GraphicalToolBox.drawLine(g, X_in, Y_in, X_in + 15, Y_in);
            if (!parentGraphicalZone.info.rightToLeft)
                GraphicalToolBox.drawLine(g, X_in + 15, Y1, X_in + 15, Y1 + Height);
            else
                GraphicalToolBox.drawLine(g, X_in, Y1, X_in, Y1 + Height);
        } else {
            g.setColor(parentGraphicalZone.info.backgroundColor);
            GraphicalToolBox.fillRect(g, X1 + 1, Y1 + 1, Width - 2, Height - 2);
            g.setColor(parentGraphicalZone.info.foregroundColor);
            GraphicalToolBox.drawRect(g, X1, Y1, Width, Height);
        }
        // and the triangle if necessary
        if (hasOutgoingTransitions || type == INITIAL) {
            if (!parentGraphicalZone.info.rightToLeft) {
                int a = X1 + Width;
                int b = Y1 + Height;
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
            if (is_greyed.booleanValue()) {
                g.setColor(parentGraphicalZone.info.subgraphColor);
                if (l.startsWith(":")) {
                    // if we have a subgraph within a package
                    g.setColor(parentGraphicalZone.info.packageColor);
                }
                GraphicalToolBox.fillRect(g, X1 + 3, Y1 + 4 + (i) * h_ligne, Width - 4, h_ligne);
                g.setColor(parentGraphicalZone.info.foregroundColor);
            }
            TextLayout textlayout = new TextLayout(l, parentGraphicalZone.info.input.font, g.getFontRenderContext());
            textlayout.draw(g, X1 + 5, Y1 - descent + 3 + (i + 1) * h_ligne);
        }
        // prints the output, if any
        g.setColor(parentGraphicalZone.info.foregroundColor);
        if (!transduction.equals("")) {
            g.setFont(parentGraphicalZone.info.output.font);
            g.drawString(transduction, X1 + 5, Y1 + Height
                    + g.getFontMetrics().getHeight());
        }
    }

    void drawOtherSelected(Graphics2D g) {
        int i;
        String l;
        if (variable) {
            drawVariableSelected(g);
            return;
        }
        if (contextMark) {
            drawContextMarkSelected(g);
            return;
        }
        if (morphologicalModeMark) {
            drawMorphologicalModeMarkSelected(g);
            return;
        }
        g.setColor(parentGraphicalZone.info.foregroundColor);
        // drawing the box
        if (n_lines == 0) {
            g.setColor(parentGraphicalZone.info.selectedColor);
            GraphicalToolBox.fillRect(g, X_in, Y_in - 10, 15, 20);
            g.setColor(parentGraphicalZone.info.backgroundColor);
            GraphicalToolBox.drawLine(g, X_in, Y_in, X_in + 15, Y_in);
        } else {
            g.setColor(parentGraphicalZone.info.selectedColor);
            GraphicalToolBox.fillRect(g, X1, Y1, Width, Height);
        }
        // and the triangle if necessary
        if (hasOutgoingTransitions || type == INITIAL) {
            g.setColor(parentGraphicalZone.info.foregroundColor);
            if (!parentGraphicalZone.info.rightToLeft) {
                GraphicalToolBox.drawLine(g, X_out, Y_out, X1 + Width, Y1);
                GraphicalToolBox.drawLine(g, X1 + Width, Y1, X1 + Width, Y1 + Height);
                GraphicalToolBox.drawLine(g, X1 + Width, Y1 + Height, X_out, Y_out);
            } else {
                GraphicalToolBox.drawLine(g, X_in - 5, Y_in, X1, Y1);
                GraphicalToolBox.drawLine(g, X1, Y1, X1, Y1 + Height);
                GraphicalToolBox.drawLine(g, X1, Y1 + Height, X_in - 5, Y_in);
            }
        }
        // prints the lines of the box
        g.setColor(parentGraphicalZone.info.backgroundColor);
        for (i = 0; i < n_lines; i++) {
            l = lines.get(i);
            TextLayout textlayout = new TextLayout(l, parentGraphicalZone.info.input.font, g.getFontRenderContext());
            textlayout.draw(g, X1 + 5, Y1 - descent + 3 + (i + 1) * h_ligne);
        }
        // prints the transduction, if exists
        if (!transduction.equals("")) {
            g.setColor(parentGraphicalZone.info.selectedColor);
            GraphicalToolBox.fillRect(g, X1 + 5, Y1 + Height + g.getFontMetrics().getDescent(),
                    g.getFontMetrics(parentGraphicalZone.info.output.font)
                            .stringWidth(transduction), g.getFontMetrics(
                            parentGraphicalZone.info.output.font).getHeight() + 1);
            g.setColor(parentGraphicalZone.info.backgroundColor);
            g.setFont(parentGraphicalZone.info.output.font);
            g.drawString(transduction, X1 + 5, Y1 + Height
                    + g.getFontMetrics().getHeight());
        }
    }

    private void drawInitial(Graphics2D g) {
        drawOther(g);
        if (!parentGraphicalZone.info.rightToLeft)
            GraphicalToolBox.drawLine(g, X_in, Y_in, X_in - 10, Y_in);
        else
            GraphicalToolBox.drawLine(g, X_out - 5, Y_out, X_out + 5, Y_out);
    }

    private void drawInitialSelected(Graphics2D g) {
        drawOtherSelected(g);
        g.setColor(parentGraphicalZone.info.foregroundColor);
        if (!parentGraphicalZone.info.rightToLeft)
            GraphicalToolBox.drawLine(g, X_in - 1, Y_in, X_in - 10, Y_in);
        else
            GraphicalToolBox.drawLine(g, X_out - 5, Y_out, X_out + 5, Y_out);
    }

    /**
     * Draws the box
     *
     * @param g the graphical context
     */
    public void draw(Graphics2D g) {
        updateWithContext(g);
        g.setFont(parentGraphicalZone.info.input.font);
        h_ligne = g.getFontMetrics().getHeight();
        descent = g.getFontMetrics().getDescent();
        if (singleDragging) {
            // if the box is being dragged just under the mouse,
            // we just draw its frame
            if (type == FINAL)
                drawFinalSingleDrag(g);
            else if (type == NORMAL)
                drawOtherSingleDrag(g);
            else
                drawInitialSingleDrag(g);
        } else if (selected) {
            // if the box was selected before (blue box)
            if (type == FINAL)
                drawFinalSelected(g);
            else if (type == NORMAL)
                drawOtherSelected(g);
            else
                drawInitialSelected(g);
        } else if (comment) {
            // if the box is in comment and not selected
            if (type == FINAL)
                drawFinal(g);
            else if (type == NORMAL)
                drawOtherComment(g);
            else
                drawInitial(g);
        } else {
            // the box is normal
            if (type == FINAL)
                drawFinal(g);
            else if (type == NORMAL)
                drawOther(g);
            else
                drawInitial(g);
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
        context.setFont(parentGraphicalZone.info.input.font);
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
    public int maxLineWidth() {
        if (context == null) {
            return 0;
        }
        int i, max = 0;
        String s;
        max = 0;
        FontMetrics f = context.getFontMetrics(parentGraphicalZone.info.input.font);
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
     * @param s the content
     */
    public void setContent(String s) {
        throw new UnsupportedOperationException("setContent should have been overriden!");
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
        return x;
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

        x = xPos;
        Y = yPos;

        if (type == FINAL) {
            X_in = xPos;
            Y_in = Y;
            X1 = xPos;
            Y1 = Y - 10;
            Y_out = Y_in;
            X_out = X_in + 25;
        } else {
            X1 = xPos;
            Y1 = Y;
            X_in = xPos;
            Y_in = Y + Height / 2;
            X_out = xPos + Width + 5;
            Y_out = Y_in;
        }

    }

    public boolean hasTransitionToItself() {
        //	self transition checking
        Iterator<GenericGraphBox> it = getTransitions().iterator();
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
        x = x1;
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