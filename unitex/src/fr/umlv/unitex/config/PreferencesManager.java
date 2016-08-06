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
package fr.umlv.unitex.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * 
 * @author paumier
 * 
 */
public class PreferencesManager {
	/**
	 * Loads a config file. If base is not null, then copies of its values are
	 * overridden instead of overriding default values. base is not modified.
	 * 
	 * @param f
	 * @param base
	 * @return
	 */
	public static Preferences loadPreferences(File f, Preferences base) {
		Preferences p;
		if (base != null) {
			p = base.clone();
			p.setBase(base);
		} else {
			p = new Preferences();
		}
		final Properties properties = loadProperties(f);
		p.setPreferencesFromProperties(properties);
		return p;
	}

	private static Properties loadProperties(File f) {
		final Properties languageProperties = new Properties(null);
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(f);
		} catch (final FileNotFoundException e) {
			return languageProperties;
		}
		try {
			languageProperties.load(stream);
			stream.close();
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
		return languageProperties;
	}

	public static void savePreferences(File f, Preferences p, String language) {
		final Properties prop = p.getOwnProperties();
		saveProperties(f, prop);
		firePreferencesChanged(language);
	}

	private static void saveProperties(File f, Properties prop) {
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(f);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		try {
			prop.store(stream, "Unitex configuration file");
		} catch (final IOException e1) {
			e1.printStackTrace();
			return;
		}
		try {
			stream.close();
		} catch (final IOException e2) {
			e2.printStackTrace();
		}
	}

	static class Info {
		File f;
		long date;
		Preferences p;
	}

	private static HashMap<String, Info> cache = new HashMap<String, Info>();

	/**
	 * Returns the preferences for the given language. This IS NOT A COPY, so
	 * the caller may have to clone it before any modification.
	 */
	public static Preferences getPreferences(String language) {
		if (language == null) {
			throw new IllegalArgumentException("Unexpected null language");
		}
		Info info = cache.get(language);
		if (info != null) {
			/* There is something in the cache. Is it still up-to-date ? */
			if (info.f.exists() && info.f.lastModified() <= info.date) {
				return info.p;
			}
			info.f = ConfigManager.getManager().getConfigFileForLanguage(
					language);
			info.date = info.f.lastModified();
			info.p = loadPreferences(info.f, null);
			return info.p;
		}
		info = new Info();
		info.f = ConfigManager.getManager().getConfigFileForLanguage(language);
		info.date = info.f.lastModified();
		info.p = loadPreferences(info.f, null);
		cache.put(language, info);
		return info.p;
	}
	
	private static UserPreferences userPreferences = null;
	/**
	 * Returns the user level preferences. 
	 * 
	 */
	public static UserPreferences getUserPreferences() {
		if(userPreferences == null)
			userPreferences = new UserPreferences();
		return userPreferences;
	}

	private static ArrayList<PreferencesListener> preferencesListeners = new ArrayList<PreferencesListener>();

	public static void addPreferencesListener(PreferencesListener listener) {
		preferencesListeners.add(listener);
	}

	private static boolean firingPreferencesChanged = false;

	public static void removePreferencesListener(PreferencesListener listener) {
		if (firingPreferencesChanged) {
			throw new IllegalStateException(
					"Should not try to remove a listener while firing");
		}
		preferencesListeners.remove(listener);
	}

	protected static void firePreferencesChanged(String language) {
		if (preferencesListeners == null)
			return;
		firingPreferencesChanged = true;
		try {
			for (final PreferencesListener listener : preferencesListeners) {
				listener.preferencesChanged(language);
			}
		} finally {
			firingPreferencesChanged = false;
		}
	}
}
