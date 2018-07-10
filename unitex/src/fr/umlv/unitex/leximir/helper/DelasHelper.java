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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import fr.umlv.unitex.leximir.model.Delas;
import fr.umlv.unitex.leximir.model.DictionaryPath;
import fr.umlv.unitex.leximir.util.Utils;

/**
 *
 * @author Rojo Rabelisoa
 */
public class DelasHelper {
    /**
     * This function return a list of dictionary in directory
     * @return
     * @throws FileNotFoundException 
     */
    public static ArrayList<String> getDicDelasPath() throws FileNotFoundException, IOException {
        ArrayList<String> list= new ArrayList<>();
        File folder = new File(DictionaryPath.allDelas);
        File[] listOfFiles = folder.listFiles();
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                if (listOfFile.getName().endsWith("dic")) {
                    list.add(listOfFile.getName());
                }
            } 
        }
        if(list.isEmpty()){
            throw new FileNotFoundException("dictonnary not found");
        }
        return list;
    }

    /**
     * This function return all line in delas dictionary into Object[][] 
     * @param allDelas if allDelas is true, the function takes all delas in delas folder, else it takes dictionary selected in configuration
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static Object[][] getAllDelasFromDicToObject(boolean allDelas,File dic) throws FileNotFoundException, IOException {
        List<String> list= new ArrayList<>();
        if(allDelas){
            list = getDicDelasPath();
        }
        else if(dic!=null){
            list = Arrays.asList(dic.getAbsolutePath().toString());
        }
        Delas delas = new Delas();
        Field[] lf = delas.getClass().getDeclaredFields();
        int count = 0;
        for(String dela:list){
            //String path = Utils.getValueXml("pathDelas")+"/"+dela;
            String path="";
            if(allDelas)
            path = DictionaryPath.allDelas+File.separator+dela;
            else
            path = dela;

            ArrayList<String> readFile = Utils.readFile(path);
            count += readFile.size();
            DictionaryPath.dictionary.add(dela);
        }
        
        Object[][] ob = new Object[count][lf.length];
        int k=0;
        int lemmaId=0;
        for(String dela:list){
            String pOs,lemma,fSTCode,SynSem,comment,lemmaInv;
            
            String dicFile=dela;
            //String path = Utils.getValueXml("pathDelas")+"/"+dela;
            String path="";
            if(allDelas)
            path = DictionaryPath.allDelas+File.separator+dela;
            else
            path = dela;
            
            ArrayList<String> readFile = Utils.readFile(path);
            for(String s:readFile){
                lemma=getLemaInDelas(s);
                lemmaInv=Utils.reverseString(lemma);
                SynSem=getSynSemInDelas(s);
                fSTCode = getFstCodeInDelas(s);
                pOs = getPosInDelas(s);
                comment = getCommentInDelas(s);
                Delas tmp = new Delas(pOs, lemma, fSTCode, SynSem, comment, lemmaInv, lemmaId, dicFile);
                delacToObject(ob, k, tmp);
                k++;
                lemmaId=lemmaId+1;
                
            }
        }
        return ob;
    }

    private static void delacToObject(Object[][] ob, int k, Delas tmp) {
        ob[k][0]=tmp.getpOS();
        ob[k][1]=tmp.getLemma();
        ob[k][2]=tmp.getfSTCode();
        ob[k][3]=tmp.getSimSem();
        ob[k][4]=tmp.getComment();
        ob[k][5]=tmp.getLemmaInv();
        ob[k][6]=tmp.getLemmaId();
        ob[k][7]=tmp.getDicFile();
    }
    
    public static String getLemaInDelas(String text) {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<text.length();i++){
            if(text.charAt(i)==','){
                break;
            }
            sb.append(text.charAt(i));
        }
        return sb.toString();
    }
    public static String getSynSemInDelas(String text) {
        try{
            StringBuilder sb = new StringBuilder();
            boolean begin=false;
            for(int i=0;i<text.length();i++){

                if(begin){
                    if(text.charAt(i)=='/'){
                        break;
                    }
                    sb.append(text.charAt(i));
                }
                else{
                    if(text.charAt(i)=='+'){
                        begin=true;
                        sb.append(text.charAt(i));
                    }
                }
            }
            return sb.toString();
        } catch(java.lang.StringIndexOutOfBoundsException e) {
            return "";
        }
    }
    public static String getFstCodeInDelas(String text) {
        StringBuilder sb = new StringBuilder();
        boolean begin=false;
        for(int i=0;i<text.length();i++){
            if(text.charAt(i)==','){
                begin=true;
                i++;
            }
            if(begin){
                if(text.charAt(i)=='+'||text.charAt(i)=='/'||text.charAt(i)=='!'||text.charAt(i)=='['||text.charAt(i)=='='){
                    break;
                }
                sb.append(text.charAt(i));
            }
        }
        return sb.toString();
    }
    public static String getPosInDelas(String text) {
        StringBuilder sb = new StringBuilder();
        boolean begin=false;
        for(int i=0;i<text.length();i++){
            if(text.charAt(i)==','){
                begin=true;
                i++;
            }
            if(begin){
                char charInt=text.charAt(i);   
                if(charInt>=48 && charInt<=57){
                    break;
                }
                if(charInt=='/'||charInt=='+'){
                    break;
                }
                sb.append(text.charAt(i));
            }
        }
        return sb.toString();
    }
    public static String getCommentInDelas(String text) {
        try{
            int ind= text.contains("/")?text.indexOf("/")+1:text.length();
            String ret=text.substring(ind);
            return ret;
        } catch(java.lang.StringIndexOutOfBoundsException e) {
            return "";
        }
    }
}
