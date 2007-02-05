/*
 * Unitex
 *
 * Copyright (C) 2001-2007 Université de Marne-la-Vallée <unitex@univ-mlv.fr>
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
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * This class describes a frame that can show an HTML file. It is used to show
 * concordances.
 * 
 * @author Sébastien Paumier
 *  
 */
public class ConcordanceFrame extends JInternalFrame {

	JEditorPane text = new JEditorPane();
	private JLabel nombre_matches = new JLabel("");
	JRadioButton enableLinks = new JRadioButton("Enable links");
	JRadioButton enableEdition = new JRadioButton("Allow concordance edition");

	MyHyperlinkListener listener = new MyHyperlinkListener();
	File concordanceFile;

	/**
	 * Constructs a new empty <code>ConcordanceFrame</code>.
	 *  
	 */
	public ConcordanceFrame() {
		super("", true, true, true, true);
		text.setEditable(false);
		JScrollPane scroll = new JScrollPane(text);
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
		JPanel buttons = new JPanel(new BorderLayout());
		buttons.add(enableLinks, BorderLayout.CENTER);
		buttons.add(enableEdition, BorderLayout.EAST);
		enableLinks.setSelected(true);
		enableEdition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				text.setEditable(true);
				text.removeHyperlinkListener(listener);
			}
		});
		enableLinks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				text.setEditable(false);
				text.addHyperlinkListener(listener);
			}
		});
		ButtonGroup bg = new ButtonGroup();
		bg.add(enableLinks);
		bg.add(enableEdition);
		up.add(buttons, BorderLayout.EAST);
		top.add(up, BorderLayout.NORTH);
		setContentPane(top);
		pack();
		setBounds(150, 50, 850, 550);
		setVisible(false);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				setVisible(false);
				dispose();
			}
		});
		text.addHyperlinkListener(listener);
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
		ConcordanceFrame frame = new ConcordanceFrame();
		UnitexFrame.addInternalFrame(frame);
		frame.concordanceFile = concor;
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
		try {
      Util.getHtmlPageTitle(concor);
			frame.text.setPage(concor.toURL());
			frame.setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the frame.
	 *  
	 */
	public void close() {
		setVisible(false);
		UnitexFrame.removeInternalFrame(this);
	}

	class MyHyperlinkListener implements HyperlinkListener {
		public void hyperlinkUpdate(HyperlinkEvent e) {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				StringTokenizer s = new StringTokenizer(e.getDescription());
				int a = new Integer(s.nextToken()).intValue();
				int b = new Integer(s.nextToken()).intValue();
				int c = new Integer(s.nextToken()).intValue();
				try {
						TextFrame.getFrame().setIcon(false);
						TextFrame.getFrame().text.setSelection(a,b-1);
						TextFrame.getFrame().text.scrollToSelection();
						TextFrame.getFrame().setSelected(true);
				} catch (PropertyVetoException e2) {
					e2.printStackTrace();
				}
				TextAutomatonFrame.loadSentenceFromConcordance(c);
			}
		}
	}

}