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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * This class describes a dialog box that allows the user to align the current
 * selected boxes in the the current graph. The user can also define if a grid
 * must be drawn on backend, and eventually the size of the grid's cells.
 * 
 * @author Sébastien Paumier
 */
public class GraphAlignmentDialog extends JDialog {
	GridBagLayout horizGridBag = new GridBagLayout();
	final GridBagLayout vertGridBag = new GridBagLayout();
	final GridBagConstraints vertConstraints = new GridBagConstraints();
	final JCheckBox showGrid = new JCheckBox();
	final JTextField nPixels = new JTextField("30");
	int nPix = 30;
	GraphFrame f;

	/**
	 * Constructs a new <code>GraphAlignmentMenu</code>.
	 * 
	 * @param isGrid
	 *            indicates if there is a grid in background
	 * @param nPix
	 *            size of the grid's cells
	 */
	GraphAlignmentDialog(GraphFrame f) {
		super(UnitexFrame.mainFrame, "Alignment", true);
		configure(f);
		setContentPane(constructPanel());
		pack();
		setResizable(false);
		nPixels.setEditable(f.graphicalZone.isGrid);
		nPix = f.graphicalZone.nPixels;
		nPixels.setText(nPix + "");
		showGrid.setSelected(f.graphicalZone.isGrid);
		setLocationRelativeTo(UnitexFrame.mainFrame);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	private JPanel constructPanel() {
		final Action topAction = new AbstractAction("Top") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				f.HTopAlign();
			}
		};
		final JButton top = new JButton(topAction);
		final Action centerHAction = new AbstractAction("Center") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				f.HCenterAlign();
			}
		};
		final JButton centerH = new JButton(centerHAction);
		final Action bottomAction = new AbstractAction("Bottom") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				f.HBottomAlign();
			}
		};
		final JButton bottom = new JButton(bottomAction);
		final Action leftAction = new AbstractAction("Left") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				f.VLeftAlign();
			}
		};
		final JButton left = new JButton(leftAction);
		final Action centerVAction = new AbstractAction("Center") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				f.VCenterAlign();
			}
		};
		final JButton centerV = new JButton(centerVAction);
		final Action rightAction = new AbstractAction("Right") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				f.VRightAlign();
			}
		};
		final JButton right = new JButton(rightAction);
		final JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		final Action okAction = new AbstractAction("OK") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!showGrid.isSelected()) {
					setVisible(false);
					f.setGrid(false, -1);
					return;
				}
				int n;
				try {
					n = Integer.parseInt(nPixels.getText());
				} catch (final NumberFormatException e) {
					return;
				}
				if (n < 10) {
					n = 10;
				}
				setVisible(false);
				f.setGrid(true, n);
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
		final JPanel horizontal = new JPanel(new GridLayout(3, 1));
		horizontal.setBorder(new TitledBorder("Horizontal"));
		horizontal.add(top);
		horizontal.add(centerH);
		horizontal.add(bottom);
		final JPanel vertical = new JPanel(new GridLayout(3, 1));
		vertical.setBorder(new TitledBorder("Vertical"));
		final JPanel leftPanel = new JPanel(new GridLayout(1, 2));
		leftPanel.add(new JPanel());
		leftPanel.add(left);
		vertical.add(leftPanel);
		final JPanel centerPane = new JPanel(vertGridBag);
		vertConstraints.fill = GridBagConstraints.NONE;
		vertGridBag.setConstraints(centerV, vertConstraints);
		centerPane.add(centerV);
		vertical.add(centerPane);
		final JPanel rightPanel = new JPanel(new GridLayout(1, 2));
		rightPanel.add(right);
		rightPanel.add(new JPanel());
		vertical.add(rightPanel);
		final JPanel centerPanel = new JPanel();
		centerPanel.add(horizontal);
		centerPanel.add(vertical);
		final JPanel southPanel = new JPanel();
		showGrid.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				nPixels.setEditable(showGrid.isSelected());
			}
		});
		southPanel.add(showGrid);
		southPanel.add(new JLabel("Use Grid, every"));
		southPanel.add(nPixels);
		southPanel.add(new JLabel("pixels"));
		final JPanel buttonPanel = new JPanel();
		buttonPanel.add(OK);
		buttonPanel.add(CANCEL);
		panel.add(centerPanel, BorderLayout.NORTH);
		panel.add(southPanel, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		return panel;
	}

	void configure(GraphFrame frame) {
		this.f = frame;
		showGrid.setSelected(f.graphicalZone.isGrid);
		nPixels.setEnabled(f.graphicalZone.isGrid);
		int n = f.graphicalZone.nPixels;
		if (n < 10) {
			n = 10;
		}
		nPixels.setText(n + "");
	}
}
