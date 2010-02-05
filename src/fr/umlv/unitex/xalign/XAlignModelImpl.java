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
package fr.umlv.unitex.xalign;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.*;

import javax.swing.*;

public class XAlignModelImpl implements XAlignModel {

	XMLTextModel src,dest;
	ArrayList<Couple> alignments;
	HashMap<String,ArrayList<String>> group;
	String sourceFile,destFile;
	int startPosition=-1;
	
	private boolean modified=false;
	
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
	
	class PublishInfo {
		String s1,s2;
		ArrayList<String> list;
		PublishInfo(String s1,String s2,ArrayList<String> list) {
			this.s1=s1;
			this.s2=s2;
			this.list=list;
		}
	}
	
	public XAlignModelImpl(XMLTextModel src,XMLTextModel dest) {
		this.src=src;
		this.dest=dest;
		alignments=new ArrayList<Couple>();
	}
	
	
	MappedByteBuffer buffer;
	int dataLength=0;
	SwingWorker<Void,PublishInfo> worker;
	FileChannel channel;
	FileInputStream stream;
	File file;
	Charset utf8=Charset.forName("UTF-8");
	
	public void load(File f) {
		this.file=f;
		if (f==null) {
			return;
		}
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
		worker=new SwingWorker<Void,PublishInfo>() {

			@Override
			protected Void doInBackground() throws Exception {
				int pos;
				/* First, we look for the description of the source
				 * and targets files */
				for (pos=0;pos<dataLength;pos=pos+1) {
					if (buffer.get(pos)=='<' && buffer.get(pos+1)=='p' &&
							buffer.get(pos+2)=='t' &&
							buffer.get(pos+3)=='r' &&
							(buffer.get(pos+4)==' ' || buffer.get(pos+4)=='\n'
								|| buffer.get(pos+4)=='\r' || buffer.get(pos+4)=='\t')) {
			        		/* If we have a ptr tag, we read its target */
						    do {
							    pos++;
							} while (!(buffer.get(pos)=='"' && 
									buffer.get(pos-1)=='=' &&
									buffer.get(pos-2)=='t' &&
									buffer.get(pos-3)=='e' &&
									buffer.get(pos-4)=='g' &&
									buffer.get(pos-5)=='r' &&
									buffer.get(pos-6)=='a' &&
									buffer.get(pos-7)=='t'));
							String target="";
							pos++;
							do {
								target=target+(char)buffer.get(pos);
							   pos++;
							} while (buffer.get(pos)!='"');
							/* Then we check if it was the source or destination
							 * file */
							do {
							    pos++;
							} while (!((buffer.get(pos)=='e' && 
									buffer.get(pos-1)=='c' &&
									buffer.get(pos-2)=='r' &&
									buffer.get(pos-3)=='u' &&
									buffer.get(pos-4)=='o' &&
									buffer.get(pos-5)=='s')
									|| (buffer.get(pos)=='n' && 
										buffer.get(pos-1)=='o' &&
										buffer.get(pos-2)=='i' &&
										buffer.get(pos-3)=='t' &&
										buffer.get(pos-4)=='a' &&
										buffer.get(pos-5)=='l' &&
										buffer.get(pos-6)=='s' &&
										buffer.get(pos-7)=='n' &&
										buffer.get(pos-8)=='a' &&
										buffer.get(pos-9)=='r' &&
										buffer.get(pos-10)=='t')));
							if (buffer.get(pos)=='e') {
								sourceFile=target;
							} else {
								destFile=target;
							}
					} else if (buffer.get(pos)=='"' && 
								buffer.get(pos+1)=='r' &&
								buffer.get(pos+2)=='e' &&
								buffer.get(pos+3)=='s' &&
								buffer.get(pos+4)=='u' &&
								buffer.get(pos+5)=='l' &&
								buffer.get(pos+6)=='t' &&
								buffer.get(pos+7)=='X' &&
								buffer.get(pos+8)=='A' &&
								buffer.get(pos+9)=='l' &&
								buffer.get(pos+10)=='i' &&
								buffer.get(pos+11)=='g' &&
								buffer.get(pos+12)=='n' &&
								buffer.get(pos+13)=='"') {
						/* If we are at the beginning of the aligment declarations,
						 * we note the current position and we exit the loop */
						startPosition=pos+16;
						pos=pos+16;
						break;
					}
				}
				for (;pos<dataLength;pos=pos+1) {
					if (buffer.get(pos)=='<' && buffer.get(pos+1)=='!'
						&& buffer.get(pos+2)=='-' && buffer.get(pos+3)=='-') {
						/* If we have a XML comment, we skip it */
						pos=pos+4;
						do {
							pos++;
						} while (!(buffer.get(pos)=='>' && buffer.get(pos-1)=='-' && buffer.get(pos-2)=='-'));
						continue;
					}
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
								publish(new PublishInfo(l.get(0),l.get(1),null));
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
							publish(new PublishInfo(id,null,l));
						}
						/* Then we look for the end of the tag */
						while (buffer.get(pos-1)!='>') {
							pos++;
						}
						setProgress((int)(100.*pos/dataLength));
		        	}
		        }
				setProgress(100);
				buffer=null;
				System.gc();
				return null;
			}
			
