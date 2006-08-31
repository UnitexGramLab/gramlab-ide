/*
 * Created on 24 déc. 2004
 *
 */
package fr.umlv.unitex;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * @author hhuh
 *
 */
public class MorphemeFrame extends JInternalFrame {
	static MorphemeFrame frame;

	MyTextArea text = new MyTextArea();
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
		UnitexFrame.addInternalFrame(frame);
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
		frame.text.killTimer();
		frame.text.setFont(Config.getCurrentTextFont());
		frame.text.setLineWrap(true);
		frame.text.setEditable(false);
		if (file.length() <= 2) {
			FILE_TOO_LARGE = true;
			frame.text.setDocument(new PlainDocument());
			frame.text.setText(Config.EMPTY_FILE_MESSAGE);
		} else if (file.length() < Preferences.pref.MAX_TEXT_FILE_SIZE) {
			try {
				frame.text.load(file);
			} catch (java.io.IOException e) {
				FILE_TOO_LARGE = true;
				frame.text.setDocument(new PlainDocument());
				frame.text.setText(Config.ERROR_WHILE_READING_FILE_MESSAGE);
				return;
			}
			FILE_TOO_LARGE = false;
		} else {
			FILE_TOO_LARGE = true;
			frame.text.setDocument(new PlainDocument());
			frame.text.setText(Config.FILE_TOO_LARGE_MESSAGE);
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
		frame.text.killTimer();
		frame.setVisible(false);
		frame.text.setDocument(new PlainDocument());
		try {
			frame.setIcon(false);
		} catch (java.beans.PropertyVetoException e2) {
			e2.printStackTrace();
		}
		System.gc();
	}

}
