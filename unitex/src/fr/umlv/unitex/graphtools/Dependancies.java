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
package fr.umlv.unitex.graphtools;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.graphrendering.GenericGraphBox;
import fr.umlv.unitex.io.GraphIO;

/**
 * This class provides methods for building graph dependencies.
 * 
 * @author Sébastien Paumier
 */
public class Dependancies {
	/**
	 * Looks recursively in the given directory for all graphs that call the
	 * given one
	 * 
	 * @param grf
	 * @param rootDir
	 * @return
	 */
	public static ArrayList<GraphCall> whoCalls(File grf, File rootDir) {
		final ArrayList<GraphCall> callers = new ArrayList<GraphCall>();
		if (!rootDir.isDirectory())
			throw new IllegalArgumentException("Directory expected");
		final HashMap<File, ArrayList<GraphCall>> map = new HashMap<File, ArrayList<GraphCall>>();
		getAllGraphDependencies(rootDir, map);
		for (final File f : map.keySet()) {
			final ArrayList<GraphCall> list = map.get(f);
			for (final GraphCall c : list) {
				if (c.getGrf().equals(grf)) {
					callers.add(new GraphCall(f));
				}
			}
		}
		Collections.sort(callers);
		return callers;
	}

	/**
	 * This function returns the list of all the graphs called by the given grf.
	 */
	public static ArrayList<GraphCall> getAllSubgraphs(File grf) {
		final HashMap<File, ArrayList<GraphCall>> map = new HashMap<File, ArrayList<GraphCall>>();
		computeGraphDependencies(grf, map, true, false);
		final ArrayList<GraphCall> result = new ArrayList<GraphCall>();
		for (final ArrayList<GraphCall> values : map.values()) {
			for (final GraphCall f : values) {
				if (!result.contains(f)) {
					result.add(f);
				}
			}
		}
		Collections.sort(result);
		return result;
	}

	private static void getAllGraphDependencies(File rootDir,
			HashMap<File, ArrayList<GraphCall>> map) {
		final File[] files = rootDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				final File tmp = new File(dir, name);
				return name.endsWith(".grf") || tmp.isDirectory();
			}
		});
		if (files == null)
			return;
		for (final File f : files) {
			if (f.isFile())
				computeGraphDependencies(f, map, false, true);
			else
				getAllGraphDependencies(f, map);
		}
	}

	/**
	 * This function computes the list of graphs directly called from grf. main
	 * is supposed to be true only if grf is the main graph on which the
	 * dependency request was made.
	 */
	private static ArrayList<GraphCall> getSubgraphs(File grf,
			boolean emitErrorMessages, boolean main, boolean whoCallsMode) {
		GraphIO io;
		try {
			io = GraphIO.loadGraph(grf, false, false);
			if (io == null)
				return null;
		} catch (final Exception e) {
			return null;
		}
		final ArrayList<GraphCall> subgraphs = new ArrayList<GraphCall>();
		final boolean[] accessible = new boolean[io.getBoxes().size()];
		final int[] coaccessible = new int[io.getBoxes().size()];
		for (int i = 0; i < coaccessible.length; i++)
			coaccessible[i] = UNTESTED;
		markAccessibleBoxes(io.getBoxes(), accessible, 0);
		for (int i = 0; i < io.getBoxes().size(); i++) {
			if (isCoaccessibleBoxes(io.getBoxes(), coaccessible, i)) {
				coaccessible[i] = TESTED_TRUE;
			} else {
				coaccessible[i] = TESTED_FALSE;
			}
		}
		for (int i = 0; i < io.getBoxes().size(); i++) {
			final boolean useful = accessible[i]
					&& coaccessible[i] == TESTED_TRUE;
			addSubgraphs(subgraphs, io.getBoxes().get(i), grf,
					emitErrorMessages, useful, main, whoCallsMode);
		}
		return subgraphs;
	}

	/**
	 * This function adds all graphs called by grf into the given map. If grf is
	 * already a key in the map, then it does nothing.
	 * 
	 * main is supposed to be true only when the function is called on the
	 * original grf on which the dependency request was made.
	 * 
	 * If whoCallsMode is true, we only consider useful graphs
	 */
	private static void computeGraphDependencies(File grf,
			HashMap<File, ArrayList<GraphCall>> map, boolean main,
			boolean whoCallsMode) {
		if (map.containsKey(grf))
			return;
		/* We look for the graphs that all directly called from grf */
		final ArrayList<GraphCall> files = getSubgraphs(grf, false, main,
				whoCallsMode);
		if (files == null)
			return;
		final ArrayList<GraphCall> res = new ArrayList<GraphCall>();
		for (final GraphCall c : files) {
			res.add(c);
		}
		map.put(grf, res);
		for (final GraphCall f : res) {
			computeGraphDependencies(f.getGrf(), map, false, whoCallsMode);
		}
	}

	/**
	 * Adds to the given list the subgraphs contained in the given box.
	 */
	private static void addSubgraphs(ArrayList<GraphCall> subgraphs,
			GenericGraphBox box, File parent, boolean emitErrorMessages,
			boolean useful, boolean main, boolean whoCallsMode) {
		if (!whoCallsMode && !useful && !main) {
			/*
			 * useless graphs are only collected 1) in whoCallsMode 2) or if we
			 * are in the main graph
			 */
			return;
		}
		for (int i = 0; i < box.lines.size(); i++) {
			if (box.greyed.get(i)) {
				/* If we have a subgraph call */
				final File f = getSubgraph(box.lines.get(i), parent,
						emitErrorMessages);
				if (f != null && !subgraphs.contains(f) && !f.equals(parent)) {
					boolean there = false;
					for (final GraphCall c : subgraphs) {
						if (c.getGrf().equals(f)) {
							/*
							 * The graph may already be present in our list, but
							 * with a different usefulness. In such a case, we
							 * update the usefulness in order to indicate that
							 * the graph appears in at least one useless path
							 */
							if (!useful) {
								c.setUseful(false);
							}
							there = true;
						}
					}
					if (!there) {
						if (whoCallsMode) {
							/*
							 * When we look for graph callers, we don't make any
							 * distinction between direct and indirect graphs
							 */
							subgraphs.add(new GraphCall(f, true, true));
						} else {
							subgraphs.add(new GraphCall(f, useful, main));
						}
					}
				}
			}
		}
	}

	private static File getSubgraph(String s, File parent,
			@SuppressWarnings("unused") boolean emitErrorMessages) {
		if (!s.endsWith(".grf")) {
			s = s + ".grf";
		}
		/* replace ':' by '/' resp. '\\' */
		if (s.startsWith(":")) {
			// if the graph is located in the package repository
			return new File(
					GenericGraphBox.getNormalizeGraphCall(s));
		}
		// otherwise
		File f = new File(s);
		if (Config.getCurrentSystem() == Config.WINDOWS_SYSTEM
				&& f.isAbsolute()) {
			// first we test if we have an absolute windows pathname,
			// in order to avoid wrong transformations like:
			//
			// C:\\foo\foo.grf => C\\\foo\foo.grf
			//
			return f;
		}
		s = s.replace(':', File.separatorChar);
		if (!f.isAbsolute()) {
			f = new File(parent.getParentFile(), s);
		}
		return f;
	}

	private static void markAccessibleBoxes(ArrayList<GenericGraphBox> boxes,
			boolean[] marked, int n) {
		if (marked[n] == true)
			return;
		marked[n] = true;
		final GenericGraphBox b = boxes.get(n);
		final ArrayList<GenericGraphBox> dest = b.getTransitions();
		if (dest == null)
			return;
		for (final GenericGraphBox box : dest) {
			markAccessibleBoxes(boxes, marked, boxes.indexOf(box));
		}
	}

	private final static int UNTESTED = 0;
	private final static int TESTED_TRUE = 1;
	private final static int TESTED_FALSE = 2;
	private final static int BEING_TESTED = 3;

	private static boolean isCoaccessibleBoxes(
			ArrayList<GenericGraphBox> boxes, int[] marked, int n) {
		if (marked[n] == TESTED_FALSE || marked[n] == TESTED_TRUE) {
			return marked[n] == TESTED_TRUE;
		}
		if (marked[n] == BEING_TESTED) {
			return false;
		}
		marked[n] = BEING_TESTED;
		final GenericGraphBox b = boxes.get(n);
		if (b.type == GenericGraphBox.FINAL) {
			marked[n] = TESTED_TRUE;
			return true;
		}
		final ArrayList<GenericGraphBox> dest = b.getTransitions();
		if (dest == null) {
			marked[n] = TESTED_FALSE;
			return false;
		}
		for (final GenericGraphBox box : dest) {
			if (isCoaccessibleBoxes(boxes, marked, boxes.indexOf(box))) {
				marked[n] = UNTESTED;
				return true;
			}
		}
		marked[n] = UNTESTED;
		return false;
	}
}
