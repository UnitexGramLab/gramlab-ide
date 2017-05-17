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
            //String path = "/Users/rojo/Documents/LeXimir4UnitexRes/Delas/"+dela;
            String path = StaticValue.allDelas+"//"+dela;
            ArrayList<String> readFile = readFile(path);
            for(String s:readFile){
                count++;
            }
            StaticValue.dictionnary.add(dela);
        }
        
        Object[][] ob = new Object[count][lf.length];
        int i=0;
        int k=0;
        int dicId=0;
        for(String dela:list){
            String pOs;
            String lemma;
            String fSTCode;
            String sinSem;
            String comment = null;
            String lemmaInv;
            String wn_SinSet = null;
            int lemmaId=10;
            String dicFile=dela;
            //String path = Utils.getValueXml("pathDelas")+"/"+dela;
            //String path = "/Users/rojo/Documents/LeXimir4UnitexRes/Delas/"+dela;
            String path = StaticValue.allDelas+"//"+dela;
            ArrayList<String> readFile = readFile(path);
            for(String s:readFile){
                String[] token = s.split(",");
                lemma=Objects.requireNonNull(s.split(",")[0]);
                lemmaInv=Utils.reverseString(lemma);
                sinSem="+"+s.split(",")[1].split("//")[0];
                if (sinSem.contains("=")) {
                    fSTCode = sinSem.split("=")[sinSem.split("=").length-1];
                } else {
                    fSTCode = "PREF";
                }
                pOs = fSTCode.split("[^A-Z0-9]+|(?<=[A-Z])(?=[0-9])|(?<=[0-9])(?=[A-Z])")[0];
                if (s.contains("//")) {
                    comment = s.split("//")[1];
                }
                Delas tmp = new Delas(pOs, lemma, fSTCode, sinSem, comment, lemmaInv, wn_SinSet, lemmaId, dicFile, dicId);
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
                k++;
                lemmaId=lemmaId+10;
                
            }
            dicId++;
            i++;
        }
        return ob;
    }
    /**
     * This function read file from path file and return an ArrayList<String>
     * @param file path of file to open
     * @return
     * @throws IOException 
     */
    public static ArrayList<String> readFile(String file) throws IOException {
        ArrayList<String> tmp;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String ligne;
            tmp = new ArrayList<>();
            while((ligne = reader.readLine()) != null){			
                tmp.add(ligne);
            }
        }
        return tmp;
    }
}
