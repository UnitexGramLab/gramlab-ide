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
		if (e.getColumn() == 1) {
			if (e.getFirstRow() != TableModelEvent.HEADER_ROW) {
				for (int i = e.getFirstRow(); i <= e.getLastRow(); i++) {
					if ((Boolean) getValueAt(i, 1)
							&& (Boolean) getValueAt(i, 2)) {
						setValueAt(Boolean.FALSE, i, 2);
					}
					if (!((Boolean) getValueAt(i, 1))
							&& !((Boolean) getValueAt(i, 2))) {
						setValueAt(Boolean.TRUE, i, 2);
					}
					
				}
			}
		}
		if (e.getColumn() == 2) {
			if (e.getFirstRow() != TableModelEvent.HEADER_ROW) {
				for (int i = e.getFirstRow(); i <= e.getLastRow(); i++) {
					if ((Boolean) getValueAt(i, 2)
							&& (Boolean) getValueAt(i, 1)) {
						setValueAt(Boolean.FALSE, i, 1);
					}
					if (!((Boolean) getValueAt(i, 2))
							&& !((Boolean) getValueAt(i, 1))) {
						setValueAt(Boolean.TRUE, i, 1);
					}
				}
			}
		}
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
