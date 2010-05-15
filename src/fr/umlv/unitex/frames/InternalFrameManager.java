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
import java.util.ArrayList;

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
	private final static Integer LAYER=Integer.valueOf(1);
	private GraphFrameFactory graphFrameFactory=new GraphFrameFactory();
	private TextFrameFactory textFrameFactory=new TextFrameFactory();
	
	public InternalFrameManager(JDesktopPane desktop) {
		this.desktop=desktop;
	}
	
	/**
	 * Creates a GraphFrame for the given .grf file.
	 * If a frame for 'grf' already exists, then it is made visible.
	 * If the .grf is not loadable, the function does nothing and
	 * returns false.
	 * 
	 * @param grf
	 * @return
	 */
	public boolean newGraphFrame(File grf) {
		GraphFrame g=graphFrameFactory.getGraphFrame(grf);
		if (g==null) return false;
		addToDesktopIfNecessary(g);
		g.setVisible(true);
		try {
			g.setSelected(true);
			g.setIcon(false);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
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
		desktop.add(f,LAYER);
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


	/**
	 * Creates a TextFrame for the given text file.
	 * If the text is not loadable, the function does nothing and
	 * returns false.
	 * 
	 * @param grf
	 * @return
	 */
	public boolean newTextFrame(File text,boolean taggedText) {
		TextFrame t=textFrameFactory.newTextFrame(text);
		if (t==null) return false;
		addToDesktopIfNecessary(t);
		t.addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				fireTextFrameClosed();
			}
		});
		t.setVisible(true);
		try {
			t.setSelected(true);
			t.setIcon(false);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		fireTextFrameOpened(taggedText);
		return true;
	}
	
	public void closeTextFrame() {
		textFrameFactory.closeTextFrame();
	}

	public TextFrame getTextFrame() {
		return textFrameFactory.getTextFrame();
	}

	private ArrayList<TextFrameListener> textFrameListeners=new ArrayList<TextFrameListener>();
	protected boolean firingTextFrame=false;
	
	public void addTextFrameListener(TextFrameListener l) {
		textFrameListeners.add(l);
	}
	
	public void removeTextFrameListener(TextFrameListener l) {
		if (firingTextFrame) {
			throw new IllegalStateException("Cannot remove a listener while firing");
		}
		textFrameListeners.remove(l);
	}
	
	protected void fireTextFrameOpened(boolean taggedText) {
		firingTextFrame=true;
		try {
			for (TextFrameListener l:textFrameListeners) {
				l.textFrameOpened(taggedText);
			}
		} finally {
			firingTextFrame=false;
		}
	}

	protected void fireTextFrameClosed() {
		firingTextFrame=true;
		try {
			for (TextFrameListener l:textFrameListeners) {
				l.textFrameClosed();
			}
		} finally {
			firingTextFrame=false;
		}
	}

}
