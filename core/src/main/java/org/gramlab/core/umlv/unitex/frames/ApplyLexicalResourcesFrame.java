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
package fr.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
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
import java.util.ArrayList;
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
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.config.PreferencesListener;
import fr.umlv.unitex.config.PreferencesManager;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.DicoCommand;
import fr.umlv.unitex.process.commands.MultiCommands;
import fr.umlv.unitex.process.commands.SortTxtCommand;
import fr.umlv.unitex.process.commands.Txt2TfstCommand;
import fr.umlv.unitex.text.BigTextArea;

/**
 * This class defines the "Apply Lexical Resources" frame, accessible from the
 * "Text" menu of Unitex. This frame shows two dictionary list. The first
 * contains all the ".bin" files found in the directory
 * <code>(user dir)/(current language dir)/Dela</code>. The second gives all the
 * ".bin" files found in the directory
 * <code>(system dir)/(current language dir)/Dela</code>. The user can select
 * the dictionaries that will be applied to the text. The "Clear" button reset
 * the selection to the empty selection. The "Default" button reset the
 * selection to the default selection. The "Set Default" button put the current
 * selection as the default one. The "Apply Selected Resources" button launch
 * the application of the selected dictionaries to the current corpus, which is
 * made by calling the <code>Dico</code> program through the creation of a
 * <code>ProcessInfoFrame</code> object.
 * 
 * @author Sébastien Paumier
 */
public class ApplyLexicalResourcesFrame extends JInternalFrame {
	JList userDicList;
	JList systemDicList;
	BigTextArea credits;
	private final String noCreditMessage = "No available description for the dictionary \"";
	private final String[] dicInfoExtensions = new String[] { "txt", "info" };
      
	// final String lexicalDir = getLexicalDir();
	ApplyLexicalResourcesFrame() {
		super("Lexical Resources", true, true);
		setContentPane(constructMainPanel());
		pack();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	private JPanel constructMainPanel() {
		final JPanel main = new JPanel(new BorderLayout());
		main.add(constructInfoPanel(), BorderLayout.NORTH);
		main.add(constructDicPanel(), BorderLayout.CENTER);
		main.add(constructButtonsPanel(), BorderLayout.SOUTH);
		main.setPreferredSize(new Dimension(390, 460));
		return main;
	}

	private JPanel constructInfoPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		final JTextArea text = new JTextArea(
				"Select the dictionaries to be applied. You can sort them one by one using the arrows. Note that system dictionaries are given to the Dico program before the user ones.");
		text.setFont(new JLabel().getFont());
		text.setEditable(false);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		text.setBackground(panel.getBackground());
		panel.add(text, BorderLayout.CENTER);
		return panel;
	}

	/**
	 * This method takes a dictionary list that comes from a configuration file
	 * and merges it with the list on dictionaries that are actually present on
	 * the disk. If a dictionary is found in the list and not on the disk, it is
	 * removed from the list. If one is found on the disk and not in the list,
	 * it is appended at the end of the list. If a dictionary is present in both
	 * list and disk, nothing is done.
	 * 
	 * @param list
	 * @param dicOnDisk
	 * @return the new list
	 */
	private Vector<String> merge(Vector<String> list, Vector<String> dicOnDisk) {
		if (list == null) {
			// if the list is empty, then we put in it all the
			// dictionaries that are on the disk
			return dicOnDisk;
		}
		// first, we remove every element of the list that does
		// appear on disk
		int i = 0;
		while (i < list.size()) {
			if (!dicOnDisk.contains((list.elementAt(i)))) {
				list.remove(i);
			} else {
				i++;
			}
		}
		// then, we look for dictionaries that are on disk but not in
		// the list
		while (!dicOnDisk.isEmpty()) {
			final String dic = dicOnDisk.remove(0);
			if (!list.contains(dic)) {
				list.addElement(dic);
			}
		}
		return list;
	}

