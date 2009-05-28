 /*
  * Unitex
  *
  * Copyright (C) 2001-2009 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

package fr.umlv.unitex;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class StatisticsFrame extends JInternalFrame {

	static StatisticsFrame frame;

	public StatisticsFrame(File file,int mode) {
		super("Statistics", true, true, true, true);
		JPanel top = new JPanel();
		top.setOpaque(true);
		top.setLayout(new BorderLayout());
		JTable table = createTable(file,mode);
		table.setFont(Preferences.getCloneOfPreferences().textFont);
		JScrollPane scroll = new JScrollPane(table);
		scroll
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		top.add(scroll, BorderLayout.CENTER);
		setContentPane(top);
		pack();
		setVisible(true);
		frame = this;
		UnitexFrame.addInternalFrame(this,true);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				setVisible(false);
				UnitexFrame.removeInternalFrame(frame);
				frame = null;
			}
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	
	private static final TableCellRenderer rightJustifiedRenderer=new DefaultTableCellRenderer() {
	    @Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	        setHorizontalAlignment(SwingConstants.RIGHT);
	        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	    }
	};
	
    private static final TableCellRenderer centeredRenderer=new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setHorizontalAlignment(SwingConstants.CENTER);
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    };
	
	private JTable createTable(File file, int mode) {
	    JTable t=null;
        switch (mode) {
        case 0: {
            t=new JTable(new StatisticsTableModelMode0(file));
            t.getColumnModel().getColumn(0).setCellRenderer(rightJustifiedRenderer);
            t.getColumnModel().getColumn(1).setCellRenderer(centeredRenderer);
            break;
        }
        case 1: {
            t=new JTable(new StatisticsTableModelMode1(file));
            break;
        }
        case 2: {
            t=new JTable(new StatisticsTableModelMode2(file));
            break;
        }
        default: throw new IllegalArgumentException("Invalid mode: "+mode);
        }
        setColumnsToTheirPreferredWidth(t);
        final TableRowSorter<TableModel> sorter=new TableRowSorter<TableModel>(t.getModel());
        t.setRowSorter(sorter);
        return t;
    }

	
    private void setColumnsToTheirPreferredWidth(JTable t) {
        for (int i=0;i<t.getColumnCount();i++) {
            int width=75;
            for (int j=0;j<t.getRowCount();j++) {
                TableCellRenderer r=t.getCellRenderer(j,i);
                Component c=r.getTableCellRendererComponent(t,t.getValueAt(j,i),false,false,j,i);
                int w=c.getPreferredSize().width;
                if (w>width) {
                    width=w;
                }
            }
            t.getColumnModel().getColumn(i).setPreferredWidth(width);
        }
    }
    

    /**
	 * Closes the frame
	 *  
	 */
	public static void closeFrame() {
		if (frame != null) {
			frame.setVisible(false);
			UnitexFrame.removeInternalFrame(frame);
			frame = null;
		}
	}

}