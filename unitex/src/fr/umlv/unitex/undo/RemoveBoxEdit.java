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
package fr.umlv.unitex.undo;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import fr.umlv.unitex.graphrendering.GenericGraphBox;
import fr.umlv.unitex.graphrendering.GenericGraphicalZone;

/**
 * class uses to save the state of the graph before removing a box
 * 
 * @author Decreton Julien
 */
public class RemoveBoxEdit extends AbstractUndoableEdit {
	/**
	 * List of transition to a box
	 */
	private final ArrayList<GenericGraphBox> transitionsToBoxe;
	/**
	 * boxes of the graph
	 */
	private final ArrayList<GenericGraphBox> boxes;
	/**
	 * box to remove
	 */
	private final GenericGraphBox box;
	/**
	 * zone where the graph is drawn
	 */
	private final GenericGraphicalZone zone;
	/**
	 * if the box to remove have a self transition or not
	 */
	private boolean itSelfTransition = false;

	/**
	 * @param boxe
	 *            the box to remove
	 * @param boxes
	 *            the boxes of the graph
	 * @param zone
	 *            the zone where remove the box
	 */
	public RemoveBoxEdit(GenericGraphBox boxe,
			ArrayList<GenericGraphBox> boxes, GenericGraphicalZone zone) {
		this.boxes = boxes;
		this.box = boxe;
		this.zone = zone;
		transitionsToBoxe = zone.getTransitionTo(boxe);
		itSelfTransition = boxe.hasTransitionToItself();
	}

	@Override
	public void undo() {
		super.undo();
		if (box.type != GenericGraphBox.NORMAL) {
			return;
		}
		boxes.add(box);
		// add thes transition which pointed on this boxe
		for (int i = 0; boxes != null && i < transitionsToBoxe.size(); i++) {
			final GenericGraphBox g = transitionsToBoxe.get(i);
			g.addTransitionTo(box);
		}
		if (itSelfTransition)
			box.addTransitionTo(box);
	}

	@Override
	public void redo() {
		super.redo();
		boxes.remove(box);
		zone.removeTransitionTo(box);
	}
}
