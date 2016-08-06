package org.gramlab.core.gramlab.project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.gramlab.core.gramlab.project.config.buildfile.FileOperationConfigPane;
import org.gramlab.core.gramlab.project.config.buildfile.FileOperationType;
import org.gramlab.core.gramlab.project.config.concordance.ConcordanceOperationConfigPane;
import org.gramlab.core.gramlab.project.config.concordance.ConcordanceOperationType;
import org.gramlab.core.gramlab.project.config.locate.LocateConfigPane;
import org.gramlab.core.gramlab.project.config.locate.OutputsPolicy;
import org.gramlab.core.gramlab.project.config.preprocess.ConfigBigPictureDialog;
import org.gramlab.core.gramlab.project.console.ConsolePanel;
import org.gramlab.core.gramlab.util.MyComboCellRenderer;
import org.gramlab.core.gramlab.util.SplitUtil;
import org.gramlab.core.umlv.unitex.LinkButton;
import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;
import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.files.PersonalFileFilter;
import org.gramlab.core.umlv.unitex.frames.InternalFrameManager;
import org.gramlab.core.umlv.unitex.frames.TextFrame;
import org.gramlab.core.umlv.unitex.io.Encoding;
import org.gramlab.core.umlv.unitex.listeners.TextFrameListener;
import org.gramlab.core.umlv.unitex.process.Launcher;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.process.commands.MultiCommands;
import org.gramlab.core.umlv.unitex.text.SntUtil;

@SuppressWarnings("serial")
public class ProcessPane extends JSplitPane {
	
	private GramlabProject project;
	JRadioButton typedText;
	JRadioButton openedText;
	JTextArea text=new JTextArea();
	JCheckBox doPreprocessing=new JCheckBox();
	JCheckBox doLocate=new JCheckBox();
	JCheckBox doResults=new JCheckBox();
	JCheckBox doConcordance=new JCheckBox();

	LocateConfigPane locateConfigPane;
	FileOperationConfigPane resultPane;
	ConcordanceOperationConfigPane concordancePane;
	DefaultComboBoxModel model;
	JComboBox lastSnts;
	
