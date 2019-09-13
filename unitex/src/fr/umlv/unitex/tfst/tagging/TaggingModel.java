/*
 * Unitex
 *
 * Copyright (C) 2001-2019 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package fr.umlv.unitex.tfst.tagging;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.graphrendering.GenericGraphBox;
import fr.umlv.unitex.graphrendering.TfstGraphBox;
import fr.umlv.unitex.graphrendering.TfstGraphicalZone;
import fr.umlv.unitex.listeners.GraphListener;

/**
 * This class is used to know whether a sentence automaton box has been
 * hand-selected or not for tagging purpose.
 * 
 * @author paumier
 */
public class TaggingModel {
	
	public class Context{
		ArrayList<Integer> path = new ArrayList<>();
		int pos;
		int currentBox;
		public boolean space;
		
		public Context(String s) {
			String [] tmp = s.split("-");
			for (String string : tmp) {
				path.add(Integer.valueOf(string));
			}
			pos = 0;
			currentBox = 1;
			space = false;
		}

		public void display() {
			System.out.println("\n\n\nDISPLAYING\n\n");
			System.out.println("path : " + path + "\npos : " + pos + "\ncurrentBox : " + currentBox
					+ "\nnum box : " + path.get(currentBox) + "\ntexte : " + getTextBoxe(boxes[sortedNodes[path.get(currentBox)]]));
		}
	}
	
	TfstGraphicalZone zone;

	/*
	 * We use an array that is a copy of zone's boxes, because we want to keep
	 * constant indices, even if some boxes are removed
	 */
	HashMap<Integer,ArrayList<String>> tokens;
	TfstGraphBox[] boxes;
	TaggingState[] taggingStates;
	public int[] renumber;
	int[] sortedNodes;
	public boolean[] factorization;
	int initialState;
	int finalState;
	
	ArrayList<Context> lstContext = new ArrayList<>();
	
	
	/*The 2 next fields are used to keep all sentences in the automaton and their path
	 * The first element of lstTok is the first sentence possible in the automaton 
	 * between 2 factorizations nodes and the first element in lstPath is the 
	 * path to have the sentence (the path is compose of all number of boxes of the path 
	 * */
	private ArrayList<String> lstTok = new ArrayList<String>();
	private ArrayList<String> lstPath = new ArrayList<>();
	
