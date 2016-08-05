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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
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

import org.gramlab.core.umlv.unitex.FontInfo;
import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;
import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.config.Preferences;
import org.gramlab.core.umlv.unitex.grf.GraphPresentationInfo;

/**
 * This class describes the graph presentation dialog box, that allows the user
 * to set current graph's options, colors and fonts.
 * 
 * @author Sébastien Paumier
 */
class GraphPresentationDialog extends JDialog {
	final JCheckBox dateCheckBox = new JCheckBox();
	final JCheckBox filenameCheckBox = new JCheckBox();
	final JCheckBox pathnameCheckBox = new JCheckBox();
	final JCheckBox frameCheckBox = new JCheckBox();
	final JLabel rightToLeftLabel = new JLabel("Right to Left  ");
	final JCheckBox rightToLeftCheckBox = new JCheckBox();
	final JCheckBox antialiasingCheckBox = new JCheckBox(
			"Enable antialising for rendering graphs");
	final JPanel color1 = getColorRectangle();
	final JPanel color2 = getColorRectangle();
	final JPanel color3 = getColorRectangle();
	final JPanel color4 = getColorRectangle();
	final JPanel color5 = getColorRectangle();
	final JLabel inputLabel = new JLabel("", SwingConstants.LEFT);
	final JLabel outputLabel = new JLabel("", SwingConstants.LEFT);
	final JRadioButton westRadioBox = new JRadioButton("West");
	final JRadioButton eastRadioBox = new JRadioButton("East");
	final JRadioButton northRadioBox = new JRadioButton("North");
	final JRadioButton southRadioBox = new JRadioButton("South");
	final JRadioButton noneRadioBox = new JRadioButton("None");
	GraphPresentationInfo info;

	public GraphPresentationDialog(GraphPresentationInfo i,
			boolean showRightToLeftCheckBox) {
		this(UnitexFrame.mainFrame, i, showRightToLeftCheckBox);
	}

