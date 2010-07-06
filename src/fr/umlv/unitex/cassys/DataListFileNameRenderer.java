package fr.umlv.unitex.cassys;
import java.awt.Component;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Render class of File Name in the table. It displays a file name in red and italic if it does not exist.
 * 
 * 
 * @author david nott
 *
 */
public class DataListFileNameRenderer extends JLabel implements
		TableCellRenderer {
	
	public DataListFileNameRenderer() {
		
		// to enable background color
		setOpaque(true);
	}
	
	/**
	 * Returns a <code>JLabel</code> which displays the content of the cell. File name is displayed in red and italic
	 * if the file does not exist.
	 * 
	 * @param table the table displaying the cell
	 * @param value the value of the cell
	 * @param isSelected true if the cell is selected
	 * @param hasFocus true if the cell has focus
	 * @param row row index of the cell
	 * @param column column index of the cell
	 * 
	 * @return the JLabel displaying the cell
	 */
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		String str = (String)value;
		
		
		File f = new File(str);
		
		// Write in red and italic if the file does not exist
		if(!f.exists()){
			setText("<html><i><font color = red>"+str+"</red></i></html>");
			setToolTipText("File does not exist");
		}
		else {
			setText(f.getName());
			setToolTipText(str);
		}

		// set color selection
		if(isSelected){
			setBackground(table.getSelectionBackground());
		}
		else {
			setBackground(table.getBackground());
		}
		
		return this;
	}

	

}
