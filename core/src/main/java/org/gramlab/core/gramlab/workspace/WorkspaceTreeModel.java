package fr.gramlab.workspace;

import javax.swing.tree.DefaultTreeModel;

import fr.gramlab.GramlabConfigManager;
import fr.gramlab.project.Project;
import fr.gramlab.project.ProjectListener;
import fr.gramlab.project.ProjectManager;

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
		ProjectManager.getManager().addProjectListener(new ProjectListener() {
			public void projectOpened(Project p,int pos) {
				/* We have to update the project node icon */
				ProjectNode node=(ProjectNode) root.getChildAt(pos);
				node.refresh(p.getRemovedFiles(),null,true);
				nodeChanged(node);
				nodeStructureChanged(node);
			}
			
			public void projectClosing(Project p,int pos, boolean[] canClose) {
				/* */
			}
			public void projectClosed(Project p,int pos) {
				/* We have to update the project node icon */
				nodeChanged(root.getChildAt(pos));
			}
			
			public void projectAdded(Project p,int pos) {
				root.addProjectNode(p,pos);
				nodesWereInserted((RootNode)getRoot(),new int[]{pos});
			}

			public void projectRemoved(Project p,int pos) {
				root.removeProjectNode(p,pos);
				nodesWereRemoved((RootNode)getRoot(),new int[]{pos},null);
			}

			public void currentProjectChanged(Project p,int pos) {
				/* We have to update all the project nodes */
				int n=root.getChildCount();
				for (int i=0;i<n;i++) {
					nodeChanged(root.getChildAt(i));
				}
			}

			@Override
			public void projectSVNModified(Project p, int pos) {
				ProjectNode node=(ProjectNode) root.getChildAt(pos);
				node.refresh(p.getRemovedFiles(),null,true);
				nodeChanged(node);
				nodeStructureChanged(node);
			}
			
		});
	}

	public ProjectNode getProjectNode(Project p) {
		return (ProjectNode) root.getProjectNode(p);
	}
	
}
