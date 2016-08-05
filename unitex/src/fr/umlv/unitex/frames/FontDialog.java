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
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.umlv.unitex.FontInfo;

/**
 * This class describes a font selection dialog box.
 * 
 * @author Sébastien Paumier
 */
class FontDialog extends JDialog {
	final JTextField name = new JTextField(10);
	final JTextField style = new JTextField(10);
	final JTextField size = new JTextField("  ");
	final JTextField script = new JTextField(10);
	final JTextField example = new JTextField(6);
	JList fontList;
	JList styleList;
	JList sizeList;
	JList scriptList;
	Hashtable<String, Integer> styles;
	Hashtable<String, Integer> ranges;
	private static final int ASCII_BASE = 0x0000;
	private static final int GREEK_BASE = 0x0370;
	private static final int HEBREW_BASE = 0x0590;
	private static final int ARABIC_BASE = 0x0600;
	private static final int THAI_BASE = 0x0E00;
	private static final int GEORGIAN_BASE = 0x10A0;
	private static final int HIRAGANA_BASE = 0x3040;
	private static final int KATAKANA_BASE = 0x30A0;
	private static final int HANGUL_BASE = 0x3130;
	private static final int KANJI_BASE = 0x4E00;
	FontInfo info;

	/**
	 * Creates a new font dialog box.
	 * 
	 * @param in
	 *            indicates if we select an input or an output font for graphs.
	 */
	public FontDialog(FontInfo i) {
		super(UnitexFrame.mainFrame, "Font", true);
		init();
		configureFont(i);
		setContentPane(constructPanel());
		pack();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				info = null;
			}
		});
		setLocationRelativeTo(UnitexFrame.mainFrame);
	}

	void configureFont(FontInfo i) {
		if (i == null) {
			throw new IllegalArgumentException(
					"Cannot configure a null font info");
		}
		info = i.clone();
		name.setText(i.getFont().getName());
		style.setText("" + i.getFont().getStyle());
		size.setText("" + i.getSize());
		fontList.clearSelection();
		styleList.clearSelection();
		sizeList.clearSelection();
		refresh();
	}

	void refresh() {
		/* name */
		final String s = (String) (fontList.getSelectedValue());
		if (s != null) {
			name.setText(s);
		}/* style */
		int fontStyle = info.getFont().getStyle();
		/*
		 * We can use getSelectedIndex() instead of getSelectedValue() because
		 * style values are 0 1 2 3
		 */
		final int n = styleList.getSelectedIndex();
		if (n != -1) {
			style.setText((String) styleList.getSelectedValue());
			fontStyle = n;
		}/* size */
		final Integer j = (Integer) (sizeList.getSelectedValue());
		if (j != null) {
			size.setText(j + "");
			info.setSize(j);
		}
		/* updating font */
		info.setFont(new Font(name.getText(), fontStyle,
				(int) (info.getSize() / 0.72)));
		example.setFont(info.getFont());
	}

	public FontInfo getFontInfo() {
		return info;
	}

	private JPanel constructPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(constructUpPanel(), BorderLayout.CENTER);
		panel.add(constructDownPanel(), BorderLayout.SOUTH);
		return panel;
	}

	private JPanel constructUpPanel() {
		final JPanel upPanel = new JPanel(new BorderLayout());
		upPanel.add(constructFontPanel(), BorderLayout.WEST);
		upPanel.add(constructStylePanel(), BorderLayout.CENTER);
		upPanel.add(constructSizePanel(), BorderLayout.EAST);
		return upPanel;
	}

	private JPanel constructDownPanel() {
		final JPanel downPanel = new JPanel(new BorderLayout());
		downPanel.add(constructScriptPanel(), BorderLayout.WEST);
		downPanel.add(constructExampleAndButtonPanel(), BorderLayout.CENTER);
		return downPanel;
	}

	private JPanel constructFontPanel() {
		final JPanel fontPanel = new JPanel(new BorderLayout());
		fontPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		final JPanel p = new JPanel();
		p.setLayout(new GridLayout(2, 1));
		p.add(new JLabel(" Font: "));
		p.add(name);
		fontPanel.add(p, BorderLayout.NORTH);
		fontPanel.add(new JScrollPane(fontList), BorderLayout.CENTER);
		return fontPanel;
	}

	private JPanel constructStylePanel() {
		final JPanel stylePanel = new JPanel(new BorderLayout());
		stylePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		final JPanel p = new JPanel();
		p.setLayout(new GridLayout(2, 1));
		p.add(new JLabel(" Style: "));
		p.add(style);
		stylePanel.add(p, BorderLayout.NORTH);
		stylePanel.add(new JScrollPane(styleList), BorderLayout.CENTER);
		return stylePanel;
	}

	private JPanel constructSizePanel() {
		final JPanel sizePanel = new JPanel(new BorderLayout());
		sizePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		final JPanel p = new JPanel();
		p.setLayout(new GridLayout(2, 1));
		p.add(new JLabel(" Size: "));
		p.add(size);
		sizePanel.add(p, BorderLayout.NORTH);
		sizePanel.add(new JScrollPane(sizeList), BorderLayout.CENTER);
		return sizePanel;
	}

	private JPanel constructScriptPanel() {
		final JPanel scriptPanel = new JPanel(new BorderLayout());
		scriptPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		final JPanel p = new JPanel();
		p.setLayout(new GridLayout(2, 1));
		p.add(new JLabel(" Script: "));
		p.add(script);
		scriptPanel.add(p, BorderLayout.NORTH);
		scriptPanel.add(new JScrollPane(scriptList), BorderLayout.CENTER);
		return scriptPanel;
	}

	private JPanel constructExampleAndButtonPanel() {
		final JPanel exampleAndButtonPanel = new JPanel(new BorderLayout());
		exampleAndButtonPanel.setBorder(new EmptyBorder(0, 0, 0, 5));
		exampleAndButtonPanel.add(constructExamplePanel(), BorderLayout.CENTER);
		exampleAndButtonPanel.add(constructButtonPanel(), BorderLayout.SOUTH);
		return exampleAndButtonPanel;
	}

	private JPanel constructExamplePanel() {
		final JPanel examplePanel = new JPanel(new BorderLayout());
		examplePanel.setBorder(new TitledBorder("Example"));
		examplePanel.add(new JPanel(), BorderLayout.WEST);
		examplePanel.add(example, BorderLayout.CENTER);
		examplePanel.add(new JPanel(), BorderLayout.EAST);
		return examplePanel;
	}

	private JPanel constructButtonPanel() {
		final JPanel buttonPanel = new JPanel();
		final Action okAction = new AbstractAction("OK") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		};
		final Action cancelAction = new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				/*
				 * After cancelling, a call to getFontInfo() return null
				 */
				info = null;
				setVisible(false);
			}
		};
		final JButton OK = new JButton(okAction);
		final JButton CANCEL = new JButton(cancelAction);
		buttonPanel.add(OK);
		buttonPanel.add(CANCEL);
		return buttonPanel;
	}

	private void init() {
		name.setDisabledTextColor(Color.white);
		name.setBackground(Color.white);
		name.setText("");
		style.setEditable(false);
		style.setDisabledTextColor(Color.white);
		style.setBackground(Color.white);
		style.setText("");
		style.setEditable(false);
		size.setDisabledTextColor(Color.white);
		size.setBackground(Color.white);
		size.setText("");
		size.setEditable(false);
		script.setDisabledTextColor(Color.white);
		script.setBackground(Color.white);
		script.setText("");
		script.setEditable(false);
		styles = new Hashtable<String, Integer>();
		styles.put("Plain", Font.PLAIN);
		styles.put("Bold", Font.BOLD);
		styles.put("Italic", Font.ITALIC);
		styles.put("Bold Italic", Font.BOLD | Font.ITALIC);
		ranges = new Hashtable<String, Integer>();
		ranges.put("Occidental", ASCII_BASE);
		ranges.put("Greek", GREEK_BASE);
		ranges.put("Hebrew", HEBREW_BASE);
		ranges.put("Arabic", ARABIC_BASE);
		ranges.put("Thai", THAI_BASE);
		ranges.put("Georgian", GEORGIAN_BASE);
		ranges.put("Hiragana", HIRAGANA_BASE);
		ranges.put("Katakana", KATAKANA_BASE);
		ranges.put("Hangul", HANGUL_BASE);
		ranges.put("Kanji", KANJI_BASE);
		final GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		fontList = new JList(ge.getAvailableFontFamilyNames());
		fontList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				refresh();
			}
		});
		styleList = new JList(new String[] { "Plain", "Bold", "Italic",
				"Bold Italic" });
		styleList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				refresh();
			}
		});
		sizeList = new JList(new Integer[] { 8, 9, 10, 11, 12, 14, 16, 18, 20,
				22, 24, 28, 36, 48, 72 });
		sizeList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				refresh();
			}
		});
		scriptList = new JList(new String[] { "Occidental", "Greek", "Hebrew",
				"Arabic", "Thai", "Georgian", "Hiragana", "Katakana", "Hangul",
				"Kanji" });
		scriptList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				final String s = (String) (scriptList.getSelectedValue());
				if (s == null)
					return;
				script.setText(s);
				switch (ranges.get(s)) {
				default:
					example.setText("AaBbCc");
					break;
				case GREEK_BASE:
					example.setText("\u0391\u03b1\u0392\u03b2\u0393\u03b3");
					break;
				case HEBREW_BASE:
					example.setText("\u05d0\u05d1\u05d2\u05d3\u05d4\u05d5");
					break;
				case ARABIC_BASE:
					example.setText("\u0621\u0622\u0623\u0624\u0625\u0626");
					break;
				case THAI_BASE:
					example.setText("\u0e01\u0e02\u0e03\u0e04\u0e05\u0e06");
					break;
				case GEORGIAN_BASE:
					example.setText("\u10a0\u10a1\u10a2\u10a3\u10a4\u10a5");
					break;
				case HIRAGANA_BASE:
					example.setText("\u3041\u3042\u3043\u3044\u3045\u3046");
					break;
				case KATAKANA_BASE:
					example.setText("\u30a1\u30a2\u30a3\u30a4\u30a5\u30a6");
					break;
				case HANGUL_BASE:
					example.setText("\u3131\u3132\u3133\u3134\u3135\u3136");
					break;
				case KANJI_BASE:
					example.setText("\u4e07\u4e08\u4e09\u4e0a\u4e0b\u4e5f");
					break;
				}
			}
		});
		scriptList.setSelectedIndex(0);
	}
}
