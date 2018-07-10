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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.frames.InternalFrameManager;
import fr.umlv.unitex.frames.UnitexInternalFrameManager;
import fr.umlv.unitex.io.UnicodeIO;
import fr.umlv.unitex.leximir.helper.GridHelper;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import fr.umlv.unitex.leximir.delac.menu.MenuDelac;
import fr.umlv.unitex.leximir.helper.Help;
import fr.umlv.unitex.leximir.model.DictionaryPath;
import fr.umlv.unitex.leximir.util.DuplicationFinder;
import fr.umlv.unitex.leximir.util.Utils;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * @author Rojo Rabelisoa
 * @author Anas Ait cheikh
 */
public final class EditorDelac extends javax.swing.JInternalFrame {

    private DefaultTableModel tableModel;
    private boolean unsaved = false;

    public EditorDelac() {
    }

    /**
     * Creates new form EditorLadl
     *
     * @param alldelac if alldelas is true, the program open all dictionaries in
     * delac folder,
     */
    public EditorDelac(boolean alldelac, File dic) {
        super("LeXimir Editor for Dela dictionaries of compound words", true, true, true, true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        try {
            initComponents();
            DictionaryPath.dictionary.clear();
            this.setTitle("LeXimir Editor for Dela dictionaries of compound words");
            tableModel = GridHelper.getOpenEditorforDelac(alldelac, dic);
            for (String d : DictionaryPath.dictionary) {
                jComboBoxDic.addItem(d);
            }
            JTable table = new JTable(getTableModel());

            RowSorter<DefaultTableModel> sort = new TableRowSorter<>(tableModel);
            this.getjTable1().setRowSorter(sort);
            this.getjTable1().setModel(table.getModel());
            jLabel13.setText(String.valueOf(jTable1.getRowCount()));
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.getjTable1().setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    c.setBackground(row % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE);
                    c.setForeground(Color.black);
                    return c;
                }
            });
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        final JInternalFrame frame = this;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                if (unsaved) {
                    int dialogResult = JOptionPane.showConfirmDialog(null, "You "
                            + "have some unsaved data, do you want to exit?", "Exit Delac dictioneries in unicode", JOptionPane.YES_NO_OPTION);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        frame.dispose();
                    }
                } else {
                    frame.dispose();
                }
            }
        });
    }


    @SuppressWarnings("unchecked")
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldPos = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldLemma = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldFst = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldSynSem = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButtonAll = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jButtonMove = new javax.swing.JButton();
        jComboBoxDic = new javax.swing.JComboBox();
        jCheckBoxExtract = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        jTextFieldComment = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldSearch = new javax.swing.JTextField();
        jButtonSearch = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButtonHelp = new javax.swing.JButton();
        jTextField4 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuNew = new javax.swing.JMenu();
        jMenuItemInsertBefore = new javax.swing.JMenuItem();
        jMenuItemInsertAfter = new javax.swing.JMenuItem();
        jMenuBefore = new javax.swing.JMenu();
        jMenuAfter = new javax.swing.JMenu();
        jMenuEdit = new javax.swing.JMenu();
        jMenuView = new javax.swing.JMenu();
        jMenuDelete = new javax.swing.JMenu();
        jMenuInflect = new javax.swing.JMenu();
        jMenuDuplicate = new javax.swing.JMenu();
        jMenuStatistics = new javax.swing.JMenu();
        jMenuSave = new javax.swing.JMenu();
        jMenuSaveAs = new javax.swing.JMenu();
        jMenuExit = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel2.setText("POS");

        jTextFieldPos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldPosKeyPressed(evt);
            }
        });

        jLabel1.setText("Cp Lemma");

        jTextFieldLemma.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldLemmaKeyPressed(evt);
            }
        });

        jLabel3.setText("FST");

        jTextFieldFst.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldFstKeyPressed(evt);
            }
        });

        jLabel4.setText("SynSem");

        jTextFieldSynSem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldSynSemKeyPressed(evt);
            }
        });

        jButton2.setText("Open graph");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButtonAll.setText("Statistics on table");
        jButtonAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAllActionPerformed(evt);
            }
        });

        jLabel6.setText("Move all entries in table to :");

        jButtonMove.setText("Move");
        jButtonMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMoveActionPerformed(evt);
            }
        });

        jComboBoxDic.setModel(new javax.swing.DefaultComboBoxModel(new String[]{}));

        jCheckBoxExtract.setText("Exact match");

        jLabel14.setText("Comment");

        jTextFieldComment.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldCommentKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addGap(44, 44, 44)
                                                .addComponent(jLabel1))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jTextFieldPos, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextFieldLemma, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel3)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jTextFieldFst, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton2)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel4)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jTextFieldSynSem, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButtonAll)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel14)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jTextFieldComment, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jCheckBoxExtract)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel6)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jComboBoxDic, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButtonMove)))
                                .addGap(76, 76, 76))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel4)
                                        .addComponent(jLabel6)
                                        .addComponent(jLabel14))
                                .addGap(12, 12, 12)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jTextFieldPos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTextFieldLemma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTextFieldFst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTextFieldSynSem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton2)
                                        .addComponent(jButtonAll)
                                        .addComponent(jButtonMove)
                                        .addComponent(jComboBoxDic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jCheckBoxExtract)
                                        .addComponent(jTextFieldComment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null}
                },
                new String[]{
                    "Title 1", "Title 2", "Title 3", "Title 4"
                }
        )
        );
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1)
                                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE))
        );

        jLabel7.setText("Search : ");

        jButtonSearch.setText("Search");
        jButtonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchActionPerformed(evt);
            }
        });

        jLabel12.setText("No of lines");

        jLabel13.setText("jLabel13");

        jLabel8.setText("POS");

        jLabel9.setText("CLemma");

        jLabel10.setText("FST Code");

        jLabel5.setText("SynSem");

        jButton1.setText("Clear");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButtonHelp.setText("Help");
        jButtonHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHelpActionPerformed(evt);
            }
        });

        jButton4.setText("Search multicriteria");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel11.setText("All columns");

        jLabel15.setText("Comment");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jTextFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButtonSearch))
                                        .addComponent(jLabel11))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(26, 26, 26)
                                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel8)
                                                .addGap(86, 86, 86)
                                                .addComponent(jLabel9)
                                                .addGap(31, 31, 31)
                                                .addComponent(jLabel10)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel5)
                                                .addGap(0, 14, Short.MAX_VALUE))
                                        .addComponent(jTextField4))
                                .addGap(27, 27, 27)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel15)
                                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(26, 26, 26)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jButton1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButtonHelp))
                                        .addComponent(jButton4))
                                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jButton1)
                                                .addComponent(jButtonHelp)
                                                .addComponent(jLabel11))
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jLabel8)
                                                .addComponent(jLabel9)
                                                .addComponent(jLabel10)
                                                .addComponent(jLabel5)
                                                .addComponent(jLabel15)))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(8, 8, 8)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(jLabel12)
                                                                .addComponent(jLabel13))
                                                        .addComponent(jButton4)
                                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(jLabel7)
                                                                .addComponent(jTextFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jButtonSearch))))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(24, 24, 24)
                                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(45, 45, 45))
        );

        jScrollPane2.setViewportView(jPanel1);

        jMenuNew.setText("New");
        jMenuNew.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuNewMouseClicked(evt);
            }
        });

        jMenuItemInsertBefore.setText("insert before");
        jMenuItemInsertBefore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemInsertActionPerformed(evt,"insertBefore");
            }
        });
        jMenuNew.add(jMenuItemInsertBefore);

        jMenuItemInsertAfter.setText("insert after");
        jMenuItemInsertAfter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemInsertActionPerformed(evt,"insertAfter");
            }
        });
        jMenuNew.add(jMenuItemInsertAfter);

        jMenuBar1.add(jMenuNew);

        jMenuBefore.setText("Copy before");
        jMenuBefore.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuBeforeAfterMouseClicked(evt,"copyBefore");
            }
        });
        jMenuBar1.add(jMenuBefore);

        jMenuAfter.setText("Copy after");
        jMenuAfter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuBeforeAfterMouseClicked(evt,"copyAfter");
            }
        });
        jMenuBar1.add(jMenuAfter);

        jMenuEdit.setText("Edit");
        jMenuEdit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuViewEditMouseClicked(evt,"edit");
            }
        });
        jMenuBar1.add(jMenuEdit);

        jMenuView.setText("View");
        jMenuView.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuViewEditMouseClicked(evt,"view");
            }
        });
        jMenuBar1.add(jMenuView);

        jMenuDelete.setText("Delete");
        jMenuDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuDeleteMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenuDelete);

        jMenuInflect.setText("Inflect");
        jMenuInflect.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuInflectMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenuInflect);

        jMenuDuplicate.setText("Check duplicate");
        jMenuDuplicate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuDuplicateMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenuDuplicate);

        jMenuStatistics.setText("Statistics");
        jMenuStatistics.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuStatisticsMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenuStatistics);

        jMenuSave.setText("Save");
        jMenuSave.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuSaveMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenuSave);

        jMenuSaveAs.setText("Save as...");
        jMenuSaveAs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuSaveAsMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenuSaveAs);

        jMenuExit.setText("Exit");
        jMenuExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuExitMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenuExit);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane2))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
        );

        pack();
    }

    private void jButtonSearchActionPerformed(java.awt.event.ActionEvent evt) {
        String text = jTextFieldSearch.getText();
        TableRowSorter<DefaultTableModel> rowSorter;
        rowSorter = new TableRowSorter<>(tableModel);
        this.getjTable1().setRowSorter(rowSorter);
        this.getjTable1().removeAll();
        if (text.length() == 0) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
        jTable1.setModel(rowSorter.getModel());
        jLabel13.setText(String.valueOf(this.getjTable1().getRowCount()));

    }

    private void jMenuNewMouseClicked(java.awt.event.MouseEvent evt) {

    }

    private void jMenuStatisticsMouseClicked(java.awt.event.MouseEvent evt) {

        Map<String, HashMap<String, String>> data = new HashMap<>();
        for (int i = 0; i < this.getjTable1().getRowCount(); i++) {
            String dic = (String) this.getjTable1().getValueAt(i, 7);
            String value = (String) this.getjTable1().getValueAt(i, 0);
            if (!data.containsKey(dic)) {
                data.put(dic, new HashMap<String, String>());
            } else {
                if (data.get(dic).containsKey(value)) {
                    int count = Integer.parseInt(data.get(dic).get(value)) + 1;
//                    data.get(dic).replace(value, String.valueOf(count));
                    if (data.get(dic).containsKey(value)) {
                        data.get(dic).put(value, String.valueOf(count));
                    }
                } else {
                    data.get(dic).put(value, "1");
                }
            }
        }

        List<Object[]> dicPos = new ArrayList<Object[]>();
        dicPos.add(new Object[]{"Dic", "POS", "Number"});
        for (Map.Entry<String, HashMap<String, String>> f : data.entrySet()) {
            String key = f.getKey();
            for (Map.Entry<String, String> p : f.getValue().entrySet()) {
                dicPos.add(new Object[]{key, p.getKey(), p.getValue()});

            }
        }

        GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
                .newStatisticOutput(dicPos);
    }

    private void jMenuDeleteMouseClicked(java.awt.event.MouseEvent evt) {
        int dialogResult = JOptionPane.showConfirmDialog(null, "Do you want to delete this row?", "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            int t = this.getjTable1().getSelectedRow();
            this.getTableModel().removeRow(t);
            JOptionPane.showMessageDialog(null, "Row deleted !");
            this.setUnsaved(true);
        }
    }


    private void jMenuItemInsertActionPerformed(java.awt.event.ActionEvent evt, String flag) {//insertAfter/insertBefore
            if (this.getjTable1().getSelectedRow() != -1) {
                Object[] obj = new Object[8];
                for (int i = 0; i < 8; i++) {
                    obj[i] = this.getjTable1().getValueAt(this.getjTable1().getSelectedRow(), i);
                }
                 MenuDelac ad = new MenuDelac(this, flag, obj, this.getjTable1().getSelectedRow());
                ad.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "No selected value");
            }      
    }

    private void jMenuViewEditMouseClicked(java.awt.event.MouseEvent evt, String flag) {
            if (this.getjTable1().getSelectedRow() != -1) {
                Object[] obj = new Object[8];
                for (int i = 0; i < 8; i++) {
                    obj[i] = this.getjTable1().getValueAt(this.getjTable1().getSelectedRow(), i);
                }
            MenuDelac ad = new MenuDelac(this, flag, obj, this.getjTable1().getSelectedRow());
                ad.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "No selected value");
            }
        } 
    
    private void jMenuBeforeAfterMouseClicked(java.awt.event.MouseEvent evt, String flag) {//copyBefore/copyAfter
            if (this.getjTable1().getSelectedRow() != -1) {
                Object[] obj = new Object[8];
                for (int i = 0; i < 8; i++) {
                    obj[i] = this.getjTable1().getValueAt(this.getjTable1().getSelectedRow(), i);
                }
            MenuDelac ad = new MenuDelac(this, flag, obj, this.getjTable1().getSelectedRow());
                ad.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "No selected value");
            }
        } 

    private void jMenuSaveMouseClicked(java.awt.event.MouseEvent evt) {
        int dialogResult = JOptionPane.showConfirmDialog(null, "This will overwrite your dictionaries. Are you sure?", "Save Delac Dictioneries in Unicode", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            Map<String, List<String>> fileData = new HashMap<>();
            for (int row = 0; row < tableModel.getRowCount(); row++) {

                String file = (String) tableModel.getValueAt(row, 7);
                String lemma = (String) tableModel.getValueAt(row, 1);
                String fstCode = tableModel.getValueAt(row, 3).toString().concat(tableModel.getValueAt(row, 4).toString());
                //String SynSem = ((String) tableModel.getValueAt(row, 4));
                String str = lemma + "," + fstCode;
                String comment = (String) tableModel.getValueAt(row, 5);
                if (comment != null && comment.trim().length() > 0) {
                    str = str + "/" + tableModel.getValueAt(row, 5);
                }
                str = str + "\n";
                if (fileData.containsKey(file)) {
                    fileData.get(file).add(str);
                } else {
                    List<String> tmp = new ArrayList<>();
                    tmp.add(str);
                    fileData.put(file, tmp);
                }
            }
            for (Map.Entry<String, List<String>> data : fileData.entrySet()) {
                try {
                    String fn=data.getKey().contains(File.separator)?new File(data.getKey()).getName():data.getKey();
                    try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(DictionaryPath.allDelac + File.separator + fn))) {
                        for (String lines : data.getValue()) {
                            UnicodeIO.writeString(out,lines);
                        }
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "error :" + ex.getMessage());
                }
            }
            this.setUnsaved(false);
            JOptionPane.showMessageDialog(null, "Files where saved successfully");
        }
    }

    private void jMenuExitMouseClicked(java.awt.event.MouseEvent evt) {
        if (this.getUnsaved()) {
            int dialogResult = JOptionPane.showConfirmDialog(null, "You "
                    + "have some unsaved data, do you want to exit?", "exit Delac dictioneries in unicode", JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.YES_OPTION) {
                this.setVisible(false);
            }

        } else {
            this.setVisible(false);
        }
    }

    private void jMenuInflectMouseClicked(java.awt.event.MouseEvent evt) {
        if (this.getjTable1().getSelectedRow() != -1) {

            try {
                String lemma = (String) this.getjTable1().getValueAt(this.getjTable1().getSelectedRow(), 1);
                String fst = (String) this.getjTable1().getValueAt(this.getjTable1().getSelectedRow(), 3);
                Utils.InflectDelas(lemma, fst);
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "error :" + ex.getMessage());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "error :" + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "No selected value");
        }

    }

    private void jButtonAllActionPerformed(java.awt.event.ActionEvent evt) {

        Map<String, List<String>> data = new HashMap<>();
        Map<String, HashMap<String, HashMap<String, String>>> dataForSynSem2 = new HashMap<>();
        for (int i = 0; i < this.getjTable1().getRowCount(); i++) {
            String SynSem = (String) this.getjTable1().getValueAt(i, 4);
            String pos = (String) this.getjTable1().getValueAt(i, 0);
            if (!data.containsKey(pos)) {
                List<String> symSem = new ArrayList<>();
                String[] tmp = SynSem.split("=")[0].split(Pattern.quote("+"));
                symSem.addAll(Arrays.asList(tmp));
                data.put(pos, symSem);
            } else {
                List<String> valueInData = data.get(pos);
                String[] tmp = SynSem.split("=")[0].split(Pattern.quote("+"));
                valueInData.addAll(Arrays.asList(tmp));
                data.put(pos, valueInData);
            }
            String SynSemForPos = (String) this.getjTable1().getValueAt(i, 4);
            String[] domain = SynSemForPos.split("=")[0].split(Pattern.quote("+"));

            String realSynSem = domain[domain.length - 1];

            String domainCategory = "";
            try {
                domainCategory = SynSemForPos.split("=")[1].split(Pattern.quote("+"))[0];
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                try {
                    domainCategory = SynSemForPos.split("=")[1];
                } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                    domainCategory = SynSemForPos;
                }
            }
            if (!dataForSynSem2.containsKey(pos)) {
                dataForSynSem2.put(pos, new HashMap<String, HashMap<String, String>>());
                dataForSynSem2.get(pos).put(realSynSem, new HashMap<String, String>());
                dataForSynSem2.get(pos).get(realSynSem).put(domainCategory, "1");
            } else {
                if (!dataForSynSem2.get(pos).containsKey(realSynSem)) {
                    dataForSynSem2.get(pos).put(realSynSem, new HashMap<String, String>());
                    dataForSynSem2.get(pos).get(realSynSem).put(domainCategory, "1");
                } else {
                    if (!dataForSynSem2.get(pos).get(realSynSem).containsKey(domainCategory)) {
                        dataForSynSem2.get(pos).get(realSynSem).put(domainCategory, "1");
                    } else {
                        int count = Integer.parseInt(dataForSynSem2.get(pos).get(realSynSem).get(domainCategory)) + 1;
                        if (dataForSynSem2.get(pos).get(realSynSem).containsKey(domainCategory)) {
                            dataForSynSem2.get(pos).get(realSynSem).put(domainCategory, String.valueOf(count));
                        }
                    }
                }
            }
        }

        Map<String, Object[]> statSimSem = new HashMap<>();
        int v = 2;
        for (Map.Entry<String, HashMap<String, HashMap<String, String>>> t : dataForSynSem2.entrySet()) {
            String key = t.getKey();
            for (Map.Entry<String, HashMap<String, String>> y : t.getValue().entrySet()) {
                for (Map.Entry<String, String> u : y.getValue().entrySet()) {
                    statSimSem.put(String.valueOf(v), new Object[]{key, y.getKey(), u.getKey(), u.getValue()});
                    v++;
                }
            }
        }

        GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
                .newStatisticOutput(statSimSem);

    }

    private void jTextFieldPosKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            JTextField textField = (JTextField) evt.getSource();
            String text = textField.getText();
            TableRowSorter<DefaultTableModel> rowSorter;
            rowSorter = new TableRowSorter<>(tableModel);
            this.getjTable1().setRowSorter(rowSorter);
            this.getjTable1().removeAll();
            if (text.trim().length() == 0) {
                rowSorter.setRowFilter(null);
            } else {
                if (jCheckBoxExtract.isSelected()) {
                    text = "^" + text + "$";
                } else {
                    if (!text.contains(".") && !text.contains("$")) {
                        text = "^" + text;
                    }
                }
                RowFilter<Object,Object> rowFilter = RowFilter.regexFilter("(?i)" + text, 0);// search column at index 0
                rowSorter.setRowFilter(rowFilter);
            }
            jTable1.setModel(rowSorter.getModel());
            jLabel13.setText(String.valueOf(this.getjTable1().getRowCount()));
        }
    }

    private void jTextFieldLemmaKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            JTextField textField = (JTextField) evt.getSource();
            String text = textField.getText();
            TableRowSorter<DefaultTableModel> rowSorter;
            rowSorter = new TableRowSorter<>(tableModel);
            this.getjTable1().setRowSorter(rowSorter);
            this.getjTable1().removeAll();
            if (text.trim().length() == 0) {
                rowSorter.setRowFilter(null);
            } else {
                //String regex = String.format("^%s$", text);
                if (jCheckBoxExtract.isSelected()) {
                    text = "^" + text + "$";
                } else {
                    if (!text.contains(".") && !text.contains("$")) {
                        text = "^" + text;
                    }

                }
                RowFilter <Object,Object> rowFilter = RowFilter.regexFilter(text, 2);// search column at index 0
                rowSorter.setRowFilter(rowFilter);
            }
            jTable1.setModel(rowSorter.getModel());
            jLabel13.setText(String.valueOf(this.getjTable1().getRowCount()));
        }
    }

    private void jTextFieldFstKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            JTextField textField = (JTextField) evt.getSource();
            String text = textField.getText();
            TableRowSorter<DefaultTableModel> rowSorter;
            rowSorter = new TableRowSorter<>(tableModel);
            this.getjTable1().setRowSorter(rowSorter);
            this.getjTable1().removeAll();
            if (text.trim().length() == 0) {
                rowSorter.setRowFilter(null);
            } else {
                if (jCheckBoxExtract.isSelected()) {
                    text = "^" + text + "$";
                }
                RowFilter <Object,Object> rowFilter = RowFilter.regexFilter(text, 3);// search column at index 0
                rowSorter.setRowFilter(rowFilter);
            }
            jTable1.setModel(rowSorter.getModel());
            jLabel13.setText(String.valueOf(this.getjTable1().getRowCount()));
        }
    }

    private void jTextFieldSynSemKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            JTextField textField = (JTextField) evt.getSource();
            String text = textField.getText();
            TableRowSorter<DefaultTableModel> rowSorter;
            rowSorter = new TableRowSorter<>(tableModel);
            this.getjTable1().setRowSorter(rowSorter);
            this.getjTable1().removeAll();
            if (text.trim().length() == 0) {
                rowSorter.setRowFilter(null);
            } else {
                if (jCheckBoxExtract.isSelected()) {
                    text = "^" + text + "$";
                }
                RowFilter <Object,Object> rowFilter = RowFilter.regexFilter(text, 4);// search column at index 0
                rowSorter.setRowFilter(rowFilter);
            }
            jTable1.setModel(rowSorter.getModel());
            jLabel13.setText(String.valueOf(this.getjTable1().getRowCount()));
        }
    }

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
        String pos = jTextField1.getText();
        String lemma = jTextField2.getText();
        String fst = jTextField3.getText();
        String SynSem = jTextField4.getText();
        String comment = jTextField5.getText();
        TableRowSorter<DefaultTableModel> rowSorter;
        rowSorter = new TableRowSorter<>(tableModel);
        this.getjTable1().setRowSorter(rowSorter);
        this.getjTable1().removeAll();
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        if (pos.length() != 0) {
            filters.add(RowFilter.regexFilter(pos, 0));
        }
        if (lemma.length() != 0) {
            filters.add(RowFilter.regexFilter(lemma, 2));
        }
        if (fst.length() != 0) {
            filters.add(RowFilter.regexFilter(fst, 3));
        }
        if (SynSem.length() != 0) {
            filters.add(RowFilter.regexFilter(SynSem, 4));
        }
        if (comment.length() != 0) {
            filters.add(RowFilter.regexFilter(comment, 5));
        }
        RowFilter rf = RowFilter.andFilter(filters);
        rowSorter.setRowFilter(rf);
        jTable1.setModel(rowSorter.getModel());
        jLabel13.setText(String.valueOf(this.getjTable1().getRowCount()));
    }

    private void jButtonMoveActionPerformed(java.awt.event.ActionEvent evt) {
        String dic = (String) jComboBoxDic.getSelectedItem();
        for (int i = 0; i < this.getjTable1().getRowCount(); i++) {
            tableModel.setValueAt(dic, i, 7);
        }
        JOptionPane.showMessageDialog(null, "there are " + this.getjTable1().getRowCount() + " to move to " + dic);

    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        int index=this.getjTable1().getSelectedRow()!= -1?this.getjTable1().getSelectedRow():0 ;
        
            String filename = DictionaryPath.inflectionPath + this.getjTable1().getValueAt(index, 2) + ".grf";

            final File[] graphs = new File[1];
            graphs[0] = new File(filename);
            for (int i = 0; i < graphs.length; i++) {
                String s = graphs[i].getAbsolutePath();
                if (!graphs[i].exists() && !s.endsWith(".grf")) {
                    s = s + ".grf";
                    graphs[i] = new File(s);
                    if (!graphs[i].exists()) {
                        JOptionPane.showMessageDialog(null,
                                "File " + graphs[i].getAbsolutePath()
                                + " does not exist", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                }
                GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
                        .newGraphFrame(graphs[i]);
            }
     
    }

    private void jMenuDuplicateMouseClicked(java.awt.event.MouseEvent evt) {
        new DuplicationFinder(this.getjTable1()).execute();

    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        TableRowSorter<DefaultTableModel> rowSorter;
        rowSorter = new TableRowSorter<>(tableModel);
        rowSorter.setRowFilter(null);
        this.getjTable1().setRowSorter(rowSorter);
        this.getjTable1().removeAll();
        this.getjTable1().repaint();
        jLabel13.setText(String.valueOf(this.getjTable1().getRowCount()));
    }

    private void jButtonHelpActionPerformed(java.awt.event.ActionEvent evt) {
        Help help = new Help();
        help.setVisible(true);
    }

    private void jTextFieldCommentKeyPressed(java.awt.event.KeyEvent evt) {
        TableRowSorter<DefaultTableModel> rowSorter = null;
        try {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                JTextField textField = (JTextField) evt.getSource();
                String text = textField.getText();

                rowSorter = new TableRowSorter<>(tableModel);

                this.getjTable1().setRowSorter(rowSorter);

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    if (jCheckBoxExtract.isSelected()) {
                        text = "^" + text + "$";
                    } else {
                        if (!text.contains(".") && !text.contains("$")) {
                            text = "^" + text;
                        }
                    }
                    RowFilter <Object,Object> rowFilter = RowFilter.regexFilter(text, 5);// search column at index 4
                    rowSorter.setRowFilter(rowFilter);
                }
                jTable1.setModel(rowSorter.getModel());
                jLabel13.setText(String.valueOf(this.getjTable1().getRowCount()));
            }
        } catch (java.util.regex.PatternSyntaxException e) {
            rowSorter.setRowFilter(null);
        }
    }

    private void jMenuSaveAsMouseClicked(java.awt.event.MouseEvent evt) {
        File file = null;
        String path = "";
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save");
        chooser.setApproveButtonText("Save");
        chooser.setCurrentDirectory(new File(DictionaryPath.allDela));
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            path = file.getPath();
            try {
                String filename = path.endsWith(".dic")?path:path+".dic";
                OutputStreamWriter out= new OutputStreamWriter(new FileOutputStream(filename));                
                for (int row = 0; row < this.getjTable1().getRowCount(); row++) {
                    String lemma = String.valueOf(this.getjTable1().getValueAt(row, 1));
                    String fstCode = this.getjTable1().getValueAt(row, 3).toString().concat(this.getjTable1().getValueAt(row, 4).toString());
                    String str = lemma + "," + fstCode;
                    String comment = String.valueOf(this.getjTable1().getValueAt(row, 5));
                    if (comment != null && comment.trim().length() > 0) {
                        str = str + "/" + this.getjTable1().getValueAt(row, 5);
                    }
                    str = str + "\n";
                    UnicodeIO.writeString(out,str);

                }
                out.close();
                JOptionPane.showMessageDialog(null, "Files where saved successfully");
            } catch (IOException ex) {
                Logger.getLogger(EditorDelac.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButtonAll;
    private javax.swing.JButton jButtonHelp;
    private javax.swing.JButton jButtonMove;
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JCheckBox jCheckBoxExtract;
    private javax.swing.JComboBox<String> jComboBoxDic;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenuAfter;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuBefore;
    private javax.swing.JMenu jMenuDelete;
    private javax.swing.JMenu jMenuDuplicate;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuExit;
    private javax.swing.JMenu jMenuInflect;
    private javax.swing.JMenuItem jMenuItemInsertAfter;
    private javax.swing.JMenuItem jMenuItemInsertBefore;
    private javax.swing.JMenu jMenuNew;
    private javax.swing.JMenu jMenuSave;
    private javax.swing.JMenu jMenuSaveAs;
    private javax.swing.JMenu jMenuStatistics;
    private javax.swing.JMenu jMenuView;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextFieldComment;
    private javax.swing.JTextField jTextFieldFst;
    private javax.swing.JTextField jTextFieldLemma;
    private javax.swing.JTextField jTextFieldPos;
    private javax.swing.JTextField jTextFieldSearch;
    private javax.swing.JTextField jTextFieldSynSem;

    public javax.swing.JLabel getJLablel13() {
        return this.jLabel13;
    }

    /**
     * @return the jTable1
     */
    public javax.swing.JTable getjTable1() {
        return jTable1;
    }

    /**
     * @return the gm
     */
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public boolean getUnsaved() {
        return unsaved;
    }

    public void setUnsaved(boolean value) {
        this.unsaved = value;
    }
}
