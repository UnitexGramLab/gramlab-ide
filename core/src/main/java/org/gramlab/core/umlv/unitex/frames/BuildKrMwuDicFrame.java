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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.files.PersonalFileFilter;
import org.gramlab.core.umlv.unitex.listeners.LanguageListener;
import org.gramlab.core.umlv.unitex.process.Launcher;
import org.gramlab.core.umlv.unitex.process.commands.BuildKrMwuDicCommand;

/**
 * With this frame, the user can configure the generation of a MWU dictionary
 * graph
 * 
 * @author Sébastien Paumier
 */
public class BuildKrMwuDicFrame extends JInternalFrame {
	final JTextField mwuDic = new JTextField();
	final JTextField inflectionDir = new JTextField();
	final JTextField binaryDic = new JTextField();
	final JTextField outputName = new JTextField();

	BuildKrMwuDicFrame() {
		super("Generate MWU dictionary graph", false, true);
		setContentPane(constructPanel());
		inflectionDir.setText(new File(Config.getUserCurrentLanguageDir(),
				"Inflection").getAbsolutePath());
		Config.addLanguageListener(new LanguageListener() {
			@Override
			public void languageChanged() {
				inflectionDir.setText(new File(Config
						.getUserCurrentLanguageDir(), "Inflection")
						.getAbsolutePath());
			}
		});
		pack();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	private JPanel constructPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(constructUpPanel(), BorderLayout.NORTH);
		panel.add(constructDownPanel(), BorderLayout.CENTER);
		return panel;
	}

	private JPanel createPanel(JLabel label, JTextField textField,
			JButton button) {
		final JPanel p = new JPanel(new GridLayout(2, 1));
		p.add(label);
		final JPanel tmp = new JPanel(new BorderLayout());
		tmp.add(textField, BorderLayout.CENTER);
		tmp.add(button, BorderLayout.EAST);
		p.add(tmp);
		return p;
	}

	private JPanel constructUpPanel() {
		final JPanel upPanel = new JPanel(new GridLayout(4, 1));
		upPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		mwuDic.setPreferredSize(new Dimension(280, 20));
		inflectionDir.setPreferredSize(new Dimension(280, 20));
		binaryDic.setPreferredSize(new Dimension(280, 20));
		outputName.setPreferredSize(new Dimension(280, 20));
		final Action setMwuDicAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser dialogBox = Config.getDelaDialogBox();
				dialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
				final FileFilter[] oldFilters = dialogBox
						.getChoosableFileFilters();
				for (final FileFilter f : oldFilters) {
					dialogBox.removeChoosableFileFilter(f);
				}
				final FileFilter tmp = new PersonalFileFilter("txt",
						"Tab-separated MWU text");
				dialogBox.addChoosableFileFilter(tmp);
				final int returnVal = dialogBox.showOpenDialog(null);
				final File selected = dialogBox.getSelectedFile();
				dialogBox.removeChoosableFileFilter(tmp);
				for (final FileFilter f : oldFilters) {
					dialogBox.addChoosableFileFilter(f);
				}
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				String s = selected.getAbsolutePath();
				if (!s.endsWith(".txt"))
					s = s + ".txt";
				mwuDic.setText(s);
			}
		};
		final JButton setMwuDicName = new JButton(setMwuDicAction);
		final Action setInflectionDirAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser dialogBox = Config.getInflectDialogBox(null);
				final int returnVal = dialogBox.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				final String s = dialogBox.getSelectedFile().getAbsolutePath();
				inflectionDir.setText(s);
			}
		};
		final JButton setInflectionDirName = new JButton(setInflectionDirAction);
		final Action setBinaryDicAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser dialogBox = Config.getDelaDialogBox();
				dialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
				final FileFilter[] oldFilters = dialogBox
						.getChoosableFileFilters();
				for (final FileFilter f : oldFilters) {
					dialogBox.removeChoosableFileFilter(f);
				}
				final FileFilter tmp = new PersonalFileFilter("bin",
						"Compressed Dictionaries");
				dialogBox.addChoosableFileFilter(tmp);
				final int returnVal = dialogBox.showOpenDialog(null);
				final File selected = dialogBox.getSelectedFile();
				dialogBox.removeChoosableFileFilter(tmp);
				for (final FileFilter f : oldFilters) {
					dialogBox.addChoosableFileFilter(f);
				}
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				final String s = selected.getAbsolutePath();
				binaryDic.setText(s);
			}
		};
		final JButton setBinaryDicName = new JButton(setBinaryDicAction);
		final Action setOutputAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser dialogBox = Config.getGraphDialogBox(false);
				dialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
				final File old = dialogBox.getCurrentDirectory();
				dialogBox.setCurrentDirectory(new File(Config
						.getUserCurrentLanguageDir(), "Dela"));
				final int returnVal = dialogBox.showOpenDialog(null);
				dialogBox.setCurrentDirectory(old);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				String s = dialogBox.getSelectedFile().getAbsolutePath();
				if (!s.endsWith(".grf"))
					s = s + ".grf";
				outputName.setText(s);
			}
		};
		final JButton setOutputName = new JButton(setOutputAction);
		final JPanel a = createPanel(new JLabel(
				"MWU DELAS dictionary (in tab-separated text format):"),
				mwuDic, setMwuDicName);
		final JPanel b = createPanel(new JLabel("Inflection directory:"),
				inflectionDir, setInflectionDirName);
		final JPanel c = createPanel(new JLabel(
				"Binary simple word dictionary:"), binaryDic, setBinaryDicName);
		final JPanel d = createPanel(new JLabel("Output dictionary graph:"),
				outputName, setOutputName);
		upPanel.add(a);
		upPanel.add(b);
		upPanel.add(c);
		upPanel.add(d);
		return upPanel;
	}

	private JPanel constructDownPanel() {
		final JPanel downPanel = new JPanel(new GridLayout(1, 2));
		final Action cancelAction = new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				doDefaultCloseAction();
			}
		};
		final JButton CANCEL = new JButton(cancelAction);
		final Action okAction = new AbstractAction("Generate") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				generateMwuDic();
			}
		};
		final JButton OK = new JButton(okAction);
		final JPanel left = new JPanel(new BorderLayout());
		left.setBorder(new EmptyBorder(10, 50, 10, 20));
		left.add(CANCEL, BorderLayout.CENTER);
		final JPanel right = new JPanel(new BorderLayout());
		right.setBorder(new EmptyBorder(10, 20, 10, 50));
		right.add(OK, BorderLayout.CENTER);
		downPanel.add(left);
		downPanel.add(right);
		return downPanel;
	}

	void generateMwuDic() {
		if (mwuDic.getText().equals("")) {
			JOptionPane.showMessageDialog(null,
					"You must specify the MWU dictionary", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (inflectionDir.getText().equals("")) {
			JOptionPane.showMessageDialog(null,
					"You must specify the inflection directory", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (binaryDic.getText().equals("")) {
			JOptionPane.showMessageDialog(null,
					"You must specify the simple word dictionary", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (outputName.getText().equals("")) {
			JOptionPane.showMessageDialog(null,
					"You must specify the output dictionary graph", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		final BuildKrMwuDicCommand command = new BuildKrMwuDicCommand()
				.output(new File(outputName.getText()))
				.alphabet(ConfigManager.getManager().getAlphabet(null))
				.binaryDic(new File(binaryDic.getText()))
				.inflectionDir(new File(inflectionDir.getText()))
				.input(new File(mwuDic.getText()));
		setVisible(false);
		Launcher.exec(command, false, null);
	}
}
