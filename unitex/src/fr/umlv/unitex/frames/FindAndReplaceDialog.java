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

import fr.umlv.unitex.findandreplace.FindAndReplace;
import fr.umlv.unitex.findandreplace.FindAndReplaceData;
import fr.umlv.unitex.graphrendering.GraphBox;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * This class defines a dialog that allow the user to search and replace the content of one or more boxes
 * in the current graph
 *
 * @author Maxime Petit
 */
public class FindAndReplaceDialog extends JDialog {
    private JButton quitButton;
    private JButton replaceButton;
    private JButton replaceAllButton;
    private JTextField findTextField;
    private JTextField replaceTextField;
    private JButton nextButton;
    private JButton prevButton;
    private JRadioButton regexRadioButton;
    private final FindAndReplaceData data;

    public FindAndReplaceDialog(FindAndReplaceData data) {
        super(UnitexFrame.mainFrame, "Find and replace", true);
        setResizable(false);
        setContentPane(constructPanel());
        pack();
        setLocationRelativeTo(UnitexFrame.mainFrame);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.data = data;
        createListeners();
    }

    private void onQuit() {
        dispose();
    }

    private void onNext() {
        int i = 0;
        data.getGraphicalZone().unSelectAllBoxes();
        if (isValidFindTextField()) {
            if(regexRadioButton.isSelected()) {
                while (!FindAndReplace.findRegex(data.nextBox(), findTextField.getText()) && i < data.getBoxes().size()) {
                    i++;
                }
            } else {
                while (!FindAndReplace.find(data.nextBox(), findTextField.getText()) && i < data.getBoxes().size()) {
                    i++;
                }
            }
            if (isValidTextField() && data.getCurrentBox().isSelected()) {
                replaceButton.setEnabled(true);
            } else {
                replaceButton.setEnabled(false);
            }
        }
    }

    private void onPrev() {
        int i = 0;
        data.getGraphicalZone().unSelectAllBoxes();
        if (isValidFindTextField()) {
            if(regexRadioButton.isSelected()) {
                while (!FindAndReplace.findRegex(data.prevBox(), findTextField.getText()) && i < data.getBoxes().size()) {
                    i++;
                }
            } else {
                while (!FindAndReplace.find(data.prevBox(), findTextField.getText()) && i < data.getBoxes().size()) {
                    i++;
                }
            }
            if (isValidTextField() && data.getCurrentBox().isSelected()) {
                replaceButton.setEnabled(true);
            } else {
                replaceButton.setEnabled(false);
            }
        }
    }

    private void onReplace() {
        data.getGraphicalZone().unSelectAllBoxes();
        if (isValidTextField() && data.getCurrentBox().getType() == GraphBox.NORMAL && !data.getCurrentBox().isStandaloneBox()) {
            if (regexRadioButton.isSelected()) {
                FindAndReplace.replaceRegex(data.getCurrentBox(), findTextField.getText(), replaceTextField.getText(), data.getGraphicalZone());
            } else {
                FindAndReplace.replace(data.getCurrentBox(), findTextField.getText(), replaceTextField.getText(), data.getGraphicalZone());
            }
        }
    }

    private boolean isValidTextField() {
        return isValidFindTextField() && isValidReplaceTextField();
    }

    private boolean isValidReplaceTextField() {
        return replaceTextField.getText() != null && !replaceTextField.getText().equals("");
    }

    private boolean isValidFindTextField() {
        return findTextField.getText() != null && !findTextField.getText().equals("");
    }

    private void onReplaceAll() {
        if (isValidTextField()) {
            int i = 0;
            if (regexRadioButton.isSelected()) {
                i = FindAndReplace.replaceAllRegex(data.getBoxes(), findTextField.getText(), replaceTextField.getText(), data.getGraphicalZone());
            } else {
                i = FindAndReplace.replaceAll(data.getBoxes(), findTextField.getText(), replaceTextField.getText(), data.getGraphicalZone());
            }
            JOptionPane.showMessageDialog(null, "Replaced occurrence(s): "+i);
        }
    }

    private void createListeners() {
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onQuit();
            }
        });
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onNext();
                getParent().repaint();
            }
        });
        prevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onPrev();
                getParent().repaint();
            }
        });
        replaceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onReplace();
                getParent().repaint();
            }
        });
        replaceAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onReplaceAll();
                repaint();
                getParent().repaint();
            }
        });
        replaceTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                enableReplaceButtons();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                enableReplaceButtons();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                enableReplaceButtons();
            }
        });

        findTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                enableReplaceButtons();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                enableReplaceButtons();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                enableReplaceButtons();
            }
        });
    }

    private void enableReplaceButtons() {
        replaceAllButton.setEnabled(canEnableReplaceAllButton());
        replaceButton.setEnabled(canEnableReplaceButton());
    }

    private boolean canEnableReplaceButton() {
        return isValidTextField() && data.getCurrentBox().isSelected();
    }

    private boolean canEnableReplaceAllButton() {
        return isValidTextField();
    }

    private JPanel constructPanel() {
        final JPanel contentPane = new JPanel();
        contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        contentPane.setMinimumSize(new Dimension(408, 150));
        contentPane.setPreferredSize(new Dimension(408, 150));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setMinimumSize(new Dimension(400, 145));
        panel1.setPreferredSize(new Dimension(400, 145));
        contentPane.add(panel1);
        constructTextField(panel1);
        constructButton(panel1);
        constructRadioButton(panel1);
        return contentPane;
    }

    private void constructRadioButton(JPanel panel1) {
        GridBagConstraints gbc;
        JRadioButton normalRadioButton = new JRadioButton();
        normalRadioButton.setSelected(true);
        normalRadioButton.setText("Normal");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(normalRadioButton, gbc);
        regexRadioButton = new JRadioButton();
        regexRadioButton.setText("Regex");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(regexRadioButton, gbc);
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(regexRadioButton);
        buttonGroup.add(normalRadioButton);
    }

    private void constructButton(JPanel panel1) {
        GridBagConstraints gbc;
        nextButton = new JButton();
        nextButton.setText("Next");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 2, 5);
        panel1.add(nextButton, gbc);
        prevButton = new JButton();
        prevButton.setText("Prev");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 2, 5);
        panel1.add(prevButton, gbc);
        replaceButton = new JButton();
        replaceButton.setText("Replace");
        replaceButton.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 5);
        panel1.add(replaceButton, gbc);
        replaceAllButton = new JButton();
        replaceAllButton.setText("Replace All");
        replaceAllButton.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 5);
        panel1.add(replaceAllButton, gbc);
        quitButton = new JButton();
        quitButton.setText("Quit");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 5);
        panel1.add(quitButton, gbc);
    }

    private void constructTextField(JPanel panel1) {
        findTextField = new JTextField();
        findTextField.setEditable(true);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 20);
        panel1.add(findTextField, gbc);
        replaceTextField = new JTextField();
        replaceTextField.setEditable(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 20);
        panel1.add(replaceTextField, gbc);
        JLabel findLabel = new JLabel();
        findLabel.setText("Find:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(findLabel, gbc);
        JLabel replaceLabel = new JLabel();
        replaceLabel.setText("Replace:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(replaceLabel, gbc);
    }

}
