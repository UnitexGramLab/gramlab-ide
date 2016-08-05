package org.gramlab.core.gramlab.project.config.maven;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.gramlab.core.Main;
import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.umlv.unitex.frames.FrameUtil;

@SuppressWarnings("serial")
public class EditFst2Dialog extends JDialog {
	
	GramlabProject project;
	DefaultListModel model;
	JTextField dstFst2=new JTextField("");

	public EditFst2Dialog(final GramlabProject p,final DefaultListModel model,final int index) {
		super(Main.getMainFrame(), "Fst2 configuration", true);
		this.project=p;
		this.model=model;
		JPanel pane=new JPanel(new BorderLayout());
		pane.add(createPanel(index),BorderLayout.CENTER);
		JPanel down=new JPanel();
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		JButton ok=new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String fst2=dstFst2.getText();
				if (fst2.equals("")) {
					JOptionPane.showMessageDialog(null,
							"You must specify a target name for your graph",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (fst2.contains("/") || fst2.contains("\\")) {
					JOptionPane.showMessageDialog(null,
							"Your target name should not contain any file separator / or \\\n"+
					        "since it will automatically be located in the same directory\n"+
					        "structure than the .grf one.",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (!fst2.endsWith(".fst2")) {
					fst2 = fst2 + ".fst2";
				}
				/* Now, we have to check if there is already a target with that name */
				GrfToCompile g=(GrfToCompile) model.get(index);
				File foo=new File(g.getGrf().getParentFile(),fst2);
				String srcRelativeName=MavenDialog.inSrcDirectory(project,foo);
				String dst=new File(srcRelativeName).getName();
				if (existsTarget(index,dst)) {
					JOptionPane.showMessageDialog(null,
							"There is already a target fst2 with this name.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				model.remove(index);
				model.add(index,new GrfToCompile(g.getGrf(),dst));
				setVisible(false);
			}
		});
		down.add(cancel);
		down.add(ok);
		pane.add(down,BorderLayout.SOUTH);
		setContentPane(pane);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		FrameUtil.center(getOwner(),this);
		setVisible(true);
	}


	private JPanel createPanel(final int index) {
		JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		p.add(new JLabel("Graph:"),gbc);
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		GrfToCompile g=(GrfToCompile)model.get(index);
		JTextField t=new JTextField(MavenDialog.inSrcDirectory(project,g.getGrf()));
		t.setEditable(false);
		p.add(t,gbc);
		gbc.weightx=0;
		gbc.gridwidth=1;
		p.add(new JLabel("Target name:"),gbc);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(dstFst2,gbc);
		gbc.weightx=0;
		gbc.gridwidth=1;
		dstFst2.setText(g.getFst2());
		return p;
	}


	private boolean existsTarget(int index,String targetFst2) {
		int n = model.size();
		for (int i = 0; i < n; i++) {
			if (i==index) continue;
			String fst2 = ((GrfToCompile) model.get(i)).getFst2();
			if (targetFst2.equals(fst2))
				return true;
		}
		return false;
	}

}
