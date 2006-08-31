/*
 * Created on 21 juil. 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package fr.umlv.unitex.rtn;

/**
 * @author Sébastien Paumier
 *
 */
public class Tag {

    private boolean respectCase;
    private String content;
    
    public Tag(boolean b,String s) {
       respectCase=b;
       content=s;
    }
    
    public String getContent() {
       return content;   
    }
}
