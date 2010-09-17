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
import fr.umlv.unitex.text.BigTextList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.io.File;

/**
 * This class describes a frame used to display current corpus's DLF, DLC and
 * ERR files.
 *
 * @author Sébastien Paumier
 */
public class TextDicFrame extends JInternalFrame {
    final BigTextList dlf = new BigTextList(true);
    final BigTextList dlc = new BigTextList(true);
    final BigTextList err = new BigTextList();
    final JLabel dlfLabel = new JLabel("");
    final JLabel dlcLabel = new JLabel("");
    final JLabel errLabel = new JLabel("");

    TextDicFrame() {
        super("", true, true, true, true);
        setContentPane(constructPanel());
        pack();
        setBounds(250, 300, 500, 500);
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
        dlf.setComponentOrientation(
        		Preferences.rightToLeft()?ComponentOrientation.RIGHT_TO_LEFT
        				:ComponentOrientation.LEFT_TO_RIGHT);
        dlc.setComponentOrientation(
        		Preferences.rightToLeft()?ComponentOrientation.RIGHT_TO_LEFT
        				:ComponentOrientation.LEFT_TO_RIGHT);
        err.setComponentOrientation(
        		Preferences.rightToLeft()?ComponentOrientation.RIGHT_TO_LEFT
        				:ComponentOrientation.LEFT_TO_RIGHT);
        Preferences.addTextFontListener(new FontListener() {
            public void fontChanged(Font font) {
                dlf.setFont(font);
                dlc.setFont(font);
                err.setFont(font);
                dlf.setComponentOrientation(
                		Preferences.rightToLeft()?ComponentOrientation.RIGHT_TO_LEFT
                				:ComponentOrientation.LEFT_TO_RIGHT);
                dlc.setComponentOrientation(
                		Preferences.rightToLeft()?ComponentOrientation.RIGHT_TO_LEFT
                				:ComponentOrientation.LEFT_TO_RIGHT);
                err.setComponentOrientation(
                		Preferences.rightToLeft()?ComponentOrientation.RIGHT_TO_LEFT
                				:ComponentOrientation.LEFT_TO_RIGHT);
            }
        });
    }

    private JPanel constructPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(constructDicPanel());
        panel.add(constructErrPanel());
        return panel;
    }

    private JPanel constructDicPanel() {
        JPanel dicPanel = new JPanel(new GridLayout(2, 1));
        JScrollPane dlfScroll = new JScrollPane(dlf);
        dlfScroll
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JScrollPane dlcScroll = new JScrollPane(dlc);
        dlcScroll
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel up = new JPanel(new BorderLayout());
        up.setBorder(new EmptyBorder(5, 5, 5, 5));
        up.add(dlfLabel, BorderLayout.NORTH);
        JPanel tmp = new JPanel(new BorderLayout());
        tmp.setBorder(BorderFactory.createLoweredBevelBorder());
        tmp.add(dlfScroll, BorderLayout.CENTER);
        up.add(tmp, BorderLayout.CENTER);
        JPanel down = new JPanel(new BorderLayout());
        down.setBorder(new EmptyBorder(5, 5, 5, 5));
        down.add(dlcLabel, BorderLayout.NORTH);
        JPanel tmp2 = new JPanel(new BorderLayout());
        tmp2.setBorder(BorderFactory.createLoweredBevelBorder());
        tmp2.add(dlcScroll, BorderLayout.CENTER);
        down.add(tmp2, BorderLayout.CENTER);
        dicPanel.add(up);
        dicPanel.add(down);
        return dicPanel;
    }

    private JPanel constructErrPanel() {
        JPanel errPanel = new JPanel(new BorderLayout());
        errPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JScrollPane errScroll = new JScrollPane(err);
        errScroll
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        errPanel.add(errLabel, BorderLayout.NORTH);
        JPanel tmp = new JPanel(new BorderLayout());
        tmp.setBorder(BorderFactory.createLoweredBevelBorder());
        tmp.add(errScroll, BorderLayout.CENTER);
        errPanel.add(tmp, BorderLayout.CENTER);
        return errPanel;
    }

    /**
     * Loads "dlf", "dlc" and "err" files contained in a directory, and shows
     * the frame
     *
     * @param dir directory to look in
     */
    void loadTextDic(File dir) {
        File FILE = new File(dir, "dlf");
        dlf.setFont(Config.getCurrentTextFont());
        String n = UnicodeIO.readFirstLine(new File(dir, "dlf.n"));
        String message = "DLF";
        if (n != null) {
            message = message + ": " + n + " simple-word lexical entr";
            if (Integer.parseInt(n) <= 1)
                message = message + "y";
            else
                message = message + "ies";
        }
        if (!FILE.exists() || FILE.length() <= 2) {
            dlf.setText(Config.EMPTY_FILE_MESSAGE);
            dlfLabel.setText("DLF: simple-word lexical entries");
        } else {
            dlf.load(FILE);
            dlfLabel.setText(message);
        }
        /********* Loading DLC file *********/
        FILE = new File(dir, "dlc");
        dlc.setFont(Config.getCurrentTextFont());
        n = UnicodeIO.readFirstLine(new File(dir, "dlc.n"));
        message = "DLC";
        if (n != null) {
            message = message + ": " + n + " compound lexical entr";
            if (Integer.parseInt(n) <= 1)
                message = message + "y";
            else
                message = message + "ies";
        }
        if (!FILE.exists() || FILE.length() <= 2) {
            dlc.setText(Config.EMPTY_FILE_MESSAGE);
            dlcLabel.setText("DLC: compound lexical entries");
        } else {
            dlc.load(FILE);
            dlcLabel.setText(message);
        }
        /********* Loading ERR file *********/
        FILE = new File(dir, "err");
        err.setFont(Config.getCurrentTextFont());
        n = UnicodeIO.readFirstLine(new File(dir, "err.n"));
        message = "ERR";
        if (n != null) {
            message = message + ": " + n + " unknown simple word";
            if (Integer.parseInt(n) > 1)
                message = message + "s";
        }
        if (!FILE.exists() || FILE.length() <= 2) {
            err.setText(Config.EMPTY_FILE_MESSAGE);
            errLabel.setText("ERR: unknown simple words");
        } else {
            err.load(FILE);
            errLabel.setText(message);
        }
        setTitle("Word Lists in " + dir);
    }

    /**
     * Hides the frame
     */
    void hideFrame() {
        dlf.reset();
        dlc.reset();
        err.reset();
        setVisible(false);
        System.gc();
    }
}
