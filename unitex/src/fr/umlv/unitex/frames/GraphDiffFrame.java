/*
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
package fr.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.umlv.unitex.DropTargetManager;
import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.diff.GraphDecorator;
import fr.umlv.unitex.diff.GraphDecoratorConfig;
import fr.umlv.unitex.graphrendering.GraphicalZone;
import fr.umlv.unitex.graphrendering.TextField;
import fr.umlv.unitex.io.GraphIO;

public class GraphDiffFrame extends TabbableInternalFrame {
	public GraphDiffFrame(final GraphIO base, final GraphIO dest,
			GraphDecorator diff) {
		super("Graph Diff", true, true, true, true);
		DropTargetManager.getDropTarget().newDropTarget(this);
		final JPanel main = new JPanel(new BorderLayout());
		main.add(constructTopPanel(diff), BorderLayout.NORTH);
		final GraphicalZone basePane = new GraphicalZone(base, new TextField(0,
				null), null, diff);
		final GraphicalZone destPane = new GraphicalZone(dest, new TextField(0,
				null), null, diff.clone(false));
		basePane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					GlobalProjectManager.search(base.getGrf())
							.getFrameManagerAs(InternalFrameManager.class)
							.newGraphFrame(base.getGrf());
				}
			}
		});
		destPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					GlobalProjectManager.search(dest.getGrf())
							.getFrameManagerAs(InternalFrameManager.class)
							.newGraphFrame(dest.getGrf());
				}
			}
		});
		final JPanel p = buildSynchronizedScrollPanes(basePane, destPane);
		JPanel foo=new JPanel(new BorderLayout());
		JPanel names=new JPanel(new GridLayout(1,2));
		JLabel l1=new JLabel(base.getGrf().getAbsolutePath());
		l1.setToolTipText(base.getGrf().getAbsolutePath());
		JLabel l2=new JLabel(dest.getGrf().getAbsolutePath());
		l2.setToolTipText(dest.getGrf().getAbsolutePath());
		names.add(l1);
		names.add(l2);
		foo.add(names,BorderLayout.NORTH);
		foo.add(p,BorderLayout.CENTER);
		main.add(foo,BorderLayout.CENTER);
		setContentPane(main);
		setSize(850, 550);
	}

	boolean p1Moving = false;
	boolean p2Moving = false;

	private JPanel buildSynchronizedScrollPanes(JComponent c1, JComponent c2) {
		final JPanel p = new JPanel(new GridLayout(1, 2));
		final JScrollPane p1 = new JScrollPane(c1);
		final JScrollPane p2 = new JScrollPane(c2);
		p1.getHorizontalScrollBar().getModel()
				.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						if (p1Moving)
							return;
						p1Moving = true;
						try {
							final double ratio = p1.getHorizontalScrollBar()
									.getValue()
									/ (double) p1.getHorizontalScrollBar()
											.getMaximum();
							p2.getHorizontalScrollBar().setValue(
									(int) (p2.getHorizontalScrollBar()
											.getMaximum() * ratio));
						} finally {
							p1Moving = false;
						}
					}
				});
		p2.getHorizontalScrollBar().getModel()
				.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						if (p2Moving)
							return;
						p2Moving = true;
						try {
							final double ratio = p2.getHorizontalScrollBar()
									.getValue()
									/ (double) p2.getHorizontalScrollBar()
											.getMaximum();
							p1.getHorizontalScrollBar().setValue(
									(int) (p1.getHorizontalScrollBar()
											.getMaximum() * ratio));
						} finally {
							p2Moving = false;
						}
					}
				});
		p1.getVerticalScrollBar().getModel()
				.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						if (p1Moving)
							return;
						p1Moving = true;
						try {
							final double ratio = p1.getVerticalScrollBar()
									.getValue()
									/ (double) p1.getVerticalScrollBar()
											.getMaximum();
							p2.getVerticalScrollBar().setValue(
									(int) (p2.getVerticalScrollBar()
											.getMaximum() * ratio));
						} finally {
							p1Moving = false;
						}
					}
				});
		p2.getVerticalScrollBar().getModel()
				.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						if (p2Moving)
							return;
						p2Moving = true;
						try {
							final double ratio = p2.getVerticalScrollBar()
									.getValue()
									/ (double) p2.getVerticalScrollBar()
											.getMaximum();
							p1.getVerticalScrollBar().setValue(
									(int) (p1.getVerticalScrollBar()
											.getMaximum() * ratio));
						} finally {
							p2Moving = false;
						}
					}
				});
		p.add(p1);
		p.add(p2);
		return p;
	}

	private Component constructTopPanel(GraphDecorator diff) {
		final boolean propertyChanges = diff.propertyOps.size() != 0;
		final JPanel p = new JPanel(new GridLayout(
				3 + (propertyChanges ? 1 : 0), 1));
		final JPanel p2 = new JPanel(null);
		p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
		p2.add(new JLabel(" "));
		p2.add(createOpaqueLabel(GraphDecoratorConfig.ADDED));
		p2.add(new JLabel(" added   "));
		p2.add(createOpaqueLabel(GraphDecoratorConfig.REMOVED));
		p2.add(new JLabel(" removed   "));
		p2.add(createOpaqueLabel(GraphDecoratorConfig.MOVED));
		p2.add(new JLabel(" moved   "));
		p2.add(createOpaqueLabel(GraphDecoratorConfig.CONTENT_CHANGED));
		p2.add(new JLabel(" content changed"));
		p.add(p2);
		if (propertyChanges) {
			String s = " Changed properties:";
			for (final String tmp : diff.propertyOps) {
				s = s + " " + tmp;
			}
			p.add(new JLabel(s));
		}
		return p;
	}

	private JLabel createOpaqueLabel(Color c) {
		final JLabel l = new JLabel("   ");
		l.setOpaque(true);
		l.setBackground(c);
		return l;
	}

	@Override
	public String getTabName() {
		return "Graph diff";
	}
}
