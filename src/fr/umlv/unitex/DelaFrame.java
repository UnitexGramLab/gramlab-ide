 /*
  * Unitex
  *
  * Copyright (C) 2001-2006 Université de Marne-la-Vallée <unitex@univ-mlv.fr>
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
import javax.swing.text.*;

import fr.umlv.unitex.conversion.*;
import fr.umlv.unitex.io.*;
import fr.umlv.unitex.process.*;

/**
 * This class describes a frame used to display a dictionary.
 * 
 * @author Sébastien Paumier
 *  
 */
public class DelaFrame extends JInternalFrame {

	static DelaFrame frame;

	private MyTextArea text = new MyTextArea();
	static boolean FILE_TOO_LARGE = false;

	private DelaFrame() {
		super("", true, true, true, true);
		JPanel top = new JPanel();
		top.setOpaque(true);
		top.setLayout(new BorderLayout());
		top.setBorder(new EmptyBorder(2, 2, 2, 2));
		JScrollPane scroll = new JScrollPane(text);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel middle = new JPanel();
		middle.setOpaque(true);
		middle.setLayout(new BorderLayout());
		middle.setBorder(BorderFactory.createLoweredBevelBorder());
		middle.add(scroll, BorderLayout.CENTER);
		top.add(middle, BorderLayout.CENTER);
		setContentPane(top);
		pack();
		setBounds(100, 100, 600, 600);
		setVisible(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				UnitexFrame.mainFrame.closeDELA();
			}
		});
	}

	/**
	 * Initializes the frame
	 *  
	 */
	private static void init() {
		frame = new DelaFrame();
		UnitexFrame.addInternalFrame(frame);
	}

	/**
	 * Loads a dictionary.
	 * 
	 * @param dela
	 *            the dictionary to be loaded
	 */
	public static void loadDela(File dela) {
		LoadDelaDo toDo = new LoadDelaDo(dela);
		try {
			if (!UnicodeIO.isAUnicodeLittleEndianFile(dela)) {
				ConvertOneFileFrame.reset();
				ConvertCommand res = ConvertOneFileFrame
						.getCommandLineForConversion(dela);
				if (res == null) {
					return;
				}
				new ProcessInfoFrame(res, true, toDo);
			} else {
				toDo.toDo();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
	}

	static void loadUnicodeDela(File dela) {
		if (frame == null) {
			init();
		}
		frame.text.setFont(Config.getCurrentTextFont());
		frame.text.setWrapStyleWord(true);
		frame.text.setLineWrap(true);
		frame.text.setEditable(false);
		if (!dela.exists() || dela.length() <= 2) {
			FILE_TOO_LARGE = true;
			frame.text.setDocument(new PlainDocument());
			frame.text.setText(Config.EMPTY_FILE_MESSAGE);
		} else if (dela.length() < Preferences.pref.MAX_TEXT_FILE_SIZE) {
			try {
				frame.text.load(dela);
			} catch (java.io.IOException e) {
				FILE_TOO_LARGE = true;
				return;
			}
			FILE_TOO_LARGE = false;
		} else {
			FILE_TOO_LARGE = true;
			frame.text.setDocument(new PlainDocument());
			frame.text.setText(Config.FILE_TOO_LARGE_MESSAGE);
		}
		frame.setTitle(dela.getAbsolutePath());
		frame.setVisible(true);
		try {
			frame.setIcon(false);
			frame.setSelected(true);
		} catch (java.beans.PropertyVetoException e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * Closes the frame.
	 *  
	 */
	public static void closeDela() {
		if (frame == null) {
			return;
		}
		frame.text.killTimer();
		frame.setVisible(false);
		frame.text.setDocument(new PlainDocument());
		System.gc();
	}

	static class LoadDelaDo extends ToDoAbstract {
		File dela;

		LoadDelaDo(File s) {
			dela = s;
		}

		public void toDo() {
			loadUnicodeDela(dela);
		}

	}

}