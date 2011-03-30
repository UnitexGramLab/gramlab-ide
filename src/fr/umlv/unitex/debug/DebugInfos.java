/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

package fr.umlv.unitex.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import fr.umlv.unitex.Util;
import fr.umlv.unitex.io.GraphIO;

public class DebugInfos {

	public File concordIndFile=null;
	public ArrayList<String> graphNames=new ArrayList<String>();
	public ArrayList<File> graphs=new ArrayList<File>();
	public ArrayList<String> lines=new ArrayList<String>();
	public HashMap<Integer,GraphIO> graphIOMap=new HashMap<Integer,GraphIO>();

	
	public static DebugInfos loadConcordanceIndex(File html) {
		String concord_ind=Util.getFileNameWithoutExtension(html)+".ind";
		File f=new File(concord_ind);
		if (!f.exists()) return null;
		Scanner scanner=null;
		try {
			scanner=new Scanner(f,"UTF-16LE");
			String z=scanner.nextLine();
			if (z.startsWith("\uFEFF")) {
				z=z.substring(1);
			}
			if (!z.startsWith("#D")) {
				scanner.close();
				return null;
			}
			DebugInfos infos=new DebugInfos();
			infos.concordIndFile=f;
			int n=scanner.nextInt();
			scanner.nextLine();
			Pattern normalDelimiter=scanner.delimiter();
			while (n>0) {
				scanner.useDelimiter(""+(char)1);
				String s=scanner.next();
				infos.graphNames.add(s);
				scanner.useDelimiter(normalDelimiter);
				/* We skip the delimiter char # 1*/
				s=scanner.nextLine().substring(1);
				infos.graphs.add(new File(s));
				n--;
			}
			/* We skip the #[IMR] line */
			scanner.nextLine();
			while (scanner.hasNextLine()) {
				/* We skip the match coordinates */
				scanner.next();
				scanner.next();
				/* We skip the normal output part */
				scanner.useDelimiter(""+(char)1);
				scanner.next();
				scanner.useDelimiter(normalDelimiter);
				infos.lines.add(scanner.nextLine());
			}
			scanner.close();
			return infos;
		} catch (FileNotFoundException e) {
			return null;
		} catch (NoSuchElementException e2) {
			if (scanner!=null) scanner.close();
			return null;
		}
	}
	
	
	public GraphIO getGraphIO(int n) {
		GraphIO gio=graphIOMap.get(Integer.valueOf(n));
		if (gio==null) {
			/* If we try to load a graph for the first time,
			 * we check if it has been modified since the concordance was built.
			 * Once loaded, we use the cached version, so that we are sure
			 * to debug on the correct version, even if the graph has been changed
			 * while debugging
			 */
			File f=graphs.get(n-1);
			if (f.lastModified()>concordIndFile.lastModified()) {
				JOptionPane
	            .showMessageDialog(
	                    null,
	                    "File "+f.getAbsolutePath()+ " has been modified\n"+
	                    "since the concordance index was built. Cannot debug it.",
	                    "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
			gio=GraphIO.loadGraph(f,false,false);
			if (gio==null) {
				JOptionPane
	            .showMessageDialog(
	                    null,
	                    "Cannot load graph "+f.getAbsolutePath(),
	                    "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
			graphIOMap.put(Integer.valueOf(n),gio);
		}
		return gio;
	}


}
