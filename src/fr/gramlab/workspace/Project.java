package fr.gramlab.workspace;

import java.io.File;
import java.io.IOException;

import fr.gramlab.config.GramlabConfigManager;
import fr.umlv.unitex.config.ConfigModel;
import fr.umlv.unitex.files.FileUtil;

/**
 * Description of a gramlab project.
 * 
 * @author paumier
 *
 */
public class Project implements Comparable<Project> {
	
	private ConfigModel config;
	private String name;
	private File directory;
	private File corpusDirectory;
	private File delaDirectory;
	private File graphsDirectory;
	private File preprocessingDirectory;
	private File sentenceDirectory;
	private File replaceDirectory;
	private File configFile;
	private boolean open;
	
	public static final String CONFIG_FILE_NAME=".gramlab_config";
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null || !(obj instanceof Project)) return false;
		Project p=(Project)obj;
		return directory.equals(p.getDirectory());
	}
	
	@Override
	public int hashCode() {
		return getDirectory().hashCode();
	}
	
	public int compareTo(Project p) {
		return getName().compareTo(p.getName());
	}
	
	
	public boolean isOpen() {
		return open;
	}

	private void setOpen(boolean open) {
		this.open = open;
	}
	
	void open() {
		setOpen(true);
	}

	void close() {
		setOpen(false);
	}

	public Project(String name) {
		this.name=name;
		this.directory=new File(GramlabConfigManager.getWorkspaceDirectory(),name);
		this.configFile=new File(directory,CONFIG_FILE_NAME);
	}

	public String getName() {
		return name;
	}
	
	public File getDirectory() {
		return directory;
	}
	
	public File getConfigFile() {
		return configFile;
	}
	
	/**
	 * Returns a new project or null if the project was not properly created.
	 * @param name
	 * @return
	 */
	public static Project createEmptyProject(String name) {
		Project p=new Project(name);
		if (p.getDirectory().exists()) return null;
		if (!p.getDirectory().mkdir()) return null;
		try {
			if (!p.getConfigFile().createNewFile()) {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		if (!p.getCorpusDirectory().mkdir()) return null;
		if (!p.getDelaDirectory().mkdir()) return null;
		if (!p.getGraphsDirectory().mkdir()) return null;
		if (!p.getPreprocessingDirectory().mkdir()) return null;
		if (!p.getSentenceDirectory().mkdir()) return null;
		if (!p.getReplaceDirectory().mkdir()) return null;
		return p;
	}
	
		
	public File getCorpusDirectory() {
		if (corpusDirectory==null) {
			corpusDirectory=new File(directory,"Corpus");
		}
		return corpusDirectory;
	}

	public File getDelaDirectory() {
		if (delaDirectory==null) {
			delaDirectory=new File(directory,"Dela");
		}
		return delaDirectory;
	}

	public File getGraphsDirectory() {
		if (graphsDirectory==null) {
			graphsDirectory=new File(directory,"Graphs");
		}
		return graphsDirectory;
	}
	
	public File getPreprocessingDirectory() {
		if (preprocessingDirectory==null) {
			preprocessingDirectory=new File(getGraphsDirectory(),"Preprocessing");
		}
		return preprocessingDirectory;
	}
	
	public File getSentenceDirectory() {
		if (sentenceDirectory==null) {
			sentenceDirectory=new File(getPreprocessingDirectory(),"Sentence");
		}
		return sentenceDirectory;
	}
	
	public File getReplaceDirectory() {
		if (replaceDirectory==null) {
			replaceDirectory=new File(getPreprocessingDirectory(),"Replace");
		}
		return replaceDirectory;
	}

	public ConfigModel getConfigModel() {
		return config;
	}

	public static Project cloneWorkspaceProject(String name,File src) {
		Project p=createEmptyProject(name);
		FileUtil.copyDirRec(src,p.getDirectory());
		return p;
	}

	public static Project cloneUnitexResourcesProject(String name,File src) {
		Project p=createEmptyProject(name);
		FileUtil.copyDirRec(new File(src,"Corpus"),p.getCorpusDirectory());
		FileUtil.copyDirRec(new File(src,"Dela"),p.getDelaDirectory());
		FileUtil.copyDirRec(new File(src,"Graphs"),p.getGraphsDirectory());
		FileUtil.copyFile(new File(src,"Alphabet.txt"),new File(p.getDirectory(),"Alphabet.txt"));
		FileUtil.copyFile(new File(src,"Alphabet_sort.txt"),new File(p.getDirectory(),"Alphabet_sort.txt"));
		FileUtil.copyFile(new File(src,"Config"),p.getConfigFile());
		return p;
	}
	
}
