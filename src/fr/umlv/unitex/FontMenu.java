 /*
  * Unitex
  *
  * Copyright (C) 2001-2008 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
import java.awt.List;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 * This class describes the graph's font selection dialog box.
 * 
 * @author Sébastien Paumier
 *  
 */
public class FontMenu extends JDialog {

	JTextField font = new JTextField(10);
	JTextField style = new JTextField(10);
	NumericTextField size = new NumericTextField(3, "");
	JTextField script = new JTextField(10);
	JTextField example = new JTextField(6);
	FontList fontList;
	StyleList styleList;
	SizeList sizeList;
	ScriptList scriptList;
	Hashtable<String,Integer> styles;
	Hashtable<String,Integer> ranges;

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

	boolean input;
	// used to know if we parameter an input font or an output font
	
	boolean globalConfiguration;
	// used to know if we deal with the parameters of a particular graph
	// or not

	/**
	 * The dialog box
	 */
    // TODO clean that
	static FontMenu pref;

	String fontName;
	int fontStyle;
	int fontSize;
	int fontScript;

	Font res;
	
	static Preferences preferences;

	/**
	 * Creates a new font dialog box.
	 * 
	 * @param in
	 *            indicates if we select an input or an output font for graphs.
	 */
	public FontMenu(boolean in,boolean globalConfig,Preferences p) {
		super(UnitexFrame.mainFrame, "Font", true);
		input = in;
		globalConfiguration=globalConfig;
		preferences=p;
		if (input) {
			fontName = preferences.input.getName();
			fontStyle = preferences.input.getStyle();
			fontSize = preferences.inputSize;
		} else {
			fontName = preferences.output.getName();
			fontStyle = preferences.output.getStyle();
			fontSize = preferences.outputSize;
		}
		init();
		refresh();
		setContentPane(constructPanel());
		pack();
		setResizable(false);
		pref = this;
		showFontMenu();
	}

	/**
	 * Shows the dialog box
	 *  
	 */
	public void showFontMenu() {
		setLocationRelativeTo(UnitexFrame.mainFrame);
		setVisible(true);
	}

