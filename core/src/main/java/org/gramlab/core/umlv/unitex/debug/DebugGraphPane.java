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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.gramlab.core.umlv.unitex.diff.GraphDecorator;
import org.gramlab.core.umlv.unitex.frames.InternalFrameManager;
import org.gramlab.core.umlv.unitex.graphrendering.GenericGraphBox;
import org.gramlab.core.umlv.unitex.graphrendering.GraphicalZone;
import org.gramlab.core.umlv.unitex.graphrendering.TextField;
import org.gramlab.core.umlv.unitex.io.GraphIO;
import org.gramlab.core.umlv.unitex.project.manager.UnitexProjectManager;

public class DebugGraphPane extends JPanel {
	private final DebugInfos infos;
	private int currentGraph = -1;
	private final GraphDecorator decorator = new GraphDecorator(null);
	private JScrollPane scroll = null;
	private final HashMap<File, Point> scrollPreferences = new HashMap<File, Point>();
	private GraphicalZone graphicalZone = null;
	private final MouseListener listener;

	public DebugGraphPane(final DebugInfos infos) {
		super(new BorderLayout());
		this.infos = infos;
		this.listener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && getCurrentGraph() != -1) {
					final File f = infos.graphs.get(getCurrentGraph() - 1);
					UnitexProjectManager.search(f).getFrameManagerAs(InternalFrameManager.class)
							.newGraphFrame(f);
				}
			}
		};
		decorator.setCoverage(Coverage.computeCoverageInfos(infos));
	}

	public void clear() {
		if (scroll != null) {
			scrollPreferences.put(infos.graphs.get(getCurrentGraph() - 1),
					scroll.getViewport().getViewPosition());
		}
		setCurrentGraph(-1);
		scroll = null;
		if (graphicalZone != null) {
			graphicalZone.removeMouseListener(listener);
			graphicalZone = null;
		}
		removeAll();
		revalidate();
		repaint();
	}

	public void setDisplay(int graph, int box, int line) {
		if (getCurrentGraph() != graph) {
			removeAll();
			final File f = infos.graphs.get(graph - 1);
			if (scroll != null) {
				scrollPreferences.put(infos.graphs.get(getCurrentGraph() - 1),
						scroll.getViewport().getViewPosition());
			}
			this.setCurrentGraph(graph);
			final GraphIO gio = infos.getGraphIO(graph);
			if (gio == null) {
				throw new IllegalStateException(
						"null GraphIO in setDisplay should not happen");
			}
			decorator.clear();
			graphicalZone = new GraphicalZone(gio, new TextField(0, null),
					null, decorator);
			graphicalZone.addMouseListener(listener);
			scroll = new JScrollPane(graphicalZone);
			scroll.getHorizontalScrollBar().setUnitIncrement(20);
			scroll.getVerticalScrollBar().setUnitIncrement(20);
			scroll.setPreferredSize(new Dimension(1188, 840));
			final Point p = scrollPreferences.get(f);
			if (p != null) {
				scroll.getViewport().setViewPosition(p);
			}
			add(scroll, BorderLayout.CENTER);
			add(new JLabel("Double-click to open the graph:"),
					BorderLayout.NORTH);
		}
		decorator.highlightBoxLine(graph, box, line);
		revalidate();
		repaint();
		final GenericGraphBox b = graphicalZone.graphBoxes.get(box);
		Rectangle visibleRect = scroll.getViewport().getViewRect();
		if (visibleRect.width == 0 && visibleRect.height == 0) {
			/*
			 * If the view port has not been given a size, we consider the panel
			 * area as default
			 */
			visibleRect = new Rectangle(0, 0, getWidth(), getHeight());
		}
		/*
		 * If necessary, we adjust the scrolling so that the middle of the box
		 * will be visible
		 */
		int newX = visibleRect.x;
		if (b.X < visibleRect.x + 50) {
			newX = b.X1 - 50;
		} else if ((b.X1 + b.Width) > (visibleRect.x + visibleRect.width)) {
			newX = b.X1 - 50;
		}
		int newY = visibleRect.y;
		if (b.Y < visibleRect.y + 50) {
			newY = b.Y1 - 50;
		} else if ((b.Y1 + b.Height) > (visibleRect.y + visibleRect.height)) {
			newY = b.Y1 - 50;
		}
		scroll.getViewport().setViewPosition(new Point(newX, newY));
	}

	public void setCurrentGraph(int currentGraph) {
		this.currentGraph = currentGraph;
	}

	public int getCurrentGraph() {
		return currentGraph;
	}
}
