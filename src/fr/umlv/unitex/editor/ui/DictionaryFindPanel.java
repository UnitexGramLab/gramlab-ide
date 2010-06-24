/*
 * Unitex
 *
 * Copyright (C) 2001-2010 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import fr.umlv.unitex.editor.EditionTextArea;
import fr.umlv.unitex.editor.ReplacementTargetException;
import fr.umlv.unitex.editor.TargetException;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class DictionaryFindPanel extends AbstractFindpanel {

    final ButtonModel modelCano;
    final ButtonModel modelUp;
    final ButtonModel modelGram;
    final ButtonModel modelDown;
    final ButtonModel modelFl;
    final ButtonModel modelFlCode;
    final ButtonModel modelBegin;

    public DictionaryFindPanel(final EditionTextArea text) {
        super(text);

        // search options
        final ButtonGroup bgS = new ButtonGroup();

        JRadioButton rdUp = new JRadioButton("Search up");
        rdUp.setMnemonic('u');
        modelUp = rdUp.getModel();
        bgS.add(rdUp);


        JCheckBox chkCano = new JCheckBox("Canonical form");
        chkCano.setMnemonic('c');
        modelCano = chkCano.getModel();


        JCheckBox chkGram = new JCheckBox("Grammatical code");
        chkGram.setMnemonic('m');
        modelGram = chkGram.getModel();


        JRadioButton rdDown = new JRadioButton("Search down", true);
        rdDown.setMnemonic('d');
        modelDown = rdDown.getModel();
        bgS.add(rdDown);


        JRadioButton rdBegin = new JRadioButton("Search from begining");
        rdBegin.setMnemonic('b');
        modelBegin = rdBegin.getModel();
        bgS.add(rdBegin);
        rdBegin.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                text.setCaretPosition(0);
            }

        });


        JCheckBox chkFl = new JCheckBox("Inflected form");
        chkFl.setMnemonic('c');
        modelFl = chkFl.getModel();


        JCheckBox chkFlCode = new JCheckBox("Flexional code");
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

            public void actionPerformed(ActionEvent e) {
                try {
                    if (modelBegin.isSelected())
                        bgS.setSelected(modelDown, true);

                    String key = docFind.getText(0, docFind.getLength());
                    text.dictionaryFindNext(
                            modelUp.isSelected(),
                            (modelGram.isSelected() || modelFlCode.isSelected()),
                            modelGram.isSelected(),
                            modelFl.isSelected(),
                            modelCano.isSelected(),
                            key);
                } catch (BadLocationException ex) {
                    warning("Bad Location Exception:\n" + ex.getMessage());
                } catch (KeyErrorException ex) {
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

        }
        );

        btRplNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                int start = text.getSelectionStart();
                int end = text.getSelectionEnd();
                //int selectionSize = end - start;

                try {

                    // get the replament word
                    String replacement =
                            docReplace.getText(0, docReplace.getLength());

                    // replace
                    text.setSelection(start, end, modelUp.isSelected());
                    text.replaceSelection(replacement);
                    text.setSelection(
                            start,
                            start + replacement.length(),
                            modelUp.isSelected());

                    String key = docFind.getText(0, docFind.getLength());
                    text.dictionaryFindNext(
                            modelUp.isSelected(),
                            (modelGram.isSelected() || modelFlCode.isSelected()),
                            modelGram.isSelected(),
                            modelFl.isSelected(),
                            modelCano.isSelected(),
                            key);

                } catch (BadLocationException ble) {
                    warning("Bad Location Exception\n" + ble.getMessage());
                } catch (KeyErrorException ex) {
                    warning(ex.getMessage());
                }
            }

        });

        ActionListener replaceAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int start = text.getSelectionStart();
                int end = text.getSelectionEnd();
                //int selectionSize = end - start;

                try {

                    // get the replament word
                    String replacement =
                            docReplace.getText(0, docReplace.getLength());

                    // replace
                    text.setSelection(start, end, modelUp.isSelected());
                    text.replaceSelection(replacement);
                    text.setSelection(
                            start,
                            start + replacement.length(),
                            modelUp.isSelected());

                } catch (BadLocationException ble) {
                    warning("Bad Location Exception:\n" + ble.getMessage());
                }
            }
        };
        btReplace.addActionListener(replaceAction);

        btcntO.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {

                try {

                    String key = docFind.getText(0, docFind.getLength());
                    count.setText(
                            new Integer(
                                    text.countAll(
                                            key,
                                            modelUp.isSelected(),
                                            modelGram.isSelected(),
                                            modelGram.isSelected()))
                                    .toString());

                } catch (BadLocationException e) {
                    warning("Bad Location Exception:\n" + e.getMessage());
                } catch (KeyErrorException e) {
                    warning(e.getMessage());
                }

            }

        });


        // replace all
        ActionListener replaceAll = new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {

                    String key = docFind.getText(0, docFind.getLength());
                    String rkey = docReplace.getText(0, docFind.getLength());
                    text.replaceAll(
                            key,
                            rkey,
                            modelUp.isSelected(),
                            (modelGram.isSelected() || modelFlCode.isSelected()),
                            modelGram.isSelected());
                } catch (BadLocationException ble) {
                    warning("Bad Location Exception:\n" + ble.getMessage());
                } catch (TargetException te) {
                    warning(te.getMessage());
                } catch (ReplacementTargetException rte) {
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
        JOptionPane.showMessageDialog(
                text,
                message,
                "Warning",
                JOptionPane.INFORMATION_MESSAGE);
	}
}
