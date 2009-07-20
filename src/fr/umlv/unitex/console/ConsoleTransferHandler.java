package fr.umlv.unitex.console;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;


@SuppressWarnings("serial")
public class ConsoleTransferHandler extends TransferHandler {

    private ConsoleTableModel model;
    
    public ConsoleTransferHandler(ConsoleTableModel model) {
        this.model=model;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY;
    }
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        String s="";
        int n=model.getRowCount();
        for (int i=0;i<n;i++) {
            s=s+model.getValueAt(i,1)+"\n";
        }
        return new StringSelection(s);
    }
    
    @Override
    protected void exportDone(JComponent source,Transferable data, int action) {
        /* Nothing to do */
    }
    
    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        return false;
    }
    
    @Override
    public boolean importData(JComponent comp, Transferable t) {
        return false;
    }
}
