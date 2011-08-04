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

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.files.PersonalFileFilter;
import fr.umlv.unitex.listeners.LanguageListener;

/**
 * This class contains general configuration information. It contains constants
 * used by many other classes.
 *
 * @author Sébastien Paumier
 */
public class Config {
    /**
     * Path of the directory <code>.../Unitex/App</code>
     */
    private static File applicationDir;

    /**
     * Path of the directory <code>.../Unitex</code>
     */
    private static File unitexDir;

    /**
     * Path of the user directory <code>.../(user dir)</code>
     */
    private static File userDir;

    /**
     * Current language. Languages must be in English, with the first letter in
     * uppercase and the others in lowercase. Example: "French"
     */
    private static String currentLanguage;

    /**
     * Path of the user's current language directory
     * <code>.../(user dir)/(current language)</code>
     */
    private static File userCurrentLanguageDir;

    /**
     * Path of the system's current language directory
     * <code>.../Unitex/(current language)</code>
     */
    private static File unitexCurrentLanguageDir;

    /**
     * Path of the user's corpus directory
     * <code>.../(user dir)/(current language)/Corpus</code>
     */
    private static File currentCorpusDir;

    /**
     * Path of the user's graph directory
     * <code>.../(user dir)/(current language)/Graphs</code>
     */
    private static File currentGraphDir;

    /**
     * Path of the user's current corpus
     * <code>.../(user dir)/(current language)/Corpus/(my_corpus.snt)</code>
     */
    private static File currentSnt;

    /**
     * Path of the user's current corpus directory
     * <code>.../(user dir)/(current language)/Corpus/(my_corpus_snt)</code>
     */
    private static File currentSntDir;

    /**
     * Path of the user's ELAG directory
     * <code>.../(user dir)/(current language)/Elag</code>
     */
    private static File currentElagDir;

    /**
     * Path of the user's current language alphabet file
     * <code>.../(user dir)/(current language)/Alphabet.txt</code>
     */
    private static File alphabet;

    /**
     * Path of the user's current cassys path directory
     * <code>.../(user dir)/(current language)/Cassys</code>
     */
    private static File cassysDir;

    /**
     * Path of the current cassys transducer list
     */
    private static File currentTransducerList;

    public final static int WINDOWS_SYSTEM = 0;
    private final static int LINUX_SYSTEM = 1;
    private final static int MAC_OS_X_SYSTEM = 2;
    private final static int SUN_OS_SYSTEM = 3;

    /**
     * Limit over which the concordance should be viewed with a web navigator
     */
    public final static int MAXIMUM_UTTERANCE_NUMBER_TO_DISPLAY_WITH_JAVA = 2000;

    private static String currentSystemName;

    /**
     * Path of the user's current sentence delimitation graph
     * <code>.../(user dir)/(current language)/Graphs/Preprocessing/Sentence/grf</code>
     */
    private static File currentSentenceGraph;

    /**
     * Path of the user's current replace graph
     * <code>.../(user dir)/(current language)/Graphs/Preprocessing/Replace/Replace.grf</code>
     */
    private static File currentReplaceGraph;

    /**
     * Path of the user's current normalization graph
     * <code>.../(user dir)/(current language)/Graphs/Normalisation/Norm.grf</code>
     */
    private static File currentNormGraph;

    /**
     * Constant indicating under which system Unitex is running
     */
    private static int currentSystem = WINDOWS_SYSTEM;

    private static String userName;

    private static File currentDELA;


    /**
     * Message shown when a text file is too large to be loaded.
     */

    public static final String FILE_TOO_LARGE_MESSAGE = "This file is too large to be displayed. Use a wordprocessor to view it.";

    /**
     * Empty file error message.
     */
    public static final String EMPTY_FILE_MESSAGE = "This file is empty.";

    /**
     * File reading error message.
     */
    public static final String ERROR_WHILE_READING_FILE_MESSAGE = "Cannot read this file.";

    /**
     * Dialog box used to open or save graphs
     */
    private static JFileChooser graphDialogBox;
    private static PersonalFileFilter pngFileFilter;
    private static PersonalFileFilter svgFileFilter;
    private static PersonalFileFilter grfFileFilter;

    private static JFileChooser graphDiffDialogBox;

    /**
     * Dialog box used to choose a ".grf" or ".fst2" graph to be used in pattern
     * matching
     */
    private static JFileChooser grfAndFst2DialogBox;

    /**
     * Dialog box used to choose the 'Sentence' graph
     */
    private static JFileChooser sentenceDialogBox;

    /**
     * Dialog box used to choose the 'Replace' graph
     */
    private static JFileChooser replaceDialogBox;

    /**
     * Dialog box used to choose the 'norm' graph
     */
    private static JFileChooser normDialogBox;

    /**
     * Dialog box used to choose the dictionary to be used with the
     * Tagger program
     */
    private static JFileChooser taggerDataDialogBox;

    /**
     * Dialog box used to open ".txt" or ".snt" text files
     */
    private static JFileChooser corpusDialogBox;

    /**
     * Dialog box used to open DELAF dictionaries. The file filter of this
     * dialog box accepts ".dic" files, and also "dlf" and "dlc" files, that are
     * text dictionaries.
     */
    private static JFileChooser delaDialogBox;

    /**
     * Dialog box used to select the directory where the inflection grammars are
     * supposed to be
     */
    private static JFileChooser inflectDialogBox;

    /**
     * Dialog box used to open lexicon-grammars tables
     */
    private static JFileChooser tableDialogBox;

    /**
     * Dialog box used to open files to transcode
     */
    private static JFileChooser transcodeDialogBox;

    /**
     * Dialog box used to open files to edit
     */
    private static JFileChooser fileEditionDialogBox;

    /**
     * Dialog box used to select for derivation directory
     */
    private static JFileChooser variationDialogBox;

