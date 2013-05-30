package fr.umlv.unitex.cassys;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;

import fr.umlv.unitex.cassys.ConfigurationFileAnalyser.EmptyLineException;
import fr.umlv.unitex.cassys.ConfigurationFileAnalyser.InvalidLineException;
import fr.umlv.unitex.config.Config;



/**
 * This ShareTransducerList class provides the ability to easily share transducer files between users. The
 * feature is obtained by translating absolute path used by unitex to relative path (export action) and 
 * by translating relative path to absolute path (import action).
 * 
 * 
 * @author David Nott, Nathalie Friburger (nathalie.friburger@univ-tours.fr)
 *
 */
public class ShareTransducerList {
	
	private final String shareDirectoryName;
	private final String relativeRootDirectory;
	
	
	public ShareTransducerList(){
		shareDirectoryName = "Share";
		relativeRootDirectory= Config.getCurrentGraphDir().getPath();
	}
	
	
	/**
	 * Export <em>absoluteTransducerList</em> file. 
	 * The exported file is copied in the <em>shareDirectoryName</em> file. The relative root directory
	 * is <em>relativeRootDirectory</em>.
	 *
	 * 
	 * @param absoluteTransducerList
	 * @return the file containing the export
	 * @throws IOException
	 * @throws FormatFileException
	 * @throws RequiredDirectoryNotExist
	 */
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
	
	
	/**
	 * Import <em>absoluteTransducerList</em> file. 
	 * The imported file is copied from the <em>shareDirectoryName</em> file. The relative root directory
	 * is <em>relativeRootDirectory</em>.
	 *
	 * 
	 * @param absoluteTransducerList
	 * @return the file containing the export
	 * @throws IOException
	 * @throws FormatFileException
	 * @throws RequiredDirectoryNotExist
	 */
	public File importList(File shareFile) throws IOException, FormatFileException  {
		
		final File transducerListFile = new File(Config.getCassysDir(),shareFile.getName());
		
		portList(shareFile, transducerListFile, false);
		
		return transducerListFile;
		
	}
	
	
	/**
	 * This method read each file name path in <em>source</em>, 
	 * absolutize it or relativize it according to the <em>is_export</em> value and copy it to
	 * <em>target</em>
	 * 
	 * @param source the source file
	 * @param target the target file
	 * @param is_export 
	 * @throws IOException
	 * @throws FormatFileException
	 */
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
				if (cfa.isDisabled()) {
					fw.write(" Enabled");
				} else {
					fw.write(" Disabled");
				}
				if(cfa.isStar()){
					fw.write(" *");
				} else {
					fw.write(" 1");
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
	
	
	/**
	 * Converts an absolute path into a relative path
	 * 
	 * @param fileName
	 * @return an absolute path name file
	 * @throws NotAnAbsolutePathException
	 */
	public String relativize(String fileName) throws NotAnAbsolutePathException{
		
		// check whether the file is absolute
		final File file = new File(fileName);
		if(!file.isAbsolute()){
			throw new NotAnAbsolutePathException();
		}
		
		String relativePath = new String();
		
		
		String file_separator;
		if(Config.getSystem() == Config.WINDOWS_SYSTEM){
			// The windows file separator char is also char protected in regex java class !
			// So we need to protect it
			file_separator = "\\"+File.separator;
		} else {
			file_separator=File.separator;
		}
		
		String filePath[]= fileName.split(file_separator);
		String rootDirectoryPath[] = relativeRootDirectory.split(file_separator);
		
		
		
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
	
	
	/**
	 * Converts an relative path into an absolute path
	 * 
	 * 
	 * @param fileName a file name path
	 * @return a relative path name file
	 * @throws IOException 
	 * @throws AlreadyAbsolutePathException
	 */
	public String absolutize(String fileName) throws IOException, AlreadyAbsolutePathException{
		
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
