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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.text.BadLocationException;

import org.gramlab.core.umlv.unitex.editor.EditionTextArea;
import org.gramlab.core.umlv.unitex.editor.ReplacementTargetException;
import org.gramlab.core.umlv.unitex.editor.TargetException;

public class DictionaryFindPanel extends AbstractFindpanel {
	private final ButtonModel modelCano;
	private final ButtonModel modelUp;
	private final ButtonModel modelGram;
	private final ButtonModel modelDown;
	private final ButtonModel modelFl;
	private final ButtonModel modelFlCode;
	private final ButtonModel modelBegin;

	public DictionaryFindPanel(final EditionTextArea text) {
		super(text);
		// search options
		final ButtonGroup bgS = new ButtonGroup();
		final JRadioButton rdUp = new JRadioButton("Search up");
		rdUp.setMnemonic('u');
		modelUp = rdUp.getModel();
		bgS.add(rdUp);
		final JCheckBox chkCano = new JCheckBox("Canonical form");
		chkCano.setMnemonic('c');
		modelCano = chkCano.getModel();
		final JCheckBox chkGram = new JCheckBox("Grammatical code");
		chkGram.setMnemonic('m');
		modelGram = chkGram.getModel();
		final JRadioButton rdDown = new JRadioButton("Search down", true);
		rdDown.setMnemonic('d');
		modelDown = rdDown.getModel();
		bgS.add(rdDown);
		final JRadioButton rdBegin = new JRadioButton("Search from begining");
		rdBegin.setMnemonic('b');
		modelBegin = rdBegin.getModel();
		bgS.add(rdBegin);
		rdBegin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				text.setCaretPosition(0);
			}
		});
		final JCheckBox chkFl = new JCheckBox("Inflected form");
		chkFl.setMnemonic('c');
		modelFl = chkFl.getModel();
		final JCheckBox chkFlCode = new JCheckBox("Flexional code");
		chkFlCode.setMnemonic('x');
		modelFlCode = chkFlCode.getModel();
		po.add(rdBegin);
		po.add(chkGram);
		po.add(chkCano);
		po.add(rdUp);
		po.add(chkFl);
		po.add(chkFlCode);
		po.add(rdDown);
		// find
		final ActionListener findAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (getModelBegin().isSelected())
						bgS.setSelected(getModelDown(), true);
					final String key = docFind.getText(0, docFind.getLength());
					text.dictionaryFindNext(getModelUp().isSelected(),
							(getModelGram().isSelected() || getModelFlCode()
									.isSelected()),
							getModelGram().isSelected(), getModelFl()
									.isSelected(), getModelCano().isSelected(),
							key);
				} catch (final BadLocationException ex) {
					warning("Bad Location Exception:\n" + ex.getMessage());
				} catch (final KeyErrorException ex) {
					warning(ex.getMessage());
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
					text.setSelection(start, end, getModelUp().isSelected());
					text.replaceSelection(replacement);
					text.setSelection(start, start + replacement.length(),
							getModelUp().isSelected());
					final String key = docFind.getText(0, docFind.getLength());
					text.dictionaryFindNext(getModelUp().isSelected(),
							(getModelGram().isSelected() || getModelFlCode()
									.isSelected()),
							getModelGram().isSelected(), getModelFl()
									.isSelected(), getModelCano().isSelected(),
							key);
				} catch (final BadLocationException ble) {
					warning("Bad Location Exception\n" + ble.getMessage());
				} catch (final KeyErrorException ex) {
					warning(ex.getMessage());
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
					text.setSelection(start, end, getModelUp().isSelected());
					text.replaceSelection(replacement);
					text.setSelection(start, start + replacement.length(),
							getModelUp().isSelected());
				} catch (final BadLocationException ble) {
					warning("Bad Location Exception:\n" + ble.getMessage());
				}
			}
		};
		btReplace.addActionListener(replaceAction);
		btcntO.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					final String key = docFind.getText(0, docFind.getLength());
					count.setText(Integer.toString(text.countAll(key,
							getModelUp().isSelected(), getModelGram()
									.isSelected(), getModelGram().isSelected())));
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
							(getModelGram().isSelected() || getModelFlCode()
									.isSelected()), getModelGram().isSelected());
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

	/**
	 * Generate message dialogue box
	 * 
	 * @param message
	 */
	@Override
	protected void warning(String message) {
		JOptionPane.showMessageDialog(text, message, "Warning",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public ButtonModel getModelBegin() {
		return modelBegin;
	}

	public ButtonModel getModelDown() {
		return modelDown;
	}

	public ButtonModel getModelGram() {
		return modelGram;
	}

	public ButtonModel getModelUp() {
		return modelUp;
	}

	public ButtonModel getModelFlCode() {
		return modelFlCode;
	}

	public ButtonModel getModelFl() {
		return modelFl;
	}

	public ButtonModel getModelCano() {
		return modelCano;
	}
}
