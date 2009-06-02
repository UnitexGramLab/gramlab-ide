/*
 * Unitex
 *
 * Copyright (C) 2001-2009 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import fr.umlv.unitex.console.Console;
import fr.umlv.unitex.exceptions.NotAUnicodeLittleEndianFileException;
import fr.umlv.unitex.io.GraphIO;
import fr.umlv.unitex.io.UnicodeIO;
import fr.umlv.unitex.process.ElagCommand;
import fr.umlv.unitex.process.ImplodeTfstCommand;
import fr.umlv.unitex.process.ProcessInfoFrame;
import fr.umlv.unitex.process.RebuildTfstCommand;
import fr.umlv.unitex.process.TagsetNormTfstCommand;
import fr.umlv.unitex.process.Tfst2GrfCommand;
import fr.umlv.unitex.tfst.Bounds;
import fr.umlv.unitex.tfst.BoundsEditor;
import fr.umlv.unitex.tfst.TokensInfo;


/**
 * This class describes a frame used to display sentence automata.
 * 
 * @author Sébastien Paumier
 *  
 */

public class TextAutomatonFrame extends JInternalFrame {



  public static TextAutomatonFrame frame = null;
  JTextArea text = new JTextArea();
  JLabel sentence_count_label = new JLabel(" 0 sentence");
  boolean elagON;
  private JSpinner spinner;
  static SpinnerNumberModel spinnerModel;
  TfstGraphicalZone elaggraph;
  File elagrules;
  JLabel ruleslabel;
  TfstGraphicalZone graphicalZone;
  TfstTextField textfield = new TfstTextField(25, this);
  boolean modified = false;
  static int sentence_count = 0;
  static File sentence_text;
  static File sentence_grf;
  static File sentence_tok;
  static File sentence_modified;
  static File text_tfst;
  static File elag_tfst;
  static File elagsentence_grf;
  static boolean isAcurrentLoadingThread = false;
  static boolean isAcurrentElagLoadingThread = false;
  static Process currentElagLoadingProcess = null;
  private JScrollPane scroll;
  private JSplitPane superpanel;
  private JButton RESET_SENTENCE_GRAPH;
  public BoundsEditor bounds=new BoundsEditor();
  

