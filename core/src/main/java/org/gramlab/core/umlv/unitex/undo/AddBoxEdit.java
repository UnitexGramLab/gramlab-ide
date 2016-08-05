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
package org.gramlab.core.umlv.unitex.undo;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import org.gramlab.core.umlv.unitex.graphrendering.GenericGraphBox;
import org.gramlab.core.umlv.unitex.graphrendering.GenericGraphicalZone;

/**
 * class uses to save the state of the graph before add a boxe
 * 
 * @author Decreton Julien
 */
public class AddBoxEdit extends AbstractUndoableEdit {
	/**
	 * boxes of the graph
	 */
	private final ArrayList<GenericGraphBox> boxes;
	/**
	 * boxe to add to the graph
	 */
	private final GenericGraphBox boxe;
	/**
	 * area where the graph is drawn
	 */
	private final GenericGraphicalZone zone;
	/**
	 * list of transition to the boxe
	 */
	private ArrayList<GenericGraphBox> transitionsToBoxe;

	/**
	 * contruct an edit to redo and undo an add boxe action
	 * 
	 * @param boxe
	 *            the boxe to save for the do undo process
	 * @param boxes
	 *            the unit boxes of a graph
	 * @param zone
	 *            the zone where boxes are drawn
	 */
	public AddBoxEdit(GenericGraphBox boxe, ArrayList<GenericGraphBox> boxes,
			GenericGraphicalZone zone) {
		this.boxes = boxes;
		this.boxe = boxe;
		this.zone = zone;
	}

	/**
	 * undo action
	 */
	@Override
	public void undo() {
		super.undo();
		GenericGraphBox g;
		boxes.remove(boxe);
		transitionsToBoxe = zone.getTransitionTo(boxe);
		for (final GenericGraphBox aTransitionsToBoxe : transitionsToBoxe) {
			g = aTransitionsToBoxe;
			g.setSelected(true);
		}
		zone.removeTransitionTo(boxe);
	}

	/**
	 * redo action
	 */
	@Override
	public void redo() {
		super.redo();
		GenericGraphBox g;
		boxes.add(boxe);
		// add old transition to boxe
		for (final GenericGraphBox aTransitionsToBoxe : transitionsToBoxe) {
			g = aTransitionsToBoxe;
			g.addTransitionTo(boxe);
		}
	}
}
