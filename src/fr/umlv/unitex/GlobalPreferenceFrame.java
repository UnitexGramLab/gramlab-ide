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

package fr.umlv.unitex;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * This class describes a frame that offers to the user to set his preferences.
 * 
 * @author Sébastien Paumier
 *  
 */
public class GlobalPreferenceFrame extends JInternalFrame {

	static ArrayList<FontListener> textFontListeners=new ArrayList<FontListener>(); 
	static ArrayList<FontListener> concordanceFontListeners=new ArrayList<FontListener>(); 
	
	static GlobalPreferenceFrame frame;

	JTextField privateDirectory = new JTextField("");

	protected JTextField textFont = new JTextField("");

	JTextField concordanceFont = new JTextField("");

	JTextField htmlViewer = new JTextField("");

	JTextField morphologicalDicViewer = new JTextField("");

	JCheckBox antialiasingCheckBox = new JCheckBox(
			"Enable antialising for rendering graphs", false);

	JCheckBox dateCheckBox = new JCheckBox();

	JCheckBox filenameCheckBox = new JCheckBox();

	JCheckBox pathnameCheckBox = new JCheckBox();

	JCheckBox frameCheckBox = new JCheckBox();

	JCheckBox rightToLeftCheckBox = new JCheckBox(
			"Right to left rendering for corpus and graphs");

	JCheckBox charByCharCheckBox = new JCheckBox(
			"Analyze this language char by char");

	JCheckBox morphologicalUseOfSpaceCheckBox = new JCheckBox(
	"Enable morphological use of space");
	
	
	JPanel color1=ColorRectangle.getColorRectangle();
	JPanel color2=ColorRectangle.getColorRectangle();
	JPanel color3=ColorRectangle.getColorRectangle();
	JPanel color4=ColorRectangle.getColorRectangle();
	JPanel color5=ColorRectangle.getColorRectangle();
	
	JLabel inputLabel = new JLabel("", SwingConstants.LEFT);

	JLabel outputLabel = new JLabel("", SwingConstants.LEFT);

	JRadioButton westRadioBox = new JRadioButton("West", false);

	JRadioButton eastRadioBox = new JRadioButton("East", false);

	JRadioButton northRadioBox = new JRadioButton("North", false);

	JRadioButton southRadioBox = new JRadioButton("South", false);

	JRadioButton noneRadioBox = new JRadioButton("None", false);

	JTextField packageDirectory = new JTextField("");

	static Preferences pref;

	DefaultListModel morphoDicListModel=new DefaultListModel();
	
	private GlobalPreferenceFrame() {
		super("", false, true, false, false);
		setContentPane(constructPanel());
		pack();
		//setBounds(200, 200, 400, 450);
		setVisible(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addInternalFrameListener(new InternalFrameAdapter() {

			public void internalFrameClosing(InternalFrameEvent arg0) {
				setVisible(false);
			}
		});
	}

	/**
	 * Initializes the frame
	 *  
	 */
	private static void init() {
		frame = new GlobalPreferenceFrame();
		UnitexFrame.addInternalFrame(frame,false);
	}

	/**
	 * @return the frame
	 */
	public static GlobalPreferenceFrame getFrame() {
		return frame;
	}

	/**
	 * Shows the frame
	 *  
	 */
	public static void showFrame() {
		if (frame == null) {
			init();
		}
		pref = Preferences.getCloneOfPreferences();
		System.out.println();
		frame.setTitle("Preferences for " + Config.getCurrentLanguage());
		frame.privateDirectory.setText(Config.getUserDir().getAbsolutePath());

		frame.refresh();
		frame.setVisible(true);
		try {
			frame.setSelected(true);
		} catch (java.beans.PropertyVetoException e2) {
			e2.printStackTrace();
		}
	}

	private JPanel constructPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(true);
		panel.add(constructUpPanel(), BorderLayout.CENTER);
		panel.add(constructDownPanel(), BorderLayout.SOUTH);
		return panel;
	}

	private JPanel constructUpPanel() {
		JPanel upPanel = new JPanel(new BorderLayout());
		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tabbedPane.addTab("Directories", constructPage1());
		tabbedPane.addTab("Language & Presentation", constructPage2());
		tabbedPane.addTab("Graph Presentation", constructPage3());
		tabbedPane.addTab("Morphological dictionaries", constructPage4());
		upPanel.add(tabbedPane);
		return upPanel;
	}

