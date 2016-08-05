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
package fr.umlv.unitex.process.commands;

import java.io.File;
import java.util.ArrayList;

import fr.umlv.unitex.exceptions.InvalidDestinationEncodingException;
import fr.umlv.unitex.exceptions.InvalidSourceEncodingException;
import fr.umlv.unitex.transcoding.Transcoder;

/**
 * @author Sébastien Paumier
 */
public class ConvertCommand extends CommandBuilder {
	public ConvertCommand() {
		super("Convert");
	}

	private ConvertCommand(ArrayList<String> l) {
		super(l);
	}

	public ConvertCommand src(String s) throws InvalidSourceEncodingException {
		if (Transcoder.isValidEncoding(s)) {
			element("-s" + s);
			return this;
		}
		throw new InvalidSourceEncodingException(s);
	}

	public ConvertCommand dest(String s)
			throws InvalidDestinationEncodingException {
		if (Transcoder.isValidEncoding(s)) {
			element("-d" + s);
			return this;
		}
		throw new InvalidDestinationEncodingException();
	}

	public ConvertCommand replace() {
		element("-r");
		return this;
	}

	public ConvertCommand rename(boolean addPrefix, boolean renameSource,
			String s) {
		final String element = "--" + (addPrefix ? "p" : "s")
				+ (renameSource ? "s" : "d") + "=" + s;
		protectElement(element);
		return this;
	}

	public ConvertCommand renameSourceWithPrefix(String s) {
		final String element = "--ps=" + s;
		protectElement(element);
		return this;
	}

	public ConvertCommand renameSourceWithSuffix(String s) {
		final String element = "--ss=" + s;
		protectElement(element);
		return this;
	}

	public ConvertCommand renameDestWithPrefix(String s) {
		final String element = "--pd=" + s;
		protectElement(element);
		return this;
	}

	public ConvertCommand renameDestWithSuffix(String s) {
		final String element = "--sd=" + s;
		protectElement(element);
		return this;
	}

	public ConvertCommand file(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public ConvertCommand copy() {
		return new ConvertCommand(getCopyOfList());
	}

	public ConvertCommand getEncodings() {
		protectElement("--aliases");
		return this;
	}

	public ConvertCommand delas() {
		element("--delas");
		return this;
	}

	public ConvertCommand delaf() {
		element("--delaf");
		return this;
	}
}
