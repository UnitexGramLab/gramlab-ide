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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableCellRenderer;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.MyDropTarget;
import fr.umlv.unitex.PersonalFileFilter;
import fr.umlv.unitex.Preferences;
import fr.umlv.unitex.console.Console;
import fr.umlv.unitex.exceptions.NotAUnicodeLittleEndianFileException;
import fr.umlv.unitex.graphrendering.TfstGraphicalZone;
import fr.umlv.unitex.graphrendering.TfstTextField;
import fr.umlv.unitex.io.GraphIO;
import fr.umlv.unitex.io.UnicodeIO;
import fr.umlv.unitex.listeners.FontListener;
import fr.umlv.unitex.listeners.GraphListener;
import fr.umlv.unitex.process.EatStreamThread;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.Log;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.ElagCommand;
import fr.umlv.unitex.process.commands.ImplodeTfstCommand;
import fr.umlv.unitex.process.commands.RebuildTfstCommand;
import fr.umlv.unitex.process.commands.TagsetNormTfstCommand;
import fr.umlv.unitex.process.commands.Tfst2GrfCommand;
import fr.umlv.unitex.tfst.TagFilter;
import fr.umlv.unitex.tfst.TfstTableModel;
import fr.umlv.unitex.tfst.TokensInfo;

/**
 * This class describes a frame used to display sentence automata.
 *
 * @author Sébastien Paumier
 */
public class TextAutomatonFrame extends JInternalFrame {

	TagFilter filter=new TagFilter();
	TfstTableModel tfstTableModel=new TfstTableModel(filter);
    JTextArea sentenceTextArea = new JTextArea();
    JLabel sentence_count_label = new JLabel(" 0 sentence");
    boolean elagON;
    JSpinner spinner;
    SpinnerNumberModel spinnerModel;
    TfstGraphicalZone elaggraph;
    File elagrules;
    JLabel ruleslabel;
    TfstGraphicalZone graphicalZone;

    GraphListener listener = new GraphListener() {
        public void graphChanged(boolean m) {
            if (m) setModified(true);
            repaint();
        }
    };

    public TfstGraphicalZone getGraphicalZone() {
        return graphicalZone;
    }

    TfstTextField textfield = new TfstTextField(25, this);
    boolean modified = false;
    int sentence_count = 0;
    File sentence_text;
    File sentence_grf;
    File sentence_tok;
    File sentence_modified;
    File text_tfst;
    File elag_tfst;
    File elagsentence_grf;
    boolean isAcurrentLoadingThread = false;
    boolean isAcurrentElagLoadingThread = false;
    Process currentElagLoadingProcess = null;
    private JSplitPane superpanel;
    private JButton resetSentenceGraph;

