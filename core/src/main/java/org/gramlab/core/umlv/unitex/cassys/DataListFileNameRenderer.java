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

import java.awt.Component;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import fr.umlv.unitex.config.Config;
/**
 * Render class of File Name in the table. It displays a file name in red and
 * italic if it does not exist.
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
	 * Returns a <code>JLabel</code> which displays the content of the cell.
	 * File name is displayed in red and italic if the file does not exist.
	 * 
	 * @param table
	 *            the table displaying the cell
	 * @param value
	 *            the value of the cell
	 * @param isSelected
	 *            true if the cell is selected
	 * @param hasFocus
	 *            true if the cell has focus
	 * @param row
	 *            row index of the cell
	 * @param column
	 *            column index of the cell
	 * 
	 * @return the JLabel displaying the cell
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		
		final String str = (String) value;
		final File f = new File(Config.getCurrentGraphDir(),str);
		// Write in red and italic if the file does not exist
		
		
		if (!f.exists()) {
			setText("<html><i><font color = 'red'>" + str + "</font></i></html>");
			setToolTipText("File does not exist");
		} else {
			final TransducerListTableModel model = (TransducerListTableModel) table.getModel();
			if((Boolean) table.getValueAt(row, model.getDisabledIndex()) == true){
				setText("<html><font color = 'gray'><strike>" + f.getName()+"</strike></font></html>");
			} else {
				
				setText( f.getName());
			}
			
			setToolTipText(str);
		}
		// set color selection
		if (isSelected) {
			setBackground(table.getSelectionBackground());
			
		} else {
			setBackground(table.getBackground());
		}
		return this;
	}
}
