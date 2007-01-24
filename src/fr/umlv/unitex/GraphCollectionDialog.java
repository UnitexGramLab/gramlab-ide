 /*
  * Unitex
  *
  * Copyright (C) 2001-2007 Université de Marne-la-Vallée <unitex@univ-mlv.fr>
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

package fr.umlv.unitex;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 * This class provides a dialog box that allows the user to select a source
 * directory and a destination graph name, for building a graph collection.
 * 
 * @author Sébastien Paumier
 *  
 */
public class GraphCollectionDialog extends JDialog {

	JTextField srcDir = new JTextField();
	JTextField resultGrf = new JTextField();

	/**
	 * Constructs a new <code>GraphCollectionDialog</code>.
	 */
	public GraphCollectionDialog() {
		super(UnitexFrame.mainFrame, "Building Graph Collection", true);
		setContentPane(constructPanel());
		pack();
		setResizable(false);
		showPreferences();
	}

	/**
	 * Shows the dialog box
	 *  
	 */
	public void showPreferences() {
		setLocationRelativeTo(UnitexFrame.mainFrame);
		setVisible(true);
	}

	private JPanel constructPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(true);
		panel.add(constructUpPanel(), BorderLayout.NORTH);
		panel.add(constructDownPanel(), BorderLayout.CENTER);
		return panel;
	}

	private JPanel createPanel(JLabel label, JTextField textField,
			JButton button) {
		JPanel p = new JPanel(new GridLayout(2, 1));
		p.setOpaque(true);
		p.add(label);
		JPanel tmp = new JPanel(new BorderLayout());
		tmp.setOpaque(true);
		tmp.add(textField, BorderLayout.CENTER);
		tmp.add(button, BorderLayout.EAST);
		p.add(tmp);
		return p;
	}

	private JPanel constructUpPanel() {
		JPanel upPanel = new JPanel(new GridLayout(2, 1));
		upPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		srcDir.setPreferredSize(new Dimension(280, 20));
		resultGrf.setPreferredSize(new Dimension(280, 20));
		Action setSrcAction = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JFileChooser f = new JFileChooser();
						f.setDialogTitle("Choose source directory");
						f.setCurrentDirectory(Config.getGraphDialogBox(false)
								.getCurrentDirectory());
						f.setDialogType(JFileChooser.OPEN_DIALOG);
						f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
							return;
						srcDir.setText(f.getSelectedFile().getAbsolutePath());
					}
				});
			}
		};
		JButton setSrcDir = new JButton(setSrcAction);
		Action setResultAction = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser dialogBox=Config.getGraphDialogBox(false);
				dialogBox.setDialogType(JFileChooser.SAVE_DIALOG);
				int returnVal = dialogBox.showSaveDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				File file = dialogBox.getSelectedFile();
        if (file==null) {
            return;
        }
				String s=file.getAbsolutePath();
				if (!s.endsWith(".grf"))
					s = s + ".grf";
				resultGrf.setText(s);
			}
		};
		JButton setResultGrf = new JButton(setResultAction);
		JPanel a = createPanel(new JLabel("Source directory:"), srcDir,
				setSrcDir);
		JPanel b = createPanel(new JLabel("Resulting GRF grammar:"), resultGrf,
				setResultGrf);
		upPanel.add(a);
		upPanel.add(b);
		return upPanel;
	}

	private JPanel constructDownPanel() {
		JPanel downPanel = new JPanel(new GridLayout(1, 2));
		downPanel.setOpaque(true);
		Action okAction = new AbstractAction("OK") {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new Thread() {
							public void run() {
								setVisible(false);
								GraphCollection.build(new File(srcDir.getText()),
										new File(resultGrf.getText()), true);
							}
						}.start();
					}
				});
			}
		};
		JButton OK = new JButton(okAction);
		Action cancelAction = new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		};
		JButton CANCEL = new JButton(cancelAction);
		JPanel left = new JPanel();
		left.setBorder(new EmptyBorder(10, 50, 10, 20));
		left.setLayout(new BorderLayout());
		left.add(CANCEL, BorderLayout.CENTER);
		JPanel right = new JPanel();
		right.setBorder(new EmptyBorder(10, 20, 10, 50));
		right.setLayout(new BorderLayout());
		right.add(OK, BorderLayout.CENTER);
		downPanel.add(left);
		downPanel.add(right);
		return downPanel;
	}

}