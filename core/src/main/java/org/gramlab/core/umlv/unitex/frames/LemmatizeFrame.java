/*
 * Unitex
 *
 * Copyright (C) 2001-2016 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package org.gramlab.core.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gramlab.core.gramlab.util.KeyUtil;
import org.gramlab.core.umlv.unitex.DropTargetManager;
import org.gramlab.core.umlv.unitex.concord.BigConcordance;
import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.console.Console;
import org.gramlab.core.umlv.unitex.exceptions.InvalidConcordanceOrderException;
import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.graphrendering.GenericGraphBox;
import org.gramlab.core.umlv.unitex.graphrendering.TfstGraphBox;
import org.gramlab.core.umlv.unitex.graphrendering.TfstGraphicalZone;
import org.gramlab.core.umlv.unitex.graphrendering.TfstTextField;
import org.gramlab.core.umlv.unitex.io.Encoding;
import org.gramlab.core.umlv.unitex.io.GraphIO;
import org.gramlab.core.umlv.unitex.io.UnicodeIO;
import org.gramlab.core.umlv.unitex.listeners.GraphListener;
import org.gramlab.core.umlv.unitex.process.EatStreamThread;
import org.gramlab.core.umlv.unitex.process.Launcher;
import org.gramlab.core.umlv.unitex.process.Log;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.process.commands.ConcordCommand;
import org.gramlab.core.umlv.unitex.process.commands.LocateTfstCommand;
import org.gramlab.core.umlv.unitex.process.commands.MultiCommands;
import org.gramlab.core.umlv.unitex.process.commands.RebuildTfstCommand;
import org.gramlab.core.umlv.unitex.process.commands.Tfst2GrfCommand;
import org.gramlab.core.umlv.unitex.project.manager.UnitexProjectManager;
import org.gramlab.core.umlv.unitex.tfst.TokensInfo;

/**
 * This class describes a frame used to lemmatize sentence automata.
 * 
 * @author Sébastien Paumier
 */
public class LemmatizeFrame extends TfstFrame {
	
	JPanel concordancePanel=new JPanel(new BorderLayout());
	BigConcordance list;
	JComboBox lemmaCombo=new JComboBox();
	
	final JLabel sentenceCountLabel = new JLabel(" 0 sentence");
	JSpinner spinner;
	SpinnerNumberModel spinnerModel;
	JScrollBar tfstScrollbar;
	TfstGraphicalZone graphicalZone;
	public JScrollPane scrollPane;
	private final GraphListener listener = new GraphListener() {
		@Override
		public void graphChanged(boolean m) {
			if (m)
				setModified(true);
			repaint();
		}
	};

	@Override
	public JScrollPane getTfstScrollPane() {
		return scrollPane;
	}

	@Override
	public TfstGraphicalZone getTfstGraphicalZone() {
		return graphicalZone;
	}

	private final TfstTextField textfield = new TfstTextField(25, this);
	boolean modified = false;
	int sentence_count = 0;
	File sentence_text;
	File sentence_grf;
	File sentence_tok;
	File sentence_modified;
	File text_tfst;
	boolean isAcurrentLoadingThread = false;
	JSplitPane superpanel;
	JButton resetSentenceGraphs;
	JLabel nMatches=new JLabel("0 match");

