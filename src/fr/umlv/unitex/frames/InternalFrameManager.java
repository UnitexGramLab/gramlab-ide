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

package fr.umlv.unitex.frames;

import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 * This class is responsible for managing all internal frames in Unitex
 * main frame.
 * 
 * @author paumier
 */
public class InternalFrameManager {

	JDesktopPane desktop;
	private GraphFrameFactory graphFrameFactory=new GraphFrameFactory();
	
	public InternalFrameManager(JDesktopPane desktop) {
		this.desktop=desktop;
	}
	
	/**
	 * Creates and returns a GraphFrame for the given .grf file.
	 * If a frame for 'grf' already exists, then it is made visible.
	 * If the .grf is not loadable, the function does nothing and
	 * returns null.
	 * 
	 * @param grf
	 * @return
	 */
	public GraphFrame newGraphFrame(File grf) {
		GraphFrame g=graphFrameFactory.getGraphFrame(grf);
		if (g==null) return null;
		addToDesktopIfNecessary(g);
		g.setVisible(true);
		try {
			g.setSelected(true);
			g.setIcon(false);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return g;
	}

	private void addToDesktopIfNecessary(final JInternalFrame f) {
		for (JInternalFrame frame:desktop.getAllFrames()) {
			if (frame.equals(f)) {
				return;
			}
		}
		f.addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				desktop.remove(f);
			}
		});
		desktop.add(f);
	}

	
	public void closeAllGraphFrames() {
		graphFrameFactory.closeAllGraphFrames();
	}


	public GraphFrame getCurrentFocusedGraphFrame() {
		JInternalFrame frame = desktop.getSelectedFrame();
		if (frame instanceof GraphFrame)
			return (GraphFrame) frame;
		return null;
	}

	public GraphFrame[] getGraphFrames() {
		return graphFrameFactory.getGraphFrames();
	}
}
