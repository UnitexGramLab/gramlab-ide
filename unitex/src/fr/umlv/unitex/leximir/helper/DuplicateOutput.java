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
package fr.umlv.unitex.leximir.helper;

import javax.swing.JFrame;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import fr.umlv.unitex.leximir.delac.EditorDelac;
import fr.umlv.unitex.leximir.delas.EditorDelas;
import fr.umlv.unitex.leximir.model.DictionaryPath;
import fr.umlv.unitex.leximir.util.Utils;

public class DuplicateOutput extends javax.swing.JInternalFrame  {
	
	private JTable results;
	private final boolean isDelas;

	public DuplicateOutput(JTable results, boolean isDelas) {
		super("Set output folder", true, true, true, true);
		this.isDelas =isDelas;
		initComponents();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.results = results;
	}

	private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jButtonSet = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        
        String outputFile;
        
        if(isDelas == true) {
        	outputFile = EditorDelas.getStatisticDirectory() + DictionaryPath.duplicatesTmpPath;
        }else {
        	outputFile = EditorDelac.getStatisticDirectory() + DictionaryPath.duplicatesTmpPath;
        }
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTextField1.setText(outputFile); 

        jButtonSet.setText("Set");
        jButtonSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSetActionPerformed(evt);
            }
        });

        jButtonSave.setText("Save");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        
        jLabel2.setText("Select output folder to save the duplicates");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(143, 143, 143)
                        .addComponent(jButtonSave))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonSet))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSet))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addComponent(jButtonSave)
                .addGap(32, 32, 32))
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
	}
	
	
	
	
	
    private void jButtonSetActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
	        File selectedFile = fileChooser.getSelectedFile();
	        if(isDelas) {
	        	EditorDelas.setStatisticDirectory(selectedFile.getAbsolutePath()+File.separator);
	        	jTextField1.setText(EditorDelas.getStatisticDirectory() + DictionaryPath.duplicatesTmpPath);
	        }else {
	        	EditorDelac.setStatisticDirectory(selectedFile.getAbsolutePath()+File.separator);
		        jTextField1.setText(EditorDelac.getStatisticDirectory() + DictionaryPath.duplicatesTmpPath);
	        }
	        
        }
    }

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {
    	try{   
        	String filename= jTextField1.getText();
        	File saveFolder = new File(new File(filename).getParent());
        	if(!saveFolder.exists()) {
        		 int reply = JOptionPane.showConfirmDialog(null, "This folder doesn't exist yet \nCreate this folder?", title,
                         JOptionPane.YES_NO_OPTION);
                 if (reply == JOptionPane.YES_OPTION) {
                     saveFolder.mkdirs();
                     if (saveFolder.exists()) {
                         JOptionPane.showMessageDialog(null, "The folder created successfully !", "Information", JOptionPane.INFORMATION_MESSAGE);
                     }
                 }
        	}
	        if(this.results!=null){
	        	Utils.exportDatasInDictionary(results, new File(filename));
	        }else{
	        	JOptionPane.showMessageDialog(null, "Error : nothing to save");
	        }
	        this.setVisible(false);
        } catch (IOException ex) {
           JOptionPane.showMessageDialog(null, "Error : "+ex.getMessage());
        }
    }
	

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonSet;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