	/**
	 * Refreshes the two dictionary lists.
	 */
	void refreshDicLists() {
		Vector<String> userListDef;
		Vector<String> systemListDef;
		Vector<String> userList;
		Vector<String> systemList;
		userListDef = loadDicList(new File(Config.getUserCurrentLanguageDir(),
				"user_dic.def"));
		systemListDef = loadDicList(new File(
				Config.getUserCurrentLanguageDir(), "system_dic.def"));
		userList = loadDicList(new File(Config.getUserCurrentLanguageDir(),
				"user_dic_list.txt"));
		final Vector<String> userDicOnDisk = getDicList(new File(
				Config.getUserCurrentLanguageDir(), "Dela" /* lexicalDir */));
		userList = merge(userList, userDicOnDisk);
		systemList = loadDicList(new File(Config.getUserCurrentLanguageDir(),
				"system_dic_list.txt"));
		final Vector<String> systemDicOnDisk = getDicList(new File(
				Config.getUnitexCurrentLanguageDir(), "Dela" /* lexicalDir */));
		systemList = merge(systemList, systemDicOnDisk);
		setContent(userDicList, userList);
		setContent(systemDicList, systemList);
		userDicList.clearSelection();
		systemDicList.clearSelection();
		setDefaultSelection(userDicList, userListDef);
		setDefaultSelection(systemDicList, systemListDef);
		credits.setText("");
	}

	private JPanel constructCreditsPanel() {
		final JPanel p = new JPanel(new BorderLayout());
		p.add(new JLabel(
				"Right-click a dictionary to get information about it :"),
				BorderLayout.NORTH);
		credits = new BigTextArea();
		PreferencesManager.addPreferencesListener(new PreferencesListener() {
			@Override
			public void preferencesChanged(String language) {
				credits.setFont(ConfigManager.getManager().getTextFont(null));
			}
		});
		p.add(credits, BorderLayout.CENTER);
		return p;
	}

