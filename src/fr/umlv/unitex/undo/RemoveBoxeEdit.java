 /*
  * Unitex
  *
  * Copyright (C) 2001-2006 Université de Marne-la-Vallée <unitex@univ-mlv.fr>
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
 * class uses to save the state of the graph before remove a boxe 
 * @author Decreton Julien
 *
 */
public class RemoveBoxeEdit extends AbstractUndoableEdit {

	/** List of transition to a boxe */
	private ArrayList transitionsToBoxe;
	/** boxes of the graph */
	private ArrayList boxes;
	/** boxe to remove */
	private GenericGraphBox boxe;
	/** zone where the graph is drawn */
	private GenericGraphicalZone zone;
	/** if the boxe to remove have a selftransition or not */	
	private boolean itSelfTransition = false;

	/**
 	* 
 	* @param boxe the boxe to remove
 	* @param boxes the boxes of the graph
 	* @param zone the zone where remove the boxe
 	*/
	public RemoveBoxeEdit(
		GenericGraphBox boxe,
		ArrayList boxes,
		GenericGraphicalZone zone) {
		this.boxes = boxes;
		this.boxe = boxe;
		this.zone = zone;
		transitionsToBoxe = zone.getTransitionTo(boxe);		
		itSelfTransition = boxe.hasTransitionToItself();
		
	}

	public void undo() {
		super.undo();
		boxes.add(boxe);
		// add thes transition which pointed on this boxe
		for (int i = 0; boxes != null && i < transitionsToBoxe.size(); i++) {
			GenericGraphBox g = (GenericGraphBox) transitionsToBoxe.get(i);
			g.addTransitionTo(boxe);
		}
		if( itSelfTransition ) boxe.addTransitionTo(boxe);
	}

	public void redo() {
		super.redo();
		boxes.remove(boxe);
		zone.removeTransitionTo(boxe);		
	}

}
