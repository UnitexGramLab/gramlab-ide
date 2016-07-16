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
package org.gramlab.core.umlv.unitex.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import org.gramlab.core.umlv.unitex.FontInfo;
import org.gramlab.core.umlv.unitex.grf.GraphPresentationInfo;
import org.gramlab.core.umlv.unitex.io.Encoding;

/**
 * This class describes language preferences.
 * 
 * @author Sébastien Paumier
 */
public class Preferences {
	/*
	 * Base preferences: the preferences the current preferences inherited of.
	 * This is useful to know when we want to save the config file, in order to
	 * dump only values that differs between preferences and base.
	 * 
	 * A null value indicates that we inherits from the default preferences.
	 */
	private Preferences base;
	private FontInfo menuFont;
	private FontInfo textFont;
	private FontInfo concordanceFont;
	/**
	 * Name of the external program to use to view HTML concordances. If
	 * <code>null</code>, concordances will be shown in a Unitex frame
	 */
	private File htmlViewer;
	/**
	 * .bin dictionaries to be used in Locate's morphological mode
	 */
	private ArrayList<File> morphologicalDic;
	/**
	 * Indicates if the language must be processed char by char or not
	 */
	private boolean charByChar;
	/**
	 * Indicates if the language must be processed allowing morphological use of
	 * spaces.
	 */
	private boolean morphologicalUseOfSpace;
	/**
	 * Indicates if the language must be read from right to left or not
	 */
	private boolean rightToLeftForText;
	private boolean rightToLeftForGraphs;
	/**
	 * Indicates if the language is a semitic one, according to the consonant
	 * skeleton model
	 */
	private boolean semitic;
	/**
	 * Path of the graph package repository
	 */
	private File graphRepositoryPath;
	// public File lexicalPackagePath;
	/**
	 * Path of the logging directory
	 */
	private File loggingDir;
	private boolean mustLog = false;
	private boolean svnMonitoring = true;
	private boolean onlyCosmetic = false;
	
	/**
	 * LocateTfst option: if true, forbids that 'air' + 'port' in the grammar can match 'airport' in the TFST
	 */
	private boolean matchWordBoundaries = true;
	
	/**
	 * Maximum size in bytes of text files. If a file is bigger than this limit,
	 * it won't be loaded.
	 */
	public static final int MAX_TEXT_FILE_SIZE = 2 * 1024 * 1024;
	public static String ICON_BAR_WEST = BorderLayout.WEST;
	public static String ICON_BAR_EAST = BorderLayout.EAST;
	public static String ICON_BAR_NORTH = BorderLayout.NORTH;
	public static String ICON_BAR_SOUTH = BorderLayout.SOUTH;
	public static String NO_ICON_BAR = "NONE";
	public static String ICON_BAR_DEFAULT = ICON_BAR_NORTH;
	private GraphPresentationInfo info;
	private Encoding encoding;

