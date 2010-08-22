package fr.umlv.unitex.cassys;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Class specifying how drag and drop transfer from and to the table are done.
 * <p/>
 * The functionalities of this class can be split in two parts : how export is done and how import is done.
 * <p/>
 * The <code>canImport</code> and <code>importData</code> methods deals about how import is done.
 * The <code>canImport</code> method specifies which kind of data can be imported and the <code>importData</code>
 * method converts receivable data in a <code>DataList</code> that a row in the table can import.
 * <p/>
 * The <code>createTransferable</code> and <code>exportDone</code> methods deals how export is done.
 * <code>createTransferable</code> encapsulates table row in a class that can support drag and drop support.
 * <code>exportDone</code> removes source data from the table after a successful drop if the transfer is mode
 * <code>MOVE</code> rather than <code>COPY</code>.
 * <p/>
 *
 * @author david nott
 */
public class ListDataTransfertHandler extends TransferHandler {


    /**
     * Invoked during drag and drop gesture, tests whether data can be imported.
     * <p/>
     * Only String and DataList are supported.
     *
     * @return true if data to be imported is <code>String</code> or <code>DataList</code>
     */
    @Override
    public boolean canImport(TransferSupport support) {

        if (!support.isDrop()) {
            return false;
        }

        return !(!support.isDataFlavorSupported(DataFlavor.stringFlavor) &&
                !support.isDataFlavorSupported(DataListTransferable.DataListFlavor));

    }


    /**
     * Invoked during drag an drop gesture, specifies how data are imported.
     * <p/>
     * Only <code>String</code> and <code>DataList</code> are supported. In the case of <code>DataList</code>,
     * the data is imported as it. In the case of a <code>String</code>, a <code>DataList</code> is constructed
     * from with field <code>name</code> equal to the <code>String</code>, fields <code>merge</code> is set to
     * <code>true</code> and
     * <code>replace</code> set to <code>false</code>.
     *
     * @return true if data is successfully imported
     */
    @Override
    public boolean importData(TransferSupport support) {
        // if we can't handle the import, say so
        if (!canImport(support)) {
            return false;
        }

        // fetch the drop location
        JTable.DropLocation dl = (JTable.DropLocation) support
                .getDropLocation();

        int row = dl.getRow();

        // fetch the data and bail if this fails
        if (support.isDataFlavorSupported(DataListTransferable.DataListFlavor)) {
            try {
                DataList dlf = (DataList) support.getTransferable().getTransferData(
                        DataListTransferable.DataListFlavor);
                JTable table = (JTable) support.getComponent();
                Object o[] = {dlf.getName(),
                        dlf.isMerge(),
                        dlf.isReplace()};

                ((DefaultTableModel) table.getModel()).insertRow(row, o);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
                return false;
            }
        }

        // If a String is to be imported, the dafault option is merge
        if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                JTable table = (JTable) support.getComponent();

                Object[] rowData = {data, true, false};
                ((DefaultTableModel) table.getModel()).insertRow(row, rowData);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
                return false;
            }

        }
        return false;
    }

    /**
     * Invoked during the drag gesture, return whether the dragged data should be move or copied. Set at <code>MOVE</code>.
     *
     * @return <code>MOVE</code>
     */
    @Override
    public int getSourceActions(JComponent jc) {
        return TransferHandler.MOVE;
    }


    /**
     * Invoked during the drag gesture, creates a <code>Transferable</code> object to be dropped
     *
     * @return <code>Transferable</code> object to be dropped
     */
    @Override
    protected Transferable createTransferable(JComponent c) {
        JTable jt = (JTable) c;
        int row_selected = jt.getSelectedRow();

        Object[] o = {(String) jt.getModel().getValueAt(row_selected, 0),
                (Boolean) jt.getModel().getValueAt(row_selected, 1),
                (Boolean) jt.getModel().getValueAt(row_selected, 2)};


        return new DataListTransferable(o);
    }

    /**
     * Invoked after a successful drop during a drag and drop gesture, removes the source data if transfer mode is set to <code>MOVE</code>
     */
    @Override
    public void exportDone(JComponent source, Transferable data, int action) {

        if (action == TransferHandler.MOVE) {
            JTable jt = (JTable) source;
            DefaultTableModel dtm = (DefaultTableModel) jt.getModel();
            dtm.removeRow(jt.getSelectedRow());

        }

    }

}
