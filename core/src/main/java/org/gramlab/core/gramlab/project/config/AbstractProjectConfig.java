package fr.gramlab.project.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import fr.gramlab.project.GramlabProject;

public abstract class AbstractProjectConfig {
	
	private final static String FILE="\tFILE";

	public abstract void save(OutputStreamWriter s) throws IOException;
	
	protected static File readProjectRelativeFile(GramlabProject p, String name, BufferedReader s) throws IOException {
		String tmp=new StringProperty(name,null).load(s);
		if (tmp.equals("")) return null;
		if (File.pathSeparatorChar=='\\') {
			tmp=tmp.replaceAll("/","\\");
		} else {
			tmp=tmp.replaceAll("\\\\","/");
		}
		File f=new File(p.getProjectDirectory(),tmp);
		return f;
	}


	protected static ArrayList<File> readProjectRelativeFileList(String name, GramlabProject p,
			BufferedReader s) throws IOException {
		ArrayList<File> list=new ArrayList<File>();
		String tmp=new StringProperty(name,null).load(s);
		try {
			int n=Integer.valueOf(tmp);
			for (int i=0;i<n;i++) {
				File src=readProjectRelativeFile(p,FILE,s);
				list.add(src);
			}
		} catch (NumberFormatException e) {
			throw new IOException();
		}
		return list;
	}

	protected void saveProjectRelativeFileList(GramlabProject project,String name, ArrayList<File> list,
			OutputStreamWriter s) throws IOException {
		new StringProperty(name,""+list.size()).save(s);
		for (File f:list) {
			saveProjectRelativeFile(project,FILE,f,s);
		}
	}

	protected void saveProjectRelativeFile(GramlabProject project,String name,File f,OutputStreamWriter s) throws IOException {
		String tmp;
		if (f!=null) tmp=project.getRelativeFileName(f);
		else tmp="";
		if (tmp==null) throw new IOException();
		new StringProperty(name,tmp).save(s);
	}

	protected static File readFile(GramlabProject p, String name, BufferedReader s) throws IOException {
		String tmp=new StringProperty(name,null).load(s);
		if (tmp.equals("")) return null;
		File f=new File(tmp);
		/* A valid absolute file name */
		if (f.isAbsolute()) return f;
		/* Or a project-relative file name */
		return new File(p.getProjectDirectory(),tmp);
	}

	protected void saveFile(GramlabProject project,String name,File f,OutputStreamWriter s) throws IOException {
		String tmp="";
		if (f!=null) {
			tmp=project.getNormalizedFileName(f);
		}
		new StringProperty(name,tmp).save(s);
	}

	protected static boolean readBoolean(String name,BufferedReader s) throws IOException {
		String tmp=new StringProperty(name,null).load(s);
		return "true".equals(tmp);
	}

	protected void saveBoolean(String name,boolean b,OutputStreamWriter s) throws IOException {
		new StringProperty(name,b?"true":"false").save(s);
	}
	
	protected static int readInt(String name,BufferedReader s) throws IOException {
		String tmp=new StringProperty(name,null).load(s);
		try {
			return Integer.parseInt(tmp);
		} catch (NumberFormatException e) {
			throw new IOException("int value expected: "+tmp);
		}
	}

	protected void saveInt(String name,int n,OutputStreamWriter s) throws IOException {
		new StringProperty(name,""+n).save(s);
	}
	
}
