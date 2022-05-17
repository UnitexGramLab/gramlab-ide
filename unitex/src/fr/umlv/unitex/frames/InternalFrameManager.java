/*
 * Unitex
 *
 * Copyright (C) 2001-2021 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import fr.umlv.unitex.FontInfo;
import fr.umlv.unitex.Unitex;
import fr.umlv.unitex.common.frames.manager.FrameManager;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.config.PreferencesManager;
import fr.umlv.unitex.diff.GraphDecorator;
import fr.umlv.unitex.exceptions.UserRefusedFrameClosingError;
import fr.umlv.unitex.grf.GraphPresentationInfo;
import fr.umlv.unitex.io.GraphIO;
import fr.umlv.unitex.listeners.DelaFrameListener;
import fr.umlv.unitex.listeners.LexiconGrammarTableFrameListener;
import fr.umlv.unitex.listeners.TextFrameListener;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.MultiCommands;
import fr.umlv.unitex.svn.SvnMonitor;
import fr.umlv.unitex.tfst.TagFilter;
import java.util.List;
import java.util.Map;
import fr.umlv.unitex.leximir.delas.*;
import fr.umlv.unitex.leximir.helper.*;
import fr.umlv.unitex.leximir.shell.*;


/**
 * This class is responsible for managing all internal frames in Unitex and GramLab main
 * frame.
 *
 * @author paumier
 */
public abstract class InternalFrameManager implements FrameManager {
	final JDesktopPane desktop;
	private final static Integer LAYER = 1;
	private final static Integer CONSOLE_LAYER = 10;
	private final FrameFactory applyLexicalResourcesFrameFactory = new FrameFactory(
			ApplyLexicalResourcesFrame.class);
	private final FrameFactory checkDicFrameFactory = new FrameFactory(
			CheckDicFrame.class);
	private final FrameFactory checkResultFrameFactory = new FrameFactory(
			CheckResultFrame.class);
	private final FrameFactory concordanceDiffFrameFactory = new FrameFactory(
			ConcordanceDiffFrame.class);
	private final FrameFactory concordanceParameterFrameFactory = new FrameFactory(
			ConcordanceParameterFrame.class);
	private final FrameFactory seq2GrfFrameFactory = new FrameFactory(
			Seq2GrfFrame.class);
	private final FrameFactory graphCollectionFrameFactory = new FrameFactory(
			GraphCollectionFrame.class);
	private final FrameFactory inflectFrameFactory = new FrameFactory(
			InflectFrame.class);
	private final FrameFactory convertLexiconGrammarFrameFactory = new FrameFactory(
			ConvertLexiconGrammarFrame.class);
	private final FrameFactory locateFrameFactory = new FrameFactory(
			LocateFrame.class);
	private final FrameFactory transcodingFrameFactory = new FrameFactory(
			TranscodingFrame.class);
	private final FrameFactory consoleFrameFactory = new FrameFactory(
			ConsoleFrame.class);
	private final SvnConflictsFrameFactory svnConflictsFrameFactory = new SvnConflictsFrameFactory();
	private final TransducerListConfigurationFrameFactory transducerListConfigurationFrameFactory =
			new TransducerListConfigurationFrameFactory();
	private final FileEditionTextFrameFactory fileEditionTextFrameFactory = new FileEditionTextFrameFactory();
	private final GraphFrameFactory graphFrameFactory = new GraphFrameFactory();
	final MultiInstanceFrameFactory<DelaFrame, File> delaFrameFactory = new MultiInstanceFrameFactory<DelaFrame, File>();
	private final TextFrameFactory textFrameFactory = new TextFrameFactory();
	private final TokensFrameFactory tokensFrameFactory = new TokensFrameFactory();
	private final TfstTagsFrameFactory tfstTagsFrameFactory = new TfstTagsFrameFactory();
	private final TextDicFrameFactory textDicFrameFactory = new TextDicFrameFactory();
	private final TextAutomatonFrameFactory textAutomatonFrameFactory = new TextAutomatonFrameFactory();
	private final ConcordanceFrameFactory concordanceFrameFactory = new ConcordanceFrameFactory();
	private final LexiconGrammarTableFrameFactory lexiconGrammarTableFrameFactory = new LexiconGrammarTableFrameFactory();
	private final StatisticsFrameFactory statisticsFrameFactory = new StatisticsFrameFactory();
	private final FrameFactory graphPathFrameFactory = new FrameFactory(
			GraphPathFrame.class);
	private final DialogFactory transcodeOneFileDialogFactory = new DialogFactory(
			TranscodeOneFileDialog.class);
	private final FontDialogFactory fontDialogFactory = new FontDialogFactory();
	private final GraphPresentationDialogFactory graphPresentationDialogFactory = new GraphPresentationDialogFactory();
	private final GraphAlignmentDialogFactory graphAlignmentDialogFactory = new GraphAlignmentDialogFactory();
	private final GraphSizeDialogFactory graphSizeDialogFactory = new GraphSizeDialogFactory();
	private final ExportTextAsPOSListDialogFactory exportTextAsPOSListDialogFactory = new ExportTextAsPOSListDialogFactory();
  	private final FindAndReplaceDialogFactory findAndReplaceFactory = new FindAndReplaceDialogFactory();
  	private final TextAutomatonFindAndReplaceDialogFactory textAutomatonFindAndReplaceFactory = new TextAutomatonFindAndReplaceDialogFactory();
	private final TextAutomatonTagFilterDialogFactory textAutomatonTagFilterFactory = new TextAutomatonTagFilterDialogFactory();
	private final CheckTextAutomatonDialogFactory checkTextAutomatonDialogFactory = new CheckTextAutomatonDialogFactory();
    private final ChooseDelasFactory chooseDelasFactory = new ChooseDelasFactory();
    private final ShellFactory shellFactory = new ShellFactory();
    private final EditorDelasFactory editorDelasFactory = new EditorDelasFactory();
    private final StatisticOutputFactory statisticOutputFactory = new StatisticOutputFactory();
    private final CsvOpenerFactory csvOpenerFactory = new CsvOpenerFactory();

