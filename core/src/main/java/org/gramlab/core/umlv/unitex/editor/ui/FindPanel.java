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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.text.BadLocationException;

import org.gramlab.core.umlv.unitex.editor.EditionTextArea;
import org.gramlab.core.umlv.unitex.editor.ReplacementTargetException;
import org.gramlab.core.umlv.unitex.editor.TargetException;

public class FindPanel extends AbstractFindpanel {
	private final ButtonModel modelUp;
	private final ButtonModel modelPrefixe;
	ButtonModel modelWordmodelCase;
	private final ButtonModel modelDown;
	private final ButtonModel modelSuffixe;
	private final ButtonModel modelWord;
	private final ButtonModel modelCase;
	private final ButtonModel modelRadical;
	private final ButtonModel modelBegin;

	public FindPanel(final EditionTextArea text) {
		super(text);
		text.setCaretPosition(0);
		// options
		final ButtonGroup bg = new ButtonGroup();
		final ButtonGroup bgS = new ButtonGroup();
		final JRadioButton rdBegin = new JRadioButton("Search from begining",
				true);
		rdBegin.setMnemonic('b');
		modelBegin = rdBegin.getModel();
		bgS.add(rdBegin);
		rdBegin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				text.setCaretPosition(0);
				/*
				 * Caret caret = text.getCaret(); caret.setDot(0);
				 * caret.setVisible(true);
				 */
			}
		});
		po.add(rdBegin);
		final JRadioButton rdR = new JRadioButton("Radical");
		rdR.setMnemonic('r');
		modelRadical = rdR.getModel();
		bg.add(rdR);
		po.add(rdR);
		final JRadioButton chkWord = new JRadioButton("Whole words only");
		chkWord.setMnemonic('w');
		modelWord = chkWord.getModel();
		bg.add(chkWord);
		po.add(chkWord);
		final JRadioButton rdUp = new JRadioButton("Search up");
		rdUp.setMnemonic('u');
		modelUp = rdUp.getModel();
		bgS.add(rdUp);
		po.add(rdUp);
		final JRadioButton rdP = new JRadioButton("Prefixe");
		rdP.setMnemonic('i');
		modelPrefixe = rdP.getModel();
		bg.add(rdP);
		po.add(rdP);
		final JRadioButton rdA = new JRadioButton("Any");
		rdA.setMnemonic('a');
		bg.add(rdA);
		po.add(rdA);
		final JRadioButton rdDown = new JRadioButton("Search down", true);
		rdDown.setMnemonic('d');
		modelDown = rdDown.getModel();
		bgS.add(rdDown);
		po.add(rdDown);
		final JRadioButton rdS = new JRadioButton("Sufixe");
		rdS.setMnemonic('x');
		modelSuffixe = rdS.getModel();
		bg.add(rdS);
		po.add(rdS);
		final JCheckBox chkCase = new JCheckBox("Match case");
		chkCase.setMnemonic('c');
		modelCase = chkCase.getModel();
		po.add(chkCase);
		pc1.add(po, BorderLayout.SOUTH);
		add(pc1, BorderLayout.CENTER);
		// Acions
		final ActionListener findAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (getModelBegin().isSelected())
						bgS.setSelected(getModelDown(), true);
					final String key = docFind.getText(0, docFind.getLength());
					text.findNext(getModelUp().isSelected(), getModelCase()
							.isSelected(), getModelWord().isSelected(),
							getModelPrefixe().isSelected(), getModelSuffixe()
									.isSelected(), getModelRadical()
									.isSelected(), key);
				} catch (final BadLocationException ex) {
					warning("Bad Location Exception:\n" + ex.getMessage());
				} catch (final KeyErrorException ke) {
					warning(ke.getMessage());
				}
			}
		};
		btFind.addActionListener(findAction);
		// find next when enter is pressed
		txtFind.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					findAction.actionPerformed(null);
			}
		});
		btRplNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final int start = text.getSelectionStart();
				final int end = text.getSelectionEnd();
				// int selectionSize = end - start;
				try {
					// get the replament word
					final String replacement = docReplace.getText(0,
							docReplace.getLength());
					// replace
					text.setSelection(start, end, rdUp.isSelected());
					text.replaceSelection(replacement);
					text.setSelection(start, start + replacement.length(),
							rdUp.isSelected());
					final String key = docFind.getText(0, docFind.getLength());
					text.findNext(getModelUp().isSelected(), getModelCase()
							.isSelected(), getModelWord().isSelected(),
							getModelPrefixe().isSelected(), getModelSuffixe()
									.isSelected(), getModelRadical()
									.isSelected(), key);
				} catch (final BadLocationException ble) {
					warning("Bad Location Exception\n" + ble.getMessage());
				} catch (final KeyErrorException e) {
					warning(e.getMessage());
				}
			}
		});
		final ActionListener replaceAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final int start = text.getSelectionStart();
				final int end = text.getSelectionEnd();
				// int selectionSize = end - start;
				try {
					// get the replament word
					final String replacement = docReplace.getText(0,
							docReplace.getLength());
					// replace
					text.setSelection(start, end, rdUp.isSelected());
					text.replaceSelection(replacement);
					text.setSelection(start, start + replacement.length(),
							rdUp.isSelected());
				} catch (final BadLocationException ble) {
					warning("Bad Location Exception:\n" + ble.getMessage());
				}
			}
		};
		btReplace.addActionListener(replaceAction);
		// count word occurences
		btcntO.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					final String key = docFind.getText(0, docFind.getLength());
					count.setText(Integer.toString(text.countAll(key,
							getModelUp().isSelected(), getModelWord()
									.isSelected(), getModelCase().isSelected())));
				} catch (final BadLocationException e) {
					warning("Bad Location Exception:\n" + e.getMessage());
				} catch (final KeyErrorException e) {
					warning(e.getMessage());
				}
			}
		});
		// replace all
		final ActionListener replaceAll = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					final String key = docFind.getText(0, docFind.getLength());
					final String rkey = docReplace.getText(0,
							docFind.getLength());
					text.replaceAll(key, rkey, getModelUp().isSelected(),
							getModelCase().isSelected(), getModelWord()
									.isSelected());
				} catch (final BadLocationException ble) {
					warning("Bad Location Exception:\n" + ble.getMessage());
				} catch (final TargetException te) {
					warning(te.getMessage());
				} catch (final ReplacementTargetException rte) {
					warning(rte.getMessage());
				}
			}
		};
		btReplaceAll.addActionListener(replaceAll);
	}

	public ButtonModel getModelBegin() {
		return modelBegin;
	}

	public ButtonModel getModelDown() {
		return modelDown;
	}

	public ButtonModel getModelCase() {
		return modelCase;
	}

	public ButtonModel getModelUp() {
		return modelUp;
	}

	public ButtonModel getModelPrefixe() {
		return modelPrefixe;
	}

	public ButtonModel getModelWord() {
		return modelWord;
	}

	public ButtonModel getModelRadical() {
		return modelRadical;
	}

	public ButtonModel getModelSuffixe() {
		return modelSuffixe;
	}
}