    TextAutomatonFrame() {
        super("FST-Text", true, true, true, true);
        MyDropTarget.newDropTarget(this);
        setContentPane(constructPanel());
        pack();
        setBounds(10, 10, 850, 600);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                try {
                    setIcon(true);
                } catch (java.beans.PropertyVetoException e2) {
                    e2.printStackTrace();
                }
            }
        });
        textfield.setEditable(false);
        closeElagFrame();
        Preferences.addTextFontListener(new FontListener() {
            public void fontChanged(Font font) {
                sentenceTextArea.setFont(font);
            }
        });
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
        graphicalZone = new TfstGraphicalZone(null, textfield, this, true);
        graphicalZone.addGraphListener(listener);
        graphicalZone.setPreferredSize(new Dimension(1188, 840));
        final JScrollPane scroll = new JScrollPane(graphicalZone);
        scroll.getHorizontalScrollBar().setUnitIncrement(20);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setPreferredSize(new Dimension(1188, 840));
        textfield.setFont(Preferences.getCloneOfPreferences().info.input.font);
        downPanel.add(textfield, BorderLayout.NORTH);
        downPanel.add(scroll, BorderLayout.CENTER);
        JTabbedPane tabbed=new JTabbedPane();
		downPanel.add(scroll, BorderLayout.CENTER);
		tabbed.addTab("Automaton",downPanel);
		tabbed.addTab("Table",createTablePanel()); 
		textframe.add(tabbed, BorderLayout.CENTER);
        return textframe;
    }

	private JPanel createTablePanel() {
		JPanel p=new JPanel(new BorderLayout());
		final JTable table=new JTable(tfstTableModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setFont(Preferences.textFont());
		Preferences.addTextFontListener(new FontListener() {
			public void fontChanged(Font font) {
				table.setFont(font);
				refreshTableColumnWidths(table);
				refreshTableRowHeight(table);
			}
		});
		table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
			public void columnSelectionChanged(ListSelectionEvent e) {
				/* nop */
			}
			
			public void columnRemoved(TableColumnModelEvent e) {
				refreshTableColumnWidths(table);
			}
			
			public void columnMoved(TableColumnModelEvent e) {
				/* nop */
			}
			
			public void columnMarginChanged(ChangeEvent e) {
				/* nop */
			}
			
			public void columnAdded(TableColumnModelEvent e) {
				refreshTableColumnWidths(table);
			}
		});
		refreshTableColumnWidths(table);
		refreshTableRowHeight(table);
		p.add(new JScrollPane(table));
		JPanel filterPanel=new JPanel(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();
		filterPanel.setBorder(BorderFactory.createTitledBorder("Filter grammatical/semantic codes"));
		final JCheckBox alwaysShowGramCode=new JCheckBox("Always show POS category, regardless filtering");
		final JRadioButton all=new JRadioButton("All",true);
		final JRadioButton onlyShowGramCode=new JRadioButton("Only POS category");
		final JRadioButton usePattern=new JRadioButton("Use filter: ");
		JButton export=new JButton("Export all text as POS list");
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportTextAsTable();
			}
		});
		ButtonGroup group=new ButtonGroup();
		group.add(all);
		group.add(onlyShowGramCode);
		group.add(usePattern);
		final JFormattedTextField filterField=new JFormattedTextField(new JFormattedTextField.AbstractFormatter() {
			@Override
			public String valueToString(Object value) throws ParseException {
				try {
					Pattern p2=(Pattern)value;
					if (p2==null) return null;
					String s=p2.toString();
					return s.substring(2,s.length()-2);
				} catch (ClassCastException  e) {
					throw new ParseException("",0);
				}
			}
			
			@Override
			public Object stringToValue(String text) throws ParseException {
				try {
					if (text.equals("")) return null;
					return Pattern.compile(".*"+text+".*");
				} catch (PatternSyntaxException e) {
					throw new ParseException("",0);
				}
			}
		});
		final ActionListener l=new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean onlyGramCode=onlyShowGramCode.isSelected();
				boolean alwaysGramCode=alwaysShowGramCode.isSelected();
				boolean showAll=all.isSelected();
				Pattern pattern=(Pattern) ((showAll||onlyGramCode||!usePattern.isSelected())?null:filterField.getValue());
				filter.setFilter(pattern,alwaysGramCode,onlyGramCode);
			}
		};
		filterField.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				try {
					filterField.commitEdit();
					filterField.setForeground(Color.BLACK);
					l.actionPerformed(null);
				} catch (ParseException e2) {
					filterField.setForeground(Color.RED);
				}
			}
		});
		alwaysShowGramCode.addActionListener(l);
		all.addActionListener(l);
		onlyShowGramCode.addActionListener(l);
		usePattern.addActionListener(l);
		gbc.anchor=GridBagConstraints.WEST;
		filterPanel.add(alwaysShowGramCode,gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		filterPanel.add(export,gbc);
		gbc.gridwidth=1;
		filterPanel.add(all,gbc);
		filterPanel.add(onlyShowGramCode,gbc);
		filterPanel.add(usePattern,gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.weightx=1;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		filterPanel.add(filterField,gbc);
		p.add(filterPanel,BorderLayout.NORTH);
		return p;
	}

	protected void exportTextAsTable() {
		JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new PersonalFileFilter("txt",
				"Unicode Raw Texts"));
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setCurrentDirectory(Config.getCurrentCorpusDir());
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		UnitexFrame.getFrameManager().newExportTextAsPOSListDialog(chooser.getSelectedFile(),filter);
	}

	protected void refreshTableRowHeight(JTable table) {
		if (table.getRowCount()==0) return;
		TableCellRenderer renderer=table.getCellRenderer(0,0);
		int h=renderer.getTableCellRendererComponent(table,table.getValueAt(0,0),
				false,false,0,0).getPreferredSize().height;
		table.setRowHeight(h);
	}

	/**
	 * We set each column to its preferred width.
	 */
	protected void refreshTableColumnWidths(JTable table) {
		for (int c=0;c<table.getColumnCount();c++) {
			int w=getPreferredWidth(table,c);
			table.getColumnModel().getColumn(c).setPreferredWidth(w);
			table.getColumnModel().getColumn(c).setWidth(w);
		}
	}

	private int getPreferredWidth(JTable table, int column) {
		int max=0;
		TableCellRenderer renderer=table.getCellRenderer(0,column);
		int w=renderer.getTableCellRendererComponent(table,table.getColumnName(column),
				false,false,0,column).getPreferredSize().width;
		if (w>max) max=w;
		for (int row=0;row<table.getRowCount();row++) {
			w=renderer.getTableCellRendererComponent(table,table.getValueAt(row,column),
					false,false,row,column).getPreferredSize().width;
			if (w>max) max=w;
		}
		return max+10;
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
                implodeElagFst();
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
        elaggraph = new TfstGraphicalZone(null, textfield, this, false);
        elaggraph.setPreferredSize(new Dimension(1188, 840));
        elagframe.add(new JScrollPane(elaggraph), BorderLayout.CENTER);
        return elagframe;
    }

    private JPanel constructUpPanel() {
        JPanel upPanel = new JPanel(new BorderLayout());
        sentenceTextArea
                .setFont(Preferences.getCloneOfPreferences().textFont.font);
        sentenceTextArea.setEditable(false);
        sentenceTextArea.setText("");
        sentenceTextArea.setLineWrap(true);
        sentenceTextArea.setWrapStyleWord(true);
        JScrollPane textScroll = new JScrollPane(sentenceTextArea);
        textScroll.setPreferredSize(new Dimension(600, 100));
        textScroll
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        JPanel tmp = new JPanel(new BorderLayout());
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
                int n = spinnerModel.getNumber().intValue();
                File f2 = new File(sentence_modified.getAbsolutePath() + n
                        + ".grf");
                if (f2.exists())
                    f2.delete();
                loadSentence(n);
            }
        };
        resetSentenceGraph = new JButton(resetSentenceAction);
        resetSentenceGraph.setVisible(false);
        cornerPanel.add(resetSentenceGraph);
        Action rebuildAction = new AbstractAction("Rebuild FST-Text") {
            public void actionPerformed(ActionEvent arg0) {
                UnitexFrame.getFrameManager().closeTextAutomatonFrame();
                UnitexFrame.getFrameManager().closeTfstTagsFrame();
                RebuildTfstCommand command = new RebuildTfstCommand()
                        .automaton(new File(Config.getCurrentSntDir(),
                                "text.tfst"));
                Launcher.exec(command, true, new RebuildTextAutomatonDo());
            }
        };
        JButton rebuildTfstButton = new JButton(rebuildAction);
        cornerPanel.add(rebuildTfstButton);
        final JButton elagButton = new JButton("Elag Frame");
        elagButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleElagFrame();
                if (elagON) {
                    elagButton.setText("Close Elag Frame");
                } else {
                    elagButton.setText("Open  Elag Frame");
                }
            }
        });
        cornerPanel.add(elagButton);
        return cornerPanel;
    }

    /**
     * Shows the frame
     */
    boolean loadTfst() {
        text_tfst = new File(Config.getCurrentSntDir(), "text.tfst");
        if (!text_tfst.exists()) {
            return false;
        }
        sentence_text = new File(Config.getCurrentSntDir(), "cursentence.txt");
        sentence_grf = new File(Config.getCurrentSntDir(), "cursentence.grf");
        sentence_tok = new File(Config.getCurrentSntDir(), "cursentence.tok");
        sentence_modified = new File(Config.getCurrentSntDir(), "sentence");
        elag_tfst = new File(Config.getCurrentSntDir(), "text-elag.tfst");
        elagsentence_grf = new File(Config.getCurrentSntDir(),
                "currelagsentence.grf");
        sentence_count = readSentenceCount(text_tfst);
        String s = " " + sentence_count;
        s = s + " sentence";
        if (sentence_count > 1)
            s = s + "s";
        sentence_count_label.setText(s);
        spinnerModel.setMaximum(new Integer(sentence_count));
        spinnerModel.setValue(new Integer(1));
        if (sentence_count == 1) {
            /*
                * The sentence_count!=1, spinnerModel.setValue does the job.
                * Otherwise, setValue(1) is ignored because the current value is
                * already 1
                */
            loadSentence(1);
        }
        return true;
    }

    /**
     * Indicates if the graph has been modified
     *
     * @param b <code>true</code> if the graph has been modified,
     *          <code>false</code> otherwise
     */
    public void setModified(boolean b) {
        repaint();
        resetSentenceGraph.setVisible(b);
        int n = spinnerModel.getNumber().intValue();
        if (b && !isAcurrentLoadingThread && n != 0) {
            /* We save each modification, but only
                * if the sentence graph loading is terminated
                */
            GraphIO g = new GraphIO(graphicalZone);
            g.saveSentenceGraph(new File(sentence_modified.getAbsolutePath()
                    + n + ".grf"),
                    graphicalZone.getGraphPresentationInfo());
        }
    }

    private int readSentenceCount(File f) {
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
        return Integer.parseInt(s);
    }

    public void loadSentenceFromConcordance(int n) {
        if (!isVisible() || isIcon()) {
            return;
        }
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
     * @param n sentence number
     * @return <code>false</code> if a sentence is already being loaded,
     *         <code>true</code> otherwise
     */
    public boolean loadSentence(int n) {
        if (n < 1 || n > sentence_count)
            return false;
        final int z = n;
        if (isAcurrentLoadingThread)
            return false;
        isAcurrentLoadingThread = true;
        graphicalZone.empty();
        sentenceTextArea.setText("");
        Tfst2GrfCommand cmd = new Tfst2GrfCommand().automaton(text_tfst)
                .sentence(z);
        if (Config.isKorean() || Config.isKoreanJeeSun()) {
            cmd = cmd.font("Gulim").fontSize(12);
        } else {
            cmd = cmd.font(Preferences.inputFont().getName()).fontSize(
                    Preferences.inputFontSize());
        }
        Console.addCommand(cmd.getCommandLine(), false, Log.getCurrentLogID());
        Process p;
        try {
            p = Runtime.getRuntime().exec(cmd.getCommandArguments());
            BufferedInputStream in = new BufferedInputStream(p.getInputStream());
            BufferedInputStream err = new BufferedInputStream(p
                    .getErrorStream());
            new EatStreamThread(in).start();
            new EatStreamThread(err).start();
            p.waitFor();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        String text=readSentenceText();
        try {
            TokensInfo.loadTokensInfo(sentence_tok,text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        File f = new File(sentence_modified + String.valueOf(z) + ".grf");
        boolean isSentenceModified = f.exists();
        if (isSentenceModified) {
            loadSentenceGraph(new File(sentence_modified.getAbsolutePath()
                    + String.valueOf(z) + ".grf"));
            setModified(isSentenceModified);
        } else {
            loadSentenceGraph(sentence_grf);
        }
        isAcurrentLoadingThread = false;
        loadElagSentence(z);
        return true;
    }

    public boolean loadElagSentence(int n) {
        if (n < 1 || n > sentence_count) {
            System.err.println("loadElagSentence: n = " + n + " out of bounds");
            return false;
        }
        final int z = n;
        if (isAcurrentElagLoadingThread) {
            return false;
        }
        isAcurrentElagLoadingThread = true;
        elaggraph.empty();
        if (!elag_tfst.exists()) { // if fst file does not exist exit
            isAcurrentElagLoadingThread = false;
            return false;
        }
        Tfst2GrfCommand cmd = new Tfst2GrfCommand().automaton(elag_tfst)
                .sentence(z).output("currelagsentence").font(
                        Preferences.inputFont().getName()).fontSize(
                        Preferences.inputFontSize());
        Console.addCommand(cmd.getCommandLine(), false, Log.getCurrentLogID());
        try {
            Process p = Runtime.getRuntime().exec(cmd.getCommandArguments());
            BufferedInputStream in = new BufferedInputStream(p.getInputStream());
            BufferedInputStream err = new BufferedInputStream(p
                    .getErrorStream());
            new EatStreamThread(in).start();
            new EatStreamThread(err).start();
            p.waitFor();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        loadElagSentenceGraph(elagsentence_grf);
        isAcurrentElagLoadingThread = false;
        return true;
    }

    public void changeAntialiasingValue() {
        boolean a = graphicalZone.getAntialiasing();
        graphicalZone.setAntialiasing(!a);
    }

    String readSentenceText() {
        String s = "";
        try {
            FileInputStream br = UnicodeIO
                    .openUnicodeLittleEndianFileInputStream(sentence_text);
            s = UnicodeIO.readLine(br);
            if (s == null || s.equals("")) {
                return "";
            }
            sentenceTextArea.setFont(Config.getCurrentTextFont());
            sentenceTextArea.setText(s);
            br.close();
        } catch (NotAUnicodeLittleEndianFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    boolean loadSentenceGraph(File file) {
        setModified(false);
        GraphIO g = GraphIO.loadGraph(file, true);
        if (g == null) {
            return false;
        }
        textfield.setFont(g.info.input.font);
        graphicalZone.setup(g);
		tfstTableModel.init(g.boxes);
		return true;
    }

    boolean loadElagSentenceGraph(File file) {
        setModified(false);
        GraphIO g = GraphIO.loadGraph(file, true);
        if (g == null)
            return false;
        elaggraph.setup(g);
        return true;
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
        JLabel titlelabel = new JLabel("Elag Rule:");
        elagrules = new File(Config.getCurrentElagDir(), "elag.rul");
        ruleslabel = new JLabel(elagrules.getName());
        ruleslabel.setBorder(new LineBorder(Color.black, 1, true));
        JButton button = new JButton("Browse");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(elagrules.getParentFile());
                fc.setFileFilter(new PersonalFileFilter("rul",
                        "Elag rules file ( .rul)"));
                fc.setAcceptAllFileFilterUsed(false);
                fc.setDialogTitle("Choose Elag Rule File");
                fc.setDialogType(JFileChooser.OPEN_DIALOG);
                if ((fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
                        || (fc.getSelectedFile() == null)) {
                    return;
                }
                elagrules = fc.getSelectedFile();
                ruleslabel.setText(elagrules.getName());
            }
        });
        JCheckBox implodeCheckBox = new JCheckBox(
                "Implode resulting text automaton", true);
        BorderLayout layout = new BorderLayout();
        layout.setVgap(10);
        layout.setHgap(10);
        JPanel p = new JPanel(layout);
        p.add(titlelabel, BorderLayout.WEST);
        p.add(ruleslabel, BorderLayout.CENTER);
        p.add(button, BorderLayout.EAST);
        p.add(implodeCheckBox, BorderLayout.SOUTH);
        if (JOptionPane.showInternalConfirmDialog(this, p,
                "Apply Elag Rule", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }
        ElagCommand elagcmd = new ElagCommand().lang(
                new File(Config.getCurrentElagDir(), "tagset.def")).rules(
                elagrules).output(elag_tfst).automaton(text_tfst);
        if (implodeCheckBox.isSelected()) {
            Launcher.exec(elagcmd, false, new ImploseDo(this, elag_tfst));
        } else {
            Launcher.exec(elagcmd, false, new loadSentenceDo(this));
        }
    }

    void replaceElagFst() {
        if (!elag_tfst.exists()) {
            JOptionPane.showInternalMessageDialog(UnitexFrame.mainFrame,
                    "replaceElagFst: file '" + elag_tfst + "' doesn't exists");
            return;
        }
        /* cleanup files */
        File dir = Config.getCurrentSntDir();
        Config.deleteFileByName(new File(Config.getCurrentSntDir(),
                "sentence*.grf"));
        File f = new File(dir, "currelagsentence.grf");
        if (f.exists() && !f.delete()) {
            JOptionPane.showInternalMessageDialog(UnitexFrame.mainFrame,
                    "Failed to delete " + f);
        }
        f = new File(dir, "currelagsentence.txt");
        if (f.exists() && !f.delete()) {
            JOptionPane.showInternalMessageDialog(UnitexFrame.mainFrame,
                    "Failed to delete " + f);
        }
        if (text_tfst.exists() && !text_tfst.delete()) {
            JOptionPane.showInternalMessageDialog(UnitexFrame.mainFrame,
                    "Failed to delete " + text_tfst);
        }
        if (!elag_tfst.renameTo(text_tfst)) {
            JOptionPane.showInternalMessageDialog(UnitexFrame.mainFrame
                    ,
                    "Failed to replace " + text_tfst + " with " + elag_tfst);
        }
        loadCurrSentence();
    }

    void exploseElagFst() {
        explodeTextAutomaton(elag_tfst);
    }

    void implodeElagFst() {
        implodeTextAutomaton(elag_tfst);
    }

    boolean explodeTextAutomaton(File f) {
        if (!f.exists()) {
            return false;
        }
        TagsetNormTfstCommand tagsetcmd = new TagsetNormTfstCommand().tagset(
                new File(Config.getCurrentElagDir(), "tagset.def"))
                .automaton(f);
        Launcher.exec(tagsetcmd, true, new loadSentenceDo(this));
        return true;
    }

    boolean implodeTextAutomaton(File f) {
        if (!f.exists()) {
            return false;
        }
        ImplodeTfstCommand imploseCmd = new ImplodeTfstCommand().automaton(f);
        Launcher.exec(imploseCmd, true, new loadSentenceDo(this));
        return true;
    }

    /**
     * Normalize the main text automaton according to tagset description in
     * tagset.def if implode is true, then implode the resulting automaton
     */
    void normalizeFst(boolean implode) {
        TagsetNormTfstCommand tagsetcmd = new TagsetNormTfstCommand().tagset(
                new File(Config.getCurrentElagDir(), "tagset.def")).automaton(
                text_tfst);
        if (implode) {
            Launcher.exec(tagsetcmd, false, new ImploseDo(this, text_tfst));
        } else {
            Launcher.exec(tagsetcmd, false, new loadSentenceDo(this));
        }
    }

    class RebuildTextAutomatonDo implements ToDo {
        public void toDo() {
            Config.deleteFileByName(new File(Config.getCurrentSntDir(),
                    "sentence*.grf"));
            File dir = Config.getCurrentSntDir();
            File f = new File(dir, "currelagsentence.grf");
            if (f.exists() && !f.delete()) {
                JOptionPane.showInternalMessageDialog(UnitexFrame.mainFrame,
                        "Failed to delete " + f);
            }
            f = new File(dir, "currelagsentence.txt");
            if (f.exists() && !f.delete()) {
                JOptionPane.showInternalMessageDialog(UnitexFrame.mainFrame,
                        "Failed to delete " + f);
            }
            f = new File(dir, "text-elag.tfst");
            if (f.exists() && !f.delete()) {
                JOptionPane.showInternalMessageDialog(UnitexFrame.mainFrame,
                        "unable to delete " + f);
            }
            UnitexFrame.getFrameManager().newTextAutomatonFrame(1, false);
        }
    }

    public int getSentenceCount() {
    	return ((Integer)spinnerModel.getMaximum()).intValue();
    }
    
}


class loadSentenceDo implements ToDo {
    TextAutomatonFrame frame;

    loadSentenceDo(TextAutomatonFrame f) {
        frame = f;
    }

    public void toDo() {
        frame.loadCurrSentence();
    }
}


class ImploseDo implements ToDo {
    File fst;
    TextAutomatonFrame fr;

    public ImploseDo(TextAutomatonFrame frame, File f) {
        fst = f;
        fr = frame;
    }

    public void toDo() {
        fr.implodeTextAutomaton(fst);
    }
}
