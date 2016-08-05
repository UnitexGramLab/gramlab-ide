package fr.gramlab.project.config.preprocess.fst2txt;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * This class provides a table cell editor that acts like a JCheckBox, except
 * that if we click on it but not in the actual check box square, the
 * selection state does not change. That way, it is possible to just select a line
 * in the table without selecting the corresponding file.
 * 
 * @author paumier
 *
 */
@SuppressWarnings("serial")
public class SelectionTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    private JCheckBox checkbox;
    private JPanel panel;
    private JLabel label=new JLabel("");
    
    public SelectionTableCellEditor() {
        checkbox=new JCheckBox("");
        panel=new JPanel(new BorderLayout());
        panel.add(checkbox,BorderLayout.WEST);
        panel.add(label,BorderLayout.CENTER);
        checkbox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped();
			}
        });
        panel.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		fireEditingStopped();
        	}
        	@Override
        	public void mouseReleased(MouseEvent e) {
        		fireEditingStopped();
        	}
		});
        label.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		fireEditingStopped();
        	}
        	@Override
        	public void mouseReleased(MouseEvent e) {
        		fireEditingStopped();
        	}
		});
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    	Boolean b=(Boolean)value;
    	checkbox.setSelected(b);
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return checkbox.isSelected();
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
    	return false;
    }

}
