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
import fr.umlv.unitex.utils.KeyUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

/**
 * This class defines a dialog that allow the user to search and replace the content of one or more boxes
 * in the open graphs.
 *
 * @author Maxime Petit
 */
public class FindAndReplaceDialog extends JDialog implements MultiInstanceFrameFactoryObserver<GraphFrame> {
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
    ArrayList<GraphFrame> graphFrames = GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).getGraphFrames();
    GraphFrame currentFrame = GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).getCurrentFocusedGraphFrame();
    if (currentFrame == null) {
      currentFrame = graphFrames.get(0);
    }
    FindAndReplaceData data = new FindAndReplaceData(currentFrame.getGraphicalZone().getBoxes(), currentFrame.getGraphicalZone());
    return new FindAndReplaceDialog(graphFrames, currentFrame, data);
  }

  private void createListeners() {
    closeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onClose();
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
        if (findTextField.getText() != null) {
          updateFoundResultTextField();
        }
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
        updateFoundResultTextField();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        updateFoundResultTextField();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        // Do nothing
      }
    });
    useRegularExpressionsCheckBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateFoundResultTextField();
      }
    });
    caseSensitiveCheckBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateFoundResultTextField();
      }
    });
    matchOnlyAWholeCheckBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateFoundResultTextField();
      }
    });
    ignoreCommentBoxesCheckBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateFoundResultTextField();
      }
    });
  }

  private JPanel constructPanel() {
    JPanel panel1 = new JPanel();
    KeyUtil.addCloseDialogListener(panel1);
    panel1.setLayout(new GridBagLayout());
    panel1.setPreferredSize(new Dimension(480, 259));
    final JLabel label1 = new JLabel();
    label1.setText("Find what:");
    GridBagConstraints gbc;
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    panel1.add(label1, gbc);
    final JLabel label2 = new JLabel();
    label2.setText("Replace with:");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.WEST;
    panel1.add(label2, gbc);
    final JLabel label3 = new JLabel();
    label3.setText("Graphs:");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 5;
    gbc.anchor = GridBagConstraints.WEST;
    panel1.add(label3, gbc);
    graphComboBox = new JComboBox();
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
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 5;
    gbc.gridwidth = 3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel1.add(graphComboBox, gbc);
    caseSensitiveCheckBox = new JCheckBox();
    caseSensitiveCheckBox.setText("Case sensitive");
    caseSensitiveCheckBox.setSelected(true);
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 7;
    gbc.anchor = GridBagConstraints.WEST;
    panel1.add(caseSensitiveCheckBox, gbc);
    matchOnlyAWholeCheckBox = new JCheckBox();
    matchOnlyAWholeCheckBox.setText("Match only a whole line");
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 7;
    gbc.anchor = GridBagConstraints.WEST;
    panel1.add(matchOnlyAWholeCheckBox, gbc);
    closeButton = new JButton();
    closeButton.setMnemonic(KeyEvent.VK_C);
    closeButton.setMaximumSize(new Dimension(30, 32));
    closeButton.setPreferredSize(new Dimension(90, 26));
    closeButton.setText("Close");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 12;
    gbc.anchor = GridBagConstraints.WEST;
    panel1.add(closeButton, gbc);
    final JPanel spacer1 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 10;
    gbc.fill = GridBagConstraints.VERTICAL;
    panel1.add(spacer1, gbc);
    findNextButton = new JButton();
    findNextButton.setMnemonic(KeyEvent.VK_N);
    findNextButton.setText("Find Next");
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 10;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel1.add(findNextButton, gbc);
    findPreviousButton = new JButton();
    findPreviousButton.setMnemonic(KeyEvent.VK_P);
    findPreviousButton.setText("Find Previous");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 10;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel1.add(findPreviousButton, gbc);
    replaceAllButton = new JButton();
    replaceAllButton.setMnemonic(KeyEvent.VK_A);
    replaceAllButton.setText("Replace All");
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 12;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel1.add(replaceAllButton, gbc);
    replaceButton = new JButton();
    replaceButton.setMnemonic(KeyEvent.VK_R);
    replaceButton.setText("Replace");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 12;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel1.add(replaceButton, gbc);
    final JPanel spacer2 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 6;
    gbc.fill = GridBagConstraints.VERTICAL;
    panel1.add(spacer2, gbc);
    final JPanel spacer3 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.VERTICAL;
    panel1.add(spacer3, gbc);
    final JPanel spacer4 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 4;
    gbc.fill = GridBagConstraints.VERTICAL;
    panel1.add(spacer4, gbc);
    final JPanel spacer5 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 11;
    gbc.fill = GridBagConstraints.VERTICAL;
    panel1.add(spacer5, gbc);
    final JPanel spacer6 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 12;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel1.add(spacer6, gbc);
    final JPanel spacer7 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 10;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel1.add(spacer7, gbc);
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridBagLayout());
    panel2.setPreferredSize(new Dimension(480, 24));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 14;
    gbc.gridwidth = 7;
    gbc.fill = GridBagConstraints.BOTH;
    panel1.add(panel2, gbc);
    statusBarTextField = new JTextField();
    statusBarTextField.setEditable(false);
    statusBarTextField.setPreferredSize(new Dimension(480, 24));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel2.add(statusBarTextField, gbc);
    final JPanel spacer8 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel1.add(spacer8, gbc);
    final JPanel spacer9 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 13;
    gbc.fill = GridBagConstraints.VERTICAL;
    panel1.add(spacer9, gbc);
    final JPanel spacer10 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.VERTICAL;
    panel1.add(spacer10, gbc);
    final JPanel spacer11 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 6;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel1.add(spacer11, gbc);
    ignoreCommentBoxesCheckBox = new JCheckBox();
    ignoreCommentBoxesCheckBox.setText("Ignore comment boxes");
    ignoreCommentBoxesCheckBox.setSelected(true);
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 8;
    gbc.anchor = GridBagConstraints.WEST;
    panel1.add(ignoreCommentBoxesCheckBox, gbc);
    useRegularExpressionsCheckBox = new JCheckBox();
    useRegularExpressionsCheckBox.setText("Use regular expressions");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 8;
    gbc.anchor = GridBagConstraints.WEST;
    panel1.add(useRegularExpressionsCheckBox, gbc);
    final JPanel spacer12 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 9;
    gbc.fill = GridBagConstraints.VERTICAL;
    panel1.add(spacer12, gbc);
    findTextField = new JTextField();
    findTextField.setEditable(true);
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 1;
    gbc.gridwidth = 3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel1.add(findTextField, gbc);
    replaceTextField = new JTextField();
    replaceTextField.setEditable(true);
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 3;
    gbc.gridwidth = 3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel1.add(replaceTextField, gbc);
    return panel1;
  }

  private void onClose() {
    dispose();
  }

  private void onNext() {
    int i = 0;
    data.getGraphicalZone().unSelectAllBoxes();
    if (isValidFindTextField()) {
      while (!FindAndReplace.find(data.getGraphicalZone(), data.nextBox(), findTextField.getText(), useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), matchOnlyAWholeCheckBox.isSelected(), ignoreCommentBoxesCheckBox.isSelected()) && i < data.getBoxes().size()) {
        i++;
      }
    }
  }

  private void onPrev() {
    int i = 0;
    data.getGraphicalZone().unSelectAllBoxes();
    if (isValidFindTextField()) {
      while (!FindAndReplace.find(data.getGraphicalZone(), data.prevBox(), findTextField.getText(), useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), matchOnlyAWholeCheckBox.isSelected(), ignoreCommentBoxesCheckBox.isSelected()) && i < data.getBoxes().size()) {
        i++;
      }
    }
  }

  private void onReplace() {
    if (!isValidTextField()) {
      return;
    }
    data.getGraphicalZone().unSelectAllBoxes();
    boolean wasReplaced = FindAndReplace.replace(data.getCurrentBox(), findTextField.getText(), replaceTextField.getText(), data.getGraphicalZone(), useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), matchOnlyAWholeCheckBox.isSelected(), ignoreCommentBoxesCheckBox.isSelected());
    if (wasReplaced) {
      updateReplaceResultTextField(1);
    } else {
      updateReplaceResultTextField(0);
    }
  }

  private void onReplaceAll() {
    if (!isValidTextField()) {
      return;
    }
    int i = 0;
    if (graphComboBox.getSelectedItem().toString().equals(graphDefaultText)) {
      for (GraphFrame graphFrame : graphFrames) {
        i += FindAndReplace.replaceAll(graphFrame.getGraphicalZone().getBoxes(), findTextField.getText(), replaceTextField.getText(), graphFrame.getGraphicalZone(), useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), matchOnlyAWholeCheckBox.isSelected(), ignoreCommentBoxesCheckBox.isSelected());
      }
    } else {
      i += FindAndReplace.replaceAll(currentFrame.getGraphicalZone().getBoxes(), findTextField.getText(), replaceTextField.getText(), currentFrame.getGraphicalZone(), useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), matchOnlyAWholeCheckBox.isSelected(), ignoreCommentBoxesCheckBox.isSelected());
    }
    updateReplaceResultTextField(i);
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

  private void updateFoundResultTextField() {
    if (!isValidFindTextField()) {
      statusBarTextField.setText("");
      return;
    }
    int res = 0;
    if (!graphComboBox.getSelectedItem().toString().equals(graphDefaultText)) {
      res = FindAndReplace.findAll(currentFrame.getGraphicalZone().getBoxes(), findTextField.getText(), useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), matchOnlyAWholeCheckBox.isSelected(), ignoreCommentBoxesCheckBox.isSelected());
    } else {
      for (GraphFrame f : graphFrames) {
        res += FindAndReplace.findAll(f.getGraphicalZone().getBoxes(), findTextField.getText(), useRegularExpressionsCheckBox.isSelected(), caseSensitiveCheckBox.isSelected(), matchOnlyAWholeCheckBox.isSelected(), ignoreCommentBoxesCheckBox.isSelected());
      }
    }
    Integer t = res;
    String msg;
    switch (res) {
      case 0:
        msg = "No match found with " + findTextField.getText();
        break;
      case 1:
        msg = "Found 1 box which match with " + findTextField.getText();
        break;
      default:
        msg = "Found " + t.toString() + " boxes which match with " + findTextField.getText();
    }
    statusBarTextField.setText(msg);
    getParent().repaint();
  }

  private void updateReplaceResultTextField(int i) {
    String msg;
    switch (i) {
      case 0:
        msg = "No match found with " + findTextField.getText();
        break;
      case 1:
        msg = "Replaced 1 box which match " + findTextField.getText() + " with " + replaceTextField.getText();
        break;
      default:
        msg = "Replaced " + i + " boxes which match " + findTextField.getText() + " with " + replaceTextField.getText();
        break;
    }
    statusBarTextField.setText(msg);
  }

  void updateDialog() {
    graphFrames = GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).getGraphFrames();
    currentFrame = GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).getCurrentFocusedGraphFrame();
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
    currentFrame = GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).getCurrentFocusedGraphFrame();
    if (currentFrame == null) {
      currentFrame = graphFrames.get(0);
    }
    try {
      currentFrame.setIcon(false);
      currentFrame.setSelected(true);
      data = new FindAndReplaceData(currentFrame.getGraphicalZone().getBoxes(), currentFrame.getGraphicalZone());
    } catch (PropertyVetoException exception) {
      exception.printStackTrace();
    }
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
