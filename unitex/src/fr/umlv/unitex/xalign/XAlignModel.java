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
package fr.umlv.unitex.xalign;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import fr.umlv.unitex.listeners.AlignmentListener;

public interface XAlignModel {
	public ArrayList<Integer> getAlignedSrcSequences(int sentence);

	public ArrayList<Integer> getAlignedDestSequences(int sentence);

	public ArrayList<Integer> getAlignedSequences(int sentence, boolean fromSrc);

	public void load(File f) throws IOException;

	public void align(int sentenceSrc, int sentenceDest, AlignmentEvent e);

	public void unAlign(int sentenceSrc, int sentenceDest);

	public void changeAlignment(int sentenceSrc, int sentenceDest);

	public void dumpAlignments(File f) throws FileNotFoundException,
			IOException;

	public void addAlignmentListener(AlignmentListener l);

	public void removeAlignmentListener(AlignmentListener l);

	public boolean isModified();

	public void reset();

	public void clear();
}
