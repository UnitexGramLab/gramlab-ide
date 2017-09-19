/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.StaticValue;
import util.Utils;

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
        String path = StaticValue.text_sntAbsPath;
        ArrayList<String> readFile = Utils.readFile(path);
        Object[][] ob = new Object[readFile.size()][5];        
        try{
            boolean alldelas = true;
            Object[][] getAllDelas = DelasHelper.getAllDelasFromDicToObject(alldelas);
            int i=0;
            for (Object[] allDela : getAllDelas) {
                String lema = (String) allDela[1];
                String pos = (String) allDela[0];
                for(String s:readFile){
                    //
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
