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
package fr.umlv.unitex.debug;

public class DebugDetails {
	public static String[] fields = new String[] { "Tag", "Output", "Matched",
			"Graph", "Box", "Line" };
	public String tag, output, matched;
	public int graph, box, line;

	public DebugDetails(String tag, String output, String matched, int graph,
			int box, int line, DebugInfos infos) {
		this.tag = tag;
		if (tag.startsWith((char) 5 + "<")) {
			this.tag = "<< "
					+ infos.graphNames
							.get(Integer.parseInt(tag.substring(2)) - 1);
		} else if (tag.startsWith((char) 5 + ">")) {
			this.tag = ">> "
					+ infos.graphNames
							.get(Integer.parseInt(tag.substring(2)) - 1);
		}
		this.output = output;
		this.matched = matched;
		this.graph = graph;
		this.box = box;
		this.line = line;
	}

	public Object getField(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return tag;
		case 1:
			return output;
		case 2:
			return matched;
		case 3:
			return Integer.valueOf(graph);
		case 4:
			return Integer.valueOf(box);
		case 5:
			return Integer.valueOf(line);
		default:
			throw new IllegalArgumentException("Invalid field index: "
					+ columnIndex);
		}
	}
}