	private JPanel constructDownPanel() {
		JPanel downPanel = new JPanel(new BorderLayout());
		JPanel tmp = new JPanel();
		tmp.setBorder(new EmptyBorder(2, 2, 2, 2));
		tmp.setLayout(new GridLayout(1, 2));
		JPanel tmp1 = new JPanel();
		JPanel tmp2 = new JPanel();
		tmp1.setBorder(new EmptyBorder(5, 5, 5, 5));
		tmp2.setBorder(new EmptyBorder(5, 5, 5, 5));
		tmp1.setLayout(new BorderLayout());
		tmp2.setLayout(new BorderLayout());

		Action okAction = new AbstractAction("OK") {

			public void actionPerformed(ActionEvent arg0) {
				fireTextFontChanged(pref.textFont);
				fireConcordanceFontChanged(new Font(pref.htmlFontName,0,pref.htmlFontSize));
				pref.antialiasing = frame.antialiasingCheckBox.isSelected();
				if (frame.htmlViewer.getText().equals(""))
					pref.htmlViewer = null;
				else
					pref.htmlViewer = new File(frame.htmlViewer.getText());
				pref.morphologicalDic=getFileList(frame.morphoDicListModel);
				pref.date = frame.dateCheckBox.isSelected();
				pref.filename = frame.filenameCheckBox.isSelected();
				pref.pathname = frame.pathnameCheckBox.isSelected();
				pref.frame = frame.frameCheckBox.isSelected();
				pref.rightToLeft = frame.rightToLeftCheckBox.isSelected();
				if (frame.westRadioBox.isSelected()) {
					pref.iconBarPosition = Preferences.ICON_BAR_WEST;
				} else if (frame.eastRadioBox.isSelected()) {
					pref.iconBarPosition = Preferences.ICON_BAR_EAST;
				} else if (frame.northRadioBox.isSelected()) {
					pref.iconBarPosition = Preferences.ICON_BAR_NORTH;
				} else if (frame.southRadioBox.isSelected()) {
					pref.iconBarPosition = Preferences.ICON_BAR_SOUTH;
				} else if (frame.noneRadioBox.isSelected()) {
					pref.iconBarPosition = Preferences.NO_ICON_BAR;
				} else {
					pref.iconBarPosition = Preferences.ICON_BAR_DEFAULT;
				}
				pref.charByChar = frame.charByCharCheckBox.isSelected();
				pref.morphologicalUseOfSpace = frame.morphologicalUseOfSpaceCheckBox.isSelected();
				if (frame.packageDirectory.getText().equals(""))
					pref.packagePath = null;
				else {
					File f=new File(frame.packageDirectory.getText());
					if (!f.exists()) {
						JOptionPane
						.showMessageDialog(
								null,
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
					pref.packagePath = f;
				}
				Preferences.savePreferences(pref);
				if (Config.getCurrentSystem() == Config.WINDOWS_SYSTEM) {
					// if we are under Windows, we must save the user dir
					if (privateDirectory.getText() != null
							&& !privateDirectory.getText().equals("")) {
						File rep = new File(privateDirectory.getText());
						if (!rep.equals(Config.getUserDir())) {
							File userFile = new File(Config.getUnitexDir(),
									"Users");
							userFile = new File(userFile, Config.getUserName()
									+ ".cfg");
							if (userFile.exists())
								userFile.delete();
							try {
								userFile.createNewFile();
								BufferedWriter bw = new BufferedWriter(
										new FileWriter(userFile));
								bw.write(rep.getAbsolutePath(), 0, rep
										.getAbsolutePath().length());
								bw.close();
							} catch (IOException e2) {
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
				}
				setVisible(false);
			}

		};
		Action cancelAction = new AbstractAction("Cancel") {

			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		};
		JButton OK = new JButton(okAction);
		JButton CANCEL = new JButton(cancelAction);
		tmp1.add(OK);
		tmp2.add(CANCEL);
		tmp.add(tmp1);
		tmp.add(tmp2);
		downPanel.add(tmp, BorderLayout.EAST);
		return downPanel;
	}

	
	protected ArrayList<File> getFileList(DefaultListModel model) {
		ArrayList<File> list=new ArrayList<File>();
		for (int i=0;i<model.size();i++) {
			list.add((File)model.get(i));
		}
		return list;
	}

	private JComponent constructPage1() {
		JPanel page1 = new JPanel(new GridBagLayout());
		page1.setBorder(new EmptyBorder(5, 5, 5, 5));

		JLabel label = new JLabel(
				"Private Unitex directory (where all user's data is to be stored):");

		privateDirectory.setEditable(false);
		privateDirectory.setBackground(Color.WHITE);
		Action privateDirAction = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser f = new JFileChooser();
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
		JButton setPrivateDirectory = new JButton(privateDirAction);
		if (Config.getCurrentSystem()!=Config.WINDOWS_SYSTEM) {
			setPrivateDirectory.setEnabled(false);
		}
		GridBagConstraints gbc = new GridBagConstraints();

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

		//--------------------------------------------------------

		JLabel label2 = new JLabel("Graph repository:");

		packageDirectory.setBackground(Color.WHITE);
		Action packageDirAction = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser f = new JFileChooser();
				f.setDialogTitle("Choose your graph package directory");
				f.setDialogType(JFileChooser.OPEN_DIALOG);
				f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
					return;
				}
				packageDirectory.setText(f.getSelectedFile().getAbsolutePath());
			}
		};
		JButton setPackageDirectory = new JButton(packageDirAction);

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

		gbc.weighty = 1;
		page1.add(new JComponent() {/**/
		}, gbc);

		return page1;
	}

	private JPanel constructPage2() {
		JPanel page2 = new JPanel(new GridLayout(5,1));

		textFont.setEnabled(false);
		concordanceFont.setEnabled(false);
		textFont.setDisabledTextColor(Color.black);
		concordanceFont.setDisabledTextColor(Color.black);

		page2.setBorder(new EmptyBorder(5, 5, 5, 5));
		JPanel yuyu = new JPanel(new GridLayout(3, 1));

		yuyu.add(charByCharCheckBox);
		yuyu.add(morphologicalUseOfSpaceCheckBox);
		yuyu.add(rightToLeftCheckBox);
		page2.add(yuyu);

		JPanel tmp = new JPanel();
		tmp.setPreferredSize(new Dimension(180, 60));
		tmp.setLayout(new GridLayout(2, 1));
		tmp.add(new JLabel("Text Font:"));
		JPanel tmp2 = new JPanel();
		tmp2.setLayout(new BorderLayout());
		tmp2.add(textFont, BorderLayout.CENTER);
		Action textFontAction = new AbstractAction("Set...") {

			public void actionPerformed(ActionEvent arg0) {
				// TODO avoid setVisible in constructor
				new TextFontMenu(pref, TextFontMenu.TEXT_FONT);
			}
		};
		JButton setTextFont = new JButton(textFontAction);
		tmp2.add(setTextFont, BorderLayout.EAST);
		tmp.add(tmp2);
		//page2.add(new JLabel(""));
		page2.add(tmp);

		JPanel tmp_ = new JPanel();
		tmp_.setPreferredSize(new Dimension(180, 60));
		tmp_.setLayout(new GridLayout(2, 1));
		tmp_.add(new JLabel("Concordance Font:"));
		JPanel tmp2_ = new JPanel();
		tmp2_.setLayout(new BorderLayout());
		tmp2_.add(concordanceFont, BorderLayout.CENTER);
		Action concord = new AbstractAction("Set...") {

			public void actionPerformed(ActionEvent arg0) {
				new ConcordanceFontMenu(pref);
			}
		};
		JButton setConcordanceFont = new JButton(concord);
		tmp2_.add(setConcordanceFont, BorderLayout.EAST);
		tmp_.add(tmp2_);
		page2.add(tmp_);

		JPanel htmlViewerPanel = new JPanel();
		htmlViewerPanel.setPreferredSize(new Dimension(180, 60));
		htmlViewerPanel.setLayout(new GridLayout(2, 1));
		htmlViewerPanel.add(new JLabel("Html Viewer:"));
		JPanel tmp3_ = new JPanel();
		tmp3_.setLayout(new BorderLayout());
		Action html = new AbstractAction("Set...") {

			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {
						JFileChooser f = new JFileChooser();
						f.setDialogTitle("Choose your html viewer");
						f.setDialogType(JFileChooser.OPEN_DIALOG);
						if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
							return;
						htmlViewer.setText(f.getSelectedFile()
								.getAbsolutePath());
					}
				});
			}
		};
		JButton setHtmlViewer = new JButton(html);
		tmp3_.add(htmlViewer, BorderLayout.CENTER);
		tmp3_.add(setHtmlViewer, BorderLayout.EAST);
		htmlViewerPanel.add(tmp3_);
		page2.add(htmlViewerPanel);
		
