package org.gramlab.core.gramlab.project;

import java.awt.EventQueue;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.gramlab.api.InternalFileEditor;
import org.gramlab.core.GramlabConfigManager;
import org.gramlab.core.Main;
import org.gramlab.core.gramlab.frames.GramlabInternalFrameManager;
import org.gramlab.core.gramlab.project.config.ProjectLocalConfig;
import org.gramlab.core.gramlab.project.config.ProjectVersionableConfig;
import org.gramlab.core.gramlab.project.config.buildfile.ExtractMatchType;
import org.gramlab.core.gramlab.project.config.buildfile.FileOperationType;
import org.gramlab.core.gramlab.project.config.concordance.ConcordanceOperationType;
import org.gramlab.core.gramlab.project.config.concordance.ConcordanceSortType;
import org.gramlab.core.gramlab.project.config.concordance.ConcordanceType;
import org.gramlab.core.gramlab.project.config.concordance.ResultDisplay;
import org.gramlab.core.gramlab.project.config.locate.MatchesPolicy;
import org.gramlab.core.gramlab.project.config.locate.OutputsPolicy;
import org.gramlab.core.gramlab.project.config.locate.VariableErrorPolicy;
import org.gramlab.core.gramlab.project.config.maven.Artifact;
import org.gramlab.core.gramlab.project.config.maven.MvnBuildConfig;
import org.gramlab.core.gramlab.project.config.maven.MvnSourceConfig;
import org.gramlab.core.gramlab.project.config.maven.PackageOperation;
import org.gramlab.core.gramlab.project.config.maven.Pom;
import org.gramlab.core.gramlab.project.config.maven.PomIO;
import org.gramlab.core.gramlab.project.config.preprocess.fst2txt.Preprocessing;
import org.gramlab.core.gramlab.project.config.preprocess.fst2txt.PreprocessingStep;
import org.gramlab.core.gramlab.project.console.ConsolePanel;
import org.gramlab.core.gramlab.project.console.ConsoleUtil;
import org.gramlab.core.gramlab.svn.SvnExecutor;
import org.gramlab.core.gramlab.svn.SvnInfo;
import org.gramlab.core.gramlab.util.SplitUtil;
import org.gramlab.core.gramlab.workspace.ProjectNode;
import org.gramlab.core.gramlab.workspace.WorkspaceTreeModel;
import org.gramlab.core.umlv.unitex.common.frames.manager.FrameManager;
import org.gramlab.core.umlv.unitex.common.project.Project;
import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;
import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.config.ConfigModel;
import org.gramlab.core.umlv.unitex.config.InjectedVariable;
import org.gramlab.core.umlv.unitex.config.NamedRepository;
import org.gramlab.core.umlv.unitex.config.Preferences;
import org.gramlab.core.umlv.unitex.config.PreferencesManager;
import org.gramlab.core.umlv.unitex.console.ConsoleEntry;
import org.gramlab.core.umlv.unitex.exceptions.InvalidConcordanceOrderException;
import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.frames.InternalFrameManager;
import org.gramlab.core.umlv.unitex.frames.TextDicFrame;
import org.gramlab.core.umlv.unitex.frames.TextFrame;
import org.gramlab.core.umlv.unitex.frames.TokensFrame;
import org.gramlab.core.umlv.unitex.io.Encoding;
import org.gramlab.core.umlv.unitex.listeners.TextFrameListener;
import org.gramlab.core.umlv.unitex.process.Launcher;
import org.gramlab.core.umlv.unitex.process.ToDoAfterSingleCommand;
import org.gramlab.core.umlv.unitex.process.ToDoBeforeSingleCommand;
import org.gramlab.core.umlv.unitex.process.commands.CommandBuilder;
import org.gramlab.core.umlv.unitex.process.commands.ConcorDiffCommand;
import org.gramlab.core.umlv.unitex.process.commands.ConcordCommand;
import org.gramlab.core.umlv.unitex.process.commands.DicoCommand;
import org.gramlab.core.umlv.unitex.process.commands.ExtractCommand;
import org.gramlab.core.umlv.unitex.process.commands.Grf2Fst2Command;
import org.gramlab.core.umlv.unitex.process.commands.LocateCommand;
import org.gramlab.core.umlv.unitex.process.commands.MkdirCommand;
import org.gramlab.core.umlv.unitex.process.commands.MultiCommands;
import org.gramlab.core.umlv.unitex.process.commands.NormalizeCommand;
import org.gramlab.core.umlv.unitex.process.commands.Reg2GrfCommand;
import org.gramlab.core.umlv.unitex.process.commands.SortTxtCommand;
import org.gramlab.core.umlv.unitex.process.commands.SvnCommand;
import org.gramlab.core.umlv.unitex.process.commands.TokenizeCommand;
import org.gramlab.core.umlv.unitex.svn.SvnConflict;
import org.gramlab.core.umlv.unitex.svn.SvnMonitor;
import org.gramlab.core.umlv.unitex.text.Text;

import ro.fortsoft.pf4j.DefaultPluginManager;
/**
 * Description of a gramlab project.
 * 
 * @author paumier
 * 
 */
public class GramlabProject implements Project, Comparable<GramlabProject> {

	private ConfigModel config;
	private String name;

	private File projectDirectory;
	private File corpusDirectory;
	private File delaDirectory;
	private File inflectionDirectory;
	private File graphsDirectory;
	private File preprocessingDirectory;
	private File sentenceDirectory;
	private File replaceDirectory;
	private File preferencesFile;
	private boolean open;

	private Preferences preferences;
	private File inheritedPreferences;

	GramlabInternalFrameManager frameManager;
	private Pom POM;

	private ProjectVersionableConfig projectVersionableConfig;
	private ProjectLocalConfig projectLocalConfig;
	private ConsolePanel consolePanel = new ConsolePanel();

	public static final String PROJECT_PREFERENCES_FILE = "project.preferences";
	private static final String PROJECT_VERSIONABLE_CONFIG_FILE = "project.versionable_config";
	private static final String PROJECT_LOCAL_CONFIG_FILE = "project.local_config";

	private ConsoleUtil consoleUtil = new ConsoleUtil();
	private SplitUtil splitUtil = new SplitUtil(null, null);
	

