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
 * class uses to save the state of the graph before add transitions to a boxe
 * 
 * @author Decreton Julien
 */
public class TransitionGroupEdit extends AbstractUndoableEdit {
	/**
	 * boxes selected in the graph
	 */
	private final ArrayList<GenericGraphBox> selectedBoxes;
	/**
	 * boxes selected in the graph before adding a transition
	 */
	private final ArrayList<GenericGraphBox> oldSelectedBoxes;
	/**
	 * transition destination boxe
	 */
	private final GenericGraphBox dst;
	/**
	 * zone where the graph is drawn
	 */
	private final GenericGraphicalZone zone;

	/**
	 * @param selectedBoxes
	 *            selected boxes in the graph
	 * @param dst
	 *            destination boxe
	 * @param zone
	 *            the zone where remove the boxe
	 */
	@SuppressWarnings("unchecked")
	public TransitionGroupEdit(ArrayList<GenericGraphBox> selectedBoxes,
			GenericGraphBox dst, GenericGraphicalZone zone) {
		this.selectedBoxes = selectedBoxes;
		this.oldSelectedBoxes = (ArrayList<GenericGraphBox>) selectedBoxes
				.clone();
		this.dst = dst;
		this.zone = zone;
	}

	@Override
	public void undo() {
		super.undo();
		GenericGraphBox g;
		for (final GenericGraphBox oldSelectedBoxe : oldSelectedBoxes) {
			g = oldSelectedBoxe;
			g.addTransitionTo(dst);
			// select this boxe
			g.setSelected(true);
			selectedBoxes.add(g);
			zone.initText(g.getContent());
		}
	}

	@Override
	public void redo() {
		super.redo();
		GenericGraphBox g;
		for (final GenericGraphBox oldSelectedBoxe : oldSelectedBoxes) {
			g = oldSelectedBoxe;
			g.addTransitionTo(dst);
			// unselect this boxe
			g.setSelected(false);
			selectedBoxes.remove(g);
		}
	}
}
