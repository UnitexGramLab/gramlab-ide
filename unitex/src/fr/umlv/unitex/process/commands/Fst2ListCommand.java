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
public class Fst2ListCommand extends CommandBuilder {
	public Fst2ListCommand() {
		super("Fst2List");
	}

	public Fst2ListCommand fst2(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public Fst2ListCommand limit(int n) {
		element("-l");
		element("" + n);
		return this;
	}

	public Fst2ListCommand noLimit() {
		return this;
	}

	public Fst2ListCommand mode(String s) { // multi or single
		element("-t");
		element(s);
		return this;
	}

	public Fst2ListCommand ignoreListFile(File f) {
		element("-I");
		protectElement(f.getAbsolutePath());
		return this;
	}

	public Fst2ListCommand ignoreOutputs() {
		element("-a");
		element("s");
		return this;
	}

	public Fst2ListCommand separateOutputs(boolean separate) {
		element("-t");
		element("s");
		element("-f");
		element(separate ? "s" : "a");
		element("-s0");
		protectElement("/");
		return this;
	}

	public Fst2ListCommand output(File file) {
		element("-o");
		protectElement(file.getAbsolutePath());
		return this;
	}

	public Fst2ListCommand listsOfSubgraph(File file) {
		element("-p");
		element("s");
		protectElement(file.getAbsolutePath());
		return this;
	}

	public Fst2ListCommand listOfPaths(File ifile, File ofile) {
		element("-s");
		element("\"" + ", " + "\"");
		element("-o");
		protectElement(ofile.getAbsolutePath());
		protectElement(ifile.getAbsolutePath());
		return this;
	}

	/**
	 * Since Fst2List is a piece of $%*! it does not parse properly its
	 * arguments with getopt. So, you cannot provide options after the name of
	 * the .fst2, and as the encoding option is added after everything else, it
	 * is a problem. However, as Unitex/Gramlab can read the output list.txt
	 * file no matter its encoding, it is not a big problem.
	 */
	@Override
	public String getOutputEncoding() {
		return null;
	}
}
