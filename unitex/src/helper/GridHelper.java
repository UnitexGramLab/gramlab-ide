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
    public static JTable getOpenEditorLadl() throws IOException{
        
        String[] entete = {"POS","Lemma","FSTCode","SinSem","Comment","Lemmalnv","WN_Sinset","LemmaID","DictFile","Id"};
        Object[][] data = DelasHelper.getAllDelasFromDicToObject();
        GridModel tm=new GridModel(entete,data);
        JTable tableau = new JTable(tm.getDonnees(), tm.getEntete());
        return tableau;
    }
    public static JTable getOpenEditorLadl(DefaultTableModel model) throws IOException{
        
        String[] entete = {"POS","Lemma","FSTCode","SynSem","Comment","Lemmalnv","WN_SynSet","LemmaID","DictFile","Id"};
        Object[][] data = DelasHelper.getAllDelasFromDicToObject();
        GridModel tm=new GridModel(entete,data);
        tm.insererObjet(data);
        JTable tableau = new JTable(tm.getDonnees(), tm.getEntete());
        return tableau;
    }
    public static DefaultTableModel getOpenEditor() throws IOException{
        
        String[] entete = {"POS","Lemma","FSTCode","SynSem","Comment","Lemmalnv","WN_SynSet","LemmaID","DictFile","Id"};
        Object[][] data = DelasHelper.getAllDelasFromDicToObject();
        return new DefaultTableModel(data,entete);
    }
}