    /**
     * Dialog box used to select for derivation directory
     */
    private static JFileChooser derivationDialogBox;

    /**
     * Dialog box used to choose an output text file for
     * Fst2Unambig
     */
    private static JFileChooser fst2UnambigDialogBox;

    /**
     * Dialog box used to choose the transducer list file used
     * with Cassys
     */
    private static JFileChooser transducerListDialogBox;


    /**
     * Initializes the system. This method finds which system is running, which
     * user is working. Then, it initializes dialog boxes and ask the user to
     * choose the initial language he wants to work on. if appPath is not
     * null, it is supposed to represent the directory in which the
     * external programs are located.
     */
    public static void initConfig(String appPath) {
        determineWhichSystemIsRunning();
        determineUnitexDir(appPath);
        determineCurrentUser();
        chooseInitialLanguage(); // TODO set&save a default start-up language in settings/preferences?
        setDefaultPreprocessingGraphs();
    }


    private static void updateGraphFileFilters(boolean allowImageFormats) {
        graphDialogBox.resetChoosableFileFilters();
        if (allowImageFormats) {
            graphDialogBox.addChoosableFileFilter(pngFileFilter);
            graphDialogBox.addChoosableFileFilter(svgFileFilter);
            graphDialogBox.addChoosableFileFilter(grfFileFilter);
        } else {
            graphDialogBox.addChoosableFileFilter(grfFileFilter);
        }
    }

    public static JFileChooser getGraphDialogBox(boolean allowImageFormats) {
        if (graphDialogBox != null) {
            updateGraphFileFilters(allowImageFormats);
            return graphDialogBox;
        }
        graphDialogBox = new JFileChooser();
        pngFileFilter = new PersonalFileFilter("png", "PNG Image");
        svgFileFilter = new PersonalFileFilter("svg", "Scalable Vector Graphics");
        grfFileFilter = new PersonalFileFilter("grf", "Unicode Graphs");
        if (allowImageFormats) {
            graphDialogBox.addChoosableFileFilter(pngFileFilter);
            graphDialogBox.addChoosableFileFilter(svgFileFilter);
        }
        graphDialogBox.addChoosableFileFilter(grfFileFilter);
        graphDialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
        graphDialogBox.setCurrentDirectory(Config.getCurrentGraphDir());
        graphDialogBox.setMultiSelectionEnabled(true);
        return graphDialogBox;
    }

    public static JFileChooser getGraphDiffDialogBox(File base) {
        if (graphDiffDialogBox == null) {
            graphDiffDialogBox = new JFileChooser();
            grfFileFilter = new PersonalFileFilter("grf", "Unicode Graphs");
            graphDiffDialogBox.addChoosableFileFilter(grfFileFilter);
            graphDiffDialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
            graphDiffDialogBox.setMultiSelectionEnabled(false);
        }
        graphDiffDialogBox.setCurrentDirectory(base.getParentFile());
        return graphDiffDialogBox;
    }

    public static JFileChooser getGrfAndFst2DialogBox() {
        if (grfAndFst2DialogBox != null)
            return grfAndFst2DialogBox;
        grfAndFst2DialogBox = new JFileChooser();
        grfAndFst2DialogBox.addChoosableFileFilter(new PersonalFileFilter(
                "fst2", "Unicode Compiled Graphs"));
        grfAndFst2DialogBox.addChoosableFileFilter(new PersonalFileFilter(
                "grf", "Unicode Graphs"));
        grfAndFst2DialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
        grfAndFst2DialogBox.setCurrentDirectory(Config.getCurrentGraphDir());
        grfAndFst2DialogBox.setMultiSelectionEnabled(false);
        return grfAndFst2DialogBox;
    }

    public static JFileChooser getSentenceDialogBox() {
        if (sentenceDialogBox != null)
            return sentenceDialogBox;
        sentenceDialogBox = new JFileChooser();
        sentenceDialogBox.addChoosableFileFilter(new PersonalFileFilter(
                "fst2", "Unicode Compiled Graphs"));
        sentenceDialogBox.addChoosableFileFilter(new PersonalFileFilter(
                "grf", "Unicode Graphs"));
        sentenceDialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
        File f = new File(Config.getCurrentGraphDir(), "Preprocessing");
        f = new File(f, "Sentence");
        sentenceDialogBox.setCurrentDirectory(f);
        sentenceDialogBox.setMultiSelectionEnabled(false);
        return sentenceDialogBox;
    }

    public static JFileChooser getReplaceDialogBox() {
        if (replaceDialogBox != null)
            return replaceDialogBox;
        replaceDialogBox = new JFileChooser();
        replaceDialogBox.addChoosableFileFilter(new PersonalFileFilter(
                "fst2", "Unicode Compiled Graphs"));
        replaceDialogBox.addChoosableFileFilter(new PersonalFileFilter(
                "grf", "Unicode Graphs"));
        replaceDialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
        File currentGraphDir_ = new File(Config.getCurrentGraphDir(), "Preprocessing");
        currentGraphDir_ = new File(currentGraphDir_, "Replace");
        replaceDialogBox.setCurrentDirectory(currentGraphDir);
        replaceDialogBox.setMultiSelectionEnabled(false);
        return replaceDialogBox;
    }

    public static JFileChooser getNormDialogBox() {
        if (normDialogBox != null)
            return normDialogBox;
        normDialogBox = new JFileChooser();
        normDialogBox.addChoosableFileFilter(new PersonalFileFilter(
                "fst2", "Unicode Compiled Graphs"));
        normDialogBox.addChoosableFileFilter(new PersonalFileFilter(
                "grf", "Unicode Graphs"));
        normDialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
        File f = new File(Config.getCurrentGraphDir(), "Normalization");
        normDialogBox.setCurrentDirectory(f);
        normDialogBox.setMultiSelectionEnabled(false);
        return normDialogBox;
    }

