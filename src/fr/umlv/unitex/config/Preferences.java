/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import fr.umlv.unitex.FontInfo;
import fr.umlv.unitex.grf.GraphPresentationInfo;
import fr.umlv.unitex.io.Encoding;


/**
 * This class describes language preferences.
 *
 * @author Sébastien Paumier
 */
public class Preferences {

	/* Base preferences: the preferences the current preferences inherited of. This
	 * is useful to know when we want to save the config file, in order
	 * to dump only values that differs between preferences and base.
	 * 
	 * A null value indicates that we inherits from the default preferences.
	 */
	private Preferences base;
	
    public FontInfo textFont;
    public FontInfo concordanceFont;


    /**
     * Name of the external program to use to view HTML concordances. If <code>null</code>,
     * concordances will be shown in a Unitex frame
     */
    public File htmlViewer;

    /**
     * .bin dictionaries to be used in Locate's morphological mode
     */
    public ArrayList<File> morphologicalDic;

    /**
     * Indicates if the language must be processed char by char or not
     */
    public boolean charByChar;

    /**
     * Indicates if the language must be processed allowing morphological use of spaces.
     */
    public boolean morphologicalUseOfSpace;

    /**
     * Indicates if the language must be read from right to left or not
     */
    public boolean rightToLeftForText;
    public boolean rightToLeftForGraphs;

    /**
     * Indicates if the language is a semitic one, according to
     * the consonant skeleton model
     */
    public boolean semitic;

    /**
     * Path of the graph package repository
     */
    public File graphRepositoryPath;

    //public File lexicalPackagePath;

    /**
     * Path of the logging directory
     */
    public File loggingDir;
    public boolean mustLog = false;

    public boolean svnMonitoring = true;
    public boolean onlyCosmetic = false;

    /**
     * Maximum size in bytes of text files. If a file is bigger than this
     * limit, it won't be loaded.
     */
    public static final int MAX_TEXT_FILE_SIZE = 2 * 1024 * 1024;

    public static String ICON_BAR_WEST = BorderLayout.WEST;
    public static String ICON_BAR_EAST = BorderLayout.EAST;
    public static String ICON_BAR_NORTH = BorderLayout.NORTH;
    public static String ICON_BAR_SOUTH = BorderLayout.SOUTH;
    public static String NO_ICON_BAR = "NONE";
    public static String ICON_BAR_DEFAULT = ICON_BAR_NORTH;

    public GraphPresentationInfo info;
    
