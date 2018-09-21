/*
 * Unitex
 *
 * Copyright (C) 2001-2018 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.config.PreferencesListener;
import fr.umlv.unitex.config.PreferencesManager;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.FlattenCommand;
import fr.umlv.unitex.process.commands.Fst2ListCommand;
import fr.umlv.unitex.process.commands.Grf2Fst2Command;
import fr.umlv.unitex.process.commands.MultiCommands;
import fr.umlv.unitex.text.BigTextList;
import fr.umlv.unitex.utils.KeyUtil;
import fr.umlv.unitex.frames.UnitexFrame;

/**
 * This class defines a frame that allows the user to show paths of a graph.
 * 
 * @author Sébastien Paumier 11.11.2005 modified HyunGue HUH
 */
public class GraphPathFrame extends JInternalFrame {
	final BigTextList textArea = new BigTextList();
	final JTextField graphName = new JTextField();
	final JTextField outputFileName = new JTextField();
	JCheckBox limit;
	JCheckBox flattenGraph;
	JTextField limitSize;
	JRadioButton ignoreOutputs;
	JRadioButton separateOutputs;
	JRadioButton mergeOutputs;
	JRadioButton exploreRecursively;
	JRadioButton onlyPaths;
	MultiCommands preprocessCommands;
	// default values for the flatten command
	Boolean flattenMode = false;
	String flattenDepth = "10";
	
	ListDataListener listListener = new ListDataListener() {
		@Override
		public void intervalRemoved(ListDataEvent e) {
			/* */
		}

		@Override
		public void intervalAdded(ListDataEvent e) {
			final int n = textArea.getModel().getSize();
			setTitle(n + " line" + (n > 1 ? "s" : ""));
		}

		@Override
		public void contentsChanged(ListDataEvent e) {
			/* */
		}
	};

	GraphPathFrame() {
		super("Explore graph paths", true,true);
		setContentPane(constructPanel());
		pack();
		graphName.setEditable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		textArea.setFont(ConfigManager.getManager().getTextFont(null));
		PreferencesManager.addPreferencesListener(new PreferencesListener() {
			@Override
			public void preferencesChanged(String language) {
				textArea.setFont(ConfigManager.getManager().getTextFont(null));
			}
		});
	}

