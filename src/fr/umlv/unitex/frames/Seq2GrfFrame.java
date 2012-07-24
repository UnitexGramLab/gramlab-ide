/*
 * Unitex
 *
 * Copyright (C) 2001-2012 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.umlv.unitex.LinkButton;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.files.PersonalFileFilter;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.Seq2GrfCommand;

public class Seq2GrfFrame extends JInternalFrame {
	
	File corpus=null;
	File outputDir=ConfigManager.getManager().getCurrentGraphDirectory();
	JTextField textCorpus=new JTextField();
	JTextField textOutputDir=new JTextField();
	JCheckBox applyBeautify=new JCheckBox("Apply beautifying algorithm",true);
	JCheckBox exactCaseMatching=new JCheckBox("Exact case matching",true);
	
	SpinnerNumberModel totalModel=new SpinnerNumberModel(1,1,3,1);
	SpinnerNumberModel replaceModel=new SpinnerNumberModel(1,0,1,1);
	SpinnerNumberModel insertModel=new SpinnerNumberModel(1,0,1,1);
	SpinnerNumberModel deleteModel=new SpinnerNumberModel(1,0,1,1);
	JSpinner spinnerTotal=new JSpinner(totalModel);
	JSpinner spinnerReplace=new JSpinner(replaceModel);
	JSpinner spinnerInsert=new JSpinner(insertModel);
	JSpinner spinnerDelete=new JSpinner(deleteModel);
	
	Seq2GrfFrame(File corpus) {
		super("Construct sequence automaton", true,true);
		setContentPane(constructMainPanel());
		pack();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setCorpus(corpus);
	}
	
	Seq2GrfFrame() {
		this(null);
	}
	
	void setCorpus(File corpus) {
		if (corpus==null || !corpus.equals(this.corpus)) {
			this.corpus=corpus;
			if (corpus==null) {
				textCorpus.setText("");
			} else {
				textCorpus.setText(corpus.getAbsolutePath());
			}
			totalModel.setValue(1); totalModel.setMaximum(3);
			replaceModel.setValue(1); replaceModel.setMaximum(1);
			deleteModel.setValue(1); deleteModel.setMaximum(1);
			insertModel.setValue(1); insertModel.setMaximum(1);
		}
	}



	private JPanel constructMainPanel() {
		final JPanel p=new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(new JLabel("Choose your sequence corpus:"),gbc);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.RELATIVE;
		p.add(textCorpus,gbc);
		JButton setCorpus=new JButton("Set...");
		setCorpus.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				File dir=new File(ConfigManager.getManager().getCurrentLanguageDir(),"Corpus");
				if (corpus!=null) {
					dir=corpus.getParentFile();
				}
				JFileChooser jfc = new JFileChooser(dir);
				jfc.addChoosableFileFilter(new PersonalFileFilter("snt",
						"Preprocessed texts"));
				jfc.addChoosableFileFilter(new PersonalFileFilter("txt",
						"Raw texts"));
				jfc.addChoosableFileFilter(new PersonalFileFilter("xml",
						"TEI encoded texts"));
				jfc.setFileFilter(jfc.getAcceptAllFileFilter());
				jfc.setDialogType(JFileChooser.OPEN_DIALOG);
				jfc.setMultiSelectionEnabled(false);
				final int returnVal = jfc.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				setCorpus(jfc.getSelectedFile());
			}
		});
		gbc.weightx=0;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(setCorpus,gbc);
		p.add(new JLabel(" "),gbc);
		p.add(new JLabel("Choose your output directory:"),gbc);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.RELATIVE;
		textOutputDir.setText((outputDir==null)?"":outputDir.getAbsolutePath());
		p.add(textOutputDir,gbc);
		JButton setDir=new JButton("Set...");
		gbc.weightx=0;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		setDir.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				File dir=new File(ConfigManager.getManager().getCurrentLanguageDir(),"Graphs");
				if (outputDir!=null) {
					dir=outputDir;
				}
				JFileChooser jfc = new JFileChooser(dir);
				jfc.setDialogType(JFileChooser.OPEN_DIALOG);
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				final int returnVal = jfc.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					return;
				}
				outputDir=jfc.getSelectedFile();
				textOutputDir.setText(outputDir.getAbsolutePath());
			}
		});
		p.add(setDir,gbc);
		p.add(applyBeautify,gbc);
		p.add(exactCaseMatching,gbc);
		gbc.fill=GridBagConstraints.NONE;
		gbc.anchor=GridBagConstraints.EAST;
		JButton go1=new JButton("Build exact sequence automaton");
		go1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				constructSequenceAutomaton(false);
			}
		});
		p.add(go1,gbc);
		LinkButton advanced=new LinkButton("Show advanced options \u25BC",true);
		JPanel p2=createHidablePane(advanced,"Show advanced options \u25BC","Hide advanced options \u25B2",
				createAdvancedPanel(),null);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.anchor=GridBagConstraints.WEST;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.weighty=1;
		p2.add(advanced,BorderLayout.NORTH);
		p.add(p2,gbc);
		return p;
	}
	
	
	
	private JPanel createAdvancedPanel() {
		JPanel p=new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.anchor=GridBagConstraints.WEST;
		gbc.weightx=0;
		gbc.gridwidth=1;
		p.add(new JLabel("Maximum number of wilcards: "),gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.weightx=1;
		p.add(spinnerTotal,gbc);
		gbc.weightx=0;
		gbc.gridwidth=1;
		p.add(new JLabel("Maximum number of insertions: "),gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.weightx=1;
		p.add(spinnerInsert,gbc);
		gbc.weightx=0;
		gbc.gridwidth=1;
		p.add(new JLabel("Maximum number of deletions: "),gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.weightx=1;
		p.add(spinnerDelete,gbc);
		gbc.weightx=0;
		gbc.gridwidth=1;
		p.add(new JLabel("Maximum number of replacements: "),gbc);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.weightx=1;
		p.add(spinnerReplace,gbc);
		spinnerTotal.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int max=(Integer) totalModel.getValue();
				int v=(Integer) replaceModel.getValue();
				replaceModel.setMaximum(max);
				if (v>max) {
					replaceModel.setValue(max);
				}
				v=(Integer) deleteModel.getValue();
				deleteModel.setMaximum(max);
				if (v>max) {
					deleteModel.setValue(max);
				}
				v=(Integer) insertModel.getValue();
				insertModel.setMaximum(max);
				if (v>max) {
					insertModel.setValue(max);
				}
			}
			
		});
		final JButton go2=new JButton("Build approximate sequence automaton");
		go2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				constructSequenceAutomaton(true);
			}
		});
		ChangeListener cl=new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int n=((Integer)replaceModel.getValue())+
						((Integer)insertModel.getValue())+
						((Integer)deleteModel.getValue());
				go2.setEnabled(n!=0);
			}
		};
		spinnerDelete.addChangeListener(cl);
		spinnerReplace.addChangeListener(cl);
		spinnerInsert.addChangeListener(cl);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.anchor=GridBagConstraints.EAST;
		gbc.fill=GridBagConstraints.NONE;
		p.add(go2,gbc);
		gbc.weighty=1;
		p.add(new JLabel(" "),gbc);
		return p;
	}

	public JPanel createHidablePane(final JButton showHide,final String showCaption,
			final String hideCaption,final JPanel pane,final Runnable onHide) {
		final JPanel p=new JPanel(new BorderLayout());
		showHide.setText(showCaption);
		pane.setVisible(false);
		showHide.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!pane.isVisible()) {
					/* We have to show the panel */
					pane.setVisible(true);
					p.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
					showHide.setText(hideCaption);
				} else {
					pane.setVisible(false);
					p.setBorder(null);
					showHide.setText(showCaption);
					if (onHide!=null) {
						onHide.run();
					}
				}
				p.revalidate();
				p.repaint();
				Seq2GrfFrame.this.pack();
			}
		});
		p.add(pane,BorderLayout.CENTER);
		return p;
	}



	protected void constructSequenceAutomaton(boolean approximate) {
		if (textCorpus.getText().equals("")) {
			JOptionPane.showMessageDialog(null, "You must specify a corpus!",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		File f=new File(textCorpus.getText());
		if (!f.exists()) {
			JOptionPane.showMessageDialog(null, "File "+corpus.getAbsolutePath()+" does not exist!",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (textOutputDir.getText().equals("")) {
			JOptionPane.showMessageDialog(null, "You must specify an output directory!",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		File dir=new File(textOutputDir.getText());
		String name;
		if (approximate) {
			name="seq2grf-"+FileUtil.getFileNameWithoutExtension(f.getName())+
					"_"+
					totalModel.getValue()+
					insertModel.getValue()+
					deleteModel.getValue()+
					replaceModel.getValue()+
					".grf";
		} else {
			name="seq2grf-"+FileUtil.getFileNameWithoutExtension(f.getName())+".grf";
		}
		final File output=new File(dir,name);
		if (output.exists()) {
			final String[] options = { "No", "Yes" };
			final int n = JOptionPane.showOptionDialog(this,
					"File "+output.getAbsolutePath()+" already exist and will be overwritten.\n"+
							"\nDo you want to continue ?", "", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
			if (n == 0) {
				return;
			}
		}
		Seq2GrfCommand cmd=new Seq2GrfCommand().text(f).output(output)
				.alphabet(ConfigManager.getManager().getAlphabet(null))
				.applyBeautify(applyBeautify.isSelected())
				.exactCaseMatching(exactCaseMatching.isSelected());
		if (approximate) {
			cmd=cmd.wildcards((Integer) totalModel.getValue())
					.wildcardDelete((Integer) deleteModel.getValue())
					.wildcardReplace((Integer) replaceModel.getValue())
					.wildcardInsert((Integer) insertModel.getValue());
		}
		Launcher.exec(cmd,true,new ToDo() {

			@Override
			public void toDo(boolean success) {
				if (!success) return;
				InternalFrameManager.getManager(output).newGraphFrame(output);
			}
			
		});
	}
	

	
}
