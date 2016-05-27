package fr.gramlab.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import fr.gramlab.GramlabConfigManager;
import fr.gramlab.icons.Icons;
import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.GramlabProjectManager;
import fr.gramlab.project.config.ProjectPreferences;
import fr.gramlab.project.config.maven.MavenDialog;
import fr.gramlab.project.config.maven.UpdateDependenciesDialog;
import fr.gramlab.project.config.preprocess.ConfigBigPictureDialog;
import fr.gramlab.project.config.preprocess.CreateProjectDialog;
import fr.gramlab.project.config.rendering.RenderingConfigDialog;
import fr.gramlab.svn.SvnAddDialog;
import fr.gramlab.svn.SvnCommitDialog;
import fr.gramlab.svn.SvnConflictDialog;
import fr.gramlab.svn.SvnDeleteDialog;
import fr.gramlab.svn.SvnDeleteUrlDialog;
import fr.gramlab.svn.SvnExecutor;
import fr.gramlab.svn.SvnInfo;
import fr.gramlab.svn.SvnInfoDialog;
import fr.gramlab.svn.SvnRevertDialog;
import fr.gramlab.svn.SvnShareDialog;
import fr.gramlab.svn.SvnStatus;
import fr.gramlab.svn.SvnStatusInfo;
import fr.gramlab.svn.SvnUpdateDialog;
import fr.gramlab.util.GramlabDropTarget;
import fr.gramlab.util.GraphSearchDialog;
import fr.gramlab.util.MouseUtil;
import fr.gramlab.workspace.ChangeWorkspaceDialog;
import fr.gramlab.workspace.ProjectAdapter;
import fr.gramlab.workspace.ProjectNode;
import fr.gramlab.workspace.ProjectTabbedPane;
import fr.gramlab.workspace.RootNode;
import fr.gramlab.workspace.WorkspaceTreeModel;
import fr.gramlab.workspace.WorkspaceTreeNode;
import fr.umlv.unitex.DropTargetManager;
import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.files.PersonalFileFilter;
import fr.umlv.unitex.frames.*;
import fr.umlv.unitex.graphrendering.GraphMenuBuilder;
import fr.umlv.unitex.grf.GraphPresentationInfo;
import fr.umlv.unitex.print.PrintManager;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.CompressCommand;
import fr.umlv.unitex.process.commands.Grf2Fst2Command;
import fr.umlv.unitex.process.commands.GrfDiffCommand;
import fr.umlv.unitex.process.commands.MultiCommands;
import fr.umlv.unitex.process.commands.SortTxtCommand;
import fr.umlv.unitex.process.commands.SvnCommand;
import fr.umlv.unitex.process.commands.UncompressCommand;
import fr.umlv.unitex.svn.SvnConflict;
import fr.umlv.unitex.utils.HelpMenuBuilder;

@SuppressWarnings("serial")
public class GramlabFrame extends JFrame {
	ProjectTabbedPane tabbedPane;
	final JPanel processPane=new JPanel(new BorderLayout());
	JTree tree;

