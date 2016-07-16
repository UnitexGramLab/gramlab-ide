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
 * @(#)       InsertLinkGrp.java
 * 
 * 
 * 
 * 
 * @version   
 * @author    NGUYEN Thi Minh Huyen
 * Copyright  1999
 *
 *
 */
package org.gramlab.core.loria.xsilfide.multialign;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.TextImpl;
import org.apache.xerces.parsers.DOMParser;
import org.gramlab.core.loria.nguyen.mytools.XMLTools;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class InsertLinkGrp {
	@SuppressWarnings("rawtypes")
	private final Hashtable idSrc;
	@SuppressWarnings("rawtypes")
	private final Hashtable idTar; // idSrc and idTar establish

	// the link between ids internal to xalign
	// such as d1p1s1 with the ids actually
	// appearing in the documents.
	// CreateXptr recoit qq chose de la forme :
	// [x1, d1p1s1, 47]
	// et ecrit dans le document :
	// <xptr from="ID (idDanstarget)" id="x1">
	// Le pb est que on a chang� la forme du r�sultat !
	public ElementImpl CreateXptr(DocumentImpl xdoc, Vector<?> xp) {
		final ElementImpl elt = new ElementImpl(xdoc, "xptr");
		final String xptr = xp.elementAt(0).toString();
		// setAttribute(new QName("from"),"ID ("+tarId+")");
		// setAttribute(new QName("id"),xptr);
		elt.setAttribute("from", "ID (" + idTar.get(xp.elementAt(1).toString())
				+ ")");
		elt.setAttribute("xml:id", xptr);
		return elt;
	}

	public ElementImpl CreateLinking(DocumentImpl xdoc, Vector<?> linking) {
		// Creation of an element "linking",
		// given the identity of linking and the string of
		// real identities in the document.
		final String ids = idTrans(linking.elementAt(1).toString());
		if (ids == null || ids.equals(""))
			return null;
		final ElementImpl elt = new ElementImpl(xdoc, "link");
		elt.setAttribute("xml:id", linking.elementAt(0).toString());
		elt.setAttribute("type", "linking");
		elt.setAttribute("targets", ids);
		return elt;
	}

	private String idTrans(String ids) {
		// don't translate xY's
		if (ids.indexOf('x') != -1)
			return ids;
		ids = ids.trim();
		String ret = "";
		int y = ids.indexOf(' ');
		if (y != -1) {
			int x = -1;
			for (; y != -1; y = ids.indexOf(' ', x + 1)) {
				final String s = (String) idSrc.get(ids.substring(x + 1, y));
				if (s != null)
					ret += s + " ";
				x = y;
			}
			final String s = (String) idSrc.get(ids.substring(x + 1));
			if (s != null)
				ret += s;
			return ret;
		}
		return (String) idSrc.get(ids);
	}

	// CreateConcord recoit qq chose de la forme :
	// [d1p1s1, x1]
	// nous, on ne veut pas de x1.
	// il faut donc passer par stcTar...
	public ElementImpl CreateConcord(DocumentImpl xdoc, Vector<?> concord) {
		// creation of an element "link" (concordance),
		// given the string of identities.
		String src = concord.elementAt(0).toString();
		if (src.length() == 0 || src.charAt(0) != 'l') // not a lX grouping ID.
			src = (String) idSrc.get(src);
		if (src == null || src.equals(""))
			return null;
		final ElementImpl elt = new ElementImpl(xdoc, "link");
		elt.setAttribute("targets", src + " " + concord.elementAt(1).toString());
		return elt;
	}

	// Bertrand.Gaiffe@atilf.fr december 7, 2006
	// added a boolean addingmode : false if we build a result
	// from "nothing" as it used to be with the old xalign
	// and true if we build a result into a pre-existing file
	// (new way of doing things in xalign)
	// idem in french : Bertrand.Gaiffe@atilf.fr 7 d�cembre 2006
	// Ajout d'un bool�en addingMode qui vaut faux si on
	// fabrique un r�sultat � partir de rien (ancien comportement)
	// et vrai si on ajoute dans un fichier pr�-existant.
	@SuppressWarnings({ "unused", "null" })
	public InsertLinkGrp(String fileName, String sLang, String tLang,
			String srcName, String tarName, Vector<?> stcTar,
			Vector<?> Linking, Vector<?> Links,
			@SuppressWarnings("rawtypes") Hashtable idSrc,
			@SuppressWarnings("rawtypes") Hashtable idTar, boolean addingMode) {
		this.idSrc = idSrc;
		this.idTar = idTar;
		DocumentImpl xdoc = null;
		Node whereToInsert = null;
		// On ne veut plus forc�ment partir d'un document vide...
		if (addingMode) {
			// il faut qu'on lise fileName. Qu'on regarde dedans s'il contient
			// un linkGrp de type alignmentXalign. Si oui, on remplace ce
			// linkGrp
			// si non, on en ajoute un.
			final DOMParser parser = new DOMParser();
			try {
				parser.parse(fileName);
				xdoc = (DocumentImpl) (parser.getDocument());
				// On cherche maintenant o� ins�rer...
				whereToInsert = xdoc.getDocumentElement();
				// il faut qu'on aille dans le body
				// je ne veux pas trop pr�juger de la forme du document...
				whereToInsert = findBody(whereToInsert);
				if (whereToInsert == null) {
					System.err.println("No body element found in " + fileName
							+ "\nBye.\n");
					System.exit(1);
				}
				removeXAlignResults(whereToInsert);
			} catch (final Exception e) {
				System.err.println("Problem while parsing " + fileName
						+ " for writing\n");
				System.exit(1);
			}
		} else { // Ancienne forme d'appel...
			xdoc = new DocumentImpl();
			// On ajoute quand m�me un peu de sauce autour pour en faire un
			// document TEI
			Element nouveauxNoeuds, teiNode, fileDescNode, sourceDescNode;
			// Got to change this once more !
			nouveauxNoeuds = new ElementImpl(xdoc, "TEI");
			teiNode = nouveauxNoeuds;
			xdoc.appendChild(nouveauxNoeuds);
			whereToInsert = nouveauxNoeuds;
			nouveauxNoeuds = new ElementImpl(xdoc, "teiHeader");
			whereToInsert.appendChild(nouveauxNoeuds);
			whereToInsert = nouveauxNoeuds;
			nouveauxNoeuds = new ElementImpl(xdoc, "fileDesc");
			fileDescNode = nouveauxNoeuds;
			whereToInsert.appendChild(nouveauxNoeuds);
			whereToInsert = nouveauxNoeuds;
			nouveauxNoeuds = new ElementImpl(xdoc, "titleStmt");
			whereToInsert.appendChild(nouveauxNoeuds);
			whereToInsert = nouveauxNoeuds;
			nouveauxNoeuds = new ElementImpl(xdoc, "title");
			whereToInsert.appendChild(nouveauxNoeuds);
			nouveauxNoeuds = new ElementImpl(xdoc, "publicationStmt");
			nouveauxNoeuds.appendChild(new ElementImpl(xdoc, "p"));
			fileDescNode.appendChild(nouveauxNoeuds);
			nouveauxNoeuds = new ElementImpl(xdoc, "sourceDesc");
			sourceDescNode = nouveauxNoeuds;
			fileDescNode.appendChild(nouveauxNoeuds);
			nouveauxNoeuds = new ElementImpl(xdoc, "bibl");
			sourceDescNode.appendChild(nouveauxNoeuds);
			whereToInsert = nouveauxNoeuds;
			nouveauxNoeuds = new ElementImpl(xdoc, "ref");
			whereToInsert.appendChild(nouveauxNoeuds);
			whereToInsert = nouveauxNoeuds;
			nouveauxNoeuds = new ElementImpl(xdoc, "ptr");
			nouveauxNoeuds.setAttribute("target", srcName);
			whereToInsert.appendChild(nouveauxNoeuds);
			nouveauxNoeuds = new ElementImpl(xdoc, "note");
			nouveauxNoeuds.setAttribute("type", "status");
			nouveauxNoeuds.appendChild(new TextImpl(xdoc, "source"));
			whereToInsert.appendChild(nouveauxNoeuds);
			nouveauxNoeuds = new ElementImpl(xdoc, "bibl");
			sourceDescNode.appendChild(nouveauxNoeuds);
			whereToInsert = nouveauxNoeuds;
			nouveauxNoeuds = new ElementImpl(xdoc, "ref");
			whereToInsert.appendChild(nouveauxNoeuds);
			whereToInsert = nouveauxNoeuds;
			nouveauxNoeuds = new ElementImpl(xdoc, "ptr");
			nouveauxNoeuds.setAttribute("target", tarName);
			whereToInsert.appendChild(nouveauxNoeuds);
			nouveauxNoeuds = new ElementImpl(xdoc, "note");
			nouveauxNoeuds.setAttribute("type", "status");
			nouveauxNoeuds.appendChild(new TextImpl(xdoc, "translation"));
			whereToInsert.appendChild(nouveauxNoeuds);
			whereToInsert = teiNode;
			nouveauxNoeuds = new ElementImpl(xdoc, "text");
			whereToInsert.appendChild(nouveauxNoeuds);
			whereToInsert = nouveauxNoeuds;
			nouveauxNoeuds = new ElementImpl(xdoc, "body");
			whereToInsert.appendChild(nouveauxNoeuds);
			whereToInsert = nouveauxNoeuds;
			/*
			 * nouveauxNoeuds = new ElementImpl(xdoc, "linkGrp");
			 * nouveauxNoeuds.setAttribute("type","alignmentCognates");
			 * whereToInsert.appendChild(nouveauxNoeuds); nnn = new
			 * ElementImpl(xdoc, "link"); nnn.setAttribute("targets",
			 * srcName+" "+tarName); nnn.setAttribute("type",
			 * "alignmentDomain"); nouveauxNoeuds.appendChild(nnn);
			 */
			// On ajoute aussi un linkGrp de type cognate pour d�signer
			// les fichiers mis en correspondance.
		}
		/*
		 * ElementImpl newLinkGrp = new ElementImpl(xdoc, "linkGrp");
		 * newLinkGrp.setAttribute("type", "essaiResultXALign");
		 */
		final ElementImpl newDiv = new ElementImpl(xdoc, "div");
		newDiv.setAttribute("type", "resultXAlign");
		ElementImpl newLink;
		Paquet paq;
		XmlId xiCour;
		Alignement aCour;
		String paqContent;
		String alignContent;
		ElementImpl newLinkGrp = new ElementImpl(xdoc, "linkGrp");
		newLinkGrp.setAttribute("type", "segmentGroup");
		newDiv.appendChild(newLinkGrp);
		// System.out.println("Result = "+Sentences.getXalignResult());
		// we now have to xmlize the result
		for (final Object o4 : Sentences.getXalignResult().getSourcePaquets()) {
			paq = (Paquet) o4;
			newLink = new ElementImpl(xdoc, "link");
			newLink.setAttribute("type", "linking");
			newLink.setAttribute("xml:id", paq.getId());
			paqContent = "";
			for (final Object o : paq.getContent()) {
				paqContent = paqContent + ((XmlId) o).internalToExternalId()
						+ " ";
			}
			newLink.setAttribute("targets", paqContent);
			newLinkGrp.appendChild(newLink);
		}
		for (final Object o3 : Sentences.getXalignResult().getTargetPaquets()) {
			paq = (Paquet) o3;
			newLink = new ElementImpl(xdoc, "link");
			newLink.setAttribute("type", "linking");
			newLink.setAttribute("xml:id", paq.getId());
			paqContent = "";
			for (final Object o : paq.getContent()) {
				paqContent = paqContent + ((XmlId) o).internalToExternalId()
						+ " ";
			}
			newLink.setAttribute("targets", paqContent);
			newLinkGrp.appendChild(newLink);
		}
		newLinkGrp = new ElementImpl(xdoc, "linkGrp");
		newLinkGrp.setAttribute("type", "noCorresp");
		newDiv.appendChild(newLinkGrp);
		// now for the noCorresp :
		for (final Object o2 : Sentences.getXalignResult().getNoCorrespSource()) {
			xiCour = (XmlId) o2;
			newLink = new ElementImpl(xdoc, "link");
			newLink.setAttribute("type", "noCorresp");
			newLink.setAttribute("targets", "" + xiCour.internalToExternalId());
			newLinkGrp.appendChild(newLink);
		}
		for (final Object o1 : Sentences.getXalignResult().getNoCorrespTarget()) {
			xiCour = (XmlId) o1;
			newLink = new ElementImpl(xdoc, "link");
			newLink.setAttribute("type", "noCorresp");
			newLink.setAttribute("targets", "" + xiCour.internalToExternalId());
			newLinkGrp.appendChild(newLink);
		}
		newLinkGrp = new ElementImpl(xdoc, "linkGrp");
		newLinkGrp.setAttribute("type", "alignment");
		newDiv.appendChild(newLinkGrp);
		// and finally the alignments :
		for (final Object o : Sentences.getXalignResult().getAlignments()) {
			aCour = (Alignement) o;
			newLink = new ElementImpl(xdoc, "link");
			newLink.setAttribute("type", "alignment");
			alignContent = "" + aCour.getXmlIdSource().internalToExternalId()
					+ " " + aCour.getXmlIdTarget().internalToExternalId();
			newLink.setAttribute("targets", alignContent);
			newLinkGrp.appendChild(newLink);
		}
		whereToInsert.appendChild(newDiv);
		/*
		 * ElementImpl linkGrp = new ElementImpl(xdoc,"linkGrp"); // create
		 * XAttributeList linkGrp.setAttribute("domains","b1 b1");
		 * linkGrp.setAttribute("targType","seg");
		 * linkGrp.setAttribute("targFunc",sLang+" "+tLang);
		 * linkGrp.setAttribute("targOrder","Y");
		 * linkGrp.setAttribute("evaluate","all");
		 * linkGrp.setAttribute("crdate","empty");
		 * linkGrp.setAttribute("source",srcName);
		 * linkGrp.setAttribute("target",tarName);
		 * linkGrp.setAttribute("type","alignmentXalign");
		 */
		/*
		 * System.out.println("Dans InsertLinkGrp");
		 * System.out.println("stcTar ="); System.out.println(stcTar);
		 * System.out.println("Linking ="); System.out.println(Linking);
		 * System.out.println("Links ="); System.out.println(Links);
		 * System.out.println("idSrc = "); System.out.println(idSrc);
		 * System.out.println("idTar"); System.out.println(idTar);
		 */
		// La structure est un peu compliquee !
		// put xptr elements
		/*
		 * for (int i = 0 ; i < stcTar.size() ; i++) { Vector tmp =
		 * (Vector)stcTar.elementAt(i);
		 * linkGrp.appendChild(CreateXptr(xdoc,tmp)); linkGrp.appendChild(new
		 * TextImpl(xdoc,"\n")); }
		 * 
		 * stcTar.removeAllElements(); // liberate the memory // put linking
		 * elements
		 * 
		 * for (int i = 0 ; i < Linking.size() ; i++) { Vector tmp =
		 * (Vector)Linking.elementAt(i); ElementImpl elt = CreateLinking(xdoc,
		 * tmp); if(elt != null) linkGrp.appendChild(elt);
		 * linkGrp.appendChild(new TextImpl(xdoc,"\n")); }
		 * Linking.removeAllElements(); // put link elements int Size =
		 * Links.size(); for(int i=0;i<Size;i++) { Vector tmp =
		 * (Vector)Links.elementAt(0); // tmp est de la forme [d1p1s1, x1] // on
		 * veut l'ID de x1 // il faut passer par stcTar
		 * 
		 * ElementImpl elt = CreateConcord(xdoc, tmp); if(elt != null)
		 * //linkGrp.appendChild(CreateConcord(xdoc,tmp));
		 * linkGrp.appendChild(elt); linkGrp.appendChild(new
		 * TextImpl(xdoc,"\n")); Links.removeElementAt(0); }
		 */
		// xdoc.appendChild(linkGrp);
		// whereToInsert.appendChild(linkGrp);
		XMLTools.saveDocument(fileName, xdoc, "UTF-8");
		/*
		 * try { Writer pw = new PrintWriter(new FileOutputStream(fileName)); //
		 * Prints the XML document pw.write(XMLTools.STD_HEADER);
		 * pw.write(XMLTools.toString(xdoc)); pw.close(); } catch
		 * (java.io.IOException e) { System.err.println("InsertLinkGrp: " + e);
		 * e.printStackTrace(); }
		 */
	}

	private Node findBody(Node n) {
		if (n.getNodeName().equals("body")) {
			return n;
		}
		final NodeList sons = n.getChildNodes();
		if (sons.getLength() == 0) {
			return null;
		}
		int i = 0;
		Node bodyNode;
		while (i < sons.getLength()) {
			bodyNode = findBody(sons.item(i));
			if (bodyNode != null) {
				return bodyNode;
			}
			i++;
		}
		return null;
	}

	/*
	 * private void removeXAlignResults(Node body){ // On cherche si parmi les
	 * fils on a un linkGrp type="alignmentXalign" NodeList sons =
	 * body.getChildNodes(); Node cour; for (int i = 0; i < sons.getLength();
	 * i++){ cour = sons.item(i); if (cour.getNodeType() == Node.ELEMENT_NODE){
	 * if (cour.getNodeName().equals("linkGrp")){ if
	 * (((Element)cour).getAttribute("type").equals("alignmentXalign") ||
	 * ((Element)cour).getAttribute("type").equals("essaiResultXALign")){
	 * body.removeChild(cour); // je ne fais pas de return ici des fois qu'il y
	 * en ait plusieurs :-) } } } } };
	 */
	private void removeXAlignResults(Node body) {
		// On cherche si parmi les fils on a un linkGrp type="alignmentXalign"
		final NodeList sons = body.getChildNodes();
		Node cour;
		for (int i = 0; i < sons.getLength(); i++) {
			cour = sons.item(i);
			if (cour.getNodeType() == Node.ELEMENT_NODE) {
				if (cour.getNodeName().equals("div")) {
					if (((Element) cour).getAttribute("type").equals(
							"resultXAlign")
							|| ((Element) cour).getAttribute("type").equals(
									"essaiResultXALign")) {
						body.removeChild(cour);
						// je ne fais pas de return ici des fois qu'il y en ait
						// plusieurs :-)
					}
				}
			}
		}
	}
}
