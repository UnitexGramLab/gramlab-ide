package fr.gramlab.workspace;

import java.io.File;
import java.io.IOException;

import fr.gramlab.config.GramlabConfigManager;
import fr.umlv.unitex.config.ConfigModel;

/**
 * Description of a gramlab project.
 * 
 * @author paumier
 *
 */
public class Project {
	
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
	
	private Project(String name) {
		this.name=name;
		this.directory=new File(GramlabConfigManager.getWorkspaceDirectory(),name);
		this.configFile=new File(directory,".gramlab_project");
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
	
}
