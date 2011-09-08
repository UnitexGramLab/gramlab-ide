package fr.gramlab.config;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;

import fr.gramlab.workspace.Project;
import fr.gramlab.workspace.ProjectManager;
import fr.umlv.unitex.config.AbstractConfigModel;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.Preferences;
import fr.umlv.unitex.grf.GraphPresentationInfo;
import fr.umlv.unitex.io.Encoding;

/**
 * This class provides the default Unitex config information,
 * except for the encoding that is UTF8.
 * @author paumier
 *
 */
public class ProjectConfig extends AbstractConfigModel {

	private Preferences defaultPreferences;
	private File unitexToolLogger;
	
	public ProjectConfig(File jarPath) {
		unitexToolLogger=new File(jarPath,"UnitexToolLogger"+(Config.getSystem() == Config.WINDOWS_SYSTEM ? ".exe" : ""));
		defaultPreferences=new Preferences();
		defaultPreferences.encoding=Encoding.UTF8;
	}
	
	/**
	 * If language is not null, returns the project corresponding to it or null
	 * if there is no such project. If language is null, the current project is
	 * returned.
	 */
	private Project getProject(String language) {
		if (language==null) return ProjectManager.getManager().getCurrentProject();
		return ProjectManager.getManager().getProject(language);
	}
	
	public Preferences getPreferences(String language) {
		Project p=getProject(language);
		if (p==null) return defaultPreferences;
		Preferences prefs=p.getPreferences();
		if (prefs!=null) return prefs;
		return defaultPreferences;
	}
	
	public File getAlphabet(String language) {
		Project p=getProject(language);
		if (p!=null) {
			return p.getAlphabet();
		}
		return null;
	}
	
	public Font getConcordanceFont(String language) {
		return getPreferences(language).concordanceFont.font;
	}

	public String getConcordanceFontName(String language) {
		return getPreferences(language).concordanceFont.font.getName();
	}

	public int getConcordanceFontSize(String language) {
		return getPreferences(language).concordanceFont.size;
	}

	public File getConfigFileForLanguage(String language) {
		Project p=getProject(language);
		if (p==null) return null;
		return p.getConfigFile();
	}

	public String getCurrentLanguage() {
		Project p=ProjectManager.getManager().getCurrentProject();
		if (p==null) return null;
		return p.getName();
	}

	public Encoding getEncoding(String language) {
		return getPreferences(language).encoding;
	}

	public GraphPresentationInfo getGraphPresentationPreferences(String language) {
		return getPreferences(language).info;
	}

	public File getGraphRepositoryPath(String language) {
		return null;
	}

	public File getHtmlViewer(String language) {
		return null;
	}

	public Font getInputFont(String language) {
		return getPreferences(language).info.input.font;
	}

	public int getInputFontSize(String language) {
		return getPreferences(language).info.input.size;
	}

	public File getLogDirectory(String language) {
		return null;
	}

	public Font getTextFont(String language) {
		return getPreferences(language).textFont.font;
	}

	public boolean isCharByCharLanguage(String language) {
		return getPreferences(language).charByChar;
	}

	public boolean isMorphologicalUseOfSpaceAllowed(String language) {
		return getPreferences(language).morphologicalUseOfSpace;
	}

	public boolean isRightToLeftForGraphs(String language) {
		return getPreferences(language).rightToLeftForGraphs;
	}

	public boolean isRightToLeftForText(String language) {
		return getPreferences(language).rightToLeftForText;
	}

	public boolean isSemiticLanguage(String language) {
		return getPreferences(language).semitic;
	}

	public ArrayList<File> morphologicalDictionaries(String language) {
		return null;
	}

	public boolean mustLog(String language) {
		return getPreferences(language).mustLog;
	}

	public boolean onlyCosmetic(String language) {
		return getPreferences(language).onlyCosmetic;
	}

	public void savePreferences(Preferences p,String language) {
		throw new UnsupportedOperationException("To do");
	}

	public boolean svnMonitoring(String language) {
		return getPreferences(language).svnMonitoring;
	}

	public File getUnitexToolLogger() {
		return unitexToolLogger;
	}

	public File getMainDirectory() {
		return getApplicationDirectory().getParentFile();
	}

	public File getApplicationDirectory() {
		return unitexToolLogger.getParentFile();
	}

	public File getCurrentGraphDirectory() {
		Project p=getProject(null);
		if (p==null) {
			return GramlabConfigManager.getWorkspaceDirectory();
		}
		return p.getGraphsDirectory();
	}
	
	/**
	 * The behavior for compiling grf files is the following:
	 * 1) if project is not null, we try to use the alphabet file of this project
	 * 2) otherwise, we try to determine the project from grf path. If we
	 *    succeed, we use the project alphabet; otherwise, we return null.
	 */
	public File getAlphabetForGrf(String project,File grf) {
		if (project!=null) {
			Project p=ProjectManager.getManager().getProject(project);
			if (p==null) return null;
			return p.getAlphabet();
		}
		Project p=ProjectManager.getManager().getProject(grf);
		if (p==null) return null;
		return p.getAlphabet();
	}
}
