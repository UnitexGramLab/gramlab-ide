package fr.gramlab.svn;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.table.AbstractTableModel;

import fr.gramlab.project.GramlabProject;

@SuppressWarnings("serial")
public class CommitTableModel extends AbstractTableModel {

	ArrayList<SvnCommitInfo> data=new ArrayList<SvnCommitInfo>();
	GramlabProject project;
	
	public CommitTableModel(GramlabProject p,SvnStatusInfo info,ArrayList<File> filesToSelect) {
		this.project=p;
		/* First we add only the given items */
		for (String s:info.getUnversionedFiles()) {
			File f=p.getFileFromNormalizedName(s);
			data.add(new SvnCommitInfo(s,filesToSelect.contains(f),SvnStatus.UNVERSIONED));
		}
		for (String s:info.getAddedFiles()) {
			data.add(new SvnCommitInfo(s,true,SvnStatus.ADDED));
		}
		for (String s:info.getModifiedFiles()) {
			data.add(new SvnCommitInfo(s,true,SvnStatus.MODIFIED));
		}
		for (String s:info.getRemovedFiles()) {
			data.add(new SvnCommitInfo(s,true,SvnStatus.DELETED));
		}
		/* Then we add their parents, if needed */
		for (String s:info.getUnversionedFiles()) {
			addParentsIfNeeded(s,true);
		}
		for (String s:info.getAddedFiles()) {
			addParentsIfNeeded(s,true);
		}
		for (String s:info.getModifiedFiles()) {
			addParentsIfNeeded(s,false);
		}
		for (String s:info.getRemovedFiles()) {
			addParentsIfNeeded(s,false);
		}
		/* The sort is very important, because CommitTreeModel needs it */
		Collections.sort(data);
	}

	private void addParentsIfNeeded(String name,boolean modified) {
		int currentSize=data.size();
		File f=project.getFileFromNormalizedName(name).getParentFile();
		SvnStatus status;
		while (!f.equals(project.getProjectDirectory()))  {
			String tmp=project.getRelativeFileName(f);
			if (!alreadyContainsName(tmp)) {
				status=project.getSvnInfo(f).getStatus();
				SvnCommitInfo info=new SvnCommitInfo(tmp,true,modified?status:null);
				data.add(currentSize,info);
			}
			f=f.getParentFile();
		}
	}

	private boolean alreadyContainsName(String tmp) {
		for (SvnCommitInfo info:data) {
			if (info.getName().equals(tmp)) return true;
		}
		return false;
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
		SvnCommitInfo s=data.get(rowIndex);
		switch(columnIndex) {
		case 0: {
			return s.isSelected();
		}
		case 1: return s.getName();
		case 2: return s.getStatus();
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (columnIndex==0);
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		SvnCommitInfo s=data.get(rowIndex);
		switch(columnIndex) {
		case 0: {
			setTreeSelected(s.getName(),(Boolean)aValue);
			break;
		}
		default: throw new IllegalStateException("");
		}
	}

	public void setTreeSelected(String rootName,boolean selected) {
		for (int i=0;i<getRowCount();i++) {
			SvnCommitInfo s=data.get(i);
			if (s.getName().equals(rootName)
					|| s.getName().startsWith(rootName+File.separatorChar)) {
				s.setSelected(selected);
				fireTableRowsUpdated(i,i);
			} else {
				/* In case of selected==true, we have to force all the parents
				 * to true
				 */
				if (selected && rootName.startsWith(s.getName()+File.separatorChar)) {
					s.setSelected(selected);
					fireTableRowsUpdated(i,i);
				}
			}
		}
	}

	public SvnCommitInfo getElement(int i) {
		return data.get(i);
	}

	public ArrayList<SvnCommitInfo> getElements() {
		return data;
	}

	@Override
	public String getColumnName(int column) {
		switch(column) {
		case 0: return " ";
		case 1: return "Name";
		case 2: return "Status";
		}
		return null;
	}

	/**
	 * Returns true if there is at least one operation to do
	 */
	public boolean mustCommit() {
		for (SvnCommitInfo i:data) {
			if (i.isSelected()) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<String> getUnversionedFiles() {
		ArrayList<String> list=new ArrayList<String>();
		for (SvnCommitInfo i:data) {
			if (i.getStatus()==SvnStatus.UNVERSIONED && i.isSelected()) {
				list.add(i.getName());
			}
		}
		return list;
	}

	public ArrayList<String> getRemovedFiles() {
		ArrayList<String> list=new ArrayList<String>();
		for (SvnCommitInfo i:data) {
			if (i.getStatus()==SvnStatus.DELETED && i.isSelected()) {
				list.add(i.getName());
			}
		}
		return list;
	}

	public ArrayList<String> getFilesToCommit() {
		ArrayList<String> list=new ArrayList<String>();
		for (SvnCommitInfo i:data) {
			if (i.isSelected()) {
				list.add(i.getName());
			}
		}
		return list;
	}

	public void unselectUnversionedFiles() {
		for (int i=0;i<data.size();i++) {
			SvnCommitInfo info=data.get(i);
			if (info.getStatus().equals(SvnStatus.UNVERSIONED)) {
				info.setSelected(false);
				fireTableRowsUpdated(i,i);
			}
		}
	}
	
}
