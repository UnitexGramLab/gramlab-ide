package fr.gramlab.workspace;

import java.io.File;
import java.util.ArrayList;

import fr.gramlab.project.Project;
import fr.gramlab.project.ProjectManager;

public class RootNode extends WorkspaceTreeNode {

	public RootNode(File f) {
		super(f,null);
	}
	
	private ArrayList<WorkspaceTreeNode> nodes=null;
	
	@Override
	public ArrayList<WorkspaceTreeNode> getNodes(boolean reload,ArrayList<File> removedFiles,
			ArrayList<File> forceRefresh,ArrayList<WorkspaceTreeNode> nodesToRefresh, boolean forceAll) {
		if (nodes!=null && !reload) return nodes;
		nodes=new ArrayList<WorkspaceTreeNode>();
		ArrayList<Project> projects=ProjectManager.getManager().getProjects();
		for (Project p:projects) {
			nodes.add(new ProjectNode(p,this));
		}
		return nodes;
	}

	public void addProjectNode(Project p, int pos) {
		nodes.add(pos,new ProjectNode(p,this));
	}

	public void removeProjectNode(Project p,int pos) {
		nodes.remove(pos);
	}
	
	public WorkspaceTreeNode getProjectNode(Project p) {
		for (WorkspaceTreeNode node:nodes) {
			if (node.getFile().equals(p.getProjectDirectory())) {
				return node;
			}
		}
		return null;
	}
}
