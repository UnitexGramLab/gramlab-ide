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

import java.util.*;

public class XAlignModelImpl implements XAlignModel {

	XMLmodel src,dest;
	ArrayList<Couple> alignments;
	
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
	
	public XAlignModelImpl(XMLmodel src,XMLmodel dest) {
		this.src=src;
		this.dest=dest;
		alignments=new ArrayList<Couple>();
		generateAlignments();
	}

	private void generateAlignments() {
		int limit=(src.size()<dest.size())?src.size():dest.size();
		Random random=new Random(14);
		for (int i=0;i<limit;i++) {
			int limit2=random.nextInt(3);
			for (int j=0;j<limit2;j++) {
				int xx=i-1+random.nextInt(3);
				int yy=i-1+random.nextInt(3);
				if (xx<0) xx=0;
				else if (xx>=src.size()) xx=src.size()-1;
				if (yy<0) yy=0;
				else if (yy>=dest.size()) yy=dest.size()-1;
				align(xx,yy);
			}
		}
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
