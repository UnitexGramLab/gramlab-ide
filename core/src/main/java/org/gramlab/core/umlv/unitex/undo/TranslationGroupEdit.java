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

/**
 * Class used to save the state of the graph before translate boxes
 * 
 * @author Decreton Julien
 */
public class TranslationGroupEdit extends AbstractUndoableEdit {
	/**
	 * boxes selected in the graph
	 */
	private final ArrayList<GenericGraphBox> selectedBoxes;
	/**
	 * length of X, Y shift in pixels
	 */
	private final int x;
	private final int y;

	/**
	 * @param selectedBoxes
	 *            boes selected in the graph
	 * @param x
	 *            length of X shift in pixels
	 * @param y
	 *            length of Y shift in pixels
	 */
	@SuppressWarnings("unchecked")
	public TranslationGroupEdit(ArrayList<GenericGraphBox> selectedBoxes,
			int x, int y) {
		this.selectedBoxes = (ArrayList<GenericGraphBox>) selectedBoxes.clone();
		this.x = x;
		this.y = y;
	}

	@Override
	public void undo() {
		super.undo();
		GenericGraphBox g;
		final int L = selectedBoxes.size();
		for (int i = 0; i < L; i++) {
			g = selectedBoxes.get(i);
			g.translate(-x, -y);
		}
	}

	@Override
	public void redo() {
		super.redo();
		GenericGraphBox g;
		final int L = selectedBoxes.size();
		for (int i = 0; i < L; i++) {
			g = selectedBoxes.get(i);
			g.translate(x, y);
		}
	}
}
