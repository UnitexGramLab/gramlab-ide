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
package fr.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.exceptions.InvalidConcordanceOrderException;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.files.PersonalFileFilter;
import fr.umlv.unitex.io.Encoding;
import fr.umlv.unitex.io.UnicodeIO;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.ConcordCommand;
import fr.umlv.unitex.process.commands.Grf2Fst2Command;
import fr.umlv.unitex.process.commands.LocateCommand;
import fr.umlv.unitex.process.commands.MultiCommands;
import fr.umlv.unitex.process.commands.Reg2GrfCommand;
import fr.umlv.unitex.xalign.ConcordanceLoader;
import fr.umlv.unitex.xalign.ConcordanceModel;

/**
 * This class describes the XAlign locate pattern frame.
 * 
 * @author Sébastien Paumier
 */
public class XAlignLocateFrame extends JInternalFrame {
	final JRadioButton regularExpression = new JRadioButton(
			"Regular expression:");
	final JRadioButton graph = new JRadioButton("Graph:", true);
	private final JTextField regExp = new JTextField();
	final JTextField graphName = new JTextField();
	private final JRadioButton shortestMatches = new JRadioButton(
			"Shortest matches");
	private final JRadioButton longuestMatches = new JRadioButton(
			"Longest matches", true);
	private final JRadioButton allMatches = new JRadioButton("All matches");
	private final JRadioButton stopAfterNmatches = new JRadioButton(
			"Stop after ", true);
	private final JRadioButton indexAllMatches = new JRadioButton(
			"Index all occurrences in text");
	private final JTextField nMatches = new JTextField("200");
	private String language;
	private File snt;
	private ConcordanceModel concordModel;

	XAlignLocateFrame(String language) {
		super("XAlign Locate Pattern", false, true);
		this.language = language;
		setContentPane(constructPanel());
		pack();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	void configure(String language1, File snt1, ConcordanceModel concordModel1) {
		this.language = language1;
		this.snt = snt1;
		this.concordModel = concordModel1;
	}

	private JPanel constructPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(constructPatternPanel(), BorderLayout.CENTER);
		panel.add(constructDownPanel(), BorderLayout.SOUTH);
		return panel;
	}

	private JFileChooser grfAndFst2DialogBox;

	JFileChooser getGrfAndFst2DialogBox(File dir) {
		if (grfAndFst2DialogBox != null) {
			grfAndFst2DialogBox.setCurrentDirectory(dir);
			return grfAndFst2DialogBox;
		}
		grfAndFst2DialogBox = new JFileChooser();
		grfAndFst2DialogBox.addChoosableFileFilter(new PersonalFileFilter(
				"fst2", "Unicode Compiled Graphs"));
		grfAndFst2DialogBox.addChoosableFileFilter(new PersonalFileFilter(
				"grf", "Unicode Graphs"));
		grfAndFst2DialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
		grfAndFst2DialogBox.setCurrentDirectory(dir);
		grfAndFst2DialogBox.setMultiSelectionEnabled(false);
		return grfAndFst2DialogBox;
	}

	private JPanel constructPatternPanel() {
		final JPanel patternPanel = new JPanel(new BorderLayout());
		patternPanel.setBorder(new TitledBorder(
				"Locate pattern in the form of:"));
		final File graphDir = new File(new File(Config.getUserDir(), language),
				"Graphs");
		final Action setGraphAction = new AbstractAction("Set") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser grfAndFst2 = getGrfAndFst2DialogBox(graphDir);
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
		patternPanel.add(regExp, BorderLayout.CENTER);
		final JPanel p = new JPanel(new BorderLayout());
		p.add(graph, BorderLayout.WEST);
		p.add(graphName, BorderLayout.CENTER);
		p.add(setGraphButton, BorderLayout.EAST);
		patternPanel.add(p, BorderLayout.SOUTH);
		return patternPanel;
	}

