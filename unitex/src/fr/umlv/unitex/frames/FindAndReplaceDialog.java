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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.graphrendering.GenericGraphBox;
import fr.umlv.unitex.graphrendering.GraphBox;
import fr.umlv.unitex.graphtools.FindAndReplace;
import fr.umlv.unitex.graphtools.FindAndReplaceData;

/**
 * This class defines a dialog that allow the user to search and replace the content of one or more boxes
 * in the open graphs.
 *
 * @author Maxime Petit
 */
public class FindAndReplaceDialog extends JDialog implements MultiInstanceFrameFactoryObserver<GraphFrame> {
  private final String separator = "▶";
  private JTextField findTextField;
  private JTextField replaceTextField;
  private JComboBox graphComboBox;
  private JCheckBox useRegularExpressionsCheckBox;
  private JCheckBox caseSensitiveCheckBox;
  private JCheckBox matchOnlyAWholeCheckBox;
  private JCheckBox ignoreCommentBoxesCheckBox;
  private JButton closeButton;
  private JButton findNextButton;
  private JButton findPreviousButton;
  private JButton replaceAllButton;
  private JButton replaceButton;
  private JTextField statusBarTextField;
  private GraphFrame currentFrame;
  private FindAndReplaceData data;
  private ArrayList<GraphFrame> graphFrames;
  private final String graphDefaultText = "All open graphs";

  private JTabbedPane tabbedPane;
  private JPanel box;
  private JRadioButton findOneBoxAfterAnotherRadioButton;
  private JRadioButton findAllBoxesAtOnceRadioButton;
  private JTextField findCompleteBoxesTextField;
  private JButton findAddButton;
  private JLabel findLabel;
  private JLabel replaceLabel;
  private JTextField replaceCompleteBoxesTextField;
  private JRadioButton replaceOneBoxAfterAnotherRadioButton;
  private JRadioButton replaceAllBoxesAtOnceRadioButton;
  private JButton replaceAddButton;
  private JComboBox graphCompleteBoxesComboBox;
  private JCheckBox caseSensitiveSCheckBox;
  private JCheckBox useRegularExpressionsSCheckBox;
  private JPanel mainPanel;
  private JPanel completeBoxes;
  private JPanel statusPanel;
  private JPanel findWhatPanel;
  private JPanel replaceWithPanel;
  private JPanel bottomPanel;
  private JButton findCompleteBoxesNext;
  private JButton replaceCompleteBoxesButton;
  private JButton closeCompleteBoxesButton;
  private JButton findCompleteBoxesPrevious;
  private JButton replaceAllCompleteBoxesButton;

  private ArrayList<String> findSeqList = new ArrayList<String>();
  private ArrayList<String> replaceSeqList = new ArrayList<String>();
  private ArrayList<GenericGraphBox> currentSeq = new ArrayList<GenericGraphBox>();

  private class defaultGraphFrame extends GraphFrame {

    public defaultGraphFrame() {
      super(null);
    }

    @Override
    public String toString() {
      return graphDefaultText;
    }
  }

  private FindAndReplaceDialog(ArrayList<GraphFrame> graphFrames, GraphFrame currentFrame, FindAndReplaceData data) {
    super(UnitexFrame.mainFrame, "Find and replace", true);
    this.graphFrames = graphFrames;
    this.currentFrame = currentFrame;
    this.data = data;
    setResizable(false);
    setContentPane(constructPanel());
    setModalityType(ModalityType.MODELESS);
    pack();
    setLocationRelativeTo(UnitexFrame.mainFrame);
    setDefaultCloseOperation(HIDE_ON_CLOSE);
    createListeners();
  }

