package org.gramlab.core.gramlab.project.config.rendering;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.gramlab.core.Main;
import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.umlv.unitex.frames.FrameUtil;

@SuppressWarnings("serial")
public class RenderingConfigDialog extends JDialog {
	
	GramlabProject project;

	public RenderingConfigDialog(final GramlabProject p) {
		super(Main.getMainFrame(), "Rendering options for project "+p.getName(), true);
		this.project=p;
		JPanel pane=new JPanel(new BorderLayout());
		final RenderingConfigPane configPane=new RenderingConfigPane(p);
		pane.add(configPane,BorderLayout.CENTER);
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
				if (configPane.validateConfiguration(p)
						&& saveConfiguration()) {
					setVisible(false);
				}
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


	private boolean saveConfiguration() {
		try {
			project.saveConfigurationFiles(false);
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
                    "Error while saving your project configuration:\n\n"+e.getCause(),
                    "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
}
