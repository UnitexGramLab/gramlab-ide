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
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
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

	JPanel middle;
	BigTextList text=new BigTextList(true);

	private DelaFrame() {
		super("", true, true, true, true);
		JPanel top = new JPanel();
		top.setOpaque(true);
		top.setLayout(new BorderLayout());
		top.setBorder(new EmptyBorder(2, 2, 2, 2));
		middle = new JPanel();
		middle.setOpaque(true);
		middle.setLayout(new BorderLayout());
		middle.setBorder(BorderFactory.createLoweredBevelBorder());
		middle.add(new JScrollPane(text));
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
		GlobalPreferenceFrame.addTextFontListener(new FontListener() {
			public void fontChanged(Font font) {
				text.setFont(font);
			}});
	}

	/**
	 * Initializes the frame
	 *  
	 */
	private static void init() {
		frame = new DelaFrame();
		UnitexFrame.addInternalFrame(frame,false);
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
		frame.text.load(dela);
		frame.text.setFont(Config.getCurrentTextFont());
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
		frame.text.reset();
		frame.setVisible(false);
		/* We wait to avoid blocking the creation of fooflx.dic by MultiFlex */
		try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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