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
package fr.umlv.unitex.process.commands;

import java.io.File;

import fr.umlv.unitex.config.ConfigManager;

/**
 * @author Sébastien Paumier
 * 
 */
public class SvnCommand extends CommandBuilder {

	private static final String GLOBAL_IGNORES = "config:miscellany:global-ignores=..* *.fst2 *.bin *.inf target dep build project.local_config diff";

	public SvnCommand() {
		super(false);
		element("java");
		element("-jar");
		protectElement(new File(ConfigManager.getManager()
				                    .getApplicationDirectory().getPath() +
				                     File.separatorChar + "lib",
				                     "svnkitclient.jar").getAbsolutePath());
		element("--non-interactive");
		element("--trust-server-cert");
	}

	public SvnCommand auth(String login, char[] passwd) {
		if (login != null) {
			element("--username");
			protectElement(login);
		}
		if (passwd != null) {
			element("--password");
			protectElement(passwd);
		}
		return this;
	}

	/**
	 * WARNING: every call to 'svn checkout' should be followed by a call to
	 * 'svn propset' on all .grf files in order to make them appear as binary
	 * files, so that svn won't try to merge them which might happen for UTF8
	 * .grf files
	 */
	public SvnCommand checkout(String url, File destPath) {
		element("checkout");
		element("--force");
		protectElement(url);
		protectElement(destPath.getAbsolutePath());
		return this;
	}

	public SvnCommand commit(String message, File path) {
		element("commit");
		element("-m");
		protectElement(message);
		if (path != null) {
			protectElement(path.getAbsolutePath());
		}
		return this;
	}

	/**
	 * See comment above 'checkout' revision==-1 means update to head
	 * revision==-2 means update to base if forceAccept==true, we want to force
	 * the accept in case of conflict
	 */
	public SvnCommand update(int revision, File path, boolean forceAccept) {
		element("update");
		if (revision == -2) {
			element("-r");
			element("BASE");
		} else if (revision != -1) {
			element("-r");
			element("" + revision);
		}
		if (forceAccept) {
			if (revision == -2) {
				element("--accept=base");
			} else {
				element("--accept=theirs-full");
			}
		}
		if (path != null) {
			protectElement(path.getAbsolutePath());
		}
		return this;
	}

	public SvnCommand info(File f, boolean recursive, boolean headRevision) {
		element("info");
		if (recursive)
			element("-R");
		if (headRevision) {
			element("-r");
			element("HEAD");
		}
		protectElement(f.getAbsolutePath());
		return this;
	}

	public SvnCommand info(File f, boolean recursive) {
		return info(f, recursive, false);
	}

	public SvnCommand add(File targetList) {
		element("add");
		element("--force");
		element("--parents");
		element("--auto-props");
		element("--config-option");
		protectElement("config:auto-props:*.grf=svn:mime-type=application/octet-stream");
		element("--config-option");
		protectElement(GLOBAL_IGNORES);
		element("--targets");
		protectElement(targetList.getAbsolutePath());
		return this;
	}

	public SvnCommand delete(File targetList) {
		element("delete");
		element("--force");
		element("--targets");
		protectElement(targetList.getAbsolutePath());
		return this;
	}

	public SvnCommand delete(String url, String message) {
		element("delete");
		element("--force");
		element("-m");
		protectElement(message);
		protectElement(url);
		return this;
	}

	public SvnCommand initialImport(File path, String url, String extraIgnores) {
		element("import");
		element("-m");
		protectElement("Initial import");
		element("--auto-props");
		element("--config-option");
		protectElement("config:auto-props:*.grf=svn:mime-type=application/octet-stream");
		element("--config-option");
		if (extraIgnores == null) {
			protectElement(GLOBAL_IGNORES + extraIgnores);
		} else {
			protectElement(GLOBAL_IGNORES + " " + extraIgnores);
		}
		protectElement(path.getAbsolutePath());
		protectElement(url);
		return this;
	}

	public SvnCommand status() {
		element("status");
		element("--config-option");
		protectElement(GLOBAL_IGNORES);
		return this;
	}

	public SvnCommand commit(File targetList, String message) {
		element("commit");
		element("-m");
		protectElement(message);
		element("--config-option");
		protectElement("config:auto-props:*.grf=svn:mime-type=application/octet-stream");
		element("--config-option");
		protectElement(GLOBAL_IGNORES);
		element("--depth=files");
		element("--targets");
		protectElement(targetList.getAbsolutePath());
		return this;
	}

	public SvnCommand commit(File targetList, File logFile) {
		element("commit");
		element("-F");
		protectElement(logFile.getAbsolutePath());
		element("--config-option");
		protectElement("config:auto-props:*.grf=svn:mime-type=application/octet-stream");
		element("--config-option");
		protectElement(GLOBAL_IGNORES);
		element("--depth=files");
		element("--targets");
		protectElement(targetList.getAbsolutePath());
		return this;
	}

	public SvnCommand resolve(File file, ResolveOp op) {
		element("resolve");
		element(op.getOption());
		protectElement(file.getAbsolutePath());
		return this;
	}

	public SvnCommand resolved(File file) {
		element("resolved");
		protectElement(file.getAbsolutePath());
		return this;
	}

	public enum ResolveOp {
		ACCEPT_WORKING("--accept=working"), ACCEPT_MINE("--accept=mine-full"), ACCEPT_BASE(
				"--accept=base"), ACCEPT_OTHER("--accept=theirs-full");

		private ResolveOp(String o) {
			this.option = o;
		}

		private String option;

		String getOption() {
			return option;
		}

	}

	public SvnCommand getIgnoreList(File dir) {
		element("propget");
		element("svn:ignore");
		protectElement(dir.getAbsolutePath());
		return this;
	}

	public SvnCommand setIgnoreList(File dir, File list) {
		element("propset");
		element("svn:ignore");
		element("-F");
		protectElement(list.getAbsolutePath());
		protectElement(dir.getAbsolutePath());
		return this;
	}

	public SvnCommand revert(File targetList) {
		element("revert");
		element("--depth");
		element("infinity");
		element("--targets");
		protectElement(targetList.getAbsolutePath());
		return this;
	}

	public SvnCommand cleanup(File dir) {
		element("cleanup");
		protectElement(dir.getAbsolutePath());
		return this;
	}

}
