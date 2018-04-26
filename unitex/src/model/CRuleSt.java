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
