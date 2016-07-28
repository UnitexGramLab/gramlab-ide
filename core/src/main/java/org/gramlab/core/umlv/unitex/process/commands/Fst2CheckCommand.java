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
public class Fst2CheckCommand extends CommandBuilder {
	public Fst2CheckCommand() {
		super("Fst2Check");
	}

	public Fst2CheckCommand loopCheck(boolean doLoopCheck) {
		if (doLoopCheck) {
			element("-y");
		} else {
			element("-n");
		}
		return this;
	}

	public Fst2CheckCommand fst2(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public Fst2CheckCommand tfstCheck() {
		element("-t");
		return this;
	}

	public Fst2CheckCommand noEmptyGraphWarning() {
		element("-e");
		return this;
	}

	public Fst2CheckCommand displayStatistics() {
		element("-s");
		return this;
	}

	public Fst2CheckCommand output(File out) {
		protectElement("-o" + out.getAbsolutePath());
		return this;
	}

	public Fst2CheckCommand appendMode() {
		element("-a");
		return this;
	}
}
