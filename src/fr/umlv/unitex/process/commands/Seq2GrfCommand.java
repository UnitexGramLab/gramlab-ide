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

public class Seq2GrfCommand extends CommandBuilder {
	public Seq2GrfCommand() {
		super("Seq2Grf");
	}

	public Seq2GrfCommand(File alphabet, File outputfile) {
		super("Seq2Grf");
		protectElement("-a" + alphabet.getAbsolutePath());
		protectElement("-o" + outputfile.getAbsolutePath());
	}

	public Seq2GrfCommand(File alphabet, File outputfile, int j, int i, int r,
			int d) {
		super("Seq2Grf");
		protectElement("-a" + alphabet.getAbsolutePath());
		protectElement("-o" + outputfile.getAbsolutePath());
		protectElement("-j" + j);
		protectElement("-i" + i);
		protectElement("-r" + r);
		protectElement("-d" + d);
	}

	public Seq2GrfCommand automaton(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public Seq2GrfCommand output(String o) {
		element("-o" + o);
		return this;
	}

	public Seq2GrfCommand help(String h) {
		protectElement("-h" + h);
		return this;
	}

	public Seq2GrfCommand text(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public Seq2GrfCommand jokers(int j) {
		element("-j" + j);
		return this;
	}

	public Seq2GrfCommand joker_insert(int i) {
		element("-i" + i);
		return this;
	}

	public Seq2GrfCommand joker_replace(int r) {
		element("-r" + r);
		return this;
	}

	public Seq2GrfCommand joker_delete(int d) {
		element("-d" + d);
		return this;
	}

	public Seq2GrfCommand alphabet(String a) {
		element("-a" + a);
		return this;
	}

	public Seq2GrfCommand alphabet(File alphabet) {
		protectElement("-a" + alphabet.getAbsolutePath());
		return this;
	}

	public Seq2GrfCommand clean(boolean clean) {
		if (clean) {
			element("--clean");
		}
		return this;
	}

	public Seq2GrfCommand fst2(File s) {
		protectElement("-n" + s.getAbsolutePath());
		return this;
	}
}
