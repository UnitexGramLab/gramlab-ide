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
import java.nio.charset.*;
import java.util.*;
import javax.swing.*;


/**
 * This is a model for representing an HTML concordance file as the
 * list of its paragraphs. Paragraphs are delimited by new lines. It uses
 * a mapped file to avoid to store large data in memory.
 * 
 * @author Sébastien Paumier
 */
public class ConcordanceAsListModel extends AbstractListModel {

	/**
	 * An HTML concordance file always starts with a header of
	 * HTML_START_LINES lines, then there are the real concordance lines, then
	 * there are HTML_END_LINES that close open HTML tags.
	 */
	private static final int HTML_START_LINES = 7;
	private static final int HTML_END_LINES = 2;
	private static final int HTML_CONTROL_LINES = HTML_START_LINES+HTML_END_LINES;
	MappedByteBuffer buffer;
	ArrayList<Interval> intervals;
	int dataLength;
	SwingWorker<Void,Interval> worker;
	Interval selection;
	FileChannel channel;
	FileInputStream stream;
	File file;
	private static Charset utf8=Charset.forName("UTF-8");
	
	public void load(File f) {
		this.file=f;
		dataLength=(int)file.length();
		intervals=new ArrayList<Interval>();
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
		worker=new SwingWorker<Void,Interval>() {

			@Override
			protected Void doInBackground() throws Exception {
				int lastStart=0;
				for (int pos=0;pos<dataLength;pos=pos+1) {
					int a=0xFF & buffer.get();
		        	if (a=='\n') {
		        		// if we have an end-of-line
		        		publish(new Interval(lastStart,pos));
		        		setProgress(100*pos/dataLength);
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

	public ConcordanceAsListModel() {
	}

	public int getSize() {
		if (intervals==null) return 0;
		int size=intervals.size()-HTML_CONTROL_LINES;
		if (size<0) return 0;
		return size;
	}

	
	/**
	 * Returns the text corresponding to the paragraph #i.
	 */
	public String getElementReallyAt(int i) {
		Interval interval=intervals.get(i);
		long start=interval.getStart()+15; // we don't want neither the "<tr><td nowrap>"
		long end=interval.getEnd()-12;     // nor the "</td></tr>\r\n"
		byte[] tmp=new byte[(int) (end-start+1)];
		int z=0;
		for (long pos=start;pos<=end;pos++) {
			tmp[z++]=buffer.get((int)pos);
		}
		return new String(tmp,utf8);
	}

	
	/**
	 * Returns the text corresponding to the concordance line #i.
	 */
	public String getElementAt(int i) {
		int realIndex=i+HTML_START_LINES;
		return getElementReallyAt(realIndex);
	}
	
	public String getHTMLStart() {
		// TODO take the font from the preferences
		return 
		"<html lang=en>\r\n"+
		"<body>\r\n<font style=\"font-family: Courier new; font-size: 12\">\r\n";
	}
	
	public String getHTMLEnd() {
		return "</font></body></html>";
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
}
