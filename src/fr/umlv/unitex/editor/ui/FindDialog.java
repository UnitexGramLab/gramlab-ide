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

package fr.umlv.unitex.editor.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import fr.umlv.unitex.*;


public class FindDialog extends JDialog {

	/**
	 * Creates a new fileDialog with the specified owner
	 * @param owner the FileEditionTextFrame where find words
	 */
	public FindDialog(FileEditionTextFrame owner) {
		super(UnitexFrame.mainFrame);
		ActionListener closeAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		};

		
		// tabbed pane init 
		JTabbedPane tab = new JTabbedPane();

		FindPanel p1 = new FindPanel( owner.getText());
		p1.addCloseAction(closeAction);
		FindSentencePanel p2 = new FindSentencePanel(owner.getText());		
		DictionnaryFindPanel p3 = new DictionnaryFindPanel(owner.getText());
		
		p1.addCloseAction(closeAction);
		p2.addCloseAction(closeAction);
		p3.addCloseAction(closeAction);
		
		tab.add("Find",p1);
		tab.add("Find Sentence",p2);
		tab.add("Dictionary Search",p3);
		getContentPane().add(tab, BorderLayout.CENTER);

		pack();
		setResizable(false);
	}

	
}
