package org.gramlab.core.gramlab.workspace;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.gramlab.core.GramlabConfigManager;
import org.gramlab.core.Main;
import org.gramlab.core.gramlab.project.GramlabProjectManager;
import org.gramlab.core.umlv.unitex.LinkButton;
import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;
import org.gramlab.core.umlv.unitex.frames.FrameUtil;

@SuppressWarnings("serial")
public class ChangeWorkspaceDialog extends JDialog {

	JTextField text=new JTextField();

	
	public ChangeWorkspaceDialog() {
		super(Main.getMainFrame(), "Change workspace", true);
		setContentPane(constructPanel());
		pack();
		FrameUtil.center(getOwner(),this);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private JPanel constructPanel() {
		JPanel p=new JPanel(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(new JLabel("Your current workspace is:"),gbc);
		p.add(new JLabel(" "),gbc);
		p.add(new JLabel(GramlabConfigManager.getWorkspaceDirectory().getAbsolutePath()),gbc);
		p.add(new JLabel(" "),gbc);
		gbc.gridwidth=1;
		gbc.weightx=0;
		LinkButton b=new LinkButton("Set:");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc=new JFileChooser(GramlabConfigManager.getWorkspaceDirectory());
				jfc.setMultiSelectionEnabled(false);
				jfc.setDialogType(JFileChooser.OPEN_DIALOG);
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				final int returnVal = jfc.showOpenDialog(ChangeWorkspaceDialog.this);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				File dir=jfc.getSelectedFile();
				if (dir==null) return;
				text.setText(dir.getAbsolutePath());
			}
		});
		p.add(b,gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.weightx=1;
		text.setText(GramlabConfigManager.getWorkspaceDirectory().getAbsolutePath());
		text.setPreferredSize(new Dimension(250,text.getPreferredSize().height));
		p.add(text,gbc);
		p.add(new JLabel(" "),gbc);
		JPanel buttons=new JPanel(new FlowLayout());
		JButton ok=new JButton("OK");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (text.getText().equals("")) {
					JOptionPane.showMessageDialog(null,
	                        "Empty workspace path", "Error",
	                        JOptionPane.ERROR_MESSAGE);
	                return;
				}
				File dir=new File(text.getText());
				if (!dir.exists()) {
					JOptionPane.showMessageDialog(null,
	                        "Directory "+text.getText()+" does not exist!", "Error",
	                        JOptionPane.ERROR_MESSAGE);
	                return;
				}
				if (!dir.isDirectory()) {
					JOptionPane.showMessageDialog(null,
	                        text.getText()+" is not a directory!", "Error",
	                        JOptionPane.ERROR_MESSAGE);
	                return;
				}
				GlobalProjectManager.getAs(GramlabProjectManager.class).changeWorkspace(dir);
				dispose();
			}
		});
		buttons.add(ok);
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		buttons.add(cancel);
		p.add(buttons,gbc);
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		return p;
	}
	
	
}
