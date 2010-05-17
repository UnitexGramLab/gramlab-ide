/*
 * Unitex
 *
 * Copyright (C) 2001-2010 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

package fr.umlv.unitex;

import java.util.List;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;

import javax.swing.*;

import fr.umlv.unitex.conversion.*;
import fr.umlv.unitex.frames.UnitexFrame;
import fr.umlv.unitex.io.*;
import fr.umlv.unitex.process.*;
import fr.umlv.unitex.process.commands.ConvertCommand;

/**
 * This class is used to listen drag and drop events. Files that can be dragged
 * are texts (".txt" and ".snt", graphs (".grf") and dictionaries (".dic", "dlf"
 * and "dlc"). If you want to allow drag and drop on a component, you must add a
 * <code>DropTarget</code> field to this component like the following:
 * 
 * <p>
 * <code>
 * public DropTarget dropTarget= MyDropTarget.newDropTarget(this);
 * </code>
 * 
 * @author Sébastien Paumier
 *  
 */

public class MyDropTarget {

	static DropTargetListener dropTargetListener;
	static DropTargetListener transcodeDropTargetListener;

	static DataFlavor dragNDropFlavor;

	static {
		try {
			dragNDropFlavor = new DataFlavor(
					"application/x-java-file-list; class=java.util.List");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		dropTargetListener = new DragNDropListener();
		transcodeDropTargetListener = new TranscodeDragNDropListener();
	}

	/**
	 * Creates and returns a <code>DropTarget</code> object that only accepts
	 * files supported by Unitex
	 * 
	 * @param c
	 *            the component that will be the drop target
	 * @return the <code>DropTarget</code> object
	 */
	public static DropTarget newDropTarget(Component c) {
		return new DropTarget(c, DnDConstants.ACTION_COPY_OR_MOVE,
				dropTargetListener, true);
	}

	/**
	 * Creates and returns a <code>DropTarget</code> object that accepts all
	 * files for transcoding
	 * 
	 * @param c
	 *            the component that will be the drop target
	 * @return the <code>DropTarget</code> object
	 */
	public static DropTarget newTranscodeDropTarget(Component c) {
		return new DropTarget(c, DnDConstants.ACTION_COPY_OR_MOVE,
				transcodeDropTargetListener, true);
	}

	static class DragNDropListener implements DropTargetListener {

		private boolean weCanDrag(DropTargetDragEvent e) {
			if (!e.isDataFlavorSupported(MyDropTarget.dragNDropFlavor)) {
				return false;
			}
			return ((e.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0);
		}

		public void dragEnter(DropTargetDragEvent e) {
			if (!weCanDrag(e)) {
				e.rejectDrag();
				return;
			}
			e.acceptDrag(e.getDropAction());
		}

		public void dragOver(DropTargetDragEvent e) {
			if (!weCanDrag(e)) {
				e.rejectDrag();
				return;
			}
			e.acceptDrag(e.getDropAction());
		}

		public void dropActionChanged(DropTargetDragEvent e) {
			if (!weCanDrag(e)) {
				e.rejectDrag();
				return;
			}
			e.acceptDrag(e.getDropAction());
		}

		public void dragExit(DropTargetEvent e) {
			// nothing to do
		}

		@SuppressWarnings("unchecked")
        public void drop(DropTargetDropEvent e) {
			Object data = null;
			try {
				e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				data = e.getTransferable().getTransferData(dragNDropFlavor);
				if (data == null)
					throw new NullPointerException();
			} catch (Exception e2) {
				e2.printStackTrace();
				e.dropComplete(false);
				return;
			}
			if (data instanceof List) {
				List<?> data2 = (List<?>) data;
                processDropList(data2);
				e.dropComplete(true);
			} else {
				e.dropComplete(false);
			}
		}

		private void processDropList(List<?> list) {
			if (list.size() == 0)
				return;
			Object o;
			File f;
			String extension;
			o = list.get(0);
			if (!(o instanceof File))
				return;
			f = (File) o;
			extension = Util.getFileNameExtension(f);
			if ((extension.compareToIgnoreCase("snt") == 0)
					|| (extension.compareToIgnoreCase("txt") == 0)) {
				final File F = f;
				// post pone code
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Text.loadCorpus(F);
					}
				});
				return;
			}
			if ((f.getName().compareToIgnoreCase("dlf") == 0)
					|| (f.getName().compareToIgnoreCase("dlc") == 0)) {
				final File dela = f;
				// post pone code
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						try {
							if (!UnicodeIO.isAUnicodeLittleEndianFile(dela)) {
								JOptionPane
										.showMessageDialog(
												UnitexFrame.mainFrame,
												dela.getAbsolutePath()
														+ " is not a Unicode Little-Endian dictionary",
												"Error",
												JOptionPane.ERROR_MESSAGE);
								return;
							}
						} catch (HeadlessException e) {
							e.printStackTrace();
							return;
						} catch (FileNotFoundException e) {
							e.printStackTrace();
							return;
						}
						UnitexFrame.getFrameManager().newDelaFrame(dela);
					}
				});
				return;
			}
			if (extension.compareToIgnoreCase("dic") == 0) {
				final File dela = f;
				// post pone code
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						ToDo toDo = new ToDo() {
							public void toDo() {
								UnitexFrame.getFrameManager().newDelaFrame(dela);
							}
						};
						try {
							if (!UnicodeIO.isAUnicodeLittleEndianFile(dela)) {
								ConvertOneFileFrame.reset();
								ConvertCommand res = ConvertOneFileFrame
										.getCommandLineForConversion(dela);
								if (res == null) {
									return;
								}
								new ProcessInfoFrame(res, true, toDo);
							} else {
								toDo.toDo();
							}
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
							return;
						}
					}
				});
				return;
			}
			if (extension.compareToIgnoreCase("grf") == 0) {
				ConvertOneFileFrame.reset();
				for (int i = 0; i < list.size(); i++) {
					if (Util.getFileNameExtension(f).compareToIgnoreCase("grf") == 0) {
						final File file = (File)list.get(i);
						UnitexFrame.mainFrame.loadGraph(file);
					}
				}
				return;
			}
		}

		/* end of DragNDropListener class */
	}
	static class TranscodeDragNDropListener implements DropTargetListener {

		private boolean weCanDrag(DropTargetDragEvent e) {
			if (!e.isDataFlavorSupported(MyDropTarget.dragNDropFlavor)) {
				return false;
			}
			return ((e.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0);
		}

		public void dragEnter(DropTargetDragEvent e) {
			if (!weCanDrag(e)) {
				e.rejectDrag();
				return;
			}
			e.acceptDrag(e.getDropAction());
		}

		public void dragOver(DropTargetDragEvent e) {
			if (!weCanDrag(e)) {
				e.rejectDrag();
				return;
			}
			e.acceptDrag(e.getDropAction());
		}

		public void dropActionChanged(DropTargetDragEvent e) {
			if (!weCanDrag(e)) {
				e.rejectDrag();
				return;
			}
			e.acceptDrag(e.getDropAction());
		}

		public void dragExit(DropTargetEvent e) {
			// nothing to do
		}

		public void drop(DropTargetDropEvent e) {
			Object data = null;
			try {
				e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				data = e.getTransferable().getTransferData(dragNDropFlavor);
				if (data == null)
					throw new NullPointerException();
			} catch (Exception e2) {
				e2.printStackTrace();
				e.dropComplete(false);
				return;
			}
			if (data instanceof java.util.List<?>) {
				processDropList((List<?>) data);
				e.dropComplete(true);
			} else {
				e.dropComplete(false);
			}
		}

		private void processDropList(List<?> list) {
			if (!ConversionFrame.isFrameVisible()) {
				// this case should never happen
				return;
			}
			Object o;
			File f;
			for (int i = 0; i < list.size(); i++) {
				o = list.get(i);
				if (!(o instanceof File)) {
					return;
				}
				f = (File) o;
				DefaultListModel model = ConversionFrame.getListModel();
				if (!model.contains(f)) {
					model.addElement(f);
				}
			}
		}

		/* end of UnrestrictedDragNDropListener class */
	}

	/* end of MyDropTarget class */
}