	private JPanel constructDownPanel() {
		final JPanel downPanel = new JPanel(new BorderLayout());
		final JPanel b = new JPanel(new BorderLayout());
		b.add(constructSearchLimitationPanel(), BorderLayout.WEST);
		final XAlignLocateFrame f = this;
		final Action searchAction = new AbstractAction("SEARCH") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				f.launchLocate();
			}
		};
		final JButton searchButton = new JButton(searchAction);
		b.add(searchButton, BorderLayout.CENTER);
		downPanel.add(constructIndexPanel(), BorderLayout.CENTER);
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
			// we need to process a regular expression
			if (regExp.getText().equals("")) {
				JOptionPane.showMessageDialog(null,
						"Empty regular expression !", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			final File regexpFile = new File(new File(Config.getUserDir(),
					language), "regexp.txt");
			createRegExpFile(regExp.getText(), regexpFile);
			final Reg2GrfCommand reg2GrfCmd = new Reg2GrfCommand()
					.file(regexpFile);
			commands.addCommand(reg2GrfCmd);
			final File grf = new File(new File(Config.getUserDir(), language),
					"regexp.grf");
			final Grf2Fst2Command grfCmd = new Grf2Fst2Command().grf(grf)
					.enableLoopAndRecursionDetection(true)
					.tokenizationMode(null, grf).repositories()
					.emitEmptyGraphWarning().displayGraphNames();
			commands.addCommand(grfCmd);
			fst2 = new File(new File(Config.getUserDir(), language),
					"regexp.fst2");
		} else {
			// we need to process a graph
			if (graphName.getText().equals("")) {
				JOptionPane.showMessageDialog(null,
						"You must specify a graph name", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			final String grfName = graphName.getText();
			if (grfName.substring(grfName.length() - 3, grfName.length())
					.equalsIgnoreCase("grf")) {
				// we must compile the grf
				final File grf = new File(grfName);
				final Grf2Fst2Command grfCmd = new Grf2Fst2Command().grf(grf)
						.enableLoopAndRecursionDetection(true)
						.tokenizationMode(null, grf).repositories()
						.emitEmptyGraphWarning().displayGraphNames();
				commands.addCommand(grfCmd);
				String fst2Name = grfName.substring(0, grfName.length() - 3);
				fst2Name = fst2Name + "fst2";
				fst2 = new File(fst2Name);
			} else {
				if (!(grfName.substring(grfName.length() - 4, grfName.length())
						.equalsIgnoreCase("fst2"))) {
					// if the extension is nor GRF neither FST2
					JOptionPane.showMessageDialog(null,
							"Invalid graph name extension !", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				fst2 = new File(grfName);
			}
		}
		final File alphabet = new File(new File(Config.getUserDir(), language),
				"Alphabet.txt");
		LocateCommand locateCmd = new LocateCommand().snt(snt).fst2(fst2)
				.alphabet(alphabet);
		if (shortestMatches.isSelected())
			locateCmd = locateCmd.shortestMatches();
		else if (longuestMatches.isSelected())
			locateCmd = locateCmd.longestMatches();
		else
			locateCmd = locateCmd.allMatches();
		locateCmd = locateCmd.ignoreOutputs();
		if (ConfigManager.getManager().isKorean(language)) {
			locateCmd = locateCmd.korean();
		}
		if (ConfigManager.getManager().isArabic(language)) {
			locateCmd = locateCmd.arabic(new File(Config
					.getUserCurrentLanguageDir(), "arabic_typo_rules.txt"));
		}
		if (stopAfterNmatches.isSelected()) {
			locateCmd = locateCmd.limit(n);
		} else {
			locateCmd = locateCmd.noLimit();
		}
		if (ConfigManager.getManager().isCharByCharLanguage(language)) {
			locateCmd = locateCmd.charByChar();
		}
		if (ConfigManager.getManager().isMorphologicalUseOfSpaceAllowed(
				language)) {
			locateCmd = locateCmd.enableMorphologicalUseOfSpace();
		}
		locateCmd = locateCmd.morphologicalDic(ConfigManager.getManager()
				.morphologicalDictionaries(language));
		commands.addCommand(locateCmd);
		String foo = FileUtil.getFileNameWithoutExtension(snt) + "_snt";
		final File indFile = new File(foo, "concord.ind");
		ConcordCommand concord = null;
		try {
			concord = new ConcordCommand().indFile(indFile).font("NULL")
					.fontSize(0).left(0, false).right(0, false).order(0)
					.xalign();
			commands.addCommand(concord);
		} catch (final InvalidConcordanceOrderException e) {
			e.printStackTrace();
		}
		setVisible(false);
		foo = FileUtil.getFileNameWithoutExtension(indFile) + ".txt";
		Launcher.exec(commands, true, new XAlignLocateDo(new File(foo),
				concordModel), false);
	}

	private void createRegExpFile(String regExp2, File f) {
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			final OutputStreamWriter writer = ConfigManager.getManager()
					.getEncoding(null).getOutputStreamWriter(f);
			final BufferedWriter bw = new BufferedWriter(writer);
			bw.write(regExp2, 0, regExp2.length());
			bw.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	static String readInfo(File file) {
		if (!file.exists()) {
			return null;
		}
		if (!file.canRead()) {
			return null;
		}
		String res;
		InputStreamReader source;
		try {
			source = Encoding.getInputStreamReader(file);
			if (source == null)
				return null;
			res = UnicodeIO.readLine(source) + "\n";
			res = res + UnicodeIO.readLine(source) + "\n";
			res = res + UnicodeIO.readLine(source);
			source.close();
		} catch (final FileNotFoundException e) {
			return null;
		} catch (final IOException e) {
			return null;
		}
		return res;
	}

	class XAlignLocateDo implements ToDo {
		final File file;
		final ConcordanceModel concordModel1;

		public XAlignLocateDo(File file, ConcordanceModel concordModel) {
			this.file = file;
			this.concordModel1 = concordModel;
		}

		@Override
		public void toDo(boolean success) {
			ConcordanceLoader.load(file, concordModel1);
		}
	}
}
