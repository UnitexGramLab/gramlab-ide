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
package fr.umlv.unitex.editor.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.Document;

import fr.umlv.unitex.editor.EditionTextArea;

abstract class AbstractFindpanel extends SearchPanel {
	final JTextField txtFind;
	private final JTextField txtReplace;
	final JLabel count;
	final Document docFind;
	final Document docReplace;
	final JButton btFind;
	final JButton btReplace;
	final JButton btRplNext;
	final JButton btcntO;
	final JButton btReplaceAll;
	final JPanel pc1;
	final JPanel po;

	/**
	 * @param text
	 */
	AbstractFindpanel(EditionTextArea text) {
		super(text);
		// form
		pc1 = new JPanel(new BorderLayout());
		final JPanel pf = new JPanel();
		pf.setLayout(new DialogLayout(20, 5));
		pf.setBorder(new EmptyBorder(8, 5, 8, 0));
		pf.add(new JLabel("Find what:"));
		txtFind = new JTextField();
		txtFind.setColumns(20);
		docFind = txtFind.getDocument();
		pf.add(txtFind);
		pf.add(new JLabel("Replace:"));
		txtReplace = new JTextField();
		txtReplace.setColumns(20);
		docReplace = txtReplace.getDocument();
		pf.add(txtReplace);
		pf.add(new JLabel("Occurrences:"));
		count = new JLabel("0");
		pf.add(count);
		pc1.add(pf, BorderLayout.CENTER);
		add(pc1, BorderLayout.CENTER);
		// options
		po = new JPanel(new GridLayout(3, 3, 1, 1));
		po.setBorder(new TitledBorder(new EtchedBorder(), "Options"));
		pc1.add(po, BorderLayout.SOUTH);
		// Buttons
		final JPanel p01 = new JPanel(new FlowLayout());
		final JPanel p = new JPanel(new GridLayout(6, 1));
		btFind = new JButton("Find Next");
		btFind.setMnemonic('f');
		p.add(btFind);
		btRplNext = new JButton("Replace Next");
		btRplNext.setMnemonic('n');
		p.add(btRplNext);
		btReplace = new JButton("Replace");
		btReplace.setMnemonic('r');
		p.add(btReplace);
		btcntO = new JButton("Count occurrences");
		btcntO.setMnemonic('o');
		p.add(btcntO);
		btReplaceAll = new JButton("Replace All");
		btReplaceAll.setMnemonic('g');
		p.add(btReplaceAll);
		btClose = new JButton("Close");
		btClose.setMnemonic('c');
		p.add(btClose);
		p01.add(p);
		add(p01, BorderLayout.EAST);
	}
}
