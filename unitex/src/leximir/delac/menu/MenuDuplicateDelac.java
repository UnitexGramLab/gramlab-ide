/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leximir.delac.menu;

import leximir.delas.menu.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import leximir.EditorDelac;
import leximir.EditorDelas;

/**
 *
 * @author rojo
 */
public class MenuDuplicateDelac extends javax.swing.JFrame {

    private EditorDelac editorDelac;
    private DefaultTableModel tableModel;

    /**
     * Creates new form MenuDuplicateDelas
     */
    public MenuDuplicateDelac() {
        //super("Duplicate value", true, true, true, true);
        initComponents();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public MenuDuplicateDelac(EditorDelac ed) {
        //super("Duplicate value", true, true, true, true);
        editorDelac = ed;
        initComponents();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.jTable1.removeAll();
        CheckDuplicateValue(ed.getjTable1(), tableModel);
        this.jTable1.repaint();
        this.jTable1.setDefaultRenderer(Object.class, paintGrid());
        final JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jTable1.getSelectedRow() > -1) {
                    int id = (int) jTable1.getModel().getValueAt(jTable1.getSelectedRow(), 0);
                    String lema = jTable1.getModel().getValueAt(jTable1.getSelectedRow(), 1).toString();
                    if(lema.equals(editorDelac.getTableModel().getValueAt(id, 2))){
                        editorDelac.getTableModel().removeRow(id);
                        int idDuplicate = jTable1.getSelectedRow();
                        DefaultTableModel tm = (DefaultTableModel) jTable1.getModel();
                        tm.removeRow(idDuplicate);
                        jTable1.repaint();
                        JOptionPane.showMessageDialog(null, jTable1.getSelectedRow() + "row deleted : " + id + " - " + lema);
                    }
                    
                } else {
                    JOptionPane.showMessageDialog(null, "no value selected");
                }
            }
        });
        popupMenu.add(deleteItem);
        jTable1.setComponentPopupMenu(popupMenu);

    }

    private DefaultTableCellRenderer paintGrid() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(row % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE);
                c.setForeground(Color.black);
                return c;
            }
        };
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Duplicate value");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Duplucate value", "Lemma", "Fst", "Synsem", "dic"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setColumnSelectionAllowed(true);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
                .addGap(14, 14, 14))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(304, 304, 304)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MenuDuplicateDelac.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuDuplicateDelac.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuDuplicateDelac.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuDuplicateDelac.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuDuplicateDelac().setVisible(true);
            }
        });
    }

    private void CheckDuplicateValue(JTable jtable, DefaultTableModel tableModel) {
        tableModel = (DefaultTableModel) jTable1.getModel();
        for (int i = 0; i < jtable.getRowCount() - 1; i++) {
            String lema = jtable.getModel().getValueAt(i, 2).toString();
            String fst = jtable.getModel().getValueAt(i, 3).toString();
            String sinsem = jtable.getModel().getValueAt(i, 4).toString();
            String dic = jtable.getModel().getValueAt(i, 8).toString();
            String[] sinsems = sinsem.split("(=)|(\\+)");
            for (int j = i + 1; j < jtable.getRowCount(); j++) {
                
                String lemaCompare = jtable.getModel().getValueAt(j, 2).toString();
                String fstCompare = jtable.getModel().getValueAt(j, 3).toString();
                String sinsemCompare = jtable.getModel().getValueAt(j, 4).toString();
                String dicCompare = jtable.getModel().getValueAt(j, 8).toString();
                String[] sinsemCompares = sinsemCompare.split("()=|(\\+)");
                if (lemaCompare.trim().equals(lema.trim()) && fstCompare.equals(fst)
                        && areAllTrue(sinsems, sinsemCompares)) {
                    Object[] rowCompare = new Object[]{j, lemaCompare, fstCompare, sinsemCompare, dicCompare};
                    Object[] row = new Object[]{i, lema, fst, sinsem, dic};
                    tableModel.addRow(row);
                    tableModel.addRow(rowCompare);
                    i++;

                }
            }
        }
        jTable1.repaint();
    }

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

    // Variables declaration - do not modify                     
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration                   

    /**
     * @return the tableModel
     */
    public DefaultTableModel getTableModel() {
        return tableModel;
    }
}
