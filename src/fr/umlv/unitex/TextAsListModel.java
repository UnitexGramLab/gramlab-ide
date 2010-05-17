/*
 * Unitex
 *
 * Copyright (C) 2001-2010 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

package fr.umlv.unitex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import javax.swing.AbstractListModel;
import javax.swing.SwingWorker;


/**
 * This is a model for representing a text file as the list of its paragraphs.
 * Paragraphs are delimited by new lines. It uses a mapped file to avoid 
 * to store large data in memory.
 * 
 * @author Sébastien Paumier
 */
public class TextAsListModel extends AbstractListModel {

	MappedByteBuffer buffer;
	int dataLength;
	SwingWorker<Void,Integer> worker;
	Interval selection;
	String content=null;
	FileChannel channel;
	FileInputStream stream;
	File file;
	boolean dataFromFile;
	ByteBuffer parseBuffer;
	
	int[] endOfLines;
	int numberOfEOL;
	
	public void load(File f) {
		content=null;
		dataFromFile=true;
		this.file=f;
		long fileLength=file.length();
		endOfLines=new int[0];
		numberOfEOL=0;
		try {
			stream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		channel=stream.getChannel();
		try {
			buffer=channel.map(FileChannel.MapMode.READ_ONLY,2,fileLength-2);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		dataLength=(int) ((fileLength-2)/2);
		/*
		 * parseBuffer must NOT be a final variable, because it would keep
		 * a reference on the buffer that would never be null, so that the
		 * buffer can never be garbage collected. As a consequence, the
		 * underlying file mapping will never be released.
		 */
		parseBuffer=buffer.duplicate();
		worker=new SwingWorker<Void,Integer>() {

			@Override
			protected Void doInBackground() throws Exception {
				int lastStart=0;
				for (int pos=0;pos<dataLength;pos=pos+1) {
					int a=0xFF & parseBuffer.get();
		        	int b=0xFF & parseBuffer.get();
		        	char c=(char)(b<<8 |a);
		        	if (c=='\n') {
		        		// if we have an end-of-line
		        		publish(pos);
		        		setProgress((int)((long)pos*100/dataLength));
		        		lastStart=pos+1;
		        	}
		        }
				if (lastStart<(dataLength-1)) {
					publish(dataLength-1);
		        	setProgress(100);
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
				int oldSize=numberOfEOL;
				int newSize=oldSize+chunks.size();
				int multiplier=1;
				/* We check if it is necessary to enlarge the EOL
				 * array */
				if (endOfLines.length==0) {
					endOfLines=new int[1];
				}
				while (multiplier*endOfLines.length < newSize) {
					multiplier=2*multiplier;
				}
				int[] temp=endOfLines;
				if (multiplier!=1) {
					temp=Arrays.copyOf(endOfLines,multiplier*endOfLines.length);
				}
				int insertPos=oldSize;
				for (Integer i:chunks) {
					if (i<0) {
						/* We assume that a negative position means the end of
						 * the new lines, and, so, we resize the array. */
						temp=Arrays.copyOf(temp,insertPos);
						newSize=insertPos;
						break;
					}
					temp[insertPos++]=i;
				}
				/* If we keep the following instructions in this order,
				 * there is no need to synchronize */
				endOfLines=temp;
				numberOfEOL=newSize;
				if (newSize-1>=oldSize) {
					fireIntervalAdded(this,oldSize,newSize-1);
				}
			}
						
		};
		worker.execute();
	}

	public TextAsListModel() {
		super();
		dataFromFile=false;
		setText("");
	}

	public void setText(String string) {
		dataFromFile=false;
		numberOfEOL=0;
		endOfLines=new int[0];
		content=string;
		fireIntervalAdded(this,0,0);
	}

	public int getSize() {
		if (content!=null) return 1;
		return numberOfEOL;
	}

	
	private final StringBuilder builder=new StringBuilder(40*100);
	
	/**
	 * Returns the text corresponding to the paragraph #i.
	 */
	public String getElementAt(int i) {
		if (!dataFromFile) return content;
		Interval interval=getInterval(i);
		builder.setLength(0);
		int start=interval.getStart();
		int end=interval.getEnd();
		buffer.position(2*start);
		for (int pos=start;pos<=end;pos++) {
			int a=0xFF & buffer.get();
        	int b=0xFF & buffer.get();
        	char c=(char)(b*256+a);
        	if (c!='\r' && c!='\n') {
        		builder.append(c);
			}
		}
		return builder.toString();
	}

	
	Interval getInterval(int i) {
		if (!dataFromFile) {
			return null;
		}
		int end=endOfLines[i];
		int start=(i==0)?0:(endOfLines[i-1]+1);
		return new Interval(start,end);
	}

	public Interval getSelection() {
		if (!dataFromFile) return null;
		return selection;
	}

	public void setSelection(Interval selection) {
		this.selection=selection;
		fireContentsChanged(this,0,getSize()-1);
	}

	/**
	 * Just to ask the view to refresh.
	 */
	public void refresh() {
		fireContentsChanged(this,0,getSize());
	}

	/**
	 * We want to get the number of the interval that contains the given position.
	 * @param position
	 * @return the number of the interval, or -1 if the position is not contained 
	 *         in an interval of the model 
	 */
	public int getElementContainingPosition(int position) {
		if (!dataFromFile) return -1;
		if (position<0) return -1;
		/* We cache the size and length in case there is a publish
		 * operation that updates the array.
		 */ 
		int size=numberOfEOL;
		int length=endOfLines.length;
		int pos=Arrays.binarySearch(endOfLines,0,size,position);
		if (pos<0) {
			pos=-1-pos;
			if (pos==length) {
				pos=-1;
			}
		}
		return pos;
	}

	public String getContent() {
		return content;
	}

	public void reset() {
		if (worker!=null) {
			worker.cancel(true);
			worker=null;
		}
		if (buffer!=null) buffer=null;
		if (parseBuffer!=null) parseBuffer=null;
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
		setText("");
	}
	
}
