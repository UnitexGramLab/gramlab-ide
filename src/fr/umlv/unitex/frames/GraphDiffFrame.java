/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.GraphPresentationInfo;
import fr.umlv.unitex.MyCursors;
import fr.umlv.unitex.MyDropTarget;
import fr.umlv.unitex.Preferences;
import fr.umlv.unitex.Util;
import fr.umlv.unitex.diff.GraphDecoratorConfig;
import fr.umlv.unitex.diff.GraphDecorator;
import fr.umlv.unitex.graphrendering.GenericGraphBox;
import fr.umlv.unitex.graphrendering.GraphBox;
import fr.umlv.unitex.graphrendering.GraphicalZone;
import fr.umlv.unitex.graphrendering.MultipleSelection;
import fr.umlv.unitex.graphrendering.TextField;
import fr.umlv.unitex.graphtools.Dependancies;
import fr.umlv.unitex.io.GraphIO;
import fr.umlv.unitex.io.SVG;
import fr.umlv.unitex.listeners.GraphListener;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.GrfDiffCommand;

public class GraphDiffFrame extends JInternalFrame {

	JScrollPane basePane;
	JScrollPane destPane;
	JPanel main;
	
    public GraphDiffFrame(File fbase,File fdest,GraphIO base, GraphIO dest, GraphDecorator diff) {
    	super("Graph Diff",true,true,true,true);
        MyDropTarget.newDropTarget(this);
        main=new JPanel(new BorderLayout());
        main.add(constructTopPanel(fbase,fdest,diff),BorderLayout.NORTH);
    	basePane=createPane(base,diff);
    	destPane=createPane(dest,diff.clone(false));
    	main.add(basePane);
    	setContentPane(main);
    	setSize(850,550);
	}

	private JScrollPane createPane(GraphIO gio,GraphDecorator diff) {
		GraphicalZone graphicalZone=new GraphicalZone(gio,new TextField(0,null),null,diff);
		JScrollPane scroll = new JScrollPane(graphicalZone);
        scroll.getHorizontalScrollBar().setUnitIncrement(20);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setPreferredSize(new Dimension(1188, 840));
        return scroll;
	}

	private Component constructTopPanel(File fbase,File fdest,GraphDecorator diff) {
		boolean propertyChanges=diff.propertyOps.size()!=0;
		JPanel p=new JPanel(new GridLayout(3+(propertyChanges?1:0),1));
		ButtonGroup bg=new ButtonGroup();
		final JRadioButton base=new JRadioButton(fbase.getAbsolutePath(),true);
		final JRadioButton dest=new JRadioButton(fdest.getAbsolutePath(),false);
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
		JPanel p2=new JPanel(null);
		p2.setLayout(new BoxLayout(p2,BoxLayout.X_AXIS));
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
			String s=" Changed properties:";
			for (String tmp:diff.propertyOps) {
				s=s+" "+tmp;
			}
			p.add(new JLabel(s));
		}
		return p;
	}

	private JLabel createOpaqueLabel(Color c) {
		JLabel l=new JLabel("   ");
		l.setOpaque(true);
		l.setBackground(c);
		return l;
	}

}
