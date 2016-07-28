package org.gramlab.core.gramlab.workspace;

import java.io.File;
import java.util.ArrayList;

import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.gramlab.project.GramlabProjectManager;
import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;

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
