/*
 * Unitex
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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import fr.umlv.unitex.MyDropTarget;
import fr.umlv.unitex.diff.GraphDecorator;
import fr.umlv.unitex.diff.GraphDecoratorConfig;
import fr.umlv.unitex.graphrendering.GraphicalZone;
import fr.umlv.unitex.graphrendering.TextField;
import fr.umlv.unitex.io.GraphIO;

public class GraphDiffFrame extends JInternalFrame {
	JScrollPane basePane;
	JScrollPane destPane;
	JPanel main;

	public GraphDiffFrame(File fbase, File fdest, GraphIO base, GraphIO dest,
			GraphDecorator diff) {
		super("Graph Diff", true, true, true, true);
		MyDropTarget.newDropTarget(this);
		main = new JPanel(new BorderLayout());
		main.add(constructTopPanel(fbase, fdest, diff), BorderLayout.NORTH);
		basePane = createPane(base, diff);
		destPane = createPane(dest, diff.clone(false));
		main.add(basePane);
		setContentPane(main);
		setSize(850, 550);
	}

	private JScrollPane createPane(GraphIO gio, GraphDecorator diff) {
		final GraphicalZone graphicalZone = new GraphicalZone(gio,
				new TextField(0, null), null, diff);
		final JScrollPane scroll = new JScrollPane(graphicalZone);
		scroll.getHorizontalScrollBar().setUnitIncrement(20);
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		scroll.setPreferredSize(new Dimension(1188, 840));
		return scroll;
	}

	private Component constructTopPanel(File fbase, File fdest,
			GraphDecorator diff) {
		final boolean propertyChanges = diff.propertyOps.size() != 0;
		final JPanel p = new JPanel(new GridLayout(
				3 + (propertyChanges ? 1 : 0), 1));
		final ButtonGroup bg = new ButtonGroup();
		final JRadioButton base = new JRadioButton(fbase.getAbsolutePath(),
				true);
		final JRadioButton dest = new JRadioButton(fdest.getAbsolutePath(),
				false);
		base.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (base.isSelected()) {
					main.remove(destPane);
					main.add(basePane);
					main.revalidate();
					main.repaint();
				}
			}
		});
		dest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (dest.isSelected()) {
					main.remove(basePane);
					main.add(destPane);
					main.revalidate();
					main.repaint();
				}
			}
		});
		bg.add(base);
		bg.add(dest);
		p.add(base);
		p.add(dest);
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
}
