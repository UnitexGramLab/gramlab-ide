package org.gramlab.core.gramlab.project.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.swing.JInternalFrame;

import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.gramlab.project.config.buildfile.ExtractMatchType;
import org.gramlab.core.gramlab.project.config.buildfile.FileOperationType;
import org.gramlab.core.gramlab.project.config.concordance.ConcordanceOperationType;
import org.gramlab.core.gramlab.project.config.concordance.ConcordanceSortType;
import org.gramlab.core.gramlab.project.config.concordance.ConcordanceType;
import org.gramlab.core.gramlab.project.config.concordance.ResultDisplay;
import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.frames.GraphFrame;
import org.gramlab.core.umlv.unitex.frames.TextFrame;

public class ProjectLocalConfig extends AbstractProjectConfig {
	
	private GramlabProject project;
	
																
	/**
	 * LOCATE CONFIGURATION
	 */
	private final static String LAST_PATTERN_WAS_A_REGEXP="LAST_PATTERN_WAS_A_REGEXP";
																private boolean lastPatternRegexp;
	private final static String LAST_REGEXP="LAST_REGEXP";		private String lastRegexp;
	private final static String LAST_GRAPH="LAST_GRAPH";		private ArrayList<File> lastGraphs;
	private final static int N_LAST_GRAPHS=10;
	private final static String LAST_GRAPH_DIR="LAST_GRAPH_DIR";private File lastGraphDir;
	private final static String DEBUG="DEBUG";					private boolean debug;
	private final static String LAST_CORPUS_WAS_A_FILE="LAST_CORPUS_WAS_A_FILE";		
				private  boolean lastCorpusWasAFile;
	private final static String LAST_SNT="LAST_SNT";		private ArrayList<File> lastSnt;
	private final static int N_LAST_SNT=10;
	private final static String LAST_SNT_DIR="LAST_SNT_DIR";private File lastSntDir;

	/**
	 * GRAPH COMPILATION CONFIGURATION
	 */
	private final static String EMIT_EMPTY_GRAPH_WARNING="EMIT_EMPTY_GRAPH_WARNING";	
			private boolean emitEmptyGraphWarning;
	private final static String DISPLAY_GRAPH_NAMES="DISPLAY_GRAPH_NAMES";	
			private boolean displayGraphNames;

	/**
	 * MISC
	 */
	private final static String HTML_VIEWER="HTML_VIEWER";	private File htmlViewer;
	private final static String TEXT_EDITOR="TEXT_EDITOR";	private File textEditor;
	private final static String USE_TEXT_EDITOR_FOR_DICS="USE_TEXT_EDITOR_FOR_DICS";	
			private boolean useTextEditorForDictionaries;

	/**
	 * CONCORDANCE
	 */
	private final static String CONCORDANCE_DISPLAY="CONCORDANCE_DISPLAY";	
			private ResultDisplay concordanceDisplay;
	private final static String CONCORDANCE_TYPE="CONCORDANCE_TYPE";	
			private ConcordanceType concordanceType;
	private final static String ONLY_MATCHES="ONLY_MATCHES";
			private boolean onlyMatches;
	private final static String LEFT_CONTEXT="LEFT_CONTEXT";
			private int leftContext;
	private final static String RIGHT_CONTEXT="RIGHT_CONTEXT";
			private int rightContext;
	private final static String LEFT_STOP_AT_S="LEFT_STOP_AT_S";
			private boolean leftStopAtS;
	private final static String RIGHT_STOP_AT_S="RIGHT_STOP_AT_S";
			private boolean rightStopAtS;
	private final static String SORT_ORDER="SORT_ORDER";	
			private ConcordanceSortType concordanceSortType;
	private final static String RESULT_TYPE="RESULT_TYPE";	
			private FileOperationType resultType;
	private final static String RESULT_OUTPUT_FILE="RESULT_OUTPUT_FILE";	
			private File resultOutputFile;
	private final static String EXTRACT_MATCH_TYPE="EXTRACT_MATCH_TYPE";	
			private ExtractMatchType extractMatchType;
	private final static String CONCORDANCE_OPERATION_TYPE="CONCORDANCE_OPERATION_TYPE";	
			private ConcordanceOperationType concordanceOperationType;
	private final static String FILE_OPERATION_DISPLAY="FILE_OPERATION_DISPLAY";	
			private ResultDisplay fileOperationDisplay;

