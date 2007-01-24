/*
 * Unitex
 *
 * Copyright (C) 2001-2007 Université de Marne-la-Vallée <unitex@univ-mlv.fr>
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

package fr.umlv.unitex;

import java.awt.*;
import java.io.*;
import java.util.Properties;


/**
 * This class describes graph presentation preferences.
 * @author Sébastien Paumier
 *
 */
public class Preferences {

	/**
	 * Box input font 
	 */
	public Font input;

	/**
	 * Box output font 
	 */
	public Font output;

	/**
	 * Text font used in edition text field  
	 */
	public Font textFont;

	/**
	 * Background color 
	 */
	public Color backgroundColor;

	/**
	 * Foreground color, used to draw text, boxes and transitions  
	 */
	public Color foregroundColor;

	/**
	 * Color used to draw box lines that refer to sub-graphs 
	 */
	public Color subgraphColor;

	/**
	 * Color used to draw selected boxes 
	 */
	public Color selectedColor;

	/**
	 * Color used to draw comment boxes, i.e. boxes with no input or output transition
	 */
	public Color commentColor;

	/**
	 * Color used to draw boxes lines that refers to subgraphs within packages
	 */
	public Color packageColor;
	
	/**
	 * Color used to context boxes
	 */
	public Color contextColor;
	
	/**
	 * Indicates if the date must be shown on graphs 
	 */
	public boolean date;

	/**
	 * Indicates if file names must be shown on graphs 
	 */
	public boolean filename;

	/**
	 * Indicates if path name must be shown on graphs 
	 */
	public boolean pathname;

	/**
	 * Indicates if a frame must be drawn around the graph 
	 */
	public boolean frame;

	/**
	 * Size of input font
	 */
	public int inputSize;

	/**
	 * Size of output font
	 */
	public int outputSize;

	/**
	 * Size of edition text field font
	 */
	public int textFontSize;

	/**
	 * Style of input font
	 */
	public int inputFontStyle;

	/**
	 * Style of output font
	 */
	public int outputFontStyle;

	/**
	 * Style of edition text field font
	 */
	public int textFontStyle;

	/**
	 * Name of the font used to render HTML concordances 
	 */
	public String htmlFontName;

	/**
	 * HTML size of the font used to render HTML concordances (1 to 7) 
	 */
	public int htmlFontSize;

	/**
	 * Indicates if the graph must be rendered with an antialiasing effect or not 
	 */
	public boolean antialiasing;

	/**
	 * Name of the external program to use to view HTML concordances. If <code>null</code>,
	 * concordances will be shown in a Unitex frame  
	 */
	public File htmlViewer;

	/**
	 * Indicates if the language must be processed char by char or not
	 */
	public boolean charByChar;

	/**
	 * Indicates if the language must be read from right to left or not
	 */
	public boolean rightToLeft;
	
	/**
	 * Path of the graph package repository
	 */
	public File packagePath;

	/**
	 * Maximum size in bytes of text files. If a file is bigger than this 
	 * limit, it won't be loaded. 
	 */
	public int MAX_TEXT_FILE_SIZE = 2 * 1024 * 1024;

	static String ICON_BAR_WEST = BorderLayout.WEST;

	static String ICON_BAR_EAST = BorderLayout.EAST;

	static String ICON_BAR_NORTH = BorderLayout.NORTH;

	static String ICON_BAR_SOUTH = BorderLayout.SOUTH;

	static String NO_ICON_BAR = "NONE";

	static String ICON_BAR_DEFAULT = ICON_BAR_WEST;

	public String iconBarPosition;

	/**
	 * Properties for current language
	 */
	private static final Properties defaultProperties = new Properties();
	{
		/*
		 * Initialization of default properties
		 */
		defaultProperties.setProperty("TEXT FONT NAME", "Courier New");
		defaultProperties.setProperty("TEXT FONT STYLE", "" + Font.PLAIN);
		defaultProperties.setProperty("TEXT FONT SIZE", "10");
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
		defaultProperties.setProperty("RIGHT TO LEFT", "false");
		defaultProperties.setProperty("BACKGROUND COLOR", ""
				+ Color.WHITE.getRGB());
		defaultProperties.setProperty("FOREGROUND COLOR", ""
				+ Color.BLACK.getRGB());
		defaultProperties.setProperty("AUXILIARY NODES COLOR", ""
				+ (new Color(205, 205, 205)).getRGB());//13487565);
		defaultProperties.setProperty("COMMENT NODES COLOR", ""
				+ Color.RED.getRGB());//16711680");
		defaultProperties.setProperty("PACKAGE NODES COLOR", ""
				+ (new Color(220, 220, 0)).getRGB());//16711680");
		defaultProperties.setProperty("CONTEXT NODES COLOR", ""
				+ Color.GREEN.getRGB());
		defaultProperties.setProperty("SELECTED NODES COLOR", ""
				+ Color.BLUE.getRGB());//255");
		defaultProperties.setProperty("ANTIALIASING", "false");
		defaultProperties.setProperty("HTML VIEWER", "");
		defaultProperties.setProperty("MAX TEXT FILE SIZE", "2048000");
		defaultProperties.setProperty("ICON BAR POSITION", "West");
		defaultProperties.setProperty("CHAR BY CHAR", "false");
		defaultProperties.setProperty("PACKAGE PATH", "");
	}

