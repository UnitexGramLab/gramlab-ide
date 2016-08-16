package org.gramlab.core.umlv.unitex.common.project.manager;

import java.io.File;

import org.gramlab.core.umlv.unitex.common.project.Project;

/**
 * This class constitutes a global access point for the ProjectManager in Unitex
 * and GramLab.
 * 
 * @author mdamis
 *
 */
public class GlobalProjectManager {
	/* The ProjectManager currently in use. */
	private static ProjectManager projectManager;

	public GlobalProjectManager(ProjectManager manager) {
		GlobalProjectManager.projectManager = manager;
	}
	
	/**
	 * Casts the projectManager to the specified type.
	 * 
	 * This method uses the Type Token Pattern
	 * 
	 * @param type
	 *            the type used for the cast of the projectManager.
	 * @return the casted projectManager.
	 */
	public static <P extends ProjectManager> P getAs(Class<P> type) {
		return type.cast(projectManager);
	}

	/**
	 * Makes a call to the method search of the projectManager which only takes
	 * the resource as an argument.
	 * 
	 * @param resource
	 *            the file used to determine the Project.
	 * @return the Project corresponding to the resource.
	 */
	public static Project search(File resource) {
		return projectManager.search(resource);
	}

	/**
	 * Makes a call to the method search of the projectManager which takes the
	 * resource and a boolean as arguments.
	 * 
	 * @param resource
	 *            the file used to determine the Project.
	 * @param weShallOpenTheProject
	 *            whether the project should be opened
	 * @return the Project corresponding to the resource.
	 */
	public static Project search(File resource, boolean weShallOpenTheProject) {
		return projectManager.search(resource, weShallOpenTheProject);
	}
}
