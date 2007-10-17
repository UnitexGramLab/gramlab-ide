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

package fr.umlv.unitex;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;


/**
 * This class describes a frame that can show an HTML concordance file.
 * 
 * @author Sébastien Paumier
 *  
 */
public class ConcordanceFrame extends JInternalFrame {

	static ConcordanceFrame frame;
	
	BigConcordance list=new BigConcordance();
	private JLabel nombre_matches = new JLabel("");
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
	 * Constructs a new empty <code>ConcordanceFrame</code>.
	 */
	public ConcordanceFrame() {
		super("", true, true, true, true);
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
		JPanel middle = new JPanel(new BorderLayout());
		middle.setOpaque(true);
		middle.setBorder(BorderFactory.createLoweredBevelBorder());
		middle.add(scroll, BorderLayout.CENTER);
		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(true);
		top.add(middle, BorderLayout.CENTER);
		top.setBorder(new EmptyBorder(2, 2, 2, 2));
		JPanel up = new JPanel(new BorderLayout());
		up.setOpaque(true);
		up.setBorder(new EmptyBorder(2, 2, 2, 2));
		up.add(nombre_matches, BorderLayout.CENTER);
		setContentPane(top);
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				close();
				dispose();
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
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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
	public static void load(File concor, int widthInChars) {
		if (frame==null) {
			frame = new ConcordanceFrame();
		}
		UnitexFrame.addInternalFrame(frame);
		frame.setTitle("Concordance: " + concor.getAbsolutePath());
		frame.nombre_matches.setText(Util.getHtmlPageTitle(concor));
		try {
			frame.setSelected(true);
			frame.setIcon(false);
		} catch (java.beans.PropertyVetoException e2) {
			e2.printStackTrace();
		}
		Dimension d = frame.getSize();
		int g = widthInChars * 8;
		d.setSize((g < 800) ? g : 800, d.height);
		frame.setSize(d);
		Util.getHtmlPageTitle(concor);
		GlobalPreferenceFrame.addConcordanceFontListener(new FontListener() {
			public void fontChanged(Font font) {
				frame.list.setFont(font);
			}
		});
		frame.list.setFont(new Font(Preferences.getConcordanceFontName(),0,Preferences.getConcordanceFontSize()));
		frame.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		frame.list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				String s=(String) frame.list.getSelectedValue();
				if (s==null || e.getValueIsAdjusting() || TextFrame.getFrame().isSelected()) return;
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
					TextFrame.getFrame().setIcon(false);
					TextFrame.getFrame().text.setSelection(selectionStart,selectionEnd-1);
					TextFrame.getFrame().text.scrollToSelection();
					TextFrame.getFrame().setSelected(true);
			} catch (PropertyVetoException e2) {
				e2.printStackTrace();
			}
			TextAutomatonFrame.loadSentenceFromConcordance(sentenceNumber);
			}});
		frame.list.load(concor);
		frame.setBounds(150, 50, 850, 550);
		frame.setVisible(true);
	}

	/**
	 * Closes the frame.
	 *  
	 */
	public static void close() {
		if (frame==null) return;
		frame.setVisible(false);
		frame.list.reset();
		frame.list.clearSelection();
		UnitexFrame.removeInternalFrame(frame);
	}

}