	/**
	 * Properties for current language
	 */
	protected static final Properties defaultProperties = new Properties();
	{
		/*
		 * Initialization of default properties
		 */
		defaultProperties.setProperty("TEXT FONT NAME", "Courier New");
		defaultProperties.setProperty("TEXT FONT STYLE", "" + Font.PLAIN);
		defaultProperties.setProperty("TEXT FONT SIZE", "10");
		defaultProperties.setProperty("MENU FONT NAME", "Serif");
		defaultProperties.setProperty("MENU FONT SIZE", "12");
		defaultProperties.setProperty("CONCORDANCE FONT NAME", "Courier new");
		defaultProperties.setProperty("CONCORDANCE FONT HTML SIZE", "12");
		defaultProperties.setProperty("INPUT FONT NAME", "Times New Roman");
		defaultProperties.setProperty("INPUT FONT STYLE", "" + Font.PLAIN);
		defaultProperties.setProperty("INPUT FONT SIZE", "10");
		defaultProperties.setProperty("OUTPUT FONT NAME", "Arial Unicode MS");
		defaultProperties.setProperty("OUTPUT FONT STYLE", "" + Font.BOLD);
		defaultProperties.setProperty("OUTPUT FONT SIZE", "12");
		defaultProperties.setProperty("DATE", "true");
		defaultProperties.setProperty("FILE NAME", "true");
		defaultProperties.setProperty("PATH NAME", "false");
		defaultProperties.setProperty("FRAME", "true");
		defaultProperties.setProperty("RIGHT TO LEFT FOR TEXT", "false");
		defaultProperties.setProperty("RIGHT TO LEFT FOR GRAPHS", "false");
		defaultProperties.setProperty("SEMITIC", "false");
		defaultProperties.setProperty("MATCH_WORD_BOUNDARIES", "true");
		defaultProperties.setProperty("BACKGROUND COLOR",
				"" + Color.WHITE.getRGB());
		defaultProperties.setProperty("FOREGROUND COLOR",
				"" + Color.BLACK.getRGB());
		defaultProperties.setProperty("AUXILIARY NODES COLOR", ""
				+ (new Color(205, 205, 205)).getRGB());// 13487565);
		defaultProperties.setProperty("COMMENT NODES COLOR",
				"" + Color.RED.getRGB());// 16711680");
		defaultProperties.setProperty("OUTPUT VARIABLE COLOR",
				"" + Color.BLUE.getRGB());
		defaultProperties.setProperty("PACKAGE NODES COLOR", ""
				+ (new Color(220, 220, 0)).getRGB());// 16711680");
		defaultProperties.setProperty("CONTEXT NODES COLOR",
				"" + Color.GREEN.getRGB());
		defaultProperties.setProperty("MORPHOLOGICAL NODES COLOR", ""
				+ new Color(0xC4, 0x4F, 0xD0).getRGB());
		defaultProperties.setProperty("UNREACHABLE GRAPH COLOR",
				"" + Color.RED.getRGB());
		defaultProperties.setProperty("SELECTED NODES COLOR",
				"" + Color.BLUE.getRGB());// 255");
                defaultProperties.setProperty("GENERIC GRAPH MARK COLOR", 
                                "" + Color.RED.getRGB());
		defaultProperties.setProperty("ANTIALIASING", "false");
		defaultProperties.setProperty("HTML VIEWER", "");
		defaultProperties.setProperty("MORPHOLOGICAL DICTIONARY", "");
		defaultProperties.setProperty("MAX TEXT FILE SIZE", "2048000");
		defaultProperties.setProperty("TOOLBAR POSITION", "North");
		defaultProperties.setProperty("CHAR BY CHAR", "false");
		defaultProperties.setProperty("MORPHOLOGICAL USE OF SPACE", "false");
		defaultProperties.setProperty("PACKAGE PATH", "");
		defaultProperties.setProperty("LOGGING DIR", "");
		defaultProperties.setProperty("MUST LOG", "false");
		defaultProperties.setProperty("SVN MONITORING", "true");
		defaultProperties.setProperty("ONLY COSMETIC", "false");
		defaultProperties.setProperty("ENCODING", Encoding.UTF8.toString());
	}

	/**
	 * Constructs a new <code>Preferences</code>, using language configuration
	 * values.
	 */
	public Preferences() {
		base = null;
		setPreferencesFromProperties(defaultProperties);
	}

