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

import org.gramlab.core.umlv.unitex.GraphCollection;
import org.gramlab.core.umlv.unitex.config.Config;

/**
 * This class provides a frame that allows the user to select a source directory
 * and a destination graph name, for building a graph collection.
 * 
 * @author Sébastien Paumier
 */
public class GraphCollectionFrame extends JInternalFrame {
	final JTextField srcDir = new JTextField();
	final JTextField resultGrf = new JTextField();

	/**
	 * Constructs a new <code>GraphCollectionDialog</code>.
	 */
	GraphCollectionFrame() {
		super("Building Graph Collection", false, true, false);
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
		final JPanel upPanel = new JPanel(new GridLayout(2, 1));
		upPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		srcDir.setPreferredSize(new Dimension(280, 20));
		resultGrf.setPreferredSize(new Dimension(280, 20));
		final Action setSrcAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser f = new JFileChooser();
				f.setDialogTitle("Choose source directory");
				f.setCurrentDirectory(Config.getGraphDialogBox(false)
						.getCurrentDirectory());
				f.setDialogType(JFileChooser.OPEN_DIALOG);
				f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
					return;
				srcDir.setText(f.getSelectedFile().getAbsolutePath());
			}
		};
		final JButton setSrcDir = new JButton(setSrcAction);
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
				final File file = dialogBox.getSelectedFile();
				if (file == null) {
					return;
				}
				String s = file.getAbsolutePath();
				if (!s.endsWith(".grf"))
					s = s + ".grf";
				resultGrf.setText(s);
			}
		};
		final JButton setResultGrf = new JButton(setResultAction);
		final JPanel a = createPanel(new JLabel("Source directory:"), srcDir,
				setSrcDir);
		final JPanel b = createPanel(new JLabel("Resulting GRF grammar:"),
				resultGrf, setResultGrf);
		upPanel.add(a);
		upPanel.add(b);
		return upPanel;
	}

	private JPanel constructDownPanel() {
		final JPanel downPanel = new JPanel(new GridLayout(1, 2));
		final Action okAction = new AbstractAction("OK") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				GraphCollection.build(new File(srcDir.getText()), new File(
						resultGrf.getText()), true);
			}
		};
		final JButton OK = new JButton(okAction);
		final Action cancelAction = new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		};
		final JButton CANCEL = new JButton(cancelAction);
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
}
