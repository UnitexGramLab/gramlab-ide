/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;

import fr.umlv.unitex.config.Config;

/**
 *
 * @author Rojo Rabelisoa
 */
public class StaticValue {
	public static ArrayList<String> dictionnary= new ArrayList<>();

    public static final String allDelafAbsPath = Config.getUnitexDir().toString().replace("/", "//")+"//"+Config.getCurrentLanguage()+"//Dela//";

    public static final String delafTmpPathDelac = Config.getUserDir().toString().replace("/", "//")+"//"+Config.getCurrentLanguage()+"//"+"text.txt";
    public static final String delafTmpAbsPathDelac = Config.getUserDir().toString().replace("/", "//")+"//"+Config.getCurrentLanguage()+"//";
    public static final String text_sntAbsPath = Config.getUserDir().toString().replace("/", "//")+"//"+Config.getCurrentLanguage()+"//"+"text_snt//dlf";
    
    public static final String ruleCompoundsPath = Config.getUserDir().toString().replace("/", "//")+"//"+Config.getCurrentLanguage()+"//"+"CompoundsStrat7_Ver5-3.xml";
    public static final String unitexLoggerPath = Config.getUnitexToolLogger().getAbsolutePath();
    public static final String alphabetPath = Config.getUserDir().toString().replace("/", "//")+"//"+Config.getCurrentLanguage()+"//"+"Alphabet.txt";
    public static final String inflectionPath = Config.getUserDir().toString().replace("/", "//")+"//"+Config.getCurrentLanguage()+"//"+"Inflection//";
    public static final String allDelas = Config.getUserDir()+"/"+Config.getCurrentLanguage()+"/"+"Dela/Delas/";
    public static final String allDelac = Config.getUserDir()+"/"+Config.getCurrentLanguage()+"/"+"Dela/Delac/";
    public static final String statisticsTmpPath = "statisticsTmp.xls";
    public static final String delasTmpPath = "DelasTmp.dic";
    public static final String delafTmpPath = "DelafTmp.dic";
}
