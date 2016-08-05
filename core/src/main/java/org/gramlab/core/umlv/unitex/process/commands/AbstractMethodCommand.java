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

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.gramlab.core.umlv.unitex.console.Console;
import org.gramlab.core.umlv.unitex.console.ConsoleEntry;
import org.gramlab.core.umlv.unitex.console.Couple;
import org.gramlab.core.umlv.unitex.process.ExecParameters;

public abstract class AbstractMethodCommand extends CommandBuilder {
	AbstractMethodCommand() {
		type = CommandBuilder.METHOD;
	}

	protected AbstractMethodCommand(String s) {
		/*
		 * We don't call super(s), because we want to see 'mkdir' and not
		 * '.../Unitex/App/mkdir'
		 */
		element(s);
		type = CommandBuilder.METHOD;
	}

	/**
	 * The method to invoke to do the job.
	 * 
	 * @return false if an error occurred; true otherwise
	 */
	public abstract boolean execute();

	@Override
	public String getCommandLine() {
		String res = "";
		for (final String aList : list) {
			res = res + aList + " ";
		}
		/*
		 * No additional parameter for an external command if
		 * (getOutputEncoding()!=null) { res=res+" "+getOutputEncoding(); }
		 */
		return res;
	}

	@Override
	public ConsoleEntry logIntoConsole() {
		return Console.addCommand(getCommandLine(), false, null);
	}

	@Override
	public boolean executeCommand(final ExecParameters p,
			final ConsoleEntry entry) {
		final boolean ret = execute();
		if (ret || !p.isStopOnProblem())
			return true;
		if (p.getStderr() == null)
			return false;
		try {
			final AbstractMethodCommand c = this;
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					p.getStderr().addLine(
							new Couple("Command failed: " + c.getCommandLine(),
									true));
				}
			});
		} catch (final InterruptedException e1) {
			return false;
		} catch (final InvocationTargetException e1) {
			return false;
		}
		return false;
	}

}
