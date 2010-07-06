package fr.umlv.unitex.cassys;

/**
 * Class defining a row in the table of cassys configuration frame.
 * <P>
 * A row is made of a <code>String</code> denoting a transducer file name and two <code>Boolean</code> fields
 * indicating whether merge mode and replace mode are selected.
 * 
 * @author david nott
 *
 */
public class DataList {
	/**
	 * Name of the transducer file
	 */
	String name;
	
	/**
	 * Whether the <code>merge</code> option sould be applied
	 */
	boolean merge;
	
	/**
	 * Whether the <code>replace</code> option sould be applied
	 */
	boolean replace;
	
	/**
	 * Three parameters constructor
	 * 
	 * @param n the name
	 * @param m whether to merge
	 * @param r whether to replace
	 */
	public DataList(String n, boolean m, boolean r) {
		name = n;
		merge = m;
		replace = r;
	}
	
	/**
	 * One constructor parameter 
	 * 
	 * @param dl datalist object
	 */
	public DataList(DataList dl){
		name = dl.getName();
		merge = dl.isMerge();
		replace = dl.isReplace();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
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
	 * @param merge the merge to set
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
	 * @param replace the replace to set
	 */
	public void setReplace(boolean replace) {
		this.replace = replace;
	}
}
