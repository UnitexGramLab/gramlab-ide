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

import org.gramlab.core.umlv.unitex.exceptions.InvalidPolyLexArgumentException;

/**
 * @author Sébastien Paumier
 * 
 */
public class PolyLexCommand extends CommandBuilder {
	public PolyLexCommand() {
		super("PolyLex");
	}

	public PolyLexCommand language(String s)
			throws InvalidPolyLexArgumentException {
		if (s.equals("GERMAN") || s.equals("German")) {
			element("-G");
			return this;
		}
		if (s.startsWith("NORWEGIAN") || s.startsWith("Norwegian")) {
			element("-N");
			return this;
		}
		if (s.equals("RUSSIAN") || s.equals("Russian")) {
			element("-R");
			return this;
		}
		if (s.equals("DUTCH") || s.equals("Dutch")) {
			element("-D");
			return this;
		}
		throw new InvalidPolyLexArgumentException();
	}

	public PolyLexCommand alphabet(File alphabet) {
		if (alphabet != null)
			protectElement("-a" + alphabet.getAbsolutePath());
		return this;
	}

	public PolyLexCommand bin(File s) {
		protectElement("-d" + s.getAbsolutePath());
		return this;
	}

	public PolyLexCommand wordList(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public PolyLexCommand output(File s) {
		protectElement("-o" + s.getAbsolutePath());
		return this;
	}

	public PolyLexCommand info(File s) {
		protectElement("-i" + s.getAbsolutePath());
		return this;
	}
}
