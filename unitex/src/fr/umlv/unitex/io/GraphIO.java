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
package fr.umlv.unitex.io;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import fr.umlv.unitex.Unitex;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.config.PreferencesManager;
import fr.umlv.unitex.graphrendering.GenericGraphBox;
import fr.umlv.unitex.graphrendering.GenericGraphicalZone;
import fr.umlv.unitex.graphrendering.GraphBox;
import fr.umlv.unitex.graphrendering.TfstGraphBox;
import fr.umlv.unitex.grf.GraphMetaData;
import fr.umlv.unitex.grf.GraphPresentationInfo;

/**
 * This class provides methods for loading and saving graphs.
 * 
 * @author Sébastien Paumier
 */
public class GraphIO {
	/**
	 * Boxes of a graph
	 */
	private ArrayList<GenericGraphBox> boxes;
	/**
	 * Rendering properties of a graph
	 */
	private final GraphPresentationInfo info;
	/**
	 * Width of a graph
	 */
	private int width;
	/**
	 * Height of a graph
	 */
	private int height;
	/**
	 * Number of boxes of a graph
	 */
	private int nBoxes;
	private File grf;
	private final GraphMetaData metadata;

	private GraphIO() {
		info = ConfigManager.getManager().getGraphPresentationPreferences(null)
				.clone();
		metadata = new GraphMetaData();
	}

	public GraphIO(GenericGraphicalZone zone) {
		info = zone.getGraphPresentationInfo();
		width = zone.getWidth();
		height = zone.getHeight();
		boxes = zone.getBoxes();
		metadata = zone.getMetadata();
	}

