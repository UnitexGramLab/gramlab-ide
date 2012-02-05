/*
 * Unitex
 *
 * Copyright (C) 2001-2012 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package fr.umlv.unitex.process.list;

import javax.swing.DefaultListModel;

import fr.umlv.unitex.console.Couple;

/**
 * This class provides a list model with a method to replace the last element.
 * It is used for console message: when a message is just ended by \r, we
 * interprete it as a line that must erase the previous one.
 * 
 * @author Sébastien Paumier
 */
public class ProcessOutputListModel extends DefaultListModel {
	
	void replaceLastLine(Couple c) {
		final int size = size();
		if (size == 0) {
			super.addElement(c);
		} else {
			set(size - 1, c);
		}
	}
	
	@Override
	public void addElement(Object obj) {
		throw new UnsupportedOperationException("You should not invoke this method. Please use addLine and replaceLastLine from ProcessOutputList");
	}
	
	void addLine(Couple c) {
		super.addElement(c);
	}
}
