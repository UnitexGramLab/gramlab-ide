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
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 * Undo/redo object for box selection surround operations
 */
public class MultipleEdit extends AbstractUndoableEdit {
	private final ArrayList<UndoableEdit> edits = new ArrayList<UndoableEdit>();

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		for (int i = edits.size() - 1; i >= 0; i--) {
			edits.get(i).undo();
		}
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		for (int i = 0; i < edits.size(); i++) {
			edits.get(i).redo();
		}
	}

	@Override
	public boolean addEdit(UndoableEdit edit) {
		edits.add(edit);
		return true;
	}
}
