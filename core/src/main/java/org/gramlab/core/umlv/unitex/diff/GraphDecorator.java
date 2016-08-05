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
package org.gramlab.core.umlv.unitex.diff;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Stroke;
import java.io.File;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JLabel;

import org.gramlab.core.umlv.unitex.debug.Coverage;
import org.gramlab.core.umlv.unitex.io.Encoding;
import org.gramlab.core.umlv.unitex.tfst.tagging.TaggingModel;
import org.gramlab.core.umlv.unitex.tfst.tagging.TaggingState;

public class GraphDecorator {
	/* Those fields are used in diff mode */
	boolean base = true;
	public ArrayList<String> propertyOps = new ArrayList<String>();
	ArrayList<Integer> boxAdded = new ArrayList<Integer>();
	ArrayList<Integer> boxRemoved = new ArrayList<Integer>();
	ArrayList<Integer> boxContentChanged = new ArrayList<Integer>();
	ArrayList<Integer> boxMoved = new ArrayList<Integer>();
	ArrayList<Integer> transitionAdded = new ArrayList<Integer>();
	ArrayList<Integer> transitionRemoved = new ArrayList<Integer>();
	/* Those fields are used in debug mode */
	private int currentGraph = -1;
	private int currentBox = -1;
	private int currentLine = -1;
	/* This is used for tagging display in text automaton */
	private final TaggingModel model;
	private Coverage coverage = null;

	public GraphDecorator(TaggingModel model) {
		this.model = model;
	}

	public static GraphDecorator loadDiffFile(File f) {
		final Scanner scanner = Encoding.getScanner(f);
		final GraphDecorator info = new GraphDecorator(null);
		while (scanner.hasNext()) {
			try {
				final String s = scanner.next();
				if (s.equals("P")) {
					/* Propery changed */
					info.propertyOps.add(scanner.next());
					continue;
				}
				if (s.equals("M")) {
					/* Box moved */
					info.boxMoved.add(scanner.nextInt());
					info.boxMoved.add(scanner.nextInt());
					continue;
				}
				if (s.equals("C")) {
					/* Box content changed */
					info.boxContentChanged.add(scanner.nextInt());
					info.boxContentChanged.add(scanner.nextInt());
					continue;
				}
				if (s.equals("A")) {
					/* Box added */
					info.boxAdded.add(scanner.nextInt());
					continue;
				}
				if (s.equals("R")) {
					/* Box removed */
					info.boxRemoved.add(scanner.nextInt());
					continue;
				}
				if (s.equals("T")) {
					/*
					 * Transition added: we only store the information about the
					 * dest graph
					 */
					scanner.nextInt();
					scanner.nextInt();
					info.transitionAdded.add(scanner.nextInt());
					info.transitionAdded.add(scanner.nextInt());
					continue;
				}
				if (s.equals("X")) {
					/*
					 * Transition removed: we only store the information about
					 * the base graph
					 */
					info.transitionRemoved.add(scanner.nextInt());
					info.transitionRemoved.add(scanner.nextInt());
					scanner.nextInt();
					scanner.nextInt();
					continue;
				}
				scanner.close();
				return null;
			} catch (final NoSuchElementException e) {
				scanner.close();
				return null;
			}
		}
		scanner.close();
		return info;
	}

	@SuppressWarnings("unchecked")
	public GraphDecorator clone(boolean b) {
		final GraphDecorator info = new GraphDecorator(null);
		info.base = b;
		info.propertyOps = (ArrayList<String>) propertyOps.clone();
		info.boxAdded = (ArrayList<Integer>) boxAdded.clone();
		info.boxRemoved = (ArrayList<Integer>) boxRemoved.clone();
		info.boxContentChanged = (ArrayList<Integer>) boxContentChanged.clone();
		info.boxMoved = (ArrayList<Integer>) boxMoved.clone();
		info.transitionAdded = (ArrayList<Integer>) transitionAdded.clone();
		info.transitionRemoved = (ArrayList<Integer>) transitionRemoved.clone();
		return info;
	}

	public boolean hasBeenRemoved(int n) {
		return base && boxRemoved.contains(Integer.valueOf(n));
	}

	public boolean hasBeenAdded(int n) {
		return !base && boxAdded.contains(Integer.valueOf(n));
	}

	public boolean contentChanged(int n) {
		for (int i = base ? 0 : 1; i < boxContentChanged.size(); i += 2) {
			if (n == boxContentChanged.get(i))
				return true;
		}
		return false;
	}

	public boolean hasMoved(int n) {
		for (int i = base ? 0 : 1; i < boxMoved.size(); i += 2) {
			if (n == boxMoved.get(i))
				return true;
		}
		return false;
	}

	public boolean transitionRemoved(int n, int dest) {
		if (!base)
			return false;
		for (int i = 0; i < transitionRemoved.size(); i += 2) {
			if (n == transitionRemoved.get(i)
					&& dest == transitionRemoved.get(i + 1))
				return true;
		}
		return false;
	}

	public boolean transitionAdded(int n, int dest) {
		if (base)
			return false;
		for (int i = 0; i < transitionAdded.size(); i += 2) {
			if (n == transitionAdded.get(i)
					&& dest == transitionAdded.get(i + 1))
				return true;
		}
		return false;
	}

