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

import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.files.FileUtil;

import java.io.File;
import java.util.ArrayList;

class ConcordanceFrameFactory {
	private ArrayList<ConcordanceFrame> frames = new ArrayList<ConcordanceFrame>();
	private ConcordanceFrame concordFrame;

	ConcordanceFrame newConcordanceFrame(File f, int widthInChars) {
		String filePath = FileUtil.getFilePathWithoutFileName(f);
		filePath = filePath.substring(0, filePath.length()-1);
		if (filePath.equals(Config.getCurrentSntDir().getAbsolutePath()) && f.getName().equals("concord.html")) {
			frames.remove(concordFrame);
			concordFrame = new ConcordanceFrame(f, widthInChars);
			frames.add(concordFrame);
			return concordFrame;
		}
		ConcordanceFrame frame = new ConcordanceFrame(f, widthInChars);
		frames.add(frame);
		return frame;
	}

	void closeConcordanceFrame() {
		if(concordFrame == null) {
			return;
		}
		frames.remove(concordFrame);
		concordFrame.doDefaultCloseAction();
	}

	void closeConcordanceFrame(ConcordanceFrame f) {
		if (f == null) {
			return;
		}
		frames.remove(f);
		f.doDefaultCloseAction();
	}

	ArrayList<ConcordanceFrame> getFrames() {
		return frames;
	}
}
