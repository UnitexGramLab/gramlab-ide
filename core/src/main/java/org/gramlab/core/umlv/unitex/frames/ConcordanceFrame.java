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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.umlv.unitex.concord.BigConcordance;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.config.PreferencesListener;
import fr.umlv.unitex.config.PreferencesManager;
import fr.umlv.unitex.debug.DebugDetails;
import fr.umlv.unitex.debug.DebugGraphPane;
import fr.umlv.unitex.debug.DebugInfos;
import fr.umlv.unitex.debug.DebugTableModel;
import fr.umlv.unitex.files.FileUtil;

/**
 * This class describes a frame that can show an HTML concordance file.
 * 
 * @author Sébastien Paumier
 */
public class ConcordanceFrame extends TabbableInternalFrame {
	final BigConcordance list;
	private final JLabel numberOfMatches = new JLabel("");
	final JComponent invisible = new JComponent() {
		@Override
		protected void paintComponent(Graphics g) {
			/*
			 * Do nothing since this is an invisible component only used to
			 * catch mouse events.
			 */
		}

		@Override
		public boolean contains(int x, int y) {
			return true;
		}

		@Override
		public boolean contains(Point p) {
			return true;
		}
	};
	DebugInfos index = null;
	DebugTableModel model = null;
	DebugGraphPane graphPane = null;
	ListSelectionModel selectionModel = null;

