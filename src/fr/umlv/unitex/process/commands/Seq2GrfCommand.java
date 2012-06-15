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

	public Seq2GrfCommand(File alphabet, File outputfile, int w, int i, int r,
			int d, int b) {
		super("Seq2Grf");
		protectElement("-a" + alphabet.getAbsolutePath());
		protectElement("-o" + outputfile.getAbsolutePath());
		protectElement("-w" + w);
		protectElement("-i" + i);
		protectElement("-r" + r);
		protectElement("-d" + d);
		if (b!=0){
			protectElement("--b" );
		}
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

	public Seq2GrfCommand wildcards(int w) {
		element("-w" + w);
		return this;
	}

	public Seq2GrfCommand wildcard_insert(int i) {
		element("-i" + i);
		return this;
	}

	public Seq2GrfCommand wildcard_replace(int r) {
		element("-r" + r);
		return this;
	}

	public Seq2GrfCommand wildcard_delete(int d) {
		element("-d" + d);
		return this;
	}
	public Seq2GrfCommand applyBeautify(boolean b){
		if (b){
			element("--b");
		}
		return this;
	}

 	public Seq2GrfCommand alphabet(String a) {
		element("-a" + a);
		return this;
	}
 	
 	public Seq2GrfCommand morpho(boolean morpho){
 		if (morpho){
 			element("--morpho");
 		}
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
