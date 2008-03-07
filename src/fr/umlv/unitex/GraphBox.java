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
import java.util.*;

import fr.umlv.unitex.exceptions.*;

/**
 * This class describes a box of a graph.
 * 
 * @author Sébastien Paumier
 *  
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
	 *  
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
		int L = s.length(), i = 0;
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
							BackSlashAtEndOfLineException e = new BackSlashAtEndOfLineException();
							throw e;
						}
					}
					tmp = tmp.concat(String.valueOf(ligne[i++]));
				}
				if (tmp.length() == 0) {
					MissingGraphNameException e = new MissingGraphNameException();
					throw e;
				}
				// if we had a + separator char (even a japanese one), we put a
				// standard + instead
				if (i < L)
					ligne[i] = '+';
				i++;
				n_lignes++;
				lines.add(tmp);
				greyed.add(new Boolean(true));
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
									BackSlashAtEndOfLineException e = new BackSlashAtEndOfLineException();
									throw e;
								}
							}
							tmp = tmp.concat(String.valueOf(ligne[i++]));
						}
						if (i >= L) {
							NoClosingQuoteException e = new NoClosingQuoteException();
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
									BackSlashAtEndOfLineException e = new BackSlashAtEndOfLineException();
									throw e;
								}
							}
							tmp = tmp.concat(String.valueOf(ligne[i++]));
						}
						if (i >= L) {
							NoClosingSupException e = new NoClosingSupException();
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
									BackSlashAtEndOfLineException e = new BackSlashAtEndOfLineException();
									throw e;
								}
							}
							tmp = tmp.concat(String.valueOf(ligne[i++]));
						}
						if (i >= L) {
							NoClosingRoundBracketException e = new NoClosingRoundBracketException();
							throw e;
						}
						tmp = tmp.concat(String.valueOf(ligne[i++]));
					} else {
						if (ligne[i] == '\\') {
							tmp = tmp.concat(String.valueOf(ligne[i++]));
							if (i >= L) {
								BackSlashAtEndOfLineException e = new BackSlashAtEndOfLineException();
								throw e;
							}
						}
						tmp = tmp.concat(String.valueOf(ligne[i++]));
					}
				}
				if (i < L)
					ligne[i] = '+';
				n_lignes++;
				lines.add(tmp);
				greyed.add(new Boolean(false));
				i++;
			}
		}
	}


	private int maxVariableLineWidth() {
    if (context==null) {
       return 0;   
    }
		FontMetrics f = context.getFontMetrics(variableFont);
		return f.stringWidth("(");
	}

	private int maxContextMarkLineWidth(String mark) {
	    if (context==null) {
	       return 0;   
	    }
			FontMetrics f = context.getFontMetrics(variableFont);
			return f.stringWidth(mark);
		}

	/**
	 * Sets the box content.
	 * 
	 * @param s
	 *            the content
	 */
	public void setContent(String s) {
		if (type == 1)
			return; // nothing to do if we consider the final state
		content = s;
		char ligne[] = new char[10000];
		String tmp = "";
		int i, L;

		ligne = content.toCharArray();
		i = 0;
		L = content.length();
		if (ligne[0] == '$' && (ligne[L - 1] == '(' || ligne[L - 1] == ')')) {
			////////////////////////////////////////////
			// case of $a( or $a)
			////////////////////////////////////////////
			variable = true;
			lines.clear();
			greyed.clear();
			lines.add(String.valueOf(ligne[L - 1]));
			greyed.add(new Boolean(false));
			transduction = content.substring(1, L - 1);
			n_lignes = 1;
			Height = get_h_variable_ligne() + 3;
			Width = maxVariableLineWidth() + 5;
			Y1 = Y - Height / 2;
			X_out = x + Width + 2;
			return;
		}
		variable = false;
		if (content.equals("$[") || content.equals("$![")
			|| content.equals("$]")) {
			////////////////////////////////////////////
			// case of context marks ($[ or $![) :
			// ab $[ cd $]  => ab followed by cd
			// ab $![ cd $] => ab not followed by cd
			////////////////////////////////////////////
			contextMark = true;
			lines.clear();
			greyed.clear();
			String sub=content.substring(1);
			lines.add(sub);
			greyed.add(new Boolean(false));
			transduction = "";
			n_lignes = 1;
			Height = get_h_variable_ligne() + 3;
			Width = maxContextMarkLineWidth(sub) + 5;
			Y1 = Y - Height / 2;
			X_out = x + Width + 2;
			return;
		}
		contextMark=false;
		while ((i != L) && (test_transduction(ligne, i) == 0))
			tmp = tmp.concat(String.valueOf(ligne[i++]));
		transduction = "";
		if (i != L) {
			i++;
			while (i != L)
				transduction = transduction.concat(String.valueOf(ligne[i++]));
		}
		n_lignes = 0;
		lines.clear();
		greyed.clear();
		try {
			if (!tmp.equals("<E>"))
				tokenizeTextWithoutTransduction(tmp);
		} catch (BackSlashAtEndOfLineException e) {
			e.printStackTrace();
		} catch (MissingGraphNameException e) {
			e.printStackTrace();
		} catch (NoClosingQuoteException e) {
			e.printStackTrace();
		} catch (NoClosingRoundBracketException e) {
			e.printStackTrace();
		} catch (NoClosingSupException e) {
			e.printStackTrace();
		}
		if (!tmp.equals("<E>")) {
			// dimensions of a full box
			Width = maxLineWidth() + 10;
			Height = n_lignes * get_h_ligne() + 6;
		} else {
			// dimensions of an empty box
			Height = 20;
			Width = 15;
		}
		Y1 = Y - Height / 2;
		X_out = x + Width + 5;
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

	private int partition_pour_quicksort(ArrayList<String> v, ArrayList<Boolean> v2, int m, int n) {
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
				v.set(i,v.get(j));
				v.set(j,stringTmp);
				booleanTmp = v2.get(i);
				v2.set(i,v2.get(j));
				v2.set(j,booleanTmp);
			} else
				return j;
		}
	}

	private void quicksort(ArrayList<String> v, ArrayList<Boolean> v2, int m, int n) {
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
	 *  
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

}

/* end of GraphBox */
