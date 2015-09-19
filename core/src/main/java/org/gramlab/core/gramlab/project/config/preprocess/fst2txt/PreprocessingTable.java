package fr.gramlab.project.config.preprocess.fst2txt;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.config.maven.PomIO;
import fr.umlv.unitex.files.FileUtil;

@SuppressWarnings("serial")
public class PreprocessingTable extends JTable {
	
	private boolean filter=false;
	
	public PreprocessingTable(final PreprocessingTableModel model,final GramlabProject p) {
		super(model);
		setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		installRendererAndEditorForSelectionColumn(getColumnModel().getColumn(0));
		installRendererForNameColumn(getColumnModel().getColumn(1),p);
		installRendererAndEditorForTargetColumn(getColumnModel().getColumn(2),p);
		installRendererAndEditorForModeColumn(getColumnModel().getColumn(3));
		final TableRowSorter<PreprocessingTableModel> sorter=new TableRowSorter<PreprocessingTableModel>(model);
		sorter.setSortsOnUpdates(false);
		sorter.setSortable(0,false);
		sorter.setSortable(1,false);
		sorter.setSortable(2,false);
		sorter.setSortable(3,false);
		
		RowFilter<PreprocessingTableModel,Integer> filter=new RowFilter<PreprocessingTableModel,Integer>() {

			@Override
			public boolean include(
					javax.swing.RowFilter.Entry<? extends PreprocessingTableModel, ? extends Integer> entry) {
				if (!PreprocessingTable.this.filter) return true;
				Boolean selected=(Boolean) entry.getModel().getValueAt(entry.getIdentifier(),0);
				return selected;
			}
            
        };
		sorter.setRowFilter(filter);
		setRowSorter(sorter);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row=rowAtPoint(e.getPoint());
				if (e.getClickCount()==2) {
					if (row==-1) return;
					PreprocessingStep step=model.getElement(row);
					new PreprocessingStepDialog(p,model,step.getGraph(),step);
					return;
				}
			}
		});
	}
	
	
	private void installRendererForNameColumn(TableColumn column,final GramlabProject p) {
		column.setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				File f=(File)value;
				String s=p.getRelativeFileName(f);
				super.getTableCellRendererComponent(table, s, isSelected, hasFocus,
						row, column);
				updateRendererSize(this,row,column);
				return this;
			}
		});
	}

	private void installRendererAndEditorForTargetColumn(TableColumn column,final GramlabProject project) {
		final File preprocessDir = new File(project
				.getProjectDirectory(), PomIO.TARGET_PREPROCESS_DIRECTORY);
		column.setCellRenderer(new DefaultTableCellRenderer() {
			private String getSimpleDestName(File f) {
				return FileUtil.getRelativePath(preprocessDir, f);
			}
			
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				File f=(File)value;
				String s=getSimpleDestName(f);
				super.getTableCellRendererComponent(table, s, isSelected, hasFocus,
						row, column);
				updateRendererSize(this,row,column);
				return this;
			}
		});
		column.setCellEditor(new DefaultCellEditor(new JTextField()) {
			private String getSimpleDestName(File f) {
				return FileUtil.getRelativePath(preprocessDir, f);
			}

			@Override
			public Component getTableCellEditorComponent(JTable table,
					Object value, boolean isSelected, int row, int column) {
				File f=(File)value;
				Component c=super.getTableCellEditorComponent(table, getSimpleDestName(f), isSelected, row, column);
				return c;
			}
			
			@Override
			public Object getCellEditorValue() {
				String s=(String) super.getCellEditorValue();
				return new File(preprocessDir,s);
			}
		});
	}

	private void installRendererAndEditorForSelectionColumn(TableColumn column) {
		column.setCellRenderer(new DefaultTableCellRenderer() {
		
		private JCheckBox renderer=new JCheckBox();
		
		@Override
		public Component getTableCellRendererComponent(final JTable table,
				Object value, boolean isSelected, boolean hasFocus,
				final int row,int columnN) {
			Boolean b=(Boolean)value;
			super.getTableCellRendererComponent(table,b, isSelected, hasFocus,
					row, columnN);
			renderer.setBackground(getBackground());
			renderer.setForeground(getForeground());
			renderer.setSelected(b);
			return renderer;
		}
		});
		column.setCellEditor(new SelectionTableCellEditor());
		column.setWidth(35);
		column.setPreferredWidth(35);
		revalidate();
		repaint();
	}

	private void installRendererAndEditorForModeColumn(TableColumn column) {
		column.setCellRenderer(new DefaultTableCellRenderer() {
		
		@Override
		public Component getTableCellRendererComponent(final JTable table,
				Object value, boolean isSelected, boolean hasFocus,
				final int row,int columnN) {
			Boolean b=(Boolean)value;
			return super.getTableCellRendererComponent(table,b?"merge":"replace", isSelected, hasFocus,
					row, columnN);
		}
	});
	}

	
	private void updateRendererSize(Component renderer,final int row,int col) {
		final int h=renderer.getSize().height;
		final int w=renderer.getSize().width;
		if (getRowHeight(row)<h) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					setRowHeight(row,h);
				}
			});
		}
		final TableColumn column=getColumnModel().getColumn(col);
		column.setResizable(true);
		if (column.getWidth()<w) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					column.setPreferredWidth(w+50);
					revalidate();
					repaint();
				}
			});
		}
	}
	

	public void setFilter(boolean filter) {
		this.filter = filter;
		getRowSorter().allRowsChanged();
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
		PreprocessingTableModel model=(PreprocessingTableModel) getModel();
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
