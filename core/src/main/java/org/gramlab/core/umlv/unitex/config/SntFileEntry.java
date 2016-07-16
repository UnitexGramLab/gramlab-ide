package org.gramlab.core.umlv.unitex.config;

import java.io.File;

public class SntFileEntry {
	
	public SntFileEntry() {
	}
	
	public SntFileEntry(String language, File file) {
		this.language = language;
		this.file = file;
	}
	
	public boolean equals(SntFileEntry that) {
		return ((this.language == null && that.language == null) ||
		(this.language != null && that.language != null && this.language.equals(that.language))) 
		&&
		((this.file == null && that.file == null) ||
				(this.file != null && that.file != null && this.file.equals(that.file)));
	}
	
	private String language;
	private File file;
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	

}
