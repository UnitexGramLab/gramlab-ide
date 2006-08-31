/*
 * Created on 21 juil. 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package fr.umlv.unitex.rtn;

import java.io.*;
import java.util.*;

import fr.umlv.unitex.*;
import fr.umlv.unitex.exceptions.*;
import fr.umlv.unitex.io.*;

/**
 * @author Sébastien Paumier
 *  
 */
public class RTN {

	private File file;
	private int numberOfGraphs;
	private ArrayList states;
	private ArrayList tags;
	private int[] initialStateOfGraph;
	private String[] nameOfGraph;
	private int axiom;

	

	public void loafFst2(File f) throws NotAUnicodeLittleEndianFileException,
			IOException {
		file = f;
		FileInputStream input = UnicodeIO
				.openUnicodeLittleEndianFileInputStream(file);
		numberOfGraphs = Util.toInt(UnicodeIO.readLine(input));
		initialStateOfGraph = new int[numberOfGraphs + 1];
		nameOfGraph = new String[numberOfGraphs + 1];
		states = new ArrayList();
    tags = new ArrayList();
		for (int i = 0; i < numberOfGraphs; i++) {
			readGraph(input);
		}
    axiom=initialStateOfGraph[1];
    renumberSubgraphCalls();
		readTags(input);
		input.close();
	}

	private void readGraph(FileInputStream input) throws IOException {
		int graphNumber=readGraphNumberAndName(input);
		boolean end = false;
    boolean first=true;
		do {
			char c = (char) UnicodeIO.readChar(input);
			if (c == 'f') {
				UnicodeIO.readLine(input);
				end = true;
			} else {
				if (c != ':' && c != 't')
					throw new IOException();
				State state = new State(first,graphNumber,c == 't');
        first=false;
				states.add(state);
				String s = UnicodeIO.readLine(input);
				StringTokenizer tokenizer = new StringTokenizer(s, " ");
				int tokens = tokenizer.countTokens();
				if ((tokens % 2) != 0) {
					// number of tokens must be odd because transitions
					// are couple of tag and state
					throw new IOException();
				}
				for (int i = 0; i < tokens / 2; i++) {
					String tag = tokenizer.nextToken();
					char tmp = tag.charAt(0);
					if (tmp == '-' || tmp == '*') {
						tag = tag.substring(1, tag.length());
					}
					int tagRef = Util.toInt(tag);
					int destinationState = Util.toInt(tokenizer.nextToken())+initialStateOfGraph[graphNumber];
					Transition t;
					switch (tmp) {
						case '-' :
							t = new Transition(new SubgraphCallTag(tagRef),
									destinationState);
							break;
						case '*' :
							t = new Transition(new RecursiveCallTag(tagRef),
									destinationState);
							break;
						default :
							t = new Transition(new LexicalTag(tagRef),
									destinationState);
					}
					state.addTransition(t);
				}
			}
		} while (!end);
	}

	private int readGraphNumberAndName(FileInputStream input) {
		String s = UnicodeIO.readLine(input);
		int n = s.indexOf(' ');
		int graphNumber = Util.toInt(s.substring(1, n)); // 1 because of the '-'
		// character
		nameOfGraph[graphNumber] = s.substring(n + 1, s.length());
		initialStateOfGraph[graphNumber] = states.size();
    return graphNumber;
	}

    
  private void readTags(FileInputStream input) {
     do {
        char c=(char)UnicodeIO.readChar(input);
        if (c=='f') return;
        tags.add(new Tag((c=='@'),UnicodeIO.readLine(input)));
     } while(true);
  }
  
        
  private void renumberSubgraphCalls() {
     if (states==null) return;
     for (int i=0;i<states.size();i++) {
        ArrayList trans=((State)states.get(i)).getTransitions();
        if (trans!=null) {
            for (int j=0;j<trans.size();j++) {
               Transition t=(Transition)trans.get(j);
               TransitionTag tag=t.getTag();
               if (tag instanceof SubgraphCallTag) {
                  t.setTag(new RecursiveCallTag(initialStateOfGraph[tag.getTagNumber()]));
               }
            }
        }
     }
  }
  
  
  public void printRTN() {
     if (states==null) return;
     for (int i=0;i<states.size();i++) {
         State s=(State)states.get(i);
         if (s.isSubinitial()) {
            System.out.println("-"+s.getGraphNumber()+" "+nameOfGraph[s.getGraphNumber()]);
            if (s.getGraphNumber()==1) System.out.print("AXIOM ");
         }
         System.out.print(i+" "+(s.isTerminal()?'t':':')+" ");
         ArrayList trans=s.getTransitions();
         if (trans!=null) {
             for (int j=0;j<trans.size();j++) {
                Transition t=(Transition)trans.get(j);
                TransitionTag tag=t.getTag();
                System.out.print(getTagDescription(tag)+" "+t.getDestination()+" ");
             }
         }
         System.out.println();
     }
  }
  
  public String getTagDescription(TransitionTag t) {
     if (t instanceof SubgraphCallTag) return "-"+t.getTagNumber();
     if (t instanceof RecursiveCallTag) return "*"+t.getTagNumber();
     if (tags==null) return null;
     return ((Tag)tags.get(t.getTagNumber())).getContent();
  }
  
	public static void main(String[] args) {
		RTN rtn = new RTN();
		try {
			rtn.loafFst2(new File("e:\\my unitex\\French\\graphs\\Det.fst2"));
      rtn.printRTN();
		} catch (NotAUnicodeLittleEndianFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