	private JPanel constructPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(constructTopPanel(), BorderLayout.NORTH);
		panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
		return panel;
	}

	private JPanel constructTopPanel() {
		final JPanel top = new JPanel(new GridLayout(8, 1));
		top.add(constructGraphNamePanel());
		top.add(constructListFileNamePanel());
		final ButtonGroup bg = new ButtonGroup();
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
		final JPanel top1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JPanel flattenPanel = new JPanel(new BorderLayout());
		final ButtonGroup pathWithSubGraph = new ButtonGroup();
		final JLabel explorationLabel = new JLabel("Explore subgraphs: ");
		onlyPaths = new JRadioButton("Recursively", true);
		exploreRecursively = new JRadioButton(
				"Independently, printing names of called subgraphs");
		
		// issue #61 add listeners to change default output file name based on user selection
		onlyPaths.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				outputFileName.setText(FileUtil.getFileNameWithoutExtension(graphName
						.getText()) + "-recursive-paths.txt");
			}
		});
		exploreRecursively.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				outputFileName.setText(FileUtil.getFileNameWithoutExtension(graphName
						.getText()) + "-paths.txt");
			}
		});
		
		pathWithSubGraph.add(onlyPaths);
		pathWithSubGraph.add(exploreRecursively);
		top1.add(explorationLabel);
		top1.add(onlyPaths);
		top1.add(exploreRecursively);
		flattenPanel.add(constructFlattenGraphPanel(),BorderLayout.WEST);
		top.add(flattenPanel);
		top.add(top1);
		return top;
	}

	private JPanel constructDownPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(constructLimitPanel(), BorderLayout.CENTER);
		final JPanel buttons = new JPanel(new GridLayout(1, 2));
		final Action goAction = new AbstractAction("GO") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Fst2ListCommand cmd = new Fst2ListCommand();
				final Grf2Fst2Command grfCmd = new Grf2Fst2Command();
				File fst2;
				File list; /* output file name */
				int n;
				if (limit.isSelected()) {
					try {
						n = Integer.parseInt(limitSize.getText());
					} catch (final NumberFormatException e) {
						JOptionPane.showMessageDialog(null,
								"You must specify a valid limit", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					cmd = cmd.limit(n);
				} else {
					cmd = cmd.noLimit();
				}
				if (ignoreOutputs.isSelected()) {
					cmd = cmd.ignoreOutputs();
				} else {
					cmd = cmd.separateOutputs(separateOutputs.isSelected());
				}
				// check if flatten was checked or not
				if( !flattenGraph.isSelected() ) {
					grfCmd.grf(new File(graphName.getText()))
						.enableLoopAndRecursionDetection(true).repositories()
						.emitEmptyGraphWarning().displayGraphNames();
				} else if ( preprocessCommands == null ) {
					// if no specific option were given, preprocess with default
					File graphFile = new File(graphName.getText());
					String name_fst2 = FileUtil.getFileNameWithoutExtension(graphFile);
					name_fst2 = name_fst2 + ".fst2";
					final MultiCommands commands = new MultiCommands();
					commands.addCommand(new Grf2Fst2Command().grf(graphFile)
							.enableLoopAndRecursionDetection(true)
							.tokenizationMode(null, graphFile).repositories()
							.emitEmptyGraphWarning().displayGraphNames());
					commands.addCommand(new FlattenCommand().fst2(new File(name_fst2))
							.resultType(flattenMode).depth(Integer.parseInt(flattenDepth)));
				}
				else {
					Launcher.exec(preprocessCommands, false);
				}
				
				fst2 = new File(FileUtil.getFileNameWithoutExtension(graphName
						.getText()) + ".fst2");
				if (onlyPaths.isSelected()) {
					// set file to user input
					list = new File(outputFileName.getText());
					cmd = cmd.listOfPaths(fst2, list);
				} else {
					// we can't set non recursive file name to user selection yet because the name is hard coded in UnitexToolLogger (Fst2List.cpp line 1230)
					// if we change it here ShowPathsDo will throw a FileNotFoundException 
					// we will rename the file once the UnitexToolLogger process has completed
					// alternatively that process could be changed to remove the hard coding
					list = new File(
							FileUtil.getFileNameWithoutExtension(graphName
									.getText()) + "autolst.txt");
					cmd = cmd.listsOfSubgraph(fst2);
				}
				final MultiCommands commands = new MultiCommands();
				if ( !flattenGraph.isSelected() ) {
					commands.addCommand(grfCmd);
				}
				commands.addCommand(cmd);
				textArea.reset();
				Launcher.exec(commands, true, new ShowPathsDo(list), false,true);
			}
		};
		final JButton GO = new JButton(goAction);
		buttons.add(GO);
		final Action cancelAction = new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				close();
			}
		};
		final JButton CANCEL = new JButton(cancelAction);
		buttons.add(CANCEL);
		panel.add(buttons, BorderLayout.EAST);
		KeyUtil.addCRListener(GO);
		KeyUtil.addCRListener(CANCEL);
		KeyUtil.addEscListener(panel, CANCEL);
		return panel;
	}
	
	@Override
	public void dispose() {
		textArea.reset();
		textArea.clearSelection();
		textArea.getModel().removeListDataListener(listListener);
	}
	
	void close() {
		setVisible(false);
		textArea.reset();
		textArea.clearSelection();
		textArea.getModel().removeListDataListener(listListener);
	}

	private JPanel constructLimitPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		limit = new JCheckBox("Maximum number of sequences: ", true);
		limitSize = new JTextField("100");
		limitSize.setPreferredSize(new Dimension(50, 20));
		panel.add(limit, BorderLayout.WEST);
		panel.add(limitSize, BorderLayout.CENTER);
		panel.add(new JLabel("   "), BorderLayout.EAST);
		return panel;
	}
	
	private JPanel constructFlattenGraphPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		flattenGraph = new JCheckBox("Flatten graphs ");
		
		panel.add(flattenGraph, BorderLayout.WEST);
		// add space to align option button and limit text area
		panel.add(new JLabel("                           "));
		
		final JPanel button = new JPanel(new GridLayout(1, 1));
		
		final Action flattenOptionAction = new AbstractAction("Flattening options") {
			@Override
			public void actionPerformed(ActionEvent e) {
				File graphFile = new File(graphName.getText());
				Map<String,Object> flattenOptions = UnitexFrame
						.flattenGraph(graphFile,flattenMode,flattenDepth);
				if( flattenOptions != null ) {
				// unpack the commands and its options
					preprocessCommands = (MultiCommands) flattenOptions.get("commands");
					flattenMode = (boolean) flattenOptions.get("flattenMode");
					flattenDepth = (String) flattenOptions.get("flattenDepth");
				}
			}
		};
		final JButton flattenOption = new JButton(flattenOptionAction);
		flattenOption.setEnabled(false);
		
		final ItemListener flattenCheckBox = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if ( flattenGraph.isSelected() ) {
					flattenOption.setEnabled(true);
				} else {
					flattenOption.setEnabled(false);
				}
			}
		};
		
		flattenGraph.addItemListener(flattenCheckBox);
		button.add(flattenOption);
		panel.add(button, BorderLayout.EAST);
		
		return panel;
	}

	private JPanel constructGraphNamePanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel("       Graph: "), BorderLayout.WEST);
		panel.add(graphName, BorderLayout.CENTER);
		return panel;
	}
	
	/**
	 * Constructs panel with list file output location and browse button to allow user to select other file/location
	 * issue #61
	 */
	private JPanel constructListFileNamePanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(" Output file: "), BorderLayout.WEST);
		panel.add(outputFileName, BorderLayout.CENTER);
		
		
		final JPanel button = new JPanel(new GridLayout(1, 1));
		
		final Action setFileAction = new AbstractAction("Set File") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openOutputFile();
			}
		};
		final JButton setFile = new JButton(setFileAction);
		button.add(setFile);
		panel.add(button, BorderLayout.EAST);
		
		return panel;
	}
	
	private void openOutputFile() {
		final int returnVal = Config.getExploreGraphOutputDialogBox().showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		final String name;
		try {
			name = Config.getExploreGraphOutputDialogBox().getSelectedFile()
					.getCanonicalPath();
		} catch (final IOException e) {
			return;
		}
		outputFileName.setText(name);
	}
	
	public void setOutputFileDefaultName(String graphFileName) {
		outputFileName.setText(FileUtil.getFileNameWithoutExtension(graphFileName) + "-recursive-paths.txt");
	}


	class ShowPathsDo implements ToDo {
		private final File name;

		ShowPathsDo(File name) {
			this.name = name;
		}

		@Override
		public void toDo(boolean success) {
			textArea.load(name);
			textArea.getModel().addListDataListener(listListener);
			
			try {
				// issue #61 - recursive path option invokes UnitexToolLogger which hard codes the name of the output file to GraphNameautolst.txt 
				// once that process has completed and loaded the file rename it using the user input if that differs from the default
				if (!name.getAbsolutePath().equals(outputFileName.getText())) {
					File dest = new File(outputFileName.getText());
					Files.move(name.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
			}  catch (final IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Could not save path list to " + outputFileName.getText(), "Error",
						JOptionPane.ERROR_MESSAGE);
			} 
		}
	}
}
