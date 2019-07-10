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

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.frames.UnitexInternalFrameManager;
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
    public JTable jtableRes;
    JTable jtableSrc;
    private JFrame frame = new JFrame();
    private JDialog dialog = new JDialog(frame, "Processing data", true);
    private JProgressBar progressBar = new JProgressBar();
    private JLabel l = new JLabel("Please wait while processing data ...");
    private JPanel p = new JPanel();
    private JButton b = new JButton("Cancel");
    
    private final Object lock = new Object();
    
    private enum State{PENDING, DONE};
    
    private static State state;

    public DuplicationFinder(JTable src) {

        jtableSrc = src;
        state = State.PENDING;
        jtableRes = new JTable();
        jtableRes.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Duplicate value", "Lemma", "Fst", "SynSem", "dic"
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
    /*
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
    */
    
    /**
     * This function search the table for any duplicate values and 
     * add it to the jtable that contains the results
     */
    @Override
    protected Integer doInBackground() throws Exception {
        DefaultTableModel tableModel = (DefaultTableModel) jtableRes.getModel();
       
        for (int i = 0; i < jtableSrc.getRowCount() - 1; i++) {
            String lema = jtableSrc.getValueAt(i, 1).toString();
            String fst = jtableSrc.getValueAt(i, 2).toString();
            String SynSem = jtableSrc.getValueAt(i, 3).toString();
            String dic = jtableSrc.getValueAt(i, 7).toString();
           /* String[] SynSems = SynSem.split("(=)|(\\+=)");*/
            for (int j = i + 1; j < jtableSrc.getRowCount(); j++) {
                String lemaCompare = jtableSrc.getValueAt(j, 1).toString();
                String fstCompare = jtableSrc.getValueAt(j, 2).toString();
                String SynSemCompare = jtableSrc.getValueAt(j, 3).toString();
                String dicCompare = jtableSrc.getValueAt(j, 7).toString();
                /*String[] SynSemCompares = SynSemCompare.split("()=|(\\+=)");*/
                if (lemaCompare.equals(lema) && fstCompare.equals(fst)
                        /*&& areAllTrue(SynSems, SynSemCompares)*/) {
                    Object[] rowCompare = new Object[]{j, lemaCompare, fstCompare, SynSemCompare, dicCompare};
                    Object[] row = new Object[]{i, lema, fst, SynSem, dic};
                    tableModel.addRow(row);
                    tableModel.addRow(rowCompare);
                }
            }
        }
        synchronized(lock){
        	state = State.DONE;        	
            lock.notify();
        }
        jtableRes.repaint();
        return 0;

    }

    @Override
    protected void done() {
        dialog.dispose();
        if(this.jtableRes.getModel().getRowCount()>0) {
            new MenuDuplicate(this.jtableRes);

        	final class CheckFinish implements Runnable{
        		@Override
        		public void run() {
        			synchronized(lock) {
	            		while(!isOver()) {
	            			try {
	            				lock.wait();
	            			}catch(InterruptedException e) {
	            				System.out.println("Interrupted while waiting for the finder to finish: " + e );
	            				return;
	            			}
	            		}
        			}
	            	if(jtableRes.getModel().getRowCount()>0) {
	            		GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
	            			.newDuplicateOutput(getResult(), true);
	            	}else {
	            		System.out.println("ELSE ");
	            	}
        			
                }
        	}
        	
        	Thread t = new Thread(new CheckFinish());
        	t.start();
        } else {
            JOptionPane.showMessageDialog(null, "No duplication where found !", "Duplication", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public JTable getResult() {
    	return jtableRes;
    }

	private boolean isOver() {
		if(state == State.DONE)
			return true;
		else{
			return false;	
		}
	}

}
