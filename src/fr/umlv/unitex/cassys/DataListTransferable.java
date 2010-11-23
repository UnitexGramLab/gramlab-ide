package fr.umlv.unitex.cassys;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Class allowing  {@link DataList} to support drag and drop gesture
 * <p/>
 * Drag and drop gesture is currently supported with {@link String} and {@link DataList}.
 *
 * @author dnott
 */
class DataListTransferable implements Transferable {

    /**
     * Data type identifier used for transfer
     */
    public static DataFlavor DataListFlavor;

    /**
     * The dataList to be transfered
     */
    private DataList dl;

    /**
     * Creates the <code>DataList</code> identifier type and stores the data to be transfered.
     *
     * @param o array object supposed to store a string and two booleans
     */
    public DataListTransferable(Object[] o) {
        String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class = " + DataList.class.getName();
        try {
            DataListFlavor = new DataFlavor(mimeType);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        dl = new DataList((String) o[0], (Boolean) o[1], (Boolean) o[2]);
        //System.out.println("dl = "+ dl.getName()+" "+ dl.isMerge()+ " "+ dl.isReplace());

    }

    /**
     * Constructs and returns the data type required by <code>flavor</code>
     *
     * @param flavor the flavor of data type
     * @return an object function of the <flavor> required
     * @throws <code>IOException</code> if <code>flavor</code> is null
     * @throws <code>UnsupportedFlavorException</code>
     *                                  if <code>flavor</code> is unsupported
     */
    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {

        if (flavor == null) {
            throw new IOException();
        }

        if (flavor.equals(DataListFlavor)) {
            return new DataList(dl);
        } else if (flavor.equals(DataFlavor.stringFlavor)) {
            return dl.getName();
        } else throw new UnsupportedFlavorException(flavor);

    }

    /**
     * Constructs and returns a sorted array of <code>dataFlavor</code> supported
     *
     * @return a sorted array of <code>dataFlavor</code> supported
     */
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataListFlavor, DataFlavor.stringFlavor};
    }

    /**
     * Tests whether a <code>flavor</code> is supported
     *
     * @param flavor the flavor to test
     * @return true if <code>flavor</code> is supported
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (flavor.equals(DataListFlavor) || flavor.equals(DataFlavor.stringFlavor));
    }

}
