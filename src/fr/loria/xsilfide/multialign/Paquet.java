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
 * @(#)       Paquet.java
 * 
 * Created    17 january 2007
 * 
 * Copyright  2006 (C) Bertrand.Gaiffe@atilf.fr
 *            
 *            
 */
package fr.loria.xsilfide.multialign;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

class Paquet {
	private String id;
	private TreeSet<XmlId> contenu;

	public Paquet(String unId, Collection<XmlId> unContenu) {
		id = unId;
		contenu = new TreeSet<XmlId>(unContenu);
	}

	String getId() {
		return id;
	}

	Collection<XmlId> getContent() {
		return contenu;
	}

	ArrayList<String> getContentWithoutUri() {
		final ArrayList<String> res = new ArrayList<String>();
		XmlId tmp;
		for (final Object o : getContent()) {
			tmp = (XmlId) o;
			res.add(tmp.getLocalName());
		}
		return res;
	}

	private void setContent(Collection<XmlId> cont) {
		contenu = new TreeSet<XmlId>(cont);
	}

	public void translateIds(LoadAndPrepareTexts lpt, boolean inSource) {
		String oldId;
		String newId;
		final ArrayList<XmlId> newContent = new ArrayList<XmlId>();
		XmlId cour;
		for (final Object o : getContent()) {
			cour = (XmlId) o;
			oldId = cour.getLocalName();
			if (inSource) {
				newId = lpt.extToIntIdSource(oldId);
			} else {
				newId = lpt.extToIntIdTarget(oldId);
			}
			newContent.add(new XmlId(cour.getUri(), newId, lpt));
		}
		setContent(newContent);
	}

	/*
	 * l'id du paquet si id est dedans, null si pas dans le paquet.
	 */
	public String idPaquetOf(String id1) {
		for (final Object o : getContent()) {
			if (o.equals(id1)) {
				return getId();
			}
		}
		return null;
	}

	// on recoit un vecteur d'ids, on veut ceux qui sont
	// présents dans le paquet.
	public ArrayList<String> containedInPaquet(Collection<String> ids) {
		String idP;
		Iterator<XmlId> eP;
		boolean trouve;
		XmlId cour;
		final ArrayList<String> res = new ArrayList<String>();
		for (final Object id1 : ids) {
			id = (String) id1;
			eP = getContent().iterator();
			trouve = false;
			while (eP.hasNext() && !trouve) {
				cour = eP.next();
				idP = cour.getLocalName();
				trouve = idP.equals(id);
			}
			if (trouve) {
				res.add(id);
			}
		}
		return res;
	}

	@Override
	public String toString() {
		return getId() + " : " + getContent();
	}
}
