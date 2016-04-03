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
import fr.umlv.unitex.exceptions.InvalidConcordanceOrderException;

/**
 * @author Sébastien Paumier
 * 
 */
public class ConcordCommand extends CommandBuilder {
	public ConcordCommand() {
		super("Concord");
	}

	public ConcordCommand indFile(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public ConcordCommand font(String s) {
		protectElement("-f" + s);
		return this;
	}

	public ConcordCommand fontSize(int size) {
		element("-s" + size);
		return this;
	}

	public ConcordCommand left(int size, boolean s) {
		element("-l" + size + (s ? "s" : ""));
		return this;
	}

	public ConcordCommand right(int size, boolean s) {
		element("-r" + size + (s ? "s" : ""));
		return this;
	}

	public ConcordCommand order(int order)
			throws InvalidConcordanceOrderException {
		String s;
		switch (order) {
		case 0:
			s = "TO";
			break;
		case 1:
			s = "LC";
			break;
		case 2:
			s = "LR";
			break;
		case 3:
			s = "CL";
			break;
		case 4:
			s = "CR";
			break;
		case 5:
			s = "RL";
			break;
		case 6:
			s = "RC";
			break;
		default:
			throw new InvalidConcordanceOrderException();
		}
		element("--" + s);
		return this;
	}

	public ConcordCommand html() {
		element("--html");
		return this;
	}

	public ConcordCommand lemmatize() {
		element("--lemmatize");
		return this;
	}

	public ConcordCommand exportAsCsv() {
		element("--export_csv");
		return this;
	}

	public ConcordCommand text(File output) {
		protectElement("--text"
				+ ((output != null) ? "=" + output.getAbsolutePath() : ""));

		return this;
	}

	public ConcordCommand xalign() {
		element("--xalign");
		return this;
	}

	public ConcordCommand axis() {
		element("--axis");
		return this;
	}

	public ConcordCommand index() {
		element("--index");
		return this;
	}

	public ConcordCommand xml() {
		element("--xml-with-header");
		return this;
	}

	public ConcordCommand outputModifiedTxtFile(File s) {
		protectElement("-m" + s.getAbsolutePath());
		return this;
	}

	public ConcordCommand sortAlphabet() {
		protectElement("-a"
				+ new File(Config.getUserCurrentLanguageDir(),
						"Alphabet_sort.txt").getAbsolutePath());
		return this;
	}

	public ConcordCommand thai(boolean thai) {
		if (thai)
			element("--thai");
		return this;
	}

	public ConcordCommand onlyAmbiguous() {
		element("--only_ambiguous");
		return this;
	}

	public ConcordCommand onlyMatches(boolean b) {
		if (b)
			element("--only_matches");
		return this;
	}

	public ConcordCommand PRLG(File prlgIndex, File offsets) {
		protectElement("--PRLG=" + prlgIndex.getAbsolutePath() + ","
				+ offsets.getAbsolutePath());
		return this;
	}
}
