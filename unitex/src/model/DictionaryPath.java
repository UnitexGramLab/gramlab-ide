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
package model;
import java.util.ArrayList;

import fr.umlv.unitex.config.*;

/**
 *
 * @author Rojo Rabelisoa
 */
public class DictionaryPath {
    public static ArrayList<String> dictionnary= new ArrayList<>();
    public static final String allDelafAbsPath = Config.getUserDir().toString().replace("/", "//")+"//"+ConfigManager.getManager().getAlphabet(null)+"//Dela//";

    public static final String delafTmpPathDelac = Config.getUserDir().toString().replace("/", "//")+"//"+ConfigManager.getManager().getAlphabet(null)+"//"+"text.txt";
    public static final String delafTmpAbsPathDelac = Config.getUserDir().toString().replace("/", "//")+"//"+ConfigManager.getManager().getAlphabet(null)+"//";
    public static final String text_sntAbsPath = Config.getUserDir().toString().replace("/", "//")+"//"+ConfigManager.getManager().getAlphabet(null)+"//"+"text_snt//dlf";
    public static final String allDela = Config.getUserDir().toString().replace("/", "//")+"//"+ConfigManager.getManager().getAlphabet(null);
    public static final String ruleCompoundsPath = Config.getUserDir().toString().replace("/", "//")+"//"+ConfigManager.getManager().getAlphabet(null)+"//"+"CompoundsStrat7_Ver5-3.xml";
    public static final String unitexLoggerPath = Config.getUnitexToolLogger().getAbsolutePath();
    public static final String alphabetPath = Config.getUserDir().toString().replace("/", "//")+"//"+ConfigManager.getManager().getAlphabet(null)+"//"+"Alphabet.txt";
    public static final String inflectionPath = Config.getUserDir().toString().replace("/", "//")+"//"+ConfigManager.getManager().getAlphabet(null)+"//"+"Inflection//";
    public static final String allDelas = Config.getUserDir()+"/"+ConfigManager.getManager().getAlphabet(null)+"/"+"Dela/Delas/";
    public static final String allDelac = Config.getUserDir()+"/"+ConfigManager.getManager().getAlphabet(null)+"/"+"Dela/Delac/";
    public static final String statisticsTmpPath = "statisticsTmp.xls";
    public static final String delasTmpPath = Config.getUnitexDir()+"//App//DelasTmp.dic";
    public static final String delafTmpPath = Config.getUnitexDir()+"//App//DelafTmp.dic";
    
}
