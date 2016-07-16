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
package org.gramlab.core.umlv.unitex.text;

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

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.gramlab.core.umlv.unitex.config.ConfigManager;

/**
 * This class provides a text component that can display in read-only large
 * files.
 * <p/>
 * WARNING: DON'T PUT THIS COMPONENT INTO A JSCROLLPANE!
 * 
 * @author Sébastien Paumier
 */
public class BigTextArea extends JPanel {
	private static final int NOTHING_SELECTED = 0;
	private static final int ALL_IS_SELECTED = 1;
	private static final int PREFIX_SELECTED = 2;
	private static final int INFIX_SELECTED = 3;
	private static final int SUFFIX_SELECTED = 4;
	final TextAsListModelImpl model;
	final JTextPane area;
	final JScrollBar scrollBar;
	final StyledDocument document;
	final Style normal;
	final Style highlighted;
	final StringBuilder builder = new StringBuilder(10000);

	private BigTextArea(TextAsListModelImpl m) {
		super(new BorderLayout());
		model = m;
		area = new JTextPane();
		area.setComponentOrientation(ConfigManager.getManager()
				.isRightToLeftForText(null) ? ComponentOrientation.RIGHT_TO_LEFT
				: ComponentOrientation.LEFT_TO_RIGHT);
		document = area.getStyledDocument();
		normal = StyleContext.getDefaultStyleContext().getStyle(
				StyleContext.DEFAULT_STYLE);
		highlighted = document.addStyle("highlighted", normal);
		StyleConstants.setBackground(highlighted, Color.CYAN);
		area.setEditable(false);
		scrollBar = new JScrollBar(Adjustable.VERTICAL, 0, 0, 0, 0);
		add(area);
		add(scrollBar, BorderLayout.EAST);
		area.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				final int scrollUnits = scrollBar.getBlockIncrement()
						* e.getUnitsToScroll();
				int newValue = scrollBar.getValue() + scrollUnits;
				if (newValue < 0)
					newValue = 0;
				else if (newValue >= scrollBar.getMaximum()) {
					newValue = scrollBar.getMaximum() - 1;
				}
				scrollBar.setValue(newValue);
			}
		});
		model.addListDataListener(new ListDataListener() {
			@Override
			public void intervalAdded(ListDataEvent e) {
				final int oldMaximum = scrollBar.getMaximum();
				scrollBar.setMaximum(model.getSize() - 1);
				final int maximum = scrollBar.getMaximum();
				if (maximum >= 1000) {
					scrollBar.setBlockIncrement(1 + maximum / 100);
				} else {
					scrollBar.setBlockIncrement(1);
				}
				if (oldMaximum <= 100) {
					/* Just to refresh on the first load */
					refresh();
				}
			}

			@Override
			public void intervalRemoved(ListDataEvent e) {
				// nothing to do
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				refresh();
			}
		});
		scrollBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				refresh();
			}
		});
	}

	public BigTextArea() {
		this(new TextAsListModelImpl());
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

	void refresh() {
		final int value = scrollBar.getValue();
		/*
		 * This is an awful trick: we assume that the text area won't display
		 * more then 45 lines.
		 */
		int limit = value + 45;
		if (limit > scrollBar.getMaximum()) {
			limit = scrollBar.getMaximum();
		}
		final Interval selection = model.getSelection();
		final int length = document.getLength();
		try {
			document.remove(0, length);
		} catch (final BadLocationException e1) {
			e1.printStackTrace();
		}
		builder.setLength(0);
		final Interval x = model.getInterval(value);
		if (x == null) {
			final String s = model.getContent();
			if (s != null)
				area.setText(s);
			else
				area.setText("");
			return;
		}
		final int start = x.getStartInChars();
		for (int i = value; i <= limit; i++) {
			builder.append(model.getElementAt(i));
			builder.append('\r');
			builder.append('\n');
		}
		final String content = builder.toString();
		final int end = model.getInterval(limit).getEndInChars();
		try {
			final int result = compareIntervals(selection, start, end);
			switch (result) {
			case NOTHING_SELECTED:
				document.insertString(0, content, normal);
				break;
			case ALL_IS_SELECTED:
				document.insertString(0, content, highlighted);
				break;
			case PREFIX_SELECTED:
				final int a = (selection.getEndInChars() - start + 1);
				document.insertString(0, content.substring(0, a), highlighted);
				document.insertString(a, content.substring(a), normal);
				break;
			case INFIX_SELECTED:
				final int b = (selection.getStartInChars() - start);
				final int c = (selection.getEndInChars() - start + 1);
				document.insertString(0, content.substring(0, b), normal);
				document.insertString(b, content.substring(b, c), highlighted);
				document.insertString(c, content.substring(c), normal);
				break;
			case SUFFIX_SELECTED:
				final int d = (selection.getStartInChars() - start);
				document.insertString(0, content.substring(0, d), normal);
				document.insertString(d, content.substring(d), highlighted);
				break;
			default:
			}
		} catch (final BadLocationException e1) {
			e1.printStackTrace();
		}
	}

	private int compareIntervals(Interval selection, int start, int end) {
		if (selection == null)
			return NOTHING_SELECTED;
		final int selectionStart = selection.getStartInChars();
		final int selectionEnd = selection.getEndInChars();
		if (selectionStart > end || selectionEnd < start)
			return NOTHING_SELECTED;
		if (selectionStart <= start && selectionEnd >= end)
			return ALL_IS_SELECTED;
		if (selectionStart <= start && selectionEnd < end)
			return PREFIX_SELECTED;
		if (selectionStart > start && selectionEnd < end)
			return INFIX_SELECTED;
		return SUFFIX_SELECTED;
	}

	@Override
	public void setFont(Font font) {
		if (area != null) {
			area.setFont(font);
			refresh();
		}
	}

	public void setSelection(Interval i) {
		model.setSelection(i);
	}

	public void setSelection(int startInChars, int endInChars) {
		model.setSelection(new Interval(-1, -1, startInChars, endInChars));
	}

	public void scrollToSelection() {
		final Interval selection = model.getSelection();
		if (selection == null)
			return;
		final int i = model.getElementContainingPositionInChars(selection
				.getStartInChars());
		if (i != -1)
			scrollBar.setValue(i);
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

	@Override
	public void setComponentOrientation(ComponentOrientation o) {
		area.setComponentOrientation(o);
	}
}
