package fr.umlv.unitex.cassys;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;

public class TransducerListTable extends JTable {

	
	public TransducerListTable(TransducerListTableModel m){
		super(m);
		
	}
	
	
	/**
	 * Redefinition of the tableChanged method to ensure integrity data
	 * (ie both merge and replace cannot be set at true  OR
         *     both generic and replace cannot be set to true OR 
         *     both generic and until fix (star) cannot be set to true)
	 */
	@Override
	public void tableChanged(TableModelEvent e) {

		TransducerListTableModel model = (TransducerListTableModel) getModel();
		
	   // if (e.getColumn() == 2) {
	   if (e.getColumn() == 3) {
		
			if (e.getFirstRow() != TableModelEvent.HEADER_ROW) {
				for (int i = e.getFirstRow(); i <= e.getLastRow(); i++) {
					if ((Boolean) getValueAt(i, model.getMergeIndex())
							&& (Boolean) getValueAt(i, model.getReplaceIndex())) {
						setValueAt(Boolean.FALSE, i, model.getReplaceIndex());
					}
					if (!((Boolean) getValueAt(i, model.getMergeIndex()))
							&& !((Boolean) getValueAt(i, model.getReplaceIndex()))) {
						setValueAt(Boolean.TRUE, i, model.getReplaceIndex());
					}
					
				}
			}
		}
		
		//if (e.getColumn() == 3) {
		    if (e.getColumn() == 4) {	
			if (e.getFirstRow() != TableModelEvent.HEADER_ROW) {
				for (int i = e.getFirstRow(); i <= e.getLastRow(); i++) {
					if ((Boolean) getValueAt(i, model.getReplaceIndex())
							&& (Boolean) getValueAt(i, model.getMergeIndex())) {
						setValueAt(Boolean.FALSE, i, model.getMergeIndex());
					}
					if (!((Boolean) getValueAt(i, model.getReplaceIndex()))
							&& !((Boolean) getValueAt(i, model.getMergeIndex()))) {
						setValueAt(Boolean.TRUE, i, model.getMergeIndex());
					}
                                        if ((Boolean) getValueAt(i, model.getReplaceIndex())
							&& (Boolean) getValueAt(i, model.getGenericIndex())) {
						setValueAt(Boolean.FALSE, i, model.getGenericIndex());
					}
				}
			}
		}
                    
                if (e.getColumn() == 5) {	
                    if (e.getFirstRow() != TableModelEvent.HEADER_ROW) {
			for (int i = e.getFirstRow(); i <= e.getLastRow(); i++) {
                            if ((Boolean) getValueAt(i, model.getStarIndex())
				&& (Boolean) getValueAt(i, model.getGenericIndex())) {
                                    setValueAt(Boolean.FALSE, i, model.getGenericIndex());
                            }
			}
                    }
		}
                    
                if (e.getColumn() == 6) {	
                    if (e.getFirstRow() != TableModelEvent.HEADER_ROW) {
			for (int i = e.getFirstRow(); i <= e.getLastRow(); i++) {
                            if (((Boolean) getValueAt(i, model.getReplaceIndex()) || (Boolean) getValueAt(i, model.getStarIndex()))
				&& (Boolean) getValueAt(i, model.getGenericIndex())) {
				setValueAt(Boolean.FALSE, i, model.getReplaceIndex());
                                setValueAt(Boolean.FALSE, i, model.getStarIndex());
                                setValueAt(Boolean.TRUE, i, model.getMergeIndex());
                            }
			}
                    }
		}
		
		// Update all rank columns.
		int rank = 1;
		for (int row = 0; row < getRowCount(); row++) {
	
		    if((Boolean) getValueAt(row, model.getDisabledIndex()) == false){
				
			    	// test used to avoid dataChanged method fired in infinite loop
				if((Integer)getValueAt(row, model.getRankIndex()) != rank){
					setValueAt(rank,row, model.getRankIndex());
				}
				rank++;
			} else {
				if((Integer)getValueAt(row, model.getRankIndex()) != DataList.UNRANKED){
					setValueAt(DataList.UNRANKED,row, model.getRankIndex());
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
