/*
 * Unitex
 *
 * Copyright (C) 2001-2010 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import fr.umlv.unitex.GenericGraphBox;
import fr.umlv.unitex.GenericGraphicalZone;
import fr.umlv.unitex.GraphBox;
import fr.umlv.unitex.GraphPresentationInfo;
import fr.umlv.unitex.Preferences;
import fr.umlv.unitex.TfstGraphBox;
import fr.umlv.unitex.exceptions.NotAUnicodeLittleEndianFileException;

/**
 * This class provides methods for loading and saving graphs.
 * 
 * @author Sébastien Paumier
 * 
 */
public class GraphIO {

	/**
	 * Boxes of a graph
	 */
	public ArrayList<GenericGraphBox> boxes;

	/**
	 * Rendering properties of a graph
	 */
	public GraphPresentationInfo info;

	/**
	 * Width of a graph
	 */
	public int width;

	/**
	 * Height of a graph
	 */
	public int height;

	/**
	 * Number of boxes of a graph
	 */
	private int nBoxes;


	public File grf;
	
	public GraphIO() {
		info=Preferences.getGraphPresentationPreferences();
	}
	
	public GraphIO(GenericGraphicalZone zone) {
		info=zone.getGraphPresentationInfo();
		width=zone.getWidth();
		height=zone.getHeight();
		boxes=zone.getBoxes();
	}
	
