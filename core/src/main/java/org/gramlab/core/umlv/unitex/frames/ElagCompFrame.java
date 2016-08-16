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
package org.gramlab.core.umlv.unitex.frames;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.io.PrintWriter;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;

import org.gramlab.core.gramlab.util.KeyUtil;
import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.files.PersonalFileFilter;
import org.gramlab.core.umlv.unitex.process.Launcher;
import org.gramlab.core.umlv.unitex.process.commands.ElagCompCommand;
import org.gramlab.core.umlv.unitex.process.commands.FlattenCommand;
import org.gramlab.core.umlv.unitex.process.commands.Grf2Fst2Command;
import org.gramlab.core.umlv.unitex.process.commands.MultiCommands;
import org.gramlab.core.umlv.unitex.project.manager.UnitexProjectManager;

/**
 * @author Olivier Blanc
 */
public class ElagCompFrame extends JInternalFrame {
	JFileChooser fc;
	File currdir;
	final File elagDir;
	File lstfile;
	File outputfile;
	final DefaultListModel rules;
	final JList list;
	final JLabel lstlabel;
	final JLabel outlabel;
	final JLabel pathlabel;

	ElagCompFrame() {
		super("Elag Grammar Compilation", true, true, true, true);
		elagDir = new File(Config.getUserCurrentLanguageDir(), "Elag");
		currdir = elagDir;
		lstfile = new File(currdir, "elag.lst");
		rules = new DefaultListModel();
		list = new JList(rules);
		lstlabel = new JLabel();
		outlabel = new JLabel();
		pathlabel = new JLabel();
		final JPanel panel = makePanel();
		setLstFile(lstfile);
		setContentPane(panel);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		pack();
	}

