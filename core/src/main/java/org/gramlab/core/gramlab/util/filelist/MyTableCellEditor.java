package org.gramlab.core.gramlab.util.filelist;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.gramlab.core.gramlab.project.GramlabProject;

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
public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    private JCheckBox checkbox;
    private JPanel panel;
    private JLabel label;
    private GramlabProject project;
    
    public MyTableCellEditor(GramlabProject p) {
        this.project=p;
        checkbox=new JCheckBox("");
        label=new JLabel();
        panel=new JPanel(new BorderLayout());
        panel.add(checkbox,BorderLayout.WEST);
        panel.add(label,BorderLayout.CENTER);
        checkbox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
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
    	SelectableFile f=(SelectableFile)value;
    	label.setText(project.getRelativeFileName(f.getFile()));
    	checkbox.setSelected(f.isSelected());
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return checkbox.isSelected();
    }

}
