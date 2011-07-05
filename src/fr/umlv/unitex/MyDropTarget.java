/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import fr.umlv.unitex.frames.TranscodingFrame;
import fr.umlv.unitex.frames.UnitexFrame;
import fr.umlv.unitex.io.Encoding;
import fr.umlv.unitex.io.UnicodeIO;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.text.Text;

/**
 * This class is used to listen drag and drop events. Files that can be dragged
 * are texts (".txt" and ".snt", graphs (".grf") and dictionaries (".dic", "dlf"
 * and "dlc"). If you want to allow drag and drop on a component, you must add a
 * <code>DropTarget</code> field to this component like the following:
 * <p/>
 * <p/>
 * <code>
 * public DropTarget dropTarget= MyDropTarget.newDropTarget(this);
 * </code>
 * 
 * @author Sébastien Paumier
 */
public class MyDropTarget {
	private static final DropTargetListener dropTargetListener;
	private static final DropTargetListener transcodeDropTargetListener;
	private static DataFlavor dragNDropFlavor;
	private static DataFlavor uriDragNDropFlavor;
	static {
		try {
			dragNDropFlavor = new DataFlavor(
					"application/x-java-file-list; class=java.util.List");
			uriDragNDropFlavor = new DataFlavor(
					"text/uri-list; class=java.lang.String");
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
			return (e.isDataFlavorSupported(MyDropTarget.dragNDropFlavor) || e
					.isDataFlavorSupported(MyDropTarget.uriDragNDropFlavor))
					&& ((e.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0);
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
				if (e.isDataFlavorSupported(dragNDropFlavor)) {
					data = e.getTransferable().getTransferData(dragNDropFlavor);
				} else if (e.isDataFlavorSupported(uriDragNDropFlavor)) {
					data = e.getTransferable().getTransferData(
							uriDragNDropFlavor);
				}
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
			}
			if (data instanceof String) {
				String data2 = (String) data;
				Scanner s = new Scanner(data2);
				ArrayList<File> list = new ArrayList<File>();
				while (s.hasNextLine()) {
					String name = s.nextLine();
					File f;
					try {
						f = new File(new URI(name));
						list.add(f);
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
				s.close();
				processDropList(list);
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
				if (list.size() > 1) {
					JOptionPane
							.showMessageDialog(
									UnitexFrame.mainFrame,
									"You should not try to drop more than one text file at once",
									"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
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
				if (list.size() > 1) {
					JOptionPane
							.showMessageDialog(
									UnitexFrame.mainFrame,
									"You should not try to drop more than one dictionary at once",
									"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				final File dela = f;
				// post pone code
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						try {
							Encoding e = Encoding.getEncoding(dela);
							if (e == null) {
								JOptionPane
										.showMessageDialog(
												UnitexFrame.mainFrame,
												dela.getAbsolutePath()
														+ " is not a Unicode dictionary",
												"Error",
												JOptionPane.ERROR_MESSAGE);
								return;
							}
						} catch (HeadlessException e) {
							e.printStackTrace();
							return;
						}
						UnitexFrame.getFrameManager().newDelaFrame(dela);
					}
				});
				return;
			}
			if (extension.compareToIgnoreCase("dic") == 0) {
				if (list.size() > 1) {
					JOptionPane
							.showMessageDialog(
									UnitexFrame.mainFrame,
									"You should not try to drop more than one dictionary at once",
									"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				final File dela = f;
				// post pone code
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						ToDo toDo = new ToDo() {
							public void toDo() {
								UnitexFrame.getFrameManager()
										.newDelaFrame(dela);
							}
						};
						Encoding e = Encoding.getEncoding(dela);
						if (e == null) {
							UnitexFrame.getFrameManager()
									.newTranscodeOneFileDialog(dela, toDo);
						} else {
							toDo.toDo();
						}
					}
				});
				return;
			}
			if (extension.compareToIgnoreCase("grf") == 0) {
				for (Object aList : list) {
					if (Util.getFileNameExtension(f).compareToIgnoreCase("grf") == 0) {
						final File file = (File) aList;
						UnitexFrame.getFrameManager().newGraphFrame(file);
					}
				}
			}
		}
		/* end of DragNDropListener class */
	}

	static class TranscodeDragNDropListener implements DropTargetListener {
		private boolean weCanDrag(DropTargetDragEvent e) {
			return e.isDataFlavorSupported(MyDropTarget.dragNDropFlavor)
					&& ((e.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0);
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
			TranscodingFrame frame = UnitexFrame.getFrameManager()
					.newTranscodingFrame();
			Object o;
			for (Object aList : list) {
				o = aList;
				if (!(o instanceof File)) {
					return;
				}
				File f = (File) o;
				DefaultListModel model = frame.getListModel();
				if (!model.contains(f)) {
					model.addElement(f);
				}
			}
		}
		/* end of UnrestrictedDragNDropListener class */
	}
	/* end of MyDropTarget class */
}
