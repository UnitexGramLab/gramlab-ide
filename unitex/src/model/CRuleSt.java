/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author rojo
 */
public class CRuleSt {
    public static final String tagGen = "RuleGenCond";
    public static final String tagSpec= "RuleSpecCond";
    public static final String anyPOS ="MOT";
    public static final String notInDic = "!DIC";
    public static final String startWithUpperCase = "$PRE";
    
    public static String[] gramCats = new String[]{"Case","Num","Gen","Anim","Det"};
    public static String[] specTransfFLX(String uslov){
        return "$FLXN".equals(uslov)?"FLX,N679,N681,N683,N685,N687,N613a,N743".split(","):null;
    }
}
