package org.gramlab.core.gramlab.util.filelist;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.gramlab.core.gramlab.project.GramlabProject;

@SuppressWarnings("serial")
public class SelectableFileList extends JTable {
	
	private boolean filter=false;
	
	public SelectableFileList(SelectableFileListModel model,final GramlabProject p) {
		super(model);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setDefaultRenderer(SelectableFile.class,new DefaultTableCellRenderer() {
			
			private JCheckBox renderer=new JCheckBox();
			
			@Override
			public Component getTableCellRendererComponent(final JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					final int row,int columnN) {
				SelectableFile f=(SelectableFile)value;
				super.getTableCellRendererComponent(table,p.getRelativeFileName(f.getFile()), isSelected, hasFocus,
						row, columnN);
				renderer.setText(getText());
				renderer.setBackground(getBackground());
				renderer.setForeground(getForeground());
				renderer.setSelected(f.isSelected());
				final int h=renderer.getPreferredSize().height;
				final int w=renderer.getPreferredSize().width;
				if (table.getRowHeight(row)<h) {
					EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							table.setRowHeight(row,h);
						}
					});
				}
				final TableColumn column=table.getColumnModel().getColumn(0);
				column.setResizable(true);
				if (column.getWidth()<w) {
					EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							column.setPreferredWidth(w+50);
							table.revalidate();
							table.repaint();
						}
					});
				}
				return renderer;
			}
		});
		final TableRowSorter<SelectableFileListModel> sorter=new TableRowSorter<SelectableFileListModel>(model);
		sorter.setSortable(0,false);
		
		RowFilter<SelectableFileListModel,Integer> filter=new RowFilter<SelectableFileListModel,Integer>() {

			@Override
			public boolean include(
					javax.swing.RowFilter.Entry<? extends SelectableFileListModel, ? extends Integer> entry) {
				if (!SelectableFileList.this.filter) return true;
				SelectableFile f=(SelectableFile) entry.getModel().getValueAt(entry.getIdentifier(),0);
				return f.isSelected();
			}
            
        };
		sorter.setRowFilter(filter);
		setRowSorter(sorter);
		setDefaultEditor(SelectableFile.class,new MyTableCellEditor(p));
		model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				sorter.allRowsChanged();
			}
		});
	}
	
	
	public void setFilter(boolean filter) {
		this.filter = filter;
		getRowSorter().allRowsChanged();
	}


	public ArrayList<File> getSelectedFiles() {
		SelectableFileListModel model=(SelectableFileListModel) getModel();
		return model.getSelectedFiles();
	}


	/**
	 * We shift the currently selected rows one line up or down, adjusting
	 * the selection state. The function does nothing if there is no selection
	 * if the selection cannot be moved.
	 */
	public void shiftSelectedRows(boolean increase) {
		int[] indices=getSelectedRows();
		if (indices==null || indices.length==0) return;
		int n=indices.length;
		if (increase && indices[n-1]==getRowCount()-1) return;
		if (!increase && indices[0]==0) return;
		int shift=(increase?1:-1);
		RowSorter<? extends TableModel> sorter=getRowSorter();
		SelectableFileListModel model=(SelectableFileListModel) getModel();
		for (int i=0;i<n;i++) {
			int realSrcIndex=sorter.convertRowIndexToModel(indices[i]);
			int realDstIndex=sorter.convertRowIndexToModel(indices[i]+shift);
			model.swap(realSrcIndex,realDstIndex);
			indices[i]=indices[i]+shift;
		}
		clearSelection();
		for (int i:indices) {
			addRowSelectionInterval(i,i);
		}
        Rectangle cellBounds = getCellRect(indices[0],0,true);
        if (cellBounds != null) {
            scrollRectToVisible(cellBounds);
        }
	}
	
}
