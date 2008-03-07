 /*
  * Unitex
  *
  * Copyright (C) 2001-2008 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import javax.swing.*;
import javax.swing.border.*;

/**
 * This class describes a dialog box that allows the user to align the current
 * selected boxes in the the current graph. The user can also define if a grid
 * must be drawn on backend, and eventually the size of the grid's cells.
 * 
 * @author Sébastien Paumier
 *  
 */
public class GraphAlignmentMenu extends JDialog {

	GridBagLayout horizGridBag = new GridBagLayout();
	GridBagLayout vertGridBag = new GridBagLayout();
	//GridBagConstraints horizConstraints= new GridBagConstraints();
	GridBagConstraints vertConstraints = new GridBagConstraints();
	JCheckBox checkBox = new JCheckBox();
	NumericTextField nPixels = new NumericTextField(3, String.valueOf(nPix));

	static GraphAlignmentMenu pref;

	static int nPix = 30;

	/**
	 * Constructs a new <code>GraphAlignmentMenu</code>.
	 * 
	 * @param isGrid
	 *            indicates if there is a grid in backend
	 * @param nPix
	 *            size of the grid's cells
	 */
	public GraphAlignmentMenu(boolean isGrid, int nPix) {
		super(UnitexFrame.mainFrame, "Alignment", true);
		setContentPane(constructPanel());
		pack();
		setResizable(false);
		pref = this;
		nPixels.setEditable(isGrid);
		nPixels.setText(new Integer(nPix).toString());
		checkBox.setSelected(isGrid);
		showPreferences();
	}

	/**
	 * Shows the dialog box
	 *  
	 */
	public void showPreferences() {
		setLocationRelativeTo(UnitexFrame.mainFrame);
		this.setVisible(true);
	}

	private JPanel constructPanel() {
		Action topAction = new AbstractAction("Top") {
			public void actionPerformed(ActionEvent arg0) {
				GraphAlignmentMenu.pref.setVisible(false);
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f == null)
					return;
				f.graphicalZone.HTopAlign();
				f.setModified(true);
			}
		};
		JButton top = new JButton(topAction);
		Action centerHAction = new AbstractAction("Center") {
			public void actionPerformed(ActionEvent arg0) {
				GraphAlignmentMenu.pref.setVisible(false);
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f == null)
					return;
				f.graphicalZone.HCenterAlign();
				f.setModified(true);
			}
		};
		JButton centerH = new JButton(centerHAction);
		Action bottomAction = new AbstractAction("Bottom") {
			public void actionPerformed(ActionEvent arg0) {
				GraphAlignmentMenu.pref.setVisible(false);
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f == null)
					return;
				f.graphicalZone.HBottomAlign();
				f.setModified(true);
			}
		};
		JButton bottom = new JButton(bottomAction);
		Action leftAction = new AbstractAction("Left") {
			public void actionPerformed(ActionEvent arg0) {
				GraphAlignmentMenu.pref.setVisible(false);
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f == null)
					return;
				f.graphicalZone.VLeftAlign();
				f.setModified(true);
			}
		};
		JButton left = new JButton(leftAction);
		Action centerVAction = new AbstractAction("Center") {
			public void actionPerformed(ActionEvent arg0) {
				GraphAlignmentMenu.pref.setVisible(false);
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f == null)
					return;
				f.graphicalZone.VCenterAlign();
				f.setModified(true);
			}
		};
		JButton centerV = new JButton(centerVAction);
		Action rightAction = new AbstractAction("Right") {
			public void actionPerformed(ActionEvent arg0) {
				GraphAlignmentMenu.pref.setVisible(false);
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f == null)
					return;
				f.graphicalZone.VRightAlign();
				f.setModified(true);
			}
		};
		JButton right = new JButton(rightAction);
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		Action okAction = new AbstractAction("OK") {
			public void actionPerformed(ActionEvent arg0) {
				int n;
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f == null)
					return;
				JCheckBox c = GraphAlignmentMenu.pref.checkBox;
				if (!c.isSelected()) {
					f.setModified(true);
					f.graphicalZone.setGrid(false);
					GraphAlignmentMenu.pref.setVisible(false);
					return;
				}
				if (GraphAlignmentMenu.pref.nPixels.getText().equals("")) {
					return;
				}
				n = Util.toInt(GraphAlignmentMenu.pref.nPixels.getText());
				if (n < 10) {
					GraphAlignmentMenu.pref.nPixels.setText("10");
					GraphAlignmentMenu.nPix = 10;
					return;
				}
				GraphAlignmentMenu.nPix = n;
				f.graphicalZone.setGrid(true, n);
				f.setModified(true);
				GraphAlignmentMenu.pref.setVisible(false);
			}
		};
		JButton OK = new JButton(okAction);
		Action cancelAction = new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent arg0) {
				GraphAlignmentMenu.pref.setVisible(false);
			}
		};
		JButton CANCEL = new JButton(cancelAction);
		JPanel horizontal = new JPanel(new GridLayout(3, 1));
		horizontal.setBorder(new TitledBorder("Horizontal"));
		horizontal.add(top);
		horizontal.add(centerH);
		horizontal.add(bottom);
		JPanel vertical = new JPanel(new GridLayout(3, 1));
		vertical.setBorder(new TitledBorder("Vertical"));
		JPanel leftPanel = new JPanel(new GridLayout(1, 2));
		leftPanel.add(new JPanel());
		leftPanel.add(left);
		vertical.add(leftPanel);
		JPanel centerPane = new JPanel(vertGridBag);
		vertConstraints.fill = GridBagConstraints.NONE;
		vertGridBag.setConstraints(centerV, vertConstraints);
		centerPane.add(centerV);
		vertical.add(centerPane);
		JPanel rightPanel = new JPanel(new GridLayout(1, 2));
		rightPanel.add(right);
		rightPanel.add(new JPanel());
		vertical.add(rightPanel);
		JPanel centerPanel = new JPanel();
		centerPanel.add(horizontal);
		centerPanel.add(vertical);
		JPanel southPanel = new JPanel();
		checkBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                JTextField t = GraphAlignmentMenu.pref.nPixels;
                if (GraphAlignmentMenu.pref.checkBox.isSelected())
                    t.setEditable(true);
                else
                    t.setEditable(false);
            }});
		southPanel.add(checkBox);
		southPanel.add(new JLabel("Use Grid, every"));
		southPanel.add(nPixels);
		southPanel.add(new JLabel("pixels"));
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(OK);
		buttonPanel.add(CANCEL);
		panel.add(centerPanel, BorderLayout.NORTH);
		panel.add(southPanel, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		return panel;
	}

}
