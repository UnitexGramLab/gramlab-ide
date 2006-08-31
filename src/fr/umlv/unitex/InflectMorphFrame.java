/*
 * Created on 21 oct. 2004
 *
 */
package fr.umlv.unitex;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import fr.umlv.unitex.process.*;

/**
 * @author hhuh
 *
 */
public class InflectMorphFrame extends JInternalFrame{

	static InflectMorphFrame frame;
	JTextField directory = new JTextField("");
	JTextField derivation_directory = new JTextField("");
	JTextField variation_directory = new JTextField("");	
	JCheckBox twoPointsCheck = new JCheckBox(
			"Add ':' before inflectional codes if necessary", true);
	JCheckBox removeClassNumbers = new JCheckBox("Remove class numbers", true);
	JRadioButton rac,suf,racASuf; 
    
	private InflectMorphFrame() {
		super("Mophologic Variation and Derivation", false, true);
		setContentPane(constructPanel());
		pack();
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
		frame = new InflectMorphFrame();
		frame.directory.setText(new File(Config.getUserCurrentLanguageDir(),
				"MorphemVariants").getAbsolutePath());
		frame.variation_directory.setText(
				new File(Config.getUserCurrentLanguageDir()
						,"Variation").getAbsolutePath());
		frame.derivation_directory.setText(
				new File(Config.getUserCurrentLanguageDir()
						,"Derivation").getAbsolutePath());
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
		panel.add(constructUpPanel(), BorderLayout.NORTH);		
		panel.add(constructMidPanel(), BorderLayout.CENTER);
		panel.add(constructDownPanel(), BorderLayout.SOUTH);
		return panel;
	}
	
	private JPanel constructUpPanel() {
		JPanel upPanel = new JPanel();
		upPanel.setBorder(new TitledBorder("List content: "));
		upPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		suf = new JRadioButton("Suffixes");
		rac = new JRadioButton("Stems");
		racASuf = new JRadioButton("Stems & Suf");
		rac.setPreferredSize(new Dimension(70,18));
		suf.setPreferredSize(new Dimension(80,18));
		racASuf.setPreferredSize(new Dimension(100,18));
		upPanel.add(rac);
		upPanel.add(suf);
		upPanel.add(racASuf);		
		ButtonGroup bg = new ButtonGroup();
		bg.add(rac);
		bg.add(suf);
		bg.add(racASuf);
		rac.setSelected(true);
		return upPanel;
	}
	private JPanel constructMidPanel() {
		JPanel midPanel = new JPanel(new GridLayout(2,1));
		
		JPanel DevVarDirPanel = new JPanel(new GridLayout(2, 1));
		DevVarDirPanel.setBorder(new TitledBorder(
				"Directory where variational and derivational FST2 are saved "));
		JPanel tempDevPanel = new JPanel(new BorderLayout());
		derivation_directory.setPreferredSize(new Dimension(240, 25));
		tempDevPanel.add(derivation_directory, BorderLayout.CENTER);
		
		Action setDevDirectoryAction = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = Config.getDevDialogBox().showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				derivation_directory.setText(Config.getDevDialogBox().getSelectedFile()
						.getAbsolutePath());
			}
		};
		JLabel destDevation = new JLabel("Derivation");
		tempDevPanel.add(destDevation, BorderLayout.WEST);
		JButton setDevDirectory = new JButton(setDevDirectoryAction);
		tempDevPanel.add(setDevDirectory, BorderLayout.EAST);
		derivation_directory.setPreferredSize(new Dimension(240, 25));
		tempDevPanel.add(derivation_directory, BorderLayout.CENTER);
		DevVarDirPanel.add(tempDevPanel);
		
		JPanel tempVarPanel = new JPanel(new BorderLayout());
		variation_directory.setPreferredSize(new Dimension(240, 25));
		tempVarPanel.add(variation_directory, BorderLayout.CENTER);
		
		Action setVarDirectoryAction = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = Config.getVarDialogBox().showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				variation_directory.setText(Config.getVarDialogBox().getSelectedFile()
						.getAbsolutePath());
			}
		};
		JLabel destVariaton = new JLabel("Variation  ");
		tempVarPanel.add(destVariaton, BorderLayout.WEST);
		JButton setVarDirectory = new JButton(setVarDirectoryAction);
		tempVarPanel.add(setVarDirectory, BorderLayout.EAST);
		variation_directory.setPreferredSize(new Dimension(240, 25));
		tempVarPanel.add(variation_directory, BorderLayout.CENTER);
		DevVarDirPanel.add(tempVarPanel);
		
		JPanel saveResultPanel = new JPanel(new BorderLayout());
		saveResultPanel.setBorder(new TitledBorder(
				"Save result in :"));
