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

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class ConsoleTableModel extends AbstractTableModel {
	private final ArrayList<ConsoleEntry> data;

	public ConsoleTableModel() {
		data = new ArrayList<ConsoleEntry>();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		final ConsoleEntry e = data.get(rowIndex);
		if (columnIndex == 0) {
			return e.getStatus();
		}
		if (columnIndex == 1) {
			final String s = e.getlogID();
			if (s == null)
				return "";
			return s;
		}
		if (columnIndex == 2) {
			return e;
		}
		throw new IllegalArgumentException("Invalid columun index: "
				+ columnIndex);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0) {
			return Integer.class;
		}
		if (columnIndex == 1) {
			return String.class;
		}
		if (columnIndex == 2) {
			return ConsoleEntry.class;
		}
		throw new IllegalArgumentException("Invalid columun index: "
				+ columnIndex);
	}

	public void addConsoleEntry(int index, ConsoleEntry e) {
		data.add(index, e);
		fireTableRowsInserted(index, index);
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "";
		case 1:
			return "Log #";
		case 2:
			return "Command";
		default:
			throw new IllegalArgumentException("Invalid columun index: "
					+ column);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	public ConsoleEntry getConsoleEntry(int index) {
		return data.get(index);
	}

	public void removeEntry(int index) {
		data.remove(index);
		fireTableRowsDeleted(index, index);
	}
}
