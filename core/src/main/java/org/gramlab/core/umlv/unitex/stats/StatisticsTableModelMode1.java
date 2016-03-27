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
package fr.umlv.unitex.stats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.table.AbstractTableModel;

import fr.umlv.unitex.io.Encoding;

public class StatisticsTableModelMode1 extends AbstractTableModel {
	private final String[] columnNames = new String[] { "Collocate",
			"Occurrences" };

	class Mode1Data {
		String match;
		int n;
	}

	private final ArrayList<Mode1Data> data = new ArrayList<Mode1Data>();

	public StatisticsTableModelMode1(File file) {
		try {
			final FileInputStream stream = new FileInputStream(file);
			final Scanner scanner = Encoding.getScanner(file);
			scanner.useDelimiter("\r\n|\t");
			while (scanner.hasNext()) {
				final Mode1Data d = new Mode1Data();
				d.match = scanner.next();
				if (!scanner.hasNextInt()) {
					throw new IOException();
				}
				d.n = scanner.nextInt();
				data.add(d);
			}
			scanner.close();
			stream.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		final Mode1Data d = data.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return d.match;
		case 1:
			return d.n;
		default:
			throw new IllegalArgumentException("Invalid columnIndex: "
					+ columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
			return Integer.class;
		default:
			throw new IllegalArgumentException("Invalid columnIndex: "
					+ columnIndex);
		}
	}
}
