/*
 * Unitex
 *
 * Copyright (C) 2001-2007 Université de Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

import javax.swing.*;

public class XAlignModelImpl implements XAlignModel {

	XMLTextModel src,dest;
	ArrayList<Couple> alignments;
	HashMap<String,ArrayList<String>> group;
	
	class Couple {
		int srcSentence,destSentence;
		Couple(int src,int dest) {
			this.srcSentence=src;
			this.destSentence=dest;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj==null) return false;
			if (obj instanceof Couple) {
				Couple c=(Couple)obj;
				return c.srcSentence==srcSentence && c.destSentence==destSentence;
			}
			return false;
		}
	}
	
	public XAlignModelImpl(XMLTextModel src,XMLTextModel dest) {
		this.src=src;
		this.dest=dest;
		alignments=new ArrayList<Couple>();
	}
	
	
	MappedByteBuffer buffer;
	int dataLength=0;
	SwingWorker<Void,Void> worker;
	FileChannel channel;
	FileInputStream stream;
	File file;
	Charset utf8=Charset.forName("UTF-8");
	
	public void load(File f) {
		this.file=f;
		dataLength=(int)file.length();
		group=new HashMap<String,ArrayList<String>>();
		try {
			stream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		channel=stream.getChannel();
		try {
			buffer=channel.map(FileChannel.MapMode.READ_ONLY,0,dataLength);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		worker=new SwingWorker<Void,Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				for (int pos=0;pos<dataLength;pos=pos+1) {
					if (buffer.get(pos)=='<' && buffer.get(pos+1)=='l' &&
						buffer.get(pos+2)=='i' &&
						buffer.get(pos+3)=='n' &&
						buffer.get(pos+4)=='k' &&
						(buffer.get(pos+5)==' ' || buffer.get(pos+5)=='\n'
							|| buffer.get(pos+5)=='\r' || buffer.get(pos+5)=='\t')) {
		        		/* If we have a link tag, we read its targets */
						do {
						   pos++;
						} while (!(buffer.get(pos)=='"' && 
								buffer.get(pos-1)=='=' &&
								buffer.get(pos-2)=='s' &&
								buffer.get(pos-3)=='t' &&
								buffer.get(pos-4)=='e' &&
								buffer.get(pos-5)=='g' &&
								buffer.get(pos-6)=='r' &&
								buffer.get(pos-7)=='a' &&
								buffer.get(pos-8)=='t'));
						String targets="";
						pos++;
						do {
							targets=targets+(char)buffer.get(pos);
						   pos++;
						} while (buffer.get(pos)!='"');
						/* Then we read the link type */
						do {
							   pos++;
						} while (!(buffer.get(pos)=='"' && 
									buffer.get(pos-1)=='=' &&
									buffer.get(pos-2)=='e' &&
									buffer.get(pos-3)=='p' &&
									buffer.get(pos-4)=='y' &&
									buffer.get(pos-5)=='t'));
						String type="";
						pos++;
						do {
							type=type+(char)buffer.get(pos);
						   pos++;
						} while (buffer.get(pos)!='"');
						ArrayList<String> l=split(targets);
						if (type.equals("alignment")) {
							/* If we have an alignement */
							if (l.size()!=2) {
								System.err.println("An alignment must involve two elements");
							} else {
								align(l.get(0),l.get(1)); 
							}
						} else if (type.equals("linking")) {
							/* If we have a linking, we must read its id and explicit it */
							do {
								   pos++;
							} while (!(buffer.get(pos)=='"' && 
									buffer.get(pos-1)=='=' &&
									buffer.get(pos-2)=='d' &&
									buffer.get(pos-3)=='i' &&
									buffer.get(pos-4)==':' &&
									buffer.get(pos-5)=='l' &&
									buffer.get(pos-6)=='m' &&
									buffer.get(pos-7)=='x'));
							String id="";
							pos++;
							do {
								id=id+(char)buffer.get(pos);
							   pos++;
							} while (buffer.get(pos)!='"');
							group.put(id,l);
						}
						/* Then we look for the end of the tag */
						while (buffer.get(pos-1)!='>') {
							pos++;
						}
		        		setProgress(100*pos/dataLength);
		        		publish();
		        	}
		        }
				/* We publish a last time to inform the
				 * progress method that there are no more ends of line. 
				 */
				publish();
				return null;
			}
			
			@SuppressWarnings("synthetic-access")
			@Override
			protected void process(java.util.List<Void> chunks) {
				fireAlignmentChanged();
			}
		};
		worker.execute();
	}
	
	ArrayList<AlignmentListener> listeners=new ArrayList<AlignmentListener>();
	
	protected void fireAlignmentChanged() {
		for (AlignmentListener l:listeners) {
			l.alignmentChanged();
		}
	}
	
	public void addAlignmentListener(AlignmentListener l) {
		listeners.add(l);
	}
	
	public void removeAlignmentListener(AlignmentListener l) {
		listeners.remove(l);
	}
	

	/**
	 * This method aligns the sentences represented by the two given strings.
	 * A string can represent either a single sentence like "d4p5s2" or a
	 * sentence group like "l12".
	 */
	protected void align(String s1,String s2) {
		ArrayList<String> l1=group.get(s1);
		if (l1==null) {
			l1=new ArrayList<String>();
			l1.add(s1);
		}
		ArrayList<String> l2=group.get(s2);
		if (l2==null) {
			l2=new ArrayList<String>();
			l2.add(s2);
		}
		for (String x1:l1) {
			for (String x2:l2) {
				align(src.getIndex(x1),dest.getIndex(x2));
			}
		}
	}


	/**
	 * Splits a line of the form "xxxx#yyy xxxxx#zzz xxxx#wwww" and returns
	 * the list made of "yyy" "zzz" and "wwww".
	 */
	ArrayList<String> split(String s) {
		ArrayList<String> l=new ArrayList<String>();
		if (s==null) {
			return l;
		}
		StringTokenizer tok=new StringTokenizer(s);
		while (tok.hasMoreTokens()) {
			String tmp=tok.nextToken();
			int i=tmp.indexOf('#');
			l.add(tmp.substring(i+1));
		}
		return l;
	}
	
	
	public ArrayList<Integer> getAlignedSrcSequences(int sentence) {
		ArrayList<Integer> result=new ArrayList<Integer>();
		for (Couple c:alignments) {
			if (c.srcSentence==sentence) result.add(c.destSentence);
		}
		return result;
	}

	public ArrayList<Integer> getAlignedDestSequences(int sentence) {
		ArrayList<Integer> result=new ArrayList<Integer>();
		for (Couple c:alignments) {
			if (c.destSentence==sentence) result.add(c.srcSentence);
		}
		return result;
	}

	public void align(int sentenceSrc, int sentenceDest) {
		Couple c=new Couple(sentenceSrc,sentenceDest);
		if (alignments.contains(c)) return;
		alignments.add(c);
	}

	public void unAlign(int sentenceSrc, int sentenceDest) {
		Couple c=new Couple(sentenceSrc,sentenceDest);
		alignments.remove(c);
	}

	public void changeAlignment(int sentenceSrc, int sentenceDest) {
		Couple c=new Couple(sentenceSrc,sentenceDest);
		if (alignments.contains(c)) alignments.remove(c);
		else alignments.add(c);
	}

}
