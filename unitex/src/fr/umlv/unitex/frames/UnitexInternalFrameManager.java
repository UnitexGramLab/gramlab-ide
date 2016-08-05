package fr.umlv.unitex.frames;

import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.JDesktopPane;

import fr.umlv.unitex.graphrendering.ContextsInfo;
import fr.umlv.unitex.process.commands.UnxmlizeCommand;
import fr.umlv.unitex.xalign.ConcordanceModel;

/**
 * 
 * @author mdamis
 *
 */
public class UnitexInternalFrameManager extends InternalFrameManager {
	private final FrameFactory buildKrMwuDicFrameFactory = new FrameFactory(
			BuildKrMwuDicFrame.class);
	private final FrameFactory cassysFrameFactory = new FrameFactory(
			CassysFrame.class);
	private final FrameFactory dicLookupFrameFactory = new FrameFactory(
			DicLookupFrame.class);
	private final FrameFactory transliterationFrameFactory = new FrameFactory(
			TransliterationFrame.class);
	private final FrameFactory constructTfstFrameFactory = new FrameFactory(
			ConstructTfstFrame.class);
	private final FrameFactory convertTfstToTextFrameFactory = new FrameFactory(
			ConvertTfstToTextFrame.class);
	private final FrameFactory elagCompFrameFactory = new FrameFactory(
			ElagCompFrame.class);
	private final FrameFactory globalPreferencesFrameFactory = new FrameFactory(
			GlobalPreferencesFrame.class);
	private final FrameFactory helpOnCommandFrameFactory = new FrameFactory(
			HelpOnCommandFrame.class);
	private final FrameFactory messageWhileWorkingFrameFactory = new FrameFactory(
			MessageWhileWorkingFrame.class);
	private final DialogFactory listCopyDialogFactory = new DialogFactory(
			ListCopyDialog.class);
	private final LemmatizeFrameFactory lemmatizeFrameFactory=new LemmatizeFrameFactory();
	private final PreprocessDialogFactory preprocessDialogFactory = new PreprocessDialogFactory();
	private final XAlignFrameFactory xAlignFrameFactory = new XAlignFrameFactory();
	private final XAlignLocateFrameFactory xAlignLocateFrameFactory = new XAlignLocateFrameFactory();
	private final FrameFactory xAlignConfigFrameFactory = new FrameFactory(
			XAlignConfigFrame.class);
	
	public UnitexInternalFrameManager(JDesktopPane desktop) {
		super(desktop);
	}

	public BuildKrMwuDicFrame newBuildKrMwuDicFrame() {
		return (BuildKrMwuDicFrame) setup(buildKrMwuDicFrameFactory.newFrame());
	}
	
	public CassysFrame newCassysFrame() {
		final CassysFrame f = (CassysFrame) cassysFrameFactory.newFrame();
		if (f == null)
			return null;
		setup(f);
		return f;
	}

	public void closeCassysFrame() {
		cassysFrameFactory.closeFrame();
	}
	
	public DicLookupFrame newDicLookupFrame() {
		final DicLookupFrame f = (DicLookupFrame) dicLookupFrameFactory
				.newFrame(false);
		if (f == null)
			return null;
		setup(f);
		return f;
	}

	public void closeDicLookupFrame() {
		dicLookupFrameFactory.closeFrame();
	}
	
	public TransliterationFrame newTransliterationFrame() {
		return (TransliterationFrame) setup(transliterationFrameFactory
				.newFrame());
	}

	public void closeTransliterationFrame() {
		transliterationFrameFactory.closeFrame();
	}

	public ConstructTfstFrame newConstructTfstFrame() {
		return (ConstructTfstFrame) setup(constructTfstFrameFactory.newFrame());
	}

	public void closeConstructTfstFrame() {
		constructTfstFrameFactory.closeFrame();
	}
	
	public ConvertTfstToTextFrame newConvertTfstToTextFrame() {
		return (ConvertTfstToTextFrame) setup(convertTfstToTextFrameFactory
				.newFrame());
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
		final GlobalPreferencesFrame f = (GlobalPreferencesFrame) globalPreferencesFrameFactory
				.newFrame();
		if (f == null)
			return null;
		f.reset();
		setup(f);
		return f;
	}

	public void closeGlobalPreferencesFrame() {
		globalPreferencesFrameFactory.closeFrame();
	}
	
	public MessageWhileWorkingFrame newMessageWhileWorkingFrame(String title) {
		final MessageWhileWorkingFrame f = (MessageWhileWorkingFrame) messageWhileWorkingFrameFactory
				.newFrame();
		if (f == null)
			return null;
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
	
	public PreprocessDialog newPreprocessDialog(File text, File sntFile,
			boolean taggedText, UnxmlizeCommand cmd) {
		final PreprocessDialog d = preprocessDialogFactory.newPreprocessDialog(
				text, sntFile, taggedText, cmd);
		if (d == null)
			return null;
		d.setVisible(true);
		return d;
	}
	
	public HelpOnCommandFrame newHelpOnCommandFrame() {
		return (HelpOnCommandFrame) setup(helpOnCommandFrameFactory.newFrame());
	}

	public void closeHelpOnCommandFrame() {
		helpOnCommandFrameFactory.closeFrame();
	}
	
	public ContextsInfo newListCopyDialog() {
		final ListCopyDialog d = (ListCopyDialog) listCopyDialogFactory
				.newDialog();
		if (d == null)
			return null;
		d.setVisible(true);
		return d.getContextsInfo();
	}
	
	public LemmatizeFrame newLemmatizeFrame() {
		final LemmatizeFrame f = lemmatizeFrameFactory.newFrame();
		f.setVisible(true);
		setup(f);
		try {
			f.setSelected(true);
		} catch (final PropertyVetoException e) {
			e.printStackTrace();
		}
		return f;
	}

	public void closeLemmatizeFrame() {
		lemmatizeFrameFactory.closeFrame();
	}
	
	public XAlignConfigFrame newXAlignConfigFrame() {
		return (XAlignConfigFrame) setup(xAlignConfigFrameFactory.newFrame());
	}

	public XAlignFrame newXAlignFrame(File src, File dst, File alignment) {
		return (XAlignFrame) setup(
				xAlignFrameFactory.newXAlignFrame(src, dst, alignment), true);
	}

	public void closeXAlignFrame() {
		xAlignFrameFactory.closeXAlignFrame();
	}

	public XAlignLocateFrame newXAlignLocateFrame(String language, File snt,
			ConcordanceModel model) {
		final XAlignLocateFrame f = xAlignLocateFrameFactory
				.newXAlignLocateFrame(language);
		if (f == null)
			return null;
		f.configure(language, snt, model);
		setup(f);
		return f;
	}

	public void closeXAlignLocateFrame() {
		xAlignLocateFrameFactory.closeXAlignLocateFrame();
	}
}