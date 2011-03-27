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

import java.util.ArrayList;
import java.util.Vector;


/**
 * This class is used to create a clipboard content from a vector of selected boxes.
 *
 * @author Sébastien Paumier
 */
public class MultipleSelection {

    int n = 0;

    ArrayList<GraphBoxInfo> elem;

    /**
     * Creates a new <code>MultipleSelection</code> from a <code>GraphBox</code> vector
     *
     * @param v
     */
    public MultipleSelection(ArrayList<GenericGraphBox> v, boolean graphBoxes) {
        if (graphBoxes) {
            multipleSelectionGraphBoxes(v);
        } else {
            multipleSelectionFstGraphBoxes(v);
        }
    }


    void multipleSelectionGraphBoxes(ArrayList<GenericGraphBox> v) {
        int L = v.size();
        elem = new ArrayList<GraphBoxInfo>();
        for (int i = 0; i < L; i++) {
            // here we numerote the selected states
            GraphBox g = (GraphBox) v.get(i);
            g.identificationNumber = i;
            // and create the corresponding GraphBoxInfo
            GraphBoxInfo gbi = new GraphBoxInfo();
            gbi.X = g.X;
            gbi.Y = g.Y;
            gbi.content = g.content;
            gbi.reachableBoxes = new Vector<Integer>();
            elem.add(gbi);
        }
        for (int i = 0; i < L; i++) {
            // here we create relative transitions
            GraphBox g = (GraphBox) v.get(i);
            GraphBoxInfo gbi = elem.get(i);
            ArrayList<GenericGraphBox> temp = g.transitions;
            int k = temp.size();
            for (int j = 0; j < k; j++) {
                GraphBox dest = (GraphBox) temp.get(j);
                if (dest.identificationNumber != -1) {
                    // we only consider the transitions that lead to box into the selection
                    gbi.reachableBoxes.add(dest.identificationNumber);
                }
            }
        }
        for (int i = 0; i < L; i++) {
            // finally, we put the numero value back to -1
            GraphBox g = (GraphBox) v.get(i);
            g.identificationNumber = -1;
        }
    }

    /**
     * Creates a new <code>MultipleSelection</code> from a <code>FstGraphBox</code> vector
     *
     * @param v just indicates that the objects are <code>FstGraphBox</code>. The value of this
     *          parameter is not taken into account
     */
    void multipleSelectionFstGraphBoxes(ArrayList<GenericGraphBox> v) {
        int L = v.size();
        elem = new ArrayList<GraphBoxInfo>();
        for (int i = 0; i < L; i++) {
            // here we numerote the selected states
            TfstGraphBox g = (TfstGraphBox) v.get(i);
            g.identificationNumber = i;
            // and create the corresponding GraphBoxInfo
            GraphBoxInfo gbi = new GraphBoxInfo();
            gbi.X = g.X;
            gbi.Y = g.Y;
            gbi.content = g.content;
            gbi.reachableBoxes = new Vector<Integer>();
            elem.add(gbi);
        }
        for (int i = 0; i < L; i++) {
            // here we create relative transitions
            TfstGraphBox g = (TfstGraphBox) v.get(i);
            GraphBoxInfo gbi = elem.get(i);
            ArrayList<GenericGraphBox> temp = g.transitions;
            int k = temp.size();
            for (int j = 0; j < k; j++) {
                TfstGraphBox dest = (TfstGraphBox) temp.get(j);
                if (dest.identificationNumber != -1) {
                    // we only consider the transitions that lead to box into the selection
                    gbi.reachableBoxes.add(dest.identificationNumber);
                }
            }
        }
        for (int i = 0; i < L; i++) {
            // finally, we put the numero value back to -1
            TfstGraphBox g = (TfstGraphBox) v.get(i);
            g.identificationNumber = -1;
        }
    }

}