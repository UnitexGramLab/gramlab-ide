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
package fr.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.svn.SvnMonitor;

/**
 * This class describes a frame that shows all the command lines that have been
 * launched.
 * 
 * @author Sébastien Paumier
 */
public class SvnConflictsFrame extends TabbableInternalFrame {

	SvnConflictsFrame(SvnMonitor monitor) {
		super("", true, false, true, true);
		final ListModel model = monitor.getSvnConflictModel();
		final JList list = new JList(model);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() < 2)
					return;
				final int index = list.locationToIndex(e.getPoint());
				if (index != -1) {
					final File f = (File) list.getModel().getElementAt(index);
					GlobalProjectManager.search(f).getFrameManagerAs(InternalFrameManager.class)
							.newGraphFrame(f);
				}
			}
		});
		setClosable(model.getSize() == 0);
		setTitle(model.getSize() + " SVN conflicts detected on graphs");
		model.addListDataListener(new ListDataListener() {
			@Override
			public void intervalRemoved(ListDataEvent e) {
				setTitle(model.getSize() + " SVN conflicts detected on graphs");
				setClosable(model.getSize() == 0);
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
				setTitle(model.getSize() + " SVN conflicts detected on graphs");
				setClosable(model.getSize() == 0);
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				/* */
			}
		});
		final JScrollPane scroll = new JScrollPane(list);
		getContentPane().add(scroll, BorderLayout.CENTER);
		getContentPane().add(
				new JLabel("Double-click on a graph name to open it:"),
				BorderLayout.NORTH);
		setBounds(100, 100, 600, 400);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	@Override
	public String getTabName() {
		return "SVN conflicts";
	}
}