	/**
	 * This method loads a graph.
	 * 
	 * @param grfFile
	 *            name of the graph
	 * @return a <code>GraphIO</code> object describing the graph
	 */
	public static GraphIO loadGraph(File grfFile, boolean isSentenceGraph,
			boolean emitErrorMessage) {
		final GraphIO res = new GraphIO();
		res.grf = grfFile;
		InputStreamReader reader;
		if (!grfFile.exists()) {
			if (emitErrorMessage)
				JOptionPane.showMessageDialog(null,
						"Cannot find " + grfFile.getAbsolutePath(), "Error",
						JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (!grfFile.canRead()) {
			if (emitErrorMessage)
				JOptionPane.showMessageDialog(null,
						"Cannot read " + grfFile.getAbsolutePath(), "Error",
						JOptionPane.ERROR_MESSAGE);
			return null;
		}
		try {
			reader = Encoding.getInputStreamReader(grfFile);
			if (reader == null) {
				if (emitErrorMessage)
					JOptionPane.showMessageDialog(null,
							grfFile.getAbsolutePath()
									+ " is not a Unicode graph", "Error",
							JOptionPane.ERROR_MESSAGE);
				return null;
			}
			UnicodeIO.skipLine(reader); // ignoring #...
			res.readSize(reader);
			res.readInputFont(reader);
			res.readOutputFont(reader);
			res.readBackgroundColor(reader);
			res.readForegroundColor(reader);
			res.readSubgraphColor(reader);
			res.readCommentColor(reader);
			res.readSelectedColor(reader);
			UnicodeIO.skipLine(reader); // ignoring DBOXES
			res.readDrawFrame(reader);
			res.readDate(reader);
			res.readFile(reader);
			res.readDirectory(reader);
			res.readRightToLeft(reader);
			if (isSentenceGraph) {
				res.info.setRightToLeft(ConfigManager.getManager()
						.isRightToLeftForText(null));
			}
			UnicodeIO.skipLine(reader); // ignoring DRST
			UnicodeIO.skipLine(reader); // ignoring FITS
			UnicodeIO.skipLine(reader); // ignoring PORIENT
			/* Reading metadata until we find the # line */
			String line;
			while (!(line = UnicodeIO.readLine(reader)).equals("#")) {
				final int pos = line.indexOf("=");
				if (pos == -1) {
					if (emitErrorMessage)
						JOptionPane.showMessageDialog(
								null,
								"Invalid header line in "
										+ grfFile.getAbsolutePath() + ":\n"
										+ line, "Error",
								JOptionPane.ERROR_MESSAGE);
					return null;
				}
				final String key = line.substring(0, pos);
				final String value = line.substring(pos + 1);
				res.metadata.set(key, value);
			}
			res.readBoxNumber(reader);
			res.boxes = new ArrayList<GenericGraphBox>();
			if (isSentenceGraph) {
				// adding initial state
				res.boxes.add(new TfstGraphBox(0, 0, 0, null));
				// adding final state
				res.boxes.add(new TfstGraphBox(0, 0, 1, null));
				// adding other states
				for (int i = 2; i < res.nBoxes; i++)
					res.boxes.add(new TfstGraphBox(0, 0, 2, null));
				for (int i = 0; i < res.nBoxes; i++)
					res.readSentenceGraphLine(reader, i);
			} else {
				// adding initial state
				res.boxes.add(new GraphBox(0, 0, 0, null));
				// adding final state
				res.boxes.add(new GraphBox(0, 0, 1, null));
				// adding other states
				for (int i = 2; i < res.nBoxes; i++)
					res.boxes.add(new GraphBox(0, 0, 2, null));
				for (int i = 0; i < res.nBoxes; i++)
					res.readGraphLine(reader, i);
			}
			reader.close();
		} catch (final IllegalStateException e) {
			if (emitErrorMessage)
				JOptionPane.showMessageDialog(null, grfFile.getAbsolutePath()
						+ ": " + e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			return null;
		} catch (final FileNotFoundException e) {
			if (emitErrorMessage)
				JOptionPane.showMessageDialog(null,
						"Cannot open " + grfFile.getAbsolutePath(), "Error",
						JOptionPane.ERROR_MESSAGE);
			return null;
		} catch (final IOException e) {
			if (emitErrorMessage)
				JOptionPane.showMessageDialog(
						null,
						"Error in file " + grfFile.getAbsolutePath() + ": "
								+ e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return res;
	}

	private void readSize(InputStreamReader r) throws IOException {
		// skipping the chars preceeding the width and height
		UnicodeIO.skipChars(r, 5);
		char c;
		// reading width
		width = 0;
		int z = -1;
		while ((z = UnicodeIO.readChar(r)) != -1
				&& UnicodeIO.isDigit((c = (char) z)))
			width = width * 10 + (c - '0');
		if (z == -1)
			throw new IOException("Number expected");
		// reading height
		z = -1;
		while ((z = UnicodeIO.readChar(r)) != -1
				&& UnicodeIO.isDigit((c = (char) z)))
			height = height * 10 + (c - '0');
		if (z == -1)
			throw new IOException("Number expected");
	}

	private void readInputFont(InputStreamReader r) throws IOException {
		UnicodeIO.skipChars(r, 5);
		String s = "";
		char c;
		int z = -1;
		while ((z = (char) UnicodeIO.readChar(r)) != ':' && z != -1)
			s = s + (char) z;
		if (z == -1)
			throw new IOException("Error while reading input font information");
		final boolean bold = ((z = UnicodeIO.readChar(r)) == 'B');
		if (z != 'B' && z != ' ')
			throw new IOException("Error while reading input font information");
		if (z == -1)
			throw new IOException("Error while reading input font information");
		final boolean italic = ((z = UnicodeIO.readChar(r)) == 'I');
		if (z != 'I' && z != ' ')
			throw new IOException("Error while reading input font information");
		if (z == -1)
			throw new IOException("Error while reading input font information");
		int size = 0;
		z = -1;
		while ((z = UnicodeIO.readChar(r)) != -1
				&& UnicodeIO.isDigit((c = (char) z)))
			size = size * 10 + (c - '0');
		if (z == -1)
			throw new IOException("Error while reading input font information");
		info.getInput().setSize(size);
		int style;
		if (bold && italic)
			style = Font.BOLD | Font.ITALIC;
		else if (bold)
			style = Font.BOLD;
		else if (italic)
			style = Font.ITALIC;
		else
			style = Font.PLAIN;
		info.getInput().setFont(new Font(s, style, (int) (size / 0.72)));
	}

	private void readOutputFont(InputStreamReader r) throws IOException {
		UnicodeIO.skipChars(r, 6);
		String s = "";
		char c;
		int z = -1;
		while ((z = (char) UnicodeIO.readChar(r)) != ':' && z != -1)
			s = s + (char) z;
		if (z == -1)
			throw new IOException("Error while reading output font information");
		final boolean bold = ((z = UnicodeIO.readChar(r)) == 'B');
		if (z != 'B' && z != ' ')
			throw new IOException("Error while reading output font information");
		if (z == -1)
			throw new IOException("Error while reading output font information");
		final boolean italic = ((z = UnicodeIO.readChar(r)) == 'I');
		if (z != 'I' && z != ' ')
			throw new IOException("Error while reading output font information");
		if (z == -1)
			throw new IOException("Error while reading output font information");
		int size = 0;
		z = -1;
		while ((z = UnicodeIO.readChar(r)) != -1
				&& UnicodeIO.isDigit((c = (char) z)))
			size = size * 10 + (c - '0');
		if (z == -1)
			throw new IOException("Error while reading output font information");
		info.getOutput().setSize(size);
		int style;
		if (bold && italic)
			style = Font.BOLD | Font.ITALIC;
		else if (bold)
			style = Font.BOLD;
		else if (italic)
			style = Font.ITALIC;
		else
			style = Font.PLAIN;
		info.getOutput().setFont(new Font(s, style, (int) (size / 0.72)));
	}

	private void readBackgroundColor(InputStreamReader r) throws IOException {
		UnicodeIO.skipChars(r, 7);
		char c;
		int n = 0;
		int z = -1;
		while ((z = UnicodeIO.readChar(r)) != -1
				&& UnicodeIO.isDigit((c = (char) z)))
			n = n * 10 + (c - '0');
		if (z == -1)
			throw new IOException(
					"Error while reading background color information");
		info.setBackgroundColor(new Color(n));
	}

	private void readForegroundColor(InputStreamReader r) throws IOException {
		UnicodeIO.skipChars(r, 7);
		char c;
		int n = 0;
		int z = -1;
		while ((z = UnicodeIO.readChar(r)) != -1
				&& UnicodeIO.isDigit((c = (char) z)))
			n = n * 10 + (c - '0');
		if (z == -1)
			throw new IOException(
					"Error while reading foreground color information");
		info.setForegroundColor(new Color(n));
	}

	private void readSubgraphColor(InputStreamReader r) throws IOException {
		UnicodeIO.skipChars(r, 7);
		char c;
		int n = 0;
		int z = -1;
		while ((z = UnicodeIO.readChar(r)) != -1
				&& UnicodeIO.isDigit((c = (char) z)))
			n = n * 10 + (c - '0');
		if (z == -1)
			throw new IOException(
					"Error while reading subgraph color information");
		info.setSubgraphColor(new Color(n));
	}

	private void readSelectedColor(InputStreamReader r) throws IOException {
		UnicodeIO.skipChars(r, 7);
		char c;
		int n = 0;
		int z = -1;
		while ((z = UnicodeIO.readChar(r)) != -1
				&& UnicodeIO.isDigit((c = (char) z)))
			n = n * 10 + (c - '0');
		if (z == -1)
			throw new IOException(
					"Error while reading selected color information");
		info.setSelectedColor(new Color(n));
	}

	private void readCommentColor(InputStreamReader r) throws IOException {
		UnicodeIO.skipChars(r, 7);
		char c;
		int n = 0;
		int z = -1;
		while ((z = UnicodeIO.readChar(r)) != -1
				&& UnicodeIO.isDigit((c = (char) z)))
			n = n * 10 + (c - '0');
		if (z == -1)
			throw new IOException(
					"Error while reading comment color information");
		info.setCommentColor(new Color(n));
	}

	private void readDrawFrame(InputStreamReader r) throws IOException {
		UnicodeIO.skipChars(r, 7);
		int z;
		info.setFrame((z = UnicodeIO.readChar(r)) == 'y');
		if (z != 'y' && z != 'n')
			throw new IOException("Error while reading frame information");
		if (-1 == UnicodeIO.readChar(r))
			throw new IOException("Error while reading frame information");
	}

	private void readDate(InputStreamReader r) throws IOException {
		UnicodeIO.skipChars(r, 6);
		int z;
		info.setDate((z = UnicodeIO.readChar(r)) == 'y');
		if (z != 'y' && z != 'n')
			throw new IOException("Error while reading date information");
		if (-1 == UnicodeIO.readChar(r))
			throw new IOException("Error while reading date information");
	}

	private void readFile(InputStreamReader r) throws IOException {
		UnicodeIO.skipChars(r, 6);
		int z;
		info.setFilename((z = UnicodeIO.readChar(r)) == 'y');
		if (z != 'y' && z != 'n')
			throw new IOException("Error while reading file name information");
		if (-1 == UnicodeIO.readChar(r))
			throw new IOException("Error while reading file name information");
	}

	private void readDirectory(InputStreamReader r) throws IOException {
		UnicodeIO.skipChars(r, 5);
		int z;
		info.setPathname((z = UnicodeIO.readChar(r)) == 'y');
		if (z != 'y' && z != 'n')
			throw new IOException("Error while reading path name information");
		if (-1 == UnicodeIO.readChar(r))
			throw new IOException("Error while reading path name information");
	}

	private void readRightToLeft(InputStreamReader r) throws IOException {
		UnicodeIO.skipChars(r, 5);
		int z;
		info.setRightToLeft((z = UnicodeIO.readChar(r)) == 'y');
		if (z != 'y' && z != 'n')
			throw new IOException(
					"Error while reading right to left information");
		if (-1 == UnicodeIO.readChar(r))
			throw new IOException(
					"Error while reading right to left information");
	}

	private void readBoxNumber(InputStreamReader r) throws IOException {
		char c;
		nBoxes = 0;
		int z = -1;
		while ((z = UnicodeIO.readChar(r)) != -1
				&& UnicodeIO.isDigit((c = (char) z)))
			nBoxes = nBoxes * 10 + (c - '0');
		if (z == -1)
			throw new IOException("Error while reading graph box number");
	}

	private void readGraphLine(InputStreamReader r, int n) throws IOException {
		final GenericGraphBox g = boxes.get(n);
		int z;
		if ((z = UnicodeIO.readChar(r)) == 's') {
			// is a "s" was read, then we read the " char
			z = UnicodeIO.readChar(r);
		}
		if (z != '"')
			throw new IOException("Error #1 while reading graph box #" + n);
		String s = "";
		int c;
		while ((c = UnicodeIO.readChar(r)) != '"') {
			if (c == -1)
				throw new IOException("Error #2 while reading graph box #" + n);
			if (c == '\\') {
				c = UnicodeIO.readChar(r);
				if (c == -1)
					throw new IOException("Error #3 while reading graph box #"
							+ n);
				if (c != '\\') {
					// case of \: \+ and \"
					if (c == '"')
						s = s + (char) c;
					else
						s = s + "\\" + (char) c;
				} else {
					// case of \\\" that must be transformed into \"
					c = UnicodeIO.readChar(r);
					if (c == -1)
						throw new IOException(
								"Error #4 while reading graph box #" + n);
					if (c == '\\') {
						// we are in the case \\\" -> \"
						c = UnicodeIO.readChar(r);
						if (c == -1)
							throw new IOException(
									"Error #5 while reading graph box #" + n);
						s = s + "\\" + (char) c;
					} else {
						// we are in the case \\a -> \\a
						s = s + "\\\\";
						if (c != '"')
							s = s + (char) c;
						else
							break;
					}
				}
			} else
				s = s + (char) c;
		}
		// skipping the space after "
		if (UnicodeIO.readChar(r) != ' ')
			throw new IOException("Error #6 while reading graph box #" + n);
		// reading the X coordinate
		int x = 0;
		int neg = 1;
		c = UnicodeIO.readChar(r);
		if (c == -1)
			throw new IOException("Error #7 while reading graph box #" + n);
		if (c == '-') {
			neg = -1;
		} else if (UnicodeIO.isDigit((char) c)) {
			x = ((char) c - '0');
		} else {
			throw new IOException("Error #8 while reading graph box #" + n);
		}
		c = -1;
		while ((c = UnicodeIO.readChar(r)) != -1 && UnicodeIO.isDigit((char) c)) {
			x = x * 10 + ((char) c - '0');
		}
		if (c == -1)
			throw new IOException("Error #9 while reading graph box #" + n);
		x = x * neg;
		// reading the Y coordinate
		int y = 0;
		neg = 1;
		c = UnicodeIO.readChar(r);
		if (c == -1)
			throw new IOException("Error #10 while reading graph box #" + n);
		if (c == '-') {
			neg = -1;
		} else if (UnicodeIO.isDigit((char) c)) {
			y = ((char) c - '0');
		} else {
			throw new IOException("Error #11 while reading graph box #" + n);
		}
		c = -1;
		while ((c = UnicodeIO.readChar(r)) != -1 && UnicodeIO.isDigit((char) c)) {
			y = y * 10 + ((char) c - '0');
		}
		if (c == -1)
			throw new IOException("Error #12 while reading graph box #" + n);
		y = y * neg;
		g.setX(x);
		g.setY(y);
		g.setX1(g.getX());
		g.setY1(g.getY());
		g.setX_in(g.getX());
		g.setY_in(g.getY());
		g.setX_out(g.getX() + g.getWidth() + 5);
		g.setY_out(g.getY_in());
		if (n != 1) {
			// 1 is the final state, which content is <E>
			g.setContent(s);
			// we will need to call g.update() to size the box according to the
			// text
		} else {
			g.setContent("<E>");
			g.setX_in(g.getX());
			g.setY_in(g.getY());
			g.setX1(g.getX());
			g.setY1(g.getY() - 10);
			g.setY_out(g.getY_in());
			g.setX_out(g.getX_in() + 25);
		}
		int trans = 0;
		c = -1;
		while ((c = UnicodeIO.readChar(r)) != -1 && UnicodeIO.isDigit((char) c))
			trans = trans * 10 + ((char) c - '0');
		if (c == -1)
			throw new IOException("Error #13 while reading graph box #" + n);
		for (int j = 0; j < trans; j++) {
			int dest = 0;
			c = -1;
			while ((c = UnicodeIO.readChar(r)) != -1
					&& UnicodeIO.isDigit((char) c))
				dest = dest * 10 + ((char) c - '0');
			if (c == -1)
				throw new IOException("Error #14 while reading graph box #" + n);
			g.addTransitionTo(boxes.get(dest));
		}
		// skipping the end-of-line
		final int foo = UnicodeIO.readChar(r);
		if (foo != '\n')
			throw new IOException("Error #15 while reading graph box #" + n);
	}

	/**
	 * Saves the graph referenced by the field of this <code>GraphIO</code>
	 * object
	 * 
	 * @param grfFile
	 *            graph file
	 */
	public void saveGraph(File grfFile) {
		if (info == null) {
			throw new IllegalStateException(
					"Should not save a graph with null graph information");
		}
		if (grfFile.exists() && !grfFile.canWrite()) {
			JOptionPane.showMessageDialog(null,
					"Cannot write " + grfFile.getAbsolutePath()
							+ "\nbecause it is a read-only file!", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		OutputStreamWriter writer;
		try {
			if (!grfFile.exists())
				grfFile.createNewFile();
		} catch (final IOException e) {
			JOptionPane.showMessageDialog(null,
					"Cannot write " + grfFile.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!grfFile.canWrite()) {
			JOptionPane.showMessageDialog(null,
					"Cannot write " + grfFile.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(Unitex.isRunning()) {
			PreferencesManager.getUserPreferences().addRecentGraph(grfFile);
		}
		try {
			final Encoding e = ConfigManager.getManager().getEncoding(null);
			writer = e.getOutputStreamWriter(grfFile);
			UnicodeIO.writeString(writer, "#Unigraph\n");
			UnicodeIO.writeString(writer, "SIZE " + String.valueOf(width) + " "
					+ String.valueOf(height) + "\n");
			UnicodeIO.writeString(writer, "FONT "
					+ info.getInput().getFont().getName() + ":");
			switch (info.getInput().getFont().getStyle()) {
			case Font.PLAIN:
				UnicodeIO.writeString(writer, "  ");
				break;
			case Font.BOLD:
				UnicodeIO.writeString(writer, "B ");
				break;
			case Font.ITALIC:
				UnicodeIO.writeString(writer, " I");
				break;
			default:
				UnicodeIO.writeString(writer, "BI");
				break;
			}
			UnicodeIO.writeString(writer,
					String.valueOf(info.getInput().getSize()) + "\n");
			UnicodeIO.writeString(writer, "OFONT "
					+ info.getOutput().getFont().getName() + ":");
			switch (info.getOutput().getFont().getStyle()) {
			case Font.PLAIN:
				UnicodeIO.writeString(writer, "  ");
				break;
			case Font.BOLD:
				UnicodeIO.writeString(writer, "B ");
				break;
			case Font.ITALIC:
				UnicodeIO.writeString(writer, " I");
				break;
			default:
				UnicodeIO.writeString(writer, "BI");
				break;
			}
			UnicodeIO.writeString(writer,
					String.valueOf(info.getOutput().getSize()) + "\n");
			UnicodeIO.writeString(
					writer,
					"BCOLOR "
							+ String.valueOf(16777216 + info
									.getBackgroundColor().getRGB()) + "\n");
			UnicodeIO.writeString(
					writer,
					"FCOLOR "
							+ String.valueOf(16777216 + info
									.getForegroundColor().getRGB()) + "\n");
			UnicodeIO.writeString(
					writer,
					"ACOLOR "
							+ String.valueOf(16777216 + info.getSubgraphColor()
									.getRGB()) + "\n");
			UnicodeIO.writeString(
					writer,
					"SCOLOR "
							+ String.valueOf(16777216 + info.getCommentColor()
									.getRGB()) + "\n");
			UnicodeIO.writeString(
					writer,
					"CCOLOR "
							+ String.valueOf(16777216 + info.getSelectedColor()
									.getRGB()) + "\n");
			UnicodeIO.writeString(writer, "DBOXES y\n");
			if (info.isFrame())
				UnicodeIO.writeString(writer, "DFRAME y\n");
			else
				UnicodeIO.writeString(writer, "DFRAME n\n");
			if (info.isDate())
				UnicodeIO.writeString(writer, "DDATE y\n");
			else
				UnicodeIO.writeString(writer, "DDATE n\n");
			if (info.isFilename())
				UnicodeIO.writeString(writer, "DFILE y\n");
			else
				UnicodeIO.writeString(writer, "DFILE n\n");
			if (info.isPathname())
				UnicodeIO.writeString(writer, "DDIR y\n");
			else
				UnicodeIO.writeString(writer, "DDIR n\n");
			if (info.isRightToLeft())
				UnicodeIO.writeString(writer, "DRIG y\n");
			else
				UnicodeIO.writeString(writer, "DRIG n\n");
			UnicodeIO.writeString(writer, "DRST n\n");
			UnicodeIO.writeString(writer, "FITS 100\n");
			UnicodeIO.writeString(writer, "PORIENT L\n");
			for (final String key : metadata.getKeySet()) {
				UnicodeIO.writeString(writer,
						key + "=" + metadata.getValue(key) + "\n");
			}
			UnicodeIO.writeString(writer, "#\n");
			nBoxes = boxes.size();
			UnicodeIO.writeString(writer, String.valueOf(nBoxes) + "\n");
			for (int i = 0; i < nBoxes; i++) {
				final GenericGraphBox g = boxes.get(i);
				UnicodeIO.writeChar(writer, '"');
				if (g.getType() != 1)
					writeBoxContent(writer, g.getContent());
				final int N = g.getTransitions().size();
				UnicodeIO.writeString(
						writer,
						"\" " + String.valueOf(g.getX()) + " "
								+ String.valueOf(g.getY()) + " "
								+ String.valueOf(N) + " ");
				for (int j = 0; j < N; j++) {
					final GenericGraphBox tmp = g.getTransitions().get(j);
					UnicodeIO.writeString(writer,
							String.valueOf(boxes.indexOf(tmp)) + " ");
				}
				UnicodeIO.writeChar(writer, '\n');
			}
			writer.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void writeBoxContent(OutputStreamWriter w, String s) {
		final int L = s.length();
		char c;
		for (int i = 0; i < L; i++) {
			c = s.charAt(i);
			if (c == '"') {
				// case of char "
				if (i == 0 || s.charAt(i - 1) != '\\') {
					// the " is the "abc" one; it must be saved as \"
					UnicodeIO.writeChar(w, '\\');
					UnicodeIO.writeChar(w, '"');
				} else {
					// it is the \" char that must be saved as \\\"
					// we only write 2 \ because the third has been saved at the
					// pos i-1
					UnicodeIO.writeChar(w, '\\');
					UnicodeIO.writeChar(w, '\\');
					UnicodeIO.writeChar(w, '"');
				}
			} else {
				UnicodeIO.writeChar(w, c);
			}
		}
	}

	private void readSentenceGraphLine(InputStreamReader r, int n) {
		final TfstGraphBox g = (TfstGraphBox) boxes.get(n);
		if (UnicodeIO.readChar(r) == 's') {
			// is a "s" was read, then we read the " char
			UnicodeIO.readChar(r);
		}
		String s = "";
		char c;
		while ((c = (char) UnicodeIO.readChar(r)) != '"') {
			if (c == '\\') {
				c = (char) UnicodeIO.readChar(r);
				if (c != '\\') {
					// case of \: \+ and \"
					if (c == '"')
						s = s + c;
					else
						s = s + "\\" + c;
				} else {
					// case of \\\" that must must be transformed into \"
					c = (char) UnicodeIO.readChar(r);
					if (c == '\\') {
						// we are in the case \\\" -> \"
						c = (char) UnicodeIO.readChar(r);
						s = s + "\\" + c;
					} else {
						// we are in the case \\a -> \\a
						s = s + "\\\\" + c;
					}
				}
			} else
				s = s + c;
		}
		// skipping the space after "
		UnicodeIO.readChar(r);
		// reading the X coordinate
		int x = 0;
		while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(r))))
			x = x * 10 + (c - '0');
		// reading the Y coordinate
		int y = 0;
		while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(r))))
			y = y * 10 + (c - '0');
		if (ConfigManager.getManager().getGraphPresentationPreferences(null)
				.isRightToLeft()
				|| info.isRightToLeft()) {
			info.setRightToLeft(true);
			g.setX(width - x);
		} else {
			g.setX(x);
		}
		g.setY(y);
		g.setX1(g.getX());
		g.setY1(g.getY());
		g.setX_in(g.getX());
		g.setY_in(g.getY());
		g.setX_out(g.getX() + g.getWidth() + 5);
		g.setY_out(g.getY_in());
		if (n != 1) {
			// 1 is the final state, which content is <E>
			g.setContentWithBounds(s);
			// we will need to call g.update() to size the box according to the
			// text
		} else {
			g.setContentWithBounds("<E>");
			g.setX_in(g.getX());
			g.setY_in(g.getY());
			g.setX1(g.getX());
			g.setY1(g.getY() - 10);
			g.setY_out(g.getY_in());
			g.setX_out(g.getX_in() + 25);
		}
		int trans = 0;
		while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(r))))
			trans = trans * 10 + (c - '0');
		for (int j = 0; j < trans; j++) {
			int dest = 0;
			while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(r))))
				dest = dest * 10 + (c - '0');
			g.addTransitionTo(boxes.get(dest));
		}
		// skipping the end-of-line
		UnicodeIO.readChar(r);
	}

