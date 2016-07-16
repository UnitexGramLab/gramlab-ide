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

import javax.swing.JInternalFrame;

public class FrameFactory {
	private JInternalFrame frame;
	private final Class<?> clazz;

	public FrameFactory(Class<?> clazz) {
		this.clazz = clazz;
	}

	public JInternalFrame newFrame() {
		return newFrame(true);
	}

	JInternalFrame newFrame(boolean closeBeforeGet) {
		if (frame == null) {
			try {
				frame = (JInternalFrame) clazz.newInstance();
			} catch (final InstantiationException e) {
				return null;
			} catch (final IllegalAccessException e) {
				return null;
			}
		}
		if (closeBeforeGet)
			frame.doDefaultCloseAction();
		return frame;
	}

	void closeFrame() {
		if (frame == null) {
			return;
		}
		frame.doDefaultCloseAction();
	}
}
