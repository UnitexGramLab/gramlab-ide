package fr.gramlab.project;


public interface ProjectListener {
	
	public void currentProjectChanged(GramlabProject p,int pos);
	public void projectAdded(GramlabProject p,int pos);
	public void projectRemoved(GramlabProject p,int pos);
	public void projectOpened(GramlabProject p,int pos);
	public void projectClosing(GramlabProject p,int pos,boolean[] canClose);
	public void projectClosed(GramlabProject p,int pos);
	public void projectSVNModified(GramlabProject p,int pos);

}
