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

import fr.umlv.unitex.FontInfo;
import fr.umlv.unitex.GraphPresentationInfo;
import fr.umlv.unitex.ToDo;
import fr.umlv.unitex.process.commands.MultiCommands;
import fr.umlv.unitex.xalign.ConcordanceModel;

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
	private MessageWhileWorkingFrameFactory messageWhileWorkingFrameFactory=new MessageWhileWorkingFrameFactory();
	private StatisticsFrameFactory statisticsFrameFactory=new StatisticsFrameFactory();
	private HelpOnCommandFrameFactory helpOnCommandFrameFactory=new HelpOnCommandFrameFactory();
	private ProcessInfoFrameFactory processInfoFrameFactory=new ProcessInfoFrameFactory();
	private TranscodingFrameFactory transcodingFrameFactory=new TranscodingFrameFactory();
	private ConsoleFrameFactory consoleFrameFactory=new ConsoleFrameFactory();
	private FileEditionTextFrameFactory fileEditionTextFrameFactory=new FileEditionTextFrameFactory();
	private XAlignConfigFrameFactory xAlignConfigFrameFactory=new XAlignConfigFrameFactory();
	private XAlignFrameFactory xAlignFrameFactory=new XAlignFrameFactory();
	private XAlignLocateFrameFactory xAlignLocateFrameFactory=new XAlignLocateFrameFactory();
	
	private GraphPathDialogFactory graphPathDialogFactory=new GraphPathDialogFactory();
	private PreprocessDialogFactory preprocessDialogFactory=new PreprocessDialogFactory();
	private TranscodeOneFileDialogFactory transcodeOneFileDialogFactory=new TranscodeOneFileDialogFactory();
	private FontDialogFactory fontDialogFactory=new FontDialogFactory();
	private GraphPresentationDialogFactory graphPresentationDialogFactory=new GraphPresentationDialogFactory();
	private GraphAlignmentDialogFactory graphAlignmentDialogFactory=new GraphAlignmentDialogFactory();
	private GraphSizeDialogFactory graphSizeDialogFactory=new GraphSizeDialogFactory();
	
	
	public InternalFrameManager(JDesktopPane desktop) {
		this.desktop=desktop;
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

	
	/**
	 * Creates a GraphFrame for the given .grf file.
	 * If a frame for 'grf' already exists, then it is made visible.
	 * If the .grf is not loadable, the function does nothing and
	 * returns false.
	 * 
	 * @param grf
	 * @return
	 */
	public GraphFrame newGraphFrame(File grf) {
		GraphFrame g=graphFrameFactory.getGraphFrame(grf);
		if (g==null) return null;
		addToDesktopIfNecessary(g,true);
		g.setVisible(true);
		try {
			g.setSelected(true);
			g.setIcon(false);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return g;
	}

	public void closeAllGraphFrames() {
		graphFrameFactory.closeAllGraphFrames();
	}


	public JInternalFrame getSelectedFrame() {
		return desktop.getSelectedFrame();
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
	 * returns null.
	 * 
	 * @param grf
	 * @return
	 */
	public TextFrame newTextFrame(File text,boolean taggedText) {
		TextFrame t=textFrameFactory.newTextFrame(text);
		if (t==null) return null;
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
		return t;
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

	
	public DelaFrame newDelaFrame(File dela) {
		DelaFrame f=delaFrameFactory.newDelaFrame(dela);
		if (f==null) return null;
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
		return f;
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

	
	public TokensFrame newTokensFrame(File tokens) {
		TokensFrame f=tokensFrameFactory.newTokensFrame(tokens);
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
			f.setIcon(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}
	
	public void closeTokensFrame() {
		tokensFrameFactory.closeTokensFrame();
	}

	public TextDicFrame newTextDicFrame(File sntDir,boolean iconify) {
		TextDicFrame f=textDicFrameFactory.newTextDicFrame(sntDir);
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
			f.setIcon(iconify);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}
	
	public void closeTextDicFrame() {
		textDicFrameFactory.closeTextDicFrame();
	}


	public TextAutomatonFrame newTextAutomatonFrame() {
		return newTextAutomatonFrame(1);
	}
	
	public TextAutomatonFrame newTextAutomatonFrame(int sentenceNumber) {
		TextAutomatonFrame f=textAutomatonFrameFactory.newTextAutomatonFrame(sentenceNumber);
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
			f.setIcon(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}
	
	public void closeTextAutomatonFrame() {
		textAutomatonFrameFactory.closeTextAutomatonFrame();
	}

	public AboutUnitexFrame newAboutUnitexFrame() {
		AboutUnitexFrame f=aboutUnitexFrameFactory.newAboutUnitexFrame();
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}


	public ApplyLexicalResourcesFrame newApplyLexicalResourcesFrame() {
		ApplyLexicalResourcesFrame f=applyLexicalResourcesFrameFactory.newApplyLexicalResourcesFrame();
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public void closeApplyLexicalResourcesFrame() {
		applyLexicalResourcesFrameFactory.closeApplyLexicalResourcesFrame();
	}

	public CheckDicFrame newCheckDicFrame() {
		CheckDicFrame f=checkDicFrameFactory.newCheckDicFrame();
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public void closeCheckDicFrame() {
		checkDicFrameFactory.closeCheckDicFrame();
	}

	public CheckResultFrame newCheckResultFrame(File file) {
		CheckResultFrame f=checkResultFrameFactory.newCheckResultFrame(file);
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public void closeCheckResultFrame() {
		checkResultFrameFactory.closeCheckResultFrame();
	}

	public ConcordanceDiffFrame newConcordanceDiffFrame(File file,int widthInChars) {
		ConcordanceDiffFrame f=concordanceDiffFrameFactory.newConcordanceDiffFrame(file,widthInChars);
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public void closeConcordanceDiffFrame() {
		concordanceDiffFrameFactory.closeConcordanceDiffFrame();
	}


	public ConcordanceFrame newConcordanceFrame(File file,int widthInChars) {
		ConcordanceFrame f=concordanceFrameFactory.newConcordanceFrame(file,widthInChars);
		if (f==null) return null;
		addToDesktopIfNecessary(f,true);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public void closeConcordanceFrame() {
		concordanceFrameFactory.closeConcordanceFrame();
	}


	public ConcordanceParameterFrame newConcordanceParameterFrame() {
		return newConcordanceParameterFrame(-1);
	}
	
	public ConcordanceParameterFrame newConcordanceParameterFrame(int matches) {
		ConcordanceParameterFrame f=concordanceParameterFrameFactory.newConcordanceParameterFrame(matches);
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public void closeConcordanceParameterFrame() {
		concordanceParameterFrameFactory.closeConcordanceParameterFrame();
	}


	public ConstructTfstFrame newConstructTfstFrame() {
		ConstructTfstFrame f=constructTfstFrameFactory.newConstructTfstFrame();
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public void closeConstructTfstFrame() {
		constructTfstFrameFactory.closeConstructTfstFrame();
	}


	public ConvertTfstToTextFrame newConvertTfstToTextFrame() {
		ConvertTfstToTextFrame f=convertTfstToTextFrameFactory.newConvertTfstToTextFrame();
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public void closeConvertTfstToTextFrame() {
		convertTfstToTextFrameFactory.closeConvertTfstToTextFrame();
	}


	public ElagCompFrame newElagCompFrame() {
		ElagCompFrame f=elagCompFrameFactory.newElagCompFrame();
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void closeElagCompFrame() {
		elagCompFrameFactory.closeElagCompFrame();
	}


	public GlobalPreferencesFrame newGlobalPreferencesFrame() {
		GlobalPreferencesFrame f=globalPreferencesFrameFactory.newGlobalPreferencesFrame();
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		/* TODO factoriser le code des factory identiques */
		return f;
	}

	public void closeGlobalPreferencesFrame() {
		globalPreferencesFrameFactory.closeGlobalPreferencesFrame();
	}

	public GlobalPreferencesFrame getGlobalPreferencesFrame() {
		return globalPreferencesFrameFactory.getGlobalPreferencesFrame();
	}


	public GraphPathDialog newGraphPathDialog() {
		GraphFrame gf = getCurrentFocusedGraphFrame();
		if (gf == null) {
			return null;
		}
		GraphPathDialog d=graphPathDialogFactory.newGraphPathDialog();
		if (d==null) return null;
		d.graphName.setText(gf.getGraph().getAbsolutePath());
		d.setVisible(true);
		return d;
	}


	public GraphCollectionFrame newGraphCollectionFrame() {
		GraphCollectionFrame f=graphCollectionFrameFactory.newGraphCollectionFrame();
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public void closeGraphCollectionFrame() {
		graphCollectionFrameFactory.closeGraphCollectionFrame();
	}


	public InflectFrame newInflectFrame() {
		InflectFrame f=inflectFrameFactory.newInflectFrame();
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public void closeInflectFrame() {
		inflectFrameFactory.closeInflectFrame();
	}

	public ConvertLexiconGrammarFrame newConvertLexiconGrammarFrame() {
		ConvertLexiconGrammarFrame f=convertLexiconGrammarFrameFactory.newConvertLexiconGrammarFrame();
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
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

	public LexiconGrammarTableFrame newLexiconGrammarTableFrame(File file) {
		LexiconGrammarTableFrame f=lexiconGrammarTableFrameFactory.newLexiconGrammarTableFrame(file);
		if (f==null) return null;
		addToDesktopIfNecessary(f,true);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public void closeLexiconGrammarTableFrame() {
		lexiconGrammarTableFrameFactory.closeLexiconGrammarTableFrame();
	}

	public LocateFrame newLocateFrame() {
		return newLocateFrame(null);
	}
	
	public LocateFrame newLocateFrame(File grf) {
		LocateFrame f=locateFrameFactory.newLocateFrame(grf);
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public void closeLocateFrame() {
		locateFrameFactory.closeLocateFrame();
	}


	public MessageWhileWorkingFrame newMessageWhileWorkingFrame(String title) {
		MessageWhileWorkingFrame f=messageWhileWorkingFrameFactory.newMessageWhileWorkingFrame(title);
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public void closeMessageWhileWorkingFrame() {
		messageWhileWorkingFrameFactory.closeMessageWhileWorkingFrame();
	}

	public MessageWhileWorkingFrame getMessageWhileWorkingFrame() {
		return messageWhileWorkingFrameFactory.getMessageWhileWorkingFrame();
	}


	public PreprocessDialog newPreprocessDialog(File text, File sntFile) {
		return newPreprocessDialog(text, sntFile, false);
	}
	
	public PreprocessDialog newPreprocessDialog(File text, File sntFile, boolean taggedText) {
		PreprocessDialog d=preprocessDialogFactory.newPreprocessDialog(text,sntFile,taggedText);
		if (d==null) return null;
		d.setVisible(true);
		return d;
	}


	public StatisticsFrame newStatisticsFrame(File file,int mode) {
		StatisticsFrame f=statisticsFrameFactory.newStatisticsFrame(file,mode);
		if (f==null) return null;
		addToDesktopIfNecessary(f,true);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public void closeStatisticsFrame() {
		statisticsFrameFactory.closeStatisticsFrame();
	}


	public HelpOnCommandFrame newHelpOnCommandFrame() {
		HelpOnCommandFrame f=helpOnCommandFrameFactory.newHelpOnCommandFrame();
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public void closeHelpOnCommandFrame() {
		helpOnCommandFrameFactory.closeHelpOnCommandFrame();
	}
	

	public ProcessInfoFrame newProcessInfoFrame(MultiCommands c, boolean close, ToDo myDo,
			boolean stopIfProblem) {
		ProcessInfoFrame f=processInfoFrameFactory.newProcessInfoFrame(c,close,myDo,stopIfProblem);
		if (f==null) return null;
		addToDesktopIfNecessary(f,true);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		f.launchBuilderCommands();
		return f;
	}

	public TranscodingFrame newTranscodingFrame() {
		return newTranscodingFrame(null,null);
	}
	
	public TranscodingFrame newTranscodingFrame(File file,ToDo toDo) {
		TranscodingFrame f=transcodingFrameFactory.newTranscodingFrame(file,toDo);
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public ConsoleFrame getConsoleFrame() {
		return newConsoleFrame();
	}

	private ConsoleFrame newConsoleFrame() {
		ConsoleFrame f=consoleFrameFactory.newConsoleFrame();
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		return f;
	}

	public void showConsoleFrame() {
		ConsoleFrame f=getConsoleFrame();
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}


	public FileEditionTextFrame newFileEditionTextFrame(File file) {
		FileEditionTextFrame f=fileEditionTextFrameFactory.getFileEditionTextFrame(file);
		if (f==null) return null;
		addToDesktopIfNecessary(f,true);
		f.setVisible(true);
		try {
			f.setSelected(true);
			f.setIcon(false);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public void closeAllFileEditionTextFrames() {
		fileEditionTextFrameFactory.closeAllFileEditionTextFrames();
	}

	public FileEditionTextFrame getSelectedFileEditionTextFrame() {
		JInternalFrame f=desktop.getSelectedFrame();
		if (f==null || !(f instanceof FileEditionTextFrame)) return null;
		return (FileEditionTextFrame)f;
	}


	public TranscodeOneFileDialog newTranscodeOneFileDialog(File text,ToDo toDo) {
		TranscodeOneFileDialog d=transcodeOneFileDialogFactory.newTranscodeOneFileDialog(text,toDo);
		if (d==null) return null;
		d.setVisible(true);
		return d;
	}


	public XAlignConfigFrame newXAlignConfigFrame() {
		XAlignConfigFrame f=xAlignConfigFrameFactory.newXAlignConfigFrame();
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
			f.setIcon(false);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}


	public XAlignFrame newXAlignFrame(File src,File dst,File alignment) {
		XAlignFrame f=xAlignFrameFactory.newXAlignFrame(src,dst,alignment);
		if (f==null) return null;
		addToDesktopIfNecessary(f,true);
		/* TODO vérifier que les constructeurs des frame ne sont utilisés que dans les factory */
		f.setVisible(true);
		try {
			f.setSelected(true);
			f.setIcon(false);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public void closeXAlignFrame() {
		xAlignFrameFactory.closeXAlignFrame();
	}


	public XAlignLocateFrame newXAlignLocateFrame(String language,File snt,ConcordanceModel model) {
		XAlignLocateFrame f=xAlignLocateFrameFactory.newXAlignLocateFrame(language,snt,model);
		if (f==null) return null;
		addToDesktopIfNecessary(f,false);
		f.setVisible(true);
		try {
			f.setSelected(true);
			f.setIcon(false);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public void closeXAlignLocateFrame() {
		/* TODO mettre un listener pour fermer le XAlignLocateFrame quand on ferme le XAlignFrame */
		xAlignLocateFrameFactory.closeXAlignLocateFrame();
	}

	
	public FontInfo newFontDialog(FontInfo info) {
		FontDialog d=fontDialogFactory.newFontDialog(info);
		if (d==null) return null;
		d.setVisible(true);
		return d.getFontInfo();
	}


	public GraphPresentationInfo newGraphPresentationDialog(GraphPresentationInfo info,
			boolean showRightToLeftCheckBox) {
		GraphPresentationDialog d=graphPresentationDialogFactory.newGraphPresentationDialog(info,showRightToLeftCheckBox);
		if (d==null) return null;
		d.setVisible(true);
		return d.getGraphPresentationInfo();
	}


	public GraphAlignmentDialog newGraphAlignmentDialog(GraphFrame f) {
		GraphAlignmentDialog d=graphAlignmentDialogFactory.newGraphAlignmentDialog(f);
		if (d==null) return null;
		d.setVisible(true);
		return d;
	}


	public GraphSizeDialog newGraphSizeDialog(GraphFrame f) {
		GraphSizeDialog d=graphSizeDialogFactory.newGraphSizeDialog(f);
		if (d==null) return null;
		d.setVisible(true);
		return d;
	}

}
