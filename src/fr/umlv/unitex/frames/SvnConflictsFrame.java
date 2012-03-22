/*
 * Unitex
 *
 * Copyright (C) 2001-2012 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package fr.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.umlv.unitex.svn.SvnMonitor;

/**
 * This class describes a frame that shows all the command lines that have been
 * launched.
 * 
 * @author Sébastien Paumier
 */
public class SvnConflictsFrame extends TabbableInternalFrame {
	SvnConflictsFrame() {
		super("", true, false, true, true);
		final ListModel model = SvnMonitor.getSvnConflictModel();
		final JList list = new JList(model);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				final Object[] files = list.getSelectedValues();
				for (final Object o : files) {
					final File f = (File) o;
					InternalFrameManager.getManager(f).newGraphFrame(f);
				}
			}
		});
		setClosable(model.getSize() == 0);
		setTitle(model.getSize() + " SVN conflicts detected on graphs");
		model.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				setTitle(model.getSize() + " SVN conflicts detected on graphs");
				setClosable(model.getSize() == 0);
			}

			public void intervalAdded(ListDataEvent e) {
				setTitle(model.getSize() + " SVN conflicts detected on graphs");
				setClosable(model.getSize() == 0);
			}

			public void contentsChanged(ListDataEvent e) {
				/* */
			}
		});
		final JScrollPane scroll = new JScrollPane(list);
		getContentPane().add(scroll, BorderLayout.CENTER);
		getContentPane().add(new JLabel("Click on a graph name to open it:"),
				BorderLayout.NORTH);
		setBounds(100, 100, 600, 400);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	@Override
	public String getTabName() {
		return "SVN conflicts";
	}
}
