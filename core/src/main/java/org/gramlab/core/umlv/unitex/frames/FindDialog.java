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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import org.gramlab.core.umlv.unitex.editor.ui.DictionaryFindPanel;
import org.gramlab.core.umlv.unitex.editor.ui.FindPanel;
import org.gramlab.core.umlv.unitex.editor.ui.FindSentencePanel;

public class FindDialog extends JDialog {
	/**
	 * Creates a new fileDialog with the specified owner
	 * 
	 * @param owner
	 *            the FileEditionTextFrame where find words
	 */
	FindDialog(FileEditionTextFrame owner) {
		super(UnitexFrame.mainFrame);
		final ActionListener closeAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		};
		final JTabbedPane tab = new JTabbedPane();
		final FindPanel p1 = new FindPanel(owner.getText());
		p1.addCloseAction(closeAction);
		final FindSentencePanel p2 = new FindSentencePanel(owner.getText());
		final DictionaryFindPanel p3 = new DictionaryFindPanel(owner.getText());
		p1.addCloseAction(closeAction);
		p2.addCloseAction(closeAction);
		p3.addCloseAction(closeAction);
		tab.add("Find", p1);
		tab.add("Find Sentence", p2);
		tab.add("Dictionary Search", p3);
		getContentPane().add(tab, BorderLayout.CENTER);
		pack();
		setResizable(false);
		setLocationRelativeTo(UnitexFrame.mainFrame);
	}
}
