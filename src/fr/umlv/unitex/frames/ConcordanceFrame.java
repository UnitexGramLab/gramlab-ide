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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.umlv.unitex.BigConcordance;
import fr.umlv.unitex.FontListener;
import fr.umlv.unitex.Preferences;
import fr.umlv.unitex.Util;



/**
 * This class describes a frame that can show an HTML concordance file.
 * 
 * @author Sébastien Paumier
 *  
 */
public class ConcordanceFrame extends JInternalFrame {

	BigConcordance list;
	private JLabel numberOfMatches = new JLabel("");
	JComponent invisible=new JComponent() {
		@Override
		protected void paintComponent(Graphics g) {
			/* Do nothing since this is an invisible component
			 * only used to catch mouse events.
			 */
		}
		
		@Override
		public boolean contains(int x, int y) {
			return true;
		}

		@Override
		public boolean contains(Point p) {
			return true;
		}
	};
	
	/**
	 * Constructs a new <code>ConcordanceFrame</code>.
	 */
	ConcordanceFrame(File f,int widthInChars) {
		super("", true, true, true, true);
		list=new BigConcordance(widthInChars);
		invisible.setOpaque(false);
		invisible.setVisible(true);
		invisible.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				getLayeredPane().remove(invisible);
				revalidate();
				repaint();
			}
		});
		JScrollPane scroll = new JScrollPane(list);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		JPanel middle = new JPanel(new BorderLayout());
		middle.setBorder(BorderFactory.createLoweredBevelBorder());
		middle.add(scroll, BorderLayout.CENTER);
		JPanel top = new JPanel(new BorderLayout());
		top.add(middle, BorderLayout.CENTER);
		top.setBorder(new EmptyBorder(2, 2, 2, 2));
		JPanel up = new JPanel(new BorderLayout());
		up.setBorder(new EmptyBorder(2, 2, 2, 2));
		up.add(numberOfMatches, BorderLayout.CENTER);
		top.add(up,BorderLayout.NORTH);
		setContentPane(top);
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				list.reset();
				list.clearSelection();
			}
			
			@Override
			public void internalFrameDeactivated(InternalFrameEvent e) {
				/* Don't want to deal with a layout manager on the JLayeredPane,
				 * so we just set a big size.
				 */
				invisible.setSize(2000,2000);
				/* We add the invisible component on the top of the layered pane */ 
				getLayeredPane().add(invisible,new Integer(600));
				revalidate();
				repaint();
			}
			
			@Override
			public void internalFrameActivated(InternalFrameEvent e) {
				getLayeredPane().remove(invisible);
				revalidate();
				repaint();
			}
		});
		setBounds(150, 50, 850, 550);
		load(f,widthInChars);
	}

	/**
	 * Constructs a new <code>ConcordanceFrame</code> and loads in it an HTML
	 * file. The number of lines in the concordance is shown in the caption of
	 * the frame.
	 * 
	 * @param concor
	 *            the HTML file
     @param widthInChars width of a line in chars. Equals to the sum of left and right 
     context lengths
	 */
	private void load(File concor, int widthInChars) {
		setTitle("Concordance: " + concor.getAbsolutePath());
		numberOfMatches.setText(Util.getHtmlPageTitle(concor));
		Dimension d = getSize();
		int g = widthInChars * 8;
		d.setSize((g < 800) ? g : 800, d.height);
		setSize(d);
		Util.getHtmlPageTitle(concor);
		Preferences.addConcordanceFontListener(new FontListener() {
			public void fontChanged(Font font) {
				list.setFont(font);
			}
		});
		list.setFont(new Font(Preferences.getConcordanceFontName(),0,Preferences.getConcordanceFontSize()));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				TextFrame f=UnitexFrame.getFrameManager().getTextFrame();
				String s=(String) list.getSelectedValue();
				if (s==null || e.getValueIsAdjusting() || f.isIcon()) return;
				int start=s.indexOf("<a href=\"")+9;
				int end=s.indexOf(' ',start);
				int selectionStart=Integer.valueOf((String) s.subSequence(start,end));
				start=end+1;
				end=s.indexOf(' ',start);
				int selectionEnd=Integer.valueOf((String) s.subSequence(start,end));
				start=end+1;
				end=s.indexOf('\"',start);
				int sentenceNumber=Integer.valueOf((String) s.subSequence(start,end));
				try {
					f.getText().setSelection(selectionStart,selectionEnd-1);
					f.getText().scrollToSelection();
					f.setSelected(true);
			} catch (PropertyVetoException e2) {
				e2.printStackTrace();
			}
			UnitexFrame.getFrameManager().newTextAutomatonFrame(sentenceNumber);
			}});
		list.load(concor);
	}

}