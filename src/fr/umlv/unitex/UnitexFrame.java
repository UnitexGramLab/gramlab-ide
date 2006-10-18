/*
 * Unitex
 *
 * Copyright (C) 2001-2006 Université de Marne-la-Vallée <unitex@univ-mlv.fr>
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

package fr.umlv.unitex;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import fr.umlv.unitex.conversion.*;
import fr.umlv.unitex.editor.*;
import fr.umlv.unitex.exceptions.*;
import fr.umlv.unitex.io.*;
import fr.umlv.unitex.process.*;

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
	JMenuBar menuBar;
	/**
	 * The desktop of the frame.
	 */
	public static JDesktopPane desktop;

	/**
	 * Layer used to display document internal frames
	 */
	public static final Integer DOCLAYER = new Integer(5);
	static final Integer TOOLLAYER = new Integer(6);
	static final Integer HELPLAYER = new Integer(7);
	/**
	 * The clipboard used to copy and paste text and graph box selections.
	 */
	public static Clipboard clip = new Clipboard("Unitex clipboard");
	/**
	 * The main frame of the system.
	 */
	public static UnitexFrame mainFrame;
	static Dimension screenSize;
	PrinterJob printerJob;
	PageFormat pageFormat;
	static boolean closing = false;

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
		buildContent();
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
	}

	public static void addInternalFrame(JInternalFrame frame) {
		desktop.add(frame, DOCLAYER);
	}

	/**
	 * Builds the menu bar.
	 */
	public void buildMenus() {
		menuBar = new JMenuBar();
		/* We remove the search panel in the menu bar, if any */
		menuBar.removeAll();
		
		menuBar.setOpaque(true);

		JMenu text = buildTextMenu();
		JMenu DELA = buildDELAMenu();
		JMenu fsGraph = buildFsGraphMenu();
		JMenu lexiconGrammar = buildLexiconGrammarMenu();
		JMenu edit = buildEditMenu();
		JMenu windows = buildWindowsMenu();
		JMenu info = buildInfoMenu();

		text.setMnemonic(KeyEvent.VK_T);
		DELA.setMnemonic(KeyEvent.VK_D);
		fsGraph.setMnemonic(KeyEvent.VK_G);
		lexiconGrammar.setMnemonic(KeyEvent.VK_L);
		edit.setMnemonic(KeyEvent.VK_E);
		windows.setMnemonic(KeyEvent.VK_W);
		info.setMnemonic(KeyEvent.VK_I);

		menuBar.add(text);
		menuBar.add(DELA);
		menuBar.add(fsGraph);
		menuBar.add(lexiconGrammar);
		menuBar.add(edit);

		FileEditionMenu fileEditionMenu = new FileEditionMenu();
		fileEditionMenu.setMnemonic(KeyEvent.VK_F);
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
	AbstractAction convertFst;
	AbstractAction closeText;
	AbstractAction quitUnitex;

	/**
	 * Creates the "Text" menu.
	 * 
	 * @return this menu.
	 */
	public JMenu buildTextMenu() {
		JMenu text = new JMenu("Text");
		//-------------------------------------------------------------------
		openText = new AbstractAction("Open...") {
			public void actionPerformed(ActionEvent e) {
				openText();
			}
		};
		openText.setEnabled(true);
		text.add(new JMenuItem(openText));
//		-------------------------------------------------------------------
		openTaggedText = new AbstractAction("Open Tagged Text...") {
			public void actionPerformed(ActionEvent e) {
				openTaggedText();
			}
		};
		openTaggedText.setEnabled(true);
		text.add(new JMenuItem(openTaggedText));
		//-------------------------------------------------------------------
		preprocessText = new AbstractAction("Preprocess Text...") {
			public void actionPerformed(ActionEvent e) {
				String txt = Config.getCurrentSnt().getAbsolutePath();
				txt = txt.substring(0, txt.length() - 3);
				txt = txt + "txt";
				new PreprocessFrame(new File(txt), Config.getCurrentSnt());
			}
		};
		preprocessText.setEnabled(false);
		text.add(new JMenuItem(preprocessText));
		//-------------------------------------------------------------------
		text.addSeparator();
		//-------------------------------------------------------------------
		changeLang = new AbstractAction("Change Language...") {
			public void actionPerformed(ActionEvent e) {
				Config.changeLanguage();
			}
		};
		changeLang.setEnabled(true);
		text.add(new JMenuItem(changeLang));
		//-------------------------------------------------------------------
		text.addSeparator();
		//-------------------------------------------------------------------
		applyLexicalResources = new AbstractAction("Apply Lexical Resources...") {
			public void actionPerformed(ActionEvent e) {
				ApplyLexicalResourcesFrame.showFrame();
			}
		};
		applyLexicalResources.setEnabled(false);
		text.add(new JMenuItem(applyLexicalResources));
		//-------------------------------------------------------------------
		text.addSeparator();
		//-------------------------------------------------------------------
		// TODO use listeners to know when actions must be selected or not
		locatePattern = new AbstractAction("Locate Pattern...") {
			public void actionPerformed(ActionEvent e) {
				LocateFrame.showFrame();
			}
		};
		JMenuItem loc = new JMenuItem(locatePattern);
		locatePattern.setEnabled(false);
		loc.setAccelerator(KeyStroke.getKeyStroke('L', Event.CTRL_MASK));
		text.add(loc);
		//-------------------------------------------------------------------
		displayLocatedSequences = new AbstractAction(
				"Display Located Sequences...") {
			public void actionPerformed(ActionEvent e) {
				ConcordanceParameterFrame.showFrame();
			}
		};
		displayLocatedSequences.setEnabled(false);
		text.add(new JMenuItem(displayLocatedSequences));
		//-------------------------------------------------------------------
		text.addSeparator();
		//-------------------------------------------------------------------
		elagComp = new AbstractAction("Compile Elag Grammars") {
			public void actionPerformed(ActionEvent e) {
				ElagCompFrame.showFrame();
			}
		};
		elagComp.setEnabled(true);
		text.add(new JMenuItem(elagComp));
		//-------------------------------------------------------------------
		text.addSeparator();
		//-------------------------------------------------------------------
		constructFst = new AbstractAction("Construct FST-Text...") {
			public void actionPerformed(ActionEvent e) {
				new ConstructFstFrame();
			}
		};
		constructFst.setEnabled(false);
		text.add(new JMenuItem(constructFst));
		//-------------------------------------------------------------------
		convertFst = new AbstractAction("Convert FST-Text to Text...") {
			public void actionPerformed(ActionEvent e) {
				ConvertFstFrame.showFrame();
			}
		};
		convertFst.setEnabled(false);
		text.add(new JMenuItem(convertFst));
        //-------------------------------------------------------------------
		text.addSeparator();
		//-------------------------------------------------------------------
		closeText = new AbstractAction("Close Text...") {
			public void actionPerformed(ActionEvent e) {
				closeText();
			}
		};
		closeText.setEnabled(false);
		text.add(new JMenuItem(closeText));
		//-------------------------------------------------------------------
		quitUnitex = new AbstractAction("Quit Unitex") {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		};
		text.add(new JMenuItem(quitUnitex));
		//-------------------------------------------------------------------
		return text;
	}

	AbstractAction checkDelaFormat;
	AbstractAction sortDictionary;
	AbstractAction inflect;
	AbstractAction inflectMorph;
	AbstractAction compressIntoFST;
	AbstractAction closeDela;
	AbstractAction mergeData;	// reunification of suffixs et roots


	/**
	 * Creates the "DELA" menu.
	 * 
	 * @return this menu.
	 */
	public JMenu buildDELAMenu() {
		JMenu DELA = new JMenu("DELA");
		//-------------------------------------------------------------------
		JMenuItem open2 = new JMenuItem("Open...");
		open2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openDELA();
			}
		});
		open2.setEnabled(true);
    DELA.add(open2);
		//-------------------------------------------------------------------
		DELA.addSeparator();
		//-------------------------------------------------------------------
		checkDelaFormat = new AbstractAction("Check Format...") {
			public void actionPerformed(ActionEvent e) {
				CheckDicFrame.showFrame();
			}
		};
		checkDelaFormat.setEnabled(false);
		JMenuItem chk = new JMenuItem(checkDelaFormat);
		chk.setAccelerator(KeyStroke.getKeyStroke('K', Event.CTRL_MASK));
		DELA.add(chk);
		//-------------------------------------------------------------------
		sortDictionary = new AbstractAction("Sort Dictionary") {
			public void actionPerformed(ActionEvent e) {
				sortDELA();
			}
		};
		sortDictionary.setEnabled(false);
    DELA.add(new JMenuItem(sortDictionary));
		//-------------------------------------------------------------------
		inflect = new AbstractAction("Inflect...") {
			public void actionPerformed(ActionEvent e) {
				InflectFrame.showFrame();
			}
		};
		inflect.setEnabled(false);
    DELA.add(new JMenuItem(inflect));

		//-------------------------------------------------------------------
		compressIntoFST = new AbstractAction("Compress into FST") {
			public void actionPerformed(ActionEvent e) {
				compressDELA();
			}
		};
		compressIntoFST.setEnabled(false);
	DELA.add(new JMenuItem(compressIntoFST));
		//-------------------------------------------------------------------
		DELA.addSeparator();
		//-------------------------------------------------------------------
		closeDela = new AbstractAction("Close") {
			public void actionPerformed(ActionEvent e) {
				closeDELA();
			}
		};
		closeDela.setEnabled(false);
		DELA.add(new JMenuItem(closeDela));
		//-------------------------------------------------------------------
		DELA.addSeparator();
    	//-------------------------------------------------------------------
		inflectMorph = new AbstractAction("Morph Var & Der...") {
		public void actionPerformed(ActionEvent e) {
			InflectMorphFrame.showFrame();
		}
	};
		inflectMorph.setEnabled(false);
		DELA.add(new JMenuItem(inflectMorph));
		//-------------------------------------------------------------------
		mergeData = new AbstractAction("Append Suffixes to Stems...") {
		    public void actionPerformed(ActionEvent e) {
		        MergeRacineSuffixes.showFrame();
		    }
		};
		mergeData.setEnabled(true);
		DELA.add(new JMenuItem(mergeData));

    	return DELA;
	}

	/**
	 * Creates the "FSGraph" menu.
	 * 
	 * @return this menu.
	 */
	public JMenu buildFsGraphMenu() {
		JMenu fsGraph = new JMenu("FSGraph");
		JMenuItem New = new JMenuItem("New");
		New.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNewGraphFrame();
			}
		});
		JMenuItem openGraph = new JMenuItem("Open...");
		openGraph.setAccelerator(KeyStroke.getKeyStroke('O', Event.CTRL_MASK));
		openGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openGraph();
			}
		});
		JMenuItem save = new JMenuItem("Save");
		save.setAccelerator(KeyStroke.getKeyStroke('S', Event.CTRL_MASK));
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f != null)
					saveGraph(f);
			}
		});
		JMenuItem saveAs = new JMenuItem("Save as...");
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f != null)
					saveAsGraph(f);
			}
		});
		JMenuItem saveAll = new JMenuItem("Save All");
		saveAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAllGraphs();
			}
		});
		JMenuItem setup = new JMenuItem("Page Setup");
		setup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pageSetup();
			}
		});
		JMenuItem print = new JMenuItem("Print...");
		print.setAccelerator(KeyStroke.getKeyStroke('P', Event.CTRL_MASK));
		print.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f != null) {
					printFrame(f);
				} else {
					if (TextAutomatonFrame.getFrame() != null
							&& TextAutomatonFrame.getFrame().isSelected()) {
						printTextAutomatonFrame(TextAutomatonFrame.getFrame());
					}
				}
			}
		});
		JMenuItem printAll = new JMenuItem("Print All...");
		printAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				printAllFrames();
			}
		});
		JMenu tools = new JMenu("Tools");
		JMenuItem sortNodeLabel = new JMenuItem("Sort Node Label");
		sortNodeLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.sortNodeLabel();
				}
			}
		});
		JMenuItem explorePaths = new JMenuItem("Explore graph paths");
		explorePaths.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f != null) {
					GraphPathFrame.showFrame();
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
				new GraphCollectionDialog();
			}
		});
		tools.add(sortNodeLabel);
		tools.add(explorePaths);
		tools.addSeparator();
		tools.add(compileFST);
		tools.add(flatten);
		tools.addSeparator();
		tools.add(graphCollection);
		JMenuItem CompileGraphe = new JMenuItem("Compile Morpheme graph...");
		CompileGraphe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compileGraphMorpheme();
			}
		});
		tools.add(CompileGraphe);

		
		JMenu format = new JMenu("Format");
		JMenuItem alignment = new JMenuItem("Alignment...");
		alignment.setAccelerator(KeyStroke.getKeyStroke('M', Event.CTRL_MASK));
		alignment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f != null) {
					new GraphAlignmentMenu(f.graphicalZone.isGrid,
							f.graphicalZone.nPixels);
				}
			}
		});
		JMenuItem antialiasing = new JMenuItem("Antialiasing...");
		antialiasing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.changeAntialiasingValue();
				} else {
					if (TextAutomatonFrame.getFrame() != null
							&& TextAutomatonFrame.getFrame().isSelected()) {
						TextAutomatonFrame.getFrame().changeAntialiasingValue();
					}
				}
			}
		});
		JMenuItem presentation = new JMenuItem("Presentation...");
		presentation.setAccelerator(KeyStroke
				.getKeyStroke('R', Event.CTRL_MASK));
		presentation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f != null) {
					new GraphPresentationMenu();
				}
			}
		});
		JMenuItem graphSize = new JMenuItem("Graph Size...");
		graphSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f != null) {
					new GraphSizeMenu();
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
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					double scale_x = (double) screenSize.width
							/ (double) f.graphicalZone.Width;
					double scale_y = (double) screenSize.height
							/ (double) f.graphicalZone.Height;
					if (scale_x < scale_y)
						f.setScaleFactor(scale_x);
					else
						f.setScaleFactor(scale_y);
				}
			}
		});
		fitInWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f != null) {
					Dimension d = f.scroll.getSize();
					double scale_x = (double) (d.width - 3)
							/ (double) f.graphicalZone.Width;
					double scale_y = (double) (d.height - 3)
							/ (double) f.graphicalZone.Height;
					if (scale_x < scale_y)
						f.setScaleFactor(scale_x);
					else
						f.setScaleFactor(scale_y);
					f.compListener = new ComponentAdapter() {
						public void componentResized(ComponentEvent e2) {
							Dimension d2 = f.scroll.getSize();
							double scale_x2 = (double) (d2.width - 3)
									/ (double) f.graphicalZone.Width;
							double scale_y2 = (double) (d2.height - 3)
									/ (double) f.graphicalZone.Height;
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
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(0.6);
				}
			}
		});
		fit80.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(0.8);
				}
			}
		});
		fit100.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(1.0);
				}
			}
		});
		fit120.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
				if (f != null) {
					f.removeComponentListener(f.compListener);
					f.setScaleFactor(1.2);
				}
			}
		});
		fit140.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
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
				closeAll();
			}
		});
		fsGraph.add(New);
		fsGraph.add(openGraph);
		fsGraph.addSeparator();
		fsGraph.add(save);
		fsGraph.add(saveAs);
		fsGraph.add(saveAll);
		fsGraph.addSeparator();
		fsGraph.add(setup);
		fsGraph.add(print);
		fsGraph.add(printAll);
		fsGraph.addSeparator();
		fsGraph.add(tools);
		fsGraph.add(format);
		fsGraph.add(zoom);
		fsGraph.addSeparator();
		fsGraph.add(closeAll);
		return fsGraph;
	}

	AbstractAction openLexiconGrammar;
	AbstractAction compileLexiconGrammar;
	AbstractAction closeLexiconGrammar;

	/**
	 * Creates the "Lexicon-Grammar" menu.
	 * 
	 * @return this menu.
	 */
	public JMenu buildLexiconGrammarMenu() {
		JMenu lexiconGrammar = new JMenu("Lexicon-Grammar");
    //-------------------------------------------------------------------
		openLexiconGrammar = new AbstractAction("Open...") {
			public void actionPerformed(ActionEvent e) {
				openLexiconGrammarTable();
			}
		};
		openLexiconGrammar.setEnabled(true);
    lexiconGrammar.add(new JMenuItem(openLexiconGrammar));
    //-------------------------------------------------------------------
		compileLexiconGrammar = new AbstractAction("Compile to GRF...") {
			public void actionPerformed(ActionEvent e) {
				LexiconGrammarFrame.showFrame();
			}
		};
		compileLexiconGrammar.setEnabled(false);
    lexiconGrammar.add(new JMenuItem(compileLexiconGrammar));
    //-------------------------------------------------------------------
		closeLexiconGrammar = new AbstractAction("Close") {
			public void actionPerformed(ActionEvent e) {
				compileLexiconGrammar.setEnabled(false);
				LexiconGrammarTableFrame.closeFrame();
			}
		};
		closeLexiconGrammar.setEnabled(false);
    lexiconGrammar.add(new JMenuItem(closeLexiconGrammar));
    //-------------------------------------------------------------------
		return lexiconGrammar;
	}

	/**
	 * Creates the "Edit" menu.
	 * 
	 * @return this menu.
	 */
	public JMenu buildEditMenu() {
		JMenu edit = new JMenu("Edit");
		JMenuItem cut = new JMenuItem("Cut");
		cut.setAccelerator(KeyStroke.getKeyStroke('X', Event.CTRL_MASK));
		cut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final ActionEvent E = e;
				if (TextAutomatonFrame.getFrame() != null
						&& TextAutomatonFrame.getFrame().isSelected()) {
					((FstTextField) TextAutomatonFrame.getFrame().graphicalZone.texte).cut
							.actionPerformed(E);
					TextAutomatonFrame.getFrame().graphicalZone.repaint();
				} else {
					GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
					if (f != null) {
						((TextField) (f.graphicalZone.texte)).cut
								.actionPerformed(E);
						f.graphicalZone.repaint();
					}
				}
			}
		});
		cut.setEnabled(true);
		JMenuItem copy = new JMenuItem("Copy");
		copy.setAccelerator(KeyStroke.getKeyStroke('C', Event.CTRL_MASK));
		copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final ActionEvent E = e;
				if (TextAutomatonFrame.getFrame() != null
						&& TextAutomatonFrame.getFrame().isSelected()) {
					((FstTextField) TextAutomatonFrame.getFrame().graphicalZone.texte).specialCopy
							.actionPerformed(E);
					TextAutomatonFrame.getFrame().graphicalZone.repaint();
				} else {
					GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
					if (f != null) {
						((TextField) (f.graphicalZone.texte)).specialCopy
								.actionPerformed(E);
						f.graphicalZone.repaint();
					}
				}
			}
		});
		copy.setEnabled(true);
		JMenuItem paste = new JMenuItem("Paste");
		paste.setAccelerator(KeyStroke.getKeyStroke('V', Event.CTRL_MASK));
		paste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final ActionEvent E = e;
				if (TextAutomatonFrame.getFrame() != null
						&& TextAutomatonFrame.getFrame().isSelected()) {
					((FstTextField) TextAutomatonFrame.getFrame().graphicalZone.texte).specialPaste
							.actionPerformed(E);
					TextAutomatonFrame.getFrame().graphicalZone.repaint();
				} else {
					GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
					if (f != null) {
						((TextField) (f.graphicalZone.texte)).specialPaste
								.actionPerformed(E);
						f.graphicalZone.repaint();
					}
				}
			}
		});
		paste.setEnabled(true);
		JMenuItem selectAll = new JMenuItem("Select All");
		selectAll.setAccelerator(KeyStroke.getKeyStroke('A', Event.CTRL_MASK));
		selectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (TextAutomatonFrame.getFrame() != null
						&& TextAutomatonFrame.getFrame().isSelected()) {
					TextAutomatonFrame.getFrame().graphicalZone
							.selectAllBoxes();
					TextAutomatonFrame.getFrame().graphicalZone.repaint();
				} else {
					GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
					if (f != null) {
						f.graphicalZone.selectAllBoxes();
						f.graphicalZone.repaint();
					}
				}
			}
		});
		selectAll.setEnabled(true);
		edit.add(cut);
		edit.add(copy);
		edit.add(paste);
		edit.add(selectAll);
		return edit;
	}

	/**
	 * Creates the "Windows" menu.
	 * 
	 * @return this menu.
	 */
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
		cascade.setEnabled(true);
		cascade.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cascadeFrames();
			}
		});
		JMenuItem arrangeIcons = new JMenuItem("Arrange Icons");
		arrangeIcons.setEnabled(true);
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

	/**
	 * Creates the "Info" menu.
	 * 
	 * @return this menu.
	 */
	public JMenu buildInfoMenu() {
		JMenu info = new JMenu("Info");
		JMenuItem aboutUnitex = new JMenuItem("About Unitex...");
		aboutUnitex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AboutUnitexFrame.showFrame();
			}
		});
		aboutUnitex.setEnabled(true);
		JMenuItem references = new JMenuItem("References");
		references.setEnabled(false);
		JMenu helpOnCommands = new JMenu("Help on commands");
		helpOnCommands = CommandMenuFactory.makeCommandMenu();
		helpOnCommands.setEnabled(true);
		JMenuItem preferences = new JMenuItem("Preferences...");
		preferences.setEnabled(true);
		preferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GlobalPreferenceFrame.showFrame();
			}
		});
		JMenuItem console = new JMenuItem("Console");
		console.setEnabled(true);
		console.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Console.changeFrameVisibility();
			}
		});
		info.add(aboutUnitex);
		info.add(helpOnCommands);
		info.addSeparator();
		info.add(preferences);
		info.add(console);
		return info;
	}

	void buildContent() {
		desktop = new JDesktopPane();
		setContentPane(desktop);
	}

	/**
	 * This method is called when the user tries to close the main window, or
	 * when he clicks on the "Quit Unitex" item in the "Text" menu.
	 *  
	 */
	public void quit() {
		Object[] options = {"Yes", "No"};
		int n = JOptionPane.showOptionDialog(this,
				"Do you really want to quit ?", "", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (n == 1) {
			return;
		}
		UnitexFrame.closing = true;
		closeAll();
		System.exit(0);
	}

	/**
	 * Creates and adds to the desktop a new <code>GraphFrame</code>.
	 *  
	 */
	public void createNewGraphFrame() {
		GraphFrame doc = new GraphFrame(false);
		desktop.add(doc, DOCLAYER);
		try {
			doc.setVisible(true);
			doc.setSelected(true);
			doc.setIcon(false);
		} catch (java.beans.PropertyVetoException e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * Launch the page setup for printing.
	 *  
	 */
	public void pageSetup() {
		if (printerJob == null) {
			printerJob = PrinterJob.getPrinterJob();
		}
		if (pageFormat == null) {
			pageFormat = printerJob.defaultPage();
		}
		pageFormat = printerJob.pageDialog(pageFormat);
	}

	/**
	 * Prints a <code>GraphFrame</code>.
	 * 
	 * @param g
	 *            the <code>GraphFrame</code> to be printed.
	 */
	public void printFrame(GraphFrame g) {
		if (printerJob == null) {
			printerJob = PrinterJob.getPrinterJob();
		}
		if (pageFormat == null) {
			pageFormat = printerJob.defaultPage();
		}
		printerJob.setPrintable(g.graphicalZone, pageFormat);
		if (printerJob.printDialog()) {
			try {
				printerJob.print();
			} catch (PrinterException e) {
				JOptionPane.showMessageDialog(null,
						"Error while printing graph", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Prints a <code>TextAutomatonFrame</code>.
	 * 
	 * @param g
	 *            the <code>TextAutomatonFrame</code> to be printed.
	 */
	public void printTextAutomatonFrame(TextAutomatonFrame g) {
		if (printerJob == null) {
			printerJob = PrinterJob.getPrinterJob();
		}
		if (pageFormat == null) {
			pageFormat = printerJob.defaultPage();
		}
		if (printerJob.printDialog()) {
			try {
				printerJob.setPrintable(g.graphicalZone, pageFormat);
				printerJob.print();
			} catch (PrinterException e) {
				JOptionPane.showMessageDialog(null,
						"Error while printing sentence graph", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Prints all the <code>GraphFrame</code> s that are on the desktop.
	 */
	public void printAllFrames() {
		JInternalFrame[] frames = desktop.getAllFrames();
		if (frames.length == 0)
			return;
		if (printerJob == null) {
			printerJob = PrinterJob.getPrinterJob();
		}
		if (pageFormat == null) {
			pageFormat = printerJob.defaultPage();
		}
		if (!printerJob.printDialog())
			return;
		for (int i = 0; i < frames.length; i++) {
			if (frames[i] instanceof GraphFrame) {
				printerJob.setPrintable(((GraphFrame) frames[i]).graphicalZone,
						pageFormat);
				try {
					printerJob.print();
				} catch (PrinterException e) {
					JOptionPane.showMessageDialog(null,
							"Error while printing graph", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
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
		preprocessText.setEnabled(true);
		applyLexicalResources.setEnabled(true);
		locatePattern.setEnabled(true);
		displayLocatedSequences.setEnabled(true);
		constructFst.setEnabled(true);
		convertFst.setEnabled(true);
		closeText.setEnabled(true);
		Text.loadCorpus(Config.getCorpusDialogBox().getSelectedFile());
	}

	/**
	 * Shows a dialog box to select a tagged corpus. If a corpus is selected, it is
	 * opened with a call to the <code>Text.loadCorpus(String)</code> method.
	 */
	public void openTaggedText() {
		Config.getCorpusDialogBox().setDialogType(JFileChooser.OPEN_DIALOG);
		int returnVal = Config.getCorpusDialogBox().showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		preprocessText.setEnabled(false);
		applyLexicalResources.setEnabled(true);
		locatePattern.setEnabled(true);
		displayLocatedSequences.setEnabled(true);
		constructFst.setEnabled(true);
		convertFst.setEnabled(true);
		closeText.setEnabled(true);
		Text.loadCorpus(Config.getCorpusDialogBox().getSelectedFile(),true);
	}

	/**
	 * Shows a dialog box to select on or more graphs. The selected graphs are
	 * opened with a call to the <code>loadGraph(String,String,String)</code>
	 * method.
	 */
	public void openGraph() {
		JFileChooser fc=Config.getGraphDialogBox(false);
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		ConvertOneFileFrame.reset();
		File[] graphs = fc.getSelectedFiles();
		for (int i = 0; i < graphs.length; i++) {
			String s = graphs[i].getAbsolutePath();
			if (!s.endsWith(".grf")) {
				s = s + ".grf";
				graphs[i] = new File(s);
			}
			Config.setCurrentGraphDir(graphs[i].getParentFile());
			loadGraph(graphs[i]);
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
		LexiconGrammarTableFrame.closeFrame();
		compileLexiconGrammar.setEnabled(true);
		closeLexiconGrammar.setEnabled(true);
		new LexiconGrammarTableFrame(f);
	}

	/**
	 * Tests if a graph is allready open on the desktop.
	 * 
	 * @param grf
	 *            the name of the graph
	 * @return the <code>GraphFrame</code> if the graph is allready open, or
	 *         <code>null</code> otherwise
	 */
	public GraphFrame graphIsAllreadyOpen(File grf) {
		JInternalFrame[] frames = desktop.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			if (frames[i] instanceof GraphFrame) {
				GraphFrame f = (GraphFrame) frames[i];
				if (grf.equals(f.getGraph())) {
					return f;
				}
			}
		}
		return null;
	}

	class LoadGraphDo extends ToDoAbstract {
		File file;

		LoadGraphDo(File s) {
			file = s;
		}

		public void toDo() {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					UnitexFrame.mainFrame.loadGraph(file);
				}
			});
		}

	}

	/**
	 * loads a graph. If the graph is allready open, its <code>GraphFrame</code>
	 * is focused, otherwise, a new <code>GraphFrame</code> is created, added
	 * to the desktop and focused.
	 * 
	 * @param grf
	 *            the complete name of the graph: path and file name
	 */
	public void loadGraph(File grf) {
		GraphFrame doc;
		if ((doc = graphIsAllreadyOpen(grf)) != null) {
			// we verify if the graph is allready opened
			// in this case, we bring it back to front
			try {
				doc.setVisible(true);
				doc.setSelected(true);
				doc.setIcon(false);
			} catch (java.beans.PropertyVetoException e2) {
				e2.printStackTrace();
			}
			return;
		}
		try {
			if (!UnicodeIO.isAUnicodeLittleEndianFile(grf)) {
				ConvertCommand res = ConvertOneFileFrame
						.getCommandLineForConversion(grf);
				if (res == null) {
					return;
				}
				new ProcessInfoFrame(res, true, new LoadGraphDo(grf));
				return;
			}
		} catch (FileNotFoundException e) {
			JOptionPane
					.showMessageDialog(null, "Cannot open "
							+ grf.getAbsolutePath(), "Error",
							JOptionPane.ERROR_MESSAGE);
			return;
		}
		GraphIO g = GraphIO.loadGraph(grf);
		if (g == null)
			return;
		doc = new GraphFrame(true);
		doc.graphicalZone.pref = g.pref;
		doc.graphicalZone.pref.antialiasing = Preferences.getCloneOfPreferences().antialiasing;
		doc.texte.setFont(doc.graphicalZone.pref.input);
		doc.graphicalZone.Width = g.width;
		doc.graphicalZone.Height = g.height;
		doc.graphicalZone.graphBoxes = g.boxes;
		doc.scroll.setPreferredSize(new Dimension(g.width, g.height));
		doc.graphicalZone.setPreferredSize(new Dimension(g.width, g.height));
		doc.setGraph(grf);
		//doc.setTitle(grf.getAbsolutePath());
		doc.setTitle(grf.getName()+" ("+grf.getParent()+")");
		UnitexFrame.addInternalFrame(doc);
		try {
			doc.setVisible(true);
			doc.setSelected(true);
			doc.setIcon(false);
		} catch (java.beans.PropertyVetoException e2) {
			e2.printStackTrace();
		}
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
		GraphIO g = new GraphIO();
		g.boxes = f.graphicalZone.graphBoxes;
		g.pref = f.graphicalZone.pref;
		g.width = f.graphicalZone.Width;
		g.height = f.graphicalZone.Height;
		JFileChooser fc=Config.getGraphDialogBox(true);
		fc.setMultiSelectionEnabled(false);
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		int returnVal = fc.showSaveDialog(this);
		fc.setMultiSelectionEnabled(true);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return false;
		}
		File file = fc.getSelectedFile();
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
		//f.setTitle(file.getAbsolutePath());
		f.setTitle(file.getName()+" ("+file.getParent()+")");
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
		GraphIO g = new GraphIO();
		g.boxes = f.graphicalZone.graphBoxes;
		g.pref = f.graphicalZone.pref;
		g.width = f.graphicalZone.Width;
		g.height = f.graphicalZone.Height;
		f.modified = false;
		g.saveGraph(file);
		//f.setTitle(file.getAbsolutePath());
		f.setTitle(file.getName()+" ("+file.getParent()+")");
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
	 * Closes all <code>GraphFrame</code> s that are on the desktop.
	 *  
	 */
	public void closeAll() {
		JInternalFrame[] frames = desktop.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			if (frames[i] instanceof GraphFrame) {
				((GraphFrame) frames[i]).doDefaultCloseAction();
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
		GraphFrame currentFrame = getCurrentFocusedGraphFrame();
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
		if(Config.isKorean()) {
			MultiCommands commands = new MultiCommands();
			File grf = new File(currentFrame.getGraph().getAbsolutePath());
			String name_fst_sans_ex= grf.getAbsolutePath().substring(0,
					grf.getAbsolutePath().length() - 4);
			
			Grf2Fst2Command command = new Grf2Fst2Command().grf(grf).enableLoopAndRecursionDetection(true).tokenizationMode().library();
			commands.addCommand(command);

			File map_encoder = new File(Config.getUserCurrentLanguageDir()
					,"jamoTable.txt");
			if(map_encoder.exists()){
				Syl2JamoCommand sy2jamoConv = new Syl2JamoCommand()
				.optionForIncludeJamo()
				.optionForMapJamo(map_encoder)
				.optionRemplace()
				.src(new File(name_fst_sans_ex + ".fst2"));
				commands.addCommand(sy2jamoConv);
			}

			new ProcessInfoFrame(commands, false);

		} else {
			Grf2Fst2Command command = new Grf2Fst2Command().grf(currentFrame.getGraph()).enableLoopAndRecursionDetection(true).tokenizationMode().library();
			new ProcessInfoFrame(command, false);
			
		}

	}


	/**
	 * Shows a window that offers the user to compile and flatten a graph. If
	 * the user clicks on the "OK" button, the compilation process is launched
	 * through the creation of a <code>ProcessInfoFrame</code> object.
	 *  
	 */
	public void compileAndFlattenGraph() {
		GraphFrame currentFrame = getCurrentFocusedGraphFrame();
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
		NumericTextField depth = new NumericTextField(4, String.valueOf(10));
		subpane.add(depth);
		pane.add(rtn);
		pane.add(fst);
		mainpane.add(pane, BorderLayout.CENTER);
		mainpane.add(subpane, BorderLayout.SOUTH);

		Object[] options = {"OK", "Cancel"};
		if (0 == JOptionPane.showOptionDialog(null, mainpane,
				"Compile & Flatten", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0])) {
			if (depth.getText().equals("") || depth.getText().equals("0")) {
				JOptionPane.showMessageDialog(null, "Invalid depth value",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String name_fst2 = grf.getAbsolutePath().substring(0,
					grf.getAbsolutePath().length() - 4);
			name_fst2 = name_fst2 + ".fst2";

			MultiCommands commands = new MultiCommands();
			commands.addCommand(new Grf2Fst2Command().grf(grf).enableLoopAndRecursionDetection(true).tokenizationMode().library());
			commands.addCommand(new FlattenCommand().fst2(new File(name_fst2))
					.resultType(!rtn.isSelected()).depth(depth.getText()));
			if(Config.isKorean()) {
				File map_encoder = new File(Config.getUserCurrentLanguageDir(),"jamoTable.txt");
				if(map_encoder.exists()){
					Syl2JamoCommand sy2jamoConv = new Syl2JamoCommand()
					.optionForIncludeJamo()
					.optionForMapJamo(map_encoder)
					.optionRemplace()
					.src(new File(name_fst2));
					
					commands.addCommand(sy2jamoConv);
			
				}
			}

			new ProcessInfoFrame(commands, false);
		}
	}
	/**
	 * Shows a window that offers the user to compile graphs with head graph. If
	 * the user clicks on the "OK" button, the compilation process is launched
	 * through the creation of a <code>ProcessInfoFrame</code> object.
	 *  
	 */
	JTextField destDirectory = new JTextField("");
	JLabel compileGrapheMorphemeOption = new JLabel("-c SS=0x318D");
	/**
	 * Gets a list of all ".bin" files found in a directory.
	 * 
	 * @param dir
	 *            the directory to be scanned
	 * @return a <code>Vector</code> containing file names.
	 */
	File getSufList(File dir,String extFilter) {
		File slf = new File(dir,"SufList.txt");
//		if (!dir.exists())			return slf;
		File files_list[] = dir.listFiles();
		try {
			slf.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(slf));
			for (int i = 0; i < files_list.length; i++) {
				String s = files_list[i].getName();
				if (!files_list[i].isDirectory()
						&& s.endsWith(extFilter)) {
					s = s + "\r\n";
					bw.write(s, 0, s.length());					
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return slf;
	}
	public void compileGraphMorpheme() {
		GraphFrame currentFrame = getCurrentFocusedGraphFrame();
		if (currentFrame == null)
			return;
		if (currentFrame.modified == true) {
			JOptionPane.showMessageDialog(null,
					"Save graph before compiling it", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		File grf = currentFrame.getGraph();
		
		String grf_sans_ext = new String(grf.getAbsolutePath()
				.substring(0,grf.getAbsolutePath().length() - 4));
		if (grf == null) {
			JOptionPane.showMessageDialog(null,
					"Cannot compile a graph with no name", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		mainFrame.destDirectory.setText(new File(Config.getUserCurrentLanguageDir()
				,"MorphemVariants").getAbsolutePath());
		mainFrame.destDirectory.setPreferredSize(new Dimension(240, 25));

		JPanel mainpane = new JPanel();
		JPanel upPanel = new JPanel();
		JPanel midPanel = new JPanel();		
		JPanel downPanel = new JPanel();
		
		mainpane.setLayout(new BorderLayout());
		upPanel.setLayout(new GridLayout(2, 1));
		midPanel.setLayout(new BorderLayout());
		downPanel.setLayout(new BorderLayout());
	
		upPanel.setBorder(new TitledBorder("Compile morpheme graphs: "));
		midPanel.setBorder(new TitledBorder("Break cycles of call to subgraphs:"));
		downPanel.setBorder(new TitledBorder("Save result in: "));		
		
		JPanel paneButtonSufStem = new JPanel();
		paneButtonSufStem.setBorder(new TitledBorder("Graph content:"));
		paneButtonSufStem.setLayout(new GridLayout(1, 3));		
		JRadioButton suf = new JRadioButton("suffixes");
		JRadioButton rac = new JRadioButton("stems");
		ButtonGroup bg = new ButtonGroup();
		bg.add(suf);
		bg.add(rac);
		suf.setSelected(true);		
		
		paneButtonSufStem.add(suf);
		paneButtonSufStem.add(rac);
		
		JPanel subpane = new JPanel();
		subpane.setBorder(new TitledBorder("Detect Cycles:"));
		subpane.setLayout(new FlowLayout(SwingConstants.HORIZONTAL));
		subpane.add(new JLabel("Stop after: "));
		NumericTextField depth = new NumericTextField(4, String.valueOf(10000));
		subpane.add(depth);
		
		upPanel.add(paneButtonSufStem);
		upPanel.add(subpane);

		downPanel.add(mainFrame.destDirectory, BorderLayout.CENTER);
		Action setDirectoryAction = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = Config.getInflectDialogBox().showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				mainFrame.destDirectory.setText(
						Config.getInflectDialogBox().getSelectedFile()
						.getAbsolutePath());
			}
		};
		JButton setDirectory = new JButton(setDirectoryAction);
		downPanel.add(setDirectory, BorderLayout.EAST);
		
		
		JPanel BreakCyclePanel = new JPanel();
		BreakCyclePanel.setBorder(new TitledBorder("Set break subgraphs:"));
		BreakCyclePanel.setLayout(new GridLayout(2,3));
		
		final String[] rowData = new String[] {""};
		rowData[0] = new String(grf.getName().substring(0,grf.getName().length() - 4));
		
		final JComboBox breaksubgraphListBox = new JComboBox(rowData);
//		breaksubgraphListBox.setPreferredSize(new Dimension(240, 25));
		
		breaksubgraphListBox.setEditable(true);
		Action AddComboListAction = new AbstractAction("Add...") {
			public void actionPerformed(ActionEvent arg0) {
				String getfi= JOptionPane.showInputDialog("");
				if(!getfi.equals(""))
					breaksubgraphListBox.addItem(getfi);
//JOptionPane.showInternalMessageDialog(UnitexFrame.desktop,""
	//			+breaksubgraphListBox.getModel().getSize());
			}
		};
		JButton addListItemButton = new JButton(AddComboListAction);
		addListItemButton.setPreferredSize(new Dimension(100, 25));
		Action RemoveComboListAction = new AbstractAction("Delete...") {
			public void actionPerformed(ActionEvent arg0) {
				if(breaksubgraphListBox.getModel().getSize() == 1) return;
				String getfi= JOptionPane.showInputDialog("");
				if(!getfi.equals(""))
					breaksubgraphListBox.removeItem(getfi);
				
			}
		};
		JButton removeListItemButton = new JButton(RemoveComboListAction);
		removeListItemButton.setPreferredSize(new Dimension(100, 25));
		
		JLabel saveListFileLabel = new JLabel("save :");	
		final JTextField saveListFileName = new JTextField("                  ");
		saveListFileName.setPreferredSize(new Dimension(100, 25));
		final JFileChooser breakListSaveDir = new JFileChooser();
		breakListSaveDir.addChoosableFileFilter(new PersonalFileFilter("txt",
				"*.*"));
		breakListSaveDir.setDialogType(JFileChooser.SAVE_DIALOG);
		breakListSaveDir.setCurrentDirectory(new File(Config
				.getUserCurrentLanguageDir(), "MorphemVariants"));
		breakListSaveDir.setMultiSelectionEnabled(false);
		
		Action setSaveListAction = new AbstractAction("Save at...") {
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = breakListSaveDir.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				File saveExact = new File(breakListSaveDir.getSelectedFile()
						.getAbsolutePath());
				
				try {
					if (!saveExact.exists()) {
						saveExact.createNewFile();
					}
					BufferedWriter bw = new BufferedWriter(new FileWriter(saveExact));
					for (int i = 0; i < breaksubgraphListBox.getModel().getSize(); i++) {
						String s = (String) (breaksubgraphListBox.getModel().getElementAt(i)) + "\n";
						bw.write(s, 0, s.length());
					}
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		};
		JButton setSaveListButton = new JButton(setSaveListAction);
				
		BreakCyclePanel.add(breaksubgraphListBox);
		BreakCyclePanel.add(addListItemButton);
		BreakCyclePanel.add(removeListItemButton);
		BreakCyclePanel.add(saveListFileLabel);
		BreakCyclePanel.add(saveListFileName);
		BreakCyclePanel.add(setSaveListButton);
//		BreakCyclePanel.add(saveListFilePanel);
		
		JPanel existListFilePanel = new JPanel();
		existListFilePanel.setBorder(
				new TitledBorder("Lookup list of break subgraphs"));
		
		JLabel existListFileLabel = new JLabel("Name of list file :");	
		final JTextField existListFileName = new JTextField("");
		existListFileName.setPreferredSize(new Dimension(240, 25));
		final JFileChooser existListFile = new JFileChooser();
		existListFile.addChoosableFileFilter(new PersonalFileFilter("txt",
				"Morphems subgraphs list"));
		existListFile.setDialogType(JFileChooser.OPEN_DIALOG);
		existListFile.setCurrentDirectory(new File(Config
				.getUserCurrentLanguageDir(), "Inflection"));
		existListFile.setMultiSelectionEnabled(false);
		
		Action getSavedListAction = new AbstractAction("Load from...") {
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = existListFile.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				existListFileName.setText(
						existListFile.getSelectedFile()
						.getAbsolutePath());
			}
		};
		JButton getSavedListButton = new JButton(getSavedListAction);
		
		existListFilePanel.add(existListFileLabel,BorderLayout.WEST);
		existListFilePanel.add(existListFileName,BorderLayout.CENTER);
		existListFilePanel.add(getSavedListButton,BorderLayout.EAST);
		
		midPanel.add(BreakCyclePanel,BorderLayout.CENTER);
		midPanel.add(existListFilePanel,BorderLayout.SOUTH);
		
		
		mainpane.add(upPanel,BorderLayout.NORTH);
		mainpane.add(midPanel,BorderLayout.CENTER);
		mainpane.add(downPanel,BorderLayout.SOUTH);

		Object[] options = {"OK", "Cancel"};
		if (0 == JOptionPane.showOptionDialog(null, mainpane,
				"Compile morpheme graph", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0])) {
			if (depth.getText().equals("") || depth.getText().equals("0")) {
				JOptionPane.showMessageDialog(null, "Invalid depth value",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String name_fst2 = new String(grf_sans_ext + ".fst2");
			String name_fst3 = new String(grf_sans_ext +  "jm.fst2");
			String name_fst4 =  new String(grf_sans_ext+ ".txt");

			MultiCommands commands = new MultiCommands();
			commands.addCommand(new Grf2Fst2Command()
					.grf(grf)// a Korean morpheme graph must be
					         // compiled with the default tokenization rules
					.enableLoopAndRecursionDetection(true)
					.library()
					);
			File map_encoder = new File(Config.getUserCurrentLanguageDir(),"jamoTable.txt");
			if(map_encoder.exists()){
				Syl2JamoCommand sy2jamoConv = new Syl2JamoCommand()
				.optionForIncludeJamo()
				.optionForMapJamo(map_encoder)
				.src(new File(name_fst2));
				commands.addCommand(sy2jamoConv);
			}
			Fst2ListCommand fst2l = new Fst2ListCommand();
			
			if(suf.isSelected()){
				fst2l.modeOfInital("m");
			} else {
				fst2l.modeOfInital("s");
				
			}
//			fst2l.element("-l");
			fst2l.limit(depth.getText());
			
			String optionfixed = new String("-c SS=0x318d -m -v -f a  -s0 \",,,\" -s \";\" -rl \"\\,,:\" -ss \"#\" ");
			StringTokenizer st = new StringTokenizer(optionfixed," ");
			while(st.hasMoreTokens())
				fst2l.uneOption(st.nextToken());
			
			if(breaksubgraphListBox.getModel().getSize() > 1){
			for (int i = 0; 
			i < breaksubgraphListBox.getModel().getSize(); i++) {
				fst2l.uneOption("-i");
				fst2l.uneOption((String)breaksubgraphListBox.getModel()
						.getElementAt(i));
			}
			}

			File rf = new File(name_fst4);
			File desFile = new File(mainFrame.destDirectory.getText(),rf.getName());
			
			fst2l.setOFile(desFile);
			//
			//	get default suffix list from the save dirctory
			//
			
			fst2l.ignoreListFile( getSufList(	new File(mainFrame.destDirectory.getText()),".sic"));
			if(!existListFileName.getText().equals("")){
				fst2l.ignoreListFile(new File(existListFileName.getText()) );
			}
			fst2l.fst2(new File(name_fst3));
			commands.addCommand(fst2l);
			
			SufForm2RacCommand formchange = new SufForm2RacCommand();
			File dirhanja = new File(Config.
					getUserCurrentLanguageDir(),"hanjajm.txt");
			if(dirhanja.exists())
				formchange.convTableList(dirhanja);

			String list_filen = desFile.getAbsolutePath().substring(0,
					desFile.getAbsolutePath().length() - 4);
			formchange.dest(new String(list_filen + 
					(suf.isSelected() ? ".sic":".ric")));
			
			if(suf.isSelected()){
				formchange.inputFilesList(new String(list_filen + "lst.txt"));	
			} else {
				formchange.inputFile(desFile);
			}
			commands.addCommand(formchange);
			
			new ProcessInfoFrame(commands, false);
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
		ToDoAbstract toDo = new ToDoAbstract() {
			public void toDo() {
				Config.setCurrentDELA(dela);
				if(Config.isAgglutinativeLanguage()){ // HUH insert
					inflectMorph.setEnabled(true);
				} else {				
				    checkDelaFormat.setEnabled(true);
				    sortDictionary.setEnabled(true);
				    inflect.setEnabled(true);
				    compressIntoFST.setEnabled(true);
				}
				closeDela.setEnabled(true);
				DelaFrame.loadDela(Config.getCurrentDELA());
			}
		};
		try {
			if (!UnicodeIO.isAUnicodeLittleEndianFile(dela)) {
				ConvertOneFileFrame.reset();
				ConvertCommand res = ConvertOneFileFrame
						.getCommandLineForConversion(dela);
				if (res == null) {
					return;
				}
				new ProcessInfoFrame(res, true, toDo);
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
		if (Config.getCurrentLanguage().equals("Thai")){
			command = command.thai();
		} else {
			command = command.sortAlphabet();
		}
		DelaFrame.closeDela();
		new ProcessInfoFrame(command, true, new DelaDo(Config.getCurrentDELA()));
	}

	/**
	 * Compresses the current dictionary. The external program "Compress" is
	 * called through the creation of a <code>ProcessInfoFrame</code> object.
	 *  
	 */
	public void compressDELA() {
		if ( Config.isAgglutinativeLanguage()) {
			CompressKrCommand commandKr = new CompressKrCommand().name(Config
					.getCurrentDELA());
			new ProcessInfoFrame(commandKr, false, null);
		} else {
			CompressCommand command = new CompressCommand().name(Config
					.getCurrentDELA());
			new ProcessInfoFrame(command, false, null);
		}
	}

	/**
	 * Closes the current dictionary.
	 *  
	 */
	public void closeDELA() {
		DelaFrame.closeDela();
		CheckResultFrame.close();
		Config.setCurrentDELA(null);
		checkDelaFormat.setEnabled(false);
		sortDictionary.setEnabled(false);
		inflect.setEnabled(false);
		compressIntoFST.setEnabled(false);
		inflectMorph.setEnabled(false);
		closeDela.setEnabled(false);
	}

	/**
	 * Closes the current corpus. All the associated frames (text tokens, text
	 * dictionaries, text automaton, etc) are closed.
	 *  
	 */
	public void closeText() {
		preprocessText.setEnabled(false);
		applyLexicalResources.setEnabled(false);
		locatePattern.setEnabled(false);
		displayLocatedSequences.setEnabled(false);
		constructFst.setEnabled(false);
		convertFst.setEnabled(false);
		closeText.setEnabled(false);
		Text.closeText();
		TokensFrame.hideFrame();
		if(Config.isAgglutinativeLanguage()){
			MorphemeFrame.hideFrame();			
		}
		closeAllConcordanceFrames();
		TextDicFrame.hideFrame();
		TextAutomatonFrame.hideFrame();
		repaint();
	}

	/**
	 * Closes all the <code>ConcordanceFrame</code> that are on the desktop.
	 *  
	 */
	public void closeAllConcordanceFrames() {
		Component[] f = desktop.getComponents();
		for (int i = 0; i < f.length; i++) {
			try {
				ConcordanceFrame F = (ConcordanceFrame) f[i];
				F.close();
			} catch (ClassCastException e) {
				// nothing to do
			}
		}
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
			for (int i = 0; i < f.length; i++) {
				try {
					JInternalFrame F = f[i];
					if (F.isVisible() && !F.isIcon()) {
						F.setBounds(0, 0, bounds.width, bounds.height);
					}
				} catch (ClassCastException e) {
					// nothing to do
				}
				return;
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

	class DelaDo extends ToDoAbstract {
		File dela;

		public DelaDo(File s) {
			dela = s;
		}
		public void toDo() {
			DelaFrame.loadDela(dela);
		}
	}

	public static JDesktopPane getDesktop() {
		return desktop;
	}

	public static JInternalFrame getCurrentFocusedFrame() {
		return desktop.getSelectedFrame();
	}

	public static GraphFrame getCurrentFocusedGraphFrame() {
		JInternalFrame frame = getCurrentFocusedFrame();
		if (frame instanceof GraphFrame)
			return (GraphFrame) frame;
		return null;
	}

	public static void removeInternalFrame(JInternalFrame f) {
		desktop.remove(f);
	}

}
