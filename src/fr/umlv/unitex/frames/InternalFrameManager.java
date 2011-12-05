/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.diff.GraphDecorator;
import fr.umlv.unitex.graphrendering.ContextsInfo;
import fr.umlv.unitex.grf.GraphPresentationInfo;
import fr.umlv.unitex.io.GraphIO;
import fr.umlv.unitex.listeners.DelaFrameListener;
import fr.umlv.unitex.listeners.LexiconGrammarTableFrameListener;
import fr.umlv.unitex.listeners.TextFrameListener;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.MultiCommands;
import fr.umlv.unitex.process.commands.UnxmlizeCommand;
import fr.umlv.unitex.tfst.TagFilter;
import fr.umlv.unitex.xalign.ConcordanceModel;

/**
 * This class is responsible for managing all internal frames in Unitex
 * main frame.
 *
 * @author paumier
 */
public class InternalFrameManager {

    final JDesktopPane desktop;
    private final static Integer LAYER = 1;
    private final FrameFactory buildKrMwuDicFrameFactory = new FrameFactory(BuildKrMwuDicFrame.class);
    private final FrameFactory aboutUnitexFrameFactory = new FrameFactory(AboutUnitexFrame.class);
    private final FrameFactory applyLexicalResourcesFrameFactory = new FrameFactory(ApplyLexicalResourcesFrame.class);
    private final FrameFactory checkDicFrameFactory = new FrameFactory(CheckDicFrame.class);
    private final FrameFactory transliterationFrameFactory = new FrameFactory(TransliterationFrame.class);
    private final FrameFactory checkResultFrameFactory = new FrameFactory(CheckResultFrame.class);
    private final FrameFactory concordanceDiffFrameFactory = new FrameFactory(ConcordanceDiffFrame.class);
    private final FrameFactory concordanceParameterFrameFactory = new FrameFactory(ConcordanceParameterFrame.class);
    private final FrameFactory constructTfstFrameFactory = new FrameFactory(ConstructTfstFrame.class);
    private final FrameFactory constructSeqTfstFrameFactory = new FrameFactory(ConstructSeqTfstFrame.class);
    private final FrameFactory convertTfstToTextFrameFactory = new FrameFactory(ConvertTfstToTextFrame.class);
    private final FrameFactory dicLookupFrameFactory = new FrameFactory(DicLookupFrame.class);
    private final FrameFactory elagCompFrameFactory = new FrameFactory(ElagCompFrame.class);
    private final FrameFactory globalPreferencesFrameFactory = new FrameFactory(GlobalPreferencesFrame.class);
    private final FrameFactory graphCollectionFrameFactory = new FrameFactory(GraphCollectionFrame.class);
    private final FrameFactory inflectFrameFactory = new FrameFactory(InflectFrame.class);
    private final FrameFactory convertLexiconGrammarFrameFactory = new FrameFactory(ConvertLexiconGrammarFrame.class);
    private final FrameFactory locateFrameFactory = new FrameFactory(LocateFrame.class);
    private final FrameFactory messageWhileWorkingFrameFactory = new FrameFactory(MessageWhileWorkingFrame.class);
    private final FrameFactory helpOnCommandFrameFactory = new FrameFactory(HelpOnCommandFrame.class);
    private final FrameFactory transcodingFrameFactory = new FrameFactory(TranscodingFrame.class);
    private final FrameFactory consoleFrameFactory = new FrameFactory(ConsoleFrame.class);
    private final XAlignLocateFrameFactory xAlignLocateFrameFactory = new XAlignLocateFrameFactory();
    private final FrameFactory svnConflictsFrameFactory = new FrameFactory(SvnConflictsFrame.class);

    private final FrameFactory cassysFrameFactory = new FrameFactory(CassysFrame.class);
    private final FrameFactory transducerListConfigurationFrameFactory = new FrameFactory(TransducerListConfigurationFrame.class);

