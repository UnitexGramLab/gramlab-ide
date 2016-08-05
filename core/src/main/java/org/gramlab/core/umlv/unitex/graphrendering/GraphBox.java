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
package org.gramlab.core.umlv.unitex.graphrendering;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.gramlab.core.umlv.unitex.exceptions.BackSlashAtEndOfLineException;
import org.gramlab.core.umlv.unitex.exceptions.MissingGraphNameException;
import org.gramlab.core.umlv.unitex.exceptions.NoClosingQuoteException;
import org.gramlab.core.umlv.unitex.exceptions.NoClosingRoundBracketException;
import org.gramlab.core.umlv.unitex.exceptions.NoClosingSupException;

/**
 * This class describes a box of a graph.
 * 
 * @author Sébastien Paumier
 */
public class GraphBox extends GenericGraphBox {
	/**
	 * Constructs a new box
	 * 
	 * @param x
	 *            X coordinate of the input point of the box
	 * @param y
	 *            Y coordinate of the input point of the box
	 * @param type
	 *            indicates if the box is initial, final or normal
	 * @param p
	 *            component on which the box will be drawn
	 */
	public GraphBox(int x, int y, int type, GraphicalZone p) {
		super(x, y, type, p);
	}

	/*
	 * The following methods are used to manipulate the box text
	 */
	private int test_transduction(char s[], int i) {
		int compteur;
		if (s[i] != '/')
			return 0;
		i--;
		compteur = 0;
		while (i >= 0 && s[i] == '\\') {
			compteur++;
			i--;
		}
		if ((compteur % 2) != 0)
			return 0;
		return 1;
	}

	private static boolean isAPlusChar(char c) {
		return (c == '+' || c == '\uff0b');
	}

	private void tokenizeTextWithoutTransduction(String s)
			throws BackSlashAtEndOfLineException, MissingGraphNameException,
			NoClosingQuoteException, NoClosingSupException,
			NoClosingRoundBracketException {
		final int L = s.length();
		int i = 0;
		String tmp;
		char ligne[] = new char[10000];
		ligne = s.toCharArray();
		while (i < L) {
			tmp = "";
			if (ligne[i] == ':') {
				// case of a sub graph call
				i++;
				while ((i < L) && !isAPlusChar(ligne[i]) /* (ligne[i]!='+') */
				) {
					if (ligne[i] == '\\') {
						tmp = tmp.concat(String.valueOf(ligne[i++]));
						if (i >= L) {
							final BackSlashAtEndOfLineException e = new BackSlashAtEndOfLineException();
							throw e;
						}
					}
					tmp = tmp.concat(String.valueOf(ligne[i++]));
				}
				if (tmp.length() == 0) {
					final MissingGraphNameException e = new MissingGraphNameException();
					throw e;
				}
				// if we had a + separator char (even a japanese one), we put a
				// standard + instead
				if (i < L)
					ligne[i] = '+';
				i++;
				n_lines++;
				lines.add(tmp);
				greyed.add(Boolean.TRUE);
			} else {
				// all other cases
				while ((i < L) && !isAPlusChar(ligne[i]) /* (ligne[i]!='+') */
				) {
					if (ligne[i] == '"') {
						// case of a quote expression
						tmp = tmp.concat(String.valueOf(ligne[i++]));
						while ((i < L) && ligne[i] != '"') {
							if (ligne[i] == '\\') {
								tmp = tmp.concat(String.valueOf(ligne[i++]));
								if (i >= L) {
									final BackSlashAtEndOfLineException e = new BackSlashAtEndOfLineException();
									throw e;
								}
							}
							tmp = tmp.concat(String.valueOf(ligne[i++]));
						}
						if (i >= L) {
							final NoClosingQuoteException e = new NoClosingQuoteException();
							throw e;
						}
						tmp = tmp.concat(String.valueOf(ligne[i++]));
					} else if (ligne[i] == '<') {
						// case of a <...> expression
						tmp = tmp.concat(String.valueOf(ligne[i++]));
						while ((i < L) && ligne[i] != '>') {
							if (ligne[i] == '\\') {
								tmp = tmp.concat(String.valueOf(ligne[i++]));
								if (i >= L) {
									final BackSlashAtEndOfLineException e = new BackSlashAtEndOfLineException();
									throw e;
								}
							}
							tmp = tmp.concat(String.valueOf(ligne[i++]));
						}
						if (i >= L) {
							final NoClosingSupException e = new NoClosingSupException();
							throw e;
						}
						tmp = tmp.concat(String.valueOf(ligne[i++]));
					} else if (ligne[i] == '{') {
						// case of a {...} expression
						tmp = tmp.concat(String.valueOf(ligne[i++]));
						while ((i < L) && ligne[i] != '}') {
							if (ligne[i] == '\\') {
								tmp = tmp.concat(String.valueOf(ligne[i++]));
								if (i >= L) {
									final BackSlashAtEndOfLineException e = new BackSlashAtEndOfLineException();
									throw e;
								}
							}
							tmp = tmp.concat(String.valueOf(ligne[i++]));
						}
						if (i >= L) {
							final NoClosingRoundBracketException e = new NoClosingRoundBracketException();
							throw e;
						}
						tmp = tmp.concat(String.valueOf(ligne[i++]));
					} else {
						if (ligne[i] == '\\') {
							tmp = tmp.concat(String.valueOf(ligne[i++]));
							if (i >= L) {
								final BackSlashAtEndOfLineException e = new BackSlashAtEndOfLineException();
								throw e;
							}
						}
						tmp = tmp.concat(String.valueOf(ligne[i++]));
					}
				}
				if (i < L)
					ligne[i] = '+';
				n_lines++;
				lines.add(tmp);
				greyed.add(Boolean.FALSE);
				i++;
			}
		}
	}

