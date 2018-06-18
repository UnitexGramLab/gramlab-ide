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
package fr.umlv.unitex.leximir.delac;

import javax.swing.JFrame;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.config.PreferencesManager;
import fr.umlv.unitex.files.PersonalFileFilter;
import fr.umlv.unitex.frames.UnitexInternalFrameManager;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import fr.umlv.unitex.leximir.model.DictionaryPath;
import java.io.FilenameFilter;


/**
 * @author Rojo Rabelisoa
 * @author Anas Ait cheikh
 */
public class ChooseDelac extends javax.swing.JInternalFrame {

    /**
     * Creates new form ChooseDelac
     */
    File lastLink;

    public ChooseDelac() {
        super("Edit Delac", true, true, true, true);
        initComponents();
        jRadioallDelac.setSelected(true);
        buttonGroup1.add(jRadioallDelac);
        buttonGroup1.add(jRadioBrowse);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jTextField1.setText(PreferencesManager.getUserPreferences().getRecentDelac());
        if (!jTextField1.getText().equals("")) {
            jRadioBrowse.setSelected(true);
        }

    }

    private void updateLinks(boolean all) {
        if (!all) {
            PreferencesManager.getUserPreferences().setRecentDelac(jTextField1.getText());
        } else {
            PreferencesManager.getUserPreferences().setRecentDelac("");
        }
    }

    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jRadioallDelac = new javax.swing.JRadioButton();
        jRadioBrowse = new javax.swing.JRadioButton();
        jButtonOpen = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jBrowseButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jRadioallDelac.setText("Open all Delac in Delac folder");

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
                                                        .addComponent(jRadioallDelac)))
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
                                                .addComponent(jRadioallDelac)
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
        boolean all = jRadioallDelac.isSelected();
        updateLinks(all);
        if (all) {
            File allDFoler = new File(DictionaryPath.allDelac);

            if (allDFoler.exists()) {

                FilenameFilter filter = new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".dic");
                    }
                };

                if (allDFoler.list(filter).length == 0) {
                    JOptionPane.showMessageDialog(null, "No Delac was Found in : " + DictionaryPath.allDelac, "Information", JOptionPane.INFORMATION_MESSAGE);

                } else {
                    GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class
                    )
                            .newEditorDelacDialog(all, null);
                    this.setVisible(false);
                }
            } else {
                int reply = JOptionPane.showConfirmDialog(null, "No Delac folder found in Dela folder \nCreate Delac folder?", title,
                        JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    allDFoler.mkdirs();
                    if (allDFoler.exists()) {
                        JOptionPane.showMessageDialog(null, "The folder created successfully !", "Information", JOptionPane.INFORMATION_MESSAGE);

                    }

                    GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class
                    )
                            .newEditorDelacDialog(all, null);
                    this.setVisible(false);
                } else {
                    this.jRadioallDelac.setEnabled(false);
                    this.jRadioBrowse.setSelected(true);

                }
            }
        } else {
            GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class
            )
                    .newEditorDelacDialog(all, new File(jTextField1.getText()));
            this.setVisible(false);
        }

    }

    private void jButtonBrowseActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        PersonalFileFilter filter = new PersonalFileFilter("dic", "*.dic");
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new File(DictionaryPath.allDela));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            jTextField1.setText(selectedFile.getAbsolutePath());
            jRadioBrowse.setSelected(true);
        }
    }

    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jBrowseButton;
    private javax.swing.JButton jButtonOpen;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioallDelac;
    private javax.swing.JRadioButton jRadioBrowse;
    private javax.swing.JTextField jTextField1;

}