	/**
	 * PROJECT STATE
	 */
	private final static String DO_PREPROCESSING="DO_PREPROCESSING";	
	private boolean doPreprocessing;
	private final static String DO_LOCATE="DO_LOCATE";	
	private boolean doLocate;
	private final static String BUILD_RESULT="BUILD_RESULT";	
	private boolean buildResult;
	private final static String BUILD_CONCORDANCE="BUILD_CONCORDANCE";	
	private boolean buildConcordance;
	private final static String N_OPEN_FRAMES="N_OPEN_FRAMES";
	private final static String OPEN_FRAMES="OPEN_FRAMES";
	private ArrayList<File> openFrames;
			
	private final static String LAST_RESULT_FILE="LAST_RESULT_FILE";
	private ArrayList<File> lastResultFiles;
	private final static int N_LAST_RESULT_FILE=10;

	
	public ProjectLocalConfig(GramlabProject p) {
		this.project=p;
		this.lastPatternRegexp=true;
		this.lastRegexp="";
		this.lastGraphs=new ArrayList<File>();
		this.lastGraphDir=p.getGraphsDirectory();
		this.debug=false;
		this.lastCorpusWasAFile=false;
		this.lastSnt=new ArrayList<File>();
		this.lastSntDir=null;
		this.emitEmptyGraphWarning=true;
		this.displayGraphNames=true;
		this.htmlViewer=null;
		this.textEditor=null;
		this.useTextEditorForDictionaries=false;
		this.concordanceDisplay=ResultDisplay.INTERNAL_FRAME;
		this.concordanceType=ConcordanceType.HTML;
		this.onlyMatches=false;
		this.leftContext=40;
		this.rightContext=55;
		this.leftStopAtS=false;
		this.rightStopAtS=false;
		this.concordanceSortType=ConcordanceSortType.CL;
		this.resultType=FileOperationType.EXTRACT_MATCHES;
		this.resultOutputFile=null;
		this.extractMatchType=ExtractMatchType.TEXT_ORDER;
		this.concordanceOperationType=ConcordanceOperationType.BUILD_CONCORDANCE;
		this.fileOperationDisplay=ResultDisplay.INTERNAL_FRAME;
		this.doPreprocessing=false;
		this.doLocate=false;
		this.buildResult=false;
		this.buildConcordance=false;
		this.openFrames=new ArrayList<File>();
		this.lastResultFiles=new ArrayList<File>();
	}
	
