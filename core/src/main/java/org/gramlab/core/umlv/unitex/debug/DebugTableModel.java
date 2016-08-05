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
package org.gramlab.core.umlv.unitex.debug;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class DebugTableModel extends AbstractTableModel {
	private final DebugInfos infos;
	private final ArrayList<DebugDetails> details = new ArrayList<DebugDetails>();

	public DebugTableModel(DebugInfos infos) {
		this.infos = infos;
	}

	@Override
	public String getColumnName(int column) {
		return DebugDetails.fields[column];
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public int getRowCount() {
		return details.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		final DebugDetails d = details.get(rowIndex);
		if (d == null)
			return null;
		return d.getField(columnIndex);
	}

	public void setMatchNumber(int n) {
		final int size = details.size();
		details.clear();
		if (size > 0)
			fireTableRowsDeleted(0, size - 1);
		infos.getMatchDetails(n, details);
		fireTableRowsInserted(0, details.size());
	}

	public DebugDetails getDetailsAt(int n) {
		return details.get(n);
	}
}