	private int maxVariableLineWidth() {
		if (context == null) {
			return 0;
		}
		final FontMetrics f = context.getFontMetrics(variableFont);
		return f.stringWidth("(");
	}

	private int maxContextMarkLineWidth(String mark) {
		if (context == null) {
			return 0;
		}
		final FontMetrics f = context.getFontMetrics(variableFont);
		return f.stringWidth(mark);
	}

	/**
	 * Sets the box content.
	 * 
	 * @param s
	 *            the content
	 */
	@Override
	public void setContent(String s) {
		if (type == GenericGraphBox.FINAL)
			return; // nothing to do if we consider the final state
		content = s;
		if (s.equals("")) {
			throw new IllegalStateException(
					"The graph contains an unexpected empty box that is not the final state");
		}
		String tmp = "";
		int i = 0, L;
		L = content.length();
		variable = false;
		contextMark = false;
                genericGrfMark = false;
		morphologicalModeMark = false;
		commentBox = false;
		transduction = "";
		greyed.clear();
		if (tokenizeCommentBox(content, lines)) {
			/* Case of a special comment box */
			commentBox = true;
			standaloneBox = true;
			n_lines = lines.size();
			for (int j = 0; j < n_lines; j++) {
				greyed.add(Boolean.FALSE);
			}
			// dimensions of a full box
			Width = maxLineWidth() + 10;
			Height = n_lines * get_h_ligne() + 6;
			Y1 = Y - Height / 2;
			X_out = X + Width + 5;
			/*
			 * As a comment box cannot have any incoming or outgoing transition,
			 * we remove all such transitions, if any
			 */
			removeAllOutgoingTransitions();
			removeAllIncomingTransitions();
			return;
		}
		lines.clear();
		final char line[] = content.toCharArray();
		if (line[0] == '$' && (line[L - 1] == '(' || line[L - 1] == ')')) {
			// //////////////////////////////////////////
			// case of $a( or $a)
			// //////////////////////////////////////////
			variable = true;
			outputVariable = (line[1] == '|');
			lines.clear();
			greyed.clear();
			lines.add(String.valueOf(line[L - 1]));
			greyed.add(Boolean.FALSE);
			transduction = content.substring(1 + (outputVariable ? 1 : 0),
					L - 1);
			n_lines = 1;
			Height = get_h_variable_ligne() + 3;
			Width = maxVariableLineWidth() + 5;
			Y1 = Y - Height / 2;
			X_out = X + Width + 2;
			return;
		}
		if (content.equals("$[") || content.equals("$![")
				|| content.equals("$]") || content.equals("$*")) {
			// //////////////////////////////////////////
			// case of context marks ($[ or $![) :
			// ab $[ cd $] => ab followed by cd
			// ab $![ cd $] => ab not followed by cd
			// //////////////////////////////////////////
			contextMark = true;
			lines.clear();
			greyed.clear();
			final String sub = content.substring(1);
			lines.add(sub);
			greyed.add(Boolean.FALSE);
			transduction = "";
			n_lines = 1;
			Height = get_h_variable_ligne() + 3;
			Width = maxContextMarkLineWidth(sub) + 5;
			Y1 = Y - Height / 2;
			X_out = X + Width + 2;
			return;
		}
                if(content.equals("$G") || content.startsWith("$G/")) {
                    // //////////////////////
                    // case of mark $@:
                    // $@ ab => search ab in the token list
                    // /////////////////////
                    genericGrfMark = true;
                    lines.clear();
                    greyed.clear();
                    final String sub = content.substring(1,2);
                    lines.add(sub);
                    greyed.add(Boolean.FALSE);
                    transduction = "";
                    if(content.length() > 3 && content.charAt(2) =='/') {
                            transduction = transduction.concat(content.substring(3));
                    }
                    n_lines = 1;
                    Height = get_h_variable_ligne() + 3;
                    Width = maxContextMarkLineWidth(sub) + 5;
                    Y1 = Y - Height / 2;
                    X_out = X + Width + 2;
                    return;
                }
		if (content.equals("$<") || content.equals("$>")) {
			// //////////////////////////////////////////
			// case of morphological mode marks ($< and $>)
			// //////////////////////////////////////////
			morphologicalModeMark = true;
			lines.clear();
			greyed.clear();
			final String sub = content.substring(1);
			lines.add(sub);
			greyed.add(Boolean.FALSE);
			transduction = "";
			n_lines = 1;
			Height = get_h_variable_ligne() + 3;
			Width = maxContextMarkLineWidth(sub) + 5;
			Y1 = Y - Height / 2;
			X_out = X + Width + 2;
			return;
		}
		while ((i != L) && (test_transduction(line, i) == 0))
			tmp = tmp.concat(String.valueOf(line[i++]));
		transduction = "";
		if (i != L) {
			i++;
			while (i != L)
				transduction = transduction.concat(String.valueOf(line[i++]));
		}
		n_lines = 0;
		lines.clear();
		greyed.clear();
		try {
			if (!tmp.equals("<E>"))
				tokenizeTextWithoutTransduction(tmp);
		} catch (final BackSlashAtEndOfLineException e) {
			e.printStackTrace();
		} catch (final MissingGraphNameException e) {
			e.printStackTrace();
		} catch (final NoClosingQuoteException e) {
			e.printStackTrace();
		} catch (final NoClosingRoundBracketException e) {
			e.printStackTrace();
		} catch (final NoClosingSupException e) {
			e.printStackTrace();
		}
		if (!tmp.equals("<E>")) {
			// dimensions of a full box
			Width = maxLineWidth() + 10;
			Height = n_lines * get_h_ligne() + 6;
		} else {
			// dimensions of an empty box
			Height = 20;
			Width = 15;
		}
		Y1 = Y - Height / 2;
		X_out = X + Width + 5;
	}

