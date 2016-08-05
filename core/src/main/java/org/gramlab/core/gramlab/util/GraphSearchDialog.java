package org.gramlab.core.gramlab.util;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.gramlab.core.Main;
import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.gramlab.project.GramlabProjectManager;
import org.gramlab.core.gramlab.workspace.ProjectAdapter;
import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;
import org.gramlab.core.umlv.unitex.frames.FrameUtil;
import org.gramlab.core.umlv.unitex.frames.GraphFrame;

@SuppressWarnings("serial")
public class GraphSearchDialog extends JDialog {
	
	JTextField pattern;
	boolean init=false;
	GraphFrame frame;
	boolean closed=false;
	GramlabProject project;

	ProjectAdapter projectAdapter=new ProjectAdapter() {
		@Override
		public void currentProjectChanged(GramlabProject p, int pos) {
			setVisible(p==project);
		}
	};
	
	WindowListener windowAdapter=new WindowAdapter() {

		@Override
		public void windowClosed(WindowEvent e) {
			dispose();
		}
		
	};

	final InternalFrameAdapter adapter=new InternalFrameAdapter() {
		@Override
		public void internalFrameIconified(InternalFrameEvent e) {
			dispose();
		}
		
		@Override
		public void internalFrameClosed(InternalFrameEvent e) {
			dispose();
		}
		
		@Override
		public void internalFrameActivated(InternalFrameEvent e) {
			setVisible(true);
		}
		
		@Override
		public void internalFrameDeactivated(InternalFrameEvent e) {
			setVisible(false);
		}
	};
	
	public GraphSearchDialog(GraphFrame f) {
		super(Main.getMainFrame(),"Search",false);
		this.frame=f;
		setContentPane(createPane());
		pack();
		addWindowListener(windowAdapter);
		this.project=GlobalProjectManager.getAs(GramlabProjectManager.class)
			.getCurrentProject();
		GlobalProjectManager.getAs(GramlabProjectManager.class)
			.addProjectListener(projectAdapter);
		f.addInternalFrameListener(adapter);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
		FrameUtil.center(Main.getMainFrame(),this);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		removeWindowListener(windowAdapter);
		GlobalProjectManager.getAs(GramlabProjectManager.class)
			.removeProjectListener(projectAdapter);
		frame.removeInternalFrameListener(adapter);
	}

	private JPanel createPane() {
		JPanel p=new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.fill=GridBagConstraints.BOTH;
		gbc.anchor=GridBagConstraints.WEST;		
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(new JLabel("Graph: "+frame.getTabName()),gbc);
		gbc.gridwidth=1;
		gbc.insets=new Insets(2,2,2,2);
		pattern=new JTextField("Type the pattern to look for");
		pattern.setForeground(Color.LIGHT_GRAY);
		pattern.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				pattern.removeKeyListener(this);
				if (!init) {
					init=true;
					pattern.setText("");
					pattern.setForeground(Color.BLACK);
				}
			}
		});
		pattern.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				pattern.removeMouseListener(this);
				if (!init) {
					init=true;
					pattern.setText("");
					pattern.setForeground(Color.BLACK);
				}
			}
		});
		p.add(pattern,gbc);
		gbc.weightx=0;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		JButton find=new JButton("Find");
		find.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!init || pattern.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "You must type an expression to look for",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				int res=frame.find(pattern.getText());
				if (res==0) {
					JOptionPane.showMessageDialog(null, "Pattern not found!",
							"", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (res==1) {
					JOptionPane.showMessageDialog(null, "No more occurrence found.",
							"", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
		});
		p.add(find,gbc);
		KeyUtil.addCRListener(pattern,find);
		KeyUtil.addCRListener(find);
		return p;
	}
	
}
