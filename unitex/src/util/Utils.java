/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import helper.DelacHelper;
import helper.DelasHelper;
import java.awt.Desktop;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import leximir.delac.menu.MenuDelac;
import model.StaticValue;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author Rojo Rabelisoa
 */
public class Utils {
    /**
     * This function read file from path file and return an ArrayList<String>
     * @param file path of file to open
     * @return
     * @throws IOException 
     */
    public static ArrayList<String> readFile(String file) throws IOException {
        
        ArrayList<String> tmp;
        /*InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
        FileInputStream fs = new FileInputStream(new File(file));
        try (CRLFTerminatedReader reader = new CRLFTerminatedReader(fs)) {
            System.err.println(" e : "+file);
            String ligne;
            tmp = new ArrayList<>();
            while((ligne = reader.readLine()) != null){	
                System.err.println("err : "+ligne);
                tmp.add(ligne);
            }
        }
         */
        
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String ligne;
            tmp = new ArrayList<>();
            while((ligne = reader.readLine()) != null){
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < ligne.length(); i++) {
                    String str = String.valueOf(ligne.charAt(i));
                    if(str.matches("^[a-zA-Z0-9áàâäãåçéèêëíì=îïñóòôöõúùûüýÿæœÁÀÂÄÃÅÇÉÈÊËÍÌÎÏÑÓÒÔÖÕÚÙÛÜÝŸÆŒ._\\s-]+$||[$&+'*,:.;\\[?@#\\]/ |)_(-]")){
                        sb.append(ligne.charAt(i));
                    }
                }
                if (!sb.toString().isEmpty()) {
                    tmp.add(sb.toString());
                }
            }
        }
        
        return tmp;
    }
    /**
     * This function causes a String to be inverted from right to left
     *
     * @param text is the String to reverse
     * @return
     */
    public static String reverseString(String text) {
        return new StringBuffer(text).reverse().toString();
    }
    public static Map<String, Object[]> putPosDicGridInExcel(Map<String, HashMap<String, String>> data) {
        Map<String, Object[]> datas = new HashMap<>();
        datas.put("1", new Object[]{"Dic", "POS", "Number"});
        int inc = 2;
        for (Map.Entry<String, HashMap<String, String>> f : data.entrySet()) {
            String key = f.getKey();
            for (Map.Entry<String, String> p : f.getValue().entrySet()) {
                datas.put(String.valueOf(inc), new Object[]{key, p.getKey(), p.getValue()});
                inc++;
            }
        }
        return datas;
    }
    /**
     * This function is for All Button. Complete cell in excel file.
     * @param data 
     * @return 
     */
    public static Map<String, Object[]> putPosGridInExcel(Map<String,  String> data) {
        Map<String, Object[]> datas = new HashMap<>();
        datas.put("1", new Object[]{"POS", "Number"});
        int inc = 2;
        for (Map.Entry<String,  String> f : data.entrySet()) {
            datas.put(String.valueOf(inc), new Object[]{f.getKey(), f.getValue()});
            inc++;
        }
        return datas;
    }

    public static void exportJtableToExcel( Map<String, Object[]> dicPos,Map<String, Object[]> pos, String filename) throws IOException, FileNotFoundException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Dic Pos Stat");
        Set<String> keyset = dicPos.keySet();
        int rownum = 0;
        for (String key : keyset) {
            Row row = sheet.createRow(rownum++);
            Object[] objArr = dicPos.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if (obj instanceof Date) {
                    cell.setCellValue((Date) obj);
                } else if (obj instanceof Boolean) {
                    cell.setCellValue((Boolean) obj);
                } else if (obj instanceof String) {
                    cell.setCellValue((String) obj);
                } else if (obj instanceof Double) {
                    cell.setCellValue((Double) obj);
                }
            }
        }
        HSSFSheet sheetPos = workbook.createSheet("Pos stat");
        Set<String> keypos = pos.keySet();
        int rowPosnum = 0;
        for (String key : keypos) {
            Row row = sheetPos.createRow(rowPosnum++);
            Object[] objArr = pos.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if (obj instanceof Date) {
                    cell.setCellValue((Date) obj);
                } else if (obj instanceof Boolean) {
                    cell.setCellValue((Boolean) obj);
                } else if (obj instanceof String) {
                    cell.setCellValue((String) obj);
                } else if (obj instanceof Double) {
                    cell.setCellValue((Double) obj);
                }
            }
        }
        boolean isDone = false;
        try (FileOutputStream out = new FileOutputStream(new File(filename))) {
            workbook.write(out);
            isDone = true;
        } finally {
            if (isDone) {
                Desktop.getDesktop().open(new File(filename));
                System.out.println("Excel written successfully..");
            }
        }
    }
    public static void exportJtableDelacToExcel( Map<String, Object[]> dicPos, String filename) throws IOException, FileNotFoundException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Dic Pos Stat");
        Set<String> keyset = dicPos.keySet();
        int rownum = 0;
        for (String key : keyset) {
            Row row = sheet.createRow(rownum++);
            Object[] objArr = dicPos.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if (obj instanceof Date) {
                    cell.setCellValue((Date) obj);
                } else if (obj instanceof Boolean) {
                    cell.setCellValue((Boolean) obj);
                } else if (obj instanceof String) {
                    cell.setCellValue((String) obj);
                } else if (obj instanceof Double) {
                    cell.setCellValue((Double) obj);
                }
            }
        }
        
        boolean isDone = false;
        try (FileOutputStream out = new FileOutputStream(new File(filename))) {
            workbook.write(out);
            isDone = true;
        } finally {
            if (isDone) {
                Desktop.getDesktop().open(new File(filename));
                System.out.println("Excel written successfully..");
            }
        }
    }
    
    
    public static void exportStatAllToExcel( Map<String, Object[]> simSem1,Map<String, Object[]> simSem2, String filename) throws IOException, FileNotFoundException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("SinSem1");
        Set<String> keyset = simSem1.keySet();
        int rownum = 0;
        for (String key : keyset) {
            Row row = sheet.createRow(rownum++);
            Object[] objArr = simSem1.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if (obj instanceof Date) {
                    cell.setCellValue((Date) obj);
                } else if (obj instanceof Boolean) {
                    cell.setCellValue((Boolean) obj);
                } else if (obj instanceof String) {
                    cell.setCellValue((String) obj);
                } else if (obj instanceof Double) {
                    cell.setCellValue((Double) obj);
                }
            }
        }
        HSSFSheet sheets = workbook.createSheet("SimSem2");
        Object[] objTitle =  new Object[]{"POS", "SinSem","Category", "Number"};
        Row rowTitle = sheets.createRow(0);
        Cell cellTitle = rowTitle.createCell(0);
        cellTitle.setCellValue((String)objTitle[0]);
        cellTitle = rowTitle.createCell(1);
        cellTitle.setCellValue((String)objTitle[1]);
        cellTitle = rowTitle.createCell(2);
        cellTitle.setCellValue((String)objTitle[2]);
        cellTitle = rowTitle.createCell(3);
        cellTitle.setCellValue((String)objTitle[3]);
        Set<String> keysets = simSem2.keySet();
        int rownums = 1;
        for (String key : keysets) {
            Row row = sheets.createRow(rownums++);
            Object[] objArr = simSem2.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if (obj instanceof Date) {
                    cell.setCellValue((Date) obj);
                } else if (obj instanceof Boolean) {
                    cell.setCellValue((Boolean) obj);
                } else if (obj instanceof String) {
                    cell.setCellValue((String) obj);
                } else if (obj instanceof Double) {
                    cell.setCellValue((Double) obj);
                }
            }
        }
        boolean isDone = false;
        try (FileOutputStream out = new FileOutputStream(new File(filename))) {
            workbook.write(out);
            isDone = true;
        } finally {
            if (isDone) {
                Desktop.getDesktop().open(new File(filename));
                System.out.println("Excel written successfully..");
            }
        }
    }
    
    

    public static Object[] delasToObject(String lemma, String fstCode, String sinSem,String comment, String Dicname,int valueSelected) throws ArrayIndexOutOfBoundsException {
        //sinSem = sinSem+"="+fstCode;
        String line = lemma+","+fstCode+sinSem+"//"+comment;
        String pOs=DelasHelper.getPosInDelas(line);
        String lemmas = lemma;
        String fSTCode = fstCode;
        String comments = comment;
        String lemmaInv = Utils.reverseString(lemma);
        String wn_SinSet = "";
        int lemmaId = valueSelected+1;
        String dicFile = Dicname;
        int dicId = 0;
        return new Object[]{pOs, lemmas, fSTCode, sinSem, comments, lemmaInv, wn_SinSet, lemmaId, dicFile, dicId};
    }
    
    public static Object[] delacToObject(String lemma, String fstCode,String synSem, String comment, String Dicname) throws ArrayIndexOutOfBoundsException {
        String line = lemma+","+fstCode+synSem+"//"+comment;
        String pOs = DelacHelper.getPosInDelac(line);
        String lemaAll = lemma;
        String lema = DelacHelper.getLemaInLemaAllDelac(lemaAll);
        String fSTCode = fstCode;
        String sinSem = synSem;
        String comments = comment;
        String wn_SinSet = "";
        int lemmaId = 10;
        String dicFile = Dicname;
        int dicId = 0;
        return new Object[]{pOs, lemaAll, lema, fSTCode, sinSem, comments, wn_SinSet, lemmaId, dicFile, dicId};
    }

    public static String getValueXml(String key) throws IOException, FileNotFoundException, IllegalArgumentException {
        File file = new File("configuration.xml");
        Properties properties;
        try (FileInputStream fileInput = new FileInputStream(file)) {
            properties = new Properties();
            properties.loadFromXML(fileInput);
        }
        Enumeration enuKeys = properties.keys();
        while (enuKeys.hasMoreElements()) {
            String keys = (String) enuKeys.nextElement();
            if (keys.equals(key)) {
                return properties.getProperty(keys);
            }
        }
        throw new IllegalArgumentException("Key not found in path");
    }
    /**
     * this fonction open terminal and run command
     * @param command 
     * @throws IOException 
     */
    public static void runCommandTerminal(String[] command) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process p =pb.start();
        while (p.isAlive()) {
        }
    }
    /**
     * This function inflect delas with the fst code which is give in parameter
     * @param lemma delas entry
     * @param fst Fst code
     * @throws IOException
     * @throws FileNotFoundException 
     */
    public static void InflectDelas(String lemma, String fst) throws IOException,FileNotFoundException {
        System.out.println("infect : " + StaticValue.inflectionPath + fst + ".grf");
        if (new File(StaticValue.inflectionPath + fst + ".grf").exists()) {
            BufferedWriter bfw;
            bfw = new BufferedWriter(new FileWriter("DelasTmp.dic"));
            bfw.write(lemma);
            bfw.write(",");
            bfw.write(fst);
            bfw.close();
            String[] command = {
                StaticValue.unitexLoggerPath, "MultiFlex",
                StaticValue.delasTmpPath,
                "-o", StaticValue.delafTmpPath,
                "-a", StaticValue.alphabetPath,
                "-d", StaticValue.inflectionPath
            };
            
            //for(String s:command)System.out.print(s+" ");
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process p = pb.start();
            while (p.isAlive()) {
            }

            Desktop.getDesktop().open(new File(StaticValue.delafTmpPath));
        }
        else{
            throw new FileNotFoundException(" FST Graph doesn't exist");
        }

    }
     /**
      * This function generate delaf from an entry of delas(c) into snt_txt/dlf
      * @param value entry of delas(c)
      * @throws IOException
      * @throws HeadlessException 
      */
   public static void generateDelaf(String value) throws IOException, HeadlessException {
       String tempPath = StaticValue.delafTmpPathDelac; 
       try (BufferedWriter bfw = new BufferedWriter(new FileWriter(tempPath))) {
            bfw.write(value+".");
        } 
        String snt = tempPath.replace(".txt", ".snt");
        try (BufferedWriter bfw = new BufferedWriter(new FileWriter(snt))) {
            bfw.write(value+".");
        }
        String[] cmd1 = {StaticValue.unitexLoggerPath, "Normalize", StaticValue.delafTmpAbsPathDelac+"text.txt" };
        String[] cmd2 = {StaticValue.unitexLoggerPath,"Tokenize",StaticValue.delafTmpAbsPathDelac+"text.snt" ,"-a",StaticValue.alphabetPath};
        List<String> allDela=new ArrayList<>();
        File folder = new File(StaticValue.allDelafAbsPath);
        File[] listOfFiles = folder.listFiles();
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                if (listOfFile.getName().endsWith(".bin")) {
                    allDela.add(StaticValue.allDelafAbsPath+listOfFile.getName());
                }
            } 
        }
        String[] cmdTmp ={StaticValue.unitexLoggerPath, "Dico","-t",StaticValue.delafTmpAbsPathDelac+"text.snt","-a",StaticValue.alphabetPath};
        String[] cmd3 = new String[cmdTmp.length+allDela.size()];
        System.arraycopy(cmdTmp, 0, cmd3, 0, cmdTmp.length);
        int indiceCmd=cmdTmp.length;
        for (String alldela : allDela) {
            cmd3[indiceCmd] = alldela;
            indiceCmd++;
        }
        Utils.runCommandTerminal(cmd1);
        Utils.runCommandTerminal(cmd2);
        Utils.runCommandTerminal(cmd3);
    }

   
}
