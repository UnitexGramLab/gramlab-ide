package fr.gramlab.config;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;

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
public class DefaultConfig extends AbstractConfigModel {

	private Preferences defaultPreferences;
	private File unitexToolLogger;
	
	public DefaultConfig(File jarPath) {
		unitexToolLogger=new File(jarPath,"UnitexToolLogger"+(Config.getCurrentSystem() == Config.WINDOWS_SYSTEM ? ".exe" : ""));
		defaultPreferences=new Preferences();
		defaultPreferences.encoding=Encoding.UTF8;
	}
	
	public File getAlphabet(String language) {
		return null;
	}

	public Font getConcordanceFont(String language) {
		return defaultPreferences.concordanceFont.font;
	}

	public String getConcordanceFontName(String language) {
		return defaultPreferences.concordanceFont.font.getName();
	}

	public int getConcordanceFontSize(String language) {
		return defaultPreferences.concordanceFont.size;
	}

	public File getConfigFileForLanguage(String language) {
		return null;
	}

	public String getCurrentLanguage() {
		return null;
	}

	public Encoding getEncoding(String language) {
		return defaultPreferences.encoding;
	}

	public GraphPresentationInfo getGraphPresentationPreferences(String language) {
		return defaultPreferences.info;
	}

	public File getGraphRepositoryPath(String language) {
		return null;
	}

	public File getHtmlViewer(String language) {
		return null;
	}

	public Font getInputFont(String language) {
		return defaultPreferences.info.input.font;
	}

	public int getInputFontSize(String language) {
		return defaultPreferences.info.input.size;
	}

	public File getLogDirectory(String language) {
		return null;
	}

	public Preferences getPreferences(String language) {
		return defaultPreferences;
	}

	public Font getTextFont(String language) {
		return defaultPreferences.textFont.font;
	}

	public boolean isCharByCharLanguage(String language) {
		return defaultPreferences.charByChar;
	}

	public boolean isMorphologicalUseOfSpaceAllowed(String language) {
		return defaultPreferences.morphologicalUseOfSpace;
	}

	public boolean isRightToLeftForGraphs(String language) {
		return defaultPreferences.rightToLeftForGraphs;
	}

	public boolean isRightToLeftForText(String language) {
		return defaultPreferences.rightToLeftForText;
	}

	public boolean isSemiticLanguage(String language) {
		return defaultPreferences.semitic;
	}

	public ArrayList<File> morphologicalDictionaries(String language) {
		return null;
	}

	public boolean mustLog(String language) {
		return defaultPreferences.mustLog;
	}

	public boolean onlyCosmetic(String language) {
		return defaultPreferences.onlyCosmetic;
	}

	public void savePreferences(Preferences p,String language) {
		/* Nothing to do, it's the default config */
	}

	public boolean svnMonitoring(String language) {
		return defaultPreferences.svnMonitoring;
	}

	public String getConfigName() {
		return "No current project";
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
		return GramlabConfigManager.getWorkspaceDirectory();
	}
}
