/*
 * Unitex
 *
 * Copyright (C) 2001-2012 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import fr.umlv.unitex.config.ConfigManager;

/**
 * @author Sébastien Paumier
 */
public class Grf2Fst2Command extends CommandBuilder {
	public Grf2Fst2Command() {
		super("Grf2Fst2");
	}

	public Grf2Fst2Command grf(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public Grf2Fst2Command enableLoopAndRecursionDetection(boolean b) {
		element(b ? "-y" : "-n");
		return this;
	}

	private Grf2Fst2Command charByCharTokenization() {
		element("--char_by_char");
		return this;
	}

	private Grf2Fst2Command alphabetTokenization(File f) {
		if (f == null)
			return this;
		protectElement("--alphabet=" + f.getAbsolutePath());
		return this;
	}

	public Grf2Fst2Command tokenizationMode(String language, File grf) {
		if (ConfigManager.getManager().isCharByCharLanguage(language)) {
			return charByCharTokenization();
		}
		final File alphabet = ConfigManager.getManager().getAlphabetForGrf(
				language, grf);
		return alphabetTokenization(alphabet);
	}

	public Grf2Fst2Command repository() {
		final File f = ConfigManager.getManager().getGraphRepositoryPath(null);
		if (f != null) {
			element("-d");
			protectElement(f.getAbsolutePath());
		}
		return this;
	}

	public Grf2Fst2Command debug() {
		element("--debug");
		return this;
	}

	public Grf2Fst2Command noEmptyGraphWarning() {
		element("-e");
		return this;
	}
}
