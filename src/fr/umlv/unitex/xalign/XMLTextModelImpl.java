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


/**
 * This is a model for representing a XML text file as the
 * list of its sentences. It is used for alignements.
 * 
 * @author Sébastien Paumier
 */
public class XMLTextModel extends AbstractListModel {

	MappedByteBuffer buffer;
	int dataLength=0;
	SwingWorker<Void,Integer> worker;
	FileChannel channel;
	FileInputStream stream;
	File file;
	public static Charset utf8=Charset.forName("UTF-8");
	
	int[] offset;
	int nSentences=0;
	int arraySize=0;
	HashMap<String,Integer> id;
	
	public void load(File f) {
		this.file=f;
		dataLength=(int)file.length();
		offset=new int[0];
		nSentences=0;
		arraySize=0;
		id=new HashMap<String,Integer>();
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
		worker=new SwingWorker<Void,Integer>() {

			@Override
			protected Void doInBackground() throws Exception {
				int currentSentence=0;
				for (int pos=0;pos<dataLength;pos=pos+1) {
					if (buffer.get(pos)=='<' && buffer.get(pos+1)=='s' &&
						buffer.get(pos+2)==' ') {
		        		/* If we have a sentence tag, we read its id */
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
						String s="";
						pos++;
						do {
						   s=s+(char)buffer.get(pos);
						   pos++;
						} while (buffer.get(pos)!='"');
						id.put(s,currentSentence++);
						/* Then we look for the beginning of
		        		 * the sentence, i.e. after the '>' char */
						while (buffer.get(pos-1)!='>') {
							pos++;
						}
						publish(pos);
						do {
							pos++;
						} while (buffer.get(pos-1)!='<');
						publish(pos-2);
		        		setProgress(100*pos/dataLength);
		        	}
		        }
				/* We publish a negative position in order to inform the
				 * progress method that there are no more ends of line. 
				 */
				publish(-1);
				return null;
			}
			
			@SuppressWarnings("synthetic-access")
			@Override
			protected void process(java.util.List<Integer> chunks) {
				int oldSize=arraySize;
				int oldNSentence=nSentences;
				int newSize=oldSize+chunks.size();
				int multiplier=1;
				/* We check if it is necessary to enlarge the EOL
				 * array */
				if (offset.length==0) {
					offset=new int[2];
				}
				while (multiplier*offset.length < newSize) {
					multiplier=2*multiplier;
				}
				int[] temp=offset;
				if (multiplier!=1) {
					temp=Arrays.copyOf(offset,multiplier*offset.length);
				}
				int insertPos=oldSize;
				for (Integer i:chunks) {
					if (i<0) {
						/* We assume that a negative position means the end of
						 * the sentences, and, so, we resize the array. */
						temp=Arrays.copyOf(temp,insertPos);
						newSize=insertPos;
						break;
					}
					temp[insertPos++]=i;
				}
				/* If we keep the following instructions in this order,
				 * there is no need to synchronize */
				offset=temp;
				arraySize=insertPos;
				nSentences=arraySize/2;
				fireIntervalAdded(this,oldNSentence,nSentences-1);
			}
			
		};
		worker.execute();
	}

	public XMLTextModel() {
		super();
	}

	public int getSize() {
		int size=nSentences;
		if (size<0) return 0;
		return size;
	}

	
	/**
	 * Returns the text corresponding to the sentence #i.
	 */
	public String getElementAt(int i) {
		long start=offset[2*i];
		long end=offset[2*i+1];
		byte[] tmp=new byte[(int) (end-start+1)];
		int z=0;
		for (long pos=start;pos<=end;pos++) {
			if (buffer.get((int)pos)=='&') {
				if (buffer.get((int)pos+1)=='a'
					&& buffer.get((int)pos+2)=='m'
					&& buffer.get((int)pos+3)=='p'
					&& buffer.get((int)pos+4)==';') {
					tmp[z++]='&';
					pos=pos+4;
				} else if (buffer.get((int)pos+1)=='l'
					    && buffer.get((int)pos+2)=='t'
						&& buffer.get((int)pos+3)==';') {
						tmp[z++]='<';
						pos=pos+3;
				}
				else if (buffer.get((int)pos+1)=='g'
				    && buffer.get((int)pos+2)=='t'
					&& buffer.get((int)pos+3)==';') {
					tmp[z++]='>';
					pos=pos+3;
				} else {
					tmp[z++]=buffer.get((int)pos);
				}
			} else {
				tmp[z++]=buffer.get((int)pos);
			}
		}
		return new String(tmp,utf8);
	}

	
	/**
	 * Just to ask the view to refresh.
	 */
	public void refresh() {
		fireContentsChanged(this,0,getSize());
	}

	public void reset() {
		if (buffer!=null) buffer=null;
		System.gc();
		if (channel!=null) {
			try {
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			channel=null;
		}
		if (stream!=null) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			stream=null;
		}
	}
	
	public int getIndex(String s) {
		return id.get(s);
	}
	
	
}
