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
package fr.umlv.unitex.leximir.delas.menu;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import fr.umlv.unitex.leximir.delas.EditorDelas;
import fr.umlv.unitex.leximir.model.DictionaryPath;
import fr.umlv.unitex.leximir.util.Utils;

/**
 * @author Rojo Rabelisoa
 */
public class MenuDelas extends javax.swing.JFrame {
    private EditorDelas elFrame ;
    private boolean edit = false;
    int idedit=0;
    /**
     * Creates new form AddDelas
     */
    public MenuDelas() {

        initComponents();
        for(String dic:DictionaryPath.dictionary){
            jComboBoxDic.addItem(dic);
        };
    }
    public MenuDelas(EditorDelas el,int selectedRow,String menuSelected,Object dictionary, Object[] obj){
        
        initComponents();
        for(String dic:DictionaryPath.dictionary){
            jComboBoxDic.addItem(dic);
        };
        jComboBoxDic.setSelectedItem(dictionary);
        this.elFrame=el;
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        switch (menuSelected) {
            case "insertBefore":
                this.idedit = (int) obj[6];
                jLabelTitle.setText("Insertion value");
                break;
            case "insertAfter":
                this.idedit = ((int) obj[6])+1;
                jLabelTitle.setText("Insertion value");
                break;
            case "copyBefore":
                this.jTextFieldLemma.setText((String) obj[1]);
                this.jTextFieldFST.setText((String) obj[2]);
                this.jTextFieldSynSem.setText((String) obj[3]);
                this.jTextFieldComment.setText((String) obj[4]);
                this.idedit = (int) obj[6];
                jLabelTitle.setText("Insertion value");
                break;
            case "copyAfter":
               this.jTextFieldLemma.setText((String) obj[1]);
                this.jTextFieldFST.setText((String) obj[2]);
                this.jTextFieldSynSem.setText((String) obj[3]);
                this.jTextFieldComment.setText((String) obj[4]);
                this.idedit = ((int) obj[6])+1;
                jLabelTitle.setText("Insertion value");
                break;
            case "view":
                this.jTextFieldLemma.setText((String) obj[1]);
                this.jTextFieldFST.setText((String) obj[2]);
                this.jTextFieldSynSem.setText((String) obj[3]);
                this.jTextFieldComment.setText((String) obj[4]);
                jButtonAdd.setVisible(false);
                
                jLabelTitle.setText("View value");
                break;
            case "edit":
                edit=true;
                this.jTextFieldLemma.setText((String) obj[1]);
                this.jTextFieldFST.setText((String) obj[2]);
                this.jTextFieldSynSem.setText((String) obj[3]);
                this.jTextFieldComment.setText((String) obj[4]);
                this.idedit = (int) obj[6];
                jLabelTitle.setText("Edit value");
                break;
        }
    }


    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldLemma = new javax.swing.JTextField();
        jTextFieldFST = new javax.swing.JTextField();
        jComboBoxDic = new javax.swing.JComboBox<String>();
        jButtonAdd = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldComment = new javax.swing.JTextField();
        jButtonInflect = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldSynSem = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabelTitle.setText(" value");

        jLabel2.setText("Lemma");

        jLabel3.setText("FST Code");

        jLabel4.setText("Dictionary");

        jComboBoxDic.setModel(new javax.swing.DefaultComboBoxModel());
      
        jButtonAdd.setText("Add");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jLabel5.setText("Comment");


        jButtonInflect.setText("Inflect");
        jButtonInflect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInflectActionPerformed(evt);
            }
        });

        jLabel6.setText("SynSem");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 125, Short.MAX_VALUE)
                        .addComponent(jButtonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonInflect)
                        .addGap(5, 5, 5)
                        .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBoxDic, 0, 265, Short.MAX_VALUE)
                                    .addComponent(jTextFieldComment)
                                    .addComponent(jTextFieldSynSem, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(47, 47, 47)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldLemma)
                                    .addComponent(jTextFieldFST))))))
                .addGap(94, 94, 94))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(210, 210, 210)
                .addComponent(jLabelTitle)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabelTitle)
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldLemma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldFST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextFieldSynSem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextFieldComment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBoxDic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAdd)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonInflect))
                .addGap(32, 32, 32))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(33, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {
       this.setVisible(false);
    }


    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {
    try{    
    String lemma = jTextFieldLemma.getText();
    String FST = jTextFieldFST.getText();
    String SynSem = jTextFieldSynSem.getText().equals("")?"":jTextFieldSynSem.getText().substring(0,1).equals("+")?jTextFieldSynSem.getText():"+"+jTextFieldSynSem.getText();
    String dic = (String) jComboBoxDic.getSelectedItem();
    String comment=jTextFieldComment.getText();

    Object[] row = Utils.delasToObject(lemma,FST,SynSem,comment,dic,idedit);
    if(edit){
        for(int i=0;i<row.length;i++){
            if(i!=6){
               elFrame.getTableModel().setValueAt(row[i],idedit,i);
            }
        }
    }else{
       elFrame.getTableModel().insertRow(idedit,row);
       elFrame.getjTable1().setModel(elFrame.getTableModel());
       for(int i=idedit;i<elFrame.getTableModel().getRowCount();i++){
           elFrame.getTableModel().setValueAt(i,i,6);
       }
       elFrame.getJLablel13().setText(String.valueOf(elFrame.getjTable1().getRowCount()));
    }
    elFrame.setUnsaved(true);
    this.setVisible(false); 
    }catch(Exception e){
    elFrame.setUnsaved(true);
    this.setVisible(false);     }
    }

    private void jButtonInflectActionPerformed(java.awt.event.ActionEvent evt) {
        if(jTextFieldFST.getText().equals("")||jTextFieldLemma.getText().equals("")){
            JOptionPane.showMessageDialog(null, "Lemma or FST code is empty");
        }
        else{
            try {
                String lemma =jTextFieldLemma.getText() ;
                String fst = jTextFieldFST.getText();
                Utils.InflectDelas(lemma, fst);
                JOptionPane.showMessageDialog(null, "done");
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "error :"+ex.getMessage());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "error :"+ex.getMessage());
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonInflect;
    private javax.swing.JComboBox<String> jComboBoxDic;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextFieldComment;
    private javax.swing.JTextField jTextFieldFST;
    private javax.swing.JTextField jTextFieldLemma;
    private javax.swing.JTextField jTextFieldSynSem;
    // End of variables declaration//GEN-END:variables

    
}
