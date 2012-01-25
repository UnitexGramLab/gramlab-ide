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

import java.util.ArrayList;

import fr.umlv.unitex.config.ConfigManager;

/**
 * This class provides facilities for build process command lines.
 * 
 * @author Sébastien Paumier
 */
public abstract class CommandBuilder {
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
}
