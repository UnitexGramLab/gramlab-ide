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
package fr.umlv.unitex.console;

public class ConsoleEntry {
	private final String content;
	private String error;
	/* 0=no error button, 1=error down button, 2=error up button, 3=nothing */
	private int status;
	private final boolean systemMsg;
	/*
	 * The log ID associated to the command, or null if the command wasn't
	 * logged
	 */
	private final String logID;

	public ConsoleEntry(String command, boolean isRealCommand,
			boolean systemMsg, String logID) {
		this.content = command;
		this.status = isRealCommand ? 0 : 3;
		this.systemMsg = systemMsg;
		this.logID = logID;
		if (logID != null && !isRealCommand) {
			throw new IllegalArgumentException(
					"Should not have a log ID for a non Unitex command");
		}
	}

	public String getContent() {
		return content;
	}

	public boolean isSystemMsg() {
		return systemMsg;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getlogID() {
		return logID;
	}

	public void addErrorMessage(String s) {
		if (error == null) {
			error = s;
			status = 1;
		} else {
			if (!error.endsWith("\r\n") && !error.endsWith("\n")) {
				error = error + "\n";
			}
			error = error + s;
		}
	}

	public String getErrorMessage() {
		return error;
	}

	@Override
	public String toString() {
		return getContent();
	}

	private boolean errorStreamEnded = false;
	private boolean normalStreamEnded = false;

	private final Object lock = new Object();

	public boolean isErrorStreamEnded() {
		synchronized (lock) {
			return errorStreamEnded;
		}
	}

	public void setErrorStreamEnded(boolean errorStreamEnded) {
		synchronized (lock) {
			this.errorStreamEnded = errorStreamEnded;
		}
	}

	public boolean isNormalStreamEnded() {
		synchronized (lock) {
			return normalStreamEnded;
		}
	}

	public void setNormalStreamEnded(boolean normalStreamEnded) {
		synchronized (lock) {
			this.normalStreamEnded = normalStreamEnded;
		}
	}

}
