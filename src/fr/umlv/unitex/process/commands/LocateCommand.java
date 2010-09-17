/*
 * Unitex
 *
 * Copyright (C) 2001-2010 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import fr.umlv.unitex.Config;
import fr.umlv.unitex.Preferences;

/**
 * @author Sébastien Paumier
 * 
 */
public class LocateCommand extends CommandBuilder {

    public LocateCommand() {
        super("Locate");
        if (Config.isArabic()) {
            arabic(new File(Config.getUserCurrentLanguageDir(), "arabic_typo_rules.txt"));
        }

    }

    public LocateCommand snt(File s) {
        protectElement("-t" + s.getAbsolutePath());
        return this;
    }

    public LocateCommand fst2(File s) {
        protectElement(s.getAbsolutePath());
        return this;
    }

    public LocateCommand alphabet(File alphabet) {
        protectElement("-a" + alphabet.getAbsolutePath());
        return this;
    }

    public LocateCommand shortestMatches() {
        element("-S");
        return this;
    }

    public LocateCommand arabic(File s) {
        element("-u");
        protectElement(s.getAbsolutePath());
        return this;
    }

    public LocateCommand longestMatches() {
        element("-L");
        return this;
    }

    public LocateCommand allMatches() {
        element("-A");
        return this;
    }

    public LocateCommand ignoreOutputs() {
        element("-I");
        return this;
    }

    public LocateCommand mergeOutputs() {
        element("-M");
        return this;
    }

    public LocateCommand replaceWithOutputs() {
        element("-R");
        return this;
    }

    public LocateCommand noLimit() {
        element("--all");
        return this;
    }

    public LocateCommand limit(int n) {
        element("-n" + n);
        return this;
    }

    public LocateCommand dynamicSntDir(File dir) {
        // the dynamicSntDir parameter is supposed to end
        // with the file separator
        protectElement("-d" + dir.getAbsolutePath() + File.separator);
        return this;
    }

    public LocateCommand charByChar() {
        element("-c");
        return this;
    }

    public LocateCommand morphologicalDic(ArrayList<File> dicList) {
        if (dicList!=null && !dicList.isEmpty()) {
            protectElement("--morpho=" + Preferences.getMorphologicalDicListAsString(dicList));
        }
        return this;
    }

    public LocateCommand enableMorphologicalUseOfSpace() {
        element("-s");
        return this;
    }
    
    public LocateCommand allowAmbiguousOutputs() {
        element("-b");
        return this;
    }

    public LocateCommand forbidAmbiguousOutputs() {
        element("-z");
        return this;
    }

    public LocateCommand exitOnVariableErrors() {
        element("-X");
        return this;
    }

    public LocateCommand ignoreVariableErrors() {
        element("-Y");
        return this;
    }

    public LocateCommand backtrackOnVariableErrors() {
        element("-Z");
        return this;
    }
    
	public LocateCommand korean() {
		element("-K");
        return this;
    }

}