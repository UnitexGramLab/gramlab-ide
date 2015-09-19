package fr.gramlab.project.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.config.locate.MatchesPolicy;
import fr.gramlab.project.config.locate.OutputsPolicy;
import fr.gramlab.project.config.locate.VariableErrorPolicy;
import fr.gramlab.project.config.maven.BinToBuild;
import fr.gramlab.project.config.maven.GrfToCompile;
import fr.gramlab.project.config.maven.MvnBuildConfig;
import fr.gramlab.project.config.maven.MvnSourceConfig;
import fr.gramlab.project.config.maven.PackageOperation;
import fr.gramlab.project.config.preprocess.fst2txt.Preprocessing;
import fr.gramlab.project.config.preprocess.fst2txt.PreprocessingStep;
import fr.umlv.unitex.config.InjectedVariable;
import fr.umlv.unitex.config.NamedRepository;
import fr.umlv.unitex.io.Encoding;

public class ProjectVersionableConfig extends AbstractProjectConfig {
	
	private GramlabProject project;
	
	/**
	 * PREPROCESSING CONFIGURATION
	 */
	private final static String LANGUAGE="LANGUAGE";			private String language;
	private final static String ENCODING="ENCODING";			private Encoding encoding;
	private final static String ALPHABET="ALPHABET";			private File alphabet;
	private final static String SORT_ALPHABET="SORT_ALPHABET";	private File sortAlphabet;
	private final static String SEPARATOR_NORMALIZATION="SEPARATOR_NORMALIZATION";
	private boolean separatorNormalization;
	private final static String NORM_TXT="NORM_TXT";			private File normTxt;
	private final static String PREPROCESSING="PREPROCESSING";	private Preprocessing preprocessing;
	private final static String SRC="\tSRC";
	private final static String TARGET="\tTARGET";
	private final static String MERGE="\tMERGE";
	private final static String SELECTED="\tSELECTED";
	
	private final static String MORPHO_DICS="MORPHO_DICS";		private ArrayList<File> morphoDics;
	private final static String APPLY_DICS="APPLY_DICS";		private boolean applyDictionaries;
	private final static String DICS="DICS";					private ArrayList<File> dics;
	private final static String POLYLEX_BIN="POLYLEX_BIN";		private File polyLexBin;
	
	private final static String SEMITIC="SEMITIC";				private boolean semitic;
	private final static String KOREAN="KOREAN";				private boolean korean;
  private final static String MATCH_WORD_BOUNDARIES="MATCH_WORD_BOUNDARIES";
                                 private boolean matchWordBoundaries; 
	private final static String ARABIC_TYPO_RULES="ARABIC_TYPO_RULES";				
																private File arabicTypoRules;
	private final static String CHAR_BY_CHAR="CHAR_BY_CHAR";	private boolean charByChar;
	private final static String MORPHOLOGICAL_USE_OF_SPACE="MORPHOLOGICAL_USE_OF_SPACE";	
																private boolean morphologicalUseOfSpace;
																
	/**
	 * LOCATE CONFIGURATION
	 */
	private final static String MATCHES_POLICY="MATCHES_POLICY";		private MatchesPolicy matchesPolicy;
	private final static String OUTPUTS_POLICY="OUTPUTS_POLICY";private OutputsPolicy outputsPolicy;
	private final static String SEARCH_LIMIT="SEARCH_LIMIT";	private int searchLimit;
	private final static String AMBIGUOUS_OUTPUTS_ALLOWED="AMBIGUOUS_OUTPUTS_ALLOWED";	
																private boolean ambiguousOutputsAllowed;
	private final static String VARIABLE_ERROR_POLICY="VARIABLE_ERROR_POLICY";
																private VariableErrorPolicy variableErrorPolicy;
	private final static String INJECTED_VARIABLES="INJECTED_VARIABLES";		
	private ArrayList<InjectedVariable> injectedVariables;
	private final static String INJECTED_VARIABLE_NAME="\tNAME";
	private final static String INJECTED_VARIABLE_VALUE="\tVALUE";

	/**
	 * GRAPH COMPILATION CONFIGURATION
	 */
	private final static String DEFAULT_GRAPH_REPOSITORY="DEFAULT_GRAPH_REPOSITORY";
			private File defaultGraphRepository;
	private final static String NAMED_REPOSITORIES="NAMED_REPOSITORIES";		
			private ArrayList<NamedRepository> namedRepositories;