		return page2;
	}

	private JPanel constructPage3() {
		GridBagLayout g = new GridBagLayout();
		JPanel page3 = new JPanel(g);
		page3.setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		JPanel upPanel_ = constructUpPanel_();
		g.setConstraints(upPanel_, c);
		page3.add(upPanel_);
		c.fill = GridBagConstraints.BOTH;
		JPanel downPanel_ = constructDownPanel_();
		g.setConstraints(downPanel_, c);
		page3.add(downPanel_);
		return page3;
	}

	private JPanel constructUpPanel_() {
		JPanel upPanel_ = new JPanel(new BorderLayout());
		upPanel_.setBorder(new EmptyBorder(0, 0, 0, 0));
		upPanel_.add(constructDisplayPanel(), BorderLayout.WEST);
		upPanel_.add(constructColorPanel(), BorderLayout.CENTER);
		return upPanel_;
	}

	private JPanel constructDownPanel_() {
		JPanel downPanel_ = new JPanel(new BorderLayout());
		downPanel_.setBorder(new EmptyBorder(0, 0, 0, 0));
		downPanel_.add(constructAntialiasingPanel(), BorderLayout.NORTH);
		downPanel_.add(constructFontPanel(), BorderLayout.CENTER);
		downPanel_.add(constructButtonPanel(), BorderLayout.EAST);
		return downPanel_;
	}

	private JPanel constructDisplayPanel() {
		JPanel displayPanel = new JPanel(new GridLayout(5, 1));
		displayPanel.setBorder(new TitledBorder("Display"));
		JPanel display1 = new JPanel(new BorderLayout());
		JPanel display2 = new JPanel(new BorderLayout());
		JPanel display3 = new JPanel(new BorderLayout());
		JPanel display4 = new JPanel(new BorderLayout());
		JPanel display5 = new JPanel(new BorderLayout());
		display1.add(dateCheckBox, BorderLayout.WEST);
		display1.add(new JLabel("Date  "), BorderLayout.CENTER);
		display2.add(filenameCheckBox, BorderLayout.WEST);
		display2.add(new JLabel("File Name  "), BorderLayout.CENTER);
		display3.add(pathnameCheckBox, BorderLayout.WEST);
		display3.add(new JLabel("Pathname  "), BorderLayout.CENTER);
		display4.add(frameCheckBox, BorderLayout.WEST);
		display4.add(new JLabel("Frame  "), BorderLayout.CENTER);
		//display5.add(rightToLeftCheckBox, BorderLayout.WEST);
		//display5.add(new JLabel("Right to Left  "), BorderLayout.CENTER);
		displayPanel.add(display1);
		displayPanel.add(display2);
		displayPanel.add(display3);
		displayPanel.add(display4);
		displayPanel.add(display5);
		return displayPanel;
	}

	private void build(GridBagConstraints c, int gx, int gy, int gw, int gh,
			int wx, int wy) {
		c.gridx = gx;
		c.gridy = gy;
		c.gridwidth = gw;
		c.gridheight = gh;
		c.weightx = wx;
		c.weighty = wy;
	}

	private JPanel constructColorPanel() {
		GridBagLayout g = new GridBagLayout();
		JPanel colorPanel = new JPanel(g);
		colorPanel.setBorder(new TitledBorder("Colors"));
		Action backgroundAction = new AbstractAction("Set...") {

			public void actionPerformed(ActionEvent arg0) {
				pref.backgroundColor = JColorChooser.showDialog(
						UnitexFrame.mainFrame, "Background Color",
						pref.backgroundColor);
				refresh();
			}
		};
		JButton background = new JButton(backgroundAction);
		Action foregroundAction = new AbstractAction("Set...") {

			public void actionPerformed(ActionEvent arg0) {
				pref.foregroundColor = JColorChooser.showDialog(
						UnitexFrame.mainFrame, "Foreground Color",
						pref.foregroundColor);
				refresh();
			}
		};
		JButton foreground = new JButton(foregroundAction);
		Action subgraphAction = new AbstractAction("Set...") {

			public void actionPerformed(ActionEvent arg0) {
				pref.subgraphColor = JColorChooser.showDialog(
						UnitexFrame.mainFrame, "Auxiliary Nodes Color",
						pref.subgraphColor);
				refresh();
			}
		};
		JButton subgraph = new JButton(subgraphAction);
		Action selectedAction = new AbstractAction("Set...") {

			public void actionPerformed(ActionEvent arg0) {
				pref.selectedColor = JColorChooser.showDialog(
						UnitexFrame.mainFrame, "Selected Nodes Color",
						pref.selectedColor);
				refresh();
			}
		};
		JButton selected = new JButton(selectedAction);
		Action commentAction = new AbstractAction("Set...") {

			public void actionPerformed(ActionEvent arg0) {
				pref.commentColor = JColorChooser.showDialog(
						UnitexFrame.mainFrame, "Comment Nodes Color",
						pref.commentColor);
				refresh();
			}
		};
		JButton comment = new JButton(commentAction);
		GridBagConstraints c = new GridBagConstraints();
		build(c, 0, 0, 1, 1, 60, 15);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		JLabel label1 = new JLabel("  Background:  ", SwingConstants.LEFT);
		JLabel label2 = new JLabel("  Foreground:  ", SwingConstants.LEFT);
		JLabel label3 = new JLabel("  Auxiliary Nodes:  ", SwingConstants.LEFT);
		JLabel label4 = new JLabel("  Selected Nodes:  ", SwingConstants.LEFT);
		JLabel label5 = new JLabel("  Comment Nodes:  ", SwingConstants.LEFT);

		g.setConstraints(label1, c);
		colorPanel.add(label1);
		build(c, 1, 0, 1, 1, 20, 0);
		c.fill = GridBagConstraints.BOTH;
		g.setConstraints(color1, c);
		colorPanel.add(color1);
		build(c, 2, 0, 1, 1, 20, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		g.setConstraints(background, c);
		colorPanel.add(background);
		build(c, 0, 1, 1, 1, 60, 15);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		g.setConstraints(label2, c);
		colorPanel.add(label2);
		build(c, 1, 1, 1, 1, 20, 0);
		c.fill = GridBagConstraints.BOTH;
		g.setConstraints(color2, c);
		colorPanel.add(color2);
		build(c, 2, 1, 1, 1, 20, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		g.setConstraints(foreground, c);
		colorPanel.add(foreground);
		build(c, 0, 2, 1, 1, 60, 15);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		g.setConstraints(label3, c);
		colorPanel.add(label3);
		build(c, 1, 2, 1, 1, 20, 0);
		c.fill = GridBagConstraints.BOTH;
		g.setConstraints(color3, c);
		colorPanel.add(color3);
		build(c, 2, 2, 1, 1, 20, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		g.setConstraints(subgraph, c);
		colorPanel.add(subgraph);
		build(c, 0, 3, 1, 1, 60, 15);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		g.setConstraints(label4, c);
		colorPanel.add(label4);
		build(c, 1, 3, 1, 1, 20, 0);
		c.fill = GridBagConstraints.BOTH;
		g.setConstraints(color4, c);
		colorPanel.add(color4);
		build(c, 2, 3, 1, 1, 20, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		g.setConstraints(selected, c);
		colorPanel.add(selected);
		build(c, 0, 4, 1, 1, 60, 15);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		g.setConstraints(label5, c);
		colorPanel.add(label5);
		build(c, 1, 4, 1, 1, 20, 0);
		c.fill = GridBagConstraints.BOTH;
		g.setConstraints(color5, c);
		colorPanel.add(color5);
		build(c, 2, 4, 1, 1, 20, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		g.setConstraints(comment, c);
		colorPanel.add(comment);
		return colorPanel;
	}

	private JPanel constructAntialiasingPanel() {
		JPanel antialiasingPanel = new JPanel(new GridLayout(2, 1));
		JPanel temp = new JPanel(new BorderLayout());
		temp.setBorder(new TitledBorder("Antialiasing"));
		temp.add(antialiasingCheckBox, BorderLayout.CENTER);
		JPanel temp2 = new JPanel(new GridLayout(1, 5));
		ButtonGroup g = new ButtonGroup();
		g.add(westRadioBox);
		g.add(eastRadioBox);
		g.add(northRadioBox);
		g.add(southRadioBox);
		g.add(noneRadioBox);
		temp2.setBorder(new TitledBorder("Icon Bar Position"));
		temp2.add(westRadioBox);
		temp2.add(northRadioBox);
		temp2.add(eastRadioBox);
		temp2.add(southRadioBox);
		temp2.add(noneRadioBox);
		antialiasingPanel.add(temp);
		antialiasingPanel.add(temp2);
		return antialiasingPanel;
	}

	private JPanel constructFontPanel() {
		GridBagLayout g = new GridBagLayout();
		JPanel fontPanel = new JPanel(g);
		fontPanel.setBorder(new TitledBorder("Fonts"));
		GridBagConstraints c = new GridBagConstraints();
		Action inputAction = new AbstractAction("Input") {

			public void actionPerformed(ActionEvent arg0) {
				new FontMenu(true, true, pref);
			}
		};
		JButton input = new JButton(inputAction);
		Action outputAction = new AbstractAction("Output") {

			public void actionPerformed(ActionEvent arg0) {
				new FontMenu(false, true, pref);
				refresh();
			}
		};
		JButton output = new JButton(outputAction);
		build(c, 0, 0, 1, 1, 60, 15);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		g.setConstraints(input, c);
		fontPanel.add(input);
		build(c, 1, 0, 1, 1, 20, 0);
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.WEST;
		g.setConstraints(inputLabel, c);
		fontPanel.add(inputLabel);
		build(c, 0, 1, 1, 1, 60, 15);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		g.setConstraints(output, c);
		fontPanel.add(output);
		build(c, 1, 1, 1, 1, 20, 0);
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.WEST;
		g.setConstraints(outputLabel, c);
		fontPanel.add(outputLabel);
		return fontPanel;
	}

	private JPanel constructButtonPanel() {
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.setBorder(new EmptyBorder(8, 5, 1, 1));
		Action resetAction = new AbstractAction("Reset to Default") {

			public void actionPerformed(ActionEvent arg0) {
				pref = Preferences.getCloneOfPreferences();
				refresh();
			}
		};
		JButton RESET_TO_DEFAULT = new JButton(resetAction);
		buttonPanel.add(RESET_TO_DEFAULT, BorderLayout.CENTER);
		return buttonPanel;
	}

	/**
	 * Refreshes the frame.
	 *  
	 */
	public void refresh() {
		textFont.setText("" + pref.textFont.getFontName() + "  "
				+ pref.textFontSize + "");
		concordanceFont.setText("" + pref.htmlFontName + "  "
				+ pref.htmlFontSize + "");
		inputLabel.setText("  " + pref.input.getFontName() + "  "
				+ pref.inputSize + "  ");
		outputLabel.setText("  " + pref.output.getFontName() + "  "
				+ pref.outputSize + "  ");
		color1.setBackground(pref.backgroundColor);
		color2.setBackground(pref.foregroundColor);
		color3.setBackground(pref.subgraphColor);
		color4.setBackground(pref.selectedColor);
		color5.setBackground(pref.commentColor);
		dateCheckBox.setSelected(pref.date);
		filenameCheckBox.setSelected(pref.filename);
		pathnameCheckBox.setSelected(pref.pathname);
		frameCheckBox.setSelected(pref.frame);
		rightToLeftCheckBox.setSelected(pref.rightToLeft);
		antialiasingCheckBox.setSelected(pref.antialiasing);
		if (pref.iconBarPosition.equals(Preferences.ICON_BAR_WEST)) {
			westRadioBox.setSelected(true);
		} else if (pref.iconBarPosition.equals(Preferences.ICON_BAR_EAST)) {
			eastRadioBox.setSelected(true);
		} else if (pref.iconBarPosition.equals(Preferences.ICON_BAR_NORTH)) {
			northRadioBox.setSelected(true);
		} else if (pref.iconBarPosition.equals(Preferences.ICON_BAR_SOUTH)) {
			southRadioBox.setSelected(true);
		} else if (pref.iconBarPosition.equals(Preferences.NO_ICON_BAR)) {
			noneRadioBox.setSelected(true);
		} else {
			westRadioBox.setSelected(true);
		}
		if (pref.htmlViewer == null) {
			htmlViewer.setText("");
		} else {
			htmlViewer.setText(pref.htmlViewer.getAbsolutePath());
		}
		charByCharCheckBox.setSelected(pref.charByChar);
		morphologicalUseOfSpaceCheckBox.setSelected(pref.morphologicalUseOfSpace);
		if (pref.packagePath==null) {
			packageDirectory.setText("");
		}
		else {
			packageDirectory.setText(pref.packagePath.getAbsolutePath());
		}
		repaint();
	}
	
	protected static void fireTextFontChanged(Font font) {
		for (FontListener listener:textFontListeners) {
			listener.fontChanged(font);
		}
	}
	
	public static void addTextFontListener(FontListener listener) {
		textFontListeners.add(listener);
	}

	public static void removeTextFontListener(FontListener listener) {
		textFontListeners.remove(listener);
	}

	protected static void fireConcordanceFontChanged(Font font) {
		for (FontListener listener:concordanceFontListeners) {
			listener.fontChanged(font);
		}
	}
	
	public static void addConcordanceFontListener(FontListener listener) {
		concordanceFontListeners.add(listener);
	}

	public static void removeConcordanceFontListener(FontListener listener) {
		concordanceFontListeners.remove(listener);
	}

	private JPanel constructPage4() {
		JPanel p=new JPanel(null);
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
		p.setBorder(new EmptyBorder(5,5,5,5));
		JPanel p_=new JPanel(new GridLayout(2,1));
		p_.add(new JLabel("Choose the .bin dictionaries to use in Locate's morphological"));
		p_.add(new JLabel("mode:"));
		p.add(p_);
		JPanel p2=new JPanel(new BorderLayout());
		morphoDicListModel.clear();
		if (Preferences.pref.morphologicalDic!=null) {
			for (File f:Preferences.pref.morphologicalDic) {
				morphoDicListModel.addElement(f);
			}
		}
		final JList list=new JList(morphoDicListModel);
		list.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList l, Object value, int index, boolean isSelected1, boolean cellHasFocus) {
				File f=(File)value;
				return super.getListCellRendererComponent(l,f.getAbsolutePath(), index, isSelected1,
						cellHasFocus);
			}
		});
		JScrollPane scroll=new JScrollPane(list,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		p2.add(scroll);

		JPanel down=new JPanel(new BorderLayout());
		JPanel tmp = new JPanel(new GridLayout(1, 2));
		tmp.setBorder(new EmptyBorder(2, 2, 2, 2));
		JPanel tmp1 = new JPanel();
		JPanel tmp2 = new JPanel();
		tmp1.setBorder(new EmptyBorder(5, 5, 5, 5));
		tmp2.setBorder(new EmptyBorder(5, 5, 5, 5));
		tmp1.setLayout(new BorderLayout());
		tmp2.setLayout(new BorderLayout());
		Action addAction = new AbstractAction("Add") {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JFileChooser f = new JFileChooser();
						f.setMultiSelectionEnabled(true);
						f.addChoosableFileFilter(new PersonalFileFilter("bin","Binary dictionary"));
						f.setDialogTitle("Choose your morphological dictionaries");
						f.setDialogType(JFileChooser.OPEN_DIALOG);
						if (f.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
							return;
						}
						File[] files=f.getSelectedFiles();
						if (files==null) return;
						for (int i=0;i<files.length;i++) {
							morphoDicListModel.addElement(files[i]);
						}
					}
				});
			}
		};
		Action removeAction = new AbstractAction("Remove") {
			public void actionPerformed(ActionEvent arg0) {
				int[] indices=list.getSelectedIndices();
				for (int i=indices.length-1;i>=0;i--) {
					morphoDicListModel.remove(indices[i]);
				}
			}
		};
		JButton addButton=new JButton(addAction);
		JButton removeButton=new JButton(removeAction);
		tmp1.add(addButton);
		tmp2.add(removeButton);
		tmp.add(tmp1);
		tmp.add(tmp2);
		down.add(tmp,BorderLayout.EAST);
		p2.add(down,BorderLayout.SOUTH);
		p.add(p2);
		return p;
	}
	
	
}