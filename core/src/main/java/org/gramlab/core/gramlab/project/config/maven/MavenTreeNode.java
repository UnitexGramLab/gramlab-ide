package org.gramlab.core.gramlab.project.config.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.gramlab.core.gramlab.util.filelist.SelectableFile;

public class MavenTreeNode implements MutableTreeNode {

	private String name;
	private ArrayList<MavenTreeNode> children=new ArrayList<MavenTreeNode>();
	private MavenTreeNode parent;
	private int index;
	private MavenTreeModel treeModel;
	
	
	public MavenTreeNode(MavenTreeModel treeModel,MavenTreeNode parent,String n,int index) {
		this.parent=parent;
		if (n==null) name="";
		this.name=n;
		this.index=index;
		this.treeModel=treeModel;
	}

	@Override
	public Enumeration<MavenTreeNode> children() {
		return Collections.enumeration(children);
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		return children.get(childIndex);
	}

	@Override
	public int getChildCount() {
		return children.size();
	}

	@Override
	public int getIndex(TreeNode node) {
		return children.indexOf(node);
	}

	@Override
	public TreeNode getParent() {
		return parent;
	}

	@Override
	public boolean isLeaf() {
		return getChildCount()==0;
	}

	
	public void addNode(MavenTreeModel treeModel,String path,int index) {
		String name,remaining;
		int pos=path.indexOf(File.separatorChar);
		if (pos==-1) {
			name=path;
			remaining=null;
		} else {
			name=path.substring(0,pos);
			remaining=path.substring(pos+1);
		}
		for (MavenTreeNode node:children) {
			if (name.equals(node.getName())) {
				/* We have found the node. If it was the last one of the path,
				 * then it is an error to find it
				 */
				if (remaining==null) {
					throw new IllegalStateException("Node "+path+" already in the tree");
				}
				/* Otherwise, we just invoke recursively the method */
				node.addNode(treeModel,remaining,index);
				return;
			}
		}
		/* There was no child node to use, we must create it */
		MavenTreeNode newNode=new MavenTreeNode(treeModel,this,name,(remaining!=null)?-1:index);
		children.add(newNode);
		if (remaining!=null) {
			throw new IllegalStateException("Missing intermediate node for path "+path);
		}
	}

	public String getName() {
		return name;
	}
	
	public int getMavenTableModelIndex() {
		return index;
	}

	@Override
	public void insert(MutableTreeNode child, int index) {
		/**/
	}

	@Override
	public void remove(int index) {
		/**/
	}

	@Override
	public void remove(MutableTreeNode node) {
		/**/
	}

	@Override
	public void removeFromParent() {
		/**/
	}

	@Override
	public void setParent(MutableTreeNode newParent) {
		/**/
	}

	@Override
	public void setUserObject(Object object) {
		SelectableFile tmp=treeModel.getTableModel().getElement(index);
		treeModel.getTableModel().setTreeSelected(MavenTreeModel.getTreeName(treeModel.project,
				tmp.getFile()),(Boolean)object,treeModel.project);
		treeModel.nodeStructureChanged(this);
		MavenTreeNode node=this;
		/* When selecting a node, we may have to update its parents as well */
		while ((node=(MavenTreeNode) node.getParent())!=null) {
			treeModel.nodeChanged(node);
		}
	}
	
	public MavenTreeNode getNode(String path) {
		if (path.equals("")) {
			/* Last element of the path */
			return this;
		}
		int pos=path.indexOf(File.separatorChar);
		String name,remaining;
		if (pos==-1) {
			name=path;
			remaining="";
		} else {
			name=path.substring(0,pos);
			remaining=path.substring(pos+1);
		}
		for (MavenTreeNode son:children) {
			if (son.getName().equals(name)) {
				return son.getNode(remaining);
			}
		}
		return null;
	}
	
}