	LemmatizeFrame() {
		super("Lemmatization", true, true, true, true);
		DropTargetManager.getDropTarget().newDropTarget(this);
		setContentPane(constructPanel());
		pack();
		setBounds(10, 10, 1000, 600);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		textfield.setEditable(false);
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				if (list!=null) {
					list.reset();
				}
			}
		});
	}

	private JPanel constructPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(constructUpPanel(), BorderLayout.NORTH);
		final JScrollPane scroll = new JScrollPane(concordancePanel);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		superpanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				scroll,constructTextPanel());
		superpanel.setOneTouchExpandable(true);
		superpanel.setResizeWeight(0.5);
		panel.add(superpanel, BorderLayout.CENTER);
		KeyUtil.addCloseFrameListener(panel);
		return panel;
	}

	private JPanel constructTextPanel() {
		final JPanel textframe = new JPanel(new BorderLayout());
		final JPanel downPanel = new JPanel(new BorderLayout());
		graphicalZone = new TfstGraphicalZone(null, textfield, this, true);
		graphicalZone.addGraphListener(listener);
		graphicalZone.setPreferredSize(new Dimension(1188, 840));
		scrollPane = new JScrollPane(graphicalZone);
		tfstScrollbar = scrollPane.getHorizontalScrollBar();
		tfstScrollbar.setUnitIncrement(20);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		scrollPane.setPreferredSize(new Dimension(1188, 840));
		textfield.setFont(ConfigManager.getManager().getInputFont(null));
		downPanel.add(textfield, BorderLayout.NORTH);
		downPanel.add(scrollPane, BorderLayout.CENTER);
		textframe.add(downPanel, BorderLayout.CENTER);
		return textframe;
	}


	private JPanel constructUpPanel() {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.fill=GridBagConstraints.BOTH;
		p.add(constructSearchPanel(),gbc);
		p.add(constructLemmaPanel(),gbc);
		p.add(constructSentenceNavigationPanel(),gbc);
		gbc.weightx=1;
		p.add(new JPanel(),gbc);
		return p;
	}


	private JPanel constructLemmaPanel() {
		JPanel p = new JPanel(new GridLayout(3,1));
		p.setBorder(BorderFactory.createTitledBorder("Lemma selection"));
		lemmaCombo.setMinimumSize(new Dimension(400,lemmaCombo.getPreferredSize().height));
		lemmaCombo.setPreferredSize(new Dimension(400,lemmaCombo.getPreferredSize().height));
		lemmaCombo.setFont(ConfigManager.getManager().getConcordanceFont(null));
		lemmaCombo.setFont(ConfigManager.getManager().getConcordanceFont(null));
		p.add(lemmaCombo);
		JButton validateOne=new JButton("Lemmatize selected item");
		validateOne.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String s=(String) list.getSelectedValue();
				if (s==null) return;
				String lemma=(String)lemmaCombo.getSelectedItem();
				if (lemma==null) {
					JOptionPane.showMessageDialog(null, "You must select a lemma!",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				int[] res=getHrefInfos(s);
				int offsetInTokens=getOffsetInTokens();
				int matchStartInTokens=res[3]-offsetInTokens;
				/* Now that we have the lemma information and the start position
				 * of the match, we can locate the graph box to select as if with a
				 * right click
				 */
				int index=getBoxToSelectIndex(lemma,matchStartInTokens);
				graphicalZone.getTaggingModel().selectBox(index);
				graphicalZone.repaint();
				graphicalZone.unsureBoxIsVisible(index);
			}

		});
		JButton validateAll=new JButton("Apply lemma to all items");
		validateAll.addActionListener(new ActionListener() {
			
			boolean contains(ComboBoxModel lemmas,String lemma) {
				for (int i=0;i<lemmas.getSize();i++) {
					if (lemmas.getElementAt(i).equals(lemma)) return true;
				}
				return false;
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String s=(String) list.getSelectedValue();
				if (s==null) return;
				String lemma=(String)lemmaCombo.getSelectedItem();
				if (lemma==null) {
					JOptionPane.showMessageDialog(null, "You must select a lemma!",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				int currentSentence=(Integer) spinnerModel.getValue();
				/* We look for compatible lemma in all matches */
				for (int i=0;i<list.getModel().getSize();i++) {
					String cellValue=(String) list.getModel().getElementAt(i);
					ComboBoxModel lemmas=getLemmaModel(cellValue);
					if (!contains(lemmas,lemma)) {
						continue;
					}
					int[] res=getHrefInfos(cellValue);
					spinner.setValue(res[2]);
					loadSentence(res[2]);
					int offsetInTokens=getOffsetInTokens();
					int matchStartInTokens=res[3]-offsetInTokens;
					/* Now that we have the lemma information and the start position
					 * of the match, we can locate the graph box to select as if with a
					 * right click
					 */
					int index=getBoxToSelectIndex(lemma,matchStartInTokens);
					if (index==-1) {
						/* This can happen when a box exists in the .tfst but not in
						 * the .grf because the .grf was manually edited
						 */
						return;
					}
					graphicalZone.getTaggingModel().selectBox(index);
					graphicalZone.saveStateSelection(res[2]);
					graphicalZone.repaint();
					graphicalZone.unsureBoxIsVisible(index);
				}
				spinner.setValue(currentSentence);
				loadSentence(currentSentence);
			}
		});
		p.add(validateOne);
		p.add(validateAll);
		return p;
	}
	

	public int getBoxToSelectIndex(String lemma,int matchStartInTokens) {
		ArrayList<GenericGraphBox> boxes=graphicalZone.getBoxes();
		for (int i=0;i<boxes.size();i++) {
			TfstGraphBox b=(TfstGraphBox)boxes.get(i);
			if (b.getBounds()==null) continue;
			if (b.getBounds().getStart_in_tokens()!=matchStartInTokens) continue;
			String boxLemma;
			if (b.lines.size()==2) {
				boxLemma=b.lines.get(1)+"."+b.transduction;
			} else {
				boxLemma=b.lines.get(0)+"."+b.transduction;
			}
		if (!boxLemma.equals(lemma)) continue;
			return i;
		}
		return -1;
	}
	
	
	protected int getOffsetInTokens() {
		File start=new File(Config.getCurrentSntDir(),"cursentence.start");
		String content=Encoding.getContent(start);
		Scanner s=new Scanner(content);
		return s.nextInt();
	}

	private JPanel constructSearchPanel() {
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createTitledBorder("Select elements to lemmatize"));
		JPanel p2=new JPanel(new BorderLayout());
		p2.add(new JLabel("Pattern: "),BorderLayout.WEST);
		final JTextField pattern=new JTextField();
		pattern.setFont(ConfigManager.getManager().getConcordanceFont(null));
		p2.add(pattern,BorderLayout.CENTER);
		JButton GO=new JButton("GO");
		p2.add(GO,BorderLayout.EAST);
		p.add(p2,BorderLayout.NORTH);

		JPanel p3=new JPanel(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();
		p3.setBorder(BorderFactory.createTitledBorder("Show matching sequences in context"));
		gbc.fill=GridBagConstraints.BOTH;
		gbc.gridwidth=GridBagConstraints.RELATIVE;
		p3.add(new JLabel("Context lengths:"),gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p3.add(new JLabel("Sort according to:"),gbc);
		gbc.gridwidth=1;
		final JTextField left=new JTextField("40");
		final JTextField right=new JTextField("55");
		final JTextField limit=new JTextField("");
		left.setPreferredSize(new Dimension(50,15));
		right.setPreferredSize(new Dimension(50,15));
		limit.setPreferredSize(new Dimension(50,15));
		p3.add(new JLabel("Left: "),gbc);
		p3.add(left,gbc);
		p3.add(new JLabel(" chars  "),gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		final String[] items = new String[7];
		items[0] = "Text Order";
		items[1] = "Left, Center";
		items[2] = "Left, Right";
		items[3] = "Center, Left";
		items[4] = "Center, Right";
		items[5] = "Right, Left";
		items[6] = "Right, Center";
		final JComboBox sortBox = new JComboBox(items);
		sortBox.setSelectedIndex(4);
		p3.add(sortBox,gbc);
		gbc.gridwidth=1;
		p3.add(new JLabel("Right: "),gbc);
		p3.add(right,gbc);
		p3.add(new JLabel(" chars  "),gbc);
		final JRadioButton all=new JRadioButton("Show all elements",false);
		final JRadioButton unresolved=new JRadioButton("Show unresolved only",true);
		ButtonGroup bg=new ButtonGroup();
		bg.add(all);
		bg.add(unresolved);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p3.add(all,gbc);
		gbc.gridwidth=1;
		p3.add(new JLabel("Limit: "),gbc);
		p3.add(limit,gbc);
		limit.setToolTipText("Leave empty to get all matches");
		p3.add(new JLabel(" matches "),gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p3.add(unresolved,gbc);
		p.add(p3,BorderLayout.CENTER);
		
		GO.addActionListener(new ActionListener() {
			
			/**
			 * Returns -2 if s is empty, -1 if not empty and not a positive integer,
			 * or n if s represents the value n.
			 */
			private int getInt(String s) {
				if (s.equals("")) return -2;
				try {
					int y=Integer.parseInt(s);
					if (y<0) return -1;
					return y;
				} catch (NumberFormatException e) {
					return -1;
				}
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String s=pattern.getText();
				if (s.equals("")) {
					JOptionPane.showMessageDialog(null, "You must specify a pattern!",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				int leftCtx=getInt(left.getText());
				if (leftCtx<0) {
					JOptionPane.showMessageDialog(null, "The left context must be a valid integer >=0",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				int rightCtx=getInt(right.getText());
				if (rightCtx<0) {
					JOptionPane.showMessageDialog(null, "The right context must be a valid integer >=0",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				int limitCtx=getInt(limit.getText());
				if (limitCtx==-1 || limitCtx==0) {
					JOptionPane.showMessageDialog(null, "The limit must either empty (all matches) or valid integer >0",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				final File fst2=new File(ConfigManager.getManager().getCurrentLanguageDir(),"lemmatize.fst2");
				createLemmatizeFst2(fst2,s);
				final MultiCommands commands=new MultiCommands();
				LocateTfstCommand cmd1=new LocateTfstCommand().allowAmbiguousOutputs()
						.alphabet(ConfigManager.getManager().getAlphabet(null))
						.mergeOutputs()
						.tfst(new File(Config.getCurrentSntDir(),"text.tfst"))
						.fst2(fst2)
						.backtrackOnVariableErrors()
						.singleTagsOnly();
				if (limitCtx==-2) cmd1=cmd1.allMatches();
				else cmd1=cmd1.limit(limitCtx);
				if (ConfigManager.getManager().isKorean(null)) {
					cmd1=cmd1.korean();
				}
				if (!ConfigManager.getManager().isMatchWordBoundaries(null)) {
					cmd1=cmd1.dontMatchWordBoundaries();
				}
				commands.addCommand(cmd1);
				ConcordCommand cmd2;
				File indFile=new File(Config.getCurrentSntDir(),"concord.ind");
				try {
					cmd2 = new ConcordCommand()
							.indFile(indFile)
							.font(ConfigManager.getManager().getConcordanceFontName(
									null))
							.fontSize(
									ConfigManager.getManager().getConcordanceFontSize(
											null))
							.left(leftCtx, false)
							.right(rightCtx, false)
							.lemmatize()
							.sortAlphabet()
							.thai(ConfigManager.getManager().isThai(null))
							.order(sortBox.getSelectedIndex());
					if (unresolved.isSelected()) {
						cmd2 = cmd2.onlyAmbiguous();
					}
					if (ConfigManager.getManager().isPRLGLanguage(null)) {
						final File prlgIndex = new File(Config.getCurrentSntDir(),
								"prlg.idx");
						final File offsets = new File(Config.getCurrentSntDir(),
								"tokenize.out.offsets");
						if (prlgIndex.exists() && offsets.exists()) {
							cmd2 = cmd2.PRLG(prlgIndex, offsets);
						}
					}
				} catch (final InvalidConcordanceOrderException e2) {
					e2.printStackTrace();
					return;
				}
				commands.addCommand(cmd2);
				final File html=new File(Config.getCurrentSntDir(),"lemmatize.html");
				final ToDo after=new ToDo() {
					
					@Override
					public void toDo(boolean success) {
						fst2.delete();
						loadTfst();
						loadConcordance(html);
					}

				};
				if (list!=null) {
					list.reset();
				}
				concordancePanel.removeAll();
				nMatches.setText("0 match");
				Launcher.exec(commands,true,after);
			}

			private void createLemmatizeFst2(File fst2,String s) {
				OutputStreamWriter writer=Encoding.UTF8.getOutputStreamWriter(fst2);
				try {
					writer.write("0000000001\n");
					writer.write("-1 biniou\n");
					writer.write(": 1 1 \n");
					writer.write(": 2 2 \n");
					writer.write(": 3 3 \n");
					writer.write(": 4 4 \n");
					writer.write(": 5 5 \n");
					writer.write("t \n");
					writer.write("f \n");
					writer.write("%<E>\n");
					writer.write("%$[0\n");
					writer.write("%"+s+"\n");
					writer.write("%$]\n");
					writer.write("%<DIC>/$:x$\n");
					writer.write("%<E>//$x.LEMMA$.$x.CODE$\n");
					writer.write("f\n");
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
		return p;
	}
	
	void loadConcordance(File html) {
		if (list!=null) {
			list.reset();
		}
		concordancePanel.removeAll();
		list=new BigConcordance();
		list.getModel().addListDataListener(new ListDataListener() {
			
			void updateLabel() {
				int size=list.getModel().getSize();
				nMatches.setText(size+" match"+((size>1)?"es":""));
			}
			
			@Override
			public void intervalRemoved(ListDataEvent e) {
				updateLabel();
			}
			
			@Override
			public void intervalAdded(ListDataEvent e) {
				updateLabel();
			}
			
			@Override
			public void contentsChanged(ListDataEvent e) {
				updateLabel();
			}
		});
		concordancePanel.add(list,BorderLayout.CENTER);
		concordancePanel.revalidate();
		concordancePanel.repaint();
		list.setFont(ConfigManager.getManager().getConcordanceFont(null));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				final String s = (String) list.getSelectedValue();
				if (s == null || e.getValueIsAdjusting())
					return;
				int[] res=getHrefInfos(s);
				spinner.setValue(res[2]);
				loadSentence(res[2]);
				lemmaCombo.setModel(getLemmaModel(s));
				lemmaCombo.revalidate();
				lemmaCombo.repaint();
			}

		});
		list.load(html);
		
	}

	public ComboBoxModel getLemmaModel(String s) {
		Vector<String> vector=new Vector<String>();
		int start=s.indexOf("<!--")+4;
		int end=s.indexOf("-->",start);
		int n=Integer.parseInt(""+s.subSequence(start,end));
		for (int i=0;i<n;i++) {
			start=s.indexOf("<!--",end)+4;
			end=s.indexOf("-->",start);
			vector.add(""+s.subSequence(start,end));
		}
		return new DefaultComboBoxModel(vector);
	}
	
	private JPanel constructSentenceNavigationPanel() {
		final JPanel p = new JPanel(new GridLayout(6, 1));
		p.setBorder(BorderFactory.createTitledBorder("Navigation"));
		p.add(nMatches);
		p.add(sentenceCountLabel);
		final JPanel middle = new JPanel(new BorderLayout());
		middle.add(new JLabel(" Sentence # "), BorderLayout.WEST);
		spinnerModel = new SpinnerNumberModel(0,0,0,1);
		spinnerModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				loadSentence(spinnerModel.getNumber().intValue());
			}
		});
		spinner = new JSpinner(spinnerModel);
		middle.add(spinner, BorderLayout.CENTER);
		middle.setPreferredSize(new Dimension(150,20));
		p.add(middle);
		final Action resetSentenceAction = new AbstractAction(
				"Restore graphs from TFST") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int n = spinnerModel.getNumber().intValue();
				for (int i=1;i<sentence_count;i++) {
					File f2 = new File(sentence_modified.getAbsolutePath()
						+ i + ".grf");
					if (f2.exists()) {
						f2.delete();
					}
				}
				graphicalZone.resetAllStateSelections();
				loadSentence(n);
			}
		};
		resetSentenceGraphs = new JButton(resetSentenceAction);
		p.add(resetSentenceGraphs);
		final Action rebuildAction = new AbstractAction("Apply modifications to TFST") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				UnitexProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.closeTextAutomatonFrame();
				UnitexProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.closeTfstTagsFrame();
				Config.cleanTfstFiles(false);
				final RebuildTfstCommand command = new RebuildTfstCommand()
						.automaton(new File(Config.getCurrentSntDir(),
								"text.tfst"));
				Launcher.exec(command, true,
						new RebuildTextAutomatonDo(Config.getCurrentSntDir()));
			}
		};
		final JButton rebuildTfstButton = new JButton(rebuildAction);
		final JButton deleteStates = new JButton("Remove greyed states");
		deleteStates.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int currentSentence=(Integer) spinnerModel.getValue();
				Integer[] modifiedSentences=graphicalZone.getModifiedSentenceIndices();
				for (int i=0;i<modifiedSentences.length;i++) {
					int n=modifiedSentences[i];
					spinner.setValue(n);
					loadSentence(n);
					final ArrayList<GenericGraphBox> boxes = new ArrayList<GenericGraphBox>();
					for (final GenericGraphBox gb : graphicalZone.graphBoxes) {
						if (graphicalZone.isBoxToBeRemoved((TfstGraphBox) gb)) {
							boxes.add(gb);
						}
					}
					graphicalZone.removeBoxes(boxes);
					/* And we save the modified graph */
					final GraphIO g = new GraphIO(graphicalZone);
					File f=new File(Config.getCurrentSntDir(),"sentence"+n+".grf");
					g.saveSentenceGraph(f,graphicalZone.getGraphPresentationInfo());
				}
				spinner.setValue(currentSentence);
				loadSentence(currentSentence);
			}
		});
		p.add(deleteStates);
		p.add(rebuildTfstButton);
		return p;
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
		sentence_count = readSentenceCount(text_tfst);
		String s = " " + sentence_count;
		s = s + " sentence";
		if (sentence_count > 1)
			s = s + "s";
		sentenceCountLabel.setText(s);
		spinnerModel.setMaximum(sentence_count);
		spinnerModel.setMinimum(1);
		return true;
	}

	/**
	 * Indicates if the graph has been modified
	 * 
	 * @param b
	 *            <code>true</code> if the graph has been modified,
	 *            <code>false</code> otherwise
	 */
	void setModified(boolean b) {
		repaint();
		resetSentenceGraphs.setVisible(b);
		final int n = spinnerModel.getNumber().intValue();
		if (b && !isAcurrentLoadingThread && n != 0) {
			/*
			 * We save each modification, but only if the sentence graph loading
			 * is terminated
			 */
			final GraphIO g = new GraphIO(graphicalZone);
			g.saveSentenceGraph(new File(sentence_modified.getAbsolutePath()
					+ n + ".grf"), graphicalZone.getGraphPresentationInfo());
		}
	}

	private int readSentenceCount(File f) {
		String s = "0";
		try {
			final InputStreamReader reader = Encoding.getInputStreamReader(f);
			if (reader == null) {
				return 0;
			}
			s = UnicodeIO.readLine(reader);
			if (s == null || s.equals("")) {
				return 0;
			}
			reader.close();
		} catch (final IOException e) {
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
	 * @param n
	 *            sentence number
	 * @return <code>false</code> if a sentence is already being loaded,
	 *         <code>true</code> otherwise
	 */
	boolean loadSentence(int n) {
		if (n < 1 || n > sentence_count)
			return false;
		final int z = n;
		if (isAcurrentLoadingThread)
			return false;
		isAcurrentLoadingThread = true;
		graphicalZone.empty();
		Tfst2GrfCommand cmd = new Tfst2GrfCommand().automaton(text_tfst)
				.sentence(z);
		cmd = cmd.font(ConfigManager.getManager().getInputFont(null).getName())
				.fontSize(ConfigManager.getManager().getInputFontSize(null));
		Console.addCommand(cmd.getCommandLine(), false, Log.getCurrentLogID());
		Process p;
		try {
			p = Runtime.getRuntime().exec(cmd.getCommandArguments(true));
			final BufferedInputStream in = new BufferedInputStream(
					p.getInputStream());
			final BufferedInputStream err = new BufferedInputStream(
					p.getErrorStream());
			new EatStreamThread(in).start();
			new EatStreamThread(err).start();
			p.waitFor();
		} catch (final IOException e1) {
			e1.printStackTrace();
		} catch (final InterruptedException e1) {
			e1.printStackTrace();
		}
		final String text = readSentenceText();
		TokensInfo.loadTokensInfo(sentence_tok, text);
		final File f = new File(sentence_modified + String.valueOf(z) + ".grf");
		final boolean isSentenceModified = f.exists();
		if (isSentenceModified) {
			loadSentenceGraph(new File(sentence_modified.getAbsolutePath()
					+ String.valueOf(z) + ".grf"),n);
			setModified(isSentenceModified);
		} else {
			loadSentenceGraph(sentence_grf,n);
		}
		isAcurrentLoadingThread = false;
		return true;
	}

	String readSentenceText() {
		String s = "";
		try {
			final InputStreamReader br = Encoding
					.getInputStreamReader(sentence_text);
			if (br == null) {
				return "";
			}
			s = UnicodeIO.readLine(br);
			if (s == null || s.equals("")) {
				return "";
			}
			br.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return s;
	}

	public void changeAntialiasingValue() {
		final boolean a = graphicalZone.getAntialiasing();
		graphicalZone.setAntialiasing(!a);
	}

	boolean loadSentenceGraph(File file,int sentence) {
		setModified(false);
		final GraphIO g = GraphIO.loadGraph(file, true, true);
		if (g == null) {
			return false;
		}
		textfield.setFont(g.getInfo().getInput().getFont());
		graphicalZone.setup(g,sentence);
		final Timer t = new Timer(300, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ConfigManager.getManager().isRightToLeftForGraphs(null)) {
					tfstScrollbar.setValue(tfstScrollbar.getMaximum());
				} else {
					tfstScrollbar.setValue(0);
				}
			}
		});
		t.setRepeats(false);
		t.start();
		return true;
	}

	
	int[] getHrefInfos(String s) {
		int[] res=new int[4];
		int start = s.indexOf("<a href=\"") + 9;
		int end = s.indexOf(' ', start);
		int selectionStart = Integer.valueOf((String) s
				.subSequence(start, end));
		start = end + 1;
		end = s.indexOf(' ', start);
		int selectionEnd = Integer.valueOf((String) s
				.subSequence(start, end));
		start = end + 1;
		end = s.indexOf(' ', start);
		int sentenceNumber = Integer.valueOf((String) s
				.subSequence(start, end));
		start = end + 1;
		end = s.indexOf('\"', start);
		int matchNumber = Integer.valueOf((String) s.subSequence(
				start, end));
		res[0]=selectionStart;
		res[1]=selectionEnd;
		res[2]=sentenceNumber;
		res[3]=matchNumber;
		return res;
	}
	

	class RebuildTextAutomatonDo implements ToDo {
		File sntDir;

		public RebuildTextAutomatonDo(File sntDir) {
			this.sntDir = sntDir;
		}

		@Override
		public void toDo(boolean success) {
			FileUtil.deleteFileByName(new File(sntDir, "sentence*.grf"));
			/* Todo: reload de la phrase courante */
		}
	}

	public int getSentenceCount() {
		return (Integer) spinnerModel.getMaximum();
	}
}

class LoadSentenceDo2 implements ToDo {
	private final LemmatizeFrame frame;

	LoadSentenceDo2(LemmatizeFrame f) {
		frame = f;
	}

	@Override
	public void toDo(boolean success) {
		frame.loadCurrSentence();
	}
}

