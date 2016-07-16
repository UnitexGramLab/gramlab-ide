package org.gramlab.core.gramlab.project.config.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.gramlab.util.filelist.SelectableFile;
import org.gramlab.core.gramlab.util.filelist.SelectableFileListModel;
import org.gramlab.core.umlv.unitex.files.FileUtil;

@SuppressWarnings("serial")
public class MavenFileTableModel extends SelectableFileListModel {

	private GramlabProject project;
	
	public MavenFileTableModel(GramlabProject p) {
		super();
		this.project=p;
		populateModel(p.getMvnSourceConfig());
	}

	
	private boolean shouldBeSelected(File f,ArrayList<File> includes,ArrayList<File> excludes,
			boolean selectGrfs,boolean selectDics) {
		boolean selected;
		if (fileShouldBeExcluded(f,excludes)) {
			selected=false;
		} else if (MvnSourceConfig.isDefaultFileToInclude(f)) {
			selected=true;
		} else if (includes.contains(f)) {
			selected=true;
		} else if (FileUtil.getExtensionInLowerCase(f).equals("grf") && selectGrfs) {
			selected=true;
		} else if (FileUtil.getExtensionInLowerCase(f).equals("dic") && selectDics) {
			selected=true;
		} else {
			selected=false;
		}
		return selected;
	}
	
	/**
	 * We fill the model with all the project's files, selecting them
	 * or not according to the MvnSourceConfig information. 
	 */
	private void populateModel(MvnSourceConfig m) {
		ArrayList<File> files=FileUtil.getFileList(project.getSrcDirectory());
		files.remove(project.getSrcDirectory());
		boolean selected;
		for (File f:files) {
			selected=shouldBeSelected(f,m.getIncludes(),m.getExcludes(),m.isIncludeGrfs(),m.isIncludeDics());
			model.add(new SelectableFile(f,selected));
			if (selected) {
				selectWithParents(model,f);
			}
		}
	}

	/**
	 * All parents of f that are already in the model will be forced to be selected.
	 * f will be selected as well.
	 * 
	 */
	private void selectWithParents(ArrayList<SelectableFile> model, File f) {
		ArrayList<File> parents=new ArrayList<File>();
		while (!f.equals(project.getSrcDirectory())) {
			parents.add(f);
			f=f.getParentFile();
		}
		for (int i=0;i<model.size();i++) {
			SelectableFile sf=model.get(i);
			if (parents.contains(sf.getFile())) {
				sf.setSelected(true);
			}
		}
	}

	/**
	 * If f or any parent of f is in the exclude list, then f must be excluded
	 */
	private boolean fileShouldBeExcluded(File f,ArrayList<File> excludes) {
		while (!f.equals(project.getSrcDirectory())) {
			if (excludes.contains(f)) return true;
			f=f.getParentFile();
		}
		return false;
	}

	public void setTreeSelected(String rootName,boolean selected,GramlabProject p) {
		for (int i=0;i<getRowCount();i++) {
			SelectableFile s=model.get(i);
			String name=MavenTreeModel.getTreeName(p,s.getFile());
			if (name.equals(rootName)
					|| name.startsWith(rootName+File.separatorChar)) {
				s.setSelected(selected);
				fireTableRowsUpdated(i,i);
			} else {
				/* In case of selected==true, we have to force all the parents
				 * to true
				 */
				if (selected && rootName.startsWith(name+File.separatorChar)) {
					s.setSelected(selected);
					fireTableRowsUpdated(i,i);
				}
			}
		}
	}

	public SelectableFile getElement(int i) {
		return model.get(i);
	}

	public ArrayList<SelectableFile> getElements() {
		return model;
	}

	public void selectFiles(String extension, boolean selected) {
		ArrayList<File> parentsToSelect=new ArrayList<File>();
		for (int i=0;i<getRowCount();i++) {
			SelectableFile s=model.get(i);
			if (extension.equals(FileUtil.getExtensionInLowerCase(s.getFile()))) {
				/* We have a match */
				s.setSelected(selected);
				if (selected) {
					File p=s.getFile().getParentFile();
					if (!parentsToSelect.contains(p)) {
						parentsToSelect.add(p);
					}
				}
			}
		}
		for (File f:parentsToSelect) {
			selectWithParents(model,f);
		}
		fireTableDataChanged();
	}