    public static JFileChooser getTaggerDataDialogBox() {
        if (taggerDataDialogBox != null)
            return taggerDataDialogBox;
        taggerDataDialogBox = new JFileChooser();
        taggerDataDialogBox.addChoosableFileFilter(new PersonalFileFilter(
                "fst2", "Unicode Compiled Graphs"));
        taggerDataDialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
        taggerDataDialogBox.setCurrentDirectory(new File(Config
                .getUserCurrentLanguageDir(), "Dela"));
        taggerDataDialogBox.setMultiSelectionEnabled(false);
        return taggerDataDialogBox;
    }


    public static JFileChooser getDelaDialogBox() {
        if (delaDialogBox != null)
            return delaDialogBox;
        delaDialogBox = new JFileChooser();
        delaDialogBox.addChoosableFileFilter(new PersonalFileFilter("dic",
                "Unicode DELA Dictionaries"));
        delaDialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
        delaDialogBox.setCurrentDirectory(new File(Config
                .getUserCurrentLanguageDir(), "Dela"));
        delaDialogBox.setMultiSelectionEnabled(false);
        return delaDialogBox;
    }

    public static JFileChooser getCorpusDialogBox() {
        if (corpusDialogBox != null) {
            return corpusDialogBox;
        }
        corpusDialogBox = new JFileChooser();
        corpusDialogBox.addChoosableFileFilter(new PersonalFileFilter("txt",
                "Raw Unicode Texts"));
        corpusDialogBox.addChoosableFileFilter(new PersonalFileFilter("xml",
        "XML files"));
        corpusDialogBox.addChoosableFileFilter(new PersonalFileFilter("html",
        "HTML files"));
        corpusDialogBox.addChoosableFileFilter(new PersonalFileFilter("snt",
                "Unitex Texts"));
        corpusDialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
        corpusDialogBox.setCurrentDirectory(Config.getCurrentCorpusDir());
        corpusDialogBox.setMultiSelectionEnabled(false);
        return corpusDialogBox;
    }

    public static JFileChooser getTaggedCorpusDialogBox() {
        if (corpusDialogBox != null) {
            return corpusDialogBox;
        }
        corpusDialogBox = new JFileChooser();
        corpusDialogBox.addChoosableFileFilter(new PersonalFileFilter("snt",
                "Unitex Texts"));
        corpusDialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
        corpusDialogBox.setCurrentDirectory(Config.getCurrentCorpusDir());
        corpusDialogBox.setMultiSelectionEnabled(false);
        return corpusDialogBox;
    }

    public static JFileChooser getInflectDialogBox() {
        if (inflectDialogBox != null)
            return inflectDialogBox;
        inflectDialogBox = new JFileChooser();
        inflectDialogBox.setDialogTitle("Choose the inflection directory");
        inflectDialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
        inflectDialogBox.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        inflectDialogBox.setCurrentDirectory(new File(Config
                .getUserCurrentLanguageDir(), "Inflection"));
        return inflectDialogBox;
    }

    public static JFileChooser getVarDialogBox() {
        if (variationDialogBox != null)
            return variationDialogBox;
        variationDialogBox = new JFileChooser();
        variationDialogBox.setDialogTitle("Choose the variation directory");
        variationDialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
        variationDialogBox.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        variationDialogBox.setCurrentDirectory(new File(Config
                .getUserCurrentLanguageDir(), "Variation"));
        return variationDialogBox;
    }

    public static JFileChooser getDevDialogBox() {
        if (derivationDialogBox != null)
            return derivationDialogBox;
        derivationDialogBox = new JFileChooser();
        derivationDialogBox.setDialogTitle("Choose the derivation directory");
        derivationDialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
        derivationDialogBox.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        derivationDialogBox.setCurrentDirectory(new File(Config
                .getUserCurrentLanguageDir(), "Derivation"));
        return derivationDialogBox;
    }

    public static JFileChooser getTableDialogBox() {
        if (tableDialogBox != null)
            return tableDialogBox;
        tableDialogBox = new JFileChooser();
        tableDialogBox.setFileFilter(new PersonalFileFilter("txt",
                "Unicode Text Tables"));
        tableDialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
        tableDialogBox.setCurrentDirectory(Config.getUserCurrentLanguageDir());
        return tableDialogBox;
    }

    public static JFileChooser getTranscodeDialogBox() {
        if (transcodeDialogBox != null)
            return transcodeDialogBox;
        transcodeDialogBox = new JFileChooser();
        transcodeDialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
        transcodeDialogBox.setMultiSelectionEnabled(true);
        transcodeDialogBox.setDialogTitle("Select files to transcode");
        transcodeDialogBox.setCurrentDirectory(Config.getCurrentCorpusDir());
        return transcodeDialogBox;
    }

    public static JFileChooser getFileEditionDialogBox() {
        if (fileEditionDialogBox != null)
            return fileEditionDialogBox;
        fileEditionDialogBox = new JFileChooser();
        fileEditionDialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
        fileEditionDialogBox.setMultiSelectionEnabled(false);
        fileEditionDialogBox.setCurrentDirectory(Config.getCurrentCorpusDir());
        fileEditionDialogBox.setDialogTitle("Select file to edit");
        return fileEditionDialogBox;
    }

    public static JFileChooser getFst2UnambigDialogBox() {
        if (fst2UnambigDialogBox != null)
            return fst2UnambigDialogBox;
        fst2UnambigDialogBox = new JFileChooser();
        fst2UnambigDialogBox.setFileFilter(new PersonalFileFilter("txt",
                "Unicode Text File"));
        fst2UnambigDialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
        fst2UnambigDialogBox.setCurrentDirectory(Config.getCurrentCorpusDir());
        fst2UnambigDialogBox.setMultiSelectionEnabled(false);
        return fst2UnambigDialogBox;
    }


