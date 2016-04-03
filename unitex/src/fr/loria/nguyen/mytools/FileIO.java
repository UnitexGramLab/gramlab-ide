/*
 * XAlign
 *
 * Copyright (C) LORIA
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
package fr.loria.nguyen.mytools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.swing.ProgressMonitorInputStream;

/**
 * FileIO.java Methods for file I/O operations
 * 
 * @author Thi Minh Huyen Nguyen
 * @author LORIA, France
 * @version 1
 */
public class FileIO {
	public static Properties loadPROPS(String fileIn) {
		/**
		 * Load properties from a file
		 * 
		 * @param fileIn
		 *            Properties file name
		 * @return a Properties object
		 */
		final Properties p = new Properties();
		try {
			p.load(new FileInputStream(fileIn));
		} catch (final IOException ioe) {
			System.err.println("Can't read properties file " + fileIn + ": ");
			ioe.printStackTrace();
			return null;
		}
		return p;
	}

	public static BufferedReader openIN(String fileIn, String enc) {
		/**
		 * Open a file to read with a given encoding
		 * 
		 * @param fileIn
		 *            reading file name, enc character encoding (UTF8/ASCII)
		 * @return BufferedReader object
		 * @exception FileNotFoundException
		 *                , IOException
		 */
		BufferedReader in = null;
		try {
			if (enc.equals("UTF-8")) {
				in = new BufferedReader(new InputStreamReader(
						new FileInputStream(fileIn), "UTF-8"));
				/**
				 * For Windows users: Edited file using UTF8 format contains a
				 * redundant character at the begining, this character should be
				 * deleted
				 */
				System.err
						.println(fileIn
								+ ": delete first character of UTF-8 file (Y/N)? (Windows user)");
				final BufferedReader stdin = new BufferedReader(
						new InputStreamReader(System.in));
				if (stdin.readLine().equalsIgnoreCase("Y"))
					in.read();
			} else
				in = new BufferedReader(new FileReader(fileIn));
		} catch (final FileNotFoundException e) {
			System.err.println(fileIn + " does not exist!");
			return null;
		} catch (final IOException exc) {
			exc.printStackTrace();
		}
		return in;
	}

	public static PrintWriter openOUT(String fileOut, String enc) {
		/**
		 * Open a file to write with a given encoding
		 * 
		 * @param fileIn
		 *            writing file name, enc character encoding (UTF8/ASCII)
		 * @return PrintWriter object
		 * @exception IOException
		 */
		PrintWriter out = null;
		try {
			final File f = new File(fileOut);
			if (f.exists()) {
				System.out.println(fileOut
						+ " exists ... Do you want to overwrite it? (Y/N)");
				final BufferedReader stdin = new BufferedReader(
						new InputStreamReader(System.in));
				if (!(stdin.readLine().equalsIgnoreCase("Y")))
					return null;
			}
			if (enc.equals("UTF-8"))
				out = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(f), "UTF-8"));
			else
				out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
		} catch (final IOException exc) {
			exc.printStackTrace();
		}
		return out;
	}

	private static final int BUFSIZE = 8192;

	public static InputStream openLargeInput(String name)
			throws FileNotFoundException {
		/**
		 * Utility function to do expensive, long processing asynchronously
		 * 
		 * @param input
		 *            file name
		 * @return Reader object
		 * @exception MalformedURLException
		 *                , IOException
		 * @throws FileNotFoundException
		 */
		InputStream is;
		try {
			final URL u = new URL(name);
			is = new ProgressMonitorInputStream(null, "Reading" + " " + name,
					u.openStream());
		} catch (final MalformedURLException e) {
			is = new ProgressMonitorInputStream(null, "Reading" + " " + name,
					new FileInputStream(name));
		} catch (final IOException f) {
			is = new ProgressMonitorInputStream(null, "Reading" + " " + name,
					new FileInputStream(name));
		}
		final InputStream buf = new BufferedInputStream(is, BUFSIZE);
		return buf;
	}
}
