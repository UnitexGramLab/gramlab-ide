package org.gramlab.core.gramlab.project.config.maven;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class Artifact {
	
	public static final String DEFAULT_GROUP_ID = "default";
	public static final String DEFAULT_ARTIFACT_ID = "default";
	public static final String DEFAULT_VERSION = "0.0.1";
	private String groupId,artifactId,version;
	
	private Artifact(String groupId,String artifactId,String version) {
		this.groupId=groupId;
		this.artifactId=artifactId;
		this.version=version;
	}
	
	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}
	public String getVersion() {
		return version;
	}

	public static Artifact checkedArtifactCreation(String groupId,String artifactId,
								String version,String projectName) {
		if (groupId.equals("")) groupId=Artifact.DEFAULT_GROUP_ID;
		if (artifactId.equals("")) {
			artifactId=projectName.toLowerCase();
			if (artifactId.equals("")) {
				artifactId=Artifact.DEFAULT_ARTIFACT_ID;
			}
		}
		if (version.equals("")) version=Artifact.DEFAULT_VERSION;
		if (!checkGroupId(groupId) || !checkArtifactId(artifactId) || !checkVersion(version)) {
			return null;
		}
		return new Artifact(groupId,artifactId,version);
	}
	
	public static boolean checkVersion(String version) {
		Pattern p=Pattern.compile("[0-9]+.[0-9]+.[0-9]+(-.+)?");
		Matcher m=p.matcher(version);
		if (!m.matches()) {
			JOptionPane.showMessageDialog(null,
                        "You must indicate a version that may look like\n"+
                        "0.0.1 or 2.1.8-beta", "Error",
                        JOptionPane.ERROR_MESSAGE);
            return false;
		}
		return true;
	}

	public static boolean checkGroupId(String groupId) {
		if ("".equals(groupId)) {
			JOptionPane.showMessageDialog(null,
                        "You must indicate a group ID that may look like\n"+
                        "com.mycompany or fr.myuniversity.mylab", "Error",
                        JOptionPane.ERROR_MESSAGE);
            return false;
		}
		return true;
	}

	public static boolean checkArtifactId(String artifactId) {
		if ("".equals(artifactId)) {
			JOptionPane.showMessageDialog(null,
                        "You must indicate an artifact ID that may be the same\n"+
                        "as your project name.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Artifact)) return false;
		Artifact a=(Artifact)obj;
		return a.getGroupId().equals(getGroupId()) 
				&& a.getArtifactId().equals(getArtifactId())
				&& a.getVersion().equals(getVersion());
	}
	
	@Override
	public int hashCode() {
		return (groupId+"\n"+artifactId+"\n"+version).hashCode();
	}
}