    public Encoding encoding;

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
        defaultProperties.setProperty("BACKGROUND COLOR", ""
                + Color.WHITE.getRGB());
        defaultProperties.setProperty("FOREGROUND COLOR", ""
                + Color.BLACK.getRGB());
        defaultProperties.setProperty("AUXILIARY NODES COLOR", ""
                + (new Color(205, 205, 205)).getRGB());//13487565);
        defaultProperties.setProperty("COMMENT NODES COLOR", ""
                + Color.RED.getRGB());//16711680");
        defaultProperties.setProperty("OUTPUT VARIABLE COLOR", ""
                + Color.BLUE.getRGB());
        defaultProperties.setProperty("PACKAGE NODES COLOR", ""
                + (new Color(220, 220, 0)).getRGB());//16711680");
        defaultProperties.setProperty("CONTEXT NODES COLOR", ""
                + Color.GREEN.getRGB());
        defaultProperties.setProperty("MORPHOLOGICAL NODES COLOR", ""
                + new Color(0xC4, 0x4F, 0xD0).getRGB());
        defaultProperties.setProperty("UNREACHABLE GRAPH COLOR", ""
                + Color.RED.getRGB());
        defaultProperties.setProperty("SELECTED NODES COLOR", ""
                + Color.BLUE.getRGB());//255");
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
        defaultProperties.setProperty("ENCODING", Encoding.UTF16LE.toString());
    }


    /**
     * Constructs a new <code>Preferences</code>, using
     * language configuration values.
     */
    public Preferences() {
        setPreferencesFromProperties(defaultProperties);
        base=null;
    }

    /**
     * Sets configuration values to those specified by prop, inheriting the base
     * properties if needed.
     *
     * @param prop
     */
    protected void setPreferencesFromProperties(Properties prop) {
    	Properties baseProperties=getBaseProperties();
    	for (Object key:baseProperties.keySet()) {
    		if (!prop.containsKey(key)) {
    			prop.put(key,baseProperties.get(key));
    		}
    	}
        int size = Integer.parseInt(prop.getProperty("TEXT FONT SIZE"));
        int style = Integer.parseInt(prop.getProperty("TEXT FONT STYLE"));
        Font font = new Font(prop.getProperty("TEXT FONT NAME"), style,
                (int) (size / 0.72));
        textFont = new FontInfo(font, size);
        size = Integer.parseInt(prop
                .getProperty("CONCORDANCE FONT HTML SIZE"));
        font = new Font(prop.getProperty("CONCORDANCE FONT NAME"), Font.PLAIN,
                (int) (size / 0.72));
        concordanceFont = new FontInfo(font, size);
        size = Integer.parseInt(prop.getProperty("INPUT FONT SIZE"));
        style = Integer.parseInt(prop.getProperty("INPUT FONT STYLE"));
        font = new Font(prop.getProperty("INPUT FONT NAME"), style,
                (int) (size / 0.72));
        FontInfo input = new FontInfo(font, size);
        size = Integer.parseInt(prop.getProperty("OUTPUT FONT SIZE"));
        style = Integer.parseInt(prop
                .getProperty("OUTPUT FONT STYLE"));
        font = new Font(prop.getProperty("OUTPUT FONT NAME"),
                style, (int) (size / 0.72));
        FontInfo output = new FontInfo(font, size);
        Color backgroundColor = new Color(Integer.parseInt(prop
                .getProperty("BACKGROUND COLOR")));
        Color foregroundColor = new Color(Integer.parseInt(prop
                .getProperty("FOREGROUND COLOR")));
        Color subgraphColor = new Color(Integer.parseInt(prop
                .getProperty("AUXILIARY NODES COLOR")));
        Color selectedColor = new Color(Integer.parseInt(prop
                .getProperty("SELECTED NODES COLOR")));
        Color commentColor = new Color(Integer.parseInt(prop
                .getProperty("COMMENT NODES COLOR")));
        Color outputVariableColor = new Color(Integer.parseInt(prop
                .getProperty("OUTPUT VARIABLE COLOR")));
        Color packageColor = new Color(Integer.parseInt(prop
                .getProperty("PACKAGE NODES COLOR")));
        Color contextColor = new Color(Integer.parseInt(prop
                .getProperty("CONTEXT NODES COLOR")));
        Color morphologicalModeColor = new Color(Integer.parseInt(prop
                .getProperty("MORPHOLOGICAL NODES COLOR")));
        Color unreachableGraphColor = new Color(Integer.parseInt(prop
                .getProperty("UNREACHABLE GRAPH COLOR")));
        boolean date = Boolean.valueOf(prop.getProperty("DATE"));
        boolean filename = Boolean.valueOf(prop.getProperty("FILE NAME"));
        boolean pathname = Boolean.valueOf(prop.getProperty("PATH NAME"));
        boolean frame = Boolean.valueOf(prop.getProperty("FRAME"));
        boolean antialiasing = Boolean.valueOf(prop.getProperty("ANTIALIASING"));
        String iconBarPosition = prop.getProperty("TOOLBAR POSITION");
        rightToLeftForText = Boolean.valueOf(prop.getProperty("RIGHT TO LEFT FOR TEXT"));
        rightToLeftForGraphs = Boolean.valueOf(prop.getProperty("RIGHT TO LEFT FOR GRAPHS"));
        semitic = Boolean.valueOf(prop.getProperty("SEMITIC"));
        info = new GraphPresentationInfo(backgroundColor, foregroundColor, subgraphColor, selectedColor,
                commentColor, outputVariableColor, packageColor, contextColor, morphologicalModeColor, 
                unreachableGraphColor, input, output,
                date, filename, pathname, frame, rightToLeftForGraphs, antialiasing, iconBarPosition);
        String s = prop.getProperty("HTML VIEWER");
        htmlViewer = (s == null || s.equals("")) ? null : new File(s);
        morphologicalDic = tokenizeMorphologicalDicList(prop.getProperty("MORPHOLOGICAL DICTIONARY"));
        charByChar = Boolean.valueOf(prop.getProperty("CHAR BY CHAR"));
        morphologicalUseOfSpace = Boolean.valueOf(prop.getProperty("MORPHOLOGICAL USE OF SPACE"));
        s = prop.getProperty("PACKAGE PATH");
        graphRepositoryPath = (s == null || s.equals("")) ? null : new File(s);
        /*s = prop.getProperty("LEXICAL PACKAGE PATH");
        lexicalPackagePath = (s == null || s.equals("")) ? null : new File(s);*/
        s = prop.getProperty("LOGGING DIR");
        loggingDir = (s == null || s.equals("")) ? null : new File(s);
        mustLog = Boolean.valueOf(prop.getProperty("MUST LOG"));
        if (mustLog && loggingDir == null) {
            /* Should not happen */
            mustLog = false;
        }
        svnMonitoring = Boolean.valueOf(prop.getProperty("SVN MONITORING"));
        onlyCosmetic = Boolean.valueOf(prop.getProperty("ONLY COSMETIC"));
        try {
        	encoding=Encoding.valueOf(prop.getProperty("ENCODING"));
        } catch (Exception e) {
        	encoding=Encoding.UTF16LE;
        }
    }

    private ArrayList<File> tokenizeMorphologicalDicList(String s) {
        if (s == null || s.equals("")) return null;
        ArrayList<File> list = new ArrayList<File>();
        StringTokenizer tokenizer = new StringTokenizer(s, ";");
        while (tokenizer.hasMoreTokens()) {
            list.add(new File(tokenizer.nextToken()));
        }
        return list;
    }


    protected Properties setPropertiesFromPreferences() {
        Properties prop = new Properties(defaultProperties);
        prop.setProperty("TEXT FONT NAME", textFont.font.getName());
        prop.setProperty("TEXT FONT STYLE", "" + textFont.font.getStyle());
        prop.setProperty("TEXT FONT SIZE", "" + textFont.size);
        prop.setProperty("CONCORDANCE FONT NAME", concordanceFont.font.getName());
        prop.setProperty("CONCORDANCE FONT HTML SIZE", "" + concordanceFont.size);
        prop.setProperty("INPUT FONT NAME", info.input.font.getName());
        prop.setProperty("INPUT FONT STYLE", "" + info.input.font.getStyle());
        prop.setProperty("INPUT FONT SIZE", "" + info.input.size);
        prop.setProperty("OUTPUT FONT NAME", info.output.font.getName());
        prop.setProperty("OUTPUT FONT STYLE", "" + info.output.font.getStyle());
        prop.setProperty("OUTPUT FONT SIZE", "" + info.output.size);
        prop.setProperty("DATE", "" + info.date);
        prop.setProperty("FILE NAME", "" + info.filename);
        prop.setProperty("PATH NAME", "" + info.pathname);
        prop.setProperty("FRAME", "" + info.frame);
        prop.setProperty("RIGHT TO LEFT FOR TEXT", "" + rightToLeftForText);
        prop.setProperty("RIGHT TO LEFT FOR GRAPHS", "" + rightToLeftForGraphs);
        prop.setProperty("SEMITIC", "" + semitic);
        prop.setProperty("BACKGROUND COLOR", "" + info.backgroundColor.getRGB());
        prop.setProperty("FOREGROUND COLOR", "" + info.foregroundColor.getRGB());
        prop.setProperty("AUXILIARY NODES COLOR", ""
                + info.subgraphColor.getRGB());
        prop.setProperty("COMMENT NODES COLOR", "" + info.commentColor.getRGB());
        prop.setProperty("SELECTED NODES COLOR", "" + info.selectedColor.getRGB());
        prop.setProperty("PACKAGE NODES COLOR", "" + info.packageColor.getRGB());
        prop.setProperty("CONTEXT NODES COLOR", "" + info.contextColor.getRGB());
        prop.setProperty("MORPHOLOGICAL NODES COLOR", "" + info.morphologicalModeColor.getRGB());
        prop.setProperty("ANTIALIASING", "" + info.antialiasing);
        prop.setProperty("HTML VIEWER", (htmlViewer == null) ? "" : htmlViewer.getAbsolutePath());
        prop.setProperty("MORPHOLOGICAL DICTIONARY", getMorphologicalDicListAsString(morphologicalDic));
        prop.setProperty("MAX TEXT FILE SIZE", "" + MAX_TEXT_FILE_SIZE);
        prop.setProperty("ICON BAR POSITION", info.iconBarPosition);
        prop.setProperty("CHAR BY CHAR", "" + charByChar);
        prop.setProperty("MORPHOLOGICAL USE OF SPACE", "" + morphologicalUseOfSpace);
        prop.setProperty("PACKAGE PATH", (graphRepositoryPath == null) ? "" : graphRepositoryPath.getAbsolutePath());
        //prop.setProperty("LEXICAL PACKAGE PATH", (lexicalPackagePath == null) ? "" : lexicalPackagePath.getAbsolutePath());
        prop.setProperty("LOGGING DIR", (loggingDir == null) ? "" : loggingDir.getAbsolutePath());
        prop.setProperty("MUST LOG", "" + mustLog);
        prop.setProperty("SVN MONITORING", "" + svnMonitoring);
        prop.setProperty("ONLY COSMETIC", "" + onlyCosmetic);
        prop.setProperty("ENCODING",encoding.toString());
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
    @Override
    public Preferences clone() {
        Properties prop = setPropertiesFromPreferences();
        Preferences p = new Preferences();
        p.setPreferencesFromProperties(prop);
        return p;
    }

    public Properties getBaseProperties() {
    	if (base==null) {
    		return defaultProperties;
    	}
    	return base.setPropertiesFromPreferences();
    }
    
    public void setBase(Preferences p) {
    	this.base=p;
    }

    /**
     * Returns properties that differ from base properties.
     */
	public Properties getOwnProperties() {
		Properties tmp=setPropertiesFromPreferences();
		Properties diff=new Properties();
		Properties base1=getBaseProperties();
		for (Object key:tmp.keySet()) {
			Object value=tmp.get(key);
			if (!base1.containsKey(key) || !base1.get(key).equals(value)) {
				diff.put(key,value);
			}
		}
		return diff;
	}
    
}