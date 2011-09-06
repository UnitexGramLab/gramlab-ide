package fr.gramlab.workspace;

import java.io.File;
import java.util.ArrayList;

import javax.swing.tree.TreeNode;

public abstract class AbstractWorkspaceTreeNode implements TreeNode, Comparable<AbstractWorkspaceTreeNode> {

	File file;
	
	public AbstractWorkspaceTreeNode(File f) {
		this.file=f;
	}
	
	public File getFile() {
		return file;
	}
	
	public abstract boolean isLink();
	
    public int compareTo(AbstractWorkspaceTreeNode node) {
    	return file.getAbsolutePath().compareTo(node.getFile().getAbsolutePath());	
    }
    
    public abstract ArrayList<WorkspaceTreeNode> getNodes();
	
}
