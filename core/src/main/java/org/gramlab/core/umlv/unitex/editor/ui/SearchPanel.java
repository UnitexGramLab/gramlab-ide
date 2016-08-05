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
package org.gramlab.core.umlv.unitex.editor.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.gramlab.core.umlv.unitex.editor.EditionTextArea;

/**
 * @author Decreton Julien
 */
abstract class SearchPanel extends JPanel {
	final EditionTextArea text;
	JButton btClose;

	SearchPanel(EditionTextArea text) {
		super(new BorderLayout());
		this.text = text;
		// close button
		btClose = new JButton("Close");
		btClose.setMnemonic('c');
	}

	/**
	 * Generate message dialogue box
	 * 
	 * @param message
	 */
	void warning(String message) {
		JOptionPane.showMessageDialog(text, message, "Warning",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Add an action to the close button
	 * 
	 * @param action
	 *            action to add to the pnel's close button
	 */
	public void addCloseAction(ActionListener action) {
		btClose.addActionListener(action);
	}
}
