package fr.gramlab.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.GramlabProjectManager;
import fr.gramlab.project.console.ConsoleUtil;
import fr.gramlab.svn.SvnInfo;

public class WorkspaceTreeNode extends AbstractWorkspaceTreeNode {
	private WorkspaceTreeNode parent;
	private long lastModified = -1;
	private SvnInfo lastSvnInfo = null;

	public WorkspaceTreeNode(File f, WorkspaceTreeNode parent) {
		super(f);
		this.parent = parent;
	}

	@Override
	public boolean isLink() {
		return false;
	}

	private ArrayList<WorkspaceTreeNode> nodes = null;

	/**
	 * This method should not be called several times in a row, since it updates
	 * lastModified && lastStatus
	 */
	private boolean isNodeModified() {
		long m = file.lastModified();
		if (m != lastModified) {
			lastModified = m;
			return true;
		}
		SvnInfo info = getSvnInfo(file);
		if (info == null && lastSvnInfo == null)
			return false;
		if (info == null || lastSvnInfo == null
				|| info.getStatus() != lastSvnInfo.getStatus()
				|| info.getRevision() != lastSvnInfo.getRevision()) {
			lastSvnInfo = info;
			return true;
		}
		return false;
	}

	private GramlabProject project = null;

	private SvnInfo getSvnInfo(File file) {
		if (project == null) {
			project = GlobalProjectManager.getAs(GramlabProjectManager.class)
					.getProject(file);
		}
		return project.getSvnInfo(file);
	}

	@Override
	public ArrayList<WorkspaceTreeNode> getNodes(boolean reload,
			ArrayList<File> removedFiles, ArrayList<File> forceRefresh,
			ArrayList<WorkspaceTreeNode> nodesToRefresh,boolean forceAll) {
		boolean forceReload=forceAll;
		if (forceRefresh != null && forceRefresh.contains(getFile())) {
			forceReload = true;
		}
		if (nodes == null) {
			nodeCreation(removedFiles);
			if (nodesToRefresh != null) {
				for (WorkspaceTreeNode n : nodes) {
					nodesToRefresh.add(n);
				}
			}
			return nodes;
		}
		/* If no reload operation is necessary, we return the current node list */
		if (!reload && !forceReload)
			return nodes;
		//System.err.println("reload "+getFile().getAbsolutePath());
		boolean nodeModified = isNodeModified();
		/* case 1: single file */
		if (getFile().isFile()) {
			if (!getFile().exists()) {
				return nodes;
			}
			if (nodeModified) {
				WorkspaceTreeModel.getModel().nodeChanged(this);
			}
			return nodes;
		}
		/* case 2: a directory node that contains subnodes */
		boolean needToUpdate = nodeModified || forceReload;
		//System.err.println("need to update "+needToUpdate);
		if (needToUpdate) {
			/* We look for deleted files */
			for (int i = nodes.size() - 1; i >= 0; i--) {
				WorkspaceTreeNode n = nodes.get(i);
				if (!n.file.exists()) {
					nodes.remove(i);
					WorkspaceTreeModel.getModel().nodesWereRemoved(this,
							new int[] { i }, new Object[] { n });
				}
			}
			File[] files = file.listFiles();
			if (files == null)
				return nodes;
			for (File f : files) {
				if (shouldIgnore(f)) {
					continue;
				}
				int index = getSortedNodeIndex(f);
				if (index != -1) {
					nodes.add(index, new WorkspaceTreeNode(f, this));
					WorkspaceTreeModel.getModel().nodesWereInserted(this,
							new int[] { index });
				}
			}
		}
		/* Even if the node itself is not modified, we may have to refresh
		 * it if if contains a deleted node
		 */
		if (needToUpdate) for (File f : removedFiles) {
			if (f.getParentFile().equals(getFile())) {
				/* f is a deleted file that belongs to the node's directory */
				if (shouldIgnore(f)) {
					continue;
				}
				int index = getSortedNodeIndex(f);
				if (index != -1) {
					needToUpdate = true;
					nodes.add(index, new WorkspaceTreeNode(f, this));
					WorkspaceTreeModel.getModel().nodesWereInserted(this,
							new int[] { index });
				}
			}
		}
		if (needToUpdate && nodesToRefresh != null) {
			for (WorkspaceTreeNode n : nodes) {
				if (!nodesToRefresh.contains(n)) nodesToRefresh.add(n);
			}
		} else if (reload && nodesToRefresh != null) {
			/* The node is not modified, but we have to look for modifications in
			 * subdirectories
			 */
			for (WorkspaceTreeNode n : nodes) {
				if (!n.getFile().isDirectory()) continue;
				if (!nodesToRefresh.contains(n)) nodesToRefresh.add(n);
			}
		}
		if (needToUpdate) {
			/*
			 * We update the current node, in case we have to update its SVN
			 * information
			 */
			WorkspaceTreeModel.getModel().nodeChanged(this);
		}
		return nodes;
	}

