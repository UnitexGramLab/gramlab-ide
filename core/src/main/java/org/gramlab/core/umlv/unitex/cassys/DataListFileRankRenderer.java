package org.gramlab.core.umlv.unitex.cassys;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class DataListFileRankRenderer extends JLabel implements
		TableCellRenderer {

	public DataListFileRankRenderer() {
		// to enable background color
		setOpaque(true);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		Integer rank = (Integer)value;
		
		if(rank != DataList.UNRANKED){
			setText(rank.toString());
		} else {
			setText("");
		}
		
		
		return this;
	}

}
