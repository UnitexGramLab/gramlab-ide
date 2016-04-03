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
package fr.umlv.unitex.console;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

@SuppressWarnings("serial")
public class ConsoleTransferHandler extends TransferHandler {
	private final ConsoleTableModel model;

	public ConsoleTransferHandler(ConsoleTableModel model) {
		this.model = model;
	}

	@Override
	public int getSourceActions(JComponent c) {
		return COPY;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		String s = "";
		final int n = model.getRowCount();
		for (int i = 0; i < n; i++) {
			s = s + model.getValueAt(i, 2) + "\n";
		}
		return new StringSelection(s);
	}

	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		/* Nothing to do */
	}

	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		return false;
	}

	@Override
	public boolean importData(JComponent comp, Transferable t) {
		return false;
	}
}