			@Override
			protected void process(java.util.List<PublishInfo> chunks) {
				for (PublishInfo c:chunks) {
					if (c.list==null) {
						align(c.s1,c.s2);						
					} else {
						group.put(c.s1,c.list);
					}
				}
			}
		};
		worker.execute();
		try {
			worker.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	ArrayList<AlignmentListener> listeners=new ArrayList<AlignmentListener>();
	
	protected void fireAlignmentChanged(AlignmentEvent e) {
		for (AlignmentListener l:listeners) {
			l.alignmentChanged(e);
		}
		if (AlignmentEvent.MANUAL_EDIT.equals(e) ||
			AlignmentEvent.CLEAR.equals(e)) {
			modified=true;
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
				align(src.getIndex(x1),dest.getIndex(x2),AlignmentEvent.LOADING);
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
		int currentPos=0,length=s.length();
		while (currentPos<length) {
			int start=s.indexOf('#',currentPos);
			if (start==-1) {
				break;
			}
			int end=s.indexOf(' ',start);
			if (end==-1) {
				end=length;
			}
			l.add(s.substring(start+1,end));
			currentPos=end;
		}
		return l;
	}

	
	
	public ArrayList<Integer> getAlignedSrcSequences(int sentence) {
		ArrayList<Integer> result=new ArrayList<Integer>();
		if (alignments==null) return result;
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

	public void align(int sentenceSrc, int sentenceDest,AlignmentEvent e) {
		Couple c=new Couple(sentenceSrc,sentenceDest);
		if (alignments.contains(c)) return;
		alignments.add(c);
		fireAlignmentChanged(e);
	}

	public void unAlign(int sentenceSrc, int sentenceDest) {
		Couple c=new Couple(sentenceSrc,sentenceDest);
		alignments.remove(c);
		fireAlignmentChanged(AlignmentEvent.MANUAL_EDIT);
	}

	public void changeAlignment(int sentenceSrc, int sentenceDest) {
		Couple c=new Couple(sentenceSrc,sentenceDest);
		if (alignments.contains(c)) alignments.remove(c);
		else alignments.add(c);
		fireAlignmentChanged(AlignmentEvent.MANUAL_EDIT);
	}

	
	@SuppressWarnings("unchecked")
	public void dumpAlignments(File f) throws IOException {
		if (f==null) {
			throw new NullPointerException();
		}
		if (worker!=null && !worker.isDone()) {
			System.err.println("Cannot dump alignments before they are loaded");
			return;
		}
		if (sourceFile==null) {
			sourceFile="srcText";
		}
		if (destFile==null) {
			destFile="destText";
		}
		FileOutputStream output=null;
		OutputStreamWriter writer=null;
		if (file!=null && f.equals(file)) {
			/* If we have to dump into the same file, then we can skip the
			 * beginning marked by startPosition.
			 */
			RandomAccessFile inputFile=new RandomAccessFile(file,"rw");
			byte[] header=new byte[startPosition];
			inputFile.readFully(header);
			inputFile.close();
			output=new FileOutputStream(f);
			writer=new OutputStreamWriter(output,"UTF8");
			output.write(header);
		} else if (file!=null) {
			/* If we have to dump an existing alignment into a new file,
			 * we must copy the beginning marked by startPosition.
			 */
			RandomAccessFile inputFile=new RandomAccessFile(file,"rw");
			byte[] header=new byte[startPosition];
			inputFile.readFully(header);
			inputFile.close();
			output=new FileOutputStream(f);
			writer=new OutputStreamWriter(output,"UTF8");
			output.write(header);
		} else {
			/* If we have to create an alignment file from scratch,
			 * we must write the header.
			 */
			output=new FileOutputStream(f);
			writer=new OutputStreamWriter(output,"UTF8");
			writeHeader(writer);
		}
		int i;
		Object[] left=new Object[src.getSize()];
		Object[] right=new Object[dest.getSize()];
		for (i=0;i<left.length;i++) {
			left[i]=new ArrayList<Integer>();
		}
		for (i=0;i<right.length;i++) {
			right[i]=new ArrayList<Integer>();
		}
		for (Couple c:alignments) {
			((ArrayList<Integer>)left[c.srcSentence]).add(c.destSentence);
			((ArrayList<Integer>)right[c.destSentence]).add(c.srcSentence);
		}
		ArrayList<String> results=new ArrayList<String>();
		ArrayList<String> destGroups=new ArrayList<String>();
		HashMap<String,String> groups=new HashMap<String,String>();
		int groupID=1;
		writer.write("            <linkGrp type=\"segmentGroup\">\n");
		for (i=0;i<left.length;i++) {
			Iterator<Integer> it=((ArrayList<Integer>)left[i]).iterator();
			if (!it.hasNext()) continue;
			ArrayList<Integer> ancestors=(ArrayList<Integer>)right[it.next()];
		    ArrayList<Integer> res=new ArrayList<Integer>();
		    for (Integer j:ancestors) {
		    	res.add(j);
		    }
			while (it.hasNext()) {
				ancestors=(ArrayList<Integer>)right[it.next()];
				for (int k=res.size()-1;k>=0;k--) {
					if (!ancestors.contains(res.get(k))) {
						res.remove(k);
					}
				}
			}
			String a,b,tmp;
			if (res.size()>1) {
				tmp=res+" left";
				if (!groups.containsKey(tmp)) {
				   a="#l"+groupID;
				   groups.put(tmp,a);
				   writer.write(createLink("l"+groupID,res,true)+"\n");
				   groupID++;
				} else {
					a=null;
				}
			} else {
				a=sourceFile+"#"+src.getID(res.get(0));
			}
			if (((ArrayList<Integer>)left[i]).size()>1) {
				tmp=left[i]+" right";
				if (!groups.containsKey(tmp)) {
					b="#l"+groupID;
					groups.put(tmp,b);
					destGroups.add(createLink("l"+groupID,(ArrayList<Integer>)left[i],false));
					groupID++;
				} else {
					b=null;
				}
			} else {
				b=destFile+"#"+dest.getID(((ArrayList<Integer>)left[i]).get(0));
			}
			if (a!=null && b!=null) {
			   results.add(a+" "+b);
			}
		}
		for (String s:destGroups) {
			writer.write(s+"\n");
		}
		destGroups.clear();
		destGroups=null;
		writer.write("            </linkGrp>\n");
		/* We dump the sentences that have no targets */
		writer.write("            <linkGrp type=\"noCorresp\">\n");
		for (int k=0;k<left.length;k++) {
			ArrayList<Integer> l=(ArrayList<Integer>)left[k];
			if (l.isEmpty()) {
				writer.write("               <link targets=\""+sourceFile+"#"+src.getID(k)+"\" type=\"noCorresp\">\n");
			}
		}
		for (int k=0;k<right.length;k++) {
			ArrayList<Integer> l=(ArrayList<Integer>)right[k];
			if (l.isEmpty()) {
				writer.write("               <link targets=\""+destFile+"#"+dest.getID(k)+"\" type=\"noCorresp\">\n");
			}
		}
		writer.write("            </linkGrp>\n");
		/* Finally, we dump the alignments */
		writer.write("            <linkGrp type=\"alignment\">\n");
		for (String s:results) {
			writer.write("               <link\n"+
					     "                  targets=\""+s+"\" type=\"alignment\"/>\n");
		}
		writer.write("            </linkGrp>\n");
		writer.write("         </div>\n");
		writer.write("      </body>\n");
		writer.write("    </text>\n");
		writer.write("</TEI>\n");
        writer.flush();
        output.close();
        modified=false;
        fireAlignmentChanged(AlignmentEvent.SAVING);
	}
	
	
	
	private void writeHeader(OutputStreamWriter writer) throws IOException {
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<TEI>\n"+
				"   <teiHeader>\n"+
				"      <fileDesc>\n"+
		        "         <titleStmt>\n"+
		        "            <title/>\n"+
		        "         </titleStmt>\n"+
		        "         <publicationStmt>\n"+
		        "            <p/>\n"+
		        "         </publicationStmt>\n"+
		        "         <sourceDesc>\n"+
		        "            <bibl>\n"+
		        "               <ref>\n"+
		        "                  <ptr target=\""+sourceFile+"\"/>\n"+
		        "                  <note type=\"status\">source</note>\n"+
		        "               </ref>\n"+
		        "            </bibl>\n"+
		        "            <bibl>\n"+
		        "               <ref>\n"+
		        "                  <ptr target=\""+destFile+"\"/>\n"+
		        "                  <note type=\"status\">translation</note>\n"+
		        "               </ref>\n"+
		        "            </bibl>\n"+
		        "         </sourceDesc>\n"+
		        "      </fileDesc>\n"+
		        "   </teiHeader>\n"+
		        "   <text>\n"+
		        "      <body>\n"+
		        "         <linkGrp type=\"alignmentCognates\">\n"+
		        "            <link\n"+
		        "               targets=\""+sourceFile+" "+destFile+"\" type=\"alignmentDomain\"/>\n"+
		        "         </linkGrp>\n"+
		        "         <div type=\"resultXAlign\">\n");
		
	}


	StringBuilder builder=new StringBuilder();
	private String createLink(String groupID,ArrayList<Integer> sentences,boolean source) {
		builder.setLength(0);
		builder.append("               <link\n");
		builder.append("                  targets=\"");
		for (Integer i:sentences) {
			builder.append((source?sourceFile:destFile)+"#");
			builder.append((source?src.getID(i):dest.getID(i))+" ");
		}
		builder.append("\"\n");
		builder.append("                  type=\"linking\" xml:id=\""+groupID+"\"/>");
		return builder.toString();
	}

	
	public ArrayList<Integer> getAlignedSequences(int sentence, boolean fromSrc) {
		if (fromSrc) return getAlignedSrcSequences(sentence);
		return getAlignedDestSequences(sentence);
	}

	public boolean isModified() {
		return modified;
	}
	
	public void reset() {
		if (buffer!=null) buffer=null;
		System.gc();
	}

	public void clear() {
		alignments.clear();
		fireAlignmentChanged(AlignmentEvent.CLEAR);
	}

}
