/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;
import model.Delas;
import model.StaticValue;
import util.Utils;

/**
 *
 * @author rojo
 */
public class DelasHelper {
    /**
     * This function return a list of dictionnary in directory
     * @return
     * @throws FileNotFoundException 
     */
    public static ArrayList<String> getDicDelasPath() throws FileNotFoundException, IOException{
        ArrayList<String> list= new ArrayList<>();
        //File folder = new File(Utils.getValueXml("pathDelas"));
        //File folder = new File("/Users/rojo/Documents/LeXimir4UnitexRes/Delas");
        File folder = new File(StaticValue.allDelas);
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
    
    public static Object[][] getAllDelasFromDicToObject() throws FileNotFoundException, IOException{
        ArrayList<String> list= getDicDelasPath();
        Delas delac = new Delas();
        Field[] lf = delac.getClass().getDeclaredFields();
        int count =0;
        for(String dela:list){
            //String path = Utils.getValueXml("pathDelas")+"/"+dela;
            String path = StaticValue.allDelas+"//"+dela;
            ArrayList<String> readFile = Utils.readFile(path);
            for(String s:readFile){
                count++;
            }
            StaticValue.dictionnary.add(dela);
        }
        
        Object[][] ob = new Object[count][lf.length];
        int k=0;
        int dicId=0;
        for(String dela:list){
            String pOs,lemma,fSTCode,sinSem,comment,lemmaInv,wn_SinSet;
            int lemmaId=10;
            String dicFile=dela;
            //String path = Utils.getValueXml("pathDelas")+"/"+dela;
            String path = StaticValue.allDelas+"//"+dela;
            ArrayList<String> readFile = Utils.readFile(path);
            for(String s:readFile){
                lemma=getLemaInDelas(s);
                lemmaInv=Utils.reverseString(lemma);
                sinSem="+"+getSynSemInDelas(s);
                fSTCode = getFstCodeInDelas(s);
                pOs = getPosInDelas(s);
                comment = getCommentInDelas(s);
                wn_SinSet = "";
                Delas tmp = new Delas(pOs, lemma, fSTCode, sinSem, comment, lemmaInv, wn_SinSet, lemmaId, dicFile, dicId);
                delacToObject(ob, k, tmp);
                k++;
                lemmaId=lemmaId+10;
                
            }
            dicId++;
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
        ob[k][6]=tmp.getWn_sinSet();
        ob[k][7]=tmp.getLemmaId();
        ob[k][8]=tmp.getDicFile();
        ob[k][9]=tmp.getDicId();
    }
    
    public static String getLemaInDelas(String text){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<text.length();i++){
            if(text.charAt(i)==','){
                break;
            }
            sb.append(text.charAt(i));
        }
        return sb.toString();
    }
    public static String getSynSemInDelas(String text){
        StringBuilder sb = new StringBuilder();
        boolean begin=false;
        for(int i=0;i<text.length();i++){
            if(text.charAt(i)==','){
                begin=true;
                i++;
            }
            if(begin){
                if(text.charAt(i)=='='||text.charAt(i)=='/'){
                    break;
                }
                sb.append(text.charAt(i));
            }
        }
        return sb.toString();
    }
    public static String getFstCodeInDelas(String text){
        StringBuilder sb = new StringBuilder();
        boolean begin=false;
        for(int i=0;i<text.length();i++){
            if(text.charAt(i)==','){
                begin=true;
                i++;
            }
            if(begin){
                if(text.charAt(i)=='+'||text.charAt(i)=='/'||text.charAt(i)=='='){
                    break;
                }
                sb.append(text.charAt(i));
            }
        }
        return sb.toString();
    }
    public static String getPosInDelas(String text){
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
    public static String getCommentInDelas(String text){
        StringBuilder sb = new StringBuilder();
        boolean begin=false;
        for(int i=0;i<text.length();i++){
            if(text.charAt(i)=='/'){
                begin=true;
                i=i+2;
            }
            if(begin){
                sb.append(text.charAt(i));
            }
        }
        return sb.toString();
    }
}
