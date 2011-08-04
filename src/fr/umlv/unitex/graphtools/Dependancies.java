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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JOptionPane;

import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.config.Preferences;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.graphrendering.GenericGraphBox;
import fr.umlv.unitex.io.GraphIO;

/**
 * This class provides methods for building graph dependencies.
 * 
 * @author Sébastien Paumier
 */
public class Dependancies {
	
	
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
			}
		}
		Collections.sort(callers);
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
		boolean[] accessible=new boolean[io.boxes.size()];
		int[] coaccessible=new int[io.boxes.size()];
		for (int i=0;i<coaccessible.length;i++) coaccessible[i]=UNTESTED;
		markAccessibleBoxes(io.boxes,accessible,0);
		for (int i=0;i<io.boxes.size();i++) {
			if (accessible[i] && isCoaccessibleBoxes(io.boxes,coaccessible,i)) {
				/* We only consider useful boxes */
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

	public static ArrayList<File> getAllSubgraphs(File grf) {
		HashMap<File,ArrayList<File>> map=new HashMap<File, ArrayList<File>>();
		computeGraphDependencies(grf,map);
		ArrayList<File> files=new ArrayList<File>();
		for (File f:map.keySet()) {
			if (!f.equals(grf) && !files.contains(f)) {
				files.add(f);
			}
		}
		for (ArrayList<File> values:map.values()) {
			for (File f:values) {
				if (!files.contains(f)) {
					files.add(f);
				}	
			}
		}
		Collections.sort(files);
		return files;
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
            File repository=ConfigManager.getManager().getGraphRepositoryPath(null);
            if (repository==null) {
                if (emitErrorMessages) 
                JOptionPane.showMessageDialog(null, "In graph "+parent.getAbsolutePath()+":\ncannot resolve relative graph path:\n\n"
                        + s + "\n\nbecause the location of the graph  repository is\n" +
                        "not defined.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return new File(repository, s.substring(1));
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

	private final static int UNTESTED=0;
	private final static int TESTED_TRUE=1;
	private final static int TESTED_FALSE=2;
	private final static int BEING_TESTED=3;
	
	
	private static boolean isCoaccessibleBoxes(ArrayList<GenericGraphBox> boxes,
			int[] marked, int n) {
		if (marked[n]==TESTED_FALSE || marked[n]==TESTED_TRUE) return marked[n]==TESTED_TRUE;
		if (marked[n]==BEING_TESTED) return false;
		marked[n]=BEING_TESTED;
		GenericGraphBox b=boxes.get(n);
		if (b.type==GenericGraphBox.FINAL) {
			marked[n]=TESTED_TRUE;
			return true;
		}
		ArrayList<GenericGraphBox> dest=b.getTransitions();
		if (dest==null) {
			marked[n]=TESTED_FALSE;
			return false;
		}
		for (GenericGraphBox box:dest) {
			if (isCoaccessibleBoxes(boxes,marked,boxes.indexOf(box))) {
				marked[n]=TESTED_TRUE;
				return true;
			}
		}
		marked[n]=TESTED_FALSE;
		return false;
	}

}