	/**
	 * General preferences 
	 */
	public static Preferences pref = new Preferences();


	/**
	 * Constructs a new <code>Preferences</code>, using 
	 * language configuration values. 
	 *
	 */
	public Preferences() {
		setPreferencesFromProperties(defaultProperties);
	}

	/**
	 * Sets the values to the default ones for the.
	 *
	 */
	public static void reset() {
		Properties prop=loadProperties();
		pref.setPreferencesFromProperties(prop);
	}

	/**
	 * Sets configuration values to those specified by prop
	 * @param prop
	 */
	private void setPreferencesFromProperties(Properties prop) {
		textFontSize = Integer.parseInt(prop.getProperty("TEXT FONT SIZE"));
		textFontStyle = Integer.parseInt(prop.getProperty("TEXT FONT STYLE"));
		textFont = new Font(prop.getProperty("TEXT FONT NAME"), textFontStyle,
				(int) (textFontSize / 0.72));
		inputSize = Integer.parseInt(prop.getProperty("INPUT FONT SIZE"));
		inputFontStyle = Integer.parseInt(prop.getProperty("INPUT FONT STYLE"));
		input = new Font(prop.getProperty("INPUT FONT NAME"), inputFontStyle,
				(int) (inputSize / 0.72));
		outputSize = Integer.parseInt(prop.getProperty("OUTPUT FONT SIZE"));
		outputFontStyle = Integer.parseInt(prop
				.getProperty("OUTPUT FONT STYLE"));
		output = new Font(prop.getProperty("OUTPUT FONT NAME"),
				outputFontStyle, (int) (outputSize / 0.72));
		backgroundColor = new Color(Integer.parseInt(prop
				.getProperty("BACKGROUND COLOR")));
		foregroundColor = new Color(Integer.parseInt(prop
				.getProperty("FOREGROUND COLOR")));
		subgraphColor = new Color(Integer.parseInt(prop
				.getProperty("AUXILIARY NODES COLOR")));
		selectedColor = new Color(Integer.parseInt(prop
				.getProperty("SELECTED NODES COLOR")));
		commentColor = new Color(Integer.parseInt(prop
				.getProperty("COMMENT NODES COLOR")));
		packageColor = new Color(Integer.parseInt(prop
				.getProperty("PACKAGE NODES COLOR")));
		contextColor = new Color(Integer.parseInt(prop
				.getProperty("CONTEXT NODES COLOR")));
		date = Boolean.valueOf(prop.getProperty("DATE")).booleanValue();
		filename = Boolean.valueOf(prop.getProperty("FILE NAME")).booleanValue();
		pathname = Boolean.valueOf(prop.getProperty("PATH NAME")).booleanValue();
		frame = Boolean.valueOf(prop.getProperty("FRAME")).booleanValue();
		rightToLeft = Boolean.valueOf(prop.getProperty("RIGHT TO LEFT")).booleanValue();
		antialiasing = Boolean.valueOf(prop.getProperty("ANTIALIASING")).booleanValue();
		htmlFontName = prop.getProperty("CONCORDANCE FONT NAME");
		htmlFontSize = Integer.parseInt(prop
				.getProperty("CONCORDANCE FONT HTML SIZE"));
		String s = prop.getProperty("HTML VIEWER");
		htmlViewer = (s == null || s.equals("")) ? null : new File(s);
		MAX_TEXT_FILE_SIZE=Integer.parseInt(prop.getProperty("MAX TEXT FILE SIZE"));
		iconBarPosition = prop.getProperty("ICON BAR POSITION");
		charByChar = Boolean.valueOf(prop.getProperty("CHAR BY CHAR")).booleanValue();
		s = prop.getProperty("PACKAGE PATH");
		packagePath = (s == null || s.equals("")) ? null : new File(s);
	}

