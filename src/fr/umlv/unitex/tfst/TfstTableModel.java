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

package fr.umlv.unitex.tfst;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import fr.umlv.unitex.graphrendering.GenericGraphBox;
import fr.umlv.unitex.graphrendering.TfstGraphBox;

/**
 * This model is used to present a sentence automaton as a table.
 *
 * @author paumier
 */
public class TfstTableModel extends AbstractTableModel {

    private int nColumns;
    private final ArrayList<TokenTags> lines = new ArrayList<TokenTags>();
    private final TagFilter filter;
    private final boolean delafStyle;

    public TfstTableModel(TagFilter f, final boolean delafStyle) {
        filter = f;
        this.delafStyle = delafStyle;
        filter.addFilterListener(new FilterListener() {
            public void filterChanged() {
                nColumns = 1;
                for (TokenTags t : lines) {
                    t.refreshFilter(filter, delafStyle);
                    int max = 1 + t.getInterpretationCount();
                    if (max > nColumns) nColumns = max;
                }
                fireTableStructureChanged();
                fireTableDataChanged();
            }
        });
    }

    public int getColumnCount() {
        return nColumns;
    }

    public int getRowCount() {
        return lines.size();
    }

    @Override
    public String getColumnName(int column) {
        return (column == 0) ? "Form" : ("POS sequence #" + column);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        TokenTags t = lines.get(rowIndex);
        if (columnIndex == 0) return t.getContent();
        if (columnIndex - 1 < t.getInterpretationCount()) {
            return t.getInterpretation(columnIndex - 1);
        }
        return "";
    }

    public void init(ArrayList<GenericGraphBox> boxes) {
        lines.clear();
        if (emptySentenceGraph(boxes)) return;
    	boolean[] boxStartingTokens=new boolean[boxes.size()];
    	for (int i=0;i<boxStartingTokens.length;i++) {
    		boxStartingTokens[i]=true;
    	}
    	for (int i=0;i<boxStartingTokens.length;i++) {
    		TfstGraphBox b=(TfstGraphBox) boxes.get(i);
    		for (GenericGraphBox b2:b.getTransitions()) {
    			if (i!=0 && b.isNextBoxInSameToken((TfstGraphBox) b2)) {
    				/* If b2 is connected to the initial state, there is need to test
    				 * whether it starts on a token or not */
    				boxStartingTokens[boxes.indexOf(b2)]=false;
    			}
    		}
    	}
        for (int i=0;i<boxes.size();i++) {
        	GenericGraphBox b=boxes.get(i);
            if (b.getType() != GenericGraphBox.NORMAL || !boxStartingTokens[i]) {
            	continue;
            }
            TfstGraphBox t = (TfstGraphBox) b;
            ArrayList<TfstGraphBox> tmp = new ArrayList<TfstGraphBox>();
            exploreBox(t, tmp);
        }
        nColumns = 1;
        for (TokenTags t : lines) {
            t.refreshFilter(filter, delafStyle);
            int max = 1 + t.getInterpretationCount();
            if (max > nColumns) nColumns = max;
        }
        fireTableStructureChanged();
        fireTableDataChanged();
    }

    private boolean emptySentenceGraph(ArrayList<GenericGraphBox> boxes) {
    	if (boxes.size()!=3) return false;
    	return boxes.get(2).getContent().equals("THIS SENTENCE AUTOMATON HAS BEEN EMPTIED");
	}

    /**
     * We explore boxes until we have to move to the next token.
     * We obtain then in 'tmp' a list of boxes that form an interpretation
     * for a token sequence. This interpretation is then added to the
     * TfstTableModel.
     */
    private void exploreBox(TfstGraphBox t, ArrayList<TfstGraphBox> list) {
        list.add(t);
        ArrayList<GenericGraphBox> transitions = t.getTransitions();
        if (transitions.size() == 0) {
            return;
        }
        TfstGraphBox tmp = (TfstGraphBox) transitions.get(0);
        if (!t.isNextBoxInSameToken(tmp)) {
            addInterpretation(list);
        } else {
            for (GenericGraphBox b : transitions) {
                tmp = (TfstGraphBox) b;
                exploreBox(tmp, list);
            }
        }
        /* Don't forget to remove the element we added */
        list.remove(list.size() - 1);
    }

    /**
     * Adding an interpretation to the TfstTableModel
     */
    private void addInterpretation(ArrayList<TfstGraphBox> list) {
        int start = list.get(0).getBounds().getStart_in_tokens();
        int end = list.get(list.size() - 1).getBounds().getEnd_in_tokens();
        String tokenSequence = TokensInfo.getTokenSequence(start, end);
        TokenTags tags = getTokenTags(start, end, tokenSequence);
        ArrayList<Tag> interpretation = new ArrayList<Tag>();
        for (TfstGraphBox b : list) {
            interpretation.add(new Tag(b.getContent()));
        }
        tags.addInterpretation(interpretation);
    }

    /**
     * Returns the TokenTags object for the given token sequence, inserting it
     * if needed, so that the TokenTags list is sorted.
     */
    private TokenTags getTokenTags(int start, int end, String tokenSequence) {
        for (int i = 0; i < lines.size(); i++) {
            TokenTags t = lines.get(i);
            if (t.getStart() == start && t.getEnd() == end && t.getContent().equals(tokenSequence)) {
                return t;
            }
            if (start < t.getStart()
                    || (start == t.getStart() && end < t.getEnd())) {
                /* If we must insert there */
                t = new TokenTags(start, end, tokenSequence);
                lines.add(i, t);
                return t;
            }
        }
        TokenTags t = new TokenTags(start, end, tokenSequence);
        lines.add(t);
        return t;
    }

    public TokenTags getTokenTags(int j) {
        return lines.get(j);
    }
}
