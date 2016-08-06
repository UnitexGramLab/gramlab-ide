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

import org.gramlab.core.umlv.unitex.console.ConsoleEntry;
import org.gramlab.core.umlv.unitex.process.ExecParameters;
import org.gramlab.core.umlv.unitex.process.ToDoAfterSingleCommand;
import org.gramlab.core.umlv.unitex.process.ToDoBeforeSingleCommand;

public interface AbstractCommand {

	/**
	 * Logs the command in Unitex's console
	 */
	public ConsoleEntry logIntoConsole();

	/**
	 * Executes the command and returns true iff the command successfully
	 * executed
	 */
	public boolean executeCommand(final ExecParameters p,
			final ConsoleEntry entry);

	public void setWhatToDoBefore(ToDoBeforeSingleCommand r);

	public ToDoBeforeSingleCommand getWhatToDoBefore();

	public void setWhatToDoOnceCompleted(ToDoAfterSingleCommand r);

	public ToDoAfterSingleCommand getWhatToDoOnceCompleted();

}
