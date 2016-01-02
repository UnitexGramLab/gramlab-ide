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
package fr.umlv.unitex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Scanner;

import fr.umlv.unitex.config.ConfigManager;

/**
 * This class contains Unitex/GramLab version information
 * 
 * @author Anonymous Builder
 *
 * DO NOT CHANGE THIS FILE DIRECTLY! IT WILL BE OVERWRITTEN.
 * BUT INSTEAD USE VERSION.H.IN
 * 
 */
public final class Version {

	public static final String UNITEX_VERSION_AUTHOR                    = "The Unitex/GramLab Authors";
	public static final String UNITEX_VERSION_COMPANY                   = "Universite Paris-Est Marne-la-Vallee";
	public static final String UNITEX_VERSION_COPYRIGHT                 = "Copyright (C) 2001-2016";
	public static final String UNITEX_VERSION_DESCRIPTION_SHORT         = "corpus processing suite";
	public static final String UNITEX_VERSION_DESCRIPTION               = "an open source, cross-platform, multilingual, lexicon- and grammar-based corpus processing suite";
	public static final String UNITEX_VERSION_LICENSE                   = "LGPL-2.1";

	public static final String UNITEX_VERSION_URL_HOMEPAGE              = "http://unitexgramlab.org";
	public static final String UNITEX_VERSION_URL_ISSUES                = "http://unitexgramlab.org/index.php?page=6";
	public static final String UNITEX_VERSION_URL_REPOSITORY            = "https://svnigm.univ-mlv.fr/svn/unitex/Unitex-C%2B%2B";

	public static final int UNITEX_VERSION_MAJOR_NUMBER                 = 3;
	public static final int UNITEX_VERSION_MINOR_NUMBER                 = 1;
	public static final int UNITEX_VERSION_REVISION_NUMBER              = 0;
	public static final String UNITEX_VERSION_SUFFIX                    = "beta";
	public static final String UNITEX_VERSION_TYPE                      = "unstable";
	public static final int UNITEX_VERSION_IS_UNSTABLE                  = 1;

	public static final int UNITEX_VERSION_BUILD_IS_ANONYMOUS           = 1;
	public static final int UNITEX_VERSION_BUILD_NUMBER                 = 0;
	public static final String UNITEX_VERSION_BUILD_DATE                = "?";
	public static final String UNITEX_VERSION_BUILD_DAY                 = "?";
	public static final String UNITEX_VERSION_BUILD_MONTH               = "?";
	public static final String UNITEX_VERSION_BUILD_YEAR                = "?";
	public static final String UNITEX_VERSION_BUILD_TIMESTAMP           = "?";
	public static final String UNITEX_VERSION_BUILD_SYSTEM              = "Anonymous Builder";

	public static final String UNITEX_VERSION_COMMIT_BRANCH             = "?";
	public static final String UNITEX_VERSION_COMMIT_DATE               = "?";
	public static final String UNITEX_VERSION_COMMIT_HASH               = "?";
	public static final String UNITEX_VERSION_COMMIT_TAG                = "?";

	public static final int UNITEX_VERSION_CORE_REVISION_NUMBER         = 0;
	public static final int UNITEX_VERSION_CLASSIC_IDE_REVISION_NUMBER  = 0;
	public static final int UNITEX_VERSION_GRAMLAB_IDE_REVISION_NUMBER  = 0;
		
	/**
	 * The string that contains the version number, and the date of the release.
	 */
	public static final String version = "Unitex/GramLab "                                 +
	                                      Integer.toString(UNITEX_VERSION_MAJOR_NUMBER)    +
	                                      "."                                              +
	                                      Integer.toString(UNITEX_VERSION_MINOR_NUMBER)    +
	                                      UNITEX_VERSION_SUFFIX                            +
	                                      " Rev."                                          +
	                                      Integer.toString(UNITEX_VERSION_REVISION_NUMBER) +
	                                      " "                                              +
	                                      getRevisionDate();

	public static String getRevisionDate() {
		return "(" + UNITEX_VERSION_BUILD_DATE + ")";
	}

	public static String getRevisionNumberForJava() {
		return Integer.toString(UNITEX_VERSION_CLASSIC_IDE_REVISION_NUMBER);
	}

	public static String getRevisionNumberForC() {
		return Integer.toString(UNITEX_VERSION_CORE_REVISION_NUMBER);
	}

	public static String getRevisionNumberForGramlab() {
		return Integer.toString(UNITEX_VERSION_GRAMLAB_IDE_REVISION_NUMBER);
	}

	/** Make constructor unavailable; class is for namespace only. */
	private Version() {
	}	
}
