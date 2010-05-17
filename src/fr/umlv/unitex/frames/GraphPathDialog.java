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

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import fr.umlv.unitex.BigTextList;
import fr.umlv.unitex.Config;
import fr.umlv.unitex.FontListener;
import fr.umlv.unitex.NumericTextField;
import fr.umlv.unitex.ToDo;
import fr.umlv.unitex.Util;
import fr.umlv.unitex.process.*;

/**
 * This class defines a frame that allows the user to show paths of a graph.
 * 
 * @author Sébastien Paumier
 * 11.11.2005 modified HyunGue HUH 
 */
public class GraphPathDialog extends JDialog {

	BigTextList textArea = new BigTextList();
	JTextField graphName = new JTextField();
	JCheckBox limit;
	NumericTextField limitSize;
	JRadioButton ignoreOutputs;
	JRadioButton separateOutputs;
	JRadioButton mergeOutputs;
	JRadioButton bySousGraph;
	JRadioButton bySansGraph;

	GraphPathDialog() {
		super(UnitexFrame.mainFrame,"Explore graph paths", true);
		setContentPane(constructPanel());
		setBounds(100, 100, 420, 400);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		textArea.setFont(Config.getCurrentTextFont());
		UnitexFrame.getFrameManager().getGlobalPreferencesFrame()
				.addTextFontListener(new FontListener() {
					public void fontChanged(Font font) {
						textArea.setFont(font);
					}
				});
		setLocationRelativeTo(UnitexFrame.mainFrame);
	}


	private JPanel constructPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(constructTopPanel(), BorderLayout.NORTH);
		panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
		return panel;
	}

	private JPanel constructTopPanel() {
		JPanel top = new JPanel(new GridLayout(6, 1));
		top.add(constructGraphNamePanel());
		ButtonGroup bg = new ButtonGroup();
		ignoreOutputs = new JRadioButton("Ignore outputs", true);
		separateOutputs = new JRadioButton("Separate inputs and outputs");
		mergeOutputs = new JRadioButton("Merge inputs and outputs");
		bg.add(ignoreOutputs);
		bg.add(separateOutputs);
		bg.add(mergeOutputs);
		top.add(ignoreOutputs);
		top.add(separateOutputs);
		top.add(mergeOutputs);
		top.add(constructDownPanel());
		
		JPanel top1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		ButtonGroup pathWithSubGraph = new ButtonGroup();
		bySansGraph = new JRadioButton("Only paths", true);
		bySousGraph = new JRadioButton("Do not explore subgraphs recursively");
		pathWithSubGraph.add(bySansGraph);
		pathWithSubGraph.add(bySousGraph);
		top1.add(bySansGraph);
		top1.add(bySousGraph);		
		top.add(top1);
		return top;
	}

	private JPanel constructDownPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(constructLimitPanel(), BorderLayout.CENTER);
		JPanel buttons = new JPanel(new GridLayout(1, 2));
		Action goAction = new AbstractAction("GO") {
			public void actionPerformed(ActionEvent arg0) {
				Fst2ListCommand cmd = new Fst2ListCommand();
				Grf2Fst2Command grfCmd = new Grf2Fst2Command();
				File fst2;
				File list; //output file name
				if (limit.isSelected()) {
					if (limitSize.getText().equals("")) {
						JOptionPane.showMessageDialog(null,
								"You must specify a limit", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					cmd = cmd.limit(limitSize.getText());
				} else {
					cmd = cmd.noLimit();
				}
				if (ignoreOutputs.isSelected()) {
					cmd = cmd.ignoreOutputs();
				} else {
					cmd = cmd.separateOutputs(separateOutputs
							.isSelected());
				}
				grfCmd.grf(new File(graphName.getText())).enableLoopAndRecursionDetection(true).library();
				fst2 = new File(Util.getFileNameWithoutExtension(graphName
						.getText()) + ".fst2");			
				if(bySansGraph.isSelected()){
					list = new File(Config.getUserCurrentLanguageDir(),"list.txt");
					
					cmd = cmd.listOfPaths(fst2,list);
				} else {
					list = new File(Util.getFileNameWithoutExtension(graphName
							.getText()) + "autolst.txt");
					cmd = cmd.listsOfSubgraph(fst2);					
				}
				
				MultiCommands commands = new MultiCommands();
				commands.addCommand(grfCmd);
				commands.addCommand(cmd);
				textArea.reset();
				new ProcessInfoFrame(commands, true, new ShowPathsDo(list));
			}
		};
		JButton GO = new JButton(goAction);
		buttons.add(GO);
		Action cancelAction = new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent arg0) {
				/* TODO remplacer les setVisible(false) des cancel par des
				 * doDefaultCloseAction()
				 */
				close();
			}
		};
		JButton CANCEL = new JButton(cancelAction);
		buttons.add(CANCEL);
		panel.add(buttons, BorderLayout.EAST);
		return panel;
	}

	protected void close() {
		setVisible(false);
		textArea.reset();
		textArea.clearSelection();
	}


	private JPanel constructLimitPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		limit = new JCheckBox("Maximum number of sequences: ", true);
		limitSize = new NumericTextField("100");
		limitSize.setPreferredSize(new Dimension(50, 20));
		panel.add(limit, BorderLayout.WEST);
		panel.add(limitSize, BorderLayout.CENTER);
		panel.add(new JLabel("   "), BorderLayout.EAST);
		return panel;
	}

	private JPanel constructGraphNamePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(" Graph: "), BorderLayout.WEST);
		panel.add(graphName, BorderLayout.CENTER);
		return panel;
	}

	class ShowPathsDo implements ToDo {
		private File name;

		ShowPathsDo(File name) {
			this.name = name;
		}

		public void toDo() {
			textArea.load(name);
		}
	}

}