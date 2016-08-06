package fr.gramlab.project.config.maven;

import java.io.File;

import fr.gramlab.GramlabConfigManager;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.io.Encoding;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.commands.CommandBuilder;
import fr.umlv.unitex.process.commands.MultiCommands;
import fr.umlv.unitex.process.commands.RmCommand;

public class MvnCommand extends CommandBuilder {

	enum MvnCmdType {
		PACKAGE("package"),
		INSTALL("install"),
		DEPLOY("deploy"),
		UNPACK_DEP("dependency:unpack-dependencies"),
		CLEAN("clean");
		
		private String cmd;
		public String getCmd() {
			return cmd;
		}
		private MvnCmdType(String cmd) {
			this.cmd=cmd;
		}
	}
	
	MvnCommand(File POM,MvnCmdType cmd,boolean clean) {
		this();
		if (clean && !cmd.equals(MvnCmdType.CLEAN)) {
			element("clean");
		}
		element("-f");
		protectElement(POM.getAbsolutePath());
		protectElement(cmd.getCmd());
		protectElement("-Dunitextoollogger="+ConfigManager.getManager().getUnitexToolLogger()
				.getAbsolutePath());
	}

	MvnCommand() {
		super(false);
		if (Config.getSystem()==Config.WINDOWS_SYSTEM) {
			element("cmd");
			element("/c");
		}
		element("mvn");
	}
	
	public MvnCommand version() {
		element("-v");
		return this;
	}
	
	public static boolean mvnInstalled() {
		MvnCommand cmd=new MvnCommand().version();
		return (0==Launcher.execWithoutTracing(cmd));
	}
	
	public static boolean gramlabPackageInstalled() {
		File dir=getLocalRepositorydir();
		if (dir==null || !dir.exists()) return false;
		File d2=new File(new File(dir,"org"),"gramlab");
		File d3=new File(new File(d2,"gramlab-assembly-descriptors"),"0.0.2");
		File f1=new File(d3,"gramlab-assembly-descriptors-0.0.2.pom");
		File f2=new File(d3,"gramlab-assembly-descriptors-0.0.2.jar");
		if (!f1.exists() || !f2.exists()) {
			return false;
		}
		File appDir=ConfigManager.getManager().getApplicationDirectory();
		File pom=new File(new File(appDir,"assembly"),"pom.xml");
		if (!sameFiles(f1,pom)) {
			return false;
		}
		pom=new File(appDir,"pom.xml");
		d3=new File(new File(d2,"gramlab-super-pom"),"0.0.1");
		f1=new File(d3,"gramlab-super-pom-0.0.1.pom");
		return f1.exists() && f2.exists() && sameFiles(pom,f1);
	}

	/**
	 * Returns true iff the given files have exactly the same content.
	 */
	private static boolean sameFiles(File a,File b) {
		long lA=a.length();
		long lB=b.length();
		if (lA!=lB) return false;
		String sA=Encoding.getContent(a);
		String sB=Encoding.getContent(b);
		return sA.equals(sB);
	}

	private static File getLocalRepositorydir() {
		File m2=new File(new File(System.getProperty("user.home")),".m2");
		if (!m2.exists() || !m2.isDirectory()) return null;
		File settings=new File(m2,"settings.xml");
		File dir=getLocalRepositoryDirFromSettingsXml(settings);
		if (dir!=null) return dir;
		dir=new File(m2,"repository");
		if (!dir.exists()) return null;
		return dir;
	}

	private static File getLocalRepositoryDirFromSettingsXml(File settings) {
		if (!settings.exists() || !settings.isFile()) return null;
		return PomIO.readLocalRepositoryFromSettingsXml(settings);
	}

	/**
	 * We use a tmp directory in the workspace directory, because the
	 * 'mvn install' command requires to be run in a writable directory,
	 * and App might not be one.
	 */
	public static MultiCommands getGramlabPackageInstallCommands() {
		MultiCommands c=new MultiCommands();
		File tmpDir=new File(GramlabConfigManager.getWorkspaceDirectory(),"..tmp_maven");
		File appDir=ConfigManager.getManager().getApplicationDirectory();
		tmpDir.mkdir();
		FileUtil.copyFile(new File(appDir,"pom.xml"),new File(tmpDir,"pom.xml"));
		FileUtil.copyDirRec(new File(appDir,"assembly"),new File(tmpDir,"assembly"));
		
		File pomAssembly=new File(new File(tmpDir,"assembly"),"pom.xml");
		c.addCommand(new MvnCommand(pomAssembly,MvnCmdType.INSTALL,true));
		File pomGramlab=new File(tmpDir,"pom.xml");
		c.addCommand(new MvnCommand(pomGramlab,MvnCmdType.INSTALL,true));
		c.addCommand(new RmCommand().rm(tmpDir));
		return c;
	}
	
}
