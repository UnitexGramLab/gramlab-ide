/*
 * Unitex
 *
 * Copyright (C) 2001-2007 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.io.*;
import java.util.regex.*;

/**
 * This class provides methods to get information about files, like path, file
 * name or extension.
 * 
 * @author Sébastien Paumier
 *  
 */
public class Util {

	/**
	 * Returns the name without extension of a file.
	 * 
	 * @param s
	 *            the file name
	 * @return the name without extension
	 */
	public static String getFileNameWithoutExtension(String s) {
		int n = s.lastIndexOf('.');
		if ((n == -1) || (n < s.lastIndexOf(File.separatorChar)))
			return s;
		return s.substring(0, n);
	}
	
	/**
	 * Returns the name without extension of a file.
	 * 
	 * @param f
	 *            the file name
	 * @return the name without extension
	 */
	public static String getFileNameWithoutExtension(File f) {
		return getFileNameWithoutExtension(f.getAbsolutePath());
	}
	/**
	 * Returns the extension of a file.
	 * 
	 * @param s
	 *            the file name
	 * @return the extension if there is one, the empty string otherwise
	 */
	public static String getFileNameExtension(String s) {
		int n = s.lastIndexOf('.');
		if ((n == -1) || (n < s.lastIndexOf(File.separatorChar)))
			return "";
		return s.substring(n + 1);
	}

	/**
	 * Returns the extension of a file. This method is equivalent to a call to
	 * <code>Util.getFileNameExtension(f.getName())</code>.
	 * 
	 * @param f
	 *            the file
	 * @return the extension if there is one, the empty string otherwise
	 */
	public static String getFileNameExtension(File f) {
		return getFileNameExtension(f.getName());
	}

	/**
	 * Returns the path name of a file, without the file name.
	 * 
	 * @param s
	 *            the file name
	 * @return the path
	 */
	public static String getFilePathWithoutFileName(String s) {
		int n = s.lastIndexOf(File.separatorChar);
		if (n == -1)
			return "";
		return s.substring(0, n + 1);
	}

	/**
	 * Returns the path name of a file, without the file name. This method is
	 * equivalent to a call to
	 * <code>getFilePathWithoutFileName(f.getName())</code>.
	 * 
	 * @param f
	 *            the file
	 * @return the path
	 */
	public static String getFilePathWithoutFileName(File f) {
		return getFilePathWithoutFileName(f.getAbsolutePath());
	}

	/**
	 * Returns the file name of a file, without the path.
	 * 
	 * @param f
	 *            the file
	 * @return the file name, without the path
	 */
	public static String getFileNameWithoutFilePath(File f) {
		return f.getName();
	}

	/**
	 * Returns the file name of a file, without the path. This method is
	 * equivalent to a call to
	 * <code>getFileNameWithoutFilePath(new File(s))</code>.
	 * 
	 * @param s
	 *            the file name
	 * @return the file name, without the path
	 */
	public static String getFileNameWithoutFilePath(String s) {
		return getFileNameWithoutFilePath(new File(s));
	}

	/**
	 * Returns the extension in lower case of a file
	 * 
	 * @param s
	 *            file name
	 * @return the extension
	 */
	public static String getExtensionInLowerCase(String s) {
		return getExtensionInLowerCase(new File(s));
	}

	/**
	 * Returns the extension in lower case of a file
	 * 
	 * @param f
	 *            the file
	 * @return the extension
	 */
	public static String getExtensionInLowerCase(File f) {
		String ext = "";
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public static int toInt(String s) {
		int j = 0;
		for (int i = 0; i < s.length(); i++)
			j = j * 10 + s.charAt(i) - '0';
		return j;
	}

	/**
   * Takes a file and looks for the text between the tags <code>&lt;title&gt;</code>
   * and <code>&lt;/title&gt;</code>. 
   * 
   * WARNING: the result string is taken rawly and
   * might be wrongly encoded if the title is not in ASCII.
	 * @param file
	 *            the HTML file
	 * @return the title of the page; <code>null</code> if the pattern is not found in the 
   *         first 200 bytes of the file
	 */
	public static String getHtmlPageTitle(File file) {
		Pattern p = Pattern.compile("<title>(.*)</title>");
		FileInputStream input;
		byte[] bytes = new byte[200];
		try {
			input = new FileInputStream(file);
			input.read(bytes, 0, 200);
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
      return null;
		} catch (IOException e) {
			e.printStackTrace();
      return null;
		}
		String charBuffer = new String(bytes);
		Matcher matcher = p.matcher(charBuffer);
		if (matcher.find()) {
			return charBuffer.subSequence(matcher.start(1), matcher.end(1))
					.toString();
		}
		return null;
	}

}