package fr.umlv.unitex.frames;

import javax.swing.*;
import java.awt.*;

public class CheckTextAutomatonDialog extends JDialog {
  private JTable table1;


  private CheckTextAutomatonDialog() {
    super(UnitexFrame.mainFrame, "Check TextAutomaton", true);
    setResizable(false);
    setContentPane(constructPanel());
    setModalityType(ModalityType.MODELESS);
    pack();
    setLocationRelativeTo(UnitexFrame.mainFrame);
    setDefaultCloseOperation(HIDE_ON_CLOSE);
    createListeners();
  }

  private void createListeners() {
  }

  private Container constructPanel() {
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridBagLayout());
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new GridBagLayout());
    GridBagConstraints gbc;
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.BOTH;
    panel1.add(panel2, gbc);
    final JLabel label1 = new JLabel();
    label1.setText("2 Errors");
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    panel2.add(label1, gbc);
    final JPanel spacer1 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel2.add(spacer1, gbc);
    final JLabel label2 = new JLabel();
    label2.setText("2 Warnings");
    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    panel2.add(label2, gbc);
    final JPanel spacer2 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel2.add(spacer2, gbc);
    final JPanel spacer3 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel2.add(spacer3, gbc);
    final JPanel spacer4 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.VERTICAL;
    panel2.add(spacer4, gbc);
    final JPanel spacer5 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.VERTICAL;
    panel1.add(spacer5, gbc);
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.BOTH;
    panel1.add(panel3, gbc);
    final JPanel spacer6 = new JPanel();
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.VERTICAL;
    panel3.add(spacer6, gbc);
    String[] columnNames = {"Box",
      "Description"};
    Object[][] data = {
      {"Kathy", "Warning: the box has no outgoing transition.",
        },
      {"John", "Warning: the box has no outgoing transition.",
        },
      {"Sue", "Warning: the box has no outgoing transition.",
        },
      {"Jane", "Warning: the box has no outgoing transition.",
        },
      {"Joe", "Warning: the box has no outgoing transition.",
        }
    };
    table1 = new JTable(data, columnNames);
    table1.setPreferredSize(new Dimension(400,400));
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.BOTH;
    panel3.add(table1, gbc);
    return panel1;
  }

  public static CheckTextAutomatonDialog createCheckTextAutomatonDialog() {
    return new CheckTextAutomatonDialog();
  }

  public void updateDialog() {
  }
}