	private DefaultPluginManager pluginManager = GramlabConfigManager.getPluginManager();
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof GramlabProject))
			return false;
		GramlabProject p = (GramlabProject) obj;
		return projectDirectory.equals(p.getProjectDirectory());
	}

	@Override
	public int hashCode() {
		return getProjectDirectory().hashCode();
	}

	public int compareTo(GramlabProject p) {
		return getName().compareTo(p.getName());
	}

	public boolean isOpen() {
		return open;
	}

	private void setOpen(boolean open) {
		this.open = open;
	}

	void open() {
		loadConfigurationFiles();
		setOpen(true);
	}

	/**
	 * Loads all the configuration files: - the maven one (pom.xml) - the
	 * project rendering preferences (project.preferences) - the project
	 * processing preferences (project.config)
	 */
	public void loadConfigurationFiles() {
		POM.loadFromFile();
		getPreferences();
		loadProjectVersionableConfig();
		loadProjectLocalConfig();
	}

	/**
	 * The pom.xml file has to saved only on a few occasions.
	 */
	public void saveConfigurationFiles(boolean savePom) throws IOException {
		if (savePom)
			PomIO.savePom(POM, this);
		PreferencesManager.savePreferences(getPreferencesFile(),
				getPreferences(), getName());
		saveProjectVersionableConfig();
		saveProjectLocalConfig();
	}

	void close() {
		setOpen(false);
	}

	/**
	 * NOTE: invoking this method creates the project directory DIR as well as
	 * DIR/src
	 */
	public GramlabProject(String name, String language, Encoding encoding,
			Artifact artifact) {
		this.name = name;
		this.projectDirectory = new File(
				GramlabConfigManager.getWorkspaceDirectory(), name);
		this.projectDirectory.mkdir();
		this.POM = new Pom(new File(getProjectDirectory(), "pom.xml"), artifact);
		getSrcDirectory().mkdir();
		this.preferencesFile = new File(getProjectDirectory(),
				PROJECT_PREFERENCES_FILE);
		this.preferences = null;
		this.inheritedPreferences = null;
		this.projectVersionableConfig = new ProjectVersionableConfig(this,
				language, encoding);
		this.projectLocalConfig = new ProjectLocalConfig(this);
	}

	public String getName() {
		return name;
	}

	public String getLanguage() {
		return projectVersionableConfig.getLanguage();
	}

	public File getProjectDirectory() {
		if (projectDirectory == null) {
			throw new IllegalStateException(
					"The project directory should not be null");
		}
		if (!projectDirectory.exists())
			projectDirectory.mkdir();
		return projectDirectory;
	}

	/**
	 * As a config file can be combined with a dependency one, this method
	 * always returns the project's own config file.
	 */
	public File getPreferencesFile() {
		return preferencesFile;
	}

	/**
	 * Returns a new project or null if the project was not properly created.
	 * 
	 * @param name
	 * @return
	 */
	public static GramlabProject createEmptyProject(String name, String language,
			Encoding encoding, Artifact artifact) {
		GramlabProject p = new GramlabProject(name, language, encoding, artifact);
		if (!p.getPom().createEmptyPom(p)) {
			return null;
		}
		try {
			if (!p.getPreferencesFile().createNewFile()) {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		if (!p.getCorpusDirectory().mkdir())
			return null;
		if (!p.getDelaDirectory().mkdir())
			return null;
		if (!p.getInflectionDirectory().mkdir())
			return null;
		if (!p.getGraphsDirectory().mkdir())
			return null;
		if (!p.getPreprocessingDirectory().mkdir())
			return null;
		if (!p.getSentenceDirectory().mkdir())
			return null;
		if (!p.getReplaceDirectory().mkdir())
			return null;
		File norm = new File(p.getSrcDirectory(), "Norm.txt");
		createDefaultNormTxt(norm);
		p.setNormTxt(norm);
		return p;
	}

	public File getSrcDirectory() {
		return new File(getProjectDirectory(), PomIO.SOURCE_DIRECTORY);
	}

	public File getTargetDirectory() {
		return new File(getProjectDirectory(), PomIO.TARGET_DIRECTORY);
	}

	public File getTargetPreprocessingDirectory() {
		return new File(getTargetDirectory(), "Preprocessing");
	}

	public Pom getPom() {
		return POM;
	}

	public File getCorpusDirectory() {
		if (corpusDirectory == null) {
			corpusDirectory = new File(getSrcDirectory(), "Corpus");
		}
		return corpusDirectory;
	}

	public File getDelaDirectory() {
		if (delaDirectory == null) {
			delaDirectory = new File(getSrcDirectory(), "Dela");
		}
		return delaDirectory;
	}

	public File getInflectionDirectory() {
		if (inflectionDirectory == null) {
			inflectionDirectory = new File(getSrcDirectory(), "Inflection");
		}
		return inflectionDirectory;
	}

	public File getGraphsDirectory() {
		if (graphsDirectory == null) {
			graphsDirectory = new File(getSrcDirectory(), "Graphs");
		}
		return graphsDirectory;
	}

	public File getPreprocessingDirectory() {
		if (preprocessingDirectory == null) {
			preprocessingDirectory = new File(getGraphsDirectory(),
					"Preprocessing");
		}
		return preprocessingDirectory;
	}

	public File getSentenceDirectory() {
		if (sentenceDirectory == null) {
			sentenceDirectory = new File(getPreprocessingDirectory(),
					"Sentence");
		}
		return sentenceDirectory;
	}

	public File getReplaceDirectory() {
		if (replaceDirectory == null) {
			replaceDirectory = new File(getPreprocessingDirectory(), "Replace");
		}
		return replaceDirectory;
	}

	public ConfigModel getConfigModel() {
		return config;
	}

	public File getAlphabet() {
		File alphabet = projectVersionableConfig.getAlphabet();
		if (alphabet == null || !alphabet.exists())
			return null;
		return alphabet;
	}

	public Encoding getEncoding() {
		return projectVersionableConfig.getEncoding();
	}

	public File getSortAlphabet() {
		File alphabet = projectVersionableConfig.getSortAlphabet();
		if (alphabet == null || !alphabet.exists())
			return null;
		return alphabet;
	}

	public static GramlabProject cloneUnitexResourcesProject(String name, File src,
			Encoding encoding, Artifact artifact, boolean systemDirectory,
			String language) {
		GramlabProject p = createEmptyProject(name, language, encoding, artifact);
		if (p == null)
			return null;
		FileUtil.copyDirRec(new File(src, "Corpus"), p.getCorpusDirectory());
		FileUtil.copyDirRec(new File(src, "Dela"), p.getDelaDirectory());
		FileUtil.copyDirRec(new File(src, "Inflection"),
				p.getInflectionDirectory());
		FileUtil.copyDirRec(new File(src, "Graphs"), p.getGraphsDirectory());
		/* If the alphabet file exists, we use it */
		File alphabet = new File(p.getSrcDirectory(), "Alphabet.txt");
		if (FileUtil.copyFile(new File(src, "Alphabet.txt"), alphabet)) {
			p.setAlphabet(alphabet);
		}
		File norm = new File(p.getSrcDirectory(), "Norm.txt");
		if (norm.exists()) {
			FileUtil.copyFile(new File(src, "Norm.txt"), norm);
		} else {
			createDefaultNormTxt(norm);
		}
		p.setNormTxt(norm);
		File sortAlphabet = new File(p.getSrcDirectory(), "Alphabet_sort.txt");
		if (FileUtil.copyFile(new File(src, "Alphabet_sort.txt"), sortAlphabet)) {
			p.setSortAlphabet(sortAlphabet);
		}
		ArrayList<PreprocessingStep> steps = new ArrayList<PreprocessingStep>();
		File sentence = new File(p.getSentenceDirectory(), "Sentence.grf");
		if (sentence.exists()) {
			File sentenceDir = new File(p.getTargetPreprocessingDirectory(),
					"Sentence");
			File fst2 = new File(sentenceDir, "Sentence.fst2");
			steps.add(new PreprocessingStep(sentence, fst2, true, true));
		}
		File replace = new File(p.getReplaceDirectory(), "Replace.grf");
		if (replace.exists()) {
			File replaceDir = new File(p.getTargetPreprocessingDirectory(),
					"Replace");
			File fst2 = new File(replaceDir, "Replace.fst2");
			steps.add(new PreprocessingStep(replace, fst2, false, true));
		}
		if (steps.size() > 0) {
			p.getPreprocessing().setPreprocessingSteps(steps);
		}
		p.setupDefaultUnitexDictionaries(src, systemDirectory);
		String unitexName = src.getName();
		/* PolyLex */
		if (systemDirectory) {
			if (unitexName.equals("German")) {
				p.setPolyLexBin(new File(p.getDelaDirectory(), "dela.bin"));
			} else if (unitexName.equals("Norwegian (Bokmal)")) {
				p.setPolyLexBin(new File(p.getDelaDirectory(),
						"Dela-sample.bin"));
			} else if (unitexName.equals("Norwegian (Nynorsk)")) {
				p.setPolyLexBin(new File(p.getDelaDirectory(),
						"Dela-sample.bin"));
			}
		}
		/* Language dependent things */
		if (unitexName.equals("Hebrew") || unitexName.equals("Arabic")) {
			p.setSemitic(true);
		}
		if (unitexName.equals("Korean")) {
			p.setKorean(true);
		}
		if (unitexName.equals("Arabic")) {
			File rules = new File(p.getSrcDirectory(), "arabic_typo_rules.txt");
			if (FileUtil
					.copyFile(new File(src, "arabic_typo_rules.txt"), rules)) {
				p.setArabicTypoRules(rules);
			}
		}
		if (unitexName.equals("Thai")) {
			p.setCharByChar(true);
		}
		FileUtil.copyFile(new File(src, "Config"), p.getPreferencesFile());
		try {
			p.saveProjectVersionableConfig();
			p.saveProjectLocalConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}

	private void setupDefaultUnitexDictionaries(File srcUnitexDir,
			boolean systemDirectory) {
		String defFile = systemDirectory ? "system_dic.def" : "user_dic.def";
		File f = new File(srcUnitexDir, defFile);
		if (!f.exists())
			return;
		ArrayList<File> files = new ArrayList<File>();
		Scanner scanner;
		try {
			scanner = new Scanner(f, "UTF8");
			while (scanner.hasNextLine()) {
				String name = scanner.nextLine();
				File dic = new File(getDelaDirectory(), name);
				if (dic.exists()) {
					files.add(dic);
				}
			}
		} catch (FileNotFoundException e) {
			return;
		}
		setDictionaries(files);
	}

	private static void createDefaultNormTxt(File norm) {
		FileUtil.write("{\t[\n" + "}\t]\n", norm);
	}

	public static GramlabProject cloneProject(String name, File srcProjectDir) {
		File projectDir = new File(
				GramlabConfigManager.getWorkspaceDirectory(), name);
		FileUtil.copyDirRec(srcProjectDir, projectDir);
		/* If there is a .svn directory, we don't want to copy it */
		File svnDir = new File(projectDir, ".svn");
		if (svnDir.exists()) {
			FileUtil.rm(svnDir);
		}
		GramlabProject p = new GramlabProject(name, null, null, null);
		p.loadConfigurationFiles();
		return p;
	}

	public Preferences getPreferences() {
		File f = preferencesFile;
		SvnConflict c = SvnConflict.getConflict(f);
		if (c != null) {
			f = c.mine;
		}
		if (preferences == null) {
			Preferences inheritance = null;
			if (inheritedPreferences != null && inheritedPreferences.exists()) {
				inheritance = PreferencesManager.loadPreferences(
						inheritedPreferences, null);
			}
			if (f.exists()) {
				preferences = PreferencesManager
						.loadPreferences(f, inheritance);
			} else {
				if (inheritance != null) {
					preferences = inheritance.clone();
				} else {
					preferences = new Preferences();
				}
			}
		}
		return preferences;
	}

	@Override
	public <M extends FrameManager> M getFrameManagerAs(Class<M> type) {
		return type.cast(frameManager);
	}

	public void setFrameManager(GramlabInternalFrameManager m) {
		frameManager = m;
		frameManager.addTextFrameListener(new TextFrameListener() {
			public void textFrameOpened(boolean taggedText) {
				File sntDir = FileUtil.getSntDir(getCurrentCorpus());
				TokensFrame tokenFrame = frameManager.newTokensFrame(new File(
						sntDir, "tok_by_freq.txt"), false);
				TextDicFrame textDicFrame = frameManager.newTextDicFrame(
						sntDir, false);
				try {
					tokenFrame.setIcon(true);
					textDicFrame.setIcon(true);
					TextFrame frame = frameManager.getTextFrame();
					if (frame != null) {
						frame.setSelected(true);
					}
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				}
			}

			public void textFrameClosed() {
				frameManager.closeTokensFrame();
				frameManager.closeConcordanceFrame();
				frameManager.closeConcordanceDiffFrame();
				frameManager.closeTextDicFrame();
				frameManager.closeApplyLexicalResourcesFrame();
				frameManager.closeConcordanceParameterFrame();
				frameManager.closeLocateFrame();
				frameManager.closeStatisticsFrame();
			}
		});
	}

	private File backupPOM;
	private ProjectVersionableConfig backupVersionableConfig;
	private ProjectLocalConfig backupLocalConfig;

	/**
	 * This method is supposed to be called before entering the project
	 * configuration process, in order to create a backup that could be restored
	 * in case the user press "Cancel" in before the process is complete.
	 */
	public void backupConfiguration() {
		backupPOM = new File(POM.getFile().getAbsolutePath() + ".backup");
		FileUtil.copyFile(POM.getFile(), backupPOM);
		backupVersionableConfig = projectVersionableConfig.clone();
		backupLocalConfig = projectLocalConfig.clone();
	}

	/**
	 * Restores the original configuration and deletes the backup. Does nothing
	 * if there is no backup, so that calling deleteBackup prior to this
	 * function does nothing wrong.
	 */
	public void restoreConfiguration() {
		if (backupPOM != null && backupPOM.exists()) {
			File f = POM.getFile();
			if (f != null && f.exists())
				f.delete();
			backupPOM.renameTo(POM.getFile());
			POM.loadFromFile();
		}
		projectVersionableConfig = backupVersionableConfig;
		projectLocalConfig = backupLocalConfig;
		deleteBackup();
	}

	public void deleteBackup() {
		if (backupPOM != null && backupPOM.exists())
			backupPOM.delete();
		backupPOM = null;
		backupVersionableConfig = null;
		backupLocalConfig = null;
	}

	public ArrayList<String> getAllRelativeFiles(String name) {
		ArrayList<String> files = new ArrayList<String>();
		lookForRelativeFile(files, getSrcDirectory(), name);
		lookForRelativeFile(files, new File(projectDirectory,
				PomIO.DEPENDENCY_DIRECTORY), name);
		return files;
	}

	public ArrayList<String> getAllRelativeFilesByExtensions(
			String[] extension, String pathName) {
		ArrayList<String> files = new ArrayList<String>();
		lookForRelativeFileByExtensions(files, getSrcDirectory(), extension,
				pathName);
		lookForRelativeFileByExtensions(files, new File(projectDirectory,
				PomIO.DEPENDENCY_DIRECTORY), extension, pathName);
		return files;
	}

	/**
	 * @return the list of the Alphabet.txt files found in the project
	 *         directory, including dependencies. The results are Strings and
	 *         not Files, because we want paths relative to the project
	 *         directory.
	 */
	public ArrayList<String> getAllAlphabetFiles() {
		return getAllRelativeFiles("Alphabet.txt");
	}

	public ArrayList<String> getAllSortAlphabetFiles() {
		return getAllRelativeFiles("Alphabet_sort.txt");
	}

	public ArrayList<String> getAllNormalizationFiles() {
		return getAllRelativeFiles("Norm.txt");
	}

	public ArrayList<String> getAllArabicTypoRulesFiles() {
		return getAllRelativeFiles("arabic_typo_rules.txt");
	}

	/**
	 * @return a String describing a relative pathname to the given file, or
	 *         null if the file is not in the project directory, or if f is
	 *         null. If the given file is a relative one, the function always
	 *         succeeds.
	 */
	public String getRelativeFileName(File f) {
		if (f == null)
			return null;
		if (!f.isAbsolute()) {
			f = new File(getProjectDirectory(), f.getAbsolutePath());
		}
		if (null == FileUtil.isAncestor(getProjectDirectory(), f)) {
			return null;
		}
		return FileUtil.getRelativePath(getProjectDirectory(), f);
	}

	/**
	 * Returns a relative path if f is in the project directory, or an absolute
	 * one otherwise
	 * 
	 * @param f
	 * @return
	 */
	public String getNormalizedFileName(File f) {
		String res = getRelativeFileName(f);
		if (res == null) {
			res = f.getAbsolutePath();
		}
		return res;
	}

	/**
	 * If the given String is an absolute path, the corresponding File is
	 * returned, otherwise, we consider the path to be relative to the project's
	 * directory.
	 */
	public File getFileFromNormalizedName(String s) {
		File f = new File(s);
		if (f.isAbsolute())
			return f;
		return new File(getProjectDirectory(), s);
	}

	public ArrayList<File> getFilesFromNormalizedNames(ArrayList<String> names) {
		ArrayList<File> files = new ArrayList<File>();
		for (String s : names) {
			File f = new File(s);
			if (f.isAbsolute())
				files.add(f);
			else
				files.add(new File(getProjectDirectory(), s));
		}
		return files;
	}

	/**
	 * Same as getFileFromNormalizedName, but returns null if the file is not in
	 * the project's directory
	 */
	public File getProjectFileFromNormalizedName(String s) {
		File f = new File(s);
		if (f.isAbsolute()) {
			if (null == getRelativeFileName(f)) {
				return null;
			}
			return f;
		}
		return new File(getProjectDirectory(), s);
	}

	private void lookForRelativeFile(ArrayList<String> files, File file,
			String name) {
		if (file == null)
			return;
		if (file.isFile()) {
			if (file.getName().equals(name)) {
				files.add(getRelativeFileName(file));
			}
			return;
		}
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				lookForRelativeFile(files, f, name);
			}
		}
	}

	private boolean contains(String[] t, String s) {
		for (int i = 0; i < t.length; i++) {
			if (t[i].equals(s))
				return true;
		}
		return false;
	}

	private boolean isInGoodPath(File f, String pathName) {
		if (pathName == null)
			return true;
		File parent = f.getParentFile();
		if (parent == null)
			return false;
		return parent.getName().equals(pathName);
	}

	private void lookForRelativeFileByExtensions(ArrayList<String> files,
			File file, String[] ext, String pathName) {
		if (file == null)
			return;
		if (file.isFile()) {
			String extension = FileUtil.getExtensionInLowerCase(file);
			if (contains(ext, extension) && isInGoodPath(file, pathName)) {
				files.add(getRelativeFileName(file));
			}
			return;
		}
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				lookForRelativeFileByExtensions(files, f, ext, pathName);
			}
		}
	}

	public void setAlphabet(File alphabet) {
		projectVersionableConfig.setAlphabet(alphabet);
	}

	public void setSortAlphabet(File alphabet) {
		projectVersionableConfig.setSortAlphabet(alphabet);
	}

	public Preprocessing getPreprocessing() {
		return projectVersionableConfig.getPreprocessing();
	}

	public File getNormTxt() {
		return projectVersionableConfig.getNormTxt();
	}

	public void setNormTxt(File f) {
		projectVersionableConfig.setNormTxt(f);
	}

	public ArrayList<File> getAllBinFiles() {
		ArrayList<String> names = getAllRelativeFilesByExtensions(
				new String[] { "bin" }, "Dela");
		ArrayList<File> files = new ArrayList<File>();
		for (String s : names) {
			files.add(new File(getProjectDirectory(), s));
		}
		return files;
	}

	public ArrayList<File> getAllDictionaryFiles() {
		ArrayList<String> names = getAllRelativeFilesByExtensions(new String[] {
				"bin", "fst2" }, "Dela");
		ArrayList<File> files = new ArrayList<File>();
		for (String s : names) {
			files.add(new File(getProjectDirectory(), s));
		}
		return files;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<File> getMorphoDics() {
		return (ArrayList<File>) projectVersionableConfig.getMorphoDics()
				.clone();
	}

	@SuppressWarnings("unchecked")
	public void setMorphoDics(ArrayList<File> morphoDics) {
		projectVersionableConfig.setMorphoDics((ArrayList<File>) morphoDics
				.clone());
	}

	@SuppressWarnings("unchecked")
	public ArrayList<File> getDictionaries() {
		return (ArrayList<File>) projectVersionableConfig.getDics().clone();
	}

	@SuppressWarnings("unchecked")
	public void setDictionaries(ArrayList<File> dictionaries) {
		projectVersionableConfig
				.setDics((ArrayList<File>) dictionaries.clone());
	}

	public void saveProjectVersionableConfig() throws IOException {
		File f = getProjectVersionableConfigFile();
		FileOutputStream stream;
		try {
			stream = new FileOutputStream(f);
			OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
			projectVersionableConfig.save(writer);
			/*
			 * The javadoc says that flush should be made implicitly made by the
			 * close, but it seems that it is not always true...
			 */
			writer.flush();
			writer.close();
			stream.close();
		} catch (FileNotFoundException e) {
			throw new IOException(e);
		} catch (UnsupportedEncodingException e) {
			throw new IOException(e);
		}
	}

	public void saveProjectLocalConfig() throws IOException {
		File f = getProjectLocalConfigFile();
		FileOutputStream stream;
		try {
			stream = new FileOutputStream(f);
			OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
			projectLocalConfig.save(writer);
			/*
			 * The javadoc says that flush should be made implicitly made by the
			 * close, but it seems that it is not always true...
			 */
			writer.flush();
			writer.close();
			stream.close();
		} catch (FileNotFoundException e) {
			throw new IOException(e);
		} catch (UnsupportedEncodingException e) {
			throw new IOException(e);
		}
	}

	public File getProjectVersionableConfigFile() {
		return new File(getProjectDirectory(), PROJECT_VERSIONABLE_CONFIG_FILE);
	}

	public File getProjectLocalConfigFile() {
		return new File(getProjectDirectory(), PROJECT_LOCAL_CONFIG_FILE);
	}

	private void loadProjectVersionableConfig() {
		File f = getProjectVersionableConfigFile();
		SvnConflict c = SvnConflict.getConflict(f);
		if (c != null) {
			f = c.mine;
		}
		projectVersionableConfig = ProjectVersionableConfig.load(this, f);
	}

	private void loadProjectLocalConfig() {
		File f = getProjectLocalConfigFile();
		projectLocalConfig = ProjectLocalConfig.load(this, f);
	}

	public void setEncoding(Encoding e) {
		projectVersionableConfig.setEncoding(e);
	}

	public boolean separatorNormalization() {
		return projectVersionableConfig.separatorNormalization();
	}

	public void setSeparatorNormalization(boolean b) {
		projectVersionableConfig.setSeparatorNormalization(b);
	}

	public boolean applyDictionaries() {
		return projectVersionableConfig.applyDictionaries();
	}

	public void setApplyDictionaries(boolean b) {
		projectVersionableConfig.setApplyDictionaries(b);
	}

	public File getPolyLexBin() {
		return projectVersionableConfig.getPolyLexBin();
	}

	public void setPolyLexBin(File bin) {
		projectVersionableConfig.setPolyLexBin(bin);
	}

	public boolean isSemitic() {
		return projectVersionableConfig.isSemitic();
	}

	public void setSemitic(boolean s) {
		projectVersionableConfig.setSemitic(s);
	}

	public boolean isKorean() {
		return projectVersionableConfig.isKorean();
	}

	public void setKorean(boolean k) {
		projectVersionableConfig.setKorean(k);
	}

  public boolean isMatchWordBoundaries() {
    return projectVersionableConfig.isMatchWordBoundaries();
  }

  public void setMatchWordBoundaries(boolean b) {
    projectVersionableConfig.setMatchWordBoundaries(b);
  }  

	public File getArabicTypoRules() {
		return projectVersionableConfig.getArabicTypoRules();
	}

	public void setArabicTypoRules(File f) {
		projectVersionableConfig.setArabicTypoRules(f);
	}

	public boolean isCharByChar() {
		return projectVersionableConfig.isCharByChar();
	}

	public void setCharByChar(boolean b) {
		projectVersionableConfig.setCharByChar(b);
	}

	public boolean isMorphologicalUseOfSpace() {
		return projectVersionableConfig.isMorphologicalUseOfSpace();
	}

	public void setMorphologicalUseOfSpace(boolean b) {
		projectVersionableConfig.setMorphologicalUseOfSpace(b);
	}

	public boolean isLastPatternRegexp() {
		return projectLocalConfig.isLastPatternRegexp();
	}

	public void setLastPatternRegexp(boolean b) {
		projectLocalConfig.setLastPatternRegexp(b);
	}

	public String getLastRegexp() {
		return projectLocalConfig.getLastRegexp();
	}

	public void setLastRegexp(String s) {
		projectLocalConfig.setLastRegexp(s);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<File> getLastGraphs() {
		return (ArrayList<File>) projectLocalConfig.getLastGraphs().clone();
	}

	@SuppressWarnings("unchecked")
	public void setLastGraphs(ArrayList<File> graphs) {
		projectLocalConfig.setLastGraphs((ArrayList<File>) graphs.clone());
	}

	public File getLastGraphDir() {
		return projectLocalConfig.getLastGraphDir();
	}

	public void setLastGraphDir(File f) {
		projectLocalConfig.setLastGraphDir(f);
	}

	public File getLastSntDir() {
		return projectLocalConfig.getLastSntDir();
	}

	public void setLastSntDir(File f) {
		projectLocalConfig.setLastSntDir(f);
	}

	public boolean isDebugMode() {
		return projectLocalConfig.isDebugMode();
	}

	public void setDebugMode(boolean selected) {
		projectLocalConfig.setDebugMode(selected);
	}

	public MatchesPolicy getMatchesPolicy() {
		return projectVersionableConfig.getMatchesPolicy();
	}

	public void setMatchesPolicy(MatchesPolicy index) {
		projectVersionableConfig.setMatchesPolicy(index);
	}

	public OutputsPolicy getOutputsPolicy() {
		return projectVersionableConfig.getOutputsPolicy();
	}

	public void setOutputsPolicy(OutputsPolicy outputs) {
		projectVersionableConfig.setOutputsPolicy(outputs);
	}

	public int getSearchLimit() {
		return projectVersionableConfig.getSearchLimit();
	}

	public void setSearchLimit(int limit) {
		projectVersionableConfig.setSearchLimit(limit);
	}

	public boolean isAmbiguousOutputsAllowed() {
		return projectVersionableConfig.isAmbiguousOutputsAllowed();
	}

	public void setAmbiguousOutputsAllowed(boolean b) {
		projectVersionableConfig.setAmbiguousOutputsAllowed(b);
	}

	public VariableErrorPolicy getVariableErrorPolicy() {
		return projectVersionableConfig.getVariableErrorPolicy();
	}

	public void setVariableErrorPolicy(VariableErrorPolicy p) {
		projectVersionableConfig.setVariableErrorPolicy(p);
	}

	public File getDefaultGraphRepository() {
		return projectVersionableConfig.getDefaultGraphRepository();
	}

	public void setDefaultGraphRepository(File f) {
		projectVersionableConfig.setDefaultGraphRepository(f);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<NamedRepository> getNamedRepositories() {
		return (ArrayList<NamedRepository>) projectVersionableConfig
				.getNamedRepositories().clone();
	}

	@SuppressWarnings("unchecked")
	public void setNamedRepositories(ArrayList<NamedRepository> l) {
		projectVersionableConfig
				.setNamedRepositories((ArrayList<NamedRepository>) l.clone());
	}

	@SuppressWarnings("unchecked")
	public ArrayList<InjectedVariable> getInjectedVariables() {
		return (ArrayList<InjectedVariable>) projectVersionableConfig
				.getInjectedVariables().clone();
	}

	@SuppressWarnings("unchecked")
	public void setInjectedVariables(ArrayList<InjectedVariable> l) {
		projectVersionableConfig
				.setInjectedVariables((ArrayList<InjectedVariable>) l.clone());
	}

	public File getNamedRepository(String repositoryName) {
		if (repositoryName == null) {
			return projectVersionableConfig.getDefaultGraphRepository();
		}
		return projectVersionableConfig.getNamedRepository(repositoryName);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<File> getLastSnt() {
		return (ArrayList<File>) projectLocalConfig.getLastSnt().clone();
	}

	@SuppressWarnings("unchecked")
	public void setLastSnt(ArrayList<File> snt) {
		projectLocalConfig.setLastSnt((ArrayList<File>) snt.clone());
	}

	public boolean displayGraphNames() {
		return projectLocalConfig.displayGraphNames();
	}

	public void setDisplayGraphNames(boolean b) {
		projectLocalConfig.setDisplayGraphNames(b);
	}

	public boolean emitEmptyGraphWarning() {
		return projectLocalConfig.emitEmptyGraphWarning();
	}

	public void setEmitEmptyGraphWarning(boolean b) {
		projectLocalConfig.setEmitEmptyGraphWarning(b);
	}

	public File getHtmlViewer() {
		return projectLocalConfig.getHtmlViewer();
	}

	public void setHtmlViewer(File f) {
		projectLocalConfig.setHtmlViewer(f);
	}

	public File getTextEditor() {
		return projectLocalConfig.getTextEditor();
	}

	public void setTextEditor(File f) {
		projectLocalConfig.setTextEditor(f);
	}

	public boolean useTextEditorForDictionaries() {
		return projectLocalConfig.useTextEditorForDictionaries();
	}

	public void setUseTextEditorForDictionaries(boolean b) {
		projectLocalConfig.setUseTextEditorForDictionaries(b);
	}

	public ResultDisplay getConcordanceDisplay() {
		return projectLocalConfig.getConcordanceDisplay();
	}

	public void setConcordanceDisplay(ResultDisplay display) {
		projectLocalConfig.setConcordanceDisplay(display);
	}

	public ResultDisplay getBuildResultDisplay() {
		return projectLocalConfig.getBuildResultDisplay();
	}

	public void setBuildResultDisplay(ResultDisplay display) {
		projectLocalConfig.setBuildResultDisplay(display);
	}

	public ConcordanceType getConcordanceType() {
		return projectLocalConfig.getConcordanceType();
	}

	public void setConcordanceType(ConcordanceType type) {
		projectLocalConfig.setConcordanceType(type);
	}

	public boolean onlyMatches() {
		return projectLocalConfig.onlyMatches();
	}

	public void setOnlyMatches(boolean b) {
		projectLocalConfig.setOnlyMatches(b);
	}

	public int getLeftContext() {
		return projectLocalConfig.getLeftContext();
	}

	public void setLeftContext(int n) {
		projectLocalConfig.setLeftContext(n);
	}

	public int getRightContext() {
		return projectLocalConfig.getRightContext();
	}

	public void setRightContext(int n) {
		projectLocalConfig.setRightContext(n);
	}

	public boolean isLeftStopAtS() {
		return projectLocalConfig.isLeftStopAtS();
	}

	public void setLeftStopAtS(boolean b) {
		projectLocalConfig.setLeftStopAtS(b);
	}

	public boolean isRightStopAtS() {
		return projectLocalConfig.isRightStopAtS();
	}

	public void setRightStopAtS(boolean b) {
		projectLocalConfig.setRightStopAtS(b);
	}

	public ConcordanceSortType getConcordanceSortType() {
		return projectLocalConfig.getConcordanceSortType();
	}

	public void setConcordanceSortType(ConcordanceSortType t) {
		projectLocalConfig.setConcordanceSortType(t);
	}

	public FileOperationType getResultType() {
		return projectLocalConfig.getResultType();
	}

	public void setResultType(FileOperationType type) {
		projectLocalConfig.setResultType(type);
	}

	public ConcordanceOperationType getBuildConcordanceType() {
		return projectLocalConfig.getBuildConcordanceType();
	}

	public void setBuildConcordanceType(ConcordanceOperationType type) {
		projectLocalConfig.setBuildConcordanceType(type);
	}

	public File getResultOutputFile() {
		return projectLocalConfig.getResultOutputFile();
	}

	public void setResultOutputFile(File f) {
		projectLocalConfig.setResultOutputFile(f);
	}

	public ExtractMatchType getExtractMatchType() {
		return projectLocalConfig.getExtractMatchType();
	}

	public void setExtractMatchType(ExtractMatchType type) {
		projectLocalConfig.setExtractMatchType(type);
	}

	public MvnSourceConfig getMvnSourceConfig() {
		return projectVersionableConfig.getMvnSourceConfig();
	}

	public void setMvnSourceConfig(MvnSourceConfig m) {
		projectVersionableConfig.setMvnSourceConfig(m);
	}

	public MvnBuildConfig getMvnBuildConfig() {
		return projectVersionableConfig.getMvnBuildConfig();
	}

	public void setMvnBuildConfig(MvnBuildConfig m) {
		projectVersionableConfig.setMvnBuildConfig(m);
	}

	public boolean isPreviousConcordance() {
		File dir = Config.getCurrentSnt();
		if (dir == null)
			return false;
		dir = Config.getCurrentSntDir();
		if (dir == null || !dir.exists())
			return false;
		return new File(dir, "previous-concord.ind").exists();
	}

	public void openFileResult(File txt) {
		File f = getTextEditor();
		if (getBuildResultDisplay() == ResultDisplay.TEXT_EDITOR && f != null) {
			Launcher.execExternalCommand(f.getAbsolutePath(),
					txt.getAbsolutePath());
			return;
		}
		f = getHtmlViewer();
		if (getBuildResultDisplay() == ResultDisplay.HTML_VIEWER && f != null) {
			Launcher.execExternalCommand(f.getAbsolutePath(),
					txt.getAbsolutePath());
			return;
		}
		/*
		 * Important to use null and not txt: if txt is a file outside the
		 * project directory, it would result in txt not to be opened
		 */
		GlobalProjectManager.search(null)
			.getFrameManagerAs(InternalFrameManager.class)
			.newFileEditionTextFrame(txt);
	}

	public void openHtmlFile(File html) {
		File f = getHtmlViewer();
		if (f == null)
			return;
		Launcher.execExternalCommand(f.getAbsolutePath(),
				html.getAbsolutePath());
	}

	public void openConcordanceFile(File concord) {
		File f = null;
		switch (getConcordanceDisplay()) {
		case HTML_VIEWER:
			f = getHtmlViewer();
			break;
		case TEXT_EDITOR:
			f = getTextEditor();
			break;
		case INTERNAL_FRAME:
			break;
		}
		if (f != null) {
			Launcher.execExternalCommand(f.getAbsolutePath(),
					concord.getAbsolutePath());
			return;
		}
		GlobalProjectManager.search(concord)
			.getFrameManagerAs(InternalFrameManager.class)
			.newConcordanceFrame(concord, 100);
	}

	public void openDiffHtmlFile(File concord) {
		File f = null;
		switch (getConcordanceDisplay()) {
		case HTML_VIEWER:
			f = getHtmlViewer();
			break;
		default:
			break;
		}
		if (f != null) {
			Launcher.execExternalCommand(f.getAbsolutePath(),
					concord.getAbsolutePath());
			return;
		}
		GlobalProjectManager.search(concord)
			.getFrameManagerAs(InternalFrameManager.class)
			.newConcordanceDiffFrame(concord);
	}

	public void openDicFile(File dic) {
		File f = getTextEditor();
		if (f != null && useTextEditorForDictionaries()) {
			Launcher.execExternalCommand(f.getAbsolutePath(),
					dic.getAbsolutePath());
			return;
		}
		GlobalProjectManager.search(dic)
			.getFrameManagerAs(InternalFrameManager.class).newDelaFrame(dic);
	}

	public void openFile(File file, boolean forceTextEditor) {
		if (!forceTextEditor) {
			if (isGrfFile(file)) {
				GlobalProjectManager.search(file)
					.getFrameManagerAs(InternalFrameManager.class).newGraphFrame(file);
				GlobalProjectManager.getAs(GramlabProjectManager.class).setCurrentProject(this);
				return;
			}
			if (file.getName().endsWith(".dic")) {
				openDicFile(file);
				GlobalProjectManager.getAs(GramlabProjectManager.class).setCurrentProject(this);
				return;
			}
			if (file.getName().endsWith(".snt")) {
				setCurrentCorpus(file, true);
				GlobalProjectManager.getAs(GramlabProjectManager.class).setCurrentProject(this);
				return;
			}
			if (file.getName().endsWith(".html")) {
				openHtmlFile(file);
				GlobalProjectManager.getAs(GramlabProjectManager.class).setCurrentProject(this);
				return;
			}
		}
		if (Encoding.getEncoding(file) == null) {
			JOptionPane.showMessageDialog(null,
					"File " + file.getAbsolutePath()
							+ " is not a UTF8/UTF16 one", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		File f = getTextEditor();
		if (f != null) {
			Launcher.execExternalCommand(f.getAbsolutePath(),
					file.getAbsolutePath());
			return;
		}
		InternalFrameManager manager = GlobalProjectManager.search(file).getFrameManagerAs(InternalFrameManager.class);
		List<InternalFileEditor> editors = pluginManager.getExtensions(InternalFileEditor.class);
		if (manager != null && !editors.isEmpty()) {
			manager.newFileEditionTextFrame(file);
			GlobalProjectManager.getAs(GramlabProjectManager.class).setCurrentProject(this);
		}
	}

	static Pattern patternGrfInConflict = Pattern
			.compile(".*\\.grf\\.r([0-9]+)");

	private boolean isGrfFile(File file) {
		String name = file.getName();
		if (name.endsWith(".grf") || name.endsWith(".grf.mine")
				|| patternGrfInConflict.matcher(name).matches()) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<File> getLastResultFiles() {
		return (ArrayList<File>) projectLocalConfig.getLastResultFiles()
				.clone();
	}

	@SuppressWarnings("unchecked")
	public void setLastResultFiles(ArrayList<File> files) {
		projectLocalConfig.setLastResultFiles((ArrayList<File>) files.clone());
	}

	private File currentCorpus = null;
	private File currentSntDir = null;

	public File getCurrentCorpus() {
		return currentCorpus;
	}

	public File getCurrentSntDir() {
		return currentSntDir;
	}

	private final InternalFrameAdapter adapter = new InternalFrameAdapter() {
		@Override
		public void internalFrameClosed(InternalFrameEvent e) {
			setCurrentCorpus(null, false);
		}
	};

	private boolean setCorpusInProgress = false;

	void openSntFrame(boolean currentCorpusChanged) {
		if (currentCorpus == null)
			return;
		File file = currentCorpus;
		if (FileUtil.getExtensionInLowerCase(currentCorpus).equalsIgnoreCase(
				"txt")) {
			file = FileUtil.getSnt(currentCorpus);
			if (!file.exists()) {
				if (!currentCorpusChanged) {
					/*
					 * If the user has selected a .txt with no .snt associated,
					 * it must not be considered an error not to find the .snt
					 */
					JOptionPane.showMessageDialog(null,
							"File " + file.getAbsolutePath()
									+ " does not exist.", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					/* We just close the frame */
					GlobalProjectManager.search(file)
						.getFrameManagerAs(InternalFrameManager.class)
						.closeTextFrame();
				}
				return;
			}
		}
		Text.loadSnt(file, false);
		TextFrame f = GlobalProjectManager.search(file)
			.getFrameManagerAs(InternalFrameManager.class)
			.getTextFrame();
		f.addInternalFrameListener(adapter);
	}

	public void setCurrentCorpus(File file, boolean showTextFrame) {
		if (setCorpusInProgress) {
			/*
			 * We don't want a null set due to a text frame close operation
			 * while we are about to load a new text frame
			 */
			return;
		}
		setCorpusInProgress = true;
		try {
			this.currentCorpus = file;
			this.currentSntDir = (file == null) ? null : FileUtil
					.getSntDir(file);
			if (showTextFrame && file != null) {
				openSntFrame(true);
			}
			fireCurrentCorpusChanged(file);
		} finally {
			setCorpusInProgress = false;
		}
	}

	private ArrayList<CorpusListener> listeners = new ArrayList<CorpusListener>();
	private boolean firing = false;

	protected void fireCurrentCorpusChanged(File f) {
		try {
			firing = true;
			for (CorpusListener l : listeners) {
				l.currentCorpusChanged(f);
			}
		} finally {
			firing = false;
		}
	}

	public void addCurrentCorpusListener(CorpusListener l) {
		listeners.add(l);
	}

	public void removeCurrentCorpusListener(CorpusListener l) {
		if (firing) {
			throw new IllegalStateException(
					"Cannot remove a listener while firing");
		}
		listeners.remove(l);
	}

	private ProcessPane processPane = null;

	public ProcessPane getProcessPane() {
		if (processPane == null) {
			processPane = new ProcessPane(this);
		}
		return processPane;
	}

	/**
	 * This method takes a txt file and applies to it the whole preprocessing as
	 * defined in the project configuration. If the preprocessing is a success,
	 * the resulting snt becomes the current one.
	 */
	public MultiCommands preprocessText(File txt) {
		MultiCommands commands = new MultiCommands();
		commands.addCommand(getNormalizeCommand(txt));
		commands.addCommand(createSntDir(txt));
		final File snt = FileUtil.getSnt(txt);
		commands.addCommand(getPreprocessing().getDeployCommands(this));
		commands.addCommand(getPreprocessing().getPreprocessCommands(this, snt));
		commands.addCommand(createTokenizeCommand(snt));
		commands.addCommand(createDicoCommand(snt));
		return commands;
	}

	private MultiCommands createDicoCommand(File snt) {
		if (!applyDictionaries() || getDictionaries().size() == 0)
			return null;
		MultiCommands commands = new MultiCommands();
		DicoCommand c = new DicoCommand().snt(snt).alphabet(getAlphabet())
				.morphologicalDic(getMorphoDics())
				.dictionaryList(getDictionaries());
		if (isSemitic())
			c = c.semitic();
		if (isKorean())
			c = c.korean();
		if (getArabicTypoRules() != null) {
			c = c.arabic(getArabicTypoRules());
		}
		monitor(c);
		commands.addCommand(c);
		File sntDir = FileUtil.getSntDir(snt);
		SortTxtCommand sort1 = new SortTxtCommand()
				.file(new File(sntDir, "dlf")).sortAlphabet(getSortAlphabet())
				.thai(ConfigManager.getManager().isThai(getName()));
		commands.addCommand(sort1);
		SortTxtCommand sort2 = new SortTxtCommand()
				.file(new File(sntDir, "dlc")).sortAlphabet(getSortAlphabet())
				.thai(ConfigManager.getManager().isThai(getName()));
		commands.addCommand(sort2);
		SortTxtCommand sort3 = new SortTxtCommand()
				.file(new File(sntDir, "err")).sortAlphabet(getSortAlphabet())
				.thai(ConfigManager.getManager().isThai(getName()));
		commands.addCommand(sort3);
		return commands;
	}

	private TokenizeCommand createTokenizeCommand(File snt) {
		TokenizeCommand c = new TokenizeCommand();
		c = c.text(snt).alphabet(getAlphabet())
				.tokenizeCharByChar(isCharByChar());
		monitor(c);
		return c;
	}

	public Grf2Fst2Command createGrf2Fst2Command(File grf, File fst2,
			boolean debug, boolean monitor) {
		boolean inInflectionDir = false;
		File dir = getInflectionDirectory();
		if (dir.exists() && null != FileUtil.isAncestor(dir, grf)) {
			inInflectionDir = true;
		}
		Grf2Fst2Command c = new Grf2Fst2Command()
				.grf(grf)
				.enableLoopAndRecursionDetection(true)
				.repositories()
				.emitEmptyGraphWarning(
						!inInflectionDir && emitEmptyGraphWarning())
				.displayGraphNames(displayGraphNames())
				.checkVariables(!inInflectionDir)
				.strictTokenization(!inInflectionDir && strictTokenization());
		if (isCharByChar()) {
			c = c.charByCharTokenization();
		} else {
			c = c.alphabetTokenization(getAlphabet());
		}
		if (fst2 != null) {
			c = c.output(fst2);
		}
		if (debug) {
			c = c.debug();
		}
		if (monitor) {
			monitor(c);
		}
		return c;
	}

	private CommandBuilder createSntDir(File txt) {
		return new MkdirCommand().name(FileUtil.getSntDir(txt));
	}

	public void monitor(final CommandBuilder c) {
		monitor(c, null);
	}

	public void monitor(final CommandBuilder c, final File regexpFst2) {
		final File f = new File(getProjectDirectory(), ConsoleUtil.TIME_PFX
				+ System.currentTimeMillis());
		final GramlabProject p = this;
		c.time(f);
		c.setWhatToDoBefore(new ToDoBeforeSingleCommand() {
			@Override
			public void toDo(ConsoleEntry entry) {
				consoleUtil.doBeforeMonitoring(p, c);
			}
		});
		c.setWhatToDoOnceCompleted(new ToDoAfterSingleCommand() {
			@Override
			public void toDo(boolean success, ConsoleEntry entry) {
				consoleUtil.doAfterMonitoring(p, success, entry, c, f);
				if (regexpFst2 != null) {
					/*
					 * If we performed a Locate operation with a regular
					 * expression, we remove the regex* files
					 */
					File f = new File(FileUtil
							.getFileNameWithoutExtension(regexpFst2) + ".*");
					FileUtil.removeFile(f);
				}
			}
		});
	}

	private NormalizeCommand getNormalizeCommand(File txt) {
		NormalizeCommand c = new NormalizeCommand();
		c = c.text(txt).separatorNormalization(separatorNormalization())
				.normFile(getNormTxt());
		monitor(c);
		return c;
	}

	private File tmpCorpusFile;

	public File getTmpCorpusFile() {
		if (tmpCorpusFile == null) {
			tmpCorpusFile = new File(getCorpusDirectory(), ".tmp.txt");
		}
		return tmpCorpusFile;
	}

	public MultiCommands getLocateCommands(File snt) {
		MultiCommands cmds = new MultiCommands();
		File fst2;
		boolean regex = false;
		if (isLastPatternRegexp()) {
			regex = true;
			File regexp = new File(getSrcDirectory(), "regexp.txt");
			FileUtil.write(getLastRegexp(), regexp);
			cmds.addCommand(getReg2GrfCommand(regexp));
			File grf = new File(FileUtil.getFileNameWithoutExtension(regexp)
					+ ".grf");
			fst2 = new File(FileUtil.getFileNameWithoutExtension(regexp)
					+ ".fst2");
			if (isDebugMode())
				JOptionPane
						.showMessageDialog(
								null,
								"The debug mode has no effect when using a regular expression.",
								"Warning", JOptionPane.WARNING_MESSAGE);
			cmds.addCommand(createGrf2Fst2Command(grf, null, false, true));
		} else {
			File grf = getLastGraphs().get(0);
			if (FileUtil.getExtensionInLowerCase(grf).equals("grf")) {
				fst2 = new File(FileUtil.getFileNameWithoutExtension(grf)
						+ ".fst2");
				cmds.addCommand(createGrf2Fst2Command(grf, null, isDebugMode(),
						true));
			} else if (FileUtil.getExtensionInLowerCase(grf).equals("fst2")) {
				fst2 = grf;
			} else {
				throw new IllegalStateException(
						"Should not have a file that is neither .grf nor .fst2:\n"
								+ grf.getAbsolutePath());
			}
		}
		cmds.addCommand(createLocateCommand(snt, fst2, regex));
		return cmds;
	}

	private LocateCommand createLocateCommand(File snt, File fst2, boolean regex) {
		LocateCommand c = new LocateCommand().snt(snt).fst2(fst2)
				.alphabet(getAlphabet())
				.setInjectedVariables(getInjectedVariables());
		if (getArabicTypoRules() != null) {
			c = c.arabic(getArabicTypoRules());
		}
		switch (getMatchesPolicy()) {
		case ALL:
			c = c.allMatches();
			break;
		case SHORTEST:
			c = c.shortestMatches();
			break;
		case LONGEST:
			c = c.longestMatches();
			break;
		}
		switch (getOutputsPolicy()) {
		case IGNORE:
			c = c.ignoreOutputs();
			break;
		case MERGE:
			c = c.mergeOutputs();
			break;
		case REPLACE:
			c = c.replaceWithOutputs();
			break;
		}
		if (getSearchLimit() == -1)
			c = c.noLimit();
		else
			c = c.limit(getSearchLimit());
		if (isCharByChar())
			c = c.charByChar();
		c = c.morphologicalDic(getMorphoDics());
		if (isMorphologicalUseOfSpace()) {
			c = c.enableMorphologicalUseOfSpace();
		}
		if (isAmbiguousOutputsAllowed()) {
			c = c.allowAmbiguousOutputs();
		} else {
			c = c.forbidAmbiguousOutputs();
		}
		switch (getVariableErrorPolicy()) {
		case IGNORE:
			c = c.ignoreVariableErrors();
			break;
		case EXIT:
			c = c.exitOnVariableErrors();
			break;
		case BACKTRACK:
			c = c.backtrackOnVariableErrors();
			break;
		}
		if (isKorean())
			c = c.korean();
		File fRegex = regex ? fst2 : null;
		monitor(c, fRegex);
		return c;
	}

	private Reg2GrfCommand getReg2GrfCommand(File f) {
		Reg2GrfCommand c = new Reg2GrfCommand().file(f);
		monitor(c);
		return c;
	}

	public MultiCommands getResultCommands(File snt) {
		MultiCommands cmds = new MultiCommands();
		switch (getResultType()) {
		case MODIFY_TEXT:
			cmds.addCommand(createModifyTextCommand(snt));
			break;
		case EXTRACT_MATCHING_UNITS:
		case EXTRACT_UNMATCHING_UNITS:
			cmds.addCommand(createExtractCommand(snt));
			break;
		case EXTRACT_MATCHES:
			cmds.addCommand(createExtractMatchesCommand(snt));
			break;
		}
		return cmds;
	}

	public MultiCommands getConcordanceCommands(File snt) {
		MultiCommands cmds = new MultiCommands();
		switch (getBuildConcordanceType()) {
		case SHOW_DIFFERENCES_WITH_PREVIOUS_CONCORDANCE:
			cmds.addCommand(createConcorDiffCommand(snt));
			break;
		case SHOW_AMBIGUOUS_OUTPUTS:
			cmds.addCommand(createConcordCommand(snt, true));
			break;
		case BUILD_CONCORDANCE:
			cmds.addCommand(createConcordCommand(snt, false));
			break;
		}
		return cmds;
	}

	private ConcorDiffCommand createConcorDiffCommand(File snt) {
		File previous = new File(FileUtil.getSntDir(snt),
				"previous-concord.ind");
		File ind = new File(FileUtil.getSntDir(snt), "concord.ind");
		ConcorDiffCommand c = new ConcorDiffCommand()
				.firstIndFile(previous)
				.secondIndFile(ind)
				.diffOnly()
				.output(getDiffOutputHtml(snt))
				.font(ConfigManager.getManager().getConcordanceFontName(null))
				.fontSize(
						ConfigManager.getManager().getConcordanceFontSize(null));
		monitor(c);
		return c;
	}

	public File getDiffOutputHtml(File snt) {
		return new File(FileUtil.getSntDir(snt), "diff.html");
	}

	private ExtractCommand createExtractCommand(File snt) {
		File ind = new File(FileUtil.getSntDir(snt), "concord.ind");
		ExtractCommand c = new ExtractCommand()
				.snt(snt)
				.extract(
						getResultType() == FileOperationType.EXTRACT_MATCHING_UNITS)
				.ind(ind).result(getResultOutputFile());
		monitor(c);
		return c;
	}

	private ConcordCommand createModifyTextCommand(File snt) {
		File ind = new File(FileUtil.getSntDir(snt), "concord.ind");
		ConcordCommand c = new ConcordCommand().indFile(ind)
				.outputModifiedTxtFile(getResultOutputFile());
		monitor(c);
		return c;
	}

	private ConcordCommand createConcordCommand(File snt, boolean onlyAmbiguous) {
		File ind = new File(FileUtil.getSntDir(snt), "concord.ind");
		ConcordCommand c = new ConcordCommand().indFile(ind);
		switch (getConcordanceType()) {
		case TEXT:
			c = c.text(null);
			break;
		case HTML: {
			c = c.html()
					.font(ConfigManager.getManager().getConcordanceFontName(
							null))
					.fontSize(
							ConfigManager.getManager().getConcordanceFontSize(
									null));
			break;
		}
		case AXIS:
			c = c.axis();
			break;
		case INDEX:
			c = c.index();
			break;
		case XML:
			c = c.xml();
			break;
		}
		try {
			c = c.order(getConcordanceSortType().getIntValue());
		} catch (InvalidConcordanceOrderException e) {
			return null;
		}
		if (onlyMatches())
			c = c.onlyMatches(true);
		else {
			c = c.left(getLeftContext(), isLeftStopAtS());
			c = c.right(getRightContext(), isRightStopAtS());
		}
		if (onlyAmbiguous)
			c = c.onlyAmbiguous();
		c.thai(getLanguage().equals(Language.th));
		monitor(c);
		return c;
	}

	private MultiCommands createExtractMatchesCommand(File snt) {
		MultiCommands cmds = new MultiCommands();
		File ind = new File(FileUtil.getSntDir(snt), "concord.ind");
		ConcordCommand c;
		try {
			c = new ConcordCommand().indFile(ind).onlyMatches(true)
					.text(getResultOutputFile()).order(0);
		} catch (InvalidConcordanceOrderException e) {
			return null;
		}
		cmds.addCommand(c);
		if (getExtractMatchType() == ExtractMatchType.SORTED_ALL
				|| getExtractMatchType() == ExtractMatchType.SORTED_NO_DUPLICATES) {
			SortTxtCommand sort = new SortTxtCommand()
					.file(getResultOutputFile())
					.removeDuplicates(
							getExtractMatchType() == ExtractMatchType.SORTED_NO_DUPLICATES)
					.sortAlphabet(getSortAlphabet())
					.thai(getLanguage().equals(Language.th));
			cmds.addCommand(sort);
		}
		return cmds;
	}

	public ConsolePanel getConsolePanel() {
		return consolePanel;
	}

	private HashMap<File, SvnInfo> map = null;
	private SvnInfo previousPomXmlSvnInfo = null;
	private boolean firstTimeSvnInfo = true;
	private ArrayList<File> removedFiles = new ArrayList<File>();

	public SvnInfo getSvnInfo(File file) {
		if (map == null)
			return null;
		return map.get(file);
	}

	public SplitUtil getSplitUtil() {
		return splitUtil;
	}

	public void setLastCorpusWasAFile(boolean b) {
		projectLocalConfig.setLastCorpusWasAFile(b);
	}

	public boolean isLastCorpusAFile() {
		return projectLocalConfig.isLastCorpusAFile();
	}

	public boolean mustDoPreprocessing() {
		return projectLocalConfig.mustDoPreprocessing();
	}

	public void setDoPreprocessing(boolean b) {
		projectLocalConfig.setDoPreprocessing(b);
	}

	public boolean mustDoLocate() {
		return projectLocalConfig.mustDoLocate();
	}

	public void setDoLocate(boolean b) {
		projectLocalConfig.setDoLocate(b);
	}

	public boolean mustBuildResult() {
		return projectLocalConfig.mustBuildResult();
	}

	public void setBuildResult(boolean b) {
		projectLocalConfig.setBuildResult(b);
	}

	public boolean mustBuildConcordance() {
		return projectLocalConfig.mustBuildConcordance();
	}

	public void setBuildConcordance(boolean b) {
		projectLocalConfig.setBuildConcordance(b);
	}

	public void saveOpenFrames(JInternalFrame[] frames) {
		if (projectLocalConfig != null) {
			projectLocalConfig.saveOpenFrames(frames);
		}
	}

	public ArrayList<File> getOpenFrames() {
		return projectLocalConfig.getOpenFrames();
	}

	public boolean pomXmlWasUpdated() {
		SvnInfo current = getSvnInfo(POM.getFile());
		if (previousPomXmlSvnInfo == null) {
			return current != null;
		}
		if (current == null) {
			/* Strange ?! */
			return true;
		}
		return !previousPomXmlSvnInfo.equals(current);
	}

	private SvnMonitor monitor = null;

	public SvnMonitor getSvnMonitor() {
		if (monitor == null) {
			monitor = new SvnMonitor(getSrcDirectory(), false);
		}
		return monitor;
	}

	private ArrayList<File> pool = new ArrayList<File>();

	boolean lock = false;

	public void asyncUpdateSvnInfo(final ArrayList<File> forceRefresh, boolean forceAll) {
		final ProjectNode projectNode = WorkspaceTreeModel.getModel()
				.getProjectNode(this);
		if (lock) {
			/*
			 * If there is already a refresh operation in progress, we don't
			 * want to lose the pending items to be refreshed
			 */
			if (forceRefresh != null) {
				synchronized (pool) {
					for (File f : forceRefresh) {
						if (!pool.contains(f))
							pool.add(f);
					}
				}
			}
			return;
		}
		/* If there are some pending items, we take them */
		final ArrayList<File> itemsToRefresh = new ArrayList<File>();
		if (forceRefresh != null) {
			for (File f : forceRefresh) {
				itemsToRefresh.add(f);
			}
		}
		synchronized (pool) {
			for (File f : pool) {
				if (!itemsToRefresh.contains(f)) {
					itemsToRefresh.add(f);
				}
			}
			pool.clear();
		}
		if (map != null) {
			previousPomXmlSvnInfo = map.get(POM.getFile());
		}
		lock = true;
		final SvnInfo backup;
		if (map != null) {
			backup = map.get(POM.getFile());
		} else {
			backup = null;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				final ArrayList<File> tmp = new ArrayList<File>();
				final HashMap<File, SvnInfo> newMap = SvnExecutor.getSvnInfos(
						GramlabProject.this, tmp);
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
						/*
						 * As we did an asynchronous job, we have to post the
						 * update in the Swing thread
						 */
						previousPomXmlSvnInfo = backup;
						map = newMap;
						if (firstTimeSvnInfo) {
							// On the very first call, map is null, but we don't
							// want to consider pom.xml as unversioned if it is,
							// so, we have a special case to deal with
							previousPomXmlSvnInfo = map.get(POM.getFile());
							firstTimeSvnInfo = false;
						}
						removedFiles = tmp;
						} finally {
							lock = false;
							if (projectNode != null) {
								projectNode.refresh(removedFiles, itemsToRefresh, false);
								Main.getMainFrame().repaint();
							}
						}
					}
				});
			}
		}).start();
	}

	public ArrayList<File> getRemovedFiles() {
		return removedFiles;
	}

	public void refreshConfigFiles() {
		SvnConflict c1 = SvnConflict.getConflict(getPreferencesFile());
		SvnConflict c2 = SvnConflict
				.getConflict(getProjectVersionableConfigFile());
		if (c1 != null || c2 != null) {
			StringBuilder b = new StringBuilder();
			b.append("There is a SVN conflict one the following configuration file(s):\n\n");
			if (c1 != null) {
				b.append(getPreferencesFile().getName() + "\n");
			}
			if (c2 != null) {
				b.append(getProjectVersionableConfigFile().getName() + "\n");
			}
			b.append("\nThe local version of the concerned file(s) will be\n");
			b.append("used until the conflict is solved.\n");
			JOptionPane.showMessageDialog(null, b.toString(), "SVN conflict",
					JOptionPane.WARNING_MESSAGE);
		}
		loadConfigurationFiles();
		fireConfigurationChanged();
	}

	private ArrayList<ConfigurationListener> configListeners = new ArrayList<ConfigurationListener>();
	private boolean firingProject = false;

	public void addConfigurationListener(ConfigurationListener l) {
		configListeners.add(l);
	}

	public void removeConfigurationListener(ConfigurationListener l) {
		if (firingProject) {
			throw new IllegalStateException(
					"Cannot remove a listener while firing");
		}
		configListeners.remove(l);
	}

	protected void fireConfigurationChanged() {
		firingProject = true;
		try {
			for (int i = 0; i < configListeners.size(); i++) {
				configListeners.get(i).configurationChanged();
			}
		} finally {
			firingProject = false;
		}
	}

	/**
	 * This method tests whether the given directory contains material required
	 * by the preprocessing, such as graphs or dictionaries.
	 */
	public boolean isRequiredByPreprocessing(File dir) {
		Preprocessing p = getPreprocessing();
		for (PreprocessingStep step : p.getPreprocessingSteps()) {
			if (null != FileUtil.isAncestor(dir, step.getGraph()))
				return true;
		}
		for (File f : getDictionaries()) {
			if (null != FileUtil.isAncestor(dir, f))
				return true;
		}
		return false;
	}

	public String getSvnRepositoryUrl() {
		String info = SvnExecutor.getCommandOutput(new SvnCommand().info(
				getProjectDirectory(), false, true));
		if (info == null)
			return null;
		Scanner s = new Scanner(info);
		while (s.hasNextLine()) {
			String tmp = s.nextLine();
			if (!tmp.startsWith("URL: "))
				continue;
			return tmp.substring(tmp.indexOf(' ')).trim();
		}
		return null;
	}

	public void ensureDirectoriesExist() {
		getCorpusDirectory().mkdir();
		getDelaDirectory().mkdir();
		getInflectionDirectory().mkdir();
		getGraphsDirectory().mkdir();
		getPreprocessingDirectory().mkdir();
		getSentenceDirectory().mkdir();
		getReplaceDirectory().mkdir();
	}

	public boolean strictTokenization() {
		return projectVersionableConfig.strictTokenization();
	}

	public void setStrictTokenization(boolean b) {
		projectVersionableConfig.setStrictTokenization(b);
	}

	public boolean isMvnSourcePackage() {
		return projectVersionableConfig.isMvnSourcePackage();
	}

	public void setMvnSourcePackage(boolean b) {
		projectVersionableConfig.setMvnSourcePackage(b);
	}

	public boolean isMvnBuildPackage() {
		return projectVersionableConfig.isMvnBuildPackage();
	}

	public void setMvnBuildPackage(boolean b) {
		projectVersionableConfig.setMvnBuildPackage(b);
	}

	public PackageOperation getPackageOperation() {
		return projectVersionableConfig.getPackageOperation();
	}

	public void setPackageOperation(PackageOperation op) {
		projectVersionableConfig.setPackageOperation(op);
	}

}
