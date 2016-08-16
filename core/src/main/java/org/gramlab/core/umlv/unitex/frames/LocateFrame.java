/*
 * Unitex
 *
 * Copyright (C) 2001-2016 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 *
 */
package org.gramlab.core.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.gramlab.core.gramlab.util.KeyUtil;
import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.config.PreferencesListener;
import org.gramlab.core.umlv.unitex.config.PreferencesManager;
import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.io.Encoding;
import org.gramlab.core.umlv.unitex.io.UnicodeIO;
import org.gramlab.core.umlv.unitex.listeners.LanguageListener;
import org.gramlab.core.umlv.unitex.process.Launcher;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.process.commands.Grf2Fst2Command;
import org.gramlab.core.umlv.unitex.process.commands.LocateCommand;
import org.gramlab.core.umlv.unitex.process.commands.LocateTfstCommand;
import org.gramlab.core.umlv.unitex.process.commands.MultiCommands;
import org.gramlab.core.umlv.unitex.process.commands.Reg2GrfCommand;
import org.gramlab.core.umlv.unitex.project.manager.UnitexProjectManager;

/**
 * This class describes the locate pattern frame.
 * 
 * @author Sébastien Paumier
 */
public class LocateFrame extends JInternalFrame {
	final JRadioButton regularExpression = new JRadioButton(
			"Regular expression:");
	final JRadioButton graph = new JRadioButton("Graph:", true);
	final JTextField regExp = new JTextField();
	final JTextField graphName = new JTextField();
	private final JRadioButton shortestMatches = new JRadioButton(
			"Shortest matches");
	private final JRadioButton longuestMatches = new JRadioButton(
			"Longest matches", true);
	private final JRadioButton allMatches = new JRadioButton("All matches");
	private final JRadioButton ignoreOutputs = new JRadioButton(
			"Are not taken into account", true);
	private final JRadioButton mergeOutputs = new JRadioButton(
			"Merge with input text");
	private final JRadioButton replaceOutputs = new JRadioButton(
			"Replace recognized sequences");
	private final JRadioButton stopAfterNmatches = new JRadioButton(
			"Stop after ", true);
	private final JRadioButton indexAllMatches = new JRadioButton(
			"Index all occurrences in text");
	private final JTextField nMatches = new JTextField("200");
	private final JRadioButton locateOnSnt = new JRadioButton(
			"Paumier 2003, working on text (quicker)", true);
	private final JRadioButton locateOnTfst = new JRadioButton(
			"automaton intersection (higher precision)");
	private final JRadioButton allowAmbiguousOutputs = new JRadioButton(
			"Allow ambiguous outputs", true);
	private final JRadioButton forbidAmbiguousOutputs = new JRadioButton(
			"Forbid ambiguous outputs");
	private final JRadioButton ignoreVariableErrors = new JRadioButton(
			"Ignore variable errors", true);
	private final JRadioButton exitOnVariableErrors = new JRadioButton(
			"Exit on variable error");
	private final JRadioButton backtrackOnVariableErrors = new JRadioButton(
			"Backtrack on variable error");
	private final JCheckBox debug = new JCheckBox("Activate debug mode", false);

	private final JTextField maxExplorationSteps = new JTextField("1000");
	private final JTextField maxMatchesPerSubgraph = new JTextField("200");
	private final JTextField maxMatchesPerToken = new JTextField("400");
	private final JRadioButton defaultTolerance = new JRadioButton(
			"Apply search bounds above", true);
	private final JRadioButton lessTolerant = new JRadioButton(
			"Restrict search bounds to 50%");
	private final JRadioButton lesserTolerant = new JRadioButton(
			"Restrict search bounds to 20%");
	private final JRadioButton leastTolerant = new JRadioButton(
			"Restrict search bounds to 10%");
	private final JTextField maxErrors = new JTextField("50");
	
	LocateFrame() {
		super("Locate Pattern", false, true);
		setContentPane(constructPanel());
		pack();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		PreferencesManager.addPreferencesListener(new PreferencesListener() {
			@Override
			public void preferencesChanged(String language) {
				regExp.setFont(ConfigManager.getManager().getTextFont(null));
			}
		});
		Config.addLanguageListener(new LanguageListener() {
			@Override
			public void languageChanged() {
				regExp.setText("");
				graphName.setText("");
			}
		});
	}