	/**
	 * We build and return a MvnSourceConfig object that describes the selected items.
	 */
	public MvnSourceConfig getMvnSourceConfig(boolean selectGrfs,boolean selectDics) {
		ArrayList<File> includes=new ArrayList<File>();
		ArrayList<File> excludes=new ArrayList<File>();
		for (SelectableFile sf:model) {
			String extension=FileUtil.getExtensionInLowerCase(sf.getFile());
			File f=sf.getFile();
			if (extension.equals("grf")) {
				if (selectGrfs) {
					if (sf.isSelected()) {
						/* grf file selected when all grf files are supposed to be: nothing to do */
					} else {
						/* Explicit exclusion of a .grf file */
						excludes.add(f);						
					}
				} else {
					if (sf.isSelected()) {
						/* Explicit inclusion of a .grf file */
						includes.add(f);
					} else {
						/* grf file not selected when no grf file is supposed to be: nothing to do */
					}
				}
			} else if (extension.equals("dic")) {
				if (selectDics) {
					if (sf.isSelected()) {
						/* dic file selected when all dic files are supposed to be: nothing to do */
					} else {
						/* Explicit exclusion of a .dic file */
						excludes.add(f);
					}
				} else {
					if (sf.isSelected()) {
						/* Explicit inclusion of a .dic file */
						includes.add(f);
					} else {
						/* dic file not selected when no dic file is supposed to be: nothing to do */
					}
				}
			} else if (MvnSourceConfig.isDefaultFileToInclude(f)) {
				if (sf.isSelected()) {
					/* f is supposed to be selected and actually is: nothing to do */
				} else {
					/* Explicit exclusion of a default file like Alphabet.txt */
					excludes.add(f);
				}
			} else {
				/* For any normal file, we add it to the include list if 
				 * it is selected */
				if (sf.isSelected()) {
					includes.add(f);
				}
			}
		}
		Collections.sort(excludes);
		filterDirectories(includes,excludes,selectGrfs,selectDics);
		return new MvnSourceConfig(selectGrfs, selectDics, includes, excludes);
	}

	/**
	 * If all the children and sub-children of a selected directory are selected,
	 * then we can remove them from the includes list, otherwise, it is the directory
	 * that must be removed from the list (see NOTE 2 in MvnSourceConfig).
	 */
	private void filterDirectories(ArrayList<File> includes,ArrayList<File> excludes,
			boolean selectGrfs,boolean selectDics) {
		Collections.sort(includes);
		/* This hashmap is used to test whether a directory
		 * 1) has not been tested yet (not in the map)
		 * 2) has been tested and only contained selected elements (map value=true)
		 * 3) has been tested and contained at least one non selected element (map value=false)
		 */
		HashMap<File,Boolean> map=new HashMap<File,Boolean>();
		for (int i=0;i<includes.size();i++) {
			File f=includes.get(i);
			if (!testFile(f,includes,excludes,map,selectGrfs,selectDics) && f.isDirectory()) {
				includes.remove(i);
				i--;
			}
		}
	}

	private boolean testFile(File file, ArrayList<File> includes,ArrayList<File> excludes,
			HashMap<File, Boolean> map, boolean selectGrfs, boolean selectDics) {
		if (map.containsKey(file)) {
			/* We already know this file */
			return map.get(file);
		}
		if (file.isFile()) {
			boolean selected=shouldBeSelected(file, includes, excludes, selectGrfs, selectDics);
			map.put(file,selected);
			return selected;
		}
		/* We have a directory */
		File[] files=file.listFiles();
		if (files==null || files.length==0) {
			/* No children ? We can mark this directory with true */
			map.put(file,true);
			return true;
		}
		for (File f:files) {
			if (!testFile(f, includes, excludes, map, selectGrfs, selectDics)) {
				map.put(file,false);
				return false;
			}
		}
		/* Ok: the directory 'file' only contains selected things. We
		 * can remove those things now from the include list */
		ArrayList<File> list=FileUtil.getFileList(file);
		list.remove(file);
		for (File foo:list) {
			includes.remove(foo);
		}
		return true;
	}
}
