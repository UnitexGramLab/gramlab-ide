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

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.frames.InternalFrameManager;
import fr.umlv.unitex.frames.UnitexInternalFrameManager;
import fr.umlv.unitex.io.Encoding;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.leximir.helper.DelacHelper;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.umlv.unitex.leximir.model.DictionaryPath;

/**
 * @author Rojo Rabelisoa
 * @author Anas Ait cheikh
 */
public class Utils {

    /**
     * This function read file from path file and return an ArrayList<String>
     *
     * @param file path of file to open
     * @return
     * @throws IOException
     */
    public static ArrayList<String> readFile(final String file) throws IOException {
        final ArrayList<String> tmp = new ArrayList<>();
        final ToDo toDo = new ToDo() {
            @Override
            public void toDo(boolean success) {
                InputStreamReader inputStreamReader = Encoding.getInputStreamReader(new File(file));
                try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                    String ligne;
                    while ((ligne = reader.readLine()) != null) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < ligne.length(); i++) {
                            sb.append(ligne.charAt(i));
                        }
                        if (!sb.toString().isEmpty()) {
                            tmp.add(sb.toString());
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        if (null == Encoding.getEncoding(new File(file))) {
            GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
                    .newTranscodeOneFileDialog(new File(file), toDo);
        } else {
            toDo.toDo(true);
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

    /**
     * This function export the JTable into a csv file
     *
     * @param dicPos is the list of the element to export
     * @param filename the name of the csv file
     */
    public static void exportJtableToCsv(List<Object[]> dicPos, String filename) throws IOException, FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        for (Object[] tab : dicPos) {
            for (Object obj : tab) {
                sb.append(obj.toString() + ";");
            }
            sb.append("\n");
        }
        boolean isDone = false;
        try {
            FileWriter fileWriter = new FileWriter(filename, false);
            fileWriter.write(sb.toString());
            fileWriter.close();
            isDone = true;
        } finally {
            if (isDone) {
                GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
                        .newCsvOpener(filename);
            }
        }
    }

    public static void exportStatAllToCsv(Map<String, Object[]> simSem, String filename) throws IOException, FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        sb.append("POS;SynSem;Count\n");
        Set<String> keysets = simSem.keySet();
        for (String key : keysets) {
            Object[] objArr = simSem.get(key);
            i = 0;
            for (Object obj : objArr) {
                if (i != 1) {
                    sb.append(obj.toString());
                    if (i < 3) {
                        sb.append(";");
                    }
                }
                i++;
            }
            sb.append("\n");
        }
        boolean isDone = false;
        try {
            FileWriter fileWriter = new FileWriter(filename, false);
            fileWriter.write(sb.toString());
            fileWriter.close();
            isDone = true;
        } finally {
            if (isDone) {
                GlobalProjectManager.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
                        .newCsvOpener(filename);

            }
        }
    }

    public static Object[] delasToObject(String lemma, String fstCode, String SynSem, String comment, String Dicname, int valueSelected) {

        String lemmas = lemma;
        String fSTCode = fstCode;
        String pOs = fSTCode.replaceAll("\\d", "");
        String comments = comment;
        String lemmaInv = Utils.reverseString(lemma);
        int lemmaId = valueSelected + 1;
        String dicFile = Dicname;
        int dicId = 0;
        return new Object[]{pOs, lemmas, fSTCode, SynSem, comments, lemmaInv, lemmaId, dicFile, dicId};
    }

    public static Object[] delacToObject(String lemma, String fstCode, String synSem, String comment, String Dicname) {
        String line = lemma + "," + fstCode + synSem + File.separator + comment;
        String pOs = DelacHelper.getPosInDelac(line);
        String lemaAll = lemma;
        String lema = DelacHelper.getLemaInLemaAllDelac(lemaAll);
        String fSTCode = fstCode;
        String SynSem = synSem;
        String comments = comment;
        int lemmaId = 10;
        String dicFile = Dicname;
        int dicId = 0;
        return new Object[]{pOs, lemaAll, lema, fSTCode, SynSem, comments, lemmaId, dicFile, dicId};
    }

    /**
     * this function open terminal and run command
     *
     * @param command
     * @throws IOException
     */
    public static void runCommandTerminal(String[] command) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process p = pb.start();
        while (isProcessAlive(p)) {
            continue;
        }
    }

    public static boolean isProcessAlive(Process p) {
    try {
        p.exitValue();
        return false;
    } catch(IllegalThreadStateException e) {
        return true;
    }
}
    
    /**
     * This function inflect delas with the fst code which is give in parameter
     *
     * @param lemma delas entry
     * @param fst Fst code
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void InflectDelas(String lemma, String fst) throws IOException, FileNotFoundException {
        System.out.println("infect : " + DictionaryPath.inflectionPath + fst + ".grf");
        File delaffolder = new File(DictionaryPath.delafPath);
        if (!delaffolder.exists()) {
            delaffolder.mkdir();
        }
        if (new File(DictionaryPath.inflectionPath + fst + ".grf").exists()) {
            BufferedWriter bfw;
            bfw = new BufferedWriter(new FileWriter(DictionaryPath.allDelas +File.separator+ "DelasTmp.dic"));
            bfw.write(lemma);
            bfw.write(",");
            bfw.write(fst);
            bfw.close();

            String[] command = {
                DictionaryPath.unitexLoggerPath, "MultiFlex",
                DictionaryPath.allDelas +File.separator+ "DelasTmp.dic",
                "-o", DictionaryPath.delafPath +File.separator+ "DelafTmp.dic",
                "-a", DictionaryPath.alphabetPath,
                "-d", DictionaryPath.inflectionPath
            };

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process p = pb.start();
            while (isProcessAlive(p)) {
                continue;
            }
            new File(DictionaryPath.allDelas + File.separator+ "DelasTmp.dic").delete();

            GlobalProjectManager.search(null).getFrameManagerAs(InternalFrameManager.class)
                    .newDelaFrame(new File(DictionaryPath.delafPath +File.separator+ "DelafTmp.dic"));

        } else {
            throw new FileNotFoundException(" FST Graph doesn't exist");
        }

    }

    /**
     * This function generate delaf from an entry of delas(c) into snt_txt/dlf
     *
     * @param value entry of delas(c)
     * @throws IOException
     * @throws HeadlessException
     */
    public static void generateDelaf(String value) throws IOException, HeadlessException {
        String tempPath = DictionaryPath.delafTmpPathDelac;
        try (BufferedWriter bfw = new BufferedWriter(new FileWriter(tempPath))) {
            bfw.write(value + ".");
        }
        String snt = tempPath.replace(".txt", ".snt");
        try (BufferedWriter bfw = new BufferedWriter(new FileWriter(snt))) {
            bfw.write(value + ".");
        }
        String[] cmd1 = {DictionaryPath.unitexLoggerPath, "Normalize", DictionaryPath.delafTmpAbsPathDelac + "text.txt"};
        String[] cmd2 = {DictionaryPath.unitexLoggerPath, "Tokenize", DictionaryPath.delafTmpAbsPathDelac + "text.snt", "-a", DictionaryPath.alphabetPath};
        List<String> allDela = new ArrayList<>();
        File folder = new File(DictionaryPath.allDelafAbsPath);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (file.getName().endsWith(".bin")) {
                    allDela.add(DictionaryPath.allDelafAbsPath + file.getName());
                }
            }
        }
        String[] cmdTmp = {DictionaryPath.unitexLoggerPath, "Dico", "-t", DictionaryPath.delafTmpAbsPathDelac + "text.snt", "-a", DictionaryPath.alphabetPath};
        String[] cmd3 = new String[cmdTmp.length + allDela.size()];
        System.arraycopy(cmdTmp, 0, cmd3, 0, cmdTmp.length);
        int indiceCmd = cmdTmp.length;
        for (String alldela : allDela) {
            cmd3[indiceCmd] = alldela;
            indiceCmd++;
        }
        Utils.runCommandTerminal(cmd1);
        Utils.runCommandTerminal(cmd2);
        Utils.runCommandTerminal(cmd3);
    }
}
