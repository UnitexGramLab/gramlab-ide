/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

package fr.umlv.unitex.debug;

import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.table.AbstractTableModel;

public class DebugTableModel extends AbstractTableModel {
	
	private DebugInfos infos;
	private ArrayList<DebugDetails> details=new ArrayList<DebugDetails>();
	
	public DebugTableModel(DebugInfos infos) {
		this.infos=infos;
	}
	
	@Override
	public String getColumnName(int column) {
		return DebugDetails.fields[column];
	}
	
	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		return details.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		DebugDetails d=details.get(rowIndex);
		if (d==null) return null;
		return d.getField(columnIndex);
	}
	
	
	public void setMatchNumber(int n) {
		int size=details.size();
		details.clear();
		if (size>0) fireTableRowsDeleted(0,size-1);
		Scanner scanner=new Scanner(infos.lines.get(n));
		scanner.useDelimiter(""+(char)2);
		while (scanner.hasNext()) {
			/* We skip the initial char #1 */
			String output=scanner.next().substring(1);
			scanner.useDelimiter(":");
			int graph=Integer.parseInt(scanner.next().substring(1));
			int box=scanner.nextInt();
			scanner.useDelimiter(""+(char)3);
			int line=Integer.parseInt(scanner.next().substring(1));
			scanner.useDelimiter(""+(char)4);
			String tag=scanner.next().substring(1);
			scanner.useDelimiter(""+(char)1);
			String matched=scanner.next().substring(1);
			details.add(new DebugDetails(tag,output,matched,graph,box,line,infos));
			scanner.useDelimiter(""+(char)2);
		}
		scanner.close();
		fireTableRowsInserted(0,details.size());
	}

	public DebugDetails getDetailsAt(int n) {
		return details.get(n);
	}
}