	/**
	 * Sets configuration values to those specified by prop, inheriting the base
	 * properties if needed.
	 * 
	 * @param prop
	 */
	protected void setPreferencesFromProperties(Properties prop) {
		final Properties baseProperties = getBaseProperties();
		for (final Object key : baseProperties.keySet()) {
			if (!prop.containsKey(key)) {
				prop.put(key, baseProperties.get(key));
			}
		}
		int size = Integer.parseInt(prop.getProperty("TEXT FONT SIZE"));
		int style = Integer.parseInt(prop.getProperty("TEXT FONT STYLE"));
		Font font = new Font(prop.getProperty("TEXT FONT NAME"), style,
				(int) (size / 0.72));
		setTextFont(new FontInfo(font, size));
		size = Integer.parseInt(prop.getProperty("MENU FONT SIZE"));
		font = new Font(prop.getProperty("MENU FONT NAME"), Font.PLAIN,
				(int) (size / 0.72));
		setMenuFont(new FontInfo(font, size));
		size = Integer.parseInt(prop.getProperty("CONCORDANCE FONT HTML SIZE"));
		font = new Font(prop.getProperty("CONCORDANCE FONT NAME"), Font.PLAIN,
				(int) (size / 0.72));
		setConcordanceFont(new FontInfo(font, size));
		size = Integer.parseInt(prop.getProperty("INPUT FONT SIZE"));
		style = Integer.parseInt(prop.getProperty("INPUT FONT STYLE"));
		font = new Font(prop.getProperty("INPUT FONT NAME"), style,
				(int) (size / 0.72));
		final FontInfo input = new FontInfo(font, size);
		size = Integer.parseInt(prop.getProperty("OUTPUT FONT SIZE"));
		style = Integer.parseInt(prop.getProperty("OUTPUT FONT STYLE"));
		font = new Font(prop.getProperty("OUTPUT FONT NAME"), style,
				(int) (size / 0.72));
		final FontInfo output = new FontInfo(font, size);
		final Color backgroundColor = new Color(Integer.parseInt(prop
				.getProperty("BACKGROUND COLOR")));
		final Color foregroundColor = new Color(Integer.parseInt(prop
				.getProperty("FOREGROUND COLOR")));
		final Color subgraphColor = new Color(Integer.parseInt(prop
				.getProperty("AUXILIARY NODES COLOR")));
		final Color selectedColor = new Color(Integer.parseInt(prop
				.getProperty("SELECTED NODES COLOR")));
		final Color commentColor = new Color(Integer.parseInt(prop
				.getProperty("COMMENT NODES COLOR")));
		final Color outputVariableColor = new Color(Integer.parseInt(prop
				.getProperty("OUTPUT VARIABLE COLOR")));
		final Color packageColor = new Color(Integer.parseInt(prop
				.getProperty("PACKAGE NODES COLOR")));
		final Color contextColor = new Color(Integer.parseInt(prop
				.getProperty("CONTEXT NODES COLOR")));
		final Color morphologicalModeColor = new Color(Integer.parseInt(prop
				.getProperty("MORPHOLOGICAL NODES COLOR")));
		final Color unreachableGraphColor = new Color(Integer.parseInt(prop
				.getProperty("UNREACHABLE GRAPH COLOR")));
                final Color genericGrfColor = new Color(Integer.parseInt(prop
                                .getProperty("GENERIC GRAPH MARK COLOR")));
		final boolean date = Boolean.valueOf(prop.getProperty("DATE"));
		final boolean filename = Boolean.valueOf(prop.getProperty("FILE NAME"));
		final boolean pathname = Boolean.valueOf(prop.getProperty("PATH NAME"));
		final boolean frame = Boolean.valueOf(prop.getProperty("FRAME"));
		final boolean antialiasing = Boolean.valueOf(prop
				.getProperty("ANTIALIASING"));
		final String iconBarPosition = prop.getProperty("TOOLBAR POSITION");
		setRightToLeftForText(Boolean.valueOf(prop
				.getProperty("RIGHT TO LEFT FOR TEXT")));
		setRightToLeftForGraphs(Boolean.valueOf(prop
				.getProperty("RIGHT TO LEFT FOR GRAPHS")));
		setSemitic(Boolean.valueOf(prop.getProperty("SEMITIC")));
		setMatchWordBoundaries(Boolean.valueOf(prop.getProperty("MATCH_WORD_BOUNDARIES")));
		info = new GraphPresentationInfo(backgroundColor, foregroundColor,
				subgraphColor, selectedColor, commentColor,
				outputVariableColor, packageColor, contextColor,
				morphologicalModeColor, unreachableGraphColor, input, output,
				date, filename, pathname, frame, isRightToLeftForGraphs(),
				antialiasing, iconBarPosition,genericGrfColor);
		String s = prop.getProperty("HTML VIEWER");
		setHtmlViewer((s == null || s.equals("")) ? null : new File(s));
		setMorphologicalDic(tokenizeMorphologicalDicList(prop
				.getProperty("MORPHOLOGICAL DICTIONARY")));
		setCharByChar(Boolean.valueOf(prop.getProperty("CHAR BY CHAR")));
		setMorphologicalUseOfSpace(Boolean.valueOf(prop
				.getProperty("MORPHOLOGICAL USE OF SPACE")));
		s = prop.getProperty("PACKAGE PATH");
		setGraphRepositoryPath((s == null || s.equals("")) ? null : new File(s));
		/*
		 * s = prop.getProperty("LEXICAL PACKAGE PATH"); lexicalPackagePath = (s
		 * == null || s.equals("")) ? null : new File(s);
		 */
		s = prop.getProperty("LOGGING DIR");
		setLoggingDir((s == null || s.equals("")) ? null : new File(s));
		setMustLog(Boolean.valueOf(prop.getProperty("MUST LOG")));
		if (isMustLog() && getLoggingDir() == null) {
			/* Should not happen */
			setMustLog(false);
		}
		setSvnMonitoring(Boolean.valueOf(prop.getProperty("SVN MONITORING")));
		setOnlyCosmetic(Boolean.valueOf(prop.getProperty("ONLY COSMETIC")));
		try {
			setEncoding(Encoding.valueOf(prop.getProperty("ENCODING")));
		} catch (final Exception e) {
			setEncoding(Encoding.UTF8);
		}
	}

