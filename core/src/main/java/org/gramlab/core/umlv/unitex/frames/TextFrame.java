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
package fr.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import fr.umlv.unitex.DropTargetManager;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.config.PreferencesListener;
import fr.umlv.unitex.config.PreferencesManager;
import fr.umlv.unitex.io.Encoding;
import fr.umlv.unitex.io.UnicodeIO;
import fr.umlv.unitex.text.BigTextArea;

/**
 * This class describes a frame used to display the current corpus.
 * 
 * @author Sébastien Paumier
 */
public class TextFrame extends TabbableInternalFrame {
	final BigTextArea text = new BigTextArea();
	private final JLabel ligne1 = new JLabel("");
	private final JLabel ligne2 = new JLabel("");

	TextFrame() {
		super("", true, false, true, true);
		DropTargetManager.getDropTarget().newDropTarget(this);
		final JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(true);
		top.setBorder(new EmptyBorder(2, 2, 2, 2));
		final JPanel middle = new JPanel(new BorderLayout());
		middle.setOpaque(true);
		middle.setBorder(BorderFactory.createLoweredBevelBorder());
		middle.add(text);
		final PreferencesListener preferencesListener = new PreferencesListener() {
			@Override
			public void preferencesChanged(String language) {
				text.setFont(ConfigManager.getManager().getTextFont(null));
				text.setComponentOrientation(ConfigManager.getManager()
						.isRightToLeftForText(null) ? ComponentOrientation.RIGHT_TO_LEFT
						: ComponentOrientation.LEFT_TO_RIGHT);
				text.revalidate();
				text.repaint();
			}
		};
		PreferencesManager.addPreferencesListener(preferencesListener);
		final JPanel up = new JPanel(new GridLayout(2, 1));
		up.setBorder(new EmptyBorder(2, 2, 2, 2));
		up.add(ligne1);
		up.add(ligne2);
		top.add(up, BorderLayout.NORTH);
		top.add(middle, BorderLayout.CENTER);
		setContentPane(top);
		pack();
		setBounds(50, 50, 800, 500);
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				PreferencesManager
						.removePreferencesListener(preferencesListener);
			}
		});
	}

	private void loadStatistics() {
		ligne1.setText("");
		ligne2.setText("");
		InputStreamReader reader;
		String s;
		s = UnicodeIO.readFirstLine(new File(Config.getCurrentSntDir(),
				"stats.n"));
		if (s != null)
			ligne1.setText(" " + s);
		s = UnicodeIO
				.readFirstLine(new File(Config.getCurrentSntDir(), "dlf.n"));
		if (s == null)
			return;
		final int dlf_entries = Integer.parseInt(s);
		s = UnicodeIO
				.readFirstLine(new File(Config.getCurrentSntDir(), "dlc.n"));
		if (s == null)
			return;
		final int dlc_entries = Integer.parseInt(s);
		s = UnicodeIO
				.readFirstLine(new File(Config.getCurrentSntDir(), "err.n"));
		if (s == null)
			return;
		final int err_entries = Integer.parseInt(s);
		final File f = new File(Config.getCurrentSntDir(), "stat_dic.n");
		if (!f.exists()) {
			return;
		}
		if (!f.canRead()) {
			return;
		}
		int simple_total;
		int compound_total;
		int err_total;
		try {
			reader = Encoding.getInputStreamReader(f);
			if (reader == null)
				return;
			simple_total = Integer.parseInt(UnicodeIO.readLine(reader));
			compound_total = Integer.parseInt(UnicodeIO.readLine(reader));
			err_total = Integer.parseInt(UnicodeIO.readLine(reader));
			reader.close();
		} catch (final FileNotFoundException e) {
			return;
		} catch (final IOException e) {
			return;
		}
		s = " " + String.valueOf(simple_total) + " occurrence"
				+ (simple_total > 1 ? "s" : "") + " (";
		s = s + String.valueOf(dlf_entries) + " DLF entries) simple word";
		if (dlf_entries > 1)
			s = s + "s";
		s = s + ", ";
		s = s + String.valueOf(compound_total) + " occurrence"
				+ (compound_total > 1 ? "s" : "") + " (";
		s = s + String.valueOf(dlc_entries) + " DLC entries) compound word";
		if (dlc_entries > 1)
			s = s + "s";
		s = s + ", ";
		s = s + String.valueOf(err_total) + " occurrence"
				+ (err_total > 1 ? "s" : "") + " (";
		s = s + String.valueOf(err_entries) + " ERR lines) unknown word";
		if (err_entries > 1)
			s = s + "s";
		ligne2.setText(s);
	}

	/**
	 * Loads a corpus
	 * 
	 * @param sntFile
	 *            name of the corpus file
	 */
	void loadText(File sntFile) {
		loadStatistics();
		text.setFont(ConfigManager.getManager().getTextFont(null));
		text.setComponentOrientation(ConfigManager.getManager()
				.isRightToLeftForText(null) ? ComponentOrientation.RIGHT_TO_LEFT
				: ComponentOrientation.LEFT_TO_RIGHT);
		if (sntFile.length() <= 2) {
			text.setText(Config.EMPTY_FILE_MESSAGE);
		} else {
			text.load(sntFile);
		}
		setTitle(sntFile.getName() + " (" + sntFile.getParent() + ")");
	}

	public BigTextArea getText() {
		return text;
	}

	@Override
	public String getTabName() {
		final String t = getTitle();
		if (t.equals(""))
			return "Text";
		return new File(t).getName();
	}
}
