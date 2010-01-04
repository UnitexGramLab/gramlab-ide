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

package fr.umlv.unitex.xalign;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import fr.umlv.unitex.*;
import fr.umlv.unitex.process.*;


/**
 * This class describes the XAlign parameter frame.
 * 
 * @author Sébastien Paumier
 *  
 */
public class AlignmentParameterFrame extends JInternalFrame {

	static AlignmentParameterFrame frame;
	
	private AlignmentParameterFrame() {
		super("XAlign", false, true);
		setContentPane(constructPanel());
		pack();
		setLocation(250,200);
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
	 * Initializes the frame
	 *  
	 */
	private static void init() {
		frame = new AlignmentParameterFrame();
		UnitexFrame.addInternalFrame(frame,false);
	}

	/**
	 * Shows the frame
	 *  
	 */
	public static void showFrame() {
		if (frame == null) {
			init();
		}
		frame.setVisible(true);
		try {
			frame.setSelected(true);
			frame.setIcon(false);
		} catch (java.beans.PropertyVetoException e2) {
			e2.printStackTrace();
		}
	}

	
	private static JFileChooser textChooser() {
		JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new PersonalFileFilter(
				"xml", "TEI text"));
		chooser.addChoosableFileFilter(new PersonalFileFilter(
				"txt", "Raw text file"));
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setCurrentDirectory(Config.getUserDir());
		chooser.setMultiSelectionEnabled(false);
		return chooser;
	}
	