	/**
	 * This method loads a graph.
	 * 
	 * @param grfFile
	 *            name of the graph
	 * @return a <code>GraphIO</code> object describing the graph
	 */
	public static GraphIO loadGraph(File grfFile) {
		GraphIO res = new GraphIO();
		res.grf=grfFile;
		FileInputStream source;
		if (!grfFile.exists()) {
			JOptionPane.showMessageDialog(null, "Cannot find "
					+ grfFile.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (!grfFile.canRead()) {
			JOptionPane.showMessageDialog(null, "Cannot read "
					+ grfFile.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (grfFile.length() <= 2) {
			JOptionPane.showMessageDialog(null, grfFile.getAbsolutePath()
					+ " is empty", "Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		try {
			source = UnicodeIO.openUnicodeLittleEndianFileInputStream(grfFile);
			UnicodeIO.skipLine(source); // ignoring #...
			res.readSize(source);
			res.readInputFont(source);
			res.readOutputFont(source);
			res.readBackgroundColor(source);
			res.readForegroundColor(source);
			res.readSubgraphColor(source);
			res.readCommentColor(source);
			res.readSelectedColor(source);
			UnicodeIO.skipLine(source); // ignoring DBOXES
			res.readDrawFrame(source);
			res.readDate(source);
			res.readFile(source);
			res.readDirectory(source);
			res.readRightToLeft(source);
			UnicodeIO.skipLine(source); // ignoring DRST
			UnicodeIO.skipLine(source); // ignoring FITS
			UnicodeIO.skipLine(source); // ignoring PORIENT
			UnicodeIO.skipLine(source); // ignoring #
			res.readBoxNumber(source);
			res.boxes = new ArrayList<GenericGraphBox>();
			// adding initial state
			res.boxes.add(new GraphBox(0, 0, 0, null));
			// adding final state
			res.boxes.add(new GraphBox(0, 0, 1, null));
			// adding other states
			for (int i = 2; i < res.nBoxes; i++)
				res.boxes.add(new GraphBox(0, 0, 2, null));
			for (int i = 0; i < res.nBoxes; i++)
				res.readGraphLine(source, i);
			source.close();
		} catch (NotAUnicodeLittleEndianFileException e) {
			JOptionPane.showMessageDialog(null, grfFile.getAbsolutePath()
					+ " is not a Unicode Little-Endian graph", "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return res;
	}

	private void readSize(FileInputStream f) {
		// skipping the chars preceeding the width and height
		UnicodeIO.skipChars(f, 5);
		char c;
		// reading width
		width = 0;
		while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(f))))
			width = width * 10 + (c - '0');
		// reading height
		while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(f))))
			height = height * 10 + (c - '0');
	}

	private void readInputFont(FileInputStream f) {
		UnicodeIO.skipChars(f, 5);
		String s = "";
		char c;
		while ((c = (char) UnicodeIO.readChar(f)) != ':')
			s = s + c;
		boolean bold = (UnicodeIO.readChar(f) == 'B');
		boolean italic = (UnicodeIO.readChar(f) == 'I');
		int size = 0;
		while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(f))))
			size = size * 10 + (c - '0');
		info.input.size = size;
		int style;
		if (bold && italic)
			style = Font.BOLD | Font.ITALIC;
		else if (bold)
			style = Font.BOLD;
		else if (italic)
			style = Font.ITALIC;
		else
			style = Font.PLAIN;
		info.input.font = new Font(s, style, (int) (size / 0.72));
	}

	private void readOutputFont(FileInputStream f) {
		UnicodeIO.skipChars(f, 6);
		String s = "";
		char c;
		while ((c = (char) UnicodeIO.readChar(f)) != ':')
			s = s + c;
		boolean bold = (UnicodeIO.readChar(f) == 'B');
		boolean italic = (UnicodeIO.readChar(f) == 'I');
		int size = 0;
		while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(f))))
			size = size * 10 + (c - '0');
		info.output.size = size;
		int style;
		if (bold && italic)
			style = Font.BOLD | Font.ITALIC;
		else if (bold)
			style = Font.BOLD;
		else if (italic)
			style = Font.ITALIC;
		else
			style = Font.PLAIN;
		info.output.font = new Font(s, style, (int) (size / 0.72));
	}

	private void readBackgroundColor(FileInputStream f) {
		UnicodeIO.skipChars(f, 7);
		char c;
		int n = 0;
		while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(f))))
			n = n * 10 + (c - '0');
		info.backgroundColor = new Color(n);
	}

	private void readForegroundColor(FileInputStream f) {
		UnicodeIO.skipChars(f, 7);
		char c;
		int n = 0;
		while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(f))))
			n = n * 10 + (c - '0');
		info.foregroundColor = new Color(n);
	}

	private void readSubgraphColor(FileInputStream f) {
		UnicodeIO.skipChars(f, 7);
		char c;
		int n = 0;
		while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(f))))
			n = n * 10 + (c - '0');
		info.subgraphColor = new Color(n);
	}

	private void readSelectedColor(FileInputStream f) {
		UnicodeIO.skipChars(f, 7);
		char c;
		int n = 0;
		while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(f))))
			n = n * 10 + (c - '0');
		info.selectedColor = new Color(n);
	}

	private void readCommentColor(FileInputStream f) {
		UnicodeIO.skipChars(f, 7);
		char c;
		int n = 0;
		while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(f))))
			n = n * 10 + (c - '0');
		info.commentColor = new Color(n);
	}

	private void readDrawFrame(FileInputStream f) {
		UnicodeIO.skipChars(f, 7);
		info.frame = (UnicodeIO.readChar(f) == 'y');
		UnicodeIO.readChar(f);
	}

	private void readDate(FileInputStream f) {
		UnicodeIO.skipChars(f, 6);
		info.date = (UnicodeIO.readChar(f) == 'y');
		UnicodeIO.readChar(f);
	}

	private void readFile(FileInputStream f) {
		UnicodeIO.skipChars(f, 6);
		info.filename = (UnicodeIO.readChar(f) == 'y');
		UnicodeIO.readChar(f);
	}

	private void readDirectory(FileInputStream f) {
		UnicodeIO.skipChars(f, 5);
		info.pathname = (UnicodeIO.readChar(f) == 'y');
		UnicodeIO.readChar(f);
	}

	private void readRightToLeft(FileInputStream f) {
		UnicodeIO.skipChars(f, 5);
		info.rightToLeft = (UnicodeIO.readChar(f) == 'y');
		UnicodeIO.readChar(f);
	}

	private void readBoxNumber(FileInputStream f) {
		char c;
		nBoxes = 0;
		while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(f))))
			nBoxes = nBoxes * 10 + (c - '0');
	}

	private void readGraphLine(FileInputStream f, int n) {
		GenericGraphBox g = boxes.get(n);
		if (UnicodeIO.readChar(f) == 's') {
			// is a "s" was read, then we read the " char
			UnicodeIO.readChar(f);
		}
		String s = "";
		char c;
		while ((c = (char) UnicodeIO.readChar(f)) != '"') {
			if (c == '\\') {
				c = (char) UnicodeIO.readChar(f);
				if (c != '\\') {
					// case of \: \+ and \"
					if (c == '"')
						s = s + c;
					else
						s = s + "\\" + c;
				} else {
					// case of \\\" that must be transformed into \"
					c = (char) UnicodeIO.readChar(f);
					if (c == '\\') {
						// we are in the case \\\" -> \"
						c = (char) UnicodeIO.readChar(f);
						s = s + "\\" + c;
					} else {
						// we are in the case \\a -> \\a
						s = s + "\\\\";
						if (c != '"')
							s = s + c;
						else
							break;
					}
				}
			} else
				s = s + c;
		}
		// skipping the space after "
		UnicodeIO.readChar(f);
		// reading the X coordinate
		int x = 0;
		int neg = 1;
		c = (char) UnicodeIO.readChar(f);
		if (c == '-') {
			neg = -1;
		} else if (UnicodeIO.isDigit(c)) {
			x = (c - '0');
		}
		while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(f)))) {
			x = x * 10 + (c - '0');
		}
		x = x * neg;
		// reading the Y coordinate
		int y = 0;
		neg = 1;
		c = (char) UnicodeIO.readChar(f);
		if (c == '-') {
			neg = -1;
		} else if (UnicodeIO.isDigit(c)) {
			y = (c - '0');
		}
		while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(f)))) {
			y = y * 10 + (c - '0');
		}
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
		while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(f))))
			trans = trans * 10 + (c - '0');
		for (int j = 0; j < trans; j++) {
			int dest = 0;
			while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(f))))
				dest = dest * 10 + (c - '0');
			g.addTransitionTo(boxes.get(dest));
		}
		// skipping the end-of-line
		UnicodeIO.readChar(f);
	}

	/**
	 * Saves the graph referenced by the field of this <code>GraphIO</code>
	 * object
	 * 
	 * @param grfFile
	 *            graph file
	 */
	public void saveGraph(File grfFile) {
		if (info==null) {
			throw new IllegalStateException("Should not save a graph with null graph information");
		}
		FileOutputStream dest;
		try {
			if (!grfFile.exists())
				grfFile.createNewFile();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Cannot write "
					+ grfFile.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!grfFile.canWrite()) {
			JOptionPane.showMessageDialog(null, "Cannot write "
					+ grfFile.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			dest = new FileOutputStream(grfFile);
			UnicodeIO.writeChar(dest, (char) 0xFEFF);
			UnicodeIO.writeString(dest, "#Unigraph\n");
			UnicodeIO.writeString(dest, "SIZE " + String.valueOf(width) + " "
					+ String.valueOf(height) + "\n");
			UnicodeIO.writeString(dest, "FONT " + info.input.font.getName() + ":");
			switch(info.input.font.getStyle()) {
				case Font.PLAIN: UnicodeIO.writeString(dest, "  "); break;
				case Font.BOLD: UnicodeIO.writeString(dest, "B "); break;
				case Font.ITALIC: UnicodeIO.writeString(dest, " I"); break;
				default: UnicodeIO.writeString(dest, "BI"); break;
			}
			UnicodeIO.writeString(dest, String.valueOf(info.input.size) + "\n");
			UnicodeIO.writeString(dest, "OFONT " + info.output.font.getName() + ":");
			switch(info.output.font.getStyle()) {
				case Font.PLAIN: UnicodeIO.writeString(dest, "  "); break;
				case Font.BOLD: UnicodeIO.writeString(dest, "B "); break;
				case Font.ITALIC: UnicodeIO.writeString(dest, " I"); break;
				default: UnicodeIO.writeString(dest, "BI"); break;
			}
			UnicodeIO.writeString(dest, String.valueOf(info.output.size) + "\n");
			UnicodeIO.writeString(dest, "BCOLOR "
					+ String.valueOf(16777216 + info.backgroundColor.getRGB())
					+ "\n");
			UnicodeIO.writeString(dest, "FCOLOR "
					+ String.valueOf(16777216 + info.foregroundColor.getRGB())
					+ "\n");
			UnicodeIO.writeString(dest, "ACOLOR "
					+ String.valueOf(16777216 + info.subgraphColor.getRGB())
					+ "\n");
			UnicodeIO.writeString(dest, "SCOLOR "
					+ String.valueOf(16777216 + info.commentColor.getRGB())
					+ "\n");
			UnicodeIO.writeString(dest, "CCOLOR "
					+ String.valueOf(16777216 + info.selectedColor.getRGB())
					+ "\n");
			UnicodeIO.writeString(dest, "DBOXES y\n");
			if (info.frame)
				UnicodeIO.writeString(dest, "DFRAME y\n");
			else
				UnicodeIO.writeString(dest, "DFRAME n\n");
			if (info.date)
				UnicodeIO.writeString(dest, "DDATE y\n");
			else
				UnicodeIO.writeString(dest, "DDATE n\n");
			if (info.filename)
				UnicodeIO.writeString(dest, "DFILE y\n");
			else
				UnicodeIO.writeString(dest, "DFILE n\n");
			if (info.pathname)
				UnicodeIO.writeString(dest, "DDIR y\n");
			else
				UnicodeIO.writeString(dest, "DDIR n\n");
			if (info.rightToLeft)
				UnicodeIO.writeString(dest, "DRIG y\n");
			else
				UnicodeIO.writeString(dest, "DRIG n\n");
			UnicodeIO.writeString(dest, "DRST n\n");
			UnicodeIO.writeString(dest, "FITS 100\n");
			UnicodeIO.writeString(dest, "PORIENT L\n");
			UnicodeIO.writeString(dest, "#\n");
			nBoxes = boxes.size();
			UnicodeIO.writeString(dest, String.valueOf(nBoxes) + "\n");
			for (int i = 0; i < nBoxes; i++) {
				GenericGraphBox g = boxes.get(i);
				UnicodeIO.writeChar(dest, '"');
				if (g.getType() != 1)
					write_content(dest, g.getContent());
				int N = g.getTransitions().size();
				UnicodeIO.writeString(dest, "\" " + String.valueOf(g.getX())
						+ " " + String.valueOf(g.getY()) + " "
						+ String.valueOf(N) + " ");
				for (int j = 0; j < N; j++) {
					GenericGraphBox tmp = g.getTransitions().get(j);
					UnicodeIO.writeString(dest, String.valueOf(boxes
							.indexOf(tmp))
							+ " ");
				}
				UnicodeIO.writeChar(dest, '\n');
			}
			dest.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void write_content(FileOutputStream f, String s) {
		int L = s.length();
		char c;
		for (int i = 0; i < L; i++) {
			c = s.charAt(i);
			if (c == '"') {
				// case of char "
				if (i == 0 || s.charAt(i - 1) != '\\') {
					// the " is the "abc" one; it must be saved as \"
					UnicodeIO.writeChar(f, '\\');
					UnicodeIO.writeChar(f, '"');
				} else {
					// it is the \" char that must be saved as \\\"
					// we only write 2 \ because the third has been saved at the
					// pos i-1
					UnicodeIO.writeChar(f, '\\');
					UnicodeIO.writeChar(f, '\\');
					UnicodeIO.writeChar(f, '"');
				}
			} else {
				UnicodeIO.writeChar(f, c);
			}
		}
	}

	/**
	 * This method loads a sentence graph.
	 * 
	 * @param file
	 *            sentence graph file
	 * @return a <code>GraphIO</code> object describing the sentence graph
	 */
	public static GraphIO loadSentenceGraph(File file) {
		GraphIO res = new GraphIO();
		res.grf=file;
		FileInputStream source;
		if (!file.exists()) {
			JOptionPane.showMessageDialog(null, "Cannot find "
					+ file.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (!file.canRead()) {
			JOptionPane.showMessageDialog(null, "Cannot read "
					+ file.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (file.length() <= 2) {
			JOptionPane.showMessageDialog(null, file.getAbsolutePath()
					+ " is empty", "Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		try {
			source = UnicodeIO.openUnicodeLittleEndianFileInputStream(file);
			UnicodeIO.skipLine(source); // ignoring #...
			res.readSize(source);
			res.readInputFont(source);
			res.readOutputFont(source);
			res.readBackgroundColor(source);
			res.readForegroundColor(source);
			res.readSubgraphColor(source);
			res.readCommentColor(source);
			res.readSelectedColor(source);
			UnicodeIO.skipLine(source); // ignoring DBOXES
			res.readDrawFrame(source);
			res.readDate(source);
			res.readFile(source);
			res.readDirectory(source);
			res.readRightToLeft(source);
			UnicodeIO.skipLine(source); // ignoring DRST
			UnicodeIO.skipLine(source); // ignoring FITS
			UnicodeIO.skipLine(source); // ignoring PORIENT
			UnicodeIO.skipLine(source); // ignoring #
			res.readBoxNumber(source);
			res.boxes = new ArrayList<GenericGraphBox>();
			// adding initial state
			res.boxes.add(new TfstGraphBox(0, 0, 0, null));
			// adding final state
			res.boxes.add(new TfstGraphBox(0, 0, 1, null));
			// adding other states
			for (int i = 2; i < res.nBoxes; i++)
				res.boxes.add(new TfstGraphBox(0, 0, 2, null));
			for (int i = 0; i < res.nBoxes; i++)
				res.readSentenceGraphLine(source, i);
			source.close();
		} catch (NotAUnicodeLittleEndianFileException e) {
			JOptionPane.showMessageDialog(null, file.getAbsolutePath()
					+ " is not a Unicode Little-Endian graph", "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return res;
	}

	private void readSentenceGraphLine(FileInputStream f, int n) {
		TfstGraphBox g = (TfstGraphBox) boxes.get(n);
		if (UnicodeIO.readChar(f) == 's') {
			// is a "s" was read, then we read the " char
			UnicodeIO.readChar(f);
		}
		String s = "";
		char c;
		while ((c = (char) UnicodeIO.readChar(f)) != '"') {
			if (c == '\\') {
				c = (char) UnicodeIO.readChar(f);
				if (c != '\\') {
					// case of \: \+ and \"
					if (c == '"')
						s = s + c;
					else
						s = s + "\\" + c;
				} else {
					// case of \\\" that must must be transformed into \"
					c = (char) UnicodeIO.readChar(f);
					if (c == '\\') {
						// we are in the case \\\" -> \"
						c = (char) UnicodeIO.readChar(f);
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
		UnicodeIO.readChar(f);
		// reading the X coordinate
		int x = 0;
		while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(f))))
			x = x * 10 + (c - '0');
		// reading the Y coordinate
		int y = 0;
		while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(f))))
			y = y * 10 + (c - '0');
		if (Preferences.getGraphPresentationPreferences().rightToLeft == true || info.rightToLeft == true) {
			info.rightToLeft = true;
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
		while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(f))))
			trans = trans * 10 + (c - '0');
		for (int j = 0; j < trans; j++) {
			int dest = 0;
			while (UnicodeIO.isDigit((c = (char) UnicodeIO.readChar(f))))
				dest = dest * 10 + (c - '0');
			g.addTransitionTo(boxes.get(dest));
		}
		// skipping the end-of-line
		UnicodeIO.readChar(f);
	}

	/**
	 * Saves the sentence graph described by the fields of this
	 * <code>GraphIO</code> object.
	 * 
	 * @param file
	 *            sentence graph file
	 */
	public void saveSentenceGraph(File file,GraphPresentationInfo inf) {
		FileOutputStream dest;
		try {
			if (!file.exists())
				file.createNewFile();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Cannot write "
					+ file.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!file.canWrite()) {
			JOptionPane.showMessageDialog(null, "Cannot write "
					+ file.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			dest = new FileOutputStream(file);
			UnicodeIO.writeChar(dest, (char) 0xFEFF);
			UnicodeIO.writeString(dest, "#Unigraph\n");
			UnicodeIO.writeString(dest, "SIZE " + String.valueOf(width) + " "
					+ String.valueOf(height) + "\n");
			UnicodeIO.writeString(dest, "FONT " + inf.input.font.getName() + ":");
			switch (inf.input.font.getStyle()) {
			case Font.PLAIN: UnicodeIO.writeString(dest, "  "); break;
			case Font.BOLD: UnicodeIO.writeString(dest, "B "); break;
			case Font.ITALIC: UnicodeIO.writeString(dest, " I"); break;
			default: UnicodeIO.writeString(dest, "BI"); break;
			}
			UnicodeIO.writeString(dest, String.valueOf(inf.input.size) + "\n");
			UnicodeIO.writeString(dest, "OFONT " + inf.output.font.getName() + ":");
			switch (inf.output.font.getStyle()) {
			case Font.PLAIN: UnicodeIO.writeString(dest, "  "); break;
			case Font.BOLD: UnicodeIO.writeString(dest, "B "); break;
			case Font.ITALIC: UnicodeIO.writeString(dest, " I"); break;
			default: UnicodeIO.writeString(dest, "BI"); break;
			}
			/* TODO factoriser tout ça avec le code de sauvegarde des grf normaux */
			UnicodeIO.writeString(dest, String.valueOf(inf.output.size) + "\n");
			UnicodeIO.writeString(dest, "BCOLOR "
					+ String.valueOf(16777216 + inf.backgroundColor.getRGB())
					+ "\n");
			UnicodeIO.writeString(dest, "FCOLOR "
					+ String.valueOf(16777216 + inf.foregroundColor.getRGB())
					+ "\n");
			UnicodeIO.writeString(dest, "ACOLOR "
					+ String.valueOf(16777216 + inf.subgraphColor.getRGB())
					+ "\n");
			UnicodeIO.writeString(dest, "SCOLOR "
					+ String.valueOf(16777216 + inf.commentColor.getRGB())
					+ "\n");
			UnicodeIO.writeString(dest, "CCOLOR "
					+ String.valueOf(16777216 + inf.selectedColor.getRGB())
					+ "\n");
			UnicodeIO.writeString(dest, "DBOXES y\n");
			if (inf.frame)
				UnicodeIO.writeString(dest, "DFRAME y\n");
			else
				UnicodeIO.writeString(dest, "DFRAME n\n");
			if (inf.date)
				UnicodeIO.writeString(dest, "DDATE y\n");
			else
				UnicodeIO.writeString(dest, "DDATE n\n");
			if (inf.filename)
				UnicodeIO.writeString(dest, "DFILE y\n");
			else
				UnicodeIO.writeString(dest, "DFILE n\n");
			if (inf.pathname)
				UnicodeIO.writeString(dest, "DDIR y\n");
			else
				UnicodeIO.writeString(dest, "DDIR n\n");
			if (inf.rightToLeft)
				UnicodeIO.writeString(dest, "DRIG y\n");
			else
				UnicodeIO.writeString(dest, "DRIG n\n");
			UnicodeIO.writeString(dest, "DRST n\n");
			UnicodeIO.writeString(dest, "FITS 100\n");
			UnicodeIO.writeString(dest, "PORIENT L\n");
			UnicodeIO.writeString(dest, "#\n");
			nBoxes = boxes.size();
			UnicodeIO.writeString(dest, String.valueOf(nBoxes) + "\n");
			for (int i = 0; i < nBoxes; i++) {
				TfstGraphBox g = (TfstGraphBox) boxes.get(i);
				UnicodeIO.writeChar(dest, '"');
				int N = g.getTransitions().size();
				if (g.getType() != GenericGraphBox.FINAL) {
					String foo = g.getContent();
					if (i == 2
							&& foo
									.equals("THIS SENTENCE AUTOMATON HAS BEEN EMPTIED")) {
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
					write_content(dest, foo);
				}
				UnicodeIO.writeString(dest, "\" " + String.valueOf(g.getX())
						+ " " + String.valueOf(g.getY()) + " "
						+ String.valueOf(N) + " ");
				for (int j = 0; j < N; j++) {
					TfstGraphBox tmp = (TfstGraphBox) g.getTransitions().get(j);
					UnicodeIO.writeString(dest, String.valueOf(boxes
							.indexOf(tmp))
							+ " ");
				}
				UnicodeIO.writeChar(dest, '\n');
			}
			dest.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public GraphPresentationInfo getGraphPresentationInfo() {
		return info;
	}

}