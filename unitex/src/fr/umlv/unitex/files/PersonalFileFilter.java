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
package fr.umlv.unitex.files;

import java.io.File;

/**
 * This class provides a file filter definition.
 * 
 * @author Sébastien Paumier
 */
public class PersonalFileFilter extends javax.swing.filechooser.FileFilter {
	private final String ext;
	private final String description;
	private boolean isDictionary = false;

	/**
	 * Constructs a new <code>PersonalFileFilter</code>
	 * 
	 * @param ex
	 *            the extension of accepted files
	 * @param descript
	 *            a short description of accepted files
	 */
	public PersonalFileFilter(String ex, String descript) {
		super();
		ext = ex;
		description = descript;
		if (ext.equals("dic"))
			isDictionary = true;
	}

	/**
	 * Tests if a file is accepted by the filter
	 * 
	 * @return <code>true</code> if the file is accepted, <code>false</code>
	 *         otherwise
	 */
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		if (isDictionary) {
			final String s = f.getName();
			if (s.equals("dlf") || s.equals("dlc"))
				return true;
		}
		final String s = FileUtil.getExtensionInLowerCase(f);
		return s != null && (s.equals(ext));
	}

	/**
	 * @return a short description of accepted files
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * @return the file filter extension
	 */
	public String getExtension() {
		return "." + ext;
	}
}
