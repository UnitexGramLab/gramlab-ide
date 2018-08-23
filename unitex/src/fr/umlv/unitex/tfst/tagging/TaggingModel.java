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
package fr.umlv.unitex.tfst.tagging;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import fr.umlv.unitex.graphrendering.GenericGraphBox;
import fr.umlv.unitex.graphrendering.TfstGraphBox;
import fr.umlv.unitex.graphrendering.TfstGraphicalZone;
import fr.umlv.unitex.listeners.GraphListener;
import fr.umlv.unitex.tfst.Bounds;

/**
 * This class is used to know whether a sentence automaton box has been
 * hand-selected or not for tagging purpose.
 * 
 * @author paumier
 */
public class TaggingModel {
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
	private boolean linearTfst;
	
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
				
				updateFactorizationNodes();
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
			if (boxes[i].type == GenericGraphBox.INITIAL)
				initialState = i;
			else if (boxes[i].type == GenericGraphBox.FINAL)
				finalState = i;
			taggingStates[i] = TaggingState.NEUTRAL;
		}
		updateFactorizationNodes();
		generateTokensList();
		System.out.println("TOKENS : "+ tokens);
	}
	
	/*
	 * for each box we will start at it's starting bound, split the inside based on delimiters and add them to the hashmap. 
	 * if 2 words split by a comma are found, they will be stored subsequently in the hashmap at the starting bound's value, then +1 and so on. 
	 */
	public void generateTokensList() {
		for( TfstGraphBox gb : boxes ) {
			if( gb == null )
				continue;
			
			if( gb.getBoxNumber() == 0 || gb.getBoxNumber() == 1)
				continue;
			
			int index = gb.getBounds().getStart_in_tokens();
			String temp;
			String[] tokensList;
			
			if( gb.getContent().contains("{")  )
				temp = gb.getContent().split(",")[0].substring(1);
			else {
				temp = gb.getContent();
			}
			System.out.println("temp : "+temp);
            tokensList = Pattern.compile("\\b").split(temp);
            System.out.println("tokensList : ");
            for( String s : tokensList ) {
            	System.out.println(s);
            	if( !tokens.containsKey(index) )  
            		tokens.put(index, new ArrayList<String>());
            	if( !tokens.get(index).contains(s))
            		tokens.get(index).add(s);
            	System.out.println();
            	index++;
            }
		}
		 for ( int i=0; i<tokens.size();i++ )
         	if( !tokens.containsKey(i) ) {
         		tokens.put( i, new ArrayList<String>());
         		tokens.get(i).add(" ");
         	}
	}
	
	void checkNewBranch( int i ){
		int prev = getPreviousFactorizationNodeIndex(renumber[i]);
		int next = getNextFactorizationNodeIndex(renumber[i]);
		StringBuilder newTokenBranch=new StringBuilder("");
		int initialI = i;
		String temp;
		while( boxes[i].type == TfstGraphBox.NORMAL && taggingStates[i] == TaggingState.USELESS ) {
			if( boxes[i].getContent().contains("{"))
				temp = boxes[i].getContent().split(",")[0].substring(1);
			else {
				temp = boxes[i].getContent();
			}
			System.out.println("i + temp :"+ i +" "+ temp);
			newTokenBranch.append(temp+" ");
			setBoxStateInternal(i, TaggingState.NEUTRAL);
			i = getBoxIndex((TfstGraphBox)(boxes[i].transitions.get(0)));
			System.out.println("tokens to verify : "+newTokenBranch.toString());
		}

		/* comparing and updating bounds here */
		System.out.println("prev ? "+boxes[prev].getContent());
		System.out.println("next ? "+boxes[next].getContent());
		i = initialI;
		for( int j = boxes[prev].getBounds().getStart_in_tokens()+2 ; j < boxes[next].getBounds().getStart_in_tokens() ; j=j+2 ) {
			
			System.out.println("j ?" + j);
			boolean isItMatching=false;
//			if( tokens.get(j) == null && newTokenBranch.toString().startsWith(" ") ) {
//				System.out.println("case space");
//				newTokenBranch.delete( 0,1 );
//				isItMatching = true;
//				break;
//			}
//			if( tokens.get(j).equals(",") && newTokenBranch.toString().startsWith(",") ) {
//				newokenBranch.delete( 0,1 );
//				isItMatching = true;
//				break;
//			}
			for ( String s : tokens.get(j) ) {
				System.out.println("testing : "+s+" ?"+newTokenBranch);
				if( s.equals("<E>")) break;
				if( newTokenBranch.toString().startsWith(s) ) {
					isItMatching = true;
					newTokenBranch.delete(0,s.length() );
					if( newTokenBranch.toString().startsWith(" ")) newTokenBranch.delete(0, 1);
					
					
					Bounds b = new Bounds(j,0,0,j,0,0); // j est une valeur temporaire
					System.out.println("box number :"+i);
					boxes[i].setBounds(b);
					i = boxes[i].transitions.get(0).getBoxNumber();
							
				}
			}
			
			if( !isItMatching ) {
				JOptionPane.showMessageDialog(null,
						"Content of new box isn't matching the content in parallel paths",
						"please try again",
						JOptionPane.PLAIN_MESSAGE);
				boxes[prev].removeTransitionTo(boxes[initialI]);
				boxes[initialI].removeTransitionTo(boxes[next]);
				
			}
		}
		
		
		
	/*
	  JOptionPane.showMessageDialog(null,
							"Everything looks OK",
							"OK",
							JOptionPane.PLAIN_MESSAGE);	
	 */
	}
	
	
	/** 
	 * 
	 */
