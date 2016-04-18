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
/**
 * Unitex/GramLab Skeleton plugin
 * add by martinec
 */ 
package org.gramlab.plugins.skel;

import ro.fortsoft.pf4j.PluginWrapper;
import com.github.zafarkhaja.semver.Version;

import org.gramlab.api.GramLabPlugin;

public class Plugin extends GramLabPlugin {

	public Plugin(PluginWrapper wrapper) {
		super(wrapper);
	}

	@Override
	public void start() {
		log.debug("{} STARTED.", getWrapper().getPluginId());
	}

	@Override
	public void stop() {
		log.debug("{} STOPPED.", getWrapper().getPluginId());
	}

	@Override
	public void onInstall() {
		log.debug("{} INSTALLED.", getWrapper().getPluginId());
	}

	@Override
	public void onUpgrade(Version previousVersion) {
		log.debug("{} UPGRADED from {}.", getWrapper().getPluginId(), previousVersion);
	}

	@Override
	public void onUninstall() {
		log.debug("{} UNINSTALLED.", getWrapper().getPluginId());
	}

	@Override
	public void onException() {
		log.debug("{} EXCEPTION.", getWrapper().getPluginId());
	}
}
