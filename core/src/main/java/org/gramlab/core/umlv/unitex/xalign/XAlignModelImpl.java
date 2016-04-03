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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import fr.umlv.unitex.listeners.AlignmentListener;

public class XAlignModelImpl implements XAlignModel {

	final XMLTextModel src;
	final XMLTextModel dest;
	final ArrayList<Couple> alignments;
	HashMap<String, ArrayList<String>> group;
	String sourceFile;
	String destFile;
	int startPosition = -1;
	boolean modified = false;

	class Couple {
		final int srcSentence;
		final int destSentence;

		Couple(int src, int dest) {
			this.srcSentence = src;
			this.destSentence = dest;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			if (obj instanceof Couple) {
				final Couple c = (Couple) obj;
				return c.srcSentence == srcSentence
						&& c.destSentence == destSentence;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return srcSentence << 16 + destSentence;
		}
	}

	class PublishInfo {
		final String s1;
		final String s2;
		final ArrayList<String> list;

		PublishInfo(String s1, String s2, ArrayList<String> list) {
			this.s1 = s1;
			this.s2 = s2;
			this.list = list;
		}
	}

	public XAlignModelImpl(XMLTextModel src, XMLTextModel dest) {
		this.src = src;
		this.dest = dest;
		alignments = new ArrayList<Couple>();
	}

	private MappedByteBuffer buffer;
	private int dataLength = 0;
	private SwingWorker<Void, PublishInfo> worker;
	private FileChannel channel;
	private FileInputStream stream;
	private File file;
	Charset utf8 = Charset.forName("UTF-8");

