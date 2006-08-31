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
public class LexicalTag implements TransitionTag {

    private int tagNumber;
    
    public LexicalTag(int n) {
       tagNumber=n;
    }
    
    public int getTagNumber() {
       return tagNumber;   
    }
}
