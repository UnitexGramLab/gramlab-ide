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
package fr.umlv.unitex.console;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.frames.InternalFrameManager;

public class Console {
	/**
	 * Adds a <code>String</code> to the the command lines
	 * 
	 * @param command
	 *            the command line to be added
	 */
	public static ConsoleEntry addCommand(String command,
			boolean isRealCommand, int pos, boolean systemMsg, String logID) {
		return GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
				.getConsoleFrame().addCommand(command, isRealCommand, pos, systemMsg, logID);
	}

	public static ConsoleEntry addCommand(String command, boolean systemMsg,
			String logID) {
		return GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
				.getConsoleFrame().addCommand(command, true, -1, systemMsg, logID);
	}
}
