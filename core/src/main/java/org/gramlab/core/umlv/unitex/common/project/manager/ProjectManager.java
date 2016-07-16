package org.gramlab.core.umlv.unitex.common.project.manager;

import java.io.File;

import org.gramlab.core.umlv.unitex.common.project.Project;

/**
 * 
 * This interface provides a common way to represent Project managers in Unitex
 * and GramLab.
 * 
 * @author mdamis
 *
 */
public interface ProjectManager {
	/**
	 * Looks for the Project corresponding to the resource parameter.
	 * 
	 * @param resource
	 *            the file used to determine the Project.
	 * @return the Project corresponding to the resource.
	 */
	public Project search(File resource);

	/**
	 * Looks for the Project corresponding to the resource parameter.
	 * 
	 * @param resource
	 *            the file used to determine the Project.
	 * @param weShallOpenTheProject
	 *            whether the project should be opened.
	 * @return the Project corresponding to the resource.
	 */
	public Project search(File resource, boolean weShallOpenTheProject);
}
