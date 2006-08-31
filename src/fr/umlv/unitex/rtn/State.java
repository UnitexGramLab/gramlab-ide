/*
 * Created on 21 juil. 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package fr.umlv.unitex.rtn;

import java.util.*;

/**
 * @author Sébastien Paumier
 *
 */
public class State {
   private boolean subInitial;
   private int graphNumber;
   private boolean terminal;
   private ArrayList transitions;
   
   public State(boolean i,int n,boolean t) {
    subInitial=i;
    graphNumber=n;
    terminal=t;
    transitions=new ArrayList();
   }
   
   public void addTransition(Transition t) {
    transitions.add(t);  
   }
   
   public ArrayList getTransitions() {
      return transitions;
   }
   
   public boolean isSubinitial() {
      return subInitial;
   }

   public boolean isTerminal() {
    return terminal;
 }

   public int getGraphNumber() {
      return graphNumber;
   }
}
