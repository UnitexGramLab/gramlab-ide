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

public interface ConfigModel {
	public String getCurrentLanguage();

	public File getCurrentLanguageDir();

	public File getAlphabetForGrf(String language, File grf);

	public File getAlphabet(String language);

	public Preferences getPreferences(String language);

	public void savePreferences(Preferences p, String language);

	public boolean isCharByCharLanguage(String language);

	public File getConfigFileForLanguage(String language);

	public Encoding getEncoding(String language);

	public boolean isSemiticLanguage(String language);

	public boolean isMatchWordBoundaries(String language);

	public boolean isRightToLeftForText(String language);

	public boolean isRightToLeftForGraphs(String language);

	public ArrayList<File> morphologicalDictionaries(String language);

	public boolean isMorphologicalUseOfSpaceAllowed(String language);

	public boolean svnMonitoring(String language);

	public boolean onlyCosmetic(String language);

	public GraphPresentationInfo getGraphPresentationPreferences(String language);

	public File getDefaultGraphRepositoryPath(String language);

	/* name==null -> default repository */
	public File getGraphRepositoryPath(String language, String repositoryName);

	public ArrayList<NamedRepository> getNamedRepositories(String language);

	public File getLogDirectory(String language);

	public boolean mustLog(String language);

	public File getHtmlViewer(String language);

	public Font getTextFont(String language);

	public Font getInputFont(String language);

	public int getInputFontSize(String language);

	public Font getConcordanceFont(String language);

	public String getConcordanceFontName(String language);

	public int getConcordanceFontSize(String language);

	public boolean isKorean(String language);

	public boolean isArabic(String language);

	public boolean isThai(String language);

	public boolean isPRLGLanguage(String language);

	public boolean isValidLanguageName(String language);

	public File getMainDirectory();

	public File getApplicationDirectory();

	public File getUnitexToolLogger();

	public File getCurrentGraphDirectory();

	public boolean emitEmptyGraphWarning(String language);

	public boolean displayGraphNames(String language);

	public boolean maximizeGraphFrames();

	public File getCurrentSnt(String language);

	public File getInflectionDir();

	public SvnMonitor getSvnMonitor(File f);

	public void userRefusedClosingFrame();

  public File getPluginsDirectory();

}
