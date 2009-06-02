package fr.umlv.unitex.console;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class ConsoleTableModel extends AbstractTableModel {

    private final ArrayList<ConsoleEntry> data;
    
    public ConsoleTableModel() {
        data=new ArrayList<ConsoleEntry>();
    }
    
    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return data.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        ConsoleEntry e=data.get(rowIndex);
        if (columnIndex==0) {
            return e.getStatus();
        }
        if (columnIndex==1) {
            return e;
        }
        throw new IllegalArgumentException("Invalid columun index: "+columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex==0) {
            return Integer.class;
        }
        if (columnIndex==1) {
            return ConsoleEntry.class;
        }
        throw new IllegalArgumentException("Invalid columun index: "+columnIndex);
    }
    
    public void addConsoleEntry(int index,ConsoleEntry e) {
        data.add(index,e);
        fireTableRowsInserted(index,index);
    }
    
    @Override
    public String getColumnName(int column) {
        return "";
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex==0;
    }

    public ConsoleEntry getConsoleEntry(int index) {
        return data.get(index);
    }

    public void removeEntry(int index) {
        data.remove(index);
        fireTableRowsDeleted(index,index);        
    }
    
    
}
