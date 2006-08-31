 /*
  * Unitex
  *
  * Copyright (C) 2001-2004 Université de Marne-la-Vallée <unitex@univ-mlv.fr>
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

import fr.umlv.unitex.Config;
/**
 * @author hhuh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Jamo2SylCommand extends CommandBuilder {
    public Jamo2SylCommand() {
        super("Jamo2Syl");
    }

    public Jamo2SylCommand decodage(File s) {
		element("-m");
		File curlangdir  = Config.getUserCurrentLanguageDir();
		File encodage = new File(curlangdir,"jamoTable.txt");
		protectElement(encodage.getAbsolutePath());
		File decodage = new File(new File(curlangdir,"Decoding"),"uneSyl.fst2");
		protectElement(decodage.getAbsolutePath() );
        protectElement(s.getAbsolutePath());
        return this;
    }

    public Jamo2SylCommand decoWithOP(File en, File de,File s) {
    	element("-m");
    	protectElement(en.getAbsolutePath());
    	protectElement(de.getAbsolutePath());
    	protectElement(s.getAbsolutePath());
      return this;
  }

}
