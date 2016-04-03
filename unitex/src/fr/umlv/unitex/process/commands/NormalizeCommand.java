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

import fr.umlv.unitex.config.Config;

/**
 * @author Sébastien Paumier
 * 
 */
public class NormalizeCommand extends CommandBuilder {
	public NormalizeCommand() {
		super("Normalize");
	}

	public NormalizeCommand textWithDefaultNormalization(File s) {
		protectElement(s.getAbsolutePath());
		final File norm = new File(Config.getUserCurrentLanguageDir(),
				"Norm.txt");
		if (norm.exists()) {
			protectElement("-r" + norm.getAbsolutePath());
		}
		ultraSimplifiedList.add(s.getName());
		return this;
	}

	public NormalizeCommand normFile(File norm) {
		if (norm != null && norm.exists()) {
			protectElement("-r" + norm.getAbsolutePath());
		}
		return this;
	}

	public NormalizeCommand noSeparatorNormalization() {
		element("--no_separator_normalization");
		return this;
	}

	public NormalizeCommand separatorNormalization(boolean b) {
		if (!b) {
			return noSeparatorNormalization();
		}
		return this;
	}

	public NormalizeCommand text(File s) {
		protectElement(s.getAbsolutePath());
		ultraSimplifiedList.add(s.getName());
		return this;
	}

	public NormalizeCommand inputOffsets(File s) {
		protectElement("--input_offsets=" + s.getAbsolutePath());
		return this;
	}

	public NormalizeCommand outputOffsets(File s) {
		protectElement("--output_offsets=" + s.getAbsolutePath());
		return this;
	}
}
