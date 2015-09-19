package fr.gramlab.workspace;

import java.io.File;
import java.util.ArrayList;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.GramlabProjectManager;

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
		ArrayList<GramlabProject> projects=GlobalProjectManager.getAs(GramlabProjectManager.class)
				.getProjects();
		for (GramlabProject p:projects) {
			nodes.add(new ProjectNode(p,this));
		}
		return nodes;
	}

	public void addProjectNode(GramlabProject p, int pos) {
		nodes.add(pos,new ProjectNode(p,this));
	}

	public void removeProjectNode(GramlabProject p,int pos) {
		nodes.remove(pos);
	}
	
	public WorkspaceTreeNode getProjectNode(GramlabProject p) {
		for (WorkspaceTreeNode node:nodes) {
			if (node.getFile().equals(p.getProjectDirectory())) {
				return node;
			}
		}
		return null;
	}
}