	public boolean noDifference() {
		return propertyOps.size() == 0 && boxAdded.size() == 0
				&& boxRemoved.size() == 0 && boxContentChanged.size() == 0
				&& transitionAdded.size() == 0 && transitionRemoved.size() == 0
				&& boxMoved.size() == 0;
	}

	public Color getTransitionColor(int boxNumber, int destNumber, Color c) {
		if (transitionAdded(boxNumber, destNumber))
			return GraphDecoratorConfig.ADDED;
		if (transitionRemoved(boxNumber, destNumber))
			return GraphDecoratorConfig.REMOVED;
		if (model != null
				&& (model.isToBeRemovedTfstIndex(boxNumber) || model
						.isToBeRemovedTfstIndex(destNumber))) {
			return GraphDecoratorConfig.SHADED;
		}
		if (model != null && model.isLinearTfst())
			return GraphDecoratorConfig.LINEAR_TFST;
		return c;
	}

	public Stroke getTransitionStroke(int boxNumber, int destNumber, Stroke s) {
		if (transitionAdded(boxNumber, destNumber))
			return GraphDecoratorConfig.STROKE;
		if (transitionRemoved(boxNumber, destNumber))
			return GraphDecoratorConfig.STROKE;
		return s;
	}

	public Stroke getBoxStroke(int boxNumber, Stroke s) {
		if (hasBeenAdded(boxNumber))
			return GraphDecoratorConfig.STROKE;
		if (hasBeenRemoved(boxNumber))
			return GraphDecoratorConfig.STROKE;
		if (hasMoved(boxNumber))
			return GraphDecoratorConfig.STROKE;
		if (boxNumber == currentBox)
			return GraphDecoratorConfig.STROKE;
		if (model != null && model.isSelected(boxNumber)) {
			return GraphDecoratorConfig.STROKE;
		}
		return s;
	}

	public Color getBoxShapeColor(int boxNumber, Color c) {
		if (hasBeenAdded(boxNumber))
			return GraphDecoratorConfig.ADDED;
		if (hasBeenRemoved(boxNumber))
			return GraphDecoratorConfig.REMOVED;
		if (hasMoved(boxNumber))
			return GraphDecoratorConfig.MOVED;
		if (boxNumber == currentBox)
			return GraphDecoratorConfig.DEBUG_HIGHLIGHT;
		if (model != null && model.isLinearTfst())
			return GraphDecoratorConfig.LINEAR_TFST;
		return c;
	}

	public boolean isLinearTfst() {
		return model != null && model.isLinearTfst();
	}

	public boolean isBoxLineHighlighted(int boxNumber, int lineNumber) {
		return (boxNumber == currentBox && lineNumber == currentLine);
	}

	public boolean requiresSpecialLineDrawing(int boxNumber) {
		return hasBeenAdded(boxNumber)
				|| hasBeenRemoved(boxNumber)
				|| hasMoved(boxNumber)
				|| boxNumber == currentBox
				|| (model != null && TaggingState.SELECTED == model
						.getBoxStateTfst(boxNumber));
	}

	public boolean isHighlighted(int boxNumber) {
		return boxNumber == currentBox;
	}

	public Color getBoxFillColor(int boxNumber, Color c) {
		if (contentChanged(boxNumber))
			return GraphDecoratorConfig.CONTENT_CHANGED;
		return c;
	}

	public Composite getBoxComposite(int boxNumber, Composite c) {
		if (model != null && model.isToBeRemovedTfstIndex(boxNumber))
			return GraphDecoratorConfig.SHADE_COMPOSITE;
		return c;
	}

	public Color getBoxOutputColor(int boxNumber, Color c) {
		if (contentChanged(boxNumber))
			return GraphDecoratorConfig.CONTENT_CHANGED;
		if (boxNumber == currentBox && currentLine == -2)
			return GraphDecoratorConfig.OUTPUT_HIGHLIGHTED;
		return c;
	}

	public void clear() {
		propertyOps.clear();
		boxAdded.clear();
		boxRemoved.clear();
		boxContentChanged.clear();
		boxMoved.clear();
		transitionAdded.clear();
		transitionRemoved.clear();
		currentGraph = -1;
		currentBox = -1;
		currentLine = -1;
	}

	/**
	 * Note: graph must be in [1;number of graphs]
	 */
	public void highlightBoxLine(int graph, int box, int line) {
		currentGraph = graph;
		currentBox = box;
		currentLine = line;
	}

	private final JLabel coverageRenderer = createCoverageRenderer();

	public JLabel getCoverageInfoLabel(int box) {
		if (coverage == null)
			return null;
		coverageRenderer
				.setText("" + coverage.getBoxCounter(currentGraph, box));
		coverageRenderer.setSize(coverageRenderer.getPreferredSize());
		return coverageRenderer;
	}

	private JLabel createCoverageRenderer() {
		final JLabel l = new JLabel();
		l.setOpaque(true);
		l.setBackground(Color.WHITE);
		l.setForeground(Color.RED);
		final Font f = l.getFont();
		l.setFont(f.deriveFont(8));
		return l;
	}

	public void setCoverage(Coverage c) {
		coverage = c;
	}

}
