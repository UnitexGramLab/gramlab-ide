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

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import fr.umlv.unitex.graphrendering.ContextsInfo;

/**
 * This class describes a <code>JPanel</code> that allows the user to set left
 * and right contexts for multiple word copies. This object is designed to be
 * inserted into a dialog box.
 * 
 * @author Sébastien Paumier
 */
class ListCopyDialog extends JDialog {
	ContextsInfo info;
	final JTextField left;
	final JTextField right;

	/**
	 * Constructs a new empty <code>ListCopyDialog</code>
	 */
	public ListCopyDialog() {
		super(UnitexFrame.mainFrame, true);
		final JPanel p = new JPanel(new GridLayout(3, 1));
		p.setBorder(new EmptyBorder(10, 10, 10, 10));
		p.add(new JLabel("Choose your left and right contexts:"));
		left = new JTextField(5);
		left.setHorizontalAlignment(SwingConstants.RIGHT);
		right = new JTextField(5);
		final JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new FlowLayout());
		inputPanel.add(left);
		inputPanel.add(new JLabel("item"));
		inputPanel.add(right);
		p.add(inputPanel);
		final JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				info = new ContextsInfo(left.getText(), right.getText());
				setVisible(false);
			}
		});
		final JPanel down = new JPanel();
		down.add(ok);
		p.add(down);
		setContentPane(p);
		pack();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				info = new ContextsInfo(left.getText(), right.getText());
			}
		});
		setLocationRelativeTo(UnitexFrame.mainFrame);
	}

	public ContextsInfo getContextsInfo() {
		return info;
	}
}