//	public void updateBoundsOfNextUseless( TfstGraphBox firstBox, TfstGraphBox SecondBox) {
//		if( taggingStates[SecondBox.getBoxNumber()] == TaggingState.SELECTED || taggingStates[SecondBox.getBoxNumber()] == TaggingState.NEUTRAL )
//			return;
//		Bounds bounds = new Bounds(firstBox.getBounds());
//		bounds.setStart_in_tokens(bounds.getStart_in_tokens()+2);
//		bounds.setEnd_in_tokens(bounds.getEnd_in_tokens()+2);
//		SecondBox.setBounds(bounds);
//	}
	
	private void updateBoundsReversed( int current, boolean[] visited ) {
		final ArrayList<Integer>[] reverse = computeReverseTransitions();
		if(visited.length == 0 )
			return;
		
		if (visited[current])
			return;
		visited[current] = true;
		for (final int destIndex : reverse[current]) {
			if( boxes[current].type == TfstGraphBox.NORMAL && ( taggingStates[destIndex] == TaggingState.TO_BE_REMOVED || taggingStates[destIndex] == TaggingState.USELESS ) ) {
				Bounds b = new Bounds(boxes[current].getBounds());
				b.setStart_in_tokens(b.getStart_in_tokens()-2);
				b.setEnd_in_tokens(b.getEnd_in_tokens()-2);
				boxes[destIndex].setBounds(b);
				
			}
			updateBoundsReversed(destIndex, visited);
		}
	}
	
	private void updateBounds( int current, boolean[] visited ) {
		
		if(visited.length == 0 )
			return;
		
		if (visited[current])
			return;
		visited[current] = true;
		for (final GenericGraphBox gb : boxes[current].transitions) {
			final int destIndex = getBoxIndex((TfstGraphBox) gb);

			if( boxes[current].type == TfstGraphBox.NORMAL && ( taggingStates[destIndex] == TaggingState.TO_BE_REMOVED || taggingStates[destIndex] == TaggingState.USELESS ) ) {
				Bounds b = new Bounds(boxes[current].getBounds());
				b.setStart_in_tokens(b.getStart_in_tokens()+2);
				b.setEnd_in_tokens(b.getEnd_in_tokens()+2);
				boxes[destIndex].setBounds(b);
				//System.out.println("box destIndex bounds :\t" + boxes[destIndex].getBounds().toString() +" | " + b.toString());
			}
			updateBounds(destIndex, visited);
		}
	}
		
	/**
	 * 
	 */
	private void updateFactorizationNodes() {
		
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
		/* And finally, we can mark as selected all factorization nodes */
		for (int i = 0; i < factorization.length; i++) {
			if (factorization[i] || taggingStates[i] == TaggingState.SELECTED) {
				selectBox(boxes[i]);
			}
		}
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
				if (boxes[i].type != GenericGraphBox.NORMAL) {
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
			factorization[i] = true;
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
				if (srcBox.transitions.contains(destBox)) {
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
	 * or TO_BE_REMOVED if they were USELESS but became either of those or both. 
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
					 * set its state to [TO_BE_REMOVED] TO_CHECK as in to be checked 
					 * 
					 */
					computeFactorizationNodes();
					/* this is the key location of verification */
					checkNewBranch( i );
					
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
		} while (!factorization[sortedNodes[pos]]);
		return sortedNodes[pos];
	}

	
	
	public void selectBox(TfstGraphBox b) {
		final int n = getBoxIndex(b);
		if (n == -1)
			throw new IllegalStateException(
					"Should not be selecting an unknown box");
		selectBox(n);
	}
	
	public void selectBox(int n) {
		if (taggingStates[n] == TaggingState.USELESS) {
			/*
			 * There is no point in selecting a box that cannot be part the
			 * final remaining path
			 */
			return;
		}
		setBoxStateInternal(n, TaggingState.SELECTED);
		updateAlternativePaths(n);
		contaminateFollowers(n, getNextFactorizationNodeIndex(n));
		contaminateFollowers2(n, getNextFactorizationNodeIndex(n));
		final ArrayList<Integer>[] reverse = computeReverseTransitions();
		contaminateAncestors(n, getPreviousFactorizationNodeIndex(n), reverse);
		contaminateAncestors2(n, getPreviousFactorizationNodeIndex(n), reverse);
		linearTfst = isLinearAutomaton();
	}

	/**
	 * Once we have set n as a selected box, we look if all its outgoing
	 * transitions point to TO_BE_REMOVED states. If so, those states are
	 * recursively made neutral.
	 */
	// better named PropagatesNeutralToAdjacentRemoved ?
	private void contaminateFollowers(int n, int limit) {
		if (n == limit)
			return;
		for (final GenericGraphBox gb : boxes[n].transitions) {
			final int destIndex = getBoxIndex((TfstGraphBox) gb);
			if (!isToBeRemovedModelIndex(destIndex))
				return;
		}
		/*
		 * If we get here, it means that all reachable states were TO_BE_REMOVED
		 * or USELESS. Then, we have to make all the TO_BE_REMOVED ones neutral,
		 * otherwise, there wouldn't exist any path from n to the limit node
		 */
		for (final GenericGraphBox gb : boxes[n].transitions) {
			final int destIndex = getBoxIndex((TfstGraphBox) gb);
			if (getBoxStateTfst(destIndex) == TaggingState.TO_BE_REMOVED) {
				setBoxStateInternal(destIndex, TaggingState.NEUTRAL);
				contaminateFollowers(destIndex, limit);
			}
		}
	}

	/**
	 * If there is only one outgoing transition from a selected state, then this
	 * state must be selected as well.
	 */
	private void contaminateFollowers2(int n, int limit) {
		if (n == limit)
			return;
		if (boxes[n].transitions.size() == 1) {
			final int destIndex = getBoxIndex((TfstGraphBox) boxes[n].transitions
					.get(0));
			setBoxStateInternal(destIndex, TaggingState.SELECTED);
			contaminateFollowers2(destIndex, limit);
		}
	}

	/**
	 * Once we have set n as a selected box, we look if all its incoming
	 * transitions point to TO_BE_REMOVED states. If so, those states are
	 * recursively made neutral.
	 */
	private void contaminateAncestors(int n, int limit,
			ArrayList<Integer>[] reverse) {
		if (n == limit)
			return;
		for (final Integer srcIndex : reverse[n]) {
			if (!isToBeRemovedModelIndex(srcIndex))
				return;
		}
		/* See similar comment in contaminateFollowers */
		for (final Integer srcIndex : reverse[n]) {
			if (getBoxStateTfst(srcIndex) == TaggingState.TO_BE_REMOVED) {
				setBoxStateInternal(srcIndex, TaggingState.NEUTRAL);
				contaminateAncestors(srcIndex, limit, reverse);
			}
		}
	}

	/**
	 * If there is only one incoming transition from a selected state, then this
	 * state must be selected as well.
	 */
	private void contaminateAncestors2(int n, int limit,
			ArrayList<Integer>[] reverse) {
		if (n == limit)
			return;
		if (reverse[n].size() == 1) {
			final int srcIndex = reverse[n].get(0);
			setBoxStateInternal(srcIndex, TaggingState.SELECTED);
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
	 * If we select a box that is not a factorization node, then we have to
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
		 * be reached from the selected box
		 */
		update(a, b, boxIndex, toBeKept, visited);
		/*
		 * Finally, we actually set TO_BE_REMOVED status for states that remain
		 * marked
		 */
		for (int i = 0; i < visited.length; i++) {
			visited[i] = false;
		}
		unmark(boxIndex, b, toBeKept, visited);
		for (int i = 0; i < toBeKept.length; i++) {
			if (!toBeKept[i]) {
				setBoxStateInternal(i, TaggingState.TO_BE_REMOVED);
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
	 * current and limit are nodes and n is a selected box. current will be mark
	 * as TO_BE_REMOVED if there is no path from it n.
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

	public boolean isSelected(int boxIndex) {
		return TaggingState.SELECTED == getBoxStateTfst(boxIndex);
	}

	public boolean isToBeRemoved(TfstGraphBox box) {
		final int boxIndex = getBoxIndex(box);
		final TaggingState s = taggingStates[boxIndex];
		return TaggingState.TO_BE_REMOVED == s || TaggingState.USELESS == s;
	}

	public boolean isToBeRemovedTfstIndex(int boxIndex) {
		final TaggingState s = getBoxStateTfst(boxIndex);
		return TaggingState.TO_BE_REMOVED == s || TaggingState.USELESS == s;
	}

	public boolean isToBeRemovedModelIndex(int boxIndex) {
		final TaggingState s = taggingStates[boxIndex];
		return TaggingState.TO_BE_REMOVED == s || TaggingState.USELESS == s;
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
				if (!isToBeRemovedModelIndex(destIndex)) {
					if (selectedOutgoing == -1) {
						selectedOutgoing = destIndex;
					} else {
						/*
						 * It's the second transition to a selected or neutral
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
	
}
