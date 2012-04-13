/*
 *
 * Copyright (C) 2001-2012 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.umlv.unitex.MyDropTarget;
import fr.umlv.unitex.diff.GraphDecorator;
import fr.umlv.unitex.diff.GraphDecoratorConfig;
import fr.umlv.unitex.graphrendering.GraphicalZone;
import fr.umlv.unitex.graphrendering.TextField;
import fr.umlv.unitex.io.GraphIO;

public class GraphDiffFrame extends TabbableInternalFrame {
	public GraphDiffFrame(GraphIO base, GraphIO dest,
			GraphDecorator diff) {
		super("Graph Diff", true, true, true, true);
		MyDropTarget.newDropTarget(this);
		JPanel main = new JPanel(new BorderLayout());
		main.add(constructTopPanel(diff), BorderLayout.NORTH);
		GraphicalZone basePane=new GraphicalZone(base,new TextField(0, null), null, diff);
		GraphicalZone destPane=new GraphicalZone(dest,new TextField(0, null), null, diff.clone(false));
		JPanel p=buildSynchronizedScrollPanes(basePane,destPane);
		main.add(p);
		setContentPane(main);
		setSize(850, 550);
	}

	private static JPanel buildSynchronizedScrollPanes(JComponent c1,JComponent c2) {
		JPanel p=new JPanel(new GridLayout(1,2));
		final JScrollPane p1=new JScrollPane(c1);
		final JScrollPane p2=new JScrollPane(c2);
		p1.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (!e.getValueIsAdjusting()) return;
				double ratio=p1.getHorizontalScrollBar().getValue()/(double)p1.getHorizontalScrollBar().getMaximum();
				p2.getHorizontalScrollBar().setValue((int)(p2.getHorizontalScrollBar().getMaximum()*ratio));
			}
		});
		p2.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (!e.getValueIsAdjusting()) return;
				double ratio=p2.getHorizontalScrollBar().getValue()/(double)p2.getHorizontalScrollBar().getMaximum();
				p1.getHorizontalScrollBar().setValue((int)(p1.getHorizontalScrollBar().getMaximum()*ratio));
			}
		});
		p1.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (!e.getValueIsAdjusting()) return;
				double ratio=p1.getVerticalScrollBar().getValue()/(double)p1.getVerticalScrollBar().getMaximum();
				p2.getVerticalScrollBar().setValue((int)(p2.getVerticalScrollBar().getMaximum()*ratio));
			}
		});
		p2.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (!e.getValueIsAdjusting()) return;
				double ratio=p2.getVerticalScrollBar().getValue()/(double)p2.getVerticalScrollBar().getMaximum();
				p1.getVerticalScrollBar().setValue((int)(p1.getVerticalScrollBar().getMaximum()*ratio));
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
