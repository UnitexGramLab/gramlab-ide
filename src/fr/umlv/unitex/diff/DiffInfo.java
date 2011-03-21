/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

package fr.umlv.unitex.diff;

import java.awt.Color;
import java.awt.Stroke;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class DiffInfo {

	boolean base=true;
	
	public ArrayList<String> propertyOps=new ArrayList<String>();
	ArrayList<Integer> boxAdded=new ArrayList<Integer>();
	ArrayList<Integer> boxRemoved=new ArrayList<Integer>();
	ArrayList<Integer> boxContentChanged=new ArrayList<Integer>();
	ArrayList<Integer> boxMoved=new ArrayList<Integer>();
	ArrayList<Integer> transitionAdded=new ArrayList<Integer>();
	ArrayList<Integer> transitionRemoved=new ArrayList<Integer>();

	public static DiffInfo loadDiffFile(File f) {
		try {
			Scanner scanner=new Scanner(f,"UTF-8");
			DiffInfo info=new DiffInfo();
			while (scanner.hasNext()) {
				try {
					String s=scanner.next();
					if (s.equals("P")) {
						/* Propery changed */
						info.propertyOps.add(scanner.next());
						continue;
					}
					if (s.equals("M")) {
						/* Box moved */
						info.boxMoved.add(scanner.nextInt());
						info.boxMoved.add(scanner.nextInt());
						continue;
					}
					if (s.equals("C")) {
						/* Box content changed */
						info.boxContentChanged.add(scanner.nextInt());
						info.boxContentChanged.add(scanner.nextInt());
						continue;
					}
					if (s.equals("A")) {
						/* Box added */
						info.boxAdded.add(scanner.nextInt());
						continue;
					}
					if (s.equals("R")) {
						/* Box removed */
						info.boxRemoved.add(scanner.nextInt());
						continue;
					}
					if (s.equals("T")) {
						/* Transition added: we only store the information
						 * about the dest graph */
						scanner.nextInt();
						scanner.nextInt();
						info.transitionAdded.add(scanner.nextInt());
						info.transitionAdded.add(scanner.nextInt());
						continue;
					}
					if (s.equals("X")) {
						/* Transition removed: we only store the information
						 * about the base graph */
						info.transitionRemoved.add(scanner.nextInt());
						info.transitionRemoved.add(scanner.nextInt());
						scanner.nextInt();
						scanner.nextInt();
						continue;
					}
					scanner.close();
					return null;
				} catch (NoSuchElementException e) {
					scanner.close();
					return null;
				}
			}
			scanner.close();
			return info;
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public DiffInfo clone(boolean b) {
		DiffInfo info=new DiffInfo();
		info.base=b;
		info.propertyOps=(ArrayList<String>) propertyOps.clone();
		info.boxAdded=(ArrayList<Integer>) boxAdded.clone();
		info.boxRemoved=(ArrayList<Integer>) boxRemoved.clone();
		info.boxContentChanged=(ArrayList<Integer>) boxContentChanged.clone();
		info.boxMoved=(ArrayList<Integer>) boxMoved.clone();
		info.transitionAdded=(ArrayList<Integer>) transitionAdded.clone();
		info.transitionRemoved=(ArrayList<Integer>) transitionRemoved.clone();
		return info;
	}
	
	
	public boolean hasBeenRemoved(int n) {
		return base && boxRemoved.contains(Integer.valueOf(n));
	}

	public boolean hasBeenAdded(int n) {
		return !base && boxAdded.contains(Integer.valueOf(n));
	}
	
	public boolean contentChanged(int n) {
		for (int i=base?0:1;i<boxContentChanged.size();i+=2) {
			if (n==boxContentChanged.get(i)) return true;
		}
		return false;
	}
	
	public boolean hasMoved(int n) {
		for (int i=base?0:1;i<boxMoved.size();i+=2) {
			if (n==boxMoved.get(i)) return true;
		}
		return false;
	}
	
	public boolean transitionRemoved(int n,int dest) {
		if (!base) return false;
		for (int i=0;i<transitionRemoved.size();i+=2) {
			if (n==transitionRemoved.get(i) && dest==transitionRemoved.get(i+1)) return true;
		}
		return false;
	}
	
	public boolean transitionAdded(int n,int dest) {
		if (base) return false;
		for (int i=0;i<transitionAdded.size();i+=2) {
			if (n==transitionAdded.get(i) && dest==transitionAdded.get(i+1)) return true;
		}
		return false;
	}
	
	
	public boolean noDifference() {
		return propertyOps.size()==0
			&& boxAdded.size()==0
			&& boxRemoved.size()==0
			&& boxContentChanged.size()==0
			&& transitionAdded.size()==0
			&& transitionRemoved.size()==0;
	}


	public Color getTransitionColor(int boxNumber, int destNumber,Color c) {
		if (transitionAdded(boxNumber,destNumber)) return DiffColors.ADDED;
		if (transitionRemoved(boxNumber,destNumber)) return DiffColors.REMOVED;
		return c;
	}


	public Stroke getTransitionStroke(int boxNumber, int destNumber,Stroke s) {
		if (transitionAdded(boxNumber,destNumber)) return DiffColors.DIFF_STROKE;
		if (transitionRemoved(boxNumber,destNumber)) return DiffColors.DIFF_STROKE;
		return s;
	}

	public Stroke getBoxStroke(int boxNumber,Stroke s) {
		if (hasBeenAdded(boxNumber)) return DiffColors.DIFF_STROKE;
		if (hasBeenRemoved(boxNumber)) return DiffColors.DIFF_STROKE;
		if (hasMoved(boxNumber)) return DiffColors.DIFF_STROKE;
		return s;
	}


	public Color getBoxLineColor(int boxNumber,Color c) {
		if (hasBeenAdded(boxNumber)) return DiffColors.ADDED;
		if (hasBeenRemoved(boxNumber)) return DiffColors.REMOVED;
		if (hasMoved(boxNumber)) return DiffColors.MOVED;
		return c;
	}

	public boolean requiresSpecialLineDrawing(int boxNumber) {
		return hasBeenAdded(boxNumber) 
			|| hasBeenRemoved(boxNumber)
			|| hasMoved(boxNumber);
	}

	public Color getBoxFillColor(int boxNumber,Color c) {
		if (contentChanged(boxNumber)) return DiffColors.CONTENT_CHANGED;
		return c;
	}

}
