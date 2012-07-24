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
 * @(#)       Sentences.java
 * 
 * 
 * Alignment at "sentence" level
 * 
 * @version   
 * @author    NGUYEN Thi Minh Huyen
 * Copyright  1999
 *
 */
package fr.loria.xsilfide.multialign;

import java.util.ArrayList;
import java.util.Vector;

@SuppressWarnings("unchecked")
class Sentences {
	private Dist srcLengths, tarLengths; // lengths of sentences of two texts
	public static final Vector<Object> Linking = new Vector<Object>(); // links of sentences
	// within a text
	public static final Vector<Object> Links = new Vector<Object>(); // links of the two texts
	private static int nbLink = 0;
	private static int nbPaquet = 0;
	private static final Cognates xalignResults = new Cognates();

	// B.G. the results of the alignment
	// process are of the exact same type
	// as the cognates.
	public Sentences(Vector<Object> srcPara, Vector<Object> tarPara, Cognates cogn,
			LoadAndPrepareTexts lpt) {
		getLengths(srcPara, tarPara); // get lengths of sentences in the src &
										// tar
		// System.out.println(srcPara.toString());
		// System.out.println(tarPara.toString());
		xalignResults.setUriSource(cogn.getUriSource());
		xalignResults.setUriTarget(cogn.getUriTarget());
		final ContraintesChemin cc = cogn.cognates2Chemins(srcPara, tarPara);
		final Path path = Align.getPath(srcLengths, tarLengths, false, cc);
		// System.out.println(path.toString());
		createLinks(srcPara, tarPara, path);
		createAlignementResults(srcPara, tarPara, cogn.getUriSource(),
				cogn.getUriTarget(), lpt, path);
	}

	void getLengths(Vector<Object> srcSentences, Vector<Object> tarSentences) {
		final int ns = srcSentences.size();
		final int nt = tarSentences.size();
		srcLengths = new Dist(ns);
		tarLengths = new Dist(nt);
		// System.out.println(ns);
		// System.out.println(nt);
		for (int i = 0; i < ns; i++) {
			srcLengths
					.setDistAt(i,
							(Integer) ((Vector<Object>) srcSentences.elementAt(i))
									.elementAt(1));
		}
		for (int i = 0; i < nt; i++) {
			tarLengths
					.setDistAt(i,
							(Integer) ((Vector<Object>) tarSentences.elementAt(i))
									.elementAt(2));
		}
	}

	// computation of the results in the new format
	void createAlignementResults(Vector<Object> srcId, Vector<Object> tarId, String uriSrc,
			String uriTar, LoadAndPrepareTexts lpt, Path path) {
		Point pt;
		int whereInSrc, whereInTar;
		ArrayList<XmlId> contentOfpaquet;
		XmlId currentXmlIdSrc = null;
		XmlId currentXmlIdTar = null;
		String currentIdSrc, currentIdTar;
		// System.out.println("\ncreateAlignementResults :");
		// System.out.println("srcId = "+srcId);
		// System.out.println("tarId = "+tarId);
		whereInSrc = 0;
		if (whereInSrc < srcId.size()) {
			currentIdSrc = (String) ((Vector<Object>) srcId.elementAt(whereInSrc))
					.elementAt(0);
			currentXmlIdSrc = new XmlId(uriSrc, currentIdSrc, lpt);
			whereInSrc++;
		}
		whereInTar = 0;
		if (whereInTar < tarId.size()) {
			currentIdTar = (String) ((Vector<Object>) tarId.elementAt(whereInTar))
					.elementAt(1);
			currentXmlIdTar = new XmlId(uriTar, currentIdTar, lpt);
			whereInTar++;
		}
		for (int i = 0; i < path.getNumberOfPoint(); i++) {
			pt = path.getPointAt(i);
			// System.out.println("\nPointCourant : "+pt);
			// System.out.println("currentXmlIdSrc : "+currentXmlIdSrc);
			// System.out.println("currentXmlIdTar : "+currentXmlIdTar);
			if (pt.x > 1) { // paquet in source
				nbPaquet++;
				contentOfpaquet = new ArrayList<XmlId>();
				contentOfpaquet.add(currentXmlIdSrc);
				for (int x = 1; x < pt.x; x++) {
					currentIdSrc = (String) ((Vector<Object>) srcId
							.elementAt(whereInSrc)).elementAt(0);
					currentXmlIdSrc = new XmlId(uriSrc, currentIdSrc, lpt);
					contentOfpaquet.add(currentXmlIdSrc);
					whereInSrc++;
				}
				// we add the paquet in xalignResults
				currentXmlIdSrc = new XmlId("", "l" + nbPaquet, lpt);
				xalignResults.addPaquetSrc(currentXmlIdSrc, contentOfpaquet);
			}
			if (pt.y > 1) { // paquet in target
				nbPaquet++;
				contentOfpaquet = new ArrayList<XmlId>();
				contentOfpaquet.add(currentXmlIdTar);
				for (int y = 1; y < pt.y; y++) {
					currentIdTar = (String) ((Vector<Object>) tarId
							.elementAt(whereInTar)).elementAt(1);
					currentXmlIdTar = new XmlId(uriTar, currentIdTar, lpt);
					contentOfpaquet.add(currentXmlIdTar);
					whereInTar++;
				}
				// we add the paquet in xalignResults
				currentXmlIdTar = new XmlId("", "l" + nbPaquet, lpt);
				xalignResults.addPaquetTarget(currentXmlIdTar, contentOfpaquet);
			}
			// now, the question is :
			// do we face a noCorrespSrc, a noCorrespTar or an
			// alignment ?
			if (pt.x == 0) { // noCorrespTar
				xalignResults.addNoCorrespTarget(currentXmlIdTar);
			}
			if (pt.y == 0) { // noCorrespSrc
				xalignResults.addNoCorrespSource(currentXmlIdSrc);
			}
			if ((pt.x != 0) && (pt.y != 0)) {
				xalignResults.addAlignment(currentXmlIdSrc, currentXmlIdTar);
			}
			if ((pt.x != 0) && (whereInSrc < srcId.size())) {
				// we read in src
				currentIdSrc = (String) ((Vector<Object>) srcId.elementAt(whereInSrc))
						.elementAt(0);
				currentXmlIdSrc = new XmlId(uriSrc, currentIdSrc, lpt);
				whereInSrc++;
			}
			if ((pt.y != 0) && (whereInTar < tarId.size())) {
				// we read in target
				currentIdTar = (String) ((Vector<Object>) tarId.elementAt(whereInTar))
						.elementAt(1);
				currentXmlIdTar = new XmlId(uriTar, currentIdTar, lpt);
				whereInTar++;
			}
			// System.out.println("Résultat Courant :"+xalignResults);
		}
	}

	void createLinks(Vector<Object> srcId, Vector<Object> Xptr, Path path) {
		// nbLink: Number of links within a text
		int srcCpt = 0, tarCpt = 0; // counter for vectors srcId & tarId
		for (int i = 0; i < path.getNumberOfPoint(); i++) {
			final Point pt = path.getPointAt(i);
			final Vector<Object> snewLink = new Vector<Object>(), tnewLink = new Vector<Object>();
			if (pt.x > 1) {
				nbLink++;
				final String linkName = "l" + nbLink;
				snewLink.addElement(linkName);
				// IDVAL: assumes no spaces in id.
				String linkIds = ((Vector<Object>) srcId.elementAt(srcCpt))
						.elementAt(0).toString();
				for (int j = 2; j <= pt.x; j++)
					linkIds = linkIds
							+ " "
							+ ((Vector<Object>) srcId.elementAt(srcCpt + j - 1))
									.elementAt(0).toString();
				snewLink.addElement(linkIds);
				Linking.addElement(snewLink);
			}
			if (pt.y > 1) {
				nbLink++;
				final String linkName = "l" + nbLink;
				tnewLink.addElement(linkName);
				String linkIds = ((Vector<Object>) Xptr.elementAt(tarCpt)).elementAt(0)
						.toString();
				// IDVAL: assumes no space in id
				for (int j = 2; j <= pt.y; j++)
					linkIds = linkIds
							+ " "
							+ ((Vector<Object>) Xptr.elementAt(tarCpt + j - 1))
									.elementAt(0).toString();
				tnewLink.addElement(linkIds);
				Linking.addElement(tnewLink);
			}
			final Vector<Object> link = new Vector<Object>();
			if (pt.x == 0)
				link.addElement("");
			else if (pt.x == 1)
				link.addElement(((Vector<Object>) srcId.elementAt(srcCpt)).elementAt(0)
						.toString());
			else
				link.addElement(snewLink.elementAt(0).toString());
			if (pt.y == 0)
				link.addElement("");
			else if (pt.y == 1)
				link.addElement(((Vector<Object>) Xptr.elementAt(tarCpt)).elementAt(0)
						.toString());
			else
				link.addElement(tnewLink.elementAt(0).toString());
			Links.addElement(link);
			srcCpt += pt.x;
			tarCpt += pt.y;
		}
	}

	public static Cognates getXalignResult() {
		return xalignResults;
	}
}
