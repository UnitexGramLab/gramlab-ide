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
 * @(#)       Alignement.java
 * 
 * Created    17 january 2007
 * 
 * Copyright  2006 (C) Bertrand.Gaiffe@atilf.fr
 *            
 *            
 */
package org.gramlab.core.loria.xsilfide.multialign;

class Alignement implements Comparable<Alignement> {
	private final XmlId pSource;
	private final XmlId pCible;
	private Alignement generatedFrom; // the alignement as specified

	// by the user which yields this one.
	// This occurs because an alignement
	// specified by the user yields fuzzy
	// alignements concerning the
	// englobing elements.
	public Alignement(String uriS, String uriC, String idS, String idC,
			LoadAndPrepareTexts lpt) {
		pSource = new XmlId(uriS, idS, lpt);
		pCible = new XmlId(uriC, idC, lpt);
		generatedFrom = null;
	}

	/*
	 * public Alignement(Vector vSource, Vector vCible){ if (vSource.size() ==
	 * 2){ pSource = new XmlId((String)vSource.elementAt(0),
	 * (String)vSource.elementAt(1)); } else{ pSource = new
	 * XmlId("",(String)vSource.elementAt(0));
	 * 
	 * } if (vCible.size() == 2){ pCible = new
	 * XmlId((String)vCible.elementAt(0), (String)vCible.elementAt(1)); } else{
	 * pCible = new XmlId("",(String)vCible.elementAt(0));
	 * 
	 * } };
	 */
	public Alignement(XmlId ps, XmlId pc) {
		pSource = ps;
		pCible = pc;
		generatedFrom = null;
	}

	public String getIdSource() {
		return pSource.getLocalName();
	}

	public void setIdSource(String newId) {
		pSource.setLocalName(newId);
	}

	public String getIdCible() {
		return pCible.getLocalName();
	}

	public void setIdCible(String newId) {
		pCible.setLocalName(newId);
	}

	@Override
	public String toString() {
		return "(" + pSource + "<->" + pCible + ")";
		/*
		 * String res = "("+pSource+"<->"+pCible+")"; if (getGeneratedFrom() ==
		 * null){ res = res + "from null"; } else{ res = res + "from "+
		 * getFirstGeneratedFrom(); } return res;
		 */
	}

	public XmlId getXmlIdSource() {
		return pSource;
	}

	public XmlId getXmlIdTarget() {
		return pCible;
	}

	// the alignments are sorted according to the order in which their
	// pSource appears in the source text.
	@Override
	public int compareTo(Alignement other) {
		return pSource.compareTo(other.getXmlIdSource());
	}

	public void setGeneratedFrom(Alignement a) {
		generatedFrom = a;
	}

	Alignement getGeneratedFrom() {
		return generatedFrom;
	}

	public Alignement getFirstGeneratedFrom() {
		Alignement cour = this;
		Alignement prec = cour;
		while (cour != null) {
			prec = cour;
			cour = cour.getGeneratedFrom();
		}
		return prec;
	}

	public Alignement duplicate() {
		return new Alignement(new XmlId(getXmlIdSource()), new XmlId(
				getXmlIdTarget()));
	}
}
