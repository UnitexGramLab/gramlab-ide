/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import model.Delac;
import model.StaticValue;
import util.Utils;

/**
 *
 * @author Rojo Rabelisoa
 */
public class DelacHelper {
    /**
     * This function return a list of dictionnary in directory
     * @return
     * @throws FileNotFoundException 
     */
    public static ArrayList<String> getDicDelacPath() throws FileNotFoundException, IOException{
        ArrayList<String> list= new ArrayList<>();
        //File folder = new File(Utils.getValueXml("pathDelas"));
        File folder = new File(StaticValue.allDelac);
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
    
    public static Object[][] getAllDelacFromDicToObject() throws FileNotFoundException, IOException{
        ArrayList<String> list= getDicDelacPath();
        Delac delac = new Delac();
        Field[] lf = delac.getClass().getDeclaredFields();
        int count =0;
        for(String dela:list){
            //String path = Utils.getValueXml("pathDelas")+"/"+dela;
            String path = StaticValue.allDelac+"//"+dela;
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
            String pOs,lemmaAll,lemma,fSTCode,sinSem,comment,wn_SinSet;
            int lemmaId=10;
            String dicFile=dela;
            String path = StaticValue.allDelac+"//"+dela;
            ArrayList<String> readFile = Utils.readFile(path);
            for(String s:readFile){
                wn_SinSet="";
                lemmaAll=getLemaAllDelac(s);
                lemma=getLemaInLemaAllDelac(lemmaAll);
                fSTCode = getFstCodeInDelac(s);
                sinSem="+"+getSynSemInDelac(s);
                pOs = getPosInDelac(s);
                comment = getCommentInDelas(s);
                Delac tmp = new Delac(pOs, lemmaAll, lemma, fSTCode, sinSem, comment, wn_SinSet, lemmaId, dicFile, dicId);
                delacToObject(ob, k, tmp);
                k++;
                lemmaId=lemmaId+10;
            }
            dicId++;
        }
        return ob;
    }
    

    private static void delacToObject(Object[][] ob, int k, Delac tmp) {
        ob[k][0]=tmp.getpOS();
        ob[k][1]=tmp.getLemmaAll();
        ob[k][2]=tmp.getLemma();
        ob[k][3]=tmp.getfSTCode();
        ob[k][4]=tmp.getSimSem();
        ob[k][5]=tmp.getComment();
        ob[k][6]=tmp.getWn_sinSet();
        ob[k][7]=tmp.getLemmaId();
        ob[k][8]=tmp.getDicFile();
        ob[k][9]=tmp.getDicId();
    }
    
    public static String getLemaInLemaAllDelac(String text) {
        StringBuilder sb = new StringBuilder();
        boolean isNotInBracket=false;
        for(int i=0;i<text.length();i++){
            if(!isNotInBracket){
                if(text.charAt(i)!='('){
                    sb.append(text.charAt(i));
                }
                else{
                    isNotInBracket=true;
                }
            }
            else{
                if(text.charAt(i)==')'){
                    isNotInBracket=false;
                }
            }
        }
        return sb.toString();
    }  
    public static String getLemaAllDelac(String text) {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<text.length();i++){
            if(text.charAt(i)!=','){
                sb.append(text.charAt(i));
            }
            else{
                break;
            }
        }
        return sb.toString();
    }  
    public static String getFstCodeInDelac(String text){
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
    public static String getSynSemInDelac(String text){
        StringBuilder sb = new StringBuilder();
        boolean begin=false;
        for(int i=0;i<text.length();i++){
            if(text.charAt(i)==','){
                begin=true;
                i++;
            }
            if(begin){
                if(text.charAt(i)=='/'){
                    break;
                }
                sb.append(text.charAt(i));
            }
        }
        return sb.toString();
    }
    public static String getPosInDelac(String text){
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
                if(charInt=='/'||charInt=='+'||charInt=='_'){
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
    public static Object[][] completeJTableFLX(String lema) {
        String[] words = lema.split("-|\\ ");
        int separatorSpace = lema.indexOf(" ");
        int separatorIndex = lema.indexOf("-");
        char separator = 0;
        if (separatorSpace > -1) {
            separator = lema.charAt(separatorSpace);
        } else if (separatorIndex > -1) {
            separator = lema.charAt(separatorIndex);
        }
        Object[][] objFlx = new Object[words.length][6];
        int k = 0;
        for (String word : words) {
            String sbForm = "";
            String sbLema = "";
            String sbFstCode = "";
            String sbGramCat = "";
            if (word.contains("(")) {
                int parantheseIndex = word.indexOf("(");
                int pointIndex = word.indexOf(".");
                int colounIndex = word.indexOf(":");
                int endParantesIndex = word.indexOf(")");
                sbForm = word.substring(0, parantheseIndex);
                sbLema = word.substring(parantheseIndex + 1, pointIndex);
                sbFstCode = word.substring(pointIndex + 1, colounIndex);
                sbGramCat = word.substring(colounIndex + 1, endParantesIndex - 1);
            } else {
                sbForm = word;
            }
            objFlx[k][0] = k + 1;
            objFlx[k][1] = sbForm;
            objFlx[k][2] = sbLema;
            objFlx[k][3] = sbFstCode;
            objFlx[k][4] = sbGramCat;
            objFlx[k][5] = separator;
            k++;
        }
        return objFlx;
    }
    public static Object[][] completeJTableDlf(List<String> result) {
        /**
         * ** complete for jtable dlf **
         */
        Object[][] predictFlex = new Object[result.size()][4]; // create a table ulaz,lema,Pos,GramCat
        int i = 0;
        for (String result1 : result) {
            int indexLema = result1.indexOf(",");
            int indexPosBegin = result1.indexOf(".");
            int indexPosEnd = result1.indexOf("+") > -1 ? result1.indexOf("+") : result1.indexOf(":");
            int indexGramCat = result1.indexOf(":");
            String ulaz = result1;
            String lema = "";
            if ((indexLema + 1) == indexPosBegin) {
                lema = result1.substring(0, indexLema);
            } else {
                lema = result1.substring(indexLema + 1, indexPosBegin);
            }
            String Pos = indexPosEnd > -1 ? result1.substring(indexPosBegin + 1, indexPosEnd) : result1.substring(indexPosBegin);
            String gramCat = result1.substring(indexGramCat + 1);
            if (!Pos.contains("V")) {
                predictFlex[i][0] = ulaz;
                predictFlex[i][1] = lema;
                predictFlex[i][2] = Pos;
                predictFlex[i][3] = gramCat;
                i++;
            }
        }
        return predictFlex;
    }
    public static Object[][] completeJTablePredict(List<String> ret) {
        Object[][] dataPredict = new Object[ret.size()][3];
        for (int k = 0; k < ret.size(); k++) {
            String[] token = ret.get(k).split(",");
            for (int l = 0; l < token.length; l++) {
                dataPredict[k][l] = token[l];
            }
        }
        return dataPredict;
    }
}
