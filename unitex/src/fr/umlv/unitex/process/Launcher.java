/*
 * Unitex
 *
 * Copyright (C) 2001-2019 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.io.BufferedInputStream;
import java.io.IOException;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.frames.InternalFrameManager;
import fr.umlv.unitex.process.commands.CommandBuilder;
import fr.umlv.unitex.process.commands.MultiCommands;

/**
 * Note: if any, the ToDo instructions are executed in the Swing Thread
 * 
 * @author paumier
 * 
 */
public class Launcher {
	public static void exec(CommandBuilder b, boolean close) {
		if (b == null)
			return;
		exec(new MultiCommands(b), close, null, true);
	}

	public static void exec(CommandBuilder b, boolean close, ToDo myDo) {
		if (b == null)
			return;
		exec(new MultiCommands(b), close, myDo, true);
	}

	public static void exec(MultiCommands c, boolean close) {
		exec(c, close, null, true);
	}

	public static void exec(MultiCommands c, boolean close, ToDo myDo) {
		exec(c, close, myDo, true);
	}

	public static void exec(MultiCommands c, boolean close, ToDo myDo,
			boolean stopIfProblem) {
		if (c == null)
			return;
		exec(c, close,myDo, stopIfProblem,false);
	}
	
	public static void exec(MultiCommands c, boolean close, ToDo myDo,
			boolean stopIfProblem,boolean forceToDo) {
		if (c == null)
			return;
		GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
				.newProcessInfoFrame(c, close,myDo, stopIfProblem,forceToDo);
	}

	/**
	 * Executing one command but without tracing it in the console.
	 */
	public static int execWithoutTracing(CommandBuilder b) {
		try {
			final Process p = Runtime.getRuntime().exec(
					b.getCommandArguments(true));
			final BufferedInputStream in = new BufferedInputStream(
					p.getInputStream());
			final BufferedInputStream err = new BufferedInputStream(
					p.getErrorStream());
			new EatStreamThread(in).start();
			new EatStreamThread(err).start();
			return p.waitFor();
		} catch (final IOException e1) {
			/* */
		} catch (final InterruptedException e) {
			/* */
		}
		return 1;
	}

	public static void execExternalCommand(String... cmd) {
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static int execAsExternalCommand(CommandBuilder cmd) {
		try {
			final Process p = Runtime.getRuntime().exec(
					cmd.getCommandArguments(true));
			new EatStreamThread(p.getErrorStream(), System.err).start();
			new EatStreamThread(p.getInputStream(), System.out).start();

			return p.waitFor();
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final InterruptedException e) {
			return -1;
		}
		return -1;
	}
}
