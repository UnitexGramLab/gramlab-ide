package org.gramlab.core.gramlab.util.filelist;

import java.io.File;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class SelectableFileListModel extends AbstractTableModel {
	
	protected ArrayList<SelectableFile> model=new ArrayList<SelectableFile>();
	

	public SelectableFileListModel() {
		/* An empty model */
	}
	
	public SelectableFileListModel(ArrayList<File> files,ArrayList<File> selectedFiles) {
		/* We force the selected files to appear first, in order to respect 
		 * the original order of the selected files
		 */
		for (File f:selectedFiles) {
			if (files.contains(f)) {
				model.add(new SelectableFile(f,true));
			}
		}
		for (File f:files) {
			if (!selectedFiles.contains(f)) {
				model.add(new SelectableFile(f,false));
			}
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return SelectableFile.class;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return null;
	}

	@Override
	public int getRowCount() {
		return model.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return model.get(rowIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		Boolean v=(Boolean)value;
		SelectableFile f=(SelectableFile) getValueAt(rowIndex,columnIndex);
		f.setSelected(v.booleanValue());
		fireTableCellUpdated(rowIndex,columnIndex);
	}

	public ArrayList<File> getSelectedFiles() {
		ArrayList<File> files=new ArrayList<File>();
		for (SelectableFile f:model) {
			if (f.isSelected()) {
				files.add(f.getFile());
			}
		}
		return files;
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
		SelectableFile fB=model.remove(b);
		SelectableFile fA=model.remove(a);
		model.add(a,fB);
		model.add(b,fA);
		fireTableCellUpdated(a,0);
		fireTableCellUpdated(b,0);
	}

	public void selectAll() {
		setSelectedForAll(true);
	}
	
	public void unselectAll() {
		setSelectedForAll(false);
	}

	private void setSelectedForAll(boolean b) {
		for (SelectableFile f:model) {
			f.setSelected(b);
		}
		fireTableRowsUpdated(0,model.size()-1);
	}

}
