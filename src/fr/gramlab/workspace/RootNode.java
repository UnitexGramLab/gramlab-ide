package fr.gramlab.workspace;

import java.io.File;
import java.util.ArrayList;

public class RootNode extends WorkspaceTreeNode {

	public RootNode(File f) {
		super(f,null);
	}
	
	private ArrayList<WorkspaceTreeNode> nodes=null;
	
	@Override
	public ArrayList<WorkspaceTreeNode> getNodes() {
		if (nodes!=null) return nodes;
		nodes=new ArrayList<WorkspaceTreeNode>();
		ArrayList<Project> projects=ProjectManager.getProjects();
		for (Project p:projects) {
			nodes.add(new ProjectNode(p,this));
		}
		return nodes;
	}

	public void addProjectNode(Project p, int pos) {
		nodes.add(pos,new ProjectNode(p,this));
	}

	public void removeProjectNode(@SuppressWarnings("unused") Project p,int pos) {
		nodes.remove(pos);
	}
}
