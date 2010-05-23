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
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import fr.umlv.unitex.BigTextList;
import fr.umlv.unitex.Config;
import fr.umlv.unitex.FontListener;
import fr.umlv.unitex.Preferences;
import fr.umlv.unitex.ToDo;
import fr.umlv.unitex.io.UnicodeIO;


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
		JPanel top = new JPanel(new BorderLayout());
		top.setBorder(new EmptyBorder(2, 2, 2, 2));
		middle = new JPanel(new BorderLayout());
		middle.setBorder(BorderFactory.createLoweredBevelBorder());
		middle.add(new JScrollPane(text));
		top.add(middle, BorderLayout.CENTER);
		setContentPane(top);
		pack();
		setBounds(100, 100, 500, 500);
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
		Preferences.addTextFontListener(new FontListener() {
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
				UnitexFrame.getFrameManager().newTranscodeOneFileDialog(dela,toDo);
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