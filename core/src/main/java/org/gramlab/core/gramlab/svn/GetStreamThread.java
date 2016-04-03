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
package fr.gramlab.svn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fr.umlv.unitex.process.NullOutputStream;


public class GetStreamThread extends Thread {
	private final InputStream in;
	private final OutputStream out;
	private StringBuilder builder=new StringBuilder();

	public GetStreamThread(InputStream _in) {
		this(_in,new NullOutputStream());
	}

	public GetStreamThread(InputStream _in, OutputStream _out) {
		in = _in;
		out = _out;
	}

	private boolean done=false;
	
	@Override
	public void run() {
		try {
			int c;
			byte[] buffer=new byte[4096];
			while ((c = in.read(buffer)) != -1) {
				out.write(buffer,0,c);
				builder.append(new String(buffer,0,c));
			}
			if (!out.equals(System.out) && !out.equals(System.err)) {
				out.close();
			}
		} catch (IOException e) {
			/* Nothing to do */
		}
		done=true;
	}
	
	public String getStreamContent() {
		while (!done) {/* */}
		return builder.toString();
	}
}
