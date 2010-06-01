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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import fr.umlv.unitex.FontInfo;
import fr.umlv.unitex.GraphPresentationInfo;
import fr.umlv.unitex.Preferences;

/**
 * This class describes the graph presentation dialog box, that allows the user
 * to set current graph's options, colors and fonts.
 * 
 * @author Sébastien Paumier
 *  
 */
public class GraphPresentationDialog extends JDialog {

	JCheckBox dateCheckBox = new JCheckBox();
	JCheckBox filenameCheckBox = new JCheckBox();
	JCheckBox pathnameCheckBox = new JCheckBox();
	JCheckBox frameCheckBox = new JCheckBox();
	JLabel rightToLeftLabel=new JLabel("Right to Left  ");
	JCheckBox rightToLeftCheckBox = new JCheckBox();
	JCheckBox antialiasingCheckBox=new JCheckBox(
			"Enable antialising for rendering graphs");
	JPanel color1 = getColorRectangle();
	JPanel color2 = getColorRectangle();
	JPanel color3 = getColorRectangle();
	JPanel color4 = getColorRectangle();
	JPanel color5 = getColorRectangle();
	JLabel inputLabel = new JLabel("", SwingConstants.LEFT);
	JLabel outputLabel = new JLabel("", SwingConstants.LEFT);
	JRadioButton westRadioBox = new JRadioButton("West");
	JRadioButton eastRadioBox = new JRadioButton("East");
	JRadioButton northRadioBox = new JRadioButton("North");
	JRadioButton southRadioBox = new JRadioButton("South");
	JRadioButton noneRadioBox = new JRadioButton("None");
	GraphPresentationInfo info;

