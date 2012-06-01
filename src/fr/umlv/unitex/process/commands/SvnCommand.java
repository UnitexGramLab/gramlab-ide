/*
 * Unitex
 *
 * Copyright (C) 2001-2012 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
		protectElement(new File(ConfigManager.getManager().getApplicationDirectory(), "svnkitclient.jar")
				.getAbsolutePath());
		element("--non-interactive");
		element("--trust-server-cert");
	}

	
	public SvnCommand auth(String login,char[] passwd) {
		if (login!=null) {
			element("--username");
			protectElement(login);
		}
		if (passwd!=null) {
			element("--password");
			protectElement(passwd);
		}
		return this;
	}
	
	/**
	 * WARNING: every call to 'svn checkout' should be followed
	 *          by a call to 'svn propset' on all .grf files in order
	 *          to make them appear as binary files, so that svn
	 *          won't try to merge them which might happen for UTF8
	 *          .grf files
	 */
	public SvnCommand checkout(String url,File destPath) {
		element("checkout");
		element("--force");
		protectElement(url);
		protectElement(destPath.getAbsolutePath());
		return this;
	}

	
	public SvnCommand commit(String message,File path) {
		element("commit");
		element("-m");
		protectElement(message);
		if (path!=null) {
			protectElement(path.getAbsolutePath());
		}
		return this;
	}
	
	/**
	 * See comment above 'checkout'
	 */
	public SvnCommand update(File path) {
		element("update");
		if (path!=null) {
			protectElement(path.getAbsolutePath());
		}
		return this;
	}


	public SvnCommand info(File f,boolean recursive) {
		element("info");
		if (recursive) element("-R");
		protectElement(f.getAbsolutePath());
		return this;
	}

	public SvnCommand add(File targetList) {
		element("add");
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
	
	
	public SvnCommand initialImport(File path,String url) {
		element("import");
		element("-m");
		protectElement("Initial import");
		element("--auto-props");
		element("--config-option");
		protectElement("config:auto-props:*.grf=svn:mime-type=application/octet-stream");
		element("--config-option");
		protectElement(GLOBAL_IGNORES);
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

	
	public SvnCommand commit(File targetList,String message) {
		element("commit");
		element("-m");
		protectElement(message);
		element("--config-option");
		protectElement("config:auto-props:*.grf=svn:mime-type=application/octet-stream");
		element("--config-option");
		protectElement(GLOBAL_IGNORES);
		element("--targets");
		protectElement(targetList.getAbsolutePath());
		return this;
	}

	public SvnCommand commit(File targetList,File logFile) {
		element("commit");
		element("-F");
		protectElement(logFile.getAbsolutePath());
		element("--config-option");
		protectElement("config:auto-props:*.grf=svn:mime-type=application/octet-stream");
		element("--config-option");
		protectElement(GLOBAL_IGNORES);
		element("--targets");
		protectElement(targetList.getAbsolutePath());
		return this;
	}

}
