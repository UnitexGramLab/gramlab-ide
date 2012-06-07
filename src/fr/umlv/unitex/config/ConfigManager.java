/*
 * Unitex
 *
 * Copyright (C) 2001-2012 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 *
 */
package fr.umlv.unitex.config;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;

import fr.umlv.unitex.grf.GraphPresentationInfo;
import fr.umlv.unitex.io.Encoding;
import fr.umlv.unitex.svn.SvnMonitor;

/**
 * This class is the corner stone of language configuration. If a project
 * manager has been set, all requests are transmitted to it, allowing callbacks
 * for Gramlab compatibility. Otherwise, the ConfigManager object uses the
 * unitex configuration associated to the given language. If the language is
 * null, then the current language is used.
 * 
 * @author paumier
 * 
 */
public class ConfigManager extends AbstractConfigModel {
	
	private static ConfigModel cm;

	public static ConfigModel getManager() {
		return cm;
	}

	public static void setManager(ConfigModel cm) {
		ConfigManager.cm = cm;
	}

	public File getAlphabet(String language) {
		if (language == null) {
			language = Config.getCurrentLanguage();
		}
		return new File(new File(Config.getUserDir(), language), "Alphabet.txt");
	}

	public boolean isCharByCharLanguage(String language) {
		if (language == null) {
			language = Config.getCurrentLanguage();
		}
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.isCharByChar();
	}

	public File getConfigFileForLanguage(String language) {
		if (language == null) {
			language = Config.getCurrentLanguage();
		}
		return new File(new File(Config.getUserDir(), language), "Config");
	}

	public Encoding getEncoding(String language) {
		if (language == null) {
			language = Config.getCurrentLanguage();
		}
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getEncoding();
	}

	public boolean isSemiticLanguage(String language) {
		if (language == null) {
			language = Config.getCurrentLanguage();
		}
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.isSemitic();
	}

	public boolean isRightToLeftForGraphs(String language) {
		if (language == null) {
			language = Config.getCurrentLanguage();
		}
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.isRightToLeftForGraphs();
	}

	public boolean isRightToLeftForText(String language) {
		if (language == null) {
			language = Config.getCurrentLanguage();
		}
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.isRightToLeftForText();
	}

	public ArrayList<File> morphologicalDictionaries(String language) {
		if (language == null) {
			language = Config.getCurrentLanguage();
		}
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getMorphologicalDic();
	}

	@Override
	public boolean isKorean(String language) {
		if (language == null)
			language = getCurrentLanguage();
		return super.isKorean(language) || language.equals("KoreanJeeSun");
	}

	public String getCurrentLanguage() {
		return Config.getCurrentLanguage();
	}

	public boolean isMorphologicalUseOfSpaceAllowed(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.isMorphologicalUseOfSpace();
	}

	public boolean onlyCosmetic(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.isOnlyCosmetic();
	}

	public boolean svnMonitoring(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.isSvnMonitoring();
	}

	public GraphPresentationInfo getGraphPresentationPreferences(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getInfo().clone();
	}

	public File getDefaultGraphRepositoryPath(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getGraphRepositoryPath();
	}	
		
	public ArrayList<NamedRepository> getNamedRepositories(String language) {
		/* In Unitex, we ignore repository names */
		return null;
	}

	public File getLogDirectory(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getLoggingDir();
	}

	public boolean mustLog(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.isMustLog();
	}

	public Font getInputFont(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getInfo().getInput().getFont();
	}

	public int getInputFontSize(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getInfo().getInput().getSize();
	}

	public Font getTextFont(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getTextFont().getFont();
	}

	public Preferences getPreferences(String language) {
		if (language == null)
			language = getCurrentLanguage();
		return PreferencesManager.getPreferences(language).clone();
	}

	public void savePreferences(Preferences p, String language) {
		if (language == null)
			language = getCurrentLanguage();
		final File f = getConfigFileForLanguage(language);
		PreferencesManager.savePreferences(f, p, language);
	}

	public File getHtmlViewer(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getHtmlViewer();
	}

	public Font getConcordanceFont(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getConcordanceFont().getFont();
	}

	public String getConcordanceFontName(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getConcordanceFont().getFont().getName();
	}

	public int getConcordanceFontSize(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getConcordanceFont().getSize();
	}

	public File getUnitexToolLogger() {
		return Config.getUnitexToolLogger();
	}

	public File getMainDirectory() {
		return Config.getUnitexDir();
	}

	public File getApplicationDirectory() {
		return Config.getApplicationDir();
	}

	public File getCurrentGraphDirectory() {
		return Config.getCurrentGraphDir();
	}

	public File getAlphabetForGrf(String language, File grf) {
		return getAlphabet(language);
	}

	public boolean displayGraphNames(String language) {
		return true;
	}

	public boolean emitEmptyGraphWarning(String language) {
		return true;
	}

	public boolean maximizeGraphFrames() {
		return false;
	}

	public File getCurrentSnt(String language) {
		return Config.getCurrentSnt();
	}

	public File getCurrentLanguageDir() {
		return Config.getUserCurrentLanguageDir();
	}

	public File getInflectionDir() {
		return new File(Config.getUserCurrentLanguageDir(),"Inflection");
	}
	
	public SvnMonitor getSvnMonitor(File f) {
		return Config.getSvnMonitor();
	}

	public void userRefusedClosingFrame() {
		/* Do nothing */
	}
	
}
