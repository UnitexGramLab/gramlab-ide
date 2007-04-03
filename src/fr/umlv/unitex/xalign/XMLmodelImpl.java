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
package fr.umlv.unitex.xalign;

import java.util.*;

public class XMLmodelImpl implements XMLmodel {

	private int size;
	private int seed;
	
	public XMLmodelImpl(int n, int seed) {
		size=n;
		this.seed=seed;
	}

	public int size() {
		return size;
	}

	public String get(int sentence) {
		Random r=new Random(sentence+seed);
		int n=r.nextInt(6)+1;
		String s="sentence #"+sentence+":\n";
		for (int i=0;i<=n;i++) {
			s=s+"pouet pouet pouet pouet "+r.nextInt()+"\n";
		}
		return s;
	}

}
