package fr.gramlab.project;


public interface ProjectListener {
	
	public void currentProjectChanged(Project p,int pos);
	public void projectAdded(Project p,int pos);
	public void projectRemoved(Project p,int pos);
	public void projectOpened(Project p,int pos);
	public void projectClosing(Project p,int pos,boolean[] canClose);
	public void projectClosed(Project p,int pos);
	public void projectSVNModified(Project p,int pos);

}
