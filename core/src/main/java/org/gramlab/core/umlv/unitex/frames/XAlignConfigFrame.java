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
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gramlab.core.gramlab.util.KeyUtil;
import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.files.PersonalFileFilter;
import org.gramlab.core.umlv.unitex.process.Launcher;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.process.commands.MultiCommands;
import org.gramlab.core.umlv.unitex.process.commands.NormalizeCommand;
import org.gramlab.core.umlv.unitex.process.commands.XMLizerCommand;
import org.gramlab.core.umlv.unitex.project.manager.UnitexProjectManager;

/**
 * This class describes the XAlign parameter frame.
 * 
 * @author Sébastien Paumier
 */
public class XAlignConfigFrame extends JInternalFrame {
	XAlignConfigFrame() {
		super("XAlign", false, true);
		setContentPane(constructPanel());
		pack();
		setLocation(250, 200);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	private JFileChooser textChooser() {
		final JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new PersonalFileFilter("xml", "TEI text"));
		chooser.addChoosableFileFilter(new PersonalFileFilter("txt",
				"Raw text file"));
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setCurrentDirectory(Config.getUserDir());
		chooser.setMultiSelectionEnabled(false);
		return chooser;
	}

	public static JFileChooser alignmentChooser() {
		final JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new PersonalFileFilter("xml",
				"XAlign alignment file"));
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setCurrentDirectory(new File(Config.getUserDir(), "XAlign"));
		chooser.setMultiSelectionEnabled(false);
		return chooser;
	}

	JFileChooser saveXMLChooser() {
		final JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new PersonalFileFilter("xml", "TEI text"));
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setCurrentDirectory(Config.getUserDir());
		chooser.setMultiSelectionEnabled(false);
		return chooser;
	}

	private final JTextField text1 = new JTextField();
	private final JTextField text2 = new JTextField();
	private final JTextField alignment = new JTextField();

	private JPanel constructPanel() {
		final JPanel panel = new JPanel(new GridLayout(4, 1));
		panel.add(buildPanel("Source text", text1, textChooser()));
		panel.add(buildPanel("Target text", text2, textChooser()));
		panel.add(buildPanel("Alignment file (optional)", alignment,
				alignmentChooser()));
		panel.add(buildButtonPanel());
		return panel;
	}

	private Component buildButtonPanel() {
		final JPanel p = new JPanel(null);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		final JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadFiles();
			}
		});
		final JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		p.add(Box.createHorizontalGlue());
		p.add(ok);
		p.add(Box.createHorizontalStrut(3));
		p.add(cancel);
		p.add(Box.createHorizontalStrut(5));
		KeyUtil.addEscListener(p, cancel);
		return p;
	}

	void loadFiles() {
		final MultiCommands commands = new MultiCommands();
		String s = text1.getText();
		if ("".equals(s)) {
			JOptionPane.showMessageDialog(null, "You must set the source text",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		final File source = new File(s);
		final File xmlSourceFile;
		if (!source.exists()) {
			JOptionPane.showMessageDialog(null, "Source text not found!",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (source.getAbsolutePath().endsWith("txt")) {
			/* If the user wants to open a raw text file */
			JOptionPane
					.showMessageDialog(
							null,
							"Your source file is a .txt one. Please select the\n"
									+ "destination file to be used by XAlign (TEI format).",
							"", JOptionPane.INFORMATION_MESSAGE);
			final JFileChooser chooser = saveXMLChooser();
			final int returnVal = chooser.showSaveDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				// we return if the user has clicked on CANCEL
				return;
			}
			File xmlSource = chooser.getSelectedFile();
			if (!xmlSource.getAbsolutePath().endsWith(".xml")) {
				xmlSource = new File(xmlSource.getAbsolutePath() + ".xml");
			}
			xmlSourceFile = xmlSource;
			File alphabet = XAlignFrame.tryToFindAlphabet(source);
			if (alphabet == null) {
				alphabet = XAlignFrame.tryToFindAlphabet(xmlSource);
			}
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
			final File sentence = new File(new File(
					Config.getXAlignDirectory(), "Sentence"),
					"SentenceXAlign.fst2");
			if (!sentence.exists()) {
				JOptionPane.showMessageDialog(null,
						"Cannot find the XAlign sentence graph.\n", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			final NormalizeCommand norm = new NormalizeCommand()
					.textWithDefaultNormalization(source);
			commands.addCommand(norm);
			final String snt = FileUtil.getFileNameWithoutExtension(source)
					+ ".snt";
			final XMLizerCommand cmd = new XMLizerCommand().output(xmlSource)
					.alphabet(alphabet).sentence(sentence).input(new File(snt));
			commands.addCommand(cmd);
		} else {
			xmlSourceFile = source;
		}
		s = text2.getText();
		if ("".equals(s)) {
			JOptionPane.showMessageDialog(null, "You must set the target text",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		final File dest = new File(s);
		final File xmlTargetFile;
		if (!dest.exists()) {
			JOptionPane.showMessageDialog(null, "Target text not found!",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (dest.getAbsolutePath().endsWith("txt")) {
			/* If the user wants to open a raw text file */
			JOptionPane
					.showMessageDialog(
							null,
							"Your target file is a .txt one. Please select the\n"
									+ "destination file to be used by XAlign (TEI format).",
							"", JOptionPane.INFORMATION_MESSAGE);
			final JFileChooser chooser = saveXMLChooser();
			final int returnVal = chooser.showSaveDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				// we return if the user has clicked on CANCEL
				return;
			}
			File xmlTarget = chooser.getSelectedFile();
			xmlTargetFile = xmlTarget;
			if (!xmlTarget.getAbsolutePath().endsWith(".xml")) {
				xmlTarget = new File(xmlTarget.getAbsolutePath() + ".xml");
			}
			File alphabet = XAlignFrame.tryToFindAlphabet(dest);
			if (alphabet == null) {
				alphabet = XAlignFrame.tryToFindAlphabet(xmlTarget);
			}
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
			final File sentence = new File(new File(
					Config.getXAlignDirectory(), "Sentence"),
					"SentenceXAlign.fst2");
			if (!sentence.exists()) {
				JOptionPane.showMessageDialog(null,
						"Cannot find the XAlign sentence graph.\n", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			final NormalizeCommand norm = new NormalizeCommand()
					.textWithDefaultNormalization(dest);
			commands.addCommand(norm);
			final String snt = FileUtil.getFileNameWithoutExtension(dest)
					+ ".snt";
			final XMLizerCommand cmd = new XMLizerCommand().output(xmlTarget)
					.alphabet(alphabet).sentence(sentence).input(new File(snt));
			commands.addCommand(cmd);
		} else {
			xmlTargetFile = dest;
		}
		File alignmentFile = null;
		s = alignment.getText();
		if (!"".equals(s)) {
			alignmentFile = new File(s);
			if (!alignmentFile.exists()) {
				JOptionPane.showMessageDialog(null,
						"Alignment file not found!", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		final File alignmentFile2 = alignmentFile;
		/* We close the parameter frame */
		setVisible(false);
		UnitexProjectManager.search(null)
				.getFrameManagerAs(UnitexInternalFrameManager.class).closeXAlignFrame();
		final ToDo toDo = new ToDo() {
			@Override
			public void toDo(boolean success) {
				UnitexProjectManager.search(null)
						.getFrameManagerAs(UnitexInternalFrameManager.class)
						.newXAlignFrame(xmlSourceFile, xmlTargetFile, alignmentFile2);
			}
		};
		/* And we launch the XMLizer commands, if any */
		if (commands.numberOfCommands() != 0) {
			Launcher.exec(commands, true, toDo, true);
		} else {
			toDo.toDo(true);
		}
	}

	private JPanel buildPanel(String s, final JTextField text,
			final JFileChooser chooser) {
		final JPanel p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createTitledBorder(s));
		text.setPreferredSize(new Dimension(300, 25));
		p.add(text, BorderLayout.CENTER);
		final JButton button = new JButton("set");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final int returnVal = chooser.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				text.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		});
		p.add(button, BorderLayout.EAST);
		return p;
	}
}
