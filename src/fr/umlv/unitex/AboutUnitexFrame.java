/*
 * Unitex
 *
 * Copyright (C) 2001-2007 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
import java.io.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * This class defines a frame that contains the Unitex logo, the body of the
 * license and some informations about Unitex.
 * 
 * @author Sébastien Paumier
 *  
 */
public class AboutUnitexFrame extends JInternalFrame {

	protected static AboutUnitexFrame frame;

	private AboutUnitexFrame() {
		super("About Unitex", true, true);
		JPanel top = new JPanel();
		top.setOpaque(true);
		top.setLayout(new BorderLayout());
		BigTextArea licenseGPL=new BigTextArea(new File(Config.getApplicationDir(),"GPL.txt"));
		BigTextArea licenseLGPL=new BigTextArea(new File(Config.getApplicationDir(),"LGPL.txt"));
		BigTextArea licenseLGPLLR=new BigTextArea(new File(Config.getApplicationDir(),"LGPLLR.txt"));
		BigTextArea apache=new BigTextArea(new File(Config.getApplicationDir(),"Apache-1.1.txt"));
		BigTextArea disclaimer=new BigTextArea(new File(Config.getApplicationDir(),"Disclaimer.txt"));
		JPanel up = new JPanel();
		up.setOpaque(true);
		up.setLayout(new BorderLayout());
		JPanel image = new JPanel();
		image.setBorder(new EmptyBorder(4, 3, 1, 1));
		image.setLayout(new BorderLayout());
		image.add(new JLabel(new ImageIcon(AboutUnitexFrame.class.getResource("Unitex.jpg"))));
		up.add(image, BorderLayout.WEST);
		JPanel info = new JPanel();
		info.setBorder(new TitledBorder("Unitex"));
		info.setLayout(new BorderLayout());
		disclaimer.setPreferredSize(new Dimension(400,image.getHeight()));
		info.add(disclaimer, BorderLayout.CENTER);
		up.add(info, BorderLayout.CENTER);
		top.add(up, BorderLayout.NORTH);
		JTabbedPane licenses = new JTabbedPane();
		licenses.add(licenseLGPL,"LGPL");
		licenses.add(licenseGPL,"GPL");
		licenses.add(licenseLGPLLR,"LGPLLR");
		licenses.add(apache,"Apache");
		top.add(licenses, BorderLayout.CENTER);
		setContentPane(top);
		setVisible(false);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				frame.setVisible(false);
			}
		});
		licenses.setPreferredSize(new Dimension(500,300));
		pack();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	private static void init() {
		frame = new AboutUnitexFrame();
		UnitexFrame.addInternalFrame(frame);
	}

	/**
	 * Shows the frame, creating it if necessary.
	 *  
	 */
	public static void showFrame() {
		if (frame == null) {
			init();
		}
		try {
			frame.setSelected(true);
			frame.setIcon(false);
		} catch (java.beans.PropertyVetoException e) {
			// do nothing
		}
		frame.setVisible(true);
	}

}