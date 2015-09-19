package fr.gramlab.project.config.graph_compilation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.config.preprocess.ConfigurationPaneFactory;
import fr.umlv.unitex.LinkButton;

@SuppressWarnings("serial")
public class GraphCompilationPane extends ConfigurationPaneFactory {

	JCheckBox emitEmptyGraphWarning;
	JCheckBox displayGraphNames;
	JCheckBox strictTokenization;
	GramlabProject project;
	
	ActionListener validateConfigListener=new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			validateAndSave();
		}
	};
	
	public void validateAndSave() {
		if (validateConfiguration(project)) {
			try {
				project.saveConfigurationFiles(false);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
	                    "Error while saving your project configuration:\n\n"+e.getCause(),
	                    "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public GraphCompilationPane(final GramlabProject project) {
		super(new GridBagLayout());
		this.project=project;
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.anchor=GridBagConstraints.WEST;
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		add(createCompilationOptionsPanel(project),gbc);
		LinkButton repositories=new LinkButton("Configure repositories");
		repositories.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ConfigureRepositoriesDialog(project);
			}
		});
		add(repositories,gbc);
		gbc.weighty=1;
		add(new JPanel(null),gbc);
	}

	
	
	private JPanel createCompilationOptionsPanel(GramlabProject project) {
		JPanel p=new JPanel(new GridLayout(3,1));
		p.setBorder(BorderFactory.createTitledBorder("Verbosity"));
		emitEmptyGraphWarning=new JCheckBox("Emit warning on <E> matching",project.emitEmptyGraphWarning());
		displayGraphNames=new JCheckBox("Display compiled graph names",project.displayGraphNames());
		strictTokenization=new JCheckBox("Use strict tokenization",project.strictTokenization());
		p.add(emitEmptyGraphWarning);
		p.add(displayGraphNames);
		p.add(strictTokenization);
		emitEmptyGraphWarning.addActionListener(validateConfigListener);
		displayGraphNames.addActionListener(validateConfigListener);
		strictTokenization.addActionListener(validateConfigListener);
		return p;
	}



	@Override
	public boolean validateConfiguration(GramlabProject project) {
		project.setEmitEmptyGraphWarning(emitEmptyGraphWarning.isSelected());
		project.setDisplayGraphNames(displayGraphNames.isSelected());
		project.setStrictTokenization(strictTokenization.isSelected());
		return true;
	}
}
