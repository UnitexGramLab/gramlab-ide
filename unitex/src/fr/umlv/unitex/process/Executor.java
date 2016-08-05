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
package fr.umlv.unitex.process;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import fr.umlv.unitex.console.ConsoleEntry;
import fr.umlv.unitex.exceptions.UnitexUncaughtExceptionHandler;
import fr.umlv.unitex.process.commands.CommandBuilder;
import fr.umlv.unitex.process.commands.MultiCommands;

/**
 * This object launches a thread that will process all the given commands.
 * 
 * @author paumier
 * 
 */
public class Executor extends Thread {

	private final ExecParameters parameters;
	private boolean success = true;
	private boolean finished = false;
	ConsoleEntry entry = null;

	public Executor(ExecParameters parameters) {
		this.parameters = parameters;
		if (parameters.getCommands() == null
				|| parameters.getCommands().numberOfCommands() == 0) {
			throw new IllegalArgumentException(
					"Invalid null or empty MultiCommands");
		}
		setUncaughtExceptionHandler(UnitexUncaughtExceptionHandler.getHandler());
	}

	@Override
	public void run() {
		final MultiCommands commands = parameters.getCommands();
		CommandBuilder command;
		for (int i = 0; success && i < commands.numberOfCommands(); i++) {
			if ((command = commands.getCommand(i)) != null) {
				entry = null;
				if (parameters.isTraceIntoConsole()) {
					entry = command.logIntoConsole();
				}
				final ToDoBeforeSingleCommand toDoBefore = command
						.getWhatToDoBefore();
				if (toDoBefore != null)
					toDoBefore.toDo(entry);

				final boolean commandSuccessful = command.executeCommand(
						parameters, entry);
				if (!commandSuccessful) {
					success = false;
				}
				if (entry != null
						&& command.getType() == CommandBuilder.PROGRAM) {
					while (!entry.isNormalStreamEnded()
							|| !entry.isErrorStreamEnded()) {
						/*
						 * We wait for the end of the stdout and stderr reading
						 * threads
						 */
						// System.err.println("stdout ended: "+entry.isNormalStreamEnded());
						// System.err.println("stderr ended: "+entry.isErrorStreamEnded());
					}
				}
				final ToDoAfterSingleCommand toDoAfter = command
						.getWhatToDoOnceCompleted();
				if (toDoAfter != null)
					toDoAfter.toDo(commandSuccessful, entry);
				entry = null;
			}
		}
		final ToDo DO = parameters.getDO();
		if (DO != null) {
			DO.toDo(success);
		}
		finished = true;
	}

	public boolean getSuccess() {
		return success;
	}

	@Override
	public void interrupt() {
		final Process p = parameters.getProcess();
		if (p != null) {
			p.destroy();
			if (entry != null) {
				try {
					if (EventQueue.isDispatchThread()) {
						entry.addErrorMessage("*** COMMAND CANCELED BY USER ***");
					} else
						EventQueue.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								entry.addErrorMessage("*** COMMAND CANCELED BY USER ***");
							}
						});
				} catch (final InterruptedException e) {
					/* */
				} catch (final InvocationTargetException e) {
					/* */
				}
			}
		}
		success = false;
		finished = true;
		super.interrupt();
	}

	public boolean hasFinished() {
		return finished;
	}

}
