/*
 * Unitex
 *
 * Copyright (C) 2001-2016 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package fr.umlv.unitex.cassys;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Class allowing {@link DataList} to support drag and drop gesture
 * <p/>
 * Drag and drop gesture is currently supported with {@link String} and
 * {@link DataList}.
 * 
 * @author David Nott, Nathalie Friburger (nathalie.friburger@univ-tours.fr)
 */
class DataListTransferable implements Transferable {
	/**
	 * Data type identifier used for transfer
	 */
	public static DataFlavor DataListFlavor;
	/**
	 * The dataList to be transfered
	 */
	private final DataList dl;

	/**
	 * Creates the <code>DataList</code> identifier type and stores the data to
	 * be transfered.
	 * 
	 * @param o
	 *            array object supposed to store a string and two booleans
	 */
	public DataListTransferable(Object[] o) {
		final String mimeType = DataFlavor.javaJVMLocalObjectMimeType
				+ ";class = " + DataList.class.getName();
		try {
			DataListFlavor = new DataFlavor(mimeType);
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		}
		dl = new DataList((String) o[2], (Boolean) o[3], (Boolean) o[4], (Boolean) o[1], (Boolean) o[5], (Boolean) o[6]);
		// System.out.println("dl = "+ dl.getName()+" "+ dl.isMerge()+ " "+
		// dl.isReplace());
	}

	/**
	 * Constructs and returns the data type required by <code>flavor</code>
	 * 
	 * @param flavor
	 *            the flavor of data type
	 * @return an object function of the <flavor> required
	 * @throws <code>IOException</code> if <code>flavor</code> is null
	 * @throws <code>UnsupportedFlavorException</code> if <code>flavor</code> is
	 *         unsupported
	 */
	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (flavor == null) {
			throw new IOException();
		}
		if (flavor.equals(DataListFlavor)) {
			return new DataList(dl);
		} else if (flavor.equals(DataFlavor.stringFlavor)) {
			return dl.getName();
		} else
			throw new UnsupportedFlavorException(flavor);
	}

	/**
	 * Constructs and returns a sorted array of <code>dataFlavor</code>
	 * supported
	 * 
	 * @return a sorted array of <code>dataFlavor</code> supported
	 */
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { DataListFlavor, DataFlavor.stringFlavor };
	}

	/**
	 * Tests whether a <code>flavor</code> is supported
	 * 
	 * @param flavor
	 *            the flavor to test
	 * @return true if <code>flavor</code> is supported
	 */
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavor.equals(DataListFlavor) || flavor
				.equals(DataFlavor.stringFlavor));
	}
}