	private void setPreferences(Preferences p) {
		pref = p;
	}

	/**
	 * Saves <code>Preferences</code> p to the current language
	 * configuration file. 
	 * @param p
	 */
	public static void savePreferences(Preferences p) {
		pref.setPreferences(p);
		Properties prop = pref.setPropertiesFromPreferences();
		saveProperties(prop);
	}

	private Properties setPropertiesFromPreferences() {
		Properties prop = new Properties(defaultProperties);
		prop.setProperty("TEXT FONT NAME", textFont.getName());
		prop.setProperty("TEXT FONT STYLE", ""+textFontStyle);
		prop.setProperty("TEXT FONT SIZE", ""+textFontSize);
		prop.setProperty("CONCORDANCE FONT NAME", htmlFontName);
		prop.setProperty("CONCORDANCE FONT HTML SIZE", ""+htmlFontSize);
		prop.setProperty("INPUT FONT NAME", input.getName());
		prop.setProperty("INPUT FONT STYLE", ""+inputFontStyle);
		prop.setProperty("INPUT FONT SIZE", ""+inputSize);
		prop.setProperty("OUTPUT FONT NAME", output.getName());
		prop.setProperty("OUTPUT FONT STYLE", ""+outputFontStyle);
		prop.setProperty("OUTPUT FONT SIZE", ""+outputSize);
		prop.setProperty("DATE", ""+date);
		prop.setProperty("FILE NAME", ""+filename);
		prop.setProperty("PATH NAME", ""+pathname);
		prop.setProperty("FRAME", ""+frame);
		prop.setProperty("RIGHT TO LEFT", ""+rightToLeft);
		prop.setProperty("BACKGROUND COLOR", ""+backgroundColor.getRGB());
		prop.setProperty("FOREGROUND COLOR", ""+foregroundColor.getRGB());
		prop.setProperty("AUXILIARY NODES COLOR", ""
				+subgraphColor.getRGB());
		prop.setProperty("COMMENT NODES COLOR", ""+commentColor.getRGB());
		prop.setProperty("SELECTED NODES COLOR", ""+selectedColor.getRGB());
		prop.setProperty("PACKAGE NODES COLOR", ""+packageColor.getRGB());
		prop.setProperty("CONTEXT NODES COLOR", ""+contextColor.getRGB());
		prop.setProperty("ANTIALIASING", ""+antialiasing);
		prop.setProperty("HTML VIEWER", (htmlViewer==null)?"":htmlViewer.getAbsolutePath());
		prop.setProperty("MAX TEXT FILE SIZE", ""+MAX_TEXT_FILE_SIZE);
		prop.setProperty("ICON BAR POSITION", iconBarPosition);
		prop.setProperty("CHAR BY CHAR", ""+charByChar);
		prop.setProperty("PACKAGE PATH", (packagePath==null)?"":packagePath.getAbsolutePath());
		return prop;
	}


	/**
	 *  
	 * @return a copy of the preferences
	 */
	public Preferences getClone() {
		Properties prop=setPropertiesFromPreferences(); 
		Preferences p = new Preferences();
		p.setPreferencesFromProperties(prop);
		return p;
	}

	/**
	 * 
	 * @return a copy of global preferences
	 */
	public static Preferences getCloneOfPreferences() {
		return pref.getClone();
	}
	
	/**
	 * 
	 * @return the name of the HTML concordance font
	 */
	public static String getConcordanceFontName() {
		return pref.htmlFontName;
	}

	/**
	 * 
	 * @return the size of the HTML concordance font
	 */
	public static int getConcordanceFontSize() {
		return pref.htmlFontSize;
	}


	/**
	 * Loads user properties for current language 
	 *
	 */
	private static Properties loadProperties() {
		Properties languageProperties = new Properties(defaultProperties);
		FileInputStream stream = null;
		try {
			File f=new File(Config
					.getUserCurrentLanguageDir(), "Config");
			stream = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			return languageProperties;
		}
		try {
			languageProperties.load(stream);
			stream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return languageProperties;
	}

	/**
	 * Loads user properties for current language 
	 *
	 */
	private static void saveProperties(Properties prop) {
		FileOutputStream stream = null;
		try {
			File f=new File(Config
					.getUserCurrentLanguageDir(), "Config");
			stream = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		try {
			prop.store(stream, "Unitex configuration file of '"
					+ Config.getUserName() + "' for '"
					+ Config.getCurrentLanguage() + "'");
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		try {
			stream.close();
		} catch (IOException e2) {
			e2.printStackTrace();
			return;
		}
	}



}