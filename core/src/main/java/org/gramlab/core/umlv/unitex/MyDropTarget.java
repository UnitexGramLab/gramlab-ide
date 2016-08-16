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
package org.gramlab.core.umlv.unitex;

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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.frames.InternalFrameManager;
import org.gramlab.core.umlv.unitex.frames.TranscodingFrame;
import org.gramlab.core.umlv.unitex.frames.UnitexFrame;
import org.gramlab.core.umlv.unitex.io.Encoding;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.project.manager.UnitexProjectManager;
import org.gramlab.core.umlv.unitex.text.Text;

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
public class MyDropTarget implements UnitexDropTarget {
	private static final DropTargetListener dropTargetListener;
	private static final DropTargetListener transcodeDropTargetListener;
	static DataFlavor dragNDropFlavor;
	static DataFlavor uriDragNDropFlavor;
	static {
		try {
			dragNDropFlavor = new DataFlavor(
					"application/x-java-file-list; class=java.util.List");
			uriDragNDropFlavor = new DataFlavor(
					"text/uri-list; class=java.lang.String");
		} catch (final ClassNotFoundException e) {
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
	@Override
	public DropTarget newDropTarget(Component c) {
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
	@Override
	public DropTarget newTranscodeDropTarget(Component c) {
		return new DropTarget(c, DnDConstants.ACTION_COPY_OR_MOVE,
				transcodeDropTargetListener, true);
	}

	static class DragNDropListener implements DropTargetListener {
		private boolean weCanDrag(DropTargetDragEvent e) {
			return (e.isDataFlavorSupported(MyDropTarget.dragNDropFlavor) || e
					.isDataFlavorSupported(MyDropTarget.uriDragNDropFlavor))
					&& ((e.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0);
		}

		@Override
		public void dragEnter(DropTargetDragEvent e) {
			if (!weCanDrag(e)) {
				e.rejectDrag();
				return;
			}
			e.acceptDrag(e.getDropAction());
		}

		@Override
		public void dragOver(DropTargetDragEvent e) {
			if (!weCanDrag(e)) {
				e.rejectDrag();
				return;
			}
			e.acceptDrag(e.getDropAction());
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent e) {
			if (!weCanDrag(e)) {
				e.rejectDrag();
				return;
			}
			e.acceptDrag(e.getDropAction());
		}

		@Override
		public void dragExit(DropTargetEvent e) {
			// nothing to do
		}

		@Override
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
			} catch (final Exception e2) {
				e2.printStackTrace();
				e.dropComplete(false);
				return;
			}
			if (data instanceof List) {
				final List<?> data2 = (List<?>) data;
				processDropList(data2);
				e.dropComplete(true);
			}
			if (data instanceof String) {
				final String data2 = (String) data;
				final Scanner s = new Scanner(data2);
				final ArrayList<File> list = new ArrayList<File>();
				while (s.hasNextLine()) {
					final String name = s.nextLine();
					File f;
					try {
						f = new File(new URI(name));
						list.add(f);
					} catch (final URISyntaxException e1) {
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
			extension = FileUtil.getFileNameExtension(f);
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
					@Override
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
					@Override
					public void run() {
						try {
							final Encoding e = Encoding.getEncoding(dela);
							if (e == null) {
								JOptionPane.showMessageDialog(
										UnitexFrame.mainFrame,
										dela.getAbsolutePath()
												+ " is not a Unicode dictionary",
										"Error", JOptionPane.ERROR_MESSAGE);
								return;
							}
						} catch (final HeadlessException e) {
							e.printStackTrace();
							return;
						}
						UnitexProjectManager.search(dela)
								.getFrameManagerAs(InternalFrameManager.class)
								.newDelaFrame(dela);
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
					@Override
					public void run() {
						final ToDo toDo = new ToDo() {
							@Override
							public void toDo(boolean success) {
								UnitexProjectManager.search(dela)
										.getFrameManagerAs(InternalFrameManager.class)
										.newDelaFrame(dela);
							}
						};
						final Encoding e = Encoding.getEncoding(dela);
						if (e == null) {
							UnitexProjectManager.search(dela)
									.getFrameManagerAs(InternalFrameManager.class)
									.newTranscodeOneFileDialog(dela, toDo);
						} else {
							toDo.toDo(true);
						}
					}
				});
				return;
			}
			if (extension.compareToIgnoreCase("grf") == 0) {
				for (final Object aList : list) {
					if (FileUtil.getFileNameExtension(f).compareToIgnoreCase(
							"grf") == 0) {
						final File file = (File) aList;
						UnitexProjectManager.search(file)
								.getFrameManagerAs(InternalFrameManager.class)
								.newGraphFrame(file);
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

		@Override
		public void dragEnter(DropTargetDragEvent e) {
			if (!weCanDrag(e)) {
				e.rejectDrag();
				return;
			}
			e.acceptDrag(e.getDropAction());
		}

		@Override
		public void dragOver(DropTargetDragEvent e) {
			if (!weCanDrag(e)) {
				e.rejectDrag();
				return;
			}
			e.acceptDrag(e.getDropAction());
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent e) {
			if (!weCanDrag(e)) {
				e.rejectDrag();
				return;
			}
			e.acceptDrag(e.getDropAction());
		}

		@Override
		public void dragExit(DropTargetEvent e) {
			// nothing to do
		}

		@Override
		public void drop(DropTargetDropEvent e) {
			Object data = null;
			try {
				e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				data = e.getTransferable().getTransferData(dragNDropFlavor);
				if (data == null)
					throw new NullPointerException();
			} catch (final Exception e2) {
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
			final TranscodingFrame frame = UnitexProjectManager
					.search(null).getFrameManagerAs(InternalFrameManager.class)
					.newTranscodingFrame();
			Object o;
			for (final Object aList : list) {
				o = aList;
				if (!(o instanceof File)) {
					return;
				}
				final File f = (File) o;
				final DefaultListModel model = frame.getListModel();
				if (!model.contains(f)) {
					model.addElement(f);
				}
			}
		}
		/* end of UnrestrictedDragNDropListener class */
	}
	/* end of MyDropTarget class */
}
