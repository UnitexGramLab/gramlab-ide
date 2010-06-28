/*
 * Unitex
 *
 * Copyright (C) 2001-2010 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import fr.umlv.unitex.listeners.FontListener;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;


/**
 * This class describes graph presentation preferences.
 *
 * @author Sébastien Paumier
 */
public class Preferences {

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
    public boolean rightToLeft;

    /**
     * Path of the graph package repository
     */
    public File packagePath;

    /**
     * Path of the logging directory
     */
    public File loggingDir;
    public boolean mustLog = false;

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
    public static String ICON_BAR_DEFAULT = ICON_BAR_WEST;

    public GraphPresentationInfo info;

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
        defaultProperties.setProperty("OUTPUT VARIABLE COLOR", ""
                + Color.BLUE.getRGB());
        defaultProperties.setProperty("PACKAGE NODES COLOR", ""
                + (new Color(220, 220, 0)).getRGB());//16711680");
        defaultProperties.setProperty("CONTEXT NODES COLOR", ""
                + Color.GREEN.getRGB());
        defaultProperties.setProperty("MORPHOLOGICAL NODES COLOR", ""
                + new Color(0xC4, 0x4F, 0xD0).getRGB());
        defaultProperties.setProperty("SELECTED NODES COLOR", ""
                + Color.BLUE.getRGB());//255");
        defaultProperties.setProperty("ANTIALIASING", "false");
        defaultProperties.setProperty("HTML VIEWER", "");
        defaultProperties.setProperty("MORPHOLOGICAL DICTIONARY", "");
        defaultProperties.setProperty("MAX TEXT FILE SIZE", "2048000");
        defaultProperties.setProperty("ICON BAR POSITION", "West");
        defaultProperties.setProperty("CHAR BY CHAR", "false");
        defaultProperties.setProperty("MORPHOLOGICAL USE OF SPACE", "false");
        defaultProperties.setProperty("PACKAGE PATH", "");
        defaultProperties.setProperty("LOGGING DIR", "");
        defaultProperties.setProperty("MUST LOG", "false");
    }

    /**
     * General preferences
     */
    private static Preferences pref = new Preferences();


    /**
     * Constructs a new <code>Preferences</code>, using
     * language configuration values.
     */
    public Preferences() {
        setPreferencesFromProperties(defaultProperties);
    }

    /**
     * Sets the values to the default ones for the.
     */
    public static void reset() {
        Properties prop = loadProperties();
        pref.setPreferencesFromProperties(prop);
    }

    /**
     * Sets configuration values to those specified by prop
     *
     * @param prop
     */
    private void setPreferencesFromProperties(Properties prop) {
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
        boolean date = Boolean.valueOf(prop.getProperty("DATE"));
        boolean filename = Boolean.valueOf(prop.getProperty("FILE NAME"));
        boolean pathname = Boolean.valueOf(prop.getProperty("PATH NAME"));
        boolean frame = Boolean.valueOf(prop.getProperty("FRAME"));
        boolean antialiasing = Boolean.valueOf(prop.getProperty("ANTIALIASING"));
        String iconBarPosition = prop.getProperty("ICON BAR POSITION");
        rightToLeft = Boolean.valueOf(prop.getProperty("RIGHT TO LEFT"));
        info = new GraphPresentationInfo(backgroundColor, foregroundColor, subgraphColor, selectedColor,
                commentColor, outputVariableColor, packageColor, contextColor, morphologicalModeColor, input, output,
                date, filename, pathname, frame, rightToLeft, antialiasing, iconBarPosition);
        String s = prop.getProperty("HTML VIEWER");
        htmlViewer = (s == null || s.equals("")) ? null : new File(s);
        morphologicalDic = tokenizeMorphologicalDicList(prop.getProperty("MORPHOLOGICAL DICTIONARY"));
        charByChar = Boolean.valueOf(prop.getProperty("CHAR BY CHAR"));
        morphologicalUseOfSpace = Boolean.valueOf(prop.getProperty("MORPHOLOGICAL USE OF SPACE"));
        s = prop.getProperty("PACKAGE PATH");
        packagePath = (s == null || s.equals("")) ? null : new File(s);
        s = prop.getProperty("LOGGING DIR");
        loggingDir = (s == null || s.equals("")) ? null : new File(s);
        mustLog = Boolean.valueOf(prop.getProperty("MUST LOG"));
        if (mustLog && loggingDir == null) {
            /* Should not happen */
            mustLog = false;
        }
    }

    public static ArrayList<File> tokenizeMorphologicalDicList(String s) {
        if (s == null || s.equals("")) return null;
        ArrayList<File> list = new ArrayList<File>();
        StringTokenizer tokenizer = new StringTokenizer(s, ";");
        while (tokenizer.hasMoreTokens()) {
            list.add(new File(tokenizer.nextToken()));
        }
        return list;
    }

    private void setPreferences(Preferences p) {
        pref = p;
        fireTextFontChanged(pref.textFont.font);
        fireConcordanceFontChanged(pref.concordanceFont.font);
    }

    /**
     * Saves <code>Preferences</code> p to the current language
     * configuration file.
     *
     * @param p
     */
    public static void savePreferences(Preferences p) {
        pref.setPreferences(p);
        Properties prop = pref.setPropertiesFromPreferences();
        saveProperties(prop);
    }

    private Properties setPropertiesFromPreferences() {
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
        prop.setProperty("RIGHT TO LEFT", "" + rightToLeft);
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
        prop.setProperty("PACKAGE PATH", (packagePath == null) ? "" : packagePath.getAbsolutePath());
        prop.setProperty("LOGGING DIR", (loggingDir == null) ? "" : loggingDir.getAbsolutePath());
        prop.setProperty("MUST LOG", "" + mustLog);
        return prop;
    }


    public static String getMorphologicalDicListAsString(ArrayList<File> list) {
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
    public Preferences getPreferences() {
        Properties prop = setPropertiesFromPreferences();
        Preferences p = new Preferences();
        p.setPreferencesFromProperties(prop);
        return p;
    }

    /**
     * @return a copy of global preferences
     */
    public static Preferences getCloneOfPreferences() {
        return pref.getPreferences();
    }

    /**
     * @return the name of the HTML concordance font
     */
    public static String getConcordanceFontName() {
        return pref.concordanceFont.font.getName();
    }

    /**
     * @return the size of the HTML concordance font
     */
    public static int getConcordanceFontSize() {
        return pref.concordanceFont.size;
    }


    /**
     * Loads user properties.
     */
    public static Properties loadProperties(File config, Properties defaultProperties1) {
        Properties languageProperties = new Properties(defaultProperties1);
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(config);
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
     */
    private static Properties loadProperties() {
        return loadProperties(new File(Config
                .getUserCurrentLanguageDir(), "Config"), defaultProperties);
    }

    /**
     * Saves user properties for current language
     */
    private static void saveProperties(Properties prop) {
        FileOutputStream stream = null;
        try {
            File f = new File(Config
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


    public static GraphPresentationInfo getGraphPresentationPreferences() {
        return pref.info.clone();
    }

    public static boolean charByChar() {
        return pref.charByChar;
    }

    public static boolean morphologicalUseOfSpace() {
        return pref.morphologicalUseOfSpace;
    }

    public static boolean rightToLeft() {
        return pref.rightToLeft;
    }

    public static ArrayList<File> morphologicalDic() {
        return pref.morphologicalDic;
    }

    public static File packagePath() {
        return pref.packagePath;
    }

    public static File loggingDir() {
        return pref.loggingDir;
    }

    public static boolean mustLog() {
        return pref.mustLog;
    }

    public static Font textFont() {
        return pref.textFont.font;
    }

    public static Font inputFont() {
        return pref.info.input.font;
    }

    public static int inputFontSize() {
        return pref.info.input.size;
    }


    private ArrayList<FontListener> textFontListeners = new ArrayList<FontListener>();
    private ArrayList<FontListener> concordanceFontListeners = new ArrayList<FontListener>();

    public static void addTextFontListener(FontListener listener) {
        pref.textFontListeners.add(listener);
    }

    protected boolean firingTextFont = false;

    public static void removeTextFontListener(FontListener listener) {
        if (pref.firingTextFont) {
            throw new IllegalStateException("Should not try to remove a listener while firing");
        }
        pref.textFontListeners.remove(listener);
    }

    protected void fireTextFontChanged(Font font) {
        firingTextFont = true;
        try {
            for (FontListener listener : textFontListeners) {
                listener.fontChanged(font);
            }
        } finally {
            firingTextFont = false;
        }
    }

    public static void addConcordanceFontListener(FontListener listener) {
        pref.concordanceFontListeners.add(listener);
    }

    protected boolean firingConcordanceFont = false;

    public static void removeConcordanceFontListener(FontListener listener) {
        if (pref.firingConcordanceFont) {
            throw new IllegalStateException("Should not try to remove a listener while firing");
        }
        pref.concordanceFontListeners.remove(listener);
    }

    protected void fireConcordanceFontChanged(Font font) {
        firingConcordanceFont = true;
        try {
            for (FontListener listener : concordanceFontListeners) {
				listener.fontChanged(font);
			}
		} finally {
			firingConcordanceFont = false;
		}
	}
	


}