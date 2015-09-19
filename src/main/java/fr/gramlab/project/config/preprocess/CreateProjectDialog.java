package fr.gramlab.project.config.preprocess;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.gramlab.GramlabConfigManager;
import fr.gramlab.Main;
import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.GramlabProjectManager;
import fr.gramlab.project.Language;
import fr.gramlab.project.config.maven.Artifact;
import fr.gramlab.svn.SvnCheckoutDialog;
import fr.gramlab.util.KeyUtil;
import fr.umlv.unitex.LinkButton;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.frames.FrameUtil;
import fr.umlv.unitex.io.Encoding;

@SuppressWarnings("serial")
public class CreateProjectDialog extends JDialog {
	
	public CreateProjectDialog() {
		super(Main.getMainFrame(), "New project", true);
		setContentPane(constructPanel());
		pack();
		FrameUtil.center(getOwner(),this);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private JPanel constructPanel() {
		JPanel p=new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		p.add(new JLabel("How do you want to create your new Gramlab project ?"),BorderLayout.NORTH);
		JPanel center=new JPanel(new GridLayout(7,1));
		center.add(new JLabel(""));
		final JRadioButton fromScratch=new JRadioButton("Create an empty project",true);
		final JRadioButton createFromUnitexSystem=new JRadioButton("Create a project from a Unitex system directory",false);
		final JRadioButton createFromUnitexUser=new JRadioButton("Create a project from a Unitex user directory",false);
		final JRadioButton clone=new JRadioButton("Clone a project",false);
		final JRadioButton checkout=new JRadioButton("Checkout a SVN Gramlab project",false);
		ButtonGroup bg=new ButtonGroup();
		center.add(fromScratch);
		center.add(createFromUnitexSystem);
		center.add(createFromUnitexUser);
		center.add(clone);
		center.add(checkout);
		bg.add(fromScratch);
		bg.add(createFromUnitexSystem);
		bg.add(createFromUnitexUser);
		bg.add(clone);
		bg.add(checkout);
		center.add(new JLabel(""));
		p.add(center,BorderLayout.CENTER);
		JPanel down=new JPanel();
		JButton ok=new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fromScratch.isSelected()) {
					setContentPane(createEmptyProjectPane());
				} else if (createFromUnitexSystem.isSelected()) {
					setContentPane(createUnitexSystemProjectPane());
				} else if (createFromUnitexUser.isSelected()) {
					setContentPane(createUnitexUserProjectPane());
				} else if (clone.isSelected()) {
					setContentPane(createCloneProjectPane());
				} else if (checkout.isSelected()) {
					setVisible(false);
					dispose();
					new SvnCheckoutDialog();
					return;
				} 
				pack();
				FrameUtil.center(getOwner(),CreateProjectDialog.this);
			}
		});
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		down.add(cancel);
		down.add(ok);
		KeyUtil.addCRListener(ok);
		KeyUtil.addCRListener(cancel);
		p.add(down,BorderLayout.SOUTH);
		return p;
	}

	protected JPanel createCloneProjectPane() {
		JPanel p=new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.anchor=GridBagConstraints.WEST;
		p.add(new JLabel("Select the project to clone and the name"),gbc);
		p.add(new JLabel("for the new one:"),gbc);
		p.add(new JLabel(""),gbc);
		gbc.gridwidth=1;
		p.add(new JLabel("Name: "),gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		final JTextField name=new JTextField();
		gbc.fill=GridBagConstraints.HORIZONTAL;
		p.add(name,gbc);
		gbc.fill=GridBagConstraints.BOTH;
		
		JPanel listPanel=new JPanel(new BorderLayout());
		final JList projectList=createProjectList(getWorkspaceProjectDirs());
		listPanel.add(createProjectListPane("Existing projects",projectList));
		gbc.weightx=1;
		gbc.weighty=1;
		gbc.fill=GridBagConstraints.BOTH;
		p.add(listPanel,gbc);
		gbc.weightx=0;
		gbc.weighty=0;
		gbc.fill=GridBagConstraints.NONE;
		gbc.anchor=GridBagConstraints.NORTHWEST;
				
		JPanel down=new JPanel();
		JButton ok=new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final File f=(File) projectList.getSelectedValue();
				if (f==null) {
					JOptionPane.showMessageDialog(null,
	                        "You must select a project to clone", "Error",
	                        JOptionPane.ERROR_MESSAGE);
	                return;
				}
				if (!checkName(name.getText())) {
					return;
				}
				setVisible(false);
				dispose();
				final JDialog dialog=createWaitingDialog("Please wait while your project is created...");
				dialog.setVisible(true);
				new Thread(new Runnable() {
					@Override
					public void run() {
						final GramlabProject project=GramlabProject.cloneProject(name.getText(),f);
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								dialog.setVisible(false);
								dialog.dispose();
								if (project==null) {
									JOptionPane.showMessageDialog(null,
					                        "Cannot create project "+name.getText(), "Error",
					                        JOptionPane.ERROR_MESSAGE);
					                return;
								}
								GlobalProjectManager.getAs(GramlabProjectManager.class).addProject(project);
								GlobalProjectManager.getAs(GramlabProjectManager.class).openProject(project);
							}
						});
						
					}
				}).start();
			}
		});
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		down.add(cancel);
		down.add(ok);
		KeyUtil.addCRListener(cancel);
		KeyUtil.addCRListener(ok);
		KeyUtil.addCRListener(name,ok);
		gbc.anchor=GridBagConstraints.CENTER;
		p.add(down,gbc);
		return p;
	}

	JPanel createEmptyProjectPane() {
		JPanel p=new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		final JTextField name=new JTextField();
		final JTextField group=new JTextField();
		final JTextField artifact=new JTextField();
		final JTextField version=new JTextField();
		final JComboBox languageComboBox=new JComboBox(Language.getSortedValues());
		final JComboBox encoding=new JComboBox(Encoding.values());
		final JRadioButton common=new JRadioButton("Common or ...");
		final JRadioButton selectLanguage=new JRadioButton("Select a language: ");
		encoding.setSelectedItem(Encoding.UTF8);
		JPanel main=createProjectSettings(name,common,selectLanguage,languageComboBox,encoding,group,artifact,version,true);
		p.add(main,BorderLayout.CENTER);
		JPanel down=new JPanel();
		JButton ok=new JButton("OK");
		KeyUtil.addCRListener(name,ok);
		KeyUtil.addCRListener(group,ok);
		KeyUtil.addCRListener(artifact,ok);
		KeyUtil.addCRListener(version,ok);
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!checkName(name.getText())) {
					return;
				}
				if (selectLanguage.isSelected() && languageComboBox.getSelectedIndex()<=0) {
					JOptionPane.showMessageDialog(null,
	                        "You must set the language of your project", "Error",
	                        JOptionPane.ERROR_MESSAGE);
					return;
				}
				final String language=common.isSelected()?"Common":((Language)languageComboBox.getSelectedItem()).name();
				Artifact a=Artifact.checkedArtifactCreation(group.getText(),artifact.getText(),version.getText(),name.getText());
				if (a==null) {
					return;
				}
				final GramlabProject project=GramlabProject.createEmptyProject(name.getText(),language,(Encoding) encoding.getSelectedItem(),a);
				if (project==null) {
					JOptionPane.showMessageDialog(null,
	                        "Cannot create project "+name.getText(), "Error",
	                        JOptionPane.ERROR_MESSAGE);
	                return;
				}
				GlobalProjectManager.getAs(GramlabProjectManager.class).addProject(project);
				setVisible(false);
				dispose();
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						new ConfigureProjectDialog(project,true,true,null);
					}
				});
			}
		});
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		down.add(cancel);
		down.add(ok);
		KeyUtil.addCRListener(cancel);
		KeyUtil.addCRListener(ok);
		p.add(down,BorderLayout.SOUTH);
		return p;
	}


	private static Pattern projectNamePattern=Pattern.compile("^[a-zA-Z0-9_\\-\\.]+$");
	public static boolean checkName(String name) {
		if ("".equals(name)) {
			JOptionPane.showMessageDialog(null,
                        "You must indicate a project name", "Error",
                        JOptionPane.ERROR_MESSAGE);
            return false;
		}
		if (!projectNamePattern.matcher(name).matches()) {
			JOptionPane.showMessageDialog(null,
                    "Invalid project name! All characters must be in [a-zA-Z0-9_-.].", "Error",
                    JOptionPane.ERROR_MESSAGE);
					return false;
		}
		File f=new File(GramlabConfigManager.getWorkspaceDirectory(),name);
		if (f.exists()) {
			JOptionPane.showMessageDialog(null,
                    "Directory "+name+" already exists in workspace", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
		}
		return true;
	}

	private static JPanel createProjectInfoSettings(JTextField name,
			JRadioButton common,JRadioButton selectLanguage,
			JComboBox languageComboBox,
			JComboBox encoding,
			boolean editable) {
		JPanel p=new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createTitledBorder("Project information"));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		addLine(p,gbc,"Name:",name);
		name.setEditable(editable);
		p.add(createLanguageSelectionPanel(common,selectLanguage,editable,languageComboBox),gbc);
		addLine(p,gbc,"Encoding:",encoding);
		p.add(new JLabel(),gbc);
		return p;
	}
	
	
	private static JPanel createLanguageSelectionPanel(
			JRadioButton common,JRadioButton selectLanguage,
			boolean editable,final JComboBox languageComboBox) {
		ButtonGroup bg=new ButtonGroup();
		bg.add(common);
		bg.add(selectLanguage);
		selectLanguage.setSelected(true);
		final Font defaultFont=languageComboBox.getFont();
		final Font italic=new Font(defaultFont.getName(),Font.ITALIC,defaultFont.getSize());
		languageComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index=languageComboBox.getSelectedIndex();
				if (index<=0) {
					languageComboBox.setFont(italic);
				} else {
					languageComboBox.setFont(defaultFont);
				}
			}
		});
		languageComboBox.setRenderer(new DefaultListCellRenderer() {
			
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
				setFont((index==0)?italic:defaultFont);
				return this;
			}
		});
		languageComboBox.setSelectedIndex(0);
		common.setEnabled(editable);
		selectLanguage.setEnabled(editable);
		languageComboBox.setEnabled(editable);
		JPanel p=new JPanel(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.anchor=GridBagConstraints.WEST;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(common,gbc);
		gbc.gridwidth=1;
		p.add(selectLanguage,gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.weightx=1;
		p.add(languageComboBox,gbc);
		return p;
	}

	private static JPanel createProjectMavenSettings(
			JTextField group, JTextField artifact, JTextField version) {
		JPanel p=new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createTitledBorder("Maven information"));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.anchor=GridBagConstraints.WEST;
		p.add(new JLabel("Those information are not required until you"),gbc);
		p.add(new JLabel("want to build and distribute your project."),gbc);
		p.add(new JLabel("Default values are "+Artifact.DEFAULT_GROUP_ID+"/"+
				Artifact.DEFAULT_ARTIFACT_ID+"/"+Artifact.DEFAULT_VERSION),gbc);
		p.add(new JLabel(),gbc);
		addLine(p,gbc,"groupId:",group);
		addLine(p,gbc,"artifactId:",artifact);
		addLine(p,gbc,"version:",version);
		p.add(new JLabel(),gbc);
		return p;
	}


	/**
	 * 'editable' should be true iff the project is created. If not,
	 * if means that we are editing the project configuration after
	 * its creation, and then, we don't allow modifying the project's 
	 * name and language. 
	 */
	public static JPanel createProjectSettings(JTextField name,
			JRadioButton common,JRadioButton selectLanguage,
			JComboBox languageComboBox,
			JComboBox encoding,
			JTextField group, JTextField artifact, JTextField version,
			boolean editable) {
		JPanel p=new JPanel(new GridLayout(2,1));
		p.add(createProjectInfoSettings(name, common, selectLanguage, languageComboBox,encoding,editable));
		p.add(createProjectMavenSettings(group, artifact, version));
		return p;
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

	JPanel createUnitexSystemProjectPane() {
		JPanel p=new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.anchor=GridBagConstraints.WEST;
		final JTextField name=new JTextField();
		final JRadioButton common=new JRadioButton("Common or ...");
		final JRadioButton selectLanguage=new JRadioButton("Select a language: ");
		final JComboBox languageComboBox=new JComboBox(Language.getSortedValues());
		final JTextField group=new JTextField();
		final JTextField artifact=new JTextField();
		final JTextField version=new JTextField();
		final JComboBox encoding=new JComboBox(Encoding.values());
		encoding.setSelectedItem(Encoding.UTF8);
		JPanel main=createProjectSettings(name,common,selectLanguage,languageComboBox,encoding,group,artifact,version,true);
		p.add(main,gbc);
		p.add(new JLabel("Select one of the language directories that"),gbc);
		p.add(new JLabel("come with Unitex:"),gbc);
		p.add(new JLabel(""),gbc);
		
		JPanel lists=new JPanel(new BorderLayout());
		final JList unitexList=createProjectList(getUnitexResourceDirs());
		unitexList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (unitexList.getSelectedIndex()!=-1) {
					String value=((File)unitexList.getSelectedValue()).getName();
					Language l=Language.getLanguage(value);
					if (l==null) {
						languageComboBox.setSelectedIndex(0);
					} else {
						languageComboBox.setSelectedItem(l);
					}
				}
			}
		});
		lists.add(createProjectListPane(" ",unitexList));
		gbc.weightx=1;
		gbc.weighty=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.fill=GridBagConstraints.BOTH;
		p.add(lists,gbc);
		gbc.weightx=1;
		gbc.weighty=0;
		p.add(new JLabel(" "),gbc);
		final JLabel progress=new JLabel(" ");
		p.add(progress,gbc);
		gbc.fill=GridBagConstraints.NONE;
		gbc.anchor=GridBagConstraints.NORTHWEST;
		JPanel down=new JPanel();
		JButton ok=new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!checkName(name.getText())) {
					return;
				}
				final Artifact a=Artifact.checkedArtifactCreation(group.getText(),artifact.getText(),version.getText(),name.getText());
				if (a==null) {
					return;
				}
				final File src;
				if ((src=(File)unitexList.getSelectedValue())==null) { 
					JOptionPane.showMessageDialog(null,
	                        "You must select a project to clone", "Error",
	                        JOptionPane.ERROR_MESSAGE);
	                return;
				}
				if (selectLanguage.isSelected() && languageComboBox.getSelectedIndex()<=0) {
					JOptionPane.showMessageDialog(null,
	                        "You must select the language of your new project.", "Error",
	                        JOptionPane.ERROR_MESSAGE);
					return;
				}
				final String language=common.isSelected()?"Common":((Language)languageComboBox.getSelectedItem()).name();
				setVisible(false);
				dispose();
				final JDialog dialog=createWaitingDialog("Please wait while your project is created...");
				dialog.setVisible(true);
				new Thread(new Runnable() {
					@Override
					public void run() {
						final GramlabProject project=GramlabProject.cloneUnitexResourcesProject(name.getText(),
								src,(Encoding) encoding.getSelectedItem(),a,true,language);
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								dialog.setVisible(false);
								dialog.dispose();
								if (project==null) {
									JOptionPane.showMessageDialog(null,
					                        "Cannot create project "+name.getText(), "Error",
					                        JOptionPane.ERROR_MESSAGE);
					                return;
								}
								GlobalProjectManager.getAs(GramlabProjectManager.class).addProject(project);
								GlobalProjectManager.getAs(GramlabProjectManager.class).openProject(project);
							}
						});
						
					}
				}).start();
			}
		});
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		down.add(cancel);
		down.add(ok);
		KeyUtil.addCRListener(cancel);
		KeyUtil.addCRListener(ok);
		KeyUtil.addCRListener(name,ok);
		gbc.anchor=GridBagConstraints.CENTER;
		p.add(down,gbc);
		return p;
	}
	
	
	protected JDialog createWaitingDialog(String string) {
		JDialog dialog=new JDialog(Main.getMainFrame(),false);
		dialog.setTitle("");
		JPanel p=new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		p.add(new JLabel(string),BorderLayout.CENTER);
		dialog.setContentPane(p);
		dialog.pack();
		FrameUtil.center(null,dialog);
		return dialog;
	}

	private JList createProjectList(ArrayList<File> dirs) {
		JList l=new JList(dirs.toArray());
		l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		l.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				File f=(File)value;
				return super.getListCellRendererComponent(list, f.getName(), index, isSelected,
						cellHasFocus);
			}
		});
		return l;
	}

	private JPanel createProjectListPane(String title,
			JList list) {
		JPanel p=new JPanel(new BorderLayout());
		p.add(new JLabel(title),BorderLayout.NORTH);
		p.add(new JScrollPane(list));
		return p;
	}

	private ArrayList<File> getWorkspaceProjectDirs() {
		ArrayList<File> l=new ArrayList<File>();
		for (GramlabProject p:GlobalProjectManager.getAs(GramlabProjectManager.class).getProjects()) {
			l.add(p.getProjectDirectory());
		}
		Collections.sort(l);
		return l;
	}

	private ArrayList<File> getUnitexResourceDirs() {
		ArrayList<File> l=new ArrayList<File>();
		File[] files=ConfigManager.getManager().getMainDirectory().listFiles();
		for (File f:files) {
			if (f.isDirectory() && ConfigManager.getManager().isValidLanguageName(f.getName())) {
				l.add(f);
			}
		}
		Collections.sort(l);
		return l;
	}

	
	JPanel createUnitexUserProjectPane() {
		JPanel p=new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.anchor=GridBagConstraints.WEST;
		final JTextField name=new JTextField();
		final JRadioButton common=new JRadioButton("Common or ...");
		final JRadioButton selectLanguage=new JRadioButton("Select a language: ");
		final JComboBox languageComboBox=new JComboBox(Language.getSortedValues());
		final JTextField group=new JTextField();
		final JTextField artifact=new JTextField();
		final JTextField version=new JTextField();
		final JComboBox encoding=new JComboBox(Encoding.values());
		encoding.setSelectedItem(Encoding.UTF8);
		JPanel main=createProjectSettings(name,common,selectLanguage,languageComboBox,encoding,group,artifact,version,true);
		p.add(main,gbc);
		p.add(new JLabel("Select the Unitex user language directory to import:"),gbc);
		JPanel foo=new JPanel(new BorderLayout());
		LinkButton set=new LinkButton("Set:");
		foo.add(set,BorderLayout.WEST);
		final JTextField dir=new JTextField("");
		foo.add(dir,BorderLayout.CENTER);
		gbc.weightx=1;
		gbc.fill=GridBagConstraints.BOTH;
		p.add(foo,gbc);
		set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc=new JFileChooser(GramlabConfigManager.getWorkspaceDirectory());
				jfc.setMultiSelectionEnabled(false);
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = jfc.showOpenDialog(CreateProjectDialog.this);
				if (returnVal != JFileChooser.APPROVE_OPTION) return;
				File f=jfc.getSelectedFile();
				if (!looksLikeAUnitexDirectory(f)) {
					final String[] options = { "Yes", "No" };
					final int n = JOptionPane
						.showOptionDialog(Main.getMainFrame(),
							"The directory "+f.getAbsolutePath()+"\n"+
							"does not look like a Unitex language directory. Do you want to go on ?", "",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options,
							options[0]);
					if (n == 1) {
						return;
					}
				}
				dir.setText(f.getAbsolutePath());
				String value=f.getName();
				Language l=Language.getLanguage(value);
				if (l==null) {
					languageComboBox.setSelectedIndex(0);
				} else {
					languageComboBox.setSelectedItem(l);
				}
			}
		});
		gbc.weightx=0;
		gbc.weighty=0;
		p.add(new JLabel(" "),gbc);
		final JLabel progress=new JLabel(" ");
		p.add(progress,gbc);
		gbc.fill=GridBagConstraints.NONE;
		gbc.anchor=GridBagConstraints.NORTHWEST;
		JPanel down=new JPanel();
		JButton ok=new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!checkName(name.getText())) {
					return;
				}
				final Artifact a=Artifact.checkedArtifactCreation(group.getText(),artifact.getText(),version.getText(),name.getText());
				if (a==null) {
					return;
				}
				if (dir.getText().equals("")) {
					JOptionPane.showMessageDialog(null,
                        "You must select a directory to import", "Error",
                        JOptionPane.ERROR_MESSAGE);
                	return;
				}
				final File src=new File(dir.getText());
				if (!src.exists()) {
					JOptionPane.showMessageDialog(null,
	                        name+" does not exist!", "Error",
	                        JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (!src.isDirectory()) {
					JOptionPane.showMessageDialog(null,
	                        name+" is not a directory!", "Error",
	                        JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (selectLanguage.isSelected() && languageComboBox.getSelectedIndex()<=0) {
					JOptionPane.showMessageDialog(null,
	                        "You must select the language of your new project.", "Error",
	                        JOptionPane.ERROR_MESSAGE);
					return;
				}
				final String language=common.isSelected()?"Common":((Language)languageComboBox.getSelectedItem()).name();
				setVisible(false);
				dispose();
				final JDialog dialog=createWaitingDialog("Please wait while your project is created...");
				dialog.setVisible(true);
				new Thread(new Runnable() {
					@Override
					public void run() {
						final GramlabProject project=GramlabProject.cloneUnitexResourcesProject(name.getText(),
								src,(Encoding) encoding.getSelectedItem(),a,false,language);
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								dialog.setVisible(false);
								dialog.dispose();
								if (project==null) {
									JOptionPane.showMessageDialog(null,
					                        "Cannot create project "+name.getText(), "Error",
					                        JOptionPane.ERROR_MESSAGE);
					                return;
								}
								GlobalProjectManager.getAs(GramlabProjectManager.class).addProject(project);
								GlobalProjectManager.getAs(GramlabProjectManager.class).openProject(project);
							}
						});
						
					}
				}).start();
			}
		});
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		down.add(cancel);
		down.add(ok);
		KeyUtil.addCRListener(cancel);
		KeyUtil.addCRListener(ok);
		KeyUtil.addCRListener(name,ok);
		gbc.anchor=GridBagConstraints.CENTER;
		p.add(down,gbc);
		return p;
	}

	protected boolean looksLikeAUnitexDirectory(File f) {
		File corpus=new File(f,"Corpus");
		if (!corpus.exists() || !corpus.isDirectory()) return false;
		File graphs=new File(f,"Graphs");
		if (!graphs.exists() || !graphs.isDirectory()) return false;
		File dela=new File(f,"Dela");
		if (!dela.exists() || !dela.isDirectory()) return false;
		return true;
	}

}
