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

import java.util.ArrayList;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

class MultiInstanceFrameFactory<F extends KeyedInternalFrame<K>, K> {
	final ArrayList<F> frames = new ArrayList<F>();

	F getFrameIfExists(K key) {
		if (key != null) {
			for (final F f : frames) {
				if (key.equals(f.getKey())) {
					return f;
				}
			}
		}
		return null;
	}

	void addFrame(final F f) {
		frames.add(f);
		f.addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				frames.remove(f);
			}
		});
	}

	@SuppressWarnings("unchecked")
	void closeAllFrames() {
		/*
		 * We have to make a copy of the frame list because as the close action
		 * of each frame way remove it from 'frames', we could have problems
		 */
		final ArrayList<F> copy = (ArrayList<F>) frames.clone();
		for (final F f : copy) {
			f.doDefaultCloseAction();
		}
	}

	@SuppressWarnings("unchecked")
	ArrayList<F> getFrames() {
		return (ArrayList<F>) frames.clone();
	}

	int getFrameCount() {
		return frames.size();
	}
}