	JPanel makePanel() {
		final JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
		final Dimension mediumdim = new Dimension(120, 25);
		final Dimension longdim = new Dimension(300, 25);
		final Dimension listdim = new Dimension(400, 250);
		final Dimension btndim = new Dimension(90, 25);
		final Dimension btn2dim = new Dimension(90, 60);
		final Border labelBorder = new LineBorder(Color.black, 1, true);
		final GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(4, 4, 4, 4);
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;
		c.weighty = 1.0;
		JLabel titlelabel = new JLabel();
		titlelabel.setText("Set of Elag Grammars:");
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		panel.add(titlelabel, c);
		lstlabel.setBorder(labelBorder);
		lstlabel.setPreferredSize(mediumdim);
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(lstlabel, c);
		JButton button = new JButton("Browse");
		button.setPreferredSize(btndim);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser f = new JFileChooser();
				f.setCurrentDirectory(currdir);
				f.setFileFilter(new PersonalFileFilter("lst",
						"Elag grammar collection ( .lst)"));
				f.setAcceptAllFileFilterUsed(false);
				f.setDialogTitle("Elag List File");
				f.setDialogType(JFileChooser.OPEN_DIALOG);
				if ((f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
						|| (f.getSelectedFile() == null)) {
					return;
				}
				currdir = f.getCurrentDirectory();
				lstfile = f.getSelectedFile();
				String ext;
				if ((ext = FileUtil.getExtensionInLowerCase(lstfile)) == null
						|| !ext.equals("lst")) {
					lstfile = new File(lstfile.getAbsolutePath() + ".lst");
				}
				setLstFile(lstfile);
				fc.setCurrentDirectory(currdir);
			}
		});
		c.gridx = 2;
		c.gridy = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		panel.add(button, c);
		button = new JButton("Save");
		button.setPreferredSize(btndim);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveLstFile();
			}
		});
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		panel.add(button, c);
		pathlabel.setBorder(labelBorder);
		pathlabel.setPreferredSize(longdim);
		pathlabel.setOpaque(true);
		c.gridx = 5;
		c.gridy = 0;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.fill = GridBagConstraints.BOTH;
		panel.add(pathlabel, c);
		fc = new JFileChooser();
		fc.setControlButtonsAreShown(false);
		fc.setPreferredSize(listdim);
		fc.setCurrentDirectory(currdir);
		fc.setFileFilter(new ElagGrfFileFilter());
		fc.setAcceptAllFileFilterUsed(false);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 4;
		c.gridheight = 4;
		c.fill = GridBagConstraints.BOTH;
		panel.add(fc, c);
		button = new JButton(">>");
		button.setPreferredSize(btn2dim);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final File f = fc.getSelectedFile();
				if (f != null && f.isFile()) {
					addRule(fc.getSelectedFile().getAbsolutePath());
				}
			}
		});
		c.gridx = 4;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		panel.add(button, c);
		button = new JButton("<<");
		button.setPreferredSize(btn2dim);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int idx = list.getSelectedIndex();
				if (idx < 0 || idx >= rules.size()) {
					return;
				}
				rules.remove(idx);
				if (idx > 0) {
					idx--;
				}
				list.setSelectedIndex(idx);
			}
		});
		c.gridx = 4;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		panel.add(button, c);
		button = new JButton("View");
		button.setPreferredSize(btn2dim);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final int idx = list.getSelectedIndex();
				if (idx < 0 || idx >= rules.size()) {
					return;
				}
				File grf = new File((String) rules.elementAt(idx));
				if (!grf.getName().endsWith(".grf")) {
					JOptionPane.showInternalMessageDialog(
							UnitexFrame.mainFrame, grf.getName()
									+ " doesn't seem like a GRF file", "ERROR",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (!grf.isAbsolute()) {
					grf = new File(elagDir, (String) rules.elementAt(idx));
				}
				if (!grf.exists()) {
					JOptionPane.showInternalMessageDialog(
							UnitexFrame.mainFrame, grf.getName()
									+ " doesn't seem to exist", "ERROR",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				UnitexProjectManager.search(grf)
						.getFrameManagerAs(InternalFrameManager.class).newGraphFrame(grf);
			}
		});
		c.gridx = 4;
		c.gridy = 3;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		panel.add(button, c);
		button = new JButton("Locate");
		button.setPreferredSize(btn2dim);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final int idx = list.getSelectedIndex();
				if (idx < 0 || idx >= rules.size()) {
					return;
				}
				String fname = (String) rules.elementAt(idx);
				if (!fname.endsWith(".grf")) {
					JOptionPane.showInternalMessageDialog(
							UnitexFrame.mainFrame, fname
									+ " doesn't seem like a GRF file", "ERROR",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				fname = fname.replaceAll(".grf$", "-conc.fst2");
				File conc = new File(fname);
				if (!conc.isAbsolute()) {
					conc = new File(elagDir, fname);
				}
				if (!conc.exists()) {
					JOptionPane
							.showInternalMessageDialog(
									UnitexFrame.mainFrame,
									"You should compile your Elag grammar before using the Locate Pattern feature",
									"ERROR", JOptionPane.ERROR_MESSAGE);
					return;
				}
				UnitexProjectManager.search(conc)
						.getFrameManagerAs(InternalFrameManager.class).newLocateFrame(conc);
			}
		});
		c.gridx = 4;
		c.gridy = 4;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		panel.add(button, c);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final JScrollPane scroll = new JScrollPane(list);
		scroll.setPreferredSize(listdim);
		c.gridx = 5;
		c.gridy = 1;
		c.gridwidth = 4;
		c.gridheight = 4;
		c.fill = GridBagConstraints.BOTH;
		panel.add(scroll, c);
		titlelabel = new JLabel();
		titlelabel.setText("Compiled Elag Rule:");
		outlabel.setBorder(labelBorder);
		outlabel.setPreferredSize(mediumdim);
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel.add(titlelabel, c);
		c.gridx = 1;
		c.gridy = 5;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		panel.add(outlabel, c);
		button = new JButton("Compile");
		button.setPreferredSize(btndim);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveLstFile();
				final File tagset = new File(elagDir, "tagset.def");
				if (!tagset.exists()) {
					JOptionPane.showInternalMessageDialog(
							UnitexFrame.mainFrame,
							"File " + tagset.getAbsolutePath()
									+ " doesn't exist.\n", "ERROR",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				final MultiCommands commands = new MultiCommands();
				for (int i = 0; i < rules.size(); i++) {
					String str = (String) rules.elementAt(i);
					File grf = new File(str);
					if (!grf.isAbsolute()) {
						grf = new File(elagDir, str);
					}
					if (!grf.getName().endsWith(".grf")) {
						JOptionPane.showInternalMessageDialog(
								UnitexFrame.mainFrame, grf.getName()
										+ " doesn't spell like a .grf file",
								"ERROR", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (!grf.exists()) {
						JOptionPane.showInternalMessageDialog(
								UnitexFrame.mainFrame,
								"Grammar " + grf.getAbsolutePath()
										+ " doesn't seem to exist", "ERROR",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					str = grf.getAbsolutePath();
					final File fst2 = new File(str.replaceAll(".grf$", ".fst2"));
					final File elg = new File(str.replaceAll(".grf$", ".elg"));
					if (!elg.exists()
							|| elg.lastModified() < grf.lastModified()
							|| elg.lastModified() < tagset.lastModified()) {
						/* We have to compile the .grf into a .elg */
						commands.addCommand(new Grf2Fst2Command().grf(grf)
								.enableLoopAndRecursionDetection(true)
								.tokenizationMode(null, grf).repositories()
								.emitEmptyGraphWarning().displayGraphNames());
						commands.addCommand(new FlattenCommand().fst2(fst2)
								.resultType(true));
						final ElagCompCommand elagCompCmd = new ElagCompCommand()
								.grammar(fst2).lang(tagset);
						commands.addCommand(elagCompCmd);
					}
				}
				final ElagCompCommand elagCompCmd = new ElagCompCommand()
						.lang(tagset).output(outputfile).ruleList(lstfile);
				commands.addCommand(elagCompCmd);
				Launcher.exec(commands, false);
			}
		});
		c.gridx = 3;
		c.gridy = 5;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		panel.add(button, c);
		button = new JButton("Cancel compilation");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveLstFile();
				doDefaultCloseAction();
			}
		});
		c.gridx = 5;
		c.gridy = 5;
		c.gridwidth = 4;
		c.gridheight = 1;
		panel.add(button, c);
		KeyUtil.addEscListener(panel, button);
		return panel;
	}

	void setLstFile(File f) {
		lstfile = f;
		readLstFile();
		lstlabel.setText(lstfile.getName());
		final String fname = lstfile.getAbsolutePath();
		if (FileUtil.getExtensionInLowerCase(lstfile).equals("lst")) {
			outputfile = new File(FileUtil.getFileNameWithoutExtension(fname)
					+ ".rul");
		} else {
			outputfile = new File(fname + ".rul");
		}
		outlabel.setText(outputfile.getName());
	}

	void saveLstFile() {
		try {
			final PrintWriter p = new PrintWriter(new FileWriter(lstfile));
			for (int i = 0; i < rules.size(); i++) {
				p.println((String) rules.elementAt(i));
			}
			p.close();
		} catch (final Exception e) {
			System.err.println("I/O error with " + lstfile.getName() + " : "
					+ e);
		}
	}

	void readLstFile() {
		rules.clear();
		if (!lstfile.exists()) {
			return;
		}
		try {
			final LineNumberReader r = new LineNumberReader(new FileReader(
					lstfile));
			String s;
			while ((s = r.readLine()) != null) {
				addRule(s);
			}
			r.close();
		} catch (final Exception e) {
			System.err.println("I/O error with " + lstfile.getName() + " : "
					+ e);
		}
	}

	void addRule(String s) {
		if (rules.indexOf(s) == -1) {
			final int idx = rules.size();
			rules.add(idx, s);
			list.ensureIndexIsVisible(idx);
			list.setSelectedIndex(idx);
		}
	}
}

class ElagGrfFileFilter extends FileFilter {
	ElagGrfFileFilter() {
		super();
	}

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		final String s = f.getPath();
		return (s.endsWith(".grf"));
	}

	@Override
	public String getDescription() {
		return "Elag Grammar (.grf)";
	}
}
