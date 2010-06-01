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

package fr.umlv.unitex;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.Caret;
import javax.swing.text.Document;

import fr.umlv.unitex.frames.TextAutomatonFrame;

/**
 * This class describes the text field used to get the box text in a sentence
 * graph.
 * 
 * @author Sébastien Paumier
 * 
 */
public class TfstTextField extends JTextField {

	protected TextAutomatonFrame parent;
	protected boolean modified = false;
	protected Clipboard clip;

	/**
	 * Constructs a new empty <code>FstTextField</code>.
	 * 
	 * @param n
	 *            number of columns
	 * @param p
	 *            frame that contains this component
	 */
	public TfstTextField(int n, TextAutomatonFrame p) {
		super(n);
		setEditable(false);
		modified = false;
		parent = p;
		clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		setDisabledTextColor(Color.white);
		setBackground(Color.white);
		addKeyListener(new MyKeyListener());
	}


	/**
	 * Sets the content of the text field
	 * 
	 * @param s
	 *            the new content
	 */
	public void initText(String s) {
		modified=false;
		if (s==null) {
			/* We want to make the text field non editable */
			setEditable(false);
			setText("");
			return;
		}
		setEditable(true);
		setText(s);
		requestFocus();
		getCaret().setVisible(true);
		selectAll();
	}

	/**
	 * Returns a new <code>ListDocument</code> object.
	 * 
	 * @return the <code>ListDocument</code>
	 */
	@Override
	public Document createDefaultModel() {
		return new ListDocument();
	}

	/**
	 * Validates the content of the text field as the content of selected boxes.
	 * 
	 * @return <code>true</code> if the content was valid, <code>false</code>
	 *         otherwise
	 */
	public boolean validateTextField() {
		if (!hasChangedTextField()) {
			return true;
		}
		if (isGoodText(getText())) {
			parent.getGraphicalZone().setTextForSelected(getText());
			parent.getGraphicalZone().unSelectAllBoxes();
			setText("");
			parent.getGraphicalZone().repaint();
			setEditable(false);
			parent.setModified(true);
			return true;
		}
		return false;
	}

	@Override
	public void setEditable(boolean b) {
		Caret caret = getCaret();
		if (caret != null) {
			caret.setVisible(b);
		}
		super.setEditable(b);
	}

	class MyKeyListener extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.isControlDown() || e.isAltDown()) {
				// if the control key or alt key is pressed, we do nothing: the
				// event we be caught by the ActionListeners
				return;
			}
			if (e.getKeyCode() == 10)
				validateTextField();
			modified = true;
		}

	}

	/**
	 * Tests if the content of the text field has changed.
	 * 
	 * @return <code>true</code> if the content has changed, <code>false</code>
	 *         otherwise
	 */
	public boolean hasChangedTextField() {
		return modified;
	}

	/**
	 * Tests if a content is a valid content for a sentence graph box.
	 * 
	 * @param s
	 *            the content to test
	 * @return <code>true</code> if the content is valid, <code>false</code>
	 *         otherwise
	 */
	public boolean isGoodText(String s) {
		if (s.equals(""))
			return true;
		char ligne[] = new char[10000];
		int i, L;

		ligne = s.toCharArray();
		L = s.length();
		if (ligne[0] != '{')
			return true;

		i = 1;
		while (i < L && ligne[i] != ',') {
			if (ligne[i] == '\\') {
				if (i < L) {
					i++;
				} else {
					JOptionPane.showMessageDialog(null,
							"Unexpected '\\' at end of line", "Error",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}

			}
			i++;
		}
		if (i == L) {
			JOptionPane.showMessageDialog(null,
					"No ',' delimiting inflected part from canonical part",
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		i++;
		while (i < L && ligne[i] != '.') {
			if (ligne[i] == '\\') {
				if (i < L) {
					i++;
				} else {
					JOptionPane.showMessageDialog(null,
							"Unexpected '\\' at end of line", "Error",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}

			}
			i++;
		}
		if (i == L) {
			JOptionPane
					.showMessageDialog(
							null,
							"No '.' delimiting canonical part from grammatical informations",
							"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		i++;
		while (i < L && ligne[i] != '}') {
			if (ligne[i] == '\\') {
				if (i < L) {
					i++;
				} else {
					JOptionPane.showMessageDialog(null,
							"Unexpected '\\' at end of line", "Error",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}

			}
			i++;
		}
		if (i == L) {
			JOptionPane.showMessageDialog(null, "No closing '}'", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

}
