package fr.gramlab.project.config.maven;

import java.io.File;

public class GrfToCompile {
	
	private File grf;
	private String fst2;

	public GrfToCompile(File grf,String fst2) {
		this.grf=grf;
		this.fst2=fst2;
	}

	public File getGrf() {
		return grf;
	}

	public String getFst2() {
		return fst2;
	}
	
}
