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
 * @(#)       Div.java
 * 
 * 
 * Alignment at "div" level. 
 * 
 * @version   
 * @author    NGUYEN Thi Minh Huyen
 * Copyright  1999
 *
 */
package org.gramlab.core.loria.xsilfide.multialign;

import java.util.Vector;

@SuppressWarnings("unchecked")
class Div {
	private Vector<Object> Linking = new Vector<Object>(); // links of sentences within a text
	private Vector<Object> Links = new Vector<Object>(); // links of the two texts
	private final int spMax;
	private final int tpMax;
	private int cpSrc = 0;
	private int cpTar = 0;
	private int level = 0; // counter for saving the current level
	private Dist srcLengths, tarLengths; // lengths of divisions to align

	public Div(Vector<Object> divSrc, Vector<Object> divTar, Vector<Object> paraSrc, Vector<Object> paraTar,
			Vector<Object> stceSrc, Vector<Object> stceTar, Cognates cogn,
			LoadAndPrepareTexts lpt) {
		// B.G : normalement, divSrc, divTar, paraSrc, paraTar, stceSrc
		// stceTar sont des vecteurs contenant à la fois les id et les
		// longueurs.
		// nb1, nb2 = nombre de niveaux de divisions imbriquees
		// dans source et target.
		final int nb1 = divSrc.size(), nb2 = divTar.size();
		/*
		 * Algorithm: For each level in the tree of divisions, align the
		 * divisions of the source and target texts. Then get aligned pairs and
		 * continue to align the divisions at the next level corresponding to
		 * these pairs. In case a div has not children in the next level, we
		 * consider it as child of itself.
		 * 
		 * Ajout B.G le 13/12/2006 : à chaque niveau on veut prendre en compte
		 * des cognates. Cela signfie qu'il faut traduire les cognates
		 * concernant ce niveau de structures en contraintes de chemin.
		 */
		final Vector<Object> Src_Tar = new Vector<Object>(); // vector of pairs of src and tar
												// div to align
		Vector<Object> src = (Vector<Object>) divSrc.elementAt(0);
		Vector<Object> tar = (Vector<Object>) divTar.elementAt(0);
		final Vector<Object> tmp = new Vector<Object>();
		tmp.addElement(src);
		tmp.addElement(tar);
		Src_Tar.addElement(tmp);
		while ((level < nb1 - 1) || (level < nb2 - 1)) { // repeat for each
															// internal level
			Vector<Object> nextSrc = new Vector<Object>();
			Vector<Object> nextTar = new Vector<Object>();
			// MultiAlign.debug.println("au cours:"+Src_Tar.toString());
			final int size = Src_Tar.size();
			// get divisions at the next level in the src and the tar
			if (level < nb1 - 1)
				nextSrc = (Vector<Object>) divSrc.elementAt(level + 1);
			if (level < nb2 - 1)
				nextTar = (Vector<Object>) divTar.elementAt(level + 1);
			int cNextSrc = 0, cNextTar = 0; // repere in the next level
			System.out
					.println("\r                                                               ");
			System.out.print("\rDiv level " + (level + 1) + ": ");
			final int mod = (size < MultiAlign.NDOTS) ? 1
					: (size / MultiAlign.NDOTS);
			for (int count = 0; count < size; count++) {
				if (count % mod == 0)
					System.out.print(".");
				final Vector<Object> buf = (Vector<Object>) Src_Tar.elementAt(0);
				Src_Tar.removeElementAt(0);
				src = (Vector<Object>) buf.elementAt(0);
				tar = (Vector<Object>) buf.elementAt(1);
				final Path cur_path = alignDiv(src, tar, cogn);
				// MultiAlign.debug.println("Niveau "+level+":"+cur_path.toString());
				int cSrc = 0, cTar = 0; // counter for passing the src and tar
										// following the path
				for (int i = 0; i < cur_path.getNumberOfPoint(); i++) {
					final Point p = cur_path.getPointAt(i);
					final Vector<Object> newSrc = new Vector<Object>(), newTar = new Vector<Object>();
					// get divisions in the next level to align
					// In the source:
					for (int j = 0; j < p.x; j++) {
						// get the div contained in this point of path
						final Vector<Object> current_div = (Vector<Object>) src.elementAt(cSrc
								+ j);
						final String idOfDiv = current_div.elementAt(0)
								.toString();
						int index = new Integer(idOfDiv.substring(1,
								idOfDiv.length()));
						while (cNextSrc < nextSrc.size()) {
							// search children of current_div in the next level
							final Vector<Object> sdiv = (Vector<Object>) nextSrc
									.elementAt(cNextSrc);
							final String id = sdiv.elementAt(0).toString();
							// B.G. C'est évidemment faux !
							if (id.compareTo("d" + (index + 1)) == 0) {
								newSrc.addElement(sdiv);
								index++;
								cNextSrc++;
							} else
								break;
						}
						if (idOfDiv.compareTo("d" + index) == 0) // then this
																	// div has
																	// not child
							newSrc.addElement(current_div);
					}
					// In the target:
					for (int j = 0; j < p.y; j++) {
						// get the div contained in this point of path
						final Vector<Object> current_div = (Vector<Object>) tar.elementAt(cTar
								+ j);
						final String idOfDiv = current_div.elementAt(0)
								.toString();
						int index = new Integer(idOfDiv.substring(1,
								idOfDiv.length()));
						while (cNextTar < nextTar.size()) {
							// search children of current_div in the next level
							final Vector<Object> sdiv = (Vector<Object>) nextTar
									.elementAt(cNextTar);
							final String id = sdiv.elementAt(0).toString();
							// B.G. C'est évidemment faux ici aussi !
							if (id.compareTo("d" + (index + 1)) == 0) {
								newTar.addElement(sdiv);
								index++;
								cNextTar++;
							} else
								break;
						}
						if (idOfDiv.compareTo("d" + index) == 0) // then this
																	// div has
																	// not child
							newTar.addElement(current_div);
					}
					final Vector<Object> src_tar = new Vector<Object>();
					src_tar.addElement(newSrc);
					src_tar.addElement(newTar);
					Src_Tar.addElement(src_tar);
					cSrc += p.x;
					cTar += p.y;
				}
			}
			System.out.println();
			level++;
		} // end while level
			// MultiAlign.debug.println("Dernier niveau:"+Src_Tar.toString());
			// System.exit(1);
		spMax = paraSrc.size();
		tpMax = paraTar.size();
		// now we align the last level of division and step by paragraph level
		final int size = Src_Tar.size();
		for (int count = 0; count < Src_Tar.size(); count++) {
			System.out
					.print("\r                                                               ");
			System.out.print("\rAligning div's (" + (count + 1) + "/" + size
					+ "): ");
			// align each pair of division's vector
			final Vector<Object> src_tar = (Vector<Object>) Src_Tar.elementAt(count);
			// get source divisions and target divisions
			src = (Vector<Object>) src_tar.elementAt(0);
			tar = (Vector<Object>) src_tar.elementAt(1);
			// MultiAlign.debug.println(src_tar.toString());
			// MultiAlign.debug.println(src.toString()+"\n"+tar.toString());
			// align them
			final Path cur_path = alignDiv(src, tar, cogn);
			// MultiAlign.debug.println("group "+count+1+":"+cur_path.toString());
			// System.exit(1);
			int cSrc = 0, cTar = 0;
			for (int i = 0; i < cur_path.getNumberOfPoint(); i++) {
				final Point p = cur_path.getPointAt(i);
				// MultiAlign.debug.println(p.toString());
				// get the coresponding group of divisions
				// In the source:
				final Vector<Object> idSrcDiv = new Vector<Object>();
				for (int j = 0; j < p.x; j++) {
					final Vector<Object> buf = (Vector<Object>) src.elementAt(cSrc);
					final String idSrc = buf.elementAt(0).toString();
					idSrcDiv.addElement(idSrc);
					cSrc++;
				}
				// In the target:
				final Vector<Object> idTarDiv = new Vector<Object>();
				for (int j = 0; j < p.y; j++) {
					final Vector<Object> buf = (Vector<Object>) tar.elementAt(cTar);
					final String idTar = buf.elementAt(0).toString();
					idTarDiv.addElement(idTar);
					cTar++;
				}
				// MultiAlign.debug.println("here");
				// align these paragraphs
				// MultiAlign.debug.println(idSrcDiv.toString()+"\n"+idTarDiv.toString());
				// System.exit(1);
				alignParas(idSrcDiv, idTarDiv, paraSrc, paraTar, stceSrc,
						stceTar, cogn, lpt);
				// MultiAlign.debug.println(Linking.toString()+"\n");
				// MultiAlign.debug.println(Links.toString());
				// System.exit(1);
			}
			// System.exit(1);
		}
	}

	// getLenghts fabrique (effet de bord) srcLength et tarLength
	// qui sont des Dist c.a.d. des vecteurs d'entiers.
	// elle reçoit en entrée des trucs qui contiennent à la fois les id et
	// les longueurs.
	// Dans srcLength et tarLength on n'a plus que les longueurs.
	void getLengths(Vector<Object> Src, Vector<Object> Tar) {
		final int ns = Src.size(), nt = Tar.size();
		srcLengths = new Dist(ns);
		tarLengths = new Dist(nt);
		for (int i = 0; i < ns; i++) {
			int t = (Integer) ((Vector<Object>) Src.elementAt(i)).elementAt(1);
			if (level == 0)
				t /= 1000;
			else
				t /= 100;
			srcLengths.setDistAt(i, t);
		}
		for (int i = 0; i < nt; i++) {
			int t = (Integer) ((Vector<Object>) Tar.elementAt(i)).elementAt(1);
			if (level == 0)
				t /= 1000;
			else
				t /= 100;
			tarLengths.setDistAt(i, t);
		}
	}

	// B.G : normalement, Src et Tar contiennent des id et des longueurs.
	Path alignDiv(Vector<Object> Src, Vector<Object> Tar, Cognates cogn) {
		Path path;
		final ContraintesChemin cc = cogn.cognates2Chemins(Src, Tar);
		MultiAlign.debug("div source:" + Src.toString());
		MultiAlign.debug("div target:" + Tar.toString());
		getLengths(Src, Tar);
		path = Align.getPath(srcLengths, tarLengths, true, cc);
		MultiAlign.debug("path:" + path.toString());
		// System.exit(1);
		return path;
	}

	void alignParas(Vector<Object> idSrcDiv, Vector<Object> idTarDiv, Vector<Object> parSrc,
			Vector<Object> parTar, Vector<Object> stcSrc, Vector<Object> stcTar, Cognates cogn,
			LoadAndPrepareTexts lpt) {
		// MultiAlign.debug.println(idSrcDiv.toString()+"\n"+idTarDiv.toString());
		// MultiAlign.debug.println("here");
		final int ssMax = stcSrc.size(), tsMax = stcTar.size();
		final Vector<Object> pSource = new Vector<Object>(), pTarget = new Vector<Object>();
		Vector<Object> paraSrc = new Vector<Object>(), paraTar = new Vector<Object>();
		for (int i = 0; i < idSrcDiv.size(); i++) {
			final String idSrc = idSrcDiv.elementAt(i).toString();
			while (cpSrc < spMax) {
				if (!((Vector<Object>) parSrc.elementAt(cpSrc)).elementAt(0).toString()
						.startsWith(idSrc)) {
					paraSrc.addElement(parSrc.elementAt(cpSrc));
					cpSrc++;
				} else
					break;
			}
			pSource.addElement(paraSrc.clone());
			paraSrc.removeAllElements();
			while (cpSrc < spMax) {
				if (((Vector<Object>) parSrc.elementAt(cpSrc)).elementAt(0).toString()
						.startsWith(idSrc)) {
					paraSrc.addElement(parSrc.elementAt(cpSrc));
					cpSrc++;
				} else
					break;
			}
			pSource.addElement(paraSrc.clone());
			paraSrc.removeAllElements();
		}
		for (int i = 0; i < idTarDiv.size(); i++) {
			final String idTar = idTarDiv.elementAt(i).toString();
			while (cpTar < tpMax) {
				if (!((Vector<Object>) parTar.elementAt(cpTar)).elementAt(0).toString()
						.startsWith(idTar)) {
					paraTar.addElement(parTar.elementAt(cpTar));
					cpTar++;
				} else
					break;
			}
			pTarget.addElement(paraTar.clone());
			paraTar.removeAllElements();
			while (cpTar < tpMax) {
				if (((Vector<Object>) parTar.elementAt(cpTar)).elementAt(0).toString()
						.startsWith(idTar)) {
					paraTar.addElement(parTar.elementAt(cpTar));
					cpTar++;
				} else
					break;
			}
			pTarget.addElement(paraTar.clone());
			paraTar.removeAllElements();
		}
		// MultiAlign.debug.println("Niveau para:"+paraSrc.toString());
		// MultiAlign.debug.println(paraTar.toString());
		if (pSource.size() == pTarget.size()) {
			for (int i = 0; i < pSource.size(); i++) {
				paraSrc = (Vector<Object>) pSource.elementAt(i);
				paraTar = (Vector<Object>) pTarget.elementAt(i);
				new Paragraphes(paraSrc, paraTar, stcSrc, stcTar, ssMax, tsMax,
						cogn, lpt);
				// MultiAlign.debug.println("Links: " + prgph.Links.toString());
				// get links of prgph
				Links = Paragraphes.Links;
				// get linkings of prgph
				Linking = Paragraphes.Linking;
			}
		} else {
			for (int i = 0; i < pSource.size(); i++) {
				final Vector<Object> tmp = (Vector<Object>) pSource.elementAt(i);
				for (int j = 0; j < tmp.size(); j++) {
					paraSrc.addElement(tmp.elementAt(j));
				}
			}
			for (int i = 0; i < pTarget.size(); i++) {
				final Vector<Object> tmp = (Vector<Object>) pTarget.elementAt(i);
				for (int j = 0; j < tmp.size(); j++) {
					paraTar.addElement(tmp.elementAt(j));
				}
			}
			new Paragraphes(paraSrc, paraTar, stcSrc, stcTar, ssMax, tsMax,
					cogn, lpt);
			// MultiAlign.debug.println("Links: " + prgph.Links.toString());
			// get links of prgph
			Links = Paragraphes.Links;
			// get linkings of prgph
			Linking = Paragraphes.Linking;
		}
	}

	public Vector<Object> getLinking() {
		return Linking;
	}

	public Vector<Object> getLinks() {
		return Links;
	}
}
