/*
 * XAlign
 *
 * Copyright (C) LORIA
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
/* 
 * @(#)       MultiAlignProperties.java
 * 
 * Created    Tue Sep 14 18:54:23 1999
 * 
 * Copyright  1999 (C) PATRICE BONHOMME
 *            UMR LORIA (Universities of Nancy, CNRS & INRIA)
 *            
 */
package fr.loria.xsilfide.multialign;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Patrice Bonhomme
 */
class MultiAlignProperties extends java.util.Properties {
	private String filename;

	public MultiAlignProperties(String filename) {
		this.filename = filename;
	}

	public void load(String filename1) throws FileNotFoundException,
			IOException {
		this.filename = filename1;
		load();
	}

	void load() throws FileNotFoundException, IOException {
		final FileInputStream f = new FileInputStream(filename);
		load(f);
	}
}
// EOF MultiAlignProperties
