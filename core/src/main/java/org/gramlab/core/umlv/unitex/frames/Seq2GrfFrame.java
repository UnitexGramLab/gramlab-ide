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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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

import org.gramlab.core.gramlab.util.KeyUtil;
import org.gramlab.core.umlv.unitex.LinkButton;
import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.files.PersonalFileFilter;
import org.gramlab.core.umlv.unitex.process.Launcher;
import org.gramlab.core.umlv.unitex.process.ToDo;
import org.gramlab.core.umlv.unitex.process.commands.Fst2TxtCommand;
import org.gramlab.core.umlv.unitex.process.commands.MultiCommands;
import org.gramlab.core.umlv.unitex.process.commands.NormalizeCommand;
import org.gramlab.core.umlv.unitex.process.commands.Seq2GrfCommand;
import org.gramlab.core.umlv.unitex.project.manager.UnitexProjectManager;

public class Seq2GrfFrame extends JInternalFrame {
	
	File corpus=null;
	File outputDir=ConfigManager.getManager().getCurrentGraphDirectory();
	JTextField textCorpus=new JTextField();
	JTextField textOutputDir=new JTextField();
	JCheckBox applyBeautify=new JCheckBox("Apply beautifying algorithm",true);
	JCheckBox exactCaseMatching=new JCheckBox("Exact case matching",true);
	
	SpinnerNumberModel totalModel=new SpinnerNumberModel(0,0,3,1);
	JSpinner spinnerTotal=new JSpinner(totalModel);
	JCheckBox checkboxReplace=new JCheckBox("Replace");
	JCheckBox checkboxDelete=new JCheckBox("Delete");
	JCheckBox checkboxInsert=new JCheckBox("Insert");
	
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
			totalModel.setValue(0);
		}
	}



	private JPanel constructMainPanel() {
		final JPanel p=new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		p.add(new JLabel("1) Choose your sequence corpus:"),gbc);
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
		p.add(new JLabel("2) Options:"),gbc);
		p.add(applyBeautify,gbc);
		p.add(exactCaseMatching,gbc);
		LinkButton advanced=new LinkButton("(Optional) approximate matching options \u25BC",true);
		JPanel p2=createHidablePane(advanced,"(Optional) approximate matching options \u25BC",
				"(Optional) approximate matching options \u25B2",
				createAdvancedPanel(),null);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		gbc.anchor=GridBagConstraints.WEST;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weightx=1;
		gbc.weighty=1;
		p2.add(advanced,BorderLayout.NORTH);
		p.add(p2,gbc);
		p.add(new JLabel(" "),gbc);

		p.add(new JLabel("3) Choose your output directory:"),gbc);
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
		p.add(new JLabel(" "),gbc);
		gbc.fill=GridBagConstraints.NONE;
		gbc.anchor=GridBagConstraints.CENTER;
		JButton go=new JButton("Create graph");
		go.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				constructSequenceAutomaton();
			}
		});
		p.add(go,gbc);
		KeyUtil.addCloseFrameListener(p);
		return p;
	}
	
	
	
	private JPanel createAdvancedPanel() {
		JPanel p=new JPanel(new GridLayout(1,2));
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		JPanel left=new JPanel(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.anchor=GridBagConstraints.WEST;
		left.add(spinnerTotal,gbc);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		left.add(new JLabel(" joker(s)"),gbc);
		p.add(left);
		JPanel right=new JPanel(null);
		right.setLayout(new BoxLayout(right,BoxLayout.Y_AXIS));
		right.add(new JLabel("Operations"));
		right.add(checkboxInsert);
		right.add(checkboxReplace);
		right.add(checkboxDelete);
		right.add(Box.createVerticalGlue());
		p.add(right);
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



	protected void constructSequenceAutomaton() {
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
		MultiCommands cmds=new MultiCommands();
		if (FileUtil.getExtensionInLowerCase(f).equals("xml")) {
			/* TEI files must receive a special treatment */
			File sequenceTEI=getSequenceTeiFst2();
			if (!sequenceTEI.exists()) {
				JOptionPane.showMessageDialog(null, "Graph "+sequenceTEI.getAbsolutePath()+" is missing.\n"+
						"No xml sequence file can be processed without it. If you have a SequenceTEI.grf file,\n"+
						"make sure you have compiled it properly.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			File replaceTEI=getReplaceTeiFst2();
			if (!replaceTEI.exists()) {
				JOptionPane.showMessageDialog(null, "Graph "+replaceTEI.getAbsolutePath()+" is missing.\n"+
						"No xml sequence file can be processed without it. If you have a ReplaceTEI.grf file,\n"+
						"make sure you have compiled it properly.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			File xml=f;
			String foo=FileUtil.getFileNameWithoutExtension(f);
			f=new File(foo+".snt");
			addTeiPreprocessingCommands(cmds,xml,f,sequenceTEI,replaceTEI);
		}
		if (textOutputDir.getText().equals("")) {
			JOptionPane.showMessageDialog(null, "You must specify an output directory!",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		File dir=new File(textOutputDir.getText());
		int nJokers=(Integer) totalModel.getValue();
		int nInsert=checkboxInsert.isSelected()?nJokers:0;
		int nReplace=checkboxReplace.isSelected()?nJokers:0;
		int nDelete=checkboxDelete.isSelected()?nJokers:0;
		String name="auto-";
		if (exactCaseMatching.isSelected()) {
			name=name+"ex-";
		}
		if (nJokers!=0) {
			name=name+FileUtil.getFileNameWithoutExtension(f.getName())+
					"_"+nJokers+nInsert+nReplace+nDelete+".grf";
		} else {
			name=name+FileUtil.getFileNameWithoutExtension(f.getName())+".grf";
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
		if (nJokers!=0) {
			cmd=cmd.wildcards(nJokers)
					.wildcardDelete(nDelete)
					.wildcardReplace(nReplace)
					.wildcardInsert(nInsert);
		}
		cmds.addCommand(cmd);
		Launcher.exec(cmds,true,new ToDo() {

			@Override
			public void toDo(boolean success) {
				if (!success) return;
				UnitexProjectManager.search(output).getFrameManagerAs(InternalFrameManager.class)
						.newGraphFrame(output);
			}
			
		});
	}

	private void addTeiPreprocessingCommands(MultiCommands cmds, File xml,
			File snt, File sequenceTEI, File replaceTEI) {
		NormalizeCommand norm=new NormalizeCommand().text(xml).noSeparatorNormalization();
		cmds.addCommand(norm);
		File alphabet=ConfigManager.getManager().getAlphabet(null);
		Fst2TxtCommand foo1=new Fst2TxtCommand().text(snt).fst2(sequenceTEI).mode(true).alphabet(alphabet);
		cmds.addCommand(foo1);
		Fst2TxtCommand foo2=new Fst2TxtCommand().text(snt).fst2(replaceTEI).mode(false).alphabet(alphabet);
		cmds.addCommand(foo2);
	}

	private File getSequenceTeiFst2() {
		File dir=new File(ConfigManager.getManager().getCurrentLanguageDir(),"Graphs");
		File preprocessing=new File(dir,"Preprocessing");
		File sentence=new File(preprocessing,"Sentence");
		return new File(sentence,"SequenceTEI.fst2");
	}
	
	private File getReplaceTeiFst2() {
		File dir=new File(ConfigManager.getManager().getCurrentLanguageDir(),"Graphs");
		File preprocessing=new File(dir,"Preprocessing");
		File sentence=new File(preprocessing,"Replace");
		return new File(sentence,"ReplaceTEI.fst2");
	}
	
}
