package org.gramlab.core.gramlab.project.config.maven;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.gramlab.core.Main;
import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.gramlab.project.config.ProjectVersionableConfig;
import org.gramlab.core.umlv.unitex.LinkButton;
import org.gramlab.core.umlv.unitex.config.NamedRepository;
import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.frames.FrameUtil;
import org.gramlab.core.umlv.unitex.process.ExecParameters;
import org.gramlab.core.umlv.unitex.process.Executor;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.process.commands.MultiCommands;
import org.gramlab.core.umlv.unitex.process.list.ProcessOutputList;
import org.gramlab.core.umlv.unitex.process.list.ProcessOutputListModel;

@SuppressWarnings("serial")
public class MavenDialog extends JDialog {
	
	JPanel mainPanel;
	Executor executor=null;
	JButton ok,cancel;
	GramlabProject project;
	ProcessOutputList stdout=new ProcessOutputList(new ProcessOutputListModel());
	MavenFileTableModel tableModel;
	MavenTreeModel treeModel;
	JTree tree;
	JCheckBox selectGrfs=new JCheckBox("Select grf files",true);
	JCheckBox selectDics=new JCheckBox("Select dic files",true);
	JTabbedPane tabbed=new JTabbedPane();
	JCheckBox includeSource;
	JCheckBox includeBuild;
	JPanel sourcePanel;
	JPanel buildPanel;
	LinkButton b1;
	LinkButton b2;
	JRadioButton justBuild;
	JRadioButton buildAndInstall;
	JRadioButton buildAndDeploy;
	DefaultListModel graphs;
	DefaultListModel dics;
	JTextField group;
	JTextField artifact;
	JTextField version;
	
	JTextField idRelease,urlRelease,idSnapshot,urlSnapshot;
	
	private boolean finished=false;
	

	public MavenDialog(GramlabProject p) {
		super(Main.getMainFrame(), "Maven packaging of "+p.getName(), true);
		tableModel=new MavenFileTableModel(p);
		this.project=p;
		tabbed.addTab("Build package",createMainPanel());
		tabbed.addTab("Select source files",sourcePanel=createSourcePanel());
		tabbed.addTab("Components to build",buildPanel=createBuildPanel());
		setContentPane(tabbed);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		FrameUtil.center(getOwner(),this);
		setVisible(true);
	}

	
	private void addText(JPanel panel, String... lines) {
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.weightx=1;
		gbc.anchor=GridBagConstraints.WEST;
		for (String s:lines) {
			panel.add(new JLabel(s),gbc);
		}
		panel.revalidate();
		panel.repaint();
	}

