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
package org.gramlab.core.umlv.unitex.process.commands;

import java.io.File;

public class Seq2GrfCommand extends CommandBuilder {
	public Seq2GrfCommand() {
		super("Seq2Grf");
	}

	public Seq2GrfCommand output(File f) {
		protectElement("-o" + f.getAbsolutePath());
		return this;
	}

	public Seq2GrfCommand text(File f) {
		protectElement(f.getAbsolutePath());
		return this;
	}

	public Seq2GrfCommand wildcards(int w) {
		element("-w" + w);
		return this;
	}

	public Seq2GrfCommand wildcardInsert(int i) {
		element("-i" + i);
		return this;
	}

	public Seq2GrfCommand wildcardReplace(int r) {
		element("-r" + r);
		return this;
	}

	public Seq2GrfCommand wildcardDelete(int d) {
		element("-d" + d);
		return this;
	}

	public Seq2GrfCommand applyBeautify(boolean b) {
		if (b) {
			element("--b");
		}
		return this;
	}

	public Seq2GrfCommand alphabet(File alphabet) {
		if (alphabet!=null) {
			protectElement("-a" + alphabet.getAbsolutePath());
		}
		return this;
	}

	public Seq2GrfCommand exactCaseMatching(boolean b) {
		if (b) {
			element("--case-sensitive");
		} else {
			element("--case-insensitive");
		}
		return this;
	}

}
