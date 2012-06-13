/*
 * Unitex
 *
 * Copyright (C) 2001-2012 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import fr.umlv.unitex.DropTargetManager;
import fr.umlv.unitex.Version;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.editor.FileEditionMenu;
import fr.umlv.unitex.grf.GraphPresentationInfo;
import fr.umlv.unitex.io.Encoding;
import fr.umlv.unitex.listeners.DelaFrameListener;
import fr.umlv.unitex.listeners.LanguageListener;
import fr.umlv.unitex.listeners.LexiconGrammarTableFrameListener;
import fr.umlv.unitex.listeners.TextFrameListener;
import fr.umlv.unitex.print.PrintManager;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.CompressCommand;
import fr.umlv.unitex.process.commands.FlattenCommand;
import fr.umlv.unitex.process.commands.Grf2Fst2Command;
import fr.umlv.unitex.process.commands.MultiCommands;
import fr.umlv.unitex.process.commands.SortTxtCommand;
import fr.umlv.unitex.text.Text;

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
	public DropTarget dropTarget = DropTargetManager.getDropTarget().newDropTarget(this);
	/**
	 * The desktop of the frame.
	 */
	private final JDesktopPane desktop;
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
		super(Version.version);
		final int inset = 50;
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height
				- inset * 2);
		desktop = new JDesktopPane();
		setContentPane(desktop);
		InternalFrameManager.setManager(new InternalFrameManager(desktop));
		buildMenus();
		mainFrame = this;
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setTitle(Version.version + " - current language is "
				+ Config.getCurrentLanguageForTitleBar());
		InternalFrameManager.getManager(null).addTextFrameListener(
				new TextFrameListener() {
					public void textFrameOpened(boolean taggedText) {
						preprocessText.setEnabled(!taggedText);
						cassys.setEnabled(true);
						applyLexicalResources.setEnabled(true);
						locatePattern.setEnabled(true);
						displayLocatedSequences.setEnabled(true);
						constructFst.setEnabled(true);
						constructSeqFst.setEnabled(true);
						convertFst.setEnabled(true);
						closeText.setEnabled(true);
						InternalFrameManager.getManager(null).newTokensFrame(
								new File(Config.getCurrentSntDir(),
										"tok_by_freq.txt"),true);
						InternalFrameManager.getManager(null).newTfstTagsFrame(
								new File(Config.getCurrentSntDir(),
										"tfst_tags_by_freq.txt"));
						InternalFrameManager.getManager(null).newTextDicFrame(
								Config.getCurrentSntDir(), true);
						InternalFrameManager.getManager(null)
								.newTextAutomatonFrame(1, true);
					}

					public void textFrameClosed() {
						cassys.setEnabled(false);
						preprocessText.setEnabled(false);
						applyLexicalResources.setEnabled(false);
						locatePattern.setEnabled(false);
						displayLocatedSequences.setEnabled(false);
						constructFst.setEnabled(false);
						convertFst.setEnabled(false);
						closeText.setEnabled(false);
						InternalFrameManager.getManager(null)
								.closeTokensFrame();
						InternalFrameManager.getManager(null)
								.closeConcordanceFrame();
						InternalFrameManager.getManager(null)
								.closeConcordanceDiffFrame();
						InternalFrameManager.getManager(null)
								.closeTextDicFrame();
						InternalFrameManager.getManager(null)
								.closeTextAutomatonFrame();
						InternalFrameManager.getManager(null)
								.closeTfstTagsFrame();
						InternalFrameManager.getManager(null)
								.closeApplyLexicalResourcesFrame();
						InternalFrameManager.getManager(null)
								.closeConcordanceParameterFrame();
						InternalFrameManager.getManager(null)
								.closeConstructTfstFrame();
						InternalFrameManager.getManager(null)
								.closeConvertTfstToTextFrame();
						InternalFrameManager.getManager(null)
								.closeLocateFrame();
						InternalFrameManager.getManager(null)
								.closeStatisticsFrame();
					}
				});
		InternalFrameManager.getManager(null).addDelaFrameListener(
				new DelaFrameListener() {
					public void delaFrameOpened() {
						checkDelaFormat.setEnabled(true);
						transliterate.setEnabled(true);
						sortDictionary.setEnabled(true);
						inflect.setEnabled(true);
						compressIntoFST.setEnabled(true);
						closeDela.setEnabled(true);
					}

					public void delaFrameClosed(int remainingFrames) {
						InternalFrameManager.getManager(null)
								.closeCheckDicFrame();
						InternalFrameManager.getManager(null)
								.closeTransliterationFrame();
						InternalFrameManager.getManager(null)
								.closeCheckResultFrame();
						InternalFrameManager.getManager(null)
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
		InternalFrameManager.getManager(null)
				.addLexiconGrammarTableFrameListener(
						new LexiconGrammarTableFrameListener() {
							public void lexiconGrammarTableFrameOpened() {
								compileLexiconGrammar.setEnabled(true);
								closeLexiconGrammar.setEnabled(true);
							}

							public void lexiconGrammarTableFrameClosed() {
								compileLexiconGrammar.setEnabled(false);
								closeLexiconGrammar.setEnabled(false);
							}
						});
		Config.addLanguageListener(new LanguageListener() {
			public void languageChanged() {
				setTitle(Version.version + " - current language is "
						+ Config.getCurrentLanguageForTitleBar());
				InternalFrameManager.getManager(null).closeTextFrame();
				InternalFrameManager.getManager(null)
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
		final JMenu info = buildInfoMenu();
		text.setMnemonic(KeyEvent.VK_T);
		DELA.setMnemonic(KeyEvent.VK_D);
		fsGraph.setMnemonic(KeyEvent.VK_G);
		lexiconGrammar.setMnemonic(KeyEvent.VK_L);
		xalign.setMnemonic(KeyEvent.VK_X);
		fileEditionMenu.setMnemonic(KeyEvent.VK_F);
		windows.setMnemonic(KeyEvent.VK_W);
		info.setMnemonic(KeyEvent.VK_I);
		menuBar.add(text);
		menuBar.add(DELA);
		menuBar.add(fsGraph);
		menuBar.add(lexiconGrammar);
		menuBar.add(xalign);
		menuBar.add(fileEditionMenu);
		menuBar.add(windows);
		menuBar.add(info);
		setJMenuBar(menuBar);
	}

	Action openText;
	Action openTaggedText;
	Action preprocessText;
	Action changeLang;
	Action applyLexicalResources;
	Action locatePattern;
	AbstractAction displayLocatedSequences;
	AbstractAction elagComp;
	AbstractAction constructFst;
	AbstractAction constructSeqFst;
	AbstractAction convertFst;
	AbstractAction closeText;
	AbstractAction quitUnitex;
	AbstractAction cassys;

	JMenu buildTextMenu() {
		final JMenu textMenu = new JMenu("Text");
		openText = new AbstractAction("Open...") {
			public void actionPerformed(ActionEvent e) {
				openText();
			}
		};
		openText.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_N, Event.CTRL_MASK));
		textMenu.add(new JMenuItem(openText));
		openTaggedText = new AbstractAction("Open Tagged Text...") {
			public void actionPerformed(ActionEvent e) {
				openTaggedText();
			}
		};
		textMenu.add(new JMenuItem(openTaggedText));
		preprocessText = new AbstractAction("Preprocess Text...") {
			public void actionPerformed(ActionEvent e) {
				String txt = Config.getCurrentSnt().getAbsolutePath();
				txt = txt.substring(0, txt.length() - 3);
				txt = txt + "txt";
				InternalFrameManager.getManager(null).newPreprocessDialog(
						new File(txt), Config.getCurrentSnt());
			}
		};
		preprocessText.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_E, Event.CTRL_MASK));
		preprocessText.setEnabled(false);
		textMenu.add(new JMenuItem(preprocessText));
		textMenu.addSeparator();
		changeLang = new AbstractAction("Change Language...") {
			public void actionPerformed(ActionEvent e) {
				Config.changeLanguage();
			}
		};
		textMenu.add(new JMenuItem(changeLang));
		textMenu.addSeparator();
		applyLexicalResources = new AbstractAction("Apply Lexical Resources...") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null)
						.newApplyLexicalResourcesFrame();
			}
		};
		applyLexicalResources.putValue(Action.ACCELERATOR_KEY, KeyStroke
				.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK));
		applyLexicalResources.setEnabled(false);
		textMenu.add(new JMenuItem(applyLexicalResources));
		textMenu.addSeparator();
		locatePattern = new AbstractAction("Locate Pattern...") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null).newLocateFrame();
			}
		};
		locatePattern.setEnabled(false);
		locatePattern.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_L, Event.CTRL_MASK));
		textMenu.add(new JMenuItem(locatePattern));
		cassys = new AbstractAction("Apply CasSys Cascade...") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null).newCassysFrame();
			}
		};
		cassys.setEnabled(false);
		final JMenuItem cassysItem = new JMenuItem(cassys);
		textMenu.add(cassysItem);
		displayLocatedSequences = new AbstractAction("Located Sequences...") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null)
						.newConcordanceParameterFrame();
			}
		};
		displayLocatedSequences.setEnabled(false);
		textMenu.add(new JMenuItem(displayLocatedSequences));
		textMenu.addSeparator();
		elagComp = new AbstractAction("Compile Elag Grammars") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null).newElagCompFrame();
			}
		};
		textMenu.add(new JMenuItem(elagComp));
		textMenu.addSeparator();
		constructFst = new AbstractAction("Construct FST-Text...") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null).newConstructTfstFrame();
			}
		};
		constructFst.setEnabled(false);
		textMenu.add(new JMenuItem(constructFst));
		constructSeqFst = new AbstractAction("Construct Sequences Automaton") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null)
						.newConstructSeqTfstFrame();
			}
		};
		constructSeqFst.setEnabled(true);
		textMenu.add(new JMenuItem(constructSeqFst));
		convertFst = new AbstractAction("Convert FST-Text to Text...") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null)
						.newConvertTfstToTextFrame();
			}
		};
		convertFst.setEnabled(false);
		textMenu.add(new JMenuItem(convertFst));
		textMenu.addSeparator();
		closeText = new AbstractAction("Close Text...") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null).closeTextFrame();
			}
		};
		closeText.setEnabled(false);
		textMenu.add(new JMenuItem(closeText));
		quitUnitex = new AbstractAction("Quit Unitex") {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		};
		textMenu.add(new JMenuItem(quitUnitex));
		return textMenu;
	}

	AbstractAction checkDelaFormat;
	AbstractAction transliterate;
	AbstractAction sortDictionary;
	AbstractAction inflect;
	AbstractAction compressIntoFST;
	AbstractAction closeDela;

	JMenu buildDELAMenu() {
		final JMenu delaMenu = new JMenu("DELA");
		final JMenuItem open2 = new JMenuItem("Open...");
		open2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openDELA();
			}
		});
		delaMenu.add(open2);
		final JMenuItem lookup = new JMenuItem("Lookup...");
		lookup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null).newDicLookupFrame();
			}
		});
		delaMenu.add(lookup);
		delaMenu.addSeparator();
		checkDelaFormat = new AbstractAction("Check Format...") {
			public void actionPerformed(ActionEvent e) {
				File f=Config.getCurrentDELA();
				if (f==null) {
					JOptionPane.showMessageDialog(null, "No dictionary is selected!",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				InternalFrameManager.getManager(null).newCheckDicFrame(f);
			}
		};
		checkDelaFormat.setEnabled(false);
		checkDelaFormat.putValue(Action.ACCELERATOR_KEY, KeyStroke
				.getKeyStroke(KeyEvent.VK_K, Event.CTRL_MASK));
		delaMenu.add(new JMenuItem(checkDelaFormat));
		transliterate = new AbstractAction("Transliterate...") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null).newTransliterationFrame();
			}
		};
		transliterate.setEnabled(false);
		delaMenu.add(new JMenuItem(transliterate));
		sortDictionary = new AbstractAction("Sort Dictionary") {
			public void actionPerformed(ActionEvent e) {
				sortDELA();
			}
		};
		sortDictionary.setEnabled(false);
		delaMenu.add(new JMenuItem(sortDictionary));
		inflect = new AbstractAction("Inflect...") {
			public void actionPerformed(ActionEvent e) {
				File dela=Config.getCurrentDELA();
				if (dela==null) return;
				InternalFrameManager.getManager(null).newInflectFrame(dela);
			}
		};
		inflect.setEnabled(false);
		delaMenu.add(new JMenuItem(inflect));
		compressIntoFST = new AbstractAction("Compress into FST") {
			public void actionPerformed(ActionEvent e) {
				compressDELA();
			}
		};
		compressIntoFST.setEnabled(false);
		delaMenu.add(new JMenuItem(compressIntoFST));
		delaMenu.addSeparator();
		final AbstractAction buildKrMwuDic = new AbstractAction(
				"Build Korean MWU dic graph...") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null).newBuildKrMwuDicFrame();
			}
		};
		buildKrMwuDic.setEnabled(ConfigManager.getManager().isKorean(null));
		delaMenu.add(new JMenuItem(buildKrMwuDic));
		Config.addLanguageListener(new LanguageListener() {
			public void languageChanged() {
				buildKrMwuDic.setEnabled(ConfigManager.getManager().isKorean(
						null));
			}
		});
		delaMenu.addSeparator();
		closeDela = new AbstractAction("Close") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null).closeCurrentDelaFrame();
			}
		};
		closeDela.setEnabled(false);
		delaMenu.add(new JMenuItem(closeDela));
		return delaMenu;
	}

	JMenu buildFsGraphMenu() {
		final JMenu graphMenu = new JMenu("FSGraph");
		final JMenuItem newGraph = new JMenuItem("New");
		newGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null).newGraphFrame(null);
			}
		});
		graphMenu.add(newGraph);
		final Action open = new AbstractAction("Open...") {
			public void actionPerformed(ActionEvent e) {
				openGraph();
			}
		};
		open.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_O, Event.CTRL_MASK));
		graphMenu.add(new JMenuItem(open));
		final Action save = new AbstractAction("Save") {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f=InternalFrameManager.getManager(null).getCurrentFocusedGraphFrame();
				if (f!=null) {
					f.saveGraph();
					return;
				}
				/* Evil hack to allow save with ctrl+S in the internal text editor */
				JInternalFrame frame=InternalFrameManager.getManager(null).getSelectedFrame();
				if (frame instanceof FileEditionTextFrame) {
					((FileEditionTextFrame)frame).saveFile();
					return;
				}
			}
		};
		save.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_S, Event.CTRL_MASK));
		graphMenu.add(new JMenuItem(save));
		final Action saveAs = new AbstractAction("Save as...") {
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = InternalFrameManager.getManager(null)
						.getCurrentFocusedGraphFrame();
				if (f != null)
					f.saveAsGraph();
			}
		};
		graphMenu.add(new JMenuItem(saveAs));
		final Action saveAll = new AbstractAction("Save All") {
			public void actionPerformed(ActionEvent e) {
				saveAllGraphs();
			}
		};
		graphMenu.add(new JMenuItem(saveAll));
		final Action setup = new AbstractAction("Page Setup") {
			public void actionPerformed(ActionEvent e) {
				PrintManager.pageSetup();
			}
		};
		graphMenu.add(new JMenuItem(setup));
		final Action print = new AbstractAction("Print...") {
			public void actionPerformed(ActionEvent e) {
				PrintManager.print(InternalFrameManager.getManager(null)
						.getSelectedFrame());
			}
		};
		print.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('P',
				Event.CTRL_MASK));
		graphMenu.add(new JMenuItem(print));
		final Action printAll = new AbstractAction("Print All...") {
			public void actionPerformed(ActionEvent e) {
				PrintManager.printAllGraphs(InternalFrameManager.getManager(
						null).getGraphFrames());
			}
		};
		graphMenu.add(new JMenuItem(printAll));
		graphMenu.addSeparator();
		final Action undo = new AbstractAction("Undo") {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f=InternalFrameManager.getManager(null).getCurrentFocusedGraphFrame();
				if (f!=null) {
					f.undo();
					return;
				}
			}
		};
		undo.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_Z, Event.CTRL_MASK));
		graphMenu.add(new JMenuItem(undo));
		final Action redo = new AbstractAction("Redo") {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f=InternalFrameManager.getManager(null).getCurrentFocusedGraphFrame();
				if (f!=null) {
					f.redo();
					return;
				}
			}
		};
		redo.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_Y, Event.CTRL_MASK));
		graphMenu.add(new JMenuItem(redo));
		graphMenu.addSeparator();
		final JMenu tools = new JMenu("Tools");
		final JMenuItem sortNodeLabel = new JMenuItem("Sort Node Label");
		sortNodeLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = InternalFrameManager.getManager(null)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.sortNodeLabel();
				}
			}
		});
		final JMenuItem explorePaths = new JMenuItem("Explore graph paths");
		explorePaths.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = InternalFrameManager.getManager(null)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					InternalFrameManager.getManager(null).newGraphPathDialog();
				}
			}
		});
		final JMenuItem compileFST = new JMenuItem("Compile FST2");
		compileFST.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final GraphFrame currentFrame = InternalFrameManager
						.getManager(null).getCurrentFocusedGraphFrame();
				if (currentFrame == null)
					return;
				currentFrame.compileGraph();
			}
		});
		final JMenuItem flatten = new JMenuItem("Compile & Flatten FST2");
		flatten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compileAndFlattenGraph();
			}
		});
		final JMenuItem graphCollection = new JMenuItem(
				"Build Graph Collection");
		graphCollection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null).newGraphCollectionFrame();
			}
		});
		final JMenuItem svn = new JMenuItem("Look for SVN conflicts");
		svn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigManager.getManager().getSvnMonitor(null).monitor(false);
			}
		});
		tools.add(sortNodeLabel);
		tools.add(explorePaths);
		tools.addSeparator();
		tools.add(compileFST);
		tools.add(flatten);
		tools.addSeparator();
		tools.add(graphCollection);
		tools.addSeparator();
		tools.add(svn);
		graphMenu.add(tools);
		final JMenu format = new JMenu("Format");
		final JMenuItem alignment = new JMenuItem("Alignment...");
		alignment.setAccelerator(KeyStroke.getKeyStroke('M', Event.CTRL_MASK));
		alignment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = InternalFrameManager.getManager(null)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					InternalFrameManager.getManager(null)
							.newGraphAlignmentDialog(f);
				}
			}
		});
		final JMenuItem antialiasing = new JMenuItem("Antialiasing...");
		antialiasing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JInternalFrame f = InternalFrameManager.getManager(null)
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
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = InternalFrameManager.getManager(null)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					final GraphPresentationInfo info = InternalFrameManager
							.getManager(null).newGraphPresentationDialog(
									f.getGraphPresentationInfo(), true);
					if (info != null) {
						f.setGraphPresentationInfo(info);
					}
				}
			}
		});
		final JMenuItem graphSize = new JMenuItem("Graph Size...");
		graphSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = InternalFrameManager.getManager(null)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					InternalFrameManager.getManager(null).newGraphSizeDialog(f);
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
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = InternalFrameManager.getManager(null)
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
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = InternalFrameManager.getManager(null)
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
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = InternalFrameManager.getManager(null)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(0.6);
				}
			}
		});
		fit80.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = InternalFrameManager.getManager(null)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(0.8);
				}
			}
		});
		fit100.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = InternalFrameManager.getManager(null)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(1.0);
				}
			}
		});
		fit120.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = InternalFrameManager.getManager(null)
						.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(1.2);
				}
			}
		});
		fit140.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = InternalFrameManager.getManager(null)
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
		final JMenuItem closeAll = new JMenuItem("Close all");
		closeAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null).closeAllGraphFrames();
			}
		});
		graphMenu.add(tools);
		graphMenu.add(format);
		graphMenu.add(zoom);
		graphMenu.addSeparator();
		graphMenu.add(closeAll);
		return graphMenu;
	}

	AbstractAction openLexiconGrammar;
	AbstractAction compileLexiconGrammar;
	AbstractAction closeLexiconGrammar;

	JMenu buildLexiconGrammarMenu() {
		final JMenu lexiconGrammar = new JMenu("Lexicon-Grammar");
		openLexiconGrammar = new AbstractAction("Open...") {
			public void actionPerformed(ActionEvent e) {
				openLexiconGrammarTable();
			}
		};
		openLexiconGrammar.setEnabled(true);
		lexiconGrammar.add(new JMenuItem(openLexiconGrammar));
		compileLexiconGrammar = new AbstractAction("Compile to GRF...") {
			public void actionPerformed(ActionEvent e) {
				final LexiconGrammarTableFrame f = InternalFrameManager
						.getManager(null).getLexiconGrammarTableFrame();
				if (f == null) {
					throw new IllegalStateException("Should not happen !");
				}
				InternalFrameManager.getManager(null)
						.newConvertLexiconGrammarFrame(f.getTable());
			}
		};
		compileLexiconGrammar.setEnabled(false);
		lexiconGrammar.add(new JMenuItem(compileLexiconGrammar));
		closeLexiconGrammar = new AbstractAction("Close") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null)
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
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null).newXAlignConfigFrame();
			}
		});
		menu.add(open);
		return menu;
	}

	JMenu buildWindowsMenu() {
		final JMenu windows = new JMenu("Windows");
		final JMenuItem tile = new JMenuItem("Tile");
		tile.setEnabled(true);
		tile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tileFrames();
			}
		});
		final JMenuItem cascade = new JMenuItem("Cascade");
		cascade.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cascadeFrames();
			}
		});
		final JMenuItem arrangeIcons = new JMenuItem("Arrange Icons");
		arrangeIcons.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				arrangeIcons();
			}
		});
		windows.add(tile);
		windows.add(cascade);
		windows.add(arrangeIcons);
		return windows;
	}

	JMenu buildInfoMenu() {
		final JMenu info = new JMenu("Info");
		final JMenuItem aboutUnitex = new JMenuItem("About Unitex...");
		aboutUnitex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null).newAboutUnitexFrame();
			}
		});
		final JMenuItem helpOnCommands = new JMenuItem("Help on commands...");
		helpOnCommands.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null).newHelpOnCommandFrame();
			}
		});
		final JMenuItem preferences = new JMenuItem("Preferences...");
		preferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null)
						.newGlobalPreferencesFrame();
			}
		});
		final JMenuItem console = new JMenuItem("Console");
		console.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null).showConsoleFrame();
			}
		});
		info.add(aboutUnitex);
		info.add(helpOnCommands);
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
		if (n == 1) {
			return;
		}
		UnitexFrame.closing = true;
		InternalFrameManager.getManager(null).closeAllFrames();
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
		Config.getCorpusDialogBox().setDialogType(JFileChooser.OPEN_DIALOG);
		final int returnVal = Config.getTaggedCorpusDialogBox().showOpenDialog(
				this);
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
					JOptionPane.showMessageDialog(null, "File "
							+ graphs[i].getAbsolutePath() + " does not exist",
							"Error", JOptionPane.ERROR_MESSAGE);
					continue;
				}
			}
			InternalFrameManager.getManager(null).newGraphFrame(graphs[i]);
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
		InternalFrameManager.getManager(null).newLexiconGrammarTableFrame(f);
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
		final GraphFrame currentFrame = InternalFrameManager.getManager(null)
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
					.enableLoopAndRecursionDetection(true).tokenizationMode(
							null, grf).repositories()
							.emitEmptyGraphWarning().displayGraphNames());
			commands.addCommand(new FlattenCommand().fst2(new File(name_fst2))
					.resultType(!rtn.isSelected()).depth(depthValue));
			Launcher.exec(commands, false);
		}
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
			public void toDo(boolean success) {
				InternalFrameManager.getManager(null).newDelaFrame(dela);
			}
		};
		if (null == Encoding.getEncoding(dela)) {
			InternalFrameManager.getManager(null).newTranscodeOneFileDialog(
					dela, toDo);
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
		InternalFrameManager.getManager(null).closeCurrentDelaFrame();
		Launcher.exec(command, true, new DelaDo(dela));
	}

	/**
	 * Compresses the current dictionary. The external program "Compress" is
	 * called through the creation of a <code>ProcessInfoFrame</code> object.
	 */
	void compressDELA() {
		CompressCommand command = new CompressCommand().name(Config
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

		public void toDo(boolean success) {
			InternalFrameManager.getManager(dela).newDelaFrame(dela);
		}
	}

	public static JInternalFrame getCurrentFocusedFrame() {
		return mainFrame.desktop.getSelectedFrame();
	}
}
