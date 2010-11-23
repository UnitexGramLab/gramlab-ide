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
 * @(#)       Paragraphes.java
 * 
 * 
 * Alignment at "paragraph" level
 * 
 * @version   
 * @author    NGUYEN Thi Minh Huyen
 * Copyright  1999
 *
 */

package fr.loria.xsilfide.multialign;

import java.util.Vector;


@SuppressWarnings("unchecked")
class Paragraphes {

    private Dist srcLengths, tarLengths; //lengths of paragraphs

    public static Vector Linking = new Vector(); //links of sentences within a test
    public static Vector Links = new Vector(); //links of the two texts

    // csSrc,csTar: counter for marking current position in sentence's vectors
    private static int csSrc = 0;
    private static int csTar = 0;

    public Paragraphes(Vector paraSrc, Vector paraTar, Vector stceSrc, Vector stceTar, int sMax, int tMax, Cognates cogn, LoadAndPrepareTexts lpt) {
        int nbSrc = paraSrc.size();
        int nbTar = paraTar.size();
        ContraintesChemin cc;

        int ratio = nbSrc / 300;

        if (ratio > 1) {
            int cSrc = 0, cTar = 0;
            int lgSrc = 0, lgTar = 0;

            for (int i = 0; i < nbSrc; i++) {
                lgSrc += (Integer) (((Vector) paraSrc.elementAt(i)).elementAt(1));
            }

            for (int i = 0; i < nbTar; i++) {
                lgTar += (Integer) (((Vector) paraTar.elementAt(i)).elementAt(1));
            }

            int ratioSrc = lgSrc / ratio, ratioTar = lgTar / ratio;

            while (true) {
                Vector tmpSrc = new Vector();
                Vector tmpTar = new Vector();
                int tmpLgSrc = 0, tmpLgTar = 0;

                while ((cSrc < nbSrc) && (tmpLgSrc < ratioSrc)) {
                    Vector buf = (Vector) paraSrc.elementAt(cSrc);
                    cSrc++;
                    tmpLgSrc += (Integer) buf.elementAt(1);
                    tmpSrc.addElement(buf);
                }

                while ((cTar < nbTar) && (tmpLgTar < ratioTar)) {
                    Vector buf = (Vector) paraTar.elementAt(cTar);
                    cTar++;
                    tmpLgTar += (Integer) buf.elementAt(1);
                    tmpTar.addElement(buf);
                }

                getLengths(tmpSrc, tmpTar); //get lengths of paragraphs in the src & tar


                cc = cogn.cognates2Chemins(tmpSrc, tmpTar);
                //cc = cogn.cognates2Chemins(paraSrc, paraTar);
                System.out.println("tmpSrc = " + tmpSrc);
                System.out.println("tmpTar = " + tmpTar);
                System.out.println("cognates =" + cogn);
                System.out.println("ContrainteChemin = " + cc);
                Path path = Align.getPath(srcLengths, tarLengths, true, cc);

                boolean debug = false;
                if (debug) {
                    System.out.println("Chemin paragraphes = "); //+path);
                    // On voudrait ce chemin de façon lisible !
                    int ptrSrc, ptrTar;
                    ptrSrc = 0;
                    ptrTar = 0;
                    Point pt;
                    for (int i = 0; i < path.getNumberOfPoint(); i++) {
                        pt = path.getPointAt(i);
                        for (int j = 0; j < pt.getX(); j++) {
                            System.out.print(paraSrc.elementAt(ptrSrc++));
                        }
                        System.out.print("<--->");
                        for (int j = 0; j < pt.getY(); j++) {
                            System.out.print(paraTar.elementAt(ptrTar++));
                        }
                        System.out.println("");
                    }
                }
                alignSentences(tmpSrc, tmpTar, path, stceSrc, stceTar, sMax, tMax,
                        cogn, lpt);

                if (cSrc >= nbSrc || cTar >= nbTar) break;
            }
        } else {
            getLengths(paraSrc, paraTar); //get lengths of paragraphs in the src & tar
            cc = cogn.cognates2Chemins(paraSrc, paraTar);
            Path path = Align.getPath(srcLengths, tarLengths, true, cc);

            alignSentences(paraSrc, paraTar, path, stceSrc, stceTar, sMax, tMax,
                    cogn, lpt);
        }
    }

    void getLengths(Vector paraSrc, Vector paraTar) {

        int ns = paraSrc.size(), nt = paraTar.size();
        srcLengths = new Dist(ns);
        tarLengths = new Dist(nt);

        for (int i = 0; i < ns; i++) {
            srcLengths.setDistAt(i, (Integer) ((Vector) paraSrc.elementAt(i)).elementAt(1) / 10);
        }

        for (int i = 0; i < nt; i++) {
            tarLengths.setDistAt(i, (Integer) ((Vector) paraTar.elementAt(i)).elementAt(1) / 10);
        }

    }


    void alignSentences(Vector srcParas, Vector tarParas,
                        Path path,
                        Vector srcSentences, Vector tarSentences,
                        int sMax, int tMax, Cognates cogn,
                        LoadAndPrepareTexts lpt)

    {

        // csSrc,csTar: counter for marking current position in sentence's vectors
        // sMax = srcSentences.size(), tMax = tarSentences.size();

        int cpSrc = 0, cpTar = 0;

        // ici, prends des parties des sentences de la source et la cible et aligner

        for (int i = 0; i < path.getNumberOfPoint(); i++) {
            if (csSrc == sMax) {
                System.err.println("\nWarning: A paragraph may be without segment in the source document");
                break;
            }

            Vector Src = new Vector(), Tar = new Vector();
            Point pt = path.getPointAt(i);

            // get sentences in the source
            for (int j = 0; j < pt.x; j++) {
                Vector para = (Vector) srcParas.elementAt(cpSrc);

                String id = para.elementAt(0).toString();

                if ((Integer) para.elementAt(1) > 0) {
                    while (!((Vector) srcSentences.elementAt(csSrc)).elementAt(0).toString().startsWith(id)) {
                        csSrc++;
                        if (csSrc == sMax) break;
                    }

                    if (csSrc < sMax)
                        while (((Vector) srcSentences.elementAt(csSrc)).elementAt(0).toString().startsWith(id)) {
                            Src.addElement(srcSentences.elementAt(csSrc));
                            csSrc++;
                            if (csSrc == sMax) break;
                        }
                }
                cpSrc++;
            } // endfor source

            // get sentences in the target
            for (int j = 0; j < pt.y; j++) {
                if (csTar == tMax) {
                    System.err.println("\nWarning: A paragraph may be without segment in the target document");
                    break;
                }
                Vector para = (Vector) tarParas.elementAt(cpTar);
                String id = para.elementAt(0).toString();

                if ((Integer) para.elementAt(1) > 0) {
                    while (!((Vector) tarSentences.elementAt(csTar)).elementAt(1).toString().startsWith(id)) {
                        csTar++;
                        if (csTar == tMax) break;
                    }
                    if (csTar < tMax)
                        while (((Vector) tarSentences.elementAt(csTar)).elementAt(1).toString().startsWith(id)) {
                            Tar.addElement(tarSentences.elementAt(csTar));
                            csTar++;
                            if (csTar == tMax) break;
                        }
                }
                cpTar++;
            } // enfor target

            // align Src and Tar
            new Sentences(Src, Tar, cogn, lpt);

            // System.out.println(stc.Links.toString());
            // System.out.println(stc.Linking.toString());

            // get links of stc
            Links = Sentences.Links;
            // get linkings of stc
            Linking = Sentences.Linking;

        } // endfor Path
    }
}
