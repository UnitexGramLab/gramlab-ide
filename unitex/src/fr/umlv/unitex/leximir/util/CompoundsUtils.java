/*
 * Unitex
 *
 * Copyright (C) 2001-2018 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package fr.umlv.unitex.leximir.util;

import java.awt.HeadlessException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import fr.umlv.unitex.leximir.model.DictionaryPath;

/**
 * @author Rojo Rabelisoa
 */
public class CompoundsUtils {
    /**
     * This function get an entry of delac and inflect to delaf and transform to List<String> of delaf
     * @param value entry of delac
     * @return List delaf of this delac
     * @throws IOException
     * @throws HeadlessException 
     */
    public static List<String> getDlfInFile(String value) throws IOException, HeadlessException {
        Utils.generateDelaf(value);
        String path = DictionaryPath.text_sntAbsPath;
        ArrayList<String> readFile = Utils.readFile(path);
        for (String s : readFile)
            System.out.println(s);
        
        return readFile;
    }
}
