/*
 * Unitex
 *
 * Copyright (C) 2001-2008 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
	private DisplayMode mode=DisplayMode.TEXT;
	private boolean source;
	
	/* This array contains the indices of matched sentences */
	private ArrayList<Integer> matchedSentences;
	
	/* This array contains the indices of sentences that are aligned
	 * with matched sentences of the other text */
	private ArrayList<Integer> alignedWithMatchedSentences;
	
	/* This array contains the union of matchedSentences and
	 * alignedWithMatchedSentences */
	private ArrayList<Integer> alignedModeSentences;

	
	/* The real type of occurrenceArray is List<Occurrence>[], but
	 * we can't declare such a thing. */
	Object[] occurrenceArray;
	
	
	@SuppressWarnings("unchecked")
	public ConcordanceModelImpl(XMLTextModel model,boolean source) {
		this.model=model;
		this.source=source;
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
		if (getMode()==DisplayMode.ALIGNED) {
			return alignedModeSentences.size();
		}
		else if (getMode()!=DisplayMode.MATCHES) {
			return model.getSize();
		}
		return matchedSentences.size();
	}

	public int getNumberOfSentences() {
		return model.getSize();
	}

	public int getSentence(int index) {
		if (index==-1) return -1;
		if (getMode()==DisplayMode.ALIGNED) return alignedModeSentences.get(index);
		if (getMode()!=DisplayMode.MATCHES) return index;
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
		if (getMode()!=DisplayMode.MATCHES && getMode()!=DisplayMode.ALIGNED) return sentence;
		ArrayList<Integer> array=matchedSentences;
		if (getMode()==DisplayMode.ALIGNED) {
			array=alignedModeSentences;
		}
		if (array.size()==0) return -1;
		int start=0;
		int end=array.size()-1;
		while (start<=end) {
			int tmp=(start+end)/2;
			int x=array.get(tmp);
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
		if (getMode()==DisplayMode.TEXT) {
			/* nothing to do */
		}
		else if (getMode()==DisplayMode.MATCHES) {
			if (position_to_insert!=-1) {
				/* If we just have inserted a new matched sentence in MATCHES
				 * mode, it means that we have added an element */
				fireIntervalAdded(this,position_to_insert,position_to_insert);
			}
			/* Otherwise, we just have changed the content of an existing cell */
			fireContentChanged(this,position_to_insert,position_to_insert);
		}
		else if (getMode()==DisplayMode.MATCHES) {
			/* We just have to inform the JList that the sentence must be repainted */
			fireContentChanged(this,sentence,sentence);
		}
		else {
			/* We just have to inform the JList that the sentence must be repainted */
			fireContentChanged(this,sentence,sentence);
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
		if (getMode()==DisplayMode.MATCHES || getMode()==DisplayMode.ALIGNED) {
			sentence=getSentence(index);
		}
		if (getMode()==DisplayMode.TEXT || !isMatchedSentenceNumber(sentence)) {
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
		ArrayList<Integer> edges=createArray(s,occurrences);
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
					case 13: /* do nothing */ break;
					case 10: builder.append("<br>"); break;
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

	private ArrayList<Integer> createArray(String s,List<Occurrence> occurrences) {
		int n=s.length();
		int[] array=new int[n];
		for (int i=0;i<n;i++) {
			array[i]=PLAIN;
		}
		for (Occurrence o:occurrences) {
			int start=getRealOffset(s,o.getStart());
			if (start>=n) continue;
			int end=getRealOffset(s,o.getEnd());
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


	/**
	 * This function takes an offset in a string and computes the
	 * real offset, i.e. the offset+n where n is the number of '\n'
	 * before offset. This is necessary because XAlign concord.txt
	 * offset are based on a calculus that counts '\r\n' as a single char.
	 */
	private int getRealOffset(String s,int offset) {
		int realOffset=offset;
		for (int i=0;i<offset;i++) {
			if (s.charAt(i)==10) {
				realOffset++;
			}
		}
		return realOffset;
	}


	ArrayList<ListDataListener> listeners=new ArrayList<ListDataListener>();
	
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}
	
	protected void fireIntervalAdded(Object source1,int start,int end) {
		ListDataEvent event=new ListDataEvent(source1,ListDataEvent.INTERVAL_ADDED,start,end);
		for (ListDataListener l:listeners) {
			l.intervalAdded(event);
		}
	}
	
	protected void fireIntervalRemoved(Object source1,int start,int end) {
		ListDataEvent event=new ListDataEvent(source1,ListDataEvent.INTERVAL_REMOVED,start,end);
		for (ListDataListener l:listeners) {
			l.intervalRemoved(event);
		}
	}
	
	protected void fireContentChanged(Object source1,int start,int end) {
		ListDataEvent event=new ListDataEvent(source1,ListDataEvent.CONTENTS_CHANGED,start,end);
		for (ListDataListener l:listeners) {
			l.contentsChanged(event);
		}
	}


	/**
	 * This method gets the list L of matched sentences in the other text.
	 * Then, it computes the list of sentences that are aligned with elements
	 * of L. Finally, it builds a sorted array containing -/-*-both matched sentences
	 * and-*-/- sentences aligned with matched sentences in the other text.
	 */
	void computeAlignedWithMatched(ConcordanceModel otherModel1) {
		ArrayList<Integer> otherMatchedSentences=otherModel1.getMatchedSentences();
		if (alignedWithMatchedSentences==null) {
			alignedWithMatchedSentences=new ArrayList<Integer>();
		} else {
			alignedWithMatchedSentences.clear();
		}
		if (alignedModeSentences==null) {
			alignedModeSentences=new ArrayList<Integer>();
		} else {
			alignedModeSentences.clear();
		}
		XAlignModel alignmentModel=XAlignFrame.model;
		for (Integer i:otherMatchedSentences) {
			alignedWithMatchedSentences.addAll(alignmentModel.getAlignedSequences(i,!source));
		}
		/*for (Integer i:matchedSentences) {
			alignedModeSentences.add(new Integer(i));
		}*/
		for (Integer i:alignedWithMatchedSentences) {
			alignedModeSentences.add(new Integer(i));
		}
		Collections.sort(alignedModeSentences);
	}
	
	ListDataListener alignModeDataLister;
	
	/**
	 * We may need to know the model of the other text, because of
	 * the DisplayMode.ALIGNED mode that requires to know which sentences
	 * of the other text are matched. 
	 */
	public void setMode(DisplayMode mode,final ConcordanceModel otherModel) {
		int oldSize=getSize();
		if (mode==DisplayMode.ALIGNED) {
			/* If we must look at the matched sentence of the other text */
			computeAlignedWithMatched(otherModel);
			alignModeDataLister=new ListDataListener() {

				public void intervalAdded(ListDataEvent e) {
					int oldSize1=getSize();
					computeAlignedWithMatched(otherModel);
					update(oldSize1,getSize());
				}

				public void intervalRemoved(ListDataEvent e) {
					int oldSize1=getSize();
					computeAlignedWithMatched(otherModel);
					update(oldSize1,getSize());
				}

				public void contentsChanged(ListDataEvent e) {
					int oldSize1=getSize();
					computeAlignedWithMatched(otherModel);
					update(oldSize1,getSize());
				}};
			if (otherModel.getMode()!=DisplayMode.ALIGNED) {
				/* We don't want to create a listener loop */ 
				otherModel.addListDataListener(alignModeDataLister);
			}
		} else {
			if (alignModeDataLister!=null) {
				otherModel.removeListDataListener(alignModeDataLister);
			}
		}
		this.mode=mode;
		update(oldSize,getSize());
	}
	
	void update(int oldSize,int newSize) {
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

	public void setMode(DisplayMode mode) {
		setMode(mode,null);
	}


	public DisplayMode getMode() {
		return mode;
	}


	public XMLTextModel getModel() {
		return model;
	}


	public void refresh() {
		fireContentChanged(this,0,getSize()-1);
	}


	public void clear() {
		int size=getSize();
		for (int i=0;i<occurrenceArray.length;i++) {
			occurrenceArray[i]=null;
		}
		matchedSentences.clear();
		fireContentChanged(this,0,size-1);
	}


	public ArrayList<Integer> getMatchedSentences() {
		if (matchedSentences==null) return null;
		ArrayList<Integer> copy=new ArrayList<Integer>();
		for (Integer i:matchedSentences) {
			copy.add(new Integer(i));
		}
		return copy;
	}
}
