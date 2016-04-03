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
package fr.umlv.unitex.xalign;

import java.util.List;

import javax.swing.ListModel;

/**
 * This is a model for representing a XML text file as the list of its
 * sentences. It is used for alignements.
 * 
 * @author Sébastien Paumier
 */
public interface XMLTextModel extends ListModel {
	/**
	 * Returns the number of sentences to be displayed.
	 */
	@Override
	public int getSize();

	/**
	 * Returns the text corresponding to the sentence #i.
	 */
	@Override
	public String getElementAt(int i);

	/**
	 * Returns the number of the sentence corresponding to the given Xalign ID.
	 */
	public int getIndex(String s);

	/**
	 * Returns the Xalign ID of the sentence #index.
	 */
	public String getID(int index);

	/**
	 * Adds sentences to the model.
	 */
	public void addSentences(List<Sentence> sentence);

	public void reset();
}
