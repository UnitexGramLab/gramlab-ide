package fr.gramlab.util.filelist;

import java.io.File;

public class SelectableFile {
	
	private File file;
	public File getFile() {
		return file;
	}

	private boolean selected;
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public SelectableFile(File file,boolean selected) {
		this.file=file;
		setSelected(selected);
	}
	
}