	private final static String REPOSITORY_NAME="\tNAME";
	private final static String REPOSITORY_DIR="\tDIR";

	private final static String STRICT_TOKENIZATION="STRICT_TOKENIZATION";
	private boolean strictTokenization;
	
	/**
	 * MAVEN PACKAGE CONFIGURATION 
	 *
	 * SP: I know it would be more elegant to deal with all those stuffs into
	 *     the pom.xml file, but doing it this way is much more easy for me,
	 *     and honestly, I'm afraid of maven's bad surprises...
	 */
	private final static String MAVEN_INCLUDE_GRFS="MAVEN_INCLUDE_GRFS";
	private final static String MAVEN_INCLUDE_DICS="MAVEN_INCLUDE_DICS";
	private final static String MAVEN_INCLUDES="MAVEN_INCLUDES";
	private final static String MAVEN_EXCLUDES="MAVEN_EXCLUDES";
	private MvnSourceConfig mvnSourceConfig;
	private final static String MAVEN_GRFS_TO_COMPILE="MAVEN_GRFS_TO_COMPILE";
	private final static String MAVEN_GRF_NAME="\tMAVEN_GRF_NAME";
	private final static String MAVEN_FST2_NAME="\tMAVEN_FST2_NAME";
	private final static String MAVEN_BINS_TO_BUILD="MAVEN_BINS_TO_BUILD";
	private final static String MAVEN_BIN_NAME="\tMAVEN_BIN_NAME";
	private final static String MAVEN_DIC_FILES="\tMAVEN_DIC_FILES";
	private MvnBuildConfig mvnBuildConfig;
	private final static String MAVEN_SOURCE_PACKAGE="MAVEN_SOURCE_PACKAGE";
	private boolean mvnSourcePackage;
	private final static String MAVEN_BUILD_PACKAGE="MAVEN_BUILD_PACKAGE";
	private boolean mvnBuildPackage;
	private final static String MAVEN_PACKAGE_OPERATION="MAVEN_PACKAGE_OPERATION";
	private PackageOperation packageOperation;

	
	public ProjectVersionableConfig(GramlabProject p) {
		this.project=p;
		this.encoding=Encoding.UTF8;
		this.separatorNormalization=true;
		this.preprocessing=new Preprocessing();
		this.morphoDics=new ArrayList<File>();
		this.applyDictionaries=true;
		this.dics=new ArrayList<File>();
		this.polyLexBin=null;
		this.semitic=false;
		this.korean=false;
    this.matchWordBoundaries=true;
		this.arabicTypoRules=null;
		this.charByChar=false;
		this.morphologicalUseOfSpace=false;
		this.matchesPolicy=MatchesPolicy.LONGEST;
		this.outputsPolicy=OutputsPolicy.IGNORE;
		this.searchLimit=200;
		this.ambiguousOutputsAllowed=true;
		this.variableErrorPolicy=VariableErrorPolicy.IGNORE;
		this.injectedVariables=new ArrayList<InjectedVariable>();
		this.defaultGraphRepository=null;
		this.namedRepositories=new ArrayList<NamedRepository>();
		this.strictTokenization=true;
		this.mvnSourceConfig=new MvnSourceConfig();
		this.mvnBuildConfig=new MvnBuildConfig();
		this.mvnSourcePackage=true;
		this.mvnBuildPackage=true;
		this.packageOperation=PackageOperation.BUILD_AND_INSTALL;
	}
	
	public ProjectVersionableConfig(GramlabProject p, String language, Encoding encoding) {
		this(p);
		this.language=language;
		this.encoding=encoding;
		if ("ar".equals(language) || "he".equals(language)) {
			this.semitic=true;
		}
		if ("ko".equals(language)) {
			this.korean=true;
		}
		if ("th".equals(language)) {
			this.charByChar=true;
		}
	}

