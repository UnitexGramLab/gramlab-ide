package org.gramlab.core.gramlab.project.config.maven;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.gramlab.project.config.maven.MvnCommand.MvnCmdType;
import org.gramlab.core.umlv.unitex.process.commands.MultiCommands;

public class Pom {
	
	private File POM;
	private Artifact artifact;
	private ArrayList<Artifact> dependencies;
	
	public File getPOM() {
		return POM;
	}

	public String getIdRepoSnapshot() {
		return idRepoSnapshot;
	}

	public String getUrlRepoSnapshot() {
		return urlRepoSnapshot;
	}

	public String getIdRepoRelease() {
		return idRepoRelease;
	}

	public String getUrlRepoRelease() {
		return urlRepoRelease;
	}

	/**
	 * See comment in setDistributionManagement
	 */
	private String idRepoSnapshot,urlRepoSnapshot;
	private String idRepoRelease,urlRepoRelease;
	
	public Pom(File POM,Artifact artifact) {
		this.POM=POM;
		this.artifact=artifact;
		this.dependencies=new ArrayList<Artifact>();
	}
	
	public MultiCommands getUnpackDependenciesCommand() {
		MultiCommands c=new MultiCommands();
		if (!MvnCommand.gramlabPackageInstalled()) {
			c.addCommand(MvnCommand.getGramlabPackageInstallCommands());
		}
		c.addCommand(new MvnCommand(POM,MvnCmdType.UNPACK_DEP,true));
		return c;
	}

	public MultiCommands getPackageCommand() {
		MultiCommands c=new MultiCommands();
		if (!MvnCommand.gramlabPackageInstalled()) {
			c.addCommand(MvnCommand.getGramlabPackageInstallCommands());
		}
		c.addCommand(new MvnCommand(POM,MvnCmdType.PACKAGE,false));
		return c;
	}

	public MultiCommands getInstallCommand() {
		MultiCommands c=new MultiCommands();
		if (!MvnCommand.gramlabPackageInstalled()) {
			c.addCommand(MvnCommand.getGramlabPackageInstallCommands());
		}
		c.addCommand(new MvnCommand(POM,MvnCmdType.INSTALL,false));
		return c;
	}

	public MultiCommands getDeployCommand() {
		MultiCommands c=new MultiCommands();
		if (!MvnCommand.gramlabPackageInstalled()) {
			c.addCommand(MvnCommand.getGramlabPackageInstallCommands());
		}
		c.addCommand(new MvnCommand(POM,MvnCmdType.DEPLOY,false));
		return c;
	}

	public MvnCommand getCleanCommand() {
		return new MvnCommand(POM,MvnCmdType.CLEAN,false);
	}
	
	/**
	 * Creates a simple pom.xml file that just inherits from the 
	 * gramlab super pom. We wrap the call to savePom in order
	 * to explicit the fact that one should always create a new empty
	 * pom when starting a new project.
	 */
	public boolean createEmptyPom(GramlabProject p) {
		return PomIO.savePom(this,p);
	}

	public File getFile() {
		return POM;
	}

	public Artifact getArtifact() {
		return artifact;
	}
	
	public void setArtifact(Artifact a) {
		this.artifact=a;
	}
	
	public ArrayList<Artifact> getDependencies() {
		return dependencies;
	}

	public void setDependencies(ArrayList<Artifact> dependencies) {
		this.dependencies=dependencies;
	}

	/**
	 * This method constructs the dependency list from the content found in the 
	 * pom file, if it exists.
	 */
	public void loadFromFile() {
		ArrayList<Artifact> list=PomIO.parsePOM(POM,this);
		this.artifact=list.get(0);
		dependencies.clear();
		for (int i=1;i<list.size();i++) {
			dependencies.add(list.get(i));
		}
	}
	
	/**
	 * For a valid invokation, parameters must be either all null or all non-null.
	 */
	public boolean setDistributionManagement(String idRelease,String urlRelease,
			String idSnapshot,String urlSnapshot) {
		if (idRelease==null && urlRelease==null && idSnapshot==null && urlSnapshot==null) {
			/* Every info to null => ok */
			idRepoRelease=null;
			urlRepoRelease=null;
			idRepoSnapshot=null;
			urlRepoSnapshot=null;
			return true;
		} else if (idRelease==null || urlRelease==null || idSnapshot==null || urlSnapshot==null) {
			throw new IllegalArgumentException();
		}
		if (!validRepoId(idRelease)) {
			JOptionPane
				.showMessageDialog(
				null,
				"Invalid repository ID: "+idRelease+"\nIt should match [A-Za-z0-9_\\-.]+",
				"Distribution management error", JOptionPane.ERROR_MESSAGE);
		}
		if (!validRepoId(idSnapshot)) {
			JOptionPane
				.showMessageDialog(
				null,
				"Invalid repository ID: "+idSnapshot+"\nIt should match [A-Za-z0-9_\\-.]+",
				"Distribution management error", JOptionPane.ERROR_MESSAGE);
		}
		if (!isValidUrl(urlRelease)) {
			JOptionPane
			.showMessageDialog(
			null,
			"Invalid repository URL: "+urlRelease,
			"Distribution management error", JOptionPane.ERROR_MESSAGE);
		}
		if (!isValidUrl(urlSnapshot)) {
			JOptionPane
			.showMessageDialog(
			null,
			"Invalid repository URL: "+urlSnapshot,
			"Distribution management error", JOptionPane.ERROR_MESSAGE);
		}
		idRepoRelease=idRelease;
		urlRepoRelease=urlRelease;
		idRepoSnapshot=idSnapshot;
		urlRepoSnapshot=urlSnapshot;
		return true;
	}

	private boolean isValidUrl(String url) {
		if (url==null || url.equals(""))	 {
			return false;
		}
		try {
			new URL(url);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}

	private static final Pattern p=Pattern.compile("[A-Za-z0-9_\\-.]+");
	private boolean validRepoId(String id) {
		return id!=null && p.matcher(id).matches();
	}
	
}
