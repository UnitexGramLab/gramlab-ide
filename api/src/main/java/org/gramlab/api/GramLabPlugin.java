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
package org.gramlab.api;

import ro.fortsoft.pf4j.Plugin;
import ro.fortsoft.pf4j.PluginWrapper;
import ro.fortsoft.pf4j.RuntimeMode;
import com.github.zafarkhaja.semver.Version;

/**
 * GramLabPlugin
 *
 * This class will be extended by all GramLab plugins and
 * serve as the common class between a plugin and the IDE.
 *
 * This class is based on the GitblitPlugin.java interface
 * @see https://github.com/gitblit
 */
public abstract class GramLabPlugin extends Plugin {

	public GramLabPlugin(PluginWrapper wrapper) {
		super(wrapper);
	}

	/**
	 * Called after a plugin as been loaded but before it is started for the
	 * first time.  This allows the plugin to install settings or perform any
	 * other required first-time initialization.
	 */
	public abstract void onInstall();

	/**
	 * Called after an updated plugin has been installed but before the updated
	 * plugin is started.  The previousVersion is passed as a parameter in the event
	 * that special processing needs to be executed.
	 *
	 * @param previousVersion
	 */
	public abstract void onUpgrade(Version previousVersion);

	/**
	 * Called before a plugin has been unloaded and deleted from the system.
	 * This allows a plugin to remove any settings it may have created or
	 * perform and other necessary cleanup.
	 */
	public abstract void onUninstall();

	/**
	 * Called after a plugin throws an exception
	 */
  public abstract void onException();
}