	private JTabbedPane constructPanel() {
		final JTabbedPane tabbedPane = new JTabbedPane();
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(constructPatternPanel(), BorderLayout.NORTH);
		panel.add(constructDownPanel(), BorderLayout.SOUTH);
		tabbedPane.addTab("Locate configuration", panel);
		tabbedPane.addTab("Advanced options", constructAdvancedOptionPanel());
		KeyUtil.addCloseFrameListener(panel);
		return tabbedPane;
	}

	private Component constructAdvancedOptionPanel() {
		final JPanel box = new JPanel(null);
		box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
		final JPanel panel1 = new JPanel(new GridLayout(2, 1));
		panel1.setBorder(BorderFactory
				.createTitledBorder("Ambiguous output policy:"));
		final ButtonGroup b1 = new ButtonGroup();
		b1.add(allowAmbiguousOutputs);
		b1.add(forbidAmbiguousOutputs);
		panel1.add(allowAmbiguousOutputs);
		panel1.add(forbidAmbiguousOutputs);
		allowAmbiguousOutputs
				.setToolTipText("Displays all the outputs for every match");
		forbidAmbiguousOutputs
				.setToolTipText("Displays only one output per match (arbitrarily chosen)");
		box.add(panel1);
		
		final JPanel panel2 = new JPanel(new GridLayout(4, 1));
		panel2.setBorder(BorderFactory
				.createTitledBorder("Variable error policy:"));
		final ButtonGroup b2 = new ButtonGroup();
		b2.add(ignoreVariableErrors);
		b2.add(exitOnVariableErrors);
		b2.add(backtrackOnVariableErrors);
		panel2.add(new JLabel(
				"Note: these options have no effect if outputs are ignored."));
		panel2.add(ignoreVariableErrors);
		panel2.add(exitOnVariableErrors);
		panel2.add(backtrackOnVariableErrors);
		ignoreVariableErrors.setToolTipText("Acts as if the variable is empty");
		exitOnVariableErrors.setToolTipText("Kills the program");
		backtrackOnVariableErrors
				.setToolTipText("Stop exploring the current path of the grammar");
		box.add(panel2);
		
		final JPanel panel3 = new JPanel(new GridLayout(3, 2));
		panel3.setBorder(BorderFactory
				.createTitledBorder("Search bounds"));
		panel3.add(new JLabel("Max exploration steps"));
		panel3.add(maxExplorationSteps);
		panel3.add(new JLabel("Max matches per subgraph"));
		panel3.add(maxMatchesPerSubgraph);
		panel3.add(new JLabel("Max matches per token"));
		panel3.add(maxMatchesPerToken);
		box.add(panel3);

		final JPanel panel4 = new JPanel(new GridLayout(4, 1));
		panel4.setBorder(BorderFactory
				.createTitledBorder("Restrict search bounds"));
		final ButtonGroup b4 = new ButtonGroup();
		b4.add(defaultTolerance);
		b4.add(lessTolerant);
		b4.add(lesserTolerant);
		b4.add(leastTolerant);
		panel4.add(defaultTolerance);
		panel4.add(lessTolerant);
		panel4.add(lesserTolerant);
		panel4.add(leastTolerant);
		box.add(panel4);

		final JPanel panel5 = new JPanel(new GridLayout(1, 2));
		panel5.setBorder(BorderFactory
				.createTitledBorder("Search error policy"));
		panel5.add(new JLabel("Max numbers of errors to display after exit"));
		panel5.add(maxErrors);
		box.add(panel5);
		
		/* This BorderLayout is important because it acts as a glue */
		box.add(new JPanel(new BorderLayout()));
		return box;
	}

