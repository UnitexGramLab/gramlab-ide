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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.config.PreferencesListener;
import org.gramlab.core.umlv.unitex.config.PreferencesManager;
import org.gramlab.core.umlv.unitex.text.BigTextList;

/**
 * This class describes a frame used to display current corpus's token lists.
 * 
 * @author Sébastien Paumier
 */
public class TokensFrame extends TabbableInternalFrame {
	final BigTextList text = new BigTextList(false);

	TokensFrame() {
		super("Token list", true, true, true, true);
		final JPanel top = new JPanel(new BorderLayout());
		top.add(constructButtonsPanel(), BorderLayout.NORTH);
		top.add(new JScrollPane(text), BorderLayout.CENTER);
		setContentPane(top);
		pack();
		setBounds(50, 200, 300, 450);
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
		});
		PreferencesManager.addPreferencesListener(new PreferencesListener() {
			@Override
			public void preferencesChanged(String language) {
				text.setFont(ConfigManager.getManager().getTextFont(null));
			}
		});
	}

	private JPanel constructButtonsPanel() {
		final JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
		final Action frequenceAction = new AbstractAction("By Frequence") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadTokens(new File(Config.getCurrentSntDir(),
						"tok_by_freq.txt"));
				try {
					setIcon(false);
					setSelected(true);
				} catch (final java.beans.PropertyVetoException e2) {
					e2.printStackTrace();
				}
			}
		};
		final JButton byFrequence = new JButton(frequenceAction);
		final Action orderAction = new AbstractAction("By Char Order") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadTokens(new File(Config.getCurrentSntDir(),
						"tok_by_alph.txt"));
				try {
					setIcon(false);
					setSelected(true);
				} catch (final java.beans.PropertyVetoException e2) {
					e2.printStackTrace();
				}
			}
		};
		final JButton byCharOrder = new JButton(orderAction);
		final JPanel tmp1 = new JPanel(new BorderLayout());
		tmp1.setBorder(new EmptyBorder(5, 5, 5, 5));
		tmp1.add(byFrequence, BorderLayout.CENTER);
		final JPanel tmp2 = new JPanel(new BorderLayout());
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
		text.setFont(ConfigManager.getManager().getTextFont(null));
		if (file.length() <= 2) {
			text.setText(Config.EMPTY_FILE_MESSAGE);
		} else {
			text.load(file);
		}
	}

	/**
	 * Hides the frame
	 */
	void hideFrame() {
		text.reset();
		setVisible(false);
		System.gc();
	}

	@Override
	public String getTabName() {
		return "Tokens";
	}
}
