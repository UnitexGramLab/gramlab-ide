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
import org.gramlab.core.umlv.unitex.graphrendering.GenericGraphicalZone;

/**
 * class uses to save the state of the graph before a boxe text edit
 * 
 * @author Decreton Julien
 */
public class BoxTextEdit extends AbstractUndoableEdit {
	/**
	 * text before editing
	 */
	private final String oldText;
	private final String /** text to put in the boxe */
	newText;
	/**
	 * boxe where change text
	 */
	private final GenericGraphBox boxe;
	/**
	 * zone where the graph is drawn
	 */
	private final GenericGraphicalZone zone;

	/**
	 * contruct an edit to redo and undo a text edition in a boxe
	 * 
	 * @param boxe
	 *            the boxe where add the text
	 * @param text
	 *            the text to add in the boxe
	 * @param zone
	 *            the zone where boxes are drawn
	 */
	public BoxTextEdit(GenericGraphBox boxe, String text,
			GenericGraphicalZone zone) {
		this.boxe = boxe;
		this.newText = text;
		this.zone = zone;
		oldText = boxe.getContent();
	}

	@Override
	public void undo() {
		super.undo();
		boxe.setContent(oldText);
		boxe.setSelected(true);
		zone.getSelectedBoxes().add(boxe);
		zone.initText(boxe.getContent());
	}

	@Override
	public void redo() {
		super.redo();
		boxe.setContent(newText);
	}
}
