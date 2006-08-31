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

package fr.umlv.unitex.editor.ui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

import fr.umlv.unitex.editor.*;


abstract class AbstractFindpanel extends SearchPanel {

	protected JTextField txtFind, txtReplace;
	protected JLabel count;
	protected Document docFind, docReplace;
	protected JButton btFind, btReplace, btRplNext, btcntO, btReplaceAll;
	protected JPanel pc1, po;
	
	/**
	 * @param text
	 */
	public AbstractFindpanel(EditionTextArea text) {
		super(text);

		//form
		pc1 = new JPanel(new BorderLayout());

		JPanel pf = new JPanel();
		pf.setLayout(new DialogLayout(20, 5));
		pf.setBorder(new EmptyBorder(8, 5, 8, 0));

		pf.add(new JLabel("Find what:"));
		txtFind = new JTextField();
		txtFind.setColumns(20);
		docFind = txtFind.getDocument();
		pf.add(txtFind);

		pf.add(new JLabel("Replace:"));
		txtReplace = new JTextField();
		txtReplace.setColumns(20);
		docReplace = txtReplace.getDocument();
		pf.add(txtReplace);

		pf.add(new JLabel("Occurrences:"));
		count = new JLabel("0");
		pf.add(count);

		pc1.add(pf, BorderLayout.CENTER);
		add(pc1, BorderLayout.CENTER);

		// options
		po = new JPanel(new GridLayout(3, 3,1,1));
		po.setBorder(new TitledBorder(new EtchedBorder(), "Options"));
		
		pc1.add(po, BorderLayout.SOUTH);
		
		//Buttons
		
		JPanel p01 = new JPanel(new FlowLayout());
		JPanel p = new JPanel(new GridLayout(6, 1));

		btFind = new JButton("Find Next");
		btFind.setMnemonic('f');
		p.add(btFind);

		btRplNext = new JButton("Replace Next");
		btRplNext.setMnemonic('n');
		p.add(btRplNext);

		btReplace = new JButton("Replace");
		btReplace.setMnemonic('r');
		p.add(btReplace);

		btcntO = new JButton("Count occurrences");
		btcntO.setMnemonic('o');
		p.add(btcntO);

		btReplaceAll = new JButton("Replace All");
		btReplaceAll.setMnemonic('g');
		p.add(btReplaceAll);

		btClose = new JButton("Close");
		btClose.setMnemonic('c');
		p.add(btClose);

		p01.add(p);
		add(p01, BorderLayout.EAST);

	}

}
