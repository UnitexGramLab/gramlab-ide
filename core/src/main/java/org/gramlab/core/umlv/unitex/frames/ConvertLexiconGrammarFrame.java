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
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.process.Launcher;
import org.gramlab.core.umlv.unitex.process.commands.Table2GrfCommand;

/**
 * This class describes the lexicon-grammar conversion frame.
 * 
 * @author Sébastien Paumier
 */
public class ConvertLexiconGrammarFrame extends JInternalFrame {
	final JTextField grfName = new JTextField();
	final JTextField resultName = new JTextField();
	final JTextField subgraphName = new JTextField();
	private File table;

	ConvertLexiconGrammarFrame() {
		super("Compile Lexicon-Grammar to GRF", false, true);
		setContentPane(constructPanel());
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
		final JPanel upPanel = new JPanel(new GridLayout(3, 1));
		upPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		grfName.setPreferredSize(new Dimension(280, 20));
		resultName.setPreferredSize(new Dimension(280, 20));
		subgraphName.setPreferredSize(new Dimension(280, 20));
		final Action setGrfAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser dialogBox = Config.getGraphDialogBox(false);
				dialogBox.setDialogType(JFileChooser.OPEN_DIALOG);
				final int returnVal = dialogBox.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				grfName.setText(dialogBox.getSelectedFile().getAbsolutePath());
			}
		};
		final JButton setGrfName = new JButton(setGrfAction);
		final Action setResultAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser dialogBox = Config.getGraphDialogBox(false);
				dialogBox.setDialogType(JFileChooser.SAVE_DIALOG);
				final int returnVal = dialogBox.showSaveDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				String s = dialogBox.getSelectedFile().getAbsolutePath();
				if (!s.endsWith(".grf"))
					s = s + ".grf";
				resultName.setText(s);
			}
		};
		final JButton setResultName = new JButton(setResultAction);
		final Action setSubgraphAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser dialogBox = Config.getGraphDialogBox(false);
				dialogBox.setDialogType(JFileChooser.SAVE_DIALOG);
				final int returnVal = dialogBox.showSaveDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				String s = dialogBox.getSelectedFile().getAbsolutePath();
				if (!s.endsWith(".grf"))
					s = s + ".grf";
				subgraphName.setText(s);
			}
		};
		final JButton setSubgraphName = new JButton(setSubgraphAction);
		final JPanel a = createPanel(new JLabel(
				"Reference Graph (in GRF format):"), grfName, setGrfName);
		final JPanel b = createPanel(new JLabel("Resulting GRF grammar:"),
				resultName, setResultName);
		final JPanel c = createPanel(new JLabel("Name of produced subgraphs:"),
				subgraphName, setSubgraphName);
		upPanel.add(a);
		upPanel.add(b);
		upPanel.add(c);
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
		final Action okAction = new AbstractAction("Compile") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				compileLGTable();
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

	void compileLGTable() {
		Table2GrfCommand command = new Table2GrfCommand().table(table)
				.parametrizedGraph(new File(grfName.getText()))
				.resultMainGraph(new File(resultName.getText()));
		if (!resultName.getText().equals("")) {
			command = command.subgraphName(new File(subgraphName.getText()));
		}
		setVisible(false);
		Launcher.exec(command, true, null);
	}

	public void setupTable(File t) {
		if (t == null) {
			throw new IllegalArgumentException(
					"Cannot work on a null table file");
		}
		this.table = t;
	}
}
