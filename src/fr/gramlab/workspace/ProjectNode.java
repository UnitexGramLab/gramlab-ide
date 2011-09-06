package fr.gramlab.workspace;


public class ProjectNode extends WorkspaceTreeNode {

	private Project project;
	
	public ProjectNode(Project p,RootNode root) {
		super(p.getDirectory(),root);
		this.project=p;
	}
	
	@Override
	public boolean isLeaf() {
		return !project.isOpen();
	}
	
	public Project getProject() {
		return project;
	}
	
}
