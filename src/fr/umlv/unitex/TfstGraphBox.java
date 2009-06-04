/*
 * Unitex
 *
 * Copyright (C) 2001-2009 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import fr.umlv.unitex.tfst.Bounds;


/**
 * This class describes a box of a sentence automaton.
 * 
 * @author Sébastien Paumier
 * 
 */
public class TfstGraphBox extends GenericGraphBox {

    private Bounds bounds;
    

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
    public TfstGraphBox(int x, int y, int type, TfstGraphicalZone p) {
        super(x, y, type, p);
    }

    /**
     * Takes a <code>String</code> representing the box content and tokenizes
     * it to divide it into several lines
     * 
     * @param s
     *            the box content
     */
    private void tokenizeText(String s, boolean firstTime) {
        int L = s.length(), i;
        String flechi;
        String canonique;
        String infos;
        if (s.equals("<E>")) {
            /* Nothing to do on the initial empty state */
            return;
        }
        if (firstTime) {
            /*
             * Any other (non final) state is supposed to contain both a content
             * and, as output, 4 integers
             */
            int slash_pos = s.lastIndexOf('/');
            if (slash_pos == -1) {
                throw new AssertionError("Content with no slash: " + s);
            }
            readTokenInfos(s.substring(slash_pos + 1));
            content = s.substring(0, slash_pos);
        }
        char[] line = content.toCharArray();
        if (line[0] != '{') {
            n_lines++;
            lines.add(s);
            greyed.add(new Boolean(false));
            return;
        }
        i = 1;
        flechi = "";
        while (i < L && line[i] != ',') {
            if (line[i] == '\\' && (i + 1) < L)
                i++;
            flechi = flechi.concat(String.valueOf(line[i]));
            i++;
        }
        i++;
        canonique = "";
        while (i < L && line[i] != '.') {
            if (line[i] == '\\' && (i + 1) < L)
                i++;
            canonique = canonique.concat(String.valueOf(line[i]));
            i++;
        }
        i++;
        infos = "";
        while (i < L && line[i] != '}') {
            if (line[i] == '\\' && (i + 1) < L)
                i++;
            infos = infos.concat(String.valueOf(line[i]));
            i++;
        }
        n_lines++;
        lines.add(flechi);
        greyed.add(new Boolean(false));
        if (!(canonique.equals("") || canonique.equals(flechi))) {
            // if inflected form is equal to canonical, we don't insert it twice
            n_lines++;
            lines.add(canonique);
            greyed.add(new Boolean(false));
        }
        transduction = infos;
    }

    @Override
    public void setSelected(boolean b) {
        super.setSelected(b);
        if (b && bounds!=null) {
            TextAutomatonFrame.frame.text.getCaret().setSelectionVisible(true);
            System.out.println("on selectionne de "+bounds.getGlobal_start_in_chars()+" a "+(bounds.getGlobal_end_in_chars()+1));
            TextAutomatonFrame.frame.text.select(bounds.getGlobal_start_in_chars(),
                                                 bounds.getGlobal_end_in_chars()+1);
        } else {
            TextAutomatonFrame.frame.text.select(0,0);
        }
    }

    private void readTokenInfos(String s) {
        Scanner scanner = new Scanner(s);
        try {
            int start_pos_in_tokens = scanner.nextInt();
            if (start_pos_in_tokens==-1) {
                bounds=null;
            } else {
                /* Nothing to do if the bounds are not computable */
                int start_pos_in_chars = scanner.nextInt();
                int start_pos_in_letters = scanner.nextInt();
                int end_pos_in_tokens = scanner.nextInt();
                int end_pos_in_chars = scanner.nextInt();
                int end_pos_in_letters = scanner.nextInt();
                if (scanner.hasNext()) {
                    throw new AssertionError("Malformed token information: " + s);
                }
                bounds=new Bounds(start_pos_in_tokens,start_pos_in_chars,start_pos_in_letters,
                        end_pos_in_tokens,end_pos_in_chars,end_pos_in_letters);
            }
        } catch (InputMismatchException e) {
            throw new AssertionError("Malformed token information: " + s);
        } catch (NoSuchElementException e) {
            throw new AssertionError("Malformed token information: " + s);
        } catch (IllegalStateException e) {
            throw new AssertionError("Malformed token information: " + s);
        }
    }

    
    /**
     * Sets the content of the box
     * 
     * @param s
     *            the content
     */
    @Override
    public void setContent(String s) {
        if (type == FINAL)
            return; // nothing to do if we consider the final state
        content = s;
        String tmp = "";
        n_lines = 0;
        tmp = s;
        transduction = "";
        lines.clear();
        greyed.clear();
        tokenizeText(s, false);

        if (!tmp.equals("<E>")) {
            // dimensions of a full box
            Width = maxLineWidth() + 10;
            Height = n_lines * get_h_ligne() + 6;
        } else {
            // dimensions of an empty box
            Height = 20;
            Width = 15;
        }
        Y1 = Y - Height / 2;
        X_out = x + Width + 5;
    }

    
    public void setContentWithBounds(String s) {
        if (type == FINAL)
            return; // nothing to do if we consider the final state
        content = s;
        String tmp = "";
        n_lines = 0;
        tmp = s;
        transduction = "";
        lines.clear();
        greyed.clear();
        tokenizeText(s, true);

        if (!tmp.equals("<E>")) {
            // dimensions of a full box
            Width = maxLineWidth() + 10;
            Height = n_lines * get_h_ligne() + 6;
        } else {
            // dimensions of an empty box
            Height = 20;
            Width = 15;
        }
        Y1 = Y - Height / 2;
        X_out = x + Width + 5;
    }

    public Bounds getBounds() {
        return bounds;
    }

    public void setBounds(Bounds b) {
        bounds=b;
    }
    
    
    BasicStroke morphologicalStroke=new BasicStroke(2,BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_BEVEL,2,new float[] {10f,10f},4f);
    
    /**
     * Draws a transition to a box.
     * Modified from GenericGraphBox in order
     * to display bold colored transitions.
     * 
     * @param gr
     *            the graphical context
     */
    public void drawTransition(Graphics2D g, GenericGraphBox dest) {
        TfstGraphBox box = (TfstGraphBox) dest;
        if (box.bounds != null) {
            int startPosInChars = box.bounds.getStart_in_chars();
            if (startPosInChars != 0 || box.bounds.getStart_in_letters()!=0
                    || box.content.startsWith("{<E>,")) {
                Stroke old = g.getStroke();
                g.setStroke(morphologicalStroke);
                super.drawTransition(g, dest);
                g.setStroke(old);
            } else {
                super.drawTransition(g, dest);
            }
        } else {
            super.drawTransition(g, dest);
        }
    }
    
    
    private static final Color koreanUntaggedTokenColor=new Color(204,255,51);
    
    @Override
    void drawOther(Graphics2D g) {
        Color old=parentGraphicalZone.pref.backgroundColor;
        if (Config.isKorean() && isKoreanUntaggedToken(content)) {
            parentGraphicalZone.pref.backgroundColor=koreanUntaggedTokenColor;
        }
        super.drawOther(g);
        parentGraphicalZone.pref.backgroundColor=old;
    }

    private boolean isKoreanUntaggedToken(String s) {
        return !s.equals("<E>") && s.charAt(0)!='{';
    }
}
