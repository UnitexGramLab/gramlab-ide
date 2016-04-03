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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import fr.umlv.unitex.FontInfo;
import fr.umlv.unitex.Unitex;
import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.config.Preferences;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.files.PersonalFileFilter;
import fr.umlv.unitex.grf.GraphPresentationInfo;
import fr.umlv.unitex.io.Encoding;
import fr.umlv.unitex.listeners.LanguageListener;

/**
 * This class describes a frame that offers to the user to set his preferences.
 * 
 * @author Sébastien Paumier
 */
public class GlobalPreferencesFrame extends JInternalFrame {

	final JTextField privateDirectory = new JTextField("");
	final JTextField menuFont = new JTextField("");
	final JTextField textFont = new JTextField("");
	final JTextField concordanceFont = new JTextField("");
	final JTextField htmlViewer = new JTextField("");
	final JCheckBox rightToLeftForCorpusCheckBox = new JCheckBox(
			"Right to left rendering for text");
	final JCheckBox rightToLeftForGraphsCheckBox = new JCheckBox(
			"Right to left rendering for graphs");
	final JCheckBox semiticCheckBox = new JCheckBox("Semitic language");
	final JCheckBox matchWordBoundariesCheckBox = new JCheckBox("Match word boundaries");
	final JCheckBox charByCharCheckBox = new JCheckBox(
			"Analyze this language char by char");
	final JCheckBox morphologicalUseOfSpaceCheckBox = new JCheckBox(
			"Enable morphological use of space");
	final JTextField packageDirectory = new JTextField("");
	final JTextField lexicalPackageDirectory = new JTextField("");
	final DefaultListModel morphoDicListModel = new DefaultListModel();
	private Preferences pref;
	final JCheckBox mustLogCheckBox = new JCheckBox(
			"Produce log information in directory:");
	final JTextField loggingDirectory = new JTextField("");
	final JCheckBox svnMonitoring = new JCheckBox(
			"Auto-monitor graphs for SVN conflicts", true);
	final JCheckBox onlyCosmetic = new JCheckBox(
			"Use --only-cosmetic option for GrfDiff3", false);
	final JRadioButton[] encodingButtons = new JRadioButton[Encoding.values().length];

	GlobalPreferencesFrame() {
		super("", true, true, false, false);
		setContentPane(constructPanel());
		pack();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		Config.addLanguageListener(new LanguageListener() {
			@Override
			public void languageChanged() {
				reset();
			}
		});
	}

