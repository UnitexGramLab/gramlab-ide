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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.commands.MultiFlexCommand;

/**
 * This class describes a frame that allows the user to set parameters of the
 * inflection of the current open dictionary.
 * 
 * @author Sébastien Paumier
 *  
 */
public class InflectFrame extends JInternalFrame {

	JTextField directory = new JTextField("");
	JRadioButton allWords=new JRadioButton("Allow both simple and compound words",true);
	JRadioButton onlySimpleWords=new JRadioButton("Allow only simple words",false);
	JRadioButton onlyCompoundWords=new JRadioButton("Allow only compound words",false);
	
    
	InflectFrame() {
		super("Inflection", false, true);
		setContentPane(constructPanel());
		pack();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}


	private JPanel constructPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(constructUpPanel(), BorderLayout.CENTER);
		panel.add(constructDownPanel(), BorderLayout.SOUTH);
		return panel;
	}

	private JPanel constructUpPanel() {
		JPanel upPanel = new JPanel(new GridLayout(4, 1));
		upPanel.setBorder(new TitledBorder(
				"Directory where inflectional FST2 are stored: "));
		JPanel tempPanel = new JPanel(new BorderLayout());
		directory.setPreferredSize(new Dimension(240, 25));
		directory.setText(new File(Config.getUserCurrentLanguageDir(),"Inflection").getAbsolutePath());
		tempPanel.add(directory, BorderLayout.CENTER);
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
		tempPanel.add(setDirectory, BorderLayout.EAST);
		upPanel.add(tempPanel);
		ButtonGroup bg=new ButtonGroup();
		bg.add(allWords);
		bg.add(onlySimpleWords);
		bg.add(onlyCompoundWords);
		upPanel.add(allWords);
		upPanel.add(onlySimpleWords);
		upPanel.add(onlyCompoundWords);
		return upPanel;
	}

	private JPanel constructDownPanel() {
		JPanel downPanel = new JPanel(new GridLayout(1, 2));
		Action cancelAction = new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		};
		JButton CANCEL = new JButton(cancelAction);
		Action goAction = new AbstractAction("Inflect Dictionary") {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				inflectDELA();
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
	void inflectDELA() {
        File f = Config.getCurrentDELA();
        String tmp=f.getAbsolutePath();
        int point = tmp.lastIndexOf('.');
        int separator = tmp.lastIndexOf(File.separatorChar);
        if (separator < point) {
            tmp = tmp.substring(0, point);
        }
        tmp = tmp + "flx.dic";
    	MultiFlexCommand command=new MultiFlexCommand().delas(f)
        .result(new File(tmp))
        .alphabet(Config.getAlphabet())
        .library()
    	.dir(new File(directory.getText()));
    	if (onlySimpleWords.isSelected()) {
    		command=command.onlySimpleWords();
    	} else if (onlyCompoundWords.isSelected()) {
    		command=command.onlyCompoundWords();
    	}
    	if (Config.isKorean()) {
    		command=command.korean();
    	}
    	Launcher.exec(command, false, null);
	}

}