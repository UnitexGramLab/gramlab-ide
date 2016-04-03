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
package fr.umlv.unitex.frames;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import fr.umlv.unitex.GraphCollection;

/**
 * This class is used to display a frame that just contains a
 * <code>JTextField</code> to display messages.
 * 
 * @author Sébastien Paumier
 */
public class MessageWhileWorkingFrame extends JInternalFrame {
	private final JLabel label = new JLabel();

	MessageWhileWorkingFrame() {
		super("", true, true);
		final JPanel top = new JPanel(new BorderLayout());
		top.setBorder(new EmptyBorder(2, 2, 2, 2));
		top.add(label);
		setContentPane(top);
		setBounds(100, 100, 450, 80);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				GraphCollection.stop();
			}
		});
	}

	/**
	 * @return the message label
	 */
	public JLabel getLabel() {
		return label;
	}
}
