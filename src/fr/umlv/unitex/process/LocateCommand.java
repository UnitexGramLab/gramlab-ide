 /*
  * Unitex
  *
  * Copyright (C) 2001-2008 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
public class LocateCommand extends CommandBuilder {

	public LocateCommand() {
		super("Locate");
	}

	public LocateCommand snt(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public LocateCommand fst2(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public LocateCommand alphabet() {
		protectElement(Config.getAlphabet().getAbsolutePath());
		return this;
	}

	public LocateCommand alphabet(File alphabet) {
		protectElement(alphabet.getAbsolutePath());
		return this;
	}

	public LocateCommand shortestMatches() {
		element("s");
		return this;
	}

	public LocateCommand longestMatches() {
		element("l");
		return this;
	}

	public LocateCommand allMatches() {
		element("a");
		return this;
	}

	public LocateCommand ignoreOutputs() {
		element("i");
		return this;
	}

	public LocateCommand mergeOutputs() {
		element("m");
		return this;
	}

	public LocateCommand replaceWithOutputs() {
		element("r");
		return this;
	}

	public LocateCommand noLimit() {
		element("all");
		return this;
	}

	public LocateCommand limit(int n) {
		element("" + n);
		return this;
	}

    public LocateCommand limit(String n) {
        Integer.parseInt(n);
        element(n);
        return this;
    }

    public LocateCommand dynamicSntDir(File dir) {
      // the dynamicSntDir parameter is supposed to end
      // with the file separator
      protectElement(dir.getAbsolutePath()+File.separator);
      return this;
  }

    public LocateCommand charByChar() {
        element("-thai");
        return this;
    }

    public LocateCommand morphologicalDic(ArrayList<File> dicList) {
    	if (dicList!=null) {
    		protectElement("-md="+Preferences.getMorphologicalDicListAsString(dicList));
    	}
        return this;
    }

    public LocateCommand enableMorphologicalUseOfSpace() {
      element("-space");
      return this;
  }

}