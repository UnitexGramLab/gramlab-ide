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
import javax.swing.border.*;
import javax.swing.event.*;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.ToDo;
import fr.umlv.unitex.process.*;
import fr.umlv.unitex.process.commands.CheckDicCommand;

/**
 * This class describes the "Check Format" frame, accessible from the "DELA"
 * menu of Unitex. The user can select the kind of dictionary he wants to check.
 * 
 * @author Sébastien Paumier
 *  
 */
public class CheckDicFrame extends JInternalFrame {

	private JRadioButton DELAS = new JRadioButton("DELAS/DELAC");
	private JRadioButton DELAF = new JRadioButton("DELAF/DELACF",true);

	CheckDicFrame() {
		super("Check Dictionary Format", false, true);
		constructPanel();
		setContentPane(constructPanel());
		setBounds(100, 100, 200, 100);
		pack();
		setResizable(false);
		setVisible(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				setVisible(false);
			}
		});
	}

	private JPanel constructPanel() {
    JPanel panel=new JPanel();
		panel.setOpaque(true);
		panel.setLayout(new BorderLayout());
		panel.add(constructLeftPanel(), BorderLayout.WEST);
		panel.add(constructRightPanel(), BorderLayout.CENTER);
    return panel;
	}

	private JPanel constructLeftPanel() {
    JPanel leftPanel = new JPanel();
		JPanel tmp = new JPanel();
		leftPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		tmp.setBorder(new TitledBorder("Dictionary Type:"));
		tmp.setLayout(new GridLayout(2, 1));
		DELAS.setSelected(false);
		DELAF.setSelected(true);
		ButtonGroup bg = new ButtonGroup();
		bg.add(DELAS);
		bg.add(DELAF);
		tmp.add(DELAS);
		tmp.add(DELAF);
		leftPanel.add(tmp);
    return leftPanel;
	}

	private JPanel constructRightPanel() {
    JPanel rightPanel=new JPanel(new GridLayout(2, 1));
		rightPanel.setBorder(new EmptyBorder(12, 5, 5, 7));
		Action goAction = new AbstractAction("Check Dictionary") {
			public void actionPerformed(ActionEvent arg0) {
			    setVisible(false);
			    // post pone code
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						checkDELA();
					}
				});
			}
		};
		JButton GO = new JButton(goAction);
		Action cancelAction = new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
            }
        };
    JButton CANCEL = new JButton(cancelAction);
		rightPanel.add(GO);
		rightPanel.add(CANCEL);
    return rightPanel;
	}

	/**
	 * Launches the <code>CheckDic</code> verification program, through the
	 * creation of a <code>ProcessInfoFrame</code> object.
	 *  
	 */
	 void checkDELA() {
		CheckDicCommand command=new CheckDicCommand().name(Config.getCurrentDELA())
		.delaType(DELAS.isSelected()).alphabet(Config.getAlphabet());
		
		File tmp = new File(Config.getCurrentDELA().getParentFile(),"CHECK_DIC.TXT");
		UnitexFrame.getFrameManager().closeCheckResultFrame();
		new ProcessInfoFrame(command.getBuilder(), true, new CheckDicDo(tmp));
	}

	class CheckDicDo implements ToDo {
		File results;
		public CheckDicDo(File s) {
			results = s;
		}

		public void toDo() {
			UnitexFrame.getFrameManager().newCheckResultFrame(results);
		}
	}

}