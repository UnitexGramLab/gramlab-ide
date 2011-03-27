/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import fr.umlv.unitex.Config;
import fr.umlv.unitex.Preferences;

/**
 * @author Sébastien Paumier
 */
public class Grf2Fst2Command extends CommandBuilder {

    public Grf2Fst2Command() {
        super("Grf2Fst2");
    }

    public Grf2Fst2Command grf(File s) {
        protectElement(s.getAbsolutePath());
        return this;
    }

    public Grf2Fst2Command enableLoopAndRecursionDetection(boolean b) {
        element(b ? "-y" : "-n");
        return this;
    }

    private Grf2Fst2Command charByCharTokenization() {
        element("--char_by_char");
        return this;
    }

    private Grf2Fst2Command alphabetTokenization(File f) {
        protectElement("--alphabet=" + f.getAbsolutePath());
        return this;
    }

    public Grf2Fst2Command tokenizationMode() {
        if (Config.isCharByCharLanguage()) {
            return charByCharTokenization();
        }
        return alphabetTokenization(Config.getAlphabet());
    }

    Grf2Fst2Command library(File f) {
        element("-d");
        protectElement(f.getAbsolutePath());
        return this;
    }

    public Grf2Fst2Command repository() {
        if (Preferences.packagePath() != null) {
            return library(Preferences.packagePath());
        }
        return this;
    }

    public Grf2Fst2Command debug() {
        element("--debug");
        return this;
    }

}