    public static JFileChooser getTransducerListDialogBox() {
		if(transducerListDialogBox != null){
			return transducerListDialogBox;
		}
		transducerListDialogBox = new JFileChooser(Config.getCassysDir());
		
		transducerListDialogBox.setFileFilter(new PersonalFileFilter("csc",
         "CaSCade configuration File"));
		//transducerListDialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
		//transducerListDialogBox.setCurrentDirectory(Config.getCassysDir());
       
		transducerListDialogBox.setMultiSelectionEnabled(false);
		transducerListDialogBox.setControlButtonsAreShown(false);
		return transducerListDialogBox;
	}


    /**
     * Updates working directories of dialog boxes. This method is called when
     * the user changes of working language.
     */
    private static void updateOpenSaveDialogBoxes() {
        if (graphDialogBox != null)
            graphDialogBox.setCurrentDirectory(Config.getCurrentGraphDir());
        if (grfAndFst2DialogBox != null)
            grfAndFst2DialogBox
                    .setCurrentDirectory(Config.getCurrentGraphDir());
        if (sentenceDialogBox != null)
            sentenceDialogBox
                    .setCurrentDirectory(new File(new File(Config.getCurrentGraphDir(), "Preprocessing"), "Sentence"));
        if (replaceDialogBox != null)
            replaceDialogBox
                    .setCurrentDirectory(new File(new File(Config.getCurrentGraphDir(), "Preprocessing"), "Replace"));
        if (corpusDialogBox != null)
            corpusDialogBox.setCurrentDirectory(Config.getCurrentCorpusDir());
        if (delaDialogBox != null)
            delaDialogBox.setCurrentDirectory(new File(Config
                    .getUserCurrentLanguageDir(), "Dela"));
        if (inflectDialogBox != null)
            inflectDialogBox.setCurrentDirectory(new File(Config
                    .getUserCurrentLanguageDir(), "Inflection"));
        if (tableDialogBox != null)
            tableDialogBox.setCurrentDirectory(Config
                    .getUserCurrentLanguageDir());
        if (transcodeDialogBox != null)
            transcodeDialogBox
                    .setCurrentDirectory(Config.getCurrentCorpusDir());
        if (fileEditionDialogBox != null)
            fileEditionDialogBox.setCurrentDirectory(Config
                    .getCurrentCorpusDir());
        if (fst2UnambigDialogBox != null)
            fst2UnambigDialogBox.setCurrentDirectory(Config
                    .getCurrentCorpusDir());
        if (taggerDataDialogBox != null)
            taggerDataDialogBox.setCurrentDirectory(new File(Config
                    .getUserCurrentLanguageDir(), "Dela"));

    }

    /**
     * Finds the path of the Unitex directory. Sets up the application
     * directory. If appPath is not null, it represents the external
     * programs directory.
     */
    private static void determineUnitexDir(String appPath) {
        setApplicationDir(appPath, new File(System.getProperty("user.dir")));
    }

    /**
     * Finds the user's name. If the user works with Unitex for the first time,
     * he is asked for choosing his private working directory if he works under
     * Windows. Under Linux or MacOS, his private directory is a directory named
     * "unitex", created in his home directory.
     */
    private static void determineCurrentUser() {
        userName = System.getProperty("user.name");
        if (currentSystem == WINDOWS_SYSTEM) {
            // configuration procedure under Windows
            File directory = new File(getUnitexDir(), "Users");
            if (!directory.exists()) {
                directory.mkdir();
            }
            final File userFile = new File(new File(getUnitexDir(), "Users"), userName
                    + ".cfg");
            if (!userFile.exists()) {
                try {
                    chooseNewUserDir();
                    userFile.createNewFile();
                    BufferedWriter bw = new BufferedWriter(new FileWriter(
                            userFile));
                    bw.write(getUserDir().getAbsolutePath(), 0, getUserDir()
                            .getAbsolutePath().length());
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(
                            userFile));
                    String s = br.readLine();
                    if (s == null || s.equals("")) {
                        System.out.println("Error: " + userFile + " is empty!");
                        s = null;
                    }
                    setUserDir(new File(s));
                    br.close();
                } catch (IOException e) {
                    System.out.println("Error: " + userFile + " is empty!");
                    e.printStackTrace();
                }
            }
            return;
        }
        if ((currentSystem == LINUX_SYSTEM)
                || (currentSystem == MAC_OS_X_SYSTEM)
                || (currentSystem == SUN_OS_SYSTEM)) {
            /* The default user directory is /home/user/unitex */
            File rep = new File(System.getProperty("user.home"), "unitex");
            final File config = new File(System.getProperty("user.home"), ".unitex.cfg");
            if (config.exists()) {
                FileInputStream s;
                try {
                    s = new FileInputStream(config);
                    Scanner scanner = new Scanner(s, "UTF8");
                    if (scanner.hasNextLine()) {
                        rep = new File(scanner.nextLine());
                    }
                    scanner.close();
                    s.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!rep.exists()) {
                // if the directory does not exist, we inform the user
                String message = "Welcome " + getUserName() + "!\n\n";
                message = message
                        + "Your private Unitex directory where you can\nstore your own data is:\n\n";
                message = message + rep + "\n";
                JOptionPane.showMessageDialog(null, message, "Welcome",
                        JOptionPane.PLAIN_MESSAGE);
            }
            setUserDir(rep);
            return;
        }
        throw new IllegalStateException("Unitex is not configured for this system!");
    }

