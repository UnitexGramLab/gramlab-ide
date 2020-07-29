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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.leximir.model.DictionaryPath;

/**
 * @author Anas Ait cheikh
 */
public class CsvOpener extends javax.swing.JInternalFrame {

    private final String csvfile;

    public CsvOpener(String csvfile, String title) {
    	
        super(title, true, true, true, true);
        this.csvfile = csvfile;
        initComponents();
        Vector<String> header = this.header();
        Vector<Vector<String>> data = this.read();
        DefaultTableModel tableModel = new DefaultTableModel(data,header);
        
        JTable table = new JTable(tableModel);
        
        RowSorter<DefaultTableModel> sort = new TableRowSorter<>(tableModel);
        table.setRowSorter(sort);
        
        table.setModel(tableModel);
        table.setRowHeight(20);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        JScrollPane scrollpane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setContentPane(scrollpane);
        int h = (data.size() > 20) ? 20 : data.size();
        this.setSize(header.size() * 124, 56 + h * 20);
    }

    private Vector<String> header() {
        try {
            try (BufferedReader sourceFile = new BufferedReader(
                    new InputStreamReader(new FileInputStream(csvfile), ConfigManager.getManager().getEncoding(null).getCharset()))) {
                String s;
                if ((s = sourceFile.readLine()) != null) {
                    return new Vector<String>(Arrays.asList(s.split(";")));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("No file " + csvfile + " was found.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Vector<Vector<String>> read() {
        Vector<Vector<String>> tmp = new Vector<Vector<String>>();
        try {
            try (BufferedReader sourceFile = new BufferedReader(
                    new InputStreamReader(new FileInputStream(csvfile), ConfigManager.getManager().getEncoding(null).getCharset()))) {
                String s;
                for (int i = 1; (s = sourceFile.readLine()) != null; i++) {
                    if (i > 1) {
                        tmp.add(new Vector<String>(Arrays.asList(s.split(";"))));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("No file " + csvfile + " was found.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        sort(tmp);
        
        return tmp;
    }

    private void sort(Vector<Vector<String>> vectors) {
    	Collections.sort(vectors, new Comparator<Vector<String>>(){
            @Override  public int compare(Vector<String> v1, Vector<String> v2) {
                return v1.get(0).compareTo(v2.get(0)); 
        }});
    }
    
    private void initComponents() {
    	
        jScrollPane1 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
        );

        pack();
    }

    private javax.swing.JScrollPane jScrollPane1;
}
