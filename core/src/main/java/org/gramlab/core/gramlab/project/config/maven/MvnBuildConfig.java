package fr.gramlab.project.config.maven;

import java.util.ArrayList;

/**
 * This class describes which .fst2 and .bin have to be compiled
 * when building the project's maven package
 * 
 * @author paumier
 *
 */
public class MvnBuildConfig {
	

	private ArrayList<GrfToCompile> grfToCompile=new ArrayList<GrfToCompile>();
	private ArrayList<BinToBuild> binToBuild=new ArrayList<BinToBuild>();

	public MvnBuildConfig() {
		/* We just use default values */
	}
	

	@SuppressWarnings("unchecked")
	public MvnBuildConfig(
			ArrayList<GrfToCompile> grfToCompile,
			ArrayList<BinToBuild> binToBuild) {
		this.grfToCompile=(ArrayList<GrfToCompile>) grfToCompile.clone();
		this.binToBuild=(ArrayList<BinToBuild>) binToBuild.clone();
	}


	
	@SuppressWarnings("unchecked")
	@Override
	public MvnBuildConfig clone() {
		return new MvnBuildConfig(
				(ArrayList<GrfToCompile>) grfToCompile.clone(),
				(ArrayList<BinToBuild>) binToBuild.clone());
	}


	public ArrayList<GrfToCompile> getGrfToCompile() {
		return grfToCompile;
	}


	public ArrayList<BinToBuild> getBinToBuild() {
		return binToBuild;
	}
	
}