	public ProcessPane(GramlabProject project) {
		super();
		this.project=project;
		JPanel p=new JPanel(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.insets=new Insets(2,2,2,2);
		p.add(createPreprocessingPane(),gbc);
		p.add(createLocatePane(),gbc);
		p.add(createFilePane(),gbc);
		p.add(createConcordancePane(),gbc);
		gbc.weighty=1;
		p.add(new JPanel(),gbc);
		p.setMinimumSize(new Dimension(0,0));
		JScrollPane scroll=new JScrollPane(p);
		scroll.setMinimumSize(new Dimension(0,0));
		JSplitPane split0=new JSplitPane(JSplitPane.VERTICAL_SPLIT,true,scroll,
				createConsolePane());
		split0.setOneTouchExpandable(true);
		split0.setMinimumSize(new Dimension(0,0));
		split0.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
		split0.setBackground(new Color(180,180,180));
		setOrientation(JSplitPane.VERTICAL_SPLIT);
		setTopComponent(createTextPane());
		setBottomComponent(split0);
		setContinuousLayout(true);
		setOneTouchExpandable(true);
		setMinimumSize(new Dimension(0,0));
		project.getSplitUtil().setSplit2(this);
		project.getSplitUtil().setSplit3(split0);
		setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
		setBackground(new Color(200,200,200));
	}


	private JPanel createPreprocessingPane() {
		JPanel p=new JPanel(new BorderLayout());
		Box top=new Box(BoxLayout.X_AXIS);
		doPreprocessing.setText("Do");
		doPreprocessing.setSelected(project.isLastCorpusAFile() && project.mustDoPreprocessing());
		top.add(doPreprocessing);
		LinkButton setPreprocessing=new LinkButton("preprocessing");
		setPreprocessing.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ConfigBigPictureDialog(project);
			}
		});
		top.add(setPreprocessing);
		top.add(Box.createHorizontalGlue());
		LinkButton b=new LinkButton("\u2666",false);
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				project.getSplitUtil().action(SplitUtil.Pane.Config);
			}
		});
		top.add(b);
		p.add(top,BorderLayout.NORTH);
		return p;
	}

	private JPanel createLocatePane() {
		locateConfigPane=new LocateConfigPane(project);
		LinkButton showHide=new LinkButton();
		Runnable onHide=new Runnable() {
			@Override
			public void run() {
				locateConfigPane.validateAndSave();
			}
		};
		JPanel p=createHidablePane(showHide,"locate \u25BC","locate \u25B2",locateConfigPane,onHide);
		Box top=new Box(BoxLayout.X_AXIS);
		doLocate.setText("Do");
		doLocate.setSelected(project.isLastCorpusAFile() && project.mustDoLocate());
		top.add(doLocate);
		top.add(showHide);
		top.add(new JLabel("  ("));
		final JCheckBox debug=new JCheckBox("debug mode)",project.isDebugMode());
		debug.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				project.setDebugMode(debug.isSelected());
				try {
					project.saveConfigurationFiles(false);
				} catch (IOException e1) {
					/* */
				}
			}
		});
		top.add(debug);
		p.add(top,BorderLayout.NORTH);
		return p;
	}

	private JPanel createFilePane() {
		resultPane=new FileOperationConfigPane(project);
		LinkButton showHide=new LinkButton();
		Runnable onHide=new Runnable() {
			@Override
			public void run() {
				resultPane.validateAndSave(true);
			}
		};
		JPanel p=createHidablePane(showHide,"file \u25BC",
				"file \u25B2",resultPane,onHide);
		Box top=new Box(BoxLayout.X_AXIS);
		doResults.setText("Build");
		doResults.setSelected(project.isLastCorpusAFile() && project.mustBuildResult());
		top.add(doResults);
		top.add(showHide);
		p.add(top,BorderLayout.NORTH);
		return p;
	}

	private JPanel createConcordancePane() {
		concordancePane=new ConcordanceOperationConfigPane(project);
		LinkButton showHide=new LinkButton();
		Runnable onHide=new Runnable() {
			@Override
			public void run() {
				concordancePane.validateAndSave();
			}
		};
		JPanel p=createHidablePane(showHide,"concordance \u25BC",
				"concordance \u25B2",concordancePane,onHide);
		Box top=new Box(BoxLayout.X_AXIS);
		doConcordance.setText("Build");
		doConcordance.setSelected(project.isLastCorpusAFile() && project.mustBuildConcordance());
		top.add(doConcordance);
		top.add(showHide);
		p.add(top,BorderLayout.NORTH);
		return p;
	}
	
	public static JPanel createHidablePane(final JButton showHide,final String showCaption,
			final String hideCaption,final JPanel pane,final Runnable onHide) {
		final JPanel p=new JPanel(new BorderLayout());
		showHide.setText(showCaption);
		pane.setVisible(false);
		showHide.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!pane.isVisible()) {
					/* We have to show the panel */
					pane.setVisible(true);
					p.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
					showHide.setText(hideCaption);
				} else {
					pane.setVisible(false);
					p.setBorder(null);
					showHide.setText(showCaption);
					if (onHide!=null) {
						onHide.run();
					}
				}
				p.revalidate();
				p.repaint();
			}
		});
		p.add(pane,BorderLayout.CENTER);
		return p;
	}


	private boolean currentCorpusBeingSelected=false;
	
	
	/**
	 * We set here the current corpus, either a one by the user
	 * or a file selected by the user that may be null if the
	 * user has selected no file in the combo box.
	 */
	private void setCurrentCorpus(boolean typedText,File f) {
		if (currentCorpusBeingSelected) return;
		currentCorpusBeingSelected=true;
		try {
			project.setLastCorpusWasAFile(!typedText);
			File currentCorpus;
			boolean showFrame;
			if (typedText || f==null) {
				f=null;
				currentCorpus=project.getTmpCorpusFile();
				showFrame=false;
				project.setLastCorpusWasAFile(false);
			} else {
				currentCorpus=f;
				TextFrame frame=project
					.getFrameManagerAs(InternalFrameManager.class).getTextFrame();
				showFrame=(frame!=null);
				if (!showFrame) {
					project.getFrameManagerAs(InternalFrameManager.class).closeTextFrame();
				}
				project.setLastCorpusWasAFile(true);
			}
			if (f==null) {
				/* Do nothing, in order to please Fanny :) */
			} else {
				String s=project.getNormalizedFileName(f);
				if (-1==model.getIndexOf(s)) {
					model.addElement(s);
				}
				lastSnts.setSelectedItem(s);
				/* We save the combo box configuration */
				ArrayList<File> snt=new ArrayList<File>();
				snt.add(f);
				for (int i=0;i<model.getSize();i++) {
					if (i==lastSnts.getSelectedIndex()) continue;
					String tmp=(String) model.getElementAt(i);
					File file=project.getFileFromNormalizedName(tmp);
					snt.add(file);
				}
				project.setLastSnt(snt);
			}
			project.setCurrentCorpus(currentCorpus,showFrame);
			try {
				project.saveConfigurationFiles(false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			currentCorpusBeingSelected=false;
		}
	}
	
	boolean textLock=false;
	
	
	private JPanel createTextPane() {
		final JPanel p=new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createTitledBorder("Text"));
		boolean lastCorpusWasAFile=project.isLastCorpusAFile();
		typedText=new JRadioButton("Type some text:",!lastCorpusWasAFile);
		openedText=new JRadioButton("Use corpus:",lastCorpusWasAFile);
		ButtonGroup bg=new ButtonGroup();
		bg.add(typedText);
		bg.add(openedText);
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		
		Box box0=new Box(BoxLayout.X_AXIS);
		box0.add(typedText);
		box0.add(Box.createHorizontalGlue());
		LinkButton foo=new LinkButton("\u2666",false);
		foo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				project.getSplitUtil().action(SplitUtil.Pane.Text);
			}
		});
		box0.add(foo);
		p.add(box0,gbc);
		text.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				if (!typedText.isSelected()) {
					typedText.doClick();
				}
			}
		});
		JScrollPane scroll=new JScrollPane(text);
		scroll.setPreferredSize(new Dimension(150,150));
		gbc.weighty=1;
		p.add(scroll,gbc);
		gbc.weighty=0;
		p.add(openedText,gbc);
		typedText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (typedText.isSelected()) {
					setCurrentCorpus(true,null);
					if (!doPreprocessing.isSelected()) {
						doPreprocessing.doClick();
					}
					doPreprocessing.setEnabled(false);
				} else {
					doPreprocessing.setEnabled(true);
				}
			}
		});
		ArrayList<File> list = project.getLastSnt();
		String[] tab = new String[list.size()];
		int i = 0;
		for (File f : list) {
			tab[i++] = project.getNormalizedFileName(f);
		}
		model=new DefaultComboBoxModel(tab);
		lastSnts = new JComboBox(model);
		lastSnts.setRenderer(new MyComboCellRenderer(lastSnts));
		lastSnts.setPreferredSize(new Dimension(0, 0));
		lastSnts.setEditable(true);
		int lastCorpusIndex=-1;
		if (lastCorpusWasAFile && tab.length>0) {
			lastCorpusIndex=0;
		}
		openedText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (textLock || !openedText.isSelected()) return;
				doPreprocessing.setEnabled(true);
				textLock=true;
				try {
					String s=(String) lastSnts.getSelectedItem();
					File f=null;
					if (s!=null) {
						f=project.getProjectFileFromNormalizedName(s);
					}
					setCurrentCorpus(false,f);
				} finally {
					textLock=false;
				}
			}
		});
		lastSnts.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (textLock) return;
				textLock=true;
				try {
					String s=(String) lastSnts.getSelectedItem();
					File f=null;
					if (s!=null) {
						f = project.getFileFromNormalizedName(s);
						if (!openedText.isSelected()) {
							openedText.doClick();
						}
						doPreprocessing.setEnabled(true);
					}
					setCurrentCorpus(false,f);
				} finally {
					textLock=false;
				}
			}
		});
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.BOTH;
		p.add(lastSnts, gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 0;
		JButton set = new JButton("Set");
		set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File currentDir = project.getLastSntDir();
				if (currentDir==null) {
					currentDir=project.getProjectDirectory();
				}
				if (lastSnts.getSelectedItem() != null) {
					String s = (String) lastSnts.getSelectedItem();
					File foo = project.getFileFromNormalizedName(s);
					if (foo.exists()) {
						currentDir = foo.getParentFile();
					}
				}
				JFileChooser jfc = new JFileChooser(currentDir);
				jfc.addChoosableFileFilter(new PersonalFileFilter("snt",
						"Preprocessed texts"));
				jfc.addChoosableFileFilter(new PersonalFileFilter("txt",
						"Raw texts"));
				jfc.setDialogType(JFileChooser.OPEN_DIALOG);
				jfc.setMultiSelectionEnabled(false);
				final int returnVal = jfc.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				File f = jfc.getSelectedFile();
				String s = project.getRelativeFileName(f);
				if (s==null) {
					JOptionPane.showMessageDialog(null,
							"You cannot select a corpus file that is not\n" +
							"in the project's directory !", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				lastSnts.setSelectedItem(s);
				setCurrentCorpus(false,f);
				project.setLastSntDir(jfc.getCurrentDirectory());
				if (!openedText.isSelected()) {
					openedText.doClick();
				}
				doPreprocessing.setEnabled(true);
			}
		});
		p.add(set, gbc);
		project.addCurrentCorpusListener(new CorpusListener() {
			@Override
			public void currentCorpusChanged(File snt) {
				if (!currentCorpusBeingSelected) {
					currentCorpusBeingSelected=true;
					if (snt==null) {
						lastSnts.setSelectedItem(null);
					} else {
						if (!isTmpCorpus(snt)) {
							if (!openedText.isSelected()) {
								openedText.doClick();
							}
							doPreprocessing.setEnabled(true);
							String s=project.getNormalizedFileName(snt);
							lastSnts.setSelectedItem(s);
						}
					}
					currentCorpusBeingSelected=false;
				}
			}

		});
		Box box=new Box(BoxLayout.X_AXIS);
		final LinkButton textButton=new LinkButton("Show snt");
		textButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class).getTextFrame()!=null) {
					File corpus=project.getCurrentCorpus();
					GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class).closeTextFrame();
					/* After closing the text frame, the current corpus has been
					 * set back to null, so we restore it */
					project.setCurrentCorpus(corpus,false);
				} else {
					project.openSntFrame(false);
				}
			}
		});
		GlobalProjectManager.search(null)
			.getFrameManagerAs(InternalFrameManager.class)
			.addTextFrameListener(new TextFrameListener() {
			@Override
			public void textFrameOpened(boolean taggedText) {
				textButton.setText("Hide snt");
			}
			
			@Override
			public void textFrameClosed() {
				textButton.setText("Show snt");
			}
		});
		box.add(textButton);
		gbc.fill=GridBagConstraints.NONE;
		gbc.anchor=GridBagConstraints.WEST;
		gbc.gridwidth=GridBagConstraints.RELATIVE;
		p.add(box,gbc);
		if (openedText.isSelected()) {
			doPreprocessing.setSelected(project.mustDoPreprocessing());
			doPreprocessing.setEnabled(true);
		} else {
			doPreprocessing.setSelected(true);
			doPreprocessing.setEnabled(false);
		}
		doLocate.setSelected(project.isLastCorpusAFile() && project.mustDoLocate());
		doResults.setSelected(project.isLastCorpusAFile() && project.mustBuildResult());
		ActionListener l=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				project.setDoPreprocessing(doPreprocessing.isSelected());
				project.setDoLocate(doLocate.isSelected());
				project.setBuildResult(doResults.isSelected());
				try {
					project.saveConfigurationFiles(false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
		doPreprocessing.addActionListener(l);
		doLocate.addActionListener(l);
		doResults.addActionListener(l);
		
		gbc.fill=GridBagConstraints.BOTH;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		final JButton go=new JButton("Go");
		go.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				launchProcess();
			}
		});
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(go,gbc);
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				p.setMinimumSize(new Dimension(0,0));
			}
		});
		lastSnts.setSelectedIndex(lastCorpusIndex);
		return p;
	}

	/**
	 * Returns true if the given file is the tmp .txt used
	 * when the user typed some text instead of opening a file.
	 */
	protected boolean isTmpCorpus(File f) {
		return f.equals(project.getTmpCorpusFile());
	}

	/**
	 * This method checks if everything is all right and then, 
	 * launches all the selected process steps (preprocessing, locate,
	 * show results).
	 */
	public void launchProcess() {
		boolean backupPreviousConcorInd=true;
		if (!doPreprocessing.isSelected() 
				&& !doLocate.isSelected()
				&& !doResults.isSelected()
				&& !doConcordance.isSelected()) {
			JOptionPane.showMessageDialog(null,
					"No operation selected !", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		final File txt,snt,corpus;
		/* We may have to generate the tmp corpus */
		if (typedText.isSelected()) {
			FileUtil.write(text.getText(),project.getTmpCorpusFile());
			project.setLastCorpusWasAFile(false);
			corpus=project.getTmpCorpusFile();
		} else {
			project.setLastCorpusWasAFile(true);
			corpus=project.getCurrentCorpus();
			if (corpus==null) {
				JOptionPane.showMessageDialog(null,
						"No corpus selected !", "Error",
						JOptionPane.ERROR_MESSAGE);
					return;
			}
		}
		project.setDoPreprocessing(doPreprocessing.isSelected());
		project.setDoLocate(doLocate.isSelected());
		project.setBuildResult(doResults.isSelected());
		if (FileUtil.getExtensionInLowerCase(corpus).equals("txt")) {
			txt=corpus;
			snt=FileUtil.getSnt(txt);
		} else {
			snt=corpus;
			txt=FileUtil.getTxt(snt);
		}
		if (typedText.isSelected() && text.getText().equals("")) {
			JOptionPane.showMessageDialog(null,
				"You did not type any test text !", "Error",
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (openedText.isSelected() && corpus==null) {
			JOptionPane.showMessageDialog(null,
					"No corpus file has been selected !", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (doPreprocessing.isSelected()) {
			/* To enable the preprocessing, we must check that the .txt exists */
			if (!txt.exists()) {
				JOptionPane.showMessageDialog(null,
						"Unable to apply preprocessing because the following file does not exist:\n\n"
						+txt.getAbsolutePath(), "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		if (doLocate.isSelected()) {
			/* For a Locate, we must validate the Locate configuration */
			if (!locateConfigPane.validateConfiguration(project)) return;
			if (!doPreprocessing.isSelected() && !snt.exists()) {
				JOptionPane.showMessageDialog(null,
						"Unable to locate pattern because the corpus has not been preprocessed yet",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		if (doResults.isSelected()) {
			if (!resultPane.validateConfiguration(project,true)) return;
		}
		if (doConcordance.isSelected()) {
			/* Same test with results configuration */
			if (!concordancePane.validateConfiguration(project)) return;
			File ind=new File(project.getCurrentSntDir(),"concord.ind");
			if (!doLocate.isSelected() && !ind.exists()) {
				JOptionPane.showMessageDialog(null,
						"Unable to build results because no locate operation has been done yet",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (project.getBuildConcordanceType()==ConcordanceOperationType.SHOW_DIFFERENCES_WITH_PREVIOUS_CONCORDANCE) {
				/* We check if there is a previous concordance to diff with */
				if (!doLocate.isSelected()) {
					/* We can do the diff even if Locate is not applied, iff
					 * there are concord.ind and previous-concord.ind
					 */
					File previous=new File(project.getCurrentSntDir(),"previous-concord.ind");
					if (!ind.exists() || !previous.exists()) {
						JOptionPane.showMessageDialog(null,
							"You can only build difference with previous concordance if:\n\n"
							+"1) Locate is done and a previous concordance exist, or\n"
							+"2) if two previous concordance index files exist",
							"Error",
							JOptionPane.ERROR_MESSAGE);
						return;
					}
					backupPreviousConcorInd=false;
				}
				if (!ind.exists()) {
					JOptionPane.showMessageDialog(null,
							"Cannot build difference with previous concordance because there is no such previous concordance",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		}
		if (doResults.isSelected() && project.getResultType()==FileOperationType.MODIFY_TEXT && resultHasNoOutputs()) {
			JOptionPane.showMessageDialog(null,
					"You should not ask for a 'Text with outputs' result when your\n"+
					"query produces no outputs (regular expression or graph applied in\n"+
					"'Ignore outputs' mode).",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (doConcordance.isSelected() && project.getBuildConcordanceType()==ConcordanceOperationType.SHOW_AMBIGUOUS_OUTPUTS && resultHasNoOutputs()) {
			JOptionPane.showMessageDialog(null,
					"You should not ask 'Show only ambiguous outputs' when your\n"+
					"query produces no outputs (regular expression or graph applied in\n"+
					"'Ignore outputs' mode).",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		/* If everything is ok, we save the configuration */
		try {
			project.saveConfigurationFiles(false);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					"Cannot save your project configuration !", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		/* We may have to backup the previous concord.ind file */
		File ind=new File(FileUtil.getSntDir(snt),"concord.ind");
		if (backupPreviousConcorInd && ind.exists()) {
			File backup=new File(FileUtil.getSntDir(snt),"previous-concord.ind");
			FileUtil.copyFileByName(ind,backup);
		}
		
		MultiCommands cmds=new MultiCommands();
		if (doPreprocessing.isSelected()) {
			if (!txt.exists()) {
				JOptionPane.showMessageDialog(null,
						"Cannot preprocess because file does exist:\n\n"+txt.getAbsolutePath(), "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			cmds.addCommand(project.preprocessText(txt));
		}
		final boolean isTextFrameVisible=
				(GlobalProjectManager.search(snt)
					.getFrameManagerAs(InternalFrameManager.class).getTextFrame()!=null);
		if (isTextFrameVisible && doPreprocessing.isSelected()) {
			GlobalProjectManager.search(snt)
				.getFrameManagerAs(InternalFrameManager.class).closeTextFrame();
			SntUtil.cleanSntDir(FileUtil.getSntDir(snt));
		}
		GlobalProjectManager.search(snt)
			.getFrameManagerAs(InternalFrameManager.class).closeConcordanceFrame();
		GlobalProjectManager.search(snt)
			.getFrameManagerAs(InternalFrameManager.class).closeConcordanceDiffFrame();
		
		if (doLocate.isSelected()) {
			cmds.addCommand(project.getLocateCommands(snt));
		}	
		if (doResults.isSelected()) {
			cmds.addCommand(project.getResultCommands(snt));
		}
		if (doConcordance.isSelected()) {
			if (project.isDebugMode() && !doLocate.isSelected()
					&& !wasADebugConcordance(ind)) {
				JOptionPane.showMessageDialog(null,
						"The debug mode option will be ignored because you want to build\n"+
						"a concordance from the previous locate operation and this\n"+
						"previous operation was not done in debug mode.", "Warning",
						JOptionPane.WARNING_MESSAGE);
			}
			cmds.addCommand(project.getConcordanceCommands(snt));
		}
		final boolean doPreprocess=doPreprocessing.isSelected();
		Launcher.exec(cmds,true,new ToDo() {
			
			JPanel createEndComponent(Color c,double pTime,double lTime,boolean debug) {
				JPanel p=new JPanel(null);
				p.setBackground(c);
				int n_lines=0;
				if (pTime>=0) n_lines++;
				if (lTime>=0) n_lines++;
				if (debug) n_lines++;
				if (n_lines!=0) {
					p.setLayout(new GridLayout(n_lines,1));
					if (debug) {
						p.add(new JLabel("[DEBUG]"));
					}
					if (pTime>=0) {
						p.add(new JLabel(String.format("[Preprocess time: %.3f secs]",pTime)));
					}
					if (lTime>=0) {
						p.add(new JLabel("[Locate time: "+lTime+" secs]"));
					}
				} else {
					p.setPreferredSize(new Dimension(1,10));
				}
				return p;
			}
			
			@Override
			public void toDo(boolean success) {
				ConsolePanel console=project.getConsolePanel();
				double pTime=console.getPreprocessingTotalTime();
				double lTime=console.getLocateTime();
				console.setPreprocessingTotalTime(-1);
				console.setLocateTime(-1);
				if (!success) {
					console.add(createEndComponent(Color.RED,pTime,lTime,project.isDebugMode()));					
					return;
				}
				if (isTextFrameVisible && doPreprocess) {
					project.setCurrentCorpus(corpus,false);
					GlobalProjectManager.search(snt)
						.getFrameManagerAs(InternalFrameManager.class).newTextFrame(snt,false);
				}
				if (doResults.isSelected()) {
					switch(project.getResultType()) {
					case MODIFY_TEXT: 
					case EXTRACT_MATCHING_UNITS:
					case EXTRACT_UNMATCHING_UNITS:
					case EXTRACT_MATCHES: {
						project.openFileResult(project.getResultOutputFile());
						break;
					}
					}
				}
				if (doConcordance.isSelected()) {
					switch(project.getBuildConcordanceType()) {
					case BUILD_CONCORDANCE:
					case SHOW_AMBIGUOUS_OUTPUTS: {
						File f;
						switch(project.getConcordanceType()) {
						case HTML: f=new File(FileUtil.getSntDir(snt),"concord.html"); break;
						case XML: f=new File(FileUtil.getSntDir(snt),"concord.xml"); break;
						default: f=new File(FileUtil.getSntDir(snt),"concord.txt"); break;
						}
						project.openConcordanceFile(f);
						break;
					}
					case SHOW_DIFFERENCES_WITH_PREVIOUS_CONCORDANCE: {
						File diff=new File(FileUtil.getSntDir(snt),"diff.html");
						project.openDiffHtmlFile(diff);
						break;
					}
					}
				}
				project.getConsolePanel().add(createEndComponent(Color.GREEN,pTime,lTime,project.isDebugMode()));
			}
		});
	}

	private boolean wasADebugConcordance(File ind) {
		Scanner s=Encoding.getScanner(ind);
		if (!s.hasNextLine()) {
			s.close();
			return false;
		}
		String line=s.nextLine();
		s.close();
		return line.equals("#D");
	}


	/**
	 * Returns true if the current query won't produce any output, or, if
	 * there is no query but only a result display, if the last query did not
	 * contain outputs.
	 */
	private boolean resultHasNoOutputs() {
		if (!doLocate.isSelected()) {
			File f=new File(project.getCurrentSntDir(),"concord.ind");
			if (!f.exists()) return true;
			Scanner s=Encoding.getScanner(f);
			if (!s.hasNext()) return false;
			String header=s.next();
			s.close();
			return header.equals("#I");
		}
		if (project.isLastPatternRegexp()) return true;
		if (project.getOutputsPolicy()==OutputsPolicy.IGNORE) return true;
		return false;
	}


	private JPanel createConsolePane() {
		JPanel p=new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createTitledBorder("Console"));
		Box box=new Box(BoxLayout.X_AXIS);
		LinkButton clear=new LinkButton("Clear");
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				project.getConsolePanel().removeAll();
			}
		});
		LinkButton copy=new LinkButton("Copy");
		copy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				project.getConsolePanel().copy();
			}
		});
		box.add(clear);
		box.add(copy);
		box.add(Box.createHorizontalGlue());
		LinkButton b=new LinkButton("\u2666",false);
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				project.getSplitUtil().action(SplitUtil.Pane.Console);
			}
		});
		box.add(b);
		p.add(box,BorderLayout.NORTH);
		p.add(new JScrollPane(project.getConsolePanel()),BorderLayout.CENTER);
		p.setMinimumSize(new Dimension(0,0));
		return p;
	}
	
}
