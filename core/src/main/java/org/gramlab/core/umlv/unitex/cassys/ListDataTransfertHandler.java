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
package org.gramlab.core.umlv.unitex.cassys;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;

import org.gramlab.core.umlv.unitex.cassys.ShareTransducerList;
import org.gramlab.core.umlv.unitex.cassys.ShareTransducerList.NotAnAbsolutePathException;

/**
 * Class specifying how drag and drop transfer from and to the table are done.
 * <p/>
 * The functionalities of this class can be split in two parts : how export is
 * done and how import is done.
 * <p/>
 * The <code>canImport</code> and <code>importData</code> methods deals about
 * how import is done. The <code>canImport</code> method specifies which kind of
 * data can be imported and the <code>importData</code> method converts
 * receivable data in a <code>DataList</code> that a row in the table can
 * import.
 * <p/>
 * The <code>createTransferable</code> and <code>exportDone</code> methods deals
 * how export is done. <code>createTransferable</code> encapsulates table row in
 * a class that can support drag and drop support. <code>exportDone</code>
 * removes source data from the table after a successful drop if the transfer is
 * mode <code>MOVE</code> rather than <code>COPY</code>.
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
	 * @return true if data to be imported is <code>String</code> or
	 *         <code>DataList</code>
	 */
	@Override
	public boolean canImport(TransferSupport support) {
		if (!support.isDrop()) {
			return false;
		}
		return !(!support.isDataFlavorSupported(DataFlavor.stringFlavor) && !support
				.isDataFlavorSupported(DataListTransferable.DataListFlavor));
	}

	/**
	 * Invoked during drag an drop gesture, specifies how data are imported.
	 * <p/>
	 * Only <code>String</code> and <code>DataList</code> are supported. In the
	 * case of <code>DataList</code>, the data is imported as it. In the case of
	 * a <code>String</code>, a <code>DataList</code> is constructed from with
	 * field <code>name</code> equal to the <code>String</code>, fields
	 * <code>merge</code> is set to <code>true</code> and <code>replace</code>
	 * set to <code>false</code>.
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
		final JTable.DropLocation dl = (JTable.DropLocation) support
				.getDropLocation();
		final int row = dl.getRow();
		// fetch the data and bail if this fails
		if (support.isDataFlavorSupported(DataListTransferable.DataListFlavor)) {
			try {
				final DataList dlf = (DataList) support.getTransferable()
						.getTransferData(DataListTransferable.DataListFlavor);
				final JTable table = (JTable) support.getComponent();
				final Object o[] = { dlf.getRank(), dlf.isDisabled(), dlf.getName(), dlf.isMerge(),
						dlf.isReplace(), dlf.isStar(), dlf.isGeneric() };
				((DefaultTableModel) table.getModel()).insertRow(row, o);
				return true;
			} catch (final IOException e) {
				e.printStackTrace();
				return false;
			} catch (final UnsupportedFlavorException e) {
				e.printStackTrace();
				return false;
			}
		}
		// If a String is to be imported, the dafault option is merge
		if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				final String data = (String) support.getTransferable()
						.getTransferData(DataFlavor.stringFlavor);
				final JTable table = (JTable) support.getComponent();
				ShareTransducerList stl = new ShareTransducerList();
				final Object[] rowData = { DataList.UNRANKED, false, stl.relativize(data), true, false, false,false };
				((DefaultTableModel) table.getModel()).insertRow(row, rowData);
				return true;
			} catch (final IOException e) {
				e.printStackTrace();
				return false;
			} catch (final UnsupportedFlavorException e) {
				e.printStackTrace();
				return false;
			}
			catch (final NotAnAbsolutePathException e1) {
				e1.printStackTrace();
				return false;
			}
		}
		return false;
	}

	/**
	 * Invoked during the drag gesture, return whether the dragged data should
	 * be move or copied. Set at <code>MOVE</code>.
	 * 
	 * @return <code>MOVE</code>
	 */
	@Override
	public int getSourceActions(JComponent jc) {
		return TransferHandler.MOVE;
	}

	/**
	 * Invoked during the drag gesture, creates a <code>Transferable</code>
	 * object to be dropped
	 * 
	 * @return <code>Transferable</code> object to be dropped
	 */
	@Override
	protected Transferable createTransferable(JComponent c) {
		final JTable jt = (JTable) c;
		final TransducerListTableModel model = (TransducerListTableModel) jt.getModel();
		final int row_selected = jt.getSelectedRow();
		final Object[] o = {
				(Integer) jt.getModel().getValueAt(row_selected, model.getRankIndex()),
				(Boolean) jt.getModel().getValueAt(row_selected, model.getDisabledIndex()),
				(String) jt.getModel().getValueAt(row_selected, model.getNameIndex()),
				(Boolean) jt.getModel().getValueAt(row_selected, model.getMergeIndex()),
				(Boolean) jt.getModel().getValueAt(row_selected, model.getReplaceIndex()),
				(Boolean) jt.getModel().getValueAt(row_selected, model.getStarIndex()),
                                (Boolean) jt.getModel().getValueAt(row_selected, model.getGenericIndex()),
				};
		return new DataListTransferable(o);
	}

	/**
	 * Invoked after a successful drop during a drag and drop gesture, removes
	 * the source data if transfer mode is set to <code>MOVE</code>
	 */
	@Override
	public void exportDone(JComponent source, Transferable data, int action) {
		if (action == TransferHandler.MOVE) {
			final JTable jt = (JTable) source;
			final DefaultTableModel dtm = (DefaultTableModel) jt.getModel();
			dtm.removeRow(jt.getSelectedRow());
		}
	}
}
