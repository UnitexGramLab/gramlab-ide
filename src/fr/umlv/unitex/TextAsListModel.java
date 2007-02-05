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

package fr.umlv.unitex;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import javax.swing.*;


/**
 * This is a model for representing a text file as the list of its paragraphs.
 * Paragraphs are delimited by new lines. It uses a mapped file to avoid 
 * to store large data in memory.
 * 
 * @author Sébastien Paumier
 */
public class TextAsListModel extends AbstractListModel {

	MappedByteBuffer buffer;
	ArrayList<Interval> intervals;
	int dataLength;
	SwingWorker<Void,Interval> worker;
	Interval selection;
	String content=null;
	FileChannel channel;
	FileInputStream stream;
	File file;
	boolean dataFromFile;
	
	public void load(File f) {
		content=null;
		dataFromFile=true;
		this.file=f;
		long fileLength=file.length();
		intervals=new ArrayList<Interval>();
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
		worker=new SwingWorker<Void,Interval>() {

			@Override
			protected Void doInBackground() throws Exception {
				long lastStart=0;
				for (long pos=0;pos<dataLength;pos=pos+1) {
					int a=0xFF & buffer.get((int)(2*pos));
		        	int b=0xFF & buffer.get((int)(2*pos+1));
		        	char c=(char)(b*256+a);
		        	if (c=='\n') {
		        		// if we have an end-of-line
		        		publish(new Interval(lastStart,pos));
		        		setProgress((int)(100*pos/dataLength));
		        		lastStart=pos+1;
		        	}
		        }
				if (lastStart<(dataLength-1)) {
		        	publish(new Interval(lastStart,dataLength-1));
		        	setProgress(100);
		        }
				return null;
			}
			
			@SuppressWarnings("synthetic-access")
			@Override
			protected void process(java.util.List<Interval> chunks) {
				int oldSize=intervals.size();
				intervals.addAll(chunks);
				fireIntervalAdded(this,oldSize,intervals.size()-1);
			}
			
		};
		worker.execute();
	}

	public TextAsListModel() {
		dataFromFile=false;
		setText("");
	}

	public void setText(String string) {
		dataFromFile=false;
		intervals=new ArrayList<Interval>();
		content=string;
		fireIntervalAdded(this,0,0);
	}

	public int getSize() {
		if (content!=null) return 1;
		return intervals.size();
	}

	
	StringBuilder builder=new StringBuilder();
	
	/**
	 * Returns the text corresponding to the paragraph #i.
	 */
	public String getElementAt(int i) {
		if (!dataFromFile) return content;
		Interval interval=intervals.get(i);
		builder.delete(0,builder.length());
		long start=interval.getStart();
		long end=interval.getEnd();
		for (long pos=start;pos<=end;pos++) {
			int a=0xFF & buffer.get((int)(2*pos));
        	int b=0xFF & buffer.get((int)(2*pos+1));
        	char c=(char)(b*256+a);
        	if (c!='\r' && c!='\n') {
        		builder.append(c);
			}
		}
		return builder.toString();
	}

	
	public Interval getSelection() {
		if (!dataFromFile) return null;
		return selection;
	}

	public void setSelection(Interval selection) {
		this.selection=selection;
		fireContentsChanged(this,0,getSize());
	}

	public Interval getIntervalAt(int i) {
		if (!dataFromFile) {
			return null;
		}
		try {
			return intervals.get(i);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
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
	public int getElementContainingPosition(long position) {
		if (!dataFromFile) return -1;
		if (position<0) return -1;
		int size=getSize();
		for (int i=0;i<size;i++) {
			Interval interval=intervals.get(i);
			if (position>=interval.getStart() && position <=interval.getEnd()) return i;
		}
		return -1;
	}

	public String getContent() {
		return content;
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
		setText("");
	}
}
