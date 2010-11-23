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

package fr.umlv.unitex.frames;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.Preferences;
import fr.umlv.unitex.io.UnicodeIO;
import fr.umlv.unitex.listeners.FontListener;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.text.BigTextList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;


/**
 * This class describes a frame used to display a dictionary.
 *
 * @author Sébastien Paumier
 */
public class DelaFrame extends JInternalFrame {

    private final JPanel middle;
    private final BigTextList text = new BigTextList(true);
    private final JScrollBar scrollBar;

    DelaFrame() {
        super("", true, true, true, true);
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(new EmptyBorder(2, 2, 2, 2));
        middle = new JPanel(new BorderLayout());
        middle.setBorder(BorderFactory.createLoweredBevelBorder());
        final JScrollPane scrollText = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        middle.add(scrollText);
        top.add(middle, BorderLayout.CENTER);
        setContentPane(top);
        pack();
        setBounds(100, 100, 500, 500);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                Config.setCurrentDELA(null);
                text.reset();
                setVisible(false);
                /* We wait to avoid blocking the creation of fooflx.dic by MultiFlex */
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                System.gc();
            }
        });
        text.setComponentOrientation(
                Preferences.rightToLeftForText() ? ComponentOrientation.RIGHT_TO_LEFT
                        : ComponentOrientation.LEFT_TO_RIGHT);
        scrollBar = scrollText.getHorizontalScrollBar();
        Timer t = new Timer(400, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scrollBar.setValue(0);
            }
        });
        t.setRepeats(false);
        t.start();
        scrollText.setComponentOrientation(
                Preferences.rightToLeftForText() ? ComponentOrientation.RIGHT_TO_LEFT
                        : ComponentOrientation.LEFT_TO_RIGHT);
        Preferences.addTextFontListener(new FontListener() {
            public void fontChanged(Font font) {
                text.setFont(font);
                text.setComponentOrientation(
                        Preferences.rightToLeftForText() ? ComponentOrientation.RIGHT_TO_LEFT
                                : ComponentOrientation.LEFT_TO_RIGHT);
                scrollText.setComponentOrientation(
                        Preferences.rightToLeftForText() ? ComponentOrientation.RIGHT_TO_LEFT
                                : ComponentOrientation.LEFT_TO_RIGHT);
                Timer t2 = new Timer(400, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        scrollBar.setValue(0);
                    }
                });
                t2.setRepeats(false);
                t2.start();
            }
        });
    }

    /**
     * Loads a dictionary.
     *
     * @param dela the dictionary to be loaded
     */
    public void loadDela(File dela) {
        LoadDelaDo toDo = new LoadDelaDo(dela);
        try {
            if (!UnicodeIO.isAUnicodeLittleEndianFile(dela)) {
                UnitexFrame.getFrameManager().newTranscodeOneFileDialog(dela, toDo);
            } else {
                toDo.toDo();
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    void loadUnicodeDela(File dela) {
        text.load(dela);
        Config.setCurrentDELA(dela);
        text.setFont(Config.getCurrentTextFont());
        setTitle(dela.getAbsolutePath());
        setVisible(true);
        Timer t = new Timer(400, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scrollBar.setValue(0);
            }
        });
        t.setRepeats(false);
        t.start();
        try {
            setIcon(false);
            setSelected(true);
        } catch (java.beans.PropertyVetoException e2) {
            e2.printStackTrace();
        }
    }


    class LoadDelaDo implements ToDo {
        final File dela;

        LoadDelaDo(File s) {
            dela = s;
        }

        public void toDo() {
            loadUnicodeDela(dela);
        }

    }

}