/*
 * Unitex
 *
 * Copyright (C) 2001-2007 Universit� Paris-Est Marne-la-Vall�e <unitex@univ-mlv.fr>
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
import fr.umlv.unitex.process.*;


/**
 * This class describes a frame in which the user can select how to use the
 * results of a pattern matching.
 * 
 * @author S�bastien Paumier
 *  
 */
public class ConcordanceParameterFrame extends JInternalFrame {

	static ConcordanceParameterFrame frame;
	private NumericTextField leftChars = new NumericTextField("40");
	private NumericTextField rightChars = new NumericTextField("55");
	JCheckBox leftCtxStopAtEOS  = new JCheckBox("", false);
	JCheckBox rightCtxStopAtEOS = new JCheckBox("", false);
	private JComboBox sortBox;
	JCheckBox checkBox = new JCheckBox(
			"Use a web browser to view the concordance");
	JTextField sntFile = new JTextField("");
	JTextField extractFile = new JTextField("");
	String nombre_matches = null;
	boolean useWebBrowser;
	private JButton diffButton;
	/**
	 * Constructs a new <code>ConcordanceParameterFrame</code>.
	 *  
	 */
	public ConcordanceParameterFrame() {
		super("Display indexed sequences...", false, true);
		setContentPane(constructPanel());
		pack();
		useWebBrowser=(Preferences.getCloneOfPreferences().htmlViewer!=null);
		setResizable(false);
		setVisible(false);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				setVisible(false);
			}
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	/**
	 * Initializes the frame.
	 *  
	 */
	private static void init() {
		frame = new ConcordanceParameterFrame();
		UnitexFrame.addInternalFrame(frame);
	}