	/**
	 * Constructs a new <code>GraphPresentationMenu</code>
	 */
	public GraphPresentationDialog(Frame owner, GraphPresentationInfo i,
			boolean showRightToLeftCheckBox) {
		super(owner, "Graph presentation", true);
		setContentPane(constructPanel());
		configure(i.clone(), showRightToLeftCheckBox);
		pack();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				info = null;
			}
		});
		setLocationRelativeTo(owner);
	}

	private JPanel constructPanel() {
		final JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		final GridBagConstraints gbc = new GridBagConstraints();
		final JPanel upPanel = constructUpPanel();
		final JPanel downPanel = constructDownPanel();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(upPanel, gbc);
		panel.add(constructAntialiasingPanel(), gbc);
		panel.add(downPanel, gbc);
		return panel;
	}

	private JPanel constructAntialiasingPanel() {
		final JPanel antialiasingPanel = new JPanel(new GridLayout(2, 1));
		final JPanel temp = new JPanel(new BorderLayout());
		temp.setBorder(new TitledBorder("Antialiasing"));
		antialiasingCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				info.setAntialiasing(antialiasingCheckBox.isSelected());
			}
		});
		temp.add(antialiasingCheckBox, BorderLayout.CENTER);
		final JPanel temp2 = new JPanel(new GridLayout(1, 5));
		final ButtonGroup g = new ButtonGroup();
		westRadioBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (westRadioBox.isSelected()) {
					info.setIconBarPosition(Preferences.ICON_BAR_WEST);
				}
			}
		});
		g.add(westRadioBox);
		eastRadioBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (eastRadioBox.isSelected()) {
					info.setIconBarPosition(Preferences.ICON_BAR_EAST);
				}
			}
		});
		g.add(eastRadioBox);
		northRadioBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (northRadioBox.isSelected()) {
					info.setIconBarPosition(Preferences.ICON_BAR_NORTH);
				}
			}
		});
		g.add(northRadioBox);
		southRadioBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (southRadioBox.isSelected()) {
					info.setIconBarPosition(Preferences.ICON_BAR_SOUTH);
				}
			}
		});
		g.add(southRadioBox);
		noneRadioBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (noneRadioBox.isSelected()) {
					info.setIconBarPosition(Preferences.NO_ICON_BAR);
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
		final JPanel upPanel = new JPanel(new BorderLayout());
		upPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		upPanel.add(constructDisplayPanel(), BorderLayout.WEST);
		upPanel.add(constructColorPanel(), BorderLayout.CENTER);
		return upPanel;
	}

	private JPanel constructDownPanel() {
		final JPanel downPanel = new JPanel(new BorderLayout());
		downPanel.add(constructFontPanel(), BorderLayout.CENTER);
		downPanel.add(constructButtonPanel(), BorderLayout.EAST);
		return downPanel;
	}

	private JPanel constructDisplayPanel() {
		final JPanel displayPanel = new JPanel(new GridLayout(5, 1));
		displayPanel.setBorder(new TitledBorder("Display"));
		final JPanel display1 = new JPanel(new BorderLayout());
		final JPanel display2 = new JPanel(new BorderLayout());
		final JPanel display3 = new JPanel(new BorderLayout());
		final JPanel display4 = new JPanel(new BorderLayout());
		final JPanel display5 = new JPanel(new BorderLayout());
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
		final GridBagLayout g = new GridBagLayout();
		final JPanel colorPanel = new JPanel(g);
		colorPanel.setBorder(new TitledBorder("Colors"));
		final Action backgroundAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final Color c = JColorChooser.showDialog(UnitexFrame.mainFrame,
						"Background Color", info.getBackgroundColor());
				if (c != null) {
					info.setBackgroundColor(c);
					configure(info, rightToLeftCheckBox.isVisible());
				}
			}
		};
		final JButton background = new JButton(backgroundAction);
		final Action foregroundAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final Color c = JColorChooser.showDialog(UnitexFrame.mainFrame,
						"Foreground Color", info.getForegroundColor());
				if (c != null) {
					info.setForegroundColor(c);
					configure(info, rightToLeftCheckBox.isVisible());
				}
			}
		};
		final JButton foreground = new JButton(foregroundAction);
		final Action subgraphAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final Color c = JColorChooser.showDialog(UnitexFrame.mainFrame,
						"Auxiliary Nodes Color", info.getSubgraphColor());
				if (c != null) {
					info.setSubgraphColor(c);
					configure(info, rightToLeftCheckBox.isVisible());
				}
			}
		};
		final JButton subgraph = new JButton(subgraphAction);
		final Action selectedAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final Color c = JColorChooser.showDialog(UnitexFrame.mainFrame,
						"Selected Nodes Color", info.getSelectedColor());
				if (c != null) {
					info.setSelectedColor(c);
					configure(info, rightToLeftCheckBox.isVisible());
				}
			}
		};
		final JButton selected = new JButton(selectedAction);
		final Action commentAction = new AbstractAction("Set...") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final Color c = JColorChooser.showDialog(UnitexFrame.mainFrame,
						"Comment Nodes Color", info.getCommentColor());
				if (c != null) {
					info.setCommentColor(c);
					configure(info, rightToLeftCheckBox.isVisible());
				}
			}
		};
		final JButton comment = new JButton(commentAction);
		final JLabel label1 = new JLabel("  Background:  ", SwingConstants.LEFT);
		final JLabel label2 = new JLabel("  Foreground:  ", SwingConstants.LEFT);
		final JLabel label3 = new JLabel("  Auxiliary Nodes:  ",
				SwingConstants.LEFT);
		final JLabel label4 = new JLabel("  Selected Nodes:  ",
				SwingConstants.LEFT);
		final JLabel label5 = new JLabel("  Comment Nodes:  ",
				SwingConstants.LEFT);
		final GridBagConstraints c = new GridBagConstraints();
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
		final JPanel fontPanel = new JPanel(new GridBagLayout());
		fontPanel.setBorder(new TitledBorder("Fonts"));
		final GridBagConstraints gbc = new GridBagConstraints();
		final Action inputAction = new AbstractAction("Input") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final FontInfo i = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.newFontDialog(info.getInput());
				if (i != null) {
					info.setInput(i);
					configure(info, rightToLeftCheckBox.isVisible());
				}
			}
		};
		final JButton input = new JButton(inputAction);
		final Action outputAction = new AbstractAction("Output") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final FontInfo i = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.newFontDialog(info.getOutput());
				if (i != null) {
					info.setOutput(i);
					configure(info, rightToLeftCheckBox.isVisible());
				}
			}
		};
		final JButton output = new JButton(outputAction);
		build(gbc, 0, 0, 1, 1, 60, 15);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		fontPanel.add(input, gbc);
		build(gbc, 1, 0, 1, 1, 20, 0);
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.anchor = GridBagConstraints.WEST;
		fontPanel.add(inputLabel, gbc);
		build(gbc, 0, 1, 1, 1, 60, 15);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		fontPanel.add(output, gbc);
		build(gbc, 1, 1, 1, 1, 20, 0);
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.anchor = GridBagConstraints.WEST;
		fontPanel.add(outputLabel, gbc);
		return fontPanel;
	}

	private JPanel constructButtonPanel() {
		final JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
		buttonPanel.setBorder(new EmptyBorder(5, 5, 0, 0));
		final Action defaultAction = new AbstractAction("Default") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getCurrentFocusedGraphFrame() == null)
					return;
				info = ConfigManager.getManager()
						.getGraphPresentationPreferences(null);
				configure(info, rightToLeftCheckBox.isVisible());
			}
		};
		final JButton DEFAULT = new JButton(defaultAction);
		final Action okAction = new AbstractAction("OK") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				info.setFilename(filenameCheckBox.isSelected());
				info.setFrame(frameCheckBox.isSelected());
				info.setDate(dateCheckBox.isSelected());
				if (rightToLeftCheckBox.isVisible()) {
					info.setRightToLeft(rightToLeftCheckBox.isSelected());
				}
				info.setPathname(pathnameCheckBox.isSelected());
				setVisible(false);
			}
		};
		final JButton OK = new JButton(okAction);
		final Action cancelAction = new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				info = null;
				setVisible(false);
			}
		};
		final JButton CANCEL = new JButton(cancelAction);
		buttonPanel.add(DEFAULT);
		buttonPanel.add(OK);
		buttonPanel.add(CANCEL);
		return buttonPanel;
	}

	/**
	 * We refresh all the dialog with the given presentation info.
	 * 
	 * @param i
	 */
	void configure(GraphPresentationInfo i, boolean showRightToLeftCheckBox) {
		this.info = i;
		color1.setBackground(info.getBackgroundColor());
		color2.setBackground(info.getForegroundColor());
		color3.setBackground(info.getSubgraphColor());
		color4.setBackground(info.getSelectedColor());
		color5.setBackground(info.getCommentColor());
		inputLabel.setText("  " + info.getInput().getFont().getFontName()
				+ "  " + info.getInput().getSize() + "  ");
		outputLabel.setText("  " + info.getOutput().getFont().getFontName()
				+ "  " + info.getOutput().getSize() + "  ");
		dateCheckBox.setSelected(info.isDate());
		filenameCheckBox.setSelected(info.isFilename());
		pathnameCheckBox.setSelected(info.isPathname());
		frameCheckBox.setSelected(info.isFrame());
		rightToLeftCheckBox.setSelected(info.isRightToLeft());
		rightToLeftCheckBox.setVisible(showRightToLeftCheckBox);
		rightToLeftLabel.setVisible(showRightToLeftCheckBox);
		antialiasingCheckBox.setSelected(i.isAntialiasing());
		if (i.getIconBarPosition().equals(Preferences.ICON_BAR_WEST)) {
			westRadioBox.setSelected(true);
		} else if (i.getIconBarPosition().equals(Preferences.ICON_BAR_EAST)) {
			eastRadioBox.setSelected(true);
		} else if (i.getIconBarPosition().equals(Preferences.ICON_BAR_NORTH)) {
			northRadioBox.setSelected(true);
		} else if (i.getIconBarPosition().equals(Preferences.ICON_BAR_SOUTH)) {
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
		final JPanel p = new JPanel(null);
		p.setMinimumSize(new Dimension(70, 22));
		p.setSize(new Dimension(70, 22));
		p.setPreferredSize(new Dimension(70, 22));
		return p;
	}
}
