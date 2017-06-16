/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import model.StaticValue;
import util.Utils;

/**
 *
 * @author Rojo Rabelisoa
 */
public class DelafHelper {
    public static Object[][] getAllDelafFromDelacToObject() throws FileNotFoundException, IOException{
        String path = StaticValue.text_sntAbsPath;
        ArrayList<String> readFile = Utils.readFile(path);
        int count=0;
        for(String s:readFile){
            count++;
        }
        Object[][] ob = new Object[count][4];
        int i=0;
        for(String s:readFile){
           ob[i][0]=getUlaz(s);
           ob[i][1]=getPOS(s);
           ob[i][2]=getLema(s);
           ob[i][3]=getGramCats(s);
           i=i+1;
        }
        return ob;
    }
    public static String getUlaz(String text){
        return text;
    }
    public static String getPOS(String text){
        StringBuilder sb = new StringBuilder();
        boolean begin=false;
        for(int i=0;i<text.length();i++){
            if(text.charAt(i)==':'){
                break;
            }
            if(text.charAt(i)=='.'){
                begin=true;
                i++;
            }
            if(begin){
                sb.append(text.charAt(i));
            }
        }
        return sb.toString();
    }
    public static String getLema(String text){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<text.length();i++){
            if(text.charAt(i)==','){
                break;
            }
            sb.append(text.charAt(i));
        }
        return sb.toString();
    }
    public static String getGramCats(String text){
        StringBuilder sb = new StringBuilder();
        boolean begin=false;
        for(int i=0;i<text.length();i++){
            if(text.charAt(i)==':'){
                begin=true;
                i++;
            }
            if(begin){
                sb.append(text.charAt(i));
            }
        }
        return sb.toString();
    }
}
