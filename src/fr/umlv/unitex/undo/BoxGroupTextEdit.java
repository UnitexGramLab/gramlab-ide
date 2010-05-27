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

package fr.umlv.unitex.undo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.undo.AbstractUndoableEdit;

import fr.umlv.unitex.GenericGraphBox;
import fr.umlv.unitex.GenericGraphicalZone;

/**
 *  class uses to save the state of the graph before a boxes' text edit
 * @author Decreton Julien
 */
public class BoxGroupTextEdit extends AbstractUndoableEdit {

	/** boxes selected in the graph */
	private ArrayList<GenericGraphBox> selectedBoxes;
	/** boxes selected in the graph before adding a transition */
	private ArrayList<GenericGraphBox> oldSelectedBoxes;
	/** hash map of the selected boxes and the text before the box group text edit action*/
	private HashMap<GenericGraphBox,String> selectedBoxesAndOldString;
	/** replacement string of each selected box of the graph */
	private String remplacementString;
	/** area where the graph is drawn */
	private GenericGraphicalZone zone ;
	
	/**
	 * Contructs an edit to redo and undo a boxes' text edition
	 * @param selectedBoxes selected boxes before the boxes' text edit
	 * @param s the text to add in all the boxes
	 * @param zone the zone where boxes are drawn 
	 */
	@SuppressWarnings("unchecked")
	public BoxGroupTextEdit(ArrayList<GenericGraphBox> selectedBoxes, String s, GenericGraphicalZone zone) {	
		this.selectedBoxes = selectedBoxes;
		oldSelectedBoxes = (ArrayList<GenericGraphBox>) selectedBoxes.clone();
		selectedBoxesAndOldString = new HashMap<GenericGraphBox,String>();
		remplacementString = s;
		this.zone = zone;
		
		// save for each boxe their text
		for( Iterator it = selectedBoxes.iterator(); it.hasNext(); ){
			GenericGraphBox g = (GenericGraphBox)it.next();
			//if( g.TYPE == GenericGraphBox.NORMAL )
			selectedBoxesAndOldString.put(g,g.getContent());
		}
		
	}

	@Override
	public void undo(){
		super.undo();
		Set<GenericGraphBox> keys = selectedBoxesAndOldString.keySet();
		
		// add old text in each boxes
		for(Iterator<GenericGraphBox> it = keys.iterator() ; it.hasNext() ; ){
			GenericGraphBox g = it.next();
			String text = selectedBoxesAndOldString.get(g);
			g.setContent(text);
			g.setSelected(true);
			selectedBoxes.add(g);
			zone.initText(g.getContent());
		}
		
	}
	
	@Override
	public void redo(){
		super.redo();
		
		// add old text in all boxes
		for(Iterator<GenericGraphBox> it = oldSelectedBoxes.iterator(); it.hasNext(); ){
			GenericGraphBox g = it.next();
			g.setContent(remplacementString);
			g.setSelected(true);
			selectedBoxes.add(g);
			zone.initText(g.getContent());			
		}
	}

}
