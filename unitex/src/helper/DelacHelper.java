/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import static helper.DelasHelper.getDicConfigDelasPath;
import static helper.DelasHelper.getDicDelasPath;
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
 * This class is an helper for delac entry
 * @author Rojo Rabelisoa
 */
public class DelacHelper {
    /**
     * This function return a list of dictionnary in directory
     * @return return an ArrayList of dictionnary
     * @throws FileNotFoundException if there are no dictionnary found in StaticValue.allDelc path 
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
    /**
     * This function return a list of dictionnary found in configuration
     * @return
     * @throws FileNotFoundException 
     */
    public static ArrayList<String> getDicConfigDelacPath() throws FileNotFoundException, IOException{
    	ArrayList<String> list= new ArrayList<>();
        File f = new File(StaticValue.allDelac+File.separator+"confDelac.conf");
        if(f.exists()){
            List<String> dic = Utils.readFile(StaticValue.allDelac+File.separator+"confDelac.conf");
            for(String line : dic ){
                String dicName = line.split(",")[0];
                if (dicName.endsWith("dic")) {
                    list.add(dicName);
                }
            }
            if(list.isEmpty()){
                throw new FileNotFoundException("dictonnary not found in "+StaticValue.allDelac);
            }
            return list;
        }
        else{
            throw new FileNotFoundException("Configuration file not found in "+StaticValue.allDelac);
        }
    }
    /***
     * This function get all dictionnaries in delac folder and return an Object [][] which contains all information(
     * POS, lemmaAll, lemma, fSTCode, sinSem, comment, wn_SinSet, lemmaId, dicFile)
     * @param alldelac if alldelac is true, the function takes all delas in delcs folder, else it takes dictionnary selected in configuration
     * @return List of lemma in Object[][] format
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static Object[][] getAllDelacFromDicToObject(boolean alldelac) throws FileNotFoundException, IOException{
        ArrayList<String> list= new ArrayList<>();
        if(alldelac){
            list = getDicDelacPath();
        }
        else{
            list = getDicConfigDelacPath();
        }
        Delac delac = new Delac();
        Field[] lf = delac.getClass().getDeclaredFields();
        int count =0;
        for(String dela:list){
            //String path = Utils.getValueXml("pathDelas")+"/"+dela;
            String path = StaticValue.allDelac+"//"+dela;
            ArrayList<String> readFile = Utils.readFile(path);
            for(String s:readFile){
                if(s.trim().length()>0)
                    count++;
            }
            StaticValue.dictionnary.add(dela);
        }
        
        Object[][] ob = new Object[count][lf.length];
        int k=0;
        int lemmaId=0;
        for(String dela:list){
            String pOs,lemmaAll,lemma,fSTCode,sinSem,comment,wn_SinSet;
            
            String dicFile=dela;
            String path = StaticValue.allDelac+"//"+dela;
            ArrayList<String> readFile = Utils.readFile(path);
            for(String s:readFile){
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < s.length(); i++) {
                    String str = String.valueOf(s.charAt(i));
                    if(str.matches("^[a-zA-Z0-9áàâäãåçéèêëíì=îïñóòôöõúùûüýÿæœÁÀÂÄÃÅÇÉÈÊËÍÌÎÏÑÓÒÔÖÕÚÙÛÜÝŸÆŒ._\\s-]+$||[$&+'*,:.;\\[?@#\\]/ |)_(-]")){
                        sb.append(s.charAt(i));
                    }
                }
                if (!sb.toString().isEmpty()) {
                    wn_SinSet="";
                    lemmaAll=getLemaAllDelac(s);
                    lemma=getLemaInLemaAllDelac(lemmaAll);
                    fSTCode = getFstCodeInDelac(s);
                    sinSem=getSynSemInDelac(s);
                    pOs = getPosInDelac(s);
                    comment = getCommentInDelas(s);
                    Delac tmp = new Delac(pOs, lemmaAll, lemma, fSTCode, sinSem, comment, wn_SinSet, lemmaId, dicFile);
                    delacToObject(ob, k, tmp);
                    k++;
                    lemmaId=lemmaId+1;
                }
            }
        }
        return ob;
    }
    
    /**
     * This function transform Delac to Object[][] to put an entry of delac into jtable
     * @param ob data in jtable
     * @param k position of entry
     * @param tmp delac entry
     */
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
    }
    /**
     * This function get lemma in All lema
     * @param text lemma All
     * @return lemma
     */
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
    /**
     * This function get lemma in entry delac
     * @param text entry delac
     * @return lemma all of delac
     */
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
    /**
     * This function get Fst Code in entry delac
     * @param text entry delac
     * @return Fst Code of delac
     */
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
    /**
     * This function get SynSem in entry delac
     * @param text entry delac
     * @return SynSem of delac
     */
    public static String getSynSemInDelac(String text){
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
        }catch(java.lang.StringIndexOutOfBoundsException e){
            return"";
        }
    }
    /**
     * This function get POS in entry delac
     * @param text entry delac
     * @return POS of delac
     */
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
    /**
     * This function get comment in entry delac
     * @param text entry delac
     * @return comment of delac
     */
    public static String getCommentInDelas(String text){
        try{
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
        }catch(java.lang.StringIndexOutOfBoundsException e){
            return"";
        }
    }
    /**
     * This function is used in Menu delac when you select an entry of delaf in JtableDlf and complete lemma of simple word in jTableFLX   
     * @param lema
     * @return 
     */
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
            if(k+1!=words.length)objFlx[k][5] = separator;
            else objFlx[k][5] = "";
            k++;
        }
        return objFlx;
    }
    /**
     * This function complete JTableDlf from delaf file  in snt_txt/dlf and transform all entries to Object[][]
     * @param result list of entries delaf 
     * @return list of entries to Object [][]
     */
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
            predictFlex[i][0] = ulaz;
            predictFlex[i][1] = lema;
            predictFlex[i][2] = Pos;
            predictFlex[i][3] = gramCat;
            i++;
            
        }
        return predictFlex;
    }
    /**
     * This is for JtablePredict. 
     * @param ret
     * @return 
     */
    public static Object[][] completeJTablePredict(List<String> ret) {
        Object[][] dataPredict = new Object[ret.size()][6];
        for (int k = 0; k < ret.size(); k++) {
            String[] token = ret.get(k).split(",");
            for (int l = 0; l < token.length; l++) {
                dataPredict[k][l] = token[l];
            }
        }
        return dataPredict;
    }
}