	private ArrayList<File> tokenizeMorphologicalDicList(String s) {
		if (s == null || s.equals(""))
			return null;
		final ArrayList<File> list = new ArrayList<File>();
		final StringTokenizer tokenizer = new StringTokenizer(s, ";");
		while (tokenizer.hasMoreTokens()) {
			list.add(new File(tokenizer.nextToken()));
		}
		return list;
	}

	public Properties setPropertiesFromPreferences() {
		final Properties prop = new Properties();
		prop.setProperty("TEXT FONT NAME", getTextFont().getFont().getName());
		prop.setProperty("TEXT FONT STYLE", ""
				+ getTextFont().getFont().getStyle());
		prop.setProperty("TEXT FONT SIZE", "" + getTextFont().getSize());
		prop.setProperty("MENU FONT NAME", getMenuFont().getFont().getName());
		prop.setProperty("MENU FONT SIZE", "" + getMenuFont().getSize());
		prop.setProperty("CONCORDANCE FONT NAME", getConcordanceFont()
				.getFont().getName());
		prop.setProperty("CONCORDANCE FONT HTML SIZE", ""
				+ getConcordanceFont().getSize());
		prop.setProperty("INPUT FONT NAME", info.getInput().getFont().getName());
		prop.setProperty("INPUT FONT STYLE", ""
				+ info.getInput().getFont().getStyle());
		prop.setProperty("INPUT FONT SIZE", "" + info.getInput().getSize());
		prop.setProperty("OUTPUT FONT NAME", info.getOutput().getFont()
				.getName());
		prop.setProperty("OUTPUT FONT STYLE", ""
				+ info.getOutput().getFont().getStyle());
		prop.setProperty("OUTPUT FONT SIZE", "" + info.getOutput().getSize());
		prop.setProperty("DATE", "" + info.isDate());
		prop.setProperty("FILE NAME", "" + info.isFilename());
		prop.setProperty("PATH NAME", "" + info.isPathname());
		prop.setProperty("FRAME", "" + info.isFrame());
		prop.setProperty("RIGHT TO LEFT FOR TEXT", "" + isRightToLeftForText());
		prop.setProperty("RIGHT TO LEFT FOR GRAPHS", ""
				+ isRightToLeftForGraphs());
		prop.setProperty("SEMITIC", "" + isSemitic());
		prop.setProperty("MATCH_WORD_BOUNDARIES", "" + isMatchWordBoundaries());
		prop.setProperty("BACKGROUND COLOR", ""
				+ info.getBackgroundColor().getRGB());
		prop.setProperty("FOREGROUND COLOR", ""
				+ info.getForegroundColor().getRGB());
		prop.setProperty("AUXILIARY NODES COLOR", ""
				+ info.getSubgraphColor().getRGB());
		prop.setProperty("COMMENT NODES COLOR", ""
				+ info.getCommentColor().getRGB());
		prop.setProperty("SELECTED NODES COLOR", ""
				+ info.getSelectedColor().getRGB());
		prop.setProperty("PACKAGE NODES COLOR", ""
				+ info.getPackageColor().getRGB());
		prop.setProperty("CONTEXT NODES COLOR", ""
				+ info.getContextColor().getRGB());
                prop.setProperty("GENERIC GRAPH MARK COLOR", ""
				+ info.getGenericGrfColor().getRGB());
		prop.setProperty("MORPHOLOGICAL NODES COLOR", ""
				+ info.getMorphologicalModeColor().getRGB());
		prop.setProperty("OUTPUT VARIABLE COLOR", ""
				+ info.getOutputVariableColor().getRGB());
		prop.setProperty("UNREACHABLE GRAPH COLOR", ""
				+ info.getUnreachableGraphColor().getRGB());
		prop.setProperty("ANTIALIASING", "" + info.isAntialiasing());
		prop.setProperty("HTML VIEWER", (getHtmlViewer() == null) ? ""
				: getHtmlViewer().getAbsolutePath());
		prop.setProperty("MORPHOLOGICAL DICTIONARY",
				getMorphologicalDicListAsString(getMorphologicalDic()));
		prop.setProperty("MAX TEXT FILE SIZE", "" + MAX_TEXT_FILE_SIZE);
		prop.setProperty("ICON BAR POSITION", info.getIconBarPosition());
		prop.setProperty("CHAR BY CHAR", "" + isCharByChar());
		prop.setProperty("MORPHOLOGICAL USE OF SPACE", ""
				+ isMorphologicalUseOfSpace());
		prop.setProperty("PACKAGE PATH",
				(getGraphRepositoryPath() == null) ? ""
						: getGraphRepositoryPath().getAbsolutePath());
		// prop.setProperty("LEXICAL PACKAGE PATH", (lexicalPackagePath == null)
		// ? "" : lexicalPackagePath.getAbsolutePath());
		prop.setProperty("LOGGING DIR", (getLoggingDir() == null) ? ""
				: getLoggingDir().getAbsolutePath());
		prop.setProperty("MUST LOG", "" + isMustLog());
		prop.setProperty("SVN MONITORING", "" + isSvnMonitoring());
		prop.setProperty("ONLY COSMETIC", "" + isOnlyCosmetic());
		prop.setProperty("ENCODING", getEncoding().toString());
		return prop;
	}

