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
 * This class describes a frame that allows the user to set parameters of the
 * inflection of the current open dictionary.
 * 
 * @author Sébastien Paumier
 *  
 */
public class InflectFrame extends JInternalFrame {

	static InflectFrame frame;
	JTextField directory = new JTextField("");
	JCheckBox twoPointsCheck = new JCheckBox(
			"Add ':' before inflectional codes if necessary", true);
	JCheckBox removeClassNumbers = new JCheckBox("Remove class numbers", true);

    
	private InflectFrame() {
		super("Inflection", false, true);
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
		frame = new InflectFrame();
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
		panel.add(constructUpPanel(), BorderLayout.CENTER);
		panel.add(constructDownPanel(), BorderLayout.SOUTH);
		return panel;
	}

	private JPanel constructUpPanel() {
		JPanel upPanel = new JPanel(new GridLayout(3, 1));
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
		//upPanel.add(twoPointsCheck);
		//upPanel.add(removeClassNumbers);
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
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						inflectDELA();
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
	void inflectDELA() {
		setVisible(false);
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
    	.dir(new File(directory.getText()));
        new ProcessInfoFrame(command, false, null);
	}

}