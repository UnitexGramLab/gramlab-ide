/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.io.IOException;
import java.util.List;
import javax.swing.JTable;
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
    public static DefaultTableModel getOpenEditorforDelas(boolean alldelas) throws IOException{
        
        String[] entete = {"POS","Lemma","FST Code","SynSem","Comment","Lemma lnv","WN_SynSet","Lemma ID","Dict. File"};
        Object[][] data = DelasHelper.getAllDelasFromDicToObject(alldelas);
        return new DefaultTableModel(data,entete);
    }
    /**
     * This function complete Jtable of Delac dictionary
     * @return 
     * @throws IOException 
     */
    public static DefaultTableModel getOpenEditorforDelac(boolean alldelac) throws IOException{
        
        String[] entete = {"POS","Comp.Lemma all","Comp.Lemma","FST Code","SynSem","Comment","WN_SynSet","Lemma ID","Dict. File"};
        Object[][] data = DelacHelper.getAllDelacFromDicToObject(alldelac);
        return new DefaultTableModel(data,entete);
    }
    /**
     * This function complete Jtable of Delaf with Fst Code
     * @return 
     * @throws IOException 
     */
    public static DefaultTableModel getDelafInDelacForDelac() throws IOException{
        
        String[] entete = {"delaf entry","POS","lemma","Gram cats","FST graph"};
        Object[][] data = DelafHelper.getAllDelafFromDelacToObject();
        return new DefaultTableModel(data,entete);
    }
    /**
     * This function complete  jTableFLX in Menu Delac to manage simple word in compound words
     * @return 
     * @throws IOException 
     */
    public static DefaultTableModel getDataforjTableFlx(String lema) throws IOException{
        
        String[] entete = {"RB", "Form", "Lemma", "FST code", "Gram cat", "Separator"};
        Object[][] data = DelacHelper.completeJTableFLX(lema);
        return new DefaultTableModel(data,entete);
    }
    /**
     * This function complete jtable of delaf
     * @return 
     * @throws IOException 
     */
    public static DefaultTableModel getDataforjTableDlf(List<String> dlf) throws IOException{
        
        Object[][] data = DelacHelper.completeJTableDlf(dlf);
        String[] entete = {"delaf entry", "lemma", "Pos", "Gram cat"};
        return new DefaultTableModel(data,entete);
    }
    /**
     * This function is for prediction
     * @return 
     * @throws IOException 
     */
    // tableau pour afficher les données du tableau jTableDlf
    public static DefaultTableModel getDataforjTablePredict(List<String> predict) throws IOException{
        
        Object[][] data = DelacHelper.completeJTablePredict(predict);
        String[] entete = {"words", "FST", "Rule","Spec/Gen","ID RULE","SynSem"};
        return new DefaultTableModel(data,entete);
    }
    /*// tableau pour afficher les données du tableau Strategie
    public static DefaultTableModel getDataforStrategy(List<String> words,String strategy) throws IOException{
        
        Object[][] data = StrategieHelper.completeJTableStrategie(words,strategy);
        String[] entete = {"select", "Clemma", "CFLX", "Word NO", "Predict Id", "Rule Id", "RulePart Id", "SynSem"};
        return new DefaultTableModel(data,entete);
    }*/
}
