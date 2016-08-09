package org.gramlab.core.gramlab.svn;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.umlv.unitex.console.Couple;
import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.process.EatStreamThread;
import org.gramlab.core.umlv.unitex.process.Launcher;
import org.gramlab.core.umlv.unitex.process.ProcessInfoThread;
import org.gramlab.core.umlv.unitex.process.commands.SvnCommand;
import org.gramlab.core.umlv.unitex.process.list.ProcessOutputList;

public class SvnExecutor {
	
	public static SvnCommandResult exec(SvnCommand c,ProcessOutputList list) {
		if (c==null) {
			throw new IllegalArgumentException("Invalid null SvnCommand");
		}
		try {
			Process p=Runtime.getRuntime().exec(c.getCommandArguments());
			if (list==null) {
				new EatStreamThread(p.getInputStream(),null).start();
			} else {
				new ProcessInfoThread(list,p.getInputStream(),null,false).start();
			}
			GetStreamThread waiter=new GetStreamThread(p.getErrorStream());
			waiter.start();
			int ret=p.waitFor();
			if (ret==0) return new SvnCommandResult(SvnOpResult.OK,"");
			return getSvnError(waiter.getStreamContent());			
		} catch (final IOException e) {
			return new SvnCommandResult(SvnOpResult.UNKNOWN_ERROR,"");
		} catch (InterruptedException e) {
			return new SvnCommandResult(SvnOpResult.UNKNOWN_ERROR,"");
		}
	}


	
	public static String getCommandOutput(SvnCommand c) {
		if (c==null) {
			throw new IllegalArgumentException("Invalid null SvnCommand");
		}
		try {
			Process p=Runtime.getRuntime().exec(c.getCommandArguments());
			GetStreamThread waiterOut=new GetStreamThread(p.getInputStream());
			GetStreamThread waiterErr=new GetStreamThread(p.getErrorStream());
			waiterOut.start();
			waiterErr.start();
			int ret=p.waitFor();
			if (ret==0) return waiterOut.getStreamContent();
			return null;			
		} catch (final IOException e) {
			return null;
		} catch (InterruptedException e) {
			return null;
		}
	}
	
	
	public static SvnCommandResult getSvnError(ProcessOutputList stderr) {
		StringBuilder b=new StringBuilder();
		for (int i=0;i<stderr.getModel().getSize();i++) {
			Couple c=(Couple) stderr.getModel().get(i);
			b.append(c.getString());
			b.append("\n");
		}
		String err=b.toString();
		return getSvnError(err);
	}
	public static SvnCommandResult getSvnError(String err) {
		SvnOpResult op;
		if (err.contains("svn: E170001: Authentication required for")) {
			op=SvnOpResult.AUTHENTICATION_REQUIRED;
		} else if (err.contains("svn: E170001: Commit failed")) {
			op=SvnOpResult.COMMIT_FORBIDDEN;
		} else if (err.contains("is out of date")) {
			op=SvnOpResult.OUT_OF_DATE;
		} else if (err.contains("svn: E155007")) {
			op=SvnOpResult.NOT_A_WORKING_COPY;
		} else if (err.contains("Commit failed")) {
			op=SvnOpResult.COMMIT_FAILED;
		} else {
			op=SvnOpResult.UNKNOWN_ERROR;
		}
		//System.err.println(err);
		return new SvnCommandResult(op,err);
	}
	
	
	
	public static HashMap<File,SvnInfo> getSvnInfos(GramlabProject project,ArrayList<File> removedFiles) {
		HashMap<File,SvnInfo> map=new HashMap<File,SvnInfo>();
		removedFiles.clear();
		exploreDirForSvnInfos(project,map,removedFiles);
		return map;
	}

	private static void exploreDirForSvnInfos(GramlabProject project,HashMap<File,SvnInfo> map,
			ArrayList<File> removedFiles) {
		File dir=project.getProjectDirectory();
		File f=new File(dir,".svn");
		if (!f.exists() || !f.isDirectory()) {
			/* We don't even try to invoke 'svn info' if there is no chance
			 * for it to find results
			 */
			return;
		}
		try {
			SvnCommand c=new SvnCommand().info(dir,true);
			Process p=Runtime.getRuntime().exec(c.getCommandArguments(),null,dir);
			GetStreamThread waiterOut=new GetStreamThread(p.getInputStream());
			GetStreamThread waiterErr=new GetStreamThread(p.getErrorStream());
			waiterOut.start();
			waiterErr.start();
			p.waitFor();
			SvnCommandResult result=getSvnError(waiterErr.getStreamContent());
			if (result.getOp()!=SvnOpResult.NOT_A_WORKING_COPY) {
				SvnInfo.analyzeInfos(dir,waiterOut.getStreamContent(),map);
				/* Now that we know the versioned files, we want to get
				 * the status information about both them and the unversioned/added
				 * ones
				 */
				updateStatusInfos(project,map,removedFiles);
			} 
		} catch (final IOException e) {
			return;
		} catch (InterruptedException e) {
			return;
		}
	}


