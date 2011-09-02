package fr.gramlab.frames;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import fr.gramlab.Main;
import fr.gramlab.config.GramlabConfigManager;
import fr.gramlab.workspace.Project;
import fr.gramlab.workspace.ProjectManager;

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
					createEmptyProject();
				}
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

	protected void createEmptyProject() {
		setContentPane(createEmptyProjectPane());
		pack();
		FrameUtil.center(getOwner(),this);
	}

	private JPanel createEmptyProjectPane() {
		JPanel p=new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		JPanel main=new JPanel(new GridLayout(5,1));
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
				if ("".equals(name)) return;
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
				ProjectManager.addProject(project);
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
	
}
