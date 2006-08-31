 /*
  * Unitex
  *
  * Copyright (C) 2001-2006 Universit� de Marne-la-Vall�e <unitex@univ-mlv.fr>
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

import java.util.*;

import javax.swing.undo.*;

import fr.umlv.unitex.*;

/**
 *  class uses to save the state of the graph before a boxes' text edit
 * @author Decreton Julien
 */
public class BoxeGroupTextEdit extends AbstractUndoableEdit {

	/** boxes selected in the graph */
	private ArrayList selectedBoxes;
	/** boxes selected in the graph before adding a transition */
	private ArrayList oldSelectedBoxes;
	/** hasmap of the selected boxes and the text before the boxe group text edit action*/
	private HashMap selectedBoxesAndOldString;
	/** remplacement string of each selected boxe of the graph */
	private String remplacementString;
	/** araa where the graph is drawn */
	private GenericGraphicalZone zone ;
	
	/**
	 * contruct an edit to redo and undo a boxes' text edition
	 * @param selectedBoxes selected boxes befores the boxes' text edit
	 * @param s the text to add in all the boxes
	 * @param zone the zone where boxes are drawn 
	 */
	public BoxeGroupTextEdit(ArrayList selectedBoxes, String s, GenericGraphicalZone zone) {	
		this.selectedBoxes = selectedBoxes;
		oldSelectedBoxes = (ArrayList) selectedBoxes.clone();
		selectedBoxesAndOldString = new HashMap();
		remplacementString = s;
		this.zone = zone;
		
		// save for each boxe their text
		for( Iterator it = selectedBoxes.iterator(); it.hasNext(); ){
			GenericGraphBox g = (GenericGraphBox)it.next();
			//if( g.TYPE == GenericGraphBox.NORMAL )
			selectedBoxesAndOldString.put(g,g.getContent());
		}
		
	}

	public void undo(){
		super.undo();
		Set keys = selectedBoxesAndOldString.keySet();
		
		// add old text in each boxes
		for(Iterator it = keys.iterator() ; it.hasNext() ; ){
			GenericGraphBox g = (GenericGraphBox)it.next();
			String text = (String)selectedBoxesAndOldString.get(g);
			g.setContent(text);
			g.setSelected(true);
			selectedBoxes.add(g);
			zone.initText(g.getContent());
		}
		
	}
	
	public void redo(){
		super.redo();
		
		// add old text in all boxes
		for( Iterator it = oldSelectedBoxes.iterator(); it.hasNext(); ){
			GenericGraphBox g = (GenericGraphBox)it.next();
			g.setContent(remplacementString);
			g.setSelected(true);
			selectedBoxes.add(g);
			zone.initText(g.getContent());			
		}
	}

}