	public GramlabFrame() {
		super();
		DropTargetManager.setDropTarget(new GramlabDropTarget());
		JPanel treePane = createWorkspacePane();
		tabbedPane = new ProjectTabbedPane();
		JSplitPane leftPane=new JSplitPane(JSplitPane.VERTICAL_SPLIT,true,treePane,processPane);
		leftPane.setDividerLocation(300);
		leftPane.setPreferredSize(new Dimension(300, 400));
		leftPane.setMinimumSize(new Dimension(0, 0));
		leftPane.setOneTouchExpandable(true);
		JSplitPane bigSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				leftPane, tabbedPane);
		bigSplit.setOneTouchExpandable(true);
		setContentPane(bigSplit);
		refreshTitle();
		GlobalProjectManager.getAs(GramlabProjectManager.class)
			.addProjectListener(new ProjectAdapter() {
			@Override
			public void projectOpened(GramlabProject p, int pos) {
				if (p != null) {
					refreshProjectMenu(p);
					/* We have a special case for the .snt file, if any,
					 * because p.openFile(f) would make it the current corpus
					 * and the user may have wanted to keep the .txt as the current
					 * corpus, even if the .snt was opened
					 */
					ArrayList<File> files = p.getOpenFrames();
					for (File f : files) {
						if (FileUtil.getExtensionInLowerCase(f).equals("snt")) {
							GlobalProjectManager.search(f)
								.getFrameManagerAs(InternalFrameManager.class)
								.newTextFrame(f,false);
						} else {
							p.openFile(f,false);
						}
					}
				}
			}

			@Override
			public void currentProjectChanged(GramlabProject p1, int pos) {
				refreshTitle();
				refreshProjectMenu(p1);
				processPane.removeAll();
				if (p1!=null) {
					processPane.add(p1.getProcessPane(),BorderLayout.CENTER);
				}
				processPane.revalidate();
				processPane.repaint();
			}
		});
		setMinimumSize(new Dimension(800, 600));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				final String[] options = { "Yes", "No" };
				final int n = JOptionPane
						.showOptionDialog(GramlabFrame.this,
								"Do you really want to quit ?", "",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[0]);
				if (n == 1) {
					return;
				}
				GramlabConfigManager.saveConfigFile();
				ProjectPreferences.setClosingGramlab(true);
				if (GlobalProjectManager.getAs(GramlabProjectManager.class).closeAllProjects()) {
					setVisible(false);
					dispose();
				} else {
					ProjectPreferences.setClosingGramlab(false);
				}
			}

			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setJMenuBar(createMenuBar());
		refreshProjectMenu(GlobalProjectManager.
		            getAs(GramlabProjectManager.class).getCurrentProject());
	}

	void refreshTitle() {
		String project = ConfigManager.getManager().getCurrentLanguage();
		setTitle("Gramlab - "
				+ ((project == null) ? "no current project"
						: "current project is " + project));
	}

	private JPanel createWorkspacePane() {
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createTitledBorder("Projects"));
		p.setMinimumSize(new Dimension(0, 0));
		p.setPreferredSize(new Dimension(200, 1));
		final WorkspaceTreeModel model = new WorkspaceTreeModel();
		tree = new JTree(model);
		tree.setLargeModel(true);
		tree.setRootVisible(false);
		GlobalProjectManager.getAs(GramlabProjectManager.class)
			.addProjectListener(new ProjectAdapter() {
			@Override
			public void projectOpened(GramlabProject p, int pos) {
				tree.expandPath(new TreePath(model.getPathToRoot(model
						.getProjectNode(p))));
				tree.setSelectionPath(null);
				refreshProjectMenu(p);
			}

			@Override
			public void projectClosed(GramlabProject p, int pos) {
				tree.collapsePath(new TreePath(model.getPathToRoot(model
						.getProjectNode(p))));
				refreshProjectMenu(p);
			}
		});
		tree.setCellRenderer(new DefaultTreeCellRenderer() {

			private String createHackSpaces(boolean html) {
				StringBuilder b=new StringBuilder();
				for (int i=0;i<30;i++) {
					b.append(" ");
					b.append(html?"&nbsp;":" ");
				}
				return b.toString();
			}

			private String HACK_SPACES_TEXT=createHackSpaces(false);
			private String HACK_SPACES_HTML=createHackSpaces(true);

			@Override
			public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean sel, boolean expanded, boolean leaf,
					int row, boolean hasFocus) {
				WorkspaceTreeNode node = (WorkspaceTreeNode) value;
				String name = node.getFile().getName();
				if (value instanceof RootNode) {
					/* Nothing to do, the root node is never shown */
				} else if (value instanceof ProjectNode) {
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
					GramlabProject project = n.getProject();
					if (GlobalProjectManager.getAs(GramlabProjectManager.class)
							.getCurrentProject() == project) {
						/*
						 * The current project is displayed with a different
						 * color
						 */
						setForeground(Color.RED);
					}
					if (project.isOpen()) {
						setIcon(Icons.openFolderIcon);
						SvnInfo info = n.getProject().getSvnInfo(n.getFile());
						if (info != null) {
							String text = "<html><body>" + getText()
									+ " <i><font color=\"#A0A0A0\">" + info
									+ "</font></i>"+HACK_SPACES_HTML+"</body></html>";
							setText(text);
						}
					}
				} else {
					/* Normal case */
					super.getTreeCellRendererComponent(tree, name, sel,
							expanded, leaf, row, hasFocus);
					if (expanded) {
						setIcon(Icons.openFolderIcon);
					}
					if (node.getFile().exists() && !node.getFile().canWrite()) {
						setForeground(Color.RED.darker());
						setText(getText() + " (read-only)"+HACK_SPACES_TEXT);
					}
					GramlabProject p = GlobalProjectManager.getAs(GramlabProjectManager.class)
							.getProject(node.getFile());
					SvnInfo info = p.getSvnInfo(node.getFile());
					if (info != null) {
						String text = getText();
						switch (info.getStatus()) {
						case UNVERSIONED: {
							text = "<html><body><strong><font color=\"#AAAAAA\">?</font></strong> "
									+ text + HACK_SPACES_HTML+"</body></html>";
							break;
						}
						case ADDED: {
							text = "<html><body><strong><font color=\"#FF00FF\">+</font></strong> "
									+ text + HACK_SPACES_HTML+"</body></html>";
							break;
						}
						case MODIFIED:
						case REPLACED:
						case TYPE_CHANGED: {
							text = "<html><body><strong><font color=\"#FF00FF\">*</font></strong> "
									+ text
									+ " <i><font color=\"#A0A0A0\">"
									+ info + HACK_SPACES_HTML+"</font></i></body></html>";
							break;
						}
						case UNMODIFIED: {
							text = "<html><body>" + text
									+ " <i><font color=\"#A0A0A0\">" + info
									+ HACK_SPACES_HTML+"</font></i></body></html>";
							break;
						}
						case DELETED:
						case MISSING: {
							text = "<html><body><strong><font color=\"#FF0000\">-</font></strong> "
									+ text + HACK_SPACES_HTML+"</body></html>";
							break;
						}
						case CONFLICT: {
							text = "<html><body><strong><font color=\"#FF0000\">C</font></strong> "
									+ text + HACK_SPACES_HTML+"</body></html>";
							break;
						}
						default:
							text=text+HACK_SPACES_HTML;
							break;
						}
						setText(text);
					} else {
						setText(getText()+HACK_SPACES_TEXT);
					}
				}
				return this;
			}

		});
		/*
		 * If we let the tree scroll on expanding directories, then a
		 * double-click a directory node will be considered as targeting the
		 * first son node of the directory and not the directory itself,
		 * provoking a bug
		 */
		tree.setScrollsOnExpand(false);
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				boolean leftClick   = SwingUtilities.isLeftMouseButton(e);
				boolean doubleClick = (e.getButton() == MouseEvent.BUTTON1 && e
						.getClickCount() == 2);
				boolean rightClick = MouseUtil.isPopupTrigger(e);
				if (!doubleClick && !rightClick && !leftClick)
					return;
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
						// update project menu
						refreshProjectMenu(pn.getProject());
						if (rightClick) {
							TreePath[] selectedPath=tree.getSelectionModel().getSelectionPaths();
							if (!isSelectedPath(path,selectedPath)) {
								tree.getSelectionModel().setSelectionPath(path);
							}
							final JPopupMenu popup = createProjectPopup(pn
									.getProject());
							EventQueue.invokeLater(new Runnable() {
								public void run() {
									popup.show(tree, x, y);
								}
							});
							return;
						}
						if (doubleClick) {
							if (!pn.getProject().isOpen()) {
								GlobalProjectManager.getAs(GramlabProjectManager.class)
									.openProject(pn.getProject());
							}
							return;
						}
						return;
					}
					/* If we have a normal node */
					WorkspaceTreeNode node = (WorkspaceTreeNode) o;
					File f = node.getFile();
					GramlabProject project = GlobalProjectManager.getAs(GramlabProjectManager.class)
								.getProject(f);
					// update project menu
          refreshProjectMenu(project);
					if (doubleClick && f.isFile()) {
						/* Double-click on a file */
						project.openFile(f,false);
						return;
					}
					if (rightClick) {
						TreePath[] selectedPath=tree.getSelectionModel().getSelectionPaths();
						if (!isSelectedPath(path,selectedPath)) {
							tree.getSelectionModel().setSelectionPath(path);
						}
						final JPopupMenu popup=createFilePopup(project);
						if (popup != null) {
							EventQueue.invokeLater(new Runnable() {
								public void run() {
									popup.show(tree, x, y);
								}
							});
						}
						return;
					}
				}
			}


			private JPopupMenu createFilePopup(final GramlabProject project) {
				JPopupMenu popup = new JPopupMenu();
				/*
				 * Some actions can be applied on several files, so we compute
				 * sublists corresponding to different file types
				 */
				final ArrayList<File> allFiles = getConcernedFiles(tree
						.getSelectionPaths(), project, null);
				if (allFiles.size()==1 && allFiles.get(0).isDirectory()) {
					JMenu newMenu=new JMenu("New");
					Action mkdir=new AbstractAction("Directory") {
						@Override
						public void actionPerformed(ActionEvent e) {
							new MkdirDialog(allFiles.get(0));
						}
					};
					newMenu.add(new JMenuItem(mkdir));
					Action newFile=new AbstractAction("File") {
						@Override
						public void actionPerformed(ActionEvent e) {
							new NewFileDialog(allFiles.get(0));
						}
					};
					newMenu.add(new JMenuItem(newFile));
					Action newGrf=new AbstractAction("Graph") {
						@Override
						public void actionPerformed(ActionEvent e) {
							new NewGrfDialog(allFiles.get(0));
						}
					};
					newMenu.add(new JMenuItem(newGrf));
					popup.add(newMenu);
				}
				final ArrayList<File> grfs=getFilesContainedInDirectories(allFiles,"grf");
				Action compileAllGrf=new AbstractAction("Compile .grf found in directories") {
					@Override
					public void actionPerformed(ActionEvent e) {
						MultiCommands commands = new MultiCommands();
						for (File f : grfs) {
							Grf2Fst2Command cmd = project
							.createGrf2Fst2Command(f, null, project
									.isDebugMode(), false);
									commands.addCommand(cmd);
						}
						Launcher.exec(commands, false);
					}
				};
				compileAllGrf.setEnabled(grfs.size()!=0);
				final ArrayList<File> dics=getFilesContainedInDirectories(allFiles,"dic");
				Action compressAllDic=new AbstractAction("Compress .dic found in directories") {
					@Override
					public void actionPerformed(ActionEvent e) {
						MultiCommands commands = new MultiCommands();
						for (File f : dics) {
							CompressCommand cmd = new CompressCommand()
									.dic(f);
							if (project.isSemitic()) {
								cmd = cmd.semitic();
							}
							commands.addCommand(cmd);
						}
						Launcher.exec(commands, false);
					}
				};
				compressAllDic.setEnabled(dics.size()!=0);
				final ArrayList<File> bins=getFilesContainedInDirectories(allFiles,"bin");
				Action uncompressAllBin=new AbstractAction("Uncompress .bin found in directories") {
					@Override
					public void actionPerformed(ActionEvent e) {
						MultiCommands commands = new MultiCommands();
						for (File f : bins) {
							UncompressCommand cmd = new UncompressCommand()
									.bin(f);
							commands.addCommand(cmd);
						}
						Launcher.exec(commands, false);
					}
				};
				uncompressAllBin.setEnabled(dics.size()!=0);
				final ArrayList<File> grfFiles = getConcernedFiles(tree
						.getSelectionPaths(), project, "grf");
				final ArrayList<File> dicFiles = getConcernedFiles(tree
						.getSelectionPaths(), project, "dic");
				final ArrayList<File> binFiles = getConcernedFiles(tree
						.getSelectionPaths(), project, "bin");
				JMenu menu = new JMenu("Open with...");
				final ArrayList<File> filesNotDirs=new ArrayList<File>();
				for (File f:allFiles) {
					if (f.isFile()) {
						filesNotDirs.add(f);
					}
				}
				Action editor = new AbstractAction("Internal editor") {
					public void actionPerformed(ActionEvent e) {
						for (File f : filesNotDirs) {
							GlobalProjectManager.search(f)
								.getFrameManagerAs(InternalFrameManager.class)
								.newFileEditionTextFrame(f);
						}
					}
				};
				menu.add(new JMenuItem(editor));
				final File htmlViewer = project.getHtmlViewer();
				Action html = new AbstractAction("Html viewer") {
					public void actionPerformed(ActionEvent e) {
						if (htmlViewer != null) {
							for (File f : filesNotDirs) {
								Launcher
										.execExternalCommand(htmlViewer
												.getAbsolutePath(), f
												.getAbsolutePath());
							}
						}
					}
				};
				if (htmlViewer == null) {
					html.setEnabled(false);
				}
				menu.add(new JMenuItem(html));
				final File textEditor = project.getTextEditor();
				Action extEditor = new AbstractAction("External text editor") {
					public void actionPerformed(ActionEvent e) {
						if (textEditor != null) {
							for (File f : filesNotDirs) {
								Launcher
										.execExternalCommand(textEditor
												.getAbsolutePath(), f
												.getAbsolutePath());
							}
						}
					}
				};
				if (textEditor == null) {
					extEditor.setEnabled(false);
				}
				menu.add(new JMenuItem(extEditor));
				menu.setEnabled(filesNotDirs.size()!=0);
				popup.add(menu);
				popup.addSeparator();
				/* Graph menu items */
				JMenu graphMenu=new JMenu("Graphs");
				graphMenu.add(new JMenuItem(compileAllGrf));
				if (grfFiles.size() > 0) {
					Action open = new AbstractAction("Open graphs") {
						public void actionPerformed(ActionEvent e) {
							for (File f : grfFiles) {
								GlobalProjectManager.search(f)
									.getFrameManagerAs(InternalFrameManager.class)
									.newGraphFrame(f);
							}
							GlobalProjectManager.getAs(GramlabProjectManager.class)
								.setCurrentProject(project);
						}
					};
					graphMenu.add(new JMenuItem(open));
					Action compile = new AbstractAction("Compile graphs") {
						public void actionPerformed(ActionEvent e) {
							MultiCommands commands = new MultiCommands();
							for (File f : grfFiles) {
								Grf2Fst2Command cmd = project
										.createGrf2Fst2Command(f, null, project
												.isDebugMode(), false);
								commands.addCommand(cmd);
							}
							Launcher.exec(commands, false);
						}
					};
					graphMenu.add(new JMenuItem(compile));
					Action diffSelected=new AbstractAction("Diff selected pair of graphs") {
						@Override
						public void actionPerformed(ActionEvent e) {
							final File diffResult = new File(grfFiles.get(0).getParent(),
							"..diff");
							final GrfDiffCommand cmd = new GrfDiffCommand()
							.files(grfFiles.get(0),grfFiles.get(1)).output(diffResult);
							Launcher.exec(cmd, true, new GraphFrame.ShowDiffDo(
									grfFiles.get(0),grfFiles.get(1),diffResult));
						}
					};
					diffSelected.setEnabled(grfFiles.size() == 2);
					graphMenu.add(new JMenuItem(diffSelected));
					Action diff = new AbstractAction(
							"Diff with another graph...") {
						public void actionPerformed(ActionEvent e) {
							File file=grfFiles.get(0);
							JFileChooser fc = new JFileChooser(file.getParentFile());
							fc.setMultiSelectionEnabled(false);
							fc.setDialogType(JFileChooser.OPEN_DIALOG);
							final int returnVal = fc
									.showOpenDialog(GramlabFrame.this);
							if (returnVal != JFileChooser.APPROVE_OPTION) {
								// we return if the user has clicked on CANCEL
								return;
							}
							final File f = fc.getSelectedFile();
							if (f == null || !f.exists())
								return;
							final File diffResult = new File(file.getParent(),
									"..diff");
							final GrfDiffCommand cmd = new GrfDiffCommand()
									.files(file, f).output(diffResult);
							Launcher.exec(cmd, true, new GraphFrame.ShowDiffDo(
									file, f, diffResult));
						}
					};
					diff.setEnabled(grfFiles.size() == 1);
					graphMenu.add(new JMenuItem(diff));
				}
				if (atLeastOneMenuItemEnabled(graphMenu)) {
					popup.add(graphMenu);
				}
				/* .dic menu items */
				JMenu dicMenu=new JMenu("Dictionaries");
				dicMenu.add(new JMenuItem(compressAllDic));
				dicMenu.add(new JMenuItem(uncompressAllBin));
				if (dicFiles.size() != 0) {
					Action open = new AbstractAction("Open dictionaries") {
						public void actionPerformed(ActionEvent e) {
							for (File f:dicFiles) {
								project.openDicFile(f);
							}
							GlobalProjectManager.getAs(GramlabProjectManager.class)
								.setCurrentProject(project);
						}
					};
					dicMenu.add(new JMenuItem(open));
					Action compress = new AbstractAction(
							"Compress selected dictionaries") {
						public void actionPerformed(ActionEvent e) {
							MultiCommands commands = new MultiCommands();
							for (File f : dicFiles) {
								CompressCommand cmd = new CompressCommand()
										.dic(f);
								if (project.isSemitic()) {
									cmd = cmd.semitic();
								}
								commands.addCommand(cmd);
							}
							Launcher.exec(commands, false);
						}
					};
					dicMenu.add(new JMenuItem(compress));
					Action check = new AbstractAction("Check dictionary") {
						public void actionPerformed(ActionEvent e) {
							File file=dicFiles.get(0);
							GlobalProjectManager.search(file)
								.getFrameManagerAs(InternalFrameManager.class)
								.newCheckDicFrame(file);
						}
					};
					check.setEnabled(dicFiles.size() == 1);
					dicMenu.add(new JMenuItem(check));
				}
				if (binFiles.size() > 0) {
					Action compress = new AbstractAction(
							"Uncompress dictionaries") {
						public void actionPerformed(ActionEvent e) {
							MultiCommands commands = new MultiCommands();
							for (File f : binFiles) {
								UncompressCommand cmd = new UncompressCommand()
										.bin(f);
								commands.addCommand(cmd);
							}
							Launcher.exec(commands, false);
						}
					};
					dicMenu.add(new JMenuItem(compress));
				}
				if (atLeastOneMenuItemEnabled(dicMenu)) {
					popup.add(dicMenu);
				}
				ArrayList<File> files = getConcernedFiles(tree
						.getSelectionPaths(), project, null);
				Action svnDelete = getDeleteAction(files, project);
				popup.add(new JMenuItem(svnDelete));
				popup = addSvnItems(popup,project);
				return popup;
			}

			private JPopupMenu createProjectPopup(final GramlabProject project) {
				JPopupMenu popup = new JPopupMenu();
				final boolean open = project.isOpen();
				Action openClose = new AbstractAction((open ? "Close" : "Open")
						+ " project " + project.getName()) {
					public void actionPerformed(ActionEvent e) {
						if (open)
							GlobalProjectManager.getAs(GramlabProjectManager.class)
								.closeProject(project);
						else {
							GlobalProjectManager.getAs(GramlabProjectManager.class)
								.openProject(project);
							GlobalProjectManager.getAs(GramlabProjectManager.class)
								.setCurrentProject(project);
						}
					}
				};
				popup.add(new JMenuItem(openClose));
				Action delete = new AbstractAction("Delete") {
					public void actionPerformed(ActionEvent e) {
						String urlToDelete=project.getSvnRepositoryUrl();
						if (!GlobalProjectManager.getAs(GramlabProjectManager.class)
								.deleteProject(project,true)) {
							return;
						}
						if (urlToDelete!=null) {
							/* If the project is versioned, the user may also
							 * want to delete the repository on the server
							 */
							new SvnDeleteUrlDialog(urlToDelete);
						}
					}
				};
				popup.add(new JMenuItem(delete));
				if (open
						&& GlobalProjectManager.getAs(GramlabProjectManager.class)
							.getCurrentProject() != project) {
					/* If the project is not the current one, we offer here to
					 * make it so */
					Action setCurrent = new AbstractAction(
							"Set as current project") {
						public void actionPerformed(ActionEvent e) {
							GlobalProjectManager.getAs(GramlabProjectManager.class)
								.setCurrentProject(project);
						}
					};
					popup.add(new JMenuItem(setCurrent));
				}
				if (open) {
					JMenu configureMenu = new JMenu("Configure");
					Action rendering = new AbstractAction("Rendering") {
						public void actionPerformed(ActionEvent e) {
							EventQueue.invokeLater(new Runnable() {
								@Override
								public void run() {
									new RenderingConfigDialog(project);
								}
							});
						}
					};
					configureMenu.add(new JMenuItem(rendering));
					Action preprocessing = new AbstractAction("Preprocessing") {
						public void actionPerformed(ActionEvent e) {
							EventQueue.invokeLater(new Runnable() {
								@Override
								public void run() {
									new ConfigBigPictureDialog(project);
								}
							});
						}
					};
					configureMenu.add(new JMenuItem(preprocessing));
					popup.add(configureMenu);
					Action console = new AbstractAction("Show Unitex console") {
						public void actionPerformed(ActionEvent e) {
							EventQueue.invokeLater(new Runnable() {
								@Override
								public void run() {
									GlobalProjectManager.search(null)
										.getFrameManagerAs(InternalFrameManager.class)
										.showConsoleFrame();
								}
							});
						}
					};
					popup.add(new JMenuItem(console));
					popup.add(new JMenuItem(getMavenAction(project)));
					popup.add(new JMenuItem(getUpdateDependenciesAction(project)));
					Action refresh=new AbstractAction("Refresh") {

						@Override
						public void actionPerformed(ActionEvent e) {
							project.asyncUpdateSvnInfo(null,true);
						}
					};
					popup.add(new JMenuItem(refresh));
				}
				JMenu svnMenu=new JMenu("SVN");
				Action svnInfo = getSvnInfoAction(project);
				if (svnInfo!=null) {
					svnMenu.add(new JMenuItem(svnInfo));
				}
				Action svnShare = getSvnShareAction(project);
				if (svnShare != null) {
					svnMenu.add(new JMenuItem(svnShare));
				}
				JMenu updateMenu=new JMenu("Update");
				updateMenu.setEnabled(false);
				Action svnUpdateToHead = getSvnUpdateAction(project,null,true);
				if (svnUpdateToHead != null) {
					updateMenu.add(new JMenuItem(svnUpdateToHead));
					if (svnUpdateToHead.isEnabled()) {
						updateMenu.setEnabled(true);
					}
				}
				Action svnUpdateToRevision = getSvnUpdateAction(project,null,false);
				if (svnUpdateToRevision != null) {
					updateMenu.add(new JMenuItem(svnUpdateToRevision));
					if (svnUpdateToRevision.isEnabled()) {
						updateMenu.setEnabled(true);
					}
				}
				if (project.isOpen() && project.getSvnInfo(project.getSrcDirectory()) != null) {
					svnMenu.add(updateMenu);
				}
				Action svnCommit = getSvnCommitAction(project);
				if (svnCommit != null) {
					svnMenu.add(new JMenuItem(svnCommit));
				}
				Action svnCleanup = getSvnCleanupAction(project);
				if (svnCleanup != null) {
					svnMenu.add(new JMenuItem(svnCleanup));
				}
				Action lookForConflicts=new AbstractAction("Look for graphs in conflict") {
					@Override
					public void actionPerformed(ActionEvent e) {
						project.getSvnMonitor().monitor(false);
					}
				};
				if (project.isOpen() && project.getSvnInfo(project.getSrcDirectory()) != null) {
					svnMenu.add(new JMenuItem(lookForConflicts));
				}
				if (atLeastOneMenuItemEnabled(svnMenu)) {
					popup.add(svnMenu);
				}
				return popup;
			}
		});
		p.add(new JScrollPane(tree));
		return p;
	}

	private void refreshProjectMenu(final GramlabProject project) {
    // TODO(martinec) avoid to redraw a menu already refreshed
    JMenu m = getJMenuBar().getMenu(1);
		m.removeAll();
		addToProjectMenu(m,project);
	}

  private JMenu addToProjectMenu(JMenu m, final GramlabProject project) {
    // nothing to do if project is null
		if(project == null) {
			return m;
		}

    final boolean open = project.isOpen();

		// run project
    if (open) {
      if(GlobalProjectManager.getAs(GramlabProjectManager.class)
                             .getCurrentProject() == project) {
				Action run=new AbstractAction("Run " + project.getName()) {
					@Override
					public void actionPerformed(ActionEvent e) {
						project.getProcessPane().launchProcess();
					}
				};
				JMenuItem runMenuItem = new JMenuItem(run);
				runMenuItem.setAccelerator( KeyStroke.getKeyStroke("F11"));
				//runMenuItem.setAccelerator(KeyStroke.getKeyStroke('R',
				                 //Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
				m.add(runMenuItem);
				m.addSeparator();
			}
		}

    // set as current
    if (open) {
      if(GlobalProjectManager.getAs(GramlabProjectManager.class)
                             .getCurrentProject() != project) {
				/* If the project is not the current one, we offer here to
				 * make it so */
				Action setCurrent = new AbstractAction(
						"Set " + project.getName() + " as current project") {
					public void actionPerformed(ActionEvent e) {
						GlobalProjectManager.getAs(GramlabProjectManager.class)
							.setCurrentProject(project);
					}
				};
				m.add(new JMenuItem(setCurrent));
				m.addSeparator();
		  }
    }

    // configure
    if (open) {
      JMenu configureMenu = new JMenu("Configure");
      // preprocessing
      Action preprocessing = new AbstractAction("Preprocessing") {
        public void actionPerformed(ActionEvent e) {
          EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
              new ConfigBigPictureDialog(project);
            }
          });
        }
      };
      configureMenu.add(new JMenuItem(preprocessing));
      // rendering
      Action rendering = new AbstractAction("Rendering") {
        public void actionPerformed(ActionEvent e) {
          EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
              new RenderingConfigDialog(project);
            }
          });
        }
      };
      configureMenu.add(new JMenuItem(rendering));
      m.add(configureMenu);
      m.addSeparator();
		}

    // open/close
    Action openClose = new AbstractAction((open ? "Close" : "Open " + project.getName())) {
      public void actionPerformed(ActionEvent e) {
        if (open)
          GlobalProjectManager.getAs(GramlabProjectManager.class)
            .closeProject(project);
        else {
          GlobalProjectManager.getAs(GramlabProjectManager.class)
            .openProject(project);
          GlobalProjectManager.getAs(GramlabProjectManager.class)
            .setCurrentProject(project);
        }
      }
    };
    m.add(new JMenuItem(openClose));

    // delete
    Action delete = new AbstractAction("Delete") {
      public void actionPerformed(ActionEvent e) {
        String urlToDelete=project.getSvnRepositoryUrl();
        if (!GlobalProjectManager.getAs(GramlabProjectManager.class)
            .deleteProject(project,true)) {
          return;
        }
        if (urlToDelete!=null) {
          /* If the project is versioned, the user may also
           * want to delete the repository on the server
           */
          new SvnDeleteUrlDialog(urlToDelete);
        }
      }
    };
    m.add(new JMenuItem(delete));

    // refresh
		if (open) {
      Action refresh=new AbstractAction("Refresh") {
        @Override
        public void actionPerformed(ActionEvent e) {
          project.asyncUpdateSvnInfo(null,true);
        }
      };
      m.add(new JMenuItem(refresh));
		}

		// console
		if (open) {
      if(GlobalProjectManager.getAs(GramlabProjectManager.class)
                             .getCurrentProject() == project) {
				m.addSeparator();
				Action console = new AbstractAction("Show console") {
					public void actionPerformed(ActionEvent e) {
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								GlobalProjectManager.search(null)
									.getFrameManagerAs(InternalFrameManager.class)
									.showConsoleFrame();
							}
						});
					}
				};
				m.add(new JMenuItem(console));
			}
    }

    return m;
  }

	protected boolean atLeastOneMenuItemEnabled(JMenu menu) {
		for (int i=0;i<menu.getMenuComponentCount();i++) {
			if (menu.getMenuComponent(i).isEnabled()) return true;
		}
		return false;
	}

	protected boolean isSelectedPath(TreePath path, TreePath[] selectedPath) {
		if (selectedPath==null) return false;
		for (TreePath p:selectedPath) {
			if (p.equals(path)) return true;
		}
		return false;
	}

	private boolean contains(String[] t,String s) {
		for (int i=0;i<t.length;i++) {
			if (t[i].equals(s)) return true;
		}
		return false;
	}

	private void lookForFilesByExtensions(ArrayList<File> files,File file,String[] ext) {
		if (file==null) return;
		if (file.isFile()) {
			String extension=FileUtil.getExtensionInLowerCase(file);
			if (contains(ext,extension)) {
				files.add(file);
			}
			return;
		}
		if (file.isDirectory()) {
			for (File f:file.listFiles()) {
				lookForFilesByExtensions(files,f,ext);
			}
		}
	}


	protected ArrayList<File> getFilesContainedInDirectories(ArrayList<File> dirs,String extension) {
		ArrayList<File> list=new ArrayList<File>();
		String[] extensions=new String[] {extension};
		for (File f:dirs) {
			if (!f.isDirectory()) continue;
			lookForFilesByExtensions(list,f,extensions);
		}
		return list;
	}

	protected JPopupMenu addSvnItems(JPopupMenu popup,final GramlabProject project) {
		if (null == project.getSvnInfo(project.getSrcDirectory())) {
			/* Nothing to do if the project is not versioned */
			return popup;
		}
		if (popup == null) {
			popup = new JPopupMenu();
		}
		JMenu menu=new JMenu("SVN");
		Action svnInfo = getSvnInfoAction(project);
		menu.add(new JMenuItem(svnInfo));
		ArrayList<File> files = getConcernedFiles(tree.getSelectionPaths(), project, null);
		ArrayList<File> versioned=getVersionedFiles(files,project);
		Action svnAdd = getSvnAddAction(files, project);
		menu.add(new JMenuItem(svnAdd));

		JMenu updateMenu=new JMenu("Update");
		updateMenu.setEnabled(false);
		Action svnUpdateToHead = getSvnUpdateAction(project,files,true);
		if (svnUpdateToHead != null) {
			updateMenu.add(new JMenuItem(svnUpdateToHead));
			if (svnUpdateToHead.isEnabled()) {
				updateMenu.setEnabled(true);
			}
		}
		Action svnUpdateToRevision = getSvnUpdateAction(project,files,false);
		if (svnUpdateToRevision != null) {
			updateMenu.add(new JMenuItem(svnUpdateToRevision));
			if (svnUpdateToRevision.isEnabled()) {
				updateMenu.setEnabled(true);
			}
		}
		menu.add(updateMenu);
		Action svnCommit = getSvnCommitAction(files,project,files);
		menu.add(new JMenuItem(svnCommit));
		Action svnResolve = getSvnResolveAction(files);
		menu.add(new JMenuItem(svnResolve));
		Action svnIgnore = getSvnIgnoreAction(files, project);
		menu.add(new JMenuItem(svnIgnore));
		JMenu replaceMenu=new JMenu("Replace with");
		replaceMenu.setEnabled(false);
		Action svnReplaceWithBase = getSvnReplaceAction(versioned, true);
		replaceMenu.add(new JMenuItem(svnReplaceWithBase));
		Action svnReplaceWithLatest = getSvnReplaceAction(versioned, false);
		replaceMenu.add(new JMenuItem(svnReplaceWithLatest));
		if (svnReplaceWithBase.isEnabled() || svnReplaceWithLatest.isEnabled()) {
			replaceMenu.setEnabled(true);
		}
		menu.add(replaceMenu);
		Action svnRevert = getSvnRevertAction(files,project);
		menu.add(new JMenuItem(svnRevert));
		popup.add(menu);
		return popup;
	}

	private Action getSvnReplaceAction(final ArrayList<File> files,
			final boolean replaceWithBaseRevision) {
		Action a = new AbstractAction(
				replaceWithBaseRevision ? "base revision"
						: "latest from repository") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (File f : files) {
					if (f.exists()) {
						FileUtil.rm(f);
					}
					SvnCommand cmd = new SvnCommand().update(
							replaceWithBaseRevision ? -2 : -1, f, true);
					Launcher.execWithoutTracing(cmd);
				}
			}
		};
		a.setEnabled(files.size()>0);
		return a;
	}

	private Action getSvnInfoAction(final GramlabProject p) {
		if (!p.isOpen() || p.getSvnInfo(p.getSrcDirectory()) == null) {
			return null;
		}
		Action a = new AbstractAction("Info") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SvnInfoDialog(p);
			}
		};
		return a;
	}


	/**
	 * Returns an action that must launch a 'svn add' on those of the given
	 * files that can be added.
	 */
	private Action getSvnAddAction(ArrayList<File> files, final GramlabProject project) {
		final ArrayList<File> unversioned = getUnversionedOrIgnoredFilesForSvnAdd(files,
				project);
		Action a = new AbstractAction("Add files...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SvnAddDialog(unversioned, project);
			}
		};
		if (unversioned.size() == 0) {
			a.setEnabled(false);
		}
		return a;
	}

	private Action getSvnIgnoreAction(ArrayList<File> files, GramlabProject project) {
		final ArrayList<File> files2 = getUnversionedFilesForSvnIgnore(files,
				project);
		Action a = new AbstractAction("Ignore files...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				SvnExecutor.ignoreFiles(files2);
			}
		};
		if (files2.size() == 0) {
			a.setEnabled(false);
		}
		return a;
	}


	private Action getSvnRevertAction(final ArrayList<File> files,final GramlabProject project) {
		final ArrayList<File> revertable = getRevertableFiles(files,
				project);
		Action a = new AbstractAction("Revert") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SvnRevertDialog(revertable,project);
			}
		};
		if (revertable.size() == 0) {
			a.setEnabled(false);
		}
		return a;
	}


	private Action getMavenAction(final GramlabProject project) {
		Action a = new AbstractAction("Export as a maven component") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new MavenDialog(project);
			}
		};
		return a;
	}

	private Action getUpdateDependenciesAction(final GramlabProject project) {
		Action a = new AbstractAction("Update maven dependencies") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new UpdateDependenciesDialog(project);
			}
		};
		return a;
	}

	/**
	 * Returns an action that must launch a 'svn delete' on those of the given
	 * files that can be added.
	 */
	private Action getDeleteAction(final ArrayList<File> files,
			final GramlabProject project) {
		Action a = new AbstractAction("Delete") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SvnDeleteDialog(files, project);
			}
		};
		if (files.size() == 0) {
			a.setEnabled(false);
		}
		return a;
	}

	/**
	 * Returns an action that must launch a 'svn commit' on those of the given
	 * files that are either modified or added ones.
	 */
	private Action getSvnCommitAction(ArrayList<File> files,
			final GramlabProject project,final ArrayList<File> clickedFiles) {
		final SvnStatusInfo info = getCommittableFiles(files, project);
		Action a = new AbstractAction("Commit files...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SvnCommitDialog(info,project,clickedFiles);
			}
		};
		if (info.getNumberOfCommittableFiles() == 0) {
			a.setEnabled(false);
		}
		return a;
	}

	private Action getSvnResolveAction(ArrayList<File> files) {
		File f;
		final SvnConflict c;
		if (files.size() == 1) {
			f = files.get(0);
			c = SvnConflict.getConflict(f);
		} else {
			c = null;
		}
		Action a = new AbstractAction("Resolve conflict") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SvnConflictDialog(c);
			}
		};
		if (c == null) {
			a.setEnabled(false);
		}
		return a;
	}

	/**
	 * For a svn add operations, we don't mind if the parents are themselves unversioned
	 */
	private ArrayList<File> getUnversionedOrIgnoredFilesForSvnAdd(ArrayList<File> files,
			GramlabProject project) {
		ArrayList<File> unversioned = new ArrayList<File>();
		for (File f : files) {
			SvnInfo i = project.getSvnInfo(f);
			if (i == null || i.getStatus() == SvnStatus.UNVERSIONED) {
				unversioned.add(f);
			}
		}
		return unversioned;
	}


	/**
	 * For a svn ignore operations, we DO mind if the parents are themselves unversioned
	 */
	private ArrayList<File> getUnversionedFilesForSvnIgnore(ArrayList<File> files,
			GramlabProject project) {
		ArrayList<File> unversioned = new ArrayList<File>();
		for (File f : files) {
			SvnInfo i = project.getSvnInfo(f);
			SvnInfo iParent = project.getSvnInfo(f.getParentFile());
			if ((i != null && i.getStatus() == SvnStatus.UNVERSIONED)
					&& iParent!=null && iParent.getStatus()!=SvnStatus.UNVERSIONED
					&& iParent.getStatus()!=SvnStatus.IGNORED) {
				unversioned.add(f);
			}
		}
		return unversioned;
	}

	private ArrayList<File> getRevertableFiles(ArrayList<File> files,
			GramlabProject project) {
		ArrayList<File> revertable = new ArrayList<File>();
		for (File f : files) {
			SvnInfo i = project.getSvnInfo(f);
			SvnInfo iParent = project.getSvnInfo(f.getParentFile());
			if ((i == null || (i.getStatus() != SvnStatus.UNMODIFIED && i.getStatus() != SvnStatus.MODIFIED && i.getStatus() != SvnStatus.UNVERSIONED))
					&& iParent!=null && iParent.getStatus()!=SvnStatus.UNVERSIONED
					&& iParent.getStatus()!=SvnStatus.IGNORED) {
				revertable.add(f);
			}
		}
		return revertable;
	}

	private ArrayList<File> getVersionedFiles(ArrayList<File> files,
			GramlabProject project) {
		if (files==null) return null;
		ArrayList<File> versioned = new ArrayList<File>();
		for (File f : files) {
			SvnInfo i = project.getSvnInfo(f);
			if (i==null) continue;
			switch (i.getStatus()) {
				case UNVERSIONED:
				case IGNORED:
				case ADDED: continue;
				default: versioned.add(f);
			}
		}
		return versioned;
	}


	@SuppressWarnings("unchecked")
	private SvnStatusInfo getCommittableFiles(ArrayList<File> srcFiles,
			GramlabProject project) {
		ArrayList<File> files=(ArrayList<File>) srcFiles.clone();
		expandDirectories(project,files);
		SvnStatusInfo info = new SvnStatusInfo(project);
		for (File f : files) {
			SvnInfo i = project.getSvnInfo(f);
			if (i == null)
				continue;
			switch (i.getStatus()) {
			case UNVERSIONED:
				info.addUnversionedFile(f);
				break;
			case ADDED:
				info.addAddedFile(f);
				break;
			case MODIFIED:
			case REPLACED:
			case TYPE_CHANGED:
				info.addModifiedFile(f);
				break;
			case DELETED:
			case MISSING:
				info.addRemovedFile(f);
				break;
			default:
				break;
			}
		}
		return info;
	}

	/**
	 * For every directory contained in files, we add the
	 * subdirectories if not already there
	 */
	private void expandDirectories(GramlabProject p,ArrayList<File> files) {
		ArrayList<File> removed=p.getRemovedFiles();
		for (int i=0;i<files.size();i++) {
			File f=files.get(i);
			if (!files.contains(f)) {
				files.add(f);
			}
			if (f.isDirectory()) {
				File[] list=f.listFiles();
				if (list==null) continue;
				for (File foo:list) {
					if (!files.contains(foo)) {
						files.add(foo);
					}
				}
				/* We also may have to add removed files */
				for (File r:removed) {
					if (r.getParentFile().equals(f)) {
						if (!files.contains(r)) {
							files.add(r);
						}
					}
				}
			}
		}
		Collections.sort(files);
	}

	/**
	 * We want the list of files concerned by a right click operation, that is:
	 * 1) the selected files that belongs to the given project if there is a
	 * selection or 2) the file that was clicked on
	 *
	 * If 'extension' is non null, we only keep files with this extension
	 */
	private ArrayList<File> getConcernedFiles(TreePath[] selectionPaths, GramlabProject project, String extension) {
		ArrayList<File> list = new ArrayList<File>();
		if (selectionPaths == null) {
			return list;
		}
		for (TreePath path : selectionPaths) {
			Object o = path.getLastPathComponent();
			if (!(o instanceof WorkspaceTreeNode))
				continue;
			WorkspaceTreeNode node = (WorkspaceTreeNode) o;
			if (FileUtil.hasExtension(node.getFile(), extension)
					&& null != project.getRelativeFileName(node.getFile())) {
				list.add(node.getFile());
			}
		}
		return list;
	}

	protected Action getSvnShareAction(final GramlabProject p) {
		if (!p.isOpen() || p.getSvnInfo(p.getSrcDirectory()) != null) {
			return null;
		}
		Action a = new AbstractAction("Share on SVN...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SvnShareDialog(p);
			}
		};
		return a;
	}

	protected Action getSvnUpdateAction(final GramlabProject p,ArrayList<File> files,final boolean toHead) {
		if (!p.isOpen() || p.getSvnInfo(p.getSrcDirectory()) == null) {
			return null;
		}
		final ArrayList<File> versioned=getVersionedFiles(files,p);
		Action a = new AbstractAction(toHead?"to head":"to revision...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SvnUpdateDialog(p,versioned,toHead);
			}
		};
		if (files!=null && versioned.size() == 0) {
			a.setEnabled(false);
		}
		return a;
	}

	protected Action getSvnCommitAction(final GramlabProject p) {
		if (!p.isOpen() || p.getSvnInfo(p.getSrcDirectory()) == null) {
			return null;
		}
		Action a = new AbstractAction("Commit") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SvnCommitDialog(p,null);
			}
		};
		return a;
	}

	protected Action getSvnCleanupAction(final GramlabProject p) {
		if (!p.isOpen() || p.getSvnInfo(p.getSrcDirectory()) == null) {
			return null;
		}
		Action a = new AbstractAction("Cleanup") {
			@Override
			public void actionPerformed(ActionEvent e) {
				SvnCommand cmd=new SvnCommand().cleanup(p.getProjectDirectory());
				SvnExecutor.exec(cmd,null);
				p.asyncUpdateSvnInfo(null,true);
			}
		};
		return a;
	}

	private JMenuBar createMenuBar() {
		JMenuBar bar = new JMenuBar();
		bar.add(createWorkspaceMenu());
		bar.add(createProjectMenu());
		bar.add(createDelaMenu());
		bar.add(createGraphsMenu());
		bar.add(createFileEditionMenu());
		bar.add(createHelpMenu());
		return bar;
	}

	private JMenu createWorkspaceMenu() {
		JMenu m = new JMenu("Workspace");
		Action n = new AbstractAction("New project") {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						new CreateProjectDialog();
					}
				});
			}
		};
		m.add(new JMenuItem(n));
		final Action delete = new AbstractAction("Delete current project") {
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.getAs(GramlabProjectManager.class)
					.deleteProject(null, true);
			}
		};
		delete.setEnabled(false);
		m.add(new JMenuItem(delete));
		final Action close = new AbstractAction("Close current project") {
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.getAs(GramlabProjectManager.class)
					.closeProject(null);
			}
		};
		close.setEnabled(false);
		m.add(new JMenuItem(close));
		final Action closeAll = new AbstractAction("Close all projects") {
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.getAs(GramlabProjectManager.class)
					.closeAllProjects();
			}
		};
		m.add(new JMenuItem(closeAll));
		GlobalProjectManager.getAs(GramlabProjectManager.class)
			.addProjectListener(new ProjectAdapter() {
			@Override
			public void currentProjectChanged(GramlabProject p, int pos) {
				delete.setEnabled(p != null);
				close.setEnabled(p != null);
				refreshProjectMenu(p);
			}
		});
		m.addSeparator();
		Action workspace = new AbstractAction("Change workspace") {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						new ChangeWorkspaceDialog();
						refreshProjectMenu(GlobalProjectManager.
		            getAs(GramlabProjectManager.class).getCurrentProject());
					}
				});
			}
		};
		m.add(workspace);
		return m;
	}

	private JMenu createProjectMenu() {
		JMenu m = new JMenu("Project");
		return m;
	}

	public void openGraph() {
		File dir;
		GramlabProject p = GlobalProjectManager.getAs(GramlabProjectManager.class)
				.getCurrentProject();
		if (p == null)
			dir = GramlabConfigManager.getWorkspaceDirectory();
		else
			dir = p.getProjectDirectory();
		if (ConfigManager.getManager().getCurrentGraphDirectory() != null) {
			dir = ConfigManager.getManager().getCurrentGraphDirectory();
		}
		final JFileChooser fc = new JFileChooser(dir);
		fc.setMultiSelectionEnabled(true);
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		final int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		final File[] graphs = fc.getSelectedFiles();
		GramlabProject proj=null;
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
			InternalFrameManager manager = GlobalProjectManager
					.search(graphs[i]).getFrameManagerAs(InternalFrameManager.class);
			if (manager == null) {
				JOptionPane.showMessageDialog(null,
						"You can not open a graph if no project is opened.",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			manager.newGraphFrame(graphs[i]);
			if (proj==null) {
				proj=GlobalProjectManager.getAs(GramlabProjectManager.class)
						.getProject(graphs[i]);
			}
		}
		if (proj!=null) {
			GlobalProjectManager.getAs(GramlabProjectManager.class)
				.setCurrentProject(proj);
		}
	}

	private JMenu createHelpMenu() {
		JMenu m = HelpMenuBuilder.build(ConfigManager.getManager()
				      .getApplicationDirectory());
    m.addSeparator();

		final JMenuItem about = new JMenuItem("About");
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File appDir = ConfigManager.getManager()
						.getApplicationDirectory();
				File disclaimersDir = new File(appDir.getPath()
						+ File.separatorChar + "disclaimers");
				File licensesDir = new File(appDir.getPath()
						+ File.separatorChar + "licenses");
				new AboutDialog(GramlabFrame.this,
												"GramLab",
												new ImageIcon(Icons.class.getResource("logo.png")),
												"Unitex-GramLab.txt",
												disclaimersDir,
												licensesDir);
			}
		});

		m.add(about);
    return m;
	}

	private JMenu createFileEditionMenu() {
		JMenu m = new JMenu("File Edition");
		Action n = new AbstractAction("New") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager m = GlobalProjectManager.search(null)
					.getFrameManagerAs(InternalFrameManager.class);
				if (m == null) {
					JOptionPane
							.showMessageDialog(
									null,
									"You cannot create a file if no project is opened.",
									"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				m.newFileEditionTextFrame(null);
			}
		};
		m.add(new JMenuItem(n));
		Action open = new AbstractAction("Open") {
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		};
		m.add(new JMenuItem(open));
		Action closeAll = new AbstractAction("Close all") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager m = GlobalProjectManager.search(null)
					.getFrameManagerAs(InternalFrameManager.class);
				if (m == null) {
					return;
				}
				m.closeAllFileEditionTextFrames();
			}
		};
		m.add(new JMenuItem(closeAll));
		return m;
	}

	public void openFile() {
		File dir;
		GramlabProject p = GlobalProjectManager.getAs(GramlabProjectManager.class)
				.getCurrentProject();
		if (p == null)
			dir = GramlabConfigManager.getWorkspaceDirectory();
		else
			dir = p.getProjectDirectory();
		final JFileChooser fc = new JFileChooser(dir);
		fc.setMultiSelectionEnabled(true);
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		final int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		final File[] files = fc.getSelectedFiles();
		for (int i = 0; i < files.length; i++) {
			if (!files[i].exists()) {
				if (!files[i].exists()) {
					JOptionPane.showMessageDialog(null, "File "
							+ files[i].getAbsolutePath() + " does not exist",
							"Error", JOptionPane.ERROR_MESSAGE);
					continue;
				}
			}
			InternalFrameManager manager = GlobalProjectManager
					.search(files[i]).getFrameManagerAs(InternalFrameManager.class);
			if (manager == null) {
				JOptionPane.showMessageDialog(null,
						"You can not open a file outside a project directory\n"
								+ "or if the project is opened.", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			manager.newFileEditionTextFrame(files[i]);
		}
	}

	void openDELA() {
		File dir;
		GramlabProject p = GlobalProjectManager.getAs(GramlabProjectManager.class)
				.getCurrentProject();
		if (p == null)
			dir = GramlabConfigManager.getWorkspaceDirectory();
		else
			dir = p.getProjectDirectory();
		final JFileChooser fc = new JFileChooser(dir);
		fc.addChoosableFileFilter(new PersonalFileFilter("dic",
				"Unicode DELA Dictionaries"));
		fc.setMultiSelectionEnabled(true);
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		final int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		GramlabProject proj=null;
		for (File f : fc.getSelectedFiles()) {
			InternalFrameManager m=GlobalProjectManager.search(f)
				.getFrameManagerAs(InternalFrameManager.class);
			if (m==null) {
				JOptionPane.showMessageDialog(null, "Dictionary "
						+ f.getAbsolutePath() + " does not belong\n"
						+"to any project of your workspace. Cannot open it if no project is open.",
						"Warning", JOptionPane.WARNING_MESSAGE);
			} else {
				m.newDelaFrame(f);
				if (proj==null) {
					proj=GlobalProjectManager.getAs(GramlabProjectManager.class).getProject(f);
				}
			}
		}
		if (proj!=null) {
			GlobalProjectManager.getAs(GramlabProjectManager.class).setCurrentProject(proj);
		}
	}

	/**
	 * Sorts the current dictionary. The external program "SortTxt" is called
	 * through the creation of a <code>ProcessInfoFrame</code> object.
	 */
	private void sortDELA() {
		InternalFrameManager manager=GlobalProjectManager.search(null)
			.getFrameManagerAs(InternalFrameManager.class);
		if (manager==null) {
			JOptionPane.showMessageDialog(null,
					"This operation is not possible if no project is open!", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		JInternalFrame frame = manager.getSelectedFrame();
		if (frame == null || !(frame instanceof DelaFrame))
			if (frame == null || !(frame instanceof DelaFrame)) {
				JOptionPane.showMessageDialog(null,
						"No dictionary is selected!", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		DelaFrame df = (DelaFrame) frame;
		File f = df.getKey();
		if (f == null)
			return;
		SortTxtCommand command = new SortTxtCommand().file(f);
		GramlabProject p = GlobalProjectManager.getAs(GramlabProjectManager.class)
				.getCurrentProject();
		if (p == null)
			return;
		if (p.getLanguage().equals("th")) {
			command = command.thai(true);
		} else {
			command = command.sortAlphabet(p.getSortAlphabet());
		}
		df.doDefaultCloseAction();
		Launcher.exec(command, true, new DelaDo(f));
	}

	class DelaDo implements ToDo {
		final File dela;

		public DelaDo(File s) {
			dela = s;
		}

		public void toDo(boolean success) {
			GlobalProjectManager.search(dela)
				.getFrameManagerAs(InternalFrameManager.class).newDelaFrame(dela);
		}
	}

	private JMenu createDelaMenu() {
		final JMenu delaMenu = new JMenu("Dictionaries");
		final JMenuItem open2 = new JMenuItem("Open...");
		open2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openDELA();
			}
		});
		delaMenu.add(open2);
		delaMenu.addSeparator();
		Action checkDelaFormat = new AbstractAction("Check Format...") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager=GlobalProjectManager.search(null)
					.getFrameManagerAs(InternalFrameManager.class);
				if (manager==null) {
					JOptionPane.showMessageDialog(null,
							"This operation is not possible if no project is open!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				JInternalFrame frame = manager.getSelectedFrame();
				if (frame == null || !(frame instanceof DelaFrame)) {
					JOptionPane.showMessageDialog(null,
							"No dictionary is selected!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				DelaFrame df = (DelaFrame) frame;
				File f = df.getKey();
				if (f == null) {
					JOptionPane.showMessageDialog(null,
							"No dictionary is selected!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				manager.newCheckDicFrame(f);
			}
		};
		checkDelaFormat.putValue(Action.ACCELERATOR_KEY, KeyStroke
				.getKeyStroke(KeyEvent.VK_K, Event.CTRL_MASK));
		delaMenu.add(new JMenuItem(checkDelaFormat));
		Action sortDictionary = new AbstractAction("Sort Dictionary") {
			public void actionPerformed(ActionEvent e) {
				sortDELA();
			}
		};
		delaMenu.add(new JMenuItem(sortDictionary));
		Action inflect = new AbstractAction("Inflect...") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager=GlobalProjectManager.search(null)
					.getFrameManagerAs(InternalFrameManager.class);
				if (manager==null) {
					JOptionPane.showMessageDialog(null,
							"This operation is not possible if no project is open!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				JInternalFrame frame = manager.getSelectedFrame();
				if (frame == null || !(frame instanceof DelaFrame)) {
					JOptionPane.showMessageDialog(null,
							"No dictionary is selected!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				DelaFrame df = (DelaFrame) frame;
				File f = df.getKey();
				if (f == null) {
					JOptionPane.showMessageDialog(null,
							"No dictionary is selected!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				manager.newInflectFrame(f);
			}
		};
		delaMenu.add(new JMenuItem(inflect));
		Action compressIntoFST = new AbstractAction("Compress into FST") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager=GlobalProjectManager.search(null)
					.getFrameManagerAs(InternalFrameManager.class);
				if (manager==null) {
					JOptionPane.showMessageDialog(null,
							"This operation is not possible if no project is open!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				JInternalFrame frame = manager.getSelectedFrame();
				if (frame == null || !(frame instanceof DelaFrame)) {
					JOptionPane.showMessageDialog(null,
							"No dictionary is selected!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				DelaFrame df = (DelaFrame) frame;
				File f = df.getKey();
				if (f == null) {
					JOptionPane.showMessageDialog(null,
							"No dictionary is selected!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				CompressCommand cmd = new CompressCommand().dic(f);
				if (GlobalProjectManager.getAs(GramlabProjectManager.class)
						.getProject(f).isSemitic()) {
					cmd = cmd.semitic();
				}
				Launcher.exec(cmd, false);
			}
		};
		delaMenu.add(new JMenuItem(compressIntoFST));
		delaMenu.addSeparator();
		Action closeDela = new AbstractAction("Close") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager=GlobalProjectManager.search(null)
					.getFrameManagerAs(InternalFrameManager.class);
				if (manager==null) {
					JOptionPane.showMessageDialog(null,
							"This operation is not possible if no project is open!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				JInternalFrame frame = manager.getSelectedFrame();
				if (frame == null || !(frame instanceof DelaFrame))
					return;
				frame.doDefaultCloseAction();
			}
		};
		delaMenu.add(new JMenuItem(closeDela));
		return delaMenu;
	}

	private JMenu createGraphsMenu() {
		JMenu m = new JMenu("Graphs");
		final Action n = new AbstractAction("New") {
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.newGraphFrame(null);
			}
		};
		m.add(new JMenuItem(n));
		final Action open = new AbstractAction("Open") {
			public void actionPerformed(ActionEvent e) {
				openGraph();
			}
		};
		open.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		m.add(new JMenuItem(open));
		final Action save = new AbstractAction("Save") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				GraphFrame f = manager.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.saveGraph();
					return;
				}
				/*
				 * Evil hack to allow save with ctrl+S in the internal text
				 * editor
				 */
				JInternalFrame frame = manager.getSelectedFrame();
				if (frame instanceof FileEditionTextFrame) {
					((FileEditionTextFrame) frame).saveFile();
					return;
				}
			}
		};
		save.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		m.add(new JMenuItem(save));
		final Action saveAs = new AbstractAction("Save as...") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				GraphFrame f = manager.getCurrentFocusedGraphFrame();
				if (f == null)
					return;
				f.saveAsGraph();
			}
		};
		m.add(new JMenuItem(saveAs));
		final Action saveAll = new AbstractAction("Save all") {
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.saveAllGraphFrames();
			}
		};
		m.add(new JMenuItem(saveAll));

		final JMenu exportMenu = GraphMenuBuilder.createExportMenu();
		m.add(exportMenu);

		m.addSeparator();
		final Action search = new AbstractAction("Find and replace") {
			public void actionPerformed(ActionEvent e) {
				ArrayList<GraphFrame> frames = GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).getGraphFrames();
        if(frames.isEmpty()) {
          return;
        }
        final FindAndReplaceDialog dialog = GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).newFindAndReplaceDialog();
        GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).addObserver(dialog);
			}
		};
		search.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK));
		m.add(new JMenuItem(search));
		final Action setup = new AbstractAction("Page Setup") {
			public void actionPerformed(ActionEvent e) {
				PrintManager.pageSetup();
			}
		};
		m.add(new JMenuItem(setup));
		final Action print = new AbstractAction("Print...") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				PrintManager.print(manager.getSelectedFrame());
			}
		};
		print.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke('P', Event.CTRL_MASK));
		m.add(new JMenuItem(print));
		final Action printAll = new AbstractAction("Print All...") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				PrintManager.printAllGraphs(manager.getGraphFrames());
			}
		};
		m.add(new JMenuItem(printAll));
		m.addSeparator();
		final Action undo = new AbstractAction("Undo") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				GraphFrame f = manager.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.undo();
					return;
				}
			}
		};
		undo.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK));
		m.add(new JMenuItem(undo));
		final Action redo = new AbstractAction("Redo") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				GraphFrame f = manager.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.redo();
					return;
				}
			}
		};
		redo.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK));
		m.add(new JMenuItem(redo));
		m.addSeparator();
		final Action seq2grf = new AbstractAction("Build sequence automaton") {
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getSeq2GrfFrame();
			}
		};
		m.add(new JMenuItem(seq2grf));
		m.addSeparator();
		final JMenu tools = new JMenu("Tools");
		final JMenuItem sortNodeLabel = new JMenuItem("Sort Node Label");
		sortNodeLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				final GraphFrame f = manager.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.sortNodeLabel();
				}
			}
		});
		final JMenuItem explorePaths = new JMenuItem("Explore graph paths");
		explorePaths.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				final GraphFrame f = manager.getCurrentFocusedGraphFrame();
				if (f != null) {
					GlobalProjectManager.search(null)
							.getFrameManagerAs(InternalFrameManager.class)
							.newGraphPathDialog();
				}
			}
		});
		final JMenuItem compileFST = new JMenuItem("Compile FST2");
		compileFST.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				final GraphFrame currentFrame = manager
						.getCurrentFocusedGraphFrame();
				if (currentFrame == null)
					return;
				currentFrame.compileGraph();
			}
		});
		final JMenuItem flatten = new JMenuItem("Compile & Flatten FST2");
		flatten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UnitexFrame.compileAndFlattenGraph();
			}
		});
		final JMenuItem graphCollection = new JMenuItem(
				"Build Graph Collection");
		graphCollection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.newGraphCollectionFrame();
			}
		});
		final JMenuItem svn = new JMenuItem("Look for SVN conflicts");
		svn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GramlabProject p = GlobalProjectManager.getAs(
						GramlabProjectManager.class).getCurrentProject();
				if (p == null)
					return;
				p.getSvnMonitor().monitor(false);
			}
		});
		tools.add(sortNodeLabel);
		tools.add(explorePaths);
		tools.addSeparator();
		tools.add(compileFST);
		tools.add(flatten);
		tools.addSeparator();
		tools.add(graphCollection);
		tools.addSeparator();
		tools.add(svn);
		m.add(tools);
		final JMenu format = new JMenu("Format");
		final JMenuItem alignment = new JMenuItem("Alignment...");
		alignment.setAccelerator(KeyStroke.getKeyStroke('M', Event.CTRL_MASK));
		alignment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				final GraphFrame f = manager.getCurrentFocusedGraphFrame();
				if (f != null) {
					manager.newGraphAlignmentDialog(f);
				}
			}
		});
		final JMenuItem antialiasing = new JMenuItem("Antialiasing...");
		antialiasing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				final JInternalFrame f = manager.getSelectedFrame();
				if (f == null)
					return;
				if (f instanceof GraphFrame) {
					final GraphFrame f2 = (GraphFrame) f;
					f2.changeAntialiasingValue();
					return;
				}
				if (f instanceof TextAutomatonFrame) {
					final TextAutomatonFrame f2 = (TextAutomatonFrame) f;
					f2.changeAntialiasingValue();
				}
			}
		});
		final JMenuItem presentation = new JMenuItem("Presentation...");
		presentation.setAccelerator(KeyStroke
				.getKeyStroke('R', Event.CTRL_MASK));
		presentation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				final GraphFrame f = manager.getCurrentFocusedGraphFrame();
				if (f != null) {
					final GraphPresentationInfo info = manager
							.newGraphPresentationDialog(
									f.getGraphPresentationInfo(), true);
					if (info != null) {
						f.setGraphPresentationInfo(info);
					}
				}
			}
		});
		final JMenuItem graphSize = new JMenuItem("Graph Size...");
		graphSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				final GraphFrame f = manager.getCurrentFocusedGraphFrame();
				if (f != null) {
					manager.newGraphSizeDialog(f);
				}
			}
		});
		format.add(antialiasing);
		format.addSeparator();
		format.add(alignment);
		format.add(presentation);
		format.add(graphSize);
		final JMenu zoom = new JMenu("Zoom");
		final ButtonGroup groupe = new ButtonGroup();
		final JRadioButtonMenuItem fitInScreen = new JRadioButtonMenuItem(
				"Fit in screen");
		final JRadioButtonMenuItem fitInWindow = new JRadioButtonMenuItem(
				"Fit in window");
		final JRadioButtonMenuItem fit60 = new JRadioButtonMenuItem("60%");
		final JRadioButtonMenuItem fit80 = new JRadioButtonMenuItem("80%");
		final JRadioButtonMenuItem fit100 = new JRadioButtonMenuItem("100%");
		final JRadioButtonMenuItem fit120 = new JRadioButtonMenuItem("120%");
		final JRadioButtonMenuItem fit140 = new JRadioButtonMenuItem("140%");
		groupe.add(fitInScreen);
		groupe.add(fitInWindow);
		groupe.add(fit60);
		groupe.add(fit80);
		groupe.add(fit100);
		fit100.setSelected(true);
		groupe.add(fit120);
		groupe.add(fit140);
		fitInScreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				final GraphFrame f = manager.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					Dimension screenSize = Toolkit.getDefaultToolkit()
							.getScreenSize();
					final double scale_x = screenSize.width
							/ (double) f.getGraphicalZone().getWidth();
					final double scale_y = screenSize.height
							/ (double) f.getGraphicalZone().getHeight();
					if (scale_x < scale_y)
						f.setScaleFactor(scale_x);
					else
						f.setScaleFactor(scale_y);
				}
			}
		});
		fitInWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				final GraphFrame f = manager.getCurrentFocusedGraphFrame();
				if (f != null) {
					final Dimension d = f.getScroll().getSize();
					final double scale_x = (d.width - 3)
							/ (double) f.getGraphicalZone().getWidth();
					final double scale_y = (d.height - 3)
							/ (double) f.getGraphicalZone().getHeight();
					if (scale_x < scale_y)
						f.setScaleFactor(scale_x);
					else
						f.setScaleFactor(scale_y);
					f.compListener = new ComponentAdapter() {
						@Override
						public void componentResized(ComponentEvent e2) {
							final Dimension d2 = f.getScroll().getSize();
							final double scale_x2 = (d2.width - 3)
									/ (double) f.getGraphicalZone().getWidth();
							final double scale_y2 = (d2.height - 3)
									/ (double) f.getGraphicalZone().getHeight();
							if (scale_x2 < scale_y2)
								f.setScaleFactor(scale_x2);
							else
								f.setScaleFactor(scale_y2);
						}
					};
					f.addComponentListener(f.compListener);
				}
			}
		});
		fit60.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				final GraphFrame f = manager.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(0.6);
				}
			}
		});
		fit80.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				final GraphFrame f = manager.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(0.8);
				}
			}
		});
		fit100.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				final GraphFrame f = manager.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(1.0);
				}
			}
		});
		fit120.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				final GraphFrame f = manager.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(1.2);
				}
			}
		});
		fit140.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager manager = GlobalProjectManager
						.search(null).getFrameManagerAs(
								InternalFrameManager.class);
				final GraphFrame f = manager.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(1.4);
				}
			}
		});
		zoom.add(fitInScreen);
		zoom.add(fitInWindow);
		zoom.add(fit60);
		zoom.add(fit80);
		zoom.add(fit100);
		zoom.add(fit120);
		zoom.add(fit140);
		m.add(tools);
		m.add(format);
		m.add(zoom);
		m.addSeparator();
		final Action closeAll = new AbstractAction("Close all") {
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.closeAllGraphFrames();
			}
		};
		m.add(new JMenuItem(closeAll));

		m.addMenuListener(new MenuAdapter() {
			@Override
			public void menuSelected(MenuEvent e) {
				boolean existsFocusedGrFrame = false;
				boolean existsAnyGrFrame = false;
				boolean existsManager = false;

				if (GlobalProjectManager.getAs(GramlabProjectManager.class)
						.getCurrentProject() != null) {
					InternalFrameManager manager = GlobalProjectManager.search(
							null).getFrameManagerAs(InternalFrameManager.class);

					if (manager != null) {
						existsManager = true;
						existsFocusedGrFrame = manager
								.getCurrentFocusedGraphFrame() != null;
						existsAnyGrFrame = manager.getGraphFrames().size() != 0;
					}
				}

				n.setEnabled(existsManager);
				open.setEnabled(existsManager);
				save.setEnabled(existsFocusedGrFrame);
				saveAs.setEnabled(existsFocusedGrFrame);
				saveAll.setEnabled(existsAnyGrFrame);
				exportMenu.setEnabled(existsFocusedGrFrame);
				search.setEnabled(existsFocusedGrFrame);
				setup.setEnabled(existsAnyGrFrame);
				print.setEnabled(existsFocusedGrFrame);
				printAll.setEnabled(existsAnyGrFrame);
				undo.setEnabled(existsFocusedGrFrame);
				redo.setEnabled(existsFocusedGrFrame);
				seq2grf.setEnabled(existsManager);
				tools.setEnabled(existsFocusedGrFrame);
				format.setEnabled(existsFocusedGrFrame);
				zoom.setEnabled(existsFocusedGrFrame);
				closeAll.setEnabled(existsManager);
			}
		});

		return m;
	}
}
