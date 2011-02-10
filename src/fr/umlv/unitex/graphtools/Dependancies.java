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
package fr.umlv.unitex.graphtools;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.Preferences;
import fr.umlv.unitex.graphrendering.GenericGraphBox;
import fr.umlv.unitex.io.GraphIO;

/**
 * This class provides methods for building graph dependencies.
 * 
 * @author Sébastien Paumier
 */
public class Dependancies {
	
	public static void main(String[] args) {
		whoCalls(new File("/home/paumier/unitex/French/Graphs/Preprocessing/Sentence/Nombres.grf"),new File("/home/paumier/unitex/French"));
	}
	
	/**
	 * Looks recursively in the given directory for all graphs that call the given one
	 * @param grf
	 * @param rootDir
	 * @return
	 */
	public static ArrayList<File> whoCalls(File grf,File rootDir) {
		ArrayList<File> callers=new ArrayList<File>();
		if (!rootDir.isDirectory()) throw new IllegalArgumentException("Directory expected");
		HashMap<File,ArrayList<File>> map=new HashMap<File, ArrayList<File>>();
		getAllGraphDependencies(rootDir,map);
		for (File f:map.keySet()) {
			if (map.get(f).contains(grf)) {
				callers.add(f);
				System.err.println(grf.getAbsolutePath()+" est appelé par:\n   "+f.getAbsolutePath());
			}
		}
		return callers;
	}
	
	private static void getAllGraphDependencies(File rootDir,
			HashMap<File, ArrayList<File>> map) {
		File[] files=rootDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				File tmp=new File(dir,name);
				return name.endsWith(".grf") || tmp.isDirectory();
			}
		});
		if (files==null) return;
		for (File f:files) {
			if (f.isFile()) 
				computeGraphDependencies(f,map);
			else getAllGraphDependencies(f,map);
		}
		
	}

	public static ArrayList<File> getSubgraphs(File grf,boolean emitErrorMessages) {
		GraphIO io = GraphIO.loadGraph(grf,false,false);
		if (io==null) return null;
		ArrayList<File> subgraphs=new ArrayList<File>();
		boolean[] marked=new boolean[io.boxes.size()];
		markAccessibleBoxes(io.boxes,marked,0);
		for (int i=0;i<io.boxes.size();i++) {
			if (marked[i]) {
				/* We only consider accessible boxes */
				addSubgraphs(subgraphs,io.boxes.get(i),grf,emitErrorMessages);
			}
		}
		return subgraphs;
	}

	public static void computeGraphDependencies(File grf,HashMap<File,ArrayList<File>> map) {
		if (map.containsKey(grf)) return;
		ArrayList<File> files=getSubgraphs(grf,false);
		if (files==null) return;
		map.put(grf,files);
		for (File f:files) {
			computeGraphDependencies(f,map);
		}
	}

	/**
	 * Adds to the given list the subgraphs contained in the given box
	 * @param subgraphs
	 * @param repositoryError 
	 * @param genericGraphBox
	 */
	private static void addSubgraphs(ArrayList<File> subgraphs,
			GenericGraphBox box,File parent,boolean emitErrorMessages) {
		for (int i=0;i<box.lines.size();i++) {
			if (box.greyed.get(i)) {
				/* If we have a subgraph call */
				File f=getSubgraph(box.lines.get(i),parent,emitErrorMessages);
				if (f!=null && !subgraphs.contains(f) && !f.equals(parent)) subgraphs.add(f);
			}
		}
	}

	private static File getSubgraph(String s, File parent,boolean emitErrorMessages) {
        if (!s.endsWith(".grf")) {
            s = s + ".grf";
        }
        /* replace ':' by '/' resp. '\\' */
        if (s.startsWith(":")) {
            // if the graph is located in the package repository
            s = s.replace(':', File.separatorChar);
            if (Preferences.packagePath()==null) {
                if (emitErrorMessages) 
                JOptionPane.showMessageDialog(null, "In graph "+parent.getAbsolutePath()+":\ncannot resolve relative graph path:\n\n"
                        + s + "\n\nbecause the location of the graph  repository is\n" +
                        "not defined.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return new File(Preferences.packagePath(), s.substring(1));
        }
        // otherwise
        File f = new File(s);
        if (Config.getCurrentSystem() == Config.WINDOWS_SYSTEM &&
                f.isAbsolute()) {
            // first we test if we have an absolute windows pathname,
            // in order to avoid wrong transformations like:
            //
            // C:\\foo\foo.grf  =>  C\\\foo\foo.grf
            //
            return f;
        }
        s = s.replace(':', File.separatorChar);
        if (!f.isAbsolute()) {
            f = new File(parent.getParentFile(), s);
        }
        return f;
	}

	private static void markAccessibleBoxes(ArrayList<GenericGraphBox> boxes,
			boolean[] marked, int n) {
		if (marked[n]==true) return;
		marked[n]=true;
		GenericGraphBox b=boxes.get(n);
		ArrayList<GenericGraphBox> dest=b.getTransitions();
		if (dest==null) return;
		for (GenericGraphBox box:dest) {
			markAccessibleBoxes(boxes,marked,boxes.indexOf(box));
		}
	}
}
