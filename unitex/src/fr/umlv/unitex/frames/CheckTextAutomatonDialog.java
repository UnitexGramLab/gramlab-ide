package fr.umlv.unitex.frames;

<<<<<<< HEAD
import fr.umlv.unitex.MyCursors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

  class CustomListRenderer extends JLabel implements ListCellRenderer<String> {

    public CustomListRenderer() {
      setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, String value, int index, boolean isSelected, boolean cellHasFocus) {
      // Either Warning or Error
      String icon = value.split(":")[0];
      ImageIcon imageIcon = new ImageIcon(
        MyCursors.class.getResource("close.png"));
      if (icon.equals("Error")) {
        imageIcon = new ImageIcon(
          MyCursors.class.getResource("error.png"));
      } else if (icon.equals("Warning")) {
        imageIcon = new ImageIcon(
          MyCursors.class.getResource("warning.png"));
      }
      setBorder(new EmptyBorder(2,5,2,5));
      setIcon(imageIcon);
      setText(value);
      if (isSelected) {
        setBackground(list.getSelectionBackground());
        setForeground(list.getSelectionForeground());
      } else {
        setBackground(list.getBackground());
        setForeground(list.getForeground());
      }
      return this;
    }
  }


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
=======
import javax.swing.*;
import java.awt.*;

public class CheckTextAutomatonDialog extends JDialog {
  private JTable table1;


  private CheckTextAutomatonDialog() {
    super(UnitexFrame.mainFrame, "Check TextAutomaton", true);
>>>>>>> Add a new dialog to the check button
    setResizable(false);
    setContentPane(constructPanel());
    setModalityType(ModalityType.MODELESS);
    pack();
    setLocationRelativeTo(UnitexFrame.mainFrame);
    setDefaultCloseOperation(HIDE_ON_CLOSE);
    createListeners();
  }

  private void createListeners() {
<<<<<<< HEAD
    closeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onClose();
      }
    });
  }

  private void onClose() {
    dispose();
=======
>>>>>>> Add a new dialog to the check button
  }

  private Container constructPanel() {
    final JPanel panel1 = new JPanel();
<<<<<<< HEAD
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
    jList.setCellRenderer(new CustomListRenderer());
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
=======
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
>>>>>>> Add a new dialog to the check button
  }
}
