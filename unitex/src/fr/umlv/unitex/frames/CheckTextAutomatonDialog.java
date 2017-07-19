package fr.umlv.unitex.frames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class CheckTextAutomatonDialog extends JDialog {
  private JButton closeButton;
  private JButton button2;
  private JList jList;
  private JLabel statusLabel;
  private ArrayList<String> checkList = new ArrayList<>();
  private int errorCount;
  private int warningCount;


  private CheckTextAutomatonDialog(ArrayList<String> checkList) {
    super(UnitexFrame.mainFrame, "Check TextAutomaton", true);
    this.checkList = checkList;
    for (int i = 0; i < checkList.size(); i++) {
      String elt = checkList.get(i);
      if (elt.startsWith("Warning")) {
        warningCount++;
      } else if (elt.startsWith("Error")) {
        errorCount++;
      }
    }
    setResizable(false);
    setContentPane(constructPanel());
    setModalityType(ModalityType.MODELESS);
    pack();
    setLocationRelativeTo(UnitexFrame.mainFrame);
    setDefaultCloseOperation(HIDE_ON_CLOSE);
    createListeners();
  }

  private void createListeners() {
    closeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onClose();
      }
    });
  }

  private void onClose() {
    dispose();
  }

  private Container constructPanel() {
    final JPanel panel1 = new JPanel();
    panel1.setMinimumSize(new Dimension(350, 100));
    panel1.setLayout(new GridBagLayout());
    final JPanel spacer1 = new JPanel();
    GridBagConstraints gbc;
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 5;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel1.add(spacer1, gbc);
    final JPanel spacer2 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.fill = GridBagConstraints.VERTICAL;
    panel1.add(spacer2, gbc);
    button2 = new JButton();
    button2.setText("Button");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 5;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel1.add(button2, gbc);
    jList = new JList();
    final DefaultListModel defaultListModel1 = new DefaultListModel();
    for (int i = 0; i < checkList.size(); i++) {
      defaultListModel1.addElement(checkList.get(i));
    }
    jList.setModel(defaultListModel1);
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 5;
    gbc.fill = GridBagConstraints.BOTH;
    panel1.add(jList, gbc);
    final JPanel spacer3 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 4;
    gbc.fill = GridBagConstraints.VERTICAL;
    panel1.add(spacer3, gbc);
    final JPanel spacer4 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.VERTICAL;
    panel1.add(spacer4, gbc);
    final JPanel spacer5 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.VERTICAL;
    panel1.add(spacer5, gbc);
    closeButton = new JButton();
    closeButton.setText("Close");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 5;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel1.add(closeButton, gbc);
    statusLabel = new JLabel();
    statusLabel.setText(errorCount + " Error(s) | " + warningCount + " Warning(s)");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    panel1.add(statusLabel, gbc);
    final JPanel spacer6 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 5;
    gbc.fill = GridBagConstraints.VERTICAL;
    panel1.add(spacer6, gbc);
    return panel1;
  }

  public static CheckTextAutomatonDialog createCheckTextAutomatonDialog(ArrayList<String> checkList) {
    return new CheckTextAutomatonDialog(checkList);
  }
}
