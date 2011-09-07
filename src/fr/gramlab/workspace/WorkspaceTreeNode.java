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
	public ArrayList<WorkspaceTreeNode> getNodes(boolean reload) {
		if (nodes!=null && !reload) return nodes;
		nodes=new ArrayList<WorkspaceTreeNode>();
		File[] files=file.listFiles();
		if (files==null) return nodes;
		for (File f:files) {
			nodes.add(new WorkspaceTreeNode(f,this));
		}
		Collections.sort(nodes);
		return nodes;
	}
	
	public Enumeration<WorkspaceTreeNode> children() {
		return Collections.enumeration(getNodes(false));
	}

	public boolean getAllowsChildren() {
		return file.exists() && file.isDirectory();
	}

	public TreeNode getChildAt(int childIndex) {
		return getNodes(false).get(childIndex);
	}

	public int getChildCount() {
		return getNodes(false).size();
	}

	public int getIndex(TreeNode node) {
		return getNodes(false).indexOf(node);
	}

	public TreeNode getParent() {
		return parent;
	}

	public boolean isLeaf() {
		return !file.exists() || file.isFile();
	}
}
