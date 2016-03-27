package fr.gramlab.svn;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

public class CommitTreeNode implements MutableTreeNode {

	private String name;
	private ArrayList<CommitTreeNode> children=new ArrayList<CommitTreeNode>();
	private ArrayList<CommitTreeNode> filteredChildren=new ArrayList<CommitTreeNode>();
	private CommitTreeNode parent;
	private int index;
	private CommitTreeModel treeModel;
	
	
	public CommitTreeNode(CommitTreeModel treeModel,CommitTreeNode parent,String n,int index) {
		this.parent=parent;
		if (n==null) name="";
		this.name=n;
		this.index=index;
		this.treeModel=treeModel;
	}

	@Override
	public Enumeration<CommitTreeNode> children() {
		if (treeModel!=null && treeModel.isHideUnversionedItems()) return Collections.enumeration(filteredChildren);
		return Collections.enumeration(children);
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		if (treeModel!=null && treeModel.isHideUnversionedItems()) return filteredChildren.get(childIndex);
		return children.get(childIndex);
	}

	@Override
	public int getChildCount() {
		if (treeModel!=null && treeModel.isHideUnversionedItems()) return filteredChildren.size();
		return children.size();
	}

	@Override
	public int getIndex(TreeNode node) {
		if (treeModel!=null && treeModel.isHideUnversionedItems()) return filteredChildren.indexOf(node);
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

	/**
	 * Indicates whether the current node should be filtered or not. We 
	 * filter all nodes that have the UNVERSIONED status, as well as node
	 * that are not modified and that have only filtered children.
	 * @return
	 */
	boolean shouldBeFiltered() {
		if (getCommitTableModelIndex()!=-1) {
			SvnCommitInfo info=treeModel.getTableModel().getElement(getCommitTableModelIndex());
			if (info.getStatus()==SvnStatus.UNVERSIONED) {
				return true;
			}
			if (info.getStatus()!=SvnStatus.UNMODIFIED) {
				return false;
			}
		}
		if (isLeaf()) {
			return false;
		}
		for (int i=0;i<getChildCount();i++) {
			if (!((CommitTreeNode) getChildAt(i)).shouldBeFiltered()) {
				return false;
			}
		}
		return true;
	}
	
	
	public void addNode(CommitTreeModel treeModel,String path,int index) {
		String name,remaining;
		int pos=path.indexOf(File.separatorChar);
		if (pos==-1) {
			name=path;
			remaining=null;
		} else {
			name=path.substring(0,pos);
			remaining=path.substring(pos+1);
		}
		for (CommitTreeNode node:children) {
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
		CommitTreeNode newNode=new CommitTreeNode(treeModel,this,name,(remaining!=null)?-1:index);
		children.add(newNode);
		if (remaining!=null) {
			throw new IllegalStateException("Missing intermediate node for path "+path);
		}
	}

	public String getName() {
		return name;
	}
	
	public int getCommitTableModelIndex() {
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
		SvnCommitInfo info=treeModel.getTableModel().getElement(index);
		treeModel.getTableModel().setTreeSelected(info.getName(),(Boolean)object);
		treeModel.nodeStructureChanged(this);
		CommitTreeNode tmp=this;
		/* When selecting a node, we may have to update its parents as well */
		while ((tmp=(CommitTreeNode) tmp.getParent())!=null) {
			treeModel.nodeChanged(tmp);
		}
	}
	
	public void updateFilteredChildren() {
		filteredChildren.clear();
		for (CommitTreeNode n:children) {
			n.updateFilteredChildren();
			if (!n.shouldBeFiltered()) filteredChildren.add(n);
		}
	}

	public CommitTreeNode getNode(String path) {
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
		ArrayList<CommitTreeNode> nodes=children;
		if (treeModel!=null && treeModel.isHideUnversionedItems()) {
			nodes=filteredChildren;
		}
		for (CommitTreeNode son:nodes) {
			if (son.getName().equals(name)) {
				return son.getNode(remaining);
			}
		}
		return null;
	}
	
}
