package fr.gramlab.workspace;


public interface ProjectListener {
	
	public void projectAdded(Project p);
	public void projectRemoved(Project p);
	public void projectOpened(Project p);
	public void projectClosed(Project p);

}
