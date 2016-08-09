/*
 * Unitex
 *
 * Copyright (C) 2001-2016 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package org.gramlab.core.umlv.unitex.concord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Arrays;

import javax.swing.AbstractListModel;
import javax.swing.SwingWorker;

import org.gramlab.core.umlv.unitex.text.Interval;

/**
 * This is a model for representing an HTML concordance file as the list of its
 * paragraphs. Paragraphs are delimited by new lines. It uses a mapped file to
 * avoid to store large data in memory.
 * 
 * @author Sébastien Paumier
 */
public class ConcordanceAsListModel extends AbstractListModel {
	/**
	 * An HTML concordance file always starts with a header of HTML_START_LINES
	 * lines, then there are the real concordance lines, then there are
	 * HTML_END_LINES that close open HTML tags.
	 */
	int HTML_START_LINES = 7;
	private int HTML_END_LINES = 2;
	private int HTML_CONTROL_LINES = HTML_START_LINES + HTML_END_LINES;
	MappedByteBuffer buffer;
	private int dataLength;
	private SwingWorker<Void, Integer> worker;
	Interval selection;
	private FileChannel channel;
	private FileInputStream stream;
	private File file;
	static final Charset utf8 = Charset.forName("UTF-8");
	private int[] endOfLines;
	private int numberOfEOL;

	public void load(File f) {
		this.file = f;
		setDataLength((int) file.length());
		endOfLines = new int[0];
		numberOfEOL = 0;
		try {
			stream = new FileInputStream(file);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		channel = stream.getChannel();
		try {
			buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0,
					getDataLength());
		} catch (final IOException e) {
			e.printStackTrace();
			return;
		}
		worker = new SwingWorker<Void, Integer>() {
			@Override
			protected Void doInBackground() throws Exception {
				int lastStart = 0;
				for (int pos = 0; pos < getDataLength(); pos = pos + 1) {
					final int a = 0xFF & buffer.get(pos);
					if (a == '\n') {
						// if we have an end-of-line
						publish(pos);
						setProgress(100 * pos / getDataLength());
						lastStart = pos + 1;
					}
				}
				if (lastStart < (getDataLength() - 1)) {
					publish(getDataLength() - 1);
					setProgress(100);
				}
				/*
				 * We publish a negative position in order to inform the
				 * progress method that there are no more ends of line.
				 */
				publish(-1);
				return null;
			}

			@SuppressWarnings("synthetic-access")
			@Override
			protected void process(java.util.List<Integer> chunks) {
				final int oldSize = numberOfEOL;
				int newSize = oldSize + chunks.size();
				int multiplier = 1;
				/*
				 * We check if it is necessary to enlarge the EOL array
				 */
				if (endOfLines.length == 0) {
					endOfLines = new int[1];
				}
				while (multiplier * endOfLines.length < newSize) {
					multiplier = 2 * multiplier;
				}
				int[] temp = endOfLines;
				if (multiplier != 1) {
					temp = Arrays.copyOf(endOfLines, multiplier
							* endOfLines.length);
				}
				int insertPos = oldSize;
				for (final Integer i : chunks) {
					if (i < 0) {
						/*
						 * We assume that a negative position means the end of
						 * the new lines, and, so, we resize the array.
						 */
						temp = Arrays.copyOf(temp, insertPos);
						newSize = insertPos;
						break;
					}
					temp[insertPos++] = i;
				}
				/*
				 * If we keep the following instructions in this order, there is
				 * no need to synchronize
				 */
				endOfLines = temp;
				numberOfEOL = newSize;
				fireIntervalAdded(this, oldSize, newSize - 1);
			}
		};
		worker.execute();
	}

	public ConcordanceAsListModel() {
		super();
	}

	ConcordanceAsListModel(int html_start_lines, int html_end_lines) {
		this.HTML_START_LINES = html_start_lines;
		this.HTML_END_LINES = html_end_lines;
		this.HTML_CONTROL_LINES = HTML_START_LINES + HTML_END_LINES;
	}

	@Override
	public int getSize() {
		final int size = numberOfEOL - HTML_CONTROL_LINES;
		if (size < 0)
			return 0;
		return size;
	}

	/**
	 * Returns the text corresponding to the paragraph #i.
	 */
	String getElementReallyAt(int i) {
		final Interval interval = getInterval(i);
		final long start = interval.getStartInBytes() + 15; // we don't want
		// neither the
		// <tr><td nowrap>
		final long end = interval.getEndInBytes() - 12; // nor the
		// </td></tr>\r\n
		final byte[] tmp = new byte[(int) (end - start + 1)];
		int z = 0;
		for (long pos = start; pos <= end; pos++) {
			tmp[z++] = buffer.get((int) pos);
		}
		return new String(tmp, utf8);
	}

	Interval getInterval(int i) {
		final int end = endOfLines[i];
		final int start = (i == 0) ? 0 : (endOfLines[i - 1] + 1);
		return new Interval(start, end, -1, -1);
	}

	/**
	 * Returns the text corresponding to the concordance line #i.
	 */
	@Override
	public Object getElementAt(int i) {
		final int realIndex = i + HTML_START_LINES;
		return getElementReallyAt(realIndex);
	}

	/**
	 * Just to ask the view to refresh.
	 */
	public void refresh() {
		fireContentsChanged(this, 0, getSize());
	}

	
	public void reset() {
		if (buffer != null) {
			ReferenceQueue<MappedByteBuffer> queue=new ReferenceQueue<MappedByteBuffer>();
			new PhantomReference<MappedByteBuffer>(buffer,queue);
			buffer = null;
			while (queue.poll()!=null) {
				System.gc();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					/* do nothing */
				}
				Thread.yield();
			}
		}
		if (channel != null) {
			try {
				channel.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
			channel = null;
		}
		if (stream != null) {
			try {
				stream.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
			stream = null;
		}
		System.gc();
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public int getDataLength() {
		return dataLength;
	}
}
