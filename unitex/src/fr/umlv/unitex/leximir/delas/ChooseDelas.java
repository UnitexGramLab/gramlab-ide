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
package fr.umlv.unitex.leximir.delas;

import javax.swing.JFrame;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.frames.UnitexInternalFrameManager;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import fr.umlv.unitex.leximir.model.DictionaryPath;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Rojo Rabelisoa
 * @author Anas Ait cheikh
 */
public class ChooseDelas extends javax.swing.JInternalFrame {

    /**
     * Creates new form ChooseDelas
     */
    File lastLink;

    public ChooseDelas() {
        super("Edit Delas", true, true, true, true);
        initComponents();
        jRadioAllDelas.setSelected(true);
        buttonGroup1.add(jRadioAllDelas);
        buttonGroup1.add(jRadioBrowse);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        lastLink = new File(DictionaryPath.allDela + "/tmp_delas.txt");
        if (lastLink.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(lastLink));
                jTextField1.setText(br.readLine());
                if (!jTextField1.getText().equals("")) {
                    jRadioBrowse.setSelected(true);
                }
            } catch (IOException ex) {
                Logger.getLogger(ChooseDelas.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void updateLinks(boolean all) {
        try {
            FileWriter bw = new FileWriter(lastLink);
            if (!all) {
                bw.write(jTextField1.getText() + "\r\n");
                bw.close();
            } else {
                bw.write("");
                bw.close();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ChooseDelas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ChooseDelas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jRadioAllDelas = new javax.swing.JRadioButton();
        jRadioBrowse = new javax.swing.JRadioButton();
        jButtonOpen = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jBrowseButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jRadioAllDelas.setText("Open all Delas in Delas folder");

        jButtonOpen.setText("Open");
        jButtonOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenActionPerformed(evt);
            }
        });

        jBrowseButton.setText("Browse");
        jBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(27, 27, 27)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(jRadioBrowse)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jBrowseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jRadioAllDelas)))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(128, 128, 128)
                                                .addComponent(jButtonOpen)))
                                .addContainerGap(68, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jRadioAllDelas)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jRadioBrowse))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(26, 26, 26)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jBrowseButton))))
                                .addGap(18, 18, 18)
                                .addComponent(jButtonOpen)
                                .addContainerGap(38, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenActionPerformed
        boolean all = jRadioAllDelas.isSelected();
        updateLinks(all);
        if (all) {
            File allDFoler = new File(DictionaryPath.allDelas);
            if (allDFoler.exists()) {

                FilenameFilter filter = new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".dic");
                    }
                };

                if (allDFoler.list(filter).length == 0) {
                    JOptionPane.showMessageDialog(null, "No Delas was Found in : " + DictionaryPath.allDelas, "Information", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
                            .newEditorDelasDialog(all, null);
                    this.setVisible(false);
                }
            } else {
                int reply = JOptionPane.showConfirmDialog(null, "No Delas folder found in Dela folder \nCreate Delas folder?", title,
                        JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    allDFoler.mkdirs();
                    if (allDFoler.exists()) {
                        JOptionPane.showMessageDialog(null, "The folder created successfully !", "Information", JOptionPane.INFORMATION_MESSAGE);
                    }

                    GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
                            .newEditorDelasDialog(all, null);
                    this.setVisible(false);
                } else {
                    this.jRadioAllDelas.setEnabled(false);
                    this.jRadioBrowse.setSelected(true);
                }
            }
        } else {
            GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
                    .newEditorDelasDialog(all, new File(jTextField1.getText()));
            this.setVisible(false);
        }

    }//GEN-LAST:event_jButtonOpenActionPerformed

    private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.dic", "dic");
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new File(DictionaryPath.allDela));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            jTextField1.setText(selectedFile.getAbsolutePath());
            jRadioBrowse.setSelected(true);
        }

    }//GEN-LAST:event_jButtonBrowseActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChooseDelas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChooseDelas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChooseDelas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChooseDelas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ChooseDelas().setVisible(true);
            }
        });
    }

    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jBrowseButton;
    private javax.swing.JButton jButtonOpen;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioAllDelas;
    private javax.swing.JRadioButton jRadioBrowse;
    private javax.swing.JTextField jTextField1;

}