	public static ProjectVersionableConfig load(GramlabProject p,File f) {
		ProjectVersionableConfig config=new ProjectVersionableConfig(p);
		try {
		FileInputStream stream;
		stream = new FileInputStream(f);
		InputStreamReader reader0=new InputStreamReader(stream,"UTF-8");
		BufferedReader s=new BufferedReader(reader0);
		/* PREPROCESSING */
		config.language=new StringProperty(LANGUAGE,null).load(s);
		String tmp=new StringProperty(ENCODING,null).load(s);
		try {
			config.encoding=Encoding.valueOf(tmp);
		} catch (IllegalArgumentException e) {
			throw new IOException();
		}
		config.alphabet=readProjectRelativeFile(p,ALPHABET,s);
		config.sortAlphabet=readProjectRelativeFile(p,SORT_ALPHABET,s);
		config.separatorNormalization=readBoolean(SEPARATOR_NORMALIZATION,s);
		config.normTxt=readProjectRelativeFile(p,NORM_TXT,s);
		config.preprocessing=readPreprocessing(p,s);
		config.morphoDics=readProjectRelativeFileList(MORPHO_DICS,p,s);
		config.applyDictionaries=readBoolean(APPLY_DICS,s);
		config.dics=readProjectRelativeFileList(DICS,p,s);
		config.polyLexBin=readProjectRelativeFile(p,POLYLEX_BIN,s);
		config.semitic=readBoolean(SEMITIC,s);
		config.korean=readBoolean(KOREAN,s);
		config.arabicTypoRules=readProjectRelativeFile(p,ARABIC_TYPO_RULES,s);
		config.charByChar=readBoolean(CHAR_BY_CHAR,s);
		config.morphologicalUseOfSpace=readBoolean(MORPHOLOGICAL_USE_OF_SPACE,s);
		/* LOCATE */
		tmp=new StringProperty(MATCHES_POLICY,null).load(s);
		try {
			config.matchesPolicy=MatchesPolicy.valueOf(tmp);
		} catch (IllegalArgumentException e) {
			throw new IOException();
		}
		tmp=new StringProperty(OUTPUTS_POLICY,null).load(s);
		try {
			config.outputsPolicy=OutputsPolicy.valueOf(tmp);
		} catch (IllegalArgumentException e) {
			throw new IOException();
		}
		config.searchLimit=readInt(SEARCH_LIMIT,s);
		config.ambiguousOutputsAllowed=readBoolean(AMBIGUOUS_OUTPUTS_ALLOWED,s);
		tmp=new StringProperty(VARIABLE_ERROR_POLICY,null).load(s);
		try {
			config.variableErrorPolicy=VariableErrorPolicy.valueOf(tmp);
		} catch (IllegalArgumentException e) {
			throw new IOException();
		}
		config.injectedVariables=readInjectedVariables(s);
		/* GRAPH COMPILATION */
		config.defaultGraphRepository=readProjectRelativeFile(p,DEFAULT_GRAPH_REPOSITORY,s);
		config.namedRepositories=readNamedRepositories(p,s);
		config.strictTokenization=readBoolean(STRICT_TOKENIZATION,s);
		/* MAVEN */
		config.mvnSourceConfig=readMvnSourceConfig(p,s);
		config.mvnBuildConfig=readMvnBuildConfig(p,s);
		config.mvnSourcePackage=readBoolean(MAVEN_SOURCE_PACKAGE,s);
		config.mvnBuildPackage=readBoolean(MAVEN_BUILD_PACKAGE,s);
		tmp=new StringProperty(MAVEN_PACKAGE_OPERATION,null).load(s);
		try {
			config.packageOperation=PackageOperation.valueOf(tmp);
		} catch (IllegalArgumentException e) {
			throw new IOException();
		}
		
		s.close();
		stream.close();
		} catch (IOException e) {
			/* Do nothing */
		}
		return config;
	}


	private static MvnSourceConfig readMvnSourceConfig(GramlabProject p,BufferedReader s) throws IOException {
		boolean includeGrfs=readBoolean(MAVEN_INCLUDE_GRFS,s);
		boolean includeDics=readBoolean(MAVEN_INCLUDE_DICS,s);
		ArrayList<File> includes=readProjectRelativeFileList(MAVEN_INCLUDES,p,s);
		ArrayList<File> excludes=readProjectRelativeFileList(MAVEN_EXCLUDES,p,s);
		return new MvnSourceConfig(includeGrfs,includeDics,includes,excludes);
	}

