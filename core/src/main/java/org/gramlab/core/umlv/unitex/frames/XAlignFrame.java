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
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;
import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.listeners.AlignmentListener;
import org.gramlab.core.umlv.unitex.process.Launcher;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.process.commands.DicoCommand;
import org.gramlab.core.umlv.unitex.process.commands.MkdirCommand;
import org.gramlab.core.umlv.unitex.process.commands.MultiCommands;
import org.gramlab.core.umlv.unitex.process.commands.NormalizeCommand;
import org.gramlab.core.umlv.unitex.process.commands.TEI2TxtCommand;
import org.gramlab.core.umlv.unitex.process.commands.TokenizeCommand;
import org.gramlab.core.umlv.unitex.process.commands.XAlignCommand;
import org.gramlab.core.umlv.unitex.xalign.AlignmentEvent;
import org.gramlab.core.umlv.unitex.xalign.ConcordanceModel;
import org.gramlab.core.umlv.unitex.xalign.ConcordanceModelImpl;
import org.gramlab.core.umlv.unitex.xalign.DisplayMode;
import org.gramlab.core.umlv.unitex.xalign.XAlignModel;
import org.gramlab.core.umlv.unitex.xalign.XAlignModelImpl;
import org.gramlab.core.umlv.unitex.xalign.XAlignPane;
import org.gramlab.core.umlv.unitex.xalign.XMLTextLoader;
import org.gramlab.core.umlv.unitex.xalign.XMLTextModel;
import org.gramlab.core.umlv.unitex.xalign.XMLTextModelImpl;

public class XAlignFrame extends JInternalFrame {
	Font sourceFont;
	Font targetFont;
	File alignementFile;
	final XMLTextModel text1;
	final XMLTextModel text2;
	final XAlignModel model;
	final ConcordanceModel concordModel1;
	final ConcordanceModel concordModel2;

