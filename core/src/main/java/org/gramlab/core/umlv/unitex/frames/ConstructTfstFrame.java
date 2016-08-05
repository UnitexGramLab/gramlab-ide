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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;
import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.process.Launcher;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.process.commands.ErrorMessageCommand;
import org.gramlab.core.umlv.unitex.process.commands.Grf2Fst2Command;
import org.gramlab.core.umlv.unitex.process.commands.LocateCommand;
import org.gramlab.core.umlv.unitex.process.commands.MkdirCommand;
import org.gramlab.core.umlv.unitex.process.commands.MultiCommands;
import org.gramlab.core.umlv.unitex.process.commands.ReconstrucaoCommand;
import org.gramlab.core.umlv.unitex.process.commands.TaggerCommand;
import org.gramlab.core.umlv.unitex.process.commands.Txt2TfstCommand;

/**
 * This class describes the "Construct Text FST" frame that offers to the user
 * to build the text automaton.
 * 
 * @author Sébastien Paumier
 */
public class ConstructTfstFrame extends JInternalFrame {
	private final JCheckBox reconstrucao = new JCheckBox(
			"Build clitic normalization grammar (available only for Portuguese (Portugal))");
	private final JCheckBox normFst = new JCheckBox(
			"Apply the Normalization grammar");
	private final JCheckBox cleanFst = new JCheckBox("Clean Text FST");
	private final JCheckBox elagFst = new JCheckBox(
			"Normalize according to Elag tagset.def");
	final JTextField normGrf = new JTextField(Config.getCurrentNormGraph()
			.getAbsolutePath());
	private final JCheckBox tagger = new JCheckBox("Linearize with the Tagger");
	final JTextField tagger_data = new JTextField(
			new File(new File(Config.getUserCurrentLanguageDir(), "Dela"),
					"tagger_data_cat.bin").getAbsolutePath());

	/**
	 * Creates and shows a new <code>ConstructFstFrame</code>.
	 */
	ConstructTfstFrame() {
		super("Construct the Text FST", false);
		setContentPane(constructPanel());
		pack();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	private JPanel constructPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(constructNormalizationPanel(), BorderLayout.NORTH);
		panel.add(constructDicPanel(), BorderLayout.CENTER);
		panel.add(constructButtonsPanel(), BorderLayout.SOUTH);
		return panel;
	}

