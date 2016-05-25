package fr.gramlab.frames;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.gramlab.Main;
import fr.gramlab.project.GramlabProjectManager;
import fr.gramlab.util.KeyUtil;
import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.frames.FrameUtil;

@SuppressWarnings("serial")
public class NewFileDialog extends JDialog {

	JTextField text=new JTextField();
	
	public NewFileDialog(final File file) {
		super(Main.getMainFrame(),true);
		JPanel main=new JPanel(new BorderLayout());
		main.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		JPanel p=new JPanel(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(new JLabel("Enter the name of the file (including its extension) to create in"),gbc);
		p.add(new JLabel(file.getAbsolutePath()),gbc);
		p.add(new JLabel(" "),gbc);
		gbc.weightx=0;
		gbc.gridwidth=1;
		p.add(new JLabel("Name:"),gbc);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(text,gbc);
		main.add(p,BorderLayout.CENTER);
		JPanel down=new JPanel(new FlowLayout());
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		down.add(cancel);
		KeyUtil.addEscListener(p, cancel);
		JButton ok=new JButton("Ok");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (text.getText().equals("")) {
					JOptionPane.showMessageDialog(null,
	                       "You must indicate a file name!", "Error",
	                       JOptionPane.ERROR_MESSAGE);
					return;
				}
				File f=new File(file,text.getText());
				if (f.exists()) {
					JOptionPane.showMessageDialog(null,
		                       "A file with that name already exists !", "Error",
		                       JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					f.createNewFile();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null,
		                       "Cannot create this file !", "Error",
		                       JOptionPane.ERROR_MESSAGE);
					return;
				}
				setVisible(false);
				dispose();
				GlobalProjectManager.getAs(GramlabProjectManager.class)
					.getProject(f).openFile(f,true);
			}
		});
		down.add(ok);
		main.add(down,BorderLayout.SOUTH);
		setContentPane(main);
		pack();
		FrameUtil.center(null,this);
		setVisible(true);
	}
	
	
	
}
