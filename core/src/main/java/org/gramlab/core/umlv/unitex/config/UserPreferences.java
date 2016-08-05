package org.gramlab.core.umlv.unitex.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.prefs.BackingStoreException;

/**
 * This class implements support for user level preferences.
 * 
 * @author Nebojša Vasiljević
 */
public class UserPreferences {

	private static final String ID_NODE = "unitex";
	private static final String ID_PREFERED_LANGUAGE = "prefered_language";
	private static final String ID_USER_DIR = "userDir";
	private static final String ID_RECENT_GRAPHS_NODE = "recent_graphs";
	private static final String PREFIX_ID_RECENT_GRAPH = "grph";
	private static final int MAX_RECENT_GRAPHS = 20;

	private static final String ID_RECENT_TEXTS_NODE = "recent_texts";
	private static final String PREFIX_ID_RECENT_TEXT = "snt";
	private static final String PREFIX_ID_RECENT_TEXTLANG = "lng";
	private static final int MAX_RECENT_TEXTS = 20;
	
	private static final String ID_RECENT_DICTIONARIES_NODE = "recent_dictionaries";
	private static final String PREFIX_ID_RECENT_DICTIONARY = "dict";
	private static final int MAX_RECENT_DICTIONARIES = 20;

	private java.util.prefs.Preferences prefs;

	boolean isInitialized() {
		return prefs != null;
	}

	public UserPreferences() {
		try {
			prefs = java.util.prefs.Preferences.userRoot().node(ID_NODE);
		} catch (SecurityException e) {
			prefs = null;
		} catch (IllegalStateException e) {
			prefs = null;
		}
	}

	public String getPreferedLanguage() {
		if (prefs == null)
			return null;
		try {
			return prefs.get(ID_PREFERED_LANGUAGE, null);
		} catch (IllegalStateException e) {
			return null;
		}
	}

	public boolean setPreferedLanguage(String lang) {
		if (prefs == null)
			return false;
		try {
			prefs.put(ID_PREFERED_LANGUAGE, lang);
			return true;
		} catch (IllegalStateException e) {
			return false;
		}
	}

	public String getUserDir() {
		if (prefs == null)
			return null;
		try {
			return prefs.get(ID_USER_DIR, null);
		} catch (IllegalStateException e) {
			return null;
		}
	}

	private java.util.prefs.Preferences recentGraphsPrefs() {
		if (prefs == null)
			return null;
		try {
			return prefs.node(ID_RECENT_GRAPHS_NODE);
		} catch (IllegalStateException e) {
			return null;
		}
	}

	public List<File> getRecentGraphs() {
		List<File> l = new ArrayList<File>();
		java.util.prefs.Preferences gp = recentGraphsPrefs();
		if (gp == null)
			return null;
		try {
			String[] keys = gp.keys();
			Arrays.sort(keys);
			for (String key : keys) {
				String val = gp.get(key, null);
				if (val == null)
					return null;
				l.add(new File(val));
			}
		} catch (BackingStoreException e) {
			return null;
		} catch (IllegalStateException e) {
			return null;
		}
		return l;
	}

	private boolean setRecentGraphs(List<File> l) {
		java.util.prefs.Preferences gp = recentGraphsPrefs();
		if (gp == null)
			return false;
		try {
			gp.clear();
			int m = l.size();
			if (m > MAX_RECENT_GRAPHS)
				m = MAX_RECENT_GRAPHS;
			for (int i = 0; i < m; i++) {
				String key;
				if (i < 10)
					key = PREFIX_ID_RECENT_GRAPH + "00" + i;
				else if (i < 100)
					key = PREFIX_ID_RECENT_GRAPH + "0" + i;
				else
					key = PREFIX_ID_RECENT_GRAPH + i;
				gp.put(key, l.get(i).getAbsolutePath());
			}
		} catch (BackingStoreException e) {
			return false;
		} catch (IllegalStateException e) {
			return false;
		}
		return true;
	}
	
	public boolean clearRecentGraphs() {
		return setRecentGraphs(new ArrayList<File>());
	}

	public boolean addRecentGraph(File nf) {
		if (nf == null)
			return false;
		List<File> l = getRecentGraphs();
		if (l == null)
			l = new ArrayList<File>();
		List<File> l1 = new ArrayList<File>();
		l1.add(nf);
		for (File f : l)
			if (!f.equals(nf))
				l1.add(f);
		return setRecentGraphs(l1);
	}

	public boolean removeRecentGraph(File rf) {
		List<File> l = getRecentGraphs();
		if (l == null)
			l = new ArrayList<File>();
		List<File> l1 = new ArrayList<File>();
		boolean removed = false;
		for (File f : l)
			if (!f.equals(rf))
				l1.add(f);
			else
				removed = true;
		if (removed)
			return setRecentGraphs(l1);
		return true;
	}

	private java.util.prefs.Preferences recentTextsPrefs() {
		if (prefs == null)
			return null;
		try {
			return prefs.node(ID_RECENT_TEXTS_NODE);
		} catch (IllegalStateException e) {
			return null;
		}
	}

