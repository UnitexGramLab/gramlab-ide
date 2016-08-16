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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import org.gramlab.core.umlv.unitex.console.Console;
import org.gramlab.core.umlv.unitex.exceptions.InvalidConcordanceOrderException;
import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.files.PersonalFileFilter;
import org.gramlab.core.umlv.unitex.io.Encoding;
import org.gramlab.core.umlv.unitex.process.Launcher;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.process.commands.ConcorDiffCommand;
import org.gramlab.core.umlv.unitex.process.commands.ConcordCommand;
import org.gramlab.core.umlv.unitex.process.commands.ExtractCommand;
import org.gramlab.core.umlv.unitex.process.commands.MkdirCommand;
import org.gramlab.core.umlv.unitex.process.commands.MultiCommands;
import org.gramlab.core.umlv.unitex.process.commands.NormalizeCommand;
import org.gramlab.core.umlv.unitex.process.commands.StatsCommand;
import org.gramlab.core.umlv.unitex.process.commands.TokenizeCommand;
import org.gramlab.core.umlv.unitex.project.manager.UnitexProjectManager;

/**
 * This class describes a frame in which the user can select how to use the
 * results of a pattern matching.
 * 
 * @author Sébastien Paumier
 */
public class ConcordanceParameterFrame extends JInternalFrame {
	private final JTextField leftChars = new JTextField("40");
	private final JTextField rightChars = new JTextField("55");
	private final JCheckBox leftCtxStopAtEOS = new JCheckBox("", false);
	private final JCheckBox rightCtxStopAtEOS = new JCheckBox("", false);
	private JComboBox sortBox;
	final JCheckBox openWithBrowser = new JCheckBox(
			"Use a web browser to view the concordance");
	final JTextField modifiedTxtFile = new JTextField("");
	final JTextField extractFile = new JTextField("");
	boolean useWebBrowser;
	private JButton diffButton;
	private JButton ambiguousOutputsButton;
	private final JRadioButton mode0 = new JRadioButton(
			"collocates by z-score", true);
	final JRadioButton mode1 = new JRadioButton("collocates by frequency",
			false);
	final JRadioButton mode2 = new JRadioButton("contexts by frequency", false);
	final JTextField leftContextForStats = new JTextField("1");
	final JTextField rightContextForStats = new JTextField("1");
	final JRadioButton caseSensitive = new JRadioButton("case sensitive", true);
	private final JRadioButton caseInsensitive = new JRadioButton(
			"case insensitive", false);

