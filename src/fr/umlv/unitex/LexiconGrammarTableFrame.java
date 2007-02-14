 /*
  * Unitex
  *
  * Copyright (C) 2001-2007 Université de Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import fr.umlv.unitex.exceptions.*;
import fr.umlv.unitex.io.*;

/**
 * This class is used to display a lexicon-grammar table
 * 
 * @author Sébastien Paumier
 */
public class LexiconGrammarTableFrame extends JInternalFrame {

	static LexiconGrammarTableFrame frame;
	private Vector<Vector<String>> rowData;
	private Vector<String> columnNames;
	private static String tableName;

	/**
	 * Constructs a new <code>LexiconGrammarTableFrame</code>, loads a
	 * lexicon-grammar table and shows the frame.
	 * 
	 * @param file
	 *            the lexicon-grammar table file
	 */
	public LexiconGrammarTableFrame(File file) {
		super(file.getAbsolutePath(), true, true, true, true);
		setTableName(file.getAbsolutePath());
		buildVectors(file);
		JPanel top = new JPanel();
		top.setOpaque(true);
		top.setLayout(new BorderLayout());
		JTable table = new JTable(rowData, columnNames);
		table.setFont(Preferences.getCloneOfPreferences().textFont);
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane scroll = new JScrollPane(table);
		scroll
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		top.add(scroll, BorderLayout.CENTER);
		setContentPane(top);
		pack();
		setBounds(100, 100, 800, 600);
		setVisible(true);
		frame = this;
		UnitexFrame.addInternalFrame(this);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				UnitexFrame.mainFrame.compileLexiconGrammar.setEnabled(false);
				UnitexFrame.mainFrame.closeLexiconGrammar.setEnabled(false);
				setVisible(false);
				UnitexFrame.removeInternalFrame(frame);
				frame = null;
			}
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	/**
	 * Closes the frame
	 *  
	 */
	public static void closeFrame() {
		if (frame != null) {
			UnitexFrame.mainFrame.compileLexiconGrammar.setEnabled(false);
			UnitexFrame.mainFrame.closeLexiconGrammar.setEnabled(false);
			frame.setVisible(false);
			UnitexFrame.removeInternalFrame(frame);
			frame = null;
		}
	}

	private void buildVectors(File f) {
		if (!f.exists()) {
			JOptionPane.showMessageDialog(null, "Cannot find " + f.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!f.canRead()) {
			JOptionPane.showMessageDialog(null, "Cannot read " + f.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (f.length() <= 2) {
			JOptionPane.showMessageDialog(null, f.getAbsolutePath() + " is empty", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		FileInputStream source;
		try {
			source = UnicodeIO.openUnicodeLittleEndianFileInputStream(f);
			rowData = new Vector<Vector<String>>();
			// we process the column names first
			columnNames = tokenizeToVector(UnicodeIO.readLine(source));
			// and then the lines
			while (source.available() != 0) {
				rowData.add(tokenizeToVector(UnicodeIO.readLine(source)));
			}
			source.close();
		} catch (NotAUnicodeLittleEndianFileException e) {
			JOptionPane.showMessageDialog(null, f.getAbsolutePath()
					+ " is not a Unicode Little-Endian table file", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;

		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			return;
		}
	}

	private Vector<String> tokenizeToVector(String s) {
		Vector<String> res = new Vector<String>();
		StringTokenizer st = new StringTokenizer(s, "\t");
		if (st == null)
			return res;
		while (st.hasMoreElements()) {
			res.add(st.nextToken());
		}
		return res;
	}

	private static void setTableName(String name) {
		tableName = name;
	}

	/**
	 * 
	 * @return the name of the table
	 */
	public static String getTableName() {
		return tableName;
	}

}