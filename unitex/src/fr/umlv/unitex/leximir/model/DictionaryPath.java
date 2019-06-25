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
package fr.umlv.unitex.leximir.model;
import java.util.ArrayList;

import fr.umlv.unitex.config.*;
import java.io.File;

/**
 * @author Rojo Rabelisoa
 */
public class DictionaryPath {
    public static ArrayList<String> dictionary= new ArrayList<>();
    public static final String statisticsDelasPath = Config.getUserDir().toString()+File.separator+Config.getCurrentLanguage()+File.separator+"Dela"+File.separator+"Delas"+File.separator+"Statistics"+File.separator;
    public static final String statisticsDelacPath = Config.getUserDir().toString()+File.separator+Config.getCurrentLanguage()+File.separator+"Dela"+File.separator+"Delac"+File.separator+"Statistics"+File.separator;   
    public static final String allDelafAbsPath = Config.getUserDir().toString()+File.separator+Config.getCurrentLanguage()+File.separator+"Dela"+File.separator;
    public static final String delafTmpPathDelac = Config.getUserDir().toString()+File.separator+Config.getCurrentLanguage()+File.separator+"text.txt";
    public static final String delafTmpAbsPathDelac = Config.getUserDir().toString()+File.separator+Config.getCurrentLanguage()+File.separator;
    public static final String text_sntAbsPath = Config.getUserDir().toString()+File.separator+Config.getCurrentLanguage()+File.separator+"text_snt"+File.separator+"dlf";
    public static final String allDela = Config.getUserDir().toString()+File.separator+Config.getCurrentLanguage();
    public static final String unitexLoggerPath = Config.getUnitexToolLogger().getAbsolutePath();
    public static final String alphabetPath = ConfigManager.getManager().getAlphabet(null).getAbsolutePath();
    public static final String inflectionPath = Config.getUserDir().toString()+File.separator+Config.getCurrentLanguage()+File.separator+"Inflection"+File.separator;
    public static final String allDelas = Config.getUserDir().toString()+File.separator+Config.getCurrentLanguage()+File.separator+"Dela"+File.separator+"Delas"+File.separator;
    public static final String allDelac = Config.getUserDir().toString()+File.separator+Config.getCurrentLanguage()+File.separator+"Dela"+File.separator+"Delac"+File.separator;
    public static final String statisticsTmpPath = "statisticsTmp.csv";
    public static final String delafPath = Config.getUserDir().toString()+File.separator+Config.getCurrentLanguage()+File.separator+"Dela"+File.separator+"Delaf";
}