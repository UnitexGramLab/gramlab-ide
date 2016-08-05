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
package org.gramlab.core.umlv.unitex.frames;

import java.io.File;

import org.gramlab.core.umlv.unitex.io.GraphIO;

class GraphFrameFactory extends MultiInstanceFrameFactory<GraphFrame, File> {

	GraphFrame getGraphFrame(File grf) {
		final GraphFrame f1 = getFrameIfExists(grf);
		if (f1 != null)
			return f1;
		final GraphFrame f;
		if (grf != null) {
			final GraphIO g = GraphIO.loadGraph(grf, false, true);
			if (g == null)
				return null;
			f = new GraphFrame(g);
		} else {
			f = new GraphFrame(null);
		}
		addFrame(f);
		return f;
	}

	public void saveAllFrames() {
		for (final GraphFrame f : frames) {
			f.saveGraph();
		}
	}
}
