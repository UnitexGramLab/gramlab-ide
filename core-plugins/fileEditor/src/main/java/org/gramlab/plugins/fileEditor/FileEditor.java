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
package org.gramlab.plugins.fileEditor;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.gramlab.api.GramlabMenu;
import org.gramlab.api.InternalFileEditor;
import org.gramlab.core.GramlabConfigManager;
import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.gramlab.project.GramlabProjectManager;
import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;
import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.frames.InternalFrameManager;
import org.gramlab.core.umlv.unitex.text.Text;

import ro.fortsoft.pf4j.Extension;

/**
 * Unitex/GramLab Internal File Editor implementation
 * add by Mukarram Tailor
 */ 
@Extension
public class FileEditor implements GramlabMenu, InternalFileEditor{
  @Override
  public JMenu Addmenu(){
	  
	  JMenu m = new JMenu("File Edition");
	  Action n = new AbstractAction("New") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager m = GlobalProjectManager.search(null)
					.getFrameManagerAs(InternalFrameManager.class);
				if (m == null) {
					JOptionPane
							.showMessageDialog(
									null,
									"You cannot create a file if no project is opened.",
									"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				m.newFileEditionTextFrame(null);
			}
		};
		m.add(new JMenuItem(n));
		Action open = new AbstractAction("Open") {
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		};
		m.add(new JMenuItem(open));
		Action convert = new AbstractAction("Transcode Files") {
			public void actionPerformed(ActionEvent e) {
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
						.newTranscodingFrame();
			}
		};
		m.add(new JMenuItem(convert));
		Action closeAll = new AbstractAction("Close all") {
			public void actionPerformed(ActionEvent e) {
				InternalFrameManager m = GlobalProjectManager.search(null)
					.getFrameManagerAs(InternalFrameManager.class);
				if (m == null) {
					return;
				}
				m.closeAllFileEditionTextFrames();
			}
		};
		m.add(new JMenuItem(closeAll));
		return m;
  }
  
  public void openFile() {
		File dir;
		if((GlobalProjectManager.getGlobalProjectManager()) instanceof GramlabProjectManager){
			GramlabProject p = GlobalProjectManager.getAs(GramlabProjectManager.class)
					.getCurrentProject();
			p.getLanguage();
			if (p == null)
				dir = GramlabConfigManager.getWorkspaceDirectory();
			else
				dir = p.getProjectDirectory();
			final JFileChooser fc = new JFileChooser(dir);
			fc.setMultiSelectionEnabled(true);
			fc.setDialogType(JFileChooser.OPEN_DIALOG);
			final int returnVal = fc.showOpenDialog(fc);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				// we return if the user has clicked on CANCEL
				return;
			}
			final File[] files = fc.getSelectedFiles();
			for (int i = 0; i < files.length; i++) {
				if (!files[i].exists()) {
					if (!files[i].exists()) {
						JOptionPane.showMessageDialog(null, "File "
								+ files[i].getAbsolutePath() + " does not exist",
								"Error", JOptionPane.ERROR_MESSAGE);
						continue;
					}
				}
				InternalFrameManager manager = GlobalProjectManager
						.search(files[i]).getFrameManagerAs(InternalFrameManager.class);
				if (manager == null) {
					JOptionPane.showMessageDialog(null,
							"You can not open a file outside a project directory\n"
									+ "or if the project is opened.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				manager.newFileEditionTextFrame(files[i]);
			}
		}
		else{
			Config.getCorpusDialogBox().setDialogType(JFileChooser.OPEN_DIALOG);
			final int returnVal = Config.getCorpusDialogBox().showOpenDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				// we return if the user has clicked on CANCEL
				return;
			}
			final File f = Config.getCorpusDialogBox().getSelectedFile();
			if (!f.exists()) {
				JOptionPane.showMessageDialog(null, "File " + f.getAbsolutePath()
						+ " does not exist", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			Text.loadCorpus(f);
		}
	}
  
}