	XAlignFrame(final File f1, final File f2, final File align)
			throws IOException {
		super("XAlign", true, true);
		{
			final JTextPane foo = new JTextPane();
			sourceFont = foo.getFont();
			targetFont = foo.getFont();
		}
		alignementFile = align;
		tryToFindFonts(f1, f2);
		setSize(800, 600);
		/* First text */
		final MappedByteBuffer buffer1 = XMLTextLoader
				.buildMappedByteBuffer(f1);
		final MappedByteBuffer buffer2 = XMLTextLoader
				.buildMappedByteBuffer(f2);
		text1 = new XMLTextModelImpl(buffer1);
		text2 = new XMLTextModelImpl(buffer2);
		final XMLTextLoader loader1 = new XMLTextLoader(text1, buffer1);
		loader1.load();
		final XMLTextLoader loader2 = new XMLTextLoader(text2, buffer2);
		loader2.load();
		model = new XAlignModelImpl(text1, text2);
		concordModel1 = new ConcordanceModelImpl(text1, true, model);
		concordModel2 = new ConcordanceModelImpl(text2, false, model);
		model.load(align);
		model.addAlignmentListener(new AlignmentListener() {
			@Override
			public void alignmentChanged(AlignmentEvent e) {
				if (AlignmentEvent.MANUAL_EDIT.equals(e)) {
					setTitle(((alignementFile != null) ? (alignementFile
							.getAbsolutePath() + " (") : "(alignment ")
							+ "modified)");
				} else if (AlignmentEvent.SAVING.equals(e)) {
					setTitle((alignementFile != null) ? alignementFile
							.getAbsolutePath() : "XAlign");
				}
			}
		});
		final JInternalFrame frame = this;
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				if (model.isModified()) {
					final Object[] options_on_exit = { "Save", "Don't save" };
					final Object[] normal_options = { "Save", "Don't save",
							"Cancel" };
					int n;
					if (UnitexFrame.closing) {
						n = JOptionPane
								.showOptionDialog(
										frame,
										"Alignment has been modified. Do you want to save it ?",
										"", JOptionPane.YES_NO_CANCEL_OPTION,
										JOptionPane.QUESTION_MESSAGE, null,
										options_on_exit, options_on_exit[0]);
					} else {
						n = JOptionPane
								.showOptionDialog(
										frame,
										"Alignment has been modified. Do you want to save it ?",
										"", JOptionPane.YES_NO_CANCEL_OPTION,
										JOptionPane.QUESTION_MESSAGE, null,
										normal_options, normal_options[0]);
					}
					if (n == JOptionPane.CLOSED_OPTION)
						return;
					if (n == 0) {
						saveAlignment(model);
					}
					if (n != 2) {
						text1.reset();
						text2.reset();
						model.reset();
						frame.dispose();
						return;
					}
					return;
				}
				text1.reset();
				text2.reset();
				model.reset();
				frame.dispose();
			}

			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				GlobalProjectManager.search(null)
						.getFrameManagerAs(UnitexInternalFrameManager.class).closeXAlignLocateFrame();
			}
		});
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(
				new XAlignPane(concordModel1, concordModel2, model, sourceFont,
						targetFont), BorderLayout.CENTER);
		final JPanel radioPanel1 = createRadioPanel(concordModel1,
				concordModel2, true);
		final JPanel radioPanel2 = createRadioPanel(concordModel2,
				concordModel1, false);
		final JButton clearButton = new JButton("Clear alignment");
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final int choice = JOptionPane
						.showConfirmDialog(
								null,
								"Are you sure that you want to clear the current alignement ?",
								"Clear alignment ?", JOptionPane.YES_NO_OPTION);
				if (choice != JOptionPane.YES_OPTION) {
					return;
				}
				model.clear();
			}
		});
		final JButton alignButton = new JButton("Align");
		alignButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAlignment(model);
				if (alignementFile == null || model.isModified()) {
					/* If the user hasn't saved the alignment */
					return;
				}
				final File alignmentProperties = Config
						.getAlignmentProperties();
				XAlignCommand cmd = new XAlignCommand();
				cmd = cmd.source(f1).target(f2).properties(alignmentProperties)
						.alignment(alignementFile);
				Launcher.exec(cmd, true, new XAlignDo(model, alignementFile));
			}
		});
		final JButton saveButton = new JButton("Save alignment");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAlignment(model);
			}
		});
		final JButton saveAsButton = new JButton("Save alignment as...");
		saveAsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAlignmentAs(model);
			}
		});
		final JPanel downPanel = new JPanel(new BorderLayout());
		downPanel.add(radioPanel1, BorderLayout.WEST);
		downPanel.add(radioPanel2, BorderLayout.EAST);
		final JButton locate1 = createLocateButton(f1, concordModel1);
		final JButton locate2 = createLocateButton(f2, concordModel2);
		final JPanel buttonPanel = new JPanel(null);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		buttonPanel.add(locate1);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(clearButton);
		buttonPanel.add(alignButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(saveAsButton);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(locate2);
		downPanel.add(buttonPanel, BorderLayout.SOUTH);
		downPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		getContentPane().add(downPanel, BorderLayout.SOUTH);
	}

	private JButton createLocateButton(final File file,
			final ConcordanceModel concordModel) {
		final JButton button = new JButton("Locate...");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				launchLocate(file, concordModel);
			}
		});
		return button;
	}

	void launchLocate(File file, final ConcordanceModel concordModel) {
		final String xmlName = file.getAbsolutePath();
		String targetName;
		if (!xmlName.endsWith(".xml")) {
			targetName = xmlName + "_xalign";
		} else {
			targetName = xmlName.substring(0, xmlName.lastIndexOf("."))
					+ "_xalign";
		}
		final String txtName = targetName + ".txt";
		final String sntName = targetName + ".snt";
		final File txt = new File(txtName);
		final File snt = new File(sntName);
		final File sntDir = new File(targetName + "_snt");
		final File alphabet = tryToFindAlphabet(file);
		if (alphabet == null) {
			JOptionPane
					.showMessageDialog(
							null,
							"Cannot determine the alphabet file to use\n"
									+ "in order to process your text. You should place\n"
									+ "your file within a language directory (e.g. English).",
							"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		final String language = alphabet.getParentFile().getName();
		if (!snt.exists()) {
			final int choice = JOptionPane
					.showConfirmDialog(
							null,
							"Unitex needs a text version of your xml text in order to locate\n"
									+ "expression. Do you agree to build and preprocess\n\n"
									+ txtName + " ?", "",
							JOptionPane.YES_NO_OPTION);
			if (choice != JOptionPane.YES_OPTION) {
				return;
			}
			final MultiCommands commands = new MultiCommands();
			final TEI2TxtCommand tei2txt = new TEI2TxtCommand().input(file)
					.output(txt);
			commands.addCommand(tei2txt);
			final NormalizeCommand normalize = new NormalizeCommand().text(txt);
			commands.addCommand(normalize);
			final MkdirCommand mkdir = new MkdirCommand().name(sntDir);
			commands.addCommand(mkdir);
			final TokenizeCommand tokenize = new TokenizeCommand().text(snt)
					.alphabet(alphabet);
			commands.addCommand(tokenize);
			DicoCommand dico = new DicoCommand()
					.snt(snt)
					.alphabet(alphabet)
					.morphologicalDic(
							ConfigManager.getManager()
									.morphologicalDictionaries(language));
			if (ConfigManager.getManager().isArabic(language)) {
				dico = dico.arabic(new File(Config.getUserCurrentLanguageDir(),
						"arabic_typo_rules.txt"));
			}
			if (ConfigManager.getManager().isSemiticLanguage(language)) {
				dico = dico.semitic();
			}
			final ArrayList<File> param = Config.getDefaultDicList(language);
			if (param != null && param.size() > 0) {
				dico = dico.dictionaryList(param);
				commands.addCommand(dico);
			} else {
				dico = null;
			}
			final ToDo toDo = new ToDo() {
				@Override
				public void toDo(boolean success) {
					GlobalProjectManager.search(null)
							.getFrameManagerAs(UnitexInternalFrameManager.class)
							.newXAlignLocateFrame(language, snt, concordModel);
				}
			};
			Launcher.exec(commands, true, toDo, true);
			return;
		}
		GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
				.newXAlignLocateFrame(language,snt, concordModel);
	}

	void saveAlignment(XAlignModel model1) {
		if (alignementFile != null) {
			saveAlignment(alignementFile, model1);
		} else {
			saveAlignmentAs(model1);
		}
	}

	void saveAlignmentAs(XAlignModel model1) {
		final JFileChooser chooser = XAlignConfigFrame.alignmentChooser();
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		final int returnVal = chooser.showSaveDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		alignementFile = chooser.getSelectedFile();
		saveAlignment(alignementFile, model1);
	}

	private void saveAlignment(File alignementFile1, XAlignModel model1) {
		try {
			model1.dumpAlignments(alignementFile1);
			alignementFile = alignementFile1;
			setTitle(alignementFile1.getAbsolutePath());
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void tryToFindFonts(File f1, File f2) {
		Font f = tryToFindFont(f1);
		if (f != null) {
			sourceFont = f;
		}
		f = tryToFindFont(f2);
		if (f != null) {
			targetFont = f;
		}
	}

	/**
	 * This method tries to determine the language of the given file, on the
	 * basis of its path:
	 * <p/>
	 * ..../Unitex/Thai/...../foo.xml => Thai ..../my
	 * unitex/French/...../foo.xml => French
	 */
	private Font tryToFindFont(File f) {
		final File languageDir = FileUtil.getLanguageDirForFile(f);
		if (languageDir == null)
			return null;
		return ConfigManager.getManager().getTextFont(languageDir.getName());
	}

	/**
	 * This method tries to determine the language of the given file, on the
	 * basis of its path:
	 * <p/>
	 * ..../Unitex/Thai/...../foo.xml => Thai ..../my
	 * unitex/French/...../foo.xml => French
	 */
	public static File tryToFindAlphabet(File f) {
		final File languageDir = FileUtil.getLanguageDirForFile(f);
		if (languageDir == null) {
			return null;
		}
		return ConfigManager.getManager().getAlphabet(languageDir.getName());
	}

	private JPanel createRadioPanel(final ConcordanceModel model1,
			final ConcordanceModel model2, boolean left) {
		final JPanel p = new JPanel(new GridLayout(4, 1));
		final String[] captions = {
				"All sentences/Plain text",
				"Matched sentences",
				"All sentences/HTML",
				left ? "Aligned with target concordance"
						: "Aligned with source concordance" };
		final DisplayMode[] modes = { DisplayMode.TEXT, DisplayMode.MATCHES,
				DisplayMode.BOTH, DisplayMode.ALIGNED };
		final ButtonGroup g = new ButtonGroup();
		for (int i = 0; i < captions.length; i++) {
			final JRadioButton button = new JRadioButton(captions[i], i == 0);
			if (!left) {
				button.setHorizontalTextPosition(SwingConstants.LEFT);
				button.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			}
			final DisplayMode mode = modes[i];
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (button.isSelected()) {
						model1.setMode(mode, model2);
					}
				}
			});
			g.add(button);
			p.add(button);
		}
		if (!left) {
			p.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}
		return p;
	}

	class XAlignDo implements ToDo {
		final XAlignModel model1;
		final File f;

		XAlignDo(XAlignModel model, File f) {
			this.model1 = model;
			this.f = f;
		}

		@Override
		public void toDo(boolean success) {
			try {
				model1.load(f);
			} catch (final IOException e) {
				GlobalProjectManager.search(null)
						.getFrameManagerAs(UnitexInternalFrameManager.class).closeXAlignFrame();
			}
		}
	}
}
