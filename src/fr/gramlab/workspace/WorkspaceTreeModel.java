package fr.gramlab.workspace;

import javax.swing.tree.DefaultTreeModel;

import fr.gramlab.config.GramlabConfigManager;

public class WorkspaceTreeModel extends DefaultTreeModel {

	RootNode root;
	
	private WorkspaceTreeModel(RootNode root) {
		super(root);
		this.root=root;
	}

	public WorkspaceTreeModel() {
		this(new RootNode(GramlabConfigManager.getWorkspaceDirectory()));
		ProjectManager.getManager().addProjectListener(new ProjectListener() {
			public void projectOpened(Project p,int pos) {
				/* We have to update the project node icon */
				ProjectNode node=(ProjectNode) root.getChildAt(pos);
				node.refresh();
				nodeChanged(node);
				nodeStructureChanged(node);
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
			
		});
	}
	
}
