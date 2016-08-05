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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.gramlab.core.umlv.unitex.editor.EditionTextArea;

/**
 * @author julien
 */
public class FindSentencePanel extends SearchPanel {
	private final JTextField sentenceNumber;

	public FindSentencePanel(final EditionTextArea text) {
		super(text);
		final JPanel pf = new JPanel();
		pf.setLayout(new DialogLayout(20, 5));
		pf.setBorder(new EmptyBorder(8, 5, 8, 0));
		pf.add(new JLabel("Sentence Number:"));
		sentenceNumber = new JTextField();
		pf.add(getSentenceNumber());
		final JPanel p01 = new JPanel(new FlowLayout());
		final JPanel p = new JPanel(new GridLayout(2, 1));
		final JButton btFind = new JButton("Find");
		// to have the same dimension betwin all panel buttons
		btFind.setPreferredSize(new JButton("Count occurrences")
				.getPreferredSize());
		class findSentence implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					final Document docFind = getSentenceNumber().getDocument();
					final String key = docFind.getText(0, docFind.getLength());
					final int number = Integer.parseInt(key);
					text.findSentence(number);
				} catch (final BadLocationException e) {
					warning("Bad Loaction Exception:\n " + e.getMessage());
				} catch (final NumberFormatException e) {
					warning("Number Format Exception:\n " + e.getMessage());
				} catch (final KeyErrorException e) {
					warning(e.getMessage());
				} catch (final TextAreaSeparatorException e) {
					warning(e.getMessage());
				}
			}
		}
		final findSentence findSentenceAction = new findSentence();
		btFind.addActionListener(findSentenceAction);
		// find next when enter is pressed
		getSentenceNumber().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					findSentenceAction.actionPerformed(null);
			}
		});
		btFind.setMnemonic('f');
		p.add(btFind);
		p.add(btClose);
		p01.add(p);
		add(p01, BorderLayout.EAST);
		add(pf, BorderLayout.CENTER);
	}

	public JTextField getSentenceNumber() {
		return sentenceNumber;
	}
}
