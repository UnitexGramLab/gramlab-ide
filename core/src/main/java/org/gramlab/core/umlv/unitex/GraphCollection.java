/*
 * Unitex
 *
 * Copyright (C) 2001-2016 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package fr.umlv.unitex;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.frames.InternalFrameManager;
import fr.umlv.unitex.frames.MessageWhileWorkingFrame;
import fr.umlv.unitex.frames.UnitexInternalFrameManager;
import fr.umlv.unitex.io.UnicodeIO;

/**
 * This class provides methods that generate a graph that calls all the
 * subgraphs contained in a directory.
 * 
 * @author Sébastien Paumier
 */
public class GraphCollection {
	private volatile static boolean stop;

	/**
	 * Builds a graph that calls all subgraphs contained in a directory. If the
	 * parameter <code>copy</code> is set to <code>true</code>, subgraphs are
	 * copied into the destination directory. This method shows a frame that
	 * displays the scanned directories.
	 * 
	 * @param srcDir
	 *            the source directory
	 * @param destGraph
	 *            the destination graph
	 * @param copy
	 *            indicates if subgraphs must be copied or not
	 */
	public static void build(final File srcDir, final File destGraph,
			final boolean copy) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final MessageWhileWorkingFrame f = GlobalProjectManager
						.search(null).getFrameManagerAs(UnitexInternalFrameManager.class)
						.newMessageWhileWorkingFrame("Building graph collection");
				setStop(false);
				buildGraphCollection(srcDir, destGraph, copy, f.getLabel());
				GlobalProjectManager.search(null)
						.getFrameManagerAs(UnitexInternalFrameManager.class)
						.closeMessageWhileWorkingFrame();
			}
		}).start();
	}

	/**
	 * Builds a graph that calls all subgraphs contained in a directory. If the
	 * parameter <code>copy</code> is set to <code>true</code>, subgraphs are
	 * copied into the destination directory.
	 * 
	 * @param srcDir
	 *            the source directory
	 * @param destGraph
	 *            the destination graph
	 * @param copy
	 *            indicates if subgraphs must be copied or not
	 */
	static void buildGraphCollection(File srcDir, File destGraph, boolean copy,
			JLabel txt) {
		if (isStop()) {
			return;
		}
		if (!srcDir.isDirectory()) {
			JOptionPane.showMessageDialog(null, srcDir
					+ " is not a valid directory", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (txt != null) {
			txt.setText("Scanning dir " + srcDir);
		} else {
			System.out.println("Scanning dir " + srcDir);
		}
		if (isStop()) {
			return;
		}
		final File destinationDir = destGraph.getParentFile();
		OutputStreamWriter writer;
		try {
			if (!destinationDir.exists()) {
				destinationDir.mkdirs();
			}
			destGraph.createNewFile();
		} catch (final IOException e) {
			JOptionPane.showMessageDialog(null, "Cannot create " + destGraph,
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		writer = ConfigManager.getManager().getEncoding(null)
				.getOutputStreamWriter(destGraph);
		UnicodeIO.writeString(writer, "#Unigraph\n");
		UnicodeIO.writeString(writer, "SIZE 1188 840\n");
		UnicodeIO.writeString(writer, "FONT Times New Roman:  10\n");
		UnicodeIO.writeString(writer, "OFONT Times New Roman:B 12\n");
		UnicodeIO.writeString(writer, "BCOLOR 16777215\n");
		UnicodeIO.writeString(writer, "FCOLOR 0\n");
		UnicodeIO.writeString(writer, "ACOLOR 13487565\n");
		UnicodeIO.writeString(writer, "SCOLOR 16711680\n");
		UnicodeIO.writeString(writer, "CCOLOR 255\n");
		UnicodeIO.writeString(writer, "DBOXES y\n");
		UnicodeIO.writeString(writer, "DFRAME y\n");
		UnicodeIO.writeString(writer, "DDATE y\n");
		UnicodeIO.writeString(writer, "DFILE y\n");
		UnicodeIO.writeString(writer, "DDIR n\n");
		UnicodeIO.writeString(writer, "DRIG n\n");
		UnicodeIO.writeString(writer, "DRST n\n");
		UnicodeIO.writeString(writer, "FITS 100\n");
		UnicodeIO.writeString(writer, "PORIENT L\n");
		UnicodeIO.writeString(writer, "#\n");
		UnicodeIO.writeString(writer, "6\n");
		UnicodeIO.writeString(writer, "\"<E>\" 42 372 2 4 5 \n");
		UnicodeIO.writeString(writer, "\"\" 574 238 0 \n");
		UnicodeIO.writeString(writer,
				"\"Grammars corresponding+to sub-directories:\" 34 186 0 \n");
		UnicodeIO.writeString(writer,
				"\"Grammars corresponding to graphs:\" 180 348 0 \n");
		if (isStop()) {
			try {
				writer.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
			return;
		}
		// we construct the file & dir list
		final File files_list[] = srcDir.listFiles();
		// and we parse directories
		String graphLine = "\"";
		for (final File aFiles_list1 : files_list) {
			final String fileName = aFiles_list1.getName();
			if (isStop()) {
				try {
					writer.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
				return;
			}
			if (aFiles_list1.isDirectory()) {
				if (0 == graphLine.compareTo("\"")) {
					// if this directory is the first of the list
					graphLine = graphLine + ":"
							+ FileUtil.getFileNameWithoutExtension(fileName)
							+ "_dir";
				} else {
					graphLine = graphLine + "+:"
							+ FileUtil.getFileNameWithoutExtension(fileName)
							+ "_dir";
				}
				buildGraphCollection(aFiles_list1, new File(destinationDir,
						FileUtil.getFileNameWithoutExtension(fileName)
								+ "_dir.grf"), copy, txt);
			}
		}
		if (0 == graphLine.compareTo("\"")) {
			// if there was no line in the box
			graphLine = graphLine + "<E>\" 125 238 0 \n";
		} else {
			graphLine = graphLine + "\" 125 238 1 1 \n";
		}
		UnicodeIO.writeString(writer, graphLine);
		// then, we parse graphs
		graphLine = "\"";
		for (final File aFiles_list : files_list) {
			final String fileName = aFiles_list.getName();
			if (isStop()) {
				try {
					writer.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
				return;
			}
			if (!aFiles_list.isDirectory()
					&& FileUtil.getExtensionInLowerCase(fileName).compareTo(
							"grf") == 0) {
				if (0 == graphLine.compareTo("\"")) {
					// if this directory is the first of the list
					graphLine = graphLine + ":"
							+ FileUtil.getFileNameWithoutExtension(fileName);
				} else {
					graphLine = graphLine + "+:"
							+ FileUtil.getFileNameWithoutExtension(fileName);
				}
				if (copy) {
					FileUtil.copyFile(aFiles_list, new File(destinationDir,
							fileName));
				}
			}
		}
		if (0 == graphLine.compareTo("\"")) {
			// if there was no line in the box
			graphLine = graphLine + "<E>\" 416 372 0 \n";
		} else {
			graphLine = graphLine + "\" 416 372 1 1 \n";
		}
		UnicodeIO.writeString(writer, graphLine);
		if (isStop()) {
			try {
				writer.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
			return;
		}
		try {
			writer.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static void stop() {
		setStop(true);
	}

	public static void setStop(boolean stop) {
		GraphCollection.stop = stop;
	}

	public static boolean isStop() {
		return stop;
	}
}
