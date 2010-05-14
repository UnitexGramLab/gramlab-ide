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
import java.awt.List;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 * This class describes a dialog box that allows the user to selection the text
 * font. This font will be used to render corpora, dictionaries, token lists,
 * etc.
 * 
 * @author Sébastien Paumier
 *  
 */
public class ConcordanceFontMenu extends JDialog {

    JTextField font = new JTextField(10);
    JTextField style = new JTextField(10);
    NumericTextField size = new NumericTextField(3, "");
    JTextField script = new JTextField(10);
    private JTextField example = new JTextField(6);
    List fontList;
    List styleList;
    List sizeList;
    List scriptList;
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

    String fontName;
    int fontStyle;
    int fontSize;
    int fontScript;

    private Font res;
    Preferences pref;

    /**
     * Creates and shows a new <code>ConcordanceFontMenu</code>
     *  
     */
    public ConcordanceFontMenu(Preferences p) {
        super(UnitexFrame.mainFrame, "Concordance Font", true);
        pref=p;
        fontName = pref.htmlFontName;
        fontStyle = Font.PLAIN;
        fontSize = pref.htmlFontSize;
        init();
        refresh();
        setContentPane(constructPanel());
        pack();
        setResizable(false);
        showFontMenu();
    }

    private void showFontMenu() {
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
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.add(new JLabel(" Font: "));
        p.add(font);
        fontPanel.add(p, BorderLayout.NORTH);
        fontPanel.add(fontList, BorderLayout.CENTER);
        return fontPanel;
    }

    private JPanel constructStylePanel() {
        JPanel stylePanel = new JPanel(new BorderLayout());
        stylePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.add(new JLabel(" Style: "));
        p.add(style);
        stylePanel.add(p, BorderLayout.NORTH);
        stylePanel.add(styleList, BorderLayout.CENTER);
        return stylePanel;
    }

    private JPanel constructSizePanel() {
        JPanel sizePanel = new JPanel(new BorderLayout());
        sizePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.add(new JLabel(" Size: "));
        p.add(size);
        sizePanel.add(p, BorderLayout.NORTH);
        sizePanel.add(sizeList, BorderLayout.CENTER);
        return sizePanel;
    }

    private JPanel constructScriptPanel() {
        JPanel scriptPanel = new JPanel(new BorderLayout());
        scriptPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JPanel p = new JPanel(new GridLayout(2, 1));
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
            	pref.htmlFontName = fontName;
            	pref.htmlFontSize = fontSize;
                GlobalPreferenceFrame.getFrame().concordanceFont.setText(""
                        + pref.htmlFontName + "  "
                        + pref.htmlFontSize + "");
                setVisible(false);
            }
        };
        JButton OK = new JButton(okAction);
        Action cancelAction = new AbstractAction("Cancel") {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
            }
        };
        JButton CANCEL = new JButton(cancelAction);
        buttonPanel.add(OK);
        buttonPanel.add(CANCEL);
        return buttonPanel;
    }

    private void init() {
        // setting the components form
        fontList = new List(7);
        font.setBackground(Color.white);
        font.setText("");
        styleList = new List(7);
        style.setDisabledTextColor(Color.white);
        style.setBackground(Color.white);
        style.setText("");
        style.setEditable(false);
        sizeList = new List(7);
        size.setDisabledTextColor(Color.white);
        size.setBackground(Color.white);
        size.setText("");
        size.setEditable(false);
        scriptList = new List(5);
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
        fontList.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED)
                    return;
                font.setText(fontList.getSelectedItem());
                fontName = font.getText();
                refresh();
            }
        });
        for (i = 0; i < styleNames.length; i++) {
            styleList.add(styleNames[i]);
        }
        styleList.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED)
                    return;
                style.setText(styleList.getSelectedItem());
                fontStyle = styles.get(styleList.getSelectedItem())
                        .intValue();
                refresh();
            }
        });
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
        sizeList.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED)
                    return;
                size.setText(sizeList.getSelectedItem());
                fontSize = Util.toInt(size.getText());
                refresh();
            }
        });
        for (i = 0; i < rangeNames.length; i++) {
            scriptList.add(rangeNames[i]);
        }
        scriptList.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED)
                    return;
                script.setText(scriptList.getSelectedItem());
                fontScript = ranges.get(scriptList.getSelectedItem()).intValue();
                refresh();
            }
        });
        scriptList.select(0);
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

}
