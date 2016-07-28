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
 * @(#)       MultiAlign.java
 * 
 * Created    Sat Sep 11 14:05:56 1999
 * 
 * Copyright  1999 (C) PATRICE BONHOMME
 *            UMR LORIA (Universities of Nancy, CNRS & INRIA)
 *            
 */
package org.gramlab.core.loria.xsilfide.multialign;

import java.io.PrintStream;

/**
 * This the main class for the multi-level alignment tool.
 * 
 * @author Patrice Bonhomme
 * @version Bertrand.Gaiffe@atilf.fr : On veut prendre en entrée un fichier
 *          d'alignement qui désigne le fichier source et le fichier cible.
 */
public class MultiAlign {
	static private String inputAndResult;
	public static final int NDOTS = 80;
	private static PrintStream debugOut = null;

	public static void debug(String str) {
		if (debugOut != null)
			debugOut.println(str);
	}

	private static void usage() {
		// Just load and prepare texts @see LoadAndPrepareTexts
		System.out.println("Load and prepare texts: ");
		System.out
				.println("align [-d] -p sfile tfile src-properties tar-properties outfile");
		// Align prepared texts
		System.out.println("Align prepared texts: ");
		System.out
				.println("align [-d] -a sfile tfile slang tlang outfile lgfile");
		// Load, prepare and align texts
		System.out.println("Load, prepare and align texts: ");
		System.out
				.println("align [-d] sfile tfile src-properties tar-properties [slang tlang] lgfile");
		System.exit(0);
	}

	// Par prudence, je rajoute une option -new ...
	public static void main(String[] args) {
		int arg = 0;
		boolean newXA = false;
		while (arg < args.length) {
			System.out.println("" + arg + " : " + args[arg] + "\n");
			if (args[arg].equals("-new")) {
				inputAndResult = args[arg + 1];
				newXA = true;
			}
			arg++;
		}
		if (newXA) {
			final NewLoadAndPrepareTexts temp = new NewLoadAndPrepareTexts(
					inputAndResult);
			final LoadAndPrepareTexts lpt = temp.getPreparedTexts();
			System.out.print("Aligning...");
			final Div divs = new Div(lpt.divSrc, lpt.divTar, lpt.paraSrc,
					lpt.paraTar, lpt.stcSrc, lpt.stcTar, temp.getCognates(),
					lpt);
			System.out.println("\nDone.");
			System.out.println(divs.getLinking().size() + " linking, "
					+ divs.getLinks().size() + " links.");
			// Ne reste plus qu'à mettre les alignements calculés dans le
			// fichier...
			// Je ne sais pas encore comment il faut le faire ....
			// Dans un premier temps, on crache un fichier "result"
			System.out.println("Writing link-group file '" + inputAndResult
					+ "'...");
			// Il faut maintenant écrire dans le fichier de départ....
			new InsertLinkGrp(
					inputAndResult, // output filename.
					lpt.lsource,
					lpt.ltarget, // language names
					temp.getSourceName(),
					temp.getTargetName(), // texts.
					lpt.stcTar, divs.getLinking(), divs.getLinks(), lpt.idSrc,
					lpt.idTar, true);
			System.out.println("Done.");
		} else {
			arg = 0;
			if (args.length < 5)
				usage();
			while (arg < args.length) {
				if (args[arg].equals("-d")) {
					debugOut = System.err;
					arg++;
				} else if (args[arg].equals("-p")) {
					// java MultiAlign -p sfile tfile src-properties
					// tar-properties outfile
					MultiAlign.debug("" + args.length);
					if (args.length != arg + 6)
						usage();
					//
					// Preparing for the alignement
					//
					new LoadAndPrepareTexts(args[arg + 1], args[arg + 2],
							args[arg + 3], args[arg + 4], args[arg + 5]);
					break;
				} else if (args[arg].equals("-a")) {
					// java MultiAlign -a sfile tfile slang tlang outfile lgfile
					MultiAlign.debug("" + args.length);
					if (args.length != arg + 7)
						usage();
					//
					// Alignment of prepared texts
					//
					new AlignPreparedTexts(args[arg + 1], args[arg + 2],
							args[arg + 3], args[arg + 4], args[arg + 5],
							args[arg + 6]);
					break;
				} else if (args.length == arg + 5 || args.length == arg + 7) {
					// java MultiAlign sfile tfile src-properties tar-properties
					// slang tlang lgfile
					//
					// Preparing and Alignment
					//
					// arguments: sfile, tfile, props, outfile
					final LoadAndPrepareTexts lpt = new LoadAndPrepareTexts(
							args[arg], args[arg + 1], args[arg + 2],
							args[arg + 3]);
					// On veut voir a quoi ca ressemble (B.G.)
					lpt.savePreparedTexts("preparedTexts.txt");
					//
					// Doing the alignement
					//
					System.out.print("Aligning...");
					final Div divs = new Div(lpt.divSrc, lpt.divTar,
							lpt.paraSrc, lpt.paraTar, lpt.stcSrc, lpt.stcTar,
							new Cognates(args[arg], args[arg + 1]), lpt);
					if (args.length == arg + 6) {
						lpt.lsource = args[arg + 4];
						lpt.lsource = args[arg + 5];
						args[arg + 4] = args[arg + 6];
					}
					System.out.println("\nDone.");
					System.out.println(divs.getLinking().size() + " linking, "
							+ divs.getLinks().size() + " links.");
					System.out.println("Writing link-group file '"
							+ args[arg + 4] + "'...");
					new InsertLinkGrp(
							args[arg + 4], // output filename.
							lpt.lsource,
							lpt.ltarget, // language names
							args[arg],
							args[arg + 1], // texts.
							lpt.stcTar, divs.getLinking(), divs.getLinks(),
							lpt.idSrc, lpt.idTar, false);
					System.out.println("Done.");
					break;
				} else
					usage();
			}
		}
	}
}
// EOF MultiAlign
