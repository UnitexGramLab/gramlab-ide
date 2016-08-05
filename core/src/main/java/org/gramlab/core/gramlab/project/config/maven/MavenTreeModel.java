package org.gramlab.core.gramlab.project.config.maven;

import java.io.File;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.gramlab.util.filelist.SelectableFile;

@SuppressWarnings("serial")
public class MavenTreeModel extends DefaultTreeModel {
	
	MavenFileTableModel model;
	GramlabProject project;
	
	public MavenTreeModel(GramlabProject p,MavenFileTableModel model) {
		super(new MavenTreeNode(null,null,null,-1));
		this.project=p;
		this.model=model;
		createNodes();
	}

	public static String getTreeName(GramlabProject project,File f) {
		String foo=project.getRelativeFileName(f);
		 return foo.substring(4); /* We start after src/ */
	}
	
	/**
	 * If MavenFileTableModel did its job properly, nodes are sorted so that 
	 * adding items should never be a problem.
	 */
	private void createNodes() {
		MavenTreeNode root=(MavenTreeNode) getRoot();
		for (int i=0;i<model.getRowCount();i++) {
			SelectableFile tmp=model.getElement(i);
			root.addNode(this,getTreeName(project,tmp.getFile()),i);
		}
	}

	@Override
	public Object getChild(Object parent, int index) {
		MavenTreeNode node=(MavenTreeNode)parent;
		return node.getChildAt(index);
	}

	@Override
	public int getChildCount(Object parent) {
		MavenTreeNode node=(MavenTreeNode)parent;
		return node.getChildCount();
	}

	@Override
	public int getIndexOfChild(Object parent,Object child) {
		MavenTreeNode node=(MavenTreeNode)parent;
		return node.getIndex((TreeNode) child);
	}

	@Override
	public boolean isLeaf(Object node) {
		return getChildCount(node)==0;
	}

	public MavenFileTableModel getTableModel() {
		return model;
	}


	public MavenTreeNode getNode(String path) {
		return ((MavenTreeNode) root).getNode(path);
	}
	
}
