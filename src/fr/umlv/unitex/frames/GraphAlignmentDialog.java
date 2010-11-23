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

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * This class describes a dialog box that allows the user to align the current
 * selected boxes in the the current graph. The user can also define if a grid
 * must be drawn on backend, and eventually the size of the grid's cells.
 *
 * @author Sébastien Paumier
 */
public class GraphAlignmentDialog extends JDialog {

    GridBagLayout horizGridBag = new GridBagLayout();
    private final GridBagLayout vertGridBag = new GridBagLayout();
    private final GridBagConstraints vertConstraints = new GridBagConstraints();
    private final JCheckBox showGrid = new JCheckBox();
    private final JTextField nPixels = new JTextField("30");

    private int nPix = 30;

    private GraphFrame f;

    /**
     * Constructs a new <code>GraphAlignmentMenu</code>.
     *
     * @param isGrid indicates if there is a grid in background
     * @param nPix   size of the grid's cells
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
        Action topAction = new AbstractAction("Top") {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
                f.HTopAlign();
            }
        };
        JButton top = new JButton(topAction);
        Action centerHAction = new AbstractAction("Center") {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
                f.HCenterAlign();
            }
        };
        JButton centerH = new JButton(centerHAction);
        Action bottomAction = new AbstractAction("Bottom") {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
                f.HBottomAlign();
            }
        };
        JButton bottom = new JButton(bottomAction);
        Action leftAction = new AbstractAction("Left") {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
                f.VLeftAlign();
            }
        };
        JButton left = new JButton(leftAction);
        Action centerVAction = new AbstractAction("Center") {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
                f.VCenterAlign();
            }
        };
        JButton centerV = new JButton(centerVAction);
        Action rightAction = new AbstractAction("Right") {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
                f.VRightAlign();
            }
        };
        JButton right = new JButton(rightAction);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        Action okAction = new AbstractAction("OK") {
            public void actionPerformed(ActionEvent arg0) {
                if (!showGrid.isSelected()) {
                    setVisible(false);
                    f.setGrid(false, -1);
                    return;
                }
                int n;
                try {
                    n = Integer.parseInt(nPixels.getText());
                } catch (NumberFormatException e) {
                    return;
                }
                if (n < 10) {
                    n = 10;
                }
                setVisible(false);
                f.setGrid(true, n);
            }
        };
        JButton OK = new JButton(okAction);
        Action cancelAction = new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
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
        showGrid.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                nPixels.setEditable(showGrid.isSelected());
            }
        });
        southPanel.add(showGrid);
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
