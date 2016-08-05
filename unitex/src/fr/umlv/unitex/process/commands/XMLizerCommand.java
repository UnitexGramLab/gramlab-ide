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
public class XMLizerCommand extends CommandBuilder {
	public XMLizerCommand() {
		super("XMLizer");
	}

	public XMLizerCommand output(File s) {
		protectElement("-o" + s.getAbsolutePath());
		return this;
	}

	public XMLizerCommand input(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public XMLizerCommand alphabet(File s) {
		protectElement("-a" + s.getAbsolutePath());
		return this;
	}

	public XMLizerCommand sentence(File s) {
		protectElement("-s" + s.getAbsolutePath());
		return this;
	}
}
