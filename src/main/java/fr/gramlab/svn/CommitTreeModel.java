package fr.gramlab.svn;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

@SuppressWarnings("serial")
public class CommitTreeModel extends DefaultTreeModel {
	
	CommitTableModel model;
	private boolean hideUnversionedItems=false; 
	
	public CommitTreeModel(CommitTableModel model) {
		super(new CommitTreeNode(null,null,null,-1));
		this.model=model;
		createNodes();
	}

	/**
	 * If CommitTableModel did its job properly, nodes are sorted so that 
	 * adding items should never be a problem.
	 */
	private void createNodes() {
		CommitTreeNode root=(CommitTreeNode) getRoot();
		for (int i=0;i<model.getRowCount();i++) {
			SvnCommitInfo info=model.getElement(i);
			root.addNode(this,info.getName(),i);
		}
		root.updateFilteredChildren();
	}

	@Override
	public Object getChild(Object parent, int index) {
		CommitTreeNode node=(CommitTreeNode)parent;
		return node.getChildAt(index);
	}

	@Override
	public int getChildCount(Object parent) {
		CommitTreeNode node=(CommitTreeNode)parent;
		return node.getChildCount();
	}

	@Override
	public int getIndexOfChild(Object parent,Object child) {
		CommitTreeNode node=(CommitTreeNode)parent;
		return node.getIndex((TreeNode) child);
	}

	@Override
	public boolean isLeaf(Object node) {
		return getChildCount(node)==0;
	}

	public CommitTableModel getTableModel() {
		return model;
	}

	public boolean isHideUnversionedItems() {
		return hideUnversionedItems;
	}

	public void setHideUnversionedItems(boolean hideUnversionedItems) {
		this.hideUnversionedItems = hideUnversionedItems;
		nodeStructureChanged((TreeNode) getRoot());
	}

	public CommitTreeNode getNode(String path) {
		return ((CommitTreeNode) root).getNode(path);
	}
	
}
