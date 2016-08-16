/*
 * Unitex
 *
 * Copyright (C) 2001-2016 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package org.gramlab.core.umlv.unitex.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;

import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.frames.InternalFrameManager;
import org.gramlab.core.umlv.unitex.frames.MenuAdapter;
import org.gramlab.core.umlv.unitex.project.manager.UnitexProjectManager;

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
		final JMenu open = new JMenu("Open...");
		open.addMenuListener(new MenuAdapter() {
			@Override
			public void menuSelected(MenuEvent e) {
				open.removeAll();
				final JMenuItem txtItem = new JMenuItem("Text files");
				txtItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ev) {
						openFile("txt");
					}
				});
				final JMenuItem dicItem = new JMenuItem("Dictionaries");
				dicItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ev) {
						openFile("dic");
					}
				});
				final JMenuItem cscItem = new JMenuItem("Cascade Configuration Files");
				cscItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ev) {
						openFile("csc");
					}
				});
				final JMenuItem otherItem = new JMenuItem("Other Files");
				otherItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ev) {
						openFile();
					}
				});
				open.add(txtItem);
				open.add(dicItem);
				open.add(cscItem);
				open.add(otherItem);
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
				UnitexProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.newTranscodingFrame();
			}
		});
		final JMenuItem closeAll = new JMenuItem("Close All");
		closeAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UnitexProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
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
	
	public static void openFile(String extension) {
		final JFileChooser chooser = Config.getFileEditionDialogBox(extension);
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
