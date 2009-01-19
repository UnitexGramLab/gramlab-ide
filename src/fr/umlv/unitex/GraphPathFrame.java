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

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;

import fr.umlv.unitex.process.*;

/**
 * This class defines a frame that allows the user to show paths of a graph.
 * 
 * @author Sébastien Paumier
 * 11.11.2005 modified HyunGue HUH 
 */
public class GraphPathFrame extends JInternalFrame {

	static GraphPathFrame frame;
	BigTextList textArea = new BigTextList();
	JTextField graphName = new JTextField();
	JCheckBox limit;
	NumericTextField limitSize;
	JRadioButton ignoreOutputs;
	JRadioButton separateOutputs;
	JRadioButton mergeOutputs;
	JRadioButton bySousGraph;
	JRadioButton bySansGraph;

	private GraphPathFrame() {
		super("Explore graph paths", true, true);
		setContentPane(constructPanel());
		setBounds(100, 100, 420, 400);
		setVisible(true);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				frame.setVisible(false);
				frame.textArea.reset();
		        frame.textArea.clearSelection();
			}
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		GlobalPreferenceFrame.addTextFontListener(new FontListener() {
			public void fontChanged(Font font) {
				textArea.setFont(font);
			}});
	}

	private static void init() {
		frame = new GraphPathFrame();
		UnitexFrame.addInternalFrame(frame);
	}

	/**
	 * Shows the frame, creating it if necessary.
	 *  
	 */
	public static void showFrame() {
		GraphFrame gf = UnitexFrame.getCurrentFocusedGraphFrame();
		if (gf == null)
			return;
		if (frame == null) {
			init();
		}
		frame.graphName.setText(gf.getGraph().getAbsolutePath());
		frame.textArea.setFont(Config.getCurrentTextFont());
		frame.setVisible(true);
		try {
			frame.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
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
				if (frame.limit.isSelected()) {
					if (frame.limitSize.getText().equals("")) {
						JOptionPane.showMessageDialog(null,
								"You must specify a limit", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					cmd = cmd.limit(frame.limitSize.getText());
				} else {
					cmd = cmd.noLimit();
				}
				if (frame.ignoreOutputs.isSelected()) {
					cmd = cmd.ignoreOutputs();
				} else {
					cmd = cmd.separateOutputs(frame.separateOutputs
							.isSelected());
				}
//				Grf2Fst2Command grfCmd = new Grf2Fst2Command()
//					.grf(new File(frame.graphName.getText()))
//				.enableLoopAndRecursionDetection(true)
//						.tokenizationMode();
//				File fst2 = new File(Util.getFileNameWithoutExtension(frame.graphName
//						.getText()) + ".fst2");
//				File list = new File(Config.getUserCurrentLanguageDir(),"list.txt");
//				cmd = cmd.output(list);
//				cmd = cmd.fst2(fst2);
				grfCmd.grf(new File(frame.graphName.getText())).enableLoopAndRecursionDetection(true).library();
				fst2 = new File(Util.getFileNameWithoutExtension(frame.graphName
						.getText()) + ".fst2");			
				if(frame.bySansGraph.isSelected()){
					list = new File(Config.getUserCurrentLanguageDir(),"list.txt");
					
					cmd = cmd.listOfPaths(fst2,list);
				} else {
					list = new File(Util.getFileNameWithoutExtension(frame.graphName
							.getText()) + "autolst.txt");
					cmd = cmd.listsOfSubgraph(fst2);					
				}
				
				MultiCommands commands = new MultiCommands();
				commands.addCommand(grfCmd);
				commands.addCommand(cmd);
				frame.textArea.reset();
				new ProcessInfoFrame(commands, true, new ShowPathsDO(list));
			}
		};
		JButton GO = new JButton(goAction);
		buttons.add(GO);
		Action cancelAction = new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent arg0) {
				frame.setVisible(false);
			}
		};
		JButton CANCEL = new JButton(cancelAction);
		buttons.add(CANCEL);
		panel.add(buttons, BorderLayout.EAST);
		return panel;
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

	class ShowPathsDO extends ToDoAbstract {
		private File name;

		ShowPathsDO(File name) {
			this.name = name;
		}

		public void toDo() {
			try {
				frame.textArea.load(name);
				frame.setSelected(true);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
	}

}