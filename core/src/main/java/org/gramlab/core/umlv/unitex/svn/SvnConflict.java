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
package fr.umlv.unitex.svn;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.commands.GrfDiff3Command;
import fr.umlv.unitex.process.commands.SvnCommand;

public class SvnConflict {
	static Pattern pattern = Pattern.compile("r([0-9]+)");
	public File fileInConflict, mine, base, other, merged;
	public int baseNumber, otherNumber;

	public SvnConflict(final File fileInConflict, File mine, File base,
			File other, int baseNumber, int otherNumber) {
		this.fileInConflict = fileInConflict;
		this.merged = new File(fileInConflict.getAbsolutePath() + ".merged");
		this.mine = mine;
		this.base = base;
		this.other = other;
		this.baseNumber = baseNumber;
		this.otherNumber = otherNumber;
		addConflictSolvedListener(new ConflictSolvedListener() {
			@Override
			public void conflictSolved() {
				ConfigManager.getManager().getSvnMonitor(fileInConflict)
						.conflictResolved(fileInConflict);
			}
		});
	}

	/**
	 * Tests if there is a svn conflict on the given file.
	 * 
	 * @param file
	 * @return
	 */
	public static SvnConflict getConflict(File file) {
		if (!file.exists()) {
			/* May happen if a temp file has been detected before being deleted */
			return null;
		}
		final String name = file.getName();
		final File[] list = file.getParentFile().listFiles(
				new FilenameFilter() {
					@Override
					public boolean accept(File dir, String s) {
						if (!s.startsWith(name + ".r"))
							return false;
						final String end = s.substring(s.lastIndexOf(".") + 1);
						return pattern.matcher(end).matches();
					}
				});
		if (list.length == 0)
			return null;
		if (list.length != 2) {
			throw new IllegalStateException(list.length + " file(s) named "
					+ file.getAbsolutePath() + ".rXXXX while there should be 2");
		}
		int baseNumber = getRevisionNumber(list[0]);
		int otherNumber = getRevisionNumber(list[1]);
		File base = list[0];
		File other = list[1];
		if (baseNumber > otherNumber) {
			base = list[1];
			other = list[0];
			final int tmp = baseNumber;
			baseNumber = otherNumber;
			otherNumber = tmp;
		}
		final File mine = new File(file.getAbsolutePath() + ".mine");
		if (!mine.exists()) {
			/*
			 * Some svn clients do not produce a .mine file: they just let the
			 * original file untouched. To deal with that, we create a .mine
			 * file in that case
			 */
			FileUtil.copyFile(file, mine);
		} else {
			/*
			 * If the original grf is modified, we restore it from .mine so that
			 * the user can still open it in the graph editor
			 */
			FileUtil.copyFile(mine, file);
		}
		return new SvnConflict(file, mine, base, other, baseNumber, otherNumber);
	}

	private static int getRevisionNumber(File file) {
		final String s = file.getAbsolutePath();
		final String end = s.substring(s.lastIndexOf(".") + 1);
		final Matcher m = pattern.matcher(end);
		m.matches();
		return Integer.parseInt(m.group(1));
	}

	private final ArrayList<ConflictSolvedListener> listeners = new ArrayList<ConflictSolvedListener>();
	private boolean firing = false;

	public void addConflictSolvedListener(ConflictSolvedListener l) {
		listeners.add(l);
	}

	public void removeConflictSolvedListener(ConflictSolvedListener l) {
		if (firing) {
			throw new IllegalStateException(
					"Cannot remove a listener while firing");
		}
		listeners.remove(l);
	}

	protected void fireConflictSolved() {
		firing = true;
		try {
			for (final ConflictSolvedListener l : listeners) {
				l.conflictSolved();
			}
		} finally {
			firing = false;
		}
	}

	/**
	 * Solve the conflict by using 'mine' version
	 */
	public void useMine() {
		fileInConflict.delete();
		other.delete();
		base.delete();
		merged.delete();
		mine.renameTo(fileInConflict);
		final SvnCommand cmd = new SvnCommand().resolved(fileInConflict);
		Launcher.execWithoutTracing(cmd);
		fireConflictSolved();
	}

	/**
	 * Solve the conflict by using 'other' version
	 */
	public void useOther() {
		fileInConflict.delete();
		mine.delete();
		base.delete();
		merged.delete();
		other.renameTo(fileInConflict);
		final SvnCommand cmd = new SvnCommand().resolved(fileInConflict);
		Launcher.execWithoutTracing(cmd);
		fireConflictSolved();
	}

	public void useBase() {
		fileInConflict.delete();
		other.delete();
		mine.delete();
		merged.delete();
		base.renameTo(fileInConflict);
		final SvnCommand cmd = new SvnCommand().resolved(fileInConflict);
		Launcher.execWithoutTracing(cmd);
		fireConflictSolved();
	}

	public void useWorking() {
		other.delete();
		mine.delete();
		base.delete();
		merged.delete();
		final SvnCommand cmd = new SvnCommand().resolved(fileInConflict);
		Launcher.execWithoutTracing(cmd);
		fireConflictSolved();
	}

	/**
	 * Tries to invoke GrfDiff3 to merge the files. Returns true on success;
	 * false otherwise.
	 */
	public boolean merge(boolean onlyCosmetic) {
		final GrfDiff3Command diff3 = new GrfDiff3Command()
				.files(mine, base, other).output(merged)
				.onlyCosmetic(onlyCosmetic);
		final int res = Launcher.execWithoutTracing(diff3);
		if (res != 0) {
			merged.delete();
			return false;
		}
		/*
		 * mine.delete(); base.delete(); other.delete(); fireConflictSolved();
		 */
		return true;
	}
}
