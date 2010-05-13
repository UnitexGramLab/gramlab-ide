/*
 * Unitex
 *
 * Copyright (C) 2001-2010 Universit� Paris-Est Marne-la-Vall�e <unitex@univ-mlv.fr>
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
import java.util.*;
import java.util.regex.*;

import javax.swing.*;

/**
 * This class contains general configuration information. It contains constants
 * used by many other classes.
 * 
 * @author S�bastien Paumier
 *  
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

	public final static int WINDOWS_SYSTEM = 0;
	public final static int LINUX_SYSTEM = 1;
	public final static int MAC_OS_X_SYSTEM = 2;
	public final static int SUN_OS_SYSTEM = 3;

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

	private static File userFile;

	private static File currentDELA;
	

	/**
	 * Message shown chen a text file is too large to be loaded.
	 */
	public static final String CORPUS_TOO_LARGE_MESSAGE = "This corpus is ready to be manipulated with Unitex, but it is too large to be displayed.\nUse a wordprocessor to view it.";

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
	 * Initializes the system. This method finds which system is running, which
	 * user is working. Then, it initalizes dialog boxes and ask the user to
	 * choose the initial language he wants to work on. if appPath is not
	 * null, it is supposed to represent the directory in which the
	 * external programs are located.   
	 */
	public static void initConfig(String appPath) {
		determineWhichSystemIsRunning();
		determineUnitexDir(appPath);
		MyCursors.initCursorsAndIcons();
		determineCurrentUser();
		chooseInitialLanguage();
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
		pngFileFilter=new PersonalFileFilter("png", "PNG Image");
		svgFileFilter=new PersonalFileFilter("svg", "Scalable Vector Graphics");
		grfFileFilter=new PersonalFileFilter("grf", "Unicode Graphs");
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
		File f=new File(Config.getCurrentGraphDir(),"Preprocessing");
		f=new File(f,"Sentence");
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
		File f=new File(Config.getCurrentGraphDir(),"Preprocessing");
		f=new File(f,"Replace");
		replaceDialogBox.setCurrentDirectory(f);
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
        File f=new File(Config.getCurrentGraphDir(),"Normalization");
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

	
	/**
	 * Updates working directories of dialog boxes. This method is called when
	 * the user changes of working language.
	 *  
	 */
	public static void updateOpenSaveDialogBoxes() {
		if (graphDialogBox != null)
			graphDialogBox.setCurrentDirectory(Config.getCurrentGraphDir());
		if (grfAndFst2DialogBox != null)
			grfAndFst2DialogBox
					.setCurrentDirectory(Config.getCurrentGraphDir());
		if (sentenceDialogBox != null)
			sentenceDialogBox
					.setCurrentDirectory(new File(new File(Config.getCurrentGraphDir(),"Preprocessing"),"Sentence"));
		if (replaceDialogBox != null)
			replaceDialogBox
					.setCurrentDirectory(new File(new File(Config.getCurrentGraphDir(),"Preprocessing"),"Replace"));
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
	public static void determineUnitexDir(String appPath) {
		setApplicationDir(appPath,new File(System.getProperty("user.dir")));
	}

	/**
	 * Finds the user's name. If the user works with Unitex for the first time,
	 * he is asked for choosing his private working directory if he works under
	 * Windows. Under Linux or MacOS, his private directory is a directory named
	 * "unitex", created in his home directory.
	 *  
	 */
	public static void determineCurrentUser() {
		userName = System.getProperty("user.name");
		if (currentSystem == WINDOWS_SYSTEM) {
			// configuration procedure under Windows
			File directory = new File(getUnitexDir(), "Users");
			if (!directory.exists()) {
				directory.mkdir();
			}
			userFile = new File(new File(getUnitexDir(), "Users"), userName
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
			File rep = new File(System.getProperty("user.home"), "unitex");
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
		}
	}

	/**
	 * Finds which operating system is used. If the system is not supported by
	 * Unitex, the program stops.
	 *  
	 */
	public static void determineWhichSystemIsRunning() {
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
				|| currentSystemName.equalsIgnoreCase("Windows 7")) {
			currentSystem = WINDOWS_SYSTEM;
		} else if (currentSystemName.equalsIgnoreCase("linux")) {
			currentSystem = LINUX_SYSTEM;
		} else if (currentSystemName.equalsIgnoreCase("mac os x") ||
		           currentSystemName.equalsIgnoreCase("Darwin")) {
			currentSystem = MAC_OS_X_SYSTEM;
		} else if (currentSystemName.equalsIgnoreCase("sunos")) {
			currentSystem = SUN_OS_SYSTEM;
		} else {
			JOptionPane.showMessageDialog(null,
					"Unitex is not configured for "+currentSystemName+".\nPlease contact unitex@univ-mlv.fr", "Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		System.out.println("Unitex is running under " + currentSystemName);
	}

	/**
	 * 
	 * @return the current system constant
	 */
	public static int getCurrentSystem() {
		return currentSystem;
	}

	/**
	 * 
	 * @return the current system name
	 */
	public static String getCurrentSystemName() {
		return currentSystemName;
	}

	/**
	 * 
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
	 * @param s directory's path
	 */
	public static void setApplicationDir(String appPath,File s) {
		if (appPath!=null) {
			applicationDir = new File(appPath);
		} else {
			applicationDir = s;
		}
		setUnitexDir(applicationDir.getParentFile());
	}

	/**
	 * 
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
	 * @param s
	 *            directory's path
	 */
	public static void setUnitexDir(File s) {
		unitexDir = s;
	}

	/**
	 * 
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
	 * @param s
	 *            directory's path
	 */
	public static void setUserDir(File s) {
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
	 * 
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
	 * @param s
	 *            language's name
	 */
	public static void setCurrentLanguage(String s) {
		currentLanguage = s;
		setUnitexCurrentLanguageDir(new File(getUnitexDir(), currentLanguage));
		setUserCurrentLanguageDir(new File(getUserDir(), currentLanguage));
		setCurrentCorpusDir(new File(getUserCurrentLanguageDir(), "Corpus"));
		setCurrentGraphDir(new File(getUserCurrentLanguageDir(), "Graphs"));
		setCurrentElagDir(new File(getUserCurrentLanguageDir(), "Elag"));
		setAlphabet(new File(getUserCurrentLanguageDir(), "Alphabet.txt"));
		setDefaultPreprocessingGraphs();
		updateOpenSaveDialogBoxes();
		Preferences.reset();
	}

	
	
	private static final String[] bastien=new String[] {
	        "Greek (Ancient)",
	        "grec momifi�",
	        "grec putr�fi�",
	        "grec embaum�",
	        "grec faisand�",
	        "grec mort",
	        "vieux grec tout rabougri",
	        "grec qui sent la naphtaline",
	        "grec p�rim�",
	        "grec cadav�rique",
	        "grec de morgue",
	        "grec tellement vieux qu'on fr�le la profanation",
	        "grec m�dico-l�gal",
	        "grec grouillant d'insectes n�crophages",
	        "grec en d�composition",
	        "grec moisi (les fameux champignons � la grecque)",
	        "grec qui ferait vomir un marchand de k�babs de rat",
	        "grec pourri",
	        "grec dess�ch�"
	};
	private static final String[] jeesun=new String[] {
	    "\uC9C0\uC21C\uC744 \uC704\uD55C \uD55C\uAD6D\uC5B4",
	    "2000\uB144 \uB3D9\uC548\uC758 \uD55C\uAD6D\uC5B4",
	    "\uC0C8\uBCBD 4\uC2DC\uC758 \uD55C\uAD6D\uC5B4",
	    "\uAC74\uBC30\uB77C\uB294 \uB2E8\uC5B4\uB9CC\uC744 \uD560 \uC904 \uC544\uB294 \uC0AC\uB78C\uC758 \uD55C\uAD6D\uC5B4",
	    "\uD55C\uAD6D\uC5B4\uC640 \uC220",
	    "cor�en rien que pour toi toute seule"
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
		return currentLanguage;
	}
	
	
	/**
	 * 
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
	 * @param s
	 *            directory's path
	 */
	public static void setUnitexCurrentLanguageDir(File s) {
		unitexCurrentLanguageDir = s;
	}

	/**
	 * 
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
	 * @param s
	 *            directory's path
	 */
	public static void setUserCurrentLanguageDir(File s) {
		userCurrentLanguageDir = s;
		// now, we verify if the directory exists
		if (!userCurrentLanguageDir.exists()) {
			// if the path does not exists
			if (!userCurrentLanguageDir.mkdir()) {
				System.err.println("ERROR: cannot create directory "
						+ s.getAbsolutePath());
				System.exit(1);
			}
			copyFileByName(new File(getUnitexCurrentLanguageDir(), "*"),
					getUserCurrentLanguageDir());
		}
		if (deprecatedConfigFile(new File(s,"Config"))) {
			copyFileByName(new File(getUnitexCurrentLanguageDir(), "Config"),
					getUserCurrentLanguageDir());
		}
		// if any of the sub-directories does not exist, we create it
		File f = new File(userCurrentLanguageDir, "Corpus");
		if (!f.exists()) {
			f.mkdir();
			copyDirRec(new File(getUnitexCurrentLanguageDir(),"Corpus"),f);
		}
		f = new File(userCurrentLanguageDir, "Elag");
		if (!f.exists()) {
			copyDirRec(new File(getUnitexCurrentLanguageDir(), "Elag"), f);
		}

		f = new File(userCurrentLanguageDir, "Inflection");
		if (!f.exists()) {
			copyDirRec(new File(getUnitexCurrentLanguageDir(), "Inflection"), f);
		}
		f = new File(userCurrentLanguageDir, "Dela");
		if (!f.exists()) {
			f.mkdir();
		}
		f = new File(userCurrentLanguageDir, "Graphs");
		if (!f.exists()) {
			copyDirRec(new File(getUnitexCurrentLanguageDir(), "Graphs"), f);
		}

	}

	/**
	 * @param file
	 * @return true if the configuration file does start with '#' 
	 */
	private static boolean deprecatedConfigFile(File file) {
		if (file==null) {return false;}
		try {
			FileInputStream stream=new FileInputStream(file);
			if (stream.available()==0) {
				stream.close();
				return false;
			}
            int c=stream.read();
            stream.close();
            return (c!='#');
			
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e1) {
			return false;
		}
	}

	/**
	 * 
	 * @return user's current corpus directory
	 */
	public static File getCurrentCorpusDir() {
		if (currentCorpusDir == null) {
			System.out.println("ERROR");
		}
		return currentCorpusDir;
	}

	/**
	 * Sets the user's current corpus directory
	 * 
	 * @param s
	 *            directory's path
	 */
	public static void setCurrentCorpusDir(File s) {
		currentCorpusDir = s;
	}

	/**
	 * 
	 * @return user's current graph directory
	 */
	public static File getCurrentGraphDir() {
		if (currentGraphDir == null) {
			System.out.println("ERROR");
		}
		return currentGraphDir;
	}

	/**
	 * Sets the user's current graph directory
	 * 
	 * @param s
	 *            directory's path
	 */
	public static void setCurrentGraphDir(File s) {
		currentGraphDir = s;
	}

	/**
	 * 
	 * @return user's current ELAG directory
	 */
	public static File getCurrentElagDir() {
		if (currentElagDir == null) {
			System.out.println("ERROR");
		}
		return currentElagDir;
	}

	/**
	 * Sets the user's current ELAG directory
	 * 
	 * @param s
	 *            directory's path
	 */
	public static void setCurrentElagDir(File s) {
		currentElagDir = s;
	}

	/**
	 * 
	 * @return user's current alphabet file
	 */
	public static File getAlphabet() {
		if (alphabet == null) {
			System.out.println("ERROR");
		}
		return alphabet;
	}

	/**
	 * Sets user's current alphabet file
	 * 
	 * @param s
	 *            file's path
	 */
	public static void setAlphabet(File s) {
		alphabet = s;
	}

	/**
	 * 
	 * @return user's name
	 */
	public static String getUserName() {
		return userName;
	}

	/**
	 * Asks for the user to select his private directory. IMPORTANT: this method
	 * must be called only when Unitex is running under Windows.
	 *  
	 */
	public static void chooseNewUserDir() {
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
			Object[] options = {"OK", "Cancel"};
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
		JFileChooser f = new JFileChooser();
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
		File[] fileList = directory.listFiles(new FileFilter() {
			public boolean accept(File file) {
				String name = file.getName();
				return file.isDirectory() && !name.equals("App")
						&& !name.equals("Src") 
						&& !name.equals("Users")
						&& !name.equals("XAlign");
			}
		});
		for (int i = 0; i < fileList.length; i++) {
			languages.add(fileList[i].getName());
		}
	}

	/**
	 * Shows a dialog box that offers to the user to choose the initial language
	 * he wants to work on
	 *  
	 */
	public static void chooseInitialLanguage() {
		JPanel p = new JPanel();
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
		Object[] options = {"OK", "Exit"};
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
		TreeSet<String> languages = new TreeSet<String>();
		collectLanguage(getUnitexDir(),languages);
		collectLanguage(getUserDir(),languages);
		JComboBox langList = new JComboBox(languages.toArray());
		String old = getCurrentLanguage();
		Object[] options = {"OK", "Cancel"};
		if (0 == JOptionPane.showOptionDialog(null, langList,
				"Choose a language", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0])) {
			setCurrentLanguage((String) (langList.getSelectedItem()));
			UnitexFrame.mainFrame.setTitle(Version.version
					+ " - current language is " + getCurrentLanguageForTitleBar());
			if (!old.equals(getCurrentLanguage())) {
				// if the language has really changed
				// post pone code
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						UnitexFrame.mainFrame.closeText();
					}
				});
			}
		}
	}

	/**
	 * 
	 * @return current corpus
	 */
	public static File getCurrentSnt() {
		return currentSnt;
	}

	/**
	 * Sets the current corpus
	 * 
	 * @param s
	 *            name of the corpus file
	 */
	public static void setCurrentSnt(File s) {
		currentSnt = s;
		setCurrentSntDir(s);
	}

	/**
	 * 
	 * @return current corpus
	 */
	public static File getCurrentSntDir() {
		return currentSntDir;
	}

	/**
	 * Sets the current corpus
	 * 
	 * @param s
	 *            name of the corpus file
	 */
	public static void setCurrentSntDir(File s) {
		String path;
		path = Util.getFileNameWithoutExtension(s.getAbsolutePath());
		currentSntDir = new File(path + "_snt");
		if (currentSntDir.exists() && !currentSntDir.isDirectory()) {
			JOptionPane.showMessageDialog(null,
					currentSntDir.getAbsolutePath()+" is not a directory but a file.\n"+
					"\nUnitex will abort.",
					"Fatal error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	/**
	 * 
	 * @return current sentence delimitation graph
	 */
	public static File getCurrentSentenceGraph() {
		return currentSentenceGraph;
	}

	/**
	 * Sets current sentence delimitation graph
	 * 
	 * @param s
	 *            graph's name
	 */
	public static void setCurrentSentenceGraph(File s) {
		currentSentenceGraph = s;
	}

	/**
	 * 
	 * @return current replace graph
	 */
	public static File getCurrentReplaceGraph() {
		return currentReplaceGraph;
	}

	/**
	 * Sets current replace graph
	 * 
	 * @param s
	 *            graph's name
	 */
	public static void setCurrentReplaceGraph(File s) {
		currentReplaceGraph = s;
	}

    /**
     * 
     * @return current sentence normalization graph
     */
    public static File getCurrentNormGraph() {
        return currentNormGraph;
    }

    /**
     * Sets current sentence normalization graph
     * 
     * @param s
     *            graph's name
     */
    public static void setCurrentNormGraph(File s) {
        currentNormGraph = s;
    }

	/**
	 * Sets sentence delimitation and replace graphs
	 *  
	 */
	public static void setDefaultPreprocessingGraphs() {
		File sentence = new File(getUserCurrentLanguageDir(), "Graphs");
		sentence = new File(sentence, "Preprocessing");
		File replace = new File(sentence, "Replace");
		sentence = new File(sentence, "Sentence");
		if (getCurrentLanguage().equals("Thai")) {
			// this is an exception because we do not have anymore
			// the .grf file
			sentence = new File(sentence, "Sentence.fst2");
		}
		else {
			sentence = new File(sentence, "Sentence.grf");
		}
		replace = new File(replace, "Replace.grf");
		setCurrentSentenceGraph(sentence);
		setCurrentReplaceGraph(replace);
		File z=new File(getUserCurrentLanguageDir(),"Graphs");
		z=new File(z,"Normalization");
		z=new File(z,"Norm.grf");
        setCurrentNormGraph(z);
	}

	/**
	 * Copies files. The source is specified by a file name that can contain *
	 * and ? jokers.
	 * 
	 * @param src
	 *            source
	 * @param dest
	 *            destination
	 */
	public static void copyFileByName(File src, File dest) {
		File path_src = src.getParentFile();
		String expression = src.getName();
		if (dest.isDirectory()) {
			File files_list[] = path_src
					.listFiles(new RegFileFilter(expression));
			if (files_list != null) {
				for (int i = 0; i < files_list.length; i++) {
					File F = files_list[i];
					if (!F.isDirectory()) {
						copyFile(F, new File(dest, F.getName()));
					}
				}
			}
		} else
			copyFile(src, dest);
	}

	/**
	 * Copies a directory recursively.
	 * 
	 * @param src
	 *            source directory
	 * @param dest
	 *            destination directory
	 */

	public static void copyDirRec(File src, File dest) {

		if (!src.isDirectory()) {
			return;
		}
		if (!dest.exists()) {
			dest.mkdirs();
		}
		File files_list[] = src.listFiles();
		if (files_list == null) {
			return;
		}
		for (int i = 0; i < files_list.length; i++) {
			File f = files_list[i];
			if (f.isDirectory()) {
				copyDirRec(f, new File(dest, f.getName()));
			} else if (f.isFile()) {
				copyFile(f, new File(dest, f.getName()));
			}
		}
	}

	/**
	 * Deletes files. The source is specified by a file name that can contain *
	 * and ? jokers.
	 * 
	 * @param src
	 *            source
	 */
	public static void deleteFileByName(File src) {
		File path_src = src.getParentFile();
		String expression = src.getName();
		File files_list[] = path_src.listFiles(new RegFileFilter(
				expression));
		if (files_list != null) {
			for (int i = 0; i < files_list.length; i++) {
				files_list[i].delete();
			}
		}
	}

	/**
	 * Deletes files. The source is specified by a file name that can contains *
	 * and ? jokers. This method differs from deleteFileByName because it cannot
	 * delete directories.
	 * 
	 * @param src
	 *            source
	 */
	public static void removeFile(File src) {
		File path_src = src.getParentFile();
		String expression = src.getName();
		File files_list[] = path_src.listFiles(new RegFileFilter(
				expression));
		for (int i = 0; i < files_list.length; i++) {
			File F;
			if (!(F = files_list[i]).isDirectory()) {
				F.delete();
			}
		}
	}

	static class RegFileFilter implements FilenameFilter {

		public String expression;

		RegFileFilter(String exp) {
			expression = exp.replaceAll("\u002C", "\\.");
			expression = expression.replaceAll("\\*", ".*");
			expression = expression.replaceAll("\\?", ".");
		}

		public boolean accept(File dir, String f) {
			return Pattern.matches(expression, f);
		}
	}

	/**
	 * Copy one file.
	 * 
	 * @param src
	 *            source file
	 * @param dest
	 *            destination file
	 */
	public static void copyFile(File src, File dest) {
		try {
			FileInputStream fis = new FileInputStream(src);
			FileOutputStream fos = new FileOutputStream(dest);
			copyStream(fis, fos);
			fis.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void copyStream(InputStream fis, OutputStream fos) {
		try {
			byte[] buf = new byte[2048];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets current DELA
	 * 
	 * @param s
	 *            dictionary name
	 */
	public static void setCurrentDELA(File s) {
		currentDELA = s;
	}

	/**
	 * 
	 * @return current DELA's name
	 */
	public static File getCurrentDELA() {
		return currentDELA;
	}

	/**
	 * 
	 * @return current font used to display texts
	 */
	public static Font getCurrentTextFont() {
		return Preferences.getCloneOfPreferences().textFont;
	}
	
	
	
	
	public static boolean isCharByCharLanguage() {
		return Preferences.pref.charByChar;
	}

	public static boolean morphologicalUseOfSpaceAllowed() {
		return Preferences.pref.morphologicalUseOfSpace;
	}

	public static boolean isCharByCharLanguage(String language) {
		File config=new File(new File(Config.getUserDir(),language),"Config");
		if (!config.exists()) {
			return false;
		}
		Properties prop=Preferences.loadProperties(config,null);
		String s=prop.getProperty("CHAR BY CHAR");
		if (s==null) return false;
		return Boolean.parseBoolean(s);
	}

	public static boolean morphologicalUseOfSpaceAllowed(String language) {
		File config=new File(new File(Config.getUserDir(),language),"Config");
		if (!config.exists()) {
			return false;
		}
		Properties prop=Preferences.loadProperties(config,null);
		String s=prop.getProperty("MORPHOLOGICAL USE OF SPACE");
		if (s==null) return false;
		return Boolean.parseBoolean(s);
	}

	public static ArrayList<File> morphologicalDic(String language) {
		File config=new File(new File(Config.getUserDir(),language),"Config");
		if (!config.exists()) {
			return null;
		}
		Properties prop=Preferences.loadProperties(config,null);
		String s=prop.getProperty("MORPHOLOGICAL DICTIONARY");
		if (s==null) return null;
		return Preferences.tokenizeMorphologicalDicList(s);
	}

	public static boolean isRightToLeftLanguage() {
		return Preferences.pref.rightToLeft;
	}

	public static boolean isKorean() {
		return Config.getCurrentLanguage().equals("Korean") 
		    || isKoreanJeeSun();
	}
	
	public static boolean isKoreanJeeSun() {
        return Config.getCurrentLanguage().equals("KoreanJeeSun");
    }
	
	public static File getXAlignDirectory() {
		File dir=new File(Config.getUserDir(),"XAlign");
		if (!dir.exists()) {
			File foo=new File(Config.getUnitexDir(),"XAlign");
			if (!foo.exists()) {
				JOptionPane
				.showMessageDialog(
						null,
						"Cannot find directory "+foo.getAbsolutePath(),
						"Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
			copyDirRec(foo,dir);
		}
		return dir;
	}

	
	public static File getAlignmentProperties() {
		File dir=getXAlignDirectory();
		File f=new File(dir,"multialign.properties");
		if (!f.exists()) {
			File tmp=new File(new File(Config.getUnitexDir(),"XAlign"),"multialign.properties");
			if (!tmp.exists()) {
				JOptionPane
				.showMessageDialog(
						null,
						"Cannot find XAlign configuration file "+tmp.getAbsolutePath(),
						"Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
			copyFile(tmp,f);
		}
		return f;
	}


}