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
package fr.umlv.unitex.xalign;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

/**
 * This is a loader for XML text files manipulated by XAlign.
 * 
 * @author Sébastien Paumier
 */
public class XMLTextLoader {

	final XMLTextModel model;
	final MappedByteBuffer buffer;

	public XMLTextLoader(XMLTextModel model, MappedByteBuffer buffer) {
		this.model = model;
		this.buffer = buffer;
	}

	public void load() {
		final SwingWorker<Void, Sentence> worker = new SwingWorker<Void, Sentence>() {
			@Override
			protected Void doInBackground() throws Exception {
				final int dataLength = buffer.capacity();
				final StringBuilder ID = new StringBuilder();
				int start, end;
				for (int pos = 0; pos < dataLength; pos = pos + 1) {
					if (buffer.get(pos) == '<' && buffer.get(pos + 1) == 's'
							&& buffer.get(pos + 2) == ' ') {
						/* If we have a sentence tag, we read its id */
						do {
							pos++;
						} while (!(buffer.get(pos) == '"'
								&& buffer.get(pos - 1) == '='
								&& buffer.get(pos - 2) == 'd'
								&& buffer.get(pos - 3) == 'i'
								&& buffer.get(pos - 4) == ':'
								&& buffer.get(pos - 5) == 'l'
								&& buffer.get(pos - 6) == 'm' && buffer
								.get(pos - 7) == 'x'));
						ID.setLength(0);
						pos++;
						do {
							ID.append((char) buffer.get(pos));
							pos++;
						} while (buffer.get(pos) != '"');
						/*
						 * Then we look for the beginning of the sentence, i.e.
						 * after the '>' char
						 */
						while (buffer.get(pos - 1) != '>') {
							pos++;
						}
						start = pos;
						do {
							pos++;
						} while (buffer.get(pos - 1) != '<');
						end = pos - 2;
						publish(new Sentence(ID.toString(), start, end));
						setProgress((int) (100. * pos / dataLength));
					}
				}
				setProgress(100);
				return null;
			}

			@Override
			protected void process(java.util.List<Sentence> chunks) {
				model.addSentences(chunks);
			}
		};
		worker.execute();
		try {
			worker.get();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		} catch (final ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Builds and returns a read-only mapped byte buffer for the given file.
	 */
	public static MappedByteBuffer buildMappedByteBuffer(File file)
			throws IOException {
		final FileInputStream fileInputStream = new FileInputStream(file);
		final FileChannel channel = fileInputStream.getChannel();
		final MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_ONLY,
				0, file.length());
		fileInputStream.close();
		return map;
	}
}
