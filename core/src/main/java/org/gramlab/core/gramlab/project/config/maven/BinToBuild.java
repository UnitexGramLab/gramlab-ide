package fr.gramlab.project.config.maven;

import java.io.File;
import java.util.ArrayList;

public class BinToBuild {

	private String bin;
	private ArrayList<File> dics;
	
	@SuppressWarnings("unchecked")
	public BinToBuild(String bin, ArrayList<File> dics) {
		this.bin=bin;
		this.dics=(ArrayList<File>) dics.clone();
	}

	public BinToBuild(String bin,File dic) {
		this.bin=bin;
		this.dics=new ArrayList<File>();
		dics.add(dic);
	}

	public String getBin() {
		return bin;
	}

	public ArrayList<File> getDics() {
		return dics;
	}
	
}
