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

import java.util.ArrayList;

/**
 * This class provides facilities for build process command lines.
 * 
 * @author Sébastien Paumier
 * 
 */
public class MultiCommands {
	private final ArrayList<CommandBuilder> list;

	public MultiCommands() {
		list = new ArrayList<CommandBuilder>();
	}

	public MultiCommands(CommandBuilder builder) {
		this();
		addCommand(builder);
	}

	public void addCommand(CommandBuilder builder) {
		if (builder == null)
			return;
		list.add(builder);
	}

	public void addCommand(MultiCommands cmds) {
		if (cmds == null)
			return;
		for (int i = 0; i < cmds.numberOfCommands(); i++) {
			list.add(cmds.getCommand(i));
		}
	}

	public CommandBuilder getCommand(int n) {
		return list.get(n);
	}

	public int numberOfCommands() {
		return list.size();
	}
}
