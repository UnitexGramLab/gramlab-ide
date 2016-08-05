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
 * @(#)       Cognates.java
 * 
 * Created    06 december 2006
 * 
 * Copyright  2006 (C) Bertrand.Gaiffe@atilf.fr
 *            
 *            
 */
package fr.loria.xsilfide.multialign;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

@SuppressWarnings("unchecked")
public class Cognates {
	private final ArrayList<Paquet> paquetsSource;
	private final ArrayList<Paquet> paquetsCible;
	private ArrayList<Alignement> alignements;
	private ArrayList<XmlId> noCorrespSource;
	private ArrayList<XmlId> noCorrespCible;
	private ArrayList<Alignement> fuzzyAlignments;
	private String uriSource;
	private String uriCible;

	public Cognates(String fSource, String fCible) {
		paquetsSource = new ArrayList<Paquet>();
		paquetsCible = new ArrayList<Paquet>();
		alignements = new ArrayList<Alignement>();
		noCorrespSource = new ArrayList<XmlId>();
		noCorrespCible = new ArrayList<XmlId>();
		fuzzyAlignments = new ArrayList<Alignement>();
		uriSource = fSource;
		uriCible = fCible;
	}

	public Cognates() {
		paquetsSource = new ArrayList<Paquet>();
		paquetsCible = new ArrayList<Paquet>();
		alignements = new ArrayList<Alignement>();
		noCorrespSource = new ArrayList<XmlId>();
		noCorrespCible = new ArrayList<XmlId>();
		fuzzyAlignments = new ArrayList<Alignement>();
		uriSource = "";
		uriCible = "";
	}

	public String getUriSource() {
		return uriSource;
	}

	public void setUriSource(String n) {
		uriSource = n;
	}

	public String getUriTarget() {
		return uriCible;
	}

	public void setUriTarget(String n) {
		uriCible = n;
	}

	public ArrayList<Paquet> getSourcePaquets() {
		return paquetsSource;
	}

	public ArrayList<Paquet> getTargetPaquets() {
		return paquetsCible;
	}

	public ArrayList<XmlId> getNoCorrespSource() {
		return noCorrespSource;
	}

	public ArrayList<XmlId> getNoCorrespTarget() {
		return noCorrespCible;
	}

	public ArrayList<Alignement> getAlignments() {
		return alignements;
	}

	public void addNoCorrespSource(XmlId id) {
		noCorrespSource.add(id);
	}

	public void addNoCorrespTarget(XmlId id) {
		noCorrespCible.add(id);
	}

	public void addPaquetSrc(XmlId idPaquet, Collection<XmlId> content) {
		final Paquet p = new Paquet(idPaquet.getLocalName(), content);
		paquetsSource.add(p);
	}

	public void addPaquetTarget(XmlId idPaquet, Collection<XmlId> content) {
		final Paquet p = new Paquet(idPaquet.getLocalName(), content);
		paquetsCible.add(p);
	}

	public void addPaquet(String paquet, String paquetId,
			LoadAndPrepareTexts lpt) {
		String uri = "";
		String sCour;
		// Vector tmp;
		XmlId tmp;
		// Vector contenuP;
		ArrayList<XmlId> contenuP;
		Vector<Object> paquetVect;
		Paquet p;
		// System.out.println("addPaquet("+paquet+", "+paquetId+")\n");
		paquetVect = segmentOnWhiteSpaces(paquet);
		contenuP = new ArrayList<XmlId>();
		for (final Enumeration<Object> e = paquetVect.elements(); e
				.hasMoreElements();) {
			sCour = (String) e.nextElement();
			// tmp = segmentOnChar(sCour, '#');
			tmp = decodePointer(sCour, lpt);
			if (uri.equals("")) {
				uri = tmp.getUri();
			} else {
				if (!uri.equals(tmp.getUri())) {
					System.err.println("Cognates : invalid paquet\n");
					System.exit(1);
				}
			}
			contenuP.add(tmp);
		}
		p = new Paquet(paquetId, contenuP);
		if (uri.equals(uriSource)) {
			System.out.println("Adding source paquet\n");
			paquetsSource.add(p);
		} else if (uri.equals(uriCible)) {
			System.out.println("Adding target paquet\n");
			paquetsCible.add(p);
		} else {
			System.err.println("Cognates : paquets with invalid URI : " + uri
					+ "\n");
			System.exit(1);
		}
	}

	public void addAlignment(XmlId i1, XmlId i2) {
		final Alignement a = new Alignement(i1, i2);
		alignements.add(a);
	}

	public void addAlignment(String lnks, LoadAndPrepareTexts lpt) {
		Vector<Object> linkVect;
		Alignement a;
		linkVect = segmentOnWhiteSpaces(lnks);
		a = new Alignement(decodePointer((String) linkVect.elementAt(0), lpt),
				decodePointer((String) linkVect.elementAt(1), lpt));
		alignements.add(a);
	}

	public void addNoCorresp(String noCorresp, LoadAndPrepareTexts lpt) {
		XmlId nocV;
		nocV = decodePointer(noCorresp, lpt);
		if (nocV.getUri().equals(uriSource)) {
			noCorrespSource.add(nocV);
		} else {
			if (nocV.getUri().equals(uriCible)) {
				noCorrespCible.add(nocV);
			} else {
				// On pourrait envisager d'avoir un paquet ?
				System.err.println("Cognates : noCorresp with invalid URI : "
						+ nocV + "\n");
				System.exit(1);
			}
		}
	}

	public Alignement addFuzzyAlign(String fzA, LoadAndPrepareTexts lpt) {
		Vector<Object> linkVect;
		Alignement a;
		linkVect = segmentOnWhiteSpaces(fzA);
		a = new Alignement(decodePointer((String) linkVect.elementAt(0), lpt),
				decodePointer((String) linkVect.elementAt(1), lpt));
		fuzzyAlignments.add(a);
		return a;
	}

	Alignement addFuzzyAlign(String idSource, String idCible,
			LoadAndPrepareTexts lpt) {
		Alignement aCour;
		aCour = new Alignement(uriSource, uriCible, idSource, idCible, lpt);
		fuzzyAlignments.add(aCour);
		return aCour;
	}

	public void ecrire() {
		System.out.println("\npaquetsSource :" + paquetsSource);
		System.out.println("\npaquetsCible : " + paquetsCible);
		System.out.println("\nalignements :" + alignements);
		System.out.println("\nnoCorrespSource :" + noCorrespSource);
		System.out.println("\nnoCorrespCible : " + noCorrespCible);
		System.out.println("\nfuzzyAlignments :" + fuzzyAlignments);
		System.out.println("\nuriSource :" + uriSource);
		System.out.println("\nuriCible :" + uriCible);
	}

	@Override
	public String toString() {
		return "\npaquetsSource :" + paquetsSource + "\npaquetsCible : "
				+ paquetsCible + "\nalignements :" + alignements
				+ "\nnoCorrespSource :" + noCorrespSource
				+ "\nnoCorrespCible : " + noCorrespCible
				+ "\nfuzzyAlignments :" + fuzzyAlignments + "\nuriSource :"
				+ uriSource + "\nuriCible :" + uriCible;
	}

	private XmlId decodePointer(String s, LoadAndPrepareTexts lpt) {
		final Vector<Object> v = segmentOnChar(s, '#');
		if (v.size() == 1) {
			return new XmlId("", (String) v.elementAt(0), lpt);
		}
		return new XmlId((String) v.elementAt(0), (String) v.elementAt(1), lpt);
	}

	private Vector<Object> segmentOnChar(String s, char cs) {
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

	private Vector<Object> segmentOnWhiteSpaces(String s) {
		final Vector<Object> result = new Vector<Object>();
		int finMot;
		int debutMot;
		char c;
		s = s.trim();
		debutMot = 0;
		finMot = 0;
		while (finMot < s.length()) {
			c = s.charAt(finMot);
			if (Character.isWhitespace(c)) {
				result.addElement(s.substring(debutMot, finMot));
				while (Character.isWhitespace(c)) {
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

	/*
	 * Saturation of the cognates : - if some level appears un only one of the
	 * two texts, all what is within this level also is in one text only. - if
	 * we have an alignment then all the levels that contain this alignment are
	 * fuzzy aligned. In order to do this, we need the inclusion structures in
	 * source an target.
	 * 
	 * While doing this, we translate the id that appear in the texts into
	 * internal to xalign ids.
	 */
	public void saturerCognates(LoadAndPrepareTexts lpt) {
		Alignement cour;
		String idSource, idTarget;
		Collection<String> vIdSource, vIdCible;
		Vector<Object> contenantsSource, contenantsCible;
		Vector<Object> tmp;
		final ArrayList<Alignement> oldFuzzy = (ArrayList<Alignement>) fuzzyAlignments
				.clone();
		final ArrayList<Alignement> oldAlign = (ArrayList<Alignement>) alignements
				.clone();
		// contraints that are yielded from the alignments :
		for (final Iterator<Alignement> e = doubleIterator(oldFuzzy, oldAlign); e
				.hasNext();) {
			cour = e.next();
			// System.out.println("cour = "+cour+"\n");
			idSource = cour.getIdSource();
			idTarget = cour.getIdCible();
			// idSource et/ou idTarget peuvent désigner des paquets...
			vIdSource = paquetOf(idSource, true);
			vIdCible = paquetOf(idTarget, false);
			// System.out.println("vIdSource = "+vIdSource+"\n");
			// System.out.println("vIdCible = "+vIdCible+"\n");
			// on veut maintenant tous les contenant d'un élement de vIdSource
			// et tous les contenant d'un élément de vIdTarget
			contenantsSource = new Vector<Object>();
			for (final Object aVIdSource : vIdSource) {
				// tmp = (Vector)eSource.nextElement();
				// idSource = (String)tmp.elementAt(1);
				idSource = (String) aVIdSource;
				tmp = lpt.includedInto(idSource, true);
				// System.out.println("tmp = "+tmp+"\n");
				contenantsSource = fusionOrdonneeContenants(contenantsSource,
						tmp);
			}
			contenantsCible = new Vector<Object>();
			for (final Object aVIdCible : vIdCible) {
				// tmp = (Vector)eCible.nextElement();
				// idTarget = (String)tmp.elementAt(1);
				idTarget = (String) aVIdCible;
				// System.out.println("lpt.includedInto("+idTarget+", false) ="+
				// lpt.includedInto(idTarget, false));
				tmp = lpt.includedInto(idTarget, false);
				contenantsCible = fusionOrdonneeContenants(contenantsCible, tmp);
			}
			// reste à engendrer les fuzzy alignments en faisant attention
			// aux changements de type dans la structure.
			// pour ça, on parcourt dans l'ordre les contenants source et
			// cible.
			// System.out.println("Contenants source = "+contenantsSource+"\n");
			// System.out.println("Contenants cible = "+contenantsCible+"\n");
			final Enumeration<Object> eSource = contenantsSource.elements();
			final Enumeration<Object> eCible = contenantsCible.elements();
			String iSource, iCible;
			boolean pasFini;
			Alignement newFuzzy;
			// les divisions :
			if (eSource.hasMoreElements() && eCible.hasMoreElements()) {
				iSource = (String) eSource.nextElement();
				iCible = (String) eCible.nextElement();
				pasFini = lpt.isIdOfDiv(iSource) && lpt.isIdOfDiv(iCible);
				while (pasFini) {
					newFuzzy = addFuzzyAlign(iSource, iCible, lpt);
					newFuzzy.setGeneratedFrom(cour);
					pasFini = eSource.hasMoreElements()
							&& eCible.hasMoreElements();
					if (pasFini) {
						iSource = (String) eSource.nextElement();
						iCible = (String) eCible.nextElement();
						pasFini = pasFini && lpt.isIdOfDiv(iSource)
								&& lpt.isIdOfDiv(iCible);
					}
				}
				// les paragraphes :
				if (!lpt.isIdOfParagraph(iSource)) {
					while (eSource.hasMoreElements()) {
						iSource = (String) eSource.nextElement();
					}
				}
				if (!lpt.isIdOfParagraph(iCible)) {
					while (eCible.hasMoreElements()) {
						iCible = (String) eCible.nextElement();
					}
				}
				pasFini = lpt.isIdOfParagraph(iSource)
						&& lpt.isIdOfParagraph(iCible);
				while (pasFini) {
					newFuzzy = addFuzzyAlign(iSource, iCible, lpt);
					newFuzzy.setGeneratedFrom(cour);
					pasFini = eSource.hasMoreElements()
							&& eCible.hasMoreElements();
					if (pasFini) {
						iSource = (String) eSource.nextElement();
						iCible = (String) eCible.nextElement();
						pasFini = lpt.isIdOfParagraph(iSource)
								&& lpt.isIdOfParagraph(iCible);
					}
				}
			}
		}
		// On passe aux noCorresp. Cette fois on est en top down
		// Rq : normalement, on fait un travail inutile puisque
		// l'aligneur est également top/down. Donc : on fait pas !
		// euhhhh si : on traduit en id internes
		// et d'ailleurs, on fait ça partout !
		for (final Object aPaquetsSource : paquetsSource) {
			((Paquet) aPaquetsSource).translateIds(lpt, true);
		}
		for (final Object aPaquetsCible : paquetsCible) {
			((Paquet) aPaquetsCible).translateIds(lpt, false);
		}
		Alignement aCour;
		String lId;
		String newId;
		for (final Object alignement : alignements) {
			aCour = (Alignement) alignement;
			aCour.setGeneratedFrom(aCour.duplicate());
			lId = aCour.getIdSource();
			newId = lpt.extToIntIdSource(lId);
			if (newId == null) {
				newId = lId;
			}
			aCour.setIdSource(newId);
			lId = aCour.getIdCible();
			newId = lpt.extToIntIdTarget(lId);
			if (newId == null) {
				newId = lId;
			}
			aCour.setIdCible(newId);
		}
		for (int i = 0; i < oldFuzzy.size(); i++) {
			aCour = fuzzyAlignments.get(i);
			aCour.setGeneratedFrom(aCour.duplicate());
			lId = aCour.getIdSource();
			newId = lpt.extToIntIdSource(lId);
			if (newId == null) {
				newId = lId;
			}
			aCour.setIdSource(newId);
			lId = aCour.getIdCible();
			newId = lpt.extToIntIdTarget(lId);
			if (newId == null) {
				newId = lId;
			}
			aCour.setIdCible(newId);
		}
		for (int i = 0; i < noCorrespSource.size(); i++) {
			lId = noCorrespSource.get(i).getLocalName();
			newId = lpt.extToIntIdSource(lId);
			if (newId == null) {
				newId = lId;
			}
			noCorrespSource.set(i, new XmlId(uriSource, newId, lpt));
		}
		for (int i = 0; i < noCorrespCible.size(); i++) {
			lId = noCorrespCible.get(i).getLocalName();
			newId = lpt.extToIntIdTarget(lId);
			if (newId == null) {
				newId = lId;
			}
			noCorrespCible.set(i, new XmlId(uriCible, newId, lpt));
		}
		// reste à trier noCorrespSource et cible.
		TreeSet<XmlId> ts = new TreeSet<XmlId>(noCorrespSource);
		noCorrespSource = new ArrayList<XmlId>(ts);
		ts = new TreeSet<XmlId>(noCorrespCible);
		noCorrespCible = new ArrayList<XmlId>(ts);
		// we also have to sort the alignments and the fuzzy alignments
		final TreeSet<Alignement> ts2 = new TreeSet<Alignement>(alignements);
		alignements = new ArrayList<Alignement>(ts2);
		final TreeSet<Alignement> ts3 = new TreeSet<Alignement>(fuzzyAlignments);
		fuzzyAlignments = new ArrayList<Alignement>(ts3);
	}

	/*
	 * traduction des cognates en contraintes de chemin sachant qu'on
	 * s'intéresse à l'alignement de segsSource avec segsTarget.
	 */
	@SuppressWarnings("null")
	public ContraintesChemin cognates2Chemins(Vector<Object> segsSource,
			Vector<Object> segsTarget) {
		int i = 0;
		int j = 0;
		BiPoint biCour;
		int minSource, maxSource, minTarget, maxTarget;
		boolean contraintesCheminCree = false;
		ContraintesChemin res = null;
		/*
		 * ContraintesChemin res = new ContraintesChemin(segsSource.size()+1,
		 * segsTarget.size()+1);
		 */
		// System.out.println("cognates2Chemins("+segsSource+", "+
		// segsTarget+")\n");
		Iterator<Cognates.BiPoint> iA = interAlignements(segsSource,
				segsTarget, alignements).iterator();
		if (iA.hasNext()) {
			res = new ContraintesChemin(segsSource.size() + 1,
					segsTarget.size() + 1);
			contraintesCheminCree = true;
		}
		for (; iA.hasNext();) {
			biCour = iA.next();
			minSource = biCour.get1();
			maxSource = biCour.get2();
			minTarget = biCour.get3();
			maxTarget = biCour.get4();
			if ((minSource == maxSource) && (minTarget == maxTarget)) {
				i = minSource + 1;
				j = minTarget + 1;
				res.addCheminForce(i, j, Align.SUBSTITUTION);
				// On empêche de "sauter" par dessus i,j
				res.interdireChemin(i + 1, j, Align.MELANGE);
				res.interdireChemin(i + 1, j, Align.CONTRACTION);
				res.interdireChemin(i, j + 1, Align.MELANGE);
				res.interdireChemin(i, j + 1, Align.EXPANSION);
				// reste à forcer le meilleur chemin à passer par
				// i-1, j-1. Pour que depuis là, on aille en i,j
				// par la substitution.
				for (int i1 = 0; i1 < i; i1++) {
					for (int j1 = j; j1 <= segsTarget.size() + 1; j1++) {
						res.addCoutForce(i1, j1);
					}
				}
				for (int i1 = i; i1 <= segsSource.size() + 1; i1++) {
					for (int j1 = 0; j1 < j; j1++) {
						res.addCoutForce(i1, j1);
					}
				}
			} else {
				// On ajoute un chemin "special" en effet, ça peut être
				// une forme de chemin que Xalign ne sait pas calculer lui
				// même ex : 4 , 3. La contrainte tombe en :
				// maxSource+1, maxTarget+1 et depuis ce point,
				// on veut atteindre le point minSource, minTarget.
				res.setCheminSpecial(maxSource + 1, maxTarget + 1, maxSource
						+ 1 - minSource, maxTarget + 1 - minTarget);
				// on force le meilleur chemin à passer par là :
				for (int x = maxSource + 1; x <= segsSource.size(); x++) {
					for (int y = 0; y <= maxTarget; y++) {
						res.addCoutForce(x, y);
					}
				}
				for (int x = 0; x <= maxSource + 1; x++) {
					for (int y = maxTarget + 2; y <= segsTarget.size(); y++) {
						res.addCoutForce(x, y);
					}
				}
				if (maxSource + 2 <= segsSource.size()) {
					res.interdireChemin(maxSource + 2, maxTarget + 1,
							Align.MELANGE);
					res.interdireChemin(maxSource + 2, maxTarget + 1,
							Align.CONTRACTION);
					if (maxTarget + 2 <= segsTarget.size()) {
						res.interdireChemin(maxSource + 2, maxTarget + 2,
								Align.MELANGE);
						res.interdireChemin(maxSource + 2, maxTarget + 2,
								Align.CONTRACTION);
					}
				}
				if (maxTarget + 3 <= segsTarget.size()) {
					res.interdireChemin(maxSource + 2, maxTarget + 3,
							Align.MELANGE);
				}
			}
		}
		// On passe aux fuzzyAlignements :
		// System.out.println("interAlignements("+segsSource+", "+segsTarget+", "+fuzzyAlignments+") = "+interAlignements(segsSource,
		// segsTarget, fuzzyAlignments));
		iA = interAlignements(segsSource, segsTarget, fuzzyAlignments)
				.iterator();
		if (iA.hasNext() && (!contraintesCheminCree)) {
			res = new ContraintesChemin(segsSource.size() + 1,
					segsTarget.size() + 1);
			contraintesCheminCree = true;
		}
		for (; iA.hasNext();) {
			biCour = iA.next();
			i = biCour.get1() + 1;
			j = biCour.get3() + 1;
			for (int i1 = 1; i1 < i; i1++) {
				for (int j1 = j; j1 <= segsTarget.size(); j1++) {
					res.addCoutForce(i1, j1);
				}
			}
			for (int i1 = i; i1 <= segsSource.size(); i1++) {
				for (int j1 = 1; j1 < j; j1++) {
					res.addCoutForce(i1, j1);
				}
			}
		}
		// Reste à prendre en compte les noCorresp source et cible.
		// ce sont des contraintesChemin spéciales parce que pas situées
		// en un point précis mais concernant toute une ligne respectivement
		// colonne.
		// System.out.println("Test d'intersection : \n");
		// System.out.println("interNoCorresp("+segsSource+", "+noCorrespSource+") = "+interNoCorresp(segsSource,
		// noCorrespSource)+"\n");
		ArrayList<Integer> indNoCorresp = interNoCorresp(segsSource,
				noCorrespSource, true);
		int i1;
		Iterator<Integer> iInd = indNoCorresp.iterator();
		if (iInd.hasNext() && (!contraintesCheminCree)) {
			res = new ContraintesChemin(segsSource.size() + 1,
					segsTarget.size() + 1);
			contraintesCheminCree = true;
		}
		for (; iInd.hasNext();) {
			i1 = iInd.next();
			res.setIgnoreSource(i1 + 1);
			// Il faut, là encore empêcher le meilleur chemin de sauter par
			// dessus
			// la colonne i1+1.
			// On interdit donc les chemins de la forme 2-1 et 2-2 qui
			// traverseraient
			// i1.
			if (i1 + 1 < segsSource.size()) {
				for (int i2 = 0; i2 <= segsTarget.size(); i2++) {
					res.interdireChemin(i1 + 2, i2, Align.MELANGE);
					res.interdireChemin(i1 + 2, i2, Align.CONTRACTION);
				}
			}
		}
		indNoCorresp = interNoCorresp(segsTarget, noCorrespCible, false);
		iInd = indNoCorresp.iterator();
		if (iInd.hasNext() && (!contraintesCheminCree)) {
			res = new ContraintesChemin(segsSource.size() + 1,
					segsTarget.size() + 1);
			contraintesCheminCree = true;
		}
		for (; iInd.hasNext();) {
			i1 = iInd.next();
			res.setIgnoreTarget(i1 + 1);
			// Il faut, là encore empêcher le meilleur chemin de sauter par
			// dessus
			// la ligne i1+1.
			// On interdit donc les chemins de la forme 2-1 et 2-2 qui
			// traverseraient
			// i1.
			if (i1 + 1 < segsTarget.size()) {
				for (int i2 = 0; i2 <= segsTarget.size(); i2++) {
					res.interdireChemin(i2, i1 + 1, Align.MELANGE);
					res.interdireChemin(i2, i1 + 1, Align.CONTRACTION);
				}
			}
		}
		// System.out.println("Sortie de cognate2chemin\n");
		return res;
	}

	class BiPoint {
		final Point a;
		final Point b;

		public BiPoint(Point x, Point y) {
			a = x;
			b = y;
		}

		public BiPoint(int x, int y, int m, int n) {
			a = new Point(x, y);
			b = new Point(m, n);
		}

		public void set1(int x) {
			a.setX(x);
		}

		public void set2(int y) {
			a.setY(y);
		}

		public void set3(int x) {
			b.setX(x);
		}

		public void set4(int y) {
			b.setY(y);
		}

		@Override
		public String toString() {
			return "((" + a.getX() + ", " + a.getY() + "), (" + b.getX() + ", "
					+ b.getY() + "))";
		}

		public int get1() {
			return a.getX();
		}

		public int get2() {
			return a.getY();
		}

		public int get3() {
			return b.getX();
		}

		public int get4() {
			return b.getY();
		}
	}

	private ArrayList<BiPoint> interAlignements(Vector<Object> segsSource,
			Vector<Object> segsTarget, ArrayList<Alignement> aligns) {
		// we look for the alignements that are present in segsSource and
		// segtarget
		// we suppose that the alignements are ordered.
		// on cherche quels alignements sont présents dans segsSource et
		// segsTarget
		// on suppose que les alignements sont ordonnés (ils ne peuvent pas se
		// croiser)
		final ArrayList<BiPoint> res = new ArrayList<BiPoint>();
		final Enumeration<Object> eS = segsSource.elements();
		final Enumeration<Object> eT = segsTarget.elements();
		final Iterator<Alignement> iAlign = aligns.iterator();
		String idSegSourceCour;
		String idTargetCour;
		Alignement aCour = null;
		Vector<Object> vcour;
		ArrayList<String> temp;
		String idDebutAlignSource, idFinAlignSource, idDebutAlignTarget, idFinAlignTarget;
		int indiceSegSource, indiceSegTarget;
		BiPoint biCour;
		boolean finiAlign, finiEs, finiEt;
		// System.out.println("Début interAlignements\n");
		// System.out.println("segsSource = "+segsSource);
		// System.out.println("segsTarget = "+segsTarget);
		/* System.out.println("alignements = "+aligns); */
		indiceSegSource = 0;
		indiceSegTarget = 0;
		if (eS.hasMoreElements()) {
			vcour = (Vector<Object>) eS.nextElement();
			if (vcour.size() == 3) {
				idSegSourceCour = (String) vcour.elementAt(1);
			} else {
				idSegSourceCour = (String) vcour.elementAt(0);
			}
		} else {
			return res;
		}
		finiEs = false;
		if (eT.hasMoreElements()) {
			vcour = (Vector<Object>) eT.nextElement();
			if (vcour.size() == 3) {
				idTargetCour = (String) vcour.elementAt(1);
			} else {
				idTargetCour = (String) vcour.elementAt(0);
			}
		} else {
			return res;
		}
		finiEt = false;
		if (iAlign.hasNext()) {
			aCour = iAlign.next();
		} else {
			return res;
		}
		finiAlign = false;
		while ((!finiEs) && (!finiEt) && (!finiAlign)) {
			// System.out.println("dans la boucle : idSegSourceCour = "+idSegSourceCour);
			// System.out.println("idTargetCour = "+idTargetCour);
			// System.out.println("aCour = "+aCour);
			// on cherche le source dans l'alignement :
			if (aCour.getXmlIdSource().getUri().equals("")) { // paquet
				// on veut ce paquet
				temp = paquetOf(aCour.getIdSource(), true);
				idDebutAlignSource = temp.get(0);
				idFinAlignSource = temp.get(temp.size() - 1);
			} else { // id dans source :
				idDebutAlignSource = aCour.getIdSource();
				idFinAlignSource = idDebutAlignSource;
			}
			if (idDebutAlignSource.equals(idSegSourceCour)) { // on a trouve le
																// début :
				// System.out.println("Trouvé début\n");
				biCour = new BiPoint(0, 0, 0, 0);
				biCour.set1(indiceSegSource);
				// on cherche maintenant idFinAlignSource
				while (!idFinAlignSource.equals(idSegSourceCour)) {
					vcour = (Vector<Object>) eS.nextElement();
					indiceSegSource++;
					if (vcour.size() == 3) {
						idSegSourceCour = (String) vcour.elementAt(1);
					} else {
						idSegSourceCour = (String) vcour.elementAt(0);
					}
				}
				biCour.set2(indiceSegSource);
				// we found the source of the alignment, we look for the target
				// on a trouvé la source de l'alignement, on cherche la cible :
				if (aCour.getXmlIdTarget().getUri().equals("")) { // paquet
					temp = paquetOf(aCour.getIdCible(), false);
					idDebutAlignTarget = temp.get(0);
					idFinAlignTarget = temp.get(temp.size() - 1);
				} else { // id directement dans cible
					idDebutAlignTarget = aCour.getIdCible();
					idFinAlignTarget = idDebutAlignTarget;
				}
				// System.out.println("On cherche : "+idDebutAlignTarget);
				while (!idDebutAlignTarget.equals(idTargetCour)) {
					// System.out.println("idTargetCour = |"+idTargetCour+"|");
					// System.out.println("idDebutAlignTarget = |"+idDebutAlignTarget+"|");
					try {
						vcour = (Vector<Object>) eT.nextElement();
					} catch (final java.util.NoSuchElementException exp) {
						System.err
								.println("Unable to find "
										+ idDebutAlignTarget
										+ " in the target file. This is related to the alignment : "
										+ aCour.getFirstGeneratedFrom());
						System.exit(42);
					}
					indiceSegTarget++;
					if (vcour.size() == 3) {
						idTargetCour = (String) vcour.elementAt(1);
					} else {
						idTargetCour = (String) vcour.elementAt(0);
					}
				}
				biCour.set3(indiceSegTarget);
				while (!idFinAlignTarget.equals(idTargetCour)) {
					try {
						vcour = (Vector<Object>) eT.nextElement();
					} catch (final java.util.NoSuchElementException exp) {
						System.err
								.println("Unable to find "
										+ idFinAlignTarget
										+ " in the target file. This is related to the alignment : "
										+ aCour.getFirstGeneratedFrom());
						System.exit(42);
					}
					indiceSegTarget++;
					if (vcour.size() == 3) {
						idTargetCour = (String) vcour.elementAt(1);
					} else {
						idTargetCour = (String) vcour.elementAt(0);
					}
				}
				biCour.set4(indiceSegTarget);
				res.add(biCour);
				// we go on with the next alignment...
				// il faut maintenant qu'on passe à l'alignement suivant
				if (iAlign.hasNext()) {
					aCour = iAlign.next();
				} else {
					finiAlign = true;
				}
			} else if (XmlId.ordreDansTexte(idSegSourceCour,
					idDebutAlignSource, true) >= 0) {
				if (iAlign.hasNext()) {
					aCour = iAlign.next();
					if (aCour.getXmlIdSource().getUri().equals("")) { // paquet
						// on veut ce paquet
						temp = paquetOf(aCour.getIdSource(), true);
						idDebutAlignSource = temp.get(0);
						idFinAlignSource = temp.get(temp.size() - 1);
					} else { // id dans source :
						idDebutAlignSource = aCour.getIdSource();
						idFinAlignSource = idDebutAlignSource;
					}
				} else {
					finiAlign = true;
				}
			} else {
				// System.out.println("On avance dans segsSource...");
				if (eS.hasMoreElements()) {
					vcour = (Vector<Object>) eS.nextElement();
					indiceSegSource++;
					if (vcour.size() == 3) {
						idSegSourceCour = (String) vcour.elementAt(1);
					} else {
						idSegSourceCour = (String) vcour.elementAt(0);
					}
				} else {
					finiEs = true;
				}
			}
		}
		return res;
	}

	// on cherche les éléments de noCorresp qui apparaissent dans segs.
	// on renvoie une liste des ids de ceux qui sont présents dans segs.
	// on profite du fait que nocorresp est ordonnée de la même façon que
	// segs.
	private ArrayList<Integer> interNoCorresp(Vector<Object> segs,
			ArrayList<XmlId> nocorresp, boolean inSource) {
		final ArrayList<Integer> res = new ArrayList<Integer>();
		Enumeration<Object> e = segs.elements();
		Iterator<XmlId> i = nocorresp.iterator();
		String iCourSegs;
		String iCourCorresp;
		int indiceCour;
		Vector<Object> vcour;
		if (e.hasMoreElements()) {
			vcour = (Vector<Object>) e.nextElement();
			if (vcour.size() == 3) {
				iCourSegs = (String) vcour.elementAt(1);
			} else {
				iCourSegs = (String) vcour.elementAt(0);
			}
		} else {
			return res;
		}
		if (i.hasNext()) {
			iCourCorresp = i.next().getLocalName();
		} else {
			return res;
		}
		indiceCour = 0;
		e = segs.elements();
		i = nocorresp.iterator();
		// il faut parcourir les deux listes en parallèle.
		while (e.hasMoreElements() && i.hasNext()) {
			// System.out.println("iCourCorresp = "+iCourCorresp);
			// System.out.println("iCourSegs = "+iCourSegs);
			if (iCourCorresp.equals(iCourSegs)) {
				res.add(indiceCour);
				// on avance sur les deux :
				if (e.hasMoreElements()) {
					vcour = (Vector<Object>) e.nextElement();
					indiceCour++;
					if (vcour.size() == 3) {
						iCourSegs = (String) vcour.elementAt(1);
					} else {
						iCourSegs = (String) vcour.elementAt(0);
					}
				}
				if (i.hasNext()) {
					iCourCorresp = i.next().getLocalName();
				}
			} else {
				if (XmlId.ordreDansTexte(iCourSegs, iCourCorresp, inSource) <= 0) {
					if (e.hasMoreElements()) {
						vcour = (Vector<Object>) e.nextElement();
						indiceCour++;
						if (vcour.size() == 3) {
							iCourSegs = (String) vcour.elementAt(1);
						} else {
							iCourSegs = (String) vcour.elementAt(0);
						}
					}
				} else {
					if (i.hasNext()) {
						iCourCorresp = i.next().getLocalName();
					}
				}
			}
		}
		return res;
	}

	// dans le cas où id est un identifiant associé à un paquet
	// on renvoie la liste des éléments contenus dans ce paquet
	// dans le cas contraire, on renvoie un vecteur ne contenant que
	// l'id.
	private ArrayList<String> paquetOf(String id, boolean inSource) {
		Collection<Paquet> paquets;
		String paquetId;
		Paquet paquetCour;
		if (inSource) {
			paquets = paquetsSource;
		} else {
			paquets = paquetsCible;
		}
		for (final Object paquet : paquets) {
			paquetCour = (Paquet) paquet;
			paquetId = paquetCour.getId();
			if (paquetId.equals(id)) {
				return paquetCour.getContentWithoutUri();
			}
		}
		final ArrayList<String> res = new ArrayList<String>();
		res.add(id);
		return res;
	}

	// fusion de vecteurs de contenants :
	// on veut que le résultat soit ordonné des plus englobants
	// aux plus englobés.
	private Vector<Object> fusionOrdonneeContenants(Vector<Object> v1,
			Vector<Object> v2) {
		final Vector<Object> res = new Vector<Object>();
		String id1, id2;
		final Enumeration<Object> e1 = v1.elements();
		final Enumeration<Object> e2 = v2.elements();
		if (e1.hasMoreElements()) {
			id1 = (String) e1.nextElement();
		} else {
			while (e2.hasMoreElements()) {
				res.addElement(e2.nextElement());
			}
			return res;
		}
		if (e2.hasMoreElements()) {
			id2 = (String) e2.nextElement();
		} else {
			while (e1.hasMoreElements()) {
				res.addElement(e1.nextElement());
			}
			return res;
		}
		while (e1.hasMoreElements() || e2.hasMoreElements()) {
			if (higherIdThan(id1, id2)) {
				res.addElement(id1);
				if (e1.hasMoreElements()) {
					id1 = (String) e1.nextElement();
				} else {
					while (e2.hasMoreElements()) {
						res.addElement(e2.nextElement());
					}
				}
			} else {
				res.addElement(id2);
				if (e2.hasMoreElements()) {
					id2 = (String) e2.nextElement();
				} else {
					while (e1.hasMoreElements()) {
						res.addElement(e1.nextElement());
					}
				}
			}
		}
		return res;
	}

	// l'ordre entre deux id, on renvoie vrai si id1 contient id2
	private boolean higherIdThan(String id1, String id2) {
		return id2.startsWith(id1);
	}

	// l'ordre entre deux id, id1 apparaît dans le texte avant id2
	/*
	 * private boolean isLexSmallerThan(String id1, String id2){ return
	 * (id1.compareTo(id2) <= 0); };
	 */
	// pour énumérer sur deux vecteurs
	class myIterator<T> implements Iterator<T> {
		private final Iterator<T> e1;
		private final Iterator<T> e2;

		public myIterator(Iterator<T> en1, Iterator<T> en2) {
			e1 = en1;
			e2 = en2;
		}

		@Override
		public boolean hasNext() {
			return (e1.hasNext() || e2.hasNext());
		}

		@Override
		public T next() {
			if (e1.hasNext()) {
				return e1.next();
			}
			return e2.next();
		}

		@Override
		public void remove() {/* */
		}
	}

	private Iterator<Alignement> doubleIterator(Collection<Alignement> v1,
			Collection<Alignement> v2) {
		return new myIterator<Alignement>(v1.iterator(), v2.iterator());
	}
}
