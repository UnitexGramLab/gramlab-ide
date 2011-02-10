/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.Preferences;
import fr.umlv.unitex.listeners.FontListener;
import fr.umlv.unitex.text.BigTextList;

/**
 * This class describes a frame used to display current corpus's token lists.
 *
 * @author Sébastien Paumier
 */
public class TfstTagsFrame extends JInternalFrame {
    private final BigTextList text = new BigTextList(false);

    TfstTagsFrame() {
        super("Tfst tag list", true, true, true, true);
        JPanel top = new JPanel(new BorderLayout());
        top.add(constructButtonsPanel(), BorderLayout.NORTH);
        top.add(new JScrollPane(text), BorderLayout.CENTER);
        setContentPane(top);
        pack();
        setBounds(50, 200, 300, 450);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                try {
                    setIcon(true);
                } catch (java.beans.PropertyVetoException e2) {
                    e2.printStackTrace();
                }
            }
        });
        Preferences.addTextFontListener(new FontListener() {
            public void fontChanged(Font font) {
                text.setFont(font);
            }
        });
    }

    private JPanel constructButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
        Action frequenceAction = new AbstractAction("By Frequence") {
            public void actionPerformed(ActionEvent arg0) {
                loadTokens(new File(Config.getCurrentSntDir(),
                        "tfst_tags_by_freq.txt"));
                try {
                    setIcon(false);
                    setSelected(true);
                } catch (java.beans.PropertyVetoException e2) {
                    e2.printStackTrace();
                }
            }
        };
        JButton byFrequence = new JButton(frequenceAction);
        Action orderAction = new AbstractAction("By Char Order") {
            public void actionPerformed(ActionEvent arg0) {
                loadTokens(new File(Config.getCurrentSntDir(),
                        "tfst_tags_by_alph.txt"));
                try {
                    setIcon(false);
                    setSelected(true);
                } catch (java.beans.PropertyVetoException e2) {
                    e2.printStackTrace();
                }
            }
        };
        JButton byCharOrder = new JButton(orderAction);
        JPanel tmp1 = new JPanel(new BorderLayout());
        tmp1.setBorder(new EmptyBorder(5, 5, 5, 5));
        tmp1.add(byFrequence, BorderLayout.CENTER);
        JPanel tmp2 = new JPanel(new BorderLayout());
        tmp2.setBorder(new EmptyBorder(5, 5, 5, 5));
        tmp2.add(byCharOrder, BorderLayout.CENTER);
        buttonsPanel.add(tmp1);
        buttonsPanel.add(tmp2);
        return buttonsPanel;
    }

    /**
     * Loads a token list
     *
     * @param file name of the token list file
     */
    boolean loadTokens(File file) {
        if (!file.exists()) return false;
        text.setFont(Config.getCurrentTextFont());
        if (file.length() <= 2) {
            text.setText(Config.EMPTY_FILE_MESSAGE);
        } else {
            text.load(file);
        }
        return true;
    }

    /**
     * Hides the frame
     */
    void hideFrame() {
        text.reset();
        setVisible(false);
        System.gc();
    }
}
