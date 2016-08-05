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
package org.gramlab.core.gramlab.util;

import java.awt.Component;
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

import org.gramlab.core.umlv.unitex.UnitexDropTarget;
import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;
import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.frames.InternalFrameManager;

/**
 * This class is used to listen drag and drop events on grf files.
 * <p/>
 * <p/>
 * <code>
 * public DropTarget dropTarget= MyDropTarget.newDropTarget(this);
 * </code>
 * 
 * @author Sébastien Paumier
 */
public class GramlabDropTarget implements UnitexDropTarget {

	private static final DropTargetListener dropTargetListener;
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
	}

	/**
	 * Creates and returns a <code>DropTarget</code> object that only accepts
	 * grf files
	 * 
	 * @param c
	 *            the component that will be the drop target
	 * @return the <code>DropTarget</code> object
	 */
	public DropTarget newDropTarget(Component c) {
		return new DropTarget(c, DnDConstants.ACTION_COPY_OR_MOVE,
				dropTargetListener, true);
	}

	static class DragNDropListener implements DropTargetListener {
		private boolean weCanDrag(DropTargetDragEvent e) {
			return (e.isDataFlavorSupported(GramlabDropTarget.dragNDropFlavor) || e
					.isDataFlavorSupported(GramlabDropTarget.uriDragNDropFlavor))
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
			if (extension.compareToIgnoreCase("grf") == 0) {
				for (final Object aList : list) {
					if (FileUtil.getFileNameExtension(f).compareToIgnoreCase(
							"grf") == 0) {
						final File file = (File) aList;
						GlobalProjectManager.search(file)
							.getFrameManagerAs(InternalFrameManager.class)
							.newGraphFrame(file);
					}
				}
			}
		}
		/* end of DragNDropListener class */
	}

	@Override
	public DropTarget newTranscodeDropTarget(Component c) {
		return null;
	}

	/* end of MyDropTarget class */
}
