package org.gramlab.core.gramlab.project.config.maven;


import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;

import org.gramlab.core.gramlab.util.filelist.SelectableFile;

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
public class MvnSelectionTreeCellEditor extends AbstractCellEditor implements TreeCellEditor, TreeCellRenderer {

    private JCheckBox checkbox;
    private MavenFileTableModel model;
    
    public MvnSelectionTreeCellEditor(MavenFileTableModel model) {
        checkbox=new JCheckBox("");
        checkbox.setOpaque(false);
        this.model=model;
        checkbox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped();
			}
        });
    }

    @Override
    public Object getCellEditorValue() {
        return checkbox.isSelected();
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
    	return false;
    }

	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {
    	MavenTreeNode node=(MavenTreeNode)value;
    	int index=node.getMavenTableModelIndex();
    	if (index==-1) {
    		checkbox.setSelected(true);
    		checkbox.setForeground(Color.BLACK);
    		checkbox.setText(node.getName());
    		return checkbox;
    	}
    	SelectableFile info=model.getElement(node.getMavenTableModelIndex());
    	checkbox.setSelected(info.isSelected());
    	checkbox.setForeground(Color.BLACK);
		checkbox.setText(node.getName());
		return checkbox;
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		return getTreeCellEditorComponent(tree,value,selected,expanded,leaf,row);
	}

}
