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
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;


/**
 * @author hhuh
 *
 */
public class MorphemeFrame extends JInternalFrame {
	static MorphemeFrame frame;

	BigTextList text = new BigTextList();
	static boolean FILE_TOO_LARGE = false;

	private MorphemeFrame() {
		super("Morphemes list", true, true, true, true);
		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(true);
		JScrollPane scroll = new JScrollPane(text);
		scroll
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		top.add(constructButtonsPanel(), BorderLayout.NORTH);
		top.add(scroll, BorderLayout.CENTER);
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
		JPanel buttonsPanel = new JPanel(new GridLayout(1, 3));
		buttonsPanel.setOpaque(true);
		Action frequenceAction = new AbstractAction("By Frequence") {
			public void actionPerformed(ActionEvent arg0) {
				loadMorphemes(new File(Config.getCurrentSntDir(),"morph_by_freq.txt"));
				try {
					frame.setIcon(false);
					frame.setSelected(true);
				} catch (java.beans.PropertyVetoException e2) {
					e2.printStackTrace();
				}
			}
		};
		JButton byFrequence = new JButton(frequenceAction);
		Action orderActionByFlechi = new AbstractAction("By Canonical form") {
			public void actionPerformed(ActionEvent arg0) {
                loadMorphemes(new File(Config.getCurrentSntDir(),"morph_by_cano.txt"));
                try {
                    frame.setIcon(false);
                    frame.setSelected(true);
                } catch (java.beans.PropertyVetoException e2) {
                	e2.printStackTrace();
                }
			}
		};
		JButton byFlechiOrder = new JButton(orderActionByFlechi);
		Action orderActionByCanonique = new AbstractAction("By morpheme form") {
			public void actionPerformed(ActionEvent arg0) {
                loadMorphemes(new File(Config.getCurrentSntDir(),"morph_by_flei.txt"));
                try {
                    frame.setIcon(false);
                    frame.setSelected(true);
                } catch (java.beans.PropertyVetoException e2) {
                	e2.printStackTrace();
                }
			}
		};
		JButton byCanoniqueOrder = new JButton(orderActionByCanonique);
		JPanel tmp1 = new JPanel(new BorderLayout());
		tmp1.setOpaque(true);
		tmp1.setBorder(new EmptyBorder(5, 5, 5, 5));
		tmp1.add(byFrequence, BorderLayout.CENTER);
		JPanel tmp2 = new JPanel(new BorderLayout());
		tmp2.setOpaque(true);
		tmp2.setBorder(new EmptyBorder(5, 5, 5, 5));
		tmp2.add(byCanoniqueOrder, BorderLayout.CENTER);
		JPanel tmp3 = new JPanel(new BorderLayout());
		tmp3.setOpaque(true);
		tmp3.setBorder(new EmptyBorder(5, 5, 5, 5));
		tmp3.add(byFlechiOrder, BorderLayout.CENTER);
		buttonsPanel.add(tmp1);
		buttonsPanel.add(tmp2);
		buttonsPanel.add(tmp3);
		return buttonsPanel;
	}

	/**
	 * Initializes the frame
	 *  
	 */
	private static void init() {
		frame = new MorphemeFrame();
		UnitexFrame.addInternalFrame(frame,false);
	}

	/**
	 * Loads a token list
	 * 
	 * @param file
	 *            name of the token list file
	 */
	public static void loadMorphemes(File file) {
		if (frame == null) {
			init();
		}
		frame.text.load(file);
		frame.text.setFont(Config.getCurrentTextFont());
		if (file.length() <= 2) {
			frame.text.setText(Config.EMPTY_FILE_MESSAGE);
		}
		frame.setVisible(true);
		try {
			frame.setIcon(true);
			frame.setSelected(true);
		} catch (java.beans.PropertyVetoException e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * Hides the frame
	 *  
	 */
	public static void hideFrame() {
		if (frame == null) {
			return;
		}
		frame.setVisible(false);
		frame.text.reset();
		try {
			frame.setIcon(false);
		} catch (java.beans.PropertyVetoException e2) {
			e2.printStackTrace();
		}
		System.gc();
	}

}
