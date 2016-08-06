package fr.gramlab.svn;

import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import fr.gramlab.project.config.preprocess.fst2txt.SelectionTableCellEditor;

@SuppressWarnings("serial")
public class CommitTable extends JTable {
	
	private boolean filterUnversionedFiles=false;
	private CommitTableModel model;
	
	public CommitTable(final CommitTableModel model) {
		super(model);
		this.model=model;
		setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		installRendererAndEditorForSelectionColumn(getColumnModel().getColumn(0));
		installRendererForNameColumn(getColumnModel().getColumn(1));
		installRendererForStatusColumn(getColumnModel().getColumn(2));
		final TableRowSorter<CommitTableModel> sorter=new TableRowSorter<CommitTableModel>(model);
		sorter.setSortsOnUpdates(false);
		sorter.setSortable(0,false);
		sorter.setSortable(1,false);
		sorter.setSortable(2,false);
		
		RowFilter<CommitTableModel,Integer> filter=new RowFilter<CommitTableModel,Integer>() {

			@Override
			public boolean include(
					javax.swing.RowFilter.Entry<? extends CommitTableModel, ? extends Integer> entry) {
				if (!CommitTable.this.filterUnversionedFiles) return true;
				Boolean unversioned=entry.getModel().getValueAt(entry.getIdentifier(),2).equals(SvnStatus.UNVERSIONED);
				return !unversioned;
			}
            
        };
		sorter.setRowFilter(filter);
		setRowSorter(sorter);
	}
	
	
	private void installRendererForNameColumn(TableColumn column) {
		column.setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				String s=(String)value;
				super.getTableCellRendererComponent(table, s, isSelected, hasFocus,
						row, column);
				updateRendererSize(this,row,column);
				setForeground(((SvnStatus)model.getValueAt(row,2)).getColor());
				return this;
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

	private void installRendererForStatusColumn(TableColumn column) {
		column.setCellRenderer(new DefaultTableCellRenderer() {
		
		@Override
		public Component getTableCellRendererComponent(final JTable table,
				Object value, boolean isSelected, boolean hasFocus,
				final int row,int columnN) {
			SvnStatus status=(SvnStatus)value;
			super.getTableCellRendererComponent(table,status.getDescription(), isSelected, hasFocus,
					row, columnN);
			setForeground(status.getColor());
			return this;
		}
	});
	}

	
	private void updateRendererSize(Component renderer,final int row,int col) {
		final int h=renderer.getPreferredSize().height;
		final int w=renderer.getPreferredSize().width;
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
	

	public void setFilterUnversionedFiles(boolean filter) {
		this.filterUnversionedFiles = filter;
		model.unselectUnversionedFiles();
		getRowSorter().allRowsChanged();
	}


}