	private static MvnBuildConfig readMvnBuildConfig(GramlabProject p, BufferedReader s) throws IOException {
		int nGrf=readInt(MAVEN_GRFS_TO_COMPILE,s);
		ArrayList<GrfToCompile> grfToCompile=new ArrayList<GrfToCompile>();
		for (int i=0;i<nGrf;i++) {
			File grf=readProjectRelativeFile(p, MAVEN_GRF_NAME, s);
			String fst2=new StringProperty(MAVEN_FST2_NAME,null).load(s);
			grfToCompile.add(new GrfToCompile(grf, fst2));
		}
		int nBin=readInt(MAVEN_BINS_TO_BUILD,s);
		ArrayList<BinToBuild> binToBuild=new ArrayList<BinToBuild>();
		for (int i=0;i<nBin;i++) {
			String bin=new StringProperty(MAVEN_BIN_NAME,null).load(s);
			ArrayList<File> dics=readProjectRelativeFileList(MAVEN_DIC_FILES, p, s);
			binToBuild.add(new BinToBuild(bin, dics));
		}
		return new MvnBuildConfig(grfToCompile, binToBuild);
	}

	@Override
	public void save(OutputStreamWriter s) throws IOException {
		/* PREPROCESSING */
		new StringProperty(LANGUAGE,language).save(s);
		new StringProperty(ENCODING,encoding.name()).save(s);
		saveProjectRelativeFile(project,ALPHABET,alphabet,s);
		saveProjectRelativeFile(project,SORT_ALPHABET,sortAlphabet,s);
		saveBoolean(SEPARATOR_NORMALIZATION,separatorNormalization,s);
		saveProjectRelativeFile(project,NORM_TXT,normTxt,s);
		savePreprocessing(s);
		saveProjectRelativeFileList(project,MORPHO_DICS,morphoDics,s);
		saveBoolean(APPLY_DICS,applyDictionaries,s);
		saveProjectRelativeFileList(project,DICS,dics,s);
		saveProjectRelativeFile(project,POLYLEX_BIN,polyLexBin,s);
		saveBoolean(SEMITIC,semitic,s);
		saveBoolean(KOREAN,korean,s);
		saveProjectRelativeFile(project,ARABIC_TYPO_RULES,arabicTypoRules,s);
		saveBoolean(CHAR_BY_CHAR,charByChar,s);
		saveBoolean(MORPHOLOGICAL_USE_OF_SPACE,morphologicalUseOfSpace,s);
		/* LOCATE */
		new StringProperty(MATCHES_POLICY,matchesPolicy.name()).save(s);
		new StringProperty(OUTPUTS_POLICY,outputsPolicy.name()).save(s);
		saveInt(SEARCH_LIMIT,searchLimit,s);
		saveBoolean(AMBIGUOUS_OUTPUTS_ALLOWED,ambiguousOutputsAllowed,s);
		new StringProperty(VARIABLE_ERROR_POLICY,variableErrorPolicy.name()).save(s);
		saveInjectedVariables(s);
		/* GRAPH COMPILATION */
		saveProjectRelativeFile(project,DEFAULT_GRAPH_REPOSITORY,defaultGraphRepository,s);
		saveNamedRepositories(s);
		saveBoolean(STRICT_TOKENIZATION,strictTokenization,s);
		/* MAVEN */
		saveMvnSourceConfig(s);
		saveMvnBuildConfig(s);
		saveBoolean(MAVEN_SOURCE_PACKAGE,mvnSourcePackage,s);
		saveBoolean(MAVEN_BUILD_PACKAGE,mvnBuildPackage,s);
		new StringProperty(MAVEN_PACKAGE_OPERATION,packageOperation.name()).save(s);
	}
	
	private void saveMvnSourceConfig(OutputStreamWriter s) throws IOException {
		saveBoolean(MAVEN_INCLUDE_GRFS,mvnSourceConfig.isIncludeGrfs(),s);
		saveBoolean(MAVEN_INCLUDE_DICS,mvnSourceConfig.isIncludeDics(),s);
		saveProjectRelativeFileList(project,MAVEN_INCLUDES,mvnSourceConfig.getIncludes(),s);
		saveProjectRelativeFileList(project,MAVEN_EXCLUDES,mvnSourceConfig.getExcludes(),s);
	}

