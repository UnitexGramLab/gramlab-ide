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
package fr.umlv.unitex.text;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JEditorPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.io.Encoding;

import com.github.rjeschke.txtmark.Processor;

/**
 * This class provides a text component that can render in read-only mode
 * Markdown files
 * 
 * @author martinec
 */
public class MarkdownArea extends JPanel {
  final JEditorPane area;
  final JScrollBar scrollBar;
  final HTMLDocument document;
  final HTMLEditorKit editorKit;
  final StyleSheet styleSheet;
  final StringBuilder builder = new StringBuilder(10000);

  public MarkdownArea() {
    super(new BorderLayout());
    
    area = new JEditorPane();
    area.setContentType("text/html");
    area.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, false);
    area.setEditable(false);
    area.setComponentOrientation(ConfigManager.getManager()
        .isRightToLeftForText(null) ? ComponentOrientation.RIGHT_TO_LEFT
        : ComponentOrientation.LEFT_TO_RIGHT);

    editorKit  = (HTMLEditorKit) area.getEditorKit();
    document   = (HTMLDocument)  area.getDocument();
    styleSheet = editorKit.getStyleSheet();

    scrollBar = new JScrollBar(Adjustable.VERTICAL, 0, 0, 0, 0);
    add(area);
    add(scrollBar, BorderLayout.EAST);
    //area.addMouseWheelListener(new MouseWheelListener() {
      //@Override
      //public void mouseWheelMoved(MouseWheelEvent e) {
        //final int scrollUnits = scrollBar.getBlockIncrement()
            //* e.getUnitsToScroll();
        //int newValue = scrollBar.getValue() + scrollUnits;
        //if (newValue < 0) {
          //newValue = 0;
        //}  
        //else if (newValue >= scrollBar.getMaximum()) {
          //newValue = scrollBar.getMaximum() - 1;
        //}
        //scrollBar.setValue(newValue);
      //}
    //});
  }

  public MarkdownArea(File file) {
    this();
    load(file);
  }

  public void load(File f) {
    //scrollBar.setMinimum(0);
    //scrollBar.setMaximum(0);
    //scrollBar.setBlockIncrement(0);
    //<<scrollBar.setValue(0);
    //load(f);
    
    // getContent returns null if the size is
    // >= Preferences.MAX_TEXT_FILE_SIZE
    setText(Encoding.getContent((f)));
    
    //FileInputStream stream;
    //try {
      //stream = new FileInputStream(f);
    //} catch (final FileNotFoundException e) {
      //e.printStackTrace();
      //return;
    //}
  }

  @Override
  public void setFont(Font font) {
    if (area != null) {
      area.setFont(font);
    }
  }

  public void setText(String string) {
    //scrollBar.setMinimum(0);
    //scrollBar.setMaximum(0);
    //scrollBar.setBlockIncrement(0);
    //<<scrollBar.setValue(0);

    final int length = document.getLength();

    try {
      document.remove(0, length);
    } catch (final BadLocationException e1) {
      e1.printStackTrace();
    }

    builder.setLength(0);
    builder.append("<html><body>");
    builder.append(Processor.process(string));
    builder.append("</body></html>");

    final String content = builder.toString();

    try {
      editorKit.insertHTML(document, 0, content, 0, 0, null);
    } catch (final BadLocationException|IOException e1) {
      e1.printStackTrace();
    }

    //Element root = document.getDefaultRootElement();

    //scrollBar.setMaximum(root.getElementCount() - 1);
    //final int maximum = scrollBar.getMaximum();
    //if (maximum >= 1000) {
      //scrollBar.setBlockIncrement(1 + maximum / 100);
    //} else {
      //scrollBar.setBlockIncrement(1);
    //}

  }

  @Override
  public void setComponentOrientation(ComponentOrientation o) {
    area.setComponentOrientation(o);
  }
}