	/**
	 * Tokenizes a comment box. Returns true if the given String was a valid
	 * comment. If lines is not null, it is filled with the String to be
	 * displayed in the box.
	 */
	public static boolean tokenizeCommentBox(String line,
			ArrayList<String> lines) {
		if (lines != null)
			lines.clear();
		if (line == null || !line.startsWith("/") || line.equals("/")
				|| line.startsWith("/+"))
			return false;
		final int l = line.length();
		final StringBuilder builder = new StringBuilder();
		int i = 1;
		while (i < l) {
			builder.setLength(0);
			while (i < l && line.charAt(i) != '+') {
				if (line.charAt(i) == '\\') {
					if (i + 1 == l) {
						/* A \ at the end of the content is an error */
						if (lines != null)
							lines.clear();
						return false;
					}
					i++;
				}
				builder.append(line.charAt(i));
				i++;
			}
			if (i + 1 == l && line.charAt(i) == '+') {
				/* A + at the end of the content is an error */
				if (lines != null)
					lines.clear();
				return false;
			}
			i++;
			if (lines != null)
				lines.add(builder.toString());
		}
		return true;
	}

	private int strcmp(String a, String b) {
		if (a.compareTo("<E>") == 0) {
			if (b.compareTo("<E>") == 0)
				return 0;
			return -1;
		}
		if (b.compareTo("<E>") == 0)
			return 1;
		return a.compareTo(b);
	}

