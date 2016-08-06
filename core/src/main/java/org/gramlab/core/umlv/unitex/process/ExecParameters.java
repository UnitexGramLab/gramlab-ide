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
package org.gramlab.core.umlv.unitex.process;

import java.io.File;

import org.gramlab.core.umlv.unitex.process.commands.CommandBuilder;
import org.gramlab.core.umlv.unitex.process.commands.MultiCommands;
import org.gramlab.core.umlv.unitex.process.list.ProcessOutputList;

/**
 * @author paumier
 * 
 */
public class ExecParameters {

	/**
	 * If not null, this represents the current running process. This field is
	 * used to know which is the current process from within an Executor object
	 */
	private Process process;

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public boolean isStopOnProblem() {
		return stopOnProblem;
	}

	public MultiCommands getCommands() {
		return commands;
	}

	public ProcessOutputList getStdout() {
		return stdout;
	}

	public ProcessOutputList getStderr() {
		return stderr;
	}

	public ToDo getDO() {
		return DO;
	}

	public boolean isTraceIntoConsole() {
		return traceIntoConsole;
	}

	public File getWorkingDirectory() {
		return workingDirectory;
	}

	/**
	 * true means that if a command does not return 0, the remaining commands
	 * will be skipped
	 */
	private final boolean stopOnProblem;

	/**
	 * The commands to be executed, one by one
	 */
	private final MultiCommands commands;

	/**
	 * The models used to manage outputs. If stdxxx is null, it means that the
	 * output should be ignored. In that case, a NullOutputStream will be used
	 * to consume the data.
	 */
	private final ProcessOutputList stdout, stderr;

	/**
	 * What to execute after the command sequence has been executed. This will
	 * be invoked in the Swing thread.
	 */
	private final ToDo DO;

	/**
	 * If true, the commands and their error outputs will be added to Unitex
	 * console.
	 */
	private final boolean traceIntoConsole;

	private final File workingDirectory;

	public ExecParameters(boolean stopOnProblem, MultiCommands commands,
			ProcessOutputList stdout, ProcessOutputList stderr, ToDo DO,
			boolean traceIntoConsole, File workingDirectory) {
		super();
		this.stopOnProblem = stopOnProblem;
		this.commands = commands;
		this.stdout = stdout;
		this.stderr = stderr;
		this.DO = DO;
		this.traceIntoConsole = traceIntoConsole;
		this.workingDirectory = workingDirectory;
	}

	public ExecParameters(boolean stopOnProblem, CommandBuilder c,
			ProcessOutputList stdout, ProcessOutputList stderr, ToDo DO,
			boolean traceIntoConsole, File workingDirectory) {
		this(stopOnProblem, new MultiCommands(c), stdout, stderr, DO,
				traceIntoConsole, workingDirectory);
	}

	public ExecParameters(boolean stopOnProblem, CommandBuilder c,
			ProcessOutputList stdout, ProcessOutputList stderr, ToDo DO,
			boolean traceIntoConsole) {
		this(stopOnProblem, new MultiCommands(c), stdout, stderr, DO,
				traceIntoConsole, null);
	}

	public ExecParameters(boolean stopOnProblem, MultiCommands c,
			ProcessOutputList stdout, ProcessOutputList stderr, ToDo DO,
			boolean traceIntoConsole) {
		this(stopOnProblem, c, stdout, stderr, DO, traceIntoConsole, null);
	}
}
