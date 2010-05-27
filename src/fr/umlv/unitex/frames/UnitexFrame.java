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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import fr.umlv.unitex.Config;
import fr.umlv.unitex.GraphPresentationInfo;
import fr.umlv.unitex.MyDropTarget;
import fr.umlv.unitex.Text;
import fr.umlv.unitex.TextField;
import fr.umlv.unitex.TfstTextField;
import fr.umlv.unitex.ToDo;
import fr.umlv.unitex.Version;
import fr.umlv.unitex.editor.FileEditionMenu;
import fr.umlv.unitex.exceptions.NotAUnicodeLittleEndianFileException;
import fr.umlv.unitex.io.GraphIO;
import fr.umlv.unitex.io.UnicodeIO;
import fr.umlv.unitex.listeners.DelaFrameListener;
import fr.umlv.unitex.listeners.LexiconGrammarTableFrameListener;
import fr.umlv.unitex.listeners.TextFrameListener;
import fr.umlv.unitex.print.PrintManager;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.commands.CompressCommand;
import fr.umlv.unitex.process.commands.FlattenCommand;
import fr.umlv.unitex.process.commands.Grf2Fst2Command;
import fr.umlv.unitex.process.commands.MultiCommands;
import fr.umlv.unitex.process.commands.SortTxtCommand;

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
	public DropTarget dropTarget = MyDropTarget.newDropTarget(this);
	/**
	 * The desktop of the frame.
	 */
	JDesktopPane desktop;
	InternalFrameManager frameManager;
	/**
	 * The clipboard used to copy and paste text and graph box selections.
	 */
	public static Clipboard clip = new Clipboard("Unitex clipboard");
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
	 * 
	 */
	public UnitexFrame() {
		super(Version.version);
		final int inset = 50;
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height
				- inset * 2);
		desktop = new JDesktopPane();
		setContentPane(desktop);
		frameManager = new InternalFrameManager(desktop);
		buildMenus();
		mainFrame = this;
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setTitle(Version.version + " - current language is "
				+ Config.getCurrentLanguageForTitleBar());
		frameManager.addTextFrameListener(new TextFrameListener() {
			public void textFrameOpened(boolean taggedText) {
				preprocessText.setEnabled(!taggedText);
				applyLexicalResources.setEnabled(true);
				locatePattern.setEnabled(true);
				displayLocatedSequences.setEnabled(true);
				constructFst.setEnabled(true);
				convertFst.setEnabled(true);
				closeText.setEnabled(true);
				frameManager.newTokensFrame(new File(Config.getCurrentSntDir(),
						"tok_by_freq.txt"));
				frameManager.newTextDicFrame(Config.getCurrentSntDir(), true);
				frameManager.newTextAutomatonFrame(1,true);
			}

			public void textFrameClosed() {
				preprocessText.setEnabled(false);
				applyLexicalResources.setEnabled(false);
				locatePattern.setEnabled(false);
				displayLocatedSequences.setEnabled(false);
				constructFst.setEnabled(false);
				convertFst.setEnabled(false);
				closeText.setEnabled(false);
				frameManager.closeTokensFrame();
				frameManager.closeConcordanceFrame();
				frameManager.closeTextDicFrame();
				frameManager.closeTextAutomatonFrame();
				frameManager.closeApplyLexicalResourcesFrame();
				frameManager.closeConcordanceDiffFrame();
				frameManager.closeConcordanceParameterFrame();
				frameManager.closeConstructTfstFrame();
				frameManager.closeConvertTfstToTextFrame();
				frameManager.closeLocateFrame();
				frameManager.closeStatisticsFrame();
			}
		});
		frameManager.addDelaFrameListener(new DelaFrameListener() {
			public void delaFrameOpened() {
				checkDelaFormat.setEnabled(true);
				sortDictionary.setEnabled(true);
				inflect.setEnabled(true);
				compressIntoFST.setEnabled(true);
				closeDela.setEnabled(true);
			}

			public void delaFrameClosed() {
				frameManager.closeCheckDicFrame();
				frameManager.closeCheckResultFrame();
				frameManager.closeInflectFrame();
				checkDelaFormat.setEnabled(false);
				sortDictionary.setEnabled(false);
				inflect.setEnabled(false);
				compressIntoFST.setEnabled(false);
				closeDela.setEnabled(false);
			}
		});
		/*
		 * TODO ajouter des listeners pour désactiver les options des graphes
		 * quand aucun graphe n'est ouvert
		 */
		frameManager
				.addLexiconGrammarTableFrameListener(new LexiconGrammarTableFrameListener() {
					public void lexiconGrammarTableFrameOpened() {
						compileLexiconGrammar.setEnabled(true);
						closeLexiconGrammar.setEnabled(true);
					}

					public void lexiconGrammarTableFrameClosed() {
						compileLexiconGrammar.setEnabled(false);
						closeLexiconGrammar.setEnabled(false);
					}
				});
	}

	/**
	 * Builds the menu bar.
	 */
	public void buildMenus() {
		JMenuBar menuBar = new JMenuBar();
		JMenu text = buildTextMenu();
		JMenu DELA = buildDELAMenu();
		JMenu fsGraph = buildFsGraphMenu();
		JMenu lexiconGrammar = buildLexiconGrammarMenu();
		JMenu xalign = buildXAlignMenu();
		JMenu edit = buildEditMenu();
		FileEditionMenu fileEditionMenu = new FileEditionMenu();
		JMenu windows = buildWindowsMenu();
		JMenu info = buildInfoMenu();
		text.setMnemonic(KeyEvent.VK_T);
		DELA.setMnemonic(KeyEvent.VK_D);
		fsGraph.setMnemonic(KeyEvent.VK_G);
		lexiconGrammar.setMnemonic(KeyEvent.VK_L);
		xalign.setMnemonic(KeyEvent.VK_X);
		edit.setMnemonic(KeyEvent.VK_E);
		fileEditionMenu.setMnemonic(KeyEvent.VK_F);
		windows.setMnemonic(KeyEvent.VK_W);
		info.setMnemonic(KeyEvent.VK_I);
		menuBar.add(text);
		menuBar.add(DELA);
		menuBar.add(fsGraph);
		menuBar.add(lexiconGrammar);
		menuBar.add(xalign);
		menuBar.add(edit);
		menuBar.add(fileEditionMenu);
		menuBar.add(windows);
		menuBar.add(info);
		setJMenuBar(menuBar);
	}

	Action openText;
	Action openTaggedText;
	public Action preprocessText;
	Action changeLang;
	public Action applyLexicalResources;
	public Action locatePattern;
	public AbstractAction displayLocatedSequences;
	AbstractAction elagComp;
	AbstractAction constructFst;
	AbstractAction convertFst;
	AbstractAction closeText;
	AbstractAction quitUnitex;

	public JMenu buildTextMenu() {
		JMenu textMenu = new JMenu("Text");
		openText = new AbstractAction("Open...") {
			public void actionPerformed(ActionEvent e) {
				openText();
			}
		};
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
				frameManager.newPreprocessDialog(new File(txt), Config
						.getCurrentSnt());
			}
		};
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
				frameManager.newApplyLexicalResourcesFrame();
			}
		};
		applyLexicalResources.setEnabled(false);
		textMenu.add(new JMenuItem(applyLexicalResources));
		textMenu.addSeparator();
		locatePattern = new AbstractAction("Locate Pattern...") {
			public void actionPerformed(ActionEvent e) {
				frameManager.newLocateFrame();
			}
		};
		locatePattern.setEnabled(false);
		locatePattern.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_L, Event.CTRL_MASK));
		textMenu.add(new JMenuItem(locatePattern));
		displayLocatedSequences = new AbstractAction("Located Sequences...") {
			public void actionPerformed(ActionEvent e) {
				frameManager.newConcordanceParameterFrame();
			}
		};
		displayLocatedSequences.setEnabled(false);
		textMenu.add(new JMenuItem(displayLocatedSequences));
		textMenu.addSeparator();
		elagComp = new AbstractAction("Compile Elag Grammars") {
			public void actionPerformed(ActionEvent e) {
				UnitexFrame.getFrameManager().newElagCompFrame();
			}
		};
		textMenu.add(new JMenuItem(elagComp));
		textMenu.addSeparator();
		constructFst = new AbstractAction("Construct FST-Text...") {
			public void actionPerformed(ActionEvent e) {
				frameManager.newConstructTfstFrame();
			}
		};
		constructFst.setEnabled(false);
		textMenu.add(new JMenuItem(constructFst));
		convertFst = new AbstractAction("Convert FST-Text to Text...") {
			public void actionPerformed(ActionEvent e) {
				UnitexFrame.getFrameManager().newConvertTfstToTextFrame();
			}
		};
		convertFst.setEnabled(false);
		textMenu.add(new JMenuItem(convertFst));
		textMenu.addSeparator();
		closeText = new AbstractAction("Close Text...") {
			public void actionPerformed(ActionEvent e) {
				frameManager.closeTextFrame();
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
	AbstractAction sortDictionary;
	AbstractAction inflect;
	AbstractAction compressIntoFST;
	AbstractAction closeDela;

	public JMenu buildDELAMenu() {
		JMenu delaMenu = new JMenu("DELA");
		JMenuItem open2 = new JMenuItem("Open...");
		open2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openDELA();
			}
		});
		delaMenu.add(open2);
		delaMenu.addSeparator();
		checkDelaFormat = new AbstractAction("Check Format...") {
			public void actionPerformed(ActionEvent e) {
				frameManager.newCheckDicFrame();
			}
		};
		checkDelaFormat.setEnabled(false);
		checkDelaFormat.putValue(Action.ACCELERATOR_KEY, KeyStroke
				.getKeyStroke(KeyEvent.VK_K, Event.CTRL_MASK));
		delaMenu.add(new JMenuItem(checkDelaFormat));
		sortDictionary = new AbstractAction("Sort Dictionary") {
			public void actionPerformed(ActionEvent e) {
				sortDELA();
			}
		};
		sortDictionary.setEnabled(false);
		delaMenu.add(new JMenuItem(sortDictionary));
		inflect = new AbstractAction("Inflect...") {
			public void actionPerformed(ActionEvent e) {
				frameManager.newInflectFrame();
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
		closeDela = new AbstractAction("Close") {
			public void actionPerformed(ActionEvent e) {
				frameManager.closeDelaFrame();
			}
		};
		closeDela.setEnabled(false);
		delaMenu.add(new JMenuItem(closeDela));
		return delaMenu;
	}

	public JMenu buildFsGraphMenu() {
		JMenu graphMenu = new JMenu("FSGraph");
		JMenuItem newGraph = new JMenuItem("New");
		newGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frameManager.newGraphFrame(null);
			}
		});
		graphMenu.add(newGraph);
		Action open = new AbstractAction("Open...") {
			public void actionPerformed(ActionEvent e) {
				openGraph();
			}
		};
		open.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_O, Event.CTRL_MASK));
		graphMenu.add(new JMenuItem(open));
		Action save = new AbstractAction("Save") {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = frameManager.getCurrentFocusedGraphFrame();
				if (f != null)
					saveGraph(f);
			}
		};
		save.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_S, Event.CTRL_MASK));
		graphMenu.add(new JMenuItem(save));
		Action saveAs = new AbstractAction("Save as...") {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = frameManager.getCurrentFocusedGraphFrame();
				if (f != null)
					saveAsGraph(f);
			}
		};
		graphMenu.add(new JMenuItem(saveAs));
		Action saveAll = new AbstractAction("Save All") {
			public void actionPerformed(ActionEvent e) {
				saveAllGraphs();
			}
		};
		graphMenu.add(new JMenuItem(saveAll));
		Action setup = new AbstractAction("Page Setup") {
			public void actionPerformed(ActionEvent e) {
				PrintManager.pageSetup();
			}
		};
		graphMenu.add(new JMenuItem(setup));
		Action print = new AbstractAction("Print...") {
			public void actionPerformed(ActionEvent e) {
				PrintManager.print(frameManager.getSelectedFrame());
			}
		};
		print.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('P',
				Event.CTRL_MASK));
		graphMenu.add(new JMenuItem(print));
		Action printAll = new AbstractAction("Print All...") {
			public void actionPerformed(ActionEvent e) {
				PrintManager.printAllGraphs(frameManager.getGraphFrames());
			}
		};
		graphMenu.add(new JMenuItem(printAll));
		graphMenu.addSeparator();
		JMenu tools = new JMenu("Tools");
		JMenuItem sortNodeLabel = new JMenuItem("Sort Node Label");
		sortNodeLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = frameManager.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.sortNodeLabel();
				}
			}
		});
		JMenuItem explorePaths = new JMenuItem("Explore graph paths");
		explorePaths.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = frameManager.getCurrentFocusedGraphFrame();
				if (f != null) {
					frameManager.newGraphPathDialog();
				}
			}
		});
		JMenuItem compileFST = new JMenuItem("Compile FST2");
		compileFST.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compileGraph();
			}
		});
		JMenuItem flatten = new JMenuItem("Compile & Flatten FST2");
		flatten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compileAndFlattenGraph();
			}
		});
		JMenuItem graphCollection = new JMenuItem("Build Graph Collection");
		graphCollection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* TODO ajouter un changeLanguageListener */
				frameManager.newGraphCollectionFrame();
			}
		});
		tools.add(sortNodeLabel);
		tools.add(explorePaths);
		tools.addSeparator();
		tools.add(compileFST);
		tools.add(flatten);
		tools.addSeparator();
		tools.add(graphCollection);
		graphMenu.add(tools);
		JMenu format = new JMenu("Format");
		JMenuItem alignment = new JMenuItem("Alignment...");
		alignment.setAccelerator(KeyStroke.getKeyStroke('M', Event.CTRL_MASK));
		alignment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = frameManager.getCurrentFocusedGraphFrame();
				if (f != null) {
					frameManager.newGraphAlignmentDialog(f);
				}
			}
		});
		JMenuItem antialiasing = new JMenuItem("Antialiasing...");
		antialiasing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JInternalFrame f = frameManager.getSelectedFrame();
				if (f == null)
					return;
				if (f instanceof GraphFrame) {
					GraphFrame f2 = (GraphFrame) f;
					f2.changeAntialiasingValue();
					return;
				}
				if (f instanceof TextAutomatonFrame) {
					TextAutomatonFrame f2 = (TextAutomatonFrame) f;
					f2.changeAntialiasingValue();
					return;
				}
			}
		});
		JMenuItem presentation = new JMenuItem("Presentation...");
		presentation.setAccelerator(KeyStroke
				.getKeyStroke('R', Event.CTRL_MASK));
		presentation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = frameManager.getCurrentFocusedGraphFrame();
				if (f != null) {
					GraphPresentationInfo info = frameManager
							.newGraphPresentationDialog(f
									.getGraphPresentationInfo(), true);
					if (info != null) {
						f.setGraphPresentationInfo(info);
					}
				}
			}
		});
		JMenuItem graphSize = new JMenuItem("Graph Size...");
		graphSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = frameManager.getCurrentFocusedGraphFrame();
				if (f != null) {
					frameManager.newGraphSizeDialog(f);
				}
			}
		});
		format.add(antialiasing);
		format.addSeparator();
		format.add(alignment);
		format.add(presentation);
		format.add(graphSize);
		JMenu zoom = new JMenu("Zoom");
		ButtonGroup groupe = new ButtonGroup();
		JRadioButtonMenuItem fitInScreen = new JRadioButtonMenuItem(
				"Fit in screen");
		JRadioButtonMenuItem fitInWindow = new JRadioButtonMenuItem(
				"Fit in window");
		JRadioButtonMenuItem fit60 = new JRadioButtonMenuItem("60%");
		JRadioButtonMenuItem fit80 = new JRadioButtonMenuItem("80%");
		JRadioButtonMenuItem fit100 = new JRadioButtonMenuItem("100%");
		JRadioButtonMenuItem fit120 = new JRadioButtonMenuItem("120%");
		JRadioButtonMenuItem fit140 = new JRadioButtonMenuItem("140%");
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
				GraphFrame f = frameManager.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					double scale_x = (double) screenSize.width
							/ (double) f.graphicalZone.getWidth();
					double scale_y = (double) screenSize.height
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
				final GraphFrame f = frameManager.getCurrentFocusedGraphFrame();
				if (f != null) {
					Dimension d = f.getScroll().getSize();
					double scale_x = (double) (d.width - 3)
							/ (double) f.graphicalZone.getWidth();
					double scale_y = (double) (d.height - 3)
							/ (double) f.graphicalZone.getHeight();
					if (scale_x < scale_y)
						f.setScaleFactor(scale_x);
					else
						f.setScaleFactor(scale_y);
					f.compListener = new ComponentAdapter() {
						public void componentResized(ComponentEvent e2) {
							Dimension d2 = f.getScroll().getSize();
							double scale_x2 = (double) (d2.width - 3)
									/ (double) f.graphicalZone.getWidth();
							double scale_y2 = (double) (d2.height - 3)
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
				GraphFrame f = frameManager.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(0.6);
				}
			}
		});
		fit80.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = frameManager.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(0.8);
				}
			}
		});
		fit100.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = frameManager.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(1.0);
				}
			}
		});
		fit120.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = frameManager.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(1.2);
				}
			}
		});
		fit140.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = frameManager.getCurrentFocusedGraphFrame();
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
		JMenuItem closeAll = new JMenuItem("Close all");
		closeAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frameManager.closeAllGraphFrames();
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

	public JMenu buildLexiconGrammarMenu() {
		JMenu lexiconGrammar = new JMenu("Lexicon-Grammar");
		openLexiconGrammar = new AbstractAction("Open...") {
			public void actionPerformed(ActionEvent e) {
				openLexiconGrammarTable();
			}
		};
		openLexiconGrammar.setEnabled(true);
		lexiconGrammar.add(new JMenuItem(openLexiconGrammar));

		compileLexiconGrammar = new AbstractAction("Compile to GRF...") {
			public void actionPerformed(ActionEvent e) {
				LexiconGrammarTableFrame f = frameManager
						.getLexiconGrammarTableFrame();
				if (f == null) {
					throw new IllegalStateException("Should not happen !");
				}
				frameManager.newConvertLexiconGrammarFrame(f.getTable());
			}
		};
		compileLexiconGrammar.setEnabled(false);
		lexiconGrammar.add(new JMenuItem(compileLexiconGrammar));

		closeLexiconGrammar = new AbstractAction("Close") {
			public void actionPerformed(ActionEvent e) {
				frameManager.closeLexiconGrammarTableFrame();
			}
		};
		closeLexiconGrammar.setEnabled(false);
		lexiconGrammar.add(new JMenuItem(closeLexiconGrammar));

		return lexiconGrammar;
	}


	private JMenu buildXAlignMenu() {
		JMenu menu = new JMenu("XAlign");
		JMenuItem open = new JMenuItem("Open files...");
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frameManager.newXAlignConfigFrame();
			}
		});
		menu.add(open);
		return menu;
	}


	public JMenu buildEditMenu() {
		JMenu edit = new JMenu("Edit");
		JMenuItem cut = new JMenuItem("Cut");
		cut.setAccelerator(KeyStroke.getKeyStroke('X', Event.CTRL_MASK));
		cut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final ActionEvent E = e;
				JInternalFrame f = frameManager.getSelectedFrame();
				if (f == null)
					return;
				if (f instanceof GraphFrame) {
					GraphFrame f2 = (GraphFrame) f;
					((TextField) (f2.getGraphicalZone().text)).getCut()
							.actionPerformed(E);
					f2.getGraphicalZone().repaint();
					return;
				}
				if (f instanceof TextAutomatonFrame) {
					TextAutomatonFrame f2 = (TextAutomatonFrame) f;
					((TfstTextField) f2.getGraphicalZone().text).getCut()
							.actionPerformed(E);
					f2.getGraphicalZone().repaint();
					return;
				}
			}
		});
		JMenuItem copy = new JMenuItem("Copy");
		copy.setAccelerator(KeyStroke.getKeyStroke('C', Event.CTRL_MASK));
		copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final ActionEvent E = e;
				JInternalFrame f = frameManager.getSelectedFrame();
				if (f == null)
					return;
				if (f instanceof GraphFrame) {
					GraphFrame f2 = (GraphFrame) f;
					((TextField) (f2.getGraphicalZone().text)).getSpecialCopy()
							.actionPerformed(E);
					f2.getGraphicalZone().repaint();
					return;
				}
				if (f instanceof TextAutomatonFrame) {
					TextAutomatonFrame f2 = (TextAutomatonFrame) f;
					((TfstTextField) f2.getGraphicalZone().text)
							.getSpecialCopy().actionPerformed(E);
					f2.getGraphicalZone().repaint();
					return;
				}
			}
		});
		JMenuItem paste = new JMenuItem("Paste");
		paste.setAccelerator(KeyStroke.getKeyStroke('V', Event.CTRL_MASK));
		paste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final ActionEvent E = e;
				JInternalFrame f = frameManager.getSelectedFrame();
				if (f == null)
					return;
				if (f instanceof GraphFrame) {
					GraphFrame f2 = (GraphFrame) f;
					((TextField) (f2.getGraphicalZone().text))
							.getSpecialPaste().actionPerformed(E);
					f2.getGraphicalZone().repaint();
					return;
				}
				if (f instanceof TextAutomatonFrame) {
					TextAutomatonFrame f2 = (TextAutomatonFrame) f;
					((TfstTextField) f2.getGraphicalZone().text)
							.getSpecialPaste().actionPerformed(E);
					f2.getGraphicalZone().repaint();
					return;
				}
			}
		});
		JMenuItem selectAll = new JMenuItem("Select All");
		selectAll.setAccelerator(KeyStroke.getKeyStroke('A', Event.CTRL_MASK));
		selectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JInternalFrame f = frameManager.getSelectedFrame();
				if (f == null)
					return;
				if (f instanceof GraphFrame) {
					GraphFrame f2 = (GraphFrame) f;
					f2.getGraphicalZone().selectAllBoxes();
					f2.getGraphicalZone().repaint();
					return;
				}
				if (f instanceof TextAutomatonFrame) {
					TextAutomatonFrame f2 = (TextAutomatonFrame) f;
					f2.getGraphicalZone().selectAllBoxes();
					f2.getGraphicalZone().repaint();
					return;
				}
			}
		});
		edit.add(cut);
		edit.add(copy);
		edit.add(paste);
		edit.add(selectAll);
		return edit;
	}


	public JMenu buildWindowsMenu() {
		JMenu windows = new JMenu("Windows");
		JMenuItem tile = new JMenuItem("Tile");
		tile.setEnabled(true);
		tile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tileFrames();
			}
		});
		JMenuItem cascade = new JMenuItem("Cascade");
		cascade.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cascadeFrames();
			}
		});
		JMenuItem arrangeIcons = new JMenuItem("Arrange Icons");
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


	public JMenu buildInfoMenu() {
		JMenu info = new JMenu("Info");
		JMenuItem aboutUnitex = new JMenuItem("About Unitex...");
		aboutUnitex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frameManager.newAboutUnitexFrame();
			}
		});

		JMenuItem helpOnCommands = new JMenuItem("Help on commands...");
		helpOnCommands.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frameManager.newHelpOnCommandFrame();
			}
		});

		JMenuItem preferences = new JMenuItem("Preferences...");
		preferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frameManager.newGlobalPreferencesFrame();
			}
		});
		
		JMenuItem console = new JMenuItem("Console");
		console.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frameManager.showConsoleFrame();
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
	 * 
	 */
	public void quit() {
		Object[] options = { "Yes", "No" };
		int n = JOptionPane.showOptionDialog(this,
				"Do you really want to quit ?", "", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (n == 1) {
			return;
		}
		UnitexFrame.closing = true;
		frameManager.closeAllFrames();
		System.exit(0);
	}


	/**
	 * Shows a dialog box to select a corpus. If a corpus is selected, it is
	 * opened with a call to the <code>Text.loadCorpus(String)</code> method.
	 */
	public void openText() {
		Config.getCorpusDialogBox().setDialogType(JFileChooser.OPEN_DIALOG);
		int returnVal = Config.getCorpusDialogBox().showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		Text.loadCorpus(Config.getCorpusDialogBox().getSelectedFile());
	}

	/**
	 * Shows a dialog box to select a tagged corpus. If a corpus is selected, it
	 * is opened with a call to the <code>Text.loadCorpus(String)</code> method.
	 */
	public void openTaggedText() {
		Config.getCorpusDialogBox().setDialogType(JFileChooser.OPEN_DIALOG);
		int returnVal = Config.getCorpusDialogBox().showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		Text.loadCorpus(Config.getCorpusDialogBox().getSelectedFile(), true);
	}

	/**
	 * Shows a dialog box to select on or more graphs. The selected graphs are
	 * opened with a call to the <code>loadGraph(String,String,String)</code>
	 * method.
	 */
	public void openGraph() {
		JFileChooser fc = Config.getGraphDialogBox(false);
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		File[] graphs = fc.getSelectedFiles();
		for (int i = 0; i < graphs.length; i++) {
			String s = graphs[i].getAbsolutePath();
			if (!s.endsWith(".grf")) {
				s = s + ".grf";
				graphs[i] = new File(s);
			}
			Config.setCurrentGraphDir(graphs[i].getParentFile());
			frameManager.newGraphFrame(graphs[i]);
		}
	}

	/**
	 * Shows a dialog box to select a lexicon-grammar table. If a table is
	 * selected, a <code>LexiconGrammarTableFrame</code> object is created.
	 */
	public void openLexiconGrammarTable() {
		int returnVal = Config.getTableDialogBox().showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		String name;
		try {
			name = Config.getTableDialogBox().getSelectedFile()
					.getCanonicalPath();
		} catch (IOException e) {
			return;
		}
		File f = new File(name);
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
		try {
			FileInputStream source = UnicodeIO
					.openUnicodeLittleEndianFileInputStream(f);
			source.close();
		} catch (NotAUnicodeLittleEndianFileException e) {
			JOptionPane.showMessageDialog(null, name
					+ " is not a Unicode Little-Endian lexicon grammar table",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		} catch (IOException e) {
			return;
		}
		frameManager.newLexiconGrammarTableFrame(f);
	}


	/**
	 * Opens a "Save As" dialog box to save a graph. The graph is actually saved
	 * by a call to the <code>GraphFrame.saveGraph(String)</code> method.
	 * 
	 * @param f
	 *            the <code>GraphFrame</code> to be saved
	 */
	public boolean saveAsGraph(GraphFrame f) {
		if (f == null)
			return false;
		GraphIO g = new GraphIO(f.graphicalZone);
		JFileChooser fc = Config.getGraphDialogBox(true);
		fc.setMultiSelectionEnabled(false);
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		File file = null;
		for (;;) {
			int returnVal = fc.showSaveDialog(this);
			fc.setMultiSelectionEnabled(true);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				// we return if the user has clicked on CANCEL
				return false;
			}
			file = fc.getSelectedFile();
			if (file == null || !file.exists())
				break;
			String message = file
					+ "\nalready exists. Do you want to replace it ?";
			Object[] options = { "Yes", "No" };
			int n = JOptionPane.showOptionDialog(null, message, "Error",
					JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null,
					options, options[0]);
			if (n == 0) {
				break;
			}
		}
		if (file == null) {
			return false;
		}
		String name = file.getAbsolutePath();
		// if the user wants to save the graph as an image
		if (name.endsWith(".png") || name.endsWith(".PNG")) {
			// we do not change the "modified" status and the title of the
			// frame
			f.saveGraphAsAnImage(file);
			return true;
		}
		// if the user wants to save the graph as a vectorial file
		if (name.endsWith(".svg") || name.endsWith(".SVG")) {
			// we do not change the "modified" status and the title of the
			// frame
			f.saveGraphAsAnSVG(file);
			return true;
		}
		if (!name.endsWith(".grf")) {
			file = new File(name + ".grf");
		}
		f.modified = false;
		g.saveGraph(file);
		f.setGraph(file);
		f.setTitle(file.getName() + " (" + file.getParent() + ")");
		return true;
	}

	/**
	 * If the graph has no name, the <code>saveAsGraph(GraphFrame)</code> is
	 * called. Otherwise, the graph is saved by a call to the
	 * <code>GraphFrame.saveGraph(String)</code> method.
	 * 
	 * @param f
	 *            the <code>GraphFrame</code> to be saved
	 */
	public boolean saveGraph(GraphFrame f) {
		if (f == null)
			return false;
		File file = f.getGraph();
		if (file == null) {
			return saveAsGraph(f);
		}
		GraphIO g = new GraphIO(f.graphicalZone);
		f.modified = false;
		g.saveGraph(file);
		f.setTitle(file.getName() + " (" + file.getParent() + ")");
		return true;
	}

	/**
	 * Saves all <code>GraphFrame</code> s that are on the desktop.
	 * 
	 */
	public void saveAllGraphs() {
		JInternalFrame[] frames = desktop.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			if (frames[i] instanceof GraphFrame) {
				saveGraph((GraphFrame) frames[i]);
			}
		}
	}

	/**
	 * Compiles the current focused <code>GraphFrame</code>. If the graph is
	 * unsaved, an error message is shown and nothing is done; otherwise the
	 * compilation process is launched through the creation of a
	 * <code>ProcessInfoFrame</code> object.
	 * 
	 */
	public void compileGraph() {
		GraphFrame currentFrame = frameManager.getCurrentFocusedGraphFrame();
		if (currentFrame == null)
			return;
		if (currentFrame.modified == true) {
			JOptionPane.showMessageDialog(null,
					"Save graph before compiling it", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (currentFrame.getGraph() == null) {
			JOptionPane.showMessageDialog(null,
					"Cannot compile a graph with no name", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		Grf2Fst2Command command = new Grf2Fst2Command().grf(
				currentFrame.getGraph()).enableLoopAndRecursionDetection(true)
				.tokenizationMode().library();
		Launcher.exec(command, false);
	}

	/**
	 * Shows a window that offers the user to compile and flatten a graph. If
	 * the user clicks on the "OK" button, the compilation process is launched
	 * through the creation of a <code>ProcessInfoFrame</code> object.
	 * 
	 */
	public void compileAndFlattenGraph() {
		GraphFrame currentFrame = frameManager.getCurrentFocusedGraphFrame();
		if (currentFrame == null)
			return;
		if (currentFrame.modified == true) {
			JOptionPane.showMessageDialog(null,
					"Save graph before compiling it", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		File grf = currentFrame.getGraph();
		if (grf == null) {
			JOptionPane.showMessageDialog(null,
					"Cannot compile a graph with no name", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		JPanel mainpane = new JPanel();
		mainpane.setLayout(new BorderLayout());
		JPanel pane = new JPanel();
		pane.setLayout(new GridLayout(2, 1));
		pane.setBorder(new TitledBorder("Expected result grammar format:"));
		JRadioButton rtn = new JRadioButton(
				"equivalent FST2 (subgraph calls may remain)");
		JRadioButton fst = new JRadioButton(
				"Finite State Transducer (can be just an approximation)");
		ButtonGroup bg = new ButtonGroup();
		bg.add(rtn);
		bg.add(fst);
		rtn.setSelected(true);
		JPanel subpane = new JPanel();
		subpane.setBorder(new TitledBorder("Flattening depth:"));
		subpane.setLayout(new FlowLayout(SwingConstants.HORIZONTAL));
		subpane.add(new JLabel("Maximum flattening depth: "));
		JTextField depth = new JTextField("10");
		subpane.add(depth);
		pane.add(rtn);
		pane.add(fst);
		mainpane.add(pane, BorderLayout.CENTER);
		mainpane.add(subpane, BorderLayout.SOUTH);
		Object[] options = { "OK", "Cancel" };
		if (0 == JOptionPane.showOptionDialog(null, mainpane,
				"Compile & Flatten", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0])) {
			int depthValue;
			try {
				depthValue = Integer.parseInt(depth.getText());
			} catch (NumberFormatException e) {
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
			MultiCommands commands = new MultiCommands();
			commands.addCommand(new Grf2Fst2Command().grf(grf)
					.enableLoopAndRecursionDetection(true).tokenizationMode()
					.library());
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
	public void openDELA() {
		Config.getDelaDialogBox().setDialogType(JFileChooser.OPEN_DIALOG);
		int returnVal = Config.getDelaDialogBox().showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		final File dela = Config.getDelaDialogBox().getSelectedFile();
		ToDo toDo = new ToDo() {
			public void toDo() {
				frameManager.newDelaFrame(dela);
			}
		};
		try {
			if (!UnicodeIO.isAUnicodeLittleEndianFile(dela)) {
				UnitexFrame.getFrameManager().newTranscodeOneFileDialog(dela,
						toDo);
			} else {
				toDo.toDo();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
	}

	/**
	 * Sorts the current dictionary. The external program "SortTxt" is called
	 * through the creation of a <code>ProcessInfoFrame</code> object.
	 * 
	 */
	public void sortDELA() {
		SortTxtCommand command = new SortTxtCommand().file(Config
				.getCurrentDELA());
		if (Config.getCurrentLanguage().equals("Thai")) {
			command = command.thai();
		} else {
			command = command.sortAlphabet(new File(Config
					.getUserCurrentLanguageDir(), "Alphabet_sort.txt"));
		}
		frameManager.closeDelaFrame();
		Launcher.exec(command, true, new DelaDo(Config.getCurrentDELA()));
	}

	/**
	 * Compresses the current dictionary. The external program "Compress" is
	 * called through the creation of a <code>ProcessInfoFrame</code> object.
	 * 
	 */
	public void compressDELA() {
		CompressCommand command = new CompressCommand().name(Config
				.getCurrentDELA());
		Launcher.exec(command, false, null);
	}

	/**
	 * Tiles all the frames that are on the desktop and that are not iconified.
	 * 
	 */
	public void tileFrames() {
		JInternalFrame[] f = desktop.getAllFrames();
		int openFrameCount = 0;
		for (int i = 0; i < f.length; i++) {
			if (f[i].isVisible() && !f[i].isIcon()) {
				openFrameCount++;
			}
		}
		if (openFrameCount == 0)
			return;
		Dimension bounds = getContentPane().getSize();
		if (openFrameCount == 1) {
			try {
				JInternalFrame F = f[0];
				if (F.isVisible() && !F.isIcon()) {
					F.setBounds(0, 0, bounds.width, bounds.height);
				}
			} catch (ClassCastException e) {
				// nothing to do
			}
		}
		if (openFrameCount == 2) {
			for (int i = 0; i < f.length; i++) {
				try {
					JInternalFrame F = f[i];
					if (F.isVisible() && !F.isIcon()) {
						if (openFrameCount == 2)
							F.setBounds(0, 0, bounds.width, bounds.height / 2);
						else
							F.setBounds(0, bounds.height / 2, bounds.width,
									bounds.height / 2);
					}
					openFrameCount--;
				} catch (ClassCastException e) {
					// nothing to do
				}
			}
			return;
		}
		if (openFrameCount == 3) {
			for (int i = 0; i < f.length; i++) {
				try {
					JInternalFrame F = f[i];
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
				} catch (ClassCastException e) {
					// nothing to do
				}
			}
			return;
		}
		int premiere_moitie = openFrameCount / 2;
		int seconde_moitie = openFrameCount - premiere_moitie;
		openFrameCount = 0;
		// drawing frames on the first half of screen
		int i;
		for (i = 0; i < f.length && openFrameCount < premiere_moitie; i++) {
			try {
				JInternalFrame F = f[i];
				if (F.isVisible() && !F.isIcon()) {
					F.setBounds(0, openFrameCount * bounds.height
							/ premiere_moitie, bounds.width / 2, bounds.height
							/ premiere_moitie);
				}
				openFrameCount++;
			} catch (ClassCastException e) {
				// nothing to do
			}
		}
		// drawing frames on the second half of screen
		openFrameCount = 0;
		for (; i < f.length && openFrameCount < seconde_moitie; i++) {
			try {
				JInternalFrame F = f[i];
				if (F.isVisible() && !F.isIcon()) {
					F.setBounds(bounds.width / 2, openFrameCount
							* bounds.height / seconde_moitie, bounds.width / 2,
							bounds.height / seconde_moitie);
				}
				openFrameCount++;
			} catch (ClassCastException e) {
				// nothing to do
			}
		}
	}

	/**
	 * Cascades all the frames that are on the desktop and that are not
	 * iconified.
	 * 
	 */
	public void cascadeFrames() {
		Component[] f = desktop.getComponents();
		int openFrameCount = 0;
		final int offset = 30;
		for (int i = 0; i < f.length; i++) {
			try {
				JInternalFrame F = (JInternalFrame) f[i];
				if (F.isVisible() && !F.isIcon()) {
					openFrameCount++;
					F.setBounds(offset * (openFrameCount % 6), offset
							* (openFrameCount % 6), 800, 600);
					try {
						F.setSelected(true);
					} catch (java.beans.PropertyVetoException e) {
						e.printStackTrace();
					}
				}
			} catch (ClassCastException e) {
				// nothing to do
			}
		}
	}

	/**
	 * Arranges all the iconified frames that are on the desktop.
	 * 
	 */
	public void arrangeIcons() {
		Component[] f = desktop.getComponents();
		int openFrameCount = 0;
		Dimension desktop_bounds = getContentPane().getSize();
		for (int i = 0; i < f.length; i++) {
			try {
				JInternalFrame.JDesktopIcon F = (JInternalFrame.JDesktopIcon) f[i];
				if (F.isVisible()) {
					Dimension icon_bounds = F.getSize();
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
			} catch (ClassCastException e) {
				// nothing to do
			}
		}
	}

	class DelaDo implements ToDo {
		File dela;

		public DelaDo(File s) {
			dela = s;
		}

		public void toDo() {
			frameManager.newDelaFrame(dela);
		}
	}

	public static JInternalFrame getCurrentFocusedFrame() {
		return mainFrame.desktop.getSelectedFrame();
	}

	public static InternalFrameManager getFrameManager() {
		return mainFrame.frameManager;
	}
}