  private TextAutomatonFrame() {
    super("FST-Text", true, true, true, true);
    MyDropTarget.newDropTarget(this);
    setContentPane(constructPanel());
    pack();
    setBounds(150, 150, 850, 650);
    setVisible(false);
    addInternalFrameListener(new InternalFrameAdapter() {
      public void internalFrameClosing(InternalFrameEvent e) {
        try {
          setIcon(true);
        } catch (java.beans.PropertyVetoException e2) {
        	e2.printStackTrace();
        }
      }
    });
    textfield.setEditable(false);
    text.getCaret().setSelectionVisible(true);
    text.setSelectionColor(Color.GREEN);
    text.addCaretListener(new CaretListener() {
        public void caretUpdate(CaretEvent e) {
            String s=text.getSelectedText();
            if (s==null || s.equals("")) {
                bounds.setValue(null);
            } else {
                System.out.println("la selection va de "+text.getSelectionStart()+" a "+text.getSelectionEnd());
                bounds.setValue(new Bounds(text.getSelectionStart(),text.getSelectionEnd()-1));
            }
        }
    });
    text.addFocusListener(new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            /* The default behavior is to hide the selection when focus is lost */
            text.getCaret().setSelectionVisible(true);
        }
    });
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    closeElagFrame();
    GlobalPreferenceFrame.addTextFontListener(new FontListener() {
		public void fontChanged(Font font) {
			text.setFont(font);
		}});
  }
  
  
  private JPanel constructPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(constructUpPanel(), BorderLayout.NORTH);
    superpanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        constructTextPanel(), constructElagPanel());
    superpanel.setOneTouchExpandable(true);
    superpanel.setResizeWeight(0.5);
    superpanel.setDividerLocation(10000);
    panel.add(superpanel, BorderLayout.CENTER);
    return panel;
  }

  private JPanel constructTextPanel() {
    JPanel textframe = new JPanel(new BorderLayout());
    JPanel p = new JPanel(new GridLayout(3, 1));
    JButton button = new JButton("Explode");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        explodeTextAutomaton(text_tfst);
      }
    });
    p.add(button);

    button = new JButton("Implode");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        implodeTextAutomaton(text_tfst);
      }
    });
    p.add(button);

    button = new JButton("Apply Elag Rule");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        elagDialog();
      }
    });
    p.add(button);

    textframe.add(p, BorderLayout.WEST);

    JPanel downPanel = new JPanel(new BorderLayout());
    downPanel.setOpaque(true);

    graphicalZone = new TfstGraphicalZone(1188, 840, textfield, this, true);
    graphicalZone.setPreferredSize(new Dimension(1188, 840));

    scroll = new JScrollPane(graphicalZone);
    scroll.setOpaque(true);
    scroll.getHorizontalScrollBar().setUnitIncrement(20);
    scroll.getVerticalScrollBar().setUnitIncrement(20);
    scroll.setPreferredSize(new Dimension(1188, 840));

    textfield.setFont(Preferences.getCloneOfPreferences().input);

    downPanel.add(textfield, BorderLayout.NORTH);
    downPanel.add(bounds, BorderLayout.EAST);
    downPanel.add(scroll, BorderLayout.CENTER);

    textframe.add(downPanel, BorderLayout.CENTER);

    return textframe;
  }

  private JPanel constructElagPanel() {
    JPanel elagframe = new JPanel(new BorderLayout());
    elagframe.setMinimumSize(new Dimension(0, 0));
    JPanel p = new JPanel(new GridLayout(3, 1));
    JButton button = new JButton("Explode");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        exploseElagFst();
      }
    });
    p.add(button);

    button = new JButton("Implode");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        imploseElagFst();
      }
    });
    p.add(button);

    button = new JButton("Replace");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        replaceElagFst();
      }
    });
    p.add(button);

    elagframe.add(p, BorderLayout.WEST);
    elaggraph = new TfstGraphicalZone(1188, 840, textfield, this, false);
    elaggraph.setPreferredSize(new Dimension(1188, 840));
    elagframe.add(new JScrollPane(elaggraph), BorderLayout.CENTER);
    return elagframe;
  }

  private JPanel constructUpPanel() {
    JPanel upPanel = new JPanel(new BorderLayout());
    text.setFont(Preferences.getCloneOfPreferences().textFont);
    text.setEditable(false);
    text.setText("");
    text.setLineWrap(true);
    text.setWrapStyleWord(true);
    JScrollPane textScroll = new JScrollPane(text);
    textScroll.setOpaque(true);
    textScroll.setPreferredSize(new Dimension(600, 100));
    textScroll
      .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    JPanel tmp = new JPanel(new BorderLayout());
    tmp.setOpaque(true);
    tmp.setBorder(new EmptyBorder(2, 2, 2, 2));
    tmp.add(textScroll, BorderLayout.CENTER);
    upPanel.add(tmp, BorderLayout.CENTER);
    upPanel.add(constructCornerPanel(), BorderLayout.WEST);
    return upPanel;
  }

  private JPanel constructCornerPanel() {
    JPanel cornerPanel = new JPanel(new GridLayout(5, 1));
    cornerPanel.setBorder(BorderFactory.createRaisedBevelBorder());
    cornerPanel.add(sentence_count_label);
    JPanel middle = new JPanel(new BorderLayout());
    middle.add(new JLabel(" Sentence # "), BorderLayout.WEST);
    JPanel right = new JPanel(new BorderLayout());
    spinnerModel = new SpinnerNumberModel(1, 1, 1, 1);
    spinnerModel.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent arg0) {
        loadSentence(spinnerModel.getNumber().intValue());
      }
    });
    spinner = new JSpinner(spinnerModel);
    spinner.setPreferredSize(new Dimension(50, 20));
    right.add(spinner);
    middle.add(right, BorderLayout.EAST);
    cornerPanel.add(middle);
    Action resetSentenceAction = new AbstractAction("Reset Sentence Graph") {
      public void actionPerformed(ActionEvent arg0) {
        File f2 = new File(sentence_modified.getAbsolutePath()+spinnerModel.getNumber().intValue() + ".grf");
        if (f2.exists())
          f2.delete();
        loadSentence(spinnerModel.getNumber().intValue());
      }
    };
    RESET_SENTENCE_GRAPH = new JButton(resetSentenceAction);
    RESET_SENTENCE_GRAPH.setVisible(false);
    cornerPanel.add(RESET_SENTENCE_GRAPH);
    Action rebuildAction = new AbstractAction("Rebuild FST-Text") {
      public void actionPerformed(ActionEvent arg0) {
        // post pone code
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            TextAutomatonFrame.hideFrame();
            RebuildTfstCommand command = new RebuildTfstCommand()
          .automaton(new File(Config.getCurrentSntDir(),"text.tfst"));
        new ProcessInfoFrame(command, true,
          new RebuildTextAutomatonDo());
          }
        });
      }
    };
    JButton REBUILD_TEXT_AUTOMATON = new JButton(rebuildAction);
    cornerPanel.add(REBUILD_TEXT_AUTOMATON);
    final JButton elagButton = new JButton("Elag Frame");
    elagButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        toggleElagFrame();
        if (elagON) {
          elagButton.setText("Close elag frame");
        } else {
          elagButton.setText("Open  Elag Frame");
        }
      }
    });
    cornerPanel.add(elagButton);
    return cornerPanel;
  }

  /**
   * Initializes the frame
   *  
   */

  public static void init() {
    frame = new TextAutomatonFrame();
    UnitexFrame.addInternalFrame(frame,false);
  }

  /**
   * 
   * @return the frame
   */
  public static TextAutomatonFrame getFrame() {
    return frame;
  }

  /**
   * Shows the frame
   *  
   */

  public static void showFrame() {

	/*if(Config.isAgglutinativeLanguage()){
		text_tfst = new File(Config.getCurrentSntDir(),"phrase.cod");
		
	} else*/ {
		text_tfst = new File(Config.getCurrentSntDir(),"text.tfst");
	}

    if (!text_tfst.exists()) { // if there is no text FST, we do nothing
        return;
    }

    
    sentence_text = new File(Config.getCurrentSntDir(),"cursentence.txt");
    sentence_grf = new File(Config.getCurrentSntDir(),"cursentence.grf");
    sentence_tok = new File(Config.getCurrentSntDir(),"cursentence.tok");
    sentence_modified = new File(Config.getCurrentSntDir(),"sentence");

    elag_tfst = new File(Config.getCurrentSntDir(),"text-elag.tfst");
    elagsentence_grf = new File(Config.getCurrentSntDir(),"currelagsentence.grf");

    if (frame == null) {
      init();
    }

    /*if(Config.isAgglutinativeLanguage()){
		int b[] = {0,0,0,0};
		try {
			FileInputStream raf = new FileInputStream(text_tfst);
			for(int i = 0;i<4 ;i++){
				// we ignore the 4 first bytes
				raf.read();
			}
			sentence_count = 0;
			for(int i = 0;i<4;i++){
				b[i] = raf.read();
			}
			sentence_count = b[0]+256*b[1]+b[2]*256*256 +b[3]*256*256*256;
			raf.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	} else */{
		sentence_count = readSentenceCount(text_tfst);
	}

    String s = " " + sentence_count;
    s = s + " sentence";

    if (sentence_count > 1)
      s = s + "s";

    frame.sentence_count_label.setText(s);
    spinnerModel.setMaximum(new Integer(sentence_count));
    spinnerModel.setValue(new Integer(1));
    /* Comment because spinnerModel.setValue does the job */ 
    //loadSentence(1);
    frame.setVisible(true);

    try {
      frame.setIcon(true);
    } catch (java.beans.PropertyVetoException e2) {
    	e2.printStackTrace();
    }
  }

  /**
   * Hides the frame
   *  
   */

  public static void hideFrame() {
    if (frame == null)
      return;
    frame.setVisible(false);
    frame.graphicalZone.is_initialised = false;
    frame.graphicalZone.graphBoxes.clear();
    frame.graphicalZone.repaint();
    frame.text.setText("");
    spinnerModel.setValue(new Integer(0));
    try {
      frame.setIcon(false);
    } catch (java.beans.PropertyVetoException e2) {
    	e2.printStackTrace();
    }
    System.gc();
  }

  class SpecialNumericTextDocument extends PlainDocument {
    public void insertString(int offs, String s, AttributeSet a)
      throws BadLocationException {
      int i;
      if (s == null)
        return;
      char c[] = s.toCharArray();
      for (i = 0; i < c.length; i++) {
        if ((c[i] != 10) && (c[i] < '0' || c[i] > '9'))
          return;
      }
      super.insertString(offs, s, a);
    }
  }

  /**
   * Indicates if the graph has been modified
   * 
   * @param b
   *            <code>true</code> if the graph has been modified,
   *            <code>false</code> otherwise
   */
  public void setModified(boolean b) {
    RESET_SENTENCE_GRAPH.setVisible(b);
    if (b) {
      // we save each modification
      GraphIO g = new GraphIO();
      g.boxes = frame.graphicalZone.graphBoxes;
      g.pref = frame.graphicalZone.pref;
      g.width = frame.graphicalZone.Width;
      g.height = frame.graphicalZone.Height;
      g.saveSentenceGraph(new File(sentence_modified.getAbsolutePath()
            + spinnerModel.getNumber().intValue() + ".grf"));
    }
  }

  private static int readSentenceCount(File f) {
    String s = "0";
    try {
      FileInputStream br = UnicodeIO
        .openUnicodeLittleEndianFileInputStream(f);
      s = UnicodeIO.readLine(br);
      if (s == null || s.equals("")) {
        return 0;
      }
      br.close();
    } catch (NotAUnicodeLittleEndianFileException e) {
    	e.printStackTrace();
    } catch (IOException e) {
    	e.printStackTrace();
    }

    return new Integer(s).intValue();
  }

  static void loadSentenceFromConcordance(int n) {
    if (frame == null || frame.isVisible() == false
        || frame.isIcon() == true)
      return;
    if (n < 1 || n > sentence_count)
      return;
    if (loadSentence(n))
      spinnerModel.setValue(new Integer(n));
  }

  public boolean loadCurrSentence() {
    return loadSentence(spinnerModel.getNumber().intValue());
  }

  /**
   * Loads a sentence automaton
   * 
   * @param n
   *            sentence number
   * @return <code>false</code> if a sentence is allready being loaded,
   *         <code>true</code> otherwise
   */

  public static boolean loadSentence(int n) {

    if (n < 1 || n > sentence_count)
      return false;
    final int z = n;
    if (isAcurrentLoadingThread)
      return false;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new Thread() {
		  Tfst2GrfCommand cmd;
          public void run() {
            isAcurrentLoadingThread = true;
            frame.graphicalZone.is_initialised = false;
            if (frame.graphicalZone.graphBoxes != null) {
              frame.graphicalZone.graphBoxes.clear();
            }
            frame.graphicalZone.repaint();
            frame.text.setText("");
            /*if(Config.isKorean()){
				cmdkr = new Txt2Fst2KrCommand();
				cmdkr.getsentence(z,text_tfst);
				Console.addCommand(cmdkr.getCommandLine());

			} else*/ {
				cmd = new Tfst2GrfCommand().automaton(
						text_tfst).sentence(z);
				Console.addCommand(cmd.getCommandLine());
			}
			Process p;

            try {

			 /*if(Config.isAgglutinativeLanguage()){
					p= Runtime.getRuntime().exec(
						cmdkr.getCommandArguments());
			 } else */{
				p= Runtime.getRuntime().exec(
						cmd.getCommandArguments());
 			 }

              BufferedInputStream in = new BufferedInputStream(p
                .getInputStream());
              BufferedInputStream err = new BufferedInputStream(p
                .getErrorStream());

              (new EatStreamThread(in)).start();
              (new EatStreamThread(err)).start();

              /* waitFor Fst2Grf to terminate */

              p.waitFor();

            } catch (Exception e) {

              System.err.println("Exception: " + e);
              e.printStackTrace();
              isAcurrentLoadingThread = false;
              return;
            }
			/*if(Config.isKorean()) {
				// in Korean, we must apply a conversion Jamo -> Syllabic
				Process p0,p1;
				Jamo2SylCommand getSylSentence = new Jamo2SylCommand()
				.decodage(new File(Config.getCurrentSntDir(),"sentence.fst2"));
										
				Console.addCommand(getSylSentence.getCommandLine());
				try {
					p0= Runtime.getRuntime().exec(
							getSylSentence.getCommandArguments());
					
					BufferedInputStream in = new BufferedInputStream(p0
							.getInputStream());
					BufferedInputStream err = new BufferedInputStream(p0
							.getErrorStream());

					(new EatStreamThread(in)).start();
					(new EatStreamThread(err)).start();

					// waitFor Fst2Grf to terminate 

					p0.waitFor();
					

				} catch (Exception e) {

					System.err.println("Exception: " + e);
					e.printStackTrace();
					isAcurrentLoadingThread = false;
					return;
				}
				
				Tfst2GrfCommand cmdGetUnGraphe = 
			        new Tfst2GrfCommand().automaton(
				        new File(Config.getCurrentSntDir(),
				                "sentencesyl.fst2")
						).sentence(1).font("Gulim");
//				CommandGen cmdfst2grf = new CommandGen("fst2grfkr");
//				cmdfst2grf.dirs(new File(Config.getCurrentSntDir(),"sentencesyl.fst2"));
//				String numofsentence = "1";
//				cmdfst2grf.element(numofsentence);								
				Console.addCommand(cmdGetUnGraphe.getCommandLine());
				try {
					p1= Runtime.getRuntime().exec(
					        cmdGetUnGraphe.getCommandArguments());
					
					BufferedInputStream in = new BufferedInputStream(p1
							.getInputStream());
					BufferedInputStream err = new BufferedInputStream(p1
							.getErrorStream());

					(new EatStreamThread(in)).start();
					(new EatStreamThread(err)).start();

					// waitFor Fst2Grf to terminate

					p1.waitFor();
					

				} catch (Exception e) {

					System.err.println("Exception: " + e);
					e.printStackTrace();
					isAcurrentLoadingThread = false;
					return;
				}
			}*/

            readSentenceText();
            try {
                TokensInfo.loadTokensInfo(sentence_tok);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            File f = new File(sentence_modified + String.valueOf(z)
                + ".grf");
            boolean modified = f.exists();

            if (modified) {
              loadSentenceGraph(new File(sentence_modified.getAbsolutePath()
                    + String.valueOf(z) + ".grf"));
              frame.setModified(modified);
            } else
              loadSentenceGraph(sentence_grf);
            isAcurrentLoadingThread = false;
          }
        }.start();
      }
    });

    loadElagSentence(n);

    return true;
  }

  public static boolean loadElagSentence(int n) {

    if (n < 1 || n > sentence_count) {
      System.err.println("loadElagSentence: n = " + n + " out of bounds");
      return false;
    }

    final int z = n;

    if (isAcurrentElagLoadingThread) {
      System.err
        .println("loadElagSentence: isAcurrentElagLoading Thread=true");
      return false;
    }

    SwingUtilities.invokeLater(new Thread() {
      public void run() {

        isAcurrentElagLoadingThread = true;

        frame.elaggraph.is_initialised = false;
        frame.elaggraph.graphBoxes.clear();
        frame.elaggraph.repaint();

        if (!elag_tfst.exists()) { // if fst file does not exist exit
          isAcurrentElagLoadingThread = false;
          return;
        }
        Tfst2GrfCommand cmd=new Tfst2GrfCommand().automaton(elag_tfst)
      .sentence(z)
      .output("currelagsentence");

    /*String command = '"' + Config.getApplicationDir() + "Fst2Grf"
      + '"' + ' ' + '"' + elag_fst.getAbsolutePath() + '"'
      + ' ' + z + " currelagsentence";*/

    Console.addCommand(cmd.getCommandLine());

    /*String[] cmd = new String[4];
      cmd[0] = Config.getApplicationDir() + "Fst2Grf";
      cmd[1] = elag_fst.getAbsolutePath();
      cmd[2] = String.valueOf(z);
      cmd[3] = "currelagsentence";
      */
    try {
      Process p = Runtime.getRuntime().exec(cmd.getCommandArguments());
      BufferedInputStream in = new BufferedInputStream(p.getInputStream());
      BufferedInputStream err = new BufferedInputStream(p.getErrorStream());
      (new EatStreamThread(in)).start();
      (new EatStreamThread(err)).start();

      p.waitFor();

    } catch (Exception e) {

      System.err.println("Exception: " + e);
      e.printStackTrace();
      isAcurrentElagLoadingThread = false;
      return;
    }

    try {
        loadElagSentenceGraph(elagsentence_grf);
    } finally {
        isAcurrentElagLoadingThread = false;
    }
    }
    });

    return true;
  }

  /**
   * Inverts the antialiasing flag
   *  
   */
  public void changeAntialiasingValue() {
    graphicalZone.pref.antialiasing = !graphicalZone.pref.antialiasing;
    graphicalZone.repaint();
  }

  static void readSentenceText() {
    String s = "";
    try {
      FileInputStream br = UnicodeIO
        .openUnicodeLittleEndianFileInputStream(sentence_text);
      s = UnicodeIO.readLine(br);
      if (s == null || s.equals("")) {
        return;
      }
      frame.text.setFont(Config.getCurrentTextFont());
      frame.text.setText(s);
      br.close();
    } catch (NotAUnicodeLittleEndianFileException e) {
    	e.printStackTrace();
    } catch (IOException e) {
    	e.printStackTrace();
    }
  }

  static void loadSentenceGraph(File file) {
    frame.setModified(false);
    GraphIO g = GraphIO.loadSentenceGraph(file);
    if (g == null) {
      //System.err.println("Cannot load " + file.getAbsolutePath());
      return;
    }
    frame.graphicalZone.is_initialised = false;
    frame.textfield.setFont(frame.graphicalZone.pref.input);
    frame.graphicalZone.pref = Preferences.getCloneOfPreferences().getClone();
    frame.graphicalZone.Width = g.width;
    frame.graphicalZone.Height = g.height;
    frame.graphicalZone.graphBoxes = g.boxes;
    frame.scroll.setPreferredSize(new Dimension(g.width, g.height));
    frame.graphicalZone.setPreferredSize(new Dimension(g.width, g.height));
    frame.graphicalZone.revalidate();
    frame.graphicalZone.repaint();
  }

  static void loadElagSentenceGraph(File file) {
    frame.setModified(false);
    GraphIO g = GraphIO.loadSentenceGraph(file);
    if (g == null)
      return;
    frame.elaggraph.is_initialised = false;
    frame.elaggraph.pref = Preferences.getCloneOfPreferences();
    frame.elaggraph.Width = g.width;
    frame.elaggraph.Height = g.height;
    frame.elaggraph.graphBoxes = g.boxes;
    frame.elaggraph.setPreferredSize(new Dimension(g.width, g.height));
    frame.elaggraph.revalidate();
    frame.elaggraph.repaint();
  }

  void openElagFrame() {
    superpanel.setDividerLocation(0.5);
    superpanel.setResizeWeight(0.5);
    elagON = true;
  }

  void closeElagFrame() {
    superpanel.setDividerLocation(1000);
    superpanel.setResizeWeight(1.0);
    elagON = false;
  }

  void toggleElagFrame() {
    if (elagON) {
      closeElagFrame();
    } else {
      openElagFrame();
    }
  }

  void elagDialog() {

    JLabel titlelabel = new JLabel();
    titlelabel.setText("Elag Rule:");

    elagrules = new File(Config.getCurrentElagDir(),"elag.rul");

    ruleslabel = new JLabel(elagrules.getName());
    ruleslabel.setBorder(new LineBorder(Color.black, 1, true));

    JButton button = new JButton("browse");

    button.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {

        JFileChooser fc = new JFileChooser();

        fc.setCurrentDirectory(elagrules.getParentFile());

        fc.setFileFilter(new PersonalFileFilter("rul", "Elag rules file ( .rul)"));
        fc.setAcceptAllFileFilterUsed(false);

        fc.setDialogTitle("Choose Elag Rule File");
        fc.setDialogType(JFileChooser.OPEN_DIALOG);

        if ((fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) || (fc.getSelectedFile() == null)) {
          return;
        }

        elagrules = fc.getSelectedFile();
        ruleslabel.setText(elagrules.getName());
      }
    });

    JCheckBox imploseCheckBox = new JCheckBox("Implose resulting text automaton", true);
  
    BorderLayout layout = new BorderLayout();
    layout.setVgap(10);
    layout.setHgap(10);

    JPanel p = new JPanel();
    p.setLayout(layout);


    p.add(titlelabel, BorderLayout.WEST);
    p.add(ruleslabel, BorderLayout.CENTER);
    p.add(button, BorderLayout.EAST);

    p.add(imploseCheckBox, BorderLayout.SOUTH);

    if (JOptionPane.showInternalConfirmDialog(UnitexFrame.desktop, p,
          "Apply Elag Rule", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
      return;
    }

    ElagCommand elagcmd = new ElagCommand()
      .lang(new File(Config.getCurrentElagDir(),"tagset.def"))
      .dir(elagrules.getParentFile())
      .rules(elagrules)
      .output(elag_tfst)
      .automaton(text_tfst);


    if (imploseCheckBox.isSelected()) {
      new ProcessInfoFrame(elagcmd, false, new ImploseDO(elag_tfst));
    } else {
      new ProcessInfoFrame(elagcmd, false, new loadSentenceDO());
    }
  }

  void replaceElagFst() {
    if (!elag_tfst.exists()) {
      JOptionPane.showInternalMessageDialog(UnitexFrame.desktop,
          "replaceElagFst: file '" + elag_tfst + "' doesn't exists");
      return;
    }
    /* cleanup files */
    File dir = Config.getCurrentSntDir();
    Config.deleteFileByName(new File(Config.getCurrentSntDir(),"sentence*.grf"));
    File f = new File(dir, "currelagsentence.grf");
    if (f.exists() && !f.delete()) {
      JOptionPane.showInternalMessageDialog(UnitexFrame.desktop,
          "failed to delete " + f);
    }
    f = new File(dir, "currelagsentence.txt");
    if (f.exists() && !f.delete()) {
      JOptionPane.showInternalMessageDialog(UnitexFrame.desktop,
          "failed to delete " + f);
    }
    if (text_tfst.exists() && !text_tfst.delete()) {
      JOptionPane.showInternalMessageDialog(UnitexFrame.desktop,
          "failed to delete " + text_tfst);
    }
    if (!elag_tfst.renameTo(text_tfst)) {
      System.err.println("unable to replace: " + elag_tfst + " -> "
          + text_tfst);
      JOptionPane.showInternalMessageDialog(UnitexFrame.desktop,
          "failed to replace " + text_tfst + " with " + elag_tfst);
    }
    loadCurrSentence();
  }

  void exploseElagFst() {
    explodeTextAutomaton(elag_tfst);
  }

  void imploseElagFst() {
    implodeTextAutomaton(elag_tfst);
  }

  static void explodeTextAutomaton(File f) {

    if (!f.exists()) {
      return;
    }
    TagsetNormTfstCommand tagsetcmd=new TagsetNormTfstCommand()
    	.tagset(new File(Config.getCurrentElagDir(), "tagset.def"))
    	.automaton(f);
    new ProcessInfoFrame(tagsetcmd,true,new loadSentenceDO());
  }

  static void implodeTextAutomaton(File f) {
    if (!f.exists()) {
      return;
    }
    ImplodeTfstCommand imploseCmd = new ImplodeTfstCommand().automaton(f);
    new ProcessInfoFrame(imploseCmd,true,new loadSentenceDO());
  }


  /* Normalize the main text automaton according to tagset description in tagset.def
   * if implode is true, then implode the resulting automaton
   */
  
  static void normalizeFst(boolean implode) {

    TagsetNormTfstCommand tagsetcmd = new TagsetNormTfstCommand()
      .tagset(new File(Config.getCurrentElagDir(), "tagset.def"))
      .automaton(text_tfst);

    if (implode) {
      new ProcessInfoFrame(tagsetcmd, false, new ImploseDO(text_tfst));
    } else {
      new ProcessInfoFrame(tagsetcmd, false, new loadSentenceDO());
    }
  }

  class RebuildTextAutomatonDo extends ToDoAbstract {

    public void toDo() {
      Config
        .deleteFileByName(new File(Config.getCurrentSntDir(),"sentence*.grf"));
      File dir = Config.getCurrentSntDir();
      File f = new File(dir, "currelagsentence.grf");
      if (f.exists() && !f.delete()) {
        JOptionPane.showInternalMessageDialog(UnitexFrame.desktop,
            "unable to delete " + f);
      }
      f = new File(dir, "currelagsentence.txt");
      if (f.exists() && !f.delete()) {
        JOptionPane.showInternalMessageDialog(UnitexFrame.desktop,
            "unable to delete " + f);
      }
      f = new File(dir, "text-elag.fst2");
      if (f.exists() && !f.delete()) {
        JOptionPane.showInternalMessageDialog(UnitexFrame.desktop,
            "unable to delete " + f);
      }
      TextAutomatonFrame.showFrame();
      try {
        TextAutomatonFrame.getFrame().setIcon(false);
        TextAutomatonFrame.getFrame().setSelected(true);
      } catch (java.beans.PropertyVetoException e) {
    	  e.printStackTrace();
      }
    }
  }

}

class loadSentenceDO extends ToDoAbstract {

  public void toDo() {
    TextAutomatonFrame.frame.loadCurrSentence();
  }
}

class ImploseDO extends ToDoAbstract {
  
  File fst;
  
  public ImploseDO(File f) { fst = f; }
  
  public void toDo() { TextAutomatonFrame.implodeTextAutomaton(fst); }
}
