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

import fr.umlv.unitex.DropTargetManager;
import fr.umlv.unitex.Unitex;
import fr.umlv.unitex.Version;
import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.config.PreferencesManager;
import fr.umlv.unitex.config.SntFileEntry;
import fr.umlv.unitex.editor.FileEditionMenu;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.graphrendering.GraphMenuBuilder;
import fr.umlv.unitex.grf.GraphPresentationInfo;
import fr.umlv.unitex.io.Encoding;
import fr.umlv.unitex.listeners.DelaFrameListener;
import fr.umlv.unitex.listeners.LanguageListener;
import fr.umlv.unitex.listeners.LexiconGrammarTableFrameListener;
import fr.umlv.unitex.listeners.TextFrameListener;
import fr.umlv.unitex.print.PrintManager;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.*;
import fr.umlv.unitex.project.UnitexProject;
import fr.umlv.unitex.project.manager.UnitexProjectManager;
import fr.umlv.unitex.text.Text;
import fr.umlv.unitex.utils.UnitexHelpMenuBuilder;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.MenuEvent;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DropTarget;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.List;

/**
 * This is the main frame of the Unitex system.
 *
 * @author Sébastien Paumier
 */
public class UnitexFrame extends JFrame {
	/**
	 * This object is used to enable drag-and-drop, so that the user can pick up
	 * texts, graphs and dictionaries from a file explorer.
	 */
	public DropTarget dropTarget = DropTargetManager.getDropTarget()
			.newDropTarget(this);
	/**
	 * The desktop of the frame.
	 */
	protected final JDesktopPane desktop;
	/**
	 * The clipboard used to copy and paste text and graph box selections.
	 */
	public static final Clipboard clip = new Clipboard("Unitex clipboard");
	/**
	 * The main frame of the system.
	 */
	public static UnitexFrame mainFrame;
	static Dimension screenSize;
	public static boolean closing = false;

