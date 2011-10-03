/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.Grf2Fst2Command;
import fr.umlv.unitex.process.commands.Seq2GrfCommand;
import fr.umlv.unitex.process.commands.LocateCommand;
import fr.umlv.unitex.process.commands.MessageCommand;
import fr.umlv.unitex.process.commands.MkdirCommand;
import fr.umlv.unitex.process.commands.MultiCommands;
import fr.umlv.unitex.process.commands.ReconstrucaoCommand;
import fr.umlv.unitex.process.commands.TaggerCommand;
//import fr.umlv.unitex.process.commands.Txt2TfstCommand;

/**
 * This class describes the "Construct Text FST" frame that offers to the user
 * to build the text automaton.
 *
 * @author Sébastien Paumier
 */
public class ConstructSeqTfstFrame extends JInternalFrame {


    //private final JTextField SNTfile = new JTextField();
    private final JTextField GRFfile = new JTextField();
    private final JTextField alphFile = new JTextField();
    
    /**
     * Creates and shows a new <code>ConstructSeqFstFrame</code>.
     */
    ConstructSeqTfstFrame() {
        super("Construct the Seq FST", false);
        setContentPane(constructPanel());
        pack();
        setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    private JPanel constructPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(textPanel(),BorderLayout.CENTER);
        JPanel FilePanel = new JPanel(new BorderLayout());
        //midPanel.add(alphabetPanel(),BorderLayout.NORTH);
        FilePanel.add(outputFilePanel(),BorderLayout.SOUTH);
        panel.add(FilePanel,BorderLayout.NORTH);
        panel.add(constructButtonsPanel(), BorderLayout.SOUTH);
        return panel;
    }
    private JPanel constructButtonsPanel() {
        JPanel buttons = new JPanel(new GridLayout(1, 2));
        buttons.setBorder(new EmptyBorder(6, 6, 2, 2));
        Action okAction = new AbstractAction("Construct FST") {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
                constructSeqTfst();
            }
        };
        Action cancelAction = new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
            }
        };
        JButton OK = new JButton(okAction);
        JButton CANCEL = new JButton(cancelAction);
        buttons.add(CANCEL);
        buttons.add(OK);
        return buttons;
    }
    private JPanel textPanel(){
    	JPanel textP = new JPanel();
    	textP.setBorder(new TitledBorder("Name the output GRF File"));
    	textP.add(new JLabel("The programm will construct the automata that regognizes all theses sequences."));
    	
    	return textP;
    }
    private JPanel alphabetPanel(){
    	JPanel alphabetPanel = new JPanel(new GridLayout(1,3));
    	alphabetPanel.setBorder(new EmptyBorder(8,8,1,1));
    	alphabetPanel.add(new JLabel("choose your alphabet"));
    	String aFile =ConfigManager.getManager().getAlphabet(null).getAbsolutePath();
    	System.out.println("alphabetFile : "+aFile);
    	alphFile.setEditable(true);
    	alphFile.setText(aFile);
    	alphabetPanel.add(alphFile, BorderLayout.LINE_START);
    	Action alphabetAction = new AbstractAction("Choose your Alphabet") {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = Config.getNormDialogBox();
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    // we return if the user has clicked on CANCEL
                    return;
                }
                alphFile.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		};
    	JButton setButton = new JButton(alphabetAction);
    	alphabetPanel.add(setButton);
    	return alphabetPanel;
    }
    private JPanel outputFilePanel(){
    	JPanel fileP = new JPanel(new FlowLayout());
    	String sntName = Config.getCurrentSnt().getName();
    	String grfFile =Config.getCurrentSntDir().getPath()
    			+File.separatorChar
    			+sntName.substring(0,sntName.length()-3)+"grf";
    	System.out.println("GRF FILE : "+grfFile);
// ?    	
    	GRFfile.setName(grfFile);
    	fileP.setBorder(new EmptyBorder(8,8,1,1));
    	fileP.add(new JLabel("choose the name of the resulting grf file"));
// ?    	
    	GRFfile.setText(grfFile);
    	fileP.add(GRFfile, BorderLayout.LINE_START);
    	Action setAction = new AbstractAction("Set ..."){
    		public void actionPerformed(ActionEvent arg1){
    			JFileChooser chooser = Config.getGrfAndFst2DialogBox();
    			System.out.println("chooser dir : "+chooser.getCurrentDirectory());
    			chooser.setCurrentDirectory(Config.getCurrentSntDir());
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    // we return if the user has clicked on CANCEL
                    return;
                }
                GRFfile.setText(chooser.getSelectedFile().getAbsolutePath());
// ?                
                GRFfile.setName(chooser.getSelectedFile().getAbsolutePath());
    		}
    	};
    	JButton setButton = new JButton(setAction);
    	setButton.setSize(4, 1);
    	fileP.add(setButton);
    	return fileP;
    }
    void constructSeqTfst() {
    	MultiCommands commands = new MultiCommands();
        File dir = Config.getCurrentSntDir();
        if (!dir.exists()) {
            /* If the directory toto_snt does not exist, we create it */
            commands.addCommand(new MkdirCommand().name(dir));
        }
        /* Cleaning files */
        Config.cleanTfstFiles(true);
        Seq2GrfCommand seqCmd = new Seq2GrfCommand()
        		.alphabet(ConfigManager.getManager().getAlphabet(null).getAbsolutePath())
                .output(GRFfile.getText())
        		.text(Config.getCurrentSnt());
//        File normFile = null;
//        File normGrfFile = null;
        commands.addCommand(seqCmd);
        InternalFrameManager.getManager(null).closeTextAutomatonFrame();
        InternalFrameManager.getManager(null).closeTfstTagsFrame();
        /* We also have to rebuild the text automaton */
        Config.cleanTfstFiles(true);
        Launcher.exec(commands,true);
        File GRF = new File(Config.getCurrentSntDir(),
        		GRFfile.getName());
        Launcher.exec(commands, true, new ConstructTfstDo(GRF),
                false);
    }
    class ConstructTfstDo implements ToDo {
    	File GrfFileName;
    	ConstructTfstDo(File grf){
    		GrfFileName = grf;
    	}
        public void toDo() {
            //UnitexFrame.getFrameManager().newTextAutomatonFrame(1, false);
//        	File f = new File(GRFfile.getText());
        	Config.cleanTfstFiles(true);
        	InternalFrameManager.getManager(GrfFileName).newGraphFrame(GrfFileName);
        	InternalFrameManager.getManager(GrfFileName).newGraphFrame(new File(GRFfile.getName()));
        }
    }
}