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
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.MyCursors;
import fr.umlv.unitex.editor.EditionTextArea;
import fr.umlv.unitex.editor.FileEditionMenu;
import fr.umlv.unitex.editor.FileManager;

/*
 * This class is used to display the text
 *  
 */

public class FileEditionTextFrame extends JInternalFrame {
    /**
     * Area where the text is stored and can be edited
     */
    private final EditionTextArea text;
    private FileManager fileManager;
    private File file;
    private final Action saveAction = new AbstractAction("Save", MyCursors.saveIcon) {
        public void actionPerformed(ActionEvent e) {
            saveFile(file);
        }
    };
    Action saveAsAction = new AbstractAction("Save as...") {
        public void actionPerformed(ActionEvent e) {
            saveFile(null);
        }
    };
    private final Action cutAction = new AbstractAction("Cut", MyCursors.cutIcon) {
        public void actionPerformed(ActionEvent e) {
            text.cut();
        }
    };
    private final Action copyAction = new AbstractAction("Copy", MyCursors.copyIcon) {
        public void actionPerformed(ActionEvent e) {
            text.copy();
        }
    };
    private final Action pasteAction = new AbstractAction("Paste", MyCursors.pasteIcon) {
        public void actionPerformed(ActionEvent e) {
            text.paste();
        }
    };
    private final Action findAction = new AbstractAction("Find", MyCursors.findIcon) {
        public void actionPerformed(ActionEvent e) {
            UnitexFrame.getFrameManager().newFindDialog(
                    FileEditionTextFrame.this);
        }
    };

    FileEditionTextFrame(File file) {
        super("", true, true, true, true);
        text = new EditionTextArea();
        this.file = file;
        if (file == null) {
            this.setTitle("New File");
        } else {
            this.setTitle(file.getAbsolutePath());
        }
        init();
    }

    private void init() {
        fileManager = new FileManager();
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(new EmptyBorder(2, 2, 2, 2));
        JScrollPane scroll = new JScrollPane(text);
        scroll
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel middle = new JPanel(new BorderLayout());
        middle.setBorder(BorderFactory.createLoweredBevelBorder());
        middle.add(scroll);
        setJMenuBar(initMenuBar());
        top.add(middle, BorderLayout.CENTER);
        JToolBar toolBar = initToolBar();
        top.add(toolBar, BorderLayout.NORTH);
        setContentPane(top);
        pack();
        setBounds(100, 100, 800, 600);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                if (text.isModified()) {
                    Object[] options_on_exit = {"Save", "Don't save"};
                    Object[] normal_options = {"Save", "Don't save", "Cancel"};
                    int n;
                    if (UnitexFrame.closing) {
                        n = JOptionPane
                                .showOptionDialog(
                                        FileEditionTextFrame.this,
                                        "Text has been modified. Do you want to save it ?",
                                        "", JOptionPane.YES_NO_CANCEL_OPTION,
                                        JOptionPane.QUESTION_MESSAGE, null,
                                        options_on_exit, options_on_exit[0]);
                    } else {
                        n = JOptionPane
                                .showOptionDialog(
                                        FileEditionTextFrame.this,
                                        "Text has been modified. Do you want to save it ?",
                                        "", JOptionPane.YES_NO_CANCEL_OPTION,
                                        JOptionPane.QUESTION_MESSAGE, null,
                                        normal_options, normal_options[0]);
                    }
                    if (n == JOptionPane.CLOSED_OPTION)
                        return;
                    if (n == 0) {
                        saveFile(file);
                        dispose();
                        return;
                    }
                    if (n != 2) {
                        dispose();
                        return;
                    }
                    return;
                }
                dispose();
            }
        });
    }

    private JMenuBar initMenuBar() {
        JMenuBar jb = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newFrame = new JMenuItem("New");
        newFrame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                fileManager.newFile();
            }
        });
        JMenuItem open = new JMenuItem("Open...");
        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                FileEditionMenu.openFile();
            }
        });
        JMenuItem close = new JMenuItem("Close");
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                dispose();
            }
        });
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile(file);
            }
        });
        JMenuItem saveAs = new JMenuItem("Save As...");
        saveAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile(null);
            }
        });
        fileMenu.add(newFrame);
        fileMenu.add(open);
        fileMenu.add(save);
        fileMenu.add(saveAs);
        fileMenu.add(close);
        JMenu edit = new JMenu("Edit");
        JMenuItem cut = new JMenuItem(cutAction);
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                InputEvent.CTRL_MASK));
        JMenuItem copy = new JMenuItem(copyAction);
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                InputEvent.CTRL_MASK));
        edit.addSeparator();
        JMenuItem paste = new JMenuItem(pasteAction);
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                InputEvent.CTRL_MASK));
        JMenuItem find = new JMenuItem(findAction);
        find.setMnemonic('f');
        find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
                InputEvent.CTRL_MASK));
        edit.add(cut);
        edit.add(copy);
        edit.add(paste);
        edit.addSeparator();
        edit.add(find);
        jb.add(fileMenu);
        jb.add(edit);
        return jb;
    }

    /**
     * Initialization of the tool bar
     */
    private JToolBar initToolBar() {
        JToolBar myToolBar = new JToolBar("file Edition tool bar");
        myToolBar.setMargin(new Insets(0, 0, 0, 0));
        JButton save = new JButton(saveAction);
        save.setHideActionText(true);
        initToolBarIcone(save);
        JButton copy = new JButton(copyAction);
        copy.setHideActionText(true);
        initToolBarIcone(copy);
        JButton cut = new JButton(cutAction);
        cut.setHideActionText(true);
        initToolBarIcone(cut);
        JButton paste = new JButton(pasteAction);
        paste.setHideActionText(true);
        initToolBarIcone(paste);
        JButton find = new JButton(findAction);
        find.setHideActionText(true);
        initToolBarIcone(find);
        save.setToolTipText("Save text");
        copy.setToolTipText("Copy");
        cut.setToolTipText("Cut");
        paste.setToolTipText("Paste");
        find.setToolTipText("Find");
        myToolBar.add(save);
        myToolBar.add(copy);
        myToolBar.add(cut);
        myToolBar.add(paste);
        myToolBar.add(find);
        text.setFont(Config.getCurrentTextFont());
        text.setLineWrap(true);
        return myToolBar;
    }

    private void initToolBarIcone(JButton button) {
        button.setMaximumSize(new Dimension(36, 36));
        button.setMinimumSize(new Dimension(36, 36));
        button.setPreferredSize(new Dimension(36, 36));
    }

    /**
     * Save a file
     */
    void saveFile(File f) {
        if (f == null) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(Config.getCurrentCorpusDir());
            chooser.setMultiSelectionEnabled(false);
            chooser.setDialogType(JFileChooser.SAVE_DIALOG);
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                // we return if the user has clicked on CANCEL
                return;
            }
            f = chooser.getSelectedFile();
        }
        this.file = f;
        setTitle(f.getAbsolutePath());
        fileManager.save(f.getAbsolutePath());
    }

    /**
     * Returns the text.
     *
     * @return MyTextArea
     */
    public EditionTextArea getText() {
        return text;
    }

    File getFile() {
        return file;
    }

}
