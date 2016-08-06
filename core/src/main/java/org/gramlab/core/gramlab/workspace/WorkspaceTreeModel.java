package org.gramlab.core.gramlab.workspace;

import javax.swing.tree.DefaultTreeModel;

import org.gramlab.core.GramlabConfigManager;
import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.gramlab.project.GramlabProjectManager;
import org.gramlab.core.gramlab.project.ProjectListener;
import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;

@SuppressWarnings("serial")
public class WorkspaceTreeModel extends DefaultTreeModel {

	RootNode root;
	private static WorkspaceTreeModel model;
	
	private WorkspaceTreeModel(RootNode root) {
		super(root);
		this.root=root;
		model=this;
	}

	public static WorkspaceTreeModel getModel() {
		return model;
	}
	
	public WorkspaceTreeModel() {
		this(new RootNode(GramlabConfigManager.getWorkspaceDirectory()));
		GlobalProjectManager.getAs(GramlabProjectManager.class)
			.addProjectListener(new ProjectListener() {
			public void projectOpened(GramlabProject p,int pos) {
				/* We have to update the project node icon */
				ProjectNode node=(ProjectNode) root.getChildAt(pos);
				node.refresh(p.getRemovedFiles(),null,true);
				nodeChanged(node);
				nodeStructureChanged(node);
			}
			
			public void projectClosing(GramlabProject p,int pos, boolean[] canClose) {
				/* */
			}
			public void projectClosed(GramlabProject p,int pos) {
				/* We have to update the project node icon */
				nodeChanged(root.getChildAt(pos));
			}
			
			public void projectAdded(GramlabProject p,int pos) {
				root.addProjectNode(p,pos);
				nodesWereInserted((RootNode)getRoot(),new int[]{pos});
			}

			public void projectRemoved(GramlabProject p,int pos) {
				root.removeProjectNode(p,pos);
				nodesWereRemoved((RootNode)getRoot(),new int[]{pos},null);
			}

			public void currentProjectChanged(GramlabProject p,int pos) {
				/* We have to update all the project nodes */
				int n=root.getChildCount();
				for (int i=0;i<n;i++) {
					nodeChanged(root.getChildAt(i));
				}
			}

			@Override
			public void projectSVNModified(GramlabProject p, int pos) {
				ProjectNode node=(ProjectNode) root.getChildAt(pos);
				node.refresh(p.getRemovedFiles(),null,true);
				nodeChanged(node);
				nodeStructureChanged(node);
			}
			
		});
	}

	public ProjectNode getProjectNode(GramlabProject p) {
		return (ProjectNode) root.getProjectNode(p);
	}
	
}
