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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class FindPanel extends AbstractFindpanel {


    final ButtonModel modelUp;
    final ButtonModel modelPrefixe;
    ButtonModel modelWordmodelCase;
    final ButtonModel modelDown;
    final ButtonModel modelSuffixe;
    final ButtonModel modelWord;
    final ButtonModel modelCase;
    final ButtonModel modelRadical;
    final ButtonModel modelBegin;


    public FindPanel(final EditionTextArea text) {
        super(text);

        text.setCaretPosition(0);
        // options
        ButtonGroup bg = new ButtonGroup();
        final ButtonGroup bgS = new ButtonGroup();


        JRadioButton rdBegin = new JRadioButton("Search from begining", true);
        rdBegin.setMnemonic('b');
        modelBegin = rdBegin.getModel();
        bgS.add(rdBegin);
        rdBegin.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                text.setCaretPosition(0);
                /*Caret caret = text.getCaret();
                                    caret.setDot(0);
                                    caret.setVisible(true);*/
            }

        }

        );
        po.add(rdBegin);

        final JRadioButton rdR = new JRadioButton("Radical");
        rdR.setMnemonic('r');
        modelRadical = rdR.getModel();
        bg.add(rdR);
        po.add(rdR);

        JRadioButton chkWord = new JRadioButton("Whole words only");
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


        JRadioButton rdDown = new JRadioButton("Search down", true);
        rdDown.setMnemonic('d');
        modelDown = rdDown.getModel();
        bgS.add(rdDown);
        po.add(rdDown);

        final JRadioButton rdS = new JRadioButton("Sufixe");
        rdS.setMnemonic('x');
        modelSuffixe = rdS.getModel();
        bg.add(rdS);
        po.add(rdS);

        JCheckBox chkCase = new JCheckBox("Match case");
        chkCase.setMnemonic('c');
        modelCase = chkCase.getModel();
        po.add(chkCase);


        pc1.add(po, BorderLayout.SOUTH);
        add(pc1, BorderLayout.CENTER);


        // Acions
        final ActionListener findAction = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    if (modelBegin.isSelected())
                        bgS.setSelected(modelDown, true);

                    String key = docFind.getText(0, docFind.getLength());
                    text.findNext(
                            modelUp.isSelected(),
                            modelCase.isSelected(),
                            modelWord.isSelected(),
                            modelPrefixe.isSelected(),
                            modelSuffixe.isSelected(),
                            modelRadical.isSelected(),
                            key);
                } catch (BadLocationException ex) {
                    warning("Bad Location Exception:\n" + ex.getMessage());
                } catch (KeyErrorException ke) {
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
                    text.setSelection(start, end, rdUp.isSelected());
                    text.replaceSelection(replacement);
                    text.setSelection(
                            start,
                            start + replacement.length(),
                            rdUp.isSelected());

                    String key = docFind.getText(0, docFind.getLength());
                    text.findNext(
                            modelUp.isSelected(),
                            modelCase.isSelected(),
                            modelWord.isSelected(),
                            modelPrefixe.isSelected(),
                            modelSuffixe.isSelected(),
                            modelRadical.isSelected(),
                            key);

                } catch (BadLocationException ble) {
                    warning("Bad Location Exception\n" + ble.getMessage());
                } catch (KeyErrorException e) {
                    warning(e.getMessage());
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
                    text.setSelection(start, end, rdUp.isSelected());
                    text.replaceSelection(replacement);
                    text.setSelection(
                            start,
                            start + replacement.length(),
                            rdUp.isSelected());

                } catch (BadLocationException ble) {
                    warning("Bad Location Exception:\n" + ble.getMessage());
                }
            }
        };

        btReplace.addActionListener(replaceAction);


        // count word occurences
        btcntO.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {

                try {

                    String key = docFind.getText(0, docFind.getLength());
                    count.setText(
                            new Integer(
                                    text.countAll(
                                            key,
                                            modelUp.isSelected(),
                                            modelWord.isSelected(),
                                            modelCase.isSelected()))
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
                            modelCase.isSelected(),
                            modelWord.isSelected());
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

}
