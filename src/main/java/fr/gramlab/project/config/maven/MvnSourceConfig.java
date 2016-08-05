package fr.gramlab.project.config.maven;

import java.io.File;
import java.util.ArrayList;

/**
 * This class describes which files have to be exported as source files
 * when build the project's maven package.
 * 
 * NOTE 1: exclusion has priority over inclusion
 * 
 * NOTE 2: when an element of the 'includes' list is a directory, it is assumed that
 *         the whole content of the directory will be copied recursively. As a consequence,
 *         if you want to copy only some files within a directory, the directory itself
 *         must not appear in the 'includes' list. For instance, let 'tutu/dir' contains 3
 *         files 'tutu/dir/a', 'tutu/dir/b' and 'tutu/dir/c' and suppose that you want all of them
 *         except 'b'. Then, the 'includes' list should only contain:
 *         
 *         tutu/dir/a
 *         tutu/dir/c
 * 
 * @author paumier
 *
 */
public class MvnSourceConfig {
	

	private boolean includeGrfs=true;
	private boolean includeDics=true;
	private ArrayList<File> includes=new ArrayList<File>();
	/* NOTE: exclusion has priority over inclusion */
	private ArrayList<File> excludes=new ArrayList<File>();

	public MvnSourceConfig() {
		/* We just use default values */
	}
	

	@SuppressWarnings("unchecked")
	public MvnSourceConfig(boolean includeGrfs,boolean includeDics,
			ArrayList<File> includes,ArrayList<File> excludes) {
		this.includeGrfs=includeGrfs;
		this.includeDics=includeDics;
		this.includes=(ArrayList<File>) includes.clone();
		this.excludes=(ArrayList<File>) excludes.clone();
	}

	public boolean isIncludeGrfs() {
		return includeGrfs;
	}
	

	public boolean isIncludeDics() {
		return includeDics;
	}
	
	
	public ArrayList<File> getIncludes() {
		return includes;
	}
	

	public ArrayList<File> getExcludes() {
		return excludes;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public MvnSourceConfig clone() {
		return new MvnSourceConfig(includeGrfs,includeDics,
				(ArrayList<File>) includes.clone(),
				(ArrayList<File>) excludes.clone());
	}
	
	
	private static final String[] defaultFilestoInclude=new String[] {
		"Alphabet.txt",
		"Alphabet_sort.txt",
		"Norm.txt"
	};
	
	public static boolean isDefaultFileToInclude(File f) {
		if (f==null) return false;
		String name=f.getName();
		for (String s:defaultFilestoInclude) {
			if (s.equals(name)) return true;
		}
		return false;
	}

	public static String[] getDefaultFilesToInclude() {
		return defaultFilestoInclude;
	}
	
}
