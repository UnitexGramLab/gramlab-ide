/*
 * Unitex
 *
 * Copyright (C) 2001-2012 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.console.Console;
import fr.umlv.unitex.console.ConsoleEntry;
import fr.umlv.unitex.console.Couple;
import fr.umlv.unitex.process.EatStreamThread;
import fr.umlv.unitex.process.ExecParameters;
import fr.umlv.unitex.process.Log;
import fr.umlv.unitex.process.ProcessInfoThread;

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
	int type = PROGRAM;
	private boolean unitexProgram=true;

	CommandBuilder(String programName) {
		list = new ArrayList<String>();
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
		element(programName);
	}

	public CommandBuilder() {
		this(true);
	}
	
	public CommandBuilder(boolean unitexProgram) {
		this.unitexProgram=unitexProgram;
		list = new ArrayList<String>();
	}

	CommandBuilder(ArrayList<String> list) {
		this.list = list;
	}

	public void element(String s) {
		list.add(s);
	}

	public void protectElement(String s) {
		element("\"" + s + "\"");
	}

	public String getOutputEncoding() {
		if (!unitexProgram) {
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

	public String getCommandLine() {
		String res = "";
		for (final String aList : list) {
			res = res + aList + " ";
		}
		if (getOutputEncoding() != null) {
			res = res + " " + getOutputEncoding();
		}
		return res;
	}

	public String[] getCommandArguments() {
		final String encoding = getOutputEncoding();
		final String[] res = list.toArray(new String[list.size()
				+ ((encoding != null) ? 1 : 0)]);
		for (int i = 0; i < list.size(); i++) {
			if (res[i].startsWith("\"")) {
				res[i] = res[i].substring(1, res[i].length() - 1);
			}
		}
		if (encoding != null)
			res[res.length - 1] = getOutputEncoding();
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
	
	public ConsoleEntry logIntoConsole() {
		return Console.addCommand(
				getCommandLine(), false, Log
						.getCurrentLogID());
	}
	
	/**
	 * This is overrided by GrfDiffCommand
	 */
	public boolean isCommandSuccessful(int retValue) {
		return retValue==0;
	}
	
	/**
	 * Executes the command, dealing with its outputs.
	 */
	public boolean executeCommand(final ExecParameters parameters,final ConsoleEntry entry) {
		Process p=null;
		boolean problem=false;
		final CommandBuilder currentCommand=this;
		final String[] comm=getCommandArguments();
		try {
			/* We create the process */
			p = Runtime.getRuntime().exec(comm);
			if (parameters.getStdout()==null) {
				/* If needed, we just consume the output stream */
				new EatStreamThread(p.getInputStream()).start();
			} else {
				new ProcessInfoThread(parameters.getStdout(), p
					.getInputStream(),null)
					.start();
			}
			if (parameters.getStderr()==null) {
				/* If needed, we just consume the error stream */
				new EatStreamThread(p.getErrorStream()).start();
			} else {
				new ProcessInfoThread(parameters.getStderr(), p
					.getErrorStream(),entry)
					.start();
			}
			/* Now, we just wait for the end of the process */
			try {
				p.waitFor();
				if (parameters.isStopOnProblem()) {
					/* iff we need to report a problem */
					if (!currentCommand.isCommandSuccessful(p.exitValue())) {
						problem=true;
					}
				}
				return !problem;
			} catch (final java.lang.InterruptedException e) {
				/* If the process is interrupted for any reason,
				 * like a click on a "Cancel" button
				 */
				if (parameters.isStopOnProblem()) {
					try {
						SwingUtilities
								.invokeAndWait(new Runnable() {
									public void run() {
										parameters.getStderr()
												.addLine(new Couple(
														"The program "
																+ comm[0]
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
				return problem;
			}
		} catch (final java.io.IOException e) {
			/* If the process could not be created */
			final String programName = comm[0];
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					parameters.getStderr().addLine(new Couple(
							"Cannot launch the program "
									+ programName + "\n",
							true));
				}
			});
			if (parameters.isStopOnProblem()) {
				problem = true;
			}
			return !problem;
		}
	}
	
}
