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

package fr.umlv.unitex.frames;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.Preferences;

public class GlobalPreferencesFrameFactory {

	private GlobalPreferencesFrame frame;
	
	GlobalPreferencesFrame newGlobalPreferencesFrame() {
		if (frame==null) {
			frame=new GlobalPreferencesFrame();
		}
		frame.pref = Preferences.getCloneOfPreferences();
		frame.setTitle("Preferences for " + Config.getCurrentLanguage());
		frame.privateDirectory.setText(Config.getUserDir().getAbsolutePath());
		frame.refresh();
		return frame;
	}
	
	
	void closeGlobalPreferencesFrame() {
		if (frame==null) {
			return;
		}
		frame.doDefaultCloseAction();
	}


	GlobalPreferencesFrame getGlobalPreferencesFrame() {
		return newGlobalPreferencesFrame();
	}

}
