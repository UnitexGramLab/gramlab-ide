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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import fr.umlv.unitex.leximir.model.DictionaryPath;
import fr.umlv.unitex.leximir.util.Utils;

/**
 *This class is an helper for delaf entry
 * @author Rojo Rabelisoa
 */
public class DelafHelper {
    /**
     * this function transform a list of delaf in file "Dfl" to Object[][] to complete a jtable with their Fst Code if exist
     * @return list of delaf which contains in 
     * [0] : Ulaz,
     * [1] : POS,
     * [2] : Lema,
     * [3] : Gram Cat,
     * [4] : FST Code of this lema if exist in delas dictionaries
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static Object[][] getAllDelafFromDelacToObject() throws FileNotFoundException, IOException{
        String path = DictionaryPath.text_sntAbsPath;
        ArrayList<String> readFile = Utils.readFile(path);
        Object[][] ob = new Object[readFile.size()][5];        
        try{
            boolean alldelas = true;
            Object[][] getAllDelas = DelasHelper.getAllDelasFromDicToObject(alldelas,null);
            int i=0;
            for (Object[] allDela : getAllDelas) {
                String lema = (String) allDela[1];
                String pos = (String) allDela[0];
                for(String s:readFile){
                    if (lema.equals(getLema(s))) {
                        ob[i][0]=getUlaz(s);
                        ob[i][1]=getPOS(s);
                        ob[i][2]=getLema(s);
                        ob[i][3]=getGramCats(s);
                        if(pos.equals(getPOS(s))){
                            ob[i][4]=allDela[2];
                        }
                        i=i+1;
                    }
                }
            }
        }
        catch(java.lang.ArrayIndexOutOfBoundsException e){
            ob = new Object[0][0]; 
            ob[0][0]="dlf not found";
        }
        return ob;
    }
    /**
     * This function get entry delaf
     * @param text entry delaf
     * @return entry delaf
     */
    public static String getUlaz(String text){
        return text;
    }
    /**
     * This function get POS of an entry delaf
     * @param text entry delaf
     * @return POS of delaf
     */
    public static String getPOS(String text){
        StringBuilder sb = new StringBuilder();
        boolean begin=false;
        for(int i=0;i<text.length();i++){
            if(text.charAt(i)==':'||text.charAt(i)=='+'){
                break;
            }
            if(text.charAt(i)=='.'){
                begin=true;
                i++;
            }
            if(begin){
                sb.append(text.charAt(i));
            }
        }
        return sb.toString();
    }
    /**
     * This function get lemma of an entry delaf
     * @param text entry delaf
     * @return lemma of delaf
     */
    public static String getLema(String text){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<text.length();i++){
            if(text.charAt(i)==','){
                break;
            }
            sb.append(text.charAt(i));
        }
        return sb.toString();
    }
    /**
     * This function get GramCats of an entry delaf
     * @param text entry delaf
     * @return GramCat of delaf
     */
    public static String getGramCats(String text){
        StringBuilder sb = new StringBuilder();
        boolean begin=false;
        for(int i=0;i<text.length();i++){
            if(text.charAt(i)==':'){
                begin=true;
                i++;
            }
            if(begin){
                sb.append(text.charAt(i));
            }
        }
        return sb.toString();
    }
}
