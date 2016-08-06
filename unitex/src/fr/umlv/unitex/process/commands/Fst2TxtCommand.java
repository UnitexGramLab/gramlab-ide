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
package fr.umlv.unitex.process.commands;

import java.io.File;

/**
 * @author Sébastien Paumier
 * 
 */
public class Fst2TxtCommand extends CommandBuilder {
	public Fst2TxtCommand() {
		super("Fst2Txt");
	}

	public Fst2TxtCommand text(File s) {
		protectElement("-t" + s.getAbsolutePath());
		ultraSimplifiedList.add(s.getName());
		return this;
	}

	public Fst2TxtCommand fst2(File s) {
		protectElement(s.getAbsolutePath());
		ultraSimplifiedList.add(s.getName());
		return this;
	}

	public Fst2TxtCommand alphabet(File alphabet) {
		if (alphabet != null)
			protectElement("-a" + alphabet.getAbsolutePath());
		return this;
	}

	public Fst2TxtCommand mode(boolean merge) {
		element(merge ? "-M" : "-R");
		return this;
	}

	public Fst2TxtCommand charByChar(boolean b) {
		if (b) {
			element("--char_by_char");
		}
		return this;
	}

	public Fst2TxtCommand morphologicalUseOfSpace(boolean b) {
		if (b) {
			element("--start_on_space");
		}
		return this;
	}

	public Fst2TxtCommand inputOffsets(File s) {
		protectElement("--input_offsets=" + s.getAbsolutePath());
		return this;
	}

	public Fst2TxtCommand outputOffsets(File s) {
		protectElement("--output_offsets=" + s.getAbsolutePath());
		return this;
	}

	File srcGrfPath = null;

	public File getSrcGrfPath() {
		return srcGrfPath;
	}

	public void setSrcGrfPath(File dir) {
		srcGrfPath = dir;
	}

}
