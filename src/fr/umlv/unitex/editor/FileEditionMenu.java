/*
 * Unitex
 *
 * Copyright (C) 2001-2013 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package fr.umlv.unitex.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.frames.InternalFrameManager;

/**
 * Menu to handle file edition
 */
public class FileEditionMenu extends JMenu {
	private static FileManager fileManager;

	/**
	 * File edition menu constructor
	 */
	public FileEditionMenu() {
		super("File Edition");
		fileManager = new FileManager();
		final JMenuItem open = new JMenuItem("Open...");
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openFile();
				// save.setEnabled(true);
			}
		});
		final JMenuItem newFile = new JMenuItem("New File");
		newFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newFile();
				// save.setEnabled(true);
			}
		});
		// conversion menu
		final JMenuItem convert = new JMenuItem("Transcode Files");
		convert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				InternalFrameManager.getManager(null).newTranscodingFrame();
			}
		});
		final JMenuItem closeAll = new JMenuItem("Close All");
		closeAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager.getManager(null)
						.closeAllFileEditionTextFrames();
			}
		});
		add(newFile);
		add(open);
		add(convert);
		add(closeAll);
	}

	/**
	 * Opens and displays the file content in an edition area.
	 */
	public static void openFile() {
		final JFileChooser chooser = Config.getFileEditionDialogBox();
		final int returnVal = chooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			// we return if the user has clicked on CANCEL
			return;
		}
		fileManager.loadFile(chooser.getSelectedFile());
	}

	/**
	 * Create a new empty text area
	 */
	void newFile() {
		fileManager.newFile();
	}

}