	/**
	 * Constructs a new <code>ConcordanceParameterFrame</code>.
	 */
	ConcordanceParameterFrame() {
		super("Located sequences...", true, true);
		setContentPane(constructPanel());
		pack();
		useWebBrowser = (ConfigManager.getManager().getHtmlViewer(null) != null);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	/**
     *
     */
	void updateDiffButton() {
		final File f = new File(Config.getCurrentSntDir(),
				"previous-concord.ind");
		diffButton.setEnabled(f.exists());
	}

	private JTabbedPane constructPanel() {
		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Concordance", constructConcordancePanel());
		tabbedPane.addTab("Statistics", constructStatisticsPanel());
		KeyUtil.addCloseFrameListener(tabbedPane);
		return tabbedPane;
	}

	private Component constructStatisticsPanel() {
		final JPanel box = new JPanel(new GridBagLayout());
		final GridBagConstraints g = new GridBagConstraints();
		g.gridwidth = GridBagConstraints.REMAINDER;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		final JPanel panel1 = new JPanel(new GridLayout(3, 1));
		panel1.setBorder(BorderFactory.createTitledBorder("Mode:"));
		final ButtonGroup b1 = new ButtonGroup();
		b1.add(mode2);
		b1.add(mode1);
		b1.add(mode0);
		panel1.add(mode0);
		panel1.add(mode1);
		panel1.add(mode2);
		box.add(panel1, g);
		final JPanel panel2 = new JPanel(new GridBagLayout());
		panel2.setBorder(BorderFactory
				.createTitledBorder("Sizes of contexts in non space tokens:"));
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel2.add(new JLabel(" Left: "), gbc);
		gbc.weightx = 1;
		panel2.add(leftContextForStats, gbc);
		gbc.weightx = 0;
		panel2.add(new JLabel("  Right: "), gbc);
		gbc.weightx = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		panel2.add(rightContextForStats, gbc);
		box.add(panel2, g);
		final JPanel panel3 = new JPanel(new GridLayout(2, 1));
		panel3.setBorder(BorderFactory.createTitledBorder("Case sensitivity:"));
		final ButtonGroup b2 = new ButtonGroup();
		b2.add(caseSensitive);
		b2.add(caseInsensitive);
		panel3.add(caseSensitive);
		panel3.add(caseInsensitive);
		box.add(panel3, g);
		/* A kind of glue */
		g.weighty = 1;
		box.add(new JPanel(new BorderLayout()), g);
		g.weighty = 0;
		final JButton button = new JButton("Compute statistics");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final File indFile = new File(Config.getCurrentSntDir(),
						"concord.ind");
				if (!indFile.exists()) {
					JOptionPane.showMessageDialog(null, "Cannot find "
							+ indFile.getAbsolutePath(), "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				int leftContext;
				int rightContext;
				try {
					leftContext = Integer.parseInt(leftContextForStats
							.getText());
				} catch (final NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"You must specify a valid left context length",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					rightContext = Integer.parseInt(rightContextForStats
							.getText());
				} catch (final NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"You must specify a valid right context length",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				final File output = new File(indFile.getParentFile(),
						"statistics.txt");
				StatsCommand cmd = new StatsCommand();
				cmd = cmd.concord(indFile)
						.alphabet(ConfigManager.getManager().getAlphabet(null))
						.left(leftContext).right(rightContext).output(output)
						.caseSensitive(caseSensitive.isSelected());
				int mode;
				if (mode2.isSelected())
					mode = 0;
				else if (mode1.isSelected())
					mode = 1;
				else
					mode = 2;
				cmd = cmd.mode(mode);
				final MultiCommands commands = new MultiCommands();
				commands.addCommand(cmd);
				setVisible(false);
				Launcher.exec(commands, true,
						new LoadStatisticsDo(output, mode));
			}
		});
		final Box b = new Box(BoxLayout.X_AXIS);
		b.add(Box.createHorizontalGlue());
		b.add(button);
		b.add(Box.createHorizontalStrut(10));
		box.add(b, g);
		box.add(new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(10, 10);
			}
		}, g);
		return box;
	}

	private JPanel constructConcordancePanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		final JPanel up = new JPanel(new BorderLayout());
		up.add(constructUpPanel(), BorderLayout.NORTH);
		up.add(constructExtractPanel(), BorderLayout.CENTER);
		up.add(constructMiddlePanel(), BorderLayout.SOUTH);
		panel.add(up, BorderLayout.NORTH);
		panel.add(constructDiffPanel(), BorderLayout.CENTER);
		panel.add(constructDownPanel(), BorderLayout.SOUTH);
		return panel;
	}

	private JPanel constructExtractPanel() {
		final JPanel extract = new JPanel(new GridLayout(2, 1));
		extract.setBorder(new TitledBorder("Extract units"));
		final JPanel a = new JPanel(new BorderLayout());
		a.add(extractFile, BorderLayout.CENTER);
		final JPanel b = new JPanel(new GridLayout(1, 2));
		final Action setAction = new AbstractAction("Set File: ") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser chooser = new JFileChooser();
				chooser.addChoosableFileFilter(new PersonalFileFilter("txt",
						"Unicode Raw Texts"));
				chooser.setDialogType(JFileChooser.SAVE_DIALOG);
				chooser.setCurrentDirectory(Config.getCurrentCorpusDir());
				final int returnVal = chooser.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				extractFile
						.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		};
		final JButton setSntFile = new JButton(setAction);
		a.add(setSntFile, BorderLayout.WEST);
		final Action matchingAction = new AbstractAction(
				"Extract matching units") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				extractUnits(true);
			}
		};
		final JButton matching = new JButton(matchingAction);
		final Action unmatchingAction = new AbstractAction(
				"Extract unmatching units") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				extractUnits(false);
			}
		};
		final JButton unmatching = new JButton(unmatchingAction);
		b.add(matching);
		b.add(unmatching);
		extract.add(a);
		extract.add(b);
		return extract;
	}

	private JPanel constructDiffPanel() {
		final JPanel panel = new JPanel(new GridLayout(2, 1));
		panel.setBorder(new TitledBorder(""));
		final Action diffAction = new AbstractAction(
				"Show differences with previous concordance") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buildDiffConcordance();
			}
		};
		diffButton = new JButton(diffAction);
		panel.add(diffButton);
		final Action ambiguous = new AbstractAction("Show ambiguous outputs") {
			@Override
			public void actionPerformed(ActionEvent e) {
				buildConcordance(true);
			}
		};
		ambiguousOutputsButton = new JButton(ambiguous);
		panel.add(ambiguousOutputsButton);
		return panel;
	}

	private JPanel constructUpPanel() {
		final JPanel upPanel = new JPanel(new GridLayout(2, 1));
		upPanel.setBorder(new TitledBorder("Modify text"));
		final JPanel a = new JPanel(new BorderLayout());
		a.add(new JLabel(" Resulting .txt file: "), BorderLayout.WEST);
		a.add(modifiedTxtFile, BorderLayout.CENTER);
		final JPanel b = new JPanel(new GridLayout(1, 2));
		final Action setAction = new AbstractAction("Set File") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser chooser = new JFileChooser();
				chooser.addChoosableFileFilter(new PersonalFileFilter("txt",
						"Unicode Raw Texts"));
				chooser.setDialogType(JFileChooser.SAVE_DIALOG);
				chooser.setCurrentDirectory(Config.getCurrentCorpusDir());
				final int returnVal = chooser.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				final File snt = Config.getCurrentSnt();
				String txt = snt.getAbsolutePath();
				if (!txt.endsWith(".snt")) {
					txt = null;
				} else {
					txt = txt.substring(0, txt.lastIndexOf('.') + 1);
					txt = txt + "txt";
				}
				if (chooser.getSelectedFile().getAbsolutePath().equals(txt)) {
					JOptionPane.showMessageDialog(null,
							"You are about to replace your existing .txt file: "
									+ txt, "Warning",
							JOptionPane.WARNING_MESSAGE);
				}
				modifiedTxtFile.setText(chooser.getSelectedFile()
						.getAbsolutePath());
			}
		};
		final JButton setModifiedTextFile = new JButton(setAction);
		b.add(setModifiedTextFile);
		final Action goAction = new AbstractAction("GO") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				modifyText();
			}
		};
		final JButton GO = new JButton(goAction);
		b.add(GO);
		upPanel.add(a);
		upPanel.add(b);
		return upPanel;
	}

	private JPanel constructMiddlePanel() {
		final JPanel middlePanel = new JPanel(new BorderLayout());
		middlePanel.setBorder(new TitledBorder("Concordance presentation"));
		openWithBrowser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				useWebBrowser = openWithBrowser.isSelected();
			}
		});
		middlePanel.add(openWithBrowser, BorderLayout.CENTER);
		return middlePanel;
	}

	private JPanel constructDownPanel() {
		final JPanel downPanel = new JPanel(new GridLayout(1, 2));
		downPanel.setBorder(new TitledBorder(
				"Show matching sequences in context"));
		final JPanel ctxLengthCol = new JPanel(new BorderLayout());
		ctxLengthCol.add(new JLabel("Context length:"), BorderLayout.NORTH);
		final JPanel a = new JPanel(new GridLayout(2, 1));
		a.add(new JLabel("Left "));
		a.add(new JLabel("Right "));
		final JPanel b = new JPanel(new GridLayout(2, 1));
		leftChars.setPreferredSize(new Dimension(30, 20));
		rightChars.setPreferredSize(new Dimension(30, 20));
		b.add(leftChars);
		b.add(rightChars);
		final JPanel c = new JPanel(new GridLayout(2, 1));
		c.add(new JLabel(" chars "));
		c.add(new JLabel(" chars "));
		ctxLengthCol.add(a, BorderLayout.WEST);
		ctxLengthCol.add(b, BorderLayout.CENTER);
		ctxLengthCol.add(c, BorderLayout.EAST);
		final JPanel stopAtEosCol = new JPanel(new BorderLayout());
		stopAtEosCol.add(new JLabel("Stop at:"), BorderLayout.NORTH);
		final JPanel s = new JPanel(new GridLayout(2, 2));
		s.add(leftCtxStopAtEOS);
		s.add(new JLabel("{S}"));
		s.add(rightCtxStopAtEOS);
		s.add(new JLabel("{S}"));
		stopAtEosCol.add(s, BorderLayout.CENTER);
		final JPanel tmp_left = new JPanel();
		tmp_left.add(ctxLengthCol);
		tmp_left.add(stopAtEosCol);
		downPanel.add(tmp_left, BorderLayout.WEST);
		final JPanel sortAccTo = new JPanel(new GridLayout(2, 1));
		sortAccTo.add(new JLabel("Sort according to:"));
		final String[] items = new String[7];
		items[0] = "Text Order";
		items[1] = "Left, Center";
		items[2] = "Left, Right";
		items[3] = "Center, Left";
		items[4] = "Center, Right";
		items[5] = "Right, Left";
		items[6] = "Right, Center";
		sortBox = new JComboBox(items);
		sortBox.setSelectedIndex(3);
		sortAccTo.add(sortBox);
		final JPanel tmp_right = new JPanel(new GridLayout(2, 1, 0, 5));
		tmp_right.add(sortAccTo);
		final Action buildAction = new AbstractAction("Build concordance") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buildConcordance(false);
			}
		};
		final JButton buildConcordance = new JButton(buildAction);
		tmp_right.add(buildConcordance);
		downPanel.add(tmp_right, BorderLayout.EAST);
		return downPanel;
	}

	void modifyText() {
		if (modifiedTxtFile.getText().equals("")) {
			JOptionPane.showMessageDialog(null, "You must specify a text file",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		File txt;
		if (-1 == modifiedTxtFile.getText().indexOf(File.separatorChar)) {
			/*
			 * If the text field contains a file name without path, we append
			 * the corpus path
			 */
			txt = new File(Config.getCurrentCorpusDir(),
					modifiedTxtFile.getText());
		} else {
			txt = new File(modifiedTxtFile.getText());
		}
		ConcordCommand modifyCommand = new ConcordCommand();
		final File indFile = new File(Config.getCurrentSntDir(), "concord.ind");
		if (!indFile.exists()) {
			JOptionPane.showMessageDialog(null,
					"Cannot find " + indFile.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		modifyCommand = modifyCommand.indFile(indFile).outputModifiedTxtFile(
				txt);
		final String sntDir = FileUtil.getFileNameWithoutExtension(txt
				.getAbsolutePath()) + "_snt";
		final File tmp = new File(sntDir);
		ModifyTextDo toDo = null;
		final String sntName = FileUtil.getFileNameWithoutExtension(txt)
				+ ".snt";
		if (new File(sntName).equals(Config.getCurrentSnt())) {
			UnitexProjectManager.search(null)
					.getFrameManagerAs(InternalFrameManager.class).closeTextFrame();
			toDo = new ModifyTextDo(new File(sntName));
		}
		final MultiCommands commands = new MultiCommands();
		commands.addCommand(modifyCommand);
		final NormalizeCommand normalizeCmd = new NormalizeCommand()
				.textWithDefaultNormalization(txt);
		commands.addCommand(normalizeCmd);
		final MkdirCommand mkdir = new MkdirCommand().name(tmp);
		commands.addCommand(mkdir);
		TokenizeCommand tokenizeCmd = new TokenizeCommand().text(txt).alphabet(
				ConfigManager.getManager().getAlphabet(null));
		if (ConfigManager.getManager().isCharByCharLanguage(null)) {
			tokenizeCmd = tokenizeCmd.tokenizeCharByChar();
		}
		commands.addCommand(tokenizeCmd);
		setVisible(false);
		Launcher.exec(commands, true, toDo);
	}

	void extractUnits(boolean matching) {
		if (extractFile.getText().equals("")) {
			JOptionPane.showMessageDialog(null, "You must specify a text file",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		ExtractCommand command = new ExtractCommand().extract(matching);
		final File indFile = new File(Config.getCurrentSntDir(), "concord.ind");
		if (!indFile.exists()) {
			JOptionPane.showMessageDialog(null,
					"Cannot find " + indFile.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		final File result = new File(extractFile.getText());
		command = command.snt(Config.getCurrentSnt()).ind(indFile)
				.result(result);
		final MultiCommands builder = new MultiCommands();
		final String sntDir = FileUtil.getFileNameWithoutExtension(result
				.getAbsolutePath()) + "_snt";
		final File tmp = new File(sntDir);
		if (!tmp.exists()) {
			final MkdirCommand c = new MkdirCommand().name(tmp);
			builder.addCommand(c);
		}
		setVisible(false);
		builder.addCommand(command);
		Launcher.exec(builder, true, null);
	}

	void buildConcordance(boolean onlyAmbiguous) {
		int leftContext;
		int rightContext;
		try {
			leftContext = Integer.parseInt(leftChars.getText());
		} catch (final NumberFormatException e) {
			JOptionPane.showMessageDialog(null,
					"You must specify a valid left context length", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			rightContext = Integer.parseInt(rightChars.getText());
		} catch (final NumberFormatException e) {
			JOptionPane.showMessageDialog(null,
					"You must specify a valid right context length", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		final File indFile = new File(Config.getCurrentSntDir(), "concord.ind");
		if (!indFile.exists()) {
			JOptionPane.showMessageDialog(null,
					"Cannot find " + indFile.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		ConcordCommand command;
		try {
			command = new ConcordCommand()
					.indFile(indFile)
					.font(ConfigManager.getManager().getConcordanceFontName(
							null))
					.fontSize(
							ConfigManager.getManager().getConcordanceFontSize(
									null))
					.left(leftContext, leftCtxStopAtEOS.isSelected())
					.right(rightContext, rightCtxStopAtEOS.isSelected()).html()
					.sortAlphabet()
					.thai(ConfigManager.getManager().isThai(null));
			if (onlyAmbiguous) {
				command = command.onlyAmbiguous();
			} else {
				command = command.order(sortBox.getSelectedIndex());
			}
			if (ConfigManager.getManager().isPRLGLanguage(null)) {
				final File prlgIndex = new File(Config.getCurrentSntDir(),
						"prlg.idx");
				final File offsets = new File(Config.getCurrentSntDir(),
						"tokenize.out.offsets");
				if (prlgIndex.exists() && offsets.exists()) {
					command = command.PRLG(prlgIndex, offsets);
				}
			}
		} catch (final InvalidConcordanceOrderException e) {
			e.printStackTrace();
			return;
		}
		setVisible(false);
		int width = leftContext + rightContext;
		if (width < 40) {
			width = 40;
		}
		UnitexProjectManager.search(null)
				.getFrameManagerAs(InternalFrameManager.class).closeConcordanceFrame();
		Launcher.exec(command, true,
				new ConcordanceDo(false, new File(Config.getCurrentSntDir(),
						"concord.html"), openWithBrowser.isSelected(), width));
	}

	void buildDiffConcordance() {
		final File prevIndFile = new File(Config.getCurrentSntDir(),
				"previous-concord.ind");
		final File indFile = new File(Config.getCurrentSntDir(), "concord.ind");
		if (!indFile.exists()) {
			JOptionPane.showMessageDialog(null,
					"Cannot find " + indFile.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		ConcorDiffCommand command;
		final File outputHtmlFile = new File(Config.getCurrentSntDir(),
				"diff.html");
		command = new ConcorDiffCommand()
				.firstIndFile(prevIndFile)
				.secondIndFile(indFile)
				.output(outputHtmlFile)
				.font(ConfigManager.getManager().getConcordanceFontName(null))
				.fontSize(
						ConfigManager.getManager().getConcordanceFontSize(null))
				.diffOnly();
		setVisible(false);
		final int width = 160;
		UnitexProjectManager.search(null)
				.getFrameManagerAs(InternalFrameManager.class).closeConcordanceDiffFrame();
		Launcher.exec(command, true, new ConcordanceDo(true, outputHtmlFile,
				openWithBrowser.isSelected(), width));
	}

	class ConcordanceDo implements ToDo {
		final File htmlFile;
		final boolean browser;
		final int widthInChars;
		final boolean diff;
		final File htmlViewer;

		public ConcordanceDo(File page) {
			htmlFile = page;
			browser = false;
			widthInChars = 95;
			diff = false;
			htmlViewer = ConfigManager.getManager().getHtmlViewer(null);
		}

		public ConcordanceDo(boolean diff, File page, boolean br, int width) {
			htmlFile = page;
			browser = br;
			widthInChars = width;
			this.diff = diff;
			htmlViewer = ConfigManager.getManager().getHtmlViewer(null);
		}

		@Override
		public void toDo(boolean success) {
			if (browser && htmlViewer != null) {
				final String[] s = new String[2];
				s[0] = htmlViewer.getAbsolutePath();
				s[1] = htmlFile.getAbsolutePath();
				Console.addCommand("\"" + s[0] + "\" \"" + s[1] + "\"", false,
						null);
				try {
					Runtime.getRuntime().exec(s);
				} catch (final IOException e) {
					e.printStackTrace();
				}
			} else {
				if (!diff) {
					UnitexProjectManager.search(htmlFile)
							.getFrameManagerAs(InternalFrameManager.class)
							.newConcordanceFrame(htmlFile, widthInChars);
				} else {
					UnitexProjectManager.search(htmlFile)
							.getFrameManagerAs(InternalFrameManager.class)
							.newConcordanceDiffFrame(htmlFile);
				}
			}
		}
	}

	static class ModifyTextDo implements ToDo {
		final File snt;

		public ModifyTextDo(File s) {
			snt = s;
		}

		@Override
		public void toDo(boolean success) {
			UnitexProjectManager.search(snt)
					.getFrameManagerAs(InternalFrameManager.class)
					.newTextFrame(snt, false);
		}
	}

	static class LoadStatisticsDo implements ToDo {
		final File f;
		final int mode;

		public LoadStatisticsDo(File f, int mode) {
			this.f = f;
			this.mode = mode;
		}

		@Override
		public void toDo(boolean success) {
			UnitexProjectManager.search(f)
					.getFrameManagerAs(InternalFrameManager.class)
					.newStatisticsFrame(f, mode);
		}
	}

	void reset() {
		updateDiffButton();
		updateShowAmbiguousOutputsButton();
	}

	private static final Pattern pattern = Pattern
			.compile("([0-9]+)\\.[0-9]+\\.[0-9]+ ([0-9]+)\\.[0-9]+\\.[0-9]+.*");

	private void updateShowAmbiguousOutputsButton() {
		final File f = new File(Config.getCurrentSntDir(), "concord.ind");
		ambiguousOutputsButton.setEnabled(true);
		if (!f.exists()) {
			/* Should not happen */
			System.err.println(f.getAbsolutePath() + " does not exist!");
			return;
		}
		final Scanner scanner = Encoding.getScanner(f);
		int a = -1, b = -1;
		/*
		 * Skipping the header. We look for the first line containing #[IMR],
		 * because there may be lines before it, if the concord.ind file was
		 * produced in debug mode
		 */
		while (scanner.hasNextLine()) {
			final String s = scanner.nextLine();
			if (s.equals("#I") || s.equals("#M") || s.equals("#R"))
				break;
		}
		while (scanner.hasNextLine()) {
			final String s = scanner.nextLine();
			final Matcher m = pattern.matcher(s);
			if (m.matches()) {
				/* Should always happen */
				if (a == -1) {
					a = Integer.parseInt(m.group(1));
					b = Integer.parseInt(m.group(2));
				} else {
					final int a2 = Integer.parseInt(m.group(1));
					final int b2 = Integer.parseInt(m.group(2));
					if (a2 == a && b2 == b) {
						ambiguousOutputsButton.setEnabled(true);
						scanner.close();
						return;
					}
					a = a2;
					b = b2;
				}
			}
		}
	}
}
