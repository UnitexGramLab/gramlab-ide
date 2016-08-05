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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.console.Console;
import org.gramlab.core.umlv.unitex.console.ConsoleEntry;
import org.gramlab.core.umlv.unitex.console.Couple;
import org.gramlab.core.umlv.unitex.process.EatStreamThread;
import org.gramlab.core.umlv.unitex.process.ExecParameters;
import org.gramlab.core.umlv.unitex.process.Log;
import org.gramlab.core.umlv.unitex.process.ProcessInfoThread;
import org.gramlab.core.umlv.unitex.process.ToDoAfterSingleCommand;
import org.gramlab.core.umlv.unitex.process.ToDoBeforeSingleCommand;

/**
 * This class provides facilities for build process command lines.
 * 
 * @author Sébastien Paumier
 */
public abstract class CommandBuilder implements AbstractCommand {
	public static final int PROGRAM = 0;
	public static final int MESSAGE = 1;
	public static final int ERROR_MESSAGE = 2;
	public static final int METHOD = 3;
	protected final ArrayList<String> list;
	
	/** 
	 * Used for a very compact display in Gramlab's console
	 */
	protected final ArrayList<String> ultraSimplifiedList;
	
	int type = PROGRAM;
	private boolean unitexProgram = true;
	private int programNamePosition;

	CommandBuilder(String programName) {
		list = new ArrayList<String>();
		ultraSimplifiedList = new ArrayList<String>();
		protectElement(ConfigManager.getManager().getUnitexToolLogger()
				.getAbsolutePath());
		if (ConfigManager.getManager().mustLog(null)) {
			element("{");
			element("CreateLog");
			element("-d");
			protectElement(ConfigManager.getManager().getLogDirectory(null)
					.getAbsolutePath());
			element("-u");
			element("}");
		}
		programNamePosition = list.size();
		element(programName);
		ultraSimplifiedList.add(programName);
	}

	public CommandBuilder() {
		this(true);
	}

	public CommandBuilder(boolean unitexProgram) {
		this.unitexProgram = unitexProgram;
		list = new ArrayList<String>();
		ultraSimplifiedList = new ArrayList<String>();
	}

	CommandBuilder(ArrayList<String> list) {
		this.list = list;
		this.ultraSimplifiedList = new ArrayList<String>();
	}

	public void element(String s) {
		list.add(s);
	}

	public void element(char[] s) {
		list.add(new String(s));
	}

	public void protectElement(String s) {
		element("\"" + s + "\"");
	}

	public void protectElement(char[] s) {
		element("\"" + new String(s) + "\"");
	}

	public String getOutputEncoding() {
		if (!unitexProgram || getType() != PROGRAM) {
			/* This is meaningful only for Unitex programs */
			return null;
		}
		switch (ConfigManager.getManager().getEncoding(null)) {
		case UTF8:
			return "-qutf8-no-bom";
		case UTF16LE:
			return null;
		case UTF16BE:
			return "-qutf16be-bom";
		}
		return null;
	}

	public void time(File f) {
		list.add(programNamePosition, "\"--time=" + f.getAbsolutePath() + "\"");
		programNamePosition++;
	}

	public String getCommandLine() {
		String res = "";
		for (final String aList : list) {
			res = res + aList + " ";
		}
		if (getOutputEncoding() != null) {
			res = res + getOutputEncoding();
		}
		return res;
	}

	public String getSimplifiedCommandLine() {
		String res = "";
		for (int i = programNamePosition; i < list.size(); i++) {
			res = res + list.get(i) + " ";
		}
		if (getOutputEncoding() != null) {
			res = res + getOutputEncoding();
		}
		return res;
	}

	public String getUltraSimplifiedCommandLine() {
		String res = "";
		for (final String s : ultraSimplifiedList) {
			res = res + s + " ";
		}
		return res;
	}

	public String[] getCommandArguments() {
		return getCommandArguments(true);
	}