	private boolean linearTfst;
	String regex;
	File alphabetFile;
	ActionListener actionListener=new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			resetModel();
		}
	};
	
	GraphListener graphListener=new GraphListener() {
		@Override
		public void graphChanged(boolean modified) {
			updateModel();
		}
	};

	

	
	

	public TaggingModel(TfstGraphicalZone zone) {
		this.zone = zone;
		registerListeners();
		resetModel();
	}

	
	public void registerListeners() {
		zone.addActionListner(actionListener);
		zone.addGraphListener(graphListener);		
	}
	
	public void unregisterListeners() {
		zone.removeActionListner(actionListener);
		zone.removeGraphListener(graphListener);		
	}
	
	/** @Yass
	 * This basically checks if a new box was created, if yes, it resets the model, otherwise it updates the factorization Nodes.
	 * This method must be called when the sentence automaton has been modified,
	 * either by a box removal, or by a transition added or removed.
	 */
	protected void updateModel() {
		if (factorization == null)
			return;
		if (zone.graphBoxes.size() == 0) {
			//resetModel();
		} else {
			if (boxes.length!=0 && zone.graphBoxes.size()!=boxes.length) {
				/* If the number of boxes has changed, then we must recompute
				 * eveything
				 */
				resetModel();
			} else {
				
				updateNodes();
				//updateBoundsReversed(finalState, new boolean[boxes.length]);
				//updateBounds(initialState, new boolean[boxes.length]);
				
			}
		}
	}
	
	
	/** @Yass
	 * This method must be called when a new sentence automaton has been loaded.
	 * This method resets the various arrays used in this class by applying all the graphBoxes, including new ones, in them.
	 */
	public void resetModel() {
		StringBuilder s = new StringBuilder();
		final int n = zone.graphBoxes.size();
		boxes = new TfstGraphBox[n];
		taggingStates = new TaggingState[n];
		factorization = new boolean[n];
		renumber = new int[n];
		//init 
		tokens = new HashMap<Integer,ArrayList<String>>();
		//System.out.println("size"+tokens.size());
		for (int i = 0; i < n; i++) {
			boxes[i] = (TfstGraphBox) zone.graphBoxes.get(i);
			if (boxes[i].type == GenericGraphBox.INITIAL) {
				initialState = i;
			}
			else if (boxes[i].type == GenericGraphBox.FINAL)
				finalState = i;
			taggingStates[i] = TaggingState.NEUTRAL;
			boxes[i].state = TaggingState.NEUTRAL;
		}
		updateNodes();
		generateAlphabet();
		generateTokensList();
		zone.unSelectAllBoxes();
		lstTok.clear();
		lstPath.clear();
		
	}
	
	void generateAlphabet() {
		if( regex == null || !alphabetFile.equals(ConfigManager.getManager().getAlphabet(null))) { 
			// switching languages must also switch this
			alphabetFile = ConfigManager.getManager().getAlphabet(null);
			try {
				StringBuilder alphabet= new StringBuilder();
				
				BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream(alphabetFile) , "UTF16"));
				String line; 
				while ((line = br.readLine()) != null && !line.equals("\r\n")) {
					if( line.startsWith("#")) {
						alphabet.append(line.charAt(0)).append("-").append(line.charAt(1));
					}		
					alphabet.append(line.trim());
				}
				StringBuilder regexBuilder = new StringBuilder();
				regexBuilder.append("(<E>)|([").append(alphabet.toString())
					.append("]+|[^").append(alphabet.toString()).append("]+)");
				regex = regexBuilder.toString();
			} catch (IOException e) {
				 //TODO Auto-generated catch block
				System.out.println("exception");
			} finally {}	
		}	
	}
	
	/*
	 * for each box we will start at it's starting bound, split the inside based on delimiters and add them to the hashmap. 
	 * if 2 words split by a comma are found, they will be stored subsequently in the hashmap at the starting bound's value, then +1 and so on. 
	 */
	public void generateTokensList() {
		for( TfstGraphBox gb : boxes ) {
			if( taggingStates[gb.getBoxNumber()] == TaggingState.NOT_PREFERRED || taggingStates[gb.getBoxNumber()] == TaggingState.USELESS )
				break;
			if( gb == null || gb.getContent() == null )
				continue;
			
			if( gb.getBounds() == null)
				continue;
			
			int index = gb.getBounds().getStart_in_tokens();
			String temp = null;
			ArrayList<String> tokensList = new ArrayList<String>();
			if( gb.getContent().contains("{")  )
				temp = gb.getContent().split(",")[0].substring(1);
			else {
				temp = gb.getContent();
			}
			/* ERROR WITH KOREAN MODE
			 * Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(temp);
			
			while( matcher.find() ) {
				tokensList.add(matcher.group());
			}*/
			
                        if( gb.getBounds().getEnd_in_tokens()
			== gb.getBounds().getStart_in_tokens() + tokensList.size() - 1) {
			
		    for( String s : tokensList ) {
	            	if( !tokens.containsKey(index) )  
	            		tokens.put(index, new ArrayList<String>());
	            	if( !tokens.get(index).contains(s))
	            		tokens.get(index).add(s);
	            	index++;
	            }
			} else
			if( gb.getBounds().getEnd_in_tokens()
			== gb.getBounds().getStart_in_tokens() ){
				if( !tokens.containsKey(index) )  
            		tokens.put(index, new ArrayList<String>());
            	if( !tokens.get(index).contains(temp))
            		tokens.get(index).add(temp);
			} else {
				// implicit case where the difference is 0 < D < tokensList.length
				tokens.put(index, new ArrayList<String>());
				tokens.get(index).add(temp);
			}
			
		}
		for ( int i=0; i<tokens.size();i++ )
	    	if( !tokens.containsKey(i) ) {
	    		tokens.put( i, new ArrayList<String>());
	    		tokens.get(i).add(" ");
	    }
	}
	
	
	/**  This function creates all the paths we can have between the first box
	 *  "bfp" and the last box "bfs" and put them in allPaths. Then, it keeps only
	 *  paths which contains at least a box which is tagged USELESS
	 * 
	 * @param bfp 
	 * 			The number of the first box in the path
	 * 
	 * @param bfs
	 * 			The number of the last box in the path
	 * 
	 * @param allPaths
	 * 			The list of all path which contains at least a box USELESS
	 */		
	void computeAllPaths( int bfp, int bfs, ArrayList<ArrayList<Integer>> allPaths ){
		ArrayList<ArrayList<Integer>> lstRemove = new ArrayList<>();
		
		computePath(boxes[sortedNodes[bfp]], boxes[sortedNodes[bfs]], new ArrayList<Integer>(), allPaths);
 		for (int tmp = 0; tmp < allPaths.size(); tmp++) {
			allPaths.get(tmp).remove(0);
			if(!containsUseless(allPaths.get(tmp)))
				lstRemove.add(allPaths.get(tmp));
		}
		for(int i = 0; i < lstRemove.size(); i++)
			allPaths.remove(lstRemove.get(i));
		
		for (ArrayList<Integer> arrayList : allPaths) {
			for (int i = 0; i < arrayList.size(); i++) {
				if(taggingStates[sortedNodes[arrayList.get(i)]] != TaggingState.NEUTRAL
						&& taggingStates[sortedNodes[arrayList.get(i)]] != TaggingState.PREFERRED)
					setBoxStateInternal(sortedNodes[arrayList.get(i)], TaggingState.TO_CHECK);
			}
			
		}
	}
	

	/** @Aissa
	 * 
	 * This function checks if the at least one box of the boxes in arrayList
	 * is in state "USELESS"
	 * 
	 * @param arrayList 
	 * 			ArrayList of the number of each boxes of the path
	 * 
	 * @return  true if at least one box is USELESS
	 * 			false if not
	 */			
	private boolean containsUseless(ArrayList<Integer> arrayList) {
		for(int i = 0; i < arrayList.size(); i++) {
			if(taggingStates[sortedNodes[arrayList.get(i)]] == TaggingState.USELESS)
				return true;
		}
		return false;
	}

	
	/** This function creates all paths between the first box bfp and the last box bfs 
	 * Put a path in "current"
	 * When the path is complete, put it in allPaths
	 * Then we restart
	 * 
	 * @param bfp 
	 * 			The first box in the path
	 * 
	 * @param bfs
	 * 			The last box in the path
	 * 
	 * @param current
	 * 			The list of boxes of the current path
	 * 
	 * @param allPath
	 * 			The list of all paths between the box bfp and bfs
	 */	
	void computePath(GenericGraphBox box, GenericGraphBox last, ArrayList<Integer> currentPath, ArrayList<ArrayList<Integer>> allPaths) {
		if(box.equals(last))
			allPaths.add(currentPath);
		else {
			currentPath.add(renumber[box.getBoxNumber()]);
			for (GenericGraphBox next : box.transitions) {
				computePath(boxes[next.getBoxNumber()], last, new ArrayList<Integer>(currentPath), allPaths);
			}
			
		}
	}
	
	
	void clearList() {
		lstTok.clear();
		lstPath.clear();
	}
	
	
	void createLstContext() {
		lstContext.clear();
		for (String arrayList : lstPath) 
			lstContext.add(new Context(arrayList));
		
		for (Context c : lstContext) {
			for (int i = 0; i < c.path.size(); i++) {
				if(getTextBoxe(boxes[sortedNodes[c.path.get(i)]]).equals("<E>")
						&& (boxes[sortedNodes[c.path.get(i)]]).type != GenericGraphBox.INITIAL
					&& (boxes[sortedNodes[c.path.get(i)]]).type != GenericGraphBox.FINAL) {
					if(c.currentBox > i)
						c.currentBox--;
					c.path.remove(i);
				}
			}
		}
	}
	
	/** This function checks if the new branch can be add to the automaton or not
	 * If it's not, we remove the last transition in the automaton
	 */		
	void checkNewBranch( int i ){
		int prev = getPreviousFactorizationNodeIndex(i);
		int next = getNextFactorizationNodeIndex(i);
		ArrayList<ArrayList<Integer>> allPaths = new ArrayList<>();		
		
		computeAllPaths( renumber[prev], renumber[next], allPaths );
		clearList();
		findString(boxes[sortedNodes[renumber[prev]]], boxes[sortedNodes[renumber[next]]], new StringBuilder(), boxes[sortedNodes[renumber[prev]]], new StringBuilder());
		createLstContext();
		
		int cpt = 0;
		for (Context c : lstContext) {
			cpt++;
		}
		
		if(cpt == 0) {
			return;
		}
		
		TaggingState[] taggingStatesTmp = new TaggingState[taggingStates.length];
		copyStates(taggingStates, taggingStatesTmp);
		
		
		boolean b = verifyAllPath(allPaths, sortedNodes[renumber[prev]], sortedNodes[renumber[next]]);
		int n = zone.graphBoxes.size();
		for (int i1 = 0; i1 < n; i1++) {
			boxes[i1] = (TfstGraphBox) zone.graphBoxes.get(i1);
			if (boxes[i1].type == GenericGraphBox.INITIAL) {
				initialState = i1;
			}
			else if (boxes[i1].type == GenericGraphBox.FINAL)
				finalState = i1;
			taggingStates[i1] = TaggingState.NEUTRAL;
			boxes[i].state = TaggingState.NEUTRAL;
		}
		
		
		if(b == false)  {
			zone.removeLastTransition();
			copyStates(taggingStatesTmp, taggingStates);
		}

		updateNodes();
		
	}
	
	
	private void copyStates(TaggingState[] src, TaggingState[] dst) {
		for(int i = 0; i < dst.length; i++) 
			dst[i] = TaggingState.NEUTRAL;
	}


	private boolean verifyAllPath(ArrayList<ArrayList<Integer>> allPaths, int bfp, int bfs) {
		int newBegin;
		ArrayList<Integer> lstText = new ArrayList<>();
		
		for (ArrayList<Integer> lst : allPaths) {
			createLstContext();
			ArrayList<Context> copyContext = (ArrayList<Context>) lstContext.clone();
			
			for (int i = 0; i < lst.size(); i++) {
				if(getTextBoxe(boxes[sortedNodes[lst.get(i)]]).equals("<E>")) {
					newBegin = findPreviousTokenStart(boxes[sortedNodes[lst.get(i)]]);
					boxes[sortedNodes[lst.get(i)]].getBounds().setEnd_in_chars(2);
					if(taggingStates[sortedNodes[lst.get(i)]] == TaggingState.TO_CHECK) {
						boxes[sortedNodes[lst.get(i)]].getBounds().setStart_in_tokens(newBegin);
						boxes[sortedNodes[lst.get(i)]].getBounds().setEnd_in_tokens(findLastToken(boxes[sortedNodes[lst.get(i)]], copyContext));
					}
				}
				else {					
					for (Context context : copyContext) {
						String sequence = findSequence(lst);
						if(context.currentBox >= context.path.size()) {
							JOptionPane.showMessageDialog(null,
									sequence + " isn't correct",
									"Matching Error",
									JOptionPane.PLAIN_MESSAGE);
							return false;
						}
					}
					if(i == 0) {
						newBegin = findNextTokenNumber(boxes[bfp]);
					}
					else {
						int tmp = findNewBegin(lstContext);
						if(tmp != -1)
							newBegin = boxes[sortedNodes[lst.get(i-1)]].getBounds().getEnd_in_tokens() + tmp;
						else
							newBegin = boxes[sortedNodes[lst.get(i-1)]].getBounds().getStart_in_tokens();
					}
					if(taggingStates[sortedNodes[lst.get(i)]] == TaggingState.TO_CHECK)
						boxes[sortedNodes[lst.get(i)]].getBounds().setStart_in_tokens(newBegin);
					
						
					if(!verifyBox(boxes[sortedNodes[lst.get(i)]], copyContext)) {
						String sentence = findSequence(lst);
						JOptionPane.showMessageDialog(null,
								sentence + " isn't correct",
								"Matching Error",
								JOptionPane.PLAIN_MESSAGE);
						return false;
					}
					
					
					if(taggingStates[sortedNodes[lst.get(i)]] == TaggingState.TO_CHECK) {
						boxes[sortedNodes[lst.get(i)]].getBounds().setEnd_in_tokens(findLastToken(boxes[sortedNodes[lst.get(i)]], copyContext));
						
					}
					String s = Normalizer.normalize(getTextBoxe(boxes[sortedNodes[lst.get(i)]]), Normalizer.Form.NFKC);
					StringBuilder sb = new StringBuilder();
					sb.append(s.charAt(s.length()-1));
					String tmp = Normalizer.normalize(sb, Normalizer.Form.NFD);
					if(isKorean(tmp.charAt(0))) {
						
						
						int cpt = 0;
						for(int k = 0; k < tmp.length(); k++) {
							if(isSimpleKoreanLetter(tmp.charAt(k))) {
								cpt += 1;
							}
							else {
								cpt += 2;
							}
						}
						boxes[sortedNodes[lst.get(i)]].getBounds().setEnd_in_letters(cpt-1);
					}
				}
				lstText = lst;
			}
		
			for (Context context : copyContext) {
				if(context.currentBox < context.path.size() && boxes[sortedNodes[context.path.get(context.currentBox)]].equals(boxes[bfs])) {
					return true;
				}
			}
		}
		String sequence = findSequence(lstText);
		JOptionPane.showMessageDialog(null,
				sequence + " isn't correct",
				"Matching Error",
				JOptionPane.PLAIN_MESSAGE);
		return false;
		
	}
	
	/**
	 * Find the sentence of the new path and return it
	 * 
	 * @param lst
	 * @return the sequence of the path
	 */
	private String findSequence(ArrayList<Integer> lst) {
		StringBuilder sb = new StringBuilder();
		int tmp = 0;
		for(Integer in : lst) {
			
			if(boxes[sortedNodes[in]].getBounds().getStart_in_tokens() - tmp == 2)
				sb.append(" ");
			tmp = boxes[sortedNodes[in]].getBounds().getEnd_in_tokens();
			if(!getTextBoxe(boxes[sortedNodes[in]]).equals("<E>"))
				sb.append(getTextBoxe(boxes[sortedNodes[in]]));
		}
		return sb.toString();
	}


	private boolean isKorean(char c) {
		if((c >= 'ᄀ' &&  c <=  'ᇿ' ) ||  (c >= 'ㄱ' && c <= 'ㆎ') )
			return true;
		
		return false;
	}


	private boolean isSimpleKoreanLetter(char c) {
		String singleLetters = " ᄀ    ᆨ    ㄱ     ᄂ   ᆫ     ㄴ    ᄃ    ᆮ    ㄷ    ᄅ     ᆯ    ㄹ     ᄆ     ᆷ      ㅁ     ᄇ     ᆸ    ㅂ  ᄉ    ᆺ   ㅅ    ᄋ    ᆼ      ㅇ     ᄌ     ᆽ    ㅈ     ᄎ     ᆾ     ㅊ      ᄏ      ᆿ       ㅋ   ᄐ     ᇀ    ㅌ    ᄑ    ᇁ    ㅍ  ᄒ     ᇂ      ㅎ       ᅡ      ㅏ     ᅣ     ㅑ    ᅥ     ㅓ        ᅩ     ㅗ     ᅭ     ㅛ      ᅮ     ㅜ      ᅲ    ㅠ    ᅳ      ㅡ        ᅵ      ㅣ    ";
		singleLetters = Normalizer.normalize(singleLetters, Normalizer.Form.NFD);
		singleLetters = Normalizer.normalize(singleLetters, Normalizer.Form.NFKC);
		singleLetters = Normalizer.normalize(singleLetters, Normalizer.Form.NFD);
		return singleLetters.indexOf(c) >= 0;
	}


	private int findPreviousTokenStart(TfstGraphBox box) {
		String txt = getTextBoxe(box);
		if(txt.equals("<E>")) {
			if(findPreviousBox(box).getBounds() != null) {
				return findPreviousBox(box).getBounds().getStart_in_tokens();
			}
		}
		return 0;
	}
	
	private TfstGraphBox findPreviousBox(GenericGraphBox current) {
		for (int i = 0; i < boxes.length; i++) {
			for (GenericGraphBox next : boxes[i].transitions) {
				if(next.equals(current)) {
					return boxes[i];
				}
			}
		}
		
		return null;
	}


	/** @Aissa
	 * 
	 * This function finds the last coordinated of each previous boxes of boxes in lstContext 
	 * If no one is corresponding with the coordinated, we compute the value with the number
	 * of characters
	 * 
	 * @param current
	 * 			The current GenericGraphBox
	 * 
	 * @param lstContext
	 * 			The list of actual contexts
	 * 
	 * @return the last coordinated of the current box
	 * 			
	 */	
	private int findLastToken(GenericGraphBox current, ArrayList<Context> lstContext) {
		String txt = getTextBoxe(current);
		if(txt.equals("<E>")) {
			if(findPreviousBox(current).getBounds() != null)
				return findPreviousBox(current).getBounds().getEnd_in_tokens();
		}
		
		for (Context context : lstContext) {
			if(context.pos == 0 && context.currentBox > 1)
				return boxes[sortedNodes[context.path.get(context.currentBox - 1)]].getBounds().getEnd_in_tokens();
		}
		
		int size = txt.length();
		int last = 0;
		for(int i = 0; i < size; i++) {
			if(txt.charAt(i) == ' ' && i <= size-2 && txt.charAt(i+1) != ' ')
				last += 2;
		}
		return boxes[current.getBoxNumber()].getBounds().getStart_in_tokens() + last;
	}


	/** @Aissa
	 * 
	 * This function finds the first coordinated of one of the next box of the current one
	 * which is not tagged USELESS
	 * 
	 * @param current
	 * 			The current GenericGraphBox
	 * 	
	 * @return the first coordinated of the next box not USELESS
	 * 
	 */		
	private int findNextTokenNumber(GenericGraphBox current) {
		for (GenericGraphBox next : current.transitions) {
			if(taggingStates[next.getBoxNumber()] != TaggingState.USELESS)
				return boxes[next.getBoxNumber()].getBounds().getStart_in_tokens();
		}
		
		return 0;
	}

	
	/** @Aissa
	 * 
	 * This function finds the first coordinated of boxes which are in lstContext
	 * These boxes are in lstContext means that they are corresponding with the current box
	 * 
	 * @param lstContext
	 * 			The list of all context
	 * 
	 * @return the first coordinated of the current box
	 */		
	private int findNewBegin(ArrayList<Context> lstContext) {
		boolean hasSpace = false;
		
		for (Context context : lstContext) {
				if(context.space)
					hasSpace = true;
				
				context.space = false;
		}
		
		if(hasSpace)
			return 2;
		
		for (Context context : lstContext) {
			if(context.pos == 0) {
				if(boxes[sortedNodes[context.path.get(context.currentBox - 1)]].getBounds() != null)
					return boxes[sortedNodes[context.path.get(context.currentBox)]].getBounds().getStart_in_tokens() -
							boxes[sortedNodes[context.path.get(context.currentBox - 1)]].getBounds().getEnd_in_tokens();
				else
					return boxes[sortedNodes[context.path.get(context.currentBox)]].getBounds().getStart_in_tokens();
		
			}
		}
		return -1;
	}

	
	private boolean verifyBox(GenericGraphBox box, ArrayList<Context> copyContext) {
		String txt = Normalizer.normalize(getTextBoxe(box), Normalizer.Form.NFD);
		int size = txt.length();
		ArrayList<Context> ctxtRemove = new ArrayList<>();
		
		
		if(txt.equals("<E>"))
			return true;
		
		for (Context context : copyContext) {
			
			if(context.currentBox >= context.path.size()) {
				ctxtRemove.add(context);
				continue;
			}
			int numBox = context.path.get(context.currentBox);
			String txtBis = Normalizer.normalize(getTextBoxe(boxes[sortedNodes[numBox]]), Normalizer.Form.NFD);
				
			for(int i = 0; i < size; i++) {
				if(context.currentBox >= context.path.size()) {
					ctxtRemove.add(context);
					break;
				}
				numBox = context.path.get(context.currentBox);
				txtBis =  Normalizer.normalize(getTextBoxe(boxes[sortedNodes[numBox]]), Normalizer.Form.NFD);

				if(context.space == true && i != 0) {
					if(txt.charAt(i) != ' ') 
						ctxtRemove.add(context);
					context.space = false;
				}
				else {
					if(context.pos >= txtBis.length()) {
						context.pos = 0;
						context.currentBox ++;
					}
					if(txt.charAt(i) != txtBis.charAt(context.pos)) {
						ctxtRemove.add(context);
					}
					context.pos ++;
					if(context.pos >= txtBis.length()) {
						if(context.currentBox + 1 < context.path.size() && boxes[sortedNodes[context.path.get(context.currentBox+1)]].type != GenericGraphBox.FINAL
								&& boxes[sortedNodes[context.path.get(context.currentBox+1)]].getBounds().getStart_in_tokens() -
								boxes[sortedNodes[numBox]].getBounds().getEnd_in_tokens() == 2)
							context.space = true;
						context.pos = 0;
						context.currentBox ++;
					}
				}
			}
			if(context.pos < txtBis.length() && txtBis.charAt(context.pos) == ' ') {
				context.space = true;
				context.pos++;
			}
		}
		
		if(taggingStates[box.getBoxNumber()] != TaggingState.TO_CHECK)
			return true;
		
		for (Context context : ctxtRemove)
			copyContext.remove(context);

		if(copyContext.isEmpty())
			return false;
		
		return true;
	}
	
	
	/** @Aissa
	 * 
	 * This function is used to find all sentences between the 2 factorizations box 
	 * (one at the beginning of the branch and one at the end)
	 * It constructs the sentence and the path (composed of the number of boxes of the path)
	 * It puts the sentence in the ArrayList lstTok and the path in lstPath
	 * 
	 * @param begin 
	 * 				the GenericGraphBox of the previous factorization box
	 * 
	 * @param end 
	 * 				the GenericGraphBox of the next factorization box
	 * 
	 * @param sb
	 * 				the StringBuilder which will contain the sentence of the current path
	 * 
	 * @param b
	 * 				the current GenericGraphBox 
	 * 
	 * @param path
	 * 				the StringBuilder which will contain the actual path 
	 * 
	 * @return
	 */
	void findString(GenericGraphBox begin, GenericGraphBox end, StringBuilder sb, GenericGraphBox b, StringBuilder path) {
		int num_box = b.getBoxNumber();	
		
		if(end.equals(b)) {
			if(b.type != GenericGraphBox.FINAL) 
				sb.append(boxes[num_box].getContentText());
			path.append(renumber[num_box]);
			lstTok.add(sb.toString());
			lstPath.add(path.toString());
			return ;
		}
		
		int nb_transition_out = b.transitions.size();
		path.append(renumber[num_box]).append("-");
				
		if(boxes[num_box].type != GenericGraphBox.INITIAL && !boxes[num_box].getContentText().equals("<E>"))
			sb.append(boxes[num_box].getContentText());
		
		for(int i = 0; i < nb_transition_out; i++) {
			int num_next_box = b.transitions.get(i).getBoxNumber();
			int start = sb.length();
			int startPath = path.length();
			if(taggingStates[num_next_box] != TaggingState.TO_CHECK
					|| boxes[num_next_box].equals(end)) {
				if(boxes[num_box].type != GenericGraphBox.INITIAL 
						&& boxes[num_next_box].type != GenericGraphBox.FINAL
						&& (boxes[num_box].getBounds().getEnd_in_tokens() 
						== boxes[num_next_box].getBounds().getStart_in_tokens() - 2)) {
					sb.append(" ");
				}
				findString(begin, end, sb, b.transitions.get(i), path);
				int last = sb.length();
				sb.delete(start, last);
				path.delete(startPath, path.length());
			}
		}
	}
	
	/* Return the text contains in the box */
	String getTextBoxe(GenericGraphBox box) {
		if(box.getContent().contains("{")  )
			return box.getContent().split(",")[0].substring(1);
		else {
			return box.getContent();
		}
	}
	


	/*boolean checkingNewText( int bfp, int bfs, ArrayList<ArrayList<Integer>> allPaths ) {
		
		displayTokensHash();
		
		System.out.println("-----> checkingNewText Start");
		
		StringBuilder sb = new StringBuilder();
		
		if( allPaths.isEmpty() )
			return false;
		
		int index, end, last_start_token;
				
		if(boxes[sortedNodes[bfp]].getBounds() != null) {
			index = boxes[sortedNodes[bfp]].getBounds().getEnd_in_tokens(); // Exception here
		}
		else if(boxes[sortedNodes[bfp]].type == GenericGraphBox.FINAL) {
			System.err.println("Impossible to connect the final box to another");
			return false;
		}
		else{
			index = -1; // first tokens, so he is the number 0
		}
		
		if(boxes[sortedNodes[bfs]].getBounds() != null) {
			end = boxes[sortedNodes[bfs]].getBounds().getStart_in_tokens(); // Exception here
		}		
		else if(boxes[sortedNodes[bfs]].type == GenericGraphBox.INITIAL) {
			System.err.println("Impossible to connect a box to the initial box");
			return false;
		}
		else {
			end = findStartTokenOfLastBoxe(boxes);
		}
		
		last_start_token = index;
		
		findString(boxes[sortedNodes[bfp]], boxes[sortedNodes[bfs]], new StringBuilder(), boxes[sortedNodes[bfp]], new StringBuilder());
		displayAllSentence();
		
		System.out.println("INDEX = " + index + " END = " + end);
		//int j = index+1; // +2 by default, to be changed
		ArrayList<String> otherTokens = new ArrayList<String>();
		
		for( ArrayList<Integer> e : allPaths ) {
			System.out.println("checking text in path : "+e.toString());
			for( Integer i : e ) {
				j++;
				int newStart = j;
				
				String stringToMatch;
				if( boxes[sortedNodes[i]].getContent().contains("{")  )
					stringToMatch = boxes[sortedNodes[i]].getContent().split(",")[0].substring(1);
				else {
					stringToMatch = boxes[sortedNodes[i]].getContent();
				}
				
				
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(stringToMatch);
				
				while( matcher.find() ) {
					System.out.println("renumber[b] word "+i+" "+matcher.group());
					otherTokens.add(matcher.group());
				}

				if(boxes[sortedNodes[i]].getBounds().getStart_in_tokens() - last_start_token == 2)
					sb.append(" ");
				
				sb.append(stringToMatch);
				last_start_token = boxes[sortedNodes[i]].getBounds().getStart_in_tokens();
				
				
			}
		}
		
		if(end - last_start_token == 2)
			sb.append(" ");
		
		
		lstTok.clear();
		lstPath.clear();
		System.err.println("Chaine 2 : \"" + sb.toString() + "\" ");
		System.out.println("OtherToken : " + otherTokens);
		if(!sb.toString().equals("")) {
			JOptionPane.showMessageDialog(null,
					"Correct Matching",
					"Branch is acceptable",
					JOptionPane.PLAIN_MESSAGE);
			return true;
			
		}
		else{
			JOptionPane.showMessageDialog(null,
					"Error matching",
					"Matching Error",
					JOptionPane.PLAIN_MESSAGE);
			return false;
		}
	}*/
	
	
	/* Diplay all sentences in lstTok and their path in lstPath */
	private void displayAllSentence() {
		System.out.println("Diplaying list token");
		for (int i = 0; i < lstTok.size(); i++) {
			System.out.println("Sentence " + (i+1) + " : \"" + lstTok.get(i) + "\" -> " + lstPath.get(i));
		}
		System.out.println("Finish displaying");
	}


	/*
	public void updateBoundsOfNextUseless( TfstGraphBox firstBox, TfstGraphBox SecondBox) {
		if( taggingStates[SecondBox.getBoxNumber()] == TaggingState.SELECTED || taggingStates[SecondBox.getBoxNumber()] == TaggingState.NEUTRAL )
			return;
		Bounds bounds = new Bounds(firstBox.getBounds());
		bounds.setStart_in_tokens(bounds.getStart_in_tokens()+2);
		bounds.setEnd_in_tokens(bounds.getEnd_in_tokens()+2);
		SecondBox.setBounds(bounds);
	}*/
	
	/*private void updateBoundsReversed( int current, boolean[] visited ) {
		final ArrayList<Integer>[] reverse = computeReverseTransitions();
		if(visited.length == 0 )
			return;
		
		if (visited[current])
			return;
		visited[current] = true;
		for (final int destIndex : reverse[current]) {
			if( boxes[current].type == TfstGraphBox.NORMAL && ( taggingStates[destIndex] == TaggingState.NOT_PREFERRED || taggingStates[destIndex] == TaggingState.USELESS ) ) {
				Bounds b = new Bounds(boxes[current].getBounds());
				b.setStart_in_tokens(b.getStart_in_tokens()-2);
				b.setEnd_in_tokens(b.getEnd_in_tokens()-2);
				boxes[destIndex].setBounds(b);
				
			}
			updateBoundsReversed(destIndex, visited);
		}
	}*/
	/*
	private void updateBounds( int current, boolean[] visited ) {
		
		if(visited.length == 0 )
			return;
		
		if (visited[current])
			return;
		visited[current] = true;
		for (final GenericGraphBox gb : boxes[current].transitions) {
			final int destIndex = getBoxIndex((TfstGraphBox) gb);

			if( boxes[current].type == TfstGraphBox.NORMAL && ( taggingStates[destIndex] == TaggingState.NOT_PREFERRED || taggingStates[destIndex] == TaggingState.USELESS ) ) {
				Bounds b = new Bounds(boxes[current].getBounds());
				b.setStart_in_tokens(b.getStart_in_tokens()+2);
				b.setEnd_in_tokens(b.getEnd_in_tokens()+2);
				boxes[destIndex].setBounds(b);
				//System.out.println("box destIndex bounds :\t" + boxes[destIndex].getBounds().toString() +" | " + b.toString());
			}
			updateBounds(destIndex, visited);
		}
	}*/
	/**
	 * 
	 */
	private void updateNodes() {
		if (boxes.length == 0)
			return;
		/*
		 * First, we look for useless states (neither accessible and
		 * co-accessible)
		 * 
		 */
		markUselessStates();
		
		/* Then we look for factorization nodes */
		/* There should be the real calculus here */ 
		computeFactorizationNodes();
		/* And finally, we can mark as preferred all factorization nodes */
		for (int i = 0; i < factorization.length; i++) {
			if (factorization[i] || taggingStates[i] == TaggingState.PREFERRED) {
				preferBox(i);
			}
		}
		markUselessStates();

	}
	
	/**
	 * 
	 */
	private void computeFactorizationNodes() {
		renumber = topologicalSort();
		if (renumber == null) {
			/*
			 * If the automaton is not acyclic, then we fail to compute the
			 * factorization nodes, so we just say that the initial and final
			 * ones aren't.
			 */
			for (int i = 0; i < factorization.length; i++) {
				if (boxes[i].type != GenericGraphBox.NORMAL && taggingStates[i] != TaggingState.USELESS) {
					factorization[i] = true;
				} else {
					factorization[i] = false;
				}
			}
			return;
		}
		/*
		 * If we have a topological sort, then we can compute the factorization
		 * nodes
		 */
		for (int i = 0; i < factorization.length; i++) {
			if(taggingStates[i] != TaggingState.USELESS)
				factorization[i] = true;
			else
				factorization[i] = false;
		}
		/*
		 * We have to test if there is a transition from i to j, but with i and
		 * j relative to the sorted states
		 */
		for (int i = 0; i < boxes.length; i++) {
			final int realStateI = sortedNodes[i];
			final TfstGraphBox srcBox = boxes[realStateI];
			for (int j = 1; j < boxes.length; j++) {
				final int realStateJ = sortedNodes[j];
				final TfstGraphBox destBox = boxes[realStateJ];
				if (srcBox.transitions.contains(destBox) && (destBox.hasOutgoingTransitions || destBox.type == GenericGraphBox.FINAL)) {
					for (int k = i + 1; k < j; k++) {
						/*
						 * We can do this only because we have performed a
						 * topological sort before
						 */
						factorization[sortedNodes[k]] = false;
					}
				}
			}
		}
	}
	/** @Yass
	 * incoming holds the size of all incoming reverse transitions. meaning the size of all outgoing transition.
	 * @return
	 */
	private int[] topologicalSort() {
		renumber = new int[boxes.length];
		final int[] incoming = new int[boxes.length];
		final ArrayList<Integer>[] reverse = computeReverseTransitions();
		for (int i = 0; i < incoming.length; i++) {
			incoming[i] = reverse[i].size();
		}
		/*
		 * We set up the renumber array so that renumber[q] will give us the
		 * rank of the node #q after the topological sort
		 */
		for (int q = 0; q < incoming.length; q++) {
			int old = 0;
			while (old < incoming.length && incoming[old] != 0) {
				old++;
			}
			if (old == incoming.length) {
				/*
				 * If that happens, we have automaton that is not acyclic
				 */
				sortedNodes = null;
				renumber = null;
				return null;
			}
			// shouldn't this be renumber[q] = old; ?
			renumber[old] = q;
			incoming[old] = -1;
			for (final GenericGraphBox gb : boxes[old].transitions) {
				final int destIndex = getBoxIndex((TfstGraphBox) gb);
				incoming[destIndex]--;
			}
		}
		/*
		 * Finally, we create another array so that sortedNodes[x] will give us
		 * the index of the node whose rank is x
		 */
		sortedNodes = new int[boxes.length];
		for (int q = 0; q < renumber.length; q++) {
			sortedNodes[renumber[q]] = q;
		}
		return renumber;
	}
	
	/** @Yass
	 * Goes through the boxes, checking accessibility and coaccessiblity, and then tagging them
	 * USELESS if they're neither of those.
	 * or NOT_PREFERRED if they were USELESS but became either of those or both. 
	 */
	private void markUselessStates() {
		final boolean[] accessible = new boolean[boxes.length];
		final boolean[] coaccessible = new boolean[boxes.length];
		checkAccessibility(initialState, accessible);
		final ArrayList<Integer>[] reverse = computeReverseTransitions();
		checkCoaccessibility(finalState, coaccessible, reverse);
		for (int i = 0; i < accessible.length; i++) {
			if (!accessible[i] || !coaccessible[i]) {
				setBoxStateInternal(i, TaggingState.USELESS);
			} else {
				if (taggingStates[i] == TaggingState.USELESS) {
					/*
					 * If the state used to be useless but is not anymore, we
					 * set its state to [NOT_PREFERRED] TO_CHECK as in to be checked 
					 * 
					 */
					
					computeFactorizationNodes();
					/* this is the key location of verification */
					checkNewBranch(i);
				}
			}
}
	}

	/** @Yass
	 * This function takes the Initial state and an empty boolean array and recursively checks if the boxes are accessible, ie
	 * if there's a set of transitions that link the n box to the Initial state.
	 * @param current, box index, starts with the Initial state.
	 * @param visited, boolean array,values start blank, and are set to True if the index box is indeed accessible.
	 */
	private void checkAccessibility(int current, boolean[] visited) {
		if (visited[current])
			return;
		visited[current] = true;
		for (final GenericGraphBox gb : boxes[current].transitions) {
			final int destIndex = getBoxIndex((TfstGraphBox) gb);
			checkAccessibility(destIndex, visited);
		}
	}
	/** @Yass
	 * This function takes the Final state and an empty boolean array and recursively checks if the boxes are coaccessible, ie
	 * if there's a set of transitions that link the n box to the Final state.
	 * This function is basically
	 * @param current, box index, starts with the Final state.
	 * @param visited, boolean array,values start blank, and are set to True if the index box is indeed accessible.
	 * @param reverse, a representation of the graph but reversed, as to see it as basically a reverse accessibility check.
	 */
	private void checkCoaccessibility(int current, boolean[] visited,
			ArrayList<Integer>[] reverse) {
		if (visited[current])
			return;
		visited[current] = true;
		for (final int srcIndex : reverse[current]) {
			checkCoaccessibility(srcIndex, visited, reverse);
		}
	}

	private boolean isFactorizationNode(int boxIndex) {
		if (boxIndex == -1)
			return false;
		return factorization[boxIndex];
	}

	private int getPreviousFactorizationNodeIndex(int boxIndex) {
		if (renumber == null)
			return initialState;
		int pos = renumber[boxIndex];
		if (pos == 0)
			return boxIndex;
		do {
			pos--;
		} while (!factorization[sortedNodes[pos]]);
		return sortedNodes[pos];
	}

	private int getNextFactorizationNodeIndex(int boxIndex) {
		if (renumber == null)
			return finalState;
		int pos = renumber[boxIndex];
		if (pos == boxes.length - 1)
			return boxIndex;
		do {
			pos++;
		} while (pos < factorization.length && !factorization[sortedNodes[pos]]);
		if(pos == factorization.length)
			return sortedNodes[1];
		return sortedNodes[pos];
	}

	
	
	public void preferBox(TfstGraphBox b) {
		final int n = getBoxIndex(b);
		if (n == -1)
			throw new IllegalStateException(
					"Should not be preferring an unknown box");
		preferBox(n);
	}
	
	public void preferBox(int n) {
		if (taggingStates[n] == TaggingState.USELESS) {
			/*
			 * There is no point in preferring a box that cannot be part of the
			 * final remaining path
			 */
			return;
		}
		setBoxStateInternal(n, TaggingState.PREFERRED);
		updateAlternativePaths(n);
		contaminateFollowers(n, getNextFactorizationNodeIndex(n));
		contaminateFollowers2(n, getNextFactorizationNodeIndex(n));
		final ArrayList<Integer>[] reverse = computeReverseTransitions();
		contaminateAncestors(n, getPreviousFactorizationNodeIndex(n), reverse);
		contaminateAncestors2(n, getPreviousFactorizationNodeIndex(n), reverse);
		linearTfst = isLinearAutomaton();
	}

	/**
	 * Once we have set n as a preferred box, we look if all its outgoing
	 * transitions point to NOT_PREFERRED states. If so, those states are
	 * recursively made neutral.
	 */
	// better named PropagatesNeutralToAdjacentRemoved ?
	private void contaminateFollowers(int n, int limit) {
		if (n == limit)
			return;
		for (final GenericGraphBox gb : boxes[n].transitions) {
			final int destIndex = getBoxIndex((TfstGraphBox) gb);
			if (!isNotPreferredModelIndex(destIndex))
				return;
		}
		/*
		 * If we get here, it means that all reachable states were NOT_PREFERRED
		 * or USELESS. Then, we have to make all the NOT_PREFERRED ones neutral,
		 * otherwise, there wouldn't exist any path from n to the limit node
		 */
		for (final GenericGraphBox gb : boxes[n].transitions) {
			final int destIndex = getBoxIndex((TfstGraphBox) gb);
			if (getBoxStateTfst(destIndex) == TaggingState.NOT_PREFERRED) {
				setBoxStateInternal(destIndex, TaggingState.NEUTRAL);
				contaminateFollowers(destIndex, limit);
			}
		}
	}

	/**
	 * If there is only one outgoing transition into a preferred state, then this
	 * state must be preferred as well.
	 */
	private void contaminateFollowers2(int n, int limit) {
		if (n == limit)
			return;
		if (boxes[n].transitions.size() == 1) {
			final int destIndex = getBoxIndex((TfstGraphBox) boxes[n].transitions
					.get(0));
			setBoxStateInternal(destIndex, TaggingState.PREFERRED);
			contaminateFollowers2(destIndex, limit);
		}
	}

	/**
	 * Once we have set n as a preferred box, we look if all its incoming
	 * transitions point to NOT_PREFERRED states. If so, those states are
	 * recursively made neutral.
	 */
	private void contaminateAncestors(int n, int limit,
			ArrayList<Integer>[] reverse) {
		if (n == limit)
			return;
		for (final Integer srcIndex : reverse[n]) {
			if (!isNotPreferredModelIndex(srcIndex))
				return;
		}
		/* See similar comment in contaminateFollowers */
		for (final Integer srcIndex : reverse[n]) {
			if (getBoxStateTfst(srcIndex) == TaggingState.NOT_PREFERRED) {
				setBoxStateInternal(srcIndex, TaggingState.NEUTRAL);
				contaminateAncestors(srcIndex, limit, reverse);
			}
		}
	}

	/**
	 * If there is only one incoming transition from a preferred state, then this
	 * state must be preferred as well.
	 */
	private void contaminateAncestors2(int n, int limit,
			ArrayList<Integer>[] reverse) {
		if (n == limit)
			return;
		if (reverse[n].size() == 1) {
			final int srcIndex = reverse[n].get(0);
			setBoxStateInternal(srcIndex, TaggingState.PREFERRED);
			contaminateAncestors2(srcIndex, limit, reverse);
		}
	}

	@SuppressWarnings("unchecked")
	ArrayList<Integer>[] computeReverseTransitions() {
		final ArrayList<Integer>[] reverse = new ArrayList[boxes.length];
		for (int i = 0; i < reverse.length; i++) {
			reverse[i] = new ArrayList<Integer>();
		}
		for (int i = 0; i < reverse.length; i++) {
			final TfstGraphBox box = boxes[i];
			if (box == null)
				continue;
			/* We explore all outgoing transitions */
			for (final GenericGraphBox gb : box.transitions) {
				final int destIndex = getBoxIndex((TfstGraphBox) gb);
				reverse[destIndex].add(i);
			}
		}
		return reverse;
	}

	/**
	 * If we prefer a box that is not a factorization node, then we have to
	 * update the state of boxes on alternative paths
	 */
	private void updateAlternativePaths(int boxIndex) {
		if (isFactorizationNode(boxIndex))
			return;
		final int a = getPreviousFactorizationNodeIndex(boxIndex);
		final int b = getNextFactorizationNodeIndex(boxIndex);
		final boolean[] visited = new boolean[boxes.length];
		final boolean[] toBeKept = new boolean[boxes.length];
		for (int i = 0; i < visited.length; i++) {
			toBeKept[i] = true;
		}
		/*
		 * We have marked candidates, but now we have to unmark states that can
		 * be reached from the preferred box
		 */
		update(a, b, boxIndex, toBeKept, visited);
		/*
		 * Finally, we actually set NOT_PREFERRED status for states that remain
		 * marked
		 */
		for (int i = 0; i < visited.length; i++) {
			visited[i] = false;
		}
		unmark(boxIndex, b, toBeKept, visited);
		for (int i = 0; i < toBeKept.length; i++) {
			if (!toBeKept[i]) {
				setBoxStateInternal(i, TaggingState.NOT_PREFERRED);
			}
		}
	}

	private void unmark(int current, int limit, boolean[] toBeKept,
			boolean[] visited) {
		if (visited[current] || current == limit)
			return;
		toBeKept[current] = true;
		/* We explore all outgoing transitions */
		for (final GenericGraphBox gb : boxes[current].transitions) {
			final int destIndex = getBoxIndex((TfstGraphBox) gb);
			unmark(destIndex, limit, toBeKept, visited);
		}
		visited[current] = true;
	}

	/**
	 * current and limit are nodes and n is a preferred box. current will be mark
	 * as NOT_PREFERRED if there is no path from it n.
	 */
	private boolean update(int current, int limit, int n, boolean[] toBeKept,
			boolean[] visited) {
		if (current == n) {
			return true;
		}
		if (current == limit) {
			return false;
		}
		if (visited[current]) {
			return toBeKept[current];
		}
		boolean foundAPath = false;
		final TfstGraphBox b = boxes[current];
		/* We explore all outgoing transitions */
		for (final GenericGraphBox gb : b.transitions) {
			final int destIndex = getBoxIndex((TfstGraphBox) gb);
			if (update(destIndex, limit, n, toBeKept, visited)) {
				foundAPath = true;
			}
		}
		if (!foundAPath) {
			toBeKept[current] = false;
		}
		visited[current] = true;
		return foundAPath;
	}

	private void setBoxStateInternal(int boxIndex, TaggingState state) {
		if (boxIndex == -1)
			return;
		/* The initial and final states should never be modified */
		if (boxIndex == initialState || boxIndex == finalState)
			return;
		taggingStates[boxIndex] = state;
		boxes[boxIndex].state = state;
	}

	int getBoxIndex(TfstGraphBox b) {
		if (b == null)
			return -1;
		for (int i = 0; i < boxes.length; i++) {
			if (b.equals(boxes[i]))
				return i;
		}
		// If the box is unknown then we return -1
		return -1;
	}

	public TaggingState getBoxState(TfstGraphBox b) {
		if (b.type != GenericGraphBox.NORMAL) {
			/*
			 * The initial and final states should always be considered as
			 * neutral states
			 */
			return TaggingState.NEUTRAL;
		}
		final int index = getBoxIndex(b);
		// If the box is unknown then it is considered as neutral
		if (index == -1)
			return TaggingState.NEUTRAL;
		return taggingStates[index];
	}

	/**
	 * NOTE: the given box number is relative to the box array list of the
	 * graphical zone
	 */
	public TaggingState getBoxStateTfst(int boxIndexInTfst) {
		final TfstGraphBox box = (TfstGraphBox) zone.graphBoxes
				.get(boxIndexInTfst);
		return getBoxState(box);
	}

	public boolean isPreferred(int boxIndex) {
		return TaggingState.PREFERRED == getBoxStateTfst(boxIndex);
	}

	public boolean isNotPreferred(TfstGraphBox box) {
		final int boxIndex = getBoxIndex(box);
		final TaggingState s = taggingStates[boxIndex];
		return TaggingState.NOT_PREFERRED == s || TaggingState.USELESS == s;
	}

	public boolean isNotPreferredTfstIndex(int boxIndex) {
		final TaggingState s = getBoxStateTfst(boxIndex);
		return TaggingState.NOT_PREFERRED == s || TaggingState.USELESS == s;
	}

	public boolean isNotPreferredModelIndex(int boxIndex) {
		final TaggingState s = taggingStates[boxIndex];
		return TaggingState.NOT_PREFERRED == s || TaggingState.USELESS == s;
	}

	public void updateAutomatonLinearity() {
		linearTfst=isLinearAutomaton();
	}
	
	private boolean isLinearAutomaton() {
		int current = initialState;
		while (current != finalState) {
			int selectedOutgoing = -1;
			final TfstGraphBox b = boxes[current];
			/* We explore all outgoing transitions */
			for (final GenericGraphBox gb : b.transitions) {
				final int destIndex = getBoxIndex((TfstGraphBox) gb);
				if (!isNotPreferredModelIndex(destIndex)) {
					if (selectedOutgoing == -1) {
						selectedOutgoing = destIndex;
					} else {
						/*
						 * It's the second transition to a preferred or neutral
						 * state, so we don't have a linear automaton
						 */
						return false;
					}
				}
			}
			if (selectedOutgoing == -1) {
				/*
				 * By convention, we do not consider that an automaton with no
				 * path is a valid linear one
				 */
				return false;
			}
			current = selectedOutgoing;
		}
		return true;
	}

	public boolean isLinearTfst() {
		return linearTfst;
	}
	
	public TaggingState[] getTaggingStates() {
		return taggingStates;
	}
	
	public void setTaggingStates(TaggingState[] selection) {
		if (selection!=null && taggingStates.length!=selection.length) {
			return;
		}
		taggingStates=selection;
	}

	/*public void updateBoundsDiffToken(ArrayList<GenericGraphBox> selectedBoxes, TfstGraphBox b) {
		int t = selectedBoxes.get(0).getBoxNumber();
		System.err.println("The value of " + t + " is bigger than the number of boxes");
		Bounds temp;
		if(t > boxes.length)
			temp = new Bounds(b.getBounds().getGlobal_start_in_chars(), b.getBounds().getGlobal_end_in_chars());
		else
			temp = new Bounds(boxes[t].getBounds());
		
		int i = 2;
		
		int j = 0;
		
		if( b.getContent().equals("-") || b.getContent().equals("/") || b.getContent().equals("'") 
				|| b.getContent().equals(".") || b.getContent().equals(","))
			i = 1;
		
		if(b.getContent().contains(" "))
			j = 2;
		
		temp.setStart_in_tokens(temp.getStart_in_tokens()+i);
		
		if(b.getBounds() != null) {
			temp.setStart_in_letters(b.getBounds().getStart_in_letters());
			temp.setStart_in_chars(b.getBounds().getStart_in_chars());
			temp.setEnd_in_letters(b.getBounds().getEnd_in_letters());
			temp.setEnd_in_chars(b.getBounds().getEnd_in_chars());
		}
		if(t == 0) {
			temp.setStart_in_tokens(0);
		}
		
		temp.setEnd_in_tokens(temp.getStart_in_tokens()+j);
		
		b.setBounds( temp );
	}
	
	public void updateBoundsSameToken(ArrayList<GenericGraphBox> selectedBoxes, TfstGraphBox b) {
		b.setBounds( new Bounds(boxes[selectedBoxes.get(0).getBoxNumber()].getBounds()) );
	}*/
	
}
