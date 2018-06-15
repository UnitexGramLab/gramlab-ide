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
package fr.umlv.unitex.leximir.shell;

import fr.umlv.unitex.files.PersonalFileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import fr.umlv.unitex.leximir.model.DictionaryPath;
import fr.umlv.unitex.leximir.util.Utils;

/**
 * @author Rojo Rabelisoa
 */
public class Shell extends javax.swing.JInternalFrame {
    /**
     * Creates new form Shell
     */
    public Shell() {
        super("Compile...", true, true, true, true);
        initComponents();
        jRadioInflectCompress.setSelected(true);
        buttonGroup1.add(jRadioCompress);
        buttonGroup1.add(jRadioInflectCompress);
        jTextFieldInflection.setText(DictionaryPath.inflectionPath);
        final JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jTable1.getSelectedRow() > -1) {
                    int idDuplicate = jTable1.getSelectedRow();
                    DefaultTableModel tm = (DefaultTableModel) jTable1.getModel();
                    tm.removeRow(idDuplicate);
                    jTable1.repaint();
                } else {
                    JOptionPane.showMessageDialog(null, "no value selected");
                }
            }
        });
        popupMenu.add(deleteItem);
        jTable1.setComponentPopupMenu(popupMenu);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jRadioInflectCompress = new javax.swing.JRadioButton();
        jRadioCompress = new javax.swing.JRadioButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButtonDo = new javax.swing.JButton();
        jButtonQuit = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jTextFieldPath = new javax.swing.JTextField();
        jButtonSearch = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jTextFieldInflection = new javax.swing.JTextField();
        jButtonInflectionPath = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jRadioInflectCompress.setText("Inflect delas(c) and compress");
        jRadioInflectCompress.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRadioInflectCompressMouseClicked(evt);
            }
        });

        jRadioCompress.setText("Compress delaf");
        jRadioCompress.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRadioCompressMouseClicked(evt);
            }
        });

        jTable1.setModel(tableModel
        );
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 232, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 199, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Selected", jPanel4);

        jButtonDo.setText("Compile");
        jButtonDo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDoActionPerformed(evt);
            }
        });

        jButtonQuit.setText("Quit");
        jButtonQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonQuitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioInflectCompress)
                            .addComponent(jRadioCompress)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jButtonDo, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonQuit)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jRadioInflectCompress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioCompress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonQuit, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Select dela dictionnaries (dic files)"));

        jTextFieldPath.setEditable(false);
        jTextFieldPath.setBackground(new java.awt.Color(204, 204, 204));

        jButtonSearch.setText("Browse");
        jButtonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jTextFieldPath, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSearch)
                .addGap(0, 49, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSearch))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Inflection path"));

        jTextFieldInflection.setEditable(false);
        jTextFieldInflection.setBackground(new java.awt.Color(204, 204, 204));

        jButtonInflectionPath.setText("Browse");
        jButtonInflectionPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInflectionPathActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jTextFieldInflection, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonInflectionPath)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldInflection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonInflectionPath)))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(22, 22, 22))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.getAccessibleContext().setAccessibleName("Select dela dictionaries (dic files)");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSearchActionPerformed(java.awt.event.ActionEvent evt) {
        if(!isDelas){
            JFileChooser theFileChooser = new JFileChooser();
            PersonalFileFilter filter = new PersonalFileFilter("Dic FILES", "dic");
            theFileChooser.setFileFilter(filter);
            theFileChooser.setCurrentDirectory(new File(DictionaryPath.allDela+ File.separator+ "Dela"));
            theFileChooser.setDialogTitle("Search dela dictionary");
            theFileChooser.setMultiSelectionEnabled(true);
            theFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            theFileChooser.setAcceptAllFileFilterUsed(false);
            if(theFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                jTable1.removeAll();
                File[] f = theFileChooser.getSelectedFiles();
                jTextFieldPath.setText(f[0].getParent());
                tableModel = new DefaultTableModel();
                tableModel.addColumn("dic");
                tableModel.addColumn("path");
                for(File i:f){
                    String tmp = i.getName();
                    if(tmp.endsWith(".dic")){
                        tableModel.addRow(new Object[]{tmp,i.getAbsolutePath()});
                    }
                }  
                jTable1.setModel(tableModel);
                jTable1.repaint();
            }
        }
    }

    private void jButtonQuitActionPerformed(java.awt.event.ActionEvent evt) {
       this.setVisible(false);
    }

    private void jButtonDoActionPerformed(java.awt.event.ActionEvent evt) {
        if(jRadioInflectCompress.isSelected()){
            try {
                InflectCompressDela();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "error : "+ex);
            }
        }
        else if(jRadioCompress.isSelected()){
            try {
                CompressDelaf();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "error : "+ex);
            }
        }else{
            JOptionPane.showMessageDialog(null, "no radio selected");
        }
    }

    private void jRadioInflectCompressMouseClicked(java.awt.event.MouseEvent evt) {
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Select dela dictionary (dic file)"));
        isDelas=false;
        jTextFieldPath.setEditable(false);
        jTextFieldPath.setBackground(new java.awt.Color(204, 204, 204));
    }

    private void jRadioCompressMouseClicked(java.awt.event.MouseEvent evt) {
      jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Select delaf dictionary (dic file)"));
      isDelas=false;
      jTextFieldPath.setEditable(false);
      jTextFieldPath.setBackground(new java.awt.Color(204, 204, 204));
    }

    private void jButtonInflectionPathActionPerformed(java.awt.event.ActionEvent evt) {
       if(!isDelas){
            JFileChooser theFileChooser = new JFileChooser();
            theFileChooser.setCurrentDirectory(new File(DictionaryPath.inflectionPath));
            theFileChooser.setDialogTitle("Search dela dictionary");
            theFileChooser.setMultiSelectionEnabled(true);
            theFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            theFileChooser.setAcceptAllFileFilterUsed(false);
            if(theFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                jTable1.removeAll();
                File[] f = theFileChooser.getSelectedFiles();
                jTextFieldInflection.setText(f[0].getAbsolutePath());
            }
        }
    }

    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonDo;
    private javax.swing.JButton jButtonInflectionPath;
    private javax.swing.JButton jButtonQuit;
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JRadioButton jRadioCompress;
    private javax.swing.JRadioButton jRadioInflectCompress;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextFieldInflection;
    private javax.swing.JTextField jTextFieldPath;
    // End of variables declaration//GEN-END:variables
    private DefaultTableModel tableModel = new DefaultTableModel();
    private boolean isDelas=false;

    private void InflectCompressDela() throws IOException {
        jTextArea1.append("Inflect Delas dictionary to Delaf ...\n");
        for(int i=0;i<jTable1.getRowCount();i++){
            String strDelas = (String) jTable1.getModel().getValueAt(i, 1);
            /* Generate delaf */
            
            String strDelaf = strDelas.replace("delas", "delaf");
            String[] command = new String[]{DictionaryPath.unitexLoggerPath, "MultiFlex",
                strDelas,"-o",strDelaf,"-a",DictionaryPath.alphabetPath,"-d",jTextFieldInflection.getText()};
            Utils.runCommandTerminal(command);
            
            /* compress delaf */
            String[] command1 = new String[]{DictionaryPath.unitexLoggerPath, "Compress",strDelaf};
            Utils.runCommandTerminal(command1);
        }
        jTextArea1.append("Done\n");
    }

    private void CompressDelaf() throws IOException {
        jTextArea1.append("Compress selected Dela(c)f dictionary to Bin, or compress all Dela(c)f files in selected folder...\n");
        for(int i=0;i<jTable1.getRowCount();i++){
            String strDelaf = (String) jTable1.getModel().getValueAt(i, 1);
            /* compress delaf */
            String[] command1 = new String[]{DictionaryPath.unitexLoggerPath, "Compress",strDelaf};
            Utils.runCommandTerminal(command1);
        }
        jTextArea1.append("Done\n");
    }
}
