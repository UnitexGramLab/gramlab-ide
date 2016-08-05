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

/**
 * class uses to save the state of the graph before a selection
 * 
 * @author Decreton Julien
 */
public class SelectEdit extends AbstractUndoableEdit {
	/**
	 * boxes selected in the graph
	 */
	private final ArrayList<GenericGraphBox> selectedBoxes;
	/**
	 * boxes selected in the graph before adding a transition
	 */
	private final ArrayList<GenericGraphBox> oldSelectedBoxes;

	/**
	 * @param selectedBoxes
	 *            boxes selected in the graph
	 */
	@SuppressWarnings("unchecked")
	public SelectEdit(ArrayList<GenericGraphBox> selectedBoxes) {
		this.oldSelectedBoxes = (ArrayList<GenericGraphBox>) selectedBoxes
				.clone();
		this.selectedBoxes = selectedBoxes;
	}

	@Override
	public void redo() {
		super.redo();
		for (final GenericGraphBox g : oldSelectedBoxes) {
			g.setSelected(true);
			selectedBoxes.add(g);
		}
	}

	@Override
	public void undo() {
		super.undo();
		for (final GenericGraphBox g : oldSelectedBoxes) {
			g.setSelected(false);
			selectedBoxes.remove(g);
		}
	}
}
