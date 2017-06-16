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
import model.GridModel;

/**
 *
 * @author Rojo Rabelisoa
 */
public class GridHelper {
    //Helper for search
    public static JTable getOpenEditorLadl() throws IOException{
        
        String[] entete = {"POS","Lemma","FSTCode","SinSem","Comment","Lemmalnv","WN_Sinset","LemmaID","DictFile","Id"};
        Object[][] data = DelasHelper.getAllDelasFromDicToObject();
        GridModel tm=new GridModel(entete,data);
        JTable tableau = new JTable(tm.getDonnees(), tm.getEntete());
        return tableau;
    }
    //instance default grid
    public static DefaultTableModel getOpenEditorforDelas() throws IOException{
        
        String[] entete = {"POS","Lemma","FSTCode","SynSem","Comment","Lemmalnv","WN_SynSet","LemmaID","DictFile","Id"};
        Object[][] data = DelasHelper.getAllDelasFromDicToObject();
        return new DefaultTableModel(data,entete);
    }
    // tableau pour afficher après recherche
    public static JTable getOpenEditorLadlforDelac() throws IOException{
        
        String[] entete = {"POS","Comp.Lema all","Comp.Lema","FSTCode","SynSem","Comment","WN_SynSet","LemmaID","DictFile","DicId"};
        Object[][] data = DelacHelper.getAllDelacFromDicToObject();
        GridModel tm=new GridModel(entete,data);
        JTable tableau = new JTable(tm.getDonnees(), tm.getEntete());
        return tableau;
    }
    //tableau par defaut
    public static DefaultTableModel getOpenEditorforDelac() throws IOException{
        
        String[] entete = {"POS","Comp.Lema all","Comp.Lema","FSTCode","SynSem","Comment","WN_SynSet","LemmaID","DictFile","DicId"};
        Object[][] data = DelacHelper.getAllDelacFromDicToObject();
        return new DefaultTableModel(data,entete);
    }
    //tableau par defaut
    public static DefaultTableModel getDelafInDelacForDelac() throws IOException{
        
        String[] entete = {"Ulaz","POS","lema","GramCats"};
        Object[][] data = DelafHelper.getAllDelafFromDelacToObject();
        return new DefaultTableModel(data,entete);
    }
    // tableau pour afficher les données du tableau jTableFLX
    public static DefaultTableModel getDataforjTableFlx(String lema) throws IOException{
        
        String[] entete = {"RB", "Form", "Lema", "FST Code", "GramCat", "Separator"};
        Object[][] data = DelacHelper.completeJTableFLX(lema);
        return new DefaultTableModel(data,entete);
    }
    // tableau pour afficher les données du tableau jTableDlf
    public static DefaultTableModel getDataforjTableDlf(List<String> dlf) throws IOException{
        
        Object[][] data = DelacHelper.completeJTableDlf(dlf);
        String[] entete = {"ulaz", "lema", "Pos", "GramCat"};
        return new DefaultTableModel(data,entete);
    }
    // tableau pour afficher les données du tableau jTableDlf
    public static DefaultTableModel getDataforjTablePredict(List<String> predict) throws IOException{
        
        Object[][] data = DelacHelper.completeJTablePredict(predict);
        String[] entete = {"words", "FLX", "Rule"};
        return new DefaultTableModel(data,entete);
    }
}