	private void saveMvnBuildConfig(OutputStreamWriter s) throws IOException {
		ArrayList<GrfToCompile> grfToCompile=mvnBuildConfig.getGrfToCompile();
		saveInt(MAVEN_GRFS_TO_COMPILE, grfToCompile.size(), s);
		for (GrfToCompile g:grfToCompile) {
			saveProjectRelativeFile(project, MAVEN_GRF_NAME, g.getGrf(), s);
			new StringProperty(MAVEN_FST2_NAME,g.getFst2()).save(s);
		}
		ArrayList<BinToBuild> binToBuild=mvnBuildConfig.getBinToBuild();
		saveInt(MAVEN_BINS_TO_BUILD, binToBuild.size(), s);
		for (BinToBuild b:binToBuild) {
			new StringProperty(MAVEN_BIN_NAME,b.getBin()).save(s);
			saveProjectRelativeFileList(project, MAVEN_DIC_FILES, b.getDics(), s);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ProjectVersionableConfig clone() {
		/* PREPROCESSING */
		ProjectVersionableConfig c=new ProjectVersionableConfig(project,language,encoding);
		c.alphabet=alphabet;
		c.sortAlphabet=sortAlphabet;
		c.separatorNormalization=separatorNormalization;
		c.normTxt=normTxt;
		c.preprocessing=preprocessing.clone();
		c.morphoDics=(ArrayList<File>) morphoDics.clone();
		c.applyDictionaries=applyDictionaries;
		c.dics=(ArrayList<File>) dics.clone();
		c.polyLexBin=polyLexBin;
		c.semitic=semitic;
		c.korean=korean;
		c.arabicTypoRules=arabicTypoRules;
		c.charByChar=charByChar;
		c.morphologicalUseOfSpace=morphologicalUseOfSpace;
		/* LOCATE */
		c.matchesPolicy=matchesPolicy;
		c.outputsPolicy=outputsPolicy;
		c.searchLimit=searchLimit;
		c.ambiguousOutputsAllowed=ambiguousOutputsAllowed;
		c.variableErrorPolicy=variableErrorPolicy;
		c.injectedVariables=(ArrayList<InjectedVariable>) injectedVariables.clone();
		/* GRAPH COMPILATION */
		c.defaultGraphRepository=defaultGraphRepository;
		c.namedRepositories=(ArrayList<NamedRepository>) namedRepositories.clone();
		c.strictTokenization=strictTokenization;
		/* MAVEN */
		c.mvnSourceConfig=mvnSourceConfig.clone();
		c.mvnBuildConfig=mvnBuildConfig.clone();
		c.mvnSourcePackage=mvnSourcePackage;
		c.mvnBuildPackage=mvnBuildPackage;
		c.packageOperation=packageOperation;
		return c;
	}
	
	private static Preprocessing readPreprocessing(GramlabProject p, BufferedReader s) throws IOException {
		ArrayList<PreprocessingStep> steps=new ArrayList<PreprocessingStep>();
		String tmp=new StringProperty(PREPROCESSING,null).load(s);
		try {
			int n=Integer.valueOf(tmp);
			for (int i=0;i<n;i++) {
				File src=readProjectRelativeFile(p,SRC,s);
				File target=readProjectRelativeFile(p,TARGET,s);
				tmp=new StringProperty(MERGE,null).load(s);
				boolean merge=Boolean.parseBoolean(tmp);
				tmp=new StringProperty(SELECTED,null).load(s);
				boolean selected=Boolean.parseBoolean(tmp);
				steps.add(new PreprocessingStep(src,target,merge,selected));
			}
		} catch (NumberFormatException e) {
			throw new IOException();
		}
		Preprocessing preprocessing=new Preprocessing();
		preprocessing.setPreprocessingSteps(steps);
		return preprocessing;
	}
	
	private void savePreprocessing(OutputStreamWriter s) throws IOException {
		ArrayList<PreprocessingStep> steps=preprocessing.getPreprocessingSteps();
		new StringProperty(PREPROCESSING,""+steps.size()).save(s);
		for (PreprocessingStep step:steps) {
			saveProjectRelativeFile(project,SRC,step.getGraph(),s);
			saveProjectRelativeFile(project,TARGET,step.getDestFst2(),s);
			new StringProperty(MERGE,""+step.isMerge()).save(s);
			new StringProperty(SELECTED,""+step.isSelected()).save(s);
		}
	}
	
	private static ArrayList<NamedRepository> readNamedRepositories(GramlabProject p,BufferedReader s) throws IOException {
		ArrayList<NamedRepository> list=new ArrayList<NamedRepository>();
		String tmp=new StringProperty(NAMED_REPOSITORIES,null).load(s);
		try {
			int n=Integer.valueOf(tmp);
			for (int i=0;i<n;i++) {
				tmp=new StringProperty(REPOSITORY_NAME,null).load(s);
				File dir=readProjectRelativeFile(p,REPOSITORY_DIR,s);
				list.add(new NamedRepository(tmp,dir));
			}
		} catch (NumberFormatException e) {
			throw new IOException();
		}
		return list;
	}
	
	private void saveNamedRepositories(OutputStreamWriter s) throws IOException {
		new StringProperty(NAMED_REPOSITORIES,""+namedRepositories.size()).save(s);
		for (NamedRepository r:namedRepositories) {
			new StringProperty(REPOSITORY_NAME,""+r.getName()).save(s);
			saveProjectRelativeFile(project,REPOSITORY_DIR,r.getFile(),s);
		}
	}

	
	private static ArrayList<InjectedVariable> readInjectedVariables(BufferedReader s) throws IOException {
		ArrayList<InjectedVariable> list=new ArrayList<InjectedVariable>();
		String tmp=new StringProperty(INJECTED_VARIABLES,null).load(s);
		String value;
		try {
			int n=Integer.valueOf(tmp);
			for (int i=0;i<n;i++) {
				tmp=new StringProperty(INJECTED_VARIABLE_NAME,null).load(s);
				value=new StringProperty(INJECTED_VARIABLE_VALUE,null).load(s);
				list.add(new InjectedVariable(tmp,value));
			}
		} catch (NumberFormatException e) {
			throw new IOException();
		}
		return list;
	}
	
	private void saveInjectedVariables(OutputStreamWriter s) throws IOException {
		new StringProperty(INJECTED_VARIABLES,""+injectedVariables.size()).save(s);
		for (InjectedVariable i:injectedVariables) {
			new StringProperty(INJECTED_VARIABLE_NAME,""+i.getName()).save(s);
			new StringProperty(INJECTED_VARIABLE_VALUE,""+i.getValue()).save(s);
		}
	}
	
	/****** GETTERS AND SETTERS ********/	
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Encoding getEncoding() {
		return encoding;
	}

	public void setEncoding(Encoding encoding) {
		this.encoding = encoding;
	}

	public File getAlphabet() {
		return alphabet;
	}

	public void setAlphabet(File alphabet) {
		if (alphabet!=null && project.getRelativeFileName(alphabet)==null) {
			throw new IllegalArgumentException("File "+alphabet+" is not in the project directory");
		}
		this.alphabet = alphabet;
	}

	public File getSortAlphabet() {
		return sortAlphabet;
	}

	public void setSortAlphabet(File sortAlphabet) {
		if (sortAlphabet!=null && project.getRelativeFileName(sortAlphabet)==null) {
			throw new IllegalArgumentException("File "+sortAlphabet+" is not in the project directory");
		}
		this.sortAlphabet = sortAlphabet;
	}

	public boolean separatorNormalization() {
		return separatorNormalization;
	}
	
	public void setSeparatorNormalization(boolean b) {
		this.separatorNormalization=b;
	}
	
	public File getNormTxt() {
		return normTxt;
	}

	public void setNormTxt(File normTxt) {
		if (normTxt!=null && project.getRelativeFileName(normTxt)==null) {
			throw new IllegalArgumentException("File "+normTxt+" is not in the project directory");
		}
		this.normTxt = normTxt;
	}

	public Preprocessing getPreprocessing() {
		return preprocessing;
	}

	public void setPreprocessing(Preprocessing preprocessing) {
		this.preprocessing = preprocessing;
	}

	public ArrayList<File> getMorphoDics() {
		return morphoDics;
	}

	public void setMorphoDics(ArrayList<File> morphoDics) {
		this.morphoDics = morphoDics;
	}

	public boolean applyDictionaries() {
		return applyDictionaries;
	}
	
	public void setApplyDictionaries(boolean b) {
		this.applyDictionaries=b;
	}
	
	public ArrayList<File> getDics() {
		return dics;
	}

	public void setDics(ArrayList<File> dics) {
		this.dics = dics;
	}



	public File getPolyLexBin() {
		return polyLexBin;
	}
	
	public void setPolyLexBin(File bin) {
		if (bin!=null) {
			if (!bin.exists()) {
				bin=null;
			} else if (project.getRelativeFileName(bin)==null) {
				throw new IllegalArgumentException("File "+bin+" is not in the project directory");
			}
		}
		this.polyLexBin=bin;
	}
	
	public boolean isSemitic() {
		return semitic;
	}

	public void setSemitic(boolean s) {
		this.semitic=s;
	}

	public boolean isKorean() {
		return korean;
	}

	public void setKorean(boolean k) {
		this.korean=k;
	}
	
  public boolean isMatchWordBoundaries() {
    return matchWordBoundaries;
  }

  public void setMatchWordBoundaries(boolean b) {
    this.matchWordBoundaries = b;
  }
  
	public File getArabicTypoRules() {
		return arabicTypoRules;
	}
	
	public void setArabicTypoRules(File f) {
		if (f!=null) {
			if (!f.exists()) {
				f=null;
			} else if (project.getRelativeFileName(f)==null) {
				throw new IllegalArgumentException("File "+f+" is not in the project directory");
			}
		}
		this.arabicTypoRules=f;
	}

	public boolean isCharByChar() {
		return this.charByChar;
	}

	public void setCharByChar(boolean b) {
		this.charByChar=b;
	}

	public boolean isMorphologicalUseOfSpace() {
		return this.morphologicalUseOfSpace;
	}

	public void setMorphologicalUseOfSpace(boolean b) {
		this.morphologicalUseOfSpace=b;
	}
	
	public MatchesPolicy getMatchesPolicy() {
		return this.matchesPolicy;
	}

	public void setMatchesPolicy(MatchesPolicy index) {
		this.matchesPolicy=index;
	}

	public OutputsPolicy getOutputsPolicy() {
		return this.outputsPolicy;
	}

	public void setOutputsPolicy(OutputsPolicy outputs) {
		this.outputsPolicy=outputs;
	}

	public int getSearchLimit() {
		return this.searchLimit;
	}
	
	public void setSearchLimit(int limit) {
		if (limit<-1) {
			throw new IllegalArgumentException("Invalid search limit value: "+limit);
		}
		this.searchLimit=limit;
	}

	public boolean isAmbiguousOutputsAllowed() {
		return ambiguousOutputsAllowed;
	}

	public void setAmbiguousOutputsAllowed(boolean b) {
		this.ambiguousOutputsAllowed=b;
	}

	public VariableErrorPolicy getVariableErrorPolicy() {
		return variableErrorPolicy;
	}
	
	public void setVariableErrorPolicy(VariableErrorPolicy p) {
		this.variableErrorPolicy=p;
	}

	public File getDefaultGraphRepository() {
		return defaultGraphRepository;
	}

	public void setDefaultGraphRepository(File f) {
		if (f!=null && project.getRelativeFileName(f)==null) {
			throw new IllegalArgumentException("File "+f+" is not in the project directory");
		}
		this.defaultGraphRepository = f;
	}

	public ArrayList<NamedRepository> getNamedRepositories() {
		return namedRepositories;
	}

	public void setNamedRepositories(ArrayList<NamedRepository> l) {
		this.namedRepositories = l;
	}

	public ArrayList<InjectedVariable> getInjectedVariables() {
		return injectedVariables;
	}

	public void setInjectedVariables(ArrayList<InjectedVariable> l) {
		this.injectedVariables = l;
	}

	public File getNamedRepository(String repositoryName) {
		for (NamedRepository n:namedRepositories) {
			if (n.getName().equals(repositoryName)) {
				return n.getFile();
			}
		}
		return null;
	}

	public boolean strictTokenization() {
		return this.strictTokenization;
	}

	public void setStrictTokenization(boolean b) {
		this.strictTokenization=b;
	}
	
	public MvnSourceConfig getMvnSourceConfig() {
		return this.mvnSourceConfig;
	}
	
	public void setMvnSourceConfig(MvnSourceConfig m) {
		this.mvnSourceConfig=m;
	}

	public MvnBuildConfig getMvnBuildConfig() {
		return this.mvnBuildConfig;
	}
	
	public void setMvnBuildConfig(MvnBuildConfig m) {
		this.mvnBuildConfig=m;
	}

	public boolean isMvnSourcePackage() {
		return this.mvnSourcePackage;
	}

	public void setMvnSourcePackage(boolean b) {
		this.mvnSourcePackage=b;
	}

	public boolean isMvnBuildPackage() {
		return this.mvnBuildPackage;
	}

	public void setMvnBuildPackage(boolean b) {
		this.mvnBuildPackage=b;
	}

	public PackageOperation getPackageOperation() {
		return this.packageOperation;
	}

	public void setPackageOperation(PackageOperation op) {
		this.packageOperation=op;
	}
}