	private int partition_pour_quicksort(ArrayList<String> v,
			ArrayList<Boolean> v2, int m, int n) {
		String pivot;
		String stringTmp;
		Boolean booleanTmp;
		int i = m - 1;
		int j = n + 1;
		pivot = v.get((m + n) / 2);
		while (true) {
			do
				j--;
			while ((j > (m - 1)) && (strcmp(pivot, v.get(j)) < 0));
			do
				i++;
			while ((i < n + 1) && (strcmp(v.get(i), pivot) < 0));
			if (i < j) {
				stringTmp = v.get(i);
				v.set(i, v.get(j));
				v.set(j, stringTmp);
				booleanTmp = v2.get(i);
				v2.set(i, v2.get(j));
				v2.set(j, booleanTmp);
			} else
				return j;
		}
	}

	private void quicksort(ArrayList<String> v, ArrayList<Boolean> v2, int m,
			int n) {
		int p;
		if (m < n) {
			p = partition_pour_quicksort(v, v2, m, n);
			quicksort(v, v2, m, p);
			quicksort(v, v2, p + 1, n);
		}
	}

	/**
	 * Sorts the lines of the box. If there is an output, it is not taken in
	 * account.
	 */
	public void sortNodeLabel() {
		if (!lines.isEmpty()) {
			quicksort(lines, greyed, 0, lines.size() - 1);
			content = lines.get(0);
			for (int i = 1; i < lines.size(); i++) {
				content = content + "+" + lines.get(i);
			}
		}
	}

	private boolean isUnitTestBox() {
		if (!standaloneBox)
			return false;
		if (lines.size() == 0) {
			/* Should not happen */
			return false;
		}
		if (!Pattern.matches("@TEST:[NIMR]:[SLA]@", lines.get(0)))
			return false;
		/* We check if the second line is of the form xxx<yyy>zzz */
		if (!Pattern.matches("(\\.|[^\\\\])*<(\\.|[^\\\\])+>.*", lines.get(1)))
			return false;
		if (lines.get(0).startsWith("@TEST:M")
				|| lines.get(0).startsWith("@TEST:R")) {
			return lines.size() == 3;
		}
		return lines.size() == 2;
	}

	@Override
	void drawOtherStandalone(Graphics2D g, DrawGraphParams params) {
		if (!isUnitTestBox()) {
			super.drawOtherStandalone(g, params);
			return;
		}
		final Color old = params.getForegroundColor();
		try {
			parentGraphicalZone.getGraphPresentationInfo().setForegroundColor(
					Color.BLUE);
			drawOther(g, params);
		} finally {
			parentGraphicalZone.getGraphPresentationInfo().setForegroundColor(
					old);
		}
	}
}
/* end of GraphBox */