	/**
	 * Saves the sentence graph described by the fields of this
	 * <code>GraphIO</code> object.
	 * 
	 * @param file
	 *            sentence graph file
	 */
	public void saveSentenceGraph(File file, GraphPresentationInfo inf) {
		OutputStreamWriter writer;
		try {
			if (!file.exists())
				file.createNewFile();
		} catch (final IOException e) {
			JOptionPane.showMessageDialog(null,
					"Cannot write " + file.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!file.canWrite()) {
			JOptionPane.showMessageDialog(null,
					"Cannot write " + file.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			writer = ConfigManager.getManager().getEncoding(null)
					.getOutputStreamWriter(file);
			UnicodeIO.writeChar(writer, (char) 0xFEFF);
			UnicodeIO.writeString(writer, "#Unigraph\n");
			UnicodeIO.writeString(writer, "SIZE " + String.valueOf(width) + " "
					+ String.valueOf(height) + "\n");
			UnicodeIO.writeString(writer, "FONT "
					+ inf.getInput().getFont().getName() + ":");
			switch (inf.getInput().getFont().getStyle()) {
			case Font.PLAIN:
				UnicodeIO.writeString(writer, "  ");
				break;
			case Font.BOLD:
				UnicodeIO.writeString(writer, "B ");
				break;
			case Font.ITALIC:
				UnicodeIO.writeString(writer, " I");
				break;
			default:
				UnicodeIO.writeString(writer, "BI");
				break;
			}
			UnicodeIO.writeString(writer,
					String.valueOf(inf.getInput().getSize()) + "\n");
			UnicodeIO.writeString(writer, "OFONT "
					+ inf.getOutput().getFont().getName() + ":");
			switch (inf.getOutput().getFont().getStyle()) {
			case Font.PLAIN:
				UnicodeIO.writeString(writer, "  ");
				break;
			case Font.BOLD:
				UnicodeIO.writeString(writer, "B ");
				break;
			case Font.ITALIC:
				UnicodeIO.writeString(writer, " I");
				break;
			default:
				UnicodeIO.writeString(writer, "BI");
				break;
			}
			UnicodeIO.writeString(writer,
					String.valueOf(inf.getOutput().getSize()) + "\n");
			UnicodeIO.writeString(
					writer,
					"BCOLOR "
							+ String.valueOf(16777216 + inf
									.getBackgroundColor().getRGB()) + "\n");
			UnicodeIO.writeString(
					writer,
					"FCOLOR "
							+ String.valueOf(16777216 + inf
									.getForegroundColor().getRGB()) + "\n");
			UnicodeIO.writeString(
					writer,
					"ACOLOR "
							+ String.valueOf(16777216 + inf.getSubgraphColor()
									.getRGB()) + "\n");
			UnicodeIO.writeString(
					writer,
					"SCOLOR "
							+ String.valueOf(16777216 + inf.getCommentColor()
									.getRGB()) + "\n");
			UnicodeIO.writeString(
					writer,
					"CCOLOR "
							+ String.valueOf(16777216 + inf.getSelectedColor()
									.getRGB()) + "\n");
			UnicodeIO.writeString(writer, "DBOXES y\n");
			if (inf.isFrame())
				UnicodeIO.writeString(writer, "DFRAME y\n");
			else
				UnicodeIO.writeString(writer, "DFRAME n\n");
			if (inf.isDate())
				UnicodeIO.writeString(writer, "DDATE y\n");
			else
				UnicodeIO.writeString(writer, "DDATE n\n");
			if (inf.isFilename())
				UnicodeIO.writeString(writer, "DFILE y\n");
			else
				UnicodeIO.writeString(writer, "DFILE n\n");
			if (inf.isPathname())
				UnicodeIO.writeString(writer, "DDIR y\n");
			else
				UnicodeIO.writeString(writer, "DDIR n\n");
			if (inf.isRightToLeft())
				UnicodeIO.writeString(writer, "DRIG y\n");
			else
				UnicodeIO.writeString(writer, "DRIG n\n");
			UnicodeIO.writeString(writer, "DRST n\n");
			UnicodeIO.writeString(writer, "FITS 100\n");
			UnicodeIO.writeString(writer, "PORIENT L\n");
			UnicodeIO.writeString(writer, "#\n");
			nBoxes = boxes.size();
			UnicodeIO.writeString(writer, String.valueOf(nBoxes) + "\n");
			for (int i = 0; i < nBoxes; i++) {
				final TfstGraphBox g = (TfstGraphBox) boxes.get(i);
				UnicodeIO.writeChar(writer, '"');
				int N = g.getTransitions().size();
				if (g.getType() != GenericGraphBox.FINAL) {
					String foo = g.getContent();
					if (i == 2
							&& foo.equals("THIS SENTENCE AUTOMATON HAS BEEN EMPTIED")) {
						foo = "<E>";
						N = 0;
					}
					if (!foo.equals("<E>")) {
						if (g.getBounds() != null) {
							foo = foo + "/" + g.getBounds();
						} else {
							/* Should not happen */
							throw new AssertionError(
									"Bounds should not be null for a box content != <E>");
						}
					}
					writeBoxContent(writer, foo);
				}
				UnicodeIO.writeString(
						writer,
						"\" " + String.valueOf(g.getX()) + " "
								+ String.valueOf(g.getY()) + " "
								+ String.valueOf(N) + " ");
				for (int j = 0; j < N; j++) {
					final TfstGraphBox tmp = (TfstGraphBox) g.getTransitions()
							.get(j);
					UnicodeIO.writeString(writer,
							String.valueOf(boxes.indexOf(tmp)) + " ");
				}
				UnicodeIO.writeChar(writer, '\n');
			}
			writer.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public GraphPresentationInfo getInfo() {
		return info;
	}

	public ArrayList<GenericGraphBox> getBoxes() {
		return boxes;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getnBoxes() {
		return nBoxes;
	}

	public File getGrf() {
		return grf;
	}

	public GraphMetaData getMetadata() {
		return metadata;
	}

	public static void createNewGrf(File f) {
		final GraphIO gio = createEmptyGraphIO();
		gio.saveGraph(f);
	}

	private static GraphIO createEmptyGraphIO() {
		final GraphIO gio = new GraphIO();
		gio.width = 1188;
		gio.height = 840;
		gio.boxes = new ArrayList<GenericGraphBox>();
		GraphBox b = new GraphBox(70, 200, 0, null);
		b.setContent("<E>");
		gio.boxes.add(b);
		b = new GraphBox(300, 200, 1, null);
		b.setContent("");
		gio.boxes.add(b);
		return gio;
	}

}
