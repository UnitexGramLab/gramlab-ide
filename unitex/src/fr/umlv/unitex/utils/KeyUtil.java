/*
 * Unitex
 *
 * Copyright (C) 2001-2019 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.frames.InternalFrameManager;

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
	 * This method implements hotkey  binding for [Esc] for currently focused JComponent with a "Cancel" Button.
	 * Pressing Esc, would mimic as clicking on cancel button.
	 * 
	 * @param b
	 * 		the cancel button(or exit or Back)
	 * @param c
	 * 		the focused Jcomponent
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
	 * This method implements hotkey  binding for [Enter] for currently focused JComponent with a "Ok" Button.
	 * Pressing Enter, would mimic as clicking on Ok button.
	 * 
	 * @param b
	 * 		the Ok button(or Next or Done)
	 * @param c
	 * 		the focused Jcomponent
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
	 * This method implements hotkey  binding for [Esc] for currently focused Dialog box.
	 * Pressing Esc, would close the dialog box.
	 * 
	 * @param c
	 * 		the focused component that can be a part of JDialog 
	 * 
	 */
	public static void addCloseDialogListener(final JComponent c) {
		c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "closeTheDialog");
		c.getActionMap().put("closeTheDialog", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent close) {
				c.getFocusCycleRootAncestor().setVisible(false);
			}
		});
	}
	
	/**
	 * This method implements hotkey  binding for [Esc] for currently focused Frame.
	 * Pressing Esc, would minimize the frame.
	 * 
	 * @param c
	 * 		the focused component that can be a part of JInternalFrame
	 * 
	 */
	public static void addMinimizeFrameListener(final JComponent c) {
		c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "minimizeTheFrame");
		c.getActionMap().put("minimizeTheFrame", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent close) {
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).minimizeCurrentFocusedFrame();
			}
		});
	}
	
	/**
	 * This method implements hotkey  binding for [Esc] for currently focused Frame.
	 * Pressing Esc, would close the frame.
	 * 
	 * @param c
	 * 		the focused component that can be a part of JInternalFrame
	 * 
	 */
	public static void addCloseFrameListener(final JComponent c) {
		c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "closeTheFrame");
		c.getActionMap().put("closeTheFrame", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent close) {
				GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class).closeCurrentFocusedFrame();
			}
		});
	}
}