	/**
	 * Shows the frame.
	 *  
	 */
	public static void showFrame() {
		if (frame == null) {
			init();
		}
		frame.nombre_matches = null;
		frame.setVisible(true);
		frame.updateDiffButton();
		try {
			frame.setSelected(true);
			frame.setIcon(false);
		} catch (java.beans.PropertyVetoException e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private void updateDiffButton() {
		File f=new File(Config.getCurrentSntDir(),"previous-concord.ind");
		diffButton.setEnabled(f.exists());
	}

	/**
	 * Shows the frame. If the number of matches is bigger than a certain limit,
	 * an option is automatically selected to show the concordance with a web
	 * navigator.
	 * 
	 * @param matches
	 *            the number of matches
	 */
	public static void showFrame(int matches) {
		if (frame == null) {
			init();
		}
		if (matches >= Config.MAXIMUM_UTTERANCE_NUMBER_TO_DISPLAY_WITH_JAVA) {
			frame.checkBox.setSelected(true);
		}
		else {
			frame.checkBox.setSelected(frame.useWebBrowser);
		}
		showFrame();
	}

	private JPanel constructPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel up = new JPanel(new BorderLayout());
		up.add(constructUpPanel(), BorderLayout.NORTH);
		up.add(constructExtractPanel(), BorderLayout.CENTER);
		up.add(constructMiddlePanel(), BorderLayout.SOUTH);
		panel.add(up, BorderLayout.NORTH);
		panel.add(constructDiffPanel(), BorderLayout.CENTER);
		panel.add(constructDownPanel(), BorderLayout.SOUTH);
		return panel;
	}

	private JPanel constructExtractPanel() {
		JPanel extract = new JPanel(new GridLayout(2, 1));
		extract.setBorder(new TitledBorder("Extract units"));
		JPanel a = new JPanel();
		a.setLayout(new BorderLayout());
		a.add(extractFile, BorderLayout.CENTER);
		JPanel b = new JPanel();
		b.setLayout(new GridLayout(1, 2));
		Action setAction = new AbstractAction("Set File: ") {
			public void actionPerformed(ActionEvent arg0) {
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
				extractFile
						.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		};
		JButton setSntFile = new JButton(setAction);
		a.add(setSntFile, BorderLayout.WEST);
		Action matchingAction = new AbstractAction("Extract matching units") {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						frame.extractUnits(true);
					}
				});
			}
		};
		JButton matching = new JButton(matchingAction);
		Action unmatchingAction = new AbstractAction("Extract unmatching units") {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						frame.extractUnits(false);
					}
				});
			}
		};
		JButton unmatching = new JButton(unmatchingAction);
		b.add(matching);
		b.add(unmatching);
		extract.add(a);
		extract.add(b);
		return extract;
	}

	private JPanel constructDiffPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(""));
		Action diffAction = new AbstractAction("Show differences with previous concordance") {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						frame.buildDiffConcordance();
					}
				});
			}
		};
		diffButton = new JButton(diffAction);
		panel.add(diffButton,BorderLayout.CENTER);
		return panel;
	}

	private JPanel constructUpPanel() {
		JPanel upPanel = new JPanel(new GridLayout(2, 1));
		upPanel.setBorder(new TitledBorder("Modify text"));
		JPanel a = new JPanel();
		a.setLayout(new BorderLayout());
		a.add(new JLabel(" Resulting .snt file: "), BorderLayout.WEST);
		a.add(sntFile, BorderLayout.CENTER);
		JPanel b = new JPanel();
		b.setLayout(new GridLayout(1, 2));
		Action setAction = new AbstractAction("Set File") {
			public void actionPerformed(ActionEvent arg0) {
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
				sntFile.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		};
		JButton setSntFile = new JButton(setAction);
		b.add(setSntFile);
		Action goAction = new AbstractAction("GO") {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						frame.modifyText();
					}
				});
			}
		};
		JButton GO = new JButton(goAction);
		b.add(GO);
		upPanel.add(a);
		upPanel.add(b);
		return upPanel;
	}

	private JPanel constructMiddlePanel() {
		JPanel middlePanel = new JPanel();
		middlePanel.setBorder(new TitledBorder("Concordance presentation"));
		middlePanel.setLayout(new BorderLayout());
		checkBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				useWebBrowser=checkBox.isSelected();
				frame.setTitle(""+useWebBrowser);
			}});
		middlePanel.add(checkBox, BorderLayout.CENTER);
		middlePanel.add(new JLabel(
				"        (better for more than 2000 matches)"),
				BorderLayout.SOUTH);
		return middlePanel;
	}

	private JPanel constructDownPanel() {
		JPanel downPanel = new JPanel(new GridLayout(1, 2));
		downPanel.setBorder(new TitledBorder(
				"Show matching sequences in context"));

                JPanel CtxLengthCol = new JPanel(new BorderLayout());
                CtxLengthCol.add(new JLabel("Context length:"), BorderLayout.NORTH);
		JPanel a = new JPanel(new GridLayout(2, 1));
		a.add(new JLabel("Left "));
		a.add(new JLabel("Right "));
		JPanel b = new JPanel(new GridLayout(2, 1));
		leftChars.setPreferredSize(new Dimension(30, 20));
		rightChars.setPreferredSize(new Dimension(30, 20));
		b.add(leftChars);
		b.add(rightChars);
		JPanel c = new JPanel(new GridLayout(2, 1));
		c.add(new JLabel(" chars "));
		c.add(new JLabel(" chars "));
                CtxLengthCol.add(a, BorderLayout.WEST);
                CtxLengthCol.add(b, BorderLayout.CENTER);
                CtxLengthCol.add(c, BorderLayout.EAST);

                JPanel StopAtEosCol = new JPanel(new BorderLayout());
                StopAtEosCol.add(new JLabel("Stop at:"), BorderLayout.NORTH);
                JPanel s = new JPanel(new GridLayout(2, 2));
		s.add(leftCtxStopAtEOS);
                s.add(new JLabel("{S}"));
 		s.add(rightCtxStopAtEOS);
                s.add(new JLabel("{S}"));
                StopAtEosCol.add(s, BorderLayout.CENTER);

		JPanel tmp_left = new JPanel();
                tmp_left.add(CtxLengthCol, BorderLayout.WEST);
                tmp_left.add(StopAtEosCol, BorderLayout.CENTER);
		JPanel dummy = new JPanel();
                tmp_left.add(dummy, BorderLayout.EAST);
                downPanel.add(tmp_left,     BorderLayout.WEST);

                JPanel SortAccTo = new JPanel(new GridLayout(2, 1));
                SortAccTo.add(new JLabel("Sort according to:"));
		String[] items = new String[7];
		items[0] = "Text Order";
		items[1] = "Left, Center";
		items[2] = "Left, Right";
		items[3] = "Center, Left";
		items[4] = "Center, Right";
		items[5] = "Right, Left";
		items[6] = "Right, Center";
		sortBox = new JComboBox(items);
		sortBox.setSelectedIndex(3);
		SortAccTo.add(sortBox);

		JPanel tmp_right = new JPanel(new GridLayout(2, 1, 0, 5));
                tmp_right.add(SortAccTo); 

		Action buildAction = new AbstractAction("Build concordance") {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						frame.buildConcordance();
					}
				});
			}
		};
		JButton buildConcordance = new JButton(buildAction);

		tmp_right.add(buildConcordance);

                downPanel.add(tmp_right,    BorderLayout.EAST);

		return downPanel;
	}

	void modifyText() {
		if (sntFile.getText().equals("")) {
			JOptionPane.showMessageDialog(null, "You must specify a text file",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		File snt;
		if (-1 == sntFile.getText().indexOf(File.separatorChar)) {
			// if the text field contains a file name without path,
			// we append the corpus path
			snt = new File(Config.getCurrentCorpusDir(),sntFile.getText());
		}
		else {
			snt = new File(sntFile.getText());
		}
		ConcordCommand command = new ConcordCommand();
		File indFile = new File(Config.getCurrentSntDir(), "concord.ind");
		if (!indFile.exists()) {
			JOptionPane.showMessageDialog(null, "Cannot find "
					+ indFile.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		command = command.indFile(indFile).outputModifiedTxtFile(snt);
		String sntDir = Util.getFileNameWithoutExtension(snt.getAbsolutePath())
				+ "_snt";
		File tmp = new File(sntDir);
		if (!tmp.exists())
			tmp.mkdir();
		ModifyTextDo DO = null;
		if (sntFile.equals(Config.getCurrentSnt())) {
			// if the result file is the current text, we must close some things
			UnitexFrame.mainFrame.closeText();
			DO = new ModifyTextDo(new File(sntFile.getText()));
		}
		MultiCommands commands = new MultiCommands();
		commands.addCommand(command);
		NormalizeCommand normalizeCmd = new NormalizeCommand().text(snt);
		commands.addCommand(normalizeCmd);
		TokenizeCommand tokenizeCmd = new TokenizeCommand().text(snt).alphabet();
		if (Config.isCharByCharLanguage()) {
			tokenizeCmd = tokenizeCmd.tokenizeCharByChar();
		}
		commands.addCommand(tokenizeCmd);
		setVisible(false);
		new ProcessInfoFrame(commands, true, DO);
	}

	void extractUnits(boolean matching) {
		if (extractFile.getText().equals("")) {
			JOptionPane.showMessageDialog(null, "You must specify a text file",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		ExtractCommand command = new ExtractCommand().extract(matching);
		File indFile = new File(Config.getCurrentSntDir(), "concord.ind");
		if (!indFile.exists()) {
			JOptionPane.showMessageDialog(null, "Cannot find "
					+ indFile.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		File result = new File(extractFile.getText());
		command = command.snt(Config.getCurrentSnt()).ind(indFile).result(
				result);
		String sntDir = Util.getFileNameWithoutExtension(result
				.getAbsolutePath())
				+ "_snt";
		File tmp = new File(sntDir);
		if (!tmp.exists())
			tmp.mkdir();
		setVisible(false);
		new ProcessInfoFrame(command, true, null);
	}

	void buildConcordance() {
        if (leftChars.getText().equals("")) {
			JOptionPane.showMessageDialog(null,
					"You must specify the left context length", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (rightChars.getText().equals("")) {
			JOptionPane.showMessageDialog(null,
					"You must specify the right context length", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		File indFile = new File(Config.getCurrentSntDir(), "concord.ind");
		if (!indFile.exists()) {
			JOptionPane.showMessageDialog(null, "Cannot find "
					+ indFile.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		ConcordCommand command;
		try {
			command =
                          new ConcordCommand()
                                .indFile(indFile)
                                .font(Preferences.getConcordanceFontName())
                                .fontSize(Preferences.getConcordanceFontSize())
                                .left(Integer.parseInt(leftChars.getText())
                                      + (leftCtxStopAtEOS.isSelected() ? "s" : ""))
                                .right(Integer.parseInt(rightChars.getText())
                                      + (rightCtxStopAtEOS.isSelected() ? "s" : ""))
                                .order(sortBox.getSelectedIndex())
                                .html()
                                .sortAlphabet()
                                .thai(Config.getCurrentLanguage().equals("Thai"));
		} catch (InvalidConcordanceOrderException e) {
			e.printStackTrace();
			return;
		}
		setVisible(false);
		int width = Integer.parseInt(leftChars.getText())
				+ Integer.parseInt(rightChars.getText());
		ConcordanceFrame.close();
		new ProcessInfoFrame(command, true, new ConcordanceDo(false,new File(Config
				.getCurrentSntDir(), "concord.html"), checkBox.isSelected(),
				width));
	}


	void buildDiffConcordance() {
		    File prevIndFile = new File(Config.getCurrentSntDir(), "previous-concord.ind");
			File indFile = new File(Config.getCurrentSntDir(), "concord.ind");
			if (!indFile.exists()) {
				JOptionPane.showMessageDialog(null, "Cannot find "
						+ indFile.getAbsolutePath(), "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			ConcorDiffCommand command;
			File outputHtmlFile=new File(Config
					.getCurrentSntDir(), "diff.html");
			command = new ConcorDiffCommand()
				          .firstIndFile(prevIndFile)
						  .secondIndFile(indFile)
						  .output(outputHtmlFile)
						  .font(Preferences.getConcordanceFontName())
						  .fontSize(Preferences.getConcordanceFontSize());
			setVisible(false);
			int width = 160;
	    new ProcessInfoFrame(command, true, new ConcordanceDo(true,outputHtmlFile, checkBox.isSelected(),
					width));
		}

	
	
	
	class ConcordanceDo extends ToDoAbstract {
		File htmlFile;
		final boolean browser;
		int widthInChars;
		boolean diff;

		public ConcordanceDo(File page) {
			htmlFile = page;
			browser = false;
			widthInChars = 95;
			diff=false;
		}
		public ConcordanceDo(boolean diff,File page, boolean br, int width) {
			htmlFile = page;
			browser = br;
			widthInChars = width;
			this.diff=diff;
			
		}
		public void toDo() {
			if (browser && Preferences.getCloneOfPreferences().htmlViewer != null) {
				new Thread() {
					public void run() {
						String[] s = new String[2];
						s[0] = Preferences.getCloneOfPreferences().htmlViewer.getAbsolutePath();
						s[1] = htmlFile.getAbsolutePath();
						Console.addCommand("\"" + s[0] + "\" \"" + s[1] + "\"");
						try {
							Runtime.getRuntime().exec(s);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}.start();
			} else {
				if (!diff) {
					ConcordanceFrame.load(htmlFile, widthInChars);
				} else {
					ConcordanceDiffFrame.load(htmlFile, widthInChars);
				}
			}
		}
	}

	static class ModifyTextDo extends ToDoAbstract {
		File SNT;

		public ModifyTextDo(File s) {
			SNT = s;
		}
		public void toDo() {
			Text.loadCorpus(SNT);
		}
	}

}