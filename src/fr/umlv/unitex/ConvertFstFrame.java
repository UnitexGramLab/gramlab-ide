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
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import fr.umlv.unitex.process.*;

/**
 * This class describes the FST-Text to Text conversion frame.
 * 
 * @author Sébastien Paumier
 *  
 */
public class ConvertFstFrame extends JInternalFrame {

	static ConvertFstFrame frame;
	JTextField textName = new JTextField();

	private ConvertFstFrame() {
		super("Convert Text Automaton to Text", false, true);
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
		frame = new ConvertFstFrame();
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

	private JPanel constructPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new TitledBorder(
		"Output text file:"));
		Action setTextAction = new AbstractAction("Set") {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = Config.getFst2UnambigDialogBox();
				int returnVal = fc.showOpenDialog(null);
				if (returnVal != JFileChooser.APPROVE_OPTION) {
					// we return if the user has clicked on CANCEL
					return;
				}
				textName.setText(fc.getSelectedFile()
						.getAbsolutePath());
			}
		};
		JButton setTextButton = new JButton(setTextAction);
		textName.setPreferredSize(new Dimension(300,20));
		panel.add(textName, BorderLayout.CENTER);
		panel.add(setTextButton, BorderLayout.EAST);
		
		JPanel buttonPanel=new JPanel(new GridLayout(1,2));
		JButton OK=new JButton("OK");
		OK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchFst2Unambig();
			}
		});
		JButton CANCEL=new JButton("Cancel");
		CANCEL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		});
		buttonPanel.add(CANCEL);
		buttonPanel.add(OK);
		panel.add(buttonPanel,BorderLayout.SOUTH);
		panel.setOpaque(true);
		return panel;
	}





	void launchFst2Unambig() {
		MultiCommands commands = new MultiCommands();
		if (textName.getText().equals("")) {
			JOptionPane.showMessageDialog(null,
					"You must specify a file name", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		Fst2UnambigCommand cmd = new Fst2UnambigCommand();
		File fst2=new File(Config.getCurrentSntDir(),"text.tfst");
		if (!fst2.exists()) {
			JOptionPane.showMessageDialog(null,
					"Cannot find text automaton", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		cmd=cmd.fst2(fst2);
		File txt=new File(textName.getText());
		cmd.output(txt);		
		commands.addCommand(cmd);
		frame.setVisible(false);
		new ProcessInfoFrame(commands, true, null,true);
	}

}