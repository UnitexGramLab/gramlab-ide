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
package fr.umlv.unitex.leximir.delac.menu;

import java.awt.Color;
import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import fr.umlv.unitex.leximir.delac.EditorDelac;
import fr.umlv.unitex.leximir.util.Utils;

/**
 * @author Rojo Rabelisoa
 */
public class MenuDelac extends javax.swing.JFrame {

    private EditorDelac editorDelac;
    private Object[] obj;
    private boolean edit = false;
    int idedit = 0;

    /**
     * Creates new form MenuAddBeforeDelac
     */
    public MenuDelac() {
        initComponents();
    }

    public MenuDelac(EditorDelac aThis, String menuSelected, Object[] obj, int selectedRow) {
        initComponents();
        DefaultTableCellRenderer color = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object valueLemaAll, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, valueLemaAll, isSelected, hasFocus, row, column);
                c.setBackground(row % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE);
                c.setForeground(Color.black);
                return c;
            }
        };
        jMenuPrediction.setVisible(false);
        editorDelac = aThis;
        this.obj = obj;
        switch (menuSelected) {
            case "insertBefore":
                this.idedit = (int) obj[6];
                break;
            case "insertAfter":
                this.idedit = ((int) obj[6]) + 1;
                break;
            case "copyBefore":
                jTextFieldLema.setText((String) this.obj[2]);
                jTextFieldLemaAll.setText((String) this.obj[1]);
                jTextFieldComment.setText((String) this.obj[5]);
                this.idedit = (int) obj[6];
                completeJtableDelaf((String) this.obj[1]);
                break;
            case "copyAfter":
                jTextFieldLema.setText((String) this.obj[2]);
                jTextFieldLemaAll.setText((String) this.obj[1]);
                jTextFieldComment.setText((String) this.obj[5]);
                this.idedit = ((int) obj[6]) + 1;
                completeJtableDelaf((String) this.obj[1]);
                break;
            case "view":
                jTextFieldLema.setText((String) this.obj[2]);
                jTextFieldLemaAll.setText((String) this.obj[1]);
                jTextFieldComment.setText((String) this.obj[5]);
                jMenuSave.setVisible(false);
                completeJtableDelaf((String) this.obj[1]);
                break;
            case "edit":
                edit = true;
                jTextFieldLema.setText((String) this.obj[2]);
                jTextFieldLemaAll.setText((String) this.obj[1]);
                jTextFieldComment.setText((String) this.obj[5]);
                this.idedit = (int) obj[6];
                completeJtableDelaf((String) this.obj[1]);
                break;
        }

        jTextFieldPos.setText((String) this.obj[0]);
        jTextFieldCFlx.setText((String) this.obj[3]);
        jTextFielddictionary.setText((String) this.obj[7]);
        jTextFieldSynSem.setText((String) this.obj[4]);
        int lemaid = Integer.parseInt(this.obj[6].toString()) + 1;
        jTextFieldLemaId.setText(String.valueOf(lemaid));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.jTableFLX.setDefaultRenderer(Object.class, color);
    }

    private void completeJtableDelaf(String LemmaAll) {
        String[] words = LemmaAll.split("-|\\ ");
        int separatorSpace = LemmaAll.indexOf(" ");
        int separatorIndex = LemmaAll.indexOf("-");
        char separator = 0;
        if (separatorSpace > -1) {
            separator = LemmaAll.charAt(separatorSpace);
        } else if (separatorIndex > -1) {
            separator = LemmaAll.charAt(separatorIndex);
        }
        DefaultTableModel model = new DefaultTableModel();
        model = (DefaultTableModel) jTableFLX.getModel();
//        model.addColumn("RB");
//        model.addColumn("Form");
//        model.addColumn("Lemma");
//        model.addColumn("FST Code");
//        model.addColumn("GramCat");
//        model.addColumn("Separator");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word.contains("(")) {
                int indexPosBegin = word.indexOf(".");
                int indexGramCat = word.indexOf(":");
                String lema = word.substring(word.indexOf("(") + 1, indexPosBegin);
                String fst = word.substring(indexPosBegin + 1, indexGramCat);
                String gramCat = word.substring(indexGramCat + 1, word.length() - 1);
                Object[] obj = new Object[6];
                if (i + 1 == words.length) {
                    obj = new Object[]{i, lema, lema, fst, gramCat, ""};
                } else {
                    obj = new Object[]{i, lema, lema, fst, gramCat, separator};
                }
                model.addRow(obj);
                

            } else {
                Object[] obj = new Object[]{i, word, "", "", "", ""};
                model.addRow(obj);
            }
        }
        jTableFLX.setModel(model);
        jTableFLX.repaint();
    }


    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jTabbedPaneTable = new javax.swing.JTabbedPane();
        jPanelCompound = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextFieldLemaAll = new javax.swing.JTextField();
        jTextFieldLema = new javax.swing.JTextField();
        jTextFieldComment = new javax.swing.JTextField();
        WorldNet = new javax.swing.JTextField();
        jTextFieldPos = new javax.swing.JTextField();
        jTextFieldCFlx = new javax.swing.JTextField();
        jTextFieldSynSem = new javax.swing.JTextField();
        jTextFielddictionary = new javax.swing.JTextField();
        jTextFieldLemaId = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableFLX = new javax.swing.JTable();
        jButtonAddSimpleForm = new javax.swing.JButton();
        jButtonRefresh = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jLabel12 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuSave = new javax.swing.JMenu();
        jMenuClose = new javax.swing.JMenu();
        jMenuInflect = new javax.swing.JMenu();
        jMenuPrediction = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel2.setText("Lemma (All)");

        jLabel3.setText("Lemma");

        jLabel4.setText("Comment");

        jLabel5.setText("WorldNet");

        jLabel6.setText("POS");

        jLabel7.setText("CFlx");

        jLabel8.setText("SynSem");

        jLabel9.setText("Dictionary");

        jLabel11.setText("Lemma id");

        jTextFieldLemaAll.setEditable(false);
        jTextFieldLemaAll.setBackground(new java.awt.Color(204, 204, 204));

        jTextFieldLema.setEditable(false);
        jTextFieldLema.setBackground(new java.awt.Color(204, 204, 204));

        jTextFieldPos.setEditable(false);
        jTextFieldPos.setBackground(new java.awt.Color(204, 204, 204));

        jTextFielddictionary.setEditable(false);
        jTextFielddictionary.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout jPanelCompoundLayout = new javax.swing.GroupLayout(jPanelCompound);
        jPanelCompound.setLayout(jPanelCompoundLayout);
        jPanelCompoundLayout.setHorizontalGroup(
            jPanelCompoundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCompoundLayout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(jPanelCompoundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanelCompoundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCompoundLayout.createSequentialGroup()
                        .addGroup(jPanelCompoundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldComment, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                            .addComponent(WorldNet)
                            .addComponent(jTextFieldLema))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelCompoundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                        .addGroup(jPanelCompoundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldPos, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldCFlx, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldSynSem, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jTextFieldLemaAll))
                .addGap(37, 37, 37)
                .addGroup(jPanelCompoundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel11))
                .addGap(18, 18, 18)
                .addGroup(jPanelCompoundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFielddictionary, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                    .addComponent(jTextFieldLemaId))
                .addGap(40, 40, 40))
        );
        jPanelCompoundLayout.setVerticalGroup(
            jPanelCompoundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCompoundLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCompoundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldLemaAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelCompoundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(jPanelCompoundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jLabel9)
                        .addComponent(jTextFieldLema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextFieldPos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextFielddictionary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanelCompoundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel7)
                    .addComponent(jTextFieldComment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldCFlx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelCompoundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel8)
                    .addComponent(jLabel11)
                    .addComponent(WorldNet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldSynSem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldLemaId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jTabbedPaneTable.addTab("Compound", jPanelCompound);

        jTableFLX.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                
            },
            new String [] {
        "RB",
        "Form",
        "Lemma",
        "FST Code",
        "GramCat",
        "Separator"
            }
        ));
        jScrollPane1.setViewportView(jTableFLX);

        jButtonAddSimpleForm.setText("Add simple form");
        jButtonAddSimpleForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddSimpleFormActionPerformed(evt);
            }
        });

        jButtonRefresh.setText("Refresh Lemma");
        jButtonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRefreshActionPerformed(evt);
            }
        });

        jLabel12.setText("");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButtonAddSimpleForm)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonRefresh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonRefresh)
                    .addComponent(jButtonAddSimpleForm)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPaneTable)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPaneTable, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jMenuSave.setText("Save&Close");
        jMenuSave.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuSaveMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenuSave);

        jMenuClose.setText("Just close");
        jMenuClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuCloseMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenuClose);

        jMenuInflect.setText("Inflect");
        jMenuInflect.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuInflectMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenuInflect);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 3, Short.MAX_VALUE))
        );

        pack();
    }

    private void jButtonRefreshActionPerformed(java.awt.event.ActionEvent evt) {
        String valueLemaAll = "";
        String valueLema = "";
        for (int row = 0; row < jTableFLX.getRowCount(); row++) {
            if (!jTableFLX.getModel().getValueAt(row, 1).equals("")) {
                valueLemaAll = valueLemaAll + jTableFLX.getModel().getValueAt(row, 1);
                valueLema = valueLema + jTableFLX.getModel().getValueAt(row, 1);
                if (!jTableFLX.getModel().getValueAt(row, 2).equals("")) {
                    valueLemaAll = valueLemaAll + "(" + jTableFLX.getModel().getValueAt(row, 2);
                    if (!jTableFLX.getModel().getValueAt(row, 3).equals("")) {
                        valueLemaAll = valueLemaAll + "." + jTableFLX.getModel().getValueAt(row, 3);
                        if (!jTableFLX.getModel().getValueAt(row, 4).equals("")) {
                            valueLemaAll = valueLemaAll + ":" + jTableFLX.getModel().getValueAt(row, 4) + ")";
                        }
                    }
                }
                if (jTableFLX.getModel().getValueAt(row, 5) != null) {
                    valueLema = valueLema + jTableFLX.getModel().getValueAt(row, 5);
                    valueLemaAll = valueLemaAll + jTableFLX.getModel().getValueAt(row, 5);
                } else {
                    valueLema = row == jTableFLX.getRowCount() - 1 ? valueLema : valueLema + " ";
                    valueLemaAll = row == jTableFLX.getRowCount() - 1 ? valueLema : valueLemaAll + " ";
                }
            }
        }
        jTextFieldLemaAll.setText(valueLemaAll);
        jTextFieldLema.setText(valueLema);

    }

    private void jMenuSaveMouseClicked(java.awt.event.MouseEvent evt) {
        try {
            String lemmaAll = jTextFieldLemaAll.getText();
            String FST = jTextFieldCFlx.getText();
            String SynSem = "";
            try{
            SynSem = jTextFieldSynSem.getText().substring(0, 1).equals("+") ? jTextFieldSynSem.getText() : "+" + jTextFieldSynSem.getText();
            } catch (java.lang.StringIndexOutOfBoundsException ex) {
                SynSem = "";
            }
            String dic = jTextFielddictionary.getText();
            String comment = jTextFieldComment.getText();

            Object[] row = Utils.delacToObject(lemmaAll, FST, SynSem, comment, dic);
            if (edit) {
                for (int i = 0; i < row.length; i++) {
                    if (i != 6) {
                        editorDelac.getTableModel().setValueAt(row[i], idedit, i);
                    }
                }
            } else {
                editorDelac.getTableModel().insertRow(idedit, row);
                editorDelac.getjTable1().setModel(editorDelac.getTableModel());
                for (int i = idedit; i < editorDelac.getTableModel().getRowCount(); i++) {
                    editorDelac.getTableModel().setValueAt(i, i, 6);
                }
                editorDelac.getJLablel13().setText(String.valueOf(editorDelac.getjTable1().getRowCount()));
            }
            editorDelac.setUnsaved(true);     
            this.dispose();
            
        } catch (ArrayIndexOutOfBoundsException e) {
            this.dispose();
        }
    }

  
    private void jMenuInflectMouseClicked(java.awt.event.MouseEvent evt) {
        if (jTextFieldLemaAll.getText().equals("") || jTextFieldCFlx.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "something wrong");
        } else {
            try {
                String lema = jTextFieldLemaAll.getText();
                String code = jTextFieldCFlx.getText();
                Utils.InflectDelas(lema, code);
                JOptionPane.showMessageDialog(null, "done");
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "error :" + ex.getMessage());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "error :" + ex.getMessage());
            }

        }
    }

    private void jButtonAddSimpleFormActionPerformed(java.awt.event.ActionEvent evt) {
        DefaultTableModel newmodel = (DefaultTableModel) jTableFLX.getModel();
        newmodel.addRow(new Object[]{jTableFLX.getModel().getRowCount() + 1, "", "", "", "", ""});
        jTableFLX.invalidate();
    }

    private void jMenuCloseMouseClicked(java.awt.event.MouseEvent evt) {
        this.setVisible(false);
    }

    private javax.swing.JTextField WorldNet;
    private javax.swing.JButton jButtonAddSimpleForm;
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuClose;
    private javax.swing.JMenu jMenuInflect;
    private javax.swing.JMenu jMenuPrediction;
    private javax.swing.JMenu jMenuSave;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelCompound;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPaneTable;
    private javax.swing.JTable jTableFLX;
    private javax.swing.JTextField jTextFieldCFlx;
    private javax.swing.JTextField jTextFieldComment;
    private javax.swing.JTextField jTextFieldLema;
    private javax.swing.JTextField jTextFieldLemaAll;
    private javax.swing.JTextField jTextFieldLemaId;
    private javax.swing.JTextField jTextFieldPos;
    private javax.swing.JTextField jTextFieldSynSem;
    private javax.swing.JTextField jTextFielddictionary;

}
