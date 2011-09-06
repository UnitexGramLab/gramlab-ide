package fr.gramlab.workspace;


public interface ProjectListener {
	
	public void projectAdded(Project p,int pos);
	public void projectRemoved(Project p,int pos);
	public void projectOpened(Project p,int pos);
	public void projectClosed(Project p,int pos);

}
