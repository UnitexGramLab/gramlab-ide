package fr.gramlab.svn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SvnUtil {
	
	public static void createTargetListFile(File f,ArrayList<String> list) {
		FileOutputStream stream;
		try {
			stream = new FileOutputStream(f);
			/* We use the default encoding, because this file will be read by the svn
			 * client, and I (SP) did not find any precision on the file format
			 * expected by the --targets option, so I assume that the system encoding 
			 * will be used.
			 */
			OutputStreamWriter writer=new OutputStreamWriter(stream);
			for (String s:list) {
				writer.write(s+"\n");
			}
			writer.close();
			stream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void createTargetListFile2(File f,ArrayList<File> list) {
		FileOutputStream stream;
		try {
			stream = new FileOutputStream(f);
			/* We use the default encoding, because this file will be read by the svn
			 * client, and I (SP) did not find any precision on the file format
			 * expected by the --targets option, so I assume that the system encoding 
			 * will be used.
			 */
			OutputStreamWriter writer=new OutputStreamWriter(stream);
			for (File file:list) {
				writer.write(file.getAbsolutePath()+"\n");
			}
			writer.close();
			stream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
