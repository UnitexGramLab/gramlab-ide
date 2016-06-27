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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.gramlab.api.Menu;

import ro.fortsoft.pf4j.Extension;

/**
 * Unitex/GramLab Internal File Editor implementation
 * add by Mukarram Tailor
 */ 
@Extension
public class FileEditor implements Menu {
  @Override
  public JMenu Addmenu(){
	  
	  JMenu m = new JMenu("File Edition");
		Action n = new AbstractAction("New") {
			public void actionPerformed(ActionEvent e) {
				
				
			}
		};
		m.add(new JMenuItem(n));
		Action open = new AbstractAction("Open") {
			public void actionPerformed(ActionEvent e) {
				
			}
		};
		m.add(new JMenuItem(open));
		Action closeAll = new AbstractAction("Close all") {
			public void actionPerformed(ActionEvent e) {
				
			}
		};
		m.add(new JMenuItem(closeAll));
		return m;
  }
}