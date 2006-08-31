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
public class RecursiveCallTag implements TransitionTag {

    private int numberOfStateCalled;
    
    public RecursiveCallTag(int n) {
        numberOfStateCalled=n; 
    }
    
    public int getTagNumber() {
       return numberOfStateCalled;   
    }
}
