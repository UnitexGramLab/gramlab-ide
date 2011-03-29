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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import fr.umlv.unitex.graphrendering.GenericGraphBox;
import fr.umlv.unitex.io.GraphIO;

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
		if (!restore_E_steps()) {
			details.clear();
			return;
		}
		fireTableRowsInserted(0,details.size());
	}

	/**
	 * In debug mode, <E> with no output are compiled without debug 
	 * information, so that they cannot be present in debug concordance.
	 * So, this function is there to restore those <E> steps in graph
	 * exploration.
	 */
	private boolean restore_E_steps() {
		HashMap<Integer,GraphIO> map=new HashMap<Integer,GraphIO>();
		for (int i=0;i<details.size()-1;i++) {
			DebugDetails src=details.get(i);
			DebugDetails dst=details.get(i+1);
			if (src.graph!=dst.graph) {
				/* There cannot be a missing <E> if the
				 * graphs are different */
				continue;
			}
			File f=infos.graphs.get(src.graph-1);
			if (f.lastModified()>infos.concordIndFile.lastModified()) {
				JOptionPane
	            .showMessageDialog(
	                    null,
	                    "File "+f.getAbsolutePath()+ " has been modified\n"+
	                    "since the concordance index was built. Cannot debug it.",
	                    "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			GraphIO gio=map.get(Integer.valueOf(src.graph));
			if (gio==null) {
				gio=GraphIO.loadGraph(f,false,false);
				if (gio==null) {
					JOptionPane
		            .showMessageDialog(
		                    null,
		                    "Cannot load graph "+f.getAbsolutePath(),
		                    "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				map.put(Integer.valueOf(src.graph),gio);
			}
			GenericGraphBox srcBox=gio.boxes.get(src.box);
			GenericGraphBox dstBox=gio.boxes.get(dst.box);
			if (srcBox.transitions.contains(dstBox)) {
				/* Nothing to do if there is a transition */
				continue;
			}
			ArrayList<GenericGraphBox> visited=new ArrayList<GenericGraphBox>();
			ArrayList<Integer> path=new ArrayList<Integer>();
			if (!findEpsilonPath(0,srcBox,dstBox,visited,path,gio.boxes)) {
				JOptionPane
	            .showMessageDialog(
	                    null,
	                    "Cannot find <E> path between box "+src.box+" and "+dst.box+ " in graph "+f.getAbsolutePath(),
	                    "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			for (int j=0;j<path.size();j=j+2) {
				int box=path.get(j);
				int line=path.get(j+1);
				DebugDetails d=new DebugDetails("<E>","","",src.graph,box,line,infos);
				i++;
				details.add(i,d);
			}
		}
		return true;
	}

	private boolean findEpsilonPath(int depth,GenericGraphBox current,
			GenericGraphBox dstBox, ArrayList<GenericGraphBox> visited,
			ArrayList<Integer> path,ArrayList<GenericGraphBox> boxes) {
		if (current.equals(dstBox) && depth>0) return true;
		if (visited.contains(current)) return false;
		visited.add(current);
		if (depth==0) {
			/* Special of the starting box */
			for (GenericGraphBox dest:current.transitions) {
				if (findEpsilonPath(depth+1,dest,dstBox,visited,path,boxes)) return true;
			}
			return false;
		}
		if (current.transduction!=null && current.transduction.length()>0) {
			/* Boxes with an output cannot be considered */
			return false;
		}
		if (current.lines.size()==0) {
			/* Case of a box only containing <E> */
			path.add(boxes.indexOf(current));
			path.add(0);
			for (GenericGraphBox dest:current.transitions) {
				if (findEpsilonPath(depth+1,dest,dstBox,visited,path,boxes)) return true;
			}
			path.remove(path.size()-1);
			path.remove(path.size()-1);
			return false;
		}
		for (int i=0;i<current.lines.size();i++) {
			if (current.lines.get(i).equals("<E>")) {
				/* The box line is a candidate */
				path.add(boxes.indexOf(current));
				path.add(i);
				for (GenericGraphBox dest:current.transitions) {
					if (findEpsilonPath(depth+1,dest,dstBox,visited,path,boxes)) return true;
				}
				path.remove(path.size()-1);
				path.remove(path.size()-1);
				/* An <E> is enough to go through a box, so we can stop */
				break;
			}
		}
		return false;
	}

	public DebugDetails getDetailsAt(int n) {
		return details.get(n);
	}
}
