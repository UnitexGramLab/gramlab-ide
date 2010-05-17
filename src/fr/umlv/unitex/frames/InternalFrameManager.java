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
	private AboutUnitexFrameFactory aboutUnitexFrameFactory=new AboutUnitexFrameFactory();
	private ApplyLexicalResourcesFrameFactory applyLexicalResourcesFrameFactory=new ApplyLexicalResourcesFrameFactory();
	private CheckDicFrameFactory checkDicFrameFactory=new CheckDicFrameFactory();
	private CheckResultFrameFactory checkResultFrameFactory=new CheckResultFrameFactory();
	private ConcordanceDiffFrameFactory concordanceDiffFrameFactory=new ConcordanceDiffFrameFactory();
	private ConcordanceFrameFactory concordanceFrameFactory=new ConcordanceFrameFactory();
	private ConcordanceParameterFrameFactory concordanceParameterFrameFactory=new ConcordanceParameterFrameFactory();
	private ConstructTfstFrameFactory constructTfstFrameFactory=new ConstructTfstFrameFactory();
	private ConvertTfstToTextFrameFactory convertTfstToTextFrameFactory=new ConvertTfstToTextFrameFactory();
	private ElagCompFrameFactory elagCompFrameFactory=new ElagCompFrameFactory();
	private GlobalPreferencesFrameFactory globalPreferencesFrameFactory=new GlobalPreferencesFrameFactory();
	private GraphCollectionFrameFactory graphCollectionFrameFactory=new GraphCollectionFrameFactory();
	private InflectFrameFactory inflectFrameFactory=new InflectFrameFactory();
	private ConvertLexiconGrammarFrameFactory convertLexiconGrammarFrameFactory=new ConvertLexiconGrammarFrameFactory();
	private LexiconGrammarTableFrameFactory lexiconGrammarTableFrameFactory=new LexiconGrammarTableFrameFactory();
	private LocateFrameFactory locateFrameFactory=new LocateFrameFactory();
	
	private GraphPathDialogFactory graphPathDialogFactory=new GraphPathDialogFactory();
	
	
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

	public boolean newAboutUnitexFrame() {
		AboutUnitexFrame f=aboutUnitexFrameFactory.newAboutUnitexFrame();
		if (f==null) return false;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}


	public boolean newApplyLexicalResourcesFrame() {
		ApplyLexicalResourcesFrame f=applyLexicalResourcesFrameFactory.newApplyLexicalResourcesFrame();
		if (f==null) return false;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void closeApplyLexicalResourcesFrame() {
		applyLexicalResourcesFrameFactory.closeApplyLexicalResourcesFrame();
	}

	public boolean newCheckDicFrame() {
		CheckDicFrame f=checkDicFrameFactory.newCheckDicFrame();
		if (f==null) return false;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void closeCheckDicFrame() {
		checkDicFrameFactory.closeCheckDicFrame();
	}

	public boolean newCheckResultFrame(File file) {
		CheckResultFrame f=checkResultFrameFactory.newCheckResultFrame(file);
		if (f==null) return false;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void closeCheckResultFrame() {
		checkResultFrameFactory.closeCheckResultFrame();
	}

	public boolean newConcordanceDiffFrame(File file,int widthInChars) {
		ConcordanceDiffFrame f=concordanceDiffFrameFactory.newConcordanceDiffFrame(file,widthInChars);
		if (f==null) return false;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void closeConcordanceDiffFrame() {
		concordanceDiffFrameFactory.closeConcordanceDiffFrame();
	}


	public boolean newConcordanceFrame(File file,int widthInChars) {
		ConcordanceFrame f=concordanceFrameFactory.newConcordanceFrame(file,widthInChars);
		if (f==null) return false;
		addToDesktopIfNecessary(f,true);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void closeConcordanceFrame() {
		concordanceFrameFactory.closeConcordanceFrame();
	}


	public boolean newConcordanceParameterFrame() {
		return newConcordanceParameterFrame(-1);
	}
	
	public boolean newConcordanceParameterFrame(int matches) {
		ConcordanceParameterFrame f=concordanceParameterFrameFactory.newConcordanceParameterFrame(matches);
		if (f==null) return false;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void closeConcordanceParameterFrame() {
		concordanceParameterFrameFactory.closeConcordanceParameterFrame();
	}


	public boolean newConstructTfstFrame() {
		ConstructTfstFrame f=constructTfstFrameFactory.newConstructTfstFrame();
		if (f==null) return false;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void closeConstructTfstFrame() {
		constructTfstFrameFactory.closeConstructTfstFrame();
	}


	public boolean newConvertTfstToTextFrame() {
		ConvertTfstToTextFrame f=convertTfstToTextFrameFactory.newConvertTfstToTextFrame();
		if (f==null) return false;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void closeConvertTfstToTextFrame() {
		convertTfstToTextFrameFactory.closeConvertTfstToTextFrame();
	}


	public boolean newElagCompFrame() {
		ElagCompFrame f=elagCompFrameFactory.newElagCompFrame();
		if (f==null) return false;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void closeElagCompFrame() {
		elagCompFrameFactory.closeElagCompFrame();
	}


	public boolean newGlobalPreferencesFrame() {
		GlobalPreferencesFrame f=globalPreferencesFrameFactory.newGlobalPreferencesFrame();
		if (f==null) return false;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void closeGlobalPreferencesFrame() {
		globalPreferencesFrameFactory.closeGlobalPreferencesFrame();
	}

	public GlobalPreferencesFrame getGlobalPreferencesFrame() {
		return globalPreferencesFrameFactory.getGlobalPreferencesFrame();
	}


	public boolean newGraphPathDialog() {
		GraphFrame gf = getCurrentFocusedGraphFrame();
		if (gf == null) {
			return false;
		}
		GraphPathDialog d=graphPathDialogFactory.newGraphPathDialog();
		if (d==null) return false;
		d.graphName.setText(gf.getGraph().getAbsolutePath());
		d.setVisible(true);
		return true;
	}


	public boolean newGraphCollectionFrame() {
		GraphCollectionFrame f=graphCollectionFrameFactory.newGraphCollectionFrame();
		if (f==null) return false;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void closeGraphCollectionFrame() {
		graphCollectionFrameFactory.closeGraphCollectionFrame();
	}


	public boolean newInflectFrame() {
		InflectFrame f=inflectFrameFactory.newInflectFrame();
		if (f==null) return false;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void closeInflectFrame() {
		inflectFrameFactory.closeInflectFrame();
	}

	public boolean newConvertLexiconGrammarFrame() {
		ConvertLexiconGrammarFrame f=convertLexiconGrammarFrameFactory.newConvertLexiconGrammarFrame();
		if (f==null) return false;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void closeConvertLexiconGrammarFrame() {
		convertLexiconGrammarFrameFactory.closeConvertLexiconGrammarFrame();
	}



	private ArrayList<LexiconGrammarTableFrameListener> lgFrameListeners=new ArrayList<LexiconGrammarTableFrameListener>();
	protected boolean firingLGFrame=false;
	
	public void addLexiconGrammarTableFrameListener(LexiconGrammarTableFrameListener l) {
		lgFrameListeners.add(l);
	}
	
	public void removeLexiconGrammarTableFrameListener(LexiconGrammarTableFrameListener l) {
		if (firingLGFrame) {
			throw new IllegalStateException("Cannot remove a listener while firing");
		}
		lgFrameListeners.remove(l);
	}
	
	protected void fireLexiconGrammarTableFrameOpened() {
		firingLGFrame=true;
		try {
			for (LexiconGrammarTableFrameListener l:lgFrameListeners) {
				l.lexiconGrammarTableFrameOpened();
			}
		} finally {
			firingLGFrame=false;
		}
	}

	protected void fireLexiconGrammarTableFrameClosed() {
		firingLGFrame=true;
		try {
			for (LexiconGrammarTableFrameListener l:lgFrameListeners) {
				l.lexiconGrammarTableFrameClosed();
			}
		} finally {
			firingLGFrame=false;
		}
	}

	public boolean newLexiconGrammarTableFrame(File file) {
		LexiconGrammarTableFrame f=lexiconGrammarTableFrameFactory.newLexiconGrammarTableFrame(file);
		if (f==null) return false;
		addToDesktopIfNecessary(f,true);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void closeLexiconGrammarTableFrame() {
		lexiconGrammarTableFrameFactory.closeLexiconGrammarTableFrame();
	}

	public boolean newLocateFrame() {
		return newLocateFrame(null);
	}
	
	public boolean newLocateFrame(File grf) {
		LocateFrame f=locateFrameFactory.newLocateFrame(grf);
		if (f==null) return false;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return true;
	}

	public void closeLocateFrame() {
		locateFrameFactory.closeLocateFrame();
	}
}
