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

package fr.umlv.unitex.editor.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

import fr.umlv.unitex.editor.*;


/**
 * @author julien
 */
public class FindSentencePanel extends SearchPanel {

	
	JTextField sentenceNumber; 
	
	public FindSentencePanel(final EditionTextArea text ) {		
		super(text);
		
		
		JPanel pf = new JPanel();
		pf.setLayout(new DialogLayout(20, 5));
		pf.setBorder(new EmptyBorder(8, 5, 8, 0));

		pf.add(new JLabel("Sentence Number:"));
		sentenceNumber = new JTextField();		
		pf.add(sentenceNumber);
		
		
		JPanel p01 = new JPanel(new FlowLayout());
		JPanel p = new JPanel(new GridLayout(2, 1));
		
		JButton btFind = new JButton("Find");
		// to have the same dimension betwin all panel buttons
		btFind.setPreferredSize(new JButton("Count occurrences").getPreferredSize());
		
		 class findSentence implements ActionListener {

			public void actionPerformed(ActionEvent arg0) {
				try{
				
				Document docFind = sentenceNumber.getDocument();
				String key =docFind.getText(0, docFind.getLength());
				int number = Integer.parseInt(key);
				text.findSentence(number);				
				}catch( BadLocationException e ){
					warning("Bad Loaction Exception:\n "+e.getMessage());
				}catch( NumberFormatException e ){
					warning("Number Format Exception:\n "+e.getMessage());
				}catch( KeyErrorException e ){
					warning(e.getMessage());
				}catch( TextAreaSeparatorException e ){
					warning(e.getMessage());
				}
			}
			
		}
		
		final findSentence findSentenceAction = new findSentence();
		btFind.addActionListener(findSentenceAction);
		
		// find next when enter is pressed
		sentenceNumber.addKeyListener( new KeyAdapter(){

				 public void keyPressed(KeyEvent e) {
					 if( e.getKeyCode() == KeyEvent.VK_ENTER  )
					 findSentenceAction.actionPerformed(null);	
				
				 }
			
			 }
			 );
		btFind.setMnemonic('f');
		p.add(btFind);	
		p.add(btClose);
		
		p01.add(p);
		
		add(p01,BorderLayout.EAST);
		add(pf,BorderLayout.CENTER);		
	}

}