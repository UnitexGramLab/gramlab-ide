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
import java.util.*;

import fr.umlv.unitex.*;

/**
 * @author Sébastien Paumier
 *  
 */
public class DicoCommand extends CommandBuilder {

	public DicoCommand() {
		super("Dico");
	}

	public DicoCommand snt(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public DicoCommand alphabet(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public DicoCommand dictionary(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public DicoCommand userDictionary(String s) {
		dictionary(new File(new File(Config.getUserCurrentLanguageDir(),"Dela"),s));
		return this;
	}

	public DicoCommand systemDictionary(String s) {
    dictionary(new File(new File(Config.getUnitexCurrentLanguageDir(),"Dela"),s));
		return this;
	}

	public DicoCommand dictionaryList(ArrayList list) {
		for (int i = 0; i < list.size(); i++) {
			protectElement(((File)list.get(i)).getAbsolutePath());
		}
		return this;
	}

}