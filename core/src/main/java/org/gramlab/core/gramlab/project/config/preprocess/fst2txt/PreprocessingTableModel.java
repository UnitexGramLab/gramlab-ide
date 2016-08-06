package org.gramlab.core.gramlab.project.config.preprocess.fst2txt;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class PreprocessingTableModel extends AbstractTableModel {

	ArrayList<PreprocessingStep> steps=new ArrayList<PreprocessingStep>();
	
	public PreprocessingTableModel(ArrayList<PreprocessingStep> steps) {
		for (PreprocessingStep s:steps) {
			this.steps.add(s.clone());
		}
	}
	
	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public int getRowCount() {
		return steps.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PreprocessingStep s=steps.get(rowIndex);
		switch(columnIndex) {
		case 0: {
			return s.isSelected();
		}
		case 1: return s.getGraph();
		case 2: return s.getDestFst2();
		case 3: return s.isMerge();
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (columnIndex!=1);
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		PreprocessingStep s=steps.get(rowIndex);
		switch(columnIndex) {
		case 0: {
			s.setSelected((Boolean)aValue);
			break;
		}
		case 2: {
			try {
				File f=(File)aValue;
				if (containsStepWithSameTarget(f,rowIndex)) {
					JOptionPane.showMessageDialog(null,
							"There is already a target graph with this name.", "Error",
							JOptionPane.ERROR_MESSAGE);					
					return;
				}
				s.setDestFst2(f);
			} catch (IllegalArgumentException e) {
				JOptionPane
				.showMessageDialog(
						null,
						"You must specify a target file that is a .fst2 file",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
			break;
		}
		case 3: s.setMerge((Boolean)aValue); break;
		}
		fireTableRowsUpdated(rowIndex,rowIndex);
	}

	private boolean containsStepWithSameTarget(File f, int rowIndex) {
		int n = steps.size();
		for (int i = 0; i < n; i++) {
			if (i==rowIndex) continue;
			File fst2 = (File) getValueAt(i,2);
			if (f.equals(fst2)) return true;
		}
		return false;
	}
	
	public void addElement(PreprocessingStep step) {
		int size=steps.size();
		steps.add(step);
		fireTableRowsInserted(size,size);
	}

	public void remove(int row) {
		steps.remove(row);
		fireTableRowsDeleted(row,row);
	}

	public PreprocessingStep getElement(int i) {
		return steps.get(i);
	}

	public ArrayList<PreprocessingStep> getElements() {
		return steps;
	}

	@Override
	public String getColumnName(int column) {
		switch(column) {
		case 0: return "Use";
		case 1: return "Name";
		case 2: return "Target";
		case 3: return "Mode";
		}
		return null;
	}
	
	/**
	 * Swaps two rows
	 */
	public void swap(int a,int b) {
		if (a==b) return;
		/* First, we make sure that a<b */
		if (a>b) {
			int tmp=a;
			a=b;
			b=tmp;
		}
		PreprocessingStep fB=steps.remove(b);
		PreprocessingStep fA=steps.remove(a);
		steps.add(a,fB);
		steps.add(b,fA);
		fireTableCellUpdated(a,0);
		fireTableCellUpdated(b,0);
	}

}
