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
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import fr.umlv.unitex.BigTextList;
import fr.umlv.unitex.Config;


/**
 * This class describes a text frame that shows the results of dictionary
 * checkings.
 * 
 * @author Sébastien Paumier
 *  
 */
public class CheckResultFrame extends JInternalFrame {

	BigTextList text;

    
	CheckResultFrame() {
		super("Check Results", true, true, true, true);
		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(true);
		top.setBorder(new EmptyBorder(2, 2, 2, 2));
		text=new BigTextList();
		JPanel middle = new JPanel(new BorderLayout());
		middle.setBorder(BorderFactory.createLoweredBevelBorder());
		middle.add(new JScrollPane(text), BorderLayout.CENTER);
		top.add(middle, BorderLayout.CENTER);
		setContentPane(top);
		pack();
		setBounds(100, 100, 600, 600);
		setVisible(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				text.reset();
				setVisible(false);
				System.gc();
			}
		});
	}


	/**
	 * Loads a text file.
	 * 
	 * @param f
	 *            the name of the text file
	 */
	void load(File f) {
		text.load(f);
		text.setFont(Config.getCurrentTextFont());
	}

}