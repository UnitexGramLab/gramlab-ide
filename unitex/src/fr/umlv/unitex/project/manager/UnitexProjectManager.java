package fr.umlv.unitex.project.manager;

import java.io.File;

import fr.umlv.unitex.common.project.Project;
import fr.umlv.unitex.common.project.manager.ProjectManager;
import fr.umlv.unitex.project.UnitexProject;

/**
 * 
 * @author mdamis
 *
 */
public class UnitexProjectManager implements ProjectManager {
	private UnitexProject project;
	
	public UnitexProjectManager(UnitexProject project) {
		this.project = project;
	}

	@Override
	public Project search(File resource) {
		return project;
	}

	@Override
	public Project search(File resource, boolean weShallOpenTheProject) {
		return project;
	}
}