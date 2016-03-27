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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.Timer;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.frames.InternalFrameManager;

public class SvnMonitor {

	private final File rootDir;
	private final boolean unitexMode;

	/**
	 * Under Unitex, we always monitor the current graph directory as well as
	 * the graph repository, and so, 'dir' is ignored. Under gramlab, we just
	 * monitor 'dir' which would then be the 'src' directory, because in
	 * Gramlab, if a repository is not in src,
	 */
	public SvnMonitor(File dir, boolean unitexMode) {
		this.rootDir = dir;
		this.unitexMode = unitexMode;
	}

	final DefaultListModel svnConflictModel = new DefaultListModel();
	private final Timer timer = new Timer(5000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (ConfigManager.getManager().svnMonitoring(null)) {
				monitor(true);
			}
		}
	});

	public void monitor(boolean autoMonitoring) {
		for (int i = svnConflictModel.size() - 1; i >= 0; i--) {
			if (SvnConflict.getConflict((File) svnConflictModel.get(i)) == null) {
				/*
				 * If a previously reported conflict has been resolved, we
				 * remove it from our list
				 */
				svnConflictModel.remove(i);
			}
		}
		if (unitexMode) {
			monitor(Config.getCurrentGraphDir());
			monitor(ConfigManager.getManager().getGraphRepositoryPath(null,
					null));
			if (!autoMonitoring || svnConflictModel.size() > 0) {
				GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.showSvnConflictsFrame(this);
			}
		} else {
			monitor(rootDir);
			if (!autoMonitoring || svnConflictModel.size() > 0) {
				GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.showSvnConflictsFrame(this);
			}
		}
	}

	/**
	 * Looks recursively for conflicting grfs in the given directory and reports
	 * them in the given list. We use a list in order to avoid duplicate
	 * reports.
	 */
	protected void monitor(File dir) {
		if (dir == null || !dir.exists())
			return;
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("monitor() expects a directory");
		}
		final File svn = new File(dir, ".svn");
		if (unitexMode && !svn.exists()) {
			/*
			 * If the directory is not versioned with svn, there is nothing to
			 * do. Note that we don't apply this criterion in Gramlab, since we
			 * know for sure that we are in a versioned directory.
			 */
			return;
		}
		/* We look for conflicts */
		final File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(@SuppressWarnings("hiding") File dir, String s) {
				return s.endsWith(".grf");
			}
		});
		for (final File candidate : files) {
			if (SvnConflict.getConflict(candidate) != null
					&& !svnConflictModel.contains(candidate)) {
				svnConflictModel.addElement(candidate);
			}
		}
		/* And we explore recursively directories */
		final File[] dirs = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File d, String s) {
				final File f = new File(d, s);
				return f.isDirectory();
			}
		});
		for (final File d : dirs) {
			monitor(d);
		}
	}

	public void start() {
		timer.start();
	}

	public void conflictResolved(File grf) {
		svnConflictModel.removeElement(grf);
	}

	public ListModel getSvnConflictModel() {
		return svnConflictModel;
	}
}