	private JPanel constructPatternPanel() {
		final JPanel patternPanel = new JPanel(new BorderLayout());
		patternPanel.setBorder(new TitledBorder(
				"Locate pattern in the form of:"));
		final Action setGraphAction = new AbstractAction("Set") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser grfAndFst2 = Config.getGrfAndFst2DialogBox();
				final int returnVal = grfAndFst2.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				graphName.setText(grfAndFst2.getSelectedFile()
						.getAbsolutePath());
				graph.setSelected(true);
			}
		};
		final JButton setGraphButton = new JButton(setGraphAction);
		final ButtonGroup bg = new ButtonGroup();
		graphName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				graph.setSelected(true);
			}
		});
		regExp.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				regularExpression.setSelected(true);
			}
		});
		bg.add(regularExpression);
		bg.add(graph);
		patternPanel.add(regularExpression, BorderLayout.NORTH);
		regExp.setPreferredSize(new Dimension(300, 30));
		regExp.setFont(ConfigManager.getManager().getTextFont(null));
		patternPanel.add(regExp, BorderLayout.CENTER);
		final JPanel p = new JPanel(new BorderLayout());
		p.add(graph, BorderLayout.WEST);
		p.add(graphName, BorderLayout.CENTER);
		p.add(setGraphButton, BorderLayout.EAST);
		p.add(debug, BorderLayout.SOUTH);
		patternPanel.add(p, BorderLayout.SOUTH);
		return patternPanel;
	}

	private JPanel constructDownPanel() {
		final JPanel downPanel = new JPanel(new BorderLayout());
		final JPanel a = new JPanel(new BorderLayout());
		final JPanel b = new JPanel(new BorderLayout());
		a.add(constructIndexPanel(), BorderLayout.WEST);
		a.add(constructOutputPanel(), BorderLayout.CENTER);
		b.add(constructSearchLimitationPanel(), BorderLayout.WEST);
		final Action searchAction = new AbstractAction("SEARCH") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				launchLocate();
			}
		};
		final JButton searchButton = new JButton(searchAction);
		b.add(searchButton, BorderLayout.CENTER);
		final JPanel textType = new JPanel(new GridLayout(2, 1));
		textType.setBorder(BorderFactory
				.createTitledBorder("Search algorithm:"));
		final ButtonGroup bg = new ButtonGroup();
		textType.add(locateOnSnt);
		textType.add(locateOnTfst);
		locateOnTfst
				.setToolTipText("Currently, LocateTfst does not support outputs nor contexts. "
						+ "It will never handle morphological mode, but morphological "
						+ "filters can be used. It works fine on Korean data.");
		bg.add(locateOnSnt);
		bg.add(locateOnTfst);
		b.add(textType, BorderLayout.SOUTH);
		downPanel.add(a, BorderLayout.CENTER);
		downPanel.add(b, BorderLayout.SOUTH);
		return downPanel;
	}

	private JPanel constructIndexPanel() {
		final JPanel indexPanel = new JPanel(new GridLayout(3, 1));
		indexPanel.setBorder(new TitledBorder("Index"));
		final ButtonGroup bg = new ButtonGroup();
		bg.add(shortestMatches);
		bg.add(longuestMatches);
		bg.add(allMatches);
		indexPanel.add(shortestMatches);
		indexPanel.add(longuestMatches);
		indexPanel.add(allMatches);
		return indexPanel;
	}

	private JPanel constructOutputPanel() {
		final JPanel outputPanel = new JPanel(new GridLayout(3, 1));
		outputPanel.setBorder(new TitledBorder("Grammar outputs"));
		final ButtonGroup bg = new ButtonGroup();
		bg.add(ignoreOutputs);
		bg.add(mergeOutputs);
		bg.add(replaceOutputs);
		outputPanel.add(ignoreOutputs);
		outputPanel.add(mergeOutputs);
		outputPanel.add(replaceOutputs);
		return outputPanel;
	}

	private JPanel constructSearchLimitationPanel() {
		final JPanel searchLimitationPanel = new JPanel(new GridLayout(2, 1));
		searchLimitationPanel.setBorder(new TitledBorder("Search limitation"));
		final JPanel p = new JPanel(new BorderLayout());
		p.add(stopAfterNmatches, BorderLayout.WEST);
		p.add(nMatches, BorderLayout.CENTER);
		p.add(new JLabel(" matches"), BorderLayout.EAST);
		final ButtonGroup bg = new ButtonGroup();
		bg.add(stopAfterNmatches);
		bg.add(indexAllMatches);
		searchLimitationPanel.add(p);
		searchLimitationPanel.add(indexAllMatches);
		return searchLimitationPanel;
	}

	void launchLocate() {
		final MultiCommands commands = new MultiCommands();
		File fst2;
		int n = -1;
		if (stopAfterNmatches.isSelected()) {
			try {
				n = Integer.parseInt(nMatches.getText());
			} catch (final NumberFormatException e) {
				JOptionPane.showMessageDialog(null,
						"Invalid empty search limitation value !", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		if (regularExpression.isSelected()) {
			if (regExp.getText().equals("")) {
				JOptionPane.showMessageDialog(null,
						"Empty regular expression !", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			final File regexpFile = new File(
					Config.getUserCurrentLanguageDir(), "regexp.txt");
			FileUtil.write(regExp.getText(), regexpFile);
			final Reg2GrfCommand reg2GrfCmd = new Reg2GrfCommand()
					.file(regexpFile);
			commands.addCommand(reg2GrfCmd);
			final File grf = new File(Config.getUserCurrentLanguageDir(),
					"regexp.grf");
			Grf2Fst2Command grfCmd = new Grf2Fst2Command().grf(grf)
					.enableLoopAndRecursionDetection(true)
					.tokenizationMode(null, grf).repositories()
					.emitEmptyGraphWarning().displayGraphNames();
			if (debug.isSelected())
				grfCmd = grfCmd.debug();
			commands.addCommand(grfCmd);
			fst2 = new File(Config.getUserCurrentLanguageDir(), "regexp.fst2");
		} else {
			/* If we need to process a graph */
			if (graphName.getText().equals("")) {
				JOptionPane.showMessageDialog(null,
						"You must specify a graph name", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			final String grfName = graphName.getText();
			if (grfName.length() > 4
					&& grfName
							.substring(grfName.length() - 3, grfName.length())
							.equalsIgnoreCase("grf")) {
				final File grf = new File(grfName);
				Grf2Fst2Command grfCmd = new Grf2Fst2Command().grf(grf)
						.enableLoopAndRecursionDetection(true)
						.tokenizationMode(null, grf).repositories()
						.emitEmptyGraphWarning().displayGraphNames();
				if (debug.isSelected())
					grfCmd = grfCmd.debug();
				commands.addCommand(grfCmd);
				String fst2Name = grfName.substring(0, grfName.length() - 3);
				fst2Name = fst2Name + "fst2";
				fst2 = new File(fst2Name);
			} else {
				if (grfName.length() > 4
						&& !(grfName.substring(grfName.length() - 4,
								grfName.length()).equalsIgnoreCase("fst2"))) {
					JOptionPane.showMessageDialog(null,
							"Invalid graph name extension !", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (debug.isSelected()) {
					JOptionPane
							.showMessageDialog(
									null,
									"Cannot work in debug mode with precompiled .fst2.\nSelect a .grf file or a enter a regular expression.",
									"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				fst2 = new File(grfName);
			}
		}
		ToDo toDo;
		if (locateOnSnt.isSelected()) {
			/* Locate on .snt text */
			LocateCommand locateCmd = new LocateCommand()
					.snt(Config.getCurrentSnt()).fst2(fst2)
					.alphabet(ConfigManager.getManager().getAlphabet(null));
			if (shortestMatches.isSelected())
				locateCmd = locateCmd.shortestMatches();
			else if (longuestMatches.isSelected())
				locateCmd = locateCmd.longestMatches();
			else
				locateCmd = locateCmd.allMatches();
			if (ignoreOutputs.isSelected())
				locateCmd = locateCmd.ignoreOutputs();
			else if (mergeOutputs.isSelected())
				locateCmd = locateCmd.mergeOutputs();
			else
				locateCmd = locateCmd.replaceWithOutputs();
			if (stopAfterNmatches.isSelected()) {
				locateCmd = locateCmd.limit(n);
			} else {
				locateCmd = locateCmd.noLimit();
			}
			if (ConfigManager.getManager().isKorean(null)) {
				locateCmd = locateCmd.korean();
			}
			if (ConfigManager.getManager().isArabic(null)) {
				/*
				 * TODO: mettre le fichier arabic typo rules dans le config
				 * manager
				 */
				locateCmd = locateCmd.arabic(new File(Config
						.getUserCurrentLanguageDir(), "arabic_typo_rules.txt"));
			}
			if (ConfigManager.getManager().isCharByCharLanguage(null)) {
				locateCmd = locateCmd.charByChar();
			}
			if (ConfigManager.getManager().isMorphologicalUseOfSpaceAllowed(
					null)) {
				locateCmd = locateCmd.enableMorphologicalUseOfSpace();
			}
			locateCmd = locateCmd.morphologicalDic(ConfigManager.getManager()
					.morphologicalDictionaries(null));
			if (allowAmbiguousOutputs.isSelected()) {
				locateCmd = locateCmd.allowAmbiguousOutputs();
			} else {
				locateCmd = locateCmd.forbidAmbiguousOutputs();
			}
			if (ignoreVariableErrors.isSelected()) {
				locateCmd = locateCmd.ignoreVariableErrors();
			} else if (exitOnVariableErrors.isSelected()) {
				locateCmd = locateCmd.exitOnVariableErrors();
			} else {
				locateCmd = locateCmd.backtrackOnVariableErrors();
			}
			
			int maxExplorationStepsValue = 1000;
			try {
				maxExplorationStepsValue = Integer.parseInt(maxExplorationSteps
						.getText());
			} catch (final NumberFormatException e) {
				JOptionPane.showMessageDialog(null,
						"Invalid max exploration steps value !", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			locateCmd = locateCmd.maxExplorationSteps(maxExplorationStepsValue);
			
			int maxMatchesPerSubgraphValue = 200;
			try {
				maxMatchesPerSubgraphValue = Integer.parseInt(maxMatchesPerSubgraph
						.getText());
			} catch (final NumberFormatException e) {
				JOptionPane.showMessageDialog(null,
						"Invalid max matches per subgraph value !", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			locateCmd = locateCmd.maxMatchesPerSubgraph(maxMatchesPerSubgraphValue);
			
			int maxMatchesPerTokenValue = 400;
			try {
				maxMatchesPerTokenValue = Integer.parseInt(maxMatchesPerToken
						.getText());
			} catch (final NumberFormatException e) {
				JOptionPane.showMessageDialog(null,
						"Invalid max matches per token value !", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			locateCmd = locateCmd.maxMatchesPerToken(maxMatchesPerTokenValue);
			
			if (lessTolerant.isSelected()) {
				locateCmd = locateCmd.lessTolerant();
			} else if (lesserTolerant.isSelected()) {
				locateCmd = locateCmd.lesserTolerant();
			} else if (leastTolerant.isSelected()) {
				locateCmd = locateCmd.leastTolerant();
			} else {
				// default
			}
			
			int maxErrorsValue = 50;
			try {
				maxErrorsValue = Integer.parseInt(maxErrors.getText());
			} catch (final NumberFormatException e) {
				JOptionPane.showMessageDialog(null,
						"Invalid max errors value !", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			locateCmd = locateCmd.maxErrors(maxErrorsValue);
			
			commands.addCommand(locateCmd);
			toDo = new LocateDo(Config.getCurrentSntDir());
		} else {
			/* If we want to work on the text automaton */
			final File tfst = new File(Config.getCurrentSntDir(), "text.tfst");
			if (!tfst.exists()) {
				JOptionPane.showMessageDialog(null,
						"Text automaton does not exist. You must construct it\n"
								+ "before launching locate operation.",
						"Error", JOptionPane.ERROR_MESSAGE);
				locateOnSnt.setSelected(true);
				return;
			}
			LocateTfstCommand locateCmd = new LocateTfstCommand().tfst(tfst)
					.fst2(fst2)
					.alphabet(ConfigManager.getManager().getAlphabet(null));
			if (!ConfigManager.getManager().isMatchWordBoundaries(null)) {
				locateCmd = locateCmd.dontMatchWordBoundaries();
			}
			if (shortestMatches.isSelected())
				locateCmd = locateCmd.shortestMatches();
			else if (longuestMatches.isSelected())
				locateCmd = locateCmd.longestMatches();
			else
				locateCmd = locateCmd.allMatches();
			if (ignoreOutputs.isSelected())
				locateCmd = locateCmd.ignoreOutputs();
			else if (mergeOutputs.isSelected())
				locateCmd = locateCmd.mergeOutputs();
			else
				locateCmd = locateCmd.replaceWithOutputs();
			if (stopAfterNmatches.isSelected()) {
				locateCmd = locateCmd.limit(n);
			} else {
				locateCmd = locateCmd.noLimit();
			}
			if (ConfigManager.getManager().isKorean(null)) {
				locateCmd = locateCmd.korean();
			}
			if (allowAmbiguousOutputs.isSelected()) {
				locateCmd = locateCmd.allowAmbiguousOutputs();
			} else {
				locateCmd = locateCmd.forbidAmbiguousOutputs();
			}
			if (ignoreVariableErrors.isSelected()) {
				locateCmd = locateCmd.ignoreVariableErrors();
			} else if (exitOnVariableErrors.isSelected()) {
				locateCmd = locateCmd.exitOnVariableErrors();
			} else {
				locateCmd = locateCmd.backtrackOnVariableErrors();
			}
			commands.addCommand(locateCmd);
			toDo = new LocateTfstDo(Config.getCurrentSntDir());
		}
		setVisible(false);
		savePreviousConcordance();
		Launcher.exec(commands, true, toDo);
	}

	/**
     *
     */
	private void savePreviousConcordance() {
		final File prevConcord = new File(Config.getCurrentSntDir(),
				"previous-concord.ind");
		if (prevConcord.exists()) {
			prevConcord.delete();
		}
		final File concord = new File(Config.getCurrentSntDir(), "concord.ind");
		if (concord.exists()) {
			concord.renameTo(prevConcord);
		}
	}

	/**
	 * Loads the content of a 'concord.n' file.
	 */
	static String readInfo(File file) {
		if (!file.exists()) {
			return null;
		}
		if (!file.canRead()) {
			return null;
		}
		String res;
		final InputStreamReader reader = Encoding.getInputStreamReader(file);
		if (reader == null) {
			return null;
		}
		try {
			res = UnicodeIO.readLine(reader) + "\n";
			res = res + UnicodeIO.readLine(reader) + "\n";
			res = res + UnicodeIO.readLine(reader);
			reader.close();
		} catch (final FileNotFoundException e) {
			return null;
		} catch (final IOException e) {
			return null;
		}
		return res;
	}

	/**
	 * Loads the content of a 'concord_tfst.n' file.
	 */
	String readTfstInfo(File file) {
		if (!file.exists()) {
			return null;
		}
		if (!file.canRead()) {
			return null;
		}
		String res;
		final InputStreamReader source = Encoding.getInputStreamReader(file);
		if (source == null) {
			return null;
		}
		try {
			res = UnicodeIO.readLine(source);
			source.close();
		} catch (final FileNotFoundException e) {
			return null;
		} catch (final IOException e) {
			return null;
		}
		return res;
	}

	class LocateDo implements ToDo {
		File sntDir;

		public LocateDo(File sntDir) {
			this.sntDir = sntDir;
		}

		@Override
		public void toDo(boolean success) {
			final String res = readInfo(new File(sntDir, "concord.n"));
			JOptionPane.showMessageDialog(null, res, "Result Info",
					JOptionPane.PLAIN_MESSAGE);
			if (!res.startsWith("0")) {
				UnitexProjectManager.search(sntDir)
						.getFrameManagerAs(InternalFrameManager.class)
						.newConcordanceParameterFrame();
			}
		}
	}

	class LocateTfstDo implements ToDo {
		File sntDir;

		public LocateTfstDo(File sntDir) {
			this.sntDir = sntDir;
		}

		@Override
		public void toDo(boolean success) {
			final String res = readTfstInfo(new File(sntDir, "concord_tfst.n"));
			JOptionPane.showMessageDialog(null, res, "Result Info",
					JOptionPane.PLAIN_MESSAGE);
			if (!res.startsWith("0")) {
				UnitexProjectManager.search(sntDir)
						.getFrameManagerAs(InternalFrameManager.class)
						.newConcordanceParameterFrame();
			}
		}
	}
}
