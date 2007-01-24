/*
 * Unitex
 *
 * Copyright (C) 2001-2007 Université de Marne-la-Vallée <unitex@univ-mlv.fr>
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
    MyTextArea licenseGPL = new MyTextArea(); 
    JScrollPane scrollGPL = new JScrollPane(licenseGPL);
    MyTextArea licenseLGPL = new MyTextArea(); 
    JScrollPane scrollLGPL = new JScrollPane(licenseLGPL);
    MyTextArea licenseLGPLLR = new MyTextArea(); 
    JScrollPane scrollLGPLLR = new JScrollPane(licenseLGPLLR);
    MyTextArea disclaimer = new MyTextArea();		
    JPanel up = new JPanel();
		up.setOpaque(true);
		up.setLayout(new BorderLayout());
		JPanel image = new JPanel();
		image.setBorder(new EmptyBorder(4, 3, 1, 1));
		image.setLayout(new BorderLayout());
		image.add(new JLabel(new ImageIcon(AboutUnitexFrame.class
				.getResource("Unitex.jpg"))));
		up.add(image, BorderLayout.WEST);
		JPanel info = new JPanel();
		info.setBorder(new TitledBorder("Unitex"));
		info.setLayout(new BorderLayout());
    JScrollPane disclaimerScroll = new JScrollPane(disclaimer);
    disclaimerScroll.setPreferredSize(new Dimension(400,image.getHeight()));
    info.add(disclaimerScroll, BorderLayout.CENTER);
		up.add(info, BorderLayout.CENTER);
		top.add(up, BorderLayout.NORTH);
		JTabbedPane licenses = new JTabbedPane();
    licenses.add(scrollLGPL,"LGPL");
    licenses.add(scrollGPL,"GPL");
    licenses.add(scrollLGPLLR,"LGPLLR");
		top.add(licenses, BorderLayout.CENTER);
		setContentPane(top);
		setVisible(false);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				frame.setVisible(false);
			}
		});
    disclaimer.setWrapStyleWord(true);
    disclaimer.setLineWrap(true);
    disclaimer.setEditable(false);
		licenseGPL.setWrapStyleWord(true);
		licenseGPL.setLineWrap(true);
		licenseGPL.setEditable(false);
    licenses.setPreferredSize(new Dimension(500,300));
    licenseLGPL.setWrapStyleWord(true);
    licenseLGPL.setLineWrap(true);
    licenseLGPL.setEditable(false);
		try {
			File f = new File(Config.getApplicationDir(), "GPL.txt");
			licenseGPL.load(f);
      f = new File(Config.getApplicationDir(), "LGPL.txt");
      licenseLGPL.load(f);
      f = new File(Config.getApplicationDir(), "LGPLLR.txt");
      licenseLGPLLR.load(f);
      f = new File(Config.getApplicationDir(), "Disclaimer.txt");
      disclaimer.load(f);
		} catch (java.io.IOException e) {
			// do nothing
		}
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