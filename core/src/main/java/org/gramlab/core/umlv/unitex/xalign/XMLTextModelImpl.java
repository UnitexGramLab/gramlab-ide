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
package org.gramlab.core.umlv.unitex.xalign;

import java.nio.MappedByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * This is a model for representing a XML text file as the list of its
 * sentences. It is used for alignements.
 * 
 * @author Sébastien Paumier
 */
public class XMLTextModelImpl implements XMLTextModel {
	private MappedByteBuffer buffer;
	private static final Charset utf8 = Charset.forName("UTF-8");
	private final ArrayList<Sentence> sentences;
	private final HashMap<String, Integer> id;

	public XMLTextModelImpl(MappedByteBuffer buffer) {
		this.buffer = buffer;
		sentences = new ArrayList<Sentence>();
		id = new HashMap<String, Integer>();
	}

	@Override
	public int getSize() {
		return sentences.size();
	}

	@Override
	public String getElementAt(int i) {
		if (i < 0 || i >= getSize()) {
			throw new IndexOutOfBoundsException();
		}
		final Sentence s = sentences.get(i);
		final long start = s.start;
		final long end = s.end;
		final byte[] tmp = new byte[(int) (end - start + 1)];
		int z = 0;
		for (long pos = start; pos <= end; pos++) {
			if (buffer.get((int) pos) == '&') {
				if (buffer.get((int) pos + 1) == 'a'
						&& buffer.get((int) pos + 2) == 'm'
						&& buffer.get((int) pos + 3) == 'p'
						&& buffer.get((int) pos + 4) == ';') {
					tmp[z++] = '&';
					pos = pos + 4;
				} else if (buffer.get((int) pos + 1) == 'l'
						&& buffer.get((int) pos + 2) == 't'
						&& buffer.get((int) pos + 3) == ';') {
					tmp[z++] = '<';
					pos = pos + 3;
				} else if (buffer.get((int) pos + 1) == 'g'
						&& buffer.get((int) pos + 2) == 't'
						&& buffer.get((int) pos + 3) == ';') {
					tmp[z++] = '>';
					pos = pos + 3;
				} else {
					tmp[z++] = buffer.get((int) pos);
				}
			} else {
				tmp[z++] = buffer.get((int) pos);
			}
		}
		return new String(tmp, 0, z, utf8);
	}

	@Override
	public int getIndex(String s) {
		final Integer i = id.get(s);
		if (i == null) {
			System.err.println("Error: sentence id #" + s
					+ " not found in XML text!!!");
			return -1;
		}
		return i;
	}

	@Override
	public String getID(int index) {
		return sentences.get(index).ID;
	}

	private final ArrayList<ListDataListener> listeners = new ArrayList<ListDataListener>();

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

	private void add(Sentence s) {
		final int n = getSize();
		sentences.add(s);
		id.put(s.ID, n);
	}

	@Override
	public void addSentences(List<Sentence> sentence) {
		final int start = getSize();
		for (final Sentence s : sentence) {
			add(s);
		}
		final int end = getSize() - 1;
		fireIntervalAdded(this, start, end);
	}

	void fireIntervalAdded(Object source, int start, int end) {
		final ListDataEvent event = new ListDataEvent(source,
				ListDataEvent.INTERVAL_ADDED, start, end);
		for (final ListDataListener l : listeners) {
			l.intervalAdded(event);
		}
	}

	@Override
	public void reset() {
		if (buffer != null)
			buffer = null;
		System.gc();
	}
}
