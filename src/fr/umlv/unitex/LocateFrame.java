/*
 * Unitex
 *
 * Copyright (C) 2001-2008 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import fr.umlv.unitex.exceptions.*;
import fr.umlv.unitex.io.*;
import fr.umlv.unitex.process.*;


/**
 * This class describes the locate pattern frame.
 * 
 * @author Sébastien Paumier
 *  
 */
public class LocateFrame extends JInternalFrame {

	static LocateFrame frame;
	JRadioButton regularExpression = new JRadioButton("Regular expression:",
			false);
	JRadioButton graph = new JRadioButton("Graph:", true);
	JTextField regExp = new JTextField();
	JTextField graphName = new JTextField();
	JRadioButton shortestMatches = new JRadioButton("Shortest matches", false);
	JRadioButton longuestMatches = new JRadioButton("Longest matches", true);
	JRadioButton allMatches = new JRadioButton("All matches", false);
	JRadioButton ignoreOutputs = new JRadioButton("Are not taken into account",true);
	JRadioButton mergeOutputs = new JRadioButton("Merge with input text", false);
	JRadioButton replaceOutputs = new JRadioButton("Replace recognized sequences", false);
	JRadioButton stopAfterNmatches = new JRadioButton("Stop after ", true);
	JRadioButton indexAllMatches = new JRadioButton("Index all utterances in text", false);
	NumericTextField nMatches = new NumericTextField("200");

