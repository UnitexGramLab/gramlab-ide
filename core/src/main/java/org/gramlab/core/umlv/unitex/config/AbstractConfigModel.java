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
package org.gramlab.core.umlv.unitex.config;

import java.io.File;
import java.util.ArrayList;

public abstract class AbstractConfigModel implements ConfigModel {
	@Override
	public boolean isKorean(String language) {
		if (language == null)
			language = getCurrentLanguage();
		return language.equals("Korean");
	}

	@Override
	public boolean isArabic(String language) {
		if (language == null)
			language = getCurrentLanguage();
		return language.equals("Arabic");
	}

	@Override
	public boolean isThai(String language) {
		if (language == null)
			language = getCurrentLanguage();
		return language.equals("Thai");
	}

	@Override
	public boolean isPRLGLanguage(String language) {
		return true;
		/*
		 * if (language == null) language = getCurrentLanguage(); return
		 * language.equals("Greek (Ancient)") ||
		 * language.equals("Arabic (Middle Arabic)") ||
		 * language.equals("Armenian (Ancient)") ||
		 * language.equals("Georgian (Ancient)") || language.equals("Latin");
		 */
	}

	@Override
	public boolean isValidLanguageName(String language) {
		return !(language.equals("App") || language.equals("Users")
				|| language.equals("Src") || language.equals("XAlign") || language
					.startsWith("."));
	}

	@Override
	public File getGraphRepositoryPath(String language, String name) {
		if (name == null) {
			return getDefaultGraphRepositoryPath(language);
		}
		final ArrayList<NamedRepository> list = getNamedRepositories(name);
		if (list == null)
			return null;
		for (final NamedRepository n : list) {
			if (n.getName().equals(name)) {
				return n.getFile();
			}
		}
		return null;
	}

}
