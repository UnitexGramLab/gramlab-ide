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
package org.gramlab.core.umlv.unitex.debug;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.gramlab.core.umlv.unitex.files.FileUtil;
import org.gramlab.core.umlv.unitex.graphrendering.GenericGraphBox;
import org.gramlab.core.umlv.unitex.io.Encoding;
import org.gramlab.core.umlv.unitex.io.GraphIO;

public class DebugInfos {
	public File concordIndFile = null;
	public ArrayList<String> graphNames = new ArrayList<String>();
	public ArrayList<File> graphs = new ArrayList<File>();
	public ArrayList<String> lines = new ArrayList<String>();
	public HashMap<Integer, GraphIO> graphIOMap = new HashMap<Integer, GraphIO>();

	public static DebugInfos loadConcordanceIndex(File html) {
		final String concord_ind = FileUtil.getFileNameWithoutExtension(html)
				+ ".ind";
		final File f = new File(concord_ind);
		if (!f.exists())
			return null;
		Scanner scanner = null;
		try {
			scanner = Encoding.getScanner(f);
			final String z = scanner.nextLine();
			if (!z.startsWith("#D")) {
				scanner.close();
				return null;
			}
			final DebugInfos infos = new DebugInfos();
			infos.concordIndFile = f;
			int n = scanner.nextInt();
			scanner.nextLine();
			final Pattern normalDelimiter = scanner.delimiter();
			while (n > 0) {
				String line = scanner.nextLine();
				String s = "";
				int pos = line.indexOf((char) 1);
				if (pos != -1) {
					s = line.substring(0, pos);
					pos++;
				} else {
					pos = 0;
				}
				line = line.substring(pos);
				infos.graphNames.add(s);
				if (s.equals(""))
					infos.graphs.add(null);
				else
					infos.graphs.add(new File(line));
				n--;
			}
			/* We skip the #[IMR] line */
			scanner.nextLine();
			while (scanner.hasNextLine()) {
				/* We skip the match coordinates */
				scanner.next();
				scanner.next();
				/* We skip the normal output part */
				scanner.useDelimiter("" + (char) 1);
				scanner.next();
				scanner.useDelimiter(normalDelimiter);
				infos.lines.add(scanner.nextLine());
			}
			scanner.close();
			return infos;
		} catch (final NoSuchElementException e2) {
			if (scanner != null)
				scanner.close();
			return null;
		}
	}

