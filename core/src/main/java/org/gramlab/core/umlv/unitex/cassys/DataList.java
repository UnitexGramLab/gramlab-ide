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
package fr.umlv.unitex.cassys;

/**
 * Class defining a row in the table of cassys configuration frame.
 * <p/>
 * A row is made of a <code>String</code> denoting a transducer file name and
 * two <code>Boolean</code> fields indicating whether merge mode and replace
 * mode are selected.
 * 
 * @author david nott
 */
public class DataList {
	
	/**
	 * Rank of transducer file
	 */
	private int rank; // -1 states for unranked
	/**
	 * Name of the transducer file
	 */
	private String name;
	/**
	 * Whether the <code>merge</code> option should be applied
	 */
	private boolean merge;
	/**
	 * Whether the <code>replace</code> option should be applied
	 */
	private boolean replace;
	/**
	 * Whether the transducer should be applied
	 */
	private boolean disabled;
	/**
	 * Whether the transducer should be stared
	 */
	private boolean star;
        /**
         * Whether the transducer is generic
         */
        private boolean generic;
        
	public static int UNRANKED = -1;
	
	/**
	 * Three parameters constructor
	 * 
	 * @param n
	 *            the name
	 * @param m
	 *            whether to merge
	 * @param r
	 *            whether to replace
	 */
	public DataList(String n, boolean m, boolean r) {
		name = n;
		merge = m;
		replace = r;
		disabled = false;
		rank = UNRANKED;
		star = false;
                generic = false;
	}

	/**
	 * Four parameters constructor
	 * 
	 * @param n
	 *            the name
	 * @param m
	 *            whether to merge
	 * @param r
	 *            whether to replace
	 */
	public DataList(String n, boolean m, boolean r, boolean d) {
		name = n;
		merge = m;
		replace = r;
		disabled = d;
		rank = UNRANKED;
		star = false;
                generic = false;
	}
	
	public DataList(String n, boolean m, boolean r, boolean d, boolean s, boolean g) {
		name = n;
		merge = m;
		replace = r;
		disabled = d;
		rank = UNRANKED;
		star = s;
                generic = g;
	}
	
	/**
	 * One constructor parameter
	 * 
	 * @param dl
	 *            datalist object
	 */
	public DataList(DataList dl) {
		name = dl.getName();
		merge = dl.isMerge();
		replace = dl.isReplace();
		disabled = dl.isDisabled();
		rank = dl.rank;
		star = dl.star;
                generic = dl.generic;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the merge
	 */
	public boolean isMerge() {
		return merge;
	}

	/**
	 * @param merge
	 *            the merge to set
	 */
	public void setMerge(boolean merge) {
		this.merge = merge;
	}

	/**
	 * @return the replace
	 */
	public boolean isReplace() {
		return replace;
	}

	/**
	 * @param replace
	 *            the replace to set
	 */
	public void setReplace(boolean replace) {
		this.replace = replace;
	}
	
	/**
	 * 
	 * @return disabled
	 */
	public boolean isDisabled(){
		return disabled;
	}
	
	/**
	 * 
	 * @param disabled the disabled to set
	 */
	public void setDisabled(boolean disabled){
		this.disabled = disabled;
	}
	
	public int getRank(){
		return this.rank;
	}
	
	public void setRank(int rank){
		this.rank = rank;
	}
	
	
	public boolean isStar(){
		return this.star;
	}
	
	public void setStar(boolean s){
		this.star = s;
	}
        
        public boolean isGeneric() {
            return this.generic;
        }
        
        public void setGeneric(boolean g) {
            this.generic = g;
        }
	
}
