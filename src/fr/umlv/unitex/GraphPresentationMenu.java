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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import fr.umlv.unitex.frames.GraphFrame;
import fr.umlv.unitex.frames.UnitexFrame;

/**
 * This class describes the graph presentation dialog box, that allows the user
 * to set current graph's options, colors and fonts.
 * 
 * @author Sébastien Paumier
 *  
 */
public class GraphPresentationMenu extends JDialog {

	JCheckBox dateCheckBox = new JCheckBox();
	JCheckBox filenameCheckBox = new JCheckBox();
	JCheckBox pathnameCheckBox = new JCheckBox();
	JCheckBox frameCheckBox = new JCheckBox();
	JCheckBox rightToLeftCheckBox = new JCheckBox();
	JPanel color1 = ColorRectangle.getColorRectangle();
	JPanel color2 = ColorRectangle.getColorRectangle();
	JPanel color3 = ColorRectangle.getColorRectangle();
	JPanel color4 = ColorRectangle.getColorRectangle();
	JPanel color5 = ColorRectangle.getColorRectangle();
	JLabel inputLabel = new JLabel("", SwingConstants.LEFT);
	JLabel outputLabel = new JLabel("", SwingConstants.LEFT);

	static GraphPresentationMenu pref;
	Preferences preferences;

	/**
	 * Constructs a new <code>GraphPresentationMenu</code>
	 *  
	 */
	public GraphPresentationMenu() {
		super(UnitexFrame.mainFrame, "Presentation", true);
		preferences = UnitexFrame.mainFrame.frameManager.getCurrentFocusedGraphFrame().graphicalZone.getPreferences()
				.getClone();
		setContentPane(constructPanel());
		pack();
		setResizable(false);
		pref = this;
		showPresentation();
	}

	/**
	 * Refreshes the diolog box
	 *  
	 */
	public void refresh() {
		// here we refresh the components from the Preferences.temp preferences
		color1.setBackground(preferences.backgroundColor);
		color2.setBackground(preferences.foregroundColor);
		color3.setBackground(preferences.subgraphColor);
		color4.setBackground(preferences.selectedColor);
		color5.setBackground(preferences.commentColor);
		inputLabel.setText("  " + preferences.input.getFontName() + "  "
				+ preferences.inputSize + "  ");
		outputLabel.setText("  " + preferences.output.getFontName() + "  "
				+ preferences.outputSize + "  ");
		dateCheckBox.setSelected(preferences.date);
		filenameCheckBox.setSelected(preferences.filename);
		pathnameCheckBox.setSelected(preferences.pathname);
		frameCheckBox.setSelected(preferences.frame);
		rightToLeftCheckBox.setSelected(preferences.rightToLeft);
		setResizable(true);
		pack();
		setResizable(false);
		repaint();
	}

	/**
	 * Shows the dialog box
	 *  
	 */
	public void showPresentation() {
		setLocationRelativeTo(UnitexFrame.mainFrame);
		setVisible(true);
	}

	private JPanel constructPanel() {
		GridBagLayout g = new GridBagLayout();
		JPanel panel = new JPanel(g);
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagConstraints c = new GridBagConstraints();
		JPanel upPanel = constructUpPanel();
		JPanel downPanel = constructDownPanel();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		g.setConstraints(upPanel, c);
		panel.add(upPanel);
		c.fill = GridBagConstraints.BOTH;
		g.setConstraints(downPanel, c);
		panel.add(downPanel);
		return panel;
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
		downPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		downPanel.setLayout(new BorderLayout());
		downPanel.add(constructFontPanel(), BorderLayout.CENTER);
		downPanel.add(constructButtonPanel(), BorderLayout.EAST);
		return downPanel;
	}

