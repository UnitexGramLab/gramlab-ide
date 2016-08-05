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

import java.io.File;
import java.util.ArrayList;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

class FileEditionTextFrameFactory {
	final ArrayList<FileEditionTextFrame> frames = new ArrayList<FileEditionTextFrame>();

	FileEditionTextFrame getFileEditionTextFrame(File file) {
		if (file != null) {
			for (final FileEditionTextFrame fr : frames) {
				if (file.equals(fr.getFile())) {
					return fr;
				}
			}
		}
		final FileEditionTextFrame f = new FileEditionTextFrame(file);
		frames.add(f);
		f.addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				frames.remove(f);
			}
		});
		return f;
	}

	@SuppressWarnings("unchecked")
	void closeAllFileEditionTextFrames() {
		/*
		 * We have to make a copy of the frame list because as the close action
		 * of each frame way remove it from 'frames', we could have problems
		 */
		final ArrayList<FileEditionTextFrame> copy = (ArrayList<FileEditionTextFrame>) frames
				.clone();
		for (final FileEditionTextFrame f : copy) {
			f.doDefaultCloseAction();
		}
	}
}