	private JSplitPane constructDicPanel() {
		final JSplitPane p = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				constructDicListPanel(), constructCreditsPanel());
		p.setDividerLocation(250);
		return p;
	}

	private void setContent(JList list, Vector<String> dics) {
		final DefaultListModel model = new DefaultListModel();
		final int size = dics.size();
		for (int i = 0; i < size; i++) {
			model.addElement(dics.elementAt(i));
		}
		list.setModel(model);
	}

	private JPanel constructDicListPanel() {
		final JPanel dicListPanel = new JPanel(new GridLayout(1, 2));
		final JPanel userButtonsPanel = new JPanel(null);
		userButtonsPanel.setLayout(new BoxLayout(userButtonsPanel,
				BoxLayout.Y_AXIS));
		final JButton userUpButton = new JButton("\u25B2");
		userUpButton.setEnabled(false);
		userUpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final int index = userDicList.getSelectedIndex();
				if (index <= 0) {
					throw new IllegalStateException("Should not happen !");
				}
				final DefaultListModel model = (DefaultListModel) userDicList
						.getModel();
				final Object o = model.remove(index);
				model.insertElementAt(o, index - 1);
				userDicList.setSelectedIndex(index - 1);
				saveListToFile(userDicList,
						new File(Config.getUserCurrentLanguageDir(),
								"user_dic_list.txt"));
			}
		});
		final JButton userDownButton = new JButton("\u25BC");
		userDownButton.setEnabled(false);
		userDownButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final int index = userDicList.getSelectedIndex();
				if (index == -1
						|| index == userDicList.getModel().getSize() - 1) {
					throw new IllegalStateException("Should not happen !");
				}
				final DefaultListModel model = (DefaultListModel) userDicList
						.getModel();
				final Object o = model.remove(index);
				model.insertElementAt(o, index + 1);
				userDicList.setSelectedIndex(index + 1);
				saveListToFile(userDicList,
						new File(Config.getUserCurrentLanguageDir(),
								"user_dic_list.txt"));
			}
		});
		userButtonsPanel.add(userUpButton);
		userButtonsPanel.add(userDownButton);
		userDicList = new JList();
		userDicList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				final int min = userDicList.getMinSelectionIndex();
				final int max = userDicList.getMaxSelectionIndex();
				final boolean enabledUp = (min > 0) && (min == max);
				userUpButton.setEnabled(enabledUp);
				final boolean enabledDown = (min != -1)
						&& (max < userDicList.getModel().getSize() - 1)
						&& (min == max);
				userDownButton.setEnabled(enabledDown);
			}
		});
		final MouseListener userDicListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() != MouseEvent.BUTTON1) {
					final int index = userDicList.locationToIndex(e.getPoint());
					if (index != -1) {
						final String s = (String) (userDicList.getModel()
								.getElementAt(index));
						final String s2 = FileUtil
								.getFileNameWithoutExtension(s);
            boolean hasDicInfo = false;
            for (String dicInfoExtension : dicInfoExtensions) {
              final File dicInfoFile = new File(new File(
                         Config.getUserCurrentLanguageDir(),
                         "Dela" /* lexicalDir */), s2 + "." + dicInfoExtension);
              if (dicInfoFile.exists()) {
                credits.setText("");
                credits.load(dicInfoFile);
                hasDicInfo = true;
                break;
              }
            }
            if (!hasDicInfo) {
              credits.setText(noCreditMessage + s + "\"");
            }
					} else {
						credits.setText("");
					}
				}
			}
		};
		userDicList.addMouseListener(userDicListener);
		final JPanel systemButtonsPanel = new JPanel(null);
		systemButtonsPanel.setLayout(new BoxLayout(systemButtonsPanel,
				BoxLayout.Y_AXIS));
		final JButton systemUpButton = new JButton("\u25B2");
		systemUpButton.setEnabled(false);
		systemUpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final int index = systemDicList.getSelectedIndex();
				if (index <= 0) {
					throw new IllegalStateException("Should not happen !");
				}
				final DefaultListModel model = (DefaultListModel) systemDicList
						.getModel();
				final Object o = model.remove(index);
				model.insertElementAt(o, index - 1);
				systemDicList.setSelectedIndex(index - 1);
				saveListToFile(systemDicList,
						new File(Config.getUserCurrentLanguageDir(),
								"system_dic_list.txt"));
			}
		});
		final JButton systemDownButton = new JButton("\u25BC");
		systemDownButton.setEnabled(false);
		systemDownButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final int index = systemDicList.getSelectedIndex();
				if (index == -1
						|| index == systemDicList.getModel().getSize() - 1) {
					throw new IllegalStateException("Should not happen !");
				}
				final DefaultListModel model = (DefaultListModel) systemDicList
						.getModel();
				final Object o = model.remove(index);
				model.insertElementAt(o, index + 1);
				systemDicList.setSelectedIndex(index + 1);
				saveListToFile(systemDicList,
						new File(Config.getUserCurrentLanguageDir(),
								"system_dic_list.txt"));
			}
		});
		systemButtonsPanel.add(systemUpButton);
		systemButtonsPanel.add(systemDownButton);
		systemDicList = new JList(new DefaultListModel());
		systemDicList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				final int min = systemDicList.getMinSelectionIndex();
				final int max = systemDicList.getMaxSelectionIndex();
				final boolean enabledUp = (min > 0) && (min == max);
				systemUpButton.setEnabled(enabledUp);
				final boolean enabledDown = (min != -1)
						&& (max < systemDicList.getModel().getSize() - 1)
						&& (min == max);
				systemDownButton.setEnabled(enabledDown);
			}
		});
		final MouseListener systemDicListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() != MouseEvent.BUTTON1) {
					final int index = systemDicList.locationToIndex(e
							.getPoint());
					final String s = (String) (systemDicList.getModel()
							.getElementAt(index));
					if (index != -1) {
						final String s2 = FileUtil
								.getFileNameWithoutExtension(s);
            boolean hasDicInfo = false;
            for (String dicInfoExtension : dicInfoExtensions) {
              final File dicInfoFile = new File(new File(
                         Config.getUnitexCurrentLanguageDir(),
                         "Dela" /* lexicalDir */), s2 + "." + dicInfoExtension);
              if (dicInfoFile.exists()) {
                credits.setText("");
                credits.load(dicInfoFile);
                hasDicInfo = true;
                break;
              }
            }
            if (!hasDicInfo) {
              credits.setText(noCreditMessage + s + "\"");
            }
					} else {
						credits.setText("");
					}
				}
			}
		};
		systemDicList.addMouseListener(systemDicListener);
		userDicList.setBorder(BorderFactory.createLoweredBevelBorder());
		systemDicList.setBorder(BorderFactory.createLoweredBevelBorder());
		final JPanel userPanel = new JPanel(new BorderLayout());
		final JPanel systemPanel = new JPanel(new BorderLayout());
		userPanel.setBorder(new TitledBorder("User resources"));
		systemPanel.setBorder(new TitledBorder("System resources"));
		final JScrollPane scroll_1 = new JScrollPane(userDicList);
		scroll_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		final JScrollPane scroll_2 = new JScrollPane(systemDicList);
		scroll_2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll_2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		userPanel.add(scroll_1, BorderLayout.CENTER);
		userPanel.add(userButtonsPanel, BorderLayout.EAST);
		systemPanel.add(scroll_2, BorderLayout.CENTER);
		systemPanel.add(systemButtonsPanel, BorderLayout.EAST);
		dicListPanel.add(userPanel);
		dicListPanel.add(systemPanel);
		return dicListPanel;
	}

	private JPanel constructButtonsPanel() {
		final JPanel buttons = new JPanel(new GridLayout(1, 4));
		final Action clearAction = new AbstractAction("Clear") {
			@Override
			public void actionPerformed(ActionEvent e) {
				userDicList.clearSelection();
				systemDicList.clearSelection();
				credits.setText("");
			}
		};
		final JButton clearButton = new JButton(clearAction);
		final Action defaultAction = new AbstractAction("Default") {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshDicLists();
				credits.setText("");
			}
		};
		final JButton defaultButton = new JButton(defaultAction);
		final Action setDefaultAction = new AbstractAction("Set Default") {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveDefaultDicLists();
			}
		};
		final JButton setDefaultButton = new JButton(setDefaultAction);
		buttons.add(clearButton);
		buttons.add(defaultButton);
		buttons.add(setDefaultButton);
		buttons.add(constructGoButton());
		return buttons;
	}

	private JButton constructGoButton() {
		final Action goAction = new AbstractAction("Apply") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.closeTextDicFrame();
				MultiCommands commands;
				commands = getRunCmd();
				if (commands.numberOfCommands() == 0)
					return;
				if (ConfigManager.getManager().isKorean(null)) {
					/*
					 * As we construct the text automaton for Korean, we must
					 * close the text automaton frame, if any
					 */
					GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
							.closeTextAutomatonFrame();
					GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
							.closeTfstTagsFrame();
					/* We also have to rebuild the text automaton */
					Config.cleanTfstFiles(true);
					final Txt2TfstCommand txtCmd = new Txt2TfstCommand()
							.text(Config.getCurrentSnt())
							.alphabet(
									ConfigManager.getManager()
											.getAlphabet(null)).clean(true)
							.korean();
					commands.addCommand(txtCmd);
				}
				Launcher.exec(commands, true, new ApplyLexicalResourcesDo(
						Config.getCurrentSntDir()));
			}
		};
		return new JButton(goAction);
	}

	/**
	 * Builds the command lines for applying dictionaries for non agglutinative
	 * languages.
	 * 
	 * @return a <code>MultiCommands</code> object that contains the command
	 *         lines
	 */
	MultiCommands getRunCmd() {
		final MultiCommands commands = new MultiCommands();
		final Object[] userSelection = userDicList.getSelectedValues();
		final Object[] systemSelection = systemDicList.getSelectedValues();
		if ((userSelection == null || userSelection.length == 0)
				&& (systemSelection == null || systemSelection.length == 0)) {
			/* If there is no dictionary selected, we do nothing */
			return commands;
		}
		DicoCommand cmd = new DicoCommand()
				.snt(Config.getCurrentSnt())
				.alphabet(ConfigManager.getManager().getAlphabet(null))
				.morphologicalDic(
						ConfigManager.getManager().morphologicalDictionaries(
								null));
		if (ConfigManager.getManager().isKorean(null)) {
			cmd = cmd.korean();
		}
		if (ConfigManager.getManager().isArabic(null)) {
			cmd = cmd.arabic(new File(Config.getUserCurrentLanguageDir(),
					"arabic_typo_rules.txt"));
		}
		if (ConfigManager.getManager().isSemiticLanguage(null)) {
			cmd = cmd.semitic();
		}
		if (systemSelection != null && systemSelection.length != 0) {
			for (final Object aSystemSelection : systemSelection) {
				cmd = cmd.systemDictionary((String) aSystemSelection);
			}
		}
		if (userSelection != null && userSelection.length != 0) {
			for (final Object anUserSelection : userSelection) {
				cmd = cmd.userDictionary((String) anUserSelection);
			}
		}
		commands.addCommand(cmd);
		/* Sorting dictionaries dlf, dlc and err */
		final File alph = new File(Config.getUserCurrentLanguageDir(),
				"Alphabet_sort.txt");
		SortTxtCommand sortCmd = new SortTxtCommand().file(
				new File(Config.getCurrentSntDir(), "dlf")).saveNumberOfLines(
				new File(Config.getCurrentSntDir(), "dlf.n"));
		if (Config.getCurrentLanguage().equals("Thai")) {
			sortCmd = sortCmd.thai(true);
		} else {
			sortCmd = sortCmd.sortAlphabet(alph);
		}
		commands.addCommand(sortCmd);
		SortTxtCommand sortCmd2 = new SortTxtCommand().file(
				new File(Config.getCurrentSntDir(), "dlc")).saveNumberOfLines(
				new File(Config.getCurrentSntDir(), "dlc.n"));
		if (Config.getCurrentLanguage().equals("Thai")) {
			sortCmd2 = sortCmd2.thai(true);
		} else {
			sortCmd2 = sortCmd2.sortAlphabet(alph);
		}
		commands.addCommand(sortCmd2);
		SortTxtCommand sortCmd3 = new SortTxtCommand().file(
				new File(Config.getCurrentSntDir(), "err")).saveNumberOfLines(
				new File(Config.getCurrentSntDir(), "err.n"));
		if (Config.getCurrentLanguage().equals("Thai")) {
			sortCmd3 = sortCmd3.thai(true);
		} else {
			sortCmd3 = sortCmd3.sortAlphabet(alph);
		}
		commands.addCommand(sortCmd3);
		SortTxtCommand sortCmd4 = new SortTxtCommand().file(
				new File(Config.getCurrentSntDir(), "tags_err"))
				.saveNumberOfLines(
						new File(Config.getCurrentSntDir(), "tags_err.n"));
		if (Config.getCurrentLanguage().equals("Thai")) {
			sortCmd4 = sortCmd4.thai(true);
		} else {
			sortCmd4 = sortCmd4.sortAlphabet(alph);
		}
		commands.addCommand(sortCmd4);
		return (commands);
	}

	/**
	 * Gets a list of all ".bin" and ".fst2" files found in a directory.
	 * 
	 * @param dir
	 *            the directory to be scanned
	 * @return a <code>Vector</code> containing file names.
	 */
	Vector<String> getDicList(File dir) {
		final Vector<String> v = new Vector<String>();
		if (!dir.exists()) {
			return v;
		}
		final File files_list[] = dir.listFiles();
		for (final File aFiles_list : files_list) {
			final String name = aFiles_list.getAbsolutePath();
			if (!aFiles_list.isDirectory()
					&& (name.endsWith(".bin") || name.endsWith(".BIN")
							|| name.endsWith(".fst2") || name.endsWith(".FST2"))) {
				v.add(aFiles_list.getName());
			}
		}
		return v;
	}

	/**
	 * Loads a dictionary list.
	 * 
	 * @param name
	 *            the name of a file containing one ".bin" file name per line
	 * @return a <code>Vector</code> containing file names.
	 */
	Vector<String> loadDicList(File name) {
		Vector<String> v = null;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(name));
		} catch (final FileNotFoundException e) {
			return null;
		}
		try {
			String s;
			v = new Vector<String>();
			while ((s = br.readLine()) != null) {
				v.add(s);
			}
			br.close();
		} catch (final IOException e) {
			return null;
		}
		return v;
	}

	/**
	 * Selects in a list all files contained in a file name list. IMPORTANT:
	 * this method does not clear the <code>JList</code>. You must call first
	 * the <code>JList.clearSelection()</code> method.
	 * 
	 * @param list
	 *            the <code>JList</code> that contains file names.
	 * @param v
	 *            the <code>Vector</code> containing a list of the file name to
	 *            be selected.
	 */
	void setDefaultSelection(JList list, Vector<String> v) {
		final ArrayList<Integer> indices = new ArrayList<Integer>();
		if (v == null)
			return;
		final ListModel model = list.getModel();
		while (!v.isEmpty()) {
			final String s = v.remove(0);
			final int index = getElementIndex(model, s);
			if (index != -1) {
				indices.add(index);
			}
		}
		if (indices.size() != 0) {
			final int[] res = new int[indices.size()];
			for (int i = 0; i < indices.size(); i++) {
				res[i] = indices.get(i);
			}
			list.setSelectedIndices(res);
		}
	}

	/**
	 * Looks for a file name in a <code>ListModel</code>.
	 * 
	 * @param model
	 *            the <code>ListModel</code>
	 * @param s
	 *            the file name
	 * @return the position in the <code>ListModel</code> if the file name were
	 *         found, -1 otherwise
	 */
	int getElementIndex(ListModel model, String s) {
		if (model == null)
			return -1;
		final int l = model.getSize();
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
		saveSelectionToFile(userDicList,
				new File(Config.getUserCurrentLanguageDir(), "user_dic.def"));
		saveSelectionToFile(systemDicList,
				new File(Config.getUserCurrentLanguageDir(), "system_dic.def"));
	}

	/**
	 * Saves a file selection into a text file, storing one file name per line.
	 * Only selected items of the <code>JList</code> are taken into account.
	 * 
	 * @param list
	 *            the file list
	 * @param file
	 *            the output file
	 */
	void saveSelectionToFile(JList list, File file) {
		final Object[] selection = list.getSelectedValues();
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			final BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (final Object aSelection : selection) {
				final String s = aSelection + "\n";
				bw.write(s, 0, s.length());
			}
			bw.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves a file selection into a text file, storing one file name per line.
	 * Only selected items of the <code>JList</code> are taken into account.
	 * 
	 * @param list
	 *            the file list
	 * @param file
	 *            the output file
	 */
	void saveListToFile(JList list, File file) {
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			final BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			final ListModel model = list.getModel();
			final int size = model.getSize();
			for (int i = 0; i < size; i++) {
				final String s = model.getElementAt(i) + "\n";
				bw.write(s, 0, s.length());
			}
			bw.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	class ApplyLexicalResourcesDo implements ToDo {
		File sntDir;

		public ApplyLexicalResourcesDo(File sntDir) {
			this.sntDir = sntDir;
		}

		@Override
		public void toDo(boolean success) {
			GlobalProjectManager.search(sntDir)
					.getFrameManagerAs(InternalFrameManager.class)
					.newTextDicFrame(sntDir,false);
			if (ConfigManager.getManager().isKorean(null)) {
				GlobalProjectManager.search(sntDir)
						.getFrameManagerAs(InternalFrameManager.class)
						.newTextAutomatonFrame(1, false);
				GlobalProjectManager.search(sntDir)
						.getFrameManagerAs(InternalFrameManager.class)
						.newTfstTagsFrame(new File(sntDir, "tfst_tags_by_freq.txt"));
			}
		}
	}
}