	/**
	 * Constructs a new <code>ConcordanceFrame</code>.
	 */
	ConcordanceFrame(File f, int widthInChars) {
		super("", true, true, true, true);
		index = DebugInfos.loadConcordanceIndex(f);
		if (index != null) {
			model = new DebugTableModel(index);
			graphPane = new DebugGraphPane(index);
		}
		list = new BigConcordance();
		invisible.setOpaque(false);
		invisible.setVisible(true);
		invisible.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				getLayeredPane().remove(invisible);
				revalidate();
				repaint();
			}
		});
		final JScrollPane scroll = new JScrollPane(list);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		final JPanel middle = new JPanel(new BorderLayout());
		middle.setBorder(BorderFactory.createLoweredBevelBorder());
		middle.add(scroll, BorderLayout.CENTER);
		final JPanel top = new JPanel(new BorderLayout());
		top.add(middle, BorderLayout.CENTER);
		top.setBorder(new EmptyBorder(2, 2, 2, 2));
		final JPanel up = new JPanel(new BorderLayout());
		up.setBorder(new EmptyBorder(2, 2, 2, 2));
		up.add(numberOfMatches, BorderLayout.CENTER);
		top.add(up, BorderLayout.NORTH);
		if (index == null) {
			setContentPane(top);
		} else {
			/* In debug mode, we have to add things to the frame */
			setContentPane(createDebugFrame(top));
		}
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				list.reset();
				list.clearSelection();
			}

			@Override
			public void internalFrameDeactivated(InternalFrameEvent e) {
				/*
				 * Don't want to deal with a layout manager on the JLayeredPane,
				 * so we just set a big size.
				 */
				invisible.setSize(2000, 2000);
				/* We add the invisible component on the top of the layered pane */
				getLayeredPane().add(invisible, new Integer(600));
				revalidate();
				repaint();
			}

			@Override
			public void internalFrameActivated(InternalFrameEvent e) {
				getLayeredPane().remove(invisible);
				try {
					if (index != null)
						setMaximum(true);
				} catch (final PropertyVetoException e1) {
					e1.printStackTrace();
				}
				revalidate();
				repaint();
			}
		});
		setBounds(150, 50, 850, 550);
		load(f, widthInChars);
	}

	private JSplitPane createDebugFrame(JPanel concordPanel) {
		final JTable table = new JTable(model);
		table.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable t,
					Object value, boolean isSelected2, boolean hasFocus,
					int row, int column) {
				final String s = (String) value;
				setOpaque(true);
				if (column == 0 && (s.startsWith("<< ") || s.startsWith(">> "))) {
					setBackground(Color.LIGHT_GRAY);
				} else {
					setBackground(Color.WHITE);
				}
				super.getTableCellRendererComponent(t, s, isSelected2,
						hasFocus, row, column);
				return this;
			}
		});
		selectionModel = table.getSelectionModel();
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectionModel.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				final int n = selectionModel.getMinSelectionIndex();
				if (n == -1) {
					graphPane.clear();
				} else {
					final DebugDetails d = model.getDetailsAt(n);
					graphPane.setDisplay(d.graph, d.box, d.line);
				}
			}
		});
		final JScrollPane scroll = new JScrollPane(table);
		final JSplitPane split2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				concordPanel, graphPane);
		final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				scroll, split2);
		return split;
	}

	/**
	 * Constructs a new <code>ConcordanceFrame</code> and loads in it an HTML
	 * file. The number of lines in the concordance is shown in the caption of
	 * the frame.
	 * 
	 * @param concor
	 *            the HTML file
	 * @param widthInChars
	 *            width of a line in chars. Equals to the sum of left and right
	 *            context lengths
	 */
	private void load(File concor, int widthInChars) {
		setTitle("Concordance: " + concor.getAbsolutePath());
		numberOfMatches.setText(FileUtil.getHtmlPageTitle(concor));
		final Dimension d = getSize();
		final int g = widthInChars * 8;
		d.setSize((g < 800) ? g : 800, d.height);
		setSize(d);
		FileUtil.getHtmlPageTitle(concor);
		PreferencesManager.addPreferencesListener(new PreferencesListener() {
			@Override
			public void preferencesChanged(String language) {
				list.setFont(ConfigManager.getManager()
						.getConcordanceFont(null));
			}
		});
		list.setFont(ConfigManager.getManager().getConcordanceFont(null));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				TextFrame fTmp = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getTextFrame();
				if (fTmp == null) {
					/*
					 * In Gramlab, a concordance may be open while the text
					 * frame is not
					 */
					fTmp = GlobalProjectManager.search(null)
							.getFrameManagerAs(InternalFrameManager.class).newTextFrame(
							ConfigManager.getManager().getCurrentSnt(null),
							false);
				}
				final TextFrame f = fTmp;
				final String s = (String) list.getSelectedValue();
				if (s == null || e.getValueIsAdjusting())
					return;
				int start = s.indexOf("<a href=\"") + 9;
				int end = s.indexOf(' ', start);
				final int selectionStart = Integer.valueOf((String) s
						.subSequence(start, end));
				start = end + 1;
				end = s.indexOf(' ', start);
				final int selectionEnd = Integer.valueOf((String) s
						.subSequence(start, end));
				start = end + 1;
				end = s.indexOf(' ', start);
				final int sentenceNumber = Integer.valueOf((String) s
						.subSequence(start, end));
				start = end + 1;
				end = s.indexOf('\"', start);
				final int matchNumber = Integer.valueOf((String) s.subSequence(
						start, end));
				if (model != null) {
					model.setMatchNumber(matchNumber);
					if (selectionModel != null && model.getRowCount() > 0) {
						selectionModel.setSelectionInterval(0, 0);
					}
				}
				try {
					if (f.isIcon()) {
						f.setIcon(false);
					}
					f.getText().setSelection(selectionStart, selectionEnd - 1);
					f.getText().scrollToSelection();
					if (index == null) {
						/* We don't want to move text frame on top on debug mode */
						f.setSelected(true);
					}
				} catch (final PropertyVetoException e2) {
					e2.printStackTrace();
				}
				boolean iconified = true;
				final TextAutomatonFrame foo = GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.getTextAutomatonFrame();
				if (foo != null) {
					iconified = foo.isIcon();
				}
				GlobalProjectManager.search(null)
						.getFrameManagerAs(InternalFrameManager.class)
						.newTextAutomatonFrame(sentenceNumber, iconified);
				list.clearSelection();
				if (index != null) {
					try {
						setSelected(true);
					} catch (final PropertyVetoException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		list.load(concor);
	}

	@Override
	public String getTabName() {
		return "Concord";
	}
}