	/**
	 * This method initializes the system by a call to the
	 * <code>Config.initConfig()</code> method. Then, the main frame is created.
	 * The sub-frames are created the first time they are needed.
	 */
	public UnitexFrame() {
		super(Version.getFullStringVersion());
		final int inset = 50;
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height
				- inset * 2);
		desktop = new JDesktopPane();
		setContentPane(desktop);
		UnitexProject project = new UnitexProject(new UnitexInternalFrameManager(desktop));
		UnitexProjectManager projectManager = new UnitexProjectManager(project);
		new GlobalProjectManager(projectManager);
		buildMenus();
		mainFrame = this;
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setTitle(Version.getFullStringVersion() + " - current language is "
				+ Config.getCurrentLanguageForTitleBar());
		GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
				.addTextFrameListener(
				new TextFrameListener() {
					@Override
					public void textFrameOpened(boolean taggedText) {
						preprocessText.setEnabled(!taggedText);
						cassys.setEnabled(true);
						applyLexicalResources.setEnabled(true);
						locatePattern.setEnabled(true);
						displayLocatedSequences.setEnabled(true);
						constructFst.setEnabled(true);
						lemmatize.setEnabled(true);
						constructSeqFst.setEnabled(true);
						convertFst.setEnabled(true);
						exportTfstAsCsv.setEnabled(true);
						closeText.setEnabled(true);
						openConcordance.setEnabled(true);
						saveAsSnt.setEnabled(true);
						File snt = ConfigManager.getManager().getCurrentSnt(
								null);
						final File sntDir = FileUtil.getSntDir(snt);
						GlobalProjectManager.search(null)
								.getFrameManagerAs(InternalFrameManager.class).newTokensFrame(
								new File(sntDir, "tok_by_freq.txt"), true);
						GlobalProjectManager.search(null)
								.getFrameManagerAs(InternalFrameManager.class).newTfstTagsFrame(
								new File(sntDir, "tfst_tags_by_freq.txt"));
						GlobalProjectManager.search(null)
								.getFrameManagerAs(InternalFrameManager.class).newTextDicFrame(
								sntDir, true);
						GlobalProjectManager.search(null)
								.getFrameManagerAs(InternalFrameManager.class)
								.newTextAutomatonFrame(1, true);
					}

					@Override
					public void textFrameClosed() {
						cassys.setEnabled(false);
						preprocessText.setEnabled(false);
						applyLexicalResources.setEnabled(false);
						locatePattern.setEnabled(false);
						displayLocatedSequences.setEnabled(false);
						constructFst.setEnabled(false);
						lemmatize.setEnabled(false);
						convertFst.setEnabled(false);
						exportTfstAsCsv.setEnabled(false);
						closeText.setEnabled(false);
						openConcordance.setEnabled(false);
						saveAsConcordance.setEnabled(false);
						saveAsSnt.setEnabled(false);
						GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
								.closeTokensFrame();
						GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
								.closeConcordanceFrame();
						GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
								.closeConcordanceDiffFrame();
						GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
								.closeTextDicFrame();
						GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
								.closeTextAutomatonFrame();
						GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
								.closeTfstTagsFrame();
						GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
								.closeApplyLexicalResourcesFrame();
						GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
								.closeConcordanceParameterFrame();
						GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
								.closeConstructTfstFrame();
						GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
								.closeLemmatizeFrame();
						GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
								.closeConvertTfstToTextFrame();
						GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
								.closeLocateFrame();
						GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
								.closeGraphPathFrame();
						GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
								.closeStatisticsFrame();
					}
				});
		GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
				.addDelaFrameListener(
				new DelaFrameListener() {
					@Override
					public void delaFrameOpened() {
						checkDelaFormat.setEnabled(true);
						transliterate.setEnabled(true);
						sortDictionary.setEnabled(true);
						inflect.setEnabled(true);
						compressIntoFST.setEnabled(true);
						closeDela.setEnabled(true);
					}

					@Override
					public void delaFrameClosed(int remainingFrames) {
						GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
								.closeCheckDicFrame();
						GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
								.closeTransliterationFrame();
						GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
								.closeCheckResultFrame();
						GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
								.closeInflectFrame();
						if (remainingFrames == 0) {
							checkDelaFormat.setEnabled(false);
							transliterate.setEnabled(false);
							sortDictionary.setEnabled(false);
							inflect.setEnabled(false);
							compressIntoFST.setEnabled(false);
							closeDela.setEnabled(false);
						}
					}
				});
		GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
				.addLexiconGrammarTableFrameListener(
						new LexiconGrammarTableFrameListener() {
							@Override
							public void lexiconGrammarTableFrameOpened() {
								compileLexiconGrammar.setEnabled(true);
								closeLexiconGrammar.setEnabled(true);
							}

							@Override
							public void lexiconGrammarTableFrameClosed() {
								compileLexiconGrammar.setEnabled(false);
								closeLexiconGrammar.setEnabled(false);
							}
						});
		Config.addLanguageListener(new LanguageListener() {
			@Override
			public void languageChanged() {
				setTitle(Version.getFullStringVersion() + " - current language is "
						+ Config.getCurrentLanguageForTitleBar());
				GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class).closeTextFrame();
				GlobalProjectManager.search(null)
						.getFrameManagerAs(UnitexInternalFrameManager.class)
						.closeGlobalPreferencesFrame();
			}
		});
	}

	/**
	 * Builds the menu bar.
	 */
	void buildMenus() {
		final JMenuBar menuBar = new JMenuBar();
		final JMenu text = buildTextMenu();
		final JMenu DELA = buildDELAMenu();
		final JMenu fsGraph = buildFsGraphMenu();
		final JMenu lexiconGrammar = buildLexiconGrammarMenu();
		final JMenu xalign = buildXAlignMenu();
		final FileEditionMenu fileEditionMenu = new FileEditionMenu();
		final JMenu windows = buildWindowsMenu();
		final JMenu help = UnitexHelpMenuBuilder.build(Config.getApplicationDir());
		final JMenu info = buildInfoMenu();
		text.setMnemonic(KeyEvent.VK_T);
		DELA.setMnemonic(KeyEvent.VK_D);
		fsGraph.setMnemonic(KeyEvent.VK_G);
		lexiconGrammar.setMnemonic(KeyEvent.VK_L);
		xalign.setMnemonic(KeyEvent.VK_X);
		fileEditionMenu.setMnemonic(KeyEvent.VK_F);
		help.setMnemonic(KeyEvent.VK_H);
		windows.setMnemonic(KeyEvent.VK_W);
		info.setMnemonic(KeyEvent.VK_I);
		menuBar.add(text);
		menuBar.add(DELA);
		menuBar.add(fsGraph);
		menuBar.add(lexiconGrammar);
		menuBar.add(xalign);
		menuBar.add(fileEditionMenu);
		menuBar.add(windows);
		menuBar.add(help);
		menuBar.add(info);
		menuBar.add(info);
		setJMenuBar(menuBar);
	}
	Action openText;
	Action openTaggedText;
	Action preprocessText;
	Action lemmatize;
	Action changeLang;
	Action applyLexicalResources;
	Action locatePattern;
	Action openConcordance;
	Action saveAsConcordance;
	Action saveAsSnt;
	AbstractAction displayLocatedSequences;
	AbstractAction elagComp;
	AbstractAction constructFst;
	AbstractAction constructSeqFst;
	AbstractAction convertFst;
	AbstractAction exportTfstAsCsv;
	AbstractAction closeText;
	AbstractAction quitUnitex;
	AbstractAction cassys;

	JMenu buildTextMenu() {
		final JMenu textMenu = new JMenu("Text");
		openText = new AbstractAction("Open...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				openText();
			}
		};
		openText.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		textMenu.add(new JMenuItem(openText));
		openTaggedText = new AbstractAction("Open Tagged Text...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				openTaggedText();
			}
		};
		textMenu.add(new JMenuItem(openTaggedText));

		final JMenu openRecent = new JMenu("Open Recent");
		openRecent.addMenuListener(new MenuAdapter() {
			@Override
			public void menuSelected(MenuEvent e) {
				List<SntFileEntry> l = PreferencesManager.getUserPreferences()
						.getRecentTexts();
				if (l == null)
					return;
				openRecent.removeAll();
				for (final SntFileEntry sfe : l) {
					String caption = "(" + sfe.getLanguage() + ") "
							+ sfe.getFile().getPath();
					final JMenuItem item = new JMenuItem(caption);
					item.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent ev) {
							if (!sfe.getFile().exists()) {
								JOptionPane.showMessageDialog(null, "File "
										+ sfe.getFile().getAbsolutePath()
										+ " does not exist", "Error",
										JOptionPane.ERROR_MESSAGE);
								PreferencesManager.getUserPreferences()
										.removeRecentText(sfe);
							} else {
								boolean lngErr = false;
								if (!Config.getCurrentLanguage().equals(
										sfe.getLanguage())) {
									final TreeSet<String> languages = new TreeSet<String>();
									Config.collectLanguage(
											Config.getUnitexDir(), languages);
									Config.collectLanguage(Config.getUserDir(),
											languages);
									if (languages.contains(sfe.getLanguage()))
										Config.setCurrentLanguage(sfe
												.getLanguage());
									else {
										lngErr = true;
										JOptionPane.showMessageDialog(
												UnitexFrame.this,
												"Folder for language "
														+ sfe.getLanguage()
														+ " does not exist",
												"Error",
												JOptionPane.ERROR_MESSAGE);
										PreferencesManager.getUserPreferences()
												.removeRecentText(sfe);
									}
								}
								if (!lngErr)
									Text.loadCorpus(sfe.getFile(), true);
							}
						}
					});
					openRecent.add(item);
				}
				if (!l.isEmpty()) {
					final JMenuItem item = new JMenuItem(
							"Clear Recent Texts List");
					item.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent event) {
							final String[] options = { "Yes", "No" };
							final int n = JOptionPane
									.showOptionDialog(
											UnitexFrame.this,
											"Do you really want to clear the Recent Texts List?",
											"", JOptionPane.YES_NO_OPTION,
											JOptionPane.QUESTION_MESSAGE, null,
											options, options[0]);
							if (n == 0) {
								PreferencesManager.getUserPreferences()
										.clearRecentTexts();
							}
						}
					});
					openRecent.addSeparator();
					openRecent.add(item);
				}
			}
		});
		textMenu.add(openRecent);
		saveAsSnt = new AbstractAction("Save As...") {
		  @Override
		  public void actionPerformed(ActionEvent e) {
				if (Config.getCurrentSnt() == null || Config.getCurrentSntDir() == null) {
				  return;
				}
				JFileChooser fc = Config.getTaggedCorpusDialogBox();
				fc.setMultiSelectionEnabled(false);
				fc.setDialogType(JFileChooser.SAVE_DIALOG);
				File file;
				for(;;) {
				  final int returnVal = fc.showSaveDialog(UnitexFrame.mainFrame);
				  if (returnVal != JFileChooser.APPROVE_OPTION) {
						return;
				  }
				  file = fc.getSelectedFile();
				  final String name = file.getAbsolutePath();
				  if (!name.endsWith(".snt")) {
						file = new File(name + ".snt");
				  }
				  if (file == null || !file.exists()) {
						break;
				  }
				  final String message = file + "\nalready exists. Do you want to replace it?";
				  final String[] options = {"Yes", "No"};
				  final int n = JOptionPane.showOptionDialog(null, message, "Error", JOptionPane.YES_NO_OPTION, JOptionPane
						.ERROR_MESSAGE, null, options, options[0]);
				  if (n == 0) {
						break;
				  }
				}
				if (file == null) {
				  return;
				}
				FileUtil.copyFile(Config.getCurrentSnt(), file);
				String folderPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf('.'));
				File folder = new File(folderPath + "_snt");
				FileUtil.copyDirRec(Config.getCurrentSntDir(), folder);
				Text.loadSnt(file, false);
		  }
		};
		saveAsSnt.setEnabled(false);
		textMenu.add(new JMenuItem(saveAsSnt));
		preprocessText = new AbstractAction("Preprocess Text...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String txt = Config.getCurrentSnt().getAbsolutePath();
				txt = txt.substring(0, txt.length() - 3);
				txt = txt + "txt";
				GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
						.newPreprocessDialog(new File(txt), Config.getCurrentSnt());
			}
		};
		preprocessText.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK));
		preprocessText.setEnabled(false);
		textMenu.add(new JMenuItem(preprocessText));
		textMenu.addSeparator();

		changeLang = new AbstractAction("Change Language...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Config.changeLanguage();
			}
		};
		textMenu.add(new JMenuItem(changeLang));
		textMenu.addSeparator();
		applyLexicalResources = new AbstractAction("Apply Lexical Resources...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.newApplyLexicalResourcesFrame();
			}
		};
		applyLexicalResources.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK));
		applyLexicalResources.setEnabled(false);
		textMenu.add(new JMenuItem(applyLexicalResources));
		textMenu.addSeparator();
		openConcordance = new AbstractAction("Open Concordance") {
		  @Override
		  public void actionPerformed(ActionEvent e) {
				if (Config.getCurrentSnt() == null || Config.getCurrentSntDir() == null) {
				  return;
				}
				Config.getConcordanceDialogBox().setDialogType(JFileChooser.OPEN_DIALOG);
				final int returnVal = Config.getConcordanceDialogBox().showOpenDialog(UnitexFrame.mainFrame);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
				  // we return if the user has clicked on CANCEL
				  return;
				}
				final File f = Config.getConcordanceDialogBox().getSelectedFile();
				if (!f.exists()) {
				  JOptionPane.showMessageDialog(null, "File " + f.getAbsolutePath()
						+ " does not exist", "Error", JOptionPane.ERROR_MESSAGE);
				  return;
				}
				if(!f.getAbsolutePath().endsWith(".html")) {
				  JOptionPane.showMessageDialog(null, "File " + f.getAbsolutePath()
						+ " is not a HTML file", "Error", JOptionPane.ERROR_MESSAGE);
				  return;
				}
				for(ConcordanceFrame frame : GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).getConcordanceFrames()) {
				  if (frame.getFile().equals(f)) {
						if(frame.isClosed()) {
						  GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).closeConcordanceFrame(frame);
						  break;
						}
						GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).setCurrentFocusedConcordance(frame);
						return;
				  }
				}
				if(FileUtil.getHtmlPageTitle(f) == null) {
				  GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).newConcordanceDiffFrame(f);
				} else {
				  GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).newConcordanceFrame(f, 95);
				}
		  }
		};
		openConcordance.setEnabled(false);
		textMenu.add(new JMenuItem(openConcordance));
		locatePattern = new AbstractAction("Locate Pattern...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.newLocateFrame();
			}
		};
		locatePattern.setEnabled(false);
		locatePattern.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK));
		textMenu.add(new JMenuItem(locatePattern));
		saveAsConcordance = new AbstractAction("Save Concordance As...") {
		  @Override
		  public void actionPerformed(ActionEvent e) {
				if (Config.getCurrentSnt() == null || Config.getCurrentSntDir() == null || GlobalProjectManager.search(null)
				  .getFrameManagerAs(InternalFrameManager.class).getCurrentFocusedConcordance() == null) {
				  return;
				}
				JFileChooser fc = Config.getConcordanceDialogBox();
				fc.setMultiSelectionEnabled(false);
				fc.setDialogType(JFileChooser.SAVE_DIALOG);
				File file;
				for (; ; ) {
				  final int returnVal = fc.showSaveDialog(UnitexFrame.mainFrame);
				  if (returnVal != JFileChooser.APPROVE_OPTION) {
						return;
				  }
				  file = fc.getSelectedFile();
				  final String name = file.getAbsolutePath();
				  if (!name.endsWith(".html")) {
						file = new File(name + ".html");
				  }
				  if (file == null || !file.exists()) {
						break;
				  }
				  final String message = file + "\nalready exists. Do you want to replace it?";
				  final String[] options = {"Yes", "No"};
				  final int n = JOptionPane.showOptionDialog(null, message, "Error", JOptionPane.YES_NO_OPTION, JOptionPane
						.ERROR_MESSAGE, null, options, options[0]);
				  if (n == 0) {
						break;
				  }
				}
				if (file == null) {
				  return;
				}
				FileUtil.copyFile(GlobalProjectManager.search(null)
				  .getFrameManagerAs(InternalFrameManager.class)
				  .getCurrentFocusedConcordance().getFile(), file);
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).closeCurrentFocusedConcordance();
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).newConcordanceFrame(file, 95);
		  }
		};
		saveAsConcordance.setEnabled(false);
		textMenu.add(new JMenuItem(saveAsConcordance));
    cassys = new AbstractAction("Apply CasSys Cascade...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
						.newCassysFrame()
						.getContentPane().add(Config.getTransducerListDialogBox(),BorderLayout.WEST);
			}
		};
		cassys.setEnabled(false);
		final JMenuItem cassysItem = new JMenuItem(cassys);
		textMenu.add(cassysItem);
		displayLocatedSequences = new AbstractAction("Located Sequences...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.newConcordanceParameterFrame();
			}
		};
		displayLocatedSequences.setEnabled(false);
		textMenu.add(new JMenuItem(displayLocatedSequences));
		textMenu.addSeparator();
		elagComp = new AbstractAction("Compile Elag Grammars") {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
						.newElagCompFrame();
			}
		};
		textMenu.add(new JMenuItem(elagComp));
		textMenu.addSeparator();
		constructFst = new AbstractAction("Construct FST-Text...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
						.newConstructTfstFrame();
			}
		};
		constructFst.setEnabled(false);
		textMenu.add(new JMenuItem(constructFst));
		lemmatize = new AbstractAction("Lemmatize") {
			@Override
			public void actionPerformed(ActionEvent e) {
				File text_tfst = new File(Config.getCurrentSntDir(),
						"text.tfst");
				if (!text_tfst.exists()) {
					JOptionPane
							.showMessageDialog(
									null,
									"You must construct the text automaton before lemmatizing!",
									"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
						.newLemmatizeFrame();
			}
		};
		lemmatize.setEnabled(false);
		textMenu.add(new JMenuItem(lemmatize));
		constructSeqFst = new AbstractAction("Construct Sequences Automaton") {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.newConstructSeqTfstFrame();
			}
		};
		constructSeqFst.setEnabled(true);
		textMenu.add(new JMenuItem(constructSeqFst));
		convertFst = new AbstractAction("Convert FST-Text to Text...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
						.newConvertTfstToTextFrame();
			}
		};
		convertFst.setEnabled(false);
		textMenu.add(new JMenuItem(convertFst));
		exportTfstAsCsv = new AbstractAction("Export FST-Text as CSV") {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportAsCsv();
			}
		};
		exportTfstAsCsv.setEnabled(false);
		textMenu.add(new JMenuItem(exportTfstAsCsv));
		textMenu.addSeparator();
		closeText = new AbstractAction("Close Text...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.closeTextFrame();
			}
		};
		closeText.setEnabled(false);
		textMenu.add(new JMenuItem(closeText));
		quitUnitex = new AbstractAction("Quit Unitex") {
			@Override
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		};
		textMenu.add(new JMenuItem(quitUnitex));

		textMenu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuSelected(MenuEvent e) {
				List<SntFileEntry> l = PreferencesManager.getUserPreferences()
						.getRecentTexts();
        if (GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).getCurrentFocusedConcordance() != null) {
          String s = GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).getCurrentFocusedConcordance().getName();
          if (s != null) {
            JOptionPane.showMessageDialog(null, s);
          }
        }
        openRecent.setEnabled(l != null && l.size() > 0);
        if(GlobalProjectManager.search(null)
          .getFrameManagerAs(InternalFrameManager.class)
          .getCurrentFocusedConcordance() != null) {
          saveAsConcordance.setEnabled(true);
        } else {
          saveAsConcordance.setEnabled(false);
        }
			}
		});
		return textMenu;
	}

	AbstractAction checkDelaFormat;
	AbstractAction transliterate;
	AbstractAction sortDictionary;
	AbstractAction inflect;
	AbstractAction delasLeximir;
	AbstractAction confDelaLeximir;
	AbstractAction shellLeximir;
	AbstractAction compressIntoFST;
	AbstractAction closeDela;

	JMenu buildDELAMenu() {
		final JMenu delaMenu = new JMenu("DELA");
		final JMenuItem open2 = new JMenuItem("Open...");
		open2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openDELA();
			}
		});
		delaMenu.add(open2);

		final JMenu openRecent = new JMenu("Open Recent");
		openRecent.addMenuListener(new MenuAdapter() {
			@Override
			public void menuSelected(MenuEvent e) {
				List<File> l = PreferencesManager.getUserPreferences()
						.getRecentDictionaries();
				if (l == null)
					return;
				openRecent.removeAll();
				for (final File f : l) {
					final JMenuItem item = new JMenuItem(f.getPath());
					item.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent event) {
							if (!f.exists()) {
								JOptionPane.showMessageDialog(null,
										"File " + f.getAbsolutePath()
												+ " does not exist", "Error",
										JOptionPane.ERROR_MESSAGE);
								PreferencesManager.getUserPreferences()
										.removeRecentDictionary(f);
							} else {
								GlobalProjectManager.search(null)
										.getFrameManagerAs(InternalFrameManager.class)
										.newDelaFrame(f);
							}
						}
					});
					openRecent.add(item);
				}
				if (!l.isEmpty()) {
					final JMenuItem item = new JMenuItem(
							"Clear Recent DELAs List");
					item.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent event) {
							final String[] options = { "Yes", "No" };
							final int n = JOptionPane
									.showOptionDialog(
											UnitexFrame.this,
											"Do you really want to clear the Recent DELAs List?",
											"", JOptionPane.YES_NO_OPTION,
											JOptionPane.QUESTION_MESSAGE, null,
											options, options[0]);
							if (n == 0) {
								PreferencesManager.getUserPreferences()
										.clearRecentDictionaries();
							}
						}
					});
					openRecent.addSeparator();
					openRecent.add(item);
				}

			}
		});
		delaMenu.add(openRecent);
		//for shell
		shellLeximir = new AbstractAction("Compile...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
				.newShellDialog();
			}
		};
		shellLeximir.setEnabled(true);
		delaMenu.add(new JMenuItem(shellLeximir));

		final JMenuItem lookup = new JMenuItem("Lookup...");
		lookup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
						.newDicLookupFrame();
			}
		});
		delaMenu.add(lookup);
		delaMenu.addSeparator();
		checkDelaFormat = new AbstractAction("Check Format...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final File f = Config.getCurrentDELA();
				if (f == null) {
					JOptionPane.showMessageDialog(null,
							"No dictionary is selected!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.newCheckDicFrame(f);
			}
		};
		checkDelaFormat.setEnabled(false);
		checkDelaFormat.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_K, Event.CTRL_MASK));
		delaMenu.add(new JMenuItem(checkDelaFormat));
		transliterate = new AbstractAction("Transliterate...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
						.newTransliterationFrame();
			}
		};
		transliterate.setEnabled(false);
		delaMenu.add(new JMenuItem(transliterate));
		sortDictionary = new AbstractAction("Sort Dictionary") {
			@Override
			public void actionPerformed(ActionEvent e) {
				sortDELA();
			}
		};
		sortDictionary.setEnabled(false);
		delaMenu.add(new JMenuItem(sortDictionary));
		delaMenu.addSeparator();
		// for delas Menu
		delasLeximir = new AbstractAction("Edit Delas") {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
				.newChooseDelasDialog();
			}
		};
		delasLeximir.setEnabled(true);
		delaMenu.add(new JMenuItem(delasLeximir));
				
		delaMenu.addSeparator();
		inflect = new AbstractAction("Inflect...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final File dela = Config.getCurrentDELA();
				if (dela == null)
					return;
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.newInflectFrame(dela);
			}
		};
		inflect.setEnabled(false);
		delaMenu.add(new JMenuItem(inflect));
		compressIntoFST = new AbstractAction("Compress into FST") {
			@Override
			public void actionPerformed(ActionEvent e) {
				compressDELA();
			}
		};
		compressIntoFST.setEnabled(false);
		delaMenu.add(new JMenuItem(compressIntoFST));
		delaMenu.addSeparator();
		final AbstractAction buildKrMwuDic = new AbstractAction(
				"Build Korean MWU dic graph...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
						.newBuildKrMwuDicFrame();
			}
		};
		buildKrMwuDic.setEnabled(ConfigManager.getManager().isKorean(null));
		delaMenu.add(new JMenuItem(buildKrMwuDic));
		Config.addLanguageListener(new LanguageListener() {
			@Override
			public void languageChanged() {
				buildKrMwuDic.setEnabled(ConfigManager.getManager().isKorean(
						null));
			}
		});
		delaMenu.addSeparator();
		closeDela = new AbstractAction("Close") {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.closeCurrentDelaFrame();
			}
		};
		closeDela.setEnabled(false);
		delaMenu.add(new JMenuItem(closeDela));

		delaMenu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuSelected(MenuEvent e) {
				List<File> l = PreferencesManager.getUserPreferences()
						.getRecentDictionaries();
				openRecent.setEnabled(l != null && l.size() > 0);
			}
		});
		return delaMenu;
	}

	JMenu buildFsGraphMenu() {
		final JMenu graphMenu = new JMenu("FSGraph");
		final JMenuItem newGraph = new JMenuItem("New");
		newGraph.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.newGraphFrame(null);
			}
		});
		graphMenu.add(newGraph);
		final Action open = new AbstractAction("Open...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				openGraph();
			}
		};

		open.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));

		graphMenu.add(new JMenuItem(open));

		final JMenu openRecent = new JMenu("Open Recent");
		openRecent.addMenuListener(new MenuAdapter() {
			@Override
			public void menuSelected(MenuEvent e) {
				List<File> l = PreferencesManager.getUserPreferences()
						.getRecentGraphs();
				if (l == null)
					return;
				openRecent.removeAll();
				for (final File f : l) {
					final JMenuItem item = new JMenuItem(f.getPath());
					item.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent event) {
							if (!f.exists()) {
								JOptionPane.showMessageDialog(null,
										"File " + f.getAbsolutePath()
												+ " does not exist", "Error",
										JOptionPane.ERROR_MESSAGE);
								PreferencesManager.getUserPreferences()
										.removeRecentGraph(f);
							} else {
								GlobalProjectManager.search(null)
										.getFrameManagerAs(InternalFrameManager.class)
										.newGraphFrame(f);
							}
						}
					});
					openRecent.add(item);
				}
				if (!l.isEmpty()) {
					final JMenuItem item = new JMenuItem(
							"Clear Recent Graphs List");
					item.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent event) {
							final String[] options = { "Yes", "No" };
							final int n = JOptionPane
									.showOptionDialog(
											UnitexFrame.this,
											"Do you really want to clear the Recent Graphs List?",
											"", JOptionPane.YES_NO_OPTION,
											JOptionPane.QUESTION_MESSAGE, null,
											options, options[0]);
							if (n == 0) {
								PreferencesManager.getUserPreferences()
										.clearRecentGraphs();
							}
						}
					});
					openRecent.addSeparator();
					openRecent.add(item);
				}

			}
		});
		graphMenu.add(openRecent);

		final Action save = new AbstractAction("Save") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.saveGraph();
					return;
				}
				/*
				 * Evil hack to allow save with ctrl+S in the internal text
				 * editor
				 */
				final JInternalFrame frame = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class).getSelectedFrame();
				if (frame instanceof FileEditionTextFrame) {
					((FileEditionTextFrame) frame).saveFile();
					return;
				}
			}
		};
		save.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		graphMenu.add(new JMenuItem(save));
		final Action saveAs = new AbstractAction("Save as...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getCurrentFocusedGraphFrame();
				if (f != null)
					f.saveAsGraph();
			}
		};
		graphMenu.add(new JMenuItem(saveAs));
		final Action saveAll = new AbstractAction("Save All") {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAllGraphs();
			}
		};

		graphMenu.add(new JMenuItem(saveAll));
		final Action setup = new AbstractAction("Page Setup") {
			@Override
			public void actionPerformed(ActionEvent e) {
				PrintManager.pageSetup();
			}
		};

		final JMenu exportMenu = GraphMenuBuilder.createExportMenu();
		graphMenu.add(exportMenu);

		graphMenu.add(new JMenuItem(setup));
		final Action print = new AbstractAction("Print...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				PrintManager.print(GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getSelectedFrame());
			}
		};
		print.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke('P', Event.CTRL_MASK));
		graphMenu.add(new JMenuItem(print));
		final Action printAll = new AbstractAction("Print All...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				PrintManager.printAllGraphs(GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class).getGraphFrames());
			}
		};
		graphMenu.add(new JMenuItem(printAll));
		graphMenu.addSeparator();
		final Action findAndReplace = new AbstractAction("Find and replace") {
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<GraphFrame> frames = GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).getGraphFrames();
				if(frames.isEmpty()) {
					return;
				}
				final FindAndReplaceDialog dialog = GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).newFindAndReplaceDialog();
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).addObserver(dialog);
			}
		};
		findAndReplace.putValue(Action.ACCELERATOR_KEY,
		KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK));
		graphMenu.add(new JMenuItem(findAndReplace));
		graphMenu.addSeparator();
		final Action undo = new AbstractAction("Undo") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.undo();
					return;
				}
			}
		};
		undo.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK));
		graphMenu.add(new JMenuItem(undo));
		final Action redo = new AbstractAction("Redo") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.redo();
					return;
				}
			}
		};
		redo.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK));
		graphMenu.add(new JMenuItem(redo));
		graphMenu.addSeparator();
		final JMenu tools = new JMenu("Tools");
		final JMenuItem sortNodeLabel = new JMenuItem("Sort Node Label");
		sortNodeLabel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.sortNodeLabel();
				}
			}
		});
		final JMenuItem explorePaths = new JMenuItem("Explore graph paths");
		explorePaths.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
        GlobalProjectManager.search(null)
        .getFrameManagerAs(InternalFrameManager.class).newGraphPathFrame();
			}
		});

        final JMenuItem verifyBraces = new JMenuItem("Verify braces");
        verifyBraces.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final GraphFrame f = GlobalProjectManager.search(null)
                    .getFrameManagerAs(InternalFrameManager.class)
                    .getCurrentFocusedGraphFrame();
                if (f != null) {
                    f.verifyBraces();
                }
            }
        });
		final JMenuItem compileFST = new JMenuItem("Compile FST2");
		compileFST.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame currentFrame = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class).getCurrentFocusedGraphFrame();
				if (currentFrame == null)
					return;
				currentFrame.compileGraph();
			}
		});
		final JMenuItem flatten = new JMenuItem("Compile & Flatten FST2");
		flatten.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				compileAndFlattenGraph();
			}
		});
		final JMenuItem graphCollection = new JMenuItem(
				"Build Graph Collection");
		graphCollection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.newGraphCollectionFrame();
			}
		});
		final JMenuItem svn = new JMenuItem("Look for SVN conflicts");
		svn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConfigManager.getManager().getSvnMonitor(null).monitor(false);
			}
		});
		tools.add(sortNodeLabel);
		tools.add(explorePaths);
        tools.add(verifyBraces);
		tools.addSeparator();
		tools.add(compileFST);
		tools.add(flatten);
		tools.addSeparator();
		tools.add(graphCollection);
		tools.addSeparator();
		tools.add(svn);
    tools.addMenuListener(new MenuAdapter() {
      @Override
      public void menuSelected(MenuEvent e) {
        final GraphFrame f = GlobalProjectManager.search(null)
          .getFrameManagerAs(InternalFrameManager.class)
          .getCurrentFocusedGraphFrame();
        boolean existsFocusedGrFrame = f != null;
        boolean existsAnyGrFrame = GlobalProjectManager.search(null)
          .getFrameManagerAs(InternalFrameManager.class)
          .getGraphFrames().size() != 0;
        sortNodeLabel.setEnabled(existsFocusedGrFrame);
        explorePaths.setEnabled(existsAnyGrFrame);
        verifyBraces.setEnabled(existsFocusedGrFrame);
        compileFST.setEnabled(existsFocusedGrFrame);
        flatten.setEnabled(existsFocusedGrFrame);
        graphCollection.setEnabled(existsFocusedGrFrame);
        svn.setEnabled(existsFocusedGrFrame);
      }
    });
		graphMenu.add(tools);
		final JMenu format = new JMenu("Format");
		final JMenuItem alignment = new JMenuItem("Alignment...");
		alignment.setAccelerator(KeyStroke.getKeyStroke('M', Event.CTRL_MASK));
		alignment.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					GlobalProjectManager.search(null)
							.getFrameManagerAs(InternalFrameManager.class)
							.newGraphAlignmentDialog(f);
				}
			}
		});
		final JMenuItem antialiasing = new JMenuItem("Antialiasing...");
		antialiasing.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JInternalFrame f = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getSelectedFrame();
				if (f == null)
					return;
				if (f instanceof GraphFrame) {
					final GraphFrame f2 = (GraphFrame) f;
					f2.changeAntialiasingValue();
					return;
				}
				if (f instanceof TextAutomatonFrame) {
					final TextAutomatonFrame f2 = (TextAutomatonFrame) f;
					f2.changeAntialiasingValue();
				}
			}
		});
		final JMenuItem presentation = new JMenuItem("Presentation...");
		presentation.setAccelerator(KeyStroke
				.getKeyStroke('R', Event.CTRL_MASK));
		presentation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					final GraphPresentationInfo info = GlobalProjectManager.search(null)
							.getFrameManagerAs(InternalFrameManager.class)
							.newGraphPresentationDialog(f.getGraphPresentationInfo(), true);
					if (info != null) {
						f.setGraphPresentationInfo(info);
					}
				}
			}
		});
		final JMenuItem graphSize = new JMenuItem("Graph Size...");
		graphSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					GlobalProjectManager.search(null)
							.getFrameManagerAs(InternalFrameManager.class).newGraphSizeDialog(f);
				}
			}
		});
		format.add(antialiasing);
		format.addSeparator();
		format.add(alignment);
		format.add(presentation);
		format.add(graphSize);
		final JMenu zoom = new JMenu("Zoom");
		final ButtonGroup groupe = new ButtonGroup();
		final JRadioButtonMenuItem fitInScreen = new JRadioButtonMenuItem(
				"Fit in screen");
		final JRadioButtonMenuItem fitInWindow = new JRadioButtonMenuItem(
				"Fit in window");
		final JRadioButtonMenuItem fit60 = new JRadioButtonMenuItem("60%");
		final JRadioButtonMenuItem fit80 = new JRadioButtonMenuItem("80%");
		final JRadioButtonMenuItem fit100 = new JRadioButtonMenuItem("100%");
		final JRadioButtonMenuItem fit120 = new JRadioButtonMenuItem("120%");
		final JRadioButtonMenuItem fit140 = new JRadioButtonMenuItem("140%");
		groupe.add(fitInScreen);
		groupe.add(fitInWindow);
		groupe.add(fit60);
		groupe.add(fit80);
		groupe.add(fit100);
		fit100.setSelected(true);
		groupe.add(fit120);
		groupe.add(fit140);
		fitInScreen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					final double scale_x = (double) screenSize.width
							/ (double) f.graphicalZone.getWidth();
					final double scale_y = (double) screenSize.height
							/ (double) f.graphicalZone.getHeight();
					if (scale_x < scale_y)
						f.setScaleFactor(scale_x);
					else
						f.setScaleFactor(scale_y);
				}
			}
		});
		fitInWindow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					final Dimension d = f.getScroll().getSize();
					final double scale_x = (double) (d.width - 3)
							/ (double) f.graphicalZone.getWidth();
					final double scale_y = (double) (d.height - 3)
							/ (double) f.graphicalZone.getHeight();
					if (scale_x < scale_y)
						f.setScaleFactor(scale_x);
					else
						f.setScaleFactor(scale_y);
					f.compListener = new ComponentAdapter() {
						@Override
						public void componentResized(ComponentEvent e2) {
							final Dimension d2 = f.getScroll().getSize();
							final double scale_x2 = (double) (d2.width - 3)
									/ (double) f.graphicalZone.getWidth();
							final double scale_y2 = (double) (d2.height - 3)
									/ (double) f.graphicalZone.getHeight();
							if (scale_x2 < scale_y2)
								f.setScaleFactor(scale_x2);
							else
								f.setScaleFactor(scale_y2);
						}
					};
					f.addComponentListener(f.compListener);
				}
			}
		});
		fit60.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(0.6);
				}
			}
		});
		fit80.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(0.8);
				}
			}
		});
		fit100.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(1.0);
				}
			}
		});
		fit120.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(1.2);
				}
			}
		});
		fit140.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(1.4);
				}
			}
		});
		zoom.add(fitInScreen);
		zoom.add(fitInWindow);
		zoom.add(fit60);
		zoom.add(fit80);
		zoom.add(fit100);
		zoom.add(fit120);
		zoom.add(fit140);

		final JMenuItem newCascade = new JMenuItem("New Cascade");
		newCascade.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openCascade(null);
			}
		});

		final JMenuItem editCascade = new JMenuItem("Edit Cascade ...");
		editCascade.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editCascade();
			}
		});

		final JMenuItem closeAll = new JMenuItem("Close all");
		closeAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class).closeAllGraphFrames();
			}
		});
		graphMenu.add(tools);
		graphMenu.add(format);
		graphMenu.add(zoom);
		graphMenu.addSeparator();

		graphMenu.add(newCascade);
		graphMenu.add(editCascade);
		graphMenu.addSeparator();

		graphMenu.add(closeAll);

		graphMenu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuSelected(MenuEvent e) {
				GraphFrame f = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getCurrentFocusedGraphFrame();

				boolean existsFocusedGrFrame = f != null;
				boolean existsAnyGrFrame = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getGraphFrames().size() != 0;

				save.setEnabled(existsFocusedGrFrame);
				saveAs.setEnabled(existsFocusedGrFrame);
				saveAll.setEnabled(existsAnyGrFrame);
				exportMenu.setEnabled(existsFocusedGrFrame);
				setup.setEnabled(existsAnyGrFrame);
				print.setEnabled(existsFocusedGrFrame);
				printAll.setEnabled(existsAnyGrFrame);
				undo.setEnabled(existsFocusedGrFrame);
				redo.setEnabled(existsFocusedGrFrame);
				tools.setEnabled(existsAnyGrFrame);
				format.setEnabled(existsFocusedGrFrame);
				zoom.setEnabled(existsFocusedGrFrame);
				findAndReplace.setEnabled(existsAnyGrFrame);

				List<File> l = PreferencesManager.getUserPreferences()
						.getRecentGraphs();

				openRecent.setEnabled(l != null && l.size() > 0);

			}

			// Some menus are bond to keyboard shortcuts.
			// Suppose that these menus are already disabled by menuSelected(), it's still possible
			// that the states change afterwards by user's interaction. Now the user won't be able
			// to use shortcuts, unless click to show the FSGraph menu, in order to reenable the menu
			// by menuSelected() again.
			//
			// This workaround reenable the menu when the menu is collapsed. Since user won't see them
			// now, reenabling the menu is not misleading to the user. And the user can still access
			// the shortcuts.
			@Override
			public void menuDeselected(MenuEvent e) {
				save.setEnabled(true);
				print.setEnabled(true);
				undo.setEnabled(true);
				redo.setEnabled(true);
        findAndReplace.setEnabled(true);
			}
		});
		return graphMenu;
	}

	AbstractAction openLexiconGrammar;
	AbstractAction compileLexiconGrammar;
	AbstractAction closeLexiconGrammar;

	JMenu buildLexiconGrammarMenu() {
		final JMenu lexiconGrammar = new JMenu("Lexicon-Grammar");
		openLexiconGrammar = new AbstractAction("Open...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				openLexiconGrammarTable();
			}
		};
		openLexiconGrammar.setEnabled(true);
		lexiconGrammar.add(new JMenuItem(openLexiconGrammar));
		compileLexiconGrammar = new AbstractAction("Compile to GRF...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final LexiconGrammarTableFrame f = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class).getLexiconGrammarTableFrame();
				if (f == null) {
					throw new IllegalStateException("Should not happen !");
				}
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.newConvertLexiconGrammarFrame(f.getTable());
			}
		};
		compileLexiconGrammar.setEnabled(false);
		lexiconGrammar.add(new JMenuItem(compileLexiconGrammar));
		closeLexiconGrammar = new AbstractAction("Close") {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.closeLexiconGrammarTableFrame();
			}
		};
		closeLexiconGrammar.setEnabled(false);
		lexiconGrammar.add(new JMenuItem(closeLexiconGrammar));
		return lexiconGrammar;
	}

	private JMenu buildXAlignMenu() {
		final JMenu menu = new JMenu("XAlign");
		final JMenuItem open = new JMenuItem("Open files...");
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
						.newXAlignConfigFrame();
			}
		});
		menu.add(open);
		return menu;
	}

	JMenu buildWindowsMenu() {
		final JMenu windows = new JMenu("Windows");
		final JMenuItem tile = new JMenuItem("Tile");
		final List<JMenuItem> frameItems = new ArrayList<JMenuItem>();
		tile.setEnabled(true);
		tile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tileFrames();
			}
		});
		final JMenuItem cascade = new JMenuItem("Cascade");
		cascade.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cascadeFrames();
			}
		});
		final JMenuItem arrangeIcons = new JMenuItem("Arrange Icons");
		arrangeIcons.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				arrangeIcons();
			}
		});
		windows.add(tile);
		windows.add(cascade);
		windows.add(arrangeIcons);
		windows.addSeparator();

		windows.addMenuListener(new MenuAdapter() {
			protected int frameClassPrec(JInternalFrame frame) {
				if (frame instanceof TextFrame)
					return 1;
				if (frame instanceof GraphFrame)
					return 2;
				if (frame instanceof TextDicFrame)
					return 4;
				if (frame instanceof TokensFrame)
					return 5;
				return 6;
			}

			class JFrameComparator implements Comparator<JInternalFrame> {
				@Override
				public int compare(JInternalFrame f1, JInternalFrame f2) {
					int c = frameClassPrec(f1) - frameClassPrec(f2);
					if (c != 0)
						return c;
					return f1.getTitle().compareTo(f2.getTitle());
				}
			}

			protected JFrameComparator jFrameComparator = new JFrameComparator();

			@Override
			public void menuSelected(MenuEvent e) {
				for (JMenuItem item : frameItems)
					windows.remove(item);
				final JInternalFrame[] frames = desktop.getAllFrames();
				Arrays.sort(frames, jFrameComparator);
				for (final JInternalFrame f : frames) {
					if (f.isVisible()) {
						final JMenuItem fItem = new JMenuItem(f.getTitle());
						fItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent event) {
								try {
									if (f.isIcon()) {
										f.setIcon(false);
									}
									f.setSelected(true);
									f.toFront();
								} catch (PropertyVetoException ex) {
									return;
								}
							}
						});
						frameItems.add(fItem);
						windows.add(fItem);
					}
				}
			}

		});
		return windows;
	}

	JMenu buildInfoMenu() {
		final JMenu info = new JMenu("Info");
		final JMenuItem aboutUnitex = new JMenuItem("About Unitex...");
		aboutUnitex.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File appDir = Config.getApplicationDir();
				File disclaimersDir = new File(appDir.getPath()
						+ File.separatorChar + "disclaimers");
				File licensesDir = new File(appDir.getPath()
						+ File.separatorChar + "licenses");
				new AboutDialog(UnitexFrame.this, "Unitex",
				                new ImageIcon(Unitex.class.getResource("Unitex.jpg")),
												"Unitex-GramLab.txt",
												disclaimersDir,
												licensesDir);
			}
		});
		final JMenuItem preferences = new JMenuItem("Preferences...");
		preferences.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
						.newGlobalPreferencesFrame();
			}
		});
		final JMenuItem console = new JMenuItem("Console");
		console.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.showConsoleFrame();
			}
		});
		info.add(aboutUnitex);
		info.addSeparator();
		info.add(preferences);
		info.add(console);
		return info;
	}

	/**
	 * This method is called when the user tries to close the main window, or
	 * when he clicks on the "Quit Unitex" item in the "Text" menu.
	 */
	void quit() {
		final String[] options = { "Yes", "No" };
		final int n = JOptionPane.showOptionDialog(this,
				"Do you really want to quit ?", "", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (n != 0) {
			return;
		}
		UnitexFrame.closing = true;
		GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
				.closeAllFrames();
		System.exit(0);
	}

	/**
	 * Shows a dialog box to select a corpus. If a corpus is selected, it is
	 * opened with a call to the <code>Text.loadCorpus(String)</code> method.
	 */
	void openText() {
		Config.getCorpusDialogBox().setDialogType(JFileChooser.OPEN_DIALOG);
		final int returnVal = Config.getCorpusDialogBox().showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		final File f = Config.getCorpusDialogBox().getSelectedFile();
		if (!f.exists()) {
			JOptionPane.showMessageDialog(null, "File " + f.getAbsolutePath()
					+ " does not exist", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Text.loadCorpus(f);
	}

	/**
	 * Shows a dialog box to select a tagged corpus. If a corpus is selected, it
	 * is opened with a call to the <code>Text.loadCorpus(String)</code> method.
	 */
	void openTaggedText() {
		Config.getTaggedCorpusDialogBox().setDialogType(JFileChooser.OPEN_DIALOG);
		final int returnVal = Config.getTaggedCorpusDialogBox().showOpenDialog(
				this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		final File f = Config.getTaggedCorpusDialogBox().getSelectedFile();
		if (!f.exists()) {
			JOptionPane.showMessageDialog(null, "File " + f.getAbsolutePath()
					+ " does not exist", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Text.loadCorpus(f, true);
	}

	/**
	 * Shows a dialog box to select on or more graphs. The selected graphs are
	 * opened with a call to the <code>loadGraph(String,String,String)</code>
	 * method.
	 */
	public void openGraph() {
		final JFileChooser fc = Config.getGraphDialogBox(false);
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		final int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		final File[] graphs = fc.getSelectedFiles();
		for (int i = 0; i < graphs.length; i++) {
			String s = graphs[i].getAbsolutePath();
			if (!graphs[i].exists() && !s.endsWith(".grf")) {
				s = s + ".grf";
				graphs[i] = new File(s);
				if (!graphs[i].exists()) {
					JOptionPane.showMessageDialog(null,
							"File " + graphs[i].getAbsolutePath()
									+ " does not exist", "Error",
							JOptionPane.ERROR_MESSAGE);
					continue;
				}
			}
			GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
					.newGraphFrame(graphs[i]);
		}
	}

	/**
	 * Shows a dialog box to select a lexicon-grammar table. If a table is
	 * selected, a <code>LexiconGrammarTableFrame</code> object is created.
	 */
	void openLexiconGrammarTable() {
		final int returnVal = Config.getTableDialogBox().showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		final String name;
		try {
			name = Config.getTableDialogBox().getSelectedFile()
					.getCanonicalPath();
		} catch (final IOException e) {
			return;
		}
		final File f = new File(name);
		if (!f.exists()) {
			JOptionPane.showMessageDialog(null, "Cannot find " + name, "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!f.canRead()) {
			JOptionPane.showMessageDialog(null, "Cannot read " + name, "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (null == Encoding.getEncoding(f)) {
			JOptionPane.showMessageDialog(null, name
					+ " is not a Unicode lexicon grammar table", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
				.newLexiconGrammarTableFrame(f);
	}

	/**
	 * Saves all <code>GraphFrame</code> that are on the desktop.
	 */
	void saveAllGraphs() {
		final JInternalFrame[] frames = desktop.getAllFrames();
		for (final JInternalFrame frame : frames) {
			if (frame instanceof GraphFrame) {
				((GraphFrame) frame).saveGraph();
			}
		}
	}

	/**
	 * Shows a window that offers the user to compile and flatten a graph. If
	 * the user clicks on the "OK" button, the compilation process is launched
	 * through the creation of a <code>ProcessInfoFrame</code> object.
	 */
	public static void compileAndFlattenGraph() {
		final GraphFrame currentFrame = GlobalProjectManager.search(null)
				.getFrameManagerAs(InternalFrameManager.class)
				.getCurrentFocusedGraphFrame();
		if (currentFrame == null)
			return;
		if (currentFrame.modified) {
			JOptionPane.showMessageDialog(null,
					"Save graph before compiling it", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		final File grf = currentFrame.getGraph();
		if (grf == null) {
			JOptionPane.showMessageDialog(null,
					"Cannot compile a graph with no name", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		final JPanel mainpane = new JPanel();
		mainpane.setLayout(new BorderLayout());
		final JPanel pane = new JPanel();
		pane.setLayout(new GridLayout(2, 1));
		pane.setBorder(new TitledBorder("Expected result grammar format:"));
		final JRadioButton rtn = new JRadioButton(
				"equivalent FST2 (subgraph calls may remain)");
		final JRadioButton fst = new JRadioButton(
				"Finite State Transducer (can be just an approximation)");
		final ButtonGroup bg = new ButtonGroup();
		bg.add(rtn);
		bg.add(fst);
		rtn.setSelected(true);
		final JPanel subpane = new JPanel();
		subpane.setBorder(new TitledBorder("Flattening depth:"));
		subpane.setLayout(new FlowLayout(SwingConstants.HORIZONTAL));
		subpane.add(new JLabel("Maximum flattening depth: "));
		final JTextField depth = new JTextField("10");
		subpane.add(depth);
		pane.add(rtn);
		pane.add(fst);
		mainpane.add(pane, BorderLayout.CENTER);
		mainpane.add(subpane, BorderLayout.SOUTH);
		final String[] options = { "OK", "Cancel" };
		if (0 == JOptionPane.showOptionDialog(null, mainpane,
				"Compile & Flatten", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0])) {
			int depthValue;
			try {
				depthValue = Integer.parseInt(depth.getText());
			} catch (final NumberFormatException e) {
				depthValue = -1;
			}
			if (depthValue < 1) {
				JOptionPane.showMessageDialog(null, "Invalid depth value",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String name_fst2 = grf.getAbsolutePath().substring(0,
					grf.getAbsolutePath().length() - 4);
			name_fst2 = name_fst2 + ".fst2";
			final MultiCommands commands = new MultiCommands();
			commands.addCommand(new Grf2Fst2Command().grf(grf)
					.enableLoopAndRecursionDetection(true)
					.tokenizationMode(null, grf).repositories()
					.emitEmptyGraphWarning().displayGraphNames());
			commands.addCommand(new FlattenCommand().fst2(new File(name_fst2))
					.resultType(!rtn.isSelected()).depth(depthValue));
			Launcher.exec(commands, false);
		}
	}
	
	/**
	 * Shows a window that offers the user to flatten a graph. This method 
	 * allows `compile and flatten` to be used inside another frame such as 
	 * 'explore graph paths'
	 * @param flattenMode false for equivalent to fst2, true for finite state transducer
	 * @param flattenDepth maximum
	 * @return Map containing the commands and options to allow saving the options selected
	 */
	public static Map<String,Object> flattenGraph(File grf,boolean flattenMode,String flattenDepth) {
		if (grf == null) {
			JOptionPane.showMessageDialog(null,
					"Cannot compile a graph with no name", "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		final JPanel mainpane = new JPanel();
		mainpane.setLayout(new BorderLayout());
		final JPanel pane = new JPanel();
		pane.setLayout(new GridLayout(2, 1));
		pane.setBorder(new TitledBorder("Expected result grammar format:"));
		final JRadioButton rtn = new JRadioButton(
				"equivalent FST2 (subgraph calls may remain)");
		final JRadioButton fst = new JRadioButton(
				"Finite State Transducer (can be just an approximation)");
		final ButtonGroup bg = new ButtonGroup();
		bg.add(rtn);
		bg.add(fst);
		if( !flattenMode ) {
			rtn.setSelected(true);
		} else {
			fst.setSelected(true);
		}
		final JPanel subpane = new JPanel();
		subpane.setBorder(new TitledBorder("Flattening depth:"));
		subpane.setLayout(new FlowLayout(SwingConstants.HORIZONTAL));
		subpane.add(new JLabel("Maximum flattening depth: "));
		final JTextField depth = new JTextField(flattenDepth);
		subpane.add(depth);
		pane.add(rtn);
		pane.add(fst);
		mainpane.add(pane, BorderLayout.CENTER);
		mainpane.add(subpane, BorderLayout.SOUTH);
		final String[] options = { "OK", "Cancel" };
		if (0 == JOptionPane.showOptionDialog(null, mainpane,
				"Flatten", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0])) {
			int depthValue;
			try {
				depthValue = Integer.parseInt(depth.getText());
			} catch (final NumberFormatException e) {
				depthValue = -1;
			}
			if (depthValue < 1) {
				JOptionPane.showMessageDialog(null, "Invalid depth value",
						"Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
			String name_fst2 = FileUtil.getFileNameWithoutExtension(grf);
			name_fst2 = name_fst2 + ".fst2";
			final MultiCommands commands = new MultiCommands();
			commands.addCommand(new Grf2Fst2Command().grf(grf)
					.enableLoopAndRecursionDetection(true)
					.tokenizationMode(null, grf).repositories()
					.emitEmptyGraphWarning().displayGraphNames());
			commands.addCommand(new FlattenCommand().fst2(new File(name_fst2))
					.resultType(!rtn.isSelected()).depth(depthValue));
			
			Map<String,Object> result = new HashMap<>();
			result.put("commands", commands);
			result.put("flattenMode", !rtn.isSelected());
			result.put("flattenDepth", depth.getText());
			return result;
		}
		return null;
	}

	/**
	 * Shows a dialog box to select a dictionary. If a dictionary is selected,
	 * it is opened with a call to the <code>DelaFrame.loadDela(String)</code>
	 * method.
	 */
	void openDELA() {
		Config.getDelaDialogBox().setDialogType(JFileChooser.OPEN_DIALOG);
		final int returnVal = Config.getDelaDialogBox().showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		final File dela = Config.getDelaDialogBox().getSelectedFile();
		final ToDo toDo = new ToDo() {
			@Override
			public void toDo(boolean success) {
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.newDelaFrame(dela);
			}
		};
		if (null == Encoding.getEncoding(dela)) {
			GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
					.newTranscodeOneFileDialog(dela, toDo);
		} else {
			toDo.toDo(true);
		}
	}

	/**
	 * Sorts the current dictionary. The external program "SortTxt" is called
	 * through the creation of a <code>ProcessInfoFrame</code> object.
	 */
	void sortDELA() {
		final File dela = Config.getCurrentDELA();
		SortTxtCommand command = new SortTxtCommand().file(dela);
		if (Config.getCurrentLanguage().equals("Thai")) {
			command = command.thai(true);
		} else {
			command = command.sortAlphabet(new File(Config
					.getUserCurrentLanguageDir(), "Alphabet_sort.txt"));
		}
		GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
				.closeCurrentDelaFrame();
		Launcher.exec(command, true, new DelaDo(dela));
	}

	/**
	 * Compresses the current dictionary. The external program "Compress" is
	 * called through the creation of a <code>ProcessInfoFrame</code> object.
	 */
	void compressDELA() {
		CompressCommand command = new CompressCommand().dic(Config
				.getCurrentDELA());
		if (ConfigManager.getManager().isSemiticLanguage(null)) {
			command = command.semitic();
		}
		Launcher.exec(command, false, null);
	}

	/**
	 * Tiles all the frames that are on the desktop and that are not iconified.
	 */
	void tileFrames() {
		final JInternalFrame[] f = desktop.getAllFrames();
		int openFrameCount = 0;
		for (final JInternalFrame aF1 : f) {
			if (aF1.isVisible() && !aF1.isIcon()) {
				openFrameCount++;
			}
		}
		if (openFrameCount == 0)
			return;
		final Dimension bounds = getContentPane().getSize();
		if (openFrameCount == 1) {
			try {
				final JInternalFrame F = f[0];
				if (F.isVisible() && !F.isIcon()) {
					F.setBounds(0, 0, bounds.width, bounds.height);
				}
			} catch (final ClassCastException e) {
				// nothing to do
			}
		}
		if (openFrameCount == 2) {
			for (final JInternalFrame aF : f) {
				try {
					final JInternalFrame F = aF;
					if (F.isVisible() && !F.isIcon()) {
						if (openFrameCount == 2)
							F.setBounds(0, 0, bounds.width, bounds.height / 2);
						else
							F.setBounds(0, bounds.height / 2, bounds.width,
									bounds.height / 2);
					}
					openFrameCount--;
				} catch (final ClassCastException e) {
					// nothing to do
				}
			}
			return;
		}
		if (openFrameCount == 3) {
			for (final JInternalFrame aF : f) {
				try {
					final JInternalFrame F = aF;
					if (F.isVisible() && !F.isIcon()) {
						if (openFrameCount == 3)
							F.setBounds(0, 0, bounds.width, bounds.height / 3);
						else if (openFrameCount == 2)
							F.setBounds(0, bounds.height / 3, bounds.width,
									bounds.height / 3);
						else
							F.setBounds(0, 2 * bounds.height / 3, bounds.width,
									bounds.height / 3);
					}
					openFrameCount--;
				} catch (final ClassCastException e) {
					// nothing to do
				}
			}
			return;
		}
		final int premiere_moitie = openFrameCount / 2;
		final int seconde_moitie = openFrameCount - premiere_moitie;
		openFrameCount = 0;
		// drawing frames on the first half of screen
		int i;
		for (i = 0; i < f.length && openFrameCount < premiere_moitie; i++) {
			try {
				final JInternalFrame F = f[i];
				if (F.isVisible() && !F.isIcon()) {
					F.setBounds(0, openFrameCount * bounds.height
							/ premiere_moitie, bounds.width / 2, bounds.height
							/ premiere_moitie);
				}
				openFrameCount++;
			} catch (final ClassCastException e) {
				// nothing to do
			}
		}
		// drawing frames on the second half of screen
		openFrameCount = 0;
		for (; i < f.length && openFrameCount < seconde_moitie; i++) {
			try {
				final JInternalFrame F = f[i];
				if (F.isVisible() && !F.isIcon()) {
					F.setBounds(bounds.width / 2, openFrameCount
							* bounds.height / seconde_moitie, bounds.width / 2,
							bounds.height / seconde_moitie);
				}
				openFrameCount++;
			} catch (final ClassCastException e) {
				// nothing to do
			}
		}
	}

	/**
	 * Cascades all the frames that are on the desktop and that are not
	 * iconified.
	 */
	void cascadeFrames() {
		final Component[] f = desktop.getComponents();
		int openFrameCount = 0;
		final int offset = 30;
		for (final Component aF : f) {
			try {
				final JInternalFrame F = (JInternalFrame) aF;
				if (F.isVisible() && !F.isIcon()) {
					openFrameCount++;
					F.setBounds(offset * (openFrameCount % 6), offset
							* (openFrameCount % 6), 800, 600);
					try {
						F.setSelected(true);
					} catch (final PropertyVetoException e) {
						e.printStackTrace();
					}
				}
			} catch (final ClassCastException e) {
				// nothing to do
			}
		}
	}

	/**
	 * Arranges all the iconified frames that are on the desktop.
	 */
	void arrangeIcons() {
		final Component[] f = desktop.getComponents();
		int openFrameCount = 0;
		final Dimension desktop_bounds = getContentPane().getSize();
		for (final Component aF : f) {
			try {
				final JInternalFrame.JDesktopIcon F = (JInternalFrame.JDesktopIcon) aF;
				if (F.isVisible()) {
					final Dimension icon_bounds = F.getSize();
					int X, Y;
					int n_icons_by_line = (desktop_bounds.width)
							/ (icon_bounds.width);
					if (n_icons_by_line == 0)
						n_icons_by_line = 1;
					X = openFrameCount % n_icons_by_line;
					Y = openFrameCount / n_icons_by_line;
					F.setBounds(X * icon_bounds.width, desktop_bounds.height
							- (Y + 1) * icon_bounds.height, icon_bounds.width,
							icon_bounds.height);
					openFrameCount++;
				}
			} catch (final ClassCastException e) {
				// nothing to do
			}
		}
	}

	class DelaDo implements ToDo {
		final File dela;

		public DelaDo(File s) {
			dela = s;
		}

		@Override
		public void toDo(boolean success) {
			GlobalProjectManager.search(dela)
					.getFrameManagerAs(InternalFrameManager.class).newDelaFrame(dela);
		}
	}

	public static JInternalFrame getCurrentFocusedFrame() {
		return mainFrame.desktop.getSelectedFrame();
	}

	private void createExportFst2(File fst2) {
		OutputStreamWriter writer = Encoding.UTF8.getOutputStreamWriter(fst2);
		try {
			writer.write("0000000001\n");
			writer.write("-1 export_csv\n");
			writer.write(": 5 2 4 2 3 2 1 1 \n");
			writer.write(": 2 2 \n");
			writer.write("t \n");
			writer.write("f \n");
			writer.write("%<E>\n");
			writer.write("%<DIC>/$:x$\n");
			writer.write("%<E>//$x.LEMMA$.$x.CODE$\n");
			writer.write("%<!MOT>\n");
			writer.write("@ \n");
			writer.write("%<!DIC>\n");
			writer.write("f\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exportAsCsv() {
		final File fst2 = new File(ConfigManager.getManager()
				.getCurrentLanguageDir(), "export_csv.fst2");
		createExportFst2(fst2);
		final MultiCommands commands = new MultiCommands();
		LocateTfstCommand cmd1 = new LocateTfstCommand()
				.allowAmbiguousOutputs()
				.alphabet(ConfigManager.getManager().getAlphabet(null))
				.mergeOutputs()
				.tfst(new File(Config.getCurrentSntDir(), "text.tfst"))
				.fst2(fst2).allMatches().backtrackOnVariableErrors()
				.singleTagsOnly();
		if (ConfigManager.getManager().isKorean(null)) {
			cmd1 = cmd1.korean();
		}
		if (!ConfigManager.getManager().isMatchWordBoundaries(null)) {
			cmd1 = cmd1.dontMatchWordBoundaries();
		}
		commands.addCommand(cmd1);
		ConcordCommand cmd2;
		File indFile = new File(Config.getCurrentSntDir(), "concord.ind");
		cmd2 = new ConcordCommand().indFile(indFile).exportAsCsv();
		if (ConfigManager.getManager().isPRLGLanguage(null)) {
			final File prlgIndex = new File(Config.getCurrentSntDir(),
					"prlg.idx");
			final File offsets = new File(Config.getCurrentSntDir(),
					"tokenize.out.offsets");
			if (prlgIndex.exists() && offsets.exists()) {
				cmd2 = cmd2.PRLG(prlgIndex, offsets);
			}
		}
		commands.addCommand(cmd2);
		final File csv = new File(Config.getCurrentSntDir(), "export.csv");
		final ToDo after = new ToDo() {

			@Override
			public void toDo(boolean success) {
				fst2.delete();
				JOptionPane.showMessageDialog(UnitexFrame.mainFrame,
						"The text automaton was successfully exported as:\n"
								+ "\n" + csv.getAbsolutePath());
			}

		};
		Launcher.exec(commands, true, after);
	}

	/**
	 * Open Cassys Configuration
	 * <em>selected_file<\em>. If <em>selected_file<\em> is empty, open
	 * an empty configuration file
	 *
	 *
	 * @param selected_file
	 */
	void openCascade(File selected_file) {
		TransducerListConfigurationFrame t = GlobalProjectManager.search(null)
				.getFrameManagerAs(InternalFrameManager.class)
				.getTransducerListConfigurationFrame();

		// If save has to be done. Do it first
		if (t != null && t.isConfigurationHasChanged()) {
			t.quit_asked();
		} else { // Open
			if (t != null) {
				t.quit();
			}
			Config.setCurrentTransducerList(selected_file);
			GlobalProjectManager.search(null)
					.getFrameManagerAs(InternalFrameManager.class)
					.newTransducerListConfigurationFrame(selected_file);
		}
	}

	/**
	 * Opens a JFile Chooser in order to select a file. Then open the Cassys
	 * Configuration frame with the selected file
	 */
	void editCascade() {
		Config.getTransducerListDialogBox().setControlButtonsAreShown(true);
		/*Config.getTransducerListDialogBox().setDialogType(
				JFileChooser.OPEN_DIALOG);*/
		final int returnVal = Config.getTransducerListDialogBox()
				.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		final File f = Config.getTransducerListDialogBox().getSelectedFile();
		if (!f.exists()) {
			JOptionPane.showMessageDialog(null, "File " + f.getAbsolutePath()
					+ " does not exist", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		openCascade(f);
                Config.getTransducerListDialogBox().setControlButtonsAreShown(false);
	}
}