	private JPanel constructNormalizationPanel() {
		final JPanel normalizationPanel = new JPanel(new GridLayout(8, 1));
		normalizationPanel.setBorder(new TitledBorder("Normalization"));
		final boolean portuguese = Config.getCurrentLanguage().equals(
				"Portuguese (Portugal)");
		reconstrucao.setEnabled(portuguese);
		reconstrucao.setSelected(portuguese);
		cleanFst.setSelected(true);
		final boolean morphemeCase = ConfigManager.getManager().isKorean(null);
		elagFst.setSelected(false);
		if (!morphemeCase) {
			normFst.setSelected(true);
		} else {
			normFst.setSelected(false);
		}
		normalizationPanel.add(reconstrucao);
		normalizationPanel.add(normFst);
		final JCheckBox foo = new JCheckBox("");
		final JPanel norm = new JPanel(new BorderLayout());
		norm.setBorder(BorderFactory.createEmptyBorder(0,
				foo.getPreferredSize().width, 0, 0));
		norm.add(normGrf, BorderLayout.CENTER);
		final Action setAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser chooser = Config.getNormDialogBox();
				final int returnVal = chooser.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				normGrf.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		};
		final JButton setNorm = new JButton(setAction);
		norm.add(setNorm, BorderLayout.EAST);
		normalizationPanel.add(norm);
		normalizationPanel.add(cleanFst);
		normalizationPanel.add(elagFst);
		normalizationPanel.add(tagger);
		final JPanel tag = new JPanel(new BorderLayout());
		tag.setBorder(BorderFactory.createEmptyBorder(0,
				foo.getPreferredSize().width, 0, 0));
		tag.add(tagger_data, BorderLayout.CENTER);
		final Action setTaggerDataAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser chooser = Config.getTaggerDataDialogBox();
				final int returnVal = chooser.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				tagger_data
						.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		};
		final JButton setTaggerData = new JButton(setTaggerDataAction);
		tag.add(setTaggerData, BorderLayout.EAST);
		normalizationPanel.add(tag);
		return normalizationPanel;
	}

	private JPanel constructDicPanel() {
		final JPanel dicPanel = new JPanel(new GridLayout(2, 1));
		dicPanel.setBorder(new TitledBorder(
				"Use Following Dictionaries previously constructed:"));
		dicPanel.add(new JLabel(
				"The program will construct the text FST according to the DLF, DLC and tags.ind files"));
		dicPanel.add(new JLabel(
				"previously built by the Dico program for the current text."));
		return dicPanel;
	}

	private JPanel constructButtonsPanel() {
		final JPanel buttons = new JPanel(new GridLayout(1, 2));
		buttons.setBorder(new EmptyBorder(8, 8, 2, 2));
		final Action okAction = new AbstractAction("Construct FST") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				constructTfst();
			}
		};
		final Action cancelAction = new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		};
		final JButton OK = new JButton(okAction);
		final JButton CANCEL = new JButton(cancelAction);
		buttons.add(CANCEL);
		buttons.add(OK);
		return buttons;
	}

	void constructTfst() {
		final MultiCommands commands = new MultiCommands();
		final File dir = Config.getCurrentSntDir();
		if (!dir.exists()) {
			/* If the directory toto_snt does not exist, we create it */
			commands.addCommand(new MkdirCommand().name(dir));
		}
		/* Cleaning files */
		Config.cleanTfstFiles(true);
		final File graphDir = new File(Config.getUserCurrentLanguageDir(),
				"Graphs");
		final File normalizationDir = new File(graphDir, "Normalization");
		final File delaDir = new File(Config.getUnitexCurrentLanguageDir(),
				"Dela");
		final File vProSuf = new File(normalizationDir, "V-Pro-Suf.fst2");
		final File normalizePronouns = new File(normalizationDir,
				"NormalizePronouns.fst2");
		final File raizBin = new File(delaDir, "Raiz.bin");
		final File raizInf = new File(delaDir, "Raiz.inf");
		final File futuroCondicionalBin = new File(delaDir,
				"FuturoCondicional.bin");
		final File futuroCondicionalInf = new File(delaDir,
				"FuturoCondicional.inf");
		if (normFst.isSelected() && reconstrucao.isSelected()
				&& vProSuf.exists() && normalizePronouns.exists()
				&& raizBin.exists() && futuroCondicionalBin.exists()
				&& raizInf.exists() && futuroCondicionalInf.exists()) {
			// if the user has chosen both to build the clitic
			// normalization grammar
			// and to apply this grammar, and if the necessary
			// files for the
			// Reconstrucao program exist, we launch the
			// construction of this grammar
			LocateCommand locateCmd = new LocateCommand()
					.snt(Config.getCurrentSnt()).fst2(vProSuf)
					.alphabet(ConfigManager.getManager().getAlphabet(null))
					.longestMatches().mergeOutputs().noLimit();
			if (ConfigManager.getManager().isArabic(null)) {
				locateCmd = locateCmd.arabic(new File(Config
						.getUserCurrentLanguageDir(), "arabic_typo_rules.txt"));
			}
			if (ConfigManager.getManager().isKorean(null)) {
				/*
				 * Reconstrucao should not be used for Korean, but one never
				 * knows...
				 */
				locateCmd = locateCmd.korean();
			}
			commands.addCommand(locateCmd);
			final ReconstrucaoCommand reconstrucaoCmd = new ReconstrucaoCommand()
					.alphabet(ConfigManager.getManager().getAlphabet(null))
					.ind(new File(Config.getCurrentSntDir(), "concord.ind"))
					.rootDic(raizBin).dic(futuroCondicionalBin)
					.fst2(normalizePronouns)
					.nasalFst2(new File(graphDir, "NasalSuffixPronouns.fst2"))
					.output(new File(normalizationDir, "Norm.grf"));
			commands.addCommand(reconstrucaoCmd);
			final File grf = new File(normalizationDir, "Norm.grf");
			final Grf2Fst2Command grfCommand = new Grf2Fst2Command().grf(grf)
					.tokenizationMode(null, grf).repositories()
					.emitEmptyGraphWarning().displayGraphNames();
			commands.addCommand(grfCommand);
		}
		Txt2TfstCommand txtCmd = new Txt2TfstCommand()
				.text(Config.getCurrentSnt())
				.alphabet(ConfigManager.getManager().getAlphabet(null))
				.clean(cleanFst.isSelected());
		if (ConfigManager.getManager().isKorean(null)) {
			txtCmd = txtCmd.korean();
		}
		File normFile = null;
		File normGrfFile = null;
		if (normFst.isSelected()) {
			final String grfName = normGrf.getText();
			if (grfName.substring(grfName.length() - 3, grfName.length())
					.equalsIgnoreCase("grf")) {
				/* We must compile the grf */
				normGrfFile = new File(grfName);
				final Grf2Fst2Command grfCmd = new Grf2Fst2Command()
						.grf(normGrfFile).enableLoopAndRecursionDetection(true)
						.tokenizationMode(null, normGrfFile)
						.emitEmptyGraphWarning().displayGraphNames();
				commands.addCommand(grfCmd);
				String fst2Name = grfName.substring(0, grfName.length() - 3);
				fst2Name = fst2Name + "fst2";
				normFile = new File(fst2Name);
			} else {
				if (!(grfName.substring(grfName.length() - 4, grfName.length())
						.equalsIgnoreCase("fst2"))) {
					/* If the extension is nor .grf neither .fst2 */
					JOptionPane.showMessageDialog(null,
							"Invalid graph name extension !", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				normFile = normGrfFile = new File(grfName);
			}
			txtCmd = txtCmd.fst2(normFile);
			Config.setCurrentNormGraph(normGrfFile);
		}
		final File elag_tagset = new File(Config.getCurrentElagDir(),
				"tagset.def");
		if (elagFst.isSelected()) {
			txtCmd = txtCmd.tagset(elag_tagset);
		}
		commands.addCommand(txtCmd);
		if (tagger.isSelected()) {
			final File data = new File(tagger_data.getText());
			if (!data.exists()) {
				commands.addCommand(new ErrorMessageCommand(
						"*** WARNING: tagging skipped because tagger data file was not found ***\n"));
			} else {
				final File tfst = new File(Config.getCurrentSntDir(),
						"text.tfst");
				final TaggerCommand taggerCmd = new TaggerCommand().tfst(tfst)
						.dic(data).tagset(elag_tagset)
						.alphabet(ConfigManager.getManager().getAlphabet(null));
				commands.addCommand(taggerCmd);
			}
		}
		GlobalProjectManager.search(null)
				.getFrameManagerAs(InternalFrameManager.class).closeTextAutomatonFrame();
		GlobalProjectManager.search(null)
				.getFrameManagerAs(InternalFrameManager.class).closeTfstTagsFrame();
		/* We also have to rebuild the text automaton */
		Config.cleanTfstFiles(true);
		Launcher.exec(commands, true,
				new ConstructTfstDo(Config.getCurrentSntDir()), false);
	}

	class ConstructTfstDo implements ToDo {
		File sntDir;

		public ConstructTfstDo(File sntDir) {
			this.sntDir = sntDir;
		}

		@Override
		public void toDo(boolean success) {
			GlobalProjectManager.search(sntDir)
					.getFrameManagerAs(InternalFrameManager.class)
					.newTextAutomatonFrame(1,false);
			GlobalProjectManager.search(sntDir)
					.getFrameManagerAs(InternalFrameManager.class)
					.newTfstTagsFrame(new File(sntDir, "tfst_tags_by_freq.txt"));
		}
	}
}
