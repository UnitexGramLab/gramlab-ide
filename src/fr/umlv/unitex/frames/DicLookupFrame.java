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
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.config.Preferences;
import fr.umlv.unitex.config.PreferencesListener;
import fr.umlv.unitex.config.PreferencesManager;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.listeners.FontListener;
import fr.umlv.unitex.listeners.LanguageListener;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.DicoCommand;
import fr.umlv.unitex.process.commands.MultiCommands;
import fr.umlv.unitex.process.commands.SortTxtCommand;
import fr.umlv.unitex.process.commands.Txt2TfstCommand;
import fr.umlv.unitex.text.BigTextArea;
import fr.umlv.unitex.text.BigTextList;

public class DicLookupFrame extends JInternalFrame {

    private JList userDicList;
    private JList systemDicList;

    DicLookupFrame() {
        super("Dictionary Lookup", true, true);
        setContentPane(constructMainPanel());
        refreshDicLists();
        pack();
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        Config.addLanguageListener(new LanguageListener() {
			public void languageChanged() {
				refreshDicLists();
				setVisible(false);
			}
		});
    }

    private JPanel constructMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        main.add(constructInfoPanel(), BorderLayout.NORTH);
        main.add(constructDicListPanel(), BorderLayout.CENTER);
        main.add(constructLookupPanel(), BorderLayout.SOUTH);
        main.setPreferredSize(new Dimension(550,400));
        return main;
    }

    private JPanel constructInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JLabel text = new JLabel("Select dictionaries to look up into:");
        panel.add(text, BorderLayout.CENTER);
        return panel;
    }


    /**
     * Refreshes the two dictionary lists.
     */
    void refreshDicLists() {
        Vector<String> userDicOnDisk = getDicList(new File(Config
                .getUserCurrentLanguageDir(), "Dela"));
        Vector<String> systemDicOnDisk = getDicList(new File(Config
                .getUnitexCurrentLanguageDir(), "Dela"));
        setContent(userDicList, userDicOnDisk);
        setContent(systemDicList, systemDicOnDisk);
        userDicList.clearSelection();
        systemDicList.clearSelection();
    }


    private void setContent(JList list, Vector<String> dics) {
        DefaultListModel model = new DefaultListModel();
        int size = dics.size();
        for (int i = 0; i < size; i++) {
            model.addElement(dics.elementAt(i));
        }
        list.setModel(model);
    }

    private JPanel constructDicListPanel() {
        JPanel dicListPanel = new JPanel(new GridLayout(1, 2));
        userDicList = new JList(new DefaultListModel());
        systemDicList = new JList(new DefaultListModel());
        userDicList.setBorder(BorderFactory.createLoweredBevelBorder());
        systemDicList.setBorder(BorderFactory.createLoweredBevelBorder());
        JPanel userPanel = new JPanel(new BorderLayout());
        JPanel systemPanel = new JPanel(new BorderLayout());
        userPanel.setBorder(new TitledBorder("User resources"));
        systemPanel.setBorder(new TitledBorder("System resources"));
        JScrollPane scroll_1 = new JScrollPane(userDicList);
        scroll_1
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll_1
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        JScrollPane scroll_2 = new JScrollPane(systemDicList);
        scroll_2
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll_2
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        userPanel.add(scroll_1, BorderLayout.CENTER);
        systemPanel.add(scroll_2, BorderLayout.CENTER);
        dicListPanel.add(userPanel);
        dicListPanel.add(systemPanel);
        return dicListPanel;
    }

    private JPanel constructLookupPanel() {
    	JPanel panel=new JPanel(new BorderLayout());
        final BigTextList text = new BigTextList(true);
        final JScrollPane scrollText = new JScrollPane(text, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        final JScrollBar scrollBar=scrollText.getVerticalScrollBar();
        final JTextField inputText=new JTextField();
        Font font=ConfigManager.getManager().getTextFont(null);
        text.setFont(font);
        inputText.setFont(font);
        text.setFixedCellHeight(inputText.getPreferredSize().height);
        PreferencesManager.addPreferencesListener(new PreferencesListener() {
			public void preferencesChanged(String language) {
				Font f=ConfigManager.getManager().getTextFont(null);
                text.setFont(f);
                inputText.setFont(f);
                text.setFixedCellHeight(inputText.getPreferredSize().height);
                boolean rightToLeftForText=ConfigManager.getManager().isRightToLeftForText(null);
                text.setComponentOrientation(rightToLeftForText ? ComponentOrientation.RIGHT_TO_LEFT
                                : ComponentOrientation.LEFT_TO_RIGHT);
                scrollText.setComponentOrientation(rightToLeftForText ? ComponentOrientation.RIGHT_TO_LEFT
                                : ComponentOrientation.LEFT_TO_RIGHT);
                text.repaint();
                Timer t2 = new Timer(400, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        scrollBar.setValue(0);
                    }
                });
                t2.setRepeats(false);
                t2.start();
            }
        });
        
        
        
        panel.add(scrollText,BorderLayout.CENTER);
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(2,2,2,2);
        Action clearAction = new AbstractAction("Clear selection") {
            public void actionPerformed(ActionEvent e) {
                userDicList.clearSelection();
                systemDicList.clearSelection();
            }
        };
        JButton clearButton = new JButton(clearAction);
        p.add(clearButton,gbc);
        Action refreshAction = new AbstractAction("Refresh lists") {
            public void actionPerformed(ActionEvent e) {
                refreshDicLists();
            }
        };
        JButton refreshButton = new JButton(refreshAction);
        p.add(refreshButton,gbc);
        p.add(new JLabel(" Word:"),gbc);
        gbc.fill=GridBagConstraints.HORIZONTAL;
        gbc.weightx=1;
        p.add(inputText,gbc);
        Action lookupAction = new AbstractAction("Lookup") {
            public void actionPerformed(ActionEvent arg0) {
            	text.reset();
            	String s=inputText.getText();
            	if (s.equals("")) return;
            	File f=new File(Config.getUserCurrentLanguageDir(),"dic_lookup.in");
            	FileUtil.write(s,f);
                DicoCommand cmd = getRunCmd();
                if (cmd==null) return;
                Launcher.execWithoutTracing(cmd);
                text.load(new File(Config.getUserCurrentLanguageDir(),"dic_lookup.out"));
            }
        };
        scrollText.setPreferredSize(new Dimension(300,120));
        panel.add(p,BorderLayout.NORTH);
        final Timer timer=new Timer(500,lookupAction);
        timer.setRepeats(false);
        inputText.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				if (timer.isRunning()) timer.stop();
				timer.start();
			}
		});
        inputText.setEnabled(false);
        ListSelectionListener listener=new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				inputText.setEnabled((userDicList.getSelectedIndex()!=-1 || systemDicList.getSelectedIndex()!=-1));
				if (timer.isRunning()) timer.stop();
				timer.start();
			}
		};
        userDicList.addListSelectionListener(listener);
        systemDicList.addListSelectionListener(listener);
        return panel;
    }


    DicoCommand getRunCmd() {
        Object[] userSelection = userDicList.getSelectedValues();
        Object[] systemSelection = systemDicList.getSelectedValues();
        if ((userSelection == null || userSelection.length == 0)
                && (systemSelection == null || systemSelection.length == 0)) {
            /* If there is no dictionary selected, we do nothing */
            return null;
        }
        DicoCommand cmd = new DicoCommand().snt(
                new File(Config.getUserCurrentLanguageDir(),"dic_lookup.in")).alphabet(
                		ConfigManager.getManager().getAlphabet(null));
        if (systemSelection != null && systemSelection.length != 0) {
            for (Object aSystemSelection : systemSelection) {
                cmd = cmd.systemDictionary((String) aSystemSelection);
            }
        }
        if (userSelection != null && userSelection.length != 0) {
            for (Object anUserSelection : userSelection) {
                cmd = cmd.userDictionary((String) anUserSelection);
            }
        }
        File f=new File(Config.getUserCurrentLanguageDir(),"dic_lookup.out");
        cmd=cmd.raw(f);
        return cmd;
    }


    /**
     * Gets a list of all ".bin" files found in a directory.
     *
     * @param dir the directory to be scanned
     * @return a <code>Vector</code> containing file names.
     */
    Vector<String> getDicList(File dir) {
        Vector<String> v = new Vector<String>();
        if (!dir.exists()) {
            return v;
        }
        File files_list[] = dir.listFiles();
        for (File aFiles_list : files_list) {
            String name = aFiles_list.getAbsolutePath();
            if (!aFiles_list.isDirectory()
                    && (name.endsWith(".bin") || name.endsWith(".BIN"))) {
                v.add(aFiles_list.getName());
            }
        }
        return v;
    }

    /**
     * Loads a dictionary list.
     *
     * @param name the name of a file containing one ".bin" file name per line
     * @return a <code>Vector</code> containing file names.
     */
    Vector<String> loadDicList(File name) {
        Vector<String> v = null;
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(name));
        } catch (FileNotFoundException e) {
            return null;
        }
        try {
            String s;
            v = new Vector<String>();
            while ((s = br.readLine()) != null) {
                v.add(s);
            }
            br.close();
        } catch (IOException e) {
            return null;
        }
        return v;
    }

    /**
     * Selects in a list all files contained in a file name list. IMPORTANT:
     * this method does not clear the <code>JList</code>. You must call first
     * the <code>JList.clearSelection()</code> method.
     *
     * @param list the <code>JList</code> that contains file names.
     * @param v    the <code>Vector</code> containing a list of the file name
     *             to be selected.
     */
    void setDefaultSelection(JList list, Vector<String> v) {
        int[] indices = new int[256];
        int i = 0;
        if (v == null)
            return;
        ListModel model = list.getModel();
        while (!v.isEmpty()) {
            String s = v.remove(0);
            int index = getElementIndex(model, s);
            if (index != -1) {
                indices[i++] = index;
            }
        }
        if (i != 0) {
            final int[] res = new int[i];
            System.arraycopy(indices, 0, res, 0, i);
            list.setSelectedIndices(res);
        }
    }

    /**
     * Looks for a file name in a <code>ListModel</code>.
     *
     * @param model the <code>ListModel</code>
     * @param s     the file name
     * @return the position in the <code>ListModel</code> if the file name
     *         were found, -1 otherwise
     */
    int getElementIndex(ListModel model, String s) {
        if (model == null)
            return -1;
        int l = model.getSize();
        for (int i = 0; i < l; i++) {
            if (s.equals(model.getElementAt(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Saves the current file selections as the default selections. The
     * selections are stored in the text files
     * <code>(user dir)/(current language dir)/user_dic.def</code> and
     * <code>(user dir)/(current language dir)/system_dic.def</code>.
     */
    void saveDefaultDicLists() {
        saveSelectionToFile(userDicList, new File(Config
                .getUserCurrentLanguageDir(), "user_dic.def"));
        saveSelectionToFile(systemDicList, new File(Config
                .getUserCurrentLanguageDir(), "system_dic.def"));
    }

    /**
     * Saves a file selection into a text file, storing one file name per line.
     * Only selected items of the <code>JList</code> are taken into account.
     *
     * @param list the file list
     * @param file the output file
     */
    void saveSelectionToFile(JList list, File file) {
        Object[] selection = list.getSelectedValues();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (Object aSelection : selection) {
                String s = aSelection + "\n";
                bw.write(s, 0, s.length());
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Saves a file selection into a text file, storing one file name per line.
     * Only selected items of the <code>JList</code> are taken into account.
     *
     * @param list the file list
     * @param file the output file
     */
    void saveListToFile(JList list, File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            ListModel model = list.getModel();
            int size = model.getSize();
            for (int i = 0; i < size; i++) {
                String s = model.getElementAt(i) + "\n";
                bw.write(s, 0, s.length());
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class ApplyLexicalResourcesDo implements ToDo {
        public void toDo() {
        	InternalFrameManager.getManager().newTextDicFrame(Config.getCurrentSntDir(), false);
            if (ConfigManager.getManager().isKorean(null)) {
            	InternalFrameManager.getManager().newTextAutomatonFrame(1, false);
            	InternalFrameManager.getManager().newTfstTagsFrame(
                        new File(Config.getCurrentSntDir(), "tfst_tags_by_freq.txt"));
            }
        }
    }

}