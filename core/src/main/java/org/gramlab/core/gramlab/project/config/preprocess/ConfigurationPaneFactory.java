package org.gramlab.core.gramlab.project.config.preprocess;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gramlab.core.gramlab.project.GramlabProject;

@SuppressWarnings("serial")
public abstract class ConfigurationPaneFactory extends JPanel {
	
	public ConfigurationPaneFactory(LayoutManager lm) {
		super(lm);
	}

		
	/**
	 * This method tries to validate the configuration corresponding
	 * to the current state of the pane. If everything is ok,
	 * this method is supposed to actually update the project configuration
	 * and return true. If any error occurs, the project configuration 
	 * remains unmodified and the method returns false.
	 */
	public abstract boolean validateConfiguration(GramlabProject project);
	
	
	public static JPanel createArtifactPane(JTextField group,JTextField artifact,JTextField version) {
		JPanel p=new JPanel(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		addLine(p,gbc,"groupId:",group);
		addLine(p,gbc,"artifactId:",artifact);
		addLine(p,gbc,"version:",version);
		p.add(new JLabel(),gbc);
		return p;
	}

	private static void addLine(JPanel p,GridBagConstraints gbc,String string,JComponent c) {
		gbc.gridwidth=1;
		gbc.anchor=GridBagConstraints.WEST;
		p.add(new JLabel(string),gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.weightx=1;
		p.add(c,gbc);
		gbc.weightx=0;
	}
	

}
