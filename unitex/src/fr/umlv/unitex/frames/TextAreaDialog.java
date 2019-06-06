/*
 * Unitex
 *
 * Copyright (C) 2001-2019 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class defines a dialog that display a non editable JTextField.
 *
 * @author Maxime Petit
 */
public class TextAreaDialog extends JDialog {
    private final String text;
    private JButton okButton;

    public TextAreaDialog(String title, String text) {
        super(UnitexFrame.mainFrame, title, true);
        this.text = text;
        setResizable(false);
        setContentPane(constructPanel());
        pack();
        setLocationRelativeTo(UnitexFrame.mainFrame);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    private JPanel constructPanel() {
        JPanel middlePanel = new JPanel();
        JTextArea display = new JTextArea(16, 58);
        display.setEditable(false);
        JScrollPane scroll = new JScrollPane(display);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        display.setText(text);
        middlePanel.add(scroll);
        JPanel bottomPanel = new JPanel();
        okButton = new JButton();
        okButton.setText("OK");
        createListeners();
        bottomPanel.add(okButton);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(middlePanel);
        panel.add(bottomPanel);
        return panel;
    }

    private void createListeners() {
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

}
