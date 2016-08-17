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
package org.gramlab.core.umlv.unitex.graphrendering;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;
import org.gramlab.core.umlv.unitex.frames.GraphFrame;
import org.gramlab.core.umlv.unitex.frames.InternalFrameManager;

/**
 * This class defines static methods for building portions of Graph related menu options that 
 * can be reused (in graph editor popup menu, in FSGraph menu, in GramLab's Graphs menu, etc)
 * 
 * @author Nebojša Vasiljević
 */
public class GraphMenuBuilder {

	public static JMenu createExportMenu() {
		return createExportMenu(null);
	}
	
	public static JMenu createExportMenu(final GraphicalZone grZone) {
		JMenu exportMenu = new JMenu("Export as Image");
		final Action exportPng = new AbstractAction("Export as PNG Image...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame frm = findFrame(grZone);
				if(frm!=null)
					frm.exportPng();
			}
		};
		
		exportMenu.add(new JMenuItem(exportPng));

		final Action exportJpeg = new AbstractAction("Export as JPEG Image...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame frm = findFrame(grZone);
				if(frm!=null)
					frm.exportJpeg();
			}
		};
		
		exportMenu.add(new JMenuItem(exportJpeg));
		
		final Action exportSvg = new AbstractAction("Export as SVG Image...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame frm = findFrame(grZone);
				if(frm!=null)
					frm.exportSvg();
			}
		};
		
		exportMenu.add(new JMenuItem(exportSvg));
		
		return exportMenu;
	}
	
	protected static GraphFrame findFrame(GraphicalZone grZone) {
		GraphFrame frm = null;
		if(grZone != null) {
			frm = (GraphFrame) grZone.getParentFrame();
		}
		if(frm == null) {
			frm = GlobalProjectManager.search(null)
					.getFrameManagerAs(InternalFrameManager.class).getCurrentFocusedGraphFrame();
		}
		if(frm == null) {
			JOptionPane.showMessageDialog(null,"No active graph frame!","Missing Graph Frame", JOptionPane.WARNING_MESSAGE);
		} 
		return frm;
	}
}
