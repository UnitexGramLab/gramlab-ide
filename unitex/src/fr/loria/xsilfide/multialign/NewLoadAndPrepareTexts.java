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
 * @(#)       NewLoadAndPrepareTexts.java
 * 
 * Created    06 december 2006
 * 
 * Copyright  2006 (C) Bertrand.Gaiffe@atilf.fr
 *            
 *            
 */
/* Modif 08/02/2007 we read the properties */
package fr.loria.xsilfide.multialign;

import java.util.Properties;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import fr.loria.nguyen.mytools.FileIO;
import fr.loria.nguyen.mytools.XMLTools;

class NewLoadAndPrepareTexts {
	private String sourceName;
	private String targetName;
	private String idSource;
	private String idTarget;
	Vector<Object> filesPlusId;
	private final LoadAndPrepareTexts lpt;
	private Cognates cogn;
	private final Properties prop = new Properties();

	@SuppressWarnings("null")
	public NewLoadAndPrepareTexts(String fileName) {
		XMLReader parser = null;
		try {
			parser = XMLReaderFactory.createXMLReader(XMLTools.parserClass);
		} catch (final SAXException e) {
			System.err.println("lAPT: " + e);
			e.printStackTrace();
			System.exit(-1);
		}
		// we put some default properties in prop so that
		// thing run smoothly if nothin is precised.
		// these default props are overwriten by
		// those in the alignment file
		prop.setProperty("l", "PHRASE");
		prop.setProperty("PHRASE", "PHRASE");
		prop.setProperty("seg", "PHRASE");
		prop.setProperty("s", "PHRASE");
		prop.setProperty("w", "LEXUNIT");
		prop.setProperty("head", "SEQ");
		prop.setProperty("date", "IGNORE");
		prop.setProperty("author", "IGNORE");
		prop.setProperty("language", "IGNORE");
		prop.setProperty("title", "IGNORE");
		prop.setProperty("lg", "PARAG");
		prop.setProperty("div", "DIV");
		prop.setProperty("DIV", "DIV");
		prop.setProperty("p", "PARAG");
		prop.setProperty("PARAG", "PARAG");
		prop.setProperty("q", "SEQ");
		prop.setProperty("body", "BODY");
		prop.setProperty("BODY", "BODY");
		prop.setProperty("text", "TRANSP");
		prop.setProperty("tei.2", "TRANSP");
		prop.setProperty("teiheader", "IGNORE");
		prop.setProperty("teiHeader", "IGNORE");
		prop.setProperty("filedesc", "IGNORE");
		prop.setProperty("titlestmt", "IGNORE");
		prop.setProperty("publicationstmt", "IGNORE");
		prop.setProperty("publisher", "IGNORE");
		prop.setProperty("sourcedesc", "IGNORE");
		prop.setProperty("profiledesc", "IGNORE");
		prop.setProperty("langusage", "IGNORE");
		prop.setProperty("hi", "SEQ");
		prop.setProperty("emph", "SEQ");
		prop.setProperty("fileDesc", "TRANSP");
		prop.setProperty("titleStmt", "TRANSP");
		prop.setProperty("langUsage", "IGNORE");
		prop.setProperty("textclass", "IGNORE");
		prop.setProperty("item", "SEQ");
		prop.setProperty("keywords", "IGNORE");
		prop.setProperty("lb", "IGNORE");
		prop.setProperty("list", "TRANSP");
		prop.setProperty("back", "TRANSP");
		prop.setProperty("milestone", "IGNORE");
		prop.setProperty("pb", "IGNORE");
		prop.setProperty("note", "IGNORE");
		// The source document handler for SAX/XML
		final MyHandler handler = new MyHandler();
		parser.setEntityResolver(handler);
		parser.setDTDHandler(handler);
		parser.setErrorHandler(handler);
		parser.setContentHandler(handler);
		// Reading the source XML text
		System.out.print("Parsing '" + fileName + "'...");
		try {
			final InputSource is = new InputSource(
					FileIO.openLargeInput(fileName));
			is.setEncoding("UTF-8");
			is.setSystemId(fileName);
			parser.parse(is);
		} catch (final Exception e) {
			System.err.println("source: " + e);
			e.printStackTrace();
		}
		System.out.println("Alignement file read\n");
		System.out.println("Source file: " + sourceName);
		System.out.println("Target file :" + targetName);
		/*
		 * Vector tmpSrc = segmentOnChar((String)filesPlusId.elementAt(0), '#');
		 * Vector tmpTar = segmentOnChar((String)filesPlusId.elementAt(1), '#');
		 * sourceName = (String)tmpSrc.elementAt(0); targetName =
		 * (String)tmpTar.elementAt(0);
		 * 
		 * 
		 * if (tmpSrc.size() > 1){ idSource = (String)tmpSrc.elementAt(1); }
		 * else{ idSource = ""; } if (tmpTar.size() > 1){ idTarget =
		 * (String)tmpTar.elementAt(1); } else{ idTarget = ""; }
		 */
		System.out.println("Cognates :");
		cogn.ecrire();
		/*
		 * lpt = new LoadAndPrepareTexts(sourceName, targetName,
		 * "/home/bertrand/Alignement/XAlignModif/Properties/multialign.properties"
		 * ,
		 * "/home/bertrand/Alignement/XAlignModif/Properties/multialign.properties"
		 * , idSource, idTarget);
		 */
		lpt = new LoadAndPrepareTexts(sourceName, targetName,
		// System.getProperty("user.dir")+"/Properties/multialign.properties",
		// System.getProperty("user.dir")+"/Properties/multialign.properties",
				idSource, idTarget, prop);
		System.out.println("Properties : " + lpt.prop);
		// Le problème maintenant, c'est de saturer les cognates
		// ex : si deux phrases sont alignées, tout ce dans quoi
		// elles sont contenues sont fuzzyAlignés.
		// Rq annexe, il faudra probablement changer les structures
		// de données dans Cognates pour des raisons d'efficacité.
		// On veut voir à quoi ressemblent les textes préparés :
		lpt.savePreparedTexts("preparedTexts.txt");
		// Manifestement, les id sont calculés automatiquement et sont
		// de la forme : d1p10s42.... Leurs noms reflètent donc la structure
		// d'inclusion....
		cogn.saturerCognates(lpt);
		System.out.println("Cognates a saturation :");
		cogn.ecrire();
	}

	public LoadAndPrepareTexts getPreparedTexts() {
		return lpt;
	}

	public String getSourceName() {
		return sourceName;
	}

	public String getTargetName() {
		return targetName;
	}

	public Cognates getCognates() {
		return cogn;
	}

	Vector<Object> segmentOnChar(String s, char cs) {
		final Vector<Object> result = new Vector<Object>();
		int finMot;
		int debutMot;
		char c;
		debutMot = 0;
		finMot = 0;
		while (finMot < s.length()) {
			c = s.charAt(finMot);
			if (c == cs) {
				result.addElement(s.substring(debutMot, finMot));
				while (c == cs) {
					c = s.charAt(finMot);
					finMot++;
				}
				debutMot = finMot - 1;
			}
			finMot++;
		}
		result.addElement(s.substring(debutMot, finMot));
		return result;
	}

	class MyHandler extends DefaultHandler {
		String typeLink;
		String lesFichiers;
		boolean inCognates;
		String typ;
		boolean inElementSpec;
		String curElem;
		String key, thePropVal;
		boolean inRef, inNote, inNoCorresp, inAlign, inPaquets, inFuzzy;
		String curFileName;
		String fileNameStatus;

		public MyHandler() {
			inRef = false;
			inNote = false;
			inCognates = false;
			inNoCorresp = false;
			inAlign = false;
			inPaquets = false;
			inFuzzy = false;
		}

		@Override
		public void endDocument() {
			/* nothing to do */
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public void startElement(String uri, String name, String qname,
				Attributes attrs) {
			if (name.equals("ref")) {
				inRef = true;
			}
			if ((name.equals("ptr")) && inRef) {
				curFileName = attrs.getValue("target");
			}
			if ((name.equals("note") && inRef)) {
				inNote = true;
			}
			if (name.equals("div") && (attrs != null)
					&& (attrs.getValue("type") != null)
					&& (attrs.getValue("type").equals("manualAlignment"))) {
				inCognates = true;
			}
			if (name.equals("linkGrp") && inCognates) {
				// 4 possible values for the type :
				// segmentGroup,
				// alignment
				// noCorresp
				// fuzzyAlignment
				final String theType = attrs.getValue("type");
				if (theType.equals("segmentGroup")) {
					inPaquets = true;
				} else if (theType.equals("alignment")) {
					inAlign = true;
				} else if (theType.equals("noCorresp")) {
					inNoCorresp = true;
				} else if (theType.equals("fuzzyAlignement")) {
					inFuzzy = true;
				} else {
					System.err.println("Unknown linkGrp type : " + theType);
				}
			}
			if (name.equals("link")) {
				if (inPaquets) {
					System.out.println("targets = " + attrs.getValue("targets")
							+ "\n");
					System.out.println("id = " + attrs.getValue("xml:id")
							+ "\n");
					System.out.println("cognates = " + cogn + "\n");
					cogn.addPaquet(attrs.getValue("targets"),
							attrs.getValue("xml:id"), lpt);
				}
				if (inAlign) {
					cogn.addAlignment(attrs.getValue("targets"), lpt);
				}
				if (inNoCorresp) {
					cogn.addNoCorresp(attrs.getValue("targets"), lpt);
				}
			}
			/*
			 * if (name.equals("link") && (attrs != null) &&
			 * (attrs.getValue("type") != null) &&
			 * attrs.getValue("type").equals("alignmentDomain")){ typeLink =
			 * attrs.getValue("type"); if (typeLink.equals("alignmentDomain")){
			 * lesFichiers = attrs.getValue("targets").trim(); // lesFichiers
			 * est de la forme : // uri_source#id1 uri_target#id2 // ce qu'on
			 * veut, c'est récupérer uri_source // et uri_target...
			 * System.out.println("Les fichiers : "+lesFichiers); filesPlusId =
			 * segmentOnWhiteSpaces(lesFichiers);
			 * System.out.println("Les fichiers : \n"); for (Enumeration e =
			 * filesPlusId.elements(); e.hasMoreElements();){
			 * System.out.println((String)e.nextElement()); } Vector tmpSrc =
			 * segmentOnChar((String)filesPlusId.elementAt(0), '#'); Vector
			 * tmpTar = segmentOnChar((String)filesPlusId.elementAt(1), '#');
			 * sourceName = (String)tmpSrc.elementAt(0); targetName =
			 * (String)tmpTar.elementAt(0); cogn = new Cognates(sourceName,
			 * targetName); } } else if (name.equals("linkGrp") &&
			 * attrs.getValue("type").equals("manualAlignment")){ inCognates =
			 * true; } else if (name.equals("link") && inCognates){ // c'est là
			 * qu'on fait la récolte des contraintes... // 4 types de choses :
			 * // des link type=linking = fabrication de paquets // des link
			 * type=alignment = mise en correspondance // des link
			 * type=noCorresp = absent dans l'autre fichier. // des link
			 * type="fuzzyAlignment" // une difficulté à laquelle je n'avais pas
			 * pensé, on peut imposer des trucs // du genre alignement de 4
			 * phrases sur une.... typ = attrs.getValue("type"); if
			 * (typ.equals("linking")){
			 * System.out.println("targets = "+attrs.getValue("targets")+"\n");
			 * System.out.println("id = "+attrs.getValue("xml:id")+"\n");
			 * System.out.println("cognates = "+cogn+"\n");
			 * cogn.addPaquet(attrs.getValue("targets"),
			 * attrs.getValue("xml:id"), lpt); } else if
			 * (typ.equals("alignment")){
			 * cogn.addAlignment(attrs.getValue("targets"), lpt); } else if
			 * (typ.equals("noCorresp")){
			 * cogn.addNoCorresp(attrs.getValue("targets"), lpt); } else if
			 * (typ.equals("fuzzyAlignment")){
			 * cogn.addFuzzyAlign(attrs.getValue("targets"), lpt); } else{
			 * System.err.println("Cognates : type inconnu : "+typ+"\n");
			 * System.exit(1); } }
			 */
			if (name.equals("elementSpec")) {
				inElementSpec = true;
				curElem = attrs.getValue("ident");
			} else if (name.equals("memberOf")) {
				key = attrs.getValue("key");
				// the key is of the form :
				// Xalign.EMPTY or Xalign.PHRASE or ...
				thePropVal = key.substring(key.lastIndexOf('.') + 1,
						key.length());
				prop.setProperty(curElem, thePropVal);
				System.out.println(curElem + " = " + thePropVal);
			}
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public void endElement(String uri, String name, String qName) {
			if (name.equals("div") && inCognates) {
				inCognates = false;
			}
			/*
			 * if (name.equals("linkGrp")){ // on ne peut pas avoir de linkGrp
			 * dans un linkGrp inCognates = false; }
			 */
			if (name.equals("linkGrp")) {
				inNoCorresp = false;
				inAlign = false;
				inPaquets = false;
				inFuzzy = false;
			}
			if (name.equals("ref")) {
				inRef = false;
				/* we now have fileNameStatus (source or translation) */
				/* we also have curFileName */
				if (fileNameStatus.equals("source")) {
					sourceName = curFileName;
					final Vector<Object> tmpSrc = segmentOnChar(curFileName, '#');
					sourceName = (String) tmpSrc.elementAt(0);
					if (tmpSrc.size() > 1) {
						idSource = (String) tmpSrc.elementAt(1);
					} else
						idSource = "";
				} else {
					final Vector<Object> tmpTar = segmentOnChar(curFileName, '#');
					targetName = (String) tmpTar.elementAt(0);
					if (tmpTar.size() > 1) {
						idTarget = (String) tmpTar.elementAt(1);
					} else
						idTarget = "";
				}
			}
			if (name.equals("sourceDesc")) {
				cogn = new Cognates(sourceName, targetName);
			}
			if (name.equals("note")) {
				inNote = false;
			}
		}

		@Override
		public void characters(char[] content, int start, int length) {
			if (inNote) {
				fileNameStatus = new String(content, start, length);
			}
		}
	}
}
