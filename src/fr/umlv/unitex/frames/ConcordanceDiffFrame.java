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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import fr.umlv.unitex.BigConcordanceDiff;
import fr.umlv.unitex.FontListener;
import fr.umlv.unitex.Preferences;
import fr.umlv.unitex.Util;


/**
 * This class describes a frame that can show an HTML condordance diff file.
 * 
 * @author S&bastien Paumier
 */
public class ConcordanceDiffFrame extends JInternalFrame {

	BigConcordanceDiff list=new BigConcordanceDiff();
		
	
	/**
	 * Constructs a new empty <code>ConcordanceDiffFrame</code>.
	 */
	ConcordanceDiffFrame() {
		super("Concordance Diff", true, true, true, true);
		JScrollPane scroll = new JScrollPane(list);
		JPanel middle = new JPanel(new BorderLayout());
		middle.add(scroll, BorderLayout.CENTER);
		JPanel top=new JPanel(new GridLayout(3,1));
		top.setBackground(Color.WHITE);
		top.setBorder(new EmptyBorder(2,2,2,2));
		top.add(new JLabel("<html><body><font color=\"#0000FF\">Blue:</font>&nbsp;identical sequences</body></html>"));
		top.add(new JLabel("<html><body><font color=\"#FF0000\">Red:</font>&nbsp;similar but different sequences</body></html>"));
		top.add(new JLabel("<html><body><font color=\"#00FF00\">Green:</font>&nbsp;sequences that occur in only one of the two concordances</body></html>"));
		middle.add(top,BorderLayout.NORTH);
		setContentPane(middle);
		list.setFont(new Font(Preferences.getConcordanceFontName(),0,Preferences.getConcordanceFontSize()));
		UnitexFrame.getFrameManager().getGlobalPreferencesFrame().addConcordanceFontListener(new FontListener() {
			public void fontChanged(Font font) {
				list.setFont(font);
			}
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				setVisible(false);
				list.reset();
				list.clearSelection();
			}
		});
		setBounds(150, 50, 850, 550);
	}

	/**
	 * Constructs a new <code>ConcordanceDiffFrame</code> if needed and
	 * loads in it an HTML file. 
	 * 
	 * @param concor the HTML file
     * @param widthInChars width of a line in chars. Equals to the sum of left and right 
      *       context lengths
	 */
	void load(File concor, int widthInChars) {
		Dimension d = getSize();
		int g = widthInChars * 8;
		d.setSize((g < 800) ? g : 800, d.height);
		Util.getHtmlPageTitle(concor);
		list.load(concor);
	}

}
	