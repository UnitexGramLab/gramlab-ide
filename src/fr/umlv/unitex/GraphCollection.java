/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import fr.umlv.unitex.frames.MessageWhileWorkingFrame;
import fr.umlv.unitex.frames.UnitexFrame;
import fr.umlv.unitex.io.UnicodeIO;

/**
 * This class provides methods that generate a graph that calls all the subgraphs
 * contained in a directory.
 *
 * @author Sébastien Paumier
 */

public class GraphCollection {

    private volatile static boolean stop;

    /**
     * Builds a graph that calls all subgraphs contained in a directory. If the
     * parameter <code>copy</code> is set to <code>true</code>, subgraphs
     * are copied into the destination directory. This method shows a frame that
     * displays the scanned directories.
     *
     * @param srcDir    the source directory
     * @param destGraph the destination graph
     * @param copy      indicates if subgraphs must be copied or not
     */
    public static void build(final File srcDir, final File destGraph, final boolean copy) {
        new Thread(new Runnable() {
			
        	public void run() {
                MessageWhileWorkingFrame f = UnitexFrame.getFrameManager().newMessageWhileWorkingFrame("Building graph collection");
                stop=false;
                buildGraphCollection(
                        srcDir,
                        destGraph,
                        copy,
                        f.getLabel());
                UnitexFrame.getFrameManager().closeMessageWhileWorkingFrame();
            }
        }).start();
    }

    /**
     * Builds a graph that calls all subgraphs contained in a directory. If the
     * parameter <code>copy</code> is set to <code>true</code>, subgraphs
     * are copied into the destination directory.
     *
     * @param srcDir    the source directory
     * @param destGraph the destination graph
     * @param copy      indicates if subgraphs must be copied or not
     */
    private static void buildGraphCollection(
            File srcDir,
            File destGraph,
            boolean copy,
            JLabel txt) {
        if (stop) {
            return;
        }
        if (!srcDir.isDirectory()) {
            JOptionPane.showMessageDialog(
                    null,
                    srcDir + " is not a valid directory",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (txt != null) {
            txt.setText("Scanning dir " + srcDir);
        } else {
            System.out.println("Scanning dir " + srcDir);
        }
        if (stop) {
            return;
        }
        File destinationDir = destGraph.getParentFile();
        FileOutputStream stream;
        try {
            if (!destinationDir.exists()) {
                destinationDir.mkdirs();
            }
            destGraph.createNewFile();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Cannot create " + destGraph,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            stream = new FileOutputStream(destGraph);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        UnicodeIO.writeChar(stream, (char) 0xFEFF);
        UnicodeIO.writeString(stream, "#Unigraph\n");
        UnicodeIO.writeString(stream, "SIZE 1188 840\n");
        UnicodeIO.writeString(stream, "FONT Times New Roman:  10\n");
        UnicodeIO.writeString(stream, "OFONT Times New Roman:B 12\n");
        UnicodeIO.writeString(stream, "BCOLOR 16777215\n");
        UnicodeIO.writeString(stream, "FCOLOR 0\n");
        UnicodeIO.writeString(stream, "ACOLOR 13487565\n");
        UnicodeIO.writeString(stream, "SCOLOR 16711680\n");
        UnicodeIO.writeString(stream, "CCOLOR 255\n");
        UnicodeIO.writeString(stream, "DBOXES y\n");
        UnicodeIO.writeString(stream, "DFRAME y\n");
        UnicodeIO.writeString(stream, "DDATE y\n");
        UnicodeIO.writeString(stream, "DFILE y\n");
        UnicodeIO.writeString(stream, "DDIR n\n");
        UnicodeIO.writeString(stream, "DRIG n\n");
        UnicodeIO.writeString(stream, "DRST n\n");
        UnicodeIO.writeString(stream, "FITS 100\n");
        UnicodeIO.writeString(stream, "PORIENT L\n");
        UnicodeIO.writeString(stream, "#\n");
        UnicodeIO.writeString(stream, "6\n");
        UnicodeIO.writeString(stream, "\"<E>\" 42 372 2 4 5 \n");
        UnicodeIO.writeString(stream, "\"\" 574 238 0 \n");
        UnicodeIO.writeString(
                stream,
                "\"Grammars corresponding+to sub-directories:\" 34 186 0 \n");
        UnicodeIO.writeString(
                stream,
                "\"Grammars corresponding to graphs:\" 180 348 0 \n");

        if (stop) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        // we construct the file & dir list
        File files_list[] = srcDir.listFiles();

        // and we parse directories
        String graphLine = "\"";
        for (File aFiles_list1 : files_list) {
            String fileName = aFiles_list1.getName();
            if (stop) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            if (aFiles_list1.isDirectory()) {
                if (0 == graphLine.compareTo("\"")) {
                    // if this directory is the first of the list
                    graphLine =
                            graphLine
                                    + ":"
                                    + Util.getFileNameWithoutExtension(fileName)
                                    + "_dir";
                } else {
                    graphLine =
                            graphLine
                                    + "+:"
                                    + Util.getFileNameWithoutExtension(fileName)
                                    + "_dir";
                }
                buildGraphCollection(
                        aFiles_list1,
                        new File(destinationDir, Util.getFileNameWithoutExtension(fileName) + "_dir.grf"),
                        copy,
                        txt);
            }
        }
        if (0 == graphLine.compareTo("\"")) {
            // if there was no line in the box
            graphLine = graphLine + "<E>\" 125 238 0 \n";
        } else {
            graphLine = graphLine + "\" 125 238 1 1 \n";
        }
        UnicodeIO.writeString(stream, graphLine);

        // then, we parse graphs
        graphLine = "\"";
        for (File aFiles_list : files_list) {
            String fileName = aFiles_list.getName();
            if (stop) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            if (!aFiles_list.isDirectory()
                    && Util.getExtensionInLowerCase(fileName).compareTo("grf")
                    == 0) {
                if (0 == graphLine.compareTo("\"")) {
                    // if this directory is the first of the list
                    graphLine =
                            graphLine
                                    + ":"
                                    + Util.getFileNameWithoutExtension(fileName);
                } else {
                    graphLine =
                            graphLine
                                    + "+:"
                                    + Util.getFileNameWithoutExtension(fileName);
                }
                if (copy) {
                    Config.copyFile(
                            aFiles_list, new File(destinationDir, fileName));
                }
            }
        }
        if (0 == graphLine.compareTo("\"")) {
            // if there was no line in the box
            graphLine = graphLine + "<E>\" 416 372 0 \n";
        } else {
            graphLine = graphLine + "\" 416 372 1 1 \n";
        }
        UnicodeIO.writeString(stream, graphLine);
        if (stop) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        stop = true;
    }


}