	@Override
	public void load(File f) throws IOException {
		this.file = f;
		if (f == null) {
			return;
		}
		setDataLength((int) file.length());
		group = new HashMap<String, ArrayList<String>>();
		stream = new FileInputStream(file);
		channel = stream.getChannel();
		setBuffer(channel
				.map(FileChannel.MapMode.READ_ONLY, 0, getDataLength()));
		worker = new SwingWorker<Void, PublishInfo>() {
			@Override
			protected Void doInBackground() throws Exception {
				int pos;
				/*
				 * First, we look for the description of the source and targets
				 * files
				 */
				for (pos = 0; pos < getDataLength(); pos = pos + 1) {
					if (getBuffer().get(pos) == '<'
							&& getBuffer().get(pos + 1) == 'p'
							&& getBuffer().get(pos + 2) == 't'
							&& getBuffer().get(pos + 3) == 'r'
							&& (getBuffer().get(pos + 4) == ' '
									|| getBuffer().get(pos + 4) == '\n'
									|| getBuffer().get(pos + 4) == '\r' || getBuffer()
									.get(pos + 4) == '\t')) {
						/* If we have a ptr tag, we read its target */
						do {
							pos++;
						} while (!(getBuffer().get(pos) == '"'
								&& getBuffer().get(pos - 1) == '='
								&& getBuffer().get(pos - 2) == 't'
								&& getBuffer().get(pos - 3) == 'e'
								&& getBuffer().get(pos - 4) == 'g'
								&& getBuffer().get(pos - 5) == 'r'
								&& getBuffer().get(pos - 6) == 'a' && getBuffer()
								.get(pos - 7) == 't'));
						String target = "";
						pos++;
						do {
							target = target + (char) getBuffer().get(pos);
							pos++;
						} while (getBuffer().get(pos) != '"');
						/*
						 * Then we check if it was the source or destination
						 * file
						 */
						do {
							pos++;
						} while (!((getBuffer().get(pos) == 'e'
								&& getBuffer().get(pos - 1) == 'c'
								&& getBuffer().get(pos - 2) == 'r'
								&& getBuffer().get(pos - 3) == 'u'
								&& getBuffer().get(pos - 4) == 'o' && getBuffer()
								.get(pos - 5) == 's') || (getBuffer().get(pos) == 'n'
								&& getBuffer().get(pos - 1) == 'o'
								&& getBuffer().get(pos - 2) == 'i'
								&& getBuffer().get(pos - 3) == 't'
								&& getBuffer().get(pos - 4) == 'a'
								&& getBuffer().get(pos - 5) == 'l'
								&& getBuffer().get(pos - 6) == 's'
								&& getBuffer().get(pos - 7) == 'n'
								&& getBuffer().get(pos - 8) == 'a'
								&& getBuffer().get(pos - 9) == 'r' && getBuffer()
								.get(pos - 10) == 't')));
						if (getBuffer().get(pos) == 'e') {
							sourceFile = target;
						} else {
							destFile = target;
						}
					} else if (getBuffer().get(pos) == '"'
							&& getBuffer().get(pos + 1) == 'r'
							&& getBuffer().get(pos + 2) == 'e'
							&& getBuffer().get(pos + 3) == 's'
							&& getBuffer().get(pos + 4) == 'u'
							&& getBuffer().get(pos + 5) == 'l'
							&& getBuffer().get(pos + 6) == 't'
							&& getBuffer().get(pos + 7) == 'X'
							&& getBuffer().get(pos + 8) == 'A'
							&& getBuffer().get(pos + 9) == 'l'
							&& getBuffer().get(pos + 10) == 'i'
							&& getBuffer().get(pos + 11) == 'g'
							&& getBuffer().get(pos + 12) == 'n'
							&& getBuffer().get(pos + 13) == '"') {
						/*
						 * If we are at the beginning of the aligment
						 * declarations, we note the current position and we
						 * exit the loop
						 */
						startPosition = pos + 16;
						pos = pos + 16;
						break;
					}
				}
				for (; pos < getDataLength(); pos = pos + 1) {
					if (getBuffer().get(pos) == '<'
							&& getBuffer().get(pos + 1) == '!'
							&& getBuffer().get(pos + 2) == '-'
							&& getBuffer().get(pos + 3) == '-') {
						/* If we have a XML comment, we skip it */
						pos = pos + 4;
						do {
							pos++;
						} while (!(getBuffer().get(pos) == '>'
								&& getBuffer().get(pos - 1) == '-' && getBuffer()
								.get(pos - 2) == '-'));
						continue;
					}
					if (getBuffer().get(pos) == '<'
							&& getBuffer().get(pos + 1) == 'l'
							&& getBuffer().get(pos + 2) == 'i'
							&& getBuffer().get(pos + 3) == 'n'
							&& getBuffer().get(pos + 4) == 'k'
							&& (getBuffer().get(pos + 5) == ' '
									|| getBuffer().get(pos + 5) == '\n'
									|| getBuffer().get(pos + 5) == '\r' || getBuffer()
									.get(pos + 5) == '\t')) {
						/* If we have a link tag, we read its targets */
						do {
							pos++;
						} while (!(getBuffer().get(pos) == '"'
								&& getBuffer().get(pos - 1) == '='
								&& getBuffer().get(pos - 2) == 's'
								&& getBuffer().get(pos - 3) == 't'
								&& getBuffer().get(pos - 4) == 'e'
								&& getBuffer().get(pos - 5) == 'g'
								&& getBuffer().get(pos - 6) == 'r'
								&& getBuffer().get(pos - 7) == 'a' && getBuffer()
								.get(pos - 8) == 't'));
						String targets = "";
						pos++;
						do {
							targets = targets + (char) getBuffer().get(pos);
							pos++;
						} while (getBuffer().get(pos) != '"');
						/* Then we read the link type */
						do {
							pos++;
						} while (!(getBuffer().get(pos) == '"'
								&& getBuffer().get(pos - 1) == '='
								&& getBuffer().get(pos - 2) == 'e'
								&& getBuffer().get(pos - 3) == 'p'
								&& getBuffer().get(pos - 4) == 'y' && getBuffer()
								.get(pos - 5) == 't'));
						String type = "";
						pos++;
						do {
							type = type + (char) getBuffer().get(pos);
							pos++;
						} while (getBuffer().get(pos) != '"');
						final ArrayList<String> l = split(targets);
						if (type.equals("alignment")) {
							/* If we have an alignement */
							if (l.size() != 2) {
								System.err
										.println("An alignment must involve two elements");
							} else {
								publish(new PublishInfo(l.get(0), l.get(1),
										null));
							}
						} else if (type.equals("linking")) {
							/*
							 * If we have a linking, we must read its id and
							 * explicit it
							 */
							do {
								pos++;
							} while (!(getBuffer().get(pos) == '"'
									&& getBuffer().get(pos - 1) == '='
									&& getBuffer().get(pos - 2) == 'd'
									&& getBuffer().get(pos - 3) == 'i'
									&& getBuffer().get(pos - 4) == ':'
									&& getBuffer().get(pos - 5) == 'l'
									&& getBuffer().get(pos - 6) == 'm' && getBuffer()
									.get(pos - 7) == 'x'));
							String id = "";
							pos++;
							do {
								id = id + (char) getBuffer().get(pos);
								pos++;
							} while (getBuffer().get(pos) != '"');
							publish(new PublishInfo(id, null, l));
						}
						/* Then we look for the end of the tag */
						while (getBuffer().get(pos - 1) != '>') {
							pos++;
						}
						setProgress((int) (100. * pos / getDataLength()));
					}
				}
				setProgress(100);
				setBuffer(null);
				System.gc();
				return null;
			}

			@Override
			protected void process(java.util.List<PublishInfo> chunks) {
				for (final PublishInfo c : chunks) {
					if (c.list == null) {
						align(c.s1, c.s2);
					} else {
						group.put(c.s1, c.list);
					}
				}
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

	private final ArrayList<AlignmentListener> listeners = new ArrayList<AlignmentListener>();

	void fireAlignmentChanged(AlignmentEvent e) {
		for (final AlignmentListener l : listeners) {
			l.alignmentChanged(e);
		}
		if (AlignmentEvent.MANUAL_EDIT.equals(e)
				|| AlignmentEvent.CLEAR.equals(e)) {
			modified = true;
		}
	}

	@Override
	public void addAlignmentListener(AlignmentListener l) {
		listeners.add(l);
	}

	@Override
	public void removeAlignmentListener(AlignmentListener l) {
		listeners.remove(l);
	}

	/**
	 * This method aligns the sentences represented by the two given strings. A
	 * string can represent either a single sentence like "d4p5s2" or a sentence
	 * group like "l12".
	 */
	void align(String s1, String s2) {
		ArrayList<String> l1 = group.get(s1);
		if (l1 == null) {
			l1 = new ArrayList<String>();
			l1.add(s1);
		}
		ArrayList<String> l2 = group.get(s2);
		if (l2 == null) {
			l2 = new ArrayList<String>();
			l2.add(s2);
		}
		for (final String x1 : l1) {
			for (final String x2 : l2) {
				align(src.getIndex(x1), dest.getIndex(x2),
						AlignmentEvent.LOADING);
			}
		}
	}

	/**
	 * Splits a line of the form "xxxx#yyy xxxxx#zzz xxxx#wwww" and returns the
	 * list made of "yyy" "zzz" and "wwww".
	 */
	ArrayList<String> split(String s) {
		final ArrayList<String> l = new ArrayList<String>();
		if (s == null) {
			return l;
		}
		int currentPos = 0;
		final int length = s.length();
		while (currentPos < length) {
			final int start = s.indexOf('#', currentPos);
			if (start == -1) {
				break;
			}
			int end = s.indexOf(' ', start);
			if (end == -1) {
				end = length;
			}
			l.add(s.substring(start + 1, end));
			currentPos = end;
		}
		return l;
	}

	@Override
	public ArrayList<Integer> getAlignedSrcSequences(int sentence) {
		final ArrayList<Integer> result = new ArrayList<Integer>();
		if (alignments == null)
			return result;
		for (final Couple c : alignments) {
			if (c.srcSentence == sentence)
				result.add(c.destSentence);
		}
		return result;
	}

	@Override
	public ArrayList<Integer> getAlignedDestSequences(int sentence) {
		final ArrayList<Integer> result = new ArrayList<Integer>();
		for (final Couple c : alignments) {
			if (c.destSentence == sentence)
				result.add(c.srcSentence);
		}
		return result;
	}

	@Override
	public void align(int sentenceSrc, int sentenceDest, AlignmentEvent e) {
		final Couple c = new Couple(sentenceSrc, sentenceDest);
		if (alignments.contains(c))
			return;
		alignments.add(c);
		fireAlignmentChanged(e);
	}

	@Override
	public void unAlign(int sentenceSrc, int sentenceDest) {
		final Couple c = new Couple(sentenceSrc, sentenceDest);
		alignments.remove(c);
		fireAlignmentChanged(AlignmentEvent.MANUAL_EDIT);
	}

	@Override
	public void changeAlignment(int sentenceSrc, int sentenceDest) {
		final Couple c = new Couple(sentenceSrc, sentenceDest);
		if (alignments.contains(c))
			alignments.remove(c);
		else
			alignments.add(c);
		fireAlignmentChanged(AlignmentEvent.MANUAL_EDIT);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void dumpAlignments(File f) throws IOException {
		if (f == null) {
			throw new NullPointerException();
		}
		if (worker != null && !worker.isDone()) {
			System.err.println("Cannot dump alignments before they are loaded");
			return;
		}
		if (sourceFile == null) {
			sourceFile = "srcText";
		}
		if (destFile == null) {
			destFile = "destText";
		}
		FileOutputStream output = null;
		OutputStreamWriter writer = null;
		if (file != null && f.equals(file)) {
			/*
			 * If we have to dump into the same file, then we can skip the
			 * beginning marked by startPosition.
			 */
			final RandomAccessFile inputFile = new RandomAccessFile(file, "rw");
			final byte[] header = new byte[startPosition];
			inputFile.readFully(header);
			inputFile.close();
			output = new FileOutputStream(f);
			writer = new OutputStreamWriter(output, "UTF8");
			output.write(header);
		} else if (file != null) {
			/*
			 * If we have to dump an existing alignment into a new file, we must
			 * copy the beginning marked by startPosition.
			 */
			final RandomAccessFile inputFile = new RandomAccessFile(file, "rw");
			final byte[] header = new byte[startPosition];
			inputFile.readFully(header);
			inputFile.close();
			output = new FileOutputStream(f);
			writer = new OutputStreamWriter(output, "UTF8");
			output.write(header);
		} else {
			/*
			 * If we have to create an alignment file from scratch, we must
			 * write the header.
			 */
			output = new FileOutputStream(f);
			writer = new OutputStreamWriter(output, "UTF8");
			writeHeader(writer);
		}
		int i;
		final Object[] left = new Object[src.getSize()];
		final Object[] right = new Object[dest.getSize()];
		for (i = 0; i < left.length; i++) {
			left[i] = new ArrayList<Integer>();
		}
		for (i = 0; i < right.length; i++) {
			right[i] = new ArrayList<Integer>();
		}
		for (final Couple c : alignments) {
			((ArrayList<Integer>) left[c.srcSentence]).add(c.destSentence);
			((ArrayList<Integer>) right[c.destSentence]).add(c.srcSentence);
		}
		final ArrayList<String> results = new ArrayList<String>();
		ArrayList<String> destGroups = new ArrayList<String>();
		final HashMap<String, String> groups = new HashMap<String, String>();
		int groupID = 1;
		writer.write("            <linkGrp type=\"segmentGroup\">\n");
		for (i = 0; i < left.length; i++) {
			final Iterator<Integer> it = ((ArrayList<Integer>) left[i])
					.iterator();
			if (!it.hasNext())
				continue;
			ArrayList<Integer> ancestors = (ArrayList<Integer>) right[it.next()];
			final ArrayList<Integer> res = new ArrayList<Integer>();
			for (final Integer j : ancestors) {
				res.add(j);
			}
			while (it.hasNext()) {
				ancestors = (ArrayList<Integer>) right[it.next()];
				for (int k = res.size() - 1; k >= 0; k--) {
					if (!ancestors.contains(res.get(k))) {
						res.remove(k);
					}
				}
			}
			String a, b, tmp;
			if (res.size() > 1) {
				tmp = res + " left";
				if (!groups.containsKey(tmp)) {
					a = "#l" + groupID;
					groups.put(tmp, a);
					writer.write(createLink("l" + groupID, res, true) + "\n");
					groupID++;
				} else {
					a = null;
				}
			} else {
				a = sourceFile + "#" + src.getID(res.get(0));
			}
			if (((ArrayList<Integer>) left[i]).size() > 1) {
				tmp = left[i] + " right";
				if (!groups.containsKey(tmp)) {
					b = "#l" + groupID;
					groups.put(tmp, b);
					destGroups.add(createLink("l" + groupID,
							(ArrayList<Integer>) left[i], false));
					groupID++;
				} else {
					b = null;
				}
			} else {
				b = destFile + "#"
						+ dest.getID(((ArrayList<Integer>) left[i]).get(0));
			}
			if (a != null && b != null) {
				results.add(a + " " + b);
			}
		}
		for (final String s : destGroups) {
			writer.write(s + "\n");
		}
		destGroups.clear();
		destGroups = null;
		writer.write("            </linkGrp>\n");
		/* We dump the sentences that have no targets */
		writer.write("            <linkGrp type=\"noCorresp\">\n");
		for (int k = 0; k < left.length; k++) {
			final ArrayList<Integer> l = (ArrayList<Integer>) left[k];
			if (l.isEmpty()) {
				writer.write("               <link targets=\"" + sourceFile
						+ "#" + src.getID(k) + "\" type=\"noCorresp\">\n");
			}
		}
		for (int k = 0; k < right.length; k++) {
			final ArrayList<Integer> l = (ArrayList<Integer>) right[k];
			if (l.isEmpty()) {
				writer.write("               <link targets=\"" + destFile + "#"
						+ dest.getID(k) + "\" type=\"noCorresp\">\n");
			}
		}
		writer.write("            </linkGrp>\n");
		/* Finally, we dump the alignments */
		writer.write("            <linkGrp type=\"alignment\">\n");
		for (final String s : results) {
			writer.write("               <link\n"
					+ "                  targets=\"" + s
					+ "\" type=\"alignment\"/>\n");
		}
		writer.write("            </linkGrp>\n");
		writer.write("         </div>\n");
		writer.write("      </body>\n");
		writer.write("    </text>\n");
		writer.write("</TEI>\n");
		writer.flush();
		output.close();
		modified = false;
		fireAlignmentChanged(AlignmentEvent.SAVING);
	}

	private void writeHeader(OutputStreamWriter writer) throws IOException {
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<TEI>\n"
				+ "   <teiHeader>\n" + "      <fileDesc>\n"
				+ "         <titleStmt>\n" + "            <title/>\n"
				+ "         </titleStmt>\n" + "         <publicationStmt>\n"
				+ "            <p/>\n" + "         </publicationStmt>\n"
				+ "         <sourceDesc>\n" + "            <bibl>\n"
				+ "               <ref>\n" + "                  <ptr target=\""
				+ sourceFile
				+ "\"/>\n"
				+ "                  <note type=\"status\">source</note>\n"
				+ "               </ref>\n"
				+ "            </bibl>\n"
				+ "            <bibl>\n"
				+ "               <ref>\n"
				+ "                  <ptr target=\""
				+ destFile
				+ "\"/>\n"
				+ "                  <note type=\"status\">translation</note>\n"
				+ "               </ref>\n"
				+ "            </bibl>\n"
				+ "         </sourceDesc>\n"
				+ "      </fileDesc>\n"
				+ "   </teiHeader>\n"
				+ "   <text>\n"
				+ "      <body>\n"
				+ "         <linkGrp type=\"alignmentCognates\">\n"
				+ "            <link\n"
				+ "               targets=\""
				+ sourceFile
				+ " "
				+ destFile
				+ "\" type=\"alignmentDomain\"/>\n"
				+ "         </linkGrp>\n"
				+ "         <div type=\"resultXAlign\">\n");
	}

	private final StringBuilder builder = new StringBuilder();

	private String createLink(String groupID, ArrayList<Integer> sentences,
			boolean source) {
		builder.setLength(0);
		builder.append("               <link\n");
		builder.append("                  targets=\"");
		for (final Integer i : sentences) {
			builder.append(source ? sourceFile : destFile).append("#");
			builder.append(source ? src.getID(i) : dest.getID(i)).append(" ");
		}
		builder.append("\"\n");
		builder.append("                  type=\"linking\" xml:id=\"")
				.append(groupID).append("\"/>");
		return builder.toString();
	}

	@Override
	public ArrayList<Integer> getAlignedSequences(int sentence, boolean fromSrc) {
		if (fromSrc)
			return getAlignedSrcSequences(sentence);
		return getAlignedDestSequences(sentence);
	}

	@Override
	public boolean isModified() {
		return modified;
	}

	@Override
	public void reset() {
		if (getBuffer() != null)
			setBuffer(null);
		System.gc();
	}

	@Override
	public void clear() {
		alignments.clear();
		fireAlignmentChanged(AlignmentEvent.CLEAR);
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public int getDataLength() {
		return dataLength;
	}

	public void setBuffer(MappedByteBuffer buffer) {
		this.buffer = buffer;
	}

	public MappedByteBuffer getBuffer() {
		return buffer;
	}
}
