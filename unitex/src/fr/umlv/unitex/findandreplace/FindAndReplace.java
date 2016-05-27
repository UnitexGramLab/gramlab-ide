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
package fr.umlv.unitex.findandreplace;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import fr.umlv.unitex.graphrendering.GenericGraphBox;
import fr.umlv.unitex.graphrendering.GraphBox;
import fr.umlv.unitex.graphrendering.GraphicalZone;

/**
 * This class contains static methods that allow to find and replace the content of one or more boxes.
 *
 * @author Maxime Petit
 */
public class FindAndReplace {

    /**
     * Returns true if and only if this box contains the specified
     * search.
     *
     * @param box the box containing the string to search
     * @param search the sequence to search for
     * @return true if the box contains search, false otherwise
     */
    public static boolean find(GenericGraphBox box, String search) {
        if (box.getContent().contains(search) && box.getType() == GenericGraphBox.NORMAL && !box.getContent().equals("<E>")) {
            box.setSelected(true);
            return true;
        }
        return false;
    }

    /**
     * Returns true if and only if this box matches regex.
     *
     * @param box the box containing the string to search
     * @param regex the sequence to search for
     * @return true if the box matches regex, false otherwise
     */
    public static boolean findRegex(GenericGraphBox box, String regex) {
        if (isRegex(regex) && Pattern.compile(regex).matcher(box.getContent()).find() && box.getType() == GenericGraphBox.NORMAL && !box.getContent().equals("<E>")) {
            box.setSelected(true);
            return true;
        }
        return false;
    }

    /**
     * Returns true if and only if the specified regex is a valid regex.
     *
     * @param regex the regex to test
     * @return true if regex is valid, false otherwise
     */
    private static boolean isRegex(String regex) {
        boolean isRegex;
        try {
            Pattern.compile(regex);
            isRegex = true;
        } catch (PatternSyntaxException e) {
            isRegex = false;
        }
        return isRegex;
    }

    /**
     * Replace the content of g with replace if it contains search.
     *
     * @param g the box containing the string to replace
     * @param search the sequence to search for
     * @param replace the sequence to replace with
     * @param zone the GraphicalZone containing the box
     */
    public static void replace(GenericGraphBox g, String search, String replace, GraphicalZone zone) {
        if (g.getType() == GraphBox.NORMAL && !g.isStandaloneBox() && !g.getContent().equals("<E>")) {
            String newContent = g.getContent().replace(search, replace);
            if (!newContent.equals(g.getContent())) {
                g.setSelected(true);
                zone.getSelectedBoxes().add(g);
                zone.setTextBox(g, newContent);
            }
        }
    }

    /**
     * Replace the content of g with replace if it matches regex.
     *
     * @param g the box containing the string to replace
     * @param regex the sequence to search for
     * @param replace the sequence to replace with
     * @param zone the GraphicalZone containing the box
     */
    public static void replaceRegex(GenericGraphBox g, String regex, String replace, GraphicalZone zone) {
        if (isRegex(regex) && g.getType() == GraphBox.NORMAL && !g.isStandaloneBox() && !g.getContent().equals("<E>")) {
            String newContent = g.getContent().replaceAll(regex, replace);
            if (!newContent.equals(g.getContent())) {
                g.setSelected(true);
                zone.getSelectedBoxes().add(g);
                zone.setTextBox(g, newContent);
            }
        }
    }

    /**
     * Replace the content of each box in boxes with replace if it contains search.
     *
     * @param boxes the list containing the boxes
     * @param search the sequence to search for
     * @param replace the sequence to replace with
     * @param zone the GraphicalZone containing the boxes
     * @return the number of boxes that were modified
     */
    public static int replaceAll(ArrayList<GenericGraphBox> boxes, String search, String replace, GraphicalZone zone) {
        int i = 0;
        for (GenericGraphBox box : boxes) {
            final GraphBox g = (GraphBox) box;
            if (g.getType() == GraphBox.NORMAL && !g.isStandaloneBox() && !box.getContent().equals("<E>")) {
                String newContent = g.getContent().replace(search, replace);
                if (!newContent.equals(g.getContent())) {
                    i++;
                    g.setSelected(true);
                    zone.getSelectedBoxes().add(g);
                    zone.setTextBox(g, newContent);
                }
            } else {
                g.setSelected(false);
            }
        }
        return i;
    }

    /**
     * Replace the content of each box in boxes with replace if it matches regex.
     *
     * @param boxes the list containing the boxes
     * @param regex the sequence to search for
     * @param replace the sequence to replace with
     * @param zone the GraphicalZone containing the boxes
     * @return the number of boxes that were modified
     */
    public static int replaceAllRegex(ArrayList<GenericGraphBox> boxes, String regex, String replace, GraphicalZone zone) {
        int i = 0;
        for (GenericGraphBox box : boxes) {
            final GraphBox g = (GraphBox) box;
            if (isRegex(regex) && g.getType() == GraphBox.NORMAL && !g.isStandaloneBox() && !box.getContent().equals("<E>")) {
                String newContent = g.getContent().replaceAll(regex, replace);
                if (!newContent.equals(g.getContent())) {
                    i++;
                    g.setSelected(true);
                    zone.getSelectedBoxes().add(g);
                    zone.setTextBox(g, newContent);
                }
            } else {
                g.setSelected(false);
            }
        }
        return i;
    }
}
