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
package fr.umlv.unitex.leximir.helper;

import java.io.File;
import java.io.IOException;
import javax.swing.table.DefaultTableModel;


/**
 * This class manage all JTable used on this project
 * @author Rojo Rabelisoa
 */
public class GridHelper {
    /**
     * This function complete Jtable of Delas dictionary
     * @param alldelas if alldelas is true, the programm open all dictionary in delas folder, else the program open dictionary found in configuration
     * @return 
     * @throws IOException 
     */
    public static DefaultTableModel getOpenEditorforDelas(boolean alldelas,File dic) throws IOException {
        
        String[] header = {"POS","Lemma","FST Code","SynSem Feature","Comment","Inv Lemma","Lemma ID","Dict. File"};
        Object[][] data = DelasHelper.getAllDelasFromDicToObject(alldelas,dic);
        return new DefaultTableModel(data,header);
    }
    /**
     * This function complete Jtable of Delaf with Fst Code
     * @return 
     * @throws IOException 
     */
    public static DefaultTableModel getDelafInDelacForDelac() throws IOException {
        String[] header = {"delaf entry","POS","lemma","Gram cats","FST graph"};
        Object[][] data = DelafHelper.getAllDelafFromDelacToObject();
        return new DefaultTableModel(data,header);
    }
}
