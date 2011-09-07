package fr.gramlab.frames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
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

import fr.gramlab.Main;
import fr.gramlab.config.GramlabConfigManager;
import fr.gramlab.workspace.Project;
import fr.gramlab.workspace.ProjectManager;
import fr.umlv.unitex.config.ConfigManager;

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
		JPanel center=new JPanel(new GridLayout(5,1));
		center.add(new JLabel(""));
		final JRadioButton fromScratch=new JRadioButton("Create an empty project",true);
		final JRadioButton clone=new JRadioButton("Clone an existing project",false);
		final JRadioButton inherit=new JRadioButton("Inherit from existing project(s)",false);
		ButtonGroup bg=new ButtonGroup();
		center.add(fromScratch);
		center.add(clone);
		center.add(inherit);
		bg.add(fromScratch);
		bg.add(clone);
		bg.add(inherit);
		center.add(new JLabel(""));
		p.add(center,BorderLayout.CENTER);
		JPanel down=new JPanel();
		JButton ok=new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fromScratch.isSelected()) {
					setContentPane(createEmptyProjectPane());
				} else if (clone.isSelected()) {
					setContentPane(createCloneProjectPane());
				}
				pack();
				FrameUtil.center(getOwner(),CreateProjectDialog.this);
			}
		});
		down.add(ok);
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		down.add(cancel);
		p.add(down,BorderLayout.SOUTH);
		return p;
	}

	JPanel createEmptyProjectPane() {
		JPanel p=new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		JPanel main=new JPanel(new GridLayout(4,1));
		main.add(new JLabel("Select your project's name:"));
		main.add(new JLabel(""));
		final JTextField text=new JTextField();
		main.add(text);
		main.add(new JLabel(""));
		p.add(main,BorderLayout.CENTER);
		JPanel down=new JPanel();
		JButton ok=new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name=text.getText();
				if ("".equals(name)) {
					JOptionPane.showMessageDialog(null,
		                        "You must indicate a project name", "Error",
		                        JOptionPane.ERROR_MESSAGE);
		            return;
				}
				File f=new File(GramlabConfigManager.getWorkspaceDirectory(),name);
				if (f.exists()) {
					JOptionPane.showMessageDialog(null,
	                        "Directory "+name+" already exists in workspace", "Error",
	                        JOptionPane.ERROR_MESSAGE);
	                return;
				}
				Project project=Project.createEmptyProject(name);
				if (project==null) {
					JOptionPane.showMessageDialog(null,
	                        "Cannot create project "+name, "Error",
	                        JOptionPane.ERROR_MESSAGE);
	                return;
				}
				ProjectManager.getManager().addProject(project);
				setVisible(false);
				dispose();
			}
		});
		down.add(ok);
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		down.add(cancel);
		p.add(down,BorderLayout.SOUTH);
		return p;
	}

	JPanel createCloneProjectPane() {
		JPanel p=new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		JPanel main=new JPanel(new GridLayout(5,1));
		JPanel tmp=new JPanel(new BorderLayout());
		tmp.add(new JLabel("Enter your project's name:  "),BorderLayout.WEST);
		final JTextField text=new JTextField();
		tmp.add(text,BorderLayout.CENTER);
		main.add(tmp);
		main.add(new JLabel("and select a project to clone, either from your"));
		main.add(new JLabel("workspace or from the language directories that"));
		main.add(new JLabel("come with Unitex."));
		main.add(new JLabel(""));
		p.add(main,BorderLayout.NORTH);
		
		JPanel lists=new JPanel(new GridLayout(1,2));
		final JList workspaceList=createProjectList(getWorkspaceProjectDirs());
		final JList unitexList=createProjectList(getUnitexResourceDirs());
		workspaceList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (workspaceList.getSelectedIndex()!=-1) {
					unitexList.clearSelection();
				}
			}
		});
		unitexList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (unitexList.getSelectedIndex()!=-1) {
					workspaceList.clearSelection();
					if (text.getText().equals("")) {
						text.setText(((File)unitexList.getSelectedValue()).getName());
					}
				}
			}
		});
		lists.add(createProjectListPane("Workspace projects",workspaceList));
		lists.add(createProjectListPane("Unitex resources",unitexList));
		p.add(lists,BorderLayout.CENTER);
		
		JPanel down=new JPanel();
		JButton ok=new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name=text.getText();
				if ("".equals(name)) {
					JOptionPane.showMessageDialog(null,
		                        "You must indicate a project name", "Error",
		                        JOptionPane.ERROR_MESSAGE);
		            return;
				}
				File f=new File(GramlabConfigManager.getWorkspaceDirectory(),name);
				if (f.exists()) {
					JOptionPane.showMessageDialog(null,
	                        "Directory "+name+" already exists in workspace", "Error",
	                        JOptionPane.ERROR_MESSAGE);
	                return;
				}
				File src=null;
				Project project=null;
				if ((src=(File)workspaceList.getSelectedValue())!=null) {
					project=Project.cloneWorkspaceProject(name,src);
				} else if ((src=(File)unitexList.getSelectedValue())!=null) {
					project=Project.cloneUnitexResourcesProject(name,src);
				} else {
					JOptionPane.showMessageDialog(null,
	                        "You must select a project to clone", "Error",
	                        JOptionPane.ERROR_MESSAGE);
	                return;
				}
				if (project==null) {
					JOptionPane.showMessageDialog(null,
	                        "Cannot create project "+name, "Error",
	                        JOptionPane.ERROR_MESSAGE);
	                return;
				}
				
				ProjectManager.getManager().addProject(project);
				setVisible(false);
				dispose();
			}
		});
		down.add(ok);
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		down.add(cancel);
		p.add(down,BorderLayout.SOUTH);
		return p;
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
		for (Project p:ProjectManager.getManager().getProjects()) {
			l.add(p.getDirectory());
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

}
