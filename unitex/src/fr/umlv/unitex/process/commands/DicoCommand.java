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

import fr.umlv.unitex.config.Config;

/**
 * @author Sébastien Paumier
 */
public class DicoCommand extends CommandBuilder {
	public DicoCommand() {
		super("Dico");
	}

	public DicoCommand snt(File s) {
		protectElement("-t" + s.getAbsolutePath());
		ultraSimplifiedList.add(s.getName());
		return this;
	}

	public DicoCommand alphabet(File s) {
		if (s != null)
			protectElement("-a" + s.getAbsolutePath());
		return this;
	}

	public DicoCommand morphologicalDic(ArrayList<File> dicList) {
		if (dicList != null && !dicList.isEmpty()) {
			for (final File f : dicList) {
				protectElement("-m" + f.getAbsolutePath());
			}
		}
		return this;
	}

	DicoCommand dictionary(File s) {
		protectElement(s.getAbsolutePath());
		ultraSimplifiedList.add(s.getName());
		return this;
	}

	public DicoCommand userDictionary(String s) {
		dictionary(new File(
				new File(Config.getUserCurrentLanguageDir(), "Dela"), s));
		return this;
	}

	public DicoCommand systemDictionary(String s) {
		dictionary(new File(new File(Config.getUnitexCurrentLanguageDir(),
				"Dela"), s));
		return this;
	}

	public DicoCommand dictionaryList(ArrayList<File> l) {
		for (final File aList : l) {
			protectElement(aList.getAbsolutePath());
			ultraSimplifiedList.add(aList.getName());
		}
		return this;
	}

	public DicoCommand korean() {
		element("-K");
		return this;
	}

	public DicoCommand semitic() {
		element("--semitic");
		return this;
	}

	public DicoCommand arabic(File s) {
		element("-u");
		protectElement(s.getAbsolutePath());
		return this;
	}

	public DicoCommand raw(File s) {
		protectElement("-r" + s.getAbsolutePath());
		return this;
	}
}
