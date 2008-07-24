/*
 * Unitex
 * 
 * Copyright (C) 2001-2008 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 *  
 */
/**
 * @author hhuh
 *
 */
package fr.umlv.unitex;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import fr.umlv.unitex.process.*;

public class MergeRacineSuffixes extends JInternalFrame {

	static MergeRacineSuffixes frame;
	JList racDicList;
	JList sufDicList;
	JTextField outFileName;
	File savingFile;
	JFileChooser morDicSave;

	private MergeRacineSuffixes() {
		super("Append suffixes to stems", true, true);
		setContentPane(constructMainPanel());
		pack();
		setVisible(false);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				setVisible(false);
			}
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		morDicSave = new JFileChooser();
		morDicSave.setDialogType(JFileChooser.OPEN_DIALOG);
		morDicSave.setMultiSelectionEnabled(false);
		
		morDicSave.setDialogTitle("Select file");
	}

	/**
	 * Initializes the frame.
	 *  
	 */
	private static void init() {
		frame = new MergeRacineSuffixes();
		UnitexFrame.addInternalFrame(frame);
	}

	/**
	 * Shows the frame.
	 *  
	 */
	public static void showFrame() {
		if (frame == null) {
			init();
		}
		if (frame.isVisible()) {
			return;
		}
		frame.refreshDicLists();
		frame.setVisible(true);
		try {
			frame.setSelected(true);
			frame.setIcon(false);
		} catch (java.beans.PropertyVetoException e2) {
			e2.printStackTrace();
		}

	}

	private JPanel constructMainPanel() {
		JPanel main = new JPanel(new BorderLayout());
		main.setOpaque(true);
		main.add(constructDicListPanel(),BorderLayout.NORTH);
		main.add(constructOfileName(),BorderLayout.CENTER);		
		main.add(constructButtonsPanel(),BorderLayout.SOUTH);
		main.setPreferredSize(new Dimension(390,280));
		return main;
	}

	/**
	 * Refreshes the two dictionary lists.
	 */
	void refreshDicLists() {
		File dirflection = new File(Config
				.getUserCurrentLanguageDir(), "MorphemVariants");
		Vector<String> racList = getInfList(dirflection,".ric");
		Vector<String> sufList = getInfList(dirflection,".sic");
		racDicList.setListData(racList);
		sufDicList.setListData(sufList);
	}

	private JPanel constructDicListPanel() {
		JPanel dicListPanel = new JPanel(new GridLayout(1, 2));
		racDicList = new JList();
		sufDicList = new JList();
		File saveDir = new File(Config.getUserCurrentLanguageDir(),
						"Dela");
		savingFile = new File(saveDir,"saveFileName.mtb");
		outFileName = new JTextField(savingFile.getAbsolutePath());
		
		racDicList.setBorder(BorderFactory.createLoweredBevelBorder());
		sufDicList.setBorder(BorderFactory.createLoweredBevelBorder());

		JPanel racinePanel = new JPanel(new BorderLayout());
		JPanel suffixPanel = new JPanel(new BorderLayout());

		racinePanel.setBorder(new TitledBorder("Stem resources"));
		suffixPanel.setBorder(new TitledBorder("Suffix resources"));

		JScrollPane scroll_1 = new JScrollPane(racDicList);
		scroll_1.setHorizontalScrollBarPolicy
				(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll_1.setVerticalScrollBarPolicy
				(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		JScrollPane scroll_2 = new JScrollPane(sufDicList);
		scroll_2.setHorizontalScrollBarPolicy
		(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll_2.setVerticalScrollBarPolicy
		(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		racinePanel.add(scroll_1,BorderLayout.CENTER);
		suffixPanel.add(scroll_2,BorderLayout.CENTER);

		dicListPanel.add(racinePanel);
		dicListPanel.add(suffixPanel);

		return dicListPanel;
	}

	private JPanel constructButtonsPanel() {
		JPanel buttons = new JPanel(new GridLayout(1, 4));
		Action clearAction = new AbstractAction("Clear") {
			public void actionPerformed(ActionEvent e) {
				frame.racDicList.clearSelection();
				frame.sufDicList.clearSelection();
		
			}
		};
		JButton clearButton = new JButton(clearAction);
		Action defaultAction = new AbstractAction("Default") {
			public void actionPerformed(ActionEvent e) {
				frame.refreshDicLists();
			}
		};
		JButton defaultButton = new JButton(defaultAction);
		Action setDefaultAction = new AbstractAction("Set Default") {
			public void actionPerformed(ActionEvent e) {
				frame.saveDefaultDicLists();
			}
		};
		JButton setDefaultButton = new JButton(setDefaultAction);
		buttons.add(clearButton);
		buttons.add(defaultButton);
		buttons.add(setDefaultButton);
		buttons.add(constructGoButton());    
		return buttons;
	}

	private JButton constructGoButton() {
		Action goAction = new AbstractAction("Apply") {

			public void actionPerformed(ActionEvent arg0) {
				if(outFileName.getText().equals(savingFile.getAbsolutePath())
						||outFileName.getText().equals("")){
					JOptionPane.showMessageDialog(null, 
							"Set save file name!!!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				frame.setVisible(false);
				// post pone code
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {

						Object[] racSelection = racDicList
								.getSelectedValues();
						Object[] sufSelection = sufDicList
								.getSelectedValues();
						
						if ((racSelection == null || racSelection.length == 0)
								|| (sufSelection == null || sufSelection.length == 0)
								) {
							// if there is no dic selected, we do nothing
							return;
						}
						File dicinflection = new File(
								Config.getUserCurrentLanguageDir(),
								"MorphemVariants");
						File racListFile = new File(dicinflection,"rlist.txt");
						File sufListFile = new File(dicinflection,"slist.txt");
						File racOutFile = new File(dicinflection,"rac.bin");
						File sufOutFile = new File(dicinflection,"suf.bin");
						
//						if((racSelection != null) && (racSelection.length != 0))
							saveSelectionToFile(racDicList,racListFile);
//						if((sufSelection != null) && (sufSelection.length != 0))
							saveSelectionToFile(sufDicList,sufListFile);
						
						MultiCommands commands = new MultiCommands();
						
						CompressKrCommand raccmd = new CompressKrCommand()
							.avecList().dest(racOutFile).name(racListFile);
//						raccmd.element("-l");
//						raccmd.element("-o");
//						raccmd.dirs(racOutFile);
//						raccmd.dirs(racListFile);
						
						commands.addCommand(raccmd);
						CompressKrCommand sufcmd = new CompressKrCommand()
							.modeSuf().avecList().dest(sufOutFile).name(sufListFile);
//						CommandGen sufcmd = new CommandGen("compresskr");
//						sufcmd.element("-s");						
//						sufcmd.element("-o");
//						sufcmd.dirs(sufOutFile);
//						sufcmd.element("-l");
//						sufcmd.dirs(sufListFile);
						commands.addCommand(sufcmd);
						
						
						String name_suf = sufListFile.getAbsolutePath().substring(0,
								sufListFile.getAbsolutePath().length() - 4);
						name_suf=name_suf + ".bin";
						String name_rac = racListFile.getAbsolutePath().substring(0,
								racListFile.getAbsolutePath().length() - 4);
						name_rac=name_rac + ".bin";
						
//						mergcmd.element("-o");
//						File objfile = new File(Config.getUserCurrentLanguageDir(),
//						"Dela");
//						mergcmd.element(outFileName.getText());
//						mergcmd.dirs(racOutFile);
//						mergcmd.dirs(sufOutFile);
						MergeBinCommand mergcmd = new MergeBinCommand().dest(outFileName.getText())
						.InFileName(racOutFile).InFileName(sufOutFile);
						commands.addCommand(mergcmd);
						
						TextDicFrame.hideFrame();
						new ProcessInfoFrame(commands, false,
								null);
					}
				});
			}
		};
		return new JButton(goAction);
	}
	private JPanel constructOfileName() {
		JPanel ofilePanel = new JPanel(new BorderLayout());
		ofilePanel.setBorder(new TitledBorder("Resulting dictionary"));
//		outFileName.setPreferredSize(new Dimension(240, 25));
		ofilePanel.add(outFileName, BorderLayout.CENTER);
		Action setOutputFieldAction = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
					File fnadir = new File(outFileName.getText());
				    morDicSave.setCurrentDirectory(fnadir.getParentFile().getAbsoluteFile());
					
					int returnVal = morDicSave.showOpenDialog(null);
					if (returnVal != JFileChooser.APPROVE_OPTION) {
						// we return if the user has clicked on CANCEL
						return;
					}
					outFileName.setText(
							morDicSave.getSelectedFile()
							.getAbsolutePath());
				}
//				String getfi= JOptionPane.showInputDialog(outFileName.getText());
//				if(!getfi.equals(""))
//					outFileName.setText(getfi);
//	 		}
		};
		JButton setOptionField = new JButton(setOutputFieldAction);
		ofilePanel.add(setOptionField, BorderLayout.EAST);	

		return ofilePanel;
	}
	/**
	 * Gets a list of all ".sic" or ".ric" inflected form files found in a directory.
	 * 
	 * @param dir
	 *            the directory to be scanned
	 * @return a <code>Vector</code> containing file names.
	 */
	public Vector<String> getInfList(File dir,String extFilter) {
		Vector<String> v = new Vector<String>();
		if (!dir.exists())
			return v;
		File files_list[] = dir.listFiles();
		for (int i = 0; i < files_list.length; i++) {
			String name = files_list[i].getAbsolutePath();
			if (!files_list[i].isDirectory()
					&& name.endsWith(extFilter)) {
				v.add(files_list[i].getName());
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
	public Vector<String> loadDefaultDicList(File name) {
		Vector<String> v;
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
	 * @param list
	 *            the <code>JList</code> that contains file names.
	 * @param v
	 *            the <code>Vector</code> containing a list of the file name
	 *            to be selected.
	 */
	public void setDefaultSelection(JList list, Vector<String> v) {
		int[] indices = new int[1000];
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
			int[] res = new int[i];
			for (int j = 0; j < i; j++) {
				res[j] = indices[j];
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
	 * @return the position in the <code>ListModel</code> if the file name
	 *         were found, -1 otherwise
	 */
	public int getElementIndex(ListModel model, String s) {
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
	 *  
	 */
	public void saveDefaultDicLists() {
		saveSelectionToFile(racDicList, new File(new 
				File(Config.getUserCurrentLanguageDir(),"MorphemVariants")
				, "racine_dic.def"));
		saveSelectionToFile(sufDicList, new File(new 
				File(Config.getUserCurrentLanguageDir(),"MorphemVariants"),
					"suffix_dic.def"));
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
	public void saveSelectionToFile(JList list, File file) {
		Object[] selection = list.getSelectedValues();
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (int i = 0; i < selection.length; i++) {
				String s = ((String) selection[i]) + "\n";
				bw.write(s, 0, s.length());
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class ApplyLexicalResourcesDo extends ToDoAbstract {
		public void toDo() {
			TextDicFrame.loadTextDic(Config.getCurrentSntDir(),false);
		}
	}

}