	private static void updateStatusInfos(GramlabProject p, HashMap<File, SvnInfo> map,
			ArrayList<File> removedFiles) {
		SvnStatusInfo info=getSvnStatusInfo(p);
		if (info==null) return;
		removedFiles.clear();
		for (String name:info.getUnversionedFiles()) {
			File f=p.getFileFromNormalizedName(name);
			map.put(f,new SvnInfo(SvnStatus.UNVERSIONED));
			if (f.isDirectory()) {
				/* For an unversioned directory, we have to add
				 * all its children also as unversioned
				 */
				addChildrenAsUnversioned(map,f);
			}
		}
		for (String name:info.getAddedFiles()) {
			File f=p.getFileFromNormalizedName(name);
			map.put(f,new SvnInfo(SvnStatus.ADDED));
		}
		for (String name:info.getModifiedFiles()) {
			File f=p.getFileFromNormalizedName(name);
			SvnInfo i=map.get(f);
			i.setStatus(SvnStatus.MODIFIED);
			/* We also want the modified status pink star 
			 * to appear on all parents directories up to src
			 */
			while (!(f=f.getParentFile()).equals(p.getProjectDirectory())) {
				i=map.get(f);
				i.setStatus(SvnStatus.MODIFIED);
			}
		}
		for (String name:info.getRemovedFiles()) {
			File f=p.getFileFromNormalizedName(name);
			map.put(f,new SvnInfo(SvnStatus.DELETED));
			removedFiles.add(f);
		}
		for (String name:info.getConflictFiles()) {
			File f=p.getFileFromNormalizedName(name);
			map.put(f,new SvnInfo(SvnStatus.CONFLICT));
		}
	}


	private static void addChildrenAsUnversioned(HashMap<File, SvnInfo> map,
			File f) {
		if (!map.containsKey(f)) {
			map.put(f,new SvnInfo(SvnStatus.UNVERSIONED));
		}
		if (!f.isDirectory()) return;
		File[] list=f.listFiles();
		if (list==null) return;
		for (File tmp:list) {
			addChildrenAsUnversioned(map,tmp);
		}
	}



	public static SvnStatusInfo getSvnStatusInfo(GramlabProject project) {
		try {
			SvnCommand c=new SvnCommand().status();
			Process p=Runtime.getRuntime().exec(c.getCommandArguments(),null,project.getProjectDirectory());
			GetStreamThread waiterOut=new GetStreamThread(p.getInputStream());
			GetStreamThread waiterErr=new GetStreamThread(p.getErrorStream());
			waiterOut.start();
			waiterErr.start();
			p.waitFor();
			SvnCommandResult result=getSvnError(waiterErr.getStreamContent());
			if (p.exitValue()==0 || result.getOp()==SvnOpResult.OK) {
				return filterSvnStatusOutput(waiterOut.getStreamContent());
			}
			return null;
		} catch (final IOException e) {
			return null;
		} catch (InterruptedException e) {
			return null;
		}
	}


	private static SvnStatusInfo filterSvnStatusOutput(String streamContent) {
		SvnStatusInfo info=new SvnStatusInfo(null);
		Scanner scanner=new Scanner(streamContent);
		while (scanner.hasNextLine()) {
			String line=scanner.nextLine();
			String name=line.substring(8);
			if (!name.startsWith("src/")
				&& !name.startsWith("src\\")
				&& !name.equals("pom.xml")
				&& !name.equals("project.preferences")
				&& !name.equals("project.versionable_config")) {
				continue;
			}
			SvnStatus status=SvnStatus.getStatus(line.charAt(0));
			if (status==null) continue;
			switch (status) {
			case UNMODIFIED: 
			case IGNORED: 
			case EXTERNAL: break;
			case MODIFIED: 
			case REPLACED: 
			case TYPE_CHANGED: info.addModifiedFile(name); break;
			case ADDED: info.addAddedFile(name); break;
			case UNVERSIONED: info.addUnversionedFile(name); break;
			case DELETED:
			case MISSING: {
				info.addRemovedFile(name); break;
			}
			case CONFLICT: info.addConflictFile(name); break;
			}
		}
		return info;
	}


	public static void ignoreFiles(ArrayList<File> files) {
		for (File f: files) {
			ignoreFile(f);
		}
	}
	
	
	public static void ignoreFile(File f) {
		if (f==null || !f.exists()) return;
		ArrayList<String> patterns=getIgnorePatternsForDir(f.getParentFile());
		if (patterns==null) return;
		if (patterns.contains(f.getName())) {
			/* Nothing to do if the file is already ignored */
			return;
		}
		File tmp=new File(f.getParentFile(),"..list-ignore");
		patterns.add(f.getName());
		FileUtil.write(patterns,tmp);
		SvnCommand cmd=new SvnCommand().setIgnoreList(f.getParentFile(),tmp);
		SvnExecutor.exec(cmd,null);
		tmp.delete();
	}



	private static ArrayList<String> getIgnorePatternsForDir(File dir) {
		try {
			SvnCommand c=new SvnCommand().getIgnoreList(dir);
			Process p=Runtime.getRuntime().exec(c.getCommandArguments(),null,dir);
			GetStreamThread waiterOut=new GetStreamThread(p.getInputStream());
			GetStreamThread waiterErr=new GetStreamThread(p.getErrorStream());
			waiterOut.start();
			waiterErr.start();
			p.waitFor();
			SvnCommandResult result=getSvnError(waiterErr.getStreamContent());
			if (p.exitValue()==0 || result.getOp()==SvnOpResult.OK) {
				return toLineList(waiterOut.getStreamContent());
			}
			return null;
		} catch (final IOException e) {
			return null;
		} catch (InterruptedException e) {
			return null;
		}
	}


	private static ArrayList<String> toLineList(String streamContent) {
		ArrayList<String> lines=new ArrayList<String>();
		Scanner s=new Scanner(streamContent);
		while (s.hasNextLine()) {
			lines.add(s.nextLine());
		}
		s.close();
		return lines;
	}



	public static void deleteURL(String url) {
		SvnCommand c=new SvnCommand().delete(url,"Deleting project");
		Launcher.execWithoutTracing(c);
	}


}
