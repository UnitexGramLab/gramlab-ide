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
 * @(#) ContraintesChemin.java
 *
 * @version
 * @author Bertrand Gaiffe
 * Copyright 2006 (C) Bertrand Gaiffe
 *               ATILF & Loria
 */
/* 23/12 tentative de remplacement par une Hastable -> pas terrible.
 le truc à tenter : en faire une variable globale (initialisée à
 la taille nécessaire au pire dans LoadAndPrepareTexts) et profiter
 du parcours dans Align.java pour remettre les contraintes à blanc
 après utilisation (pour ne pas payer à chaque fois une initialisation


 Pour l'instant, le pb avec la hastable est qu'on en retrouve pas nb nos
 petits ! (on indexe par des points en faisant des new Point...)
 */
package org.gramlab.core.loria.xsilfide.multialign;

import java.util.Enumeration;
import java.util.Vector;

// les coûts forcés sont une table qui à i et j associent un coût
// Soit le cout est 0, soit c'est Integer.MAX_VALUE. Il faudrait donc mieux
// en faire un tableau de booleens...
// le chemins forcés sont une table qui à i et j associent une liste
// de formes de chemins. 
// Hastable.get(clef) -> valeur
// Hastable.keys() -> énumeration des clefs...
// Hastable.put(clef, valeur).
class ContraintesPoint {
	private boolean coutForce;

	public ContraintesPoint() {
		coutForce = false;
	}

	public void setCoutForce() {
		coutForce = true;
	}

	public boolean getCoutForce() {
		return coutForce;
	}
}

@SuppressWarnings("unchecked")
public class ContraintesChemin {
	private final int[][] coutsForces;
	private final Vector<Object>[][] cheminsForces; // c'est un ou logique entre
													// ces
	// chemins...
	private final Point[][] cheminSpecial; // un seul possible.
	// private Hashtable table;
	private final ContraintesPoint[][] table;
	private final boolean[] ignorerDansSource;
	private final boolean[] ignorerDansCible;

	/*
	 * public ContraintesChemin(int coutF[][], Vector cheminF[][]){ int i =
	 * coutF.length; int j = coutF[0].length;
	 * 
	 * coutsForces = coutF; cheminsForces = cheminF; cheminSpecial = new
	 * Point[i+1][j+1]; // je ne sais pas comment faire autrement ! for (int x =
	 * 0; x < i; x++){ for (int y = 0; y < j; y++){ cheminSpecial[i][j] = null;
	 * } } ignorerDansSource = new boolean[i+1]; for (int x = 0; x <= i; i++){
	 * ignorerDansSource[i] = false; } ignorerDansCible = new boolean[j+1]; for
	 * (int y = 0; y <= j; y++){ ignorerDansCible[y] = false; } }
	 */
	public ContraintesChemin(int i, int j) {
		int i1, j1;
		// System.out.println("i = "+i+" j = "+j);
		coutsForces = new int[i + 1][j + 1];
		for (i1 = 0; i1 < i; i1++)
			for (j1 = 0; j1 < j; j1++)
				coutsForces[i1][j1] = 0;
		cheminsForces = new Vector[i + 1][j + 1];
		cheminSpecial = new Point[i + 1][j + 1];
		// je ne sais pas comment faire autrement !
		for (int x = 0; x < i + 1; x++) {
			for (int y = 0; y < j + 1; y++) {
				cheminSpecial[i][j] = null;
			}
		}
		ignorerDansSource = new boolean[i + 1];
		for (int x = 0; x <= i; x++) {
			ignorerDansSource[x] = false;
		}
		ignorerDansCible = new boolean[j + 1];
		for (int y = 0; y <= j; y++) {
			ignorerDansCible[y] = false;
		}
		// System.out.println("i = "+i+" j = "+j);
		table = new ContraintesPoint[i + 1][j + 1];
	}

	/*
	 * public ContraintesChemin(int i, int j){ ignorerDansSource = new
	 * boolean[i+1]; for (int x = 0; x <= i; i++){ ignorerDansSource[x] = false;
	 * } ignorerDansCible = new boolean[j+1]; for (int y = 0; y <= j; y++){
	 * ignorerDansCible[y] = false; } table = new Hashtable(); }
	 */
	public int getCout(int i, int j) {
		int res1, res2;
		res1 = coutsForces[i][j];
		final ContraintesPoint cp = table[i][j];
		if ((cp == null) || (!cp.getCoutForce())) {
			res2 = 0;
		} else {
			res2 = Integer.MAX_VALUE;
		}
		if (res1 != res2) {
			System.err.println("res1(" + res1 + ") != res2(" + res2 + ")\n");
			// System.exit(1);
		}
		return res1;
	}

	/*
	 * public int getCout(int i, int j){ ContraintesPoint cp =
	 * (ContraintesPoint)table.get(new Point(i, j));
	 * 
	 * if ((cp == null) ||(!cp.coutForce)){ return 0; } else{ return
	 * Integer.MAX_VALUE; } };
	 */
	public void addCoutForce(int i, int j) {
		coutsForces[i][j] = Integer.MAX_VALUE;
		// Point ij = new Point(i, j);
		ContraintesPoint cp = table[i][j];
		if (cp == null) {
			cp = new ContraintesPoint();
			// cp.setCoutForce();
			table[i][j] = cp;
		}
		cp.setCoutForce();
	}

	/*
	 * public void addCoutForce(int i, int j){ Point ij = new Point(i, j);
	 * ContraintesPoint cp = (ContraintesPoint)table.get(ij);
	 * 
	 * if (cp == null){ cp = new ContraintesPoint(); table.put(ij, cp); }
	 * cp.setCoutForce();
	 * 
	 * };
	 */
	public void addCheminForce(int i, int j, int typeCh) {
		if ((typeCh != Align.DESTRUCTION) && (typeCh != Align.SUBSTITUTION)
				&& (typeCh != Align.INSERTION) && (typeCh != Align.CONTRACTION)
				&& (typeCh != Align.MELANGE) && (typeCh != Align.EXPANSION)) {
			System.err.println("Bad constraint on paths\n");
			System.exit(1);
		}
		if (cheminsForces[i][j] == null) {
			cheminsForces[i][j] = new Vector<Object>();
		}
		cheminsForces[i][j].addElement(typeCh);
		// Attention : les chemins forcés ne valent que si le meilleur chemin
		// passe par (i,j).
	}

	// l'idée est que dès qu'on spécifie un chemin forcé, c'est que
	// les autres solution sont interdites...
	/*
	 * public void addCheminForce(int i, int j, int typeCh){ Point ij = new
	 * Point(i, j); ContraintesPoint cp = (ContraintesPoint)table.get(new
	 * Point(i, j));
	 * 
	 * if (cp == null){ cp = new ContraintesPoint(); table.put(ij, cp); }; if
	 * (!cp.cheminForceUsed){ cp.cheminForceDestruction = false;
	 * cp.cheminForceSubstitution = false; cp.cheminForceInsertion = false;
	 * cp.cheminForceContraction = false; cp.cheminForceMelange = false;
	 * cp.cheminForceExpansion = false; cp.cheminForceUsed = true; }
	 * 
	 * switch (typeCh){ case Align.DESTRUCTION : cp.cheminForceDestruction =
	 * true; break; case Align.SUBSTITUTION : cp.cheminForceSubstitution = true;
	 * break; case Align.INSERTION : cp.cheminForceInsertion = true; break; case
	 * Align.CONTRACTION : cp.cheminForceContraction = true; break; case
	 * Align.MELANGE : cp.cheminForceMelange = true; break; case Align.EXPANSION
	 * : cp.cheminForceExpansion = true; break; default : {
	 * System.err.println("Mauvaise contrainte de chemin\n"); System.exit(1); };
	 * break;
	 * 
	 * 
	 * } }
	 */
	public void setCheminSpecial(int i, int j, int incrX, int incrY) {
		System.out.println("setCheminSpecial(" + i + ", " + j + ", " + incrX
				+ ", " + incrY + ")\n");
		cheminSpecial[i][j] = new Point(incrX, incrY);
	}

	/*
	 * public void setCheminSpecial(int i, int j, int incrX, int incrY){ Point
	 * ij = new Point(i, j); Point increments = new Point(incrX, incrY);
	 * ContraintesPoint cp = (ContraintesPoint)table.get(ij);
	 * 
	 * if (cp == null){ cp = new ContraintesPoint(); table.put(ij, cp); };
	 * cp.cheminSpecial = increments;
	 * 
	 * };
	 */
	public Point getCheminSpecial(int i, int j) {
		return cheminSpecial[i][j];
	}

	/*
	 * public Point getCheminSpecial(int i, int j){ ContraintesPoint cp =
	 * (ContraintesPoint)table.get(new Point(i, j));
	 * 
	 * if (cp == null){ return null; } else{ return cp.cheminSpecial; } };
	 */
	public void interdireChemin(int i, int j, int typeCh) {
		if ((typeCh != Align.DESTRUCTION) && (typeCh != Align.SUBSTITUTION)
				&& (typeCh != Align.INSERTION) && (typeCh != Align.CONTRACTION)
				&& (typeCh != Align.MELANGE) && (typeCh != Align.EXPANSION)) {
			System.err.println("Bad constraint on path\n");
			System.exit(1);
		}
		if (cheminsForces[i][j] == null) {
			cheminsForces[i][j] = new Vector<Object>();
			cheminsForces[i][j].addElement(Align.DESTRUCTION);
			cheminsForces[i][j].addElement(Align.SUBSTITUTION);
			cheminsForces[i][j].addElement(Align.INSERTION);
			cheminsForces[i][j].addElement(Align.CONTRACTION);
			cheminsForces[i][j].addElement(Align.MELANGE);
			cheminsForces[i][j].addElement(Align.EXPANSION);
		}
		for (int i1 = cheminsForces[i][j].size() - 1; i1 >= 0; i1--) {
			if (cheminsForces[i][j].elementAt(i1).equals(typeCh)) {
				cheminsForces[i][j].removeElementAt(i1);
				return;
			}
		}
	}

	/*
	 * public void interdireChemin(int i, int j, int typeCh){ Point ij = new
	 * Point(i, j); ContraintesPoint cp = (ContraintesPoint)table.get(ij);
	 * 
	 * if (cp == null){ cp = new ContraintesPoint(); table.put(ij, cp); };
	 * cp.cheminForceUsed = true; switch (typeCh){ case Align.DESTRUCTION :
	 * cp.cheminForceDestruction = false; break; case Align.SUBSTITUTION :
	 * cp.cheminForceSubstitution = false; break; case Align.INSERTION :
	 * cp.cheminForceInsertion = false; break; case Align.CONTRACTION :
	 * cp.cheminForceContraction = false; break; case Align.MELANGE :
	 * cp.cheminForceMelange = false; break; case Align.EXPANSION :
	 * cp.cheminForceExpansion = false; break; default : {
	 * System.err.println("Mauvaise contrainte de chemin\n"); System.exit(1); };
	 * break; }
	 * 
	 * };
	 */
	public void setCheminsForces(int i, int j, Vector<Object> v) {
		for (final Enumeration<Object> e = v.elements(); e.hasMoreElements();) {
			addCheminForce(i, j, (Integer) e.nextElement());
		}
	}

	private boolean member(int x, Vector<Object> v) {
		for (final Enumeration<Object> e = v.elements(); e.hasMoreElements();) {
			if (x == (Integer) e.nextElement()) {
				return true;
			}
		}
		return false;
	}

	public boolean destructionAutorisee(int i, int j) {
		return ((cheminsForces[i][j] == null)
				|| (cheminsForces[i][j].size() == 0) || member(
					Align.DESTRUCTION, cheminsForces[i][j]));
	}

	/*
	 * public boolean destructionAutorisee(int i, int j){ Point ij = new
	 * Point(i, j); ContraintesPoint cp = (ContraintesPoint)table.get(ij);
	 * return ((cp == null) || cp.cheminForceDestruction);
	 * 
	 * }
	 */
	public boolean substitutionAutorisee(int i, int j) {
		return ((cheminsForces[i][j] == null)
				|| (cheminsForces[i][j].size() == 0) || member(
					Align.SUBSTITUTION, cheminsForces[i][j]));
	}

	/*
	 * public boolean substitutionAutorisee(int i, int j){ Point ij = new
	 * Point(i, j); ContraintesPoint cp = (ContraintesPoint)table.get(ij);
	 * return ((cp == null) || cp.cheminForceSubstitution); }
	 */
	public boolean insertionAutorisee(int i, int j) {
		return ((cheminsForces[i][j] == null)
				|| (cheminsForces[i][j].size() == 0) || member(Align.INSERTION,
					cheminsForces[i][j]));
	}

	/*
	 * public boolean insertionAutorisee(int i, int j){ Point ij = new Point(i,
	 * j); ContraintesPoint cp = (ContraintesPoint)table.get(ij); return ((cp ==
	 * null) || cp.cheminForceInsertion); }
	 */
	public boolean contractionAutorisee(int i, int j) {
		return (((cheminsForces[i][j] == null) || cheminsForces[i][j].size() == 0) || member(
				Align.CONTRACTION, cheminsForces[i][j]));
	}

	/*
	 * public boolean contractionAutorisee(int i, int j){ Point ij = new
	 * Point(i, j); ContraintesPoint cp = (ContraintesPoint)table.get(ij);
	 * return ((cp == null) || cp.cheminForceContraction); }
	 */
	public boolean melangeAutorise(int i, int j) {
		return ((cheminsForces[i][j] == null)
				|| (cheminsForces[i][j].size() == 0) || member(Align.MELANGE,
					cheminsForces[i][j]));
	}

	/*
	 * public boolean melangeAutorise(int i, int j){ Point ij = new Point(i, j);
	 * ContraintesPoint cp = (ContraintesPoint)table.get(ij); return ((cp ==
	 * null) || cp.cheminForceMelange); }
	 */
	public boolean expansionAutorisee(int i, int j) {
		return ((cheminsForces[i][j] == null)
				|| (cheminsForces[i][j].size() == 0) || member(Align.EXPANSION,
					cheminsForces[i][j]));
	}

	/*
	 * public boolean expansionAutorisee(int i, int j){ Point ij = new Point(i,
	 * j); ContraintesPoint cp = (ContraintesPoint)table.get(ij); return ((cp ==
	 * null) || cp.cheminForceExpansion); }
	 */
	public void setIgnoreSource(int i) {
		ignorerDansSource[i] = true;
	}

	public void setIgnoreTarget(int j) {
		ignorerDansCible[j] = true;
	}

	public Vector<Object> getCheminsForces(int i, int j) {
		return cheminsForces[i][j];
	}

	public boolean getIgnoreSource(int i) {
		return ignorerDansSource[i];
	}

	public boolean getIgnoreTarget(int j) {
		return ignorerDansCible[j];
	}
}
