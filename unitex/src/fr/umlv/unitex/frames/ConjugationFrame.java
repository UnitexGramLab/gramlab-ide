package fr.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import fr.umlv.unitex.process.commands.Fst2ListCommand;
import fr.umlv.unitex.process.commands.Grf2Fst2Command;
import fr.umlv.unitex.process.commands.MultiCommands;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;


import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.config.PreferencesListener;
import fr.umlv.unitex.config.PreferencesManager;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.MultiFlexCommand;
import fr.umlv.unitex.text.BigTextList;

public class ConjugationFrame extends JInternalFrame {
	
	private static final int MAIN_PANEL_WIDTH = 450;
	private static final int MAIN_PANEL_HEIGHT = 400;
	private static final String DOT_SEPARATOR = "\\.";
	private static final String PLUS_SEPARATOR = "\\+";
	private static final String COMMMA_SEPARATOR = ",";
	private static final String DOUBLESLASH_SEPARATOR = "/ /";
	private static final String ENCODE = "utf-8";
	private static final String ENCODE_UTF_16 = "utf-16le";
//	private static final String UTF8_BOM = "\uFEFF";
	private static final List<String> exCludeChar = new ArrayList<String>(); 
	private static String resultFileName = null;
	private static final List<String> graphNameList = new ArrayList<String>(); 
	
	private static final String folderReferenceName = "Conjugaison";
	private static final String resultPath = Config.getUserCurrentLanguageDir()+File.separator+folderReferenceName+File.separator+"result.txt";
	private static final String conjugaisonFolderPath = Config.getUserCurrentLanguageDir()+File.separator+folderReferenceName;
	private static final String inflexionFolderPath = conjugaisonFolderPath+File.separator+"inflexion";
	private static final String exploreGrapheFolderPath = conjugaisonFolderPath+File.separator+"exploregraphe";
	private static final String resultFolderPath = conjugaisonFolderPath+File.separator+"result";
	
	private final JTextField inputText = new JTextField();
	private final JButton btnGenerateAgglutinedFormsFromFile = new JButton("Generate agglutinated forms");
	private final JButton setGraphFile = new JButton("Set...");
	private final JTextField graphFilePath = new JTextField();
	private final BigTextList textArea = new BigTextList();
	private JComboBox graphList = null;
	
	private static final String ALL = "All";
	private static final String placeHolderLemma = "$V.LEMMA$";
	private static final String placeHolderCode = "$V.CODE$";
	private static final String placeHolderVerb = "$V$";
	private static final String tmpSuffix ="tmp.dic";
	private static final String flxSuffix ="flx.dic";
	private static final String tmpFlxSuffix ="tmpflx.dic";
	private static final String ERROR_FILE_EMPTY = "Please choose a dictionary file";
	private static final String ERROR_RESULT_EMPTY = "Current criteria has not find result";
	private static final String FILE_EMPTY_DIALOG_TITLE = "No File Choosen";
	private static final String DESCRIPTION_DIC_FILE_ONLY = "Only Dictionnary File (.dic)";
	private static final List<String> extractedLines = new ArrayList<String>();

	public ConjugationFrame(){
		super("Conjugeur", true, true);
		graphNameList.clear();
		graphNameList.add(ALL);
		JPanel main = new JPanel(new GridLayout(2,1));
		constructSearchPanel(main);
		constructResultPanel(main);
		main.setPreferredSize(new Dimension(MAIN_PANEL_WIDTH, MAIN_PANEL_HEIGHT));
		textArea.setFont(ConfigManager.getManager().getTextFont(null));
		PreferencesManager.addPreferencesListener(new PreferencesListener() {
			@Override
			public void preferencesChanged(String language) {
				textArea.setFont(ConfigManager.getManager().getTextFont(null));
			}
		});
		setContentPane(main);
		pack();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setActions();
		initDefaultStates();
	}


