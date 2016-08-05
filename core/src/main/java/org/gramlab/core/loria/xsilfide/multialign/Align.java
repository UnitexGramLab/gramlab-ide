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
 * @(#)       Align.java
 * 
 * 
 * 
 * 
 * @version   
 * @author    Patrice Bonhomme
 * Copyright  1997 (C) PATRICE BONHOMME
 *            CRIN/CNRS & INRIA Lorraine
 *
 * Modifications : Bertrand.Gaiffe@atilf.fr
 *   ajout de contraintes sur les chemins pour prendre en compte des 
 *   "cognates".
 */
/* commentaire bidon pour test  et j'en rajoute !*/
package org.gramlab.core.loria.xsilfide.multialign;

// Rqs bertrand gaiffe :
//    On calcule ici toute la matrice de programmation dynamique ->
//    cout au moins quadratique. On pourrait ne faire qu'une bande autour
//    de la diagonale (c'est le truc classique !)
// l'implementation est exactement celle de Gale et Church...
// elle etait en C. On pourrait refaire ca dans un style plus Java !
// enfin, des cognates sont des points de passage oblige dans la matrice
// on peut donc, soit laisser l'algo tel quel mais n'extraire que des 
// chemins passant par les cognates (pas simple !). 
// soit au contraire segmenter la programmation dynamique pour passer 
// par les cognates.
class Align {
	private static final int BADTYPE = 0;
	static public final int DESTRUCTION = 1;
	static public final int SUBSTITUTION = 2;
	static public final int INSERTION = 3;
	static public final int CONTRACTION = 4;
	static public final int MELANGE = 5;
	static public final int EXPANSION = 6;
	private static final int BIGDESTRUCTION = 7;
	private static final int BIGINSERTION = 8;

	public int getType(Point al) {
		if ((al.x == 1) && (al.y == 0))
			return (DESTRUCTION);
		else if ((al.x == 1) && (al.y == 1))
			return (SUBSTITUTION);
		else if ((al.x == 0) && (al.y == 1))
			return (INSERTION);
		else if ((al.x == 2) && (al.y == 1))
			return (CONTRACTION);
		else if ((al.x == 1) && (al.y == 2))
			return (EXPANSION);
		else if ((al.x > 1) && (al.y == 0))
			return (BIGDESTRUCTION);
		else if ((al.x == 0) && (al.y > 1))
			return (BIGINSERTION);
		else if ((al.x == 2) && (al.y == 2))
			return (MELANGE);
		else
			return (BADTYPE);
	}

	public static Path getPath(Dist x, Dist y) {
		return getPath(x, y, false,
				new ContraintesChemin(x.getSize(), y.getSize()));
	}

	public static Path getPath(Dist x, Dist y, boolean para_level,
			ContraintesChemin contraintes) {
		// para_level=true if we align at paragraph level, false for sentence
		// level
		Matrix dist;
		int maxi, maxj;
		Path path;
		Path tmpPath;
		int[][] pathX;
		int[][] pathY;
		int n;
		int i, j, oi, oj, di, dj;
		int d1, d2, d3, d4, d5, d6, dmin;
		maxi = x.getSize() + 1;
		maxj = y.getSize() + 1;
		dist = new Matrix(maxi, maxj); // la matrice pour la
		// programmation dynamique
		if (maxi > 2 * maxj || maxj > 2 * maxi) {
			path = new Path(1, 1, 1);
			final Point p = new Point(maxi - 1, maxj - 1);
			path.setPointAt(p, 0);
			return (path);
		}
		// if(!para_level)
		// System.out.println(maxi + " x " + maxj);
		pathX = new int[maxi][maxj];
		pathY = new int[maxi][maxj];
		final int mod = (maxj < MultiAlign.NDOTS) ? 1
				: (maxj / MultiAlign.NDOTS);
		// calcul de la matrice des couts.
		for (j = 0; j < maxj; j++) {
			if (j % mod == 0)
				System.out.print(".");
			for (i = 0; i < maxi; i++) {
				// prise en compte des "noCorresp". B.G 20/12/2006
				if ((contraintes != null) && contraintes.getIgnoreSource(i)) {
					dist.setElem(i, j, dist.getElem(i - 1, j));
					pathX[i][j] = i - 1;
					pathY[i][j] = j;
					continue; // OUI, c'est mal !
				}
				if ((contraintes != null) && contraintes.getIgnoreTarget(j)) {
					dist.setElem(i, j, dist.getElem(i, j - 1));
					pathX[i][j] = i;
					pathY[i][j] = j - 1;
					continue; // OUI, c'est mal !
				}
				// B.G. 15/12/2006
				// Si on a un chemin special en i,j on courcicuite !
				if ((contraintes != null)
						&& contraintes.getCheminSpecial(i, j) != null) {
					final Point p = contraintes.getCheminSpecial(i, j);
					// ca ne coûte rien de reprendre la distance !
					dist.setElem(i, j, dist.getElem(i - p.getX(), j - p.getY()));
					pathX[i][j] = i - p.getX();
					pathY[i][j] = j - p.getY();
				} else { // chemin special
					// B.G 5 decembre 2006
					// A-t-on un cout impose pour ce point ?
					// si oui, on n'a pas d'autre calcul à faire...
					if ((contraintes != null) && contraintes.getCout(i, j) != 0) {
						dmin = Integer.MAX_VALUE;
						d1 = d2 = d3 = d4 = d5 = d6 = Integer.MAX_VALUE;
						// System.out.println("Cout imposé en "+i+", "+j+"\n");
					} else { // cout force
						// B.G. 5 décembre 2006
						// A-t-on des contraintes sur la forme du chemin ?
						// if (contraintes.getCheminsForces(i, j) != null){
						// System.out.println("contraintes chemin en "+i+", "+j+" = "+contraintes.getCheminsForces(i,
						// j));
						// }
						d1 = i > 0
								&& j > 0
								&& ((contraintes == null) || contraintes
										.substitutionAutorisee(i, j))
								&& (dist.getElem(i - 1, j - 1) != Integer.MAX_VALUE) ?
						// substitution
						dist.getElem(i - 1, j - 1)
								+ Dist.TwoSideDistance(x.getDistAt(i - 1),
										y.getDistAt(j - 1), 0, 0)
								: Integer.MAX_VALUE;
						d2 = i > 0
								&& ((contraintes == null) || contraintes
										.destructionAutorisee(i, j))
								&& (dist.getElem(i - 1, j) != Integer.MAX_VALUE) ?
						// deletion
						dist.getElem(i - 1, j)
								+ Dist.TwoSideDistance(x.getDistAt(i - 1), 0,
										0, 0)
								: Integer.MAX_VALUE;
						d3 = j > 0
								&& ((contraintes == null) || contraintes
										.insertionAutorisee(i, j))
								&& (dist.getElem(i, j - 1) != Integer.MAX_VALUE) ?
						// insertion
						dist.getElem(i, j - 1)
								+ Dist.TwoSideDistance(0, y.getDistAt(j - 1),
										0, 0)
								: Integer.MAX_VALUE;
						d4 = i > 1
								&& j > 0
								&& ((contraintes == null) || contraintes
										.contractionAutorisee(i, j))
								&& (dist.getElem(i - 2, j - 1) != Integer.MAX_VALUE) ?
						// contraction
						dist.getElem(i - 2, j - 1)
								+ Dist.TwoSideDistance(x.getDistAt(i - 2),
										y.getDistAt(j - 1), x.getDistAt(i - 1),
										0)
								: Integer.MAX_VALUE;
						d5 = i > 0
								&& j > 1
								&& ((contraintes == null) || contraintes
										.expansionAutorisee(i, j))
								&& (dist.getElem(i - 1, j - 2) != Integer.MAX_VALUE) ?
						// expansion
						dist.getElem(i - 1, j - 2)
								+ Dist.TwoSideDistance(x.getDistAt(i - 1),
										y.getDistAt(j - 2), 0,
										y.getDistAt(j - 1))
								: Integer.MAX_VALUE;
						d6 = i > 1
								&& j > 1
								&& ((contraintes == null) || contraintes
										.melangeAutorise(i, j))
								&& (dist.getElem(i - 2, j - 2) != Integer.MAX_VALUE) ?
						// melding
						dist.getElem(i - 2, j - 2)
								+ Dist.TwoSideDistance(x.getDistAt(i - 2),
										y.getDistAt(j - 2), x.getDistAt(i - 1),
										y.getDistAt(j - 1))
								: Integer.MAX_VALUE;
						dmin = d1;
						if (d2 < dmin)
							dmin = d2;
						if (d3 < dmin)
							dmin = d3;
						if (d4 < dmin)
							dmin = d4;
						if (d5 < dmin)
							dmin = d5;
						if (d6 < dmin)
							dmin = d6;
					}
					// System.out.println(i+", "+j+" : "+d1+" "+d2+" "+d3+" "+d4+" "+d5+" "+d6);
					if ((i == 0) && (j == 0)) {
						dist.setElem(0, 0, 0);
						pathX[0][0] = 0;
						pathY[0][0] = 0;
					} else if (dmin == Integer.MAX_VALUE) {
						// dist.setElem(i, j, 0); // Pourquoi ?
						dist.setElem(i, j, dmin); // Pourquoi ?
						pathX[i][j] = 0;
						pathY[i][j] = 0;
						// System.out.println("dmin="+dmin+" ");
					} else if (dmin == d1) {
						dist.setElem(i, j, d1);
						pathX[i][j] = i - 1;
						pathY[i][j] = j - 1;
						// System.out.println("d1="+d1+" ");
					} else if (dmin == d2) {
						dist.setElem(i, j, d2);
						pathX[i][j] = i - 1;
						pathY[i][j] = j;
						// System.out.println("d2="+d2+" ");
					} else if (dmin == d3) {
						dist.setElem(i, j, d3);
						pathX[i][j] = i;
						pathY[i][j] = j - 1;
						// System.out.println("d3="+d3+" ");
					} else if (dmin == d4) {
						dist.setElem(i, j, d4);
						pathX[i][j] = i - 2;
						pathY[i][j] = j - 1;
						// System.out.println("d4="+d4+" ");
					} else if (dmin == d5) {
						dist.setElem(i, j, d5);
						pathX[i][j] = i - 1;
						pathY[i][j] = j - 2;
						// System.out.println("d5="+d5+" ");
					} else /* dmin == d6 */{
						dist.setElem(i, j, d6);
						pathX[i][j] = i - 2;
						pathY[i][j] = j - 2;
						// System.out.println("d6="+d6+" ");
					}
					// System.out.println(i+", "+j+": "+pathX[i][j]+"  "+pathY[i][j]+"\n");
				}
			}
		}
		// Extraction du meilleur chemin.
		n = 0;
		tmpPath = new Path(maxi, maxj, maxi + maxj);
		for (i = maxi - 1, j = maxj - 1; i > 0 || j > 0; i = oi, j = oj) {
			oi = pathX[i][j];
			oj = pathY[i][j];
			di = i - oi;
			dj = j - oj;
			if (di == 1 && dj == 1) { // substitution
				tmpPath.setPointAt(1, 1, n);
				// System.out.println(tmpPath.toString());
			} else if (di >= 1 && dj == 0) { // deletion
				tmpPath.setPointAt(di, 0, n);
				// System.out.println(tmpPath.toString());
			} else if (di == 0 && dj >= 1) { // insertion
				tmpPath.setPointAt(0, dj, n);
				// System.out.println(tmpPath.toString());
			} else if (dj == 1) { // contraction
				tmpPath.setPointAt(2, 1, n);
				// System.out.println(tmpPath.toString());
			} else if (di == 1) { // expansion
				tmpPath.setPointAt(1, 2, n);
				// System.out.println(tmpPath.toString());
			} else if (di == 2 && dj == 2) {// melding
				tmpPath.setPointAt(2, 2, n);
				// System.out.println(tmpPath.toString());
			} else
				tmpPath.setPointAt(di, dj, n);
			n++;
		}
		// System.out.println("tmpPath = "+tmpPath+"\n");
		if (para_level)
			MultiAlign.debug("tmpPath=" + tmpPath.toString());
		path = new Path(maxi, maxj, n);
		if (!para_level) {
			for (i = n - 1; i >= 0; i--) {
				path.setPointAt(tmpPath.getPointAt(i), n - i - 1);
			}
		} // end for the cas for align sentences
		else {
			int memX = 0, memY = 0;
			int idx = n - 1;
			Point p = new Point();
			if (idx >= 0)
				do {
					p = tmpPath.getPointAt(idx);
					if (p.x == 0 || p.y == 0) {
						memX += p.x;
						memY += p.y;
						idx--;
					}
				} while ((p.x == 0 || p.y == 0) && (idx >= 0));
			int count = 0;
			if ((idx >= 0) && ((memX > 0) || memY > 0)) {
				p = tmpPath.getPointAt(idx);
				p.x += memX;
				p.y += memY;
				path.setPointAt(p, count);
				count++;
				idx--;
			}
			while (idx >= 0) {
				p = tmpPath.getPointAt(idx);
				idx--;
				if (p.x == 0 || p.y == 0) {
					final Point q = path.getPointAt(count - 1);
					q.x += p.x;
					q.y += p.y;
					// path.setPointAt(q,count-1);
					// System.out.println("path="+path.toString());
				} else {
					path.setPointAt(p, count);
					count++;
				}
			}
		}
		// System.out.println("Path: "+path.toString());
		return (path);
	}
}
// EOF
