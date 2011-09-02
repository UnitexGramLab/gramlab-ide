package fr.gramlab.listeners;

import fr.gramlab.workspace.Project;

public interface ProjectListener {
	
	public void projectAdded(Project p);
	public void projectRemoved(Project p);
	public void projectOpened(Project p);
	public void projectClosed(Project p);

}
