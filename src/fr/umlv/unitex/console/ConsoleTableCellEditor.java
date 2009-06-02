package fr.umlv.unitex.console;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

@SuppressWarnings("serial")
public class ConsoleTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    private JButton button;
    private int currentRow=-1;
    ConsoleTableModel model;
    
    public ConsoleTableCellEditor(final ConsoleTableModel model) {
        this.model=model;
        button=new JButton();
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        
        button.addActionListener(new ActionListener() {
            @SuppressWarnings("synthetic-access")
            public void actionPerformed(ActionEvent e) {
                if (button.getIcon()==Console.statusErrorDown) {
                    /* If we want to show an error message */
                    ConsoleEntry entry=model.getConsoleEntry(currentRow);
                    Console.addCommand(entry.getErrorMessage(),false,currentRow+1);
                    entry.setStatus(2);
                } else {
                    ConsoleEntry entry=model.getConsoleEntry(currentRow);
                    /* We reset the error button of the father to the down position */
                    entry.setStatus(1);
                    model.removeEntry(currentRow+1);
                }
            fireEditingStopped();
        }});
    }

    public Component getTableCellEditorComponent(JTable t, Object value, boolean isSelected, int row, int column) {
        Integer i=(Integer)value;
        if (i==0 || i==3) {
            currentRow=-1;
            return null;
        }
        if (i==1) {
            button.setIcon(Console.statusErrorDown);
        }
        if (i==2) {
            button.setIcon(Console.statusErrorUp);
        }
        currentRow=row;
        return button;
    }

    public Object getCellEditorValue() {
        return null;
    }

}
