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

/**
 * @author Sébastien Paumier
 * 
 */
public class XAlignCommand extends CommandBuilder {
	public XAlignCommand() {
		super(false);
		element("java");
		element("-jar");
		protectElement(new File(Config.getApplicationDir(), "XAlign.jar")
				.getAbsolutePath());
	}

	public XAlignCommand source(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public XAlignCommand target(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	/* We use the same properties for source and target texts */
	public XAlignCommand properties(File s) {
		protectElement(s.getAbsolutePath());
		protectElement(s.getAbsolutePath());
		return this;
	}

	public XAlignCommand alignment(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}
}
