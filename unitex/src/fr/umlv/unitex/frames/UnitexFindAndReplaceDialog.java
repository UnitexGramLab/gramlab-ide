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

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.findandreplace.FindAndReplace;
import fr.umlv.unitex.findandreplace.FindAndReplaceData;
import fr.umlv.unitex.graphrendering.GraphBox;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

/**
 * This class defines a dialog that allow the user to search and replace the content of one or more boxes
 * in one or more graph.
 *
 * @author Maxime Petit
 */
public class UnitexFindAndReplaceDialog extends JDialog {

    private JButton quitButton;
    private JButton replaceButton;
    private JButton replaceAllButton;
    private JTextField findTextField;
    private JTextField replaceTextField;
    private JButton nextButton;
    private JButton prevButton;
    private JButton nextGraphButton;
    private JButton prevGraphButton;
    private JRadioButton regexRadioButton;
    private JRadioButton singleGraphRadioButton;
    private final ArrayList<GraphFrame> graphFrames;
    private FindAndReplaceData data;
    private GraphFrame currentFrame;

    private UnitexFindAndReplaceDialog(ArrayList<GraphFrame> graphFrames, GraphFrame currentFrame, FindAndReplaceData data) {
        super(UnitexFrame.mainFrame, "Find and replace", true);
        this.graphFrames = graphFrames;
        this.currentFrame = currentFrame;
        this.data = data;
        setResizable(false);
        setContentPane(constructPanel());
        pack();
        setLocationRelativeTo(UnitexFrame.mainFrame);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        createListeners();
    }

    public static UnitexFindAndReplaceDialog createUnitexFindAndReplaceDialog() {
        ArrayList<GraphFrame> graphFrames = GlobalProjectManager.search(null)
                .getFrameManagerAs(InternalFrameManager.class)
                .getGraphFrames();
        if (graphFrames.isEmpty()) {
            throw new IllegalStateException();
        }
        GraphFrame currentFrame = graphFrames.get(0);
        for (GraphFrame graphFrame : graphFrames) {
            if (graphFrame.isSelected()) {
                currentFrame = graphFrame;
            }
        }
        FindAndReplaceData data = new FindAndReplaceData(currentFrame.getGraphicalZone().getBoxes(), currentFrame.getGraphicalZone());
        return new UnitexFindAndReplaceDialog(graphFrames, currentFrame, data);
    }


    private JPanel constructPanel() {
        final JPanel contentPane = new JPanel();
        contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        contentPane.setMinimumSize(new Dimension(458, 150));
        contentPane.setPreferredSize(new Dimension(458, 150));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setMinimumSize(new Dimension(450, 145));
        panel1.setPreferredSize(new Dimension(450, 145));
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

        JRadioButton allGraphRadioButton = new JRadioButton();
        allGraphRadioButton.setText("All graphs");
        allGraphRadioButton.setSelected(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(allGraphRadioButton, gbc);

        singleGraphRadioButton = new JRadioButton();
        singleGraphRadioButton.setText("Current graph");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        panel1.add(singleGraphRadioButton, gbc);
        ButtonGroup graphButtonGroup = new ButtonGroup();
        graphButtonGroup.add(singleGraphRadioButton);
        graphButtonGroup.add(allGraphRadioButton);
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
        replaceButton.setEnabled(false);
        replaceButton.setText("Replace");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 5);
        panel1.add(replaceButton, gbc);
        replaceAllButton = new JButton();
        replaceAllButton.setEnabled(false);
        replaceAllButton.setText("Replace All");
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
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 5);
        panel1.add(quitButton, gbc);
        nextGraphButton = new JButton();
        nextGraphButton.setText("Next graph");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 2, 5);
        panel1.add(nextGraphButton, gbc);
        prevGraphButton = new JButton();
        prevGraphButton.setText("Prev graph");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 2, 5);
        panel1.add(prevGraphButton, gbc);
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

    private void createListeners() {
        replaceButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                onReplace();
                getParent().repaint();
            }
        });
        replaceAllButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                onReplaceAll();
                getParent().repaint();
            }
        });
        quitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                onQuit();
            }
        });
        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                onNext();
                getParent().repaint();
            }
        });
        prevButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                onPrev();
                getParent().repaint();
            }
        });
        nextGraphButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                onNextGraph();
                getParent().repaint();
            }
        });
        prevGraphButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                onPrevGraph();
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

    private void onReplaceAll() {
        if (isValidTextField()) {
            int i = 0;
            if (regexRadioButton.isSelected()) {
                if(singleGraphRadioButton.isSelected()) {
                    i = FindAndReplace.replaceAllRegex(currentFrame.getGraphicalZone().getBoxes(), findTextField.getText(), replaceTextField.getText(), currentFrame.getGraphicalZone());
                } else {
                    for (GraphFrame graphFrame : graphFrames) {
                        i += FindAndReplace.replaceAllRegex(graphFrame.getGraphicalZone().getBoxes(), findTextField.getText(), replaceTextField.getText(), graphFrame.getGraphicalZone());
                    }
                }
            } else {
                if(singleGraphRadioButton.isSelected()) {
                    i = FindAndReplace.replaceAll(currentFrame.getGraphicalZone().getBoxes(), findTextField.getText(), replaceTextField.getText(), currentFrame.getGraphicalZone());
                } else {
                    for (GraphFrame graphFrame : graphFrames) {
                        i += FindAndReplace.replaceAll(graphFrame.getGraphicalZone().getBoxes(), findTextField.getText(), replaceTextField.getText(), graphFrame.getGraphicalZone());
                    }
                }
            }
            JOptionPane.showMessageDialog(null, i+" occurrences were replaced");
        }
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

    private void onNextGraph() {
        try {
            int index = graphFrames.indexOf(currentFrame);
            int nextIndex = floorMod(index + 1, graphFrames.size());
            graphFrames.get(index).setSelected(false);
            if(graphFrames.get(nextIndex).isIcon()) {
                graphFrames.get(nextIndex).setIcon(false);
            }
            graphFrames.get(nextIndex).setSelected(true);
            currentFrame = graphFrames.get(nextIndex);
            data = new FindAndReplaceData(currentFrame.getGraphicalZone().getBoxes(), currentFrame.getGraphicalZone());
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        getParent().repaint();
    }

    private void onPrevGraph() {
        try {
            int index = graphFrames.indexOf(currentFrame);
            int prevIndex = floorMod(index - 1, graphFrames.size());
            graphFrames.get(index).setSelected(false);
            if(graphFrames.get(prevIndex).isIcon()) {
                graphFrames.get(prevIndex).setIcon(false);
            }
            graphFrames.get(prevIndex).setSelected(true);
            currentFrame = graphFrames.get(prevIndex);
            data = new FindAndReplaceData(currentFrame.getGraphicalZone().getBoxes(), currentFrame.getGraphicalZone());
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        getParent().repaint();
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

    private void onQuit() {
        dispose();
    }

    private void enableReplaceButtons() {
        replaceAllButton.setEnabled(canEnableReplaceAllButton());
        replaceButton.setEnabled(canEnableReplaceButton());
        repaint();
    }

    private boolean canEnableReplaceButton() {
        return isValidTextField() && data.getCurrentBox().isSelected();
    }

    private boolean canEnableReplaceAllButton() {
        return isValidTextField();
    }

    private int floorMod(int x, int y) {
        return x - floorDiv(x, y) * y;
    }

    private int floorDiv(int x, int y) {
        int r = x / y;
        // if the signs are different and modulo not zero, round down
        if ((x ^ y) < 0 && (r * y != x)) {
            r--;
        }
        return r;
    }
}
