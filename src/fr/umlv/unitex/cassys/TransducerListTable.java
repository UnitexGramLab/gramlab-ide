package fr.umlv.unitex.cassys;

import java.awt.Dimension;

import javax.swing.DropMode;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

public class TransducerListTable extends JTable {

	
	public TransducerListTable(DefaultTableModel m){
		super(m);
		
		
	}
	
	/**
	 * Redefinition of the tableChanged method to ensure integrity data
	 * (ie both merge an replace cannot be set at true)
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getColumn() == 2) {
			if (e.getFirstRow() != TableModelEvent.HEADER_ROW) {
				for (int i = e.getFirstRow(); i <= e.getLastRow(); i++) {
					if ((Boolean) getValueAt(i, 2)
							&& (Boolean) getValueAt(i, 3)) {
						setValueAt(Boolean.FALSE, i, 3);
					}
					if (!((Boolean) getValueAt(i, 2))
							&& !((Boolean) getValueAt(i, 3))) {
						setValueAt(Boolean.TRUE, i, 3);
					}
					
				}
			}
		}
		if (e.getColumn() == 3) {
			if (e.getFirstRow() != TableModelEvent.HEADER_ROW) {
				for (int i = e.getFirstRow(); i <= e.getLastRow(); i++) {
					if ((Boolean) getValueAt(i, 3)
							&& (Boolean) getValueAt(i, 2)) {
						setValueAt(Boolean.FALSE, i, 2);
					}
					if (!((Boolean) getValueAt(i, 3))
							&& !((Boolean) getValueAt(i, 2))) {
						setValueAt(Boolean.TRUE, i, 2);
					}
				}
			}
		}
		
		// Update all rank columns.
		int rank = 0;
		for (int row = 0; row < getRowCount(); row++) {
			
			System.out.println("row = "+ row + " out of " + getRowCount());
			
			if((Boolean) getValueAt(row, 4) == false){
				// test used to avoid dataChanged method fired
				if((Integer)getValueAt(row,0) != rank){
					setValueAt(rank,row,0);
				}
				rank++;
			} else {
				if((Integer)getValueAt(row,0) != DataList.UNRANKED){
					setValueAt(DataList.UNRANKED,row,0);
				}
			}
		}
		
		System.out.println("end for");
		
		// make sure that custom renderer repaint all the
		// changed value
		repaint();
		
		super.tableChanged(e);
	}
	
	
	public int getDisabledCount(int untilRow){
		int numberOfDisabled = 0;
		
		for(int index = 0;index <untilRow; index++){
			if((Boolean)getValueAt(index, 3) == true){
				numberOfDisabled++;
			}
		}
		
		return numberOfDisabled;	
		
	}
	
	
	
}
