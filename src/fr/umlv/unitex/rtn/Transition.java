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
public class Transition {

    private TransitionTag tag;
    private int destination;
    
    public Transition(TransitionTag t,int dest) {
       tag=t;
       destination=dest;
    }
    
    public TransitionTag getTag() {
       return tag;   
    }

    public void setTag(TransitionTag t) {
      tag=t;   
   }
    
    public int getDestination() {
       return destination;   
    }
}
