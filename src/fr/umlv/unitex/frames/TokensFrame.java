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

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import fr.umlv.unitex.BigTextList;
import fr.umlv.unitex.Config;
import fr.umlv.unitex.FontListener;
import fr.umlv.unitex.GlobalPreferenceFrame;


/**
 * This class describes a frame used to display current corpus's token lists.
 * 
 * @author Sébastien Paumier
 *  
 */
public class TokensFrame extends JInternalFrame {

	BigTextList text = new BigTextList(false);

	TokensFrame() {
		super("Token list", true, true, true, true);
		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(true);
		top.add(constructButtonsPanel(), BorderLayout.NORTH);
		top.add(new JScrollPane(text), BorderLayout.CENTER);
		setContentPane(top);
		pack();
		setBounds(50, 200, 300, 450);
		setVisible(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				try {
					setIcon(true);
				} catch (java.beans.PropertyVetoException e2) {
					e2.printStackTrace();
				}
			}
		});
		GlobalPreferenceFrame.addTextFontListener(new FontListener() {
			public void fontChanged(Font font) {
				text.setFont(font);
			}});
	}

	private JPanel constructButtonsPanel() {
		JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
		buttonsPanel.setOpaque(true);
		Action frequenceAction = new AbstractAction("By Frequence") {
			public void actionPerformed(ActionEvent arg0) {
				loadTokens(new File(Config.getCurrentSntDir(),"tok_by_freq.txt"));
				try {
					setIcon(false);
					setSelected(true);
				} catch (java.beans.PropertyVetoException e2) {
					e2.printStackTrace();
				}
			}
		};
		JButton byFrequence = new JButton(frequenceAction);
		Action orderAction = new AbstractAction("By Char Order") {
			public void actionPerformed(ActionEvent arg0) {
                loadTokens(new File(Config.getCurrentSntDir(),"tok_by_alph.txt"));
                try {
                    setIcon(false);
                    setSelected(true);
                } catch (java.beans.PropertyVetoException e2) {
                	e2.printStackTrace();
                }
			}
		};
		JButton byCharOrder = new JButton(orderAction);

		JPanel tmp1 = new JPanel(new BorderLayout());
		tmp1.setOpaque(true);
		tmp1.setBorder(new EmptyBorder(5, 5, 5, 5));
		tmp1.add(byFrequence, BorderLayout.CENTER);
		JPanel tmp2 = new JPanel(new BorderLayout());
		tmp2.setOpaque(true);
		tmp2.setBorder(new EmptyBorder(5, 5, 5, 5));
		tmp2.add(byCharOrder, BorderLayout.CENTER);
		buttonsPanel.add(tmp1);
		buttonsPanel.add(tmp2);
		return buttonsPanel;
	}

	/**
	 * Loads a token list
	 * 
	 * @param file
	 *            name of the token list file
	 */
	void loadTokens(File file) {
		text.setFont(Config.getCurrentTextFont());
		if (file.length() <= 2) {
			text.setText(Config.EMPTY_FILE_MESSAGE);
		} else {
			text.load(file);
		}
	}

	/**
	 * Hides the frame
	 *  
	 */
	void hideFrame() {
		text.reset();
		setVisible(false);
		System.gc();
	}

}