	public static ProjectLocalConfig load(GramlabProject p,File f) {
		ProjectLocalConfig config=new ProjectLocalConfig(p);
		try {
		FileInputStream stream;
		stream = new FileInputStream(f);
		InputStreamReader reader0=new InputStreamReader(stream,"UTF-8");
		BufferedReader s=new BufferedReader(reader0);
		/* LOCATE */
		config.lastPatternRegexp=readBoolean(LAST_PATTERN_WAS_A_REGEXP,s);
		config.lastRegexp=new StringProperty(LAST_REGEXP,null).load(s);
		for (int i=1;i<=N_LAST_GRAPHS;i++) {
			File f2=readFile(p,LAST_GRAPH+i,s);
			if (f2!=null) {
				config.lastGraphs.add(f2);
			}
		}
		config.lastGraphDir=readFile(p,LAST_GRAPH_DIR,s);
		config.debug=readBoolean(DEBUG,s);
		config.lastCorpusWasAFile=readBoolean(LAST_CORPUS_WAS_A_FILE,s);
		for (int i=1;i<=N_LAST_SNT;i++) {
			File f2=readFile(p,LAST_SNT+i,s);
			if (f2!=null) {
				config.lastSnt.add(f2);
			}
		}
		config.lastSntDir=readProjectRelativeFile(p,LAST_SNT_DIR,s);
		/* GRAPH COMPILATION */
		config.emitEmptyGraphWarning=readBoolean(EMIT_EMPTY_GRAPH_WARNING,s);
		config.displayGraphNames=readBoolean(DISPLAY_GRAPH_NAMES,s);
		/* MISC */
		config.htmlViewer=readFile(p,HTML_VIEWER,s);
		config.textEditor=readFile(p,TEXT_EDITOR,s);
		config.useTextEditorForDictionaries=readBoolean(USE_TEXT_EDITOR_FOR_DICS,s);
		/* CONCORDANCE */
		String tmp=new StringProperty(CONCORDANCE_DISPLAY,null).load(s);
		try {
			config.concordanceDisplay=ResultDisplay.valueOf(tmp);
		} catch (IllegalArgumentException e) {
			throw new IOException();
		}
		tmp=new StringProperty(CONCORDANCE_TYPE,null).load(s);
		try {
			config.concordanceType=ConcordanceType.valueOf(tmp);
		} catch (IllegalArgumentException e) {
			throw new IOException();
		}
		config.onlyMatches=readBoolean(ONLY_MATCHES,s);
		config.leftContext=readInt(LEFT_CONTEXT,s);
		config.rightContext=readInt(RIGHT_CONTEXT,s);
		config.leftStopAtS=readBoolean(LEFT_STOP_AT_S,s);
		config.rightStopAtS=readBoolean(RIGHT_STOP_AT_S,s);
		tmp=new StringProperty(SORT_ORDER,null).load(s);
		try {
			config.concordanceSortType=ConcordanceSortType.valueOf(tmp);
		} catch (IllegalArgumentException e) {
			throw new IOException();
		}
		tmp=new StringProperty(RESULT_TYPE,null).load(s);
		try {
			config.resultType=FileOperationType.valueOf(tmp);
		} catch (IllegalArgumentException e) {
			throw new IOException();
		}
		config.resultOutputFile=readFile(p,RESULT_OUTPUT_FILE,s);
		tmp=new StringProperty(EXTRACT_MATCH_TYPE,null).load(s);
		try {
			config.extractMatchType=ExtractMatchType.valueOf(tmp);
		} catch (IllegalArgumentException e) {
			throw new IOException();
		}
		tmp=new StringProperty(CONCORDANCE_OPERATION_TYPE,null).load(s);
		try {
			config.concordanceOperationType=ConcordanceOperationType.valueOf(tmp);
		} catch (IllegalArgumentException e) {
			throw new IOException();
		}
		tmp=new StringProperty(FILE_OPERATION_DISPLAY,null).load(s);
		try {
			config.fileOperationDisplay=ResultDisplay.valueOf(tmp);
		} catch (IllegalArgumentException e) {
			throw new IOException();
		}
		/* PROJECT STATE */
		config.doPreprocessing=readBoolean(DO_PREPROCESSING,s);
		config.doLocate=readBoolean(DO_LOCATE,s);
		config.buildResult=readBoolean(BUILD_RESULT,s);
		config.buildConcordance=readBoolean(BUILD_CONCORDANCE,s);
		int nOpenFrames=readInt(N_OPEN_FRAMES,s);
		config.openFrames.clear();
		for (int i=0;i<nOpenFrames;i++) {
			File f2=readFile(p,OPEN_FRAMES,s);
			if (f2!=null) {
				config.openFrames.add(f2);
			}
		}
		for (int i=1;i<=N_LAST_RESULT_FILE;i++) {
			File f2=readFile(p,LAST_RESULT_FILE+i,s);
			if (f2!=null) {
				config.lastResultFiles.add(f2);
			}
		}
		s.close();
		stream.close();
		} catch (IOException e) {
			/* Do nothing */
		}
		return config;
	}


