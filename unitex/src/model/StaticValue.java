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
 * @author rojo
 */
public class StaticValue {
	public static ArrayList<String> dictionnary= new ArrayList<>();
    public static final String unitexLoggerPath = "//Users//rojo//Unitex-GramLab-3.1//App//UnitexToolLogger";
    public static final String alphabetPath = Config.getUserDir().toString().replace("/", "//")+"//"+Config.getCurrentLanguage()+"//"+"Alphabet.txt";
    public static final String inflectionPath = Config.getUserDir().toString().replace("/", "//")+"//"+Config.getCurrentLanguage()+"//"+"Inflection//";
    public static final String allDelas = Config.getUserDir()+"/"+Config.getCurrentLanguage()+"/"+"Dela/Delas/";
    public static final String allDelac = Config.getUserDir()+"/"+Config.getCurrentLanguage()+"/"+"Dela/Delac/";
    public static final String statisticsTmpPath = "statisticsTmp.xls";
    public static final String delasTmpPath = "DelasTmp.dic";
    public static final String delafTmpPath = "DelafTmp.dic";
    
}