	private JPanel constructPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(constructUpPanel(), BorderLayout.CENTER);
		panel.add(constructDownPanel(), BorderLayout.SOUTH);
		return panel;
	}

	private JPanel constructUpPanel() {
		JPanel upPanel = new JPanel(new BorderLayout());
		upPanel.add(constructFontPanel(), BorderLayout.WEST);
		upPanel.add(constructStylePanel(), BorderLayout.CENTER);
		upPanel.add(constructSizePanel(), BorderLayout.EAST);
		return upPanel;
	}

	private JPanel constructDownPanel() {
		JPanel downPanel = new JPanel(new BorderLayout());
		downPanel.add(constructScriptPanel(), BorderLayout.WEST);
		downPanel.add(constructExampleAndButtonPanel(), BorderLayout.CENTER);
		return downPanel;
	}

	private JPanel constructFontPanel() {
		JPanel fontPanel = new JPanel(new BorderLayout());
		fontPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(2, 1));
		p.add(new JLabel(" Font: "));
		p.add(font);
		fontPanel.add(p, BorderLayout.NORTH);
		fontPanel.add(fontList, BorderLayout.CENTER);
		return fontPanel;
	}

	private JPanel constructStylePanel() {
		JPanel stylePanel = new JPanel(new BorderLayout());
		stylePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(2, 1));
		p.add(new JLabel(" Style: "));
		p.add(style);
		stylePanel.add(p, BorderLayout.NORTH);
		stylePanel.add(styleList, BorderLayout.CENTER);
		return stylePanel;
	}

	private JPanel constructSizePanel() {
		JPanel sizePanel = new JPanel(new BorderLayout());
		sizePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(2, 1));
		p.add(new JLabel(" Size: "));
		p.add(size);
		sizePanel.add(p, BorderLayout.NORTH);
		sizePanel.add(sizeList, BorderLayout.CENTER);
		return sizePanel;
	}

	private JPanel constructScriptPanel() {
		JPanel scriptPanel = new JPanel(new BorderLayout());
		scriptPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(2, 1));
		p.add(new JLabel(" Script: "));
		p.add(script);
		scriptPanel.add(p, BorderLayout.NORTH);
		scriptPanel.add(scriptList, BorderLayout.CENTER);
		return scriptPanel;
	}

	private JPanel constructExampleAndButtonPanel() {
		JPanel exampleAndButtonPanel = new JPanel(new BorderLayout());
		exampleAndButtonPanel.setBorder(new EmptyBorder(0, 0, 0, 5));
		exampleAndButtonPanel.add(constructExamplePanel(), BorderLayout.CENTER);
		exampleAndButtonPanel.add(constructButtonPanel(), BorderLayout.SOUTH);
		return exampleAndButtonPanel;
	}

	private JPanel constructExamplePanel() {
		JPanel examplePanel = new JPanel(new BorderLayout());
		examplePanel.setBorder(new TitledBorder("Example"));
		examplePanel.add(new JPanel(), BorderLayout.WEST);
		examplePanel.add(example, BorderLayout.CENTER);
		examplePanel.add(new JPanel(), BorderLayout.EAST);
		return examplePanel;
	}

	private JPanel constructButtonPanel() {
		JPanel buttonPanel = new JPanel();
		Action okAction = new AbstractAction("OK") {
			public void actionPerformed(ActionEvent arg0) {
				if (input) {
					preferences.input = new Font(fontName, fontStyle,
							(int) (fontSize / 0.72));
					preferences.inputSize = fontSize;
					if (globalConfiguration) {GlobalPreferenceFrame.getFrame().inputLabel.setText(""
							+ preferences.input.getFontName() + "  "
							+ preferences.inputSize + "");}
				} else {
					preferences.output = new Font(fontName, fontStyle,
							(int) (fontSize / 0.72));
					preferences.outputSize = fontSize;
					if (globalConfiguration) {GlobalPreferenceFrame.getFrame().inputLabel.setText(""
							+ preferences.output.getFontName() + "  "
							+ preferences.outputSize + "");}
				}
				FontMenu.pref.setVisible(false);
			}
		};
		Action cancelAction = new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent arg0) {
				FontMenu.pref.setVisible(false);
			}
		};
		JButton OK = new JButton(okAction);
		JButton CANCEL = new JButton(cancelAction);
		buttonPanel.add(OK);
		buttonPanel.add(CANCEL);
		return buttonPanel;
	}

	private void init() {
		// setting the components form
		fontList = new FontList(7);
		font.setBackground(Color.white);
		font.setText("");
		styleList = new StyleList(7);
		style.setDisabledTextColor(Color.white);
		style.setBackground(Color.white);
		style.setText("");
		style.setEditable(false);
		sizeList = new SizeList(7);
		size.setDisabledTextColor(Color.white);
		size.setBackground(Color.white);
		size.setText("");
		size.setEditable(false);
		scriptList = new ScriptList(5);
		script.setDisabledTextColor(Color.white);
		script.setBackground(Color.white);
		script.setText("");
		script.setEditable(false);
		example.setText("AaBbCc");
		example.setEditable(false);
		example.setBorder(new EmptyBorder(0, 0, 0, 0));
		// setting the components content
		styles = new Hashtable<String,Integer>();
		styles.put("Plain", new Integer(Font.PLAIN));
		styles.put("Bold", new Integer(Font.BOLD));
		styles.put("Italic", new Integer(Font.ITALIC));
		styles.put("Bold Italic", new Integer(Font.BOLD | Font.ITALIC));
		String[] styleNames = {"Plain", "Bold", "Italic", "Bold Italic"};
		ranges = new Hashtable<String,Integer>();
		ranges.put("Occidental", new Integer(ASCII_BASE));
		ranges.put("Greek", new Integer(GREEK_BASE));
		ranges.put("Hebrew", new Integer(HEBREW_BASE));
		ranges.put("Arabic", new Integer(ARABIC_BASE));
		ranges.put("Thai", new Integer(THAI_BASE));
		ranges.put("Georgian", new Integer(GEORGIAN_BASE));
		ranges.put("Hiragana", new Integer(HIRAGANA_BASE));
		ranges.put("Katakana", new Integer(KATAKANA_BASE));
		ranges.put("Hangul", new Integer(HANGUL_BASE));
		ranges.put("Kanji", new Integer(KANJI_BASE));
		String[] rangeNames = {"Occidental", "Greek", "Hebrew", "Arabic",
				"Thai", "Georgian", "Hiragana", "Katakana", "Hangul", "Kanji"};

		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		String[] fontNames = ge.getAvailableFontFamilyNames();
		int i;

		for (i = 0; i < fontNames.length; i++) {
			if (!fontNames[i].equals("")) {
				fontList.add(fontNames[i]);
			}
		}
		fontList.addItemListener(new FontListener());

		for (i = 0; i < styleNames.length; i++) {
			styleList.add(styleNames[i]);
		}
		styleList.addItemListener(new StyleListener());

		sizeList.add(String.valueOf(8));
		sizeList.add(String.valueOf(9));
		sizeList.add(String.valueOf(10));
		sizeList.add(String.valueOf(11));
		sizeList.add(String.valueOf(12));
		sizeList.add(String.valueOf(14));
		sizeList.add(String.valueOf(16));
		sizeList.add(String.valueOf(18));
		sizeList.add(String.valueOf(20));
		sizeList.add(String.valueOf(22));
		sizeList.add(String.valueOf(24));
		sizeList.add(String.valueOf(26));
		sizeList.add(String.valueOf(28));
		sizeList.add(String.valueOf(36));
		sizeList.add(String.valueOf(48));
		sizeList.add(String.valueOf(72));
		sizeList.addItemListener(new SizeListener());

		for (i = 0; i < rangeNames.length; i++) {
			scriptList.add(rangeNames[i]);
		}
		scriptList.addItemListener(new RangeListener());
		scriptList.select(0);

	}

	int toInt(String s) {
		int j = 0;
		for (int i = 0; i < s.length(); i++)
			j = j * 10 + s.charAt(i) - '0';
		return j;
	}

	void refresh() {
		font.setText(fontName);
		if (fontStyle == Font.PLAIN)
			style.setText("Plain");
		if (fontStyle == Font.BOLD)
			style.setText("Bold");
		if (fontStyle == Font.ITALIC)
			style.setText("Italic");
		if (fontStyle == (Font.BOLD | Font.ITALIC))
			style.setText("Bold Italic");
		size.setText(String.valueOf(fontSize));
		script.setText(scriptList.getSelectedItem());
		res = new Font(fontName, fontStyle, (int) (fontSize / 0.72));
		example.setFont(res);
		if (fontScript == ASCII_BASE)
			example.setText("AaBbCc");
		if (fontScript == GREEK_BASE)
			example.setText("\u0391\u03b1\u0392\u03b2\u0393\u03b3");
		if (fontScript == HEBREW_BASE)
			example.setText("\u05d0\u05d1\u05d2\u05d3\u05d4\u05d5");
		if (fontScript == ARABIC_BASE)
			example.setText("\u0621\u0622\u0623\u0624\u0625\u0626");
		if (fontScript == THAI_BASE)
			example.setText("\u0e01\u0e02\u0e03\u0e04\u0e05\u0e06");
		if (fontScript == GEORGIAN_BASE)
			example.setText("\u10a0\u10a1\u10a2\u10a3\u10a4\u10a5");
		if (fontScript == HIRAGANA_BASE)
			example.setText("\u3041\u3042\u3043\u3044\u3045\u3046");
		if (fontScript == KATAKANA_BASE)
			example.setText("\u30a1\u30a2\u30a3\u30a4\u30a5\u30a6");
		if (fontScript == HANGUL_BASE)
			example.setText("\u3131\u3132\u3133\u3134\u3135\u3136");
		if (fontScript == KANJI_BASE)
			example.setText("\u4e07\u4e08\u4e09\u4e0a\u4e0b\u4e5f");
	}

	class FontListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() != ItemEvent.SELECTED)
				return;
			font.setText(fontList.getSelectedItem());
			fontName = font.getText();
			refresh();
		}
	}

	class StyleListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() != ItemEvent.SELECTED)
				return;
			style.setText(styleList.getSelectedItem());
			fontStyle = styles.get(styleList.getSelectedItem())
					.intValue();
			refresh();
		}
	}

	class SizeListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() != ItemEvent.SELECTED)
				return;
			size.setText(sizeList.getSelectedItem());
			fontSize = toInt(size.getText());
			refresh();
		}
	}

	class RangeListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() != ItemEvent.SELECTED)
				return;
			script.setText(scriptList.getSelectedItem());
			fontScript = ranges.get(scriptList.getSelectedItem()).intValue();
			refresh();
		}
	}

	class FontList extends List {
		public FontList(int rows) {
			super(rows);
		}
		public Dimension getPreferredSize() {
			return new Dimension(150, 100);
		}
	}

	class StyleList extends List {
		public StyleList(int rows) {
			super(rows);
		}
		public Dimension getPreferredSize() {
			return new Dimension(120, 100);
		}
	}

	class SizeList extends List {
		public SizeList(int rows) {
			super(rows);
		}
		public Dimension getPreferredSize() {
			return new Dimension(50, 100);
		}
	}

	class ScriptList extends List {
		public ScriptList(int rows) {
			super(rows);
		}
		public Dimension getPreferredSize() {
			return new Dimension(50, 50);
		}
	}

}