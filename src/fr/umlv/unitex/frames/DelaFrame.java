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
import fr.umlv.unitex.FontListener;
import fr.umlv.unitex.GlobalPreferenceFrame;
import fr.umlv.unitex.ToDo;
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

	JPanel middle;
	BigTextList text=new BigTextList(true);

	DelaFrame() {
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
				Config.setCurrentDELA(null);
				text.reset();
				setVisible(false);
				/* We wait to avoid blocking the creation of fooflx.dic by MultiFlex */
				try {
		            Thread.sleep(10);
		        } catch (InterruptedException e2) {
		            e2.printStackTrace();
		        }
				System.gc();
			}
		});
		GlobalPreferenceFrame.addTextFontListener(new FontListener() {
			public void fontChanged(Font font) {
				text.setFont(font);
			}});
	}

	/**
	 * Loads a dictionary.
	 * 
	 * @param dela
	 *            the dictionary to be loaded
	 */
	public void loadDela(File dela) {
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

	void loadUnicodeDela(File dela) {
		text.load(dela);
		Config.setCurrentDELA(dela);
		text.setFont(Config.getCurrentTextFont());
		setTitle(dela.getAbsolutePath());
		setVisible(true);
		try {
			setIcon(false);
			setSelected(true);
		} catch (java.beans.PropertyVetoException e2) {
			e2.printStackTrace();
		}
	}


	class LoadDelaDo implements ToDo {
		File dela;

		LoadDelaDo(File s) {
			dela = s;
		}

		public void toDo() {
			loadUnicodeDela(dela);
		}

	}

}