	private JPanel constructDisplayPanel() {
		JPanel displayPanel = new JPanel(new GridLayout(5, 1));
		displayPanel.setBorder(new TitledBorder("Display"));
		dateCheckBox.setSelected(preferences.date);
		filenameCheckBox.setSelected(preferences.filename);
		pathnameCheckBox.setSelected(preferences.pathname);
		frameCheckBox.setSelected(preferences.frame);
		rightToLeftCheckBox.setSelected(preferences.rightToLeft);
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
		display5.add(new JLabel("Right to Left  "), BorderLayout.CENTER);
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
				preferences.backgroundColor = JColorChooser.showDialog(
						UnitexFrame.mainFrame, "Background Color",
						preferences.backgroundColor);
				GraphPresentationMenu.pref.refresh();
			}
		};
		JButton background = new JButton(backgroundAction);
		Action foregroundAction = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				preferences.foregroundColor = JColorChooser.showDialog(
						UnitexFrame.mainFrame, "Foreground Color",
						preferences.foregroundColor);
				GraphPresentationMenu.pref.refresh();
			}
		};
		JButton foreground = new JButton(foregroundAction);
		Action subgraphAction = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				preferences.subgraphColor = JColorChooser.showDialog(
						UnitexFrame.mainFrame, "Auxiliary Nodes Color",
						preferences.subgraphColor);
				GraphPresentationMenu.pref.refresh();
			}
		};
		JButton subgraph = new JButton(subgraphAction);
		Action selectedAction = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				preferences.selectedColor = JColorChooser.showDialog(
						UnitexFrame.mainFrame, "Selected Nodes Color",
						preferences.selectedColor);
				GraphPresentationMenu.pref.refresh();
			}
		};
		JButton selected = new JButton(selectedAction);
		Action commentAction = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				preferences.commentColor = JColorChooser.showDialog(
						UnitexFrame.mainFrame, "Comment Nodes Color",
						preferences.commentColor);
				GraphPresentationMenu.pref.refresh();
			}
		};
		JButton comment = new JButton(commentAction);

		color1.setBackground(preferences.backgroundColor);
		color2.setBackground(preferences.foregroundColor);
		color3.setBackground(preferences.subgraphColor);
		color4.setBackground(preferences.selectedColor);
		color5.setBackground(preferences.commentColor);
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
		GridBagLayout g = new GridBagLayout();
		JPanel fontPanel = new JPanel(g);
		fontPanel.setBorder(new TitledBorder("Fonts"));
		GridBagConstraints c = new GridBagConstraints();
		Action inputAction = new AbstractAction("Input") {
			public void actionPerformed(ActionEvent arg0) {
				FontInfo info=UnitexFrame.getFrameManager().newFontDialog(preferences.input,preferences.inputSize);
				if (info!=null) {
					preferences.input=info.font;
					preferences.inputFontStyle=info.font.getStyle();
					preferences.inputSize=info.size;
					GraphPresentationMenu.pref.refresh();
				}
			}
		};
		JButton input = new JButton(inputAction);
		Action outputAction = new AbstractAction("Output") {
			public void actionPerformed(ActionEvent arg0) {
				FontInfo info=UnitexFrame.getFrameManager().newFontDialog(preferences.output,preferences.outputSize);
				if (info!=null) {
					preferences.output=info.font;
					preferences.outputFontStyle=info.font.getStyle();
					preferences.outputSize=info.size;
					GraphPresentationMenu.pref.refresh();
				}
			}
		};
		JButton output = new JButton(outputAction);

		inputLabel.setText("  " + preferences.input.getFontName() + "  "
				+ preferences.inputSize + "  ");
		outputLabel.setText("  " + preferences.output.getFontName() + "  "
				+ preferences.outputSize + "  ");
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
		JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
		buttonPanel.setBorder(new EmptyBorder(5, 5, 0, 0));
		Action defaultAction = new AbstractAction("Default") {
			public void actionPerformed(ActionEvent arg0) {
				if (UnitexFrame.mainFrame.frameManager.getCurrentFocusedGraphFrame() == null)
					return;
				preferences = Preferences.getCloneOfPreferences();
				GraphPresentationMenu.pref.refresh();
			}
		};
		JButton DEFAULT = new JButton(defaultAction);
		Action okAction = new AbstractAction("OK") {
			public void actionPerformed(ActionEvent arg0) {
				GraphFrame f = UnitexFrame.mainFrame.frameManager.getCurrentFocusedGraphFrame();
				if (f == null)
					return;
				Dimension d = f.getSize(null);
				preferences.date = GraphPresentationMenu.pref.dateCheckBox
						.isSelected();
				preferences.filename = GraphPresentationMenu.pref.filenameCheckBox
						.isSelected();
				preferences.pathname = GraphPresentationMenu.pref.pathnameCheckBox
						.isSelected();
				preferences.frame = GraphPresentationMenu.pref.frameCheckBox
						.isSelected();
				preferences.rightToLeft = GraphPresentationMenu.pref.rightToLeftCheckBox
						.isSelected();
				f.graphicalZone.pref = preferences.getClone();
				f.texte.setFont(f.graphicalZone.pref.input);
				f.pack();
				f.setSize(d);
				f.graphicalZone.updateAllBoxes();
				GraphPresentationMenu.pref.setVisible(false);
				f.setModified(true);
			}
		};
		JButton OK = new JButton(okAction);
		Action cancelAction = new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent arg0) {
				GraphPresentationMenu.pref.setVisible(false);
			}
		};
		JButton CANCEL = new JButton(cancelAction);
		buttonPanel.add(DEFAULT);
		buttonPanel.add(OK);
		buttonPanel.add(CANCEL);
		return buttonPanel;
	}

}