	public static JFileChooser alignmentChooser() {
		JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new PersonalFileFilter(
				"xml", "XAlign alignment file"));
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setCurrentDirectory(new File(Config.getUserDir(),"XAlign"));
		chooser.setMultiSelectionEnabled(false);
		return chooser;
	}
	
	public static JFileChooser saveXMLChooser() {
		JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new PersonalFileFilter(
				"xml", "TEI text"));
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setCurrentDirectory(Config.getUserDir());
		chooser.setMultiSelectionEnabled(false);
		return chooser;
	}
	
	final JTextField text1=new JTextField();//"D:\\My Unitex\\English\\Corpus\\LePetPrin.EngID.p5.xml");
	final JTextField text2=new JTextField();//"D:\\My Unitex\\French\\Corpus\\LePetPrin.FraID.p5.xml");
	final JTextField alignment=new JTextField();//"D:\\My Unitex\\XAlign\\foo.xml");
	
	private JPanel constructPanel() {
		JPanel panel = new JPanel(new GridLayout(4,1));
		panel.setOpaque(true);
		panel.add(buildPanel("Source text",text1,textChooser()));
		panel.add(buildPanel("Target text",text2,textChooser()));
		panel.add(buildPanel("Alignment file (optional)",alignment,alignmentChooser()));
		panel.add(buildButtonPanel());
		return panel;
	}

	private Component buildButtonPanel() {
		JPanel p=new JPanel(null);
		p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
		JButton ok=new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MultiCommands commands=new MultiCommands();
				String s=text1.getText();
				if ("".equals(s)) {
					JOptionPane.showMessageDialog(null,
							"You must set the source text", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				File source=new File(s);
				final File xmlSourceFile;
				if (!source.exists()) {
					JOptionPane.showMessageDialog(null,
							"Source text not found!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (source.getAbsolutePath().endsWith("txt")) {
					/* If the user wants to open a raw text file */
					JOptionPane.showMessageDialog(null,
							"Your source file is a .txt one. Please select the\n"+
							"destination file to be used by XAlign (TEI format).", "",
							JOptionPane.INFORMATION_MESSAGE);
					JFileChooser chooser=saveXMLChooser();
					int returnVal = chooser.showSaveDialog(null);
					if (returnVal != JFileChooser.APPROVE_OPTION) {
						// we return if the user has clicked on CANCEL
						return;
					}
					File xmlSource=chooser.getSelectedFile();
					if (!xmlSource.getAbsolutePath().endsWith(".xml")) {
						xmlSource=new File(xmlSource.getAbsolutePath()+".xml");
					}
					xmlSourceFile=xmlSource;
					File alphabet=XAlignFrame.tryToFindAlphabet(source);
					if (alphabet==null) {
						alphabet=XAlignFrame.tryToFindAlphabet(xmlSource);
					}
					if (alphabet==null) {
						JOptionPane.showMessageDialog(null,
								"Cannot determine the alphabet file to use\n"+
								"in order to process your text. You should place\n"+
								"your file within a language directory (e.g. English).", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					File sentence=new File(new File(Config.getXAlignDirectory(),"Sentence"),"SentenceXAlign.fst2");
					if (!sentence.exists()) {
						JOptionPane.showMessageDialog(null,
								"Cannot find the XAlign sentence graph.\n", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					XMLizerCommand cmd=new XMLizerCommand().output(xmlSource)
					    .alphabet(alphabet).sentence(sentence).input(source);
					commands.addCommand(cmd);
				} else {
					xmlSourceFile=source;
				}
				s=text2.getText();
				if ("".equals(s)) {
					JOptionPane.showMessageDialog(null,
							"You must set the target text", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				File dest=new File(s);
				final File xmlTargetFile;
				if (!dest.exists()) {
					JOptionPane.showMessageDialog(null,
							"Target text not found!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (dest.getAbsolutePath().endsWith("txt")) {
					/* If the user wants to open a raw text file */
					JOptionPane.showMessageDialog(null,
							"Your target file is a .txt one. Please select the\n"+
							"destination file to be used by XAlign (TEI format).", "",
							JOptionPane.INFORMATION_MESSAGE);
					JFileChooser chooser=saveXMLChooser();
					int returnVal = chooser.showSaveDialog(null);
					if (returnVal != JFileChooser.APPROVE_OPTION) {
						// we return if the user has clicked on CANCEL
						return;
					}
					File xmlTarget=chooser.getSelectedFile();
					xmlTargetFile=xmlTarget;
					if (!xmlTarget.getAbsolutePath().endsWith(".xml")) {
						xmlTarget=new File(xmlTarget.getAbsolutePath()+".xml");
					}
					File alphabet=XAlignFrame.tryToFindAlphabet(dest);
					if (alphabet==null) {
						alphabet=XAlignFrame.tryToFindAlphabet(xmlTarget);
					}
					if (alphabet==null) {
						JOptionPane.showMessageDialog(null,
								"Cannot determine the alphabet file to use\n"+
								"in order to process your text. You should place\n"+
								"your file within a language directory (e.g. English).", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					File sentence=new File(new File(Config.getXAlignDirectory(),"Sentence"),"SentenceXAlign.fst2");
					if (!sentence.exists()) {
						JOptionPane.showMessageDialog(null,
								"Cannot find the XAlign sentence graph.\n", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					XMLizerCommand cmd=new XMLizerCommand().output(xmlTarget)
					    .alphabet(alphabet).sentence(sentence).input(dest);
					commands.addCommand(cmd);
				} else {
					xmlTargetFile=dest;
				}
				File alignmentFile=null;
				s=alignment.getText();
				if (!"".equals(s)) {
					alignmentFile=new File(s);
					if (!alignmentFile.exists()) {
						JOptionPane.showMessageDialog(null,
								"Alignment file not found!", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				final File alignmentFile2=alignmentFile;
				/* We close the parameter frame */
				setVisible(false);
				/* We close the XAlign frame */
				if (XAlignFrame.frame != null) {
					try {
						XAlignFrame.frame.setClosed(true);
					} catch (PropertyVetoException e1) {
						e1.printStackTrace();
					}
					UnitexFrame
							.removeInternalFrame(XAlignFrame.frame);
					XAlignFrame.frame = null;
					System.gc();
				}
				ToDoAbstract toDo = new ToDoAbstract() {
					@Override
					public void toDo() {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								try {
									JInternalFrame frame1 = XAlignFrame
											.buildAlignFrame(xmlSourceFile,xmlTargetFile,
													alignmentFile2);
									frame1.setVisible(true);
									UnitexFrame.addInternalFrame(frame1,true);
									frame1.setSelected(true);
								} catch (IOException e1) {
									JOptionPane
											.showMessageDialog(
													null,
													"I/O error while loading alignment files",
													"Error",
													JOptionPane.ERROR_MESSAGE);
									return;
								} catch (PropertyVetoException e1) {
									e1.printStackTrace();
								}
							}
						});
					}
				};
				/* And we launch the XMLizer commands, if any */
				if (commands.numberOfCommands()!=0) {
					new ProcessInfoFrame(commands,true,toDo,true);
				} else {
					toDo.toDo();
				}
		}});
		JButton cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);				
		}});
		p.add(Box.createHorizontalGlue());
		p.add(ok);
		p.add(Box.createHorizontalStrut(3));
		p.add(cancel);
		p.add(Box.createHorizontalStrut(5));
		return p;
	}

	private JPanel buildPanel(String s,final JTextField text,final JFileChooser chooser) {
		JPanel p=new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createTitledBorder(s));
		text.setPreferredSize(new Dimension(300,25));
		p.add(text,BorderLayout.CENTER);
		JButton button=new JButton("set");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						int returnVal = chooser.showOpenDialog(null);
						if (returnVal != JFileChooser.APPROVE_OPTION) {
							// we return if the user has clicked on CANCEL
							return;
						}
						text.setText(chooser.getSelectedFile().getAbsolutePath());
					}});
			}});
		p.add(button,BorderLayout.EAST);
		return p;
	}


}