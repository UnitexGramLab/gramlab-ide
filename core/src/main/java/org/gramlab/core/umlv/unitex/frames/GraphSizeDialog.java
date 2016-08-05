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
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * This class describes a dialog box that allows the user to adjust the current
 * graph's size.
 * 
 * @author Sébastien Paumier
 */
public class GraphSizeDialog extends JDialog {
	final JTextField Width = new JTextField();
	final JTextField Height = new JTextField();
	final static int resolutionDPI = Toolkit.getDefaultToolkit()
			.getScreenResolution();
	float X;
	float Y;
	static final int PIXELS = 0;
	static final int INCHES = 1;
	static final int CM = 2;
	static int unit = PIXELS;
	GraphFrame f;

	/**
	 * Constructs a new <code>GraphSizeMenu</code>
	 */
	GraphSizeDialog(GraphFrame f) {
		super(UnitexFrame.mainFrame, "Graph Size", true);
		configure(f);
		setContentPane(constructPanel());
		pack();
		setResizable(false);
		setLocationRelativeTo(UnitexFrame.mainFrame);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	void configure(GraphFrame frame) {
		this.f = frame;
		X = f.graphicalZone.getWidth();
		Y = f.graphicalZone.getHeight();
		Width.setText(stringValueOfX());
		Height.setText(stringValueOfY());
	}

	private float getValueOfX() {
		if (unit == PIXELS)
			return X;
		else if (unit == INCHES)
			return (X / resolutionDPI);
		else
			return ((X * 24 / resolutionDPI) / 10);
	}

	private float getValueOfY() {
		if (unit == PIXELS)
			return Y;
		else if (unit == INCHES)
			return (Y / resolutionDPI);
		else
			return ((Y * 24 / resolutionDPI) / 10);
	}

	String stringValueOfX() {
		if (unit == PIXELS)
			return String.valueOf((int) getValueOfX());
		/* This strange conversion is used to get 2 decimals */
		return String.valueOf((float) ((int) (getValueOfX() * 100)) / 100);
	}

	String stringValueOfY() {
		if (unit == PIXELS)
			return String.valueOf((int) getValueOfY());
		/* This strange conversion is used to get 2 decimals */
		return String.valueOf((float) ((int) (getValueOfY() * 100)) / 100);
	}

	private JPanel constructPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		panel.add(constructPanel1(), BorderLayout.NORTH);
		panel.add(constructDownPanel(), BorderLayout.CENTER);
		return panel;
	}

	private JPanel constructPanel1() {
		final JPanel panel1 = new JPanel(new GridLayout(1, 4));
		panel1.setBorder(new EmptyBorder(5, 5, 5, 5));
		final JLabel l1 = new JLabel("Width : ");
		l1.setHorizontalAlignment(SwingConstants.RIGHT);
		panel1.add(l1);
		panel1.add(Width);
		final JLabel l2 = new JLabel(" x Height : ");
		l2.setHorizontalAlignment(SwingConstants.RIGHT);
		panel1.add(l2);
		panel1.add(Height);
		return panel1;
	}

	private JPanel constructDownPanel() {
		final JPanel downPanel = new JPanel(new BorderLayout());
		downPanel.add(constructPanel2(), BorderLayout.WEST);
		downPanel.add(constructPanel3(), BorderLayout.CENTER);
		return downPanel;
	}

	private JPanel constructPanel2() {
		final JPanel panel2 = new JPanel(new GridLayout(3, 1));
		panel2.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel2.add(new JLabel("Unit :"));
		final JComboBox unitList = new JComboBox(new String[] { "Pixels",
				"Inches", "Cm" });
		unitList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final int newUnit = unitList.getSelectedIndex();
				if (newUnit == -1)
					return;
				/* We perform a conversion into the new unit */
				float x, y;
				try {
					x = new Float(Width.getText());
					y = new Float(Height.getText());
				} catch (final NumberFormatException z) {
					JOptionPane.showMessageDialog(null, "Invalid value",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (unit == PIXELS) {
					X = x;
					Y = y;
				} else if (unit == INCHES) {
					X = (x * resolutionDPI);
					Y = (y * resolutionDPI);
				} else {
					X = (float) ((x * resolutionDPI) / 2.4);
					Y = (float) ((y * resolutionDPI) / 2.4);
				}
				unit = newUnit;
				Width.setText(stringValueOfX());
				Height.setText(stringValueOfY());
				pack();
			}
		});
		unitList.setSelectedIndex(0);
		panel2.add(unitList);
		final Action a4Action = new AbstractAction("Set to A4") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				X = (float) ((29.7 * resolutionDPI) / 2.4);
				Y = (float) ((21 * resolutionDPI) / 2.4);
				Width.setText(stringValueOfX());
				Height.setText(stringValueOfY());
			}
		};
		final JButton A4 = new JButton(a4Action);
		panel2.add(A4);
		return panel2;
	}

	private JPanel constructPanel3() {
		final JPanel panel3 = new JPanel(new GridLayout(3, 1));
		panel3.setBorder(new EmptyBorder(5, 5, 5, 5));
		constructButtonPanel();
		panel3.add(new JLabel("Orientation :"));
		final Action orientationAction = new AbstractAction(
				"Portrait/Landscape") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				float tmp;
				tmp = X;
				X = Y;
				Y = tmp;
				Width.setText(stringValueOfX());
				Height.setText(stringValueOfY());
			}
		};
		final JButton orientation = new JButton(orientationAction);
		panel3.add(orientation);
		panel3.add(constructButtonPanel());
		return panel3;
	}

	private JPanel constructButtonPanel() {
		final JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
		final Action okAction = new AbstractAction("OK") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				float x, y;
				try {
					x = new Float(Width.getText());
					y = new Float(Height.getText());
				} catch (final NumberFormatException z) {
					JOptionPane.showMessageDialog(null, "Invalid value",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (unit == PIXELS) {
					X = x;
					Y = y;
				} else if (unit == INCHES) {
					X = (x * resolutionDPI);
					Y = (y * resolutionDPI);
				} else {
					X = (float) ((x * resolutionDPI) / 2.4);
					Y = (float) ((y * resolutionDPI) / 2.4);
				}
				setVisible(false);
				f.reSizeGraphicalZone((int) X, (int) Y);
			}
		};
		final JButton OK = new JButton(okAction);
		final Action cancelAction = new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		};
		final JButton CANCEL = new JButton(cancelAction);
		buttonPanel.add(OK);
		buttonPanel.add(CANCEL);
		return buttonPanel;
	}
}