	private void initDefaultStates() {
		createConjugaisonFolder();
		graphFilePath.setEditable(false);
		setExcludeChar(new String[]{"$"," ","<",">"});
	}
	
	private void setExcludeChar(String []excludeChar){
		for (String e : excludeChar) {
			exCludeChar.add(e);
		}
	}

	private void createConjugaisonFolder(){
		Path path = Paths.get(conjugaisonFolderPath);
		if (Files.notExists(path)) {
			(new File(conjugaisonFolderPath)).mkdirs();
		}
		Path pathInfletion = Paths.get(inflexionFolderPath);
		if (Files.notExists(pathInfletion)) {
			(new File(inflexionFolderPath)).mkdirs();
		}
		Path pathExploreGraph = Paths.get(exploreGrapheFolderPath);
		if (Files.notExists(pathExploreGraph)) {
			(new File(exploreGrapheFolderPath)).mkdirs();
		}
		Path pathResult = Paths.get(resultFolderPath);
		if (Files.notExists(pathResult)) {
			(new File(resultFolderPath)).mkdirs();
		}

	}
	
//	private void writeUTF8WithoutBOM(List<String> data,String path){
//		OutputStreamWriter osw = null;
//		try{
//
//
//			FileOutputStream fos = new FileOutputStream(path,true);
//			osw = new OutputStreamWriter(fos,"UTF-8");
////			osw.write("sample");
////			boolean firstLine = true;
////			String line1 = null;
//			for (String e : data) {
////				if(!firstLine){
//					osw.write(e+"\n");
////				}else{
////					line1 =e;
////				}
////				firstLine = false;		
//			}
//			
////			osw.write("File");
//			//osw.write(Charset.forName("UTF-8").encode("Sample"));
//			osw.close();
////			System.out.println("Success");
////			fos.close();
////			RandomAccessFile f = new RandomAccessFile(new File(path), "rw");
////			f.seek(0); // to the beginning
////			f.write(line1.getBytes());
////			f.close();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
	
//	private void clearGraphListItems(){
//		int elements = graphList.getItemCount();
//		String it = (String) graphList.getItemAt(0);
//		for(int i=elements-1 ;i>=0;i--){
//			graphList.removeItemAt(i);
//		}
//	}
	
	private void setGraphNameList(String filePath){
		graphNameList.clear();
		graphNameList.add(ALL);
		graphList.addItem(ALL);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),ENCODE));
			String line = br.readLine();
		    while (line != null) {
		    	String grf = line.split(PLUS_SEPARATOR).length>1 ?line.split(PLUS_SEPARATOR)[1]:"Default";
		    	if(!graphNameList.contains(grf)){
		    		graphNameList.add(grf);
		    		graphList.addItem(grf);
		    	}
		    	line = br.readLine();
		    }
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private List<String> extractLinesFromFile(String filePath,String encodage,String word,String graphName){
		List<String> extractedLines = new ArrayList<String>();
		String line = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),encodage));
			 line = br.readLine();
		    while (line != null) {
		    	if(word != null && !word.trim().isEmpty() ){
		    		if(line.split(COMMMA_SEPARATOR)[0].equals(word)){
		    			extractedLines.add(line);
			    		break;
		    		}
		    	}else if(graphName != null && !graphName.trim().equals(ALL)){
		    		if(line.split(PLUS_SEPARATOR).length>1 && graphName.equals(line.split(PLUS_SEPARATOR)[1])){
		    			extractedLines.add(line);
		    		}
		    	}else{
		    		extractedLines.add(line);
		    	}
		        line = br.readLine();
		    }
		    br.close();
		} catch (IOException e) {
			//extractedLines = null;
			e.printStackTrace();
		}
		return extractedLines;
	}
	
	private String getSelectedGraphName(){
        return (String)graphList.getSelectedItem();
	}
	
	private void setActions() {
		
		graphList.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(getSelectedGraphName()!= null && !getSelectedGraphName().equals(ALL)){
					inputText.setText("");
				}
			}
		});
		
		inputText.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(!inputText.getText().trim().isEmpty()){
					graphList.setSelectedIndex(0);
				}
			}
		});
		
