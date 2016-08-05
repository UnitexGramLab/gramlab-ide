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
import java.util.HashMap;
import java.util.Set;

import javax.swing.undo.AbstractUndoableEdit;

import org.gramlab.core.umlv.unitex.graphrendering.GenericGraphBox;
import org.gramlab.core.umlv.unitex.graphrendering.GenericGraphicalZone;

/**
 * class uses to save the state of the graph delete a group of boxes
 * 
 * @author Decreton Julien
 */
public class DeleteBoxGroupEdit extends AbstractUndoableEdit {
	/**
	 * graph's boxes
	 */
	private final ArrayList<GenericGraphBox> boxes;
	private final ArrayList<GenericGraphBox> /** boxes selected in the graph */
	selectedBoxes;
	private final ArrayList<GenericGraphBox> /**
	 * boxes selected in the graph
	 * before adding a transition
	 */
	oldSelectedBoxes;
	/**
	 * zone where the graph is drawn
	 */
	private final GenericGraphicalZone zone;
	/**
	 * hashmap which store the boxes selected before the delete action and the
	 * transitions to its
	 */
	private final HashMap<GenericGraphBox, ArrayList<GenericGraphBox>> selectedBoxesAndTransitionsTo;
	/**
	 * hashmap which store the boxes selected before the delete action and the
	 * transitions from its
	 */
	private final HashMap<GenericGraphBox, ArrayList<GenericGraphBox>> selectedBoxesAndTransitionsFrom;

	/**
	 * @param selectedBoxes
	 *            the boxes selected before the delete action
	 * @param graphBoxes
	 *            the boxes of the graph
	 * @param zone
	 *            the zone where the graph is drawn
	 */
	@SuppressWarnings("unchecked")
	public DeleteBoxGroupEdit(ArrayList<GenericGraphBox> selectedBoxes,
			ArrayList<GenericGraphBox> graphBoxes, GenericGraphicalZone zone) {
		selectedBoxesAndTransitionsTo = new HashMap<GenericGraphBox, ArrayList<GenericGraphBox>>();
		selectedBoxesAndTransitionsFrom = new HashMap<GenericGraphBox, ArrayList<GenericGraphBox>>();
		this.selectedBoxes = selectedBoxes;
		this.oldSelectedBoxes = (ArrayList<GenericGraphBox>) selectedBoxes
				.clone();
		this.boxes = graphBoxes;
		this.zone = zone;
		// get, for each deleted boxes the box which have transition to it
		GenericGraphBox g;
		for (final Object selectedBoxe : selectedBoxes) {
			g = (GenericGraphBox) selectedBoxe;
			final ArrayList<GenericGraphBox> boxeTransitionsTo = zone
					.getTransitionTo(g);
			final ArrayList<GenericGraphBox> boxeTransitionsFrom = (ArrayList<GenericGraphBox>) g
					.getTransitions().clone();
			selectedBoxesAndTransitionsTo.put(g, boxeTransitionsTo);
			selectedBoxesAndTransitionsFrom.put(g, boxeTransitionsFrom);
		}
	}

	@Override
	public void undo() {
		super.undo();
		GenericGraphBox g, g2;
		ArrayList<GenericGraphBox> transitionsToBoxe;
		ArrayList<GenericGraphBox> transitionsFromBoxe;
		final Set<GenericGraphBox> keys = selectedBoxesAndTransitionsTo
				.keySet();
		// for each selected boxes before delete
		for (final GenericGraphBox key : keys) {
			g = key;
			g.setTransitions(new ArrayList<GenericGraphBox>());
			transitionsToBoxe = selectedBoxesAndTransitionsTo.get(g);
			transitionsFromBoxe = selectedBoxesAndTransitionsFrom.get(g);
			if (g.type == GenericGraphBox.NORMAL) {
				boxes.add(g);
			}
			// select this boxe
			g.setSelected(true);
			selectedBoxes.add(g);
			if (g.type != GenericGraphBox.FINAL) {
				zone.initText(g.getContent());
			}
			// add transitions which pointed on this box
			if (g.hasTransitionToItself())
				g.addTransitionTo(g);
			for (final GenericGraphBox aTransitionsToBoxe : transitionsToBoxe) {
				g2 = aTransitionsToBoxe;
				if (!selectedBoxes.contains(g2))
					g2.onlyAddTransitionTo(g);
			}
			// add transitions from each boxe
			for (final GenericGraphBox aTransitionsFromBoxe : transitionsFromBoxe) {
				g2 = aTransitionsFromBoxe;
				g.onlyAddTransitionTo(g2);
			}
		}
		zone.repaint();
	}

	@Override
	public void redo() {
		super.redo();
		int i, L;
		GenericGraphBox g;
		if (selectedBoxes.isEmpty())
			return;
		L = oldSelectedBoxes.size();
		// delete each selected boxes before the delete action
		for (i = 0; i < L; i++) {
			g = oldSelectedBoxes.get(i);
			if (g.getType() == GenericGraphBox.NORMAL) {
				boxes.remove(g);
			}
			zone.removeTransitionsToSelected();
		}
		zone.unSelectAllBoxes();
		zone.repaint();
	}
}