	public InternalFrameManager(JDesktopPane desktop) {
		this.desktop = desktop;
	}

	public JDesktopPane getDesktop() {
		return desktop;
	}

	private void addToDesktopIfNecessary(final JInternalFrame f,
			boolean removeOnClose) {
		for (final JInternalFrame frame : desktop.getAllFrames()) {
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
		if (f instanceof ProcessInfoFrame) {
			desktop.add(f, CONSOLE_LAYER);
		} else {
			desktop.add(f, LAYER);
		}
	}
	/**
	 * this function appears to be updating the main Unitex frame, 
	 * Background or non existent frames passed as arguments get drawn on the foreground.
	 * @param f
	 *    the frame intended to be  in the foreground with focus.
	 * @return
	 */
	protected JInternalFrame setup(JInternalFrame f) {
		return setup(f, false, false);
	}

	JInternalFrame setup(JInternalFrame f, boolean removeOnClose) {
		return setup(f, removeOnClose, false);
	}

	JInternalFrame setup(JInternalFrame f, boolean removeOnClose,
			boolean iconify) {
		if (f == null)
			return null;
		addToDesktopIfNecessary(f, removeOnClose);
		f.setVisible(true);
		try {
			f.setSelected(true);
			f.setIcon(iconify);
		} catch (final PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	/**
	 * Creates a GraphFrame for the given .grf file. If a frame for 'grf'
	 * already exists, then it is made visible. If the .grf is not loadable, the
	 * function does nothing and returns false.
	 *
	 * @param grf
	 * @return
	 */
	public GraphFrame newGraphFrame(File grf) {
		final GraphFrame g = (GraphFrame) setup(
				graphFrameFactory.getGraphFrame(grf), true);
		if (g == null)
			return null;
		if (ConfigManager.getManager().maximizeGraphFrames()) {
			try {
				g.setMaximum(true);
			} catch (final PropertyVetoException e1) {
				/* */
			}
		}
		if(Unitex.isRunning()) {
			PreferencesManager.getUserPreferences().addRecentGraph(grf);
		}
		return g;
	}

	public void saveAllGraphFrames() {
		graphFrameFactory.saveAllFrames();
	}
	
	
    public ChooseDelas newChooseDelasDialog() {

        final ChooseDelas d = chooseDelasFactory.newChooseDelasDialog();
        if (d == null) {
            return null;
        }
        setup(d);
        return d;
    }

    public Shell newShellDialog() {
        final Shell d = shellFactory.newShellDialog();
        if (d == null) {
            return null;
        }
        setup(d, true);
        return d;
    }

    public EditorDelas newEditorDelasDialog(boolean alldelas, File dic) {
        final EditorDelas d = editorDelasFactory.newEditorDelasDialog(alldelas, dic);
        if (d == null) {
            return null;
        }
        setup(d, true);
        return d;
    }


    public StatisticOutput newStatisticOutput(List<Object[]> dicPos, boolean isDelas) {
        final StatisticOutput d = statisticOutputFactory.newStatisticOutputDialog(dicPos, isDelas);
        if (d == null) {
            return null;
        }
        setup(d, true);
        return d;
    }

    public StatisticOutput newStatisticOutput(Map<String, Object[]> statSimSem, boolean isDelas) {
        final StatisticOutput d = statisticOutputFactory.newStatisticOutputDialog(statSimSem, isDelas);
        if (d == null) {
            return null;
        }
        setup(d, true);
        return d;
    }

    public CsvOpener newCsvOpener(String csvFile, String title) {
        final CsvOpener d = csvOpenerFactory.newCsvOpenerDialog(csvFile, title);
        if (d == null) {
            return null;
        }
        setup(d, true);
        return d;
    }

	/**
	 * This method implements the functionality to minimize the currently focused frame.
	 *
	 * @author Mukarram Tailor
	 */
	public void minimizeCurrentFocusedFrame(){
		JInternalFrame frame = getSelectedFrame();
		try {
			frame.setIcon(true);
		} catch (final java.beans.PropertyVetoException e) {
			e.printStackTrace();
		}
		catch (NullPointerException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * This method implements the functionality to close the currently focused frame.
	 *
	 * @author Mukarram Tailor
	 */
	public void closeCurrentFocusedFrame(){
		JInternalFrame frame = getSelectedFrame();
		try {
			frame.doDefaultCloseAction();
		} catch (final NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void closeAllGraphFrames() {
		graphFrameFactory.closeAllFrames();
	}

	public JInternalFrame getSelectedFrame() {
		return desktop.getSelectedFrame();
	}

	public GraphFrame getCurrentFocusedGraphFrame() {
		final JInternalFrame frame = desktop.getSelectedFrame();
		if (frame instanceof GraphFrame)
			return (GraphFrame) frame;
		return null;
	}

	public ConcordanceFrame getCurrentFocusedConcordance() {
		final JInternalFrame frame = desktop.getSelectedFrame();
		if (frame instanceof ConcordanceFrame)
			return (ConcordanceFrame) frame;
		return null;
	}

	public void setCurrentFocusedConcordance(ConcordanceFrame f) {
		try {
			f.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<GraphFrame> getGraphFrames() {
		return graphFrameFactory.getFrames();
	}

	/**
	 * Creates a TextFrame for the given text file. If the text is not loadable,
	 * the function does nothing and returns null.
	 *
	 * @param fileInConflict
	 * @return
	 */
	public TextFrame newTextFrame(File text, boolean taggedText) {
		final TextFrame t = textFrameFactory.newTextFrame(text);
		if (t == null)
			return null;
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

	public GraphDiffFrame newGraphDiffFrame(GraphIO base, GraphIO dest,
			GraphDecorator diff) {
		final GraphDiffFrame f = new GraphDiffFrame(base, dest, diff);
		setup(f, true);
		try {
			f.setMaximum(true);
		} catch (final PropertyVetoException e) {
			/* */
		}
		return f;
	}

	private final ArrayList<TextFrameListener> textFrameListeners = new ArrayList<TextFrameListener>();
	private boolean firingTextFrame = false;

	public void addTextFrameListener(TextFrameListener l) {
		textFrameListeners.add(l);
	}

	public void removeTextFrameListener(TextFrameListener l) {
		if (firingTextFrame) {
			throw new IllegalStateException(
					"Cannot remove a listener while firing");
		}
		textFrameListeners.remove(l);
	}

	void fireTextFrameOpened(boolean taggedText) {
		firingTextFrame = true;
		try {
			for (final TextFrameListener l : textFrameListeners) {
				l.textFrameOpened(taggedText);
			}
		} finally {
			firingTextFrame = false;
		}
	}

	void fireTextFrameClosed() {
		firingTextFrame = true;
		try {
			for (final TextFrameListener l : textFrameListeners) {
				l.textFrameClosed();
			}
		} finally {
			firingTextFrame = false;
		}
	}

	private final InternalFrameListener delaFrameListener = new InternalFrameAdapter() {
		@Override
		public void internalFrameClosing(InternalFrameEvent e) {
			fireDelaFrameClosed(delaFrameFactory.getFrameCount() - 1);
		}
	};

	public DelaFrame newDelaFrame(File dela) {
		DelaFrame f = delaFrameFactory.getFrameIfExists(dela);
		if (f != null) {
			setup(f);
			return f;
		}	
		f = new DelaFrame();
		f.loadDela(dela);
		delaFrameFactory.addFrame(f);
		f.addInternalFrameListener(delaFrameListener);
		setup(f);
		fireDelaFrameOpened();
		if(Unitex.isRunning()) {
			PreferencesManager.getUserPreferences().addRecentDictionary(dela);
		}
		return f;
	}

	public void closeCurrentDelaFrame() {
		final File f = Config.getCurrentDELA();
		if (f == null)
			return;
		final DelaFrame frame = delaFrameFactory.getFrameIfExists(f);
		if (frame != null) {
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
			throw new IllegalStateException(
					"Cannot remove a listener while firing");
		}
		delaFrameListeners.remove(l);
	}

	void fireDelaFrameOpened() {
		firingDelaFrame = true;
		try {
			for (final DelaFrameListener l : delaFrameListeners) {
				l.delaFrameOpened();
			}
		} finally {
			firingDelaFrame = false;
		}
	}

	void fireDelaFrameClosed(int remainingFrames) {
		firingDelaFrame = true;
		try {
			for (final DelaFrameListener l : delaFrameListeners) {
				l.delaFrameClosed(remainingFrames);
			}
		} finally {
			firingDelaFrame = false;
		}
	}

	public TokensFrame newTokensFrame(File tokens, boolean iconify) {
		return (TokensFrame) setup(tokensFrameFactory.newTokensFrame(tokens),
				false, iconify);
	}

	public void closeTokensFrame() {
		tokensFrameFactory.closeTokensFrame();
	}

	public TfstTagsFrame newTfstTagsFrame(File tags) {
		return (TfstTagsFrame) setup(
				tfstTagsFrameFactory.newTfstTagsFrame(tags), false, true);
	}

	public void closeTfstTagsFrame() {
		tfstTagsFrameFactory.closeTfstTagsFrame();
	}

	public TextDicFrame newTextDicFrame(File sntDir, boolean iconify) {
		return (TextDicFrame) setup(
				textDicFrameFactory.newTextDicFrame(sntDir), false, iconify);
	}

	public void closeTextDicFrame() {
		textDicFrameFactory.closeTextDicFrame();
	}

	public TextAutomatonFrame newTextAutomatonFrame(int sentenceNumber,
			boolean iconify) {
		return (TextAutomatonFrame) setup(
				textAutomatonFrameFactory.newTextAutomatonFrame(sentenceNumber),
				false, iconify);
	}

	public void closeTextAutomatonFrame() {
		textAutomatonFrameFactory.closeTextAutomatonFrame();
	}

	public ApplyLexicalResourcesFrame newApplyLexicalResourcesFrame() {
		final ApplyLexicalResourcesFrame f = (ApplyLexicalResourcesFrame) applyLexicalResourcesFrameFactory
				.newFrame();
		if (f == null)
			return null;
		f.refreshDicLists();
		setup(f);
		return f;
	}

	public void closeApplyLexicalResourcesFrame() {
		applyLexicalResourcesFrameFactory.closeFrame();
	}

	public TransducerListConfigurationFrame newTransducerListConfigurationFrame(File f){
		return (TransducerListConfigurationFrame) setup(
				transducerListConfigurationFrameFactory.newTransducerListConfigurationFrame(f),
				false, false);

	}

	public boolean isTransducerListConfigurationExist(){
		return transducerListConfigurationFrameFactory.existsFrame();
	}


	public TransducerListConfigurationFrame getTransducerListConfigurationFrame(){
		return transducerListConfigurationFrameFactory.getFrame();
	}

	public void closeTransducerListConfigurationFrame(){
		transducerListConfigurationFrameFactory.closeFrame();
	}

	public CheckDicFrame newCheckDicFrame(File dela) {
		final CheckDicFrame f = (CheckDicFrame) setup(checkDicFrameFactory
				.newFrame());
		f.setDela(dela);
		return f;
	}

	public void closeCheckDicFrame() {
		checkDicFrameFactory.closeFrame();
	}

	public CheckResultFrame newCheckResultFrame(File file) {
		final CheckResultFrame f = (CheckResultFrame) checkResultFrameFactory
				.newFrame();
		if (f == null)
			return null;
		f.load(file);
		setup(f);
		return f;
	}

	public void closeCheckResultFrame() {
		checkResultFrameFactory.closeFrame();
	}

	public ProcessInfoFrame newProcessInfoFrame(MultiCommands c, boolean close,
			ToDo myDo, boolean stopIfProblem,boolean forceToDo) {
		final ProcessInfoFrame f = new ProcessInfoFrame(c, close, myDo,
				stopIfProblem);
		f.setForceToDo(forceToDo);
		setup(f, true);
		f.launchBuilderCommands();
		return f;
	}

	public ArrayList<ConcordanceFrame> getConcordanceFrames() {
		return concordanceFrameFactory.getFrames();
	}

	public ConcordanceDiffFrame newConcordanceDiffFrame(File file) {
		final ConcordanceDiffFrame f = (ConcordanceDiffFrame) concordanceDiffFrameFactory
				.newFrame();
		if (f == null)
			return null;
		f.load(file);
		setup(f, true);
		return f;
	}

	public void closeConcordanceDiffFrame() {
		concordanceDiffFrameFactory.closeFrame();
	}

	public ConcordanceFrame newConcordanceFrame(File file, int widthInChars) {
		return (ConcordanceFrame) setup(
				concordanceFrameFactory.newConcordanceFrame(file, widthInChars),
				true);
	}

	public void closeCurrentFocusedConcordance() {
		ConcordanceFrame f = getCurrentFocusedConcordance();
		if(f != null) {
			f.doDefaultCloseAction();
		}
	}

	public void closeConcordFrame() {
		concordanceFrameFactory.closeConcordanceFrame();
	}

	public void closeConcordanceFrame(ConcordanceFrame f) {
		concordanceFrameFactory.closeConcordanceFrame(f);
	}

	public void closeConcordanceFrame() {
		concordanceFrameFactory.closeConcordanceFrame(getCurrentFocusedConcordance());
	}

	public ConcordanceParameterFrame newConcordanceParameterFrame() {
		final ConcordanceParameterFrame f = (ConcordanceParameterFrame) concordanceParameterFrameFactory
				.newFrame();
		if (f == null)
			return null;
		f.reset();
		setup(f);
		return f;
	}

	public void closeConcordanceParameterFrame() {
		concordanceParameterFrameFactory.closeFrame();
	}

	public Seq2GrfFrame newConstructSeqTfstFrame() {
		return (Seq2GrfFrame) setup(seq2GrfFrameFactory
				.newFrame());
	}

	public void closeConstructSeqTfstFrame() {
		seq2GrfFrameFactory.closeFrame();
	}

	public GraphPathFrame newGraphPathFrame() {
		final GraphPathFrame d = (GraphPathFrame) setup(graphPathFrameFactory
				.newFrame(),true);
		if (d == null)
			return null;
		d.setVisible(true);
		return d;
	}
	
	public void closeGraphPathFrame() {
		graphPathFrameFactory.closeFrame();
	}

	public GraphCollectionFrame newGraphCollectionFrame() {
		return (GraphCollectionFrame) setup(graphCollectionFrameFactory
				.newFrame());
	}

	public void closeGraphCollectionFrame() {
		graphCollectionFrameFactory.closeFrame();
	}

	public InflectFrame newInflectFrame(File dela) {
		final InflectFrame f = (InflectFrame) setup(inflectFrameFactory
				.newFrame());
		f.setDela(dela);
		return f;
	}

	public void closeInflectFrame() {
		inflectFrameFactory.closeFrame();
	}

	public ConvertLexiconGrammarFrame newConvertLexiconGrammarFrame(File table) {
		final ConvertLexiconGrammarFrame f = (ConvertLexiconGrammarFrame) convertLexiconGrammarFrameFactory
				.newFrame();
		if (f == null)
			return null;
		f.setupTable(table);
		setup(f);
		return f;
	}

	public void closeConvertLexiconGrammarFrame() {
		convertLexiconGrammarFrameFactory.closeFrame();
	}

	private final ArrayList<LexiconGrammarTableFrameListener> lgFrameListeners = new ArrayList<LexiconGrammarTableFrameListener>();
	private boolean firingLGFrame = false;

	public void addLexiconGrammarTableFrameListener(
			LexiconGrammarTableFrameListener l) {
		lgFrameListeners.add(l);
	}

	public void removeLexiconGrammarTableFrameListener(
			LexiconGrammarTableFrameListener l) {
		if (firingLGFrame) {
			throw new IllegalStateException(
					"Cannot remove a listener while firing");
		}
		lgFrameListeners.remove(l);
	}

	protected void fireLexiconGrammarTableFrameOpened() {
		firingLGFrame = true;
		try {
			for (final LexiconGrammarTableFrameListener l : lgFrameListeners) {
				l.lexiconGrammarTableFrameOpened();
			}
		} finally {
			firingLGFrame = false;
		}
	}

	protected void fireLexiconGrammarTableFrameClosed() {
		firingLGFrame = true;
		try {
			for (final LexiconGrammarTableFrameListener l : lgFrameListeners) {
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
		final LexiconGrammarTableFrame f = (LexiconGrammarTableFrame) setup(
				lexiconGrammarTableFrameFactory
						.newLexiconGrammarTableFrame(file),
				true);
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
		final LocateFrame f = (LocateFrame) locateFrameFactory.newFrame();
		if (f == null)
			return null;
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

	public StatisticsFrame newStatisticsFrame(File file, int mode) {
		return (StatisticsFrame) setup(
				statisticsFrameFactory.newStatisticsFrame(file, mode), true);
	}

	public void closeStatisticsFrame() {
		statisticsFrameFactory.closeStatisticsFrame();
	}

	public TranscodingFrame newTranscodingFrame() {
		return newTranscodingFrame(null, null, false);
	}

	public TranscodingFrame newTranscodingFrame(File file, ToDo toDo,
			boolean closeAfterWork) {
		final TranscodingFrame f = (TranscodingFrame) transcodingFrameFactory
				.newFrame();
		if (f == null)
			return null;
		f.configure(file, toDo, closeAfterWork);
		setup(f);
		return f;
	}

	public ConsoleFrame getConsoleFrame() {
		return newConsoleFrame();
	}

	private ConsoleFrame newConsoleFrame() {
		final ConsoleFrame f = (ConsoleFrame) consoleFrameFactory
				.newFrame(false);
		if (f == null)
			return null;
		addToDesktopIfNecessary(f, false);
		return f;
	}

	public void showConsoleFrame() {
		final ConsoleFrame f = getConsoleFrame();
		f.setVisible(true);
		try {
			f.setSelected(true);
		} catch (final PropertyVetoException e) {
			e.printStackTrace();
		}
	}

	public SvnConflictsFrame getSvnConflictsFrame(SvnMonitor monitor) {
		return newSvnConflictsFrame(monitor);
	}

	/**
	 * rootDir will be the user current language dir in Unitex, and the project
	 * dir in Gramlab
	 */
	private SvnConflictsFrame newSvnConflictsFrame(SvnMonitor monitor) {
		final SvnConflictsFrame f = svnConflictsFrameFactory
				.newSvnConflictsFrameFactory(monitor);
		if (f == null)
			return null;
		addToDesktopIfNecessary(f, false);
		return f;
	}

	public void showSvnConflictsFrame(SvnMonitor monitor) {
		final SvnConflictsFrame f = getSvnConflictsFrame(monitor);
		f.setVisible(true);
	}

	public FileEditionTextFrame newFileEditionTextFrame(File file) {
		return (FileEditionTextFrame) setup(
				fileEditionTextFrameFactory.getFileEditionTextFrame(file), true);
	}

	public void closeAllFileEditionTextFrames() {
		fileEditionTextFrameFactory.closeAllFileEditionTextFrames();
	}

	public FileEditionTextFrame getSelectedFileEditionTextFrame() {
		final JInternalFrame f = desktop.getSelectedFrame();
		if (f == null || !(f instanceof FileEditionTextFrame))
			return null;
		return (FileEditionTextFrame) f;
	}

	public TranscodeOneFileDialog newTranscodeOneFileDialog(File text, ToDo toDo) {
		final TranscodeOneFileDialog d = (TranscodeOneFileDialog) transcodeOneFileDialogFactory
				.newDialog();
		if (d == null)
			return null;
		d.configure(text, toDo);
		d.setVisible(true);
		return d;
	}

	public FontInfo newFontDialog(FontInfo info) {
		final FontDialog d = fontDialogFactory.newFontDialog(info);
		if (d == null)
			return null;
		d.setVisible(true);
		return d.getFontInfo();
	}

	public GraphPresentationInfo newGraphPresentationDialog(
			GraphPresentationInfo info, boolean showRightToLeftCheckBox) {
		final GraphPresentationDialog d = graphPresentationDialogFactory
				.newGraphPresentationDialog(info, showRightToLeftCheckBox);
		if (d == null)
			return null;
		d.setVisible(true);
		return d.getGraphPresentationInfo();
	}

	public GraphAlignmentDialog newGraphAlignmentDialog(GraphFrame f) {
		final GraphAlignmentDialog d = graphAlignmentDialogFactory
				.newGraphAlignmentDialog(f);
		if (d == null)
			return null;
		d.setVisible(true);
		return d;
	}

	public GraphSizeDialog newGraphSizeDialog(GraphFrame f) {
		final GraphSizeDialog d = graphSizeDialogFactory.newGraphSizeDialog(f);
		if (d == null)
			return null;
		d.setVisible(true);
		return d;
	}

	public FindDialog newFindDialog(FileEditionTextFrame f) {
		final FindDialog d = new FindDialog(f);
		d.setVisible(true);
		return d;
	}

	public boolean closeAllFrames() {
		for (final JInternalFrame f : desktop.getAllFrames()) {
			try {
				f.doDefaultCloseAction();
			} catch (final UserRefusedFrameClosingError e) {
				return false;
			}
		}
		return true;
	}

	public String newVariableInsertionDialog(boolean inputVariable) {
		final VariableInsertionDialog d = new VariableInsertionDialog(
				inputVariable);
		d.setVisible(true);
		return d.getVariableName();
	}

	public ExportTextAsPOSListDialog newExportTextAsPOSListDialog(File output,
			TagFilter filter, boolean delafStyle) {
		final ExportTextAsPOSListDialog d = exportTextAsPOSListDialogFactory
				.newExportTextAsPOSListDialog(output, filter, delafStyle);
		d.launch();
		d.setVisible(true);
		return d;
	}

	public TextAutomatonFrame getTextAutomatonFrame() {
		return textAutomatonFrameFactory.getFrame();
	}

	public Seq2GrfFrame getSeq2GrfFrame() {
		final Seq2GrfFrame f = (Seq2GrfFrame) seq2GrfFrameFactory.newFrame();
		f.setVisible(true);
		setup(f);
		try {
			f.setSelected(true);
		} catch (final PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public TextAutomatonFindAndReplaceDialog newTextAutomatonFindAndReplaceDialog() {
		final TextAutomatonFindAndReplaceDialog d = textAutomatonFindAndReplaceFactory.newTextAutomatonFindAndReplaceDialog();
		if (d == null) {
			return null;
		}
		d.setVisible(true);
		return d;
	}

	public FindAndReplaceDialog newFindAndReplaceDialog() {
    final FindAndReplaceDialog d = findAndReplaceFactory.newFindAndReplaceDialog();
		if (d == null)
			return null;
		d.setVisible(true);
		return d;
	}

	public TextAutomatonTagFilterDialog newTextAutomatonTagFilterDialog() {
		final TextAutomatonTagFilterDialog d = textAutomatonTagFilterFactory.newTextAutomatonTagFilterDialog();
		if (d == null) {
			return null;
		}
		d.setVisible(true);
		return d;
	}

	public void addObserver(MultiInstanceFrameFactoryObserver o) {
		graphFrameFactory.addObserver(o);
	}

	public void updateTextAutomatonFindAndReplaceDialog() {
		textAutomatonFindAndReplaceFactory.update();
	}
	
	public CheckTextAutomatonDialog newCheckTextAutomatonDialog(ArrayList<String> checkList) {
		final CheckTextAutomatonDialog d = checkTextAutomatonDialogFactory.newCheckTextAutomatonDialog(checkList);
		if (d == null) {
			return null;
		}
		d.setVisible(true);
		return d;
	}
}
