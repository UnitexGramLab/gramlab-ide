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

import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

/**
 * This class describes a splash screen window.
 * 
 * @author Sébastien Paumier
 * 
 */
public class SplashScreen extends JWindow {
	/**
	 * Creates a new <code>SplashScreen</code>
	 * 
	 * @param icon
	 *            the image to show
	 */
	public SplashScreen(ImageIcon icon) {
		super();
		final JLabel splash = new JLabel(icon);
		getContentPane().add(splash);
		pack();
		final Rectangle screenRect = getGraphicsConfiguration().getBounds();
		setLocation(screenRect.x + screenRect.width / 2 - getSize().width / 2,
				screenRect.y + screenRect.height / 2 - getSize().height / 2);
	}
}
