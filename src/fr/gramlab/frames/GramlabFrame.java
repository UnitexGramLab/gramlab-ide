package fr.gramlab.frames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import fr.gramlab.icons.Icons;
import fr.gramlab.workspace.Project;
import fr.gramlab.workspace.ProjectManager;
import fr.gramlab.workspace.ProjectNode;
import fr.gramlab.workspace.RootNode;
import fr.gramlab.workspace.WorkspaceTreeModel;
import fr.gramlab.workspace.WorkspaceTreeNode;
import fr.umlv.unitex.frames.InternalFrameManager;

@SuppressWarnings("serial")
public class GramlabFrame extends JFrame {
	Action editProject;
	Action deleteProject;
	JDesktopPane desktop;

	public GramlabFrame() {
		super("GramLab");
		setJMenuBar(createMenuBar());
		JPanel tree = createWorkspacePane();
		desktop = new JDesktopPane();
		JSplitPane p = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, tree,
				desktop);
		InternalFrameManager.setManager(new InternalFrameManager(desktop));
		setContentPane(p);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private JPanel createWorkspacePane() {
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createTitledBorder("Workspace"));
		p.setMinimumSize(new Dimension(200, 1));
		final WorkspaceTreeModel model = new WorkspaceTreeModel();
		final JTree tree = new JTree(model);
		tree.setRootVisible(false);
		model.addTreeModelListener(new TreeModelListener() {
			public void treeStructureChanged(TreeModelEvent e) {
				// TODO Auto-generated method stub
			}

			public void treeNodesRemoved(TreeModelEvent e) {
				// TODO Auto-generated method stub
			}

			public void treeNodesInserted(TreeModelEvent e) {
				// TODO Auto-generated method stub
			}

			public void treeNodesChanged(TreeModelEvent e) {
				Object o = e.getTreePath().getLastPathComponent();
				if (o instanceof RootNode) {
					/*
					 * When a project node changes, if it is open we expand the
					 * node; if the project is closed, we collapse it
					 */
					RootNode root = (RootNode) o;
					for (int i : e.getChildIndices()) {
						ProjectNode node = (ProjectNode) root.getChildAt(i);
						if (node.getProject().isOpen()) {
							tree.expandPath(new TreePath(model
									.getPathToRoot(node)));
						} else {
							tree.collapsePath(new TreePath(model
									.getPathToRoot(node)));
						}
					}
				}
				tree.repaint();
			}
		});
		tree.setCellRenderer(new DefaultTreeCellRenderer() {
			@Override
			public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean sel, boolean expanded, boolean leaf,
					int row, boolean hasFocus) {
				WorkspaceTreeNode node = (WorkspaceTreeNode) value;
				String name = node.getFile().getName();
				if (value instanceof ProjectNode) {
					/*
					 * If we have a project node, we change the icon if the
					 * project is open
					 */
					ProjectNode n = (ProjectNode) value;
					/*
					 * For a project node, we always force leaf to false in
					 * order to have the folder icon
					 */
					super.getTreeCellRendererComponent(tree, name, sel,
							expanded, false, row, hasFocus);
					if (n.getProject().isOpen())
						setIcon(Icons.openFolderIcon);
				} else {
					/* Normal case */
					super.getTreeCellRendererComponent(tree, name, sel,
							expanded, leaf, row, hasFocus);
				}
				return this;
			}
		});
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				boolean doubleClick=(e.getButton() == MouseEvent.BUTTON1 
						&& e.getClickCount()==2);
				boolean rightClick=(e.getButton() == MouseEvent.BUTTON3);
				if (!doubleClick && !rightClick) return;
				final int x = e.getX();
				final int y = e.getY();
				TreePath path = tree.getClosestPathForLocation(x, y);
				if (path == null)
					return;
				Rectangle r = tree.getPathBounds(path);
				if (r.contains(x, y)) {
					Object o = path.getLastPathComponent();
					if (o instanceof ProjectNode) {
						/* If we have a project node */
						ProjectNode pn = (ProjectNode) o;
						if (rightClick) {
							final JPopupMenu popup = createProjectPopup(pn
								.getProject());
							EventQueue.invokeLater(new Runnable() {
								public void run() {
									popup.show(tree, x, y);
								}
							});
						}
						return;
					}
					/* If we have a normal node */
					WorkspaceTreeNode node=(WorkspaceTreeNode) o;
					File f=node.getFile();
					if (doubleClick && f.isFile()) {
						/* Double-click on a file */
						if (f.getName().endsWith(".grf")) {
							InternalFrameManager.getManager().newGraphFrame(f);
						}
					}
				}
			}

			private JPopupMenu createProjectPopup(final Project project) {
				JPopupMenu popup = new JPopupMenu();
				final boolean open = project.isOpen();
				Action openClose = new AbstractAction((open ? "Close" : "Open")
						+ " project " + project.getName()) {
					public void actionPerformed(ActionEvent e) {
						if (open)
							ProjectManager.getManager().closeProject(project);
						else
							ProjectManager.getManager().openProject(project);
					}
				};
				popup.add(new JMenuItem(openClose));
				return popup;
			}
		});
		p.add(tree);
		return p;
	}

	private JMenuBar createMenuBar() {
		JMenuBar bar = new JMenuBar();
		bar.add(createProjectMenu());
		bar.add(createActionsMenu());
		bar.add(createGraphsMenu());
		bar.add(createDictionariesMenu());
		bar.add(createSVNMenu());
		bar.add(createBuildMenu());
		bar.add(createTestMenu());
		bar.add(createConfigurationMenu());
		return bar;
	}

	private JMenu createProjectMenu() {
		JMenu m = new JMenu("Project");
		Action n = new AbstractAction("New") {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						new CreateProjectDialog();
					}
				});
			}
		};
		m.add(new JMenuItem(n));
		editProject = new AbstractAction("Modify/Edit") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		editProject.setEnabled(false);
		m.add(new JMenuItem(editProject));
		deleteProject = new AbstractAction("Delete") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		deleteProject.setEnabled(false);
		m.add(new JMenuItem(deleteProject));
		m.addSeparator();
		Action modify = new AbstractAction("Modify a project") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(modify));
		Action delete = new AbstractAction("Delete a project") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(delete));
		return m;
	}

	private JMenu createActionsMenu() {
		JMenu m = new JMenu("Actions");
		return m;
	}

	public void openGraph() {
		final JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(true);
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		final int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		final File[] graphs = fc.getSelectedFiles();
		for (int i = 0; i < graphs.length; i++) {
			String s = graphs[i].getAbsolutePath();
			if (!graphs[i].exists() && !s.endsWith(".grf")) {
				s = s + ".grf";
				graphs[i] = new File(s);
				if (!graphs[i].exists()) {
					JOptionPane.showMessageDialog(null, "File "
							+ graphs[i].getAbsolutePath() + " does not exist",
							"Error", JOptionPane.ERROR_MESSAGE);
					continue;
				}
			}
			InternalFrameManager.getManager().newGraphFrame(graphs[i]);
		}
	}

	private JMenu createGraphsMenu() {
		JMenu m = new JMenu("Graphs");
		Action n = new AbstractAction("New") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager().newGraphFrame(null);
			}
		};
		m.add(new JMenuItem(n));
		Action open = new AbstractAction("Open") {
			public void actionPerformed(ActionEvent e) {
				openGraph();
			}
		};
		m.add(new JMenuItem(open));
		Action search = new AbstractAction("Search") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(search));
		m.addSeparator();
		Action saveAll = new AbstractAction("Save all") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(saveAll));
		Action closeAll = new AbstractAction("Close all") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(closeAll));
		return m;
	}

	private JMenu createDictionariesMenu() {
		JMenu m = new JMenu("Dictionaries");
		Action n = new AbstractAction("New") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(n));
		Action open = new AbstractAction("Open") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(open));
		m.addSeparator();
		Action test = new AbstractAction("Test") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(test));
		Action check = new AbstractAction("Check") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(check));
		m.addSeparator();
		Action compress = new AbstractAction("Compress") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(compress));
		return m;
	}

	private JMenu createSVNMenu() {
		JMenu m = new JMenu("SVN");
		return m;
	}

	private JMenu createBuildMenu() {
		JMenu m = new JMenu("Build");
		Action launch = new AbstractAction("Launch") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(launch));
		Action report = new AbstractAction("Report") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(report));
		return m;
	}

	private JMenu createTestMenu() {
		JMenu m = new JMenu("Test");
		return m;
	}

	private JMenu createConfigurationMenu() {
		JMenu m = new JMenu("Configuration");
		JMenu visualization = new JMenu("Visualization");
		Action graphs = new AbstractAction("Graphs") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		visualization.add(new JMenuItem(graphs));
		Action text = new AbstractAction("Text") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		visualization.add(new JMenuItem(text));
		Action concordances = new AbstractAction("Concordances") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		visualization.add(new JMenuItem(concordances));
		Action diff = new AbstractAction("Diff") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		visualization.add(new JMenuItem(diff));
		m.add(visualization);
		Action project = new AbstractAction("Project") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(project));
		m.addSeparator();
		Action aboutUnitex = new AbstractAction("About Unitex") {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		};
		m.add(new JMenuItem(aboutUnitex));
		return m;
	}
}