//		JPanel tempPanel = new JPanel(new BorderLayout());
		directory.setPreferredSize(new Dimension(280, 25));
//		tempPanel.add(directory, BorderLayout.CENTER);
		Action setDirectoryAction = new AbstractAction("Set...") {
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = Config.getInflectDialogBox().showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				directory.setText(Config.getInflectDialogBox().getSelectedFile()
						.getAbsolutePath());
			}
		};
		JButton setDirectory = new JButton(setDirectoryAction);
//		tempPanel.add(setDirectory, BorderLayout.EAST);
		saveResultPanel.add(setDirectory,BorderLayout.EAST);
		directory.setPreferredSize(new Dimension(280, 25));
		saveResultPanel.add(directory,BorderLayout.WEST);
//		saveResultPanel.add(tempPanel);
		
		midPanel.add(DevVarDirPanel);
		midPanel.add(saveResultPanel);		
		return midPanel;
	}

	
	private JPanel constructDownPanel() {
		JPanel downPanel = new JPanel(new GridLayout(1, 2));
		Action cancelAction = new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		};
		JButton CANCEL = new JButton(cancelAction);
		Action goAction = new AbstractAction("Execute") {
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						inflectMorphDELA();
					}
				});
			}
		};
		JButton GO = new JButton(goAction);
		downPanel.add(CANCEL);
		downPanel.add(GO);
		return downPanel;
	}

	/**
	 * Launches the inflection program <code>Inflect</code>, through the
	 * creation of a <code>ProcessInfoFrame</code> object.
	 *  
	 */
	void inflectMorphDELA() {
  
		setVisible(false);
		TextDicFrame.hideFrame();
	
        File f = Config.getCurrentDELA();
		MultiCommands commands = new MultiCommands();
		
		Syl2JamoCommand jamoCmd = new Syl2JamoCommand()
		  .optionForIncludeJamo()
		  .optionForMapJamo(new File(Config.getUserCurrentLanguageDir(),
			"jamoTable.txt"))
		  .src(f);
		
		commands.addCommand(jamoCmd);
		
        File ff = Config.getCurrentDELA();
        
        String tmp=f.getName();
		int point = tmp.lastIndexOf('.');
		int separator = tmp.lastIndexOf(File.separatorChar);
		if (separator < point) {
			tmp = tmp.substring(0, point);
		}
		if(rac.isSelected()){
			tmp = tmp + ".ric";
		} else {
			tmp = tmp + ".sic";
		}
		
        String tmpD=ff.getAbsolutePath();
		int pointD = tmpD.lastIndexOf('.');
		int separatorD = tmpD.lastIndexOf(File.separatorChar);
		if (separatorD < pointD) {
			tmpD = tmpD.substring(0, pointD);
		}
		tmpD = tmpD + "jm.dic";
		
		InflectKrCommand InfCmdKr = new InflectKrCommand()
			.dirDerivation(new File(derivation_directory.getText()))
		    .dirVariation(new File(variation_directory.getText()));
		File changeTable =new File(Config.getUserCurrentLanguageDir(),"hanjajm.txt");
		if(changeTable.exists())
			InfCmdKr = InfCmdKr.convTable(changeTable);
		
		InfCmdKr = InfCmdKr.result(new File(directory.getText(),tmp));
		
		String options = new String("-c SS=0x318D -m 0x318D");
		StringTokenizer st = new StringTokenizer(options," ");
		while(st.hasMoreTokens())
			InfCmdKr = InfCmdKr.uneOption(st.nextToken());
		if(rac.isSelected()){
			InfCmdKr.racineContent();
		} else if(suf.isSelected()){
			InfCmdKr.suffixContent();
		}	else {
			InfCmdKr.racineContent();
			InfCmdKr.suffixContent();			
		}
		InfCmdKr = InfCmdKr.dir(new File(tmpD));
		commands.addCommand(InfCmdKr);
		
		new ProcessInfoFrame(commands,false, null,false);
		return;
	}
}
