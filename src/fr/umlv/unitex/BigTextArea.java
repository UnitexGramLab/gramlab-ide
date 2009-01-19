 /*
  * Unitex
  *
  * Copyright (C) 2001-2009 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * This class provides a text component that can display in read-only
 * large UTF16-LE files. 
 * 
 * WARNING: DON'T PUT THIS COMPONENT INTO A JSCROLLPANE!
 * 
 * @author Sébastien Paumier
 */
public class BigTextArea extends JPanel {

	protected static final int NOTHING_SELECTED = 0;
	protected static final int ALL_IS_SELECTED = 1;
	protected static final int PREFIX_SELECTED = 2;
	protected static final int INFIX_SELECTED = 3;
	protected static final int SUFFIX_SELECTED = 4;
	
	TextAsListModel model;
	JTextPane area;
	JScrollBar scrollBar;
	StyledDocument document;
	Style normal;
	Style highlighted;
	final StringBuilder builder=new StringBuilder(10000);
	
	
	public BigTextArea(TextAsListModel m) {
		super(new BorderLayout());
		model=m;
		area=new JTextPane();
		area.setComponentOrientation(Config.isRightToLeftLanguage()?ComponentOrientation.RIGHT_TO_LEFT:ComponentOrientation.LEFT_TO_RIGHT);
		document=area.getStyledDocument();
		normal=StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		highlighted=document.addStyle("highlighted",normal);
		StyleConstants.setBackground(highlighted,Color.CYAN);
		area.setEditable(false);
		scrollBar=new JScrollBar(Adjustable.VERTICAL,0,0,0,0);
		add(area);
		add(scrollBar,BorderLayout.EAST);
		model.addListDataListener(new ListDataListener() {
			public void intervalAdded(ListDataEvent e) {
				int oldMaximum=scrollBar.getMaximum();
				scrollBar.setMaximum(model.getSize()-1);
				int maximum=scrollBar.getMaximum();
				if (maximum>=1000) {
					scrollBar.setBlockIncrement(1+maximum/100);
				} else {
					scrollBar.setBlockIncrement(1);
				}
				if (oldMaximum<=100) {
					/* Just to refresh on the first load */
					refresh();
				}
			}

			public void intervalRemoved(ListDataEvent e) {
				// nothing to do
			}

			public void contentsChanged(ListDataEvent e) {
				refresh();
			}
		});
		
		scrollBar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				refresh();
			}

		});
	}

	
	public BigTextArea() {
		this(new TextAsListModel());
	}
	
	public BigTextArea(File file) {
		this();
		load(file);
	}


	public void load(File f) {
		scrollBar.setMinimum(0);
		scrollBar.setMaximum(0);
		scrollBar.setBlockIncrement(0);
		scrollBar.setValue(0);
		model.load(f);
	}

	@SuppressWarnings("null")
    void refresh() {
		int value=scrollBar.getValue();
		/* This is an awful trick: we assume that the text area won't
		 * display more then 45 lines.
		 */
		int limit=value+45;
		if (limit>scrollBar.getMaximum()) {
			limit=scrollBar.getMaximum();
		}
		Interval selection=model.getSelection();
		int length=document.getLength();
		try {
			document.remove(0,length);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		builder.setLength(0);
		Interval x=model.getInterval(value);
		if (x==null) {
			String s=model.getContent();
			if (s!=null) area.setText(s);
			else area.setText("");
			return;
		}
		int start=x.getStart();
		Interval element=null;
		for (int i=value;i<=limit;i++) {
			element=model.getInterval(i);
			builder.append(model.getElementAt(i));
			builder.append('\r');
			builder.append('\n');
		}
		String content=builder.toString();
		int end=element.getEnd();
		try {
			int result=compareIntervals(selection,start,end);
			switch (result) {
				case NOTHING_SELECTED: document.insertString(0,content,normal); break;
				case ALL_IS_SELECTED: document.insertString(0,content,highlighted); break;
				case PREFIX_SELECTED:
					int a=(selection.getEnd()-start+1);
					document.insertString(0,content.substring(0,a),highlighted);
					document.insertString(a,content.substring(a),normal);
					break;
				case INFIX_SELECTED:
					int b=(selection.getStart()-start);
					int c=(selection.getEnd()-start+1);
					document.insertString(0,content.substring(0,b),normal);
					document.insertString(b,content.substring(b,c),highlighted);
					document.insertString(c,content.substring(c),normal);
					break;
				case SUFFIX_SELECTED:
					int d=(selection.getStart()-start);
					document.insertString(0,content.substring(0,d),normal);
					document.insertString(d,content.substring(d),highlighted);
					break;
				default: return;
			}
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		
	}

	private int compareIntervals(Interval selection,int start,int end) {
		if (selection==null) return NOTHING_SELECTED;
		int selectionStart=selection.getStart();
		int selectionEnd=selection.getEnd();
		if (selectionStart>end || selectionEnd<start) return NOTHING_SELECTED;
		if (selectionStart<=start && selectionEnd>=end) return ALL_IS_SELECTED;
		if (selectionStart<=start && selectionEnd<end) return PREFIX_SELECTED;
		if (selectionStart>start && selectionEnd<end) return INFIX_SELECTED;
		return SUFFIX_SELECTED;
	}
	
	
	public void setFont(Font font) {
		if (area!=null)	{
			area.setFont(font);
			refresh();
		}
	}
	
	public void setSelection(Interval i) {
		model.setSelection(i);
	}
	
	public void setSelection(int start,int end) {
		model.setSelection(new Interval(start,end));
	}
	
	public void scrollToSelection() {
		Interval selection=model.getSelection();
		if (selection==null) return;
		int i=model.getElementContainingPosition(selection.getStart());
		if (i!=-1) scrollBar.setValue(i);
	}


	public void setText(String string) {
		scrollBar.setMinimum(0);
		scrollBar.setMaximum(0);
		scrollBar.setBlockIncrement(0);
		scrollBar.setValue(0);
		model.setText(string);
	}
	
	public void reset() {
		model.reset();
	}
}
