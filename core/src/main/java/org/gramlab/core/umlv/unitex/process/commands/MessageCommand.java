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

import org.gramlab.core.umlv.unitex.console.ConsoleEntry;
import org.gramlab.core.umlv.unitex.console.Couple;
import org.gramlab.core.umlv.unitex.process.ExecParameters;

/**
 * @author Sébastien Paumier
 */
public class MessageCommand extends CommandBuilder {
	private final String message;

	public MessageCommand(String mess) {
		super("");
		type = MESSAGE;
		message = mess;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public ConsoleEntry logIntoConsole() {
		/* Nothing to log for a normal message */
		return null;
	}

	@Override
	public boolean executeCommand(final ExecParameters p,
			final ConsoleEntry entry) {
		if (p.getStdout() == null)
			return true;
		final MessageCommand c = this;
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					p.getStdout().addLine(new Couple(c.getMessage(), true));
				}
			});
		} catch (final InterruptedException e) {
			return false;
		} catch (final InvocationTargetException e) {
			return false;
		}
		return true;
	}
}
