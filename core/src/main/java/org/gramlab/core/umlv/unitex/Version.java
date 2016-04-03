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

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.net.URLClassLoader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.umlv.unitex.config.ConfigManager;

/**
 * This class contains Unitex/GramLab IDE version information
 *
 * Version information is loaded from the JAR's Manifest.mf
 * Source Manifest.mf file is available at:
 *
 * Unitex.jar:  resources/fr/umlv/unitex/Manifest.mf
 * GramLab.jar: src/main/resources/fr/gramlab/Manifest.mf
 *
 * @author martinec
 *
 */
public final class Version {
 /**
  * IDE Title
  * e.g. Unitex/GramLab IDE
  */
 public static String VERSION_TITLE           = "Unitex/GramLab IDE";

 /**
  * IDE Semantic Version
  * e.g. 3.1.4239-beta
  */
 public static String VERSION_SEMVER          = "0.0.0-anonymous";

 /**
  * IDE Built Date
  * e.g. January 04, 2016
  */
 public static String VERSION_BUILT_DATE      = "?";

 /**
  * IDE MAJOR number version
  * <code>MAJOR.MINOR.REVISION-SUFFIX</code>
  */
 public static int    VERSION_MAJOR_NUMBER    = 0;

 /**
  * IDE MINOR number version
  * <code>MAJOR.MINOR.REVISION-SUFFIX</code>
  */
 public static int    VERSION_MINOR_NUMBER    = 0;

  /**
  * IDE REVISION number version
  * <code>MAJOR.MINOR.REVISION-SUFFIX</code>
  */
 public static int    VERSION_REVISION_NUMBER = 0;

  /**
  * IDE SUFFIX string version
  * <code>MAJOR.MINOR.REVISION-SUFFIX</code>
  */
 public static String VERSION_SUFFIX          = "anonymous";

 public static final Attributes manifestAttributes;

/**
  * IDE formated string version
  * e.g. Unitex/GramLab IDE 3.1beta Rev. 4239
  */
  public static String getStringVersion() {
  return VERSION_TITLE                        + " "       +
    Integer.toString(VERSION_MAJOR_NUMBER)    + "."       +
    Integer.toString(VERSION_MINOR_NUMBER)    +
    VERSION_SUFFIX                            + "  Rev. " +
    Integer.toString(VERSION_REVISION_NUMBER);
  }


 /**
  * IDE formatted string version including the release date
  * e.g. Unitex/GramLab 3.1beta Rev. 4239 (January 04, 2016)
  */
 public static String getFullStringVersion() {
  return getStringVersion()  + " (" +
         VERSION_BUILT_DATE  + ")";
 }

 static {
  // this is adapted from @see http://stackoverflow.com/a/14424273/2042871
  Attributes mainManifestAttributes = null;

  String jarFileName = "";

  try{
    // try to determine the running jar filename
    jarFileName = URLDecoder.decode(System.getProperty("sun.java.command")
                      .substring(0, System.getProperty("sun.java.command")
                      .lastIndexOf(".jar")) + ".jar", "UTF-8");

    // alternative method to get the jar filename associated with this class
    // this is not necessary the same name of the running jar
    if(!(new File(jarFileName).isFile())) {
      jarFileName = URLDecoder.decode(Version.class.getProtectionDomain()
                              .getCodeSource()
                              .getLocation()
                              .toURI()
                              .getPath(), "UTF-8");
    }
  } catch (Exception e) {
  }

  if(new File(jarFileName).isFile()) {
    try {
      // open jar and read the embedded manifest
      JarFile jarFile = new JarFile(jarFileName);

      Manifest manifest = jarFile.getManifest();

      mainManifestAttributes = manifest.getMainAttributes();
    } catch (Exception e) {
      throw new RuntimeException("Loading MANIFEST failed!", e);
    }
  }

  if(mainManifestAttributes != null) {
    // try to parse major.minor.revision-suffix from Implementation-Version
    // this is based on @see http://stackoverflow.com/a/11501749/2042871
    Matcher m = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)-?([\\da-z]+)?")
      .matcher(mainManifestAttributes.getValue("Implementation-Version"));

    if (!m.matches()) {
      throw new IllegalArgumentException("Bad Implementation-Version format!");
    }

    VERSION_MAJOR_NUMBER    = Integer.parseInt(m.group(1));
    VERSION_MINOR_NUMBER    = Integer.parseInt(m.group(2));
    VERSION_REVISION_NUMBER = Integer.parseInt(m.group(3));
    VERSION_SUFFIX          = m.group(4) == null ? "" : m.group(4);

    VERSION_TITLE           = mainManifestAttributes.getValue("Implementation-Title");
    VERSION_SEMVER          = mainManifestAttributes.getValue("Implementation-Version");
    VERSION_BUILT_DATE      = mainManifestAttributes.getValue("Built-Date");
  }

  manifestAttributes = mainManifestAttributes;
 }

 /** Make constructor unavailable; class is for namespace only. */
 private Version() {
 }
}