    private final FileEditionTextFrameFactory fileEditionTextFrameFactory = new FileEditionTextFrameFactory();
    private final FrameFactory xAlignConfigFrameFactory = new FrameFactory(XAlignConfigFrame.class);
    private final GraphFrameFactory graphFrameFactory = new GraphFrameFactory();
    private final MultiInstanceFrameFactory<DelaFrame,File> delaFrameFactory = new MultiInstanceFrameFactory<DelaFrame,File>();
    private final TextFrameFactory textFrameFactory = new TextFrameFactory();
    private final TokensFrameFactory tokensFrameFactory = new TokensFrameFactory();
    private final TfstTagsFrameFactory tfstTagsFrameFactory = new TfstTagsFrameFactory();
    private final TextDicFrameFactory textDicFrameFactory = new TextDicFrameFactory();
    private final TextAutomatonFrameFactory textAutomatonFrameFactory = new TextAutomatonFrameFactory();
    private final ConcordanceFrameFactory concordanceFrameFactory = new ConcordanceFrameFactory();
    private final LexiconGrammarTableFrameFactory lexiconGrammarTableFrameFactory = new LexiconGrammarTableFrameFactory();
    private final StatisticsFrameFactory statisticsFrameFactory = new StatisticsFrameFactory();
    private final XAlignFrameFactory xAlignFrameFactory = new XAlignFrameFactory();

    private final DialogFactory graphPathDialogFactory = new DialogFactory(GraphPathDialog.class);
    private final DialogFactory transcodeOneFileDialogFactory = new DialogFactory(TranscodeOneFileDialog.class);
    private final DialogFactory listCopyDialogFactory = new DialogFactory(ListCopyDialog.class);
    
    private final PreprocessDialogFactory preprocessDialogFactory = new PreprocessDialogFactory();
    private final FontDialogFactory fontDialogFactory = new FontDialogFactory();
    private final GraphPresentationDialogFactory graphPresentationDialogFactory = new GraphPresentationDialogFactory();
    private final GraphAlignmentDialogFactory graphAlignmentDialogFactory = new GraphAlignmentDialogFactory();
    private final GraphSizeDialogFactory graphSizeDialogFactory = new GraphSizeDialogFactory();
    private final ExportTextAsPOSListDialogFactory exportTextAsPOSListDialogFactory = new ExportTextAsPOSListDialogFactory();

    private static InternalFrameManager manager;
    
    /**
     * If the frame manager has submanagers, it must override
     * this method to get the actual manager to be used.
     * This getter is designed for compatibility with Gramlab. 
     * Unitex does not use the resource parameter, but gramlab will.
     */
    public InternalFrameManager getSubManager(@SuppressWarnings("unused") File resource) {
    	return this;
    }
    
    /**
     * Here we have a static access to the main frame manager.
     * This getter is designed for compatibility with Gramlab.
     */
    public static InternalFrameManager getManager(File resource) {
    	return manager.getSubManager(resource);
    }
    
    public static void setManager(InternalFrameManager m) {
    	manager=m;
    }
    
    public InternalFrameManager(JDesktopPane desktop) {
        this.desktop = desktop;
    }

    public JDesktopPane getDesktop() {
		return desktop;
	}
    
