/*
 * Unitex
 *
 * Copyright (C) 2001-2012 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package fr.umlv.unitex.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import fr.umlv.unitex.console.ConsoleEntry;
import fr.umlv.unitex.console.Couple;
import fr.umlv.unitex.process.list.ProcessOutputList;

/**
 * This class is used to monitor stdout and stderr messages of external
 * processes.
 * 
 * @author Sébastien Paumier
 */
public class ProcessInfoThread extends Thread {
	final ProcessOutputList list;
	private BufferedReader stream;
	final ConsoleEntry entry;

	/**
	 * Creates a new <code>ProcessInfoThread</code>
	 * 
	 * @param list
	 *            the list to display messages
	 * @param s
	 *            the stream to monitor
	 * @param close
	 *            indicate if the parent frame must be closed after the
	 *            completion of the process
	 * @param f
	 *            parent frame
	 */
	public ProcessInfoThread(ProcessOutputList list, InputStream s, ConsoleEntry entry) {
		this.list = list;
		this.entry = entry;
		try {
			stream = new BufferedReader(new InputStreamReader(s, "UTF8"));
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private char nextChar = '\0';

	String myReadLine(BufferedReader reader) {
		int c;
		String result = "";
		if (nextChar != '\0') {
			result = "" + nextChar;
			nextChar = '\0';
		}
		try {
			while ((c = reader.read()) != -1) {
				char ch = (char) c;
				if (ch == '\r') {
					result = result + ch;
					if ((c = reader.read()) != -1) {
						ch = (char) c;
						if (ch == '\n') {
							/* If we have a \r\n sequence, we return it */
							result = result + ch;
							return result;
						}
						/* Otherwise, we stock the character */
						nextChar = ch;
					}
					return result;
				} else if (ch == '\n') {
					/* If we have a single \n, we return it */
					result = result + ch;
					nextChar = '\0';
					return result;
				} else {
					nextChar = '\0';
					result = result + ch;
				}
			}
		} catch (final IOException e) {
			return null;
		}
		if ("".equals(result))
			return null;
		return result;
	}

	/**
	 * Runs the monitoring thread
	 */
	@Override
	public void run() {
		String s;
		boolean fullReturn;
		while ((s = myReadLine(stream)) != null) {
			if (!s.equals("")) {
				if (s.endsWith("\r\n")) {
					s = s.substring(0, s.length() - 2);
					fullReturn = true;
				} else if (s.endsWith("\r")) {
					fullReturn = false;
					s = s.substring(0, s.length() - 1);
				} else if (s.endsWith("\n")) {
					fullReturn = true;
					s = s.substring(0, s.length() - 1);
				} else {
					fullReturn = true;
				}
				final String s2 = s;
				final boolean ret = fullReturn;
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							if (ret) {
								list.addLine(new Couple(s2, false));
							} else {
								list.replaceLastLine(new Couple(s2, false));
							}
							if (entry != null) {
								entry.addErrorMessage(s2);
							}
						}
					});
				} catch (InterruptedException e) {
					/* */
				} catch (InvocationTargetException e) {
					/* */
				}
			}
		}
		if (entry!=null) {
			entry.setErrorStreamEnded(true);
		}
	}
}
