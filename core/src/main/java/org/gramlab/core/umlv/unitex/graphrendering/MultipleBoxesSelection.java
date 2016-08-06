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
package org.gramlab.core.umlv.unitex.graphrendering;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

/**
 * This class describes the clipboard content description for copy/paste on
 * multiple boxes.
 * 
 * @author Sébastien Paumier
 */
class MultipleBoxesSelection implements Transferable {
	private MultipleSelection content = null;

	/**
	 * Creates a new multiple box selection object for copy/paste operations
	 * 
	 * @param m
	 *            the box selection
	 */
	public MultipleBoxesSelection(MultipleSelection m) {
		content = m;
	}

	/**
	 * Returns the data contained in the clipboard, if it is a multiple box
	 * selection
	 * 
	 * @param f
	 *            dataflavor identifying the data contained in the clipboard
	 * @return the data, or <code>null</code> if the data type is invalid
	 */
	@Override
	public Object getTransferData(DataFlavor f) {
		if (f.getHumanPresentableName().equals("Unitex dataflavor"))
			return content;
		return null;
	}

	/**
	 * Defines a new dataflavor to identify Unitex multiple box selection
	 * 
	 * @return an array containing the dataflavor
	 */
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		final DataFlavor t[] = new DataFlavor[2];
		t[0] = new DataFlavor("unitex/boxes", "Unitex dataflavor");
		return t;
	}

	/**
	 * Tests if the dataflavor <code>f</code> is equal to the Unitex multiple
	 * box selection one
	 * 
	 * @param f
	 *            the dataflavor to test
	 * @return <code>true</code> if the dataflavor is equal to the Unitex
	 *         multiple box selection one, <code>false</code> otherwise
	 */
	@Override
	public boolean isDataFlavorSupported(DataFlavor f) {
		return f.getHumanPresentableName().equals("Unitex dataflavor");
	}
}