    /**
     * Finds which operating system is used. If the system is not supported by
     * Unitex, the program stops.
     */
    private static void determineWhichSystemIsRunning() {
        currentSystemName = System.getProperty("os.name");
        if (currentSystemName.equalsIgnoreCase("Windows NT")
                || currentSystemName.equalsIgnoreCase("Windows 2003")
                || currentSystemName.equalsIgnoreCase("Windows 2000")
                || currentSystemName.equalsIgnoreCase("Windows 98")
                || currentSystemName.equalsIgnoreCase("Windows 95")
                || currentSystemName.equalsIgnoreCase("Windows XP")
                || currentSystemName.equalsIgnoreCase("Windows ME")
                || currentSystemName.equalsIgnoreCase("Windows Server 2008")
                || currentSystemName.equalsIgnoreCase("Windows Vista")
                || currentSystemName.equalsIgnoreCase("Windows 7")
                || currentSystemName.startsWith("Windows ")) {
            currentSystem = WINDOWS_SYSTEM;
        } else if (currentSystemName.equalsIgnoreCase("linux")) {
            currentSystem = LINUX_SYSTEM;
        } else if (currentSystemName.equalsIgnoreCase("mac os x") ||
                currentSystemName.equalsIgnoreCase("Darwin")) {
            currentSystem = MAC_OS_X_SYSTEM;
        } else if (currentSystemName.equalsIgnoreCase("sunos")) {
            currentSystem = SUN_OS_SYSTEM;
        } else {
        	/* By default, we assume that a have a linux compatible system */
        	currentSystem = LINUX_SYSTEM;
        }
        System.out.println("Unitex is running under " + currentSystemName);
    }

    /**
     * @return the current system constant
     */
    public static int getCurrentSystem() {
        return currentSystem;
    }

    /**
     * @return the current system name
     */
    public static String getCurrentSystemName() {
        return currentSystemName;
    }

    /**
     * @return the application directory
     */
    public static File getApplicationDir() {
        if (applicationDir == null) {
            System.err.println("ERROR");
        }
        return applicationDir;
    }

    /**
     * Sets the application directory
     *
     * @param appPath if not null, it represents the external programs directory
     * @param s       directory's path
     */
    private static void setApplicationDir(String appPath, File s) {
        if (appPath != null) {
            applicationDir = new File(appPath);
        } else {
            applicationDir = s;
        }
        setUnitexDir(applicationDir.getParentFile());
    }

    /**
     * @return the Unitex directory
     */
    public static File getUnitexDir() {
        if (unitexDir == null) {
            System.err.println("ERROR");
        }
        return unitexDir;
    }

    /**
     * Sets the system directory
     *
     * @param s directory's path
     */
    private static void setUnitexDir(File s) {
        unitexDir = s;
    }

    /**
     * @return the user directory
     */
    public static File getUserDir() {
        if (userDir == null) {
            System.err.println("ERROR");
        }
        return userDir;
    }

    /**
     * Sets the user directory
     *
     * @param s directory's path
     */
    private static void setUserDir(File s) {
        userDir = s;
        if (!userDir.exists()) {
            // if the directory does not exists, we create it
            if (!userDir.mkdir()) {
                System.err.println("Fatal error: cannot create directory "
                        + userDir);
                System.exit(1);
            }
        }
    }

    /**
     * @return the current language
     */
    public static String getCurrentLanguage() {
        if (currentLanguage == null) {
            System.err.println("ERROR");
        }
        return currentLanguage;
    }

    /**
     * Sets the current language. All directories that depend of this one are
     * updated. Preferences all also updated
     *
     * @param s language's name
     */
    private static void setCurrentLanguage(String s) {
        currentLanguage = s;
        setUnitexCurrentLanguageDir(new File(getUnitexDir(), currentLanguage));
        setUserCurrentLanguageDir(new File(getUserDir(), currentLanguage));
        setCurrentCorpusDir(new File(getUserCurrentLanguageDir(), "Corpus"));
        setCurrentGraphDir(new File(getUserCurrentLanguageDir(), "Graphs"));
        setCurrentElagDir(new File(getUserCurrentLanguageDir(), "Elag"));
        setAlphabet(new File(getUserCurrentLanguageDir(), "Alphabet.txt"));
        setCassysDir(new File(getUserCurrentLanguageDir(), "Cassys"));
        setDefaultPreprocessingGraphs();
        updateOpenSaveDialogBoxes();
        fireLanguageChanged();
    }


    public static File getCurrentTransducerList() {
        return currentTransducerList;
    }


    public static void setCurrentTransducerList(File f) {
        currentTransducerList = f;
    }

    public static File getCassysDir() {
        if (cassysDir == null) {
            System.out.println("ERROR : Cassys dir not Found");
        }
        return cassysDir;
    }


    private static void setCassysDir(File c) {
        cassysDir = c;
    }


    private static final String[] bastien = new String[]{
            "Greek (Ancient)",
            "grec momifié",
            "grec putréfié",
            "grec embaumé",
            "grec faisandé",
            "grec mort",
            "vieux grec tout rabougri",
            "grec qui sent la naphtaline",
            "grec périmé",
            "grec cadavérique",
            "grec de morgue",
            "grec tellement vieux qu'on frôle la profanation",
            "grec médico-légal",
            "grec grouillant d'insectes nécrophages",
            "grec en décomposition",
            "grec moisi (les fameux champignons à la grecque)",
            "grec qui ferait vomir un marchand de kébabs de rat",
            "grec pourri",
            "grec desséché"
    };
    private static final String[] jeesun = new String[]{
            "\uC9C0\uC21C\uC744 \uC704\uD55C \uD55C\uAD6D\uC5B4",
            "2000\uB144 \uB3D9\uC548\uC758 \uD55C\uAD6D\uC5B4",
            "\uC0C8\uBCBD 4\uC2DC\uC758 \uD55C\uAD6D\uC5B4",
            "\uAC74\uBC30\uB77C\uB294 \uB2E8\uC5B4\uB9CC\uC744 \uD560 \uC904 \uC544\uB294 \uC0AC\uB78C\uC758 \uD55C\uAD6D\uC5B4",
            "\uD55C\uAD6D\uC5B4\uC640 \uC220",
            "coréen rien que pour toi toute seule"
    };