	protected void exportAsMavenComponent(final JPanel p) {
		if (!includeSource.isSelected() && !includeBuild.isSelected()) {
			JOptionPane
			.showMessageDialog(
				null,
				"You cannot create a maven component with no source files and no built files.",
				"Error", JOptionPane.ERROR_MESSAGE);					
			return;
		}
		Artifact a=Artifact.checkedArtifactCreation(group.getText(),artifact.getText(),version.getText(),project.getName());
		if (a==null) {
			return;
		}
		try {
			if (includeSource.isSelected()) {
				/* Repository information is only useful when we include source files */
				if (!saveRepositories()) {
					unlock(false);
					return;
				}
			}
			if (!saveDistributionManagement(project.getPom())) {
				unlock(false);
				return;
			}
			project.setMvnSourceConfig(tableModel.getMvnSourceConfig(selectGrfs.isSelected(),selectDics.isSelected()));
			project.setMvnBuildConfig(getMvnBuildConfig());
			project.getPom().setArtifact(a);
			project.setMvnBuildPackage(includeBuild.isSelected());
			project.setMvnSourcePackage(includeSource.isSelected());	
			PackageOperation op;
			if (justBuild.isSelected()) {
				op=PackageOperation.JUST_BUILD;
			} else if (buildAndInstall.isSelected()) {
				op=PackageOperation.BUILD_AND_INSTALL;
			} else if (buildAndDeploy.isSelected()) {
				op=PackageOperation.BUILD_AND_DEPLOY;
				if (project.getPom().getIdRepoRelease()==null) {
					JOptionPane
						.showMessageDialog(
							null,
							"You cannot deploy a project if the distribution management information is not set.",
							"Distribution management error", JOptionPane.ERROR_MESSAGE);					
					unlock(false);
					return;
				}
			} else {
				throw new IllegalStateException("Should not happen");
			}
			project.setPackageOperation(op);
			project.saveConfigurationFiles(true);
			MultiCommands c;
			if (justBuild.isSelected()) {
				c=project.getPom().getPackageCommand();
			} else if (buildAndInstall.isSelected()) {
				c=project.getPom().getInstallCommand();
			} else if (buildAndDeploy.isSelected()) {
				c=project.getPom().getDeployCommand();
			} else {
				throw new IllegalStateException("Should not happen");
			}
			ToDo myDO=new ToDo() {
				@Override
				public void toDo(boolean success) {
					executor=null;
					unlock(success);
					if (success) {
						addText(p," ","Operation successfully achieved.",
								       " ");
					} else if (!MvnCommand.mvnInstalled()) {
						addText(p," ","Maven is not installed. You must install it before",
								       "try to package your project.",
								       " ");
					} else {
						addText(p," ","Operation failed. Read the maven error message",
								      "to know the exact cause of failure.",
								       " ");
					}
				}

			};
			ExecParameters parameters=new ExecParameters(true,
					c, stdout, stdout, myDO, false);
			executor=new Executor(parameters);
			executor.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean saveDistributionManagement(Pom pom) {
		int n=0;
		if (!idRelease.getText().equals("")) n++;
		if (!urlRelease.getText().equals("")) n++;
		if (!idSnapshot.getText().equals("")) n++;
		if (!urlSnapshot.getText().equals("")) n++;
		if (n==0) {
			pom.setDistributionManagement(null, null, null, null);
			return true;
		}
		if (n==4) {
			return pom.setDistributionManagement(idRelease.getText(), urlRelease.getText(), 
					idSnapshot.getText(), urlSnapshot.getText());
		}
		JOptionPane
			.showMessageDialog(
				null,
				"You must either fill all four fields or leave them all empty.",
				"Distribution management error", JOptionPane.ERROR_MESSAGE);
		return false;
	}


	/**
	 * This method translates repositories that are in the src dir into ones into
	 * the dep/artifactId-version/project.name/ one, so that they
	 * can be used when the project package is used as a dependency.
	 * However, as all other information may have to stay private to the
	 * project developer, we do not copy the other information for the
	 * project.versionable_config file. This is why we use a blank
	 * ProjectVersionableConfig object, only setting the repository information.
	 */
	private boolean saveRepositories() {
		File defaultRepo=null;
		if (project.getDefaultGraphRepository()!=null) {
			defaultRepo=translateToDepIfNeeded(project.getDefaultGraphRepository());
			if (defaultRepo==null) return false;
		}
		ArrayList<NamedRepository> list=new ArrayList<NamedRepository>();
		for (NamedRepository n:project.getNamedRepositories()) {
			File tmp=translateToDepIfNeeded(n.getFile());
			if (tmp==null) {
				return false;
			}
			list.add(new NamedRepository(n.getName(),tmp));
		}
		ProjectVersionableConfig cfg=new ProjectVersionableConfig(project);
		cfg.setDefaultGraphRepository(defaultRepo);
		cfg.setNamedRepositories(list);
		/* And we save that information to ..repositories */
		File f=new File(project.getProjectDirectory(),"..repositories");
		FileOutputStream stream;
		try {
		try {
			stream = new FileOutputStream(f);
			OutputStreamWriter writer=new OutputStreamWriter(stream,"UTF-8");
			cfg.save(writer);
			/* The javadoc says that flush should be made implicitly made
			 * by the close, but it seems that it is not always true...
			 */
			writer.flush();
			writer.close();
			stream.close();
		} catch (FileNotFoundException e) {
			throw new IOException(e);
		} catch (UnsupportedEncodingException e) {
			throw new IOException(e);
		}
		} catch (IOException e) {
			JOptionPane
			.showMessageDialog(
					null,
					"Cannot save the repository file "+f.getAbsolutePath(),
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}


	private File translateToDepIfNeeded(File dir) {
		File depDir=new File(project.getProjectDirectory(),PomIO.DEPENDENCY_DIRECTORY);
		String ancestor=FileUtil.isAncestor(depDir,dir);
		if (ancestor!=null) {
			/* The directory is already in dep, nothing to do */
			return dir;
		}
		String s=inSrcDirectory(project,dir);
		if (s==null) {
			JOptionPane
			.showMessageDialog(
					null,
					"The repository "+dir.getAbsolutePath()+"\n"
					+"is neither in 'dep' nor in 'src'. It would be ignored and\n"
					+"some graphs in your maven package would not compile because\n"
					+"of that. You must fix that before packaging your project.\n",
					"Repository error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		File foo=new File(new File(depDir,project.getName()),s);
		return foo;
	}


	private MvnBuildConfig getMvnBuildConfig() {
		ArrayList<GrfToCompile> g=new ArrayList<GrfToCompile>();
		for (int i=0;i<graphs.size();i++) {
			g.add((GrfToCompile)graphs.get(i));
		}
		ArrayList<BinToBuild> b=new ArrayList<BinToBuild>();
		for (int i=0;i<dics.size();i++) {
			b.add((BinToBuild)dics.get(i));
		}
		return new MvnBuildConfig(g,b);
	}


	private static void addLine(JPanel p,GridBagConstraints gbc,String string,JComponent jtf) {
		gbc.gridwidth=1;
		gbc.anchor=GridBagConstraints.WEST;
		p.add(new JLabel(string),gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.weightx=1;
		p.add(jtf,gbc);
		gbc.weightx=0;
	}

	
	private JPanel createMainPanel() {
		JPanel p=new JPanel(new GridBagLayout());
		stdout.empty();
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.weighty=0;
		gbc.gridwidth=GridBagConstraints.REMAINDER;

		JPanel id=new JPanel(new GridBagLayout());
		id.setBorder(BorderFactory.createTitledBorder("Package ID"));
		GridBagConstraints gbc0=new GridBagConstraints();
		gbc0.anchor=GridBagConstraints.WEST;
		gbc0.fill=GridBagConstraints.BOTH;
		gbc0.gridwidth=GridBagConstraints.REMAINDER;
		id.add(new JLabel("To create a maven package to have to give it a groupId, an artifactId"),gbc0);
		id.add(new JLabel("and a versionId:"),gbc0);
		id.add(new JLabel(" "),gbc0);
		Artifact a=project.getPom().getArtifact();
		group=new JTextField(a.getGroupId());
		artifact=new JTextField(a.getArtifactId());
		version=new JTextField(a.getVersion());
		addLine(id,gbc0," groupId: ",group);
		addLine(id,gbc0," artifactId: ",artifact);
		addLine(id,gbc0," version: ",version);
		p.add(id,gbc);
		
		JPanel content=new JPanel(new GridBagLayout());
		content.setBorder(BorderFactory.createTitledBorder("Package content"));
		GridBagConstraints gbc2=new GridBagConstraints();
		gbc2.anchor=GridBagConstraints.WEST;
		gbc2.fill=GridBagConstraints.BOTH;
		gbc2.weightx=1;
		gbc2.weighty=0;
		gbc2.gridwidth=GridBagConstraints.REMAINDER;
		content.add(new JLabel("A maven package can contain source components (files"),gbc2);
		content.add(new JLabel("to be copied) and/or built components (compiled graphs and"),gbc2);
		content.add(new JLabel("compressed dictionaries). Select what you want to include:"),gbc2);
		content.add(new JLabel(" "),gbc2);
		includeSource=new JCheckBox("include",project.isMvnSourcePackage());
		includeSource.setFocusPainted(false);
		gbc2.gridwidth=1;
		gbc2.anchor=GridBagConstraints.WEST;
		gbc2.weightx=0;
		content.add(includeSource,gbc2);
		gbc2.gridwidth=GridBagConstraints.REMAINDER;
		gbc2.weightx=1;
		b1=new LinkButton("source files");
		b1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				tabbed.setSelectedComponent(sourcePanel);
			}
		});
		content.add(b1,gbc2);
		includeBuild=new JCheckBox("include",project.isMvnBuildPackage());
		includeBuild.setFocusPainted(false);
		gbc2.gridwidth=1;
		gbc2.anchor=GridBagConstraints.WEST;
		gbc2.weightx=0;
		content.add(includeBuild,gbc2);
		gbc2.gridwidth=GridBagConstraints.REMAINDER;
		gbc2.weightx=1;
		b2=new LinkButton("components to be built");
		b2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				tabbed.setSelectedComponent(buildPanel);
			}
		});
		content.add(b2,gbc2);
		p.add(content,gbc);

		JPanel ops=new JPanel(new GridBagLayout());
		ops.setBorder(BorderFactory.createTitledBorder("Maven task"));
		GridBagConstraints gbc3=new GridBagConstraints();
		gbc3.anchor=GridBagConstraints.WEST;
		gbc3.fill=GridBagConstraints.BOTH;
		gbc3.weightx=1;
		gbc3.weighty=0;
		gbc3.gridwidth=GridBagConstraints.REMAINDER;
		ButtonGroup bg=new ButtonGroup();
		justBuild=new JRadioButton("Just build the package",PackageOperation.JUST_BUILD==project.getPackageOperation());
		buildAndInstall=new JRadioButton("Build and install into your local maven repository",PackageOperation.BUILD_AND_INSTALL==project.getPackageOperation());
		buildAndDeploy=new JRadioButton("Build and deploy to your remote maven repository",PackageOperation.BUILD_AND_DEPLOY==project.getPackageOperation());
		ops.add(justBuild,gbc3);
		ops.add(buildAndInstall,gbc3);
		ops.add(buildAndDeploy,gbc3);
		bg.add(justBuild);
		bg.add(buildAndInstall);
		bg.add(buildAndDeploy);
		p.add(ops,gbc);

		JPanel dm=new JPanel(new GridBagLayout());
		dm.setBorder(BorderFactory.createTitledBorder("Distribution management"));
		GridBagConstraints gbc4=new GridBagConstraints();
		Pom pom=project.getPom();
		idRelease=new JTextField();
		urlRelease=new JTextField();
		idSnapshot=new JTextField();
		urlSnapshot=new JTextField();
		if (pom.getIdRepoRelease()!=null) {
			idRelease.setText(pom.getIdRepoRelease());
			urlRelease.setText(pom.getUrlRepoRelease());
			idSnapshot.setText(pom.getIdRepoSnapshot());
			urlSnapshot.setText(pom.getUrlRepoSnapshot());
		}
		gbc4.anchor=GridBagConstraints.WEST;
		gbc4.fill=GridBagConstraints.BOTH;
		gbc4.weighty=0;
		dm.add(new JLabel("Release repo. ID:"),gbc4);
		gbc4.weightx=0.5;
		dm.add(idRelease,gbc4);
		gbc4.weightx=0;
		dm.add(new JLabel(" URL:"),gbc4);
		gbc4.weightx=1;
		gbc4.gridwidth=GridBagConstraints.REMAINDER;
		dm.add(urlRelease,gbc4);
		gbc4.weightx=0;
		gbc4.gridwidth=1;
		dm.add(new JLabel("Snapshot repo. ID:"),gbc4);
		gbc4.weightx=0.5;
		dm.add(idSnapshot,gbc4);
		gbc4.weightx=0;
		dm.add(new JLabel(" URL:"),gbc4);
		gbc4.weightx=1;
		gbc4.gridwidth=GridBagConstraints.REMAINDER;
		dm.add(urlSnapshot,gbc4);
		
		p.add(dm,gbc);

		JScrollPane scroll2=new JScrollPane(stdout);
		scroll2.setPreferredSize(new Dimension(100,100));
		gbc.weighty=1;
		gbc.fill=GridBagConstraints.BOTH;
		p.add(scroll2,gbc);
		final JPanel p2=new JPanel(new GridBagLayout());
		gbc.weighty=0;
		p.add(p2,gbc);

		JPanel pane=new JPanel(new BorderLayout());
		pane.add(p,BorderLayout.CENTER);
		JPanel down=new JPanel();
		cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (executor!=null) {
					executor.interrupt();
				}
				setVisible(false);
				dispose();
			}
		});
		ok=new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (finished==true) {
					setVisible(false);
					dispose();
					return;
				}
				lock();
				p2.removeAll();
				p2.revalidate();
				p2.repaint();
				exportAsMavenComponent(p2);
			}
		});
		down.add(cancel);
		down.add(ok);
		pane.add(down,BorderLayout.SOUTH);
		return pane;
	}
	
	
	
	private JPanel createSourcePanel() {
		JPanel p=new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=0;
		gbc.weighty=0;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.anchor=GridBagConstraints.WEST;
		p.add(new JLabel("Select the files you want to copy into the maven component:"),gbc);
		p.add(new JLabel(" "),gbc);
		
		gbc.gridwidth=1;
		selectGrfs.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				tableModel.selectFiles("grf",selectGrfs.isSelected());
			}
		});
		selectDics.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				tableModel.selectFiles("dic",selectDics.isSelected());
			}
		});
		p.add(selectGrfs,gbc);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(selectDics,gbc);
		p.add(new JLabel(" "),gbc);
		gbc.weighty=2;
		treeModel=new MavenTreeModel(project,tableModel);
		tree=new JTree(treeModel);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new MvnSelectionTreeCellEditor(tableModel));
		tree.setEditable(true);
		tree.setCellEditor(new MvnSelectionTreeCellEditor(tableModel));
		tableModel.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				tree.revalidate();
				tree.repaint();
			}
		});
		p.add(new JScrollPane(tree),gbc);
		return p;
	}

	
	private JPanel createBuildPanel() {
		JPanel p=new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.weighty=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(createFst2Panel(),gbc);
		p.add(createDicsPanel(),gbc);
		return p;
	}
	

	private JPanel createFst2Panel() {
		JPanel p=new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createTitledBorder("Graphs"));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.weighty=0;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(new JLabel("Select the graphs you want to compile. Double-click"),gbc);
		p.add(new JLabel("on a line to edit it."),gbc);
		p.add(new JLabel(" "),gbc);
		JButton addGraphs=new JButton("Add graph(s)");
		final JButton removeGraphs=new JButton("Remove selected items");
		removeGraphs.setEnabled(false);
		gbc.gridwidth=1;
		gbc.weightx=0;
		addGraphs.addActionListener(new ActionListener() {
			
			private File getProbableDirectory() {
				if (graphs.size()!=0) {
					File f=((GrfToCompile) graphs.get(0)).getGrf();
					return f.getParentFile();
				}
				return project.getGraphsDirectory();
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc=new JFileChooser(getProbableDirectory());
				jfc.setMultiSelectionEnabled(true);
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						".grf","grf");
				jfc.setFileFilter(filter);
				int returnVal = jfc.showOpenDialog(MavenDialog.this);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				File[] files=jfc.getSelectedFiles();
				if (files==null) {
					return;
				}
				String srcRelativeName;
				for (File f:files) {
					if (null==(srcRelativeName=inSrcDirectory(project,f))) {
						JOptionPane.showMessageDialog(null,
								"Error with graph "+f.getAbsolutePath()+":\n\n"+
								"You cannot select a graph outside the src directory of your project!",
								"Error", JOptionPane.ERROR_MESSAGE);
					} else {
						String fst2=FileUtil.getFileNameWithoutExtension(srcRelativeName)+".fst2";
						graphs.addElement(new GrfToCompile(f,new File(fst2).getName()));
					}
				}
			}
		});
		p.add(addGraphs,gbc);
		p.add(removeGraphs,gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.weightx=1;
		p.add(new JLabel(" "),gbc);
		graphs=new DefaultListModel();
		final JList list=new JList(graphs);
		removeGraphs.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] indices=list.getSelectedIndices();
				if (indices==null) return;
				for (int i=indices.length-1;i>=0;i--) {
					graphs.remove(indices[i]);
				}
			}
		});
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index=list.locationToIndex(e.getPoint());
				if (e.getClickCount()==2) {
					if (index==-1) return;
					new EditFst2Dialog(project,graphs,index);
					return;
				}
			}
		});
		for (GrfToCompile g:project.getMvnBuildConfig().getGrfToCompile()) {
			graphs.addElement(g);
		}
		gbc.weighty=1;
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				removeGraphs.setEnabled(list.getSelectedIndex()!=-1);
			}
		});
		JScrollPane scroll=new JScrollPane(list);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		p.add(scroll,gbc);
		scroll.setSize(new Dimension(200,200));
		scroll.setPreferredSize(new Dimension(200,200));
		list.setCellRenderer(new GraphCellListRenderer(scroll,project));
		return p;
	}


	
	
	private JPanel createDicsPanel() {
		JPanel p=new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createTitledBorder("Dictionaries"));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.weighty=0;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(new JLabel("Select the dictionaries and/or dictionary groups you want to\n"),gbc);
		p.add(new JLabel("compress. Double-click on a line to edit it."),gbc);
		p.add(new JLabel(" "),gbc);
		JButton addDictionary=new JButton("Add single dictionaries");
		JButton addDictionaryGroup=new JButton("Add dictionary groups");
		final JButton removeItems=new JButton("Remove selected items");
		removeItems.setEnabled(false);
		gbc.gridwidth=1;
		gbc.weightx=0;
		addDictionary.addActionListener(new ActionListener() {
			
			private File getProbableDirectory() {
				if (dics.size()!=0) {
					File f=((BinToBuild) dics.get(0)).getDics().get(0);
					return f.getParentFile();
				}
				return project.getDelaDirectory();
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc=new JFileChooser(getProbableDirectory());
				jfc.setMultiSelectionEnabled(true);
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						".dic","dic");
				jfc.setFileFilter(filter);
				int returnVal = jfc.showOpenDialog(MavenDialog.this);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				File[] files=jfc.getSelectedFiles();
				if (files==null) {
					return;
				}
				String srcRelativeName;
				for (File f:files) {
					if (null==(srcRelativeName=inSrcDirectory(project,f))) {
						JOptionPane.showMessageDialog(null,
								"Error with dictionary "+f.getAbsolutePath()+":\n\n"+
								"You cannot select a dictionary outside the src directory of your project!",
								"Error", JOptionPane.ERROR_MESSAGE);
					} else {
						String bin=FileUtil.getFileNameWithoutExtension(srcRelativeName)+".bin";
						dics.addElement(new BinToBuild(new File(bin).getName(),f));
					}
				}
			}
		});
		p.add(addDictionary,gbc);
		addDictionaryGroup.addActionListener(new ActionListener() {
			
			private File getProbableDirectory() {
				if (dics.size()!=0) {
					File f=((BinToBuild) dics.get(0)).getDics().get(0);
					return f.getParentFile();
				}
				return project.getDelaDirectory();
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc=new JFileChooser(getProbableDirectory());
				jfc.setMultiSelectionEnabled(true);
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						".dic","dic");
				jfc.setFileFilter(filter);
				int returnVal = jfc.showOpenDialog(MavenDialog.this);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				File[] files=jfc.getSelectedFiles();
				if (files==null) {
					return;
				}
				String srcRelativeName;
				ArrayList<File> dicsToAdd=new ArrayList<File>();
				for (File f:files) {
					if (null==(srcRelativeName=inSrcDirectory(project,f))) {
						JOptionPane.showMessageDialog(null,
								"Error with dictionary "+f.getAbsolutePath()+":\n\n"+
								"You cannot select a dictionary outside the src directory of your project!",
								"Error", JOptionPane.ERROR_MESSAGE);
					} else {
						dicsToAdd.add(f);
					}
				}
				if (dicsToAdd.size()==0) return;
				srcRelativeName=inSrcDirectory(project,dicsToAdd.get(0));
				String bin=FileUtil.getFileNameWithoutExtension(srcRelativeName)+".bin";
				dics.addElement(new BinToBuild(new File(bin).getName(),dicsToAdd));
			}
		});
		p.add(addDictionaryGroup,gbc);
		p.add(removeItems,gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.weightx=1;
		p.add(new JLabel(" "),gbc);
		dics=new DefaultListModel();
		final JList list=new JList(dics);
		removeItems.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] indices=list.getSelectedIndices();
				if (indices==null) return;
				for (int i=indices.length-1;i>=0;i--) {
					dics.remove(indices[i]);
				}
			}
		});
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index=list.locationToIndex(e.getPoint());
				if (e.getClickCount()==2) {
					if (index==-1) return;
					new EditDicsDialog(project,dics,index);
					return;
				}
			}
		});
		for (BinToBuild g:project.getMvnBuildConfig().getBinToBuild()) {
			dics.addElement(g);
		}
		gbc.weighty=1;
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				removeItems.setEnabled(list.getSelectedIndex()!=-1);
			}
		});
		JScrollPane scroll=new JScrollPane(list);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		p.add(scroll,gbc);
		scroll.setSize(new Dimension(200,200));
		scroll.setPreferredSize(new Dimension(200,200));
		list.setCellRenderer(new DicCellListRenderer(scroll,project));
		return p;
	}

	
	
	public static String inSrcDirectory(GramlabProject p,File f) {
		String foo=FileUtil.isAncestor(p.getSrcDirectory(),f.getParentFile());
		if (foo==null) return null;
		return foo+f.getName();
	}


	void lock() {
		ok.setEnabled(false);
		tree.setEnabled(false);
		cancel.setEnabled(false);
		selectGrfs.setEnabled(false);
		selectDics.setEnabled(false);
		includeSource.setEnabled(false);
		includeBuild.setEnabled(false);
		tabbed.setEnabled(false);
		b1.setEnabled(false);
		b2.setEnabled(false);
		justBuild.setEnabled(false);
		buildAndInstall.setEnabled(false);
		buildAndDeploy.setEnabled(false);
		group.setEnabled(false);
		artifact.setEnabled(false);
		version.setEnabled(false);
	}
	
	void unlock(boolean success) {
		ok.setEnabled(true);
		tabbed.setEnabled(true);
		if (success) {
			ok.setText("Hide");
		} else {
			tree.setEnabled(true);
			cancel.setEnabled(true);
			selectGrfs.setEnabled(true);
			selectDics.setEnabled(true);
			includeSource.setEnabled(true);
			includeBuild.setEnabled(true);
			b1.setEnabled(true);
			b2.setEnabled(true);
			justBuild.setEnabled(true);
			buildAndInstall.setEnabled(true);
			buildAndDeploy.setEnabled(true);
			group.setEnabled(true);
			artifact.setEnabled(true);
			version.setEnabled(true);
		}
		finished=success;
	}
	
}