//		inputText.addFocusListener(new FocusListener() {
//			
//			@Override
//			public void focusLost(FocusEvent arg0) {
//				if(!inputText.getText().trim().isEmpty()){
//					graphList.setSelectedIndex(0);
//				}
//			}
//			
//			@Override
//			public void focusGained(FocusEvent arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		
		final Action chooseDicAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser f = new JFileChooser();
				f.setDialogTitle("Choose your dictionary file");
				f.setDialogType(JFileChooser.OPEN_DIALOG);
				f.setFileSelectionMode(JFileChooser.FILES_ONLY);
				f.setFileFilter(new GrfFiltre(new String[]{"dic"},DESCRIPTION_DIC_FILE_ONLY));
				f.setAcceptAllFileFilterUsed(false);
				f.setCurrentDirectory(new File(Config.getUserCurrentLanguageDir()+File.separator+"Dela"));
				if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
					return;
				graphFilePath.setText(f.getSelectedFile().getAbsolutePath());
				graphList.removeAllItems();
//				clearGraphListItems();
				setGraphNameList(f.getSelectedFile().getAbsolutePath());
				graphList.setVisible(true);
				graphList.repaint();
				
			}
		};
		
		final Action generateAgglutinedFormsFromFileAction = new AbstractAction("Generate agglutinated forms from file"){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(isDicFile(graphFilePath.getText())){
					List<String> graphNameList = null;
					textArea.reset();
					extractedLines.clear();
					extractedLines.addAll(extractLinesFromFile(graphFilePath.getText(),ENCODE,inputText.getText(),getSelectedGraphName()));
					if(extractedLines.size() > 0){
						createConjugaisonFolder();
						setResultFileName();
						graphNameList = getGraphNameListPath(extractedLines,getSelectedGraphName());
						buildAndLaunchCommand(graphNameList,extractedLines);
					}else{
						JOptionPane.showMessageDialog(null, ERROR_RESULT_EMPTY,FILE_EMPTY_DIALOG_TITLE,
								JOptionPane.PLAIN_MESSAGE);
					}
				}else{
					JOptionPane.showMessageDialog(null, ERROR_FILE_EMPTY,FILE_EMPTY_DIALOG_TITLE,
							JOptionPane.PLAIN_MESSAGE);
				}
				
				
			}
			
		};

		btnGenerateAgglutinedFormsFromFile.addActionListener(generateAgglutinedFormsFromFileAction);
		setGraphFile.addActionListener(chooseDicAction);
	}

	public String stripAccents(String s) 
	{
	    s = Normalizer.normalize(s, Normalizer.Form.NFD);
	    s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
	    return s;
	}
	private boolean isDicFile(String graphFilePath){
		if(graphFilePath != null && graphFilePath.trim() != ""){
			int i = graphFilePath.lastIndexOf('.');
		      if(i > 0 &&  i < graphFilePath.length() - 1){
		    	  String suffixe=graphFilePath.substring(i+1).toLowerCase();
		    	  return suffixe.equals("dic");
		      } 
		}
		return false;
	}
	
	private void constructResultPanel(JPanel main) {
		main.add(new JScrollPane(textArea), BorderLayout.CENTER);
	}

	private void constructSearchPanel(JPanel main){
		JComponent topPannel = new JPanel(new GridBagLayout());
		topPannel.setBorder(new EmptyBorder(5, 5, 5, 5));
		final JLabel label = new JLabel("Please select dictionary");
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 0;
		topPannel.add(label, gbc);
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		graphFilePath.setPreferredSize(new Dimension(100,25));
		topPannel.add(graphFilePath, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		topPannel.add(setGraphFile, gbc);
		final JLabel label2 = new JLabel("Enter text");
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		topPannel.add(label2, gbc);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		inputText.setPreferredSize(new Dimension(100,25));
		topPannel.add(inputText, gbc);
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		graphList = new JComboBox(graphNameList.toArray());
		graphList.setPreferredSize(new Dimension(100,25));
		topPannel.add(graphList, gbc);
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		topPannel.add(btnGenerateAgglutinedFormsFromFile, gbc);
		
		
		
//		graphList.setVisible(false);
		inputText.setBackground(Color.WHITE);
		graphFilePath.setBackground(Color.WHITE);

		main.add(topPannel);
	}

	
	Vector<String> getDicList(File dir) {
		final Vector<String> v = new Vector<String>();
		if (!dir.exists()) {
			return v;
		}
		final File files_list[] = dir.listFiles();
		for (final File aFiles_list : files_list) {
			final String name = aFiles_list.getAbsolutePath();
			if (!aFiles_list.isDirectory()
					&& (name.endsWith(".bin") || name.endsWith(".BIN"))) {
				v.add(aFiles_list.getName());
			}
		}
		return v;
	}
	
	private Map<String,String> formatListMap(List<String> inflectionList){
		Map<String,String> mapInflection = new HashMap<String,String>();
		for (String elt : inflectionList) {
			String code = formatCode(elt);
			String flechi = getFormFlechie(elt);
			mapInflection.put(code, flechi);
		}
		return mapInflection;
	}
	
	private String formatCode(String line){
		String[] tempCodeSplited = line.split(DOT_SEPARATOR)[1].split(PLUS_SEPARATOR);
		return tempCodeSplited[0]+"+"+tempCodeSplited[tempCodeSplited.length-1]+"+"+tempCodeSplited[1];
	}
	
	private String getFormFlechie(String line){
		return line.split(COMMMA_SEPARATOR)[0];
	}
	
	private void buildAndLaunchCommand(List<String> graphNameList,List<String> extractedLines){	
		final MultiCommands commands = new MultiCommands();		
		final File inflectionDir = ConfigManager.getManager().getInflectionDir();
		for (String line : extractedLines) {
			File tempFile = writeInTempFile(line,inflexionFolderPath+File.separator+stripAccents(line.split(COMMMA_SEPARATOR)[0])+tmpSuffix);
			File f = tempFile;
			String tmp = f.getAbsolutePath();
			final int point = tmp.lastIndexOf('.');
			final int separator = tmp.lastIndexOf(File.separatorChar);
			if (separator < point) {
				tmp = tmp.substring(0, point);
			}
			tmp = tmp + flxSuffix;
			final File resultDic = new File(tmp);
			MultiFlexCommand command = new MultiFlexCommand().delas(f)
					.result(resultDic)
					.alphabet(ConfigManager.getManager().getAlphabet(null))
					.repository().dir(new File(inflectionDir.getAbsolutePath()));
			commands.addCommand(command);
		}
		for (String graphName : graphNameList) {
			graphName = stripAccents(graphName);
			File fst2;
			File list;
			Fst2ListCommand cmd = new Fst2ListCommand();
			final Grf2Fst2Command grfCmd = new Grf2Fst2Command();
			cmd = cmd.noLimit();
			cmd = cmd.separateOutputs(true);
			grfCmd.grf(new File(getGraphNamePath(graphName)))
			.enableLoopAndRecursionDetection(true).repositories()
			.emitEmptyGraphWarning().displayGraphNames();
			list = new File(exploreGrapheFolderPath, graphName +".txt");
			fst2 = new File(FileUtil.getFileNameWithoutExtension(getGraphNamePath(graphName)) + ".fst2");
			cmd = cmd.listOfPaths(fst2, list);
			commands.addCommand(grfCmd);
			commands.addCommand(cmd);
		}
		
		Launcher.exec(commands, true, new ConjugaisonToDo(), false);
	}
	
	private String getGraphNamePath(String graphName){
		return Config.getUserCurrentLanguageDir()+File.separator+"DELA"+File.separator+graphName+".grf";
	}
	
	private List<String> getGraphNameListPath(List<String> extractedLines,String selectedGraphName){
		List<String> graphNameListPath = new ArrayList<String>();
		if(selectedGraphName != null && !selectedGraphName.equals(ALL)){
			graphNameListPath.add(selectedGraphName);
		}else{
			for (String line : extractedLines) {
				if(line != null && line.trim() != ""){
					String graphName = line.split(PLUS_SEPARATOR).length > 1 ?line.split(PLUS_SEPARATOR)[1] :"default";
					if(!graphNameListPath.contains(graphName)){
						graphNameListPath.add(graphName);
					}
				}
			}	
		}
		
		return graphNameListPath;
	}
	
	private String cleanString(String word){
		String cleanedString = "";
		for(int i = 0;i<word.length();i++){
			if(!exCludeChar.contains(Character.toString(word.charAt(i)))){
				cleanedString = cleanedString + word.charAt(i);
			}
		}
		return cleanedString;
	}
	
	private File writeInFile(ArrayList<String>  wordList,String tempInflectionFilePath) {
		File tempFile = new File(tempInflectionFilePath);
		FileUtil.write(wordList, tempFile);
		return tempFile;
	}
	
	private void setResultFileName(){
		String selectedGraphName = getSelectedGraphName();
		if(inputText.getText()!=null && !inputText.getText().trim().isEmpty()){
			resultFileName = stripAccents(inputText.getText());
		}else if(selectedGraphName != null && !selectedGraphName.equals(ALL)){
			resultFileName = selectedGraphName;
		}else{
			String filePath = graphFilePath.getText();
			if(filePath != null && filePath.trim() != ""){
				final int point = filePath.lastIndexOf('.');
				final int separator = filePath.lastIndexOf(File.separatorChar);
				if (separator < point) {
					resultFileName = stripAccents(filePath.substring(separator, point));
				}
			}else{
				resultFileName = "nisyResultTsyNety";
			}
		}
	}
	
	private File writeInTempFile(String wordFromRadical,String tempInflectionFilePath) {
		File tempFile = new File(tempInflectionFilePath);
		FileUtil.write(wordFromRadical, tempFile);
		return tempFile;
	}
	
	protected ArrayList<String>  buildListResultFromExploredGraphFiles(){
		ArrayList<String>  resultLines = new ArrayList<String>();
		for(String line:extractedLines){
			String graphName =line.split(PLUS_SEPARATOR).length>1? line.split(PLUS_SEPARATOR)[1]:"default";
			String[] tabLemmaCode = line.split(COMMMA_SEPARATOR);
			List<String> esploreGraphResult = extractLinesFromFile(exploreGrapheFolderPath+File.separator+stripAccents(graphName)+".txt",ENCODE_UTF_16,null,null);
			List<String> inflectionList = extractLinesFromFile(inflexionFolderPath+File.separator+stripAccents(tabLemmaCode[0])+tmpFlxSuffix,ENCODE,null,null);
			Map<String,String> formatedInflectionMap = formatListMap(inflectionList);
			List<String> resultList = generateResultList(esploreGraphResult, formatedInflectionMap,tabLemmaCode[0],tabLemmaCode.length>1? tabLemmaCode[1]:"default");
			resultLines.addAll(resultList);
		}
		return resultLines;
	}
	
	private List<String> generateResultList(List<String> esploreGraphResult,Map<String,String> formatedInflectionMap,String lemma,String code){
		List<String> resultList = new ArrayList<String>();
		for (String line : esploreGraphResult) {
			for (Map.Entry<String,String> e : formatedInflectionMap.entrySet()) {
				if(line.contains(e.getKey())){
					String[] newLine = line.replace(e.getKey(), e.getValue()).split(DOUBLESLASH_SEPARATOR);
					String cleanWord = cleanString(newLine[0]);
					String formatedCode = newLine[1].replace(placeHolderLemma, lemma).replace(placeHolderCode, code).replace(placeHolderVerb, cleanWord);
					resultList.add(cleanWord +","+formatedCode);
					break;
				}
			}
		}
		return resultList;
	}

	protected File builWordFromFile(String filepath, List<String> inflectionList,String lemma,String code) {
		Map<String,String> mapInflection = formatListMap(inflectionList);
		ArrayList<String> resultList = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath),ENCODE_UTF_16));
			String line = br.readLine();
		    while (line != null) {
		    	for (Map.Entry<String,String> e : mapInflection.entrySet()) {
					if(line.contains(e.getKey())){
						String newLine = line.replace(e.getKey(), e.getValue());
						String newLineClean = cleanString(newLine).replace("V.LEMMA", lemma).replace("V.CODE", code).replace(DOUBLESLASH_SEPARATOR, COMMMA_SEPARATOR);
						resultList.add(newLineClean);
						break;
					}
				}
		        line = br.readLine();
		    }
		    br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File f = new File(resultPath);
		FileUtil.write(resultList, f);
		return f;
	}
	
	protected Map<String, List<String>> buildWorlsdByCodes(Map<String, String> mapWords) {
		if(mapWords!=null && !mapWords.isEmpty()){
			Map<String, List<String>> mapCodeWord = new HashMap<>();
			for (Map.Entry<String,String> e : mapWords.entrySet()){
				if(!mapCodeWord.containsKey(e.getKey())){
					List<String> Words = getWorldsByCode(e.getKey(),mapWords);
					mapCodeWord.put(e.getKey(), Words);
				}
			}
			return mapCodeWord;
		}
		return null;
	}
	
	private void deleteAllTempFiles(String[] pathList){
		for (String string : pathList) {
			File index = new File(string);
			if(index.exists()){
				String[]entries = index.list();
				for(String s: entries){
				    File currentFile = new File(index.getPath(),s);
				    currentFile.delete();
				}
			}
		}
	}
	
	private String getCurrentDateTime(){
		DateFormat dateFormat = new SimpleDateFormat("-yyyy-MM-dd-HH-mm-ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	private List<String> getWorldsByCode(String key, Map<String, String> mapWords) {
		List<String> words = new ArrayList<>();
		for (Map.Entry<String,String> e : mapWords.entrySet()){
			if(e.getKey().equals(key)){
				words.add(e.getValue());
			}
		}
		return words;
	}
	
	class ConjugaisonToDo implements ToDo {
		@Override
		public void toDo(boolean success) {
			ArrayList<String> resultList = buildListResultFromExploredGraphFiles();
			String fileName = resultFolderPath+File.separator+resultFileName+getCurrentDateTime()+".txt";
			writeInFile(resultList, fileName);
			
//			writeUTF8WithoutBOM(resultList, fileName);
//			removeBOM(resultFile);
			textArea.load(new File(fileName));
//			deleteAllTempFiles(new String[]{inflexionFolderPath,exploreGrapheFolderPath});
		}
	}
	
	public class GrfFiltre extends FileFilter {
		   String [] lesSuffixes;
		   String  laDescription;
		   public GrfFiltre(String []lesSuffixes, 
		                         String laDescription){
		      this.lesSuffixes = lesSuffixes;
		      this.laDescription = laDescription;
		   }
		   boolean appartient( String suffixe ){
		      for( int i = 0; i<lesSuffixes.length; ++i)
		         if(suffixe.equals(lesSuffixes[i]))
		            return true;
		         return false;
		   }
		   public boolean accept(File f) {
		      if (f.isDirectory())  return true;
		      String suffixe = null;
		      String s = f.getName();
		      int i = s.lastIndexOf('.');
		      if(i > 0 &&  i < s.length() - 1)
		         suffixe=s.substring(i+1).toLowerCase();
		      return suffixe!=null&&appartient(suffixe);
		   }
		   public String getDescription() {
		      return laDescription;
		   }
		}
}
