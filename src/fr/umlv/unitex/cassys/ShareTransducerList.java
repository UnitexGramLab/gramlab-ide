package fr.umlv.unitex.cassys;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;

import fr.umlv.unitex.cassys.ConfigurationFileAnalyser.EmptyLineException;
import fr.umlv.unitex.cassys.ConfigurationFileAnalyser.InvalidLineException;
import fr.umlv.unitex.config.Config;

public class ShareTransducerList {
	
	private final String shareDirectoryName;
	private final String relativeRootDirectory;
	
	
	public ShareTransducerList(){
		shareDirectoryName = "Share";
		relativeRootDirectory= Config.getCurrentGraphDir().getPath();
	}
	
	public File exportList(File absoluteTransducerList) throws IOException, FormatFileException,RequiredDirectoryNotExist {
		
		// test whether the Share directory exists
		final File shareDirectory = new File(Config.getCassysDir(),shareDirectoryName);
		if(!shareDirectory.isDirectory()){
			throw new RequiredDirectoryNotExist(Config.getCassysDir().getPath() + File.separator + shareDirectoryName + " not found");
		}
		
		final File shareFile = new File(Config.getCassysDir(), shareDirectoryName + File.separator + absoluteTransducerList.getName());
		
		portList(absoluteTransducerList, shareFile, true);
		
		return shareFile;
		
	}
	
	public File importList(File shareFile) throws IOException, FormatFileException  {
		
		final File transducerListFile = new File(Config.getCassysDir(),shareFile.getName());
		
		portList(shareFile, transducerListFile, false);
		
		return transducerListFile;
		
	}
	
	
	
	private void portList(File source, File target, boolean is_export) throws IOException, FormatFileException{
		
		final BufferedWriter fw = new BufferedWriter(new FileWriter(target));
		
		LineNumberReader r = new LineNumberReader(new FileReader(source));
		String FormatErrorLine = "";
		
		String line;
		while ((line = r.readLine()) != null) {
			try {
				
				
				final ConfigurationFileAnalyser cfa = new ConfigurationFileAnalyser(
						line);
				
				
				if(is_export){
					
					try {
						final String fileName = relativize(cfa.getFileName());
						
						fw.write("\"" + fileName + "\" ");
					} catch (NotAnAbsolutePathException e) {
						
						// if it is already relativized, just copy file name
						fw.write("\"" + cfa.getFileName() + "\" ");
						// TODO keep track of the error
					}

				} else {
					
					try {
						final String fileName = absolutize(cfa.getFileName());
						fw.write("\"" + fileName + "\" ");
					} catch (AlreadyAbsolutePathException e) {
						fw.write("\"" + cfa.getFileName() + "\" ");
					}
					
				}
				
								
				if (cfa.isMergeMode()) {
					fw.write("Merge");
				} else {
					fw.write("Replace");
				}
				fw.newLine();
				
			} catch (final EmptyLineException e) {
				/*NOP*/
			} catch (final InvalidLineException e) {
				// keep track of the error to warn the user
				FormatErrorLine = FormatErrorLine
						.concat("line " + r.getLineNumber() + ": "
								+ e.getMessage());
			}
		}
		
		
		fw.close();
		r.close();
		
		if (!FormatErrorLine.equals("")) {
			throw new FormatFileException(FormatErrorLine);
		}
		
	}
	
	
	
	private String relativize(String fileName) throws NotAnAbsolutePathException{
		
		// check whether the file is absolute
		final File file = new File(fileName);
		if(!file.isAbsolute()){
			throw new NotAnAbsolutePathException();
		}
		
		String relativePath = new String();
		
		String filePath[]= fileName.split(File.separator);
		String rootDirectoryPath[] = relativeRootDirectory.split(File.separator);
		
		int level = 1;
		if(Config.getCurrentSystem() == Config.LINUX_SYSTEM){
			level = 1;
		}
		
		
		
		while(level<filePath.length && level<rootDirectoryPath.length && rootDirectoryPath[level].equals(filePath[level])){
			level++;
		}
		
		String parentDirectory = new String("..").concat(File.separator);
		for(int i=level;i<rootDirectoryPath.length;i++){
			relativePath = relativePath.concat(parentDirectory);
		}
		
		for(int i=level;i<filePath.length;i++){
			// do not add path separator on the first item
			if(i!=level){
				relativePath = relativePath.concat(File.separator);
			}
			relativePath = relativePath.concat(filePath[i]);
		}
		
		
		return relativePath;
	}
	
	
	
	private String absolutize(String fileName) throws IOException, AlreadyAbsolutePathException{
		
		final File file = new File(fileName);
		if(file.isAbsolute()){
			throw new AlreadyAbsolutePathException();
		}
		
		final String absolutePath = new String(relativeRootDirectory).concat(File.separator).concat(fileName);
		final String canonicalPath = new File(absolutePath).getCanonicalPath();
		
		return canonicalPath;
	}
	
	
	
	public class FormatFileException extends Exception {
		public FormatFileException (String s){
			super(s);
		}
		public FormatFileException (){
			/*NOP*/
		}
	}
	
	public class NotAnAbsolutePathException extends Exception{
		
		public NotAnAbsolutePathException(){
			/*NOP*/
		}
	}
	
	public class AlreadyAbsolutePathException extends Exception{
		public AlreadyAbsolutePathException(){
			/*NOP*/
		}
	}
	
	public class RequiredDirectoryNotExist extends Exception{
		public RequiredDirectoryNotExist(){
			/*NOP*/
		}
		public RequiredDirectoryNotExist(String s){
			super(s);
		}
	}
	
}