    /**
     * @return the current language to be displayed in the title bar
     */
    public static String getCurrentLanguageForTitleBar() {
        if (currentLanguage == null) {
            System.err.println("ERROR");
            return null;
        }
        /* The following is a private joke */
        if (currentLanguage.equals("Greek (Ancient)")
                && (getUserName().equalsIgnoreCase("bastien") || getUserName().equalsIgnoreCase("nastasia"))) {
            return bastien[new Random().nextInt(bastien.length)];
        }
        /* This one too */
        if (currentLanguage.equals("KoreanJeeSun")) {
            return jeesun[new Random().nextInt(jeesun.length)];
        }
        if (getUserName().hashCode() == 549477927) {
            return "\u2665\u2665\u2665 " + currentLanguage + " \u2665\u2665\u2665";
        }
        return currentLanguage;
    }


    /**
     * @return system's current language directory
     */
    public static File getUnitexCurrentLanguageDir() {
        if (unitexCurrentLanguageDir == null) {
            System.err.println("ERROR");
        }
        return unitexCurrentLanguageDir;
    }

    /**
     * Sets the system's current language directory
     *
     * @param s directory's path
     */
    private static void setUnitexCurrentLanguageDir(File s) {
        unitexCurrentLanguageDir = s;
    }

    /**
     * @return the users' current language directory
     */
    public static File getUserCurrentLanguageDir() {
        if (userCurrentLanguageDir == null) {
            System.err.println("ERROR");
        }
        return userCurrentLanguageDir;
    }

    /**
     * Sets the user's current language directory. If some sub-directories are
     * missing, they are copied from the system's current language directory
     * with all their files, excepting the dictionaries (".bin" and ".inf"
     * files).
     *
     * @param s directory's path
     */
    private static void setUserCurrentLanguageDir(File s) {
        userCurrentLanguageDir = s;
        // now, we verify if the directory exists
        if (!userCurrentLanguageDir.exists()) {
            // if the path does not exists
            if (!userCurrentLanguageDir.mkdir()) {
                System.err.println("ERROR: cannot create directory "
                        + s.getAbsolutePath());
                System.exit(1);
            }
            FileUtil.copyFileByName(new File(getUnitexCurrentLanguageDir(), "*"),
                    getUserCurrentLanguageDir());
        }
        if (deprecatedConfigFile(new File(s, "Config"))) {
        	FileUtil.copyFileByName(new File(getUnitexCurrentLanguageDir(), "Config"),
                    getUserCurrentLanguageDir());
        }
        // if any of the sub-directories does not exist, we create it
        File f = new File(userCurrentLanguageDir, "Corpus");
        if (!f.exists()) {
            f.mkdir();
            FileUtil.copyDirRec(new File(getUnitexCurrentLanguageDir(), "Corpus"), f);
        }
        f = new File(userCurrentLanguageDir, "Elag");
        if (!f.exists()) {
        	FileUtil.copyDirRec(new File(getUnitexCurrentLanguageDir(), "Elag"), f);
        }

        f = new File(userCurrentLanguageDir, "Inflection");
        if (!f.exists()) {
        	FileUtil.copyDirRec(new File(getUnitexCurrentLanguageDir(), "Inflection"), f);
        }
        f = new File(userCurrentLanguageDir, "Dela");
        if (!f.exists()) {
            f.mkdir();
        }
        f = new File(userCurrentLanguageDir, "Graphs");
        if (!f.exists()) {
        	FileUtil.copyDirRec(new File(getUnitexCurrentLanguageDir(), "Graphs"), f);
        }
        f = new File(userCurrentLanguageDir, "Cassys");
        if (!f.exists()) {
        	FileUtil.copyDirRec(new File(getUnitexCurrentLanguageDir(), "Cassys"), f);
        }

    }

    /**
     * @param file
     * @return true if the configuration file does not start with '#'
     */
    private static boolean deprecatedConfigFile(File file) {
        if (file == null) {
            return false;
        }
        try {
            FileInputStream stream = new FileInputStream(file);
            if (stream.available() == 0) {
                stream.close();
                return false;
            }
            int c = stream.read();
            stream.close();
            return (c != '#');

        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e1) {
            return false;
        }
    }

    /**
     * @return user's current corpus directory
     */
    public static File getCurrentCorpusDir() {
        if (currentCorpusDir == null) {
            System.err.println("ERROR: Corpus directory is not set.");
        }
        return currentCorpusDir;
    }

    /**
     * Sets the user's current corpus directory
     *
     * @param s directory's path
     */
    private static void setCurrentCorpusDir(File s) {
        currentCorpusDir = s;
    }

    /**
     * @return user's current graph directory
     */
    public static File getCurrentGraphDir() {
        if (currentGraphDir == null) {
            System.err.println("ERROR: Graph directory is not set.");
        }
        return currentGraphDir;
    }

    /**
     * Sets the user's current graph directory
     *
     * @param s directory's path
     */
    public static void setCurrentGraphDir(File s) {
        currentGraphDir = s;
    }

    /**
     * @return user's current ELAG directory
     */
    public static File getCurrentElagDir() {
        if (currentElagDir == null) {
            System.out.println("ERROR: Elag directory is not set.");
        }
        return currentElagDir;
    }

    /**
     * Sets the user's current ELAG directory
     *
     * @param s directory's path
     */
    private static void setCurrentElagDir(File s) {
        currentElagDir = s;
    }

    /**
     * Sets user's current alphabet file
     *
     * @param s file's path
     */
    private static void setAlphabet(File s) {
        alphabet = s;
    }