	private String getMorphologicalDicListAsString(ArrayList<File> list) {
		if (list == null || list.size() == 0) {
			return "";
		}
		String s = list.get(0).getAbsolutePath();
		for (int i = 1; i < list.size(); i++) {
			s = s + ";" + list.get(i).getAbsolutePath();
		}
		return s;
	}

	/**
	 * @return a copy of the preferences
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Preferences clone() {
		final Preferences p = new Preferences();
		p.base = base;
		p.setTextFont(textFont);
		p.setMenuFont(menuFont);
		p.setConcordanceFont(concordanceFont);
		p.setHtmlViewer(htmlViewer);
		p.setMorphologicalDic((getMorphologicalDic() == null) ? null
				: (ArrayList<File>) getMorphologicalDic().clone());
		p.setCharByChar(charByChar);
		p.setMorphologicalUseOfSpace(morphologicalUseOfSpace);
		p.setRightToLeftForText(rightToLeftForText);
		p.setRightToLeftForGraphs(rightToLeftForGraphs);
		p.setSemitic(semitic);
		p.setMatchWordBoundaries(matchWordBoundaries);
		p.setGraphRepositoryPath(graphRepositoryPath);
		p.setLoggingDir(loggingDir);
		p.setMustLog(mustLog);
		p.setSvnMonitoring(svnMonitoring);
		p.setOnlyCosmetic(onlyCosmetic);
		p.info = (info == null) ? null : info.clone();
		p.setEncoding(encoding);
		return p;
	}

	public Properties getBaseProperties() {
		if (base == null) {
			return defaultProperties;
		}
		return base.setPropertiesFromPreferences();
	}

	public void setBase(Preferences p) {
		this.base = p;
	}

	/**
	 * Returns properties that differ from base properties.
	 */
	public Properties getOwnProperties() {
		final Properties tmp = setPropertiesFromPreferences();
		final Properties diff = new Properties();
		final Properties base1 = getBaseProperties();
		for (final Object key : tmp.keySet()) {
			final Object value = tmp.get(key);
			if (!base1.containsKey(key) || !base1.get(key).equals(value)) {
				diff.put(key, value);
			}
		}
		return diff;
	}

