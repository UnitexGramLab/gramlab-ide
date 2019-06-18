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
package fr.umlv.unitex.frames;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

class XAlignFrameFactory {
	private XAlignFrame frame;

	XAlignFrame newXAlignFrame(File src, File dst, File alignment) {
		if (frame != null) {
			frame.doDefaultCloseAction();
		}
		try {
			frame = new XAlignFrame(src, dst, alignment);
		} catch (final IOException e) {
			JOptionPane.showMessageDialog(null,
					"I/O error while loading alignment files", "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return frame;
	}

	public void closeXAlignFrame() {
		if (frame != null) {
			frame.dispose();
			frame = null;
		}
	}
}