    /**
     * @return user's name
     */
    public static String getUserName() {
        return userName;
    }

    /**
     * Asks for the user to select his private directory. IMPORTANT: this method
     * must be called only when Unitex is running under Windows.
     */
    private static void chooseNewUserDir() {
        String message = "Welcome " + getUserName() + "!\n\n";
        message = message
                + "To use Unitex, you must choose a private \ndirectory to store your data ";
        message = message + "(that you\ncan change later if you want).";
        message = message + "\n\nClick on OK to choose your directory.";
        JOptionPane.showMessageDialog(null, message, "Welcome",
                JOptionPane.PLAIN_MESSAGE);
        JFileChooser f = new JFileChooser();
        f.setDialogTitle("Choose your private directory");
        f.setDialogType(JFileChooser.OPEN_DIALOG);
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        while (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION || /*
																		 * areIdenticalDirectories(f.getSelectedFile()
																		 * .getAbsolutePath(),
																		 * getUnitexDir())
																		 */
                f.getSelectedFile().equals(getUnitexDir())) {
            if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                message = "You must choose a private directory.\n\n";
                message = message + "Click on OK to select one or on\n";
                message = message + "Cancel to exit.";
            } else {
                message = "You cannot choose the Unitex directory as your private one";
            }
            final String[] options = {"OK", "Cancel"};
            int n = JOptionPane.showOptionDialog(null, message, "Error",
                    JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null,
                    options, options[0]);
            if (n == 1)
                System.exit(0);
        }
        setUserDir(f.getSelectedFile());
    }

    /**
     * Asks for the user to change his private directory. IMPORTANT: this method
     * must be called only when Unitex is running under Windows.
     */
    public static void changeUserDir() {
        final JFileChooser f = new JFileChooser();
        f.setDialogTitle("Choose your private directory");
        f.setDialogType(JFileChooser.OPEN_DIALOG);
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
            return;
        if (f.getSelectedFile().equals(getUnitexDir())) {
            JOptionPane
                    .showMessageDialog(
                            null,
                            "You cannot choose the Unitex directory as your private one",
                            "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        setUserDir(f.getSelectedFile());
    }

    private static void collectLanguage(File directory, Set<String> languages) {
        final File[] fileList = directory.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory() && ConfigManager.getManager().isValidLanguageName(file.getName());
            }
        });
        for (File aFileList : fileList) {
            languages.add(aFileList.getName());
        }
    }

    /**
     * Shows a dialog box that offers to the user to choose the initial language
     * he wants to work on
     */
    private static void chooseInitialLanguage() {
        final JPanel p = new JPanel();
        p.setLayout(new GridLayout(4, 1));
        p.setOpaque(true);
        TreeSet<String> languages = new TreeSet<String>();
        collectLanguage(getUnitexDir(), languages);
        collectLanguage(getUserDir(), languages);
        JComboBox langList = new JComboBox(languages.toArray());
        p.add(new JLabel("User: " + getUserName()));
        p.add(new JLabel("Choose the language you want"));
        p.add(new JLabel("to work on:"));
        p.add(langList);
        final String[] options = {"OK", "Exit"};
        if (1 == JOptionPane.showOptionDialog(null, p, "Unitex",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                options, options[0])) {
            System.exit(0);
        }
        setCurrentLanguage((String) (langList.getSelectedItem()));
    }

    /**
     * Shows a dialog box that offers to the user to change the language he
     * works on. If they are some frames that depend on the language (corpus
     * frame, token frame, etc), they are all closed before changing the working
     * language.
     */
    public static void changeLanguage() {
        final TreeSet<String> languages = new TreeSet<String>();
        collectLanguage(getUnitexDir(), languages);
        collectLanguage(getUserDir(), languages);
        JComboBox langList = new JComboBox(languages.toArray());
        String old = getCurrentLanguage();
        final String[] options = {"OK", "Cancel"};
        if (0 == JOptionPane.showOptionDialog(null, langList,
                "Choose a language", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0])) {
            String newLanguage = (String) (langList.getSelectedItem());
            if (!old.equals(newLanguage)) {
                setCurrentLanguage(newLanguage);
            }
        }
    }

    /**
     * @return current corpus
     */
    public static File getCurrentSnt() {
        return currentSnt;
    }

    /**
     * Sets the current corpus
     *
     * @param s name of the corpus file
     */
    public static void setCurrentSnt(File s) {
        currentSnt = s;
        setCurrentSntDir(s);
    }

    /**
     * @return current corpus
     */
    public static File getCurrentSntDir() {
        return currentSntDir;
    }

    /**
     * Sets the current corpus
     *
     * @param s name of the corpus file
     */
    private static void setCurrentSntDir(File s) {
        final String path = FileUtil.getFileNameWithoutExtension(s.getAbsolutePath());
        currentSntDir = new File(path + "_snt");
        if (currentSntDir.exists() && !currentSntDir.isDirectory()) {
            JOptionPane.showMessageDialog(null,
                    currentSntDir.getAbsolutePath() + " is not a directory but a file.\n" +
                            "\nUnitex will abort.",
                    "Fatal error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    /**
     * @return current sentence delimitation graph
     */
    public static File getCurrentSentenceGraph() {
        return currentSentenceGraph;
    }

    /**
     * Sets current sentence delimitation graph
     *
     * @param s graph's name
     */
    private static void setCurrentSentenceGraph(File s) {
        currentSentenceGraph = s;
    }

    /**
     * @return current replace graph
     */
    public static File getCurrentReplaceGraph() {
        return currentReplaceGraph;
    }

    /**
     * Sets current replace graph
     *
     * @param s graph's name
     */
    private static void setCurrentReplaceGraph(File s) {
        currentReplaceGraph = s;
    }

    /**
     * @return current sentence normalization graph
     */
    public static File getCurrentNormGraph() {
        return currentNormGraph;
    }

    /**
     * Sets current sentence normalization graph
     *
     * @param s graph's name
     */
    public static void setCurrentNormGraph(File s) {
        currentNormGraph = s;
    }

    /**
     * Sets sentence delimitation and replace graphs
     */
    private static void setDefaultPreprocessingGraphs() {
        File sentence = new File(getUserCurrentLanguageDir(), "Graphs");
        sentence = new File(sentence, "Preprocessing");
        File replace = new File(sentence, "Replace");
        sentence = new File(sentence, "Sentence");
        if (getCurrentLanguage().equals("Thai")) {
            // this is an exception because we do not have anymore
            // the .grf file
            sentence = new File(sentence, "Sentence.fst2");
        } else {
            sentence = new File(sentence, "Sentence.grf");
        }
        replace = new File(replace, "Replace.grf");
        setCurrentSentenceGraph(sentence);
        setCurrentReplaceGraph(replace);
        File z = new File(getUserCurrentLanguageDir(), "Graphs");
        z = new File(z, "Normalization");
        z = new File(z, "Norm.grf");
        setCurrentNormGraph(z);
    }

    /**
     * Sets current DELA
     *
     * @param s dictionary name
     */
    public static void setCurrentDELA(File s) {
        currentDELA = s;
    }

    /**
     * @return current DELA's name
     */
    public static File getCurrentDELA() {
        return currentDELA;
    }

    public static File getXAlignDirectory() {
        final File dir = new File(Config.getUserDir(), "XAlign");
        if (!dir.exists()) {
            final File foo = new File(Config.getUnitexDir(), "XAlign");
            if (!foo.exists()) {
                JOptionPane
                        .showMessageDialog(
                                null,
                                "Cannot find directory " + foo.getAbsolutePath(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            FileUtil.copyDirRec(foo, dir);
        }
        return dir;
    }


    public static File getAlignmentProperties() {
        final File dir = getXAlignDirectory();
        final File f = new File(dir, "multialign.properties");
        if (!f.exists()) {
            File tmp = new File(new File(Config.getUnitexDir(), "XAlign"), "multialign.properties");
            if (!tmp.exists()) {
                JOptionPane
                        .showMessageDialog(
                                null,
                                "Cannot find XAlign configuration file " + tmp.getAbsolutePath(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            FileUtil.copyFile(tmp, f);
        }
        return f;
    }

    public static ArrayList<File> getDefaultDicList() {
        return getDefaultDicList(Config.getCurrentLanguage());
    }

    public static ArrayList<File> getDefaultDicList(String language) {
        final ArrayList<File> res = new ArrayList<File>();
        final File userLanguageDir = new File(Config.getUserDir(), language);
        File name2 = new File(userLanguageDir, "user_dic.def");
        try {
            final BufferedReader br = new BufferedReader(new FileReader(name2));
            String s;
            while ((s = br.readLine()) != null) {
                res.add(new File(new File(userLanguageDir, "Dela"), s));
            }
            br.close();
        } catch (FileNotFoundException ee) {
            // nothing to do
        } catch (IOException e) {
            e.printStackTrace();
        }
        name2 = new File(userLanguageDir, "system_dic.def");
        try {
            final BufferedReader br = new BufferedReader(new FileReader(name2));
            String s;
            final File systemDelaDir = new File(new File(Config.getUnitexDir(), language), "Dela");
            while ((s = br.readLine()) != null) {
                res.add(new File(systemDelaDir, s));
            }
            br.close();
        } catch (FileNotFoundException ee) {
            // nothing to do
        } catch (IOException e) {
            // e.printStackTrace();
        }
        return res;
    }



    private static final ArrayList<LanguageListener> listeners = new ArrayList<LanguageListener>();

    private static boolean firing = false;

    public static void addLanguageListener(LanguageListener l) {
        listeners.add(l);
    }

    public static void removeLanguageListener(LanguageListener l) {
        if (firing) {
            throw new IllegalStateException("Should not remove a listener while firing");
        }
        listeners.remove(l);
    }

    private static void fireLanguageChanged() {
        firing = true;
        try {
            for (LanguageListener l : listeners) {
                l.languageChanged();
            }
        } finally {
            firing = false;
        }
    }

    public static void cleanTfstFiles(boolean deleteSentenceGraphs) {
        if (deleteSentenceGraphs) {
            FileUtil.deleteFileByName(new File(Config
                    .getCurrentSntDir(), "sentence*.grf"));
        }
        FileUtil.deleteFileByName(new File(Config
                .getCurrentSntDir(), "cursentence.grf"));
        FileUtil.deleteFileByName(new File(Config
                .getCurrentSntDir(), "cursentence.txt"));
        FileUtil
                .deleteFileByName(new File(Config
                        .getCurrentSntDir(),
                        "currentelagsentence.grf"));
        FileUtil
                .deleteFileByName(new File(Config
                        .getCurrentSntDir(),
                        "currentelagsentence.txt"));
        FileUtil.deleteFileByName(new File(Config
                .getCurrentSntDir(), "text-elag.tfst"));
        FileUtil.deleteFileByName(new File(Config
                .getCurrentSntDir(), "text-elag.tfst.bak"));
        FileUtil.deleteFileByName(new File(Config
                .getCurrentSntDir(), "text-elag.tind"));
        FileUtil.deleteFileByName(new File(Config
                .getCurrentSntDir(), "text-elag.tind.bak"));
        FileUtil.deleteFileByName(new File(Config
                .getCurrentSntDir(), "tfst_tags_by_freq.txt"));
        FileUtil.deleteFileByName(new File(Config
                .getCurrentSntDir(), "tfst_tags_by_alph.txt"));
    }


}