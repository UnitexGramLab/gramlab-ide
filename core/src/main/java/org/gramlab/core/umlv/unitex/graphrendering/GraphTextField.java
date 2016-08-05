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
package org.gramlab.core.umlv.unitex.graphrendering;

import javax.swing.JTextField;

/**
 * Ancestor of graph box text editor.
 * 
 * @author paumier
 */
public abstract class GraphTextField extends JTextField {
	GraphTextField(int n) {
		super(n);
	}

	/**
	 * Sets the content of the text editor, making it editable or not depending
	 * on s
	 * 
	 * @param s
	 */
	public abstract void setContent(String s);
	
	
	/**
	 * Tests if the content of the text editor is modified
	 * (contributed by Nebojsa Vasiljevic)
	 * 
	 * @return <code>true</code> if the content is modified,
	 *         <code>false</code> otherwise
	 */
	public abstract boolean isModified();

	/**
	 * Tests if the current content is valid and if so, validates it by
	 * committing the content to the selected boxes.
	 * 
	 * @return <code>true</code> if boxes have actually been modified,
	 *         <code>false</code> otherwise
	 */
	public abstract boolean validateContent();
}
