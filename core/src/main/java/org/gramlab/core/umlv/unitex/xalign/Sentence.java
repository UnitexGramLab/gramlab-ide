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
package org.gramlab.core.umlv.unitex.xalign;

/**
 * This class represents a sentence from a XAlign XML text file.
 * 
 * @author Sébastien Paumier
 */
class Sentence {
	/**
	 * XML id of the sentence.
	 */
	final String ID;
	/**
	 * Sentence bounds as offsets in the XML file.
	 */
	final long start;
	final long end;

	Sentence(String ID, int start, int end) {
		this.ID = ID;
		this.start = start;
		this.end = end;
	}
}
