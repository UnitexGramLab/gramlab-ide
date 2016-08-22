package org.gramlab.core.umlv.unitex.common.project.manager;

import java.io.File;

import org.gramlab.core.gramlab.project.GramlabProjectManager;
import org.gramlab.core.umlv.unitex.common.project.Project;
import org.gramlab.core.umlv.unitex.project.manager.UnitexProjectManager;

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

	private static UnitexProjectManager classic;
	private static GramlabProjectManager projectOriented;
	
	public GlobalProjectManager(ProjectManager manager) {
		GlobalProjectManager.projectManager = manager;
	}
	
	public static void setGlobalProjectManager (ProjectManager manager) {
		GlobalProjectManager.projectManager = manager;
	}
	
	public static ProjectManager getGlobalProjectManager(){
		return projectManager;
	}
	
	public static void setGramlabProjectManager (ProjectManager manager) {
		GlobalProjectManager.projectOriented = (GramlabProjectManager) manager;
	}
	
	public static void setUnitexProjectManager (ProjectManager manager) {
		GlobalProjectManager.classic = (UnitexProjectManager) manager;
	}
	
	public static GramlabProjectManager getGramlabProjectManager () {
		return projectOriented;
	}
	
	public static ProjectManager getUnitexProjectManager () {
		return classic;
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
