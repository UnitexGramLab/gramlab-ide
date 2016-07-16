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

import java.awt.Component;
import java.io.File;
import java.util.regex.Pattern;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * This class provides a text component that can display in read-only large
 * files as lists of lines. This is useful to display dictionaries.
 * 
 * @author Sébastien Paumier
 */
public class BigTextList extends JList {
	/**
	 * Builds a JList designed for large line lists.
	 * 
	 * @param m
	 *            the model containing information about the underlying text
	 *            file
	 * @param isDelaf
	 *            if true, the list will use a special renderer dedicated to
	 *            DELAF lines
	 */
	private BigTextList(TextAsListModelImpl m, boolean isDelaf) {
		super(m);
		/*
		 * Set maximal length of a line: This value must big enough holding even
		 * the longest dlc entries, or strange effects (lines cut up somewhere
		 * in the middle) will occur!
		 */
		setPrototypeCellValue("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx,xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index,
						isSelected, cellHasFocus);
				setText(getTabbedString((String) value));
				return this;
			}

			/**
			 * The default renderer of a JList is a JLabel. As JLabels don't
			 * know how to deal with tabs, we do it here, considering that a tab
			 * should be at most 8 space large.
			 */
			final StringBuilder builder = new StringBuilder();

			private String getTabbedString(String s) {
				final int tab_size = 8;
				builder.setLength(0);
				final int l = s.length();
				for (int i = 0; i < l; i++) {
					final char c = s.charAt(i);
					if (c == '\t') {
						final int z = tab_size - i % tab_size;
						for (int j = 0; j < z; j++) {
							builder.append(' ');
						}
					} else {
						builder.append(c);
					}
				}
				return builder.toString();
			}
		});
		if (isDelaf) {
			setCellRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					super.getListCellRendererComponent(list, value, index,
							isSelected, cellHasFocus);
					setText(getDecoratedDelafLine(escapeHTML((String) value)));
					return this;
				}

				final StringBuilder builder = new StringBuilder();

				private String escapeHTML(String s){
					builder.setLength(0);
					final int l = s.length();
					for (int i = 0; i < l; i++) {
						final char c = s.charAt(i);
						switch (c) {
							case '<': builder.append("&lt;");   break;
							case '>': builder.append("&gt;");   break;
							case '&': builder.append("&amp;");  break;
							case '"': builder.append("&quot;"); break;
							default:  builder.append(c); break;
						}
					}
					return builder.toString();
				}

				private String getDecoratedDelafLine(String string) {
					if (string == null)
						return null;
					final int length = string.length();
					builder.delete(0, builder.length());
					builder.append("<html><body>");
					int comma = -1;
					int pos = 0;
					do {
						comma = string.indexOf(',', pos);
						if (comma == -1)
							return string;
						pos = comma + 1;
					} while (comma > 0 && string.charAt(comma - 1) == '\\');
					builder.append("<font color=\"blue\">");
					builder.append(string.substring(0, comma));
					builder.append("</font>");
					builder.append(",");
					int startPos = pos;
					do {
						comma = string.indexOf('.', pos);
						if (comma == -1)
							return string;
						pos = comma + 1;
					} while (string.charAt(comma - 1) == '\\');
					if (pos == length)
						return string;
					if (startPos != comma) {
						builder.append("<font color=\"red\">");
						builder.append(string.substring(startPos, comma));
						builder.append("</font>");
					}
					char c = '.';
					char last;
					startPos = pos;
					do {
						builder.append(c);
						builder.append((c != '=') ? "<font color=\"#00B900\">" : "<font color=\"#660066\">");
						do {
							last = c;
							c = string.charAt(pos);
							pos++;
						} while (pos != length
								&& !(last != '\\' && (c == '+' || c == '=' || c == ':')));
						if (pos == length) {
							c = '\0';
							builder.append(string.substring(startPos));
						} else {
							builder.append(string.substring(startPos, pos - 1));
							startPos = pos;
						}
						builder.append("</font>");
					} while (c == '+' || c == '=' );
					if (c != '\0') {
						c = ':';
						startPos = pos;
						do {
							builder.append(c);
							builder.append("<font color=\"#CE6700\">");
							do {
								last = c;
								c = string.charAt(pos);
								pos++;
							} while (pos != length
									&& !(last != '\\' && (c == ':')));
							if (pos == length) {
								c = '\0';
								builder.append(string.substring(startPos));
							} else {
								builder.append(string.substring(startPos,
										pos - 1));
								startPos = pos;
							}
							builder.append("</font>");
						} while (c == ':');
					}
					builder.append("</body></html>");
					return builder.toString();
				}
			});
		}
	}

	public BigTextList(TextAsListModelImpl m) {
		this(m, false);
	}

	public BigTextList(boolean b) {
		this(new TextAsListModelImpl(), b);
	}

	public BigTextList() {
		this(new TextAsListModelImpl(), false);
	}

	public void load(File f) {
		final TextAsListModelImpl model = (TextAsListModelImpl) getModel();
		model.reset();
		model.load(f);
	}

	public void load(File f, Pattern p) {
		final TextAsListModelImpl model = (TextAsListModelImpl) getModel();
		model.reset();
		model.load(f, p);
	}

	public void reset() {
		final TextAsListModelImpl model = (TextAsListModelImpl) getModel();
		model.reset();
	}

	public void setText(String string) {
		final TextAsListModelImpl model = (TextAsListModelImpl) getModel();
		model.setText(string);
	}
}