    private void addToDesktopIfNecessary(final JInternalFrame f, boolean removeOnClose) {
        for (JInternalFrame frame : desktop.getAllFrames()) {
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
        desktop.add(f, LAYER);
    }


    JInternalFrame setup(JInternalFrame f) {
        return setup(f, false, false);
    }

    JInternalFrame setup(JInternalFrame f, boolean removeOnClose) {
        return setup(f, removeOnClose, false);
    }

    JInternalFrame setup(JInternalFrame f, boolean removeOnClose, boolean iconify) {
        if (f == null) return null;
        addToDesktopIfNecessary(f, removeOnClose);
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
        return (GraphFrame) setup(graphFrameFactory.getGraphFrame(grf), true);
    }

    public void closeAllGraphFrames() {
        graphFrameFactory.closeAllFrames();
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

    public ArrayList<GraphFrame> getGraphFrames() {
        return graphFrameFactory.getFrames();
    }


    /**
     * Creates a TextFrame for the given text file.
     * If the text is not loadable, the function does nothing and
     * returns null.
     *
     * @param grf
     * @return
     */
    public TextFrame newTextFrame(File text, boolean taggedText) {
        TextFrame t = textFrameFactory.newTextFrame(text);
        if (t == null) return null;
        t.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                fireTextFrameClosed();
            }
        });
        setup(t, true);
        fireTextFrameOpened(taggedText);
        return t;
    }

    public void closeTextFrame() {
        textFrameFactory.closeTextFrame();
    }

    public TextFrame getTextFrame() {
        return textFrameFactory.getTextFrame();
    }

    public GraphDiffFrame newGraphDiffFrame(File fbase,File fdest,GraphIO base,GraphIO dest,GraphDecorator diff) {
    	GraphDiffFrame f=new GraphDiffFrame(fbase,fdest,base,dest,diff);
        return (GraphDiffFrame) setup(f,true);
    }

    private final ArrayList<TextFrameListener> textFrameListeners = new ArrayList<TextFrameListener>();
    private boolean firingTextFrame = false;

    public void addTextFrameListener(TextFrameListener l) {
        textFrameListeners.add(l);
    }

    public void removeTextFrameListener(TextFrameListener l) {
        if (firingTextFrame) {
            throw new IllegalStateException("Cannot remove a listener while firing");
        }
        textFrameListeners.remove(l);
    }

    void fireTextFrameOpened(boolean taggedText) {
        firingTextFrame = true;
        try {
            for (TextFrameListener l : textFrameListeners) {
                l.textFrameOpened(taggedText);
            }
        } finally {
            firingTextFrame = false;
        }
    }

    void fireTextFrameClosed() {
        firingTextFrame = true;
        try {
            for (TextFrameListener l : textFrameListeners) {
                l.textFrameClosed();
            }
        } finally {
            firingTextFrame = false;
        }
    }


    private final InternalFrameListener delaFrameListener = new InternalFrameAdapter() {
        @Override
        public void internalFrameClosing(InternalFrameEvent e) {
            fireDelaFrameClosed(delaFrameFactory.getFrameCount()-1);
        }
    };

    public DelaFrame newDelaFrame(File dela) {
        DelaFrame f = delaFrameFactory.getFrameIfExists(dela);
        if (f!=null) return f;
        f=new DelaFrame();
        f.loadDela(dela);
        delaFrameFactory.addFrame(f);
        f.addInternalFrameListener(delaFrameListener);
        setup(f);
        fireDelaFrameOpened();
        return f;
    }
    
    public void closeCurrentDelaFrame() {
    	File f=Config.getCurrentDELA();
    	if (f==null) return;
    	DelaFrame frame=delaFrameFactory.getFrameIfExists(f);
    	if (frame!=null) {
    		frame.doDefaultCloseAction();
    	}
    }

    private final ArrayList<DelaFrameListener> delaFrameListeners = new ArrayList<DelaFrameListener>();
    private boolean firingDelaFrame = false;

    public void addDelaFrameListener(DelaFrameListener l) {
        delaFrameListeners.add(l);
    }

    public void removeDelaFrameListener(DelaFrameListener l) {
        if (firingDelaFrame) {
            throw new IllegalStateException("Cannot remove a listener while firing");
        }
        delaFrameListeners.remove(l);
    }

    void fireDelaFrameOpened() {
        firingDelaFrame = true;
        try {
            for (DelaFrameListener l : delaFrameListeners) {
                l.delaFrameOpened();
            }
        } finally {
            firingDelaFrame = false;
        }
    }

    void fireDelaFrameClosed(int remainingFrames) {
        firingDelaFrame = true;
        try {
            for (DelaFrameListener l : delaFrameListeners) {
                l.delaFrameClosed(remainingFrames);
            }
        } finally {
            firingDelaFrame = false;
        }
    }


    public TokensFrame newTokensFrame(File tokens) {
        return (TokensFrame) setup(tokensFrameFactory.newTokensFrame(tokens), false, true);
    }

    public void closeTokensFrame() {
        tokensFrameFactory.closeTokensFrame();
    }

    public TfstTagsFrame newTfstTagsFrame(File tags) {
        return (TfstTagsFrame) setup(tfstTagsFrameFactory.newTfstTagsFrame(tags), false, true);
    }

    public void closeTfstTagsFrame() {
        tfstTagsFrameFactory.closeTfstTagsFrame();
    }

    public TextDicFrame newTextDicFrame(File sntDir, boolean iconify) {
        return (TextDicFrame) setup(textDicFrameFactory.newTextDicFrame(sntDir), false, iconify);
    }

    public void closeTextDicFrame() {
        textDicFrameFactory.closeTextDicFrame();
    }


    public TextAutomatonFrame newTextAutomatonFrame(int sentenceNumber, boolean iconify) {
        return (TextAutomatonFrame) setup(textAutomatonFrameFactory.newTextAutomatonFrame(sentenceNumber), false, iconify);
    }

    public void closeTextAutomatonFrame() {
        textAutomatonFrameFactory.closeTextAutomatonFrame();
    }

    public AboutUnitexFrame newAboutUnitexFrame() {
        return (AboutUnitexFrame) setup(aboutUnitexFrameFactory.newFrame());
    }


    public ApplyLexicalResourcesFrame newApplyLexicalResourcesFrame() {
        ApplyLexicalResourcesFrame f = (ApplyLexicalResourcesFrame) applyLexicalResourcesFrameFactory.newFrame();
        if (f == null) return null;
        f.refreshDicLists();
        setup(f);
        return f;
    }

    public void closeApplyLexicalResourcesFrame() {
        applyLexicalResourcesFrameFactory.closeFrame();
    }

    public DicLookupFrame newDicLookupFrame() {
    	DicLookupFrame f = (DicLookupFrame) dicLookupFrameFactory.newFrame(false);
        if (f == null) return null;
        setup(f);
        return f;
    }

    public void closeDicLookupFrame() {
    	dicLookupFrameFactory.closeFrame();
    }

    public CassysFrame newCassysFrame() {
        CassysFrame f = (CassysFrame) cassysFrameFactory.newFrame();
        if (f == null) return null;
        setup(f);
        return f;
    }

    public void closeCassysFrame() {
        cassysFrameFactory.closeFrame();
    }

    public TransducerListConfigurationFrame newTransducerListConfigurationFrame(File file) {
        TransducerListConfigurationFrame f = (TransducerListConfigurationFrame) transducerListConfigurationFrameFactory.newFrame();
        if (f == null) return null;

        f.setConfigurationHasChanged(false);
        f.setFrameTitle();
        if (file != null) {
            f.fill_table(file);
        }

        setup(f);
        return f;
    }

    public void closeTransducerListConfigurationFrame() {
        transducerListConfigurationFrameFactory.closeFrame();
    }


    public CheckDicFrame newCheckDicFrame() {
        return (CheckDicFrame) setup(checkDicFrameFactory.newFrame());
    }

    public void closeCheckDicFrame() {
        checkDicFrameFactory.closeFrame();
    }

    public TransliterationFrame newTransliterationFrame() {
        return (TransliterationFrame) setup(transliterationFrameFactory.newFrame());
    }

    public void closeTransliterationFrame() {
        transliterationFrameFactory.closeFrame();
    }

    public CheckResultFrame newCheckResultFrame(File file) {
        CheckResultFrame f = (CheckResultFrame) checkResultFrameFactory.newFrame();
        if (f == null) return null;
        f.load(file);
        setup(f);
        return f;
    }

    public void closeCheckResultFrame() {
        checkResultFrameFactory.closeFrame();
    }

    public ConcordanceDiffFrame newConcordanceDiffFrame(File file) {
        ConcordanceDiffFrame f = (ConcordanceDiffFrame) concordanceDiffFrameFactory.newFrame();
        if (f == null) return null;
        f.load(file);
        setup(f,true);
        return f;
    }

    public void closeConcordanceDiffFrame() {
        concordanceDiffFrameFactory.closeFrame();
    }

    public ConcordanceFrame newConcordanceFrame(File file, int widthInChars) {
        return (ConcordanceFrame) setup(concordanceFrameFactory.newConcordanceFrame(file, widthInChars), true);
    }

    public void closeConcordanceFrame() {
        concordanceFrameFactory.closeConcordanceFrame();
    }

    public ConcordanceParameterFrame newConcordanceParameterFrame() {
        ConcordanceParameterFrame f = (ConcordanceParameterFrame) concordanceParameterFrameFactory.newFrame();
        if (f == null) return null;
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

    public ConstructSeqTfstFrame newConstructSeqTfstFrame(){
    	return (ConstructSeqTfstFrame) setup(constructSeqTfstFrameFactory.newFrame());
    }
    
    public void closeConstructSeqTfstFrame(){
    	constructSeqTfstFrameFactory.closeFrame();
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
        GlobalPreferencesFrame f = (GlobalPreferencesFrame) globalPreferencesFrameFactory.newFrame();
        if (f == null) return null;
        f.reset();
        setup(f);
        return f;
    }

    public void closeGlobalPreferencesFrame() {
        globalPreferencesFrameFactory.closeFrame();
    }

    public GraphPathDialog newGraphPathDialog() {
        GraphFrame gf = getCurrentFocusedGraphFrame();
        if (gf == null) {
            return null;
        }
        GraphPathDialog d = (GraphPathDialog) graphPathDialogFactory.newDialog();
        if (d == null) return null;
        File f=gf.getGraph();
        d.graphName.setText(f.getAbsolutePath());
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

    public ConvertLexiconGrammarFrame newConvertLexiconGrammarFrame(File table) {
        ConvertLexiconGrammarFrame f = (ConvertLexiconGrammarFrame) convertLexiconGrammarFrameFactory.newFrame();
        if (f == null) return null;
        f.setupTable(table);
        setup(f);
        return f;
    }

    public void closeConvertLexiconGrammarFrame() {
        convertLexiconGrammarFrameFactory.closeFrame();
    }


    private final ArrayList<LexiconGrammarTableFrameListener> lgFrameListeners = new ArrayList<LexiconGrammarTableFrameListener>();
    private boolean firingLGFrame = false;

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
        firingLGFrame = true;
        try {
            for (LexiconGrammarTableFrameListener l : lgFrameListeners) {
                l.lexiconGrammarTableFrameOpened();
            }
        } finally {
            firingLGFrame = false;
        }
    }

    protected void fireLexiconGrammarTableFrameClosed() {
        firingLGFrame = true;
        try {
            for (LexiconGrammarTableFrameListener l : lgFrameListeners) {
                l.lexiconGrammarTableFrameClosed();
            }
        } finally {
            firingLGFrame = false;
        }
    }

    private final InternalFrameListener lgTableFrameListener = new InternalFrameAdapter() {
        @Override
        public void internalFrameClosing(InternalFrameEvent e) {
        	fireLexiconGrammarTableFrameClosed();
        }
    };

    public LexiconGrammarTableFrame newLexiconGrammarTableFrame(File file) {
    	LexiconGrammarTableFrame f=(LexiconGrammarTableFrame) setup(lexiconGrammarTableFrameFactory.newLexiconGrammarTableFrame(file), true);
    	/* We don't want the same listener to be added twice */
    	f.removeInternalFrameListener(lgTableFrameListener);
    	f.addInternalFrameListener(lgTableFrameListener);
    	fireLexiconGrammarTableFrameOpened();
    	return f;
        
    }

    public void closeLexiconGrammarTableFrame() {
        lexiconGrammarTableFrameFactory.closeLexiconGrammarTableFrame();
    }

    public LexiconGrammarTableFrame getLexiconGrammarTableFrame() {
        return lexiconGrammarTableFrameFactory.getFrame();
    }

    public LocateFrame newLocateFrame() {
        return newLocateFrame(null);
    }

    public LocateFrame newLocateFrame(File grf) {
        LocateFrame f = (LocateFrame) locateFrameFactory.newFrame();
        if (f == null) return null;
        if (grf != null) {
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
        MessageWhileWorkingFrame f = (MessageWhileWorkingFrame) messageWhileWorkingFrameFactory.newFrame();
        if (f == null) return null;
        f.setTitle(title);
        f.getLabel().setText("");
        setup(f);
        return f;
    }

    public void closeMessageWhileWorkingFrame() {
        messageWhileWorkingFrameFactory.closeFrame();
    }

    public PreprocessDialog newPreprocessDialog(File text, File sntFile) {
        return newPreprocessDialog(text, sntFile, false, null);
    }

    public PreprocessDialog newPreprocessDialog(File text, File sntFile, boolean taggedText,
    		UnxmlizeCommand cmd) {
        PreprocessDialog d = preprocessDialogFactory.newPreprocessDialog(text, sntFile, taggedText, cmd);
        if (d == null) return null;
        d.setVisible(true);
        return d;
    }


    public StatisticsFrame newStatisticsFrame(File file, int mode) {
        return (StatisticsFrame) setup(statisticsFrameFactory.newStatisticsFrame(file, mode), true);
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
        ProcessInfoFrame f = new ProcessInfoFrame(c, close, myDo, stopIfProblem);
        setup(f, true);
        f.launchBuilderCommands();
        return f;
    }

    public TranscodingFrame newTranscodingFrame() {
        return newTranscodingFrame(null, null, false);
    }

    public TranscodingFrame newTranscodingFrame(File file, ToDo toDo,boolean closeAfterWork) {
        TranscodingFrame f = (TranscodingFrame) transcodingFrameFactory.newFrame();
        if (f == null) return null;
        f.configure(file, toDo, closeAfterWork);
        setup(f);
        return f;
    }

    public ConsoleFrame getConsoleFrame() {
        return newConsoleFrame();
    }

    private ConsoleFrame newConsoleFrame() {
        ConsoleFrame f = (ConsoleFrame) consoleFrameFactory.newFrame(false);
        if (f == null) return null;
        addToDesktopIfNecessary(f, false);
        return f;
    }

    public void showConsoleFrame() {
        ConsoleFrame f = getConsoleFrame();
        f.setVisible(true);
        try {
            f.setSelected(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public SvnConflictsFrame getSvnConflictsFrame() {
        return newSvnConflictsFrame();
    }

    private SvnConflictsFrame newSvnConflictsFrame() {
    	SvnConflictsFrame f = (SvnConflictsFrame) svnConflictsFrameFactory.newFrame(false);
        if (f == null) return null;
        addToDesktopIfNecessary(f, false);
        return f;
    }

    public void showSvnConflictsFrame() {
    	SvnConflictsFrame f = getSvnConflictsFrame();
        f.setVisible(true);
    }

    public FileEditionTextFrame newFileEditionTextFrame(File file) {
        return (FileEditionTextFrame) setup(fileEditionTextFrameFactory.getFileEditionTextFrame(file), true);
    }

    public void closeAllFileEditionTextFrames() {
        fileEditionTextFrameFactory.closeAllFileEditionTextFrames();
    }

    public FileEditionTextFrame getSelectedFileEditionTextFrame() {
        JInternalFrame f = desktop.getSelectedFrame();
        if (f == null || !(f instanceof FileEditionTextFrame)) return null;
        return (FileEditionTextFrame) f;
    }


    public TranscodeOneFileDialog newTranscodeOneFileDialog(File text, ToDo toDo) {
        TranscodeOneFileDialog d = (TranscodeOneFileDialog) transcodeOneFileDialogFactory.newDialog();
        if (d == null) return null;
        d.configure(text, toDo);
        d.setVisible(true);
        return d;
    }

    public XAlignConfigFrame newXAlignConfigFrame() {
        return (XAlignConfigFrame) setup(xAlignConfigFrameFactory.newFrame());
    }

    public XAlignFrame newXAlignFrame(File src, File dst, File alignment) {
        return (XAlignFrame) setup(xAlignFrameFactory.newXAlignFrame(src, dst, alignment), true);
    }

    public void closeXAlignFrame() {
        xAlignFrameFactory.closeXAlignFrame();
    }

    public XAlignLocateFrame newXAlignLocateFrame(String language, File snt, ConcordanceModel model) {
        XAlignLocateFrame f = xAlignLocateFrameFactory.newXAlignLocateFrame(language);
        if (f == null) return null;
        f.configure(language, snt, model);
        setup(f);
        return f;
    }

    public void closeXAlignLocateFrame() {
        xAlignLocateFrameFactory.closeXAlignLocateFrame();
    }

    public FontInfo newFontDialog(FontInfo info) {
        FontDialog d = fontDialogFactory.newFontDialog(info);
        if (d == null) return null;
        d.setVisible(true);
        return d.getFontInfo();
    }

    public GraphPresentationInfo newGraphPresentationDialog(GraphPresentationInfo info,
                                                            boolean showRightToLeftCheckBox) {
        GraphPresentationDialog d = graphPresentationDialogFactory.newGraphPresentationDialog(info, showRightToLeftCheckBox);
        if (d == null) return null;
        d.setVisible(true);
        return d.getGraphPresentationInfo();
    }

    public GraphAlignmentDialog newGraphAlignmentDialog(GraphFrame f) {
        GraphAlignmentDialog d = graphAlignmentDialogFactory.newGraphAlignmentDialog(f);
        if (d == null) return null;
        d.setVisible(true);
        return d;
    }

    public GraphSizeDialog newGraphSizeDialog(GraphFrame f) {
        GraphSizeDialog d = graphSizeDialogFactory.newGraphSizeDialog(f);
        if (d == null) return null;
        d.setVisible(true);
        return d;
    }

    public FindDialog newFindDialog(FileEditionTextFrame f) {
        FindDialog d = new FindDialog(f);
        d.setVisible(true);
        return d;
    }

    public void closeAllFrames() {
        for (JInternalFrame f : desktop.getAllFrames()) {
            f.doDefaultCloseAction();
        }
    }

    public ContextsInfo newListCopyDialog() {
        ListCopyDialog d = (ListCopyDialog) listCopyDialogFactory.newDialog();
        if (d == null) return null;
        d.setVisible(true);
        return d.getContextsInfo();
    }

    public String newVariableInsertionDialog(boolean inputVariable) {
    	VariableInsertionDialog d = new VariableInsertionDialog(inputVariable);
        d.setVisible(true);
        return d.getVariableName();
    }

    public ExportTextAsPOSListDialog newExportTextAsPOSListDialog(File output, TagFilter filter) {
        ExportTextAsPOSListDialog d = exportTextAsPOSListDialogFactory.newExportTextAsPOSListDialog(output, filter);
        d.launch();
        d.setVisible(true);
        return d;
    }

    public TextAutomatonFrame getTextAutomatonFrame() {
        return textAutomatonFrameFactory.getFrame();
    }

    public BuildKrMwuDicFrame newBuildKrMwuDicFrame() {
        return (BuildKrMwuDicFrame) setup(buildKrMwuDicFrameFactory.newFrame());
    }

}
