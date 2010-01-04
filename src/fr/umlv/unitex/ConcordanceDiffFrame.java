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

package fr.umlv.unitex;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;


/**
 * This class describes a frame that can show an HTML condordance diff file.
 * 
 * @author Sébastien Paumier
 */
public class ConcordanceDiffFrame extends JInternalFrame {

	static ConcordanceDiffFrame frame;
	
	BigConcordanceDiff list=new BigConcordanceDiff();
		
	/**
	 * Constructs a new empty <code>ConcordanceDiffFrame</code>.
	 */
	public ConcordanceDiffFrame() {
		super("", true, true, true, true);
		JScrollPane scroll = new JScrollPane(list);
		JPanel middle = new JPanel(new BorderLayout());
		middle.setOpaque(true);
		middle.add(scroll, BorderLayout.CENTER);
		JPanel top=new JPanel(new GridLayout(3,1));
		top.setBackground(Color.WHITE);
		top.setBorder(new EmptyBorder(2,2,2,2));
		top.add(new JLabel("<html><body><font color=\"#0000FF\">Blue:</font>&nbsp;identical sequences</body></html>"));
		top.add(new JLabel("<html><body><font color=\"#FF0000\">Red:</font>&nbsp;similar but different sequences</body></html>"));
		top.add(new JLabel("<html><body><font color=\"#00FF00\">Green:</font>&nbsp;sequences that occur in only one of the two concordances</body></html>"));
		middle.add(top,BorderLayout.NORTH);
		setContentPane(middle);
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				close();
				dispose();
			}
		});
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	/**
	 * Constructs a new <code>ConcordanceDiffFrame</code> if needed and
	 * loads in it an HTML file. 
	 * 
	 * @param concor the HTML file
     * @param widthInChars width of a line in chars. Equals to the sum of left and right 
      *       context lengths
	 */
	public static void load(File concor, int widthInChars) {
		if (frame==null) {
			frame = new ConcordanceDiffFrame();
		}
		UnitexFrame.addInternalFrame(frame,false);
		frame.setTitle("Concordance Diff");
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
		frame.list.setFont(new Font(Preferences.getConcordanceFontName(),0,Preferences.getConcordanceFontSize()));
		GlobalPreferenceFrame.addConcordanceFontListener(new FontListener() {
			public void fontChanged(Font font) {
				frame.list.setFont(font);
			}
		});
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
	