/*
 * Unitex
 *
 * Copyright (C) 2001-2018 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package fr.umlv.unitex.leximir.util;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import fr.umlv.unitex.leximir.helper.MenuDuplicate;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Anas Ait cheikh
 */
public class DuplicationFinder extends SwingWorker<Integer, Object> {
    JTable jtableRes;
    JTable jtableSrc;
    private JFrame frame = new JFrame();
    private JDialog dialog = new JDialog(frame, "Processing data", true);
    private JProgressBar progressBar = new JProgressBar();
    private JLabel l = new JLabel("Please wait while processing data ...");
    private JPanel p = new JPanel();
    private JButton b = new JButton("Cancel");

    public DuplicationFinder(JTable src) {

        jtableSrc = src;
        jtableRes = new JTable();
        jtableRes.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Duplucate value", "Lemma", "Fst", "SynSem", "dic"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jtableRes.setColumnSelectionAllowed(true);
        jtableRes.getTableHeader().setReorderingAllowed(false);
        jtableRes.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        progressBar.setString("");
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(true);

        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancel(true);
            }
        });
        p.add(l, BorderLayout.NORTH);
        p.add(progressBar, BorderLayout.CENTER);
        p.add(b, BorderLayout.SOUTH);
        dialog.getContentPane().add(p);
        dialog.setSize(350, 100);
        dialog.setModal(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static boolean areAllTrue(String[] text1, String[] text2) {
        int numberSame = 0;
        for (String a : text1) {
            for (String b : text2) {
                if (a.equals(b)) {
                    numberSame++;
                    break;
                }
            }
        }
        return numberSame == text1.length || numberSame == text1.length - 1;
    }
    
    /**
     * This function search the table for any duplicate values and 
     * add it to the jtable that contains the results
     */
    @Override
    protected Integer doInBackground() throws Exception {
        DefaultTableModel tableModel = (DefaultTableModel) jtableRes.getModel();
        for (int i = 0; i < jtableSrc.getRowCount() - 1; i++) {
            String lema = jtableSrc.getModel().getValueAt(i, 1).toString();
            String fst = jtableSrc.getModel().getValueAt(i, 2).toString();
            String SynSem = jtableSrc.getModel().getValueAt(i, 3).toString();
            String dic = jtableSrc.getModel().getValueAt(i, 7).toString();
            String[] SynSems = SynSem.split("(=)|(\\+)");
            for (int j = i + 1; j < jtableSrc.getRowCount(); j++) {
                String lemaCompare = jtableSrc.getModel().getValueAt(j, 1).toString();
                String fstCompare = jtableSrc.getModel().getValueAt(j, 2).toString();
                String SynSemCompare = jtableSrc.getModel().getValueAt(j, 3).toString();
                String dicCompare = jtableSrc.getModel().getValueAt(j, 7).toString();
                String[] SynSemCompares = SynSemCompare.split("()=|(\\+)");
                if (lemaCompare.equals(lema) && fstCompare.equals(fst)
                        && areAllTrue(SynSems, SynSemCompares)) {
                    Object[] rowCompare = new Object[]{j, lemaCompare, fstCompare, SynSemCompare, dicCompare};
                    Object[] row = new Object[]{i, lema, fst, SynSem, dic};
                    tableModel.addRow(row);
                    tableModel.addRow(rowCompare);
                }
            }
        }
        jtableRes.repaint();
        return 0;

    }

    @Override
    protected void done() {
        dialog.dispose();
        if(this.jtableRes.getModel().getRowCount()>0) {
            new MenuDuplicate(this.jtableRes);
        } else {
            JOptionPane.showMessageDialog(null, "No duplication where found !", "Duplication", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
