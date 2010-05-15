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
	private DelaFrameFactory delaFrameFactory=new DelaFrameFactory();
	private TokensFrameFactory tokensFrameFactory=new TokensFrameFactory();
	private TextDicFrameFactory textDicFrameFactory=new TextDicFrameFactory();
	private TextAutomatonFrameFactory textAutomatonFrameFactory=new TextAutomatonFrameFactory();

	
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
		addToDesktopIfNecessary(g,true);
		g.setVisible(true);
		try {
			g.setSelected(true);
			g.setIcon(false);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}

	private void addToDesktopIfNecessary(final JInternalFrame f,boolean removeOnClose) {
		for (JInternalFrame frame:desktop.getAllFrames()) {
			if (frame.equals(f)) {
				return;
			}
		}
		if (removeOnClose) {
			f.addInternalFrameListener(new InternalFrameAdapter() {
				@Override
				public void internalFrameClosed(InternalFrameEvent e) {
					desktop.remove(f);
				}
			});
		}
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
		t.addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				fireTextFrameClosed();
			}
		});
		addToDesktopIfNecessary(t,true);
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

	
	public boolean newDelaFrame(File dela) {
		DelaFrame f=delaFrameFactory.newDelaFrame(dela);
		if (f==null) return false;
		f.addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				fireDelaFrameClosed();
			}
		});
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
			f.setIcon(false);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		fireDelaFrameOpened();
		return true;
	}
	
	public void closeDelaFrame() {
		delaFrameFactory.closeDelaFrame();
	}

	private ArrayList<DelaFrameListener> delaFrameListeners=new ArrayList<DelaFrameListener>();
	protected boolean firingDelaFrame=false;
	
	public void addDelaFrameListener(DelaFrameListener l) {
		delaFrameListeners.add(l);
	}
	
	public void removeDelaFrameListener(DelaFrameListener l) {
		if (firingDelaFrame) {
			throw new IllegalStateException("Cannot remove a listener while firing");
		}
		delaFrameListeners.remove(l);
	}
	
	protected void fireDelaFrameOpened() {
		firingDelaFrame=true;
		try {
			for (DelaFrameListener l:delaFrameListeners) {
				l.delaFrameOpened();
			}
		} finally {
			firingDelaFrame=false;
		}
	}

	protected void fireDelaFrameClosed() {
		firingDelaFrame=true;
		try {
			for (DelaFrameListener l:delaFrameListeners) {
				l.delaFrameClosed();
			}
		} finally {
			firingDelaFrame=false;
		}
	}

	
	public boolean newTokensFrame(File tokens) {
		TokensFrame f=tokensFrameFactory.newTokensFrame(tokens);
		if (f==null) return false;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
			f.setIcon(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public void closeTokensFrame() {
		tokensFrameFactory.closeTokensFrame();
	}

	public boolean newTextDicFrame(File sntDir,boolean iconify) {
		TextDicFrame f=textDicFrameFactory.newTextDicFrame(sntDir);
		if (f==null) return false;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
			f.setIcon(iconify);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public void closeTextDicFrame() {
		textDicFrameFactory.closeTextDicFrame();
	}


	public boolean newTextAutomatonFrame() {
		TextAutomatonFrame f=textAutomatonFrameFactory.newTextAutomatonFrame();
		if (f==null) return false;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
			f.setIcon(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public void closeTextAutomatonFrame() {
		textAutomatonFrameFactory.closeTextAutomatonFrame();
	}

	public TextAutomatonFrame getTextAutomatonFrame() {
		return textAutomatonFrameFactory.getTextAutomatonFrame();
	}

}
