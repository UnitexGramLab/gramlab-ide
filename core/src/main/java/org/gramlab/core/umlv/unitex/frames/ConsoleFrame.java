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
package fr.umlv.unitex.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import fr.umlv.unitex.console.Console;
import fr.umlv.unitex.console.ConsoleEntry;
import fr.umlv.unitex.console.ConsoleTableCellEditor;
import fr.umlv.unitex.console.ConsoleTableModel;
import fr.umlv.unitex.console.ConsoleTransferHandler;

/**
 * This class describes a frame that shows all the command lines that have been
 * launched.
 * 
 * @author Sébastien Paumier
 */
public class ConsoleFrame extends TabbableInternalFrame {
	private final ConsoleTableModel model;
	private final JTable table;
	private int longestCommandWidth = 80;
	private int longestIDWidth = 50;
	static final ImageIcon statusOK = new ImageIcon(
			Console.class.getResource("OK.png"));
	public static final ImageIcon statusErrorDown = new ImageIcon(
			Console.class.getResource("error1.png"));
	public static final ImageIcon statusErrorUp = new ImageIcon(
			Console.class.getResource("error2.png"));

	ConsoleFrame() {
		super("Console", true, true);
		model = new ConsoleTableModel();
		table = new JTable(model);
		table.setTransferHandler(new ConsoleTransferHandler(model));
		table.setDragEnabled(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(0).setPreferredWidth(20);
		table.getColumnModel().getColumn(1).setPreferredWidth(longestIDWidth);
		table.getColumnModel().getColumn(2).setMinWidth(longestCommandWidth);
		table.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
			final JLabel errorDown = new JLabel(statusErrorDown,
					SwingConstants.CENTER);
			final JLabel errorUp = new JLabel(statusErrorUp,
					SwingConstants.CENTER);
			final JLabel OK = new JLabel(statusOK, SwingConstants.CENTER);
			final JLabel nothing = new JLabel();

			@Override
			public Component getTableCellRendererComponent(JTable t,
					Object value, boolean ss, boolean hasFocus, int row,
					int column) {
				final Integer i = (Integer) value;
				switch (i) {
				case 0:
					return OK;
				case 1:
					return errorDown;
				case 2:
					return errorUp;
				case 3:
					return nothing;
				default:
					throw new IllegalArgumentException("Invalid status: " + i);
				}
			}
		});
		table.setDefaultRenderer(ConsoleEntry.class,
				new DefaultTableCellRenderer() {
					final JTextArea command = new JTextArea();
					final JTextArea error = new JTextArea();
					{
						error.setLineWrap(true);
						error.setWrapStyleWord(true);
						error.setForeground(Color.RED);
					}

					@Override
					public Component getTableCellRendererComponent(JTable t,
							Object value, boolean ss, boolean hasFocus,
							int row, int column) {
						final ConsoleEntry e = (ConsoleEntry) value;
						if (e.isSystemMsg()) {
							command.setBackground(ProcessInfoFrame.systemColor);
						} else {
							command.setBackground(Color.WHITE);
						}
						switch (e.getStatus()) {
						case 0:
						case 1:
						case 2: {
							command.setText(e.getContent());
							return command;
						}
						case 3: {
							error.setText(e.getContent());
							final int h = error.getPreferredSize().height;
							final JTable t2 = t;
							final int r = row;
							if (t.getRowHeight(row) < h) {
								/* If necessary, we resize the row */
								EventQueue.invokeLater(new Runnable() {
									@Override
									public void run() {
										t2.setRowHeight(r, h);
									}
								});
							}
							return error;
						}
						default:
							throw new IllegalArgumentException(
									"Invalid status: " + e.getStatus());
						}
					}
				});
		table.setDefaultEditor(Integer.class, new ConsoleTableCellEditor(model));
		table.setDefaultEditor(ConsoleEntry.class, new DefaultCellEditor(
				new JTextField()));
		final JScrollPane scroll = new JScrollPane(table);
		final JPanel middle = new JPanel(new BorderLayout());
		middle.setBorder(BorderFactory.createLoweredBevelBorder());
		middle.add(scroll, BorderLayout.CENTER);
		final JPanel top = new JPanel(new BorderLayout());
		top.setBorder(new EmptyBorder(2, 2, 2, 2));
		top.add(middle, BorderLayout.CENTER);
		setContentPane(top);
		setBounds(100, 100, 600, 400);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	/**
	 * Adds a <code>String</code> to the the command lines
	 * 
	 * @param command
	 *            the command line to be added
	 */
	public ConsoleEntry addCommand(String command, boolean isRealCommand,
			int pos, boolean systemMsg, String logID) {
		final ConsoleEntry e = new ConsoleEntry(command, isRealCommand,
				systemMsg, logID);
		final int n = (pos != -1) ? pos : model.getRowCount();
		model.addConsoleEntry(n, e);
		/* Now, we update the width of the last two columns */
		TableCellRenderer renderer = table.getCellRenderer(n, 1);
		Component c = renderer.getTableCellRendererComponent(table,
				e.getlogID(), false, false, n, 1);
		if (c.getPreferredSize().width > longestIDWidth) {
			longestIDWidth = c.getPreferredSize().width + 50;
			table.getColumnModel().getColumn(1).setMinWidth(longestIDWidth);
		}
		renderer = table.getCellRenderer(n, 2);
		c = renderer
				.getTableCellRendererComponent(table, e, false, false, n, 2);
		if (c.getPreferredSize().width > longestCommandWidth) {
			longestCommandWidth = c.getPreferredSize().width + 50;
			table.getColumnModel().getColumn(2)
					.setMinWidth(longestCommandWidth);
		}
		repaint();
		return e;
	}

	public ConsoleEntry addCommand(String command, boolean systemMsg,
			String logID) {
		return addCommand(command, true, -1, systemMsg, logID);
	}

	@Override
	public String getTabName() {
		return "Unitex Console";
	}
}
