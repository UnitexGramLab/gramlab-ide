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

import org.gramlab.core.umlv.unitex.config.ConfigManager;

/**
 * @author Sébastien Paumier
 */
public class MultiFlexCommand extends CommandBuilder {
	public MultiFlexCommand() {
		super("MultiFlex");
	}

	public MultiFlexCommand delas(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public MultiFlexCommand result(File s) {
		protectElement("-o" + s.getAbsolutePath());
		return this;
	}

	public MultiFlexCommand alphabet(File s) {
		if (s != null)
			protectElement("-a" + s.getAbsolutePath());
		return this;
	}

	public MultiFlexCommand dir(File s) {
		protectElement("-d" + s.getAbsolutePath());
		return this;
	}

	public MultiFlexCommand onlySimpleWords() {
		protectElement("-s");
		return this;
	}

	public MultiFlexCommand onlyCompoundWords() {
		protectElement("-c");
		return this;
	}

	public MultiFlexCommand korean() {
		element("-K");
		return this;
	}

	public MultiFlexCommand repository() {
		final File f = ConfigManager.getManager().getGraphRepositoryPath(null,
				null);
		if (f != null) {
			element("-p");
			protectElement(f.getAbsolutePath());
		}
		return this;
	}
}
