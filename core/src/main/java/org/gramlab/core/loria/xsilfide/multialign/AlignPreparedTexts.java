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
/* 
 * @(#)       AlignPreparedTexts.java
 * 
 * Created    Mon Sep 20 17:59:20 1999
 * 
 * Copyright  1999 (C) PATRICE BONHOMME
 *            UMR LORIA (Universities of Nancy, CNRS & INRIA)
 *            
 */
package org.gramlab.core.loria.xsilfide.multialign;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Vector;

/**
 * Align 2 texts that have been prepared and save the result of the alignment
 * into a linkGrp file.
 * 
 * @author Patrice Bonhomme
 */
/* we should really build a LoadAndPrepareTexts in such a case */
class AlignPreparedTexts {
	/**
	 * @param outfile
	 *            The prepared data file (the input file in fact).
	 * @param lgfile
	 *            The linkGrp file (the output file).
	 * @see LoadAndPrepareTexts
	 */
	@SuppressWarnings("unchecked")
	public AlignPreparedTexts(String sfile, String tfile, // file names to put
															// in LinkGrp file
			String slang, String tlang, // source & target languages to put in
										// LinkGrp file
			String outfile, String lgfile) {
		final Properties properties = new Properties();
		try {
			final FileInputStream f = new FileInputStream(outfile);
			properties.load(f);
		} catch (final Exception e) {
			System.err.println(e);
			e.printStackTrace();
			System.exit(-1);
		}
		final String dS = properties.getProperty("divSrc");
		final String dT = properties.getProperty("divTar");
		final String pS = properties.getProperty("paraSrc");
		final String pT = properties.getProperty("paraTar");
		final String sS = properties.getProperty("stcSrc");
		final String sT = properties.getProperty("stcTar");
		final Vector<Object> dSrc = new Vector<Object>();
		int count = 0;
		while (count < dS.length() - 1) {
			final int d = dS.indexOf("]]", count);
			final String divBuf = dS.substring(count + 1, d + 2);
			dSrc.addElement(getVector(divBuf.trim()));
			count = d + 2;
		}
		final Vector<Object> dTar = new Vector<Object>();
		count = 0;
		while (count < dT.length() - 1) {
			final int d = dT.indexOf("]]", count);
			final String divBuf = dT.substring(count + 1, d + 2);
			dTar.addElement(getVector(divBuf.trim()));
			count = d + 2;
		}
		for (int i = 0; i < dSrc.size(); i++) {
			final Vector<Object> buf = (Vector<Object>) dSrc.elementAt(i);
			for (int j = 0; j < buf.size(); j++) {
				final Vector<Object> buf1 = (Vector<Object>) buf.elementAt(j);
				final Integer lg = new Integer(buf1.elementAt(1).toString());
				buf1.setElementAt(lg, 1);
			}
		}
		for (int i = 0; i < dTar.size(); i++) {
			final Vector<Object> buf = (Vector<Object>) dTar.elementAt(i);
			for (int j = 0; j < buf.size(); j++) {
				final Vector<Object> buf1 = (Vector<Object>) buf.elementAt(j);
				final Integer lg = new Integer(buf1.elementAt(1).toString());
				buf1.setElementAt(lg, 1);
			}
		}
		final Vector<Object> pSrc = getVector(pS.trim());
		final Vector<Object> pTar = getVector(pT.trim());
		final Vector<Object> sSrc = getVector(sS.trim());
		final Vector<Object> sTar = getVector(sT.trim());
		String tmp = "";
		int divNbSrc = 0, divNbTar = 0;
		for (int i = 0; i < pSrc.size(); i++) {
			final Vector<String> buf = (Vector<String>) pSrc.elementAt(i);
			final Integer lg = new Integer(buf.elementAt(1).toString());
			buf.setElementAt(lg+"", 1);
			String s = buf.elementAt(0).toString();
			int idx = s.indexOf('p');
			if (idx < 0)
				idx = s.indexOf('l');
			s = s.substring(0, idx);
			if (!tmp.startsWith(s)) {
				divNbSrc++;
			}
			tmp = s;
		}
		tmp = "";
		for (int i = 0; i < pTar.size(); i++) {
			final Vector<String> buf = (Vector<String>) pTar.elementAt(i);
			final Integer lg = new Integer(buf.elementAt(1).toString());
			buf.setElementAt(lg+"", 1);
			String s = buf.elementAt(0).toString();
			int idx = s.indexOf('p');
			if (idx < 0)
				idx = s.indexOf('l');
			s = s.substring(0, idx);
			if (!tmp.startsWith(s)) {
				divNbTar++;
			}
			tmp = s;
		}
		MultiAlign.debug("Src: " + divNbSrc + " Tar: " + divNbTar);
		for (int i = 0; i < sSrc.size(); i++) {
			final Vector<String> buf = (Vector<String>) sSrc.elementAt(i);
			buf.setElementAt(""+new Integer(buf.elementAt(1).toString()), 1);
		}
		for (int i = 0; i < sTar.size(); i++) {
			final Vector<String> buf = (Vector<String>) sTar.elementAt(i);
			buf.setElementAt(""+new Integer(buf.elementAt(2).toString()), 2);
		}
		final LoadAndPrepareTexts lpt = new LoadAndPrepareTexts(dSrc, pSrc,
				sSrc, dTar, pTar, sTar, sfile, tfile);
		final Div divs = new Div(dSrc, dTar, pSrc, pTar, sSrc, sTar,
				new Cognates(sfile, tfile), lpt);
		dSrc.removeAllElements();
		dTar.removeAllElements();
		pSrc.removeAllElements();
		pTar.removeAllElements();
		sSrc.removeAllElements();
		new InsertLinkGrp(lgfile, slang, tlang, sfile, tfile, sTar,
				divs.getLinking(), divs.getLinks(), null, null, false);
	}

	private static Vector<Object> getVector(String s) {
		final Vector<Object> grd_buf = new Vector<Object>(); // big
																				// buffer
		final Vector<String> ptit_buf = new Vector<String>(); // small buffer
		final StringBuilder tmp = new StringBuilder();
		for (int i = 1; i < s.length(); i++) {
			final char c = s.charAt(i);
			if ((c == '[') && (i > 1)) {
				grd_buf.addElement(ptit_buf.clone());
				ptit_buf.removeAllElements();
				tmp.setLength(0);
			} else if ((c == ']') && (s.charAt(i - 1) == ']')) {
				grd_buf.addElement(ptit_buf.clone());
			} else if ((c != ',') && (c != '[') && (c != ']'))
				tmp.append(c);
			else {
				switch (c) {
				case ',':
					if (s.charAt(i - 1) != ']')
						ptit_buf.addElement(tmp.toString().trim());
					break;
				case ']':
					ptit_buf.addElement(tmp.toString().trim());
					break;
				default:
					break;
				}
				tmp.setLength(0);
			}
		}
		return grd_buf;
	}
}
// EOF AlignPreparedTexts
