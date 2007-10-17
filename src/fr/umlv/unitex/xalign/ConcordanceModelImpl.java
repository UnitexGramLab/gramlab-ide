/*
 * Unitex
 *
 * Copyright (C) 2001-2007 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package fr.umlv.unitex.xalign;

import java.util.*;

import javax.swing.event.*;

public class ConcordanceModelImpl implements ConcordanceModel {

	private XMLTextModel model;
	private int mode=TEXT;
	
	private ArrayList<Integer> matchedSentences;
	Object[] occurrenceArray;
	
	
	@SuppressWarnings("unchecked")
	public ConcordanceModelImpl(XMLTextModel model) {
		this.model=model;
		matchedSentences=new ArrayList<Integer>();
		occurrenceArray=new Object[model.getSize()];
		model.addListDataListener(new ListDataListener() {

			public void intervalAdded(ListDataEvent e) {
				int oldSize=e.getIndex0();
				int newSize=e.getIndex1()+1;
				occurrenceArray=Arrays.copyOf(occurrenceArray,newSize);
				fireIntervalAdded(this,oldSize,newSize);
			}

			public void intervalRemoved(ListDataEvent e) {/* nothing to do */}

			public void contentsChanged(ListDataEvent e) {/* nothing to do */}
		});
	}
	
	
	public int getSize() {
		if (getMode()!=MATCHES) {
			return model.getSize();
		}
		return matchedSentences.size();
	}

	public int getSentence(int index) {
		if (getMode()!=MATCHES) return index;
		return matchedSentences.get(index);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Occurrence> getOccurrences(int index) {
		return (ArrayList<Occurrence>) occurrenceArray[getSentence(index)];
	}
	

	/**
	 * When we are in MATCHES mode, this method is used to know which is the visible
	 * index of the given sentence. -1 means that the sentence is not visible. 
	 */
	public int getSentenceIndex(int sentence) {
		if (getMode()!=MATCHES) return sentence;
		if (matchedSentences.size()==0) return -1;
		int start=0;
		int end=matchedSentences.size()-1;
		while (start<=end) {
			int tmp=(start+end)/2;
			int x=matchedSentences.get(tmp);
			if (x==sentence) {
				return tmp;
			}
			if (x>sentence) {
				end=tmp-1;
			} else {
				start=tmp+1;
			}
		}
		return -1;
	}

	
	/**
	 * Returns true if there is at least one match for the given sentence.
	 */
	public boolean isMatchedSentenceNumber(int sentence) {
		return (occurrenceArray[sentence]!=null);
	}

	/**
	 * Returns true if there is at least one match for the given sentence.
	 */
	public boolean isMatchedSentenceIndex(int index) {
		int sentence=getSentence(index);
		return (occurrenceArray[sentence]!=null);
	}

	
	/**
	 * Adds a new match to the model.
	 */
	@SuppressWarnings("unchecked")
	public void addMatch(int sentence,Occurrence match) {
		if (sentence>=model.getSize()) {
			System.err.println("Sentence index out of bounds:  index="+sentence+"  model size="+model.getSize());
			return;
		}
		int position_to_insert=findPositionToInsert(sentence);
		if (position_to_insert!=-1) {
			matchedSentences.add(position_to_insert,sentence);
		}
		ArrayList<Occurrence> list=(ArrayList<Occurrence>)occurrenceArray[sentence];
		if (list==null) {
			occurrenceArray[sentence]=list=new ArrayList<Occurrence>();
		}
		list.add(match);
		switch(getMode()) {
			case TEXT: /* nothing to do */ break;
			case MATCHES: {
				if (position_to_insert!=-1) {
					/* If we just have inserted a new matched sentence in MATCHES
					 * mode, it means that we have added an element */
					fireIntervalAdded(this,position_to_insert,position_to_insert);
					break;
				}
				/* Otherwise, we just have changed the content of an existing cell */
				fireContentChanged(this,position_to_insert,position_to_insert);
				break;
			}
			case BOTH: {
				/* We just have to inform the JList that the sentence must be repainted */
				fireContentChanged(this,sentence,sentence);
				break;
			}
		}
	}

	
	/**
	 * This method computes the position in matchedSentences where
	 * to insert sentence, so that the array remains sorted. -1
	 * means that the value is allready in the array.
	 */
	private int findPositionToInsert(int sentence) {
		int n=matchedSentences.size();
		int lastValue;
		if (n==0 || matchedSentences.get(0)>sentence) {
			return 0;
		}
		if (matchedSentences.get(0)==sentence) return -1;
		if ((lastValue=matchedSentences.get(n-1))<sentence) {
			return n;
		}
		if (lastValue==sentence) {
			return -1;
		}
		int nextValue;
		for (int position_to_insert=1;position_to_insert<n;position_to_insert++) {
			nextValue=matchedSentences.get(position_to_insert);
			if (nextValue==sentence) {
				/* If the sentence is allready there */ 
				return -1;
			}
			if (sentence<nextValue) return position_to_insert;
		}
		/* We should never arrive here */
		return n;
	}


	/**
	 * Returns the given sentence in plain text, or in HTML if 
	 * 1) it's a matched sentence
	 * 2) we are not in TEXT mode
	 */
	public String getElementAt(int index) {
		int sentence=index;
		if (getMode()==MATCHES) {
			sentence=getSentence(index);
		}
		if (getMode()==TEXT || !isMatchedSentenceNumber(sentence)) {
			return model.getElementAt(sentence);
		}
		return createMatchedSentenceHTML(sentence);
	}
	
	
	StringBuilder builder=new StringBuilder();
	private final static int PLAIN=0;
	private final static int MATCH=1;

	@SuppressWarnings("unchecked")
	private String createMatchedSentenceHTML(int sentence) {
		builder.setLength(0);
		//builder.append("<html>");
		List<Occurrence> occurrences=(List<Occurrence>) occurrenceArray[sentence];
		String s=model.getElementAt(sentence);
		ArrayList<Integer> edges=createArray(s.length(),occurrences);
		int currentMode=edges.get(0);
		int start=0;
		for (int i=1;i<edges.size();i++) {
			if (currentMode==MATCH) {
				builder.append("<font color=\"blue\"><u>");
			}
			int end=edges.get(i)+1;
			while (start<end) {
				char c=s.charAt(start);
				switch (c) {
					case '&': builder.append("&amp;"); break;
					case '<': builder.append("&lt;"); break;
					case '>': builder.append("&gt;"); break;
					default: builder.append(c);
				}
			   start++;
			}
			if (currentMode==MATCH) {
				builder.append("</u></font>");
			}
			currentMode=1-currentMode;
		}
		//builder.append("</html>");
		return builder.toString();
	}

	private ArrayList<Integer> createArray(int n,List<Occurrence> occurrences) {
		int[] array=new int[n];
		for (int i=0;i<n;i++) {
			array[i]=PLAIN;
		}
		for (Occurrence o:occurrences) {
			int start=o.getStart();
			if (start>=n) continue;
			int end=o.getEnd();
			if (end>=n) end=n-1;
			for (int j=start;j<=end;j++) {
				array[j]=MATCH;
			}
		}
		ArrayList<Integer> edges=new ArrayList<Integer>();
		edges.add(array[0]);
		for (int i=1;i<n;i++) {
			if (array[i]!=array[i-1]) {
				edges.add(i-1);
			}
		}
		edges.add(n-1);
		return edges;
	}


	ArrayList<ListDataListener> listeners=new ArrayList<ListDataListener>();
	
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}
	
	protected void fireIntervalAdded(Object source,int start,int end) {
		ListDataEvent event=new ListDataEvent(source,ListDataEvent.INTERVAL_ADDED,start,end);
		for (ListDataListener l:listeners) {
			l.intervalAdded(event);
		}
	}
	
	protected void fireIntervalRemoved(Object source,int start,int end) {
		ListDataEvent event=new ListDataEvent(source,ListDataEvent.INTERVAL_REMOVED,start,end);
		for (ListDataListener l:listeners) {
			l.intervalRemoved(event);
		}
	}
	
	protected void fireContentChanged(Object source,int start,int end) {
		ListDataEvent event=new ListDataEvent(source,ListDataEvent.CONTENTS_CHANGED,start,end);
		for (ListDataListener l:listeners) {
			l.contentsChanged(event);
		}
	}


	public void setMode(int mode) {
		if (mode<0 || mode>2) {
			throw new IllegalArgumentException();
		}
		int oldSize=getSize();
		this.mode=mode;
		int newSize=getSize();
		if (oldSize<newSize) {
			/* If we have to add elements */
			fireIntervalAdded(this,oldSize,newSize-1);
		}
		else if (oldSize>newSize) {
			/* If we have to remove elements */
			fireIntervalRemoved(this,oldSize-1,newSize);
		}
		/* Finally, we say that the cell must be repainted */
		fireContentChanged(this,0,newSize-1);
	}


	public int getMode() {
		return mode;
	}


	public XMLTextModel getModel() {
		return model;
	}


	public void refresh() {
		fireContentChanged(this,0,getSize()-1);
	}
}
