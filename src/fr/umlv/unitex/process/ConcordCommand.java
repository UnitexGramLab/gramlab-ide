/*
 * Unitex
 *
 * Copyright (C) 2001-2007 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.io.*;

import fr.umlv.unitex.*;
import fr.umlv.unitex.exceptions.*;

/**
 * @author Sébastien Paumier
 *  
 */
public class ConcordCommand extends CommandBuilder {

	public ConcordCommand() {
		super("Concord");
	}

	public ConcordCommand indFile(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public ConcordCommand font(String s) {
		protectElement(s);
		return this;
	}

	public ConcordCommand fontSize(int size) {
		element("" + size);
		return this;
	}

	public ConcordCommand left(int size) {
		element("" + size);
		return this;
	}

	public ConcordCommand left(String size) {
		element("" + size);
		return this;
	}

	public ConcordCommand right(int size) {
		element("" + size);
		return this;
	}

	public ConcordCommand right(String size) {
		element("" + size);
		return this;
	}

	public ConcordCommand order(int order)
			throws InvalidConcordanceOrderException {
		String s;
		switch (order) {
			case 0 :
				s = "TO";
				break;
			case 1 :
				s = "LC";
				break;
			case 2 :
				s = "LR";
				break;
			case 3 :
				s = "CL";
				break;
			case 4 :
				s = "CR";
				break;
			case 5 :
				s = "RL";
				break;
			case 6 :
				s = "RC";
				break;
			default :
				throw new InvalidConcordanceOrderException();
		}
		element(s);
		return this;
	}

	public ConcordCommand order(String s)
			throws InvalidConcordanceOrderException {
		if (s.equals("TO") || s.equals("LC") || s.equals("LR")
				|| s.equals("CL") || s.equals("CR") || s.equals("RL")
				|| s.equals("RC")) {
			element(s);
			return this;
		}
		throw new InvalidConcordanceOrderException();
	}

	public ConcordCommand html() {
		element("html");
		return this;
	}

	public ConcordCommand text() {
		element("text");
		return this;
	}

	public ConcordCommand glossanet() {
		element("glossanet");
		return this;
	}

	public ConcordCommand outputModifiedTxtFile(File s) {
		element("NULL");
		element("0");
		element("0");
		element("0");
		element("NULL");
		protectElement(s.getAbsolutePath());
    element("NULL");
		return this;
	}

	public ConcordCommand sortAlphabet() {
		protectElement(new File(Config.getUserCurrentLanguageDir(),
				"Alphabet_sort.txt").getAbsolutePath());
		return this;
	}

	public ConcordCommand noSortAlphabet() {
		element("NULL");
		return this;
	}

	public ConcordCommand thai(boolean thai) {
		if (thai)
			element("-thai");
		return this;
	}

}