	@Override
	public void save(OutputStreamWriter s) throws IOException {
		/* PREPROCESSING */
		/* LOCATE */
		saveBoolean(LAST_PATTERN_WAS_A_REGEXP,lastPatternRegexp,s);
		new StringProperty(LAST_REGEXP,lastRegexp).save(s);
		for (int i=0;i<N_LAST_GRAPHS;i++) {
			File f;
			if (i<lastGraphs.size()) {
				f=lastGraphs.get(i);
			} else {
				f=null;
			}
			saveFile(project,LAST_GRAPH+(i+1),f,s);
		}
		saveFile(project,LAST_GRAPH_DIR,lastGraphDir,s);
		saveBoolean(DEBUG,debug,s);
		saveBoolean(LAST_CORPUS_WAS_A_FILE,lastCorpusWasAFile,s);
		for (int i=0;i<N_LAST_SNT;i++) {
			File f;
			if (i<lastSnt.size()) {
				f=lastSnt.get(i);
			} else {
				f=null;
			}
			saveFile(project,LAST_SNT+(i+1),f,s);
		}
		saveFile(project,LAST_SNT_DIR,lastSntDir,s);
		/* GRAPH COMPILATION */
		saveBoolean(EMIT_EMPTY_GRAPH_WARNING,emitEmptyGraphWarning,s);
		saveBoolean(DISPLAY_GRAPH_NAMES,displayGraphNames,s);
		/* MISC */
		saveFile(project,HTML_VIEWER,htmlViewer,s);
		saveFile(project,TEXT_EDITOR,textEditor,s);
		saveBoolean(USE_TEXT_EDITOR_FOR_DICS,useTextEditorForDictionaries,s);
		/* CONCORDANCE */
		new StringProperty(CONCORDANCE_DISPLAY,concordanceDisplay.name()).save(s);
		new StringProperty(CONCORDANCE_TYPE,concordanceType.name()).save(s);
		saveBoolean(ONLY_MATCHES,onlyMatches,s);
		saveInt(LEFT_CONTEXT,leftContext,s);
		saveInt(RIGHT_CONTEXT,rightContext,s);
		saveBoolean(LEFT_STOP_AT_S,leftStopAtS,s);
		saveBoolean(RIGHT_STOP_AT_S,rightStopAtS,s);
		new StringProperty(SORT_ORDER,concordanceSortType.name()).save(s);
		new StringProperty(RESULT_TYPE,resultType.name()).save(s);
		saveFile(project,RESULT_OUTPUT_FILE,resultOutputFile,s);
		new StringProperty(EXTRACT_MATCH_TYPE,extractMatchType.name()).save(s);
		new StringProperty(CONCORDANCE_OPERATION_TYPE,concordanceOperationType.name()).save(s);
		new StringProperty(FILE_OPERATION_DISPLAY,fileOperationDisplay.name()).save(s);
		/* PROJECT STATE */
		saveBoolean(DO_PREPROCESSING,doPreprocessing,s);
		saveBoolean(DO_LOCATE,doLocate,s);
		saveBoolean(BUILD_RESULT,buildResult,s);
		saveBoolean(BUILD_CONCORDANCE,buildConcordance,s);
		saveInt(N_OPEN_FRAMES,openFrames.size(),s);
		for (File f:openFrames) {
			saveFile(project,OPEN_FRAMES,f,s);
		}
		for (int i=0;i<N_LAST_RESULT_FILE;i++) {
			File f;
			if (i<lastResultFiles.size()) {
				f=lastResultFiles.get(i);
			} else {
				f=null;
			}
			saveFile(project,LAST_RESULT_FILE+(i+1),f,s);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ProjectLocalConfig clone() {
		ProjectLocalConfig c=new ProjectLocalConfig(project);
		/* LOCATE */
		c.lastPatternRegexp=lastPatternRegexp;
		c.lastRegexp=lastRegexp;
		c.lastGraphs=(ArrayList<File>) lastGraphs.clone();
		c.lastGraphDir=lastGraphDir;
		c.debug=debug;
		c.lastCorpusWasAFile=lastCorpusWasAFile;
		c.lastSnt=(ArrayList<File>) lastSnt.clone();
		c.lastSntDir=lastSntDir;
		/* GRAPH COMPILATION */
		c.emitEmptyGraphWarning=emitEmptyGraphWarning;
		c.displayGraphNames=displayGraphNames;
		/* MISC */
		c.htmlViewer=htmlViewer;
		c.useTextEditorForDictionaries=useTextEditorForDictionaries;
		c.textEditor=textEditor;
		/* CONCORDANCE */
		c.concordanceDisplay=concordanceDisplay;
		c.concordanceType=concordanceType;
		c.onlyMatches=onlyMatches;
		c.leftContext=leftContext;
		c.rightContext=rightContext;
		c.leftStopAtS=leftStopAtS;
		c.rightStopAtS=rightStopAtS;
		c.concordanceSortType=concordanceSortType;
		c.resultType=resultType;
		c.resultOutputFile=resultOutputFile;
		c.extractMatchType=extractMatchType;
		c.concordanceOperationType=concordanceOperationType;
		c.fileOperationDisplay=fileOperationDisplay;
		/* PROJECT STATE */
		c.doPreprocessing=doPreprocessing;
		c.doLocate=doLocate;
		c.buildResult=buildResult;
		c.buildConcordance=buildConcordance;
		c.openFrames=(ArrayList<File>) openFrames.clone();
		c.lastResultFiles=(ArrayList<File>) lastResultFiles.clone();
		return c;
	}
	
	
	
	/****** GETTERS AND SETTERS ********/	
	public boolean isLastPatternRegexp() {
		return this.lastPatternRegexp;
	}

	public void setLastPatternRegexp(boolean b) {
		this.lastPatternRegexp=b;
	}
	
	public String getLastRegexp() {
		return this.lastRegexp;
	}

	public void setLastRegexp(String s) {
		this.lastRegexp=s;
	}

	public ArrayList<File> getLastGraphs() {
		return lastGraphs;
	}

	public void setLastGraphs(ArrayList<File> graphs) {
		/* First, we truncate the list to a maximum of N files */
		for (int i=graphs.size()-1;i>=N_LAST_GRAPHS;i--) {
			graphs.remove(i);
		}
		this.lastGraphs = graphs;
	}

	public File getLastGraphDir() {
		return lastGraphDir;
	}

	public void setLastGraphDir(File f) {
		this.lastGraphDir = f;
	}

	public boolean isDebugMode() {
		return debug;
	}

	public void setDebugMode(boolean selected) {
		this.debug=selected;
	}


	public ArrayList<File> getLastSnt() {
		return lastSnt;
	}

	public void setLastSnt(ArrayList<File> snt) {
		/* First, we truncate the list to a maximum of N files */
		for (int i=snt.size()-1;i>=N_LAST_SNT;i--) {
			snt.remove(i);
		}
		this.lastSnt = snt;
	}

	public File getLastSntDir() {
		return lastSntDir;
	}

	public void setLastSntDir(File f) {
		this.lastSntDir = f;
	}

	public boolean emitEmptyGraphWarning() {
		return emitEmptyGraphWarning;
	}

	public void setEmitEmptyGraphWarning(boolean emitEmptyGraphWarning) {
		this.emitEmptyGraphWarning = emitEmptyGraphWarning;
	}

	public boolean displayGraphNames() {
		return displayGraphNames;
	}

	public void setDisplayGraphNames(boolean displayGraphNames) {
		this.displayGraphNames = displayGraphNames;
	}

	public File getHtmlViewer() {
		return this.htmlViewer;
	}

	public void setHtmlViewer(File f) {
		this.htmlViewer=f;
	}

	public File getTextEditor() {
		return this.textEditor;
	}

	public void setTextEditor(File f) {
		this.textEditor=f;
	}

	public boolean useTextEditorForDictionaries() {
		return useTextEditorForDictionaries;
	}

	public void setUseTextEditorForDictionaries(boolean b) {
		this.useTextEditorForDictionaries=b;
	}
	
	public ResultDisplay getConcordanceDisplay() {
		return concordanceDisplay;
	}

	public void setConcordanceDisplay(ResultDisplay display) {
		this.concordanceDisplay=display;
	}

	public ResultDisplay getBuildResultDisplay() {
		return fileOperationDisplay;
	}

	public void setBuildResultDisplay(ResultDisplay display) {
		this.fileOperationDisplay=display;
	}

	public ConcordanceType getConcordanceType() {
		return concordanceType;
	}

	public void setConcordanceType(ConcordanceType type) {
		this.concordanceType=type;
	}
	
	public void setOnlyMatches(boolean b) {
		this.onlyMatches=b;
	}
	
	public boolean onlyMatches() {
		return onlyMatches;
	}

	public int getLeftContext() {
		return leftContext;
	}

	public void setLeftContext(int leftContext) {
		if (leftContext<0) {
			throw new IllegalArgumentException("Invalid left context size: "+leftContext);
		}
		this.leftContext = leftContext;
	}

	public int getRightContext() {
		return rightContext;
	}

	public void setRightContext(int rightContext) {
		if (rightContext<0) {
			throw new IllegalArgumentException("Invalid right context size: "+rightContext);
		}
		this.rightContext = rightContext;
	}

	public boolean isLeftStopAtS() {
		return leftStopAtS;
	}

	public void setLeftStopAtS(boolean leftStopAtS) {
		this.leftStopAtS = leftStopAtS;
	}

	public boolean isRightStopAtS() {
		return rightStopAtS;
	}

	public void setRightStopAtS(boolean rightStopAtS) {
		this.rightStopAtS = rightStopAtS;
	}

	public ConcordanceSortType getConcordanceSortType() {
		return concordanceSortType;
	}

	public void setConcordanceSortType(ConcordanceSortType concordanceSortType) {
		this.concordanceSortType = concordanceSortType;
	}

	public FileOperationType getResultType() {
		return resultType;
	}

	public void setResultType(FileOperationType type) {
		this.resultType=type;
	}

	public File getResultOutputFile() {
		return resultOutputFile;
	}

	public void setResultOutputFile(File f) {
		this.resultOutputFile=f;
	}

	public ExtractMatchType getExtractMatchType() {
		return extractMatchType;
	}

	public void setExtractMatchType(ExtractMatchType type) {
		this.extractMatchType=type;
	}

	public ConcordanceOperationType getBuildConcordanceType() {
		return concordanceOperationType;
	}

	public void setBuildConcordanceType(ConcordanceOperationType type) {
		this.concordanceOperationType=type;
	}

	public void setLastCorpusWasAFile(boolean b) {
		this.lastCorpusWasAFile=b;
	}

	public boolean isLastCorpusAFile() {
		return lastCorpusWasAFile;
	}

	public boolean mustDoPreprocessing() {
		return doPreprocessing;
	}
	
	public void setDoPreprocessing(boolean b) {
		this.doPreprocessing=b;
	}
	
	public boolean mustDoLocate() {
		return doLocate;
	}
	
	public void setDoLocate(boolean b) {
		this.doLocate=b;
	}

	public boolean mustBuildResult() {
		return buildResult;
	}
	
	public void setBuildResult(boolean b) {
		this.buildResult=b;
	}

	public boolean mustBuildConcordance() {
		return buildConcordance;
	}
	
	public void setBuildConcordance(boolean b) {
		this.buildConcordance=b;
	}

	public void saveOpenFrames(JInternalFrame[] frames) {
		ArrayList<File> files=new ArrayList<File>();
		if (frames!=null) {
			for (JInternalFrame f:frames) {
				if (f instanceof TextFrame) {
					files.add(0,FileUtil.getSnt(project.getCurrentCorpus()));
				} else if (f instanceof GraphFrame) {
					GraphFrame g=(GraphFrame)f;
					if (g.getGraph()!=null) files.add(g.getGraph());
				}
			}
		}
		this.openFrames=files;
		try {
			project.saveConfigurationFiles(false);
		} catch (IOException e) {
			/* */
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<File> getOpenFrames() {
		return (ArrayList<File>) openFrames.clone();
	}

	public ArrayList<File> getLastResultFiles() {
		return lastResultFiles;
	}

	public void setLastResultFiles(ArrayList<File> files) {
		/* First, we truncate the list to a maximum of N files */
		for (int i=files.size()-1;i>=N_LAST_RESULT_FILE;i--) {
			files.remove(i);
		}
		this.lastResultFiles = files;
	}


}
