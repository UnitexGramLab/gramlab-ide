package fr.gramlab.project.config.locate;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import fr.gramlab.project.ConfigurationListener;
import fr.gramlab.project.ProcessPane;
import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.config.graph_compilation.GraphCompilationPane;
import fr.gramlab.project.config.preprocess.ConfigurationPaneFactory;
import fr.gramlab.util.MyComboCellRenderer;
import fr.umlv.unitex.LinkButton;
import fr.umlv.unitex.config.PreferencesListener;
import fr.umlv.unitex.config.PreferencesManager;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.files.PersonalFileFilter;

@SuppressWarnings("serial")
public class LocateConfigPane extends ConfigurationPaneFactory {
	boolean patternIsRegexp;
	JTextField regex = new JTextField();
	JComboBox lastGraphs;
	MatchesPolicy index;
	OutputsPolicy outputs;
	boolean searchAll;
	JTextField limit = new JTextField();
	VariableErrorPolicy variableErrorPolicy;
	GraphCompilationPane graphCompilationConfigPane;
	GramlabProject project;
	JCheckBox ambiguousOutputsAllowed;
	DefaultComboBoxModel model;
	
	ActionListener validateConfigListener=new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			validateAndSave();
		}
	};

	public void validateAndSave() {
		if (validateConfiguration(project,false)) {
			try {
				project.saveConfigurationFiles(false);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
	                    "Error while saving your project configuration:\n\n"+e.getCause(),
	                    "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public LocateConfigPane(final GramlabProject project) {
		super(new GridBagLayout());
		this.project=project;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(createPatternPanel(project), gbc);
		add(createIndexPanel(project), gbc);
		add(createOutputsPanel(project), gbc);
		add(createSearchLimitPanel(project), gbc);
		add(createAmbiguousOutputsPanel(project), gbc);
		add(createVariableErrorPanel(project), gbc);
		LinkButton injectedVariables=new LinkButton("Variable injection");
		injectedVariables.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ConfigureVariableInjectionDialog(project);
			}
		});
		add(injectedVariables, gbc);
		gbc.weighty = 1;
		add(new JPanel(null), gbc);
	}

	private JPanel createPatternPanel(final GramlabProject project) {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		p.setBorder(BorderFactory
				.createTitledBorder("Locate pattern in the form of:"));
		patternIsRegexp = project.isLastPatternRegexp();
		ButtonGroup bg = new ButtonGroup();
		final JRadioButton regexp = new JRadioButton("Regular expression:",
				patternIsRegexp);
		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				patternIsRegexp = regexp.isSelected();
			}
		};
		regexp.addActionListener(al);
		bg.add(regexp);
		p.add(regexp, gbc);
		regex.setText(project.getLastRegexp());
		regex.setFont(project.getPreferences().getTextFont().getFont());
		PreferencesManager.addPreferencesListener(new PreferencesListener() {
			@Override
			public void preferencesChanged(String language) {
				regex.setFont(project.getPreferences().getTextFont().getFont());
				revalidate();
				repaint();
			}
		});
		p.add(regex, gbc);
		regex.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				regexp.doClick();
			}
		});
		final JRadioButton graph = new JRadioButton("Graph:", !patternIsRegexp);
		graph.addActionListener(al);
		bg.add(graph);
		p.add(graph, gbc);
		ArrayList<File> list = project.getLastGraphs();
		String[] tab = new String[list.size()];
		int i = 0;
		for (File f : list) {
			tab[i++] = project.getNormalizedFileName(f);
		}
		model=new DefaultComboBoxModel(tab);
		lastGraphs = new JComboBox(model);
		lastGraphs.setPreferredSize(new Dimension(0,0));
		lastGraphs.setEditable(true);
		lastGraphs.setRenderer(new MyComboCellRenderer(lastGraphs));
		if (project.isLastPatternRegexp()) {
			lastGraphs.setSelectedIndex(-1);
		}
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.BOTH;
		p.add(lastGraphs, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 0;
		JButton set = new JButton("Set");
		set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File currentDir = project.getLastGraphDir();
				if (lastGraphs.getSelectedItem() != null) {
					String s = (String) lastGraphs.getSelectedItem();
					File foo = project.getFileFromNormalizedName(s);
					if (foo.exists()) {
						currentDir = foo.getParentFile();
					}
				}
				if (currentDir==null) {
					currentDir=project.getGraphsDirectory();
				}
				JFileChooser jfc = new JFileChooser(currentDir);
				jfc.addChoosableFileFilter(new PersonalFileFilter("fst2",
						"Unicode Compiled Graphs"));
				jfc.addChoosableFileFilter(new PersonalFileFilter("grf",
						"Unicode Graphs"));
				jfc.setDialogType(JFileChooser.OPEN_DIALOG);
				jfc.setMultiSelectionEnabled(false);
				final int returnVal = jfc.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				File f = jfc.getSelectedFile();
				String s;
				try {
					s = project.getRelativeFileName(f);
				} catch (IllegalStateException e2) {
					s = f.getAbsolutePath();
				}
				lastGraphs.setSelectedItem(s);
				project.setLastGraphDir(jfc.getCurrentDirectory());
				graph.doClick();
			}
		});
		p.add(set, gbc);
		gbc.weightx = 1;
		
		graphCompilationConfigPane=new GraphCompilationPane(project);
		LinkButton param=new LinkButton();
		Runnable onHide=new Runnable() {
			@Override
			public void run() {
				graphCompilationConfigPane.validateAndSave();
			}
		};
		JPanel foo=ProcessPane.createHidablePane(param,"Setup graph compilation \u25BC", 
				"Setup graph compilation \u25B2", graphCompilationConfigPane,onHide);
		foo.add(param,BorderLayout.NORTH);
		p.add(foo,gbc);

		return p;
	}

	private void populateIndexPanel(JPanel p,final GramlabProject project) {
		
		
		ButtonGroup bg = new ButtonGroup();
		index = project.getMatchesPolicy();
		final MatchesPolicy[] values = new MatchesPolicy[] {
				MatchesPolicy.SHORTEST, MatchesPolicy.LONGEST,
				MatchesPolicy.ALL };
		final String[] names = new String[] { "Shortest matches",
				"Longest matches", "All matches" };
		for (int i = 0; i < values.length; i++) {
			final JRadioButton b = new JRadioButton(names[i], project
					.getMatchesPolicy().equals(values[i]));
			final MatchesPolicy tmp = values[i];
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (b.isSelected()) {
						index = tmp;
					}
				}
			});
			b.addActionListener(validateConfigListener);
			bg.add(b);
			p.add(b);
		}
	}
	
	private JPanel createIndexPanel(final GramlabProject project) {
		final JPanel p = new JPanel(new GridLayout(3, 1));
		p.setBorder(BorderFactory.createTitledBorder("Index"));
		populateIndexPanel(p,project);
		project.addConfigurationListener(new ConfigurationListener() {
			@Override
			public void configurationChanged() {
				p.removeAll();
				populateIndexPanel(p,project);
				p.revalidate();
				p.repaint();
			}
		});
		return p;
	}

	private void populateOutputsPanel(JPanel p,GramlabProject project) {
		ButtonGroup bg = new ButtonGroup();
		outputs = project.getOutputsPolicy();
		final OutputsPolicy[] values = new OutputsPolicy[] {
				OutputsPolicy.IGNORE, OutputsPolicy.MERGE,
				OutputsPolicy.REPLACE };
		final String[] names = new String[] { "Ignore",
				"Merge", "Replace" };
		for (int i = 0; i < values.length; i++) {
			final JRadioButton b = new JRadioButton(names[i], project
					.getOutputsPolicy().equals(values[i]));
			final OutputsPolicy tmp = values[i];
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (b.isSelected()) {
						outputs = tmp;
					}
				}
			});
			b.addActionListener(validateConfigListener);
			bg.add(b);
			p.add(b);
		}
	}
	
	private JPanel createOutputsPanel(final GramlabProject project) {
		final JPanel p = new JPanel(null);
		p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
		p.setBorder(BorderFactory.createTitledBorder("Grammar outputs"));
		populateOutputsPanel(p,project);
		project.addConfigurationListener(new ConfigurationListener() {
			@Override
			public void configurationChanged() {
				p.removeAll();
				populateOutputsPanel(p,project);
				p.revalidate();
				p.repaint();
			}
		});
		return p;
	}

	private void populateSearchLimitPanel(JPanel p,final GramlabProject project) {
		ButtonGroup bg = new ButtonGroup();
		int n = project.getSearchLimit();
		searchAll = (n == -1);
		if (n != -1) {
			limit.setText(n + "");
		} else {
			limit.setText("200");
		}
		limit.setPreferredSize(new Dimension(50,
				limit.getPreferredSize().height));
		final JRadioButton nMatches = new JRadioButton("Stop after ",
				!searchAll);
		ActionListener a = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchAll = !nMatches.isSelected();
			}
		};
		nMatches.addActionListener(a);
		JRadioButton allMatches = new JRadioButton(
				"Index all occurrences in text", searchAll);
		allMatches.addActionListener(a);
		bg.add(nMatches);
		bg.add(allMatches);
		GridBagConstraints gbc = new GridBagConstraints();
		p.add(nMatches, gbc);
		p.add(limit, gbc);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		p.add(new JLabel(" matches"), gbc);
		p.add(allMatches, gbc);
	}
	
	private JPanel createSearchLimitPanel(final GramlabProject project) {
		final JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createTitledBorder("Search limitation"));
		populateSearchLimitPanel(p,project);
		project.addConfigurationListener(new ConfigurationListener() {
			@Override
			public void configurationChanged() {
				p.removeAll();
				populateSearchLimitPanel(p,project);
				p.revalidate();
				p.repaint();
			}
		});
		return p;
	}

	private JCheckBox createAmbiguousOutputsPanel(final GramlabProject project) {
		ambiguousOutputsAllowed=new JCheckBox("Allow ambiguous outputs",project.isAmbiguousOutputsAllowed());
		ambiguousOutputsAllowed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				validateAndSave();
			}
		});
		project.addConfigurationListener(new ConfigurationListener() {
			@Override
			public void configurationChanged() {
				ambiguousOutputsAllowed.setSelected(project.isAmbiguousOutputsAllowed());
			}
		});
		return ambiguousOutputsAllowed;
	}

	private void populateVariableErrorPanel(JPanel p,final GramlabProject project) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		ButtonGroup bg = new ButtonGroup();
		variableErrorPolicy = project.getVariableErrorPolicy();
		final VariableErrorPolicy[] values = new VariableErrorPolicy[] {
				VariableErrorPolicy.IGNORE, VariableErrorPolicy.EXIT,
				VariableErrorPolicy.BACKTRACK };
		final String[] names = new String[] { "Ignore", "Exit",
				"Backtrack" };
		for (int i = 0; i < values.length; i++) {
			final JRadioButton b = new JRadioButton(names[i],
					variableErrorPolicy.equals(values[i]));
			final VariableErrorPolicy tmp = values[i];
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (b.isSelected()) {
						variableErrorPolicy = tmp;
					}
				}
			});
			b.addActionListener(validateConfigListener);
			bg.add(b);
			if (i == values.length - 1) {
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.weightx = 1;
			} else {
				gbc.gridwidth = 1;
				gbc.weightx = 0;
			}
			p.add(b, gbc);
		}
	}
	
	private JPanel createVariableErrorPanel(final GramlabProject project) {
		final JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createTitledBorder("Variable error policy"));
		populateVariableErrorPanel(p,project);
		project.addConfigurationListener(new ConfigurationListener() {
			@Override
			public void configurationChanged() {
				p.removeAll();
				populateVariableErrorPanel(p,project);
				p.revalidate();
				p.repaint();
			}
		});
		return p;
	}



	@Override
	public boolean validateConfiguration(GramlabProject project) {
		return validateConfiguration(project,true);
	}

	/**
	 * run==true means that we want to check the validity of the regexp/graph
	 */
	public boolean validateConfiguration(GramlabProject project,boolean run) {
		String regexp = regex.getText();
		File graphToApply = null;
		ArrayList<File> list = null;
		if (patternIsRegexp) {
			if (run && regexp.equals("")) {
				JOptionPane.showMessageDialog(null,
						"Empty regular expression !", "Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} else {
			String name = (String) lastGraphs.getSelectedItem();
			if (name != null && !"".equals(name)) {
				graphToApply = new File(name);
				if (!graphToApply.isAbsolute()) {
					graphToApply = new File(project.getProjectDirectory(), name);
				}
				if (!graphToApply.exists()) {
					JOptionPane.showMessageDialog(null,
							"This graph does not exist!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}
				String ext = FileUtil.getExtensionInLowerCase(graphToApply);
				if (!ext.equals("grf") && !ext.equals("fst2")) {
					JOptionPane.showMessageDialog(null,
							"Invalid graph name extension !", "Error",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}
				/* We update the project's last graphs */
				list = project.getLastGraphs();
				list.remove(graphToApply);
				list.add(0, graphToApply);
				model.removeAllElements();
				for (File f : list) {
					model.addElement(project.getNormalizedFileName(f));
				}
			} else {
				if (run) {
					JOptionPane.showMessageDialog(null,
						"No graph selected !", "Error",
						JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
			/* We also check the graph compilation configuration */
			if (!graphCompilationConfigPane.validateConfiguration(project)) {
				return false;
			}
		}
		int n;
		if (searchAll) {
			n = -1;
		} else {
			try {
				n = Integer.parseInt(limit.getText());
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null,
						"Invalid or empty search limitation value !", "Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if (n<0) {
				JOptionPane.showMessageDialog(null,
						"Invalid search limitation value !", "Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		/*
		 * Everything is OK, so we can actually modify the project's
		 * configuration
		 */
		project.setLastPatternRegexp(patternIsRegexp);
		project.setLastRegexp(regexp);
		if (list != null)
			project.setLastGraphs(list);
		project.setMatchesPolicy(index);
		project.setOutputsPolicy(outputs);
		project.setVariableErrorPolicy(variableErrorPolicy);
		project.setSearchLimit(n);
		project.setAmbiguousOutputsAllowed(ambiguousOutputsAllowed.isSelected());
		return true;
	}
}
