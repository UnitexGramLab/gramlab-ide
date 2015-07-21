package fr.gramlab.workspace;

import java.io.File;
import java.util.ArrayList;

import javax.swing.tree.TreeNode;

public abstract class AbstractWorkspaceTreeNode implements TreeNode, Comparable<AbstractWorkspaceTreeNode> {

	File file;
    private boolean writeStatus;
	
	public AbstractWorkspaceTreeNode(File f) {
		this.file=f;
		writeStatus=f.canWrite();
	}
	
	public File getFile() {
		return file;
	}
	
	public abstract boolean isLink();
	
    public int compareTo(AbstractWorkspaceTreeNode node) {
    	if (file.isDirectory() && node.getFile().isFile()) return -1;
    	if (file.isFile() && node.getFile().isDirectory()) return 1;
    	return file.getAbsolutePath().compareToIgnoreCase(node.getFile().getAbsolutePath());	
    }
    
    public abstract ArrayList<WorkspaceTreeNode> getNodes(boolean reload,ArrayList<File> removedFiles
    		,ArrayList<File> forceRefresh,ArrayList<WorkspaceTreeNode> nodesToRefresh,boolean forceAll);
	
    
    /**
     * Refreshes the children nodes
     */
    public void refresh(ArrayList<File> removedFiles,ArrayList<File> forceRefresh,boolean forceAll) {
    	if (file.canWrite()!=writeStatus) {
    		writeStatus=!writeStatus;
    	}
    	ArrayList<WorkspaceTreeNode> nodesToRefresh=new ArrayList<WorkspaceTreeNode>();
    	getNodes(true,removedFiles,forceRefresh,nodesToRefresh,forceAll);
    	for (WorkspaceTreeNode n:nodesToRefresh) {
    		if (n.file.isDirectory()) {
    			n.refresh(removedFiles,forceRefresh,forceAll);
    		}
    	}
    }
}
