package fr.gramlab.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

public class WorkspaceTreeNode extends AbstractWorkspaceTreeNode {
	
	private WorkspaceTreeNode parent;
	
	public WorkspaceTreeNode(File f,WorkspaceTreeNode parent) {
		super(f);
		this.parent=parent;
	}
	
	@Override
	public boolean isLink() {
		return false;
	}
	
	
	private ArrayList<WorkspaceTreeNode> nodes=null;

	@Override
	public ArrayList<WorkspaceTreeNode> getNodes() {
		if (nodes!=null) return nodes;
		nodes=new ArrayList<WorkspaceTreeNode>();
		File[] files=file.listFiles();
		if (files==null) return nodes;
		for (File f:files) {
			nodes.add(new WorkspaceTreeNode(f,this));
		}
		return nodes;
	}
	
	public Enumeration<WorkspaceTreeNode> children() {
		return Collections.enumeration(getNodes());
	}

	public boolean getAllowsChildren() {
		return file.exists() && file.isDirectory();
	}

	public TreeNode getChildAt(int childIndex) {
		return getNodes().get(childIndex);
	}

	public int getChildCount() {
		return getNodes().size();
	}

	public int getIndex(TreeNode node) {
		return getNodes().indexOf(node);
	}

	public TreeNode getParent() {
		return parent;
	}

	public boolean isLeaf() {
		return !file.exists() || file.isFile();
	}
}
