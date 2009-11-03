 /*
  * Unitex
  *
  * Copyright (C) 2001-2009 Universit� Paris-Est Marne-la-Vall�e <unitex@univ-mlv.fr>
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
 * @author S�bastien Paumier
 *  
 */
public class DicoCommand extends CommandBuilder {

	public DicoCommand() {
		super("Dico");
	}

	public DicoCommand snt(File s) {
		protectElement("-t"+s.getAbsolutePath());
		return this;
	}

	public DicoCommand alphabet(File s) {
		protectElement("-a"+s.getAbsolutePath());
		return this;
	}

	public DicoCommand morphologicalDic(ArrayList<File> dicList) {
    	if (dicList!=null && !dicList.isEmpty()) {
    		protectElement("-m"+Preferences.getMorphologicalDicListAsString(dicList));
    	}
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

	public DicoCommand dictionaryList(ArrayList<File> list) {
		for (int i = 0; i < list.size(); i++) {
			protectElement(list.get(i).getAbsolutePath());
		}
		return this;
	}
	
	public DicoCommand korean() {
		element("-j");
		File curlangdir  = Config.getUserCurrentLanguageDir();
		File encodage = new File(curlangdir,"jamoTable.txt");
		protectElement(encodage.getAbsolutePath());

		element("-f");
	    File decodage = new File(new File(curlangdir,"Decoding"),"uneSyl.fst2");
	    protectElement(decodage.getAbsolutePath() );

        return this;
    }


}