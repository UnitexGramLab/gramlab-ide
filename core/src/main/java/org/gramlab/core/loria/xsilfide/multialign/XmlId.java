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
 * @(#)       XmlId.java
 * 
 * Created    21 december 2006
 * 
 * Copyright  2006 (C) Bertrand.Gaiffe@atilf.fr
 *            
 *            
 */
// On veut être sûr de l'ordre dans lequel les identificateurs
// apparaîssent dans le texte.... L'endroit où on a l'ordre est
// LoadAndPrepareTexts.
package org.gramlab.core.loria.xsilfide.multialign;

class XmlId implements Comparable<XmlId> {
	private static LoadAndPrepareTexts lpt = null;
	private final String uri;
	private String localName;

	public XmlId(String u, String n, LoadAndPrepareTexts lpte) {
		uri = u;
		localName = n;
		lpt = lpte;
	}

	public XmlId(XmlId x) {
		uri = x.uri;
		localName = x.localName;
	}

	public String getUri() {
		return uri;
	}

	public String getLocalName() {
		return localName;
	}

	@Override
	public String toString() {
		return uri + "#" + localName;
	}

	public void setLocalName(String ln) {
		localName = ln;
	}

	// translation from an id as xalign computes them
	// to the id as they appear in the source and target files.
	public XmlId internalToExternalId() {
		if (getUri().equals(lpt.uriSource)) {
			return new XmlId(getUri(), (String) lpt.idSrc.get(getLocalName()),
					lpt);
		} else if (getUri().equals(lpt.uriTarget)) {
			return new XmlId(getUri(), (String) lpt.idTar.get(getLocalName()),
					lpt);
		} else
			return this;
	}

	static int ordreDansTexte(String s1, String s2, boolean inSource) {
		return lpt.ordreDansTexte(s1, s2, inSource);
	}

	// on veut ici l'ordre d'apparition dans le texte.
	// il n'y a que lpt qui connaisse cet ordre
	@Override
	public int compareTo(XmlId o) {
		final XmlId xo = o;
		if (lpt == null) {
			return -1;
		}
		return lpt.ordreDansTexte(this, xo);
	}
}
