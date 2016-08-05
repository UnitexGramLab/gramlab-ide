package org.gramlab.core.gramlab.project.config.maven;

import java.util.ArrayList;
import java.util.List;

public class MvnRepository {
	
	/**
	 * Connects to the repository and gets all artifacts matching the given pattern,
	 * or all available artifacts if pattern is empty.
	 * @param pattern
	 * @return
	 */
	public static List<Artifact> getArtifacts(String pattern) {
		return new ArrayList<Artifact>();
	}
	
}
