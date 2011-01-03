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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * This class describes a frame used to display current corpus's DLF, DLC and
 * ERR files.
 *
 * @author Sébastien Paumier
 */
public class TextDicFrame extends JInternalFrame {
    private final BigTextList dlf = new BigTextList(true);
    private final BigTextList dlc = new BigTextList(true);
    private final BigTextList err = new BigTextList();
    private final JLabel dlfLabel = new JLabel("");
    private final JLabel dlcLabel = new JLabel("");
    private final JLabel errLabel = new JLabel("");
    private JScrollBar dlfScrollbar;
    private JScrollBar dlcScrollbar;
    private JScrollBar errScrollbar;
    private JScrollPane dlfScroll;
    private JScrollPane dlcScroll;
    private JScrollPane errScroll;
    private JCheckBox tags_err=new JCheckBox("Filter unknown words with tags.ind",false);
    File text_dir;

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

            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
                Timer t = new Timer(400, new ActionListener() {
                    public void actionPerformed(ActionEvent e2) {
                        dlfScrollbar.setValue(0);
                        dlcScrollbar.setValue(0);
                        errScrollbar.setValue(0);
                    }
                });
                t.setRepeats(false);
                t.start();
            }
        });
        dlf.setComponentOrientation(
                Preferences.rightToLeftForText() ? ComponentOrientation.RIGHT_TO_LEFT
                        : ComponentOrientation.LEFT_TO_RIGHT);
        dlc.setComponentOrientation(
                Preferences.rightToLeftForText() ? ComponentOrientation.RIGHT_TO_LEFT
                        : ComponentOrientation.LEFT_TO_RIGHT);
        err.setComponentOrientation(
                Preferences.rightToLeftForText() ? ComponentOrientation.RIGHT_TO_LEFT
                        : ComponentOrientation.LEFT_TO_RIGHT);
        Preferences.addTextFontListener(new FontListener() {
            public void fontChanged(Font font) {
                dlf.setFont(font);
                dlc.setFont(font);
                err.setFont(font);
                dlf.setComponentOrientation(
                        Preferences.rightToLeftForText() ? ComponentOrientation.RIGHT_TO_LEFT
                                : ComponentOrientation.LEFT_TO_RIGHT);
                dlc.setComponentOrientation(
                        Preferences.rightToLeftForText() ? ComponentOrientation.RIGHT_TO_LEFT
                                : ComponentOrientation.LEFT_TO_RIGHT);
                err.setComponentOrientation(
                        Preferences.rightToLeftForText() ? ComponentOrientation.RIGHT_TO_LEFT
                                : ComponentOrientation.LEFT_TO_RIGHT);
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
        dlfScroll = new JScrollPane(dlf, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        dlfScroll.setComponentOrientation(
                Preferences.rightToLeftForText() ? ComponentOrientation.RIGHT_TO_LEFT
                        : ComponentOrientation.LEFT_TO_RIGHT);
        dlfScrollbar = dlfScroll.getHorizontalScrollBar();
        dlcScroll = new JScrollPane(dlc, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        dlcScroll.setComponentOrientation(
                Preferences.rightToLeftForText() ? ComponentOrientation.RIGHT_TO_LEFT
                        : ComponentOrientation.LEFT_TO_RIGHT);
        dlcScrollbar = dlcScroll.getHorizontalScrollBar();
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
        errScroll = new JScrollPane(err, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        errScroll.setComponentOrientation(
                Preferences.rightToLeftForText() ? ComponentOrientation.RIGHT_TO_LEFT
                        : ComponentOrientation.LEFT_TO_RIGHT);
        errScrollbar = errScroll.getHorizontalScrollBar();
        JPanel p=new JPanel(new GridLayout(2,1));
        p.add(errLabel);
        p.add(tags_err);
        tags_err.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				err.reset();
				loadERR();
			}
		});
        errPanel.add(p, BorderLayout.NORTH);
        JPanel tmp = new JPanel(new BorderLayout());
        tmp.setBorder(BorderFactory.createLoweredBevelBorder());
        tmp.add(errScroll, BorderLayout.CENTER);
        errPanel.add(tmp, BorderLayout.CENTER);
        return errPanel;
    }

    void loadDLF() {
        File FILE = new File(text_dir, "dlf");
        dlf.setFont(Config.getCurrentTextFont());
        String n = UnicodeIO.readFirstLine(new File(text_dir, "dlf.n"));
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
    }

    void loadDLC() {
        File FILE = new File(text_dir, "dlc");
        dlc.setFont(Config.getCurrentTextFont());
        String n = UnicodeIO.readFirstLine(new File(text_dir, "dlc.n"));
        String message = "DLC";
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
    }
    

    void loadERR() {
    	boolean tags_errors=tags_err.isSelected();
        File FILE = new File(text_dir, tags_errors?"tags_err":"err");
        err.setFont(Config.getCurrentTextFont());
        String n = UnicodeIO.readFirstLine(new File(text_dir, tags_errors?"tags_err.n":"err.n"));
        String message = tags_errors?"TAGS_ERR":"ERR";
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
    	
    }
    
    /**
     * Loads "dlf", "dlc" and "err" files contained in a directory, and shows
     * the frame
     *
     * @param text_dir directory to look in
     */
    void loadTextDic(File directory) {
    	this.text_dir=directory;
        loadDLF();
        loadDLC();
        loadERR();
        
        setTitle("Word Lists in " + text_dir);
        dlf.setComponentOrientation(
                Preferences.rightToLeftForText() ? ComponentOrientation.RIGHT_TO_LEFT
                        : ComponentOrientation.LEFT_TO_RIGHT);
        dlc.setComponentOrientation(
                Preferences.rightToLeftForText() ? ComponentOrientation.RIGHT_TO_LEFT
                        : ComponentOrientation.LEFT_TO_RIGHT);
        err.setComponentOrientation(
                Preferences.rightToLeftForText() ? ComponentOrientation.RIGHT_TO_LEFT
                        : ComponentOrientation.LEFT_TO_RIGHT);
        dlfScroll.setComponentOrientation(
                Preferences.rightToLeftForText() ? ComponentOrientation.RIGHT_TO_LEFT
                        : ComponentOrientation.LEFT_TO_RIGHT);
        dlcScroll.setComponentOrientation(
                Preferences.rightToLeftForText() ? ComponentOrientation.RIGHT_TO_LEFT
                        : ComponentOrientation.LEFT_TO_RIGHT);
        errScroll.setComponentOrientation(
                Preferences.rightToLeftForText() ? ComponentOrientation.RIGHT_TO_LEFT
                        : ComponentOrientation.LEFT_TO_RIGHT);
        Timer t = new Timer(400, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dlfScrollbar.setValue(0);
                dlcScrollbar.setValue(0);
                errScrollbar.setValue(0);
            }
        });
        t.setRepeats(false);
        t.start();
    }

    /**
     * Hides the frame
     */
    void hideFrame() {
    	text_dir=null;
        dlf.reset();
        dlc.reset();
        err.reset();
        setVisible(false);
        System.gc();
    }
}