	private boolean shouldIgnore(File f) {
		return f.getName().startsWith(ConsoleUtil.TIME_PFX)
				|| f.getName().equals(".svn") || f.getName().startsWith("..");
	}

	private int compareFiles(File a, File b) {
		if (a.isDirectory() && b.isFile())
			return -1;
		if (b.isDirectory() && a.isFile())
			return 1;
		return a.getName().compareToIgnoreCase(b.getName());
	}

	private int getSortedNodeIndex(File f, int index) {
		/*
		 * File not found at the end of the nodes array: we have to insert at
		 * the end
		 */
		if (index == nodes.size())
			return index;
		File tmp = nodes.get(index).file;
		int cmp = compareFiles(f, tmp);
		if (cmp == 0) {
			/* The file is already there */
			return -1;
		}
		if (cmp < 0)
			return index;
		return getSortedNodeIndex(f, index + 1);
	}

	/**
	 * Returns -1 if the file is already in the nodes, or the position where it
	 * should be sort-inserted
	 */
	private int getSortedNodeIndex(File f) {
		return getSortedNodeIndex(f, 0);
	}

	private ArrayList<WorkspaceTreeNode> nodeCreation(
			ArrayList<File> removedFiles) {
		nodes = new ArrayList<WorkspaceTreeNode>();
		File[] files = file.listFiles();
		if (files == null)
			return nodes;
		for (File f : files) {
			if (shouldIgnore(f))
				continue;
			int index = getSortedNodeIndex(f);
			if (index != -1)
				nodes.add(index, new WorkspaceTreeNode(f, this));
		}
		for (File f : removedFiles) {
			if (f.getParentFile().equals(getFile())) {
				/* f is a deleted file that belongs to the node's directory */
				if (shouldIgnore(f)) {
					continue;
				}
				int index = getSortedNodeIndex(f);
				if (index != -1)
					nodes.add(index, new WorkspaceTreeNode(f, this));
			}
		}
		/* Now, we compare the new nodes with the old ones */
		WorkspaceTreeModel.getModel().nodeStructureChanged(this);
		return nodes;
	}

	public Enumeration<WorkspaceTreeNode> children() {
		return Collections.enumeration(getNodes(false, null, null, null, false));
	}

	public boolean getAllowsChildren() {
		return file.exists() && file.isDirectory();
	}

	public TreeNode getChildAt(int childIndex) {
		return getNodes(false, null, null, null, false).get(childIndex);
	}

	public int getChildCount() {
		return getNodes(false, null, null, null, false).size();
	}

	public int getIndex(TreeNode node) {
		return getNodes(false, null, null, null, false).indexOf(node);
	}

	public TreeNode getParent() {
		return parent;
	}

	public boolean isLeaf() {
		return !file.exists() || file.isFile();
	}
}
