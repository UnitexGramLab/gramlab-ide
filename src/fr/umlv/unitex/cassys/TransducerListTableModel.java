package fr.umlv.unitex.cassys;

import javax.swing.table.DefaultTableModel;

public class TransducerListTableModel extends DefaultTableModel {

	
	// layer added to permit easy evolution of the table when inserting new columns
	private int rankIndex;
	private int nameIndex;
	private int mergeIndex;
	private int replaceIndex;
	private int disabledIndex;
	private int starIndex;
	
	
	/*public TransducerListTableModel(){
		addColumn("#"); rankIndex=0;
		addColumn("Name");nameIndex=1;
		addColumn("Merge"); mergeIndex =2;
		addColumn("Replace"); replaceIndex =3;
		addColumn("Disabled"); disabledIndex=4;
		addColumn("Iter"); starIndex=5;
	}*/
	
	public TransducerListTableModel(){
		addColumn("#"); rankIndex=0;
		addColumn("Disabled"); disabledIndex=1;
		addColumn("Name");nameIndex=2;
		addColumn("Merge"); mergeIndex =3;
		addColumn("Replace"); replaceIndex =4;
		addColumn("Until Fix point"); starIndex=5;
	}
	
	/**
	 * Redefinition of the getColumnClass method in order to provide a
	 * check box for boolean values
	 */
	@Override
	public Class<?> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}


	public int getRankIndex() {
		return rankIndex;
	}


	public int getNameIndex() {
		return nameIndex;
	}


	public int getMergeIndex() {
		return mergeIndex;
	}


	public int getReplaceIndex() {
		return replaceIndex;
	}
	
	public int getStarIndex(){
		return starIndex;
	}


	public int getDisabledIndex() {
		return disabledIndex;
	}
	
	
	
	
	
	
}