	/**
	 * Constructs a new <code>GraphPresentationMenu</code>
	 *  
	 */
	public GraphPresentationDialog(GraphPresentationInfo i,boolean showRightToLeftCheckBox) {
		super(UnitexFrame.mainFrame, "Presentation", true);
		setContentPane(constructPanel());
		configure(i.clone(),showRightToLeftCheckBox);
		pack();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				info=null;
			}
		});
		setLocationRelativeTo(UnitexFrame.mainFrame);
	}


	private JPanel constructPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagConstraints gbc = new GridBagConstraints();
		JPanel upPanel = constructUpPanel();
		JPanel downPanel = constructDownPanel();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(upPanel,gbc);
		panel.add(constructAntialiasingPanel(),gbc);
		panel.add(downPanel,gbc);
		return panel;
	}

	private JPanel constructAntialiasingPanel() {
		JPanel antialiasingPanel = new JPanel(new GridLayout(2, 1));
		JPanel temp = new JPanel(new BorderLayout());
		temp.setBorder(new TitledBorder("Antialiasing"));
		antialiasingCheckBox.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				info.antialiasing=antialiasingCheckBox.isSelected();
			}
		});
		temp.add(antialiasingCheckBox, BorderLayout.CENTER);
		JPanel temp2 = new JPanel(new GridLayout(1, 5));
		ButtonGroup g = new ButtonGroup();
		westRadioBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (westRadioBox.isSelected()) {
					info.iconBarPosition=Preferences.ICON_BAR_WEST;
				}
			}
		});
		g.add(westRadioBox);
		eastRadioBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (eastRadioBox.isSelected()) {
					info.iconBarPosition=Preferences.ICON_BAR_EAST;
				}
			}
		});
		g.add(eastRadioBox);
		northRadioBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (northRadioBox.isSelected()) {
					info.iconBarPosition=Preferences.ICON_BAR_NORTH;
				}
			}
		});
		g.add(northRadioBox);
		southRadioBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (southRadioBox.isSelected()) {
					info.iconBarPosition=Preferences.ICON_BAR_SOUTH;
				}
			}
		});
		g.add(southRadioBox);
		noneRadioBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (noneRadioBox.isSelected()) {
					info.iconBarPosition=Preferences.NO_ICON_BAR;
				}
			}
		});
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

	private JPanel constructUpPanel() {
		JPanel upPanel = new JPanel(new BorderLayout());
		upPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		upPanel.add(constructDisplayPanel(), BorderLayout.WEST);
		upPanel.add(constructColorPanel(), BorderLayout.CENTER);
		return upPanel;
	}

	private JPanel constructDownPanel() {
		JPanel downPanel = new JPanel(new BorderLayout());
		downPanel.add(constructFontPanel(), BorderLayout.CENTER);
		downPanel.add(constructButtonPanel(), BorderLayout.EAST);
		return downPanel;
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
		display5.add(rightToLeftCheckBox, BorderLayout.WEST);
		display5.add(rightToLeftLabel, BorderLayout.CENTER);
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
				Color c=JColorChooser.showDialog(
						UnitexFrame.mainFrame, "Background Color",
						info.backgroundColor);
				if (c!=null) {
					info.backgroundColor=c;
					configure(info,rightToLeftCheckBox.isVisible());
				}
			}
		};
		JButton background = new JButton(backgroundAction);
		Action foregroundAction = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				Color c=JColorChooser.showDialog(
						UnitexFrame.mainFrame, "Foreground Color",
						info.foregroundColor);
				if (c!=null) {
					info.foregroundColor=c;
					configure(info,rightToLeftCheckBox.isVisible());
				}
			}
		};
		JButton foreground = new JButton(foregroundAction);
		Action subgraphAction = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				Color c=JColorChooser.showDialog(
						UnitexFrame.mainFrame, "Auxiliary Nodes Color",
						info.subgraphColor);
				if (c!=null) {
					info.subgraphColor=c;
					configure(info,rightToLeftCheckBox.isVisible());
				}
			}
		};
		JButton subgraph = new JButton(subgraphAction);
		Action selectedAction = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				Color c=JColorChooser.showDialog(
						UnitexFrame.mainFrame, "Selected Nodes Color",
						info.selectedColor);
				if (c!=null) {
					info.selectedColor=c;
					configure(info,rightToLeftCheckBox.isVisible());
				}
			}
		};
		JButton selected = new JButton(selectedAction);
		Action commentAction = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				Color c=JColorChooser.showDialog(
						UnitexFrame.mainFrame, "Comment Nodes Color",
						info.commentColor);
				if (c!=null) {
					info.commentColor=c;
					configure(info,rightToLeftCheckBox.isVisible());
				}
			}
		};
		JButton comment = new JButton(commentAction);
		JLabel label1 = new JLabel("  Background:  ", SwingConstants.LEFT);
		JLabel label2 = new JLabel("  Foreground:  ", SwingConstants.LEFT);
		JLabel label3 = new JLabel("  Auxiliary Nodes:  ", SwingConstants.LEFT);
		JLabel label4 = new JLabel("  Selected Nodes:  ", SwingConstants.LEFT);
		JLabel label5 = new JLabel("  Comment Nodes:  ", SwingConstants.LEFT);
		GridBagConstraints c = new GridBagConstraints();
		build(c, 0, 0, 1, 1, 60, 15);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		g.setConstraints(label1, c);
		colorPanel.add(label1);
		build(c, 1, 0, 1, 1, 20, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
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
		c.fill = GridBagConstraints.HORIZONTAL;
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
		c.fill = GridBagConstraints.HORIZONTAL;
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
		c.fill = GridBagConstraints.HORIZONTAL;
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
		c.fill = GridBagConstraints.HORIZONTAL;
		g.setConstraints(color5, c);
		colorPanel.add(color5);
		build(c, 2, 4, 1, 1, 20, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		g.setConstraints(comment, c);
		colorPanel.add(comment);
		return colorPanel;
	}

	private JPanel constructFontPanel() {
		JPanel fontPanel = new JPanel(new GridBagLayout());
		fontPanel.setBorder(new TitledBorder("Fonts"));
		GridBagConstraints gbc = new GridBagConstraints();
		Action inputAction = new AbstractAction("Input") {
			public void actionPerformed(ActionEvent arg0) {
				FontInfo i=UnitexFrame.getFrameManager().newFontDialog(info.input);
				if (i!=null) {
					info.input=i;
					configure(info,rightToLeftCheckBox.isVisible());
				}
			}
		};
		JButton input = new JButton(inputAction);
		Action outputAction = new AbstractAction("Output") {
			public void actionPerformed(ActionEvent arg0) {
				FontInfo i=UnitexFrame.getFrameManager().newFontDialog(info.output);
				if (i!=null) {
					info.output=i;
					configure(info,rightToLeftCheckBox.isVisible());
				}
			}
		};
		JButton output = new JButton(outputAction);
		build(gbc, 0, 0, 1, 1, 60, 15);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		fontPanel.add(input,gbc);
		build(gbc, 1, 0, 1, 1, 20, 0);
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.anchor = GridBagConstraints.WEST;
		fontPanel.add(inputLabel,gbc);
		build(gbc, 0, 1, 1, 1, 60, 15);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		fontPanel.add(output,gbc);
		build(gbc, 1, 1, 1, 1, 20, 0);
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.anchor = GridBagConstraints.WEST;
		fontPanel.add(outputLabel,gbc);
		return fontPanel;
	}

	private JPanel constructButtonPanel() {
		JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
		buttonPanel.setBorder(new EmptyBorder(5, 5, 0, 0));
		Action defaultAction = new AbstractAction("Default") {
			public void actionPerformed(ActionEvent arg0) {
				if (UnitexFrame.mainFrame.frameManager.getCurrentFocusedGraphFrame() == null)
					return;
				info=Preferences.getGraphPresentationPreferences();
				configure(info,rightToLeftCheckBox.isVisible());
			}
		};
		JButton DEFAULT = new JButton(defaultAction);
		Action okAction = new AbstractAction("OK") {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		};
		JButton OK = new JButton(okAction);
		Action cancelAction = new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent arg0) {
				info=null;
				setVisible(false);
			}
		};
		JButton CANCEL = new JButton(cancelAction);
		buttonPanel.add(DEFAULT);
		buttonPanel.add(OK);
		buttonPanel.add(CANCEL);
		return buttonPanel;
	}

	/**
	 * We refresh all the dialog with the given presentation info.
	 * @param i
	 */
	void configure(GraphPresentationInfo i,boolean showRightToLeftCheckBox) {
		this.info=i;
		color1.setBackground(info.backgroundColor);
		color2.setBackground(info.foregroundColor);
		color3.setBackground(info.subgraphColor);
		color4.setBackground(info.selectedColor);
		color5.setBackground(info.commentColor);
		inputLabel.setText("  " + info.input.font.getFontName() + "  "
				+ info.input.size + "  ");
		outputLabel.setText("  " + info.output.font.getFontName() + "  "
				+ info.output.size + "  ");
		dateCheckBox.setSelected(info.date);
		filenameCheckBox.setSelected(info.filename);
		pathnameCheckBox.setSelected(info.pathname);
		frameCheckBox.setSelected(info.frame);
		rightToLeftCheckBox.setSelected(info.rightToLeft);
		rightToLeftCheckBox.setVisible(showRightToLeftCheckBox);
		rightToLeftLabel.setVisible(showRightToLeftCheckBox);
		antialiasingCheckBox.setSelected(i.antialiasing);
		if (i.iconBarPosition.equals(Preferences.ICON_BAR_WEST)) {
			westRadioBox.setSelected(true);
		} else if (i.iconBarPosition.equals(Preferences.ICON_BAR_EAST)) {
			eastRadioBox.setSelected(true);
		} else if (i.iconBarPosition.equals(Preferences.ICON_BAR_NORTH)) {
			northRadioBox.setSelected(true);
		} else if (i.iconBarPosition.equals(Preferences.ICON_BAR_SOUTH)) {
			southRadioBox.setSelected(true);
		} else {
			noneRadioBox.setSelected(true);
		}
		pack();
	}

	GraphPresentationInfo getGraphPresentationInfo() {
		return info;
	}

	JPanel getColorRectangle() {
		JPanel p=new JPanel(null);
		p.setMinimumSize(new Dimension(70,22));
		p.setSize(new Dimension(70,22));
		p.setPreferredSize(new Dimension(70,22));
		return p;
	}

}