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

import javax.swing.undo.AbstractUndoableEdit;

import org.gramlab.core.umlv.unitex.graphrendering.GenericGraphBox;

/**
 * class uses to save the state of the graph before add a transition
 * 
 * @author Decreton Julien
 */
public class TransitionEdit extends AbstractUndoableEdit {
	/**
	 * boxe from transition starts
	 */
	private final GenericGraphBox srcBoxe;
	private final GenericGraphBox /** boxe where tanstion go */
	dstBoxe;

	/**
	 * constuct a Transition Edit
	 * 
	 * @param srcBoxe
	 *            boxe from transition starts
	 * @param dstBoxe
	 *            boxe where tanstion go
	 */
	public TransitionEdit(GenericGraphBox srcBoxe, GenericGraphBox dstBoxe) {
		this.srcBoxe = srcBoxe;
		this.dstBoxe = dstBoxe;
	}

	@Override
	public void undo() {
		super.undo();
		srcBoxe.addTransitionTo(dstBoxe);
	}

	@Override
	public void redo() {
		super.redo();
		srcBoxe.addTransitionTo(dstBoxe);
	}
}
