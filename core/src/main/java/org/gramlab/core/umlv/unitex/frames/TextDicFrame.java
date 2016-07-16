/*
 * Unitex
 *
 * Copyright (C) 2001-2016 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package org.gramlab.core.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.config.PreferencesListener;
import org.gramlab.core.umlv.unitex.config.PreferencesManager;
import org.gramlab.core.umlv.unitex.io.UnicodeIO;
import org.gramlab.core.umlv.unitex.text.BigTextList;

/**
 * This class describes a frame used to display current corpus's DLF, DLC and
 * ERR files.
 * 
 * @author Sébastien Paumier
 */
public class TextDicFrame extends TabbableInternalFrame {
	final BigTextList dlf = new BigTextList(true);
	final BigTextList dlc = new BigTextList(true);
	final BigTextList err = new BigTextList();
	private final JLabel dlfLabel = new JLabel("");
	private final JLabel dlcLabel = new JLabel("");
	private final JLabel errLabel = new JLabel("");
	JScrollBar dlfScrollbar;
	JScrollBar dlcScrollbar;
	JScrollBar errScrollbar;
	private JScrollPane dlfScroll;
	private JScrollPane dlcScroll;
	private JScrollPane errScroll;
	private final JCheckBox tags_err = new JCheckBox(
			"Filter unknown words with tags.ind", false);
	File text_dir;

	TextDicFrame() {
		super("", true, true, true, true);
		setContentPane(constructPanel());
		pack();
		setBounds(250, 300, 500, 500);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				try {
					setIcon(true);
				} catch (final java.beans.PropertyVetoException e2) {
					e2.printStackTrace();
				}
			}

