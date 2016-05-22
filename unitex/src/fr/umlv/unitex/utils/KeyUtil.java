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
package fr.umlv.unitex.utils;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * This file stores the Key Bindings
 *
 * @author Mukarram Tailor
 */
public class KeyUtil {

	/**
	 * Pressing Enter on the given component will act as clicking on the given
	 * button
	 */
	public static void addCRListener(JComponent c, final JButton b) {
		c.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					b.doClick();
				}
			}
		});
	}

	public static void addCRListener(JButton b) {
		addCRListener(b, b);
	}

	/**
	 * Pressing Esc on a focused dialog will act as clicking cancel button
	 * 
	 */
	public static void addEscListener(JComponent c, final JButton b) {
		c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "closeTheDialog");
		c.getActionMap().put("closeTheDialog", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent cancel) {
				b.doClick();
			}
		});
	}

	/**
	 * Pressing Enter on a focused dialog will act as clicking OK button
	 * 
	 */
	public static void addEnterListener(JComponent c, final JButton b) {
		c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "pressOK");
		c.getActionMap().put("pressOK", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent next) {
				b.doClick();
			}
		});
	}

	/**
	 * Pressing Esc on a focused dialog will close it
	 * 
	 */
	public static void addCloseListener(final JComponent c) {
		c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "closeTheDialog");
		c.getActionMap().put("closeTheDialog", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent close) {
				c.getFocusCycleRootAncestor().setVisible(false);
			}
		});
	}
}