	private LocateFrame() {
		super("Locate Pattern", false, true);
		setContentPane(constructPanel());
		pack();
		setResizable(false);
		setVisible(false);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				setVisible(false);
			}
		});
		GlobalPreferenceFrame.addTextFontListener(new FontListener() {
			public void fontChanged(Font font) {
				regExp.setFont(font);
			}});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	/**
	 * Initializes the frame
	 *  
	 */
	private static void init() {
		frame = new LocateFrame();
		UnitexFrame.addInternalFrame(frame);
	}

	/**
	 * Shows the frame
	 *  
	 */
	public static void showFrame() {
		if (frame == null) {
			init();
		}
		frame.regExp.setFont(Preferences.pref.textFont);
		frame.setVisible(true);
		try {
			frame.setSelected(true);
			frame.setIcon(false);
		} catch (java.beans.PropertyVetoException e2) {
			e2.printStackTrace();
		}
	}

	private JPanel constructPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(true);
		panel.add(constructPatternPanel(), BorderLayout.CENTER);
		panel.add(constructDownPanel(), BorderLayout.SOUTH);
		return panel;
	}

	private JPanel constructPatternPanel() {
		JPanel patternPanel = new JPanel(new BorderLayout());
		patternPanel.setBorder(new TitledBorder(
				"Locate pattern in the form of:"));
		Action setGraphAction = new AbstractAction("Set") {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser grfAndFst2 = Config.getGrfAndFst2DialogBox();
				int returnVal = grfAndFst2.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				graphName.setText(grfAndFst2.getSelectedFile()
						.getAbsolutePath());
				graph.setSelected(true);
			}
		};
		JButton setGraphButton = new JButton(setGraphAction);
		ButtonGroup bg = new ButtonGroup();
		graphName.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent arg0) {
				graph.setSelected(true);
			}
		});
		regExp.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent arg0) {
				regularExpression.setSelected(true);
			}
		});
		bg.add(regularExpression);
		bg.add(graph);
		patternPanel.add(regularExpression, BorderLayout.NORTH);
		regExp.setPreferredSize(new Dimension(300, 30));
		patternPanel.add(regExp, BorderLayout.CENTER);
		JPanel p = new JPanel(new BorderLayout());
		p.add(graph, BorderLayout.WEST);
		p.add(graphName, BorderLayout.CENTER);
		p.add(setGraphButton, BorderLayout.EAST);
		patternPanel.add(p, BorderLayout.SOUTH);
		return patternPanel;
	}

	private JPanel constructDownPanel() {
		JPanel downPanel = new JPanel(new BorderLayout());
		JPanel a = new JPanel(new BorderLayout());
		JPanel b = new JPanel(new BorderLayout());
		a.add(constructIndexPanel(), BorderLayout.WEST);
		a.add(constructOutputPanel(), BorderLayout.CENTER);
		b.add(constructSearchLimitationPanel(), BorderLayout.WEST);
		Action searchAction = new AbstractAction("SEARCH") {
			public void actionPerformed(ActionEvent arg0) {
				frame.launchLocate();
			}
		};
		JButton searchButton = new JButton(searchAction);
		b.add(searchButton, BorderLayout.CENTER);
		downPanel.add(a, BorderLayout.CENTER);
		downPanel.add(b, BorderLayout.SOUTH);
		return downPanel;
	}

	private JPanel constructIndexPanel() {
		JPanel indexPanel = new JPanel(new GridLayout(3, 1));
		indexPanel.setBorder(new TitledBorder("Index"));
		ButtonGroup bg = new ButtonGroup();
		bg.add(shortestMatches);
		bg.add(longuestMatches);
		bg.add(allMatches);
		indexPanel.add(shortestMatches);
		indexPanel.add(longuestMatches);
		indexPanel.add(allMatches);
		return indexPanel;
	}

	private JPanel constructOutputPanel() {
		JPanel outputPanel = new JPanel(new GridLayout(3, 1));
		outputPanel.setBorder(new TitledBorder("Grammar outputs"));
		ButtonGroup bg = new ButtonGroup();
		bg.add(ignoreOutputs);
		bg.add(mergeOutputs);
		bg.add(replaceOutputs);
		outputPanel.add(ignoreOutputs);
		outputPanel.add(mergeOutputs);
		outputPanel.add(replaceOutputs);
		return outputPanel;
	}

	private JPanel constructSearchLimitationPanel() {
		JPanel searchLimitationPanel = new JPanel(new GridLayout(2, 1));
		searchLimitationPanel.setBorder(new TitledBorder("Search limitation"));
		JPanel p = new JPanel(new BorderLayout());
		p.add(stopAfterNmatches, BorderLayout.WEST);
		p.add(nMatches, BorderLayout.CENTER);
		p.add(new JLabel(" matches"), BorderLayout.EAST);
		ButtonGroup bg = new ButtonGroup();
		bg.add(stopAfterNmatches);
		bg.add(indexAllMatches);
		searchLimitationPanel.add(p);
		searchLimitationPanel.add(indexAllMatches);
		return searchLimitationPanel;
	}

	void launchLocate() {
		MultiCommands commands = new MultiCommands();
		File fst2;
		if (stopAfterNmatches.isSelected() && nMatches.getText().equals("")) {
			JOptionPane.showMessageDialog(null,
					"Empty search limitation field !", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (regularExpression.isSelected()) {
			// we need to process a regular expression
			if (regExp.getText().equals("")) {
				JOptionPane.showMessageDialog(null,
						"Empty regular expression !", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			File regexpFile = new File(Config.getUserCurrentLanguageDir(),
					"regexp.txt");
			createRegExpFile(regExp.getText(), regexpFile);
			Reg2GrfCommand reg2GrfCmd = new Reg2GrfCommand().file(regexpFile);
			commands.addCommand(reg2GrfCmd);
			Grf2Fst2Command grfCmd = new Grf2Fst2Command().grf(new File(Config
					.getUserCurrentLanguageDir(), "regexp.grf"))
					.enableLoopAndRecursionDetection(true)
					.tokenizationMode().library();
			commands.addCommand(grfCmd);
			fst2 = new File(Config.getUserCurrentLanguageDir(), "regexp.fst2");
		} else {
			// we need to process a graph
			if (graphName.getText().equals("")) {
				JOptionPane.showMessageDialog(null,
						"You must specify a graph name", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			String grfName = graphName.getText();
			if (grfName.substring(grfName.length() - 3, grfName.length())
					.equalsIgnoreCase("grf")) {
				// we must compile the grf
				Grf2Fst2Command grfCmd = new Grf2Fst2Command().grf(new File(
						grfName)).enableLoopAndRecursionDetection(true)
						.tokenizationMode().library();
				commands.addCommand(grfCmd);
				String fst2Name = grfName.substring(0, grfName.length() - 3);
				fst2Name = fst2Name + "fst2";
				fst2 = new File(fst2Name);
			} else {
				if (!(grfName.substring(grfName.length() - 4, grfName.length())
						.equalsIgnoreCase("fst2"))) {
					// if the extension is nor GRF neither FST2
					JOptionPane.showMessageDialog(null,
							"Invalid graph name extension !", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				fst2 = new File(grfName);
			}
		}
		LocateCommand locateCmd = new LocateCommand().snt(
				Config.getCurrentSnt()).fst2(fst2).alphabet();
		if (shortestMatches.isSelected())
			locateCmd = locateCmd.shortestMatches();
		else if (longuestMatches.isSelected())
			locateCmd = locateCmd.longestMatches();
		else
			locateCmd = locateCmd.allMatches();
		if (ignoreOutputs.isSelected())
			locateCmd = locateCmd.ignoreOutputs();
		else if (mergeOutputs.isSelected())
			locateCmd = locateCmd.mergeOutputs();
		else
			locateCmd = locateCmd.replaceWithOutputs();
		if (stopAfterNmatches.isSelected()) {
			locateCmd = locateCmd.limit(nMatches.getText());
		} else {
			locateCmd = locateCmd.noLimit();
		}
		if (Config.isCharByCharLanguage()) {
			locateCmd = locateCmd.charByChar();
		}
		if (Config.morphologicalUseOfSpaceAllowed()) {
			locateCmd = locateCmd.enableMorphologicalUseOfSpace();
		}
		locateCmd=locateCmd.morphologicalDic(Preferences.pref.morphologicalDic);
		commands.addCommand(locateCmd);
		frame.setVisible(false);
		savePreviousConcordance();
		new ProcessInfoFrame(commands, true, new LocateDo());
	}

	/**
	 * 
	 */
	private void savePreviousConcordance() {
		File prevConcord=new File(Config.getCurrentSntDir(),"previous-concord.ind");
		if (prevConcord.exists()) {
			prevConcord.delete();
		}
		File concord=new File(Config.getCurrentSntDir(),"concord.ind");
		if (concord.exists()) {
			concord.renameTo(prevConcord);
		}
	}

	private void createRegExpFile(String regExp2, File f) {
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f), "UTF-16LE"));
			bw.write('\ufeff');
			bw.write(regExp2, 0, regExp2.length());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static String readInfo(File file) {
		if (!file.exists()) {
			return null;
		}
		if (!file.canRead()) {
			return null;
		}
		String res;
		FileInputStream source;
		try {
			source = UnicodeIO.openUnicodeLittleEndianFileInputStream(file);
			res = UnicodeIO.readLine(source) + "\n";
			res = res + UnicodeIO.readLine(source) + "\n";
			res = res + UnicodeIO.readLine(source);
			source.close();
		} catch (NotAUnicodeLittleEndianFileException e) {
			return null;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		return res;
	}

	class LocateDo extends ToDoAbstract {
		
		public void toDo() {
			String res = readInfo(new File(Config.getCurrentSntDir(),
					"concord.n"));
			String res2 = UnicodeIO.readFirstLine(new File(Config
					.getCurrentSntDir(), "concord.n"));
			JOptionPane.showMessageDialog(null, res, "Result Info",
					JOptionPane.PLAIN_MESSAGE);
			res2=res2.substring(0,res2.indexOf(' '));
			ConcordanceParameterFrame.showFrame(Util.toInt(res2));
		}
	}

}