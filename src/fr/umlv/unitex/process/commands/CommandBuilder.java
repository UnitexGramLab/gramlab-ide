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

import fr.umlv.unitex.Config;
import fr.umlv.unitex.Preferences;

import java.io.File;
import java.util.ArrayList;

/**
 * This class provides facilities for build process command lines.
 *
 * @author Sébastien Paumier
 */
public abstract class CommandBuilder {


    public static final int PROGRAM = 0;
    public static final int MESSAGE = 1;
    public static final int ERROR_MESSAGE = 2;
    public static final int METHOD = 3;

    private final ArrayList<String> list;
    protected int type = PROGRAM;

    CommandBuilder(String programName) {
        list = new ArrayList<String>();
        programName("UnitexToolLogger");
        if (Preferences.mustLog()) {
            element("{");
            element("CreateLog");
            element("-d");
            protectElement(Preferences.loggingDir().getAbsolutePath());
            element("-u");
            element("}");
        }
        element(programName);
    }

    CommandBuilder() {
        list = new ArrayList<String>();
    }

    CommandBuilder(ArrayList<String> list) {
        this.list = list;
    }

    void element(String s) {
        list.add(s);
    }

    void protectElement(String s) {
        element("\"" + s + "\"");
    }

    private void programName(String s) {
        protectElement(new File(Config.getApplicationDir(), s + (Config.getCurrentSystem() == Config.WINDOWS_SYSTEM ? ".exe" : "")).getAbsolutePath());
    }

    public String getCommandLine() {
        String res = "";
        for (String aList : list) {
            res = res + aList + " ";
        }
        return res;
    }

    public String[] getCommandArguments() {
        String[] res = list.toArray(new String[list.size()]);
        for (int i = 0; i < res.length; i++) {
            if (res[i].startsWith("\"")) {
                res[i] = res[i].substring(1, res[i].length() - 1);
            }
        }
        return res;
    }

    public CommandBuilder getBuilder() {
        return this;
    }

    @SuppressWarnings("unchecked")
    ArrayList<String> getCopyOfList() {
        return (ArrayList<String>) list.clone();
    }

    public int getType() {
        return type;
    }

}
