/*
 * Unitex
 *
 * Copyright (C) 2001-2016 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

	@Override
	public File getAlphabet(String language) {
		if (language == null) {
			language = Config.getCurrentLanguage();
		}
		return new File(new File(Config.getUserDir(), language), "Alphabet.txt");
	}

	@Override
	public boolean isCharByCharLanguage(String language) {
		if (language == null) {
			language = Config.getCurrentLanguage();
		}
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.isCharByChar();
	}

	@Override
	public File getConfigFileForLanguage(String language) {
		if (language == null) {
			language = Config.getCurrentLanguage();
		}
		return new File(new File(Config.getUserDir(), language), "Config");
	}

	@Override
	public Encoding getEncoding(String language) {
		if (language == null) {
			language = Config.getCurrentLanguage();
		}
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getEncoding();
	}

	@Override
	public boolean isSemiticLanguage(String language) {
		if (language == null) {
			language = Config.getCurrentLanguage();
		}
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.isSemitic();
	}

	@Override
	public boolean isMatchWordBoundaries(String language) {
		if (language == null) {
			language = Config.getCurrentLanguage();
		}
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.isMatchWordBoundaries();
	}

	@Override
	public boolean isRightToLeftForGraphs(String language) {
		if (language == null) {
			language = Config.getCurrentLanguage();
		}
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.isRightToLeftForGraphs();
	}

	@Override
	public boolean isRightToLeftForText(String language) {
		if (language == null) {
			language = Config.getCurrentLanguage();
		}
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.isRightToLeftForText();
	}

	@Override
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

	@Override
	public String getCurrentLanguage() {
		return Config.getCurrentLanguage();
	}

	@Override
	public boolean isMorphologicalUseOfSpaceAllowed(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.isMorphologicalUseOfSpace();
	}

	@Override
	public boolean onlyCosmetic(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.isOnlyCosmetic();
	}

	@Override
	public boolean svnMonitoring(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.isSvnMonitoring();
	}

	@Override
	public GraphPresentationInfo getGraphPresentationPreferences(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getInfo().clone();
	}

	@Override
	public File getDefaultGraphRepositoryPath(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getGraphRepositoryPath();
	}

	@Override
	public ArrayList<NamedRepository> getNamedRepositories(String language) {
		/* In Unitex, we ignore repository names */
		return null;
	}

	@Override
	public File getLogDirectory(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getLoggingDir();
	}

	@Override
	public boolean mustLog(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.isMustLog();
	}

	@Override
	public Font getInputFont(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getInfo().getInput().getFont();
	}

	@Override
	public int getInputFontSize(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getInfo().getInput().getSize();
	}

	@Override
	public Font getTextFont(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getTextFont().getFont();
	}

	@Override
	public Preferences getPreferences(String language) {
		if (language == null)
			language = getCurrentLanguage();
		return PreferencesManager.getPreferences(language).clone();
	}

	@Override
	public void savePreferences(Preferences p, String language) {
		if (language == null)
			language = getCurrentLanguage();
		final File f = getConfigFileForLanguage(language);
		PreferencesManager.savePreferences(f, p, language);
	}

	@Override
	public File getHtmlViewer(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getHtmlViewer();
	}

	@Override
	public Font getConcordanceFont(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getConcordanceFont().getFont();
	}

	@Override
	public String getConcordanceFontName(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getConcordanceFont().getFont().getName();
	}

	@Override
	public int getConcordanceFontSize(String language) {
		if (language == null)
			language = getCurrentLanguage();
		final Preferences p = PreferencesManager.getPreferences(language);
		return p.getConcordanceFont().getSize();
	}

	@Override
	public File getUnitexToolLogger() {
		return Config.getUnitexToolLogger();
	}

	@Override
	public File getMainDirectory() {
		return Config.getUnitexDir();
	}

	@Override
	public File getApplicationDirectory() {
		return Config.getApplicationDir();
	}

	@Override
	public File getCurrentGraphDirectory() {
		return Config.getCurrentGraphDir();
	}

	@Override
	public File getAlphabetForGrf(String language, File grf) {
		return getAlphabet(language);
	}

	@Override
	public boolean displayGraphNames(String language) {
		return true;
	}

	@Override
	public boolean emitEmptyGraphWarning(String language) {
		return true;
	}

	@Override
	public boolean maximizeGraphFrames() {
		return false;
	}

	@Override
	public File getCurrentSnt(String language) {
		return Config.getCurrentSnt();
	}

	@Override
	public File getCurrentLanguageDir() {
		return Config.getUserCurrentLanguageDir();
	}

	@Override
	public File getInflectionDir() {
		return new File(Config.getUserCurrentLanguageDir(), "Inflection");
	}

	@Override
	public SvnMonitor getSvnMonitor(File f) {
		return Config.getSvnMonitor();
	}

	@Override
	public void userRefusedClosingFrame() {
		/* Do nothing */
	}

	@Override
	public File getPluginsDirectory() {
		return Config.getPluginsDir();
	}

}