	public String[] getCommandArguments(boolean setEncoding) {
		String encoding = null;
		if (setEncoding) {
			encoding = getOutputEncoding();
		}
		final String[] res = list.toArray(new String[list.size()
				+ ((encoding != null) ? 1 : 0)]);
		for (int i = 0; i < list.size(); i++) {
			if (res[i].startsWith("\"")) {
				res[i] = res[i].substring(1, res[i].length() - 1);
			}
		}
		if (encoding != null)
			res[res.length - 1] = encoding;
		return res;
	}

	public CommandBuilder getBuilder() {
		return this;
	}

	@SuppressWarnings("unchecked")
	ArrayList<String> getCopyOfList() {
		return (ArrayList<String>) list.clone();
	}

	public int getType() {
		return type;
	}

	@Override
	public ConsoleEntry logIntoConsole() {
		return Console.addCommand(getCommandLine(), false,
				Log.getCurrentLogID());
	}

	/**
	 * This is overridden by GrfDiffCommand
	 */
	public boolean isCommandSuccessful(int retValue) {
		return retValue == 0;
	}

	/**
	 * Executes the command, dealing with its outputs.
	 */
	@Override
	public boolean executeCommand(final ExecParameters parameters,
			final ConsoleEntry entry) {
		Process p = null;
		boolean problem = false;
		final CommandBuilder currentCommand = this;
		final String[] comm = getCommandArguments(true);
		try {
			/* We create the process */
			parameters.setProcess(Runtime.getRuntime().exec(comm, null,
					parameters.getWorkingDirectory()));
			p = parameters.getProcess();
			if (parameters.getStdout() == null) {
				/* If needed, we just consume the output stream */
				new EatStreamThread(p.getInputStream()).start();
				entry.setNormalStreamEnded(true);
			} else {
				new ProcessInfoThread(parameters.getStdout(),
						p.getInputStream(), entry, false).start();
			}
			if (parameters.getStderr() == null) {
				/* If needed, we just consume the error stream */
				new EatStreamThread(p.getErrorStream()).start();
				entry.setErrorStreamEnded(true);
			} else {
				new ProcessInfoThread(parameters.getStderr(),
						p.getErrorStream(), entry, true).start();
			}
			/* Now, we just wait for the end of the process */
			try {
				p.waitFor();
				if (parameters.isStopOnProblem()) {
					/* iff we need to report a problem */
					if (!currentCommand.isCommandSuccessful(p.exitValue())) {
						problem = true;
					}
				}
				parameters.setProcess(null);
				return !problem;
			} catch (final java.lang.InterruptedException e) {
				/*
				 * If the process is interrupted for any reason, like a click on
				 * a "Cancel" button
				 */
				if (parameters.isStopOnProblem()) {
					try {
						SwingUtilities.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								parameters.getStderr().addLine(
										new Couple("The program " + comm[0]
												+ " has been interrupted\n",
												true));
							}
						});
					} catch (final InterruptedException e1) {
						e1.printStackTrace();
					} catch (final InvocationTargetException e1) {
						e1.printStackTrace();
					}
					problem = true;
				}
				parameters.setProcess(null);
				return problem;
			}
		} catch (final java.io.IOException e) {
			/* If the process could not be created */
			final String programName = comm[0];
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					parameters.getStderr().addLine(
							new Couple("Cannot launch the program "
									+ programName + "\n", true));
				}
			});
			if (parameters.isStopOnProblem()) {
				problem = true;
			}
			parameters.setProcess(null);
			return !problem;
		}
	}

	private ToDoBeforeSingleCommand toDoBefore = null;
	private ToDoAfterSingleCommand toDoAfter = null;

	@Override
	public void setWhatToDoBefore(ToDoBeforeSingleCommand r) {
		this.toDoBefore = r;
	}

	@Override
	public ToDoBeforeSingleCommand getWhatToDoBefore() {
		return toDoBefore;
	}

	@Override
	public void setWhatToDoOnceCompleted(ToDoAfterSingleCommand r) {
		this.toDoAfter = r;
	}

	@Override
	public ToDoAfterSingleCommand getWhatToDoOnceCompleted() {
		return toDoAfter;
	}
}