  public static FindAndReplaceDialog createFindAndReplaceDialog() {
    ArrayList<GraphFrame> graphFrames = GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager
      .class).getGraphFrames();
    GraphFrame currentFrame = GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
      .getCurrentFocusedGraphFrame();
    if (currentFrame == null) {
      currentFrame = graphFrames.get(0);
    }
    FindAndReplaceData data = new FindAndReplaceData(currentFrame.getGraphicalZone().getBoxes(), currentFrame
      .getGraphicalZone());
    return new FindAndReplaceDialog(graphFrames, currentFrame, data);
  }

  private void createListeners() {
    closeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onClose();
      }
    });
    closeCompleteBoxesButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onClose();
      }
    });
    graphCompleteBoxesComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
      }
    });
    graphComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!graphComboBox.getSelectedItem().toString().equals(graphDefaultText)) {
          GraphFrame f = (GraphFrame) graphComboBox.getSelectedItem();
          try {
            f.setIcon(false);
            f.setSelected(true);
            currentFrame = f;
            data = new FindAndReplaceData(currentFrame.getGraphicalZone().getBoxes(), currentFrame.getGraphicalZone());
          } catch (PropertyVetoException exception) {
            exception.printStackTrace();
          }
        }
        updateTextField();
      }
    });
    replaceAllButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onReplaceAll();
        getParent().repaint();
      }
    });
    replaceButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onReplace();
        getParent().repaint();
      }
    });
    findNextButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onNext();
        getParent().repaint();
      }
    });
    findPreviousButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onPrev();
        getParent().repaint();
      }
    });
    findTextField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateTextField();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateTextField();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        // Do nothing
      }
    });
    replaceTextField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateTextField();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateTextField();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        // Do nothing
      }
    });
    useRegularExpressionsCheckBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateTextField();
      }
    });
    caseSensitiveCheckBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateTextField();
      }
    });
    matchOnlyAWholeCheckBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateTextField();
      }
    });
    ignoreCommentBoxesCheckBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateTextField();
      }
    });
    tabbedPane.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        if (tabbedPane.getSelectedIndex() == 1) {
          updateBoxesTab();
          statusBarTextField.setText("");
          completeBoxes.revalidate();
          tabbedPane.revalidate();
          pack();

        } else {
          statusBarTextField.setText("");
          completeBoxes.removeAll();
          completeBoxes.revalidate();
          tabbedPane.revalidate();
          pack();

        }
      }
    });
    findAddButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onAddFind();
      }
    });
    replaceAddButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onAddReplace();
      }
    });
    findOneBoxAfterAnotherRadioButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        findLabel.setText("Select a graph, then the first box, then click Add, etc.");
      }
    });
    findAllBoxesAtOnceRadioButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        findLabel.setText("Select a graph, then the sequence then click Add.");
      }
    });
    replaceOneBoxAfterAnotherRadioButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        replaceLabel.setText("Select a graph, then the first box, then click Add, etc.");
      }
    });
    replaceAllBoxesAtOnceRadioButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        replaceLabel.setText("Select a graph, then the sequence then click Add.");
      }
    });
    findCompleteBoxesNext.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onCompleteBoxesNext();
        getParent().repaint();
      }
    });
    findCompleteBoxesPrevious.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onCompleteBoxesPrevious();
        getParent().repaint();
      }
    });
    replaceCompleteBoxesButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onReplaceCompleteBoxes();
        getParent().repaint();
      }
    });
    replaceAllCompleteBoxesButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onReplaceAllS();
        getParent().repaint();
      }
    });
    findCompleteBoxesTextField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateTextField();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateTextField();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {

      }
    });
    replaceCompleteBoxesTextField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        updateTextField();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateTextField();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {

      }
    });

  }

  private void onReplaceAllS() {
    String tokens[] = findCompleteBoxesTextField.getText().split(separator);
    findSeqList.clear();
    data.getGraphicalZone().unSelectAllBoxes();
    Collections.addAll(findSeqList, tokens);
    if (findSeqList.isEmpty()) {
      return;
    }
    if (replaceCompleteBoxesTextField.getText().isEmpty()) {
      replaceCompleteBoxesTextField.setText("<E>");
    }
    tokens = replaceCompleteBoxesTextField.getText().split(separator);
    replaceSeqList.clear();
    Collections.addAll(replaceSeqList, tokens);

    int res = 0;
    if (!graphComboBox.getSelectedItem().toString().equals(graphDefaultText)) {
      res = FindAndReplace.replaceAllSeq(data.getGraphicalZone(), findSeqList, replaceSeqList, caseSensitiveSCheckBox
        .isSelected(), useRegularExpressionsSCheckBox.isSelected());
    } else {
      for (GraphFrame f : graphFrames) {
        res += FindAndReplace.replaceAllSeq(f.getGraphicalZone(), findSeqList, replaceSeqList, caseSensitiveSCheckBox
          .isSelected(), useRegularExpressionsSCheckBox.isSelected());
      }
    }
    updateSeqReplaceResultTextField(res);
  }

  private void onCompleteBoxesNext() {
    if (findCompleteBoxesTextField.getText().equals("")) {
      return;
    }
    String tokens[] = findCompleteBoxesTextField.getText().split(separator);
    findSeqList.clear();
    data.getGraphicalZone().unSelectAllBoxes();
    data.getGraphicalZone().removeHighlight();
    Collections.addAll(findSeqList, tokens);
    if (findSeqList.isEmpty()) {
      return;
    }
    if(updateSeqFoundResultTextField() == 0) {
      return;
    }
    int i = 0;
    GenericGraphBox nextBox = data.nextBox();
    if (changeNextGraph(nextBox)) {
      return;
    }
    while (i < data.getBoxes().size() && !FindAndReplace.isSeq(data.getGraphicalZone(), findSeqList, nextBox,
      true, caseSensitiveSCheckBox.isSelected(), useRegularExpressionsSCheckBox.isSelected())) {
      i++;
      nextBox = data.nextBox();
      if (changeNextGraph(nextBox)) {
        return;
      }
    }
    currentSeq.clear();
    for (i = 0; i < data.getGraphicalZone().getSelectedBoxes().size(); i++) {
      currentSeq.add(data.getGraphicalZone().getSelectedBoxes().get(i));
    }
  }

  private boolean changeNextGraph(GenericGraphBox nextBox) {
    if (nextBox == null) {
      if (!graphComboBox.getSelectedItem().toString().equals(graphDefaultText)) {
        selectGraph(currentFrame);
      } else {
        selectGraph(nextGraph());
      }
      onCompleteBoxesNext();
      return true;
    }
    return false;
  }

  private void onCompleteBoxesPrevious() {
    if (findCompleteBoxesTextField.getText().equals("")) {
      return;
    }
    String tokens[] = findCompleteBoxesTextField.getText().split(separator);
    findSeqList.clear();
    data.getGraphicalZone().unSelectAllBoxes();
    data.getGraphicalZone().removeHighlight();
    Collections.addAll(findSeqList, tokens);
    if (findSeqList.isEmpty()) {
      return;
    }
    if(updateSeqFoundResultTextField() == 0) {
      return;
    }
    int i = 0;
    GenericGraphBox prevBox = data.prevBox();
    if (changePrevGraph(prevBox)) {
      return;
    }
    while (i < data.getBoxes().size() && !FindAndReplace.isSeq(data.getGraphicalZone(), findSeqList, prevBox,
      true, caseSensitiveSCheckBox.isSelected(), useRegularExpressionsSCheckBox.isSelected())) {
      i++;
      prevBox = data.prevBox();
      if (changePrevGraph(prevBox)) {
        return;
      }
    }
    currentSeq.clear();
    for (i = 0; i < data.getGraphicalZone().getSelectedBoxes().size(); i++) {
      currentSeq.add(data.getGraphicalZone().getSelectedBoxes().get(i));
    }
  }

  private boolean changePrevGraph(GenericGraphBox prevBox) {
    if (prevBox == null) {
      if (!graphComboBox.getSelectedItem().toString().equals(graphDefaultText)) {
        selectGraph(currentFrame);
      } else {
        selectGraph(prevGraph());
      }
      onCompleteBoxesPrevious();
      return true;
    }
    return false;
  }

  private void onReplaceCompleteBoxes() {
    if (currentSeq.isEmpty()) {
      updateSeqReplaceResultTextField(-1);
      return;
    }
    if (replaceCompleteBoxesTextField.getText().isEmpty()) {
      replaceCompleteBoxesTextField.setText("<E>");
    }
    String tokens[] = replaceCompleteBoxesTextField.getText().split(separator);
    replaceSeqList.clear();
    Collections.addAll(replaceSeqList, tokens);
    boolean wasReplaced = FindAndReplace.replaceSeq(data.getGraphicalZone(), replaceSeqList, currentSeq);
    if (wasReplaced) {
      updateSeqReplaceResultTextField(1);
    } else {
      data.getGraphicalZone().setHighlight(false);
      updateSeqReplaceResultTextField(-1);
    }
  }

  private void updateTextField() {
    if (tabbedPane.getSelectedIndex() == 0) {
      updateSingleReplaceErrorTextField();
    } else {
      updateSeqReplaceErrorTextField();
    }
  }

  private void updateSeqReplaceErrorTextField() {
    String tokens[] = replaceCompleteBoxesTextField.getText().split(separator);
    replaceSeqList.clear();
    data.getGraphicalZone().unSelectAllBoxes();
    Collections.addAll(replaceSeqList, tokens);
    if (replaceSeqList.isEmpty()) {
      return;
    }
    String msg = FindAndReplace.checkNewBoxContent(data.getGraphicalZone(), (GraphBox) data.getCurrentBox(),
      replaceSeqList);
    if (!msg.isEmpty()) {
      statusBarTextField.setText("Error one or more boxes won't be replaced: " + msg);
      statusBarTextField.setForeground(Color.red);
    } else {
      updateSeqFoundResultTextField();
    }
  }

  private void updateSingleReplaceErrorTextField() {
    if (replaceTextField.getText().equals("")) {
      updateSingleFoundResultTextField();
      return;
    }
    String msg = "";
    if (!graphComboBox.getSelectedItem().toString().equals(graphDefaultText)) {
      msg = FindAndReplace.checkReplaceAll(currentFrame.getGraphicalZone().getBoxes(), findTextField.getText(),
        replaceTextField.getText(), currentFrame.getGraphicalZone(), useRegularExpressionsCheckBox.isSelected(),
        caseSensitiveCheckBox.isSelected(), matchOnlyAWholeCheckBox.isSelected(), ignoreCommentBoxesCheckBox
          .isSelected());
    } else {
      for (GraphFrame f : graphFrames) {
        msg = FindAndReplace.checkReplaceAll(f.getGraphicalZone().getBoxes(), findTextField.getText(),
          replaceTextField.getText(), f.getGraphicalZone(), useRegularExpressionsCheckBox.isSelected(),
          caseSensitiveCheckBox.isSelected(), matchOnlyAWholeCheckBox.isSelected(), ignoreCommentBoxesCheckBox
            .isSelected());
      }
    }
    if (!msg.isEmpty()) {
      statusBarTextField.setText("Error one or more boxes won't be replaced: " + msg);
      statusBarTextField.setForeground(Color.red);
    } else {
      updateSingleFoundResultTextField();
    }
  }

  private void onAddReplace() {
    selectGraph(GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).getCurrentFocusedGraphFrame());
    if (data.getGraphicalZone().getSelectedBoxes().isEmpty()) {
      return;
    }
    if (replaceOneBoxAfterAnotherRadioButton.isSelected() && currentFrame.getSelectedBoxes().size() == 1) {
      if (replaceCompleteBoxesTextField.getText().equals("")) {
        replaceCompleteBoxesTextField.setText(currentFrame.getSelectedBoxes().get(0).getContent());
      } else {
        replaceCompleteBoxesTextField.setText(replaceCompleteBoxesTextField.getText() + separator + currentFrame.getSelectedBoxes().get(0)
          .getContent());
      }
    } else if (replaceAllBoxesAtOnceRadioButton.isSelected()) {
      fillTextFieldAllBoxAtOnce(replaceCompleteBoxesTextField);
    }
    currentFrame.getGraphicalZone().unSelectAllBoxes();
  }

  private void fillTextFieldAllBoxAtOnce(JTextField textField) {
    ArrayList<GenericGraphBox> boxes = new ArrayList<GenericGraphBox>();
    fillSeqList(boxes, currentFrame);
    for (int i = 0; i < boxes.size(); i++) {
      if (textField.getText().isEmpty()) {
        textField.setText(boxes.get(i).getContent());
      } else {
        textField.setText(textField.getText() + separator + boxes.get(i).getContent());
      }
    }
  }

  private void fillSeqList(ArrayList<GenericGraphBox> boxes, GraphFrame f) {
    if (f.getSelectedBoxes().isEmpty()) {
      return;
    }
    final ArrayList<GenericGraphBox> inputBoxes = new ArrayList<GenericGraphBox>();
    final ArrayList<GenericGraphBox> outputBoxes = new ArrayList<GenericGraphBox>();
    f.getGraphicalZone().computeInputOutputBoxes(f.getSelectedBoxes(), inputBoxes, outputBoxes);
    final boolean[] inputBox = new boolean[f.getSelectedBoxes().size()];
    final boolean[] outputBox = new boolean[f.getSelectedBoxes().size()];
    for (int i = 0; i < f.getSelectedBoxes().size(); i++) {
      final GenericGraphBox box = f.getSelectedBoxes().get(i);
      if (inputBoxes.contains(box))
        inputBox[i] = true;
      if (outputBoxes.contains(box))
        outputBox[i] = true;
    }
    int root = 0;
    for (int i = 0; i < f.getSelectedBoxes().size(); i++) {
      if (inputBox[i]) {
        root = i;
      }
    }
    boxes.add(f.getSelectedBoxes().remove(root));
    fillSeqList(boxes, f);
  }

  private void onAddFind() {
    selectGraph(GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).getCurrentFocusedGraphFrame());
    if (data.getGraphicalZone().getSelectedBoxes().isEmpty()) {
      return;
    }
    if (findOneBoxAfterAnotherRadioButton.isSelected() && data.getGraphicalZone().getSelectedBoxes().size() == 1) {
      if (findCompleteBoxesTextField.getText().equals("")) {
        findCompleteBoxesTextField.setText(data.getGraphicalZone().getSelectedBoxes().get(0).getContent());
      } else {
        findCompleteBoxesTextField.setText(findCompleteBoxesTextField.getText() + separator + data.getGraphicalZone().getSelectedBoxes().get(0)
          .getContent());
      }
    } else if (findAllBoxesAtOnceRadioButton.isSelected()) {
      fillTextFieldAllBoxAtOnce(findCompleteBoxesTextField);
    }
    currentFrame.getGraphicalZone().unSelectAllBoxes();
  }

  private JPanel constructPanel() {
    mainPanel = new JPanel();
    mainPanel.setLayout(new GridBagLayout());
    tabbedPane = new JTabbedPane();
    tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
    tabbedPane.setTabPlacement(SwingConstants.TOP);
    GridBagConstraints gbc;
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.BOTH;
    mainPanel.add(tabbedPane, gbc);
    box = new JPanel();
    box.setLayout(new GridBagLayout());
    box.setPreferredSize(new Dimension(480, 280));
    tabbedPane.addTab("Inside one box", box);
    final JLabel label1 = new JLabel();
    label1.setText("Find what:");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    box.add(label1, gbc);
    final JLabel label2 = new JLabel();
    label2.setText("Replace with:");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.WEST;
    box.add(label2, gbc);
    final JLabel label3 = new JLabel();
    label3.setText("Graphs:");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 5;
    gbc.anchor = GridBagConstraints.WEST;
    box.add(label3, gbc);
    graphComboBox = new JComboBox();
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 5;
    gbc.gridwidth = 3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    box.add(graphComboBox, gbc);
    caseSensitiveCheckBox = new JCheckBox();
    caseSensitiveCheckBox.setText("Case sensitive");
    caseSensitiveCheckBox.setSelected(true);
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 7;
    gbc.anchor = GridBagConstraints.WEST;
    box.add(caseSensitiveCheckBox, gbc);
    matchOnlyAWholeCheckBox = new JCheckBox();
    matchOnlyAWholeCheckBox.setText("Match only a whole line");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 8;
    gbc.anchor = GridBagConstraints.WEST;
    box.add(matchOnlyAWholeCheckBox, gbc);
    closeButton = new JButton();
    closeButton.setMaximumSize(new Dimension(30, 32));
    closeButton.setPreferredSize(new Dimension(90, 26));
    closeButton.setText("Close");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 12;
    gbc.anchor = GridBagConstraints.WEST;
    box.add(closeButton, gbc);
    final JPanel spacer1 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 10;
    gbc.fill = GridBagConstraints.VERTICAL;
    box.add(spacer1, gbc);
    findNextButton = new JButton();
    findNextButton.setText("Find Next");
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 10;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    box.add(findNextButton, gbc);
    findPreviousButton = new JButton();
    findPreviousButton.setText("Find Previous");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 10;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    box.add(findPreviousButton, gbc);
    replaceAllButton = new JButton();
    replaceAllButton.setText("Replace All");
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 12;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    box.add(replaceAllButton, gbc);
    replaceButton = new JButton();
    replaceButton.setText("Replace");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 12;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    box.add(replaceButton, gbc);
    final JPanel spacer2 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 6;
    gbc.fill = GridBagConstraints.VERTICAL;
    box.add(spacer2, gbc);
    final JPanel spacer3 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.VERTICAL;
    box.add(spacer3, gbc);
    final JPanel spacer4 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 4;
    gbc.fill = GridBagConstraints.VERTICAL;
    box.add(spacer4, gbc);
    final JPanel spacer5 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 11;
    gbc.fill = GridBagConstraints.VERTICAL;
    box.add(spacer5, gbc);
    final JPanel spacer6 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 12;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    box.add(spacer6, gbc);
    final JPanel spacer7 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 10;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    box.add(spacer7, gbc);
    final JPanel spacer8 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    box.add(spacer8, gbc);
    final JPanel spacer9 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 13;
    gbc.fill = GridBagConstraints.VERTICAL;
    box.add(spacer9, gbc);
    final JPanel spacer10 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.VERTICAL;
    box.add(spacer10, gbc);
    final JPanel spacer11 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 6;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    box.add(spacer11, gbc);
    ignoreCommentBoxesCheckBox = new JCheckBox();
    ignoreCommentBoxesCheckBox.setText("Ignore comment boxes");
    ignoreCommentBoxesCheckBox.setSelected(true);
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 8;
    gbc.anchor = GridBagConstraints.WEST;
    box.add(ignoreCommentBoxesCheckBox, gbc);
    useRegularExpressionsCheckBox = new JCheckBox();
    useRegularExpressionsCheckBox.setText("Use regular expressions");
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 7;
    gbc.anchor = GridBagConstraints.WEST;
    box.add(useRegularExpressionsCheckBox, gbc);
    final JPanel spacer12 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 9;
    gbc.fill = GridBagConstraints.VERTICAL;
    box.add(spacer12, gbc);
    findTextField = new JTextField("");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 1;
    gbc.gridwidth = 3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    box.add(findTextField, gbc);
    replaceTextField = new JTextField("");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 3;
    gbc.gridwidth = 3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    box.add(replaceTextField, gbc);
    completeBoxes = new JPanel();
    completeBoxes.setLayout(new GridBagLayout());
    tabbedPane.addTab("Complete boxes", completeBoxes);
    statusPanel = new JPanel();
    statusPanel.setLayout(new GridBagLayout());
    statusPanel.setPreferredSize(new Dimension(530, 24));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.BOTH;
    mainPanel.add(statusPanel, gbc);
    statusBarTextField = new JTextField("");
    statusBarTextField.setEditable(false);
    statusBarTextField.setFocusable(false);
    statusBarTextField.setPreferredSize(new Dimension(530, 24));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    statusPanel.add(statusBarTextField, gbc);
    initBoxesTab();
    fillComboBox();
    return mainPanel;
  }

  private void initBoxesTab() {
    GridBagConstraints gbc;
    findWhatPanel = new JPanel();
    findWhatPanel.setLayout(new GridBagLayout());
    findWhatPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)),
      "Find what:"));
    findOneBoxAfterAnotherRadioButton = new JRadioButton();
    findOneBoxAfterAnotherRadioButton.setSelected(true);
    findOneBoxAfterAnotherRadioButton.setText("One box after another");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    findWhatPanel.add(findOneBoxAfterAnotherRadioButton, gbc);
    findLabel = new JLabel();
    findLabel.setText("Select a graph, then the first box, then click Add, etc.");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 4;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 5, 0, 0);
    findWhatPanel.add(findLabel, gbc);
    findCompleteBoxesTextField = new JTextField("");
    findCompleteBoxesTextField.setPreferredSize(new Dimension(400, 24));
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    findWhatPanel.add(findCompleteBoxesTextField, gbc);
    findAddButton = new JButton();
    findAddButton.setText("Add");
    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 4;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    findWhatPanel.add(findAddButton, gbc);
    final JPanel spacer13 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.VERTICAL;
    findWhatPanel.add(spacer13, gbc);
    final JPanel spacer14 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 3;
    gbc.fill = GridBagConstraints.VERTICAL;
    findWhatPanel.add(spacer14, gbc);
    final JPanel spacer15 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 4;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    findWhatPanel.add(spacer15, gbc);
    final JPanel spacer16 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 5;
    gbc.fill = GridBagConstraints.VERTICAL;
    findWhatPanel.add(spacer16, gbc);
    final JPanel spacer17 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    findWhatPanel.add(spacer17, gbc);
    final JPanel spacer18 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    findWhatPanel.add(spacer18, gbc);
    findAllBoxesAtOnceRadioButton = new JRadioButton();
    findAllBoxesAtOnceRadioButton.setText("All boxes at once");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    findWhatPanel.add(findAllBoxesAtOnceRadioButton, gbc);
    replaceWithPanel = new JPanel();
    replaceWithPanel.setLayout(new GridBagLayout());
    replaceWithPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)),
      "Replace with:"));
    replaceOneBoxAfterAnotherRadioButton = new JRadioButton();
    replaceOneBoxAfterAnotherRadioButton.setSelected(true);
    replaceOneBoxAfterAnotherRadioButton.setText("One box after another");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    replaceWithPanel.add(replaceOneBoxAfterAnotherRadioButton, gbc);
    replaceCompleteBoxesTextField = new JTextField("");
    replaceCompleteBoxesTextField.setPreferredSize(new Dimension(400, 24));
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    replaceWithPanel.add(replaceCompleteBoxesTextField, gbc);
    replaceAddButton = new JButton();
    replaceAddButton.setText("Add");
    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 4;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    replaceWithPanel.add(replaceAddButton, gbc);
    final JPanel spacer20 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.VERTICAL;
    replaceWithPanel.add(spacer20, gbc);
    final JPanel spacer21 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 3;
    gbc.fill = GridBagConstraints.VERTICAL;
    replaceWithPanel.add(spacer21, gbc);
    final JPanel spacer22 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 4;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    replaceWithPanel.add(spacer22, gbc);
    final JPanel spacer23 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 5;
    gbc.fill = GridBagConstraints.VERTICAL;
    replaceWithPanel.add(spacer23, gbc);
    final JPanel spacer24 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    replaceWithPanel.add(spacer24, gbc);
    final JPanel spacer25 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    replaceWithPanel.add(spacer25, gbc);
    replaceAllBoxesAtOnceRadioButton = new JRadioButton();
    replaceAllBoxesAtOnceRadioButton.setText("All boxes at once");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    replaceWithPanel.add(replaceAllBoxesAtOnceRadioButton, gbc);
    replaceLabel = new JLabel();
    replaceLabel.setText("Select a graph, then the first box, then click Add, etc.");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 4;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 5, 0, 0);
    replaceWithPanel.add(replaceLabel, gbc);
    bottomPanel = new JPanel();
    bottomPanel.setLayout(new GridBagLayout());
    graphCompleteBoxesComboBox = new JComboBox();
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.gridwidth = 5;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    bottomPanel.add(graphCompleteBoxesComboBox, gbc);
    findCompleteBoxesNext = new JButton();
    findCompleteBoxesNext.setText("Find Next");
    gbc = new GridBagConstraints();
    gbc.gridx = 6;
    gbc.gridy = 5;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    bottomPanel.add(findCompleteBoxesNext, gbc);
    replaceAllCompleteBoxesButton = new JButton();
    replaceAllCompleteBoxesButton.setText("Replace All");
    gbc = new GridBagConstraints();
    gbc.gridx = 6;
    gbc.gridy = 7;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    bottomPanel.add(replaceAllCompleteBoxesButton, gbc);
    final JPanel spacer26 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.VERTICAL;
    bottomPanel.add(spacer26, gbc);
    final JPanel spacer27 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 6;
    gbc.gridy = 6;
    gbc.fill = GridBagConstraints.VERTICAL;
    bottomPanel.add(spacer27, gbc);
    final JPanel spacer28 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 7;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    bottomPanel.add(spacer28, gbc);
    final JPanel spacer29 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 5;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    bottomPanel.add(spacer29, gbc);
    final JPanel spacer30 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 6;
    gbc.gridy = 4;
    gbc.fill = GridBagConstraints.VERTICAL;
    bottomPanel.add(spacer30, gbc);
    useRegularExpressionsSCheckBox = new JCheckBox();
    useRegularExpressionsSCheckBox.setText("Use regular expressions");
    gbc = new GridBagConstraints();
    gbc.gridx = 6;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.WEST;
    bottomPanel.add(useRegularExpressionsSCheckBox, gbc);
    final JPanel spacer31 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 6;
    gbc.gridy = 8;
    gbc.fill = GridBagConstraints.VERTICAL;
    bottomPanel.add(spacer31, gbc);
    final JPanel spacer32 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 7;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    bottomPanel.add(spacer32, gbc);
    final JPanel spacer33 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 6;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.VERTICAL;
    bottomPanel.add(spacer33, gbc);
    final JLabel label4 = new JLabel();
    label4.setText("Graphs:");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    bottomPanel.add(label4, gbc);
    final JPanel spacer34 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    bottomPanel.add(spacer34, gbc);
    closeCompleteBoxesButton = new JButton();
    closeCompleteBoxesButton.setMaximumSize(new Dimension(30, 32));
    closeCompleteBoxesButton.setPreferredSize(new Dimension(90, 26));
    closeCompleteBoxesButton.setText("Close");
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 7;
    gbc.anchor = GridBagConstraints.WEST;
    bottomPanel.add(closeCompleteBoxesButton, gbc);
    caseSensitiveSCheckBox = new JCheckBox();
    caseSensitiveSCheckBox.setText("Case sensitive");
    caseSensitiveSCheckBox.setSelected(true);
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.WEST;
    bottomPanel.add(caseSensitiveSCheckBox, gbc);
    findCompleteBoxesPrevious = new JButton();
    findCompleteBoxesPrevious.setText("Find Previous");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 5;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    bottomPanel.add(findCompleteBoxesPrevious, gbc);
    replaceCompleteBoxesButton = new JButton();
    replaceCompleteBoxesButton.setText("Replace");
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 7;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    bottomPanel.add(replaceCompleteBoxesButton, gbc);
    ButtonGroup buttonGroup;
    buttonGroup = new ButtonGroup();
    buttonGroup.add(findOneBoxAfterAnotherRadioButton);
    buttonGroup.add(findAllBoxesAtOnceRadioButton);
    buttonGroup = new ButtonGroup();
    buttonGroup.add(replaceOneBoxAfterAnotherRadioButton);
    buttonGroup.add(replaceAllBoxesAtOnceRadioButton);
  }

  private void updateBoxesTab() {
    GridBagConstraints gbc;
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.BOTH;
    completeBoxes.add(findWhatPanel, gbc);
    final JPanel spacer19 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    completeBoxes.add(spacer19, gbc);
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.fill = GridBagConstraints.BOTH;
    completeBoxes.add(replaceWithPanel, gbc);
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.fill = GridBagConstraints.BOTH;
    completeBoxes.add(bottomPanel, gbc);
    final JPanel spacer35 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridheight = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    completeBoxes.add(spacer35, gbc);
    final JPanel spacer36 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.VERTICAL;
    completeBoxes.add(spacer36, gbc);
    final JPanel spacer37 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.VERTICAL;
    completeBoxes.add(spacer37, gbc);
  }

  private void fillComboBox() {
    ComboBoxToolTipRenderer renderer = new ComboBoxToolTipRenderer();
    ArrayList<String> tooltips = new ArrayList<String>();
    DefaultComboBoxModel<GraphFrame> model = new DefaultComboBoxModel<GraphFrame>();
    model.addElement(new defaultGraphFrame());
    tooltips.add(graphDefaultText);
    for (GraphFrame f : graphFrames) {
      model.addElement(f);
      if (f.getGraph() == null) {
        tooltips.add(f.toString());
      } else {
        tooltips.add(f.getGraph().getPath());
      }
    }
    renderer.setTooltips(tooltips);
    graphComboBox.setRenderer(renderer);
    graphComboBox.setModel(model);
    graphCompleteBoxesComboBox.setRenderer(renderer);
    graphCompleteBoxesComboBox.setModel(model);
  }

  private void onClose() {
    dispose();
  }

  private void onNext() {
    int i = 0;
    data.getGraphicalZone().unSelectAllBoxes();
    int res = 0;
    if (!graphComboBox.getSelectedItem().toString().equals(graphDefaultText)) {
      res = FindAndReplace.findAll(currentFrame.getGraphicalZone().getBoxes(), findTextField.getText(),
        useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), matchOnlyAWholeCheckBox
          .isSelected(), ignoreCommentBoxesCheckBox.isSelected());
    } else {
      for (GraphFrame f : graphFrames) {
        res += FindAndReplace.findAll(f.getGraphicalZone().getBoxes(), findTextField.getText(),
          useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), matchOnlyAWholeCheckBox
            .isSelected(), ignoreCommentBoxesCheckBox.isSelected());
      }
    }
    if (res == 0) {
      data.getGraphicalZone().setHighlight(false);
      return;
    }
    if (isValidFindTextField()) {
      GenericGraphBox nextBox = data.nextBox();
      if (onNextChangeGraph(nextBox)) {
        return;
      }
      while (!FindAndReplace.find(data.getGraphicalZone(), nextBox, findTextField.getText(),
        useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), matchOnlyAWholeCheckBox
          .isSelected(), ignoreCommentBoxesCheckBox.isSelected()) && i < data.getBoxes().size()) {
        i++;
        nextBox = data.nextBox();
        if (onNextChangeGraph(nextBox)) {
          return;
        }
      }
    }
  }

  private boolean onNextChangeGraph(GenericGraphBox nextBox) {
    if (nextBox == null) {
      if (!graphComboBox.getSelectedItem().toString().equals(graphDefaultText)) {
        selectGraph(currentFrame);
      } else {
        selectGraph(nextGraph());
      }
      onNext();
      return true;
    }
    return false;
  }

  private GraphFrame nextGraph() {
    int currentIndex = (graphFrames.indexOf(currentFrame) + 1) % graphFrames.size();
    return graphFrames.get(currentIndex);
  }

  private GraphFrame prevGraph() {
    int currentIndex = floorMod(graphFrames.indexOf(currentFrame) - 1, graphFrames.size());
    return graphFrames.get(currentIndex);
  }

  private void onPrev() {
    int i = 0;
    data.getGraphicalZone().unSelectAllBoxes();
    int res = 0;
    if (!graphComboBox.getSelectedItem().toString().equals(graphDefaultText)) {
      res = FindAndReplace.findAll(currentFrame.getGraphicalZone().getBoxes(), findTextField.getText(),
        useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), matchOnlyAWholeCheckBox
          .isSelected(), ignoreCommentBoxesCheckBox.isSelected());
    } else {
      for (GraphFrame f : graphFrames) {
        res += FindAndReplace.findAll(f.getGraphicalZone().getBoxes(), findTextField.getText(),
          useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), matchOnlyAWholeCheckBox
            .isSelected(), ignoreCommentBoxesCheckBox.isSelected());
      }
    }
    if (res == 0) {
      data.getGraphicalZone().setHighlight(false);
      return;
    }
    if (isValidFindTextField()) {
      GenericGraphBox prevBox = data.prevBox();
      if (onPrevChangeGraph(prevBox)) {
        return;
      }
      while (!FindAndReplace.find(data.getGraphicalZone(), prevBox, findTextField.getText(),
        useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), matchOnlyAWholeCheckBox
          .isSelected(), ignoreCommentBoxesCheckBox.isSelected()) && i < data.getBoxes().size()) {
        i++;
        prevBox = data.prevBox();
        if (onPrevChangeGraph(prevBox)) {
          return;
        }
      }
    }
  }

  private boolean onPrevChangeGraph(GenericGraphBox prevBox) {
    if (prevBox == null) {
      if (!graphComboBox.getSelectedItem().toString().equals(graphDefaultText)) {
        selectGraph(currentFrame);
      } else {
        selectGraph(prevGraph());
      }
      onPrev();
      return true;
    }
    return false;
  }

  private void onReplace() {
    if (!isValidTextField()) {
      return;
    }
    data.getGraphicalZone().unSelectAllBoxes();
    if (findTextField.getText().equals(replaceTextField.getText())) {
      return;
    }
    boolean wasReplaced = FindAndReplace.replace(data.getCurrentBox(), findTextField.getText(), replaceTextField
      .getText(), data.getGraphicalZone(), useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox
      .isSelected(), matchOnlyAWholeCheckBox.isSelected(), ignoreCommentBoxesCheckBox.isSelected());
    if (wasReplaced) {
      updateSingleReplaceResultTextField(1);
    } else {
      data.getGraphicalZone().setHighlight(false);
      updateSingleReplaceResultTextField(-1);
    }
  }

  private void onReplaceAll() {
    if (!isValidTextField()) {
      return;
    }
    int i = 0;
    if (graphComboBox.getSelectedItem().toString().equals(graphDefaultText)) {
      for (GraphFrame graphFrame : graphFrames) {
        i += FindAndReplace.replaceAll(graphFrame.getGraphicalZone().getBoxes(), findTextField.getText(),
          replaceTextField.getText(), graphFrame.getGraphicalZone(), useRegularExpressionsCheckBox.isSelected(),
          caseSensitiveCheckBox.isSelected(), matchOnlyAWholeCheckBox.isSelected(), ignoreCommentBoxesCheckBox
            .isSelected());
      }
    } else {
      i += FindAndReplace.replaceAll(currentFrame.getGraphicalZone().getBoxes(), findTextField.getText(),
        replaceTextField.getText(), currentFrame.getGraphicalZone(), useRegularExpressionsCheckBox.isSelected(),
        caseSensitiveCheckBox.isSelected(), matchOnlyAWholeCheckBox.isSelected(), ignoreCommentBoxesCheckBox
          .isSelected());
    }
    updateSingleReplaceResultTextField(i);
  }

  private boolean isValidTextField() {
    return isValidFindTextField() && isValidReplaceTextField();
  }

  private boolean isValidReplaceTextField() {
    return replaceTextField.getText() != null;
  }

  private boolean isValidFindTextField() {
    return findTextField.getText() != null && !findTextField.getText().equals("");
  }

  private void updateSingleFoundResultTextField() {
    if (!isValidFindTextField()) {
      statusBarTextField.setText("");
      return;
    }
    int res = 0;
    if (!graphComboBox.getSelectedItem().toString().equals(graphDefaultText)) {
      res = FindAndReplace.findAll(currentFrame.getGraphicalZone().getBoxes(), findTextField.getText(),
        useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), matchOnlyAWholeCheckBox
          .isSelected(), ignoreCommentBoxesCheckBox.isSelected());
    } else {
      for (GraphFrame f : graphFrames) {
        res += FindAndReplace.findAll(f.getGraphicalZone().getBoxes(), findTextField.getText(),
          useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), matchOnlyAWholeCheckBox
            .isSelected(), ignoreCommentBoxesCheckBox.isSelected());
      }
    }
    String msg;
    switch (res) {
      case 0:
        msg = "No match found with: " + findTextField.getText();
        break;
      case 1:
        msg = "Found 1 box which matches with: " + findTextField.getText();
        break;
      default:
        msg = "Found " + res + " boxes which match with: " + findTextField.getText();
    }
    statusBarTextField.setText(msg);
    statusBarTextField.setForeground(Color.BLACK);
  }

  private void updateSingleReplaceResultTextField(int i) {
    String msg;
    switch (i) {
      case -1:
        msg = "No box is selected";
        break;
      case 0:
        msg = "No box was replaced with: " + replaceTextField.getText();
        break;
      case 1:
        msg = "Replaced 1 box with: " + replaceTextField.getText();
        break;
      default:
        msg = "Replaced " + i + " boxes with: " + replaceTextField.getText();
        break;
    }
    statusBarTextField.setText(msg);
    statusBarTextField.setForeground(Color.BLACK);
  }

  private void updateSeqReplaceResultTextField(int i) {
    String msg;
    switch (i) {
      case -1:
        msg = "No sequence is selected";
        break;
      case 0:
        msg = "No sequence was replaced with: " + replaceCompleteBoxesTextField.getText();
        break;
      case 1:
        msg = "Replaced 1 sequence with: " + replaceCompleteBoxesTextField.getText();
        break;
      default:
        msg = "Replaced " + i + " sequences with: " + replaceCompleteBoxesTextField.getText();
        break;
    }
    statusBarTextField.setText(msg);
    statusBarTextField.setForeground(Color.BLACK);
  }

  private int updateSeqFoundResultTextField() {
    if (findCompleteBoxesTextField.getText().isEmpty()) {
      statusBarTextField.setText("");
      return -1;
    }
    String tokens[] = findCompleteBoxesTextField.getText().split(separator);
    findSeqList.clear();
    data.getGraphicalZone().unSelectAllBoxes();
    Collections.addAll(findSeqList, tokens);
    if (findSeqList.isEmpty()) {
      return -1;
    }
    int res = 0;
    if (!graphComboBox.getSelectedItem().toString().equals(graphDefaultText)) {
      for (GenericGraphBox box : data.getBoxes()) {
        if (FindAndReplace.isSeq(data.getGraphicalZone(), findSeqList, box, false, caseSensitiveSCheckBox.isSelected
          (), useRegularExpressionsSCheckBox.isSelected())) {
          res++;
        }
      }
    } else {
      for (GraphFrame f : graphFrames) {
        for (GenericGraphBox box : f.getGraphicalZone().getBoxes()) {
          if (FindAndReplace.isSeq(f.getGraphicalZone(), findSeqList, box, false, caseSensitiveSCheckBox.isSelected()
            , useRegularExpressionsSCheckBox.isSelected())) {
            res++;
          }
        }
      }
    }
    String msg;
    switch (res) {
      case 0:
        msg = "No match found with: " + findCompleteBoxesTextField.getText();
        break;
      case 1:
        msg = "Found 1 sequence which matches with: " + findCompleteBoxesTextField.getText();
        break;
      default:
        msg = "Found " + res + " sequences which match with: " + findCompleteBoxesTextField.getText();
    }
    statusBarTextField.setText(msg);
    statusBarTextField.setForeground(Color.BLACK);
    return res;
  }

  void updateDialog() {
    graphFrames = GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).getGraphFrames();
    currentFrame = GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
      .getCurrentFocusedGraphFrame();
    if (currentFrame == null) {
      currentFrame = graphFrames.get(0);
    }
    data = new FindAndReplaceData(currentFrame.getGraphicalZone().getBoxes(), currentFrame.getGraphicalZone());
  }

  @Override
  public void onUpdate(ArrayList<GraphFrame> frames) {
    if (frames == null || frames.isEmpty()) {
      dispose();
      return;
    }
    graphFrames = frames;
    currentFrame = GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
      .getCurrentFocusedGraphFrame();
    if (currentFrame == null) {
      currentFrame = graphFrames.get(0);
    }
    selectGraph(currentFrame);
    fillComboBox();
    updateTextField();
  }

  private void selectGraph(GraphFrame f) {
    if (f == null) {
      return;
    }
    currentFrame = f;
    try {
      f.setIcon(false);
      f.setSelected(true);
      data = new FindAndReplaceData(f.getGraphicalZone().getBoxes(), f.getGraphicalZone());
    } catch (PropertyVetoException exception) {
      exception.printStackTrace();
    }
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

  public class ComboBoxToolTipRenderer extends DefaultListCellRenderer {
    ArrayList<String> tooltips;

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

      JComponent comp = (JComponent) super.getListCellRendererComponent(list,
        value, index, isSelected, cellHasFocus);

      if (-1 < index && null != value && null != tooltips) {
        list.setToolTipText(tooltips.get(index));
      }
      return comp;
    }

    public void setTooltips(ArrayList<String> tooltips) {
      this.tooltips = tooltips;
    }
  }

}
