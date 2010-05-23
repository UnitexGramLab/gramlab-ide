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
import javax.swing.event.InternalFrameListener;

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
	private FrameFactory delaFrameFactory=new FrameFactory(DelaFrame.class);
	private FrameFactory aboutUnitexFrameFactory=new FrameFactory(AboutUnitexFrame.class);
	private FrameFactory applyLexicalResourcesFrameFactory=new FrameFactory(ApplyLexicalResourcesFrame.class);
	private FrameFactory checkDicFrameFactory=new FrameFactory(CheckDicFrame.class);
	private FrameFactory checkResultFrameFactory=new FrameFactory(CheckResultFrame.class);
	private FrameFactory concordanceDiffFrameFactory=new FrameFactory(ConcordanceDiffFrame.class);
	private FrameFactory concordanceParameterFrameFactory=new FrameFactory(ConcordanceParameterFrame.class);
	private FrameFactory constructTfstFrameFactory=new FrameFactory(ConstructTfstFrame.class);
	private FrameFactory convertTfstToTextFrameFactory=new FrameFactory(ConvertTfstToTextFrame.class);
	private FrameFactory elagCompFrameFactory=new FrameFactory(ElagCompFrame.class);
	private FrameFactory globalPreferencesFrameFactory=new FrameFactory(GlobalPreferencesFrame.class);
	private FrameFactory graphCollectionFrameFactory=new FrameFactory(GraphCollectionFrame.class);
	private FrameFactory inflectFrameFactory=new FrameFactory(InflectFrame.class);
	private FrameFactory convertLexiconGrammarFrameFactory=new FrameFactory(ConvertLexiconGrammarFrame.class);
	private FrameFactory locateFrameFactory=new FrameFactory(LocateFrame.class);
	private FrameFactory messageWhileWorkingFrameFactory=new FrameFactory(MessageWhileWorkingFrame.class);
	private FrameFactory helpOnCommandFrameFactory=new FrameFactory(HelpOnCommandFrame.class);
	private FrameFactory transcodingFrameFactory=new FrameFactory(TranscodingFrame.class);
	private FrameFactory consoleFrameFactory=new FrameFactory(ConsoleFrame.class);
	private FrameFactory xAlignLocateFrameFactory=new FrameFactory(XAlignLocateFrame.class);

	private FileEditionTextFrameFactory fileEditionTextFrameFactory=new FileEditionTextFrameFactory();
	private FrameFactory xAlignConfigFrameFactory=new FrameFactory(XAlignConfigFrame.class);
	private GraphFrameFactory graphFrameFactory=new GraphFrameFactory();
	private TextFrameFactory textFrameFactory=new TextFrameFactory();
	private TokensFrameFactory tokensFrameFactory=new TokensFrameFactory();
	private TextDicFrameFactory textDicFrameFactory=new TextDicFrameFactory();
	private TextAutomatonFrameFactory textAutomatonFrameFactory=new TextAutomatonFrameFactory();
	private ConcordanceFrameFactory concordanceFrameFactory=new ConcordanceFrameFactory();
	private LexiconGrammarTableFrameFactory lexiconGrammarTableFrameFactory=new LexiconGrammarTableFrameFactory();
	private StatisticsFrameFactory statisticsFrameFactory=new StatisticsFrameFactory();
	private XAlignFrameFactory xAlignFrameFactory=new XAlignFrameFactory();
	
	private DialogFactory graphPathDialogFactory=new DialogFactory(GraphPathDialog.class);
	private DialogFactory transcodeOneFileDialogFactory=new DialogFactory(TranscodeOneFileDialog.class);
	
	private PreprocessDialogFactory preprocessDialogFactory=new PreprocessDialogFactory();
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

	
	public JInternalFrame setup(JInternalFrame f) {
		return setup(f,false,false);
	}
	
	public JInternalFrame setup(JInternalFrame f,boolean removeOnClose) {
		return setup(f,removeOnClose,false);
	}
	
	public JInternalFrame setup(JInternalFrame f,boolean removeOnClose,boolean iconify) {
		if (f==null) return null;
		addToDesktopIfNecessary(f,removeOnClose);
		f.setVisible(true);
		try {
			f.setSelected(true);
			f.setIcon(iconify);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
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
		return (GraphFrame) setup(graphFrameFactory.getGraphFrame(grf),true);
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
		setup(t,true);
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

	
	private InternalFrameListener delaFrameListener=new InternalFrameAdapter() {
		@Override
		public void internalFrameClosing(InternalFrameEvent e) {
			fireDelaFrameClosed();
		}
	};

	public DelaFrame newDelaFrame(File dela) {
		DelaFrame f=(DelaFrame) delaFrameFactory.newFrame();
		if (f==null) return null;
		f.loadDela(dela);
		/* We don't want to add the same listener twice, so we remove it
		 * and then add it */
		f.removeInternalFrameListener(delaFrameListener);
		f.addInternalFrameListener(delaFrameListener);
		setup(f);
		fireDelaFrameOpened();
		return f;
	}
	
	public void closeDelaFrame() {
		delaFrameFactory.closeFrame();
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
		return (TokensFrame) setup(tokensFrameFactory.newTokensFrame(tokens),false,true);
	}
	
	public void closeTokensFrame() {
		tokensFrameFactory.closeTokensFrame();
	}

	public TextDicFrame newTextDicFrame(File sntDir,boolean iconify) {
		return (TextDicFrame) setup(textDicFrameFactory.newTextDicFrame(sntDir),false,iconify);
	}
	
	public void closeTextDicFrame() {
		textDicFrameFactory.closeTextDicFrame();
	}


	public TextAutomatonFrame newTextAutomatonFrame() {
		return newTextAutomatonFrame(1);
	}
	
	public TextAutomatonFrame newTextAutomatonFrame(int sentenceNumber) {
		return (TextAutomatonFrame) setup(textAutomatonFrameFactory.newTextAutomatonFrame(sentenceNumber),false,true);
	}
	
	public void closeTextAutomatonFrame() {
		textAutomatonFrameFactory.closeTextAutomatonFrame();
	}

	public AboutUnitexFrame newAboutUnitexFrame() {
		return (AboutUnitexFrame) setup(aboutUnitexFrameFactory.newFrame());
	}


	public ApplyLexicalResourcesFrame newApplyLexicalResourcesFrame() {
		ApplyLexicalResourcesFrame f=(ApplyLexicalResourcesFrame) applyLexicalResourcesFrameFactory.newFrame();
		if (f==null) return null;
		f.refreshDicLists();
		setup(f);
		return f;
	}

	public void closeApplyLexicalResourcesFrame() {
		applyLexicalResourcesFrameFactory.closeFrame();
	}

	public CheckDicFrame newCheckDicFrame() {
		return (CheckDicFrame) setup(checkDicFrameFactory.newFrame());
	}

	public void closeCheckDicFrame() {
		checkDicFrameFactory.closeFrame();
	}

	public CheckResultFrame newCheckResultFrame(File file) {
		CheckResultFrame f=(CheckResultFrame)checkResultFrameFactory.newFrame();
		if (f==null) return null;
		f.load(file);
		setup(f);
		return f;
	}

	public void closeCheckResultFrame() {
		checkResultFrameFactory.closeFrame();
	}

	public ConcordanceDiffFrame newConcordanceDiffFrame(File file,int widthInChars) {
		ConcordanceDiffFrame f=(ConcordanceDiffFrame) concordanceDiffFrameFactory.newFrame();
		if (f==null) return null;
		f.load(file,widthInChars);
		setup(f);
		return f;
	}

	public void closeConcordanceDiffFrame() {
		concordanceDiffFrameFactory.closeFrame();
	}

	public ConcordanceFrame newConcordanceFrame(File file,int widthInChars) {
		return (ConcordanceFrame) setup(concordanceFrameFactory.newConcordanceFrame(file,widthInChars),true);
	}

	public void closeConcordanceFrame() {
		concordanceFrameFactory.closeConcordanceFrame();
	}

	public ConcordanceParameterFrame newConcordanceParameterFrame() {
		ConcordanceParameterFrame f=(ConcordanceParameterFrame) concordanceParameterFrameFactory.newFrame();
		if (f==null) return null;
		f.reset();
		setup(f);
		return f;
	}

	public void closeConcordanceParameterFrame() {
		concordanceParameterFrameFactory.closeFrame();
	}

	public ConstructTfstFrame newConstructTfstFrame() {
		return (ConstructTfstFrame) setup(constructTfstFrameFactory.newFrame());
	}

	public void closeConstructTfstFrame() {
		constructTfstFrameFactory.closeFrame();
	}

	public ConvertTfstToTextFrame newConvertTfstToTextFrame() {
		return (ConvertTfstToTextFrame) setup(convertTfstToTextFrameFactory.newFrame());
	}

	public void closeConvertTfstToTextFrame() {
		convertTfstToTextFrameFactory.closeFrame();
	}

	public ElagCompFrame newElagCompFrame() {
		return (ElagCompFrame) setup(elagCompFrameFactory.newFrame());
	}

	public void closeElagCompFrame() {
		elagCompFrameFactory.closeFrame();
	}


	public GlobalPreferencesFrame newGlobalPreferencesFrame() {
		GlobalPreferencesFrame f=(GlobalPreferencesFrame) globalPreferencesFrameFactory.newFrame();
		if (f==null) return null;
		f.reset();
		setup(f);
		return f;
	}

	public void closeGlobalPreferencesFrame() {
		globalPreferencesFrameFactory.closeFrame();
	}

	public GlobalPreferencesFrame getGlobalPreferencesFrame() {
		/* TODO à virer quand le TextFontListener sera dans Pref */
		return (GlobalPreferencesFrame) globalPreferencesFrameFactory.newFrame();
	}

	public GraphPathDialog newGraphPathDialog() {
		GraphFrame gf = getCurrentFocusedGraphFrame();
		if (gf == null) {
			return null;
		}
		GraphPathDialog d=(GraphPathDialog) graphPathDialogFactory.newDialog();
		if (d==null) return null;
		d.graphName.setText(gf.getGraph().getAbsolutePath());
		d.setVisible(true);
		return d;
	}


	public GraphCollectionFrame newGraphCollectionFrame() {
		return (GraphCollectionFrame) setup(graphCollectionFrameFactory.newFrame());
	}

	public void closeGraphCollectionFrame() {
		graphCollectionFrameFactory.closeFrame();
	}

	public InflectFrame newInflectFrame() {
		return (InflectFrame) setup(inflectFrameFactory.newFrame());
	}

	public void closeInflectFrame() {
		inflectFrameFactory.closeFrame();
	}

	public ConvertLexiconGrammarFrame newConvertLexiconGrammarFrame() {
		return (ConvertLexiconGrammarFrame) setup(convertLexiconGrammarFrameFactory.newFrame());
	}

	public void closeConvertLexiconGrammarFrame() {
		convertLexiconGrammarFrameFactory.closeFrame();
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
		return (LexiconGrammarTableFrame) setup(lexiconGrammarTableFrameFactory.newLexiconGrammarTableFrame(file),true);
	}

	public void closeLexiconGrammarTableFrame() {
		lexiconGrammarTableFrameFactory.closeLexiconGrammarTableFrame();
	}

	public LocateFrame newLocateFrame() {
		return newLocateFrame(null);
	}
	
	public LocateFrame newLocateFrame(File grf) {
		LocateFrame f=(LocateFrame) locateFrameFactory.newFrame();
		if (f==null) return null;
		if (grf!=null) {
	        f.graphName.setText(grf.getAbsolutePath());
	        f.graph.setSelected(true);
		}
		setup(f);
		return f;
	}

	public void closeLocateFrame() {
		locateFrameFactory.closeFrame();
	}

	public MessageWhileWorkingFrame newMessageWhileWorkingFrame(String title) {
		MessageWhileWorkingFrame f=(MessageWhileWorkingFrame) messageWhileWorkingFrameFactory.newFrame();
		if (f==null) return null;
		f.setTitle(title);
		f.getLabel().setText("");
		setup(f);
		return f;
	}

	public void closeMessageWhileWorkingFrame() {
		messageWhileWorkingFrameFactory.closeFrame();
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
		return (StatisticsFrame) setup(statisticsFrameFactory.newStatisticsFrame(file,mode),true);
	}

	public void closeStatisticsFrame() {
		statisticsFrameFactory.closeStatisticsFrame();
	}

	public HelpOnCommandFrame newHelpOnCommandFrame() {
		return (HelpOnCommandFrame) setup(helpOnCommandFrameFactory.newFrame());
	}

	public void closeHelpOnCommandFrame() {
		helpOnCommandFrameFactory.closeFrame();
	}

	public ProcessInfoFrame newProcessInfoFrame(MultiCommands c, boolean close, ToDo myDo,
			boolean stopIfProblem) {
		ProcessInfoFrame f=new ProcessInfoFrame(c,close,myDo,stopIfProblem);
		setup(f,true);
		f.launchBuilderCommands();
		return f;
	}

	public TranscodingFrame newTranscodingFrame() {
		return newTranscodingFrame(null,null);
	}
	
	public TranscodingFrame newTranscodingFrame(File file,ToDo toDo) {
		TranscodingFrame f=(TranscodingFrame) transcodingFrameFactory.newFrame();
		if (f==null) return null;
		f.configure(file,toDo);
		setup(f);
		return f;
	}

	public ConsoleFrame getConsoleFrame() {
		return newConsoleFrame();
	}

	private ConsoleFrame newConsoleFrame() {
		ConsoleFrame f=(ConsoleFrame) consoleFrameFactory.newFrame();
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
		return (FileEditionTextFrame) setup(fileEditionTextFrameFactory.getFileEditionTextFrame(file),true);
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
		TranscodeOneFileDialog d=(TranscodeOneFileDialog) transcodeOneFileDialogFactory.newDialog();
		if (d==null) return null;
		d.configure(text,toDo);
		d.setVisible(true);
		return d;
	}

	public XAlignConfigFrame newXAlignConfigFrame() {
		return (XAlignConfigFrame) setup(xAlignConfigFrameFactory.newFrame());
	}

	public XAlignFrame newXAlignFrame(File src,File dst,File alignment) {
		return (XAlignFrame) setup(xAlignFrameFactory.newXAlignFrame(src,dst,alignment),true);
	}

	public void closeXAlignFrame() {
		xAlignFrameFactory.closeXAlignFrame();
	}

	public XAlignLocateFrame newXAlignLocateFrame(String language,File snt,ConcordanceModel model) {
		XAlignLocateFrame f=(XAlignLocateFrame) xAlignLocateFrameFactory.newFrame();
		if (f==null) return null;
		f.configure(language,snt,model);
		setup(f);
		return f;
	}

	public void closeXAlignLocateFrame() {
		/* TODO mettre un listener pour fermer le XAlignLocateFrame quand on ferme le XAlignFrame */
		xAlignLocateFrameFactory.closeFrame();
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

	public FindDialog newFindDialog(FileEditionTextFrame f) {
		FindDialog d=new FindDialog(f);
		d.setVisible(true);
		return d;
	}

}
