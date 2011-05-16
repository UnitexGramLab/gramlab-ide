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

package fr.umlv.unitex.svn;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.umlv.unitex.Preferences;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.commands.GrfDiff3Command;

public class SvnConflict {

	static Pattern pattern=Pattern.compile("r([0-9]+)");

	public File grf,mine,base,other;
	public int baseNumber,otherNumber;
	
	public SvnConflict(final File grf, File mine, File base, File other,
			int baseNumber, int otherNumber) {
		this.grf=grf;
		this.mine=mine;
		this.base=base;
		this.other=other;
		this.baseNumber=baseNumber;
		this.otherNumber=otherNumber;
		addConflictSolvedListener(new ConflictSolvedListener() {
			public void conflictSolved() {
				SvnMonitor.conflictResolved(grf);
			}
		});
	}


	/**
	 * Tests if there is a svn conflict on the given file.
	 * @param grf
	 * @return
	 */
	public static SvnConflict getConflict(File grf) {
		if (!grf.exists()) {
			throw new IllegalArgumentException("File "+grf.getAbsolutePath()+" does not exist");
		}
		final String name=grf.getName();
		File mine=new File(grf.getAbsolutePath()+".mine");
		if (!mine.exists()) return null;
		File[] list=grf.getParentFile().listFiles(new FilenameFilter() {
			public boolean accept(File dir,String s) {
				if (!s.startsWith(name+".r")) return false;
				String end=s.substring(s.lastIndexOf(".")+1);
				return pattern.matcher(end).matches();
			}
		});
		if (list.length!=2) {
			throw new IllegalStateException("More than 2 files named "+grf.getAbsolutePath()+".rXXXX");
		}
		int baseNumber=getRevisionNumber(list[0]);
		int otherNumber=getRevisionNumber(list[1]);
		File base=list[0];
		File other=list[1];
		if (baseNumber>otherNumber) {
			base=list[1];
			other=list[0];
			int tmp=baseNumber;
			baseNumber=otherNumber;
			otherNumber=tmp;
		}
		return new SvnConflict(grf,mine,base,other,baseNumber,otherNumber);
	} 

	
	private static int getRevisionNumber(File file) {
		String s=file.getAbsolutePath();
		String end=s.substring(s.lastIndexOf(".")+1);
		Matcher m=pattern.matcher(end);
		m.matches();
		return Integer.parseInt(m.group(1));
	}



	private ArrayList<ConflictSolvedListener> listeners=new ArrayList<ConflictSolvedListener>();
	private boolean firing=false;
	
	public void addConflictSolvedListener(ConflictSolvedListener l) {
		listeners.add(l);
	}

	public void removeConflictSolvedListener(ConflictSolvedListener l) {
		if (firing) {
			throw new IllegalStateException("Cannot remove a listener while firing");
		}
		listeners.remove(l);
	}
	
	protected void fireConflictSolved() {
		firing=true;
		try {
			for (ConflictSolvedListener l:listeners) {
				l.conflictSolved();
			}
		} finally {
			firing=false;
		}
	}


	/**
	 * Solve the conflict by using 'mine' version
	 */
	public void useMine() {
		grf.delete();
		other.delete();
		base.delete();
		mine.renameTo(grf);
		fireConflictSolved();
	}

	/**
	 * Solve the conflict by using 'other' version
	 */
	public void useOther() {
		grf.delete();
		mine.delete();
		base.delete();
		other.renameTo(grf);
		fireConflictSolved();
	}


	/**
	 * Tries to invoke GrfDiff3 to merge the files. Returns true
	 * on success; false otherwise.
	 */
	public boolean merge() {
		GrfDiff3Command diff3=new GrfDiff3Command().files(mine,base,other)
		.output(grf).onlyCosmetic(Preferences.onlyCosmetic());
		int res=Launcher.execWithoutTracing(diff3);
		if (res!=0)	return false;
		mine.delete();
		base.delete();
		other.delete();
		fireConflictSolved();
		return true;
	}
}