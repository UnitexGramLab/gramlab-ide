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

/**
 * @author Sébastien Paumier
 * 
 */
public class Tfst2GrfCommand extends CommandBuilder {
	public Tfst2GrfCommand() {
		super("Tfst2Grf");
	}

	public Tfst2GrfCommand automaton(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public Tfst2GrfCommand sentence(int n) {
		element("-s" + n);
		return this;
	}

	public Tfst2GrfCommand sentence(String n) {
		Integer.parseInt(n);
		element("-s" + n);
		return this;
	}

	public Tfst2GrfCommand output(String s) {
		protectElement("-o" + s);
		return this;
	}

	public Tfst2GrfCommand font(String s) {
		protectElement("-f" + s);
		return this;
	}

	public Tfst2GrfCommand fontSize(int n) {
		protectElement("-z" + n);
		return this;
	}
}