	public GraphPresentationInfo getInfo() {
		return info;
	}

	public void setInfo(GraphPresentationInfo info) {
		this.info = info;
	}

	public void setCharByChar(boolean charByChar) {
		this.charByChar = charByChar;
	}

	public boolean isCharByChar() {
		return charByChar;
	}

	public void setEncoding(Encoding encoding) {
		this.encoding = encoding;
	}

	public Encoding getEncoding() {
		return encoding;
	}

	public void setSemitic(boolean semitic) {
		this.semitic = semitic;
	}

	public boolean isSemitic() {
		return semitic;
	}

	public void setMatchWordBoundaries(boolean b) {
		this.matchWordBoundaries = b;
	}

	public boolean isMatchWordBoundaries() {
		return matchWordBoundaries;
	}

	public void setRightToLeftForGraphs(boolean rightToLeftForGraphs) {
		this.rightToLeftForGraphs = rightToLeftForGraphs;
	}

	public boolean isRightToLeftForGraphs() {
		return rightToLeftForGraphs;
	}

	public void setRightToLeftForText(boolean rightToLeftForText) {
		this.rightToLeftForText = rightToLeftForText;
	}

	public boolean isRightToLeftForText() {
		return rightToLeftForText;
	}

	public void setMorphologicalDic(ArrayList<File> morphologicalDic) {
		this.morphologicalDic = morphologicalDic;
	}

	public ArrayList<File> getMorphologicalDic() {
		return morphologicalDic;
	}

	public void setMorphologicalUseOfSpace(boolean morphologicalUseOfSpace) {
		this.morphologicalUseOfSpace = morphologicalUseOfSpace;
	}

	public boolean isMorphologicalUseOfSpace() {
		return morphologicalUseOfSpace;
	}

	public void setOnlyCosmetic(boolean onlyCosmetic) {
		this.onlyCosmetic = onlyCosmetic;
	}

	public boolean isOnlyCosmetic() {
		return onlyCosmetic;
	}

	public void setSvnMonitoring(boolean svnMonitoring) {
		this.svnMonitoring = svnMonitoring;
	}

	public boolean isSvnMonitoring() {
		return svnMonitoring;
	}

	public void setGraphRepositoryPath(File graphRepositoryPath) {
		this.graphRepositoryPath = graphRepositoryPath;
	}

	public File getGraphRepositoryPath() {
		return graphRepositoryPath;
	}

	public void setLoggingDir(File loggingDir) {
		this.loggingDir = loggingDir;
	}

	public File getLoggingDir() {
		return loggingDir;
	}

	public void setMustLog(boolean mustLog) {
		this.mustLog = mustLog;
	}

	public boolean isMustLog() {
		return mustLog;
	}

	public void setMenuFont(FontInfo menuFont) {
		this.menuFont = menuFont;
	}

	public FontInfo getMenuFont() {
		return menuFont;
	}

	public void setTextFont(FontInfo textFont) {
		this.textFont = textFont;
	}

	public FontInfo getTextFont() {
		return textFont;
	}

	public void setHtmlViewer(File htmlViewer) {
		this.htmlViewer = htmlViewer;
	}

	public File getHtmlViewer() {
		return htmlViewer;
	}

	public void setConcordanceFont(FontInfo concordanceFont) {
		this.concordanceFont = concordanceFont;
	}

	public FontInfo getConcordanceFont() {
		return concordanceFont;
	}

}
