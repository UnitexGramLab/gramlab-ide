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

/**
 * @author Rojo Rabelisoa
 */
public class Delas{
    private  String pOS;
    private  String lemma;
    private  String fSTCode;
    private  String simSem;
    private  String comment;
    private  String lemmaInv;
    private  int lemmaId;
    private  String dicFile;

    public Delas(String pOS, String lemma, String fSTCode, String simSem, String comment, String lemmaInv, int lemmaId, String dicFile) {
        this.pOS = pOS;
        this.lemma = lemma;
        this.fSTCode = fSTCode;
        this.simSem = simSem;
        this.comment = comment;
        this.lemmaInv = lemmaInv;
        this.lemmaId = lemmaId;
        this.dicFile = dicFile;
    }

    public Delas() {

    }

    /**
     * @return the pOS
     */
    public String getpOS() {
        return pOS;
    }

    /**
     * @return the lemma
     */
    public String getLemma() {
        return lemma;
    }

    /**
     * @return the fSTCode
     */
    public String getfSTCode() {
        return fSTCode;
    }

    /**
     * @return the simSem
     */
    public String getSimSem() {
        return simSem;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @return the lemmaInv
     */
    public String getLemmaInv() {
        return lemmaInv;
    }

    /**
     * @return the lemmaId
     */
    public int getLemmaId() {
        return lemmaId;
    }

    /**
     * @return the dicFile
     */
    public String getDicFile() {
        return dicFile;
    }
}
