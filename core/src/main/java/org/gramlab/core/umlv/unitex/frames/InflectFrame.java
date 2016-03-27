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
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.MultiCommands;
import fr.umlv.unitex.process.commands.MultiFlexCommand;
import fr.umlv.unitex.process.commands.SortTxtCommand;

/**
 * This class describes a frame that allows the user to set parameters of the
 * inflection of the current open dictionary.
 * 
 * @author Sébastien Paumier
 */
public class InflectFrame extends JInternalFrame {

	private File dela;

	final JTextField directory = new JTextField("");
	private final JRadioButton allWords = new JRadioButton(
			"Allow both simple and compound words", true);
	private final JRadioButton onlySimpleWords = new JRadioButton(
			"Allow only simple words", false);
	private final JRadioButton onlyCompoundWords = new JRadioButton(
			"Allow only compound words", false);
	private final JCheckBox factorizeInflectionalCodes = new JCheckBox(
			"Sort and factorize inflectional codes", false);

	InflectFrame() {
		super("Inflection", false, true);
		setContentPane(constructPanel());
		pack();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	private JPanel constructPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(constructUpPanel(), BorderLayout.CENTER);
		panel.add(constructDownPanel(), BorderLayout.SOUTH);
		return panel;
	}

	private JPanel constructUpPanel() {
		final JPanel upPanel = new JPanel(new GridLayout(5, 1));
		upPanel.setBorder(new TitledBorder(
				"Directory where inflectional FST2 are stored: "));
		final JPanel tempPanel = new JPanel(new BorderLayout());
		directory.setPreferredSize(new Dimension(240, 25));
		final File inflectionDir = ConfigManager.getManager()
				.getInflectionDir();
		directory.setText(inflectionDir.getAbsolutePath());
		tempPanel.add(directory, BorderLayout.CENTER);
		final Action setDirectoryAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final int returnVal = Config.getInflectDialogBox(inflectionDir)
						.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				directory.setText(Config.getInflectDialogBox(inflectionDir)
						.getSelectedFile().getAbsolutePath());
			}
		};
		final JButton setDirectory = new JButton(setDirectoryAction);
		tempPanel.add(setDirectory, BorderLayout.EAST);
		upPanel.add(tempPanel);
		final ButtonGroup bg = new ButtonGroup();
		bg.add(allWords);
		bg.add(onlySimpleWords);
		bg.add(onlyCompoundWords);
		upPanel.add(allWords);
		upPanel.add(onlySimpleWords);
		upPanel.add(onlyCompoundWords);
		upPanel.add(factorizeInflectionalCodes);
		return upPanel;
	}

	private JPanel constructDownPanel() {
		final JPanel downPanel = new JPanel(new GridLayout(1, 2));
		final Action cancelAction = new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		};
		final JButton CANCEL = new JButton(cancelAction);
		final Action goAction = new AbstractAction("Inflect Dictionary") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				inflectDELA();
			}
		};
		final JButton GO = new JButton(goAction);
		downPanel.add(CANCEL);
		downPanel.add(GO);
		return downPanel;
	}

	/**
	 * Launches the inflection program <code>Inflect</code>, through the
	 * creation of a <code>ProcessInfoFrame</code> object.
	 */
	void inflectDELA() {
		final File f = dela;
		String tmp = f.getAbsolutePath();
		final int point = tmp.lastIndexOf('.');
		final int separator = tmp.lastIndexOf(File.separatorChar);
		if (separator < point) {
			tmp = tmp.substring(0, point);
		}
		tmp = tmp + "flx.dic";
		final File resultDic = new File(tmp);
		final MultiCommands cmds = new MultiCommands();
		MultiFlexCommand command = new MultiFlexCommand().delas(f)
				.result(resultDic)
				.alphabet(ConfigManager.getManager().getAlphabet(null))
				.repository().dir(new File(directory.getText()));
		if (onlySimpleWords.isSelected()) {
			command = command.onlySimpleWords();
		} else if (onlyCompoundWords.isSelected()) {
			command = command.onlyCompoundWords();
		}
		if (ConfigManager.getManager().isKorean(null)) {
			command = command.korean();
		}
		cmds.addCommand(command);
		if (factorizeInflectionalCodes.isSelected()) {
			final SortTxtCommand sort = new SortTxtCommand().file(resultDic)
					.factorizeInflectionalCodes();
			cmds.addCommand(sort);
		}
		Launcher.exec(cmds, true, new LoadDelaDo(resultDic));
	}

	class LoadDelaDo implements ToDo {
		File dela1;

		public LoadDelaDo(File dela) {
			this.dela1 = dela;
		}

		@Override
		public void toDo(boolean success) {
			GlobalProjectManager.search(dela1)
					.getFrameManagerAs(InternalFrameManager.class).newDelaFrame(dela1);
		}
	}

	public void setDela(File dela) {
		this.dela = dela;
	}
}
