/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

/**
 *
 * @author rojo
 */
public class EditorLadlHelper {
    private static String text;
    public static void editText(String text){
        EditorLadlHelper.text = text;
    }
    public static String getText(){
        return text;
    }
}
