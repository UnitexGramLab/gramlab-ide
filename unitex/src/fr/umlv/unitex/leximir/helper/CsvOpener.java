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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.Arrays;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * @author Anas Ait cheikh
 */
public class CsvOpener extends javax.swing.JInternalFrame {

    private final String csvfile;

    public CsvOpener(String csvfile) {
        super("Statistic on table", true, true, true, true);
        this.csvfile = csvfile;
        initComponents();
        Vector<String> header = this.header();
        Vector<Vector<String>> data = this.read();
        JTable table = new JTable(data, header);
        table.setRowHeight(20);
        JScrollPane scrollpane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setContentPane(scrollpane);
        int h = (data.size() > 20) ? 20 : data.size();
        this.setSize(header.size() * 124, 56 + h * 20);
    }

    private Vector<String> header() {
        try {
            try (BufferedReader sourceFile = new BufferedReader(new FileReader(csvfile))) {
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
            try (BufferedReader sourceFile = new BufferedReader(new FileReader(csvfile))) {
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
        return tmp;
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
