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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.tfst.Bounds;

/**
 * This class describes a box of a sentence automaton.
 * 
 * @author Sébastien Paumier
 */
public class TfstGraphBox extends GenericGraphBox {
	private Bounds bounds;

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
	public TfstGraphBox(int x, int y, int type, TfstGraphicalZone p) {
		super(x, y, type, p);
	}

	/**
	 * Takes a <code>String</code> representing the box content and tokenizes it
	 * to divide it into several lines
	 * 
	 * @param s
	 *            the box content
	 */
	private void tokenizeText(String s, boolean firstTime) {
		final int L = s.length();
		int i;
		String flechi;
		String canonique;
		String infos;
		if (s.equals("<E>")) {
			/* Nothing to do on the initial empty state */
			return;
		}
		if (firstTime) {
			/*
			 * Any other (non final) state is supposed to contain both a content
			 * and, as output, 4 integers
			 */
			final int slash_pos = s.lastIndexOf('/');
			if (slash_pos == -1) {
				throw new AssertionError("Content with no slash: " + s);
			}
			readTokenInfos(s.substring(slash_pos + 1));
			content = s.substring(0, slash_pos);
		}
		final char[] line = content.toCharArray();
		if (line[0] != '{') {
			n_lines++;
			lines.add(s);
			greyed.add(Boolean.FALSE);
			return;
		}
		i = 1;
		flechi = "";
		while (i < L && line[i] != ',') {
			if (line[i] == '\\' && (i + 1) < L)
				i++;
			flechi = flechi.concat(String.valueOf(line[i]));
			i++;
		}
		i++;
		canonique = "";
		while (i < L && line[i] != '.') {
			if (line[i] == '\\' && (i + 1) < L)
				i++;
			canonique = canonique.concat(String.valueOf(line[i]));
			i++;
		}
		i++;
		infos = "";
		while (i < L && line[i] != '}') {
			if (line[i] == '\\' && (i + 1) < L)
				i++;
			infos = infos.concat(String.valueOf(line[i]));
			i++;
		}
		n_lines++;
		lines.add(flechi);
		greyed.add(Boolean.FALSE);
		if (!(canonique.equals("") || canonique.equals(flechi))) {
			// if inflected form is equal to canonical, we don't insert it twice
			n_lines++;
			lines.add(canonique);
			greyed.add(Boolean.FALSE);
		}
		transduction = infos;
	}

	private void readTokenInfos(String s) {
		final Scanner scanner = new Scanner(s);
		try {
			final int start_pos_in_tokens = scanner.nextInt();
			if (start_pos_in_tokens == -1) {
				bounds = null;
			} else {
				/* Nothing to do if the bounds are not computable */
				final int start_pos_in_chars = scanner.nextInt();
				final int start_pos_in_letters = scanner.nextInt();
				final int end_pos_in_tokens = scanner.nextInt();
				final int end_pos_in_chars = scanner.nextInt();
				final int end_pos_in_letters = scanner.nextInt();
				if (scanner.hasNext()) {
					throw new AssertionError("Malformed token information: "
							+ s);
				}
				bounds = new Bounds(start_pos_in_tokens, start_pos_in_chars,
						start_pos_in_letters, end_pos_in_tokens,
						end_pos_in_chars, end_pos_in_letters);
			}
		} catch (final InputMismatchException e) {
			throw new AssertionError("Malformed token information: " + s);
		} catch (final NoSuchElementException e) {
			throw new AssertionError("Malformed token information: " + s);
		} catch (final IllegalStateException e) {
			throw new AssertionError("Malformed token information: " + s);
		}
	}

	/**
	 * Sets the content of the box
	 * 
	 * @param s
	 *            the content
	 */
	@Override
	public void setContent(String s) {
		if (type == FINAL)
			return; // nothing to do if we consider the final state
		content = s;
		String tmp = "";
		n_lines = 0;
		tmp = s;
		transduction = "";
		lines.clear();
		greyed.clear();
		tokenizeText(s, false);
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

	public void setContentWithBounds(String s) {
		if (type == FINAL)
			return; // nothing to do if we consider the final state
		content = s;
		String tmp = "";
		n_lines = 0;
		tmp = s;
		transduction = "";
		lines.clear();
		greyed.clear();
		tokenizeText(s, true);
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

	public Bounds getBounds() {
		return bounds;
	}

	public void setBounds(Bounds b) {
		bounds = b;
	}

	private final BasicStroke morphologicalStroke = new BasicStroke(2,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 2, new float[] { 10f,
					10f }, 4f);

	/**
	 * Draws a transition to a box. Modified from GenericGraphBox in order to
	 * display bold colored transitions.
	 * 
	 * @param gr
	 *            the graphical context
	 */
	@Override
	public void drawTransition(Graphics2D g, GenericGraphBox dest, DrawGraphParams params) {
		final TfstGraphBox box = (TfstGraphBox) dest;
		if (isNextBoxInSameToken(box)) {
			final Stroke old = g.getStroke();
			g.setStroke(morphologicalStroke);
			super.drawTransition(g, dest, params);
			g.setStroke(old);
		} else {
			super.drawTransition(g, dest, params);
		}
	}

	public boolean isNextBoxInSameToken(TfstGraphBox box) {
		if (box.bounds != null) {
			if (bounds != null
					&& bounds.getEnd_in_tokens() < box.bounds
							.getStart_in_tokens())
				return false;
			if (box.bounds.getStart_in_chars() != 0
					|| box.bounds.getStart_in_letters() != 0
					|| box.content.startsWith("{<E>,")
					|| box.bounds.equals(bounds)
					|| (content.startsWith("{<E>,")
							&& bounds.getEnd_in_tokens() == box.bounds
									.getStart_in_tokens() && bounds
							.getEnd_in_chars() == box.bounds
							.getStart_in_chars())) {
				return true;
			}
		}
		return false;
	}

	private static final Color koreanUntaggedTokenColor = new Color(0xCC, 0xCC, 0xFF);

	@Override
	void drawOther(Graphics2D g, DrawGraphParams params) {
		final Color old = params.getBackgroundColor();
		if (ConfigManager.getManager().isKorean(null)
				&& isKoreanUntaggedToken(content)) {
			params.setBackgroundColor(koreanUntaggedTokenColor);
		}
		final Composite c = g.getComposite();
		if (parentGraphicalZone.decorator != null) {
			g.setComposite(parentGraphicalZone.decorator.getBoxComposite(
					getBoxNumber(), c));
		}
		super.drawOther(g, params);
		g.setComposite(c);
		params.setBackgroundColor(old);
	}

	private boolean isKoreanUntaggedToken(String s) {
		return !s.equals("<E>") && s.charAt(0) != '{';
	}
	
}
