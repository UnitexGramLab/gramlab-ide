package fr.gramlab.project.config;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.gramlab.GramlabConfigManager;
import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.GramlabProjectManager;
import fr.umlv.unitex.config.AbstractConfigModel;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.NamedRepository;
import fr.umlv.unitex.config.Preferences;
import fr.umlv.unitex.exceptions.UserRefusedFrameClosingError;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.grf.GraphPresentationInfo;
import fr.umlv.unitex.io.Encoding;
import fr.umlv.unitex.svn.SvnMonitor;

/**
 * This class provides the default Unitex config information,
 * except for the encoding that is UTF8.
 * @author paumier
 *
 */
public class ProjectPreferences extends AbstractConfigModel {

	private Preferences defaultPreferences;
	private File unitexToolLogger;
	
	public ProjectPreferences(File jarPath) {
		unitexToolLogger = Config.setupUnitexToolLogger(jarPath);
		defaultPreferences=new Preferences();
		defaultPreferences.setEncoding(Encoding.UTF8);
	}
	
	/**
	 * If language is not null, returns the project corresponding to it or null
	 * if there is no such project. If language is null, the current project is
	 * returned.
	 */
	private GramlabProject getProject(String language) {
		if (language==null) return GlobalProjectManager.getAs(GramlabProjectManager.class).getCurrentProject();
		return GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(language);
	}
	
	public Preferences getPreferences(String language) {
		GramlabProject p=getProject(language);
		if (p==null) return defaultPreferences;
		Preferences prefs=p.getPreferences();
		if (prefs!=null) return prefs;
		return defaultPreferences;
	}
	
	public File getAlphabet(String language) {
		GramlabProject p=getProject(language);
		if (p!=null) {
			return p.getAlphabet();
		}
		return null;
	}
	
	public Font getConcordanceFont(String language) {
		return getPreferences(language).getConcordanceFont().getFont();
	}

	public String getConcordanceFontName(String language) {
		return getPreferences(language).getConcordanceFont().getFont().getName();
	}

	public int getConcordanceFontSize(String language) {
		return getPreferences(language).getConcordanceFont().getSize();
	}

	public File getConfigFileForLanguage(String language) {
		GramlabProject p=getProject(language);
		if (p==null) return null;
		return p.getPreferencesFile();
	}

	public String getCurrentLanguage() {
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class)
				.getCurrentProject();
		if (p==null) return null;
		return p.getName();
	}

	public GraphPresentationInfo getGraphPresentationPreferences(String language) {
		return getPreferences(language).getInfo();
	}

	public Font getInputFont(String language) {
		return getPreferences(language).getInfo().getInput().getFont();
	}

	public int getInputFontSize(String language) {
		return getPreferences(language).getInfo().getInput().getSize();
	}

	public File getLogDirectory(String language) {
		return getPreferences(language).getLoggingDir();
	}

	public Font getTextFont(String language) {
		return getPreferences(language).getTextFont().getFont();
	}

	public boolean isRightToLeftForGraphs(String language) {
		return getPreferences(language).isRightToLeftForGraphs();
	}

	public boolean isRightToLeftForText(String language) {
		return getPreferences(language).isRightToLeftForText();
	}

	public boolean mustLog(String language) {
		return getPreferences(language).isMustLog();
	}

	public boolean onlyCosmetic(String language) {
		return getPreferences(language).isOnlyCosmetic();
	}

	public void savePreferences(Preferences p,String language) {
		throw new UnsupportedOperationException("To do");
	}

	public boolean svnMonitoring(String language) {
		return getPreferences(language).isSvnMonitoring();
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
		GramlabProject p=getProject(null);
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
			GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(project);
			if (p==null) return null;
			return p.getAlphabet();
		}
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(grf);
		if (p==null) return null;
		return p.getAlphabet();
	}

	/**
	 * For the following preferences, we override what may be found in the Preferences
	 * by something found in ProjectConfig
	 */
	
	
	@Override
	public File getGraphRepositoryPath(String project,String repositoryName) {
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(project);
		if (p==null) return null;
		return p.getNamedRepository(repositoryName);
	}

	@Override
	public File getDefaultGraphRepositoryPath(String project) {
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(project);
		if (p==null) return null; 
		return p.getDefaultGraphRepository();
	}

	@Override
	public ArrayList<NamedRepository> getNamedRepositories(String project) {
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(project);
		if (p==null) return null; 
		return p.getNamedRepositories();
	}

	@Override
	public boolean displayGraphNames(String project) {
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(project);
		if (p==null) return true; 
		return p.displayGraphNames();
	}

	@Override
	public boolean emitEmptyGraphWarning(String project) {
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(project);
		if (p==null) return true; 
		return p.emitEmptyGraphWarning();
	}
	
	@Override
	public Encoding getEncoding(String project) {
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(project);
		if (p==null) return null;
		return p.getEncoding();
	}

	@Override
	public File getHtmlViewer(String project) {
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(project);
		return p.getHtmlViewer();
	}

	@Override
	public boolean isCharByCharLanguage(String project) {
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(project);
		if (p==null) return false;
		return p.isCharByChar();
	}

	@Override
	public boolean isMorphologicalUseOfSpaceAllowed(String project) {
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(project);
		if (p==null) return false;
		return p.isMorphologicalUseOfSpace();
	}

	@Override
	public boolean isSemiticLanguage(String project) {
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(project);
		if (p==null) return false;
		return p.isSemitic();
	}

	@Override
	public ArrayList<File> morphologicalDictionaries(String project) {
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(project);
		if (p==null) return null;
		return p.getMorphoDics();
	}
	
	@Override
	public boolean isKorean(String project) {
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(project);
		if (p==null) return false;
		return p.isKorean();
	}

	@Override
  public boolean isMatchWordBoundaries(String project) {
    GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(project);
    if (p==null) return false;
    return p.isMatchWordBoundaries();
  }

  @Override
	public boolean isArabic(String project) {
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(project);
		if (p==null) return false;
		return p.getLanguage().equals("ar");
	}
	
	@Override
	public boolean isThai(String project) {
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(project);
		if (p==null) return false;
		return p.getLanguage().equals("th");
	}

	@Override
	public boolean maximizeGraphFrames() {
		return true;
	}

	@Override
	public File getCurrentSnt(String project) {
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(project);
		System.err.println("getCurrentSnt");
		if (p==null) return null;
		System.err.println(" => "+p.getCurrentCorpus());
		System.err.println(" ==> "+FileUtil.getSnt(p.getCurrentCorpus()));
		return FileUtil.getSnt(p.getCurrentCorpus());
	}

	@Override
	public File getCurrentLanguageDir() {
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getCurrentProject();
		if (p==null) return null;
		return p.getProjectDirectory();
	}

	@Override
	public SvnMonitor getSvnMonitor(File f) {
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(f);
		if (p==null) return null;
		return p.getSvnMonitor();
	}

	@Override
	public void userRefusedClosingFrame() {
		if (closingGramlab) {
			throw new UserRefusedFrameClosingError();
		}
	}

	private static boolean closingGramlab=false;
	
	public static void setClosingGramlab(boolean b) {
		closingGramlab=b;
	}

	@Override
	public File getInflectionDir() {
		GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class).getCurrentProject();
		if (p==null) return null;
		return p.getInflectionDirectory();
	}

	public File getPluginsDirectory() {
		return new File(getApplicationDirectory(),Config.DEFAULT_PLUGINS_DIRECTORY);
	}
}
