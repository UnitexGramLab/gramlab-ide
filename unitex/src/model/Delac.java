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
public class Delac{
    private  String pOS;
    private  String lemmaAll;
    private  String lemma;
    private  String fSTCode;
    private  String simSem;
    private  String comment;
    private  String wn_sinSet;
    private  int lemmaId;
    private  String dicFile;
    private  int dicId;

    public Delac(String pOS, String lemmaAll, String lemma, String fSTCode, String simSem, String comment, String wn_sinSet, int lemmaId, String dicFile, int dicId) {
        
        this.pOS = pOS;
        this.lemma = lemma;
        this.fSTCode = fSTCode;
        this.simSem = simSem;
        this.comment = comment;
        this.lemmaAll = lemmaAll;
        this.wn_sinSet = wn_sinSet;
        this.lemmaId = lemmaId;
        this.dicFile = dicFile;
        this.dicId = dicId;
    }

    public Delac() {

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
     * @return the wn_sinSet
     */
    public String getWn_sinSet() {
        return wn_sinSet;
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

    /**
     * @return the dicId
     */
    public int getDicId() {
        return dicId;
    }

    /**
     * @return the lemmaAll
     */
    public String getLemmaAll() {
        return lemmaAll;
    }
    
}
