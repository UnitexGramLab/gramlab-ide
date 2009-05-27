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

import fr.umlv.unitex.*;

/**
 * @author S�bastien Paumier
 *  
 */
public class LocateTfstCommand extends CommandBuilder {

	public LocateTfstCommand() {
		super("LocateTfst");
	}

	public LocateTfstCommand tfst(File s) {
		protectElement("-t"+s.getAbsolutePath());
		return this;
	}

	public LocateTfstCommand fst2(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

	public LocateTfstCommand alphabet() {
		protectElement("-a"+Config.getAlphabet().getAbsolutePath());
		return this;
	}

	public LocateTfstCommand alphabet(File alphabet) {
		protectElement("-a"+alphabet.getAbsolutePath());
		return this;
	}
	
	public LocateTfstCommand jamo(File jamo) {
        protectElement("-j"+jamo.getAbsolutePath());
        return this;
    }

	public LocateTfstCommand shortestMatches() {
		element("-S");
		return this;
	}

	public LocateTfstCommand longestMatches() {
		element("-L");
		return this;
	}

	public LocateTfstCommand allMatches() {
		element("-A");
		return this;
	}

	public LocateTfstCommand ignoreOutputs() {
		element("-I");
		return this;
	}

	public LocateTfstCommand mergeOutputs() {
		element("-M");
		return this;
	}

	public LocateTfstCommand replaceWithOutputs() {
		element("-R");
		return this;
	}

	public LocateTfstCommand noLimit() {
		element("--all");
		return this;
	}

	public LocateTfstCommand limit(int n) {
		element("-n"+n);
		return this;
	}

    public LocateTfstCommand limit(String n) {
        Integer.parseInt(n);
        element("-n"+n);
        return this;
    }

    public LocateTfstCommand allowAmbiguousOutputs() {
        element("-b");
        return this;
    }

    public LocateTfstCommand forbidAmbiguousOutputs() {
        element("-z");
        return this;
    }

    public LocateTfstCommand exitOnVariableErrors() {
        element("-X");
        return this;
    }

    public LocateTfstCommand ignoreVariableErrors() {
        element("-Y");
        return this;
    }

    public LocateTfstCommand backtrackOnVariableErrors() {
        element("-Z");
        return this;
    }

}