	public List<SntFileEntry> getRecentTexts() {
		java.util.prefs.Preferences tp = recentTextsPrefs();
		if (tp == null)
			return null;
		List<SntFileEntry> ret = new ArrayList<SntFileEntry>(MAX_RECENT_GRAPHS);
		try {
			String[] keys = tp.keys();
			SntFileEntry[] l = new SntFileEntry[MAX_RECENT_GRAPHS];
			for (String key : keys) {
				String val = tp.get(key, null);
				if (val == null)
					return null;
				String s_ord = key.substring(key.length() - 3, key.length());
				int ord = -1;
				try {
					ord = Integer.parseInt(s_ord);
				} catch (NumberFormatException e) {/**/}
				if (ord >= 0 && ord < MAX_RECENT_GRAPHS) {
					if (l[ord] == null)
						l[ord] = new SntFileEntry();
					if (key.startsWith(PREFIX_ID_RECENT_TEXTLANG)) {
						l[ord].setLanguage(val);
					} else if (key.startsWith(PREFIX_ID_RECENT_TEXT)) {
						l[ord].setFile(new File(val));
					}
				}
			}
			for(SntFileEntry sfe:l)
				if(sfe!=null && sfe.getLanguage() != null && sfe.getFile() != null)
					ret.add(sfe);
		} catch (BackingStoreException e) {
			return null;
		} catch (IllegalStateException e) {
			return null;
		}
		return ret;
	}

	private boolean setRecentTexts(List<SntFileEntry> l) {
		java.util.prefs.Preferences tp = recentTextsPrefs();
		if (tp == null)
			return false;
		try {
			tp.clear();
			int m = l.size();
			if (m > MAX_RECENT_TEXTS)
				m = MAX_RECENT_TEXTS;
			for (int i = 0; i < m; i++) {
				String key;
				if (i < 10)
					key = PREFIX_ID_RECENT_TEXTLANG + "00" + i;
				else if (i < 100)
					key = PREFIX_ID_RECENT_TEXTLANG + "0" + i;
				else
					key = PREFIX_ID_RECENT_TEXTLANG + i;
				tp.put(key, l.get(i).getLanguage());

				if (i < 10)
					key = PREFIX_ID_RECENT_TEXT + "00" + i;
				else if (i < 100)
					key = PREFIX_ID_RECENT_TEXT + "0" + i;
				else
					key = PREFIX_ID_RECENT_TEXT + i;
				tp.put(key, l.get(i).getFile().getAbsolutePath());

			}
		} catch (BackingStoreException e) {
			return false;
		} catch (IllegalStateException e) {
			return false;
		}
		return true;
	}

	public boolean addRecentText(SntFileEntry tf) {
		List<SntFileEntry> l = getRecentTexts();
		if (l == null)
			l = new ArrayList<SntFileEntry>();
		List<SntFileEntry> l1 = new ArrayList<SntFileEntry>();
		l1.add(tf);
		for (SntFileEntry f : l)
			if (!f.equals(tf))
				l1.add(f);
		return setRecentTexts(l1);
	}

	public boolean removeRecentText(SntFileEntry rf) {
		List<SntFileEntry> l = getRecentTexts();
		if (l == null)
			l = new ArrayList<SntFileEntry>();
		List<SntFileEntry> l1 = new ArrayList<SntFileEntry>();
		boolean removed = false;
		for (SntFileEntry f : l)
			if (!f.equals(rf))
				l1.add(f);
			else
				removed = true;
		if (removed)
			return setRecentTexts(l1);
		return true;
	}

	public boolean clearRecentTexts() {
		return setRecentTexts(new ArrayList<SntFileEntry>());
	}
	
	public boolean setUserDir(String dir) {
		if (prefs == null)
			return false;
		try {
			prefs.put(ID_USER_DIR, dir);
			return true;
		} catch (IllegalStateException e) {
			return false;
		}
	}

	private java.util.prefs.Preferences recentDictionariesPrefs() {
		if (prefs == null)
			return null;
		try {
			return prefs.node(ID_RECENT_DICTIONARIES_NODE);
		} catch (IllegalStateException e) {
			return null;
		}
	}

	public List<File> getRecentDictionaries() {
		List<File> l = new ArrayList<File>();
		java.util.prefs.Preferences dp = recentDictionariesPrefs();
		if (dp == null)
			return null;
		try {
			String[] keys = dp.keys();
			Arrays.sort(keys);
			for (String key : keys) {
				String val = dp.get(key, null);
				if (val == null)
					return null;
				l.add(new File(val));
			}
		} catch (BackingStoreException e) {
			return null;
		} catch (IllegalStateException e) {
			return null;
		}
		return l;
	}

	private boolean setRecentDictionaries(List<File> l) {
		java.util.prefs.Preferences dp = recentDictionariesPrefs();
		if (dp == null)
			return false;
		try {
			dp.clear();
			int m = l.size();
			if (m > MAX_RECENT_DICTIONARIES)
				m = MAX_RECENT_DICTIONARIES;
			for (int i = 0; i < m; i++) {
				String key;
				if (i < 10)
					key = PREFIX_ID_RECENT_DICTIONARY + "00" + i;
				else if (i < 100)
					key = PREFIX_ID_RECENT_DICTIONARY + "0" + i;
				else
					key = PREFIX_ID_RECENT_DICTIONARY + i;
				dp.put(key, l.get(i).getAbsolutePath());
			}
		} catch (BackingStoreException e) {
			return false;
		} catch (IllegalStateException e) {
			return false;
		}
		return true;
	}

	public boolean clearRecentDictionaries() {
		return setRecentDictionaries(new ArrayList<File>());
	}

	public boolean addRecentDictionary(File df) {
		if (df == null)
			return false;
		List<File> l = getRecentDictionaries();
		if (l == null)
			l = new ArrayList<File>();
		List<File> l1 = new ArrayList<File>();
		l1.add(df);
		for (File f : l)
			if (!f.equals(df))
				l1.add(f);
		return setRecentDictionaries(l1);
	}
	
	public boolean removeRecentDictionary(File df) {
		List<File> l = getRecentDictionaries();
		if (l == null)
			l = new ArrayList<File>();
		List<File> l1 = new ArrayList<File>();
		boolean removed = false;
		for (File f : l)
			if (!f.equals(df))
				l1.add(f);
			else
				removed = true;
		if (removed)
			return setRecentDictionaries(l1);
		return true;
	}
}