	/**
	 * Note: n must be in [1;number of graphs]
	 */
	public GraphIO getGraphIO(int n) {
		GraphIO gio = graphIOMap.get(Integer.valueOf(n));
		if (gio == null) {
			/*
			 * If we try to load a graph for the first time, we check if it has
			 * been modified since the concordance was built. Once loaded, we
			 * use the cached version, so that we are sure to debug on the
			 * correct version, even if the graph has been changed while
			 * debugging
			 */
			final File f = graphs.get(n - 1);
			if (f == null) {
				/*
				 * If f is null, it is because it was indicated as not to be
				 * being loaded in concord.ind because it was an empty graph in
				 * the fst2, or a graph that was part of a precompiled fst2
				 */
				return null;
			}
			if (f.lastModified() > concordIndFile.lastModified()) {
				JOptionPane
						.showMessageDialog(
								null,
								"File "
										+ f.getAbsolutePath()
										+ " has been modified\n"
										+ "since the concordance index was built. Cannot debug it.",
								"Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
			gio = GraphIO.loadGraph(f, false, false);
			if (gio == null) {
				JOptionPane.showMessageDialog(null,
						"Cannot load graph " + f.getAbsolutePath(), "Error",
						JOptionPane.ERROR_MESSAGE);
				return null;
			}
			graphIOMap.put(Integer.valueOf(n), gio);
		}
		return gio;
	}

	public int getEpsilonLineInInitialState(int graph) {
		final GraphIO gio = getGraphIO(graph);
		if (gio == null)
			return -1;
		final GenericGraphBox box = gio.getBoxes().get(0);
		return box.lines.indexOf("<E>");
	}

	public ArrayList<DebugDetails> getMatchDetails(int n,
			ArrayList<DebugDetails> d) {
		if (d == null) {
			d = new ArrayList<DebugDetails>();
		}
		d.clear();
		final Scanner scanner = new Scanner(lines.get(n));
		scanner.useDelimiter("" + (char) 2);
		while (scanner.hasNext()) {
			/* We skip the initial char #1 */
			final String output = scanner.next().substring(1);
			scanner.useDelimiter(":");
			final int graph = Integer.parseInt(scanner.next().substring(1));
			final int box = scanner.nextInt();
			scanner.useDelimiter("" + (char) 3);
			final int line = Integer.parseInt(scanner.next().substring(1));
			scanner.useDelimiter("" + (char) 4);
			final String tag = scanner.next().substring(1);
			scanner.useDelimiter("" + (char) 1);
			final String matched = scanner.next().substring(1);
			d.add(new DebugDetails(tag, output, matched, graph, box, line, this));
			scanner.useDelimiter("" + (char) 2);
		}
		scanner.close();
		DebugDetails tmp = d.get(0);
		if (tmp.box != 0) {
			/* If necessary, we add the initial state */
			final DebugDetails tmp2 = new DebugDetails("<E>", "", "",
					tmp.graph, 0, getEpsilonLineInInitialState(tmp.graph), this);
			d.add(0, tmp2);
		}
		tmp = d.get(d.size() - 1);
		if (tmp.box != 1) {
			/* If necessary, we add the final state */
			final DebugDetails tmp2 = new DebugDetails("", "", "", tmp.graph,
					1, 0, this);
			d.add(tmp2);
		}
		/* And we add the initial and final states for all subgraphs called */
		for (int i = 1; i < d.size(); i++) {
			tmp = d.get(i);
			if (tmp.tag.startsWith("<< ")) {
				/*
				 * If we have a subgraph call, we add the initial state, if
				 * needed
				 */
				final DebugDetails tmp2 = d.get(i + 1);
				if (tmp2.box != 0) {
					tmp = new DebugDetails("<E>", "", "", tmp2.graph, 0,
							getEpsilonLineInInitialState(tmp2.graph), this);
					d.add(i + 1, tmp);
				}
				continue;
			}
			if (tmp.tag.startsWith(">> ")) {
				/*
				 * If we have a subgraph call end, we add the initial state, if
				 * needed
				 */
				final DebugDetails tmp2 = d.get(i - 1);
				if (tmp2.box != 0) {
					tmp = new DebugDetails("", "", "", tmp2.graph, 1, 0, this);
					d.add(i, tmp);
				}
				i++;
				continue;
			}
		}
		if (!restore_E_steps(d)) {
			d.clear();
		}
		return d;
	}

	/**
	 * In debug mode, <E> with no output are compiled without debug information,
	 * so that they cannot be present in debug concordance. So, this function is
	 * there to restore those <E> steps in graph exploration.
	 */
	private boolean restore_E_steps(ArrayList<DebugDetails> d) {
		for (int i = 0; i < d.size() - 1; i++) {
			final DebugDetails src = d.get(i);
			final DebugDetails dst = d.get(i + 1);
			final File f = graphs.get(src.graph - 1);
			if (src.tag.equals("$![")) {
				/*
				 * Special case of a forbidden right context Such contexts
				 * should never be catched in debug mode, so that the immediate
				 * next tag should be the $] one
				 */
				if (dst.tag.equals("$]")) {
					/* We go on */
					continue;
				}
				JOptionPane.showMessageDialog(null,
						"Unexpected non empty forbidden context between "
								+ src.box + " and " + dst.box + " in graph "
								+ f.getAbsolutePath(), "Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if (src.graph != dst.graph) {
				/*
				 * There cannot be a missing <E> if the graphs are different
				 */
				continue;
			}
			final GraphIO gio = getGraphIO(src.graph);
			if (gio == null) {
				return false;
			}
			final GenericGraphBox srcBox = gio.getBoxes().get(src.box);
			final GenericGraphBox dstBox = gio.getBoxes().get(dst.box);
			if (srcBox.transitions.contains(dstBox)) {
				/* Nothing to do if there is a transition */
				continue;
			}

			if (src.box == dst.box) {
				if (src.line == dst.line) {
					/*
					 * If we are in the same line of the same box, it may be
					 * because there is only one tag in the line and a loop on
					 * the box, but it may also be because the line contains
					 * several tokens
					 */
					final String line = srcBox.lines.get(src.line);
					int pos = line.indexOf(src.tag);
					pos = line.indexOf(dst.tag, pos + src.tag.length());
					if (pos != -1) {
						/* We are in the same line, nothing to do */
						continue;
					}
					/* We may also have extra # or " " tags that have been added
					 * by the strict tokenization option of Grf2Fst2.
					 */
					if (src.tag.equals("#") || src.tag.equals(" ")
							|| dst.tag.equals("#") || dst.tag.equals(" ")) {
						continue;
					}
					
					/*
					 * It may also be because the box contains a range
					 * indication
					 */
					if (srcBox.transduction != null
							&& srcBox.transduction.startsWith("$[")) {
						continue;
					}
				} else {
					/*
					 * Not in the same line. It must be because the box contains
					 * a range indication
					 */
					if (srcBox.transduction != null
							&& srcBox.transduction.startsWith("$[")) {
						continue;
					}
				}
			}
			final ArrayList<GenericGraphBox> visited = new ArrayList<GenericGraphBox>();
			final ArrayList<Integer> path = new ArrayList<Integer>();
			if (!findEpsilonPath(0, srcBox, dstBox, visited, path,
					gio.getBoxes())) {
				JOptionPane.showMessageDialog(null,
						"Cannot find <E> path between box " + src.box + " and "
								+ dst.box + " in graph " + f.getAbsolutePath(),
						"Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			for (int j = 0; j < path.size(); j = j + 2) {
				final int box = path.get(j);
				final int line = path.get(j + 1);
				final DebugDetails det = new DebugDetails("<E>", "", "",
						src.graph, box, line, this);
				i++;
				d.add(i, det);
			}
		}
		return true;
	}

	private boolean findEpsilonPath(int depth, GenericGraphBox current,
			GenericGraphBox dstBox, ArrayList<GenericGraphBox> visited,
			ArrayList<Integer> path, ArrayList<GenericGraphBox> boxes) {
		if (current.equals(dstBox) && depth > 0)
			return true;
		if (visited.contains(current))
			return false;
		visited.add(current);
		if (depth == 0) {
			/* Special of the starting box */
			for (final GenericGraphBox dest : current.transitions) {
				if (findEpsilonPath(depth + 1, dest, dstBox, visited, path,
						boxes))
					return true;
			}
			return false;
		}
		if (current.transduction != null && current.transduction.length() > 0) {
			/* Boxes with an output cannot be considered */
			return false;
		}
		if (current.lines.size() == 0) {
			/* Case of a box only containing <E> */
			path.add(boxes.indexOf(current));
			path.add(0);
			for (final GenericGraphBox dest : current.transitions) {
				if (findEpsilonPath(depth + 1, dest, dstBox, visited, path,
						boxes))
					return true;
			}
			path.remove(path.size() - 1);
			path.remove(path.size() - 1);
			return false;
		}
		for (int i = 0; i < current.lines.size(); i++) {
			if (current.lines.get(i).equals("<E>")) {
				/* The box line is a candidate */
				path.add(boxes.indexOf(current));
				path.add(i);
				for (final GenericGraphBox dest : current.transitions) {
					if (findEpsilonPath(depth + 1, dest, dstBox, visited, path,
							boxes))
						return true;
				}
				path.remove(path.size() - 1);
				path.remove(path.size() - 1);
				/* An <E> is enough to go through a box, so we can stop */
				break;
			}
		}
		return false;
	}
}
