/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.io.IOException;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.GridModel;

/**
 *
 * @author rojo
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
}