	private JPanel constructPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(constructUpPanel(), BorderLayout.CENTER);
		panel.add(constructDownPanel(), BorderLayout.SOUTH);
		return panel;
	}

	private JPanel constructUpPanel() {
		final JPanel upPanel = new JPanel(new BorderLayout());
		final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tabbedPane.addTab("General", constructGeneralTab());
		tabbedPane.addTab("Directories", constructPage1());
		tabbedPane.addTab("Language", constructLanguageTab());
		tabbedPane.addTab("Presentation", constructPresentationTab());
		tabbedPane.addTab("Morphological-mode dictionaries", constructPage4());
		tabbedPane.addTab("SVN", constructSvnPage());
		tabbedPane.addTab("Encoding", constructEncodingPage());
		upPanel.add(tabbedPane);
		return upPanel;
	}

	private JPanel constructSvnPage() {
		final JPanel p = new JPanel(null);
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		svnMonitoring.setSelected(ConfigManager.getManager()
				.svnMonitoring(null));
		p.add(svnMonitoring);
		onlyCosmetic.setSelected(ConfigManager.getManager().onlyCosmetic(null));
		p.add(onlyCosmetic);
		p.add(Box.createVerticalGlue());
		return p;
	}

	private JPanel constructGeneralTab() {
		final JPanel general = new JPanel(null);

		general.setLayout(new BoxLayout(general, BoxLayout.Y_AXIS));
		general.setBorder(new EmptyBorder(5, 5, 5, 5));

		menuFont.setEnabled(false);
		menuFont.setDisabledTextColor(Color.black);

		final JPanel tmp = new JPanel(new GridLayout(2, 1));
		tmp.setPreferredSize(new Dimension(180, 60));
		tmp.add(new JLabel("Menu Font:"));
		final JPanel tmp2 = new JPanel(new BorderLayout());
		tmp2.add(menuFont, BorderLayout.CENTER);
		final Action menuFontAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final FontInfo i = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.newFontDialog(getPref().getMenuFont());
				if (i != null) {
					getPref().setMenuFont(i);
					menuFont.setText(" " + i.getFont().getFontName() + "  "
							+ i.getSize());
					JOptionPane.showMessageDialog(null,
							"Changes will take effect the next time you start Unitex",
							"Information", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};
		final JButton setMenuFont = new JButton(menuFontAction);
		tmp2.add(setMenuFont, BorderLayout.EAST);
		tmp.add(tmp2, BorderLayout.SOUTH);
		general.add(tmp);

		return general;
	}

	private JPanel constructEncodingPage() {
		final JPanel p = new JPanel(null);
		p.setBorder(BorderFactory
				.createTitledBorder("Select encoding to be used by Unitex:"));
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		final ButtonGroup bg = new ButtonGroup();
		int i = 0;
		for (final Encoding e : Encoding.values()) {
			encodingButtons[i] = new JRadioButton(e.toString(),
					e == ConfigManager.getManager().getEncoding(null));
			final Encoding e2 = e;
			final int j = i;
			encodingButtons[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e3) {
					if (encodingButtons[j].isSelected()) {
						getPref().setEncoding(e2);
					}
				}
			});
			p.add(encodingButtons[i]);
			bg.add(encodingButtons[i]);
			i++;
		}
		p.add(Box.createVerticalGlue());
		return p;
	}

	private JPanel constructDownPanel() {
		final JPanel downPanel = new JPanel(new BorderLayout());
		final JPanel tmp = new JPanel(new GridLayout(1, 2));
		tmp.setBorder(new EmptyBorder(2, 2, 2, 2));
		final JPanel tmp1 = new JPanel(new BorderLayout());
		final JPanel tmp2 = new JPanel(new BorderLayout());
		tmp1.setBorder(new EmptyBorder(5, 5, 5, 5));
		tmp2.setBorder(new EmptyBorder(5, 5, 5, 5));
		final Action okAction = new AbstractAction("OK") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getPref().setSvnMonitoring(svnMonitoring.isSelected());
				getPref().setOnlyCosmetic(onlyCosmetic.isSelected());
				getPref().setSemitic(semiticCheckBox.isSelected());
				getPref().setMatchWordBoundaries(matchWordBoundariesCheckBox.isSelected());
				getPref().setRightToLeftForText(
						rightToLeftForCorpusCheckBox.isSelected());
				getPref().setRightToLeftForGraphs(
						rightToLeftForGraphsCheckBox.isSelected());
				getPref().getInfo().setRightToLeft(
						getPref().isRightToLeftForGraphs());
				if (htmlViewer.getText().equals(""))
					getPref().setHtmlViewer(null);
				else
					getPref().setHtmlViewer(new File(htmlViewer.getText()));
				getPref().setMorphologicalDic(getFileList(morphoDicListModel));
				getPref().setCharByChar(charByCharCheckBox.isSelected());
				getPref().setMorphologicalUseOfSpace(
						morphologicalUseOfSpaceCheckBox.isSelected());
				if (packageDirectory.getText().equals(""))
					getPref().setGraphRepositoryPath(null);
				else {
					final File f = new File(packageDirectory.getText());
					if (!f.exists()) {
						JOptionPane.showMessageDialog(null,
								"The graph repository\ndoes not exist.",
								"Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (!f.isDirectory()) {
						JOptionPane
								.showMessageDialog(
										null,
										"The path given for the graph repository\n is not a directory path.",
										"Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					getPref().setGraphRepositoryPath(f);
				}
				if (loggingDirectory.getText().equals("")
						&& mustLogCheckBox.isSelected()) {
					JOptionPane.showMessageDialog(null,
							"Cannot log in an empty directory path.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				final String logDir = loggingDirectory.getText();
				final File f = new File(logDir);
				if (f.exists() && !f.isDirectory()) {
					JOptionPane
							.showMessageDialog(
									null,
									"The path given for the graph repository\n is not a directory path.",
									"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (!f.exists()) {
					f.mkdir();
				}
				getPref().setMustLog(mustLogCheckBox.isSelected());
				getPref().setLoggingDir(f);
				ConfigManager.getManager().savePreferences(getPref(), null);
				/* We save the user directory */
				if (!privateDirectory.getText().equals("")) {
					final File rep = new File(privateDirectory.getText());
					if (!rep.equals(Config.getUserDir())) {
						File userFile;
						if (Config.getCurrentSystem() == Config.WINDOWS_SYSTEM) {
							userFile = new File(Config.getUnitexDir(), "Users");
							userFile = new File(userFile, Config.getUserName()
									+ ".cfg");
						} else {
							userFile = new File(
									System.getProperty("user.home"),
									".unitex.cfg");
						}
						if (userFile.exists())
							userFile.delete();
						try {
							userFile.createNewFile();
							final FileOutputStream stream = new FileOutputStream(
									userFile);
							stream.write(rep.getAbsolutePath().getBytes("UTF8"));
							stream.close();
						} catch (final IOException e2) {
							e2.printStackTrace();
						}
						String message = "Your private Unitex directory is now:\n\n";
						message = message + rep + "\n\n";
						message = message
								+ "You must relaunch Unitex to take this change into account.";
						JOptionPane.showMessageDialog(null, message, "",
								JOptionPane.PLAIN_MESSAGE);
					}
				}
				setVisible(false);
			}
		};
		final Action cancelAction = new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		};
		final JButton OK = new JButton(okAction);
		final JButton CANCEL = new JButton(cancelAction);
		tmp1.add(OK);
		tmp2.add(CANCEL);
		tmp.add(tmp1);
		tmp.add(tmp2);
		downPanel.add(tmp, BorderLayout.EAST);
		return downPanel;
	}

	ArrayList<File> getFileList(DefaultListModel model) {
		final ArrayList<File> list = new ArrayList<File>();
		for (int i = 0; i < model.size(); i++) {
			list.add((File) model.get(i));
		}
		return list;
	}

	private JComponent constructPage1() {
		final JPanel page1 = new JPanel(new GridBagLayout());
		page1.setBorder(new EmptyBorder(5, 5, 5, 5));
		final JLabel label = new JLabel(
				"Private Unitex directory (where all user's data is to be stored):");
		privateDirectory.setEditable(false);
		privateDirectory.setBackground(Color.WHITE);
		final Action privateDirAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser f = new JFileChooser();
				f.setDialogTitle("Choose your private directory");
				f.setDialogType(JFileChooser.OPEN_DIALOG);
				f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
					return;
				if (f.getSelectedFile().equals(Config.getUnitexDir())) {
					JOptionPane
							.showMessageDialog(
									null,
									"You cannot choose the Unitex directory as your private one",
									"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				privateDirectory.setText(f.getSelectedFile().getAbsolutePath());
			}
		};
		final JButton setPrivateDirectory = new JButton(privateDirAction);
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.NONE;
		page1.add(label, gbc);
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		page1.add(privateDirectory, gbc);
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		page1.add(setPrivateDirectory, gbc);
		final JLabel label2 = new JLabel("Graph repository:");
		packageDirectory.setBackground(Color.WHITE);
		final Action packageDirAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser f = new JFileChooser();
				f.setDialogTitle("Choose your graph package directory");
				f.setDialogType(JFileChooser.OPEN_DIALOG);
				f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
					return;
				}
				packageDirectory.setText(f.getSelectedFile().getAbsolutePath());
			}
		};
		final JButton setPackageDirectory = new JButton(packageDirAction);
		gbc.insets = new Insets(20, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.NONE;
		page1.add(label2, gbc);
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		page1.add(packageDirectory, gbc);
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		page1.add(setPackageDirectory, gbc);
		loggingDirectory.setEditable(false);
		loggingDirectory.setBackground(Color.WHITE);
		final Action loggingDirAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser f = new JFileChooser();
				f.setDialogTitle("Choose your logging directory");
				f.setDialogType(JFileChooser.OPEN_DIALOG);
				f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
					return;
				loggingDirectory.setText(f.getSelectedFile().getAbsolutePath());
			}
		};
		final JButton setLoggingDirectory = new JButton(loggingDirAction);
		gbc.insets = new Insets(20, 2, 2, 2);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.NONE;
		page1.add(mustLogCheckBox, gbc);
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		page1.add(loggingDirectory, gbc);
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		page1.add(setLoggingDirectory, gbc);
		final JButton clearLogs = new JButton("Clear all logs");
		clearLogs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (loggingDirectory.getText().equals("")) {
					return;
				}
				final int n = JOptionPane.showConfirmDialog(
						UnitexFrame.mainFrame,
						"Are you sure you want to clear logs ?", "",
						JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION) {
					final File dir = new File(loggingDirectory.getText());
					FileUtil.removeFile(new File(dir, "*.ulp"));
					FileUtil.removeFile(new File(dir,
							"unitex_logging_parameters_count.txt"));
				}
			}
		});
		page1.add(clearLogs, gbc);
		gbc.weighty = 1;
		page1.add(new JPanel(null), gbc);
		return page1;
	}

	private JPanel constructPresentationTab() {
		final JPanel presentation = new JPanel(null);
		presentation.setLayout(new BoxLayout(presentation, BoxLayout.Y_AXIS));
		presentation.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		textFont.setEnabled(false);
		concordanceFont.setEnabled(false);
		textFont.setDisabledTextColor(Color.black);
		concordanceFont.setDisabledTextColor(Color.black);
		
		final JPanel checkBoxes = new JPanel(new GridLayout(2, 1));
		checkBoxes.add(rightToLeftForCorpusCheckBox);
		checkBoxes.add(rightToLeftForGraphsCheckBox);
		presentation.add(checkBoxes);
		
		final JPanel tmp = new JPanel(new GridLayout(2, 1));
		tmp.setPreferredSize(new Dimension(180, 60));
		tmp.add(new JLabel("Text Font:"));
		final JPanel tmp2 = new JPanel(new BorderLayout());
		tmp2.add(textFont, BorderLayout.CENTER);
		final Action textFontAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final FontInfo i = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.newFontDialog(getPref().getTextFont());
				if (i != null) {
					getPref().setTextFont(i);
					textFont.setText(" " + i.getFont().getFontName() + "  "
							+ i.getSize());
				}
			}
		};
		final JButton setTextFont = new JButton(textFontAction);
		tmp2.add(setTextFont, BorderLayout.EAST);
		tmp.add(tmp2);
		presentation.add(tmp);
		final JPanel tmp_ = new JPanel(new GridLayout(2, 1));
		tmp_.setPreferredSize(new Dimension(180, 60));
		tmp_.add(new JLabel("Concordance Font:"));
		final JPanel tmp2_ = new JPanel(new BorderLayout());
		tmp2_.add(concordanceFont, BorderLayout.CENTER);
		final Action concord = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final FontInfo i = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.newFontDialog(getPref().getConcordanceFont());
				if (i != null) {
					getPref().setConcordanceFont(i);
					concordanceFont.setText(" " + i.getFont().getFontName()
							+ "  " + i.getSize());
				}
			}
		};
		final JButton setConcordanceFont = new JButton(concord);
		tmp2_.add(setConcordanceFont, BorderLayout.EAST);
		tmp_.add(tmp2_);
		presentation.add(tmp_);
		final JPanel htmlViewerPanel = new JPanel(new GridLayout(2, 1));
		htmlViewerPanel.setPreferredSize(new Dimension(180, 60));
		htmlViewerPanel.add(new JLabel("Html Viewer:"));
		final JPanel tmp3_ = new JPanel(new BorderLayout());
		final Action html = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser f = new JFileChooser();
				f.setDialogTitle("Choose your html viewer");
				f.setDialogType(JFileChooser.OPEN_DIALOG);
				if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
					return;
				htmlViewer.setText(f.getSelectedFile().getAbsolutePath());
			}
		};
		final JButton setHtmlViewer = new JButton(html);
		tmp3_.add(htmlViewer, BorderLayout.CENTER);
		tmp3_.add(setHtmlViewer, BorderLayout.EAST);
		htmlViewerPanel.add(tmp3_);
		presentation.add(htmlViewerPanel);
		final JPanel graph = new JPanel();
		final FlowLayout l = (FlowLayout) (graph.getLayout());
		l.setAlignment(FlowLayout.LEFT);
		final JButton graphConfig = new JButton("Graph configuration");
		graphConfig.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphPresentationInfo i = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.newGraphPresentationDialog(
								getPref().getInfo(), false);
				if (i != null) {
					getPref().setInfo(i);
				}
			}
		});
		graph.add(graphConfig);
		presentation.add(graph);
		
		return presentation;
	}

	private JPanel constructLanguageTab() {
		final JPanel page2 = new JPanel(null);
		page2.setLayout(new BoxLayout(page2, BoxLayout.Y_AXIS));
		
		page2.setBorder(new EmptyBorder(5, 5, 5, 5));
		final JPanel yuyu = new JPanel(new GridLayout(4, 1));
		yuyu.add(charByCharCheckBox);
		yuyu.add(morphologicalUseOfSpaceCheckBox);
		yuyu.add(semiticCheckBox);
		yuyu.add(matchWordBoundariesCheckBox);
		page2.add(yuyu);
		return page2;
	}

	/**
	 * Refreshes the frame.
	 */
	void refresh() {
		menuFont.setText("" + getPref().getMenuFont().getFont().getFontName() + " " + getPref().getMenuFont().getSize());
		textFont.setText("" + getPref().getTextFont().getFont().getFontName()
				+ "  " + getPref().getTextFont().getSize() + "");
		concordanceFont.setText(""
				+ getPref().getConcordanceFont().getFont().getName() + "  "
				+ getPref().getConcordanceFont().getSize() + "");
		if (getPref().getHtmlViewer() == null) {
			htmlViewer.setText("");
		} else {
			htmlViewer.setText(getPref().getHtmlViewer().getAbsolutePath());
		}
		semiticCheckBox.setSelected(getPref().isSemitic());
		matchWordBoundariesCheckBox.setSelected(getPref().isMatchWordBoundaries());
		rightToLeftForCorpusCheckBox.setSelected(getPref()
				.isRightToLeftForText());
		rightToLeftForGraphsCheckBox.setSelected(getPref()
				.isRightToLeftForGraphs());
		charByCharCheckBox.setSelected(getPref().isCharByChar());
		morphologicalUseOfSpaceCheckBox.setSelected(getPref()
				.isMorphologicalUseOfSpace());
		if (getPref().getGraphRepositoryPath() == null) {
			packageDirectory.setText("");
		} else {
			packageDirectory.setText(getPref().getGraphRepositoryPath()
					.getAbsolutePath());
		}
		mustLogCheckBox.setSelected(getPref().isMustLog());
		if (getPref().getLoggingDir() == null) {
			loggingDirectory.setText("");
		} else {
			loggingDirectory.setText(getPref().getLoggingDir()
					.getAbsolutePath());
		}
		morphoDicListModel.clear();
		final ArrayList<File> dictionaries = ConfigManager.getManager()
				.morphologicalDictionaries(null);
		if (dictionaries != null) {
			for (final File f : dictionaries) {
				morphoDicListModel.addElement(f);
			}
		}
		for (int i = 0; i < Encoding.values().length; i++) {
			encodingButtons[i].setSelected(getPref().getEncoding() == Encoding
					.values()[i]);
		}
	}

	JFileChooser morphoBinJFC=null;
	
	private JPanel constructPage4() {
		final JPanel p = new JPanel(null);
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(new EmptyBorder(5, 5, 5, 5));
		final JPanel p_ = new JPanel(new GridLayout(2, 1));
		p_.add(new JLabel(
				"Choose the .bin dictionaries to use in Locate's morphological"));
		p_.add(new JLabel("mode:"));
		p.add(p_);
		final JPanel p2 = new JPanel(new BorderLayout());
		morphoDicListModel.clear();
		final ArrayList<File> dictionaries = ConfigManager.getManager()
				.morphologicalDictionaries(null);
		if (dictionaries != null) {
			for (final File f : dictionaries) {
				morphoDicListModel.addElement(f);
			}
		}
		final JList list = new JList(morphoDicListModel);
		list.setPreferredSize(new Dimension(200, 400));
		list.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList l,
					Object value, int index, boolean isSelected1,
					boolean cellHasFocus) {
				final File f = (File) value;
				return super.getListCellRendererComponent(l,
						f.getAbsolutePath(), index, isSelected1, cellHasFocus);
			}
		});
		final JScrollPane scroll = new JScrollPane(list,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		p2.add(scroll);
		final JPanel down = new JPanel(new BorderLayout());
		final JPanel tmp = new JPanel(new GridLayout(1, 2));
		tmp.setBorder(new EmptyBorder(2, 2, 2, 2));
		final JPanel tmp1 = new JPanel(new BorderLayout());
		final JPanel tmp2 = new JPanel(new BorderLayout());
		tmp1.setBorder(new EmptyBorder(5, 5, 5, 5));
		tmp2.setBorder(new EmptyBorder(5, 5, 5, 5));
		final Action addAction = new AbstractAction("Add") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (morphoBinJFC==null) {
					morphoBinJFC = new JFileChooser();
					morphoBinJFC.setMultiSelectionEnabled(true);
					morphoBinJFC.addChoosableFileFilter(new PersonalFileFilter("bin",
						"Binary dictionary"));
					morphoBinJFC.setDialogTitle("Choose your morphological-mode dictionaries");
					morphoBinJFC.setDialogType(JFileChooser.OPEN_DIALOG);
				}
				if (morphoBinJFC.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
					return;
				}
				final File[] files = morphoBinJFC.getSelectedFiles();
				if (files == null)
					return;
				for (final File file : files) {
					morphoDicListModel.addElement(file);
				}
			}
		};
		final Action removeAction = new AbstractAction("Remove") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final int[] indices = list.getSelectedIndices();
				for (int i = indices.length - 1; i >= 0; i--) {
					morphoDicListModel.remove(indices[i]);
				}
			}
		};
		final JButton addButton = new JButton(addAction);
		final JButton removeButton = new JButton(removeAction);
		tmp1.add(addButton);
		tmp2.add(removeButton);
		tmp.add(tmp1);
		tmp.add(tmp2);
		down.add(tmp, BorderLayout.EAST);
		p2.add(down, BorderLayout.SOUTH);
		p.add(p2);
		return p;
	}

	void reset() {
		setPref(ConfigManager.getManager().getPreferences(null));
		setTitle("Preferences for " + Config.getCurrentLanguage());
		privateDirectory.setText(Config.getUserDir().getAbsolutePath());
		refresh();
	}

	public void setPref(Preferences pref) {
		this.pref = pref;
	}

	public Preferences getPref() {
		return pref;
	}
}