			@Override
			public void internalFrameDeiconified(InternalFrameEvent e) {
				final Timer t = new Timer(400, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e2) {
						dlfScrollbar.setValue(0);
						dlcScrollbar.setValue(0);
						errScrollbar.setValue(0);
					}
				});
				t.setRepeats(false);
				t.start();
			}
		});
		final boolean rightToLeftForText = ConfigManager.getManager()
				.isRightToLeftForText(null);
		dlf.setComponentOrientation(rightToLeftForText ? ComponentOrientation.RIGHT_TO_LEFT
				: ComponentOrientation.LEFT_TO_RIGHT);
		dlc.setComponentOrientation(rightToLeftForText ? ComponentOrientation.RIGHT_TO_LEFT
				: ComponentOrientation.LEFT_TO_RIGHT);
		err.setComponentOrientation(rightToLeftForText ? ComponentOrientation.RIGHT_TO_LEFT
				: ComponentOrientation.LEFT_TO_RIGHT);
		PreferencesManager.addPreferencesListener(new PreferencesListener() {
			@Override
			public void preferencesChanged(String language) {
				final Font font = ConfigManager.getManager().getTextFont(null);
				dlf.setFont(font);
				dlc.setFont(font);
				err.setFont(font);
				final boolean rightToLeftForText2 = ConfigManager.getManager()
						.isRightToLeftForText(null);
				dlf.setComponentOrientation(rightToLeftForText2 ? ComponentOrientation.RIGHT_TO_LEFT
						: ComponentOrientation.LEFT_TO_RIGHT);
				dlc.setComponentOrientation(rightToLeftForText2 ? ComponentOrientation.RIGHT_TO_LEFT
						: ComponentOrientation.LEFT_TO_RIGHT);
				err.setComponentOrientation(rightToLeftForText2 ? ComponentOrientation.RIGHT_TO_LEFT
						: ComponentOrientation.LEFT_TO_RIGHT);
			}
		});
	}

	private JSplitPane constructPanel() {
		final JComponent dic = constructDicPanel();
		dic.setMinimumSize(new Dimension(0, 0));
		final JComponent err2 = constructErrPanel();
		err2.setMinimumSize(new Dimension(0, 0));
		final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				true, dic, err2) {
			boolean firstTime = false;

			@Override
			protected void paintComponent(java.awt.Graphics g) {
				if (!firstTime) {
					firstTime = true;
					setDividerLocation(0.5);
				}
			}
		};
		return split;
	}

	private JSplitPane constructDicPanel() {
		final boolean rightToLeftForText = ConfigManager.getManager()
				.isRightToLeftForText(null);
		dlfScroll = new JScrollPane(dlf,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		dlfScroll
				.setComponentOrientation(rightToLeftForText ? ComponentOrientation.RIGHT_TO_LEFT
						: ComponentOrientation.LEFT_TO_RIGHT);
		dlfScrollbar = dlfScroll.getHorizontalScrollBar();
		dlcScroll = new JScrollPane(dlc,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		dlcScroll
				.setComponentOrientation(rightToLeftForText ? ComponentOrientation.RIGHT_TO_LEFT
						: ComponentOrientation.LEFT_TO_RIGHT);
		dlcScrollbar = dlcScroll.getHorizontalScrollBar();
		final JPanel up = new JPanel(new BorderLayout());
		up.setBorder(new EmptyBorder(5, 5, 5, 5));
		up.add(dlfLabel, BorderLayout.NORTH);
		final JPanel tmp = new JPanel(new BorderLayout());
		tmp.setBorder(BorderFactory.createLoweredBevelBorder());
		tmp.add(dlfScroll, BorderLayout.CENTER);
		up.add(tmp, BorderLayout.CENTER);
		final JPanel down = new JPanel(new BorderLayout());
		down.setBorder(new EmptyBorder(5, 5, 5, 5));
		down.add(dlcLabel, BorderLayout.NORTH);
		final JPanel tmp2 = new JPanel(new BorderLayout());
		tmp2.setBorder(BorderFactory.createLoweredBevelBorder());
		tmp2.add(dlcScroll, BorderLayout.CENTER);
		down.add(tmp2, BorderLayout.CENTER);
		up.setMinimumSize(new Dimension(0, 0));
		down.setMinimumSize(new Dimension(0, 0));
		final JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				true, up, down) {
			boolean firstTime = false;

			@Override
			protected void paintComponent(java.awt.Graphics g) {
				if (!firstTime) {
					firstTime = true;
					setDividerLocation(0.5);
				}
			}
		};
		return split;
	}

	private JPanel constructErrPanel() {
		final JPanel errPanel = new JPanel(new BorderLayout());
		errPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		errScroll = new JScrollPane(err,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		errScroll
				.setComponentOrientation(ConfigManager.getManager()
						.isRightToLeftForText(null) ? ComponentOrientation.RIGHT_TO_LEFT
						: ComponentOrientation.LEFT_TO_RIGHT);
		errScrollbar = errScroll.getHorizontalScrollBar();
		final JPanel p = new JPanel(new GridLayout(2, 1));
		p.add(errLabel);
		p.add(tags_err);
		tags_err.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				err.reset();
				loadERR();
			}
		});
		errPanel.add(p, BorderLayout.NORTH);
		final JPanel tmp = new JPanel(new BorderLayout());
		tmp.setBorder(BorderFactory.createLoweredBevelBorder());
		tmp.add(errScroll, BorderLayout.CENTER);
		errPanel.add(tmp, BorderLayout.CENTER);
		return errPanel;
	}

	void loadDLF() {
		final File FILE = new File(text_dir, "dlf");
		dlf.setFont(ConfigManager.getManager().getTextFont(null));
		final String n = UnicodeIO.readFirstLine(new File(text_dir, "dlf.n"));
		String message = "DLF";
		if (n != null) {
			message = message + ": " + n + " simple-word lexical entr";
			if (Integer.parseInt(n) <= 1)
				message = message + "y";
			else
				message = message + "ies";
		}
		if (!FILE.exists() || FILE.length() <= 2) {
			dlf.setText(Config.EMPTY_FILE_MESSAGE);
			dlfLabel.setText("DLF: simple-word lexical entries");
		} else {
			dlf.load(FILE);
			dlfLabel.setText(message);
		}
	}

	void loadDLC() {
		final File FILE = new File(text_dir, "dlc");
		dlc.setFont(ConfigManager.getManager().getTextFont(null));
		final String n = UnicodeIO.readFirstLine(new File(text_dir, "dlc.n"));
		String message = "DLC";
		if (n != null) {
			message = message + ": " + n + " compound lexical entr";
			if (Integer.parseInt(n) <= 1)
				message = message + "y";
			else
				message = message + "ies";
		}
		if (!FILE.exists() || FILE.length() <= 2) {
			dlc.setText(Config.EMPTY_FILE_MESSAGE);
			dlcLabel.setText("DLC: compound lexical entries");
		} else {
			dlc.load(FILE);
			dlcLabel.setText(message);
		}
	}

	void loadERR() {
		final boolean tags_errors = tags_err.isSelected();
		final File FILE = new File(text_dir, tags_errors ? "tags_err" : "err");
		err.setFont(ConfigManager.getManager().getTextFont(null));
		final String n = UnicodeIO.readFirstLine(new File(text_dir,
				tags_errors ? "tags_err.n" : "err.n"));
		String message = tags_errors ? "TAGS_ERR" : "ERR";
		if (n != null) {
			message = message + ": " + n + " unknown simple word";
			if (Integer.parseInt(n) > 1)
				message = message + "s";
		}
		if (!FILE.exists() || FILE.length() <= 2) {
			err.setText(Config.EMPTY_FILE_MESSAGE);
			errLabel.setText("ERR: unknown simple words");
		} else {
			err.load(FILE);
			errLabel.setText(message);
		}
	}

	/**
	 * Loads "dlf", "dlc" and "err" files contained in a directory, and shows
	 * the frame
	 * 
	 * @param text_dir
	 *            directory to look in
	 */
	void loadTextDic(File directory) {
		this.text_dir = directory;
		loadDLF();
		loadDLC();
		loadERR();
		setTitle("Word Lists in " + text_dir);
		final boolean rightToLeftForText = ConfigManager.getManager()
				.isRightToLeftForText(null);
		dlf.setComponentOrientation(rightToLeftForText ? ComponentOrientation.RIGHT_TO_LEFT
				: ComponentOrientation.LEFT_TO_RIGHT);
		dlc.setComponentOrientation(rightToLeftForText ? ComponentOrientation.RIGHT_TO_LEFT
				: ComponentOrientation.LEFT_TO_RIGHT);
		err.setComponentOrientation(rightToLeftForText ? ComponentOrientation.RIGHT_TO_LEFT
				: ComponentOrientation.LEFT_TO_RIGHT);
		dlfScroll
				.setComponentOrientation(rightToLeftForText ? ComponentOrientation.RIGHT_TO_LEFT
						: ComponentOrientation.LEFT_TO_RIGHT);
		dlcScroll
				.setComponentOrientation(rightToLeftForText ? ComponentOrientation.RIGHT_TO_LEFT
						: ComponentOrientation.LEFT_TO_RIGHT);
		errScroll
				.setComponentOrientation(rightToLeftForText ? ComponentOrientation.RIGHT_TO_LEFT
						: ComponentOrientation.LEFT_TO_RIGHT);
		final Timer t = new Timer(400, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dlfScrollbar.setValue(0);
				dlcScrollbar.setValue(0);
				errScrollbar.setValue(0);
			}
		});
		t.setRepeats(false);
		t.start();
	}

	/**
	 * Hides the frame
	 */
	void hideFrame() {
		text_dir = null;
		dlf.reset();
		dlc.reset();
		err.reset();
		setVisible(false);
		System.gc();
	}

	@Override
	public String getTabName() {
		return "Word Lists";
	}
}
