/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.umlv.unitex.concord.BigConcordanceDiff;
import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.config.Preferences;
import fr.umlv.unitex.config.PreferencesListener;
import fr.umlv.unitex.config.PreferencesManager;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.listeners.FontListener;

/**
 * This class describes a frame that can show an HTML condordance diff file.
 * 
 * @author S&bastien Paumier
 */
public class ConcordanceDiffFrame extends JInternalFrame {
	
	private final BigConcordanceDiff list = new BigConcordanceDiff();
	private final JComponent invisible = new JComponent() {
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

	/**
	 * Constructs a new empty <code>ConcordanceDiffFrame</code>.
	 */
	ConcordanceDiffFrame() {
		super("Concordance Diff", true, true, true, true);
		JScrollPane scroll = new JScrollPane(list);
		JPanel middle = new JPanel(new BorderLayout());
		middle.add(scroll, BorderLayout.CENTER);
		JPanel top = new JPanel(new GridLayout(4, 1));
		top.setBackground(Color.WHITE);
		top.setBorder(new EmptyBorder(2, 2, 5, 2));
		top
				.add(new JLabel(
						"<html><body><font color=\"#800080\">Violet:</font>&nbsp;identical sequences with different outputs</body></html>"));
		top
				.add(new JLabel(
						"<html><body><font color=\"#FF0000\">Red:</font>&nbsp;similar but different sequences</body></html>"));
		top
				.add(new JLabel(
						"<html><body><font color=\"#008000\">Green:</font>&nbsp;sequences that occur in only one of the two concordances</body></html>"));
		top.add(new JLabel("<html><body><font bgcolor=\"#D2D2D2\">Grey background=previous matches</font>&nbsp;&nbsp;White background=new matches</body></html>"));
		middle.add(top, BorderLayout.NORTH);
		setContentPane(middle);
        list.setFont(ConfigManager.getManager().getConcordanceFont(null));
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
		PreferencesManager.addPreferencesListener(new PreferencesListener() {
			public void preferencesChanged(String language) {
				list.setFont(ConfigManager.getManager().getConcordanceFont(null));
			}
		});
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
				revalidate();
				repaint();
			}
		});
		setBounds(150, 50, 850, 550);
	}

	/**
	 * Constructs a new <code>ConcordanceDiffFrame</code> if needed and loads in
	 * it an HTML file.
	 * 
	 * @param concor
	 *            the HTML file
	 * @param widthInChars
	 *            width of a line in chars. Equals to the sum of left and right
	 *            context lengths
	 */
	void load(File concor) {
		Dimension d = getSize();
		d.setSize(800, d.height);
		FileUtil.getHtmlPageTitle(concor);
		list.load(concor);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new ListSelectionListener() {

        	public void valueChanged(ListSelectionEvent e) {
                TextFrame f = UnitexFrame.getFrameManager().getTextFrame();
                String s = (String) list.getSelectedValue();
                if (s == null || e.getValueIsAdjusting()) return;
                int start = s.indexOf("<a href=\"") + 9;
                int end = s.indexOf(' ', start);
                int selectionStart = Integer.valueOf((String) s.subSequence(start, end));
                start = end + 1;
                end = s.indexOf(' ', start);
                int selectionEnd = Integer.valueOf((String) s.subSequence(start, end));
                start = end + 1;
                end = s.indexOf(' ', start);
                int sentenceNumber = Integer.valueOf((String) s.subSequence(start, end));
                start = end + 1;
                end = s.indexOf('\"', start);
                int matchNumber = Integer.valueOf((String) s.subSequence(start, end));
                try {
                	if (f.isIcon()) {
                		f.setIcon(false);
                	}
                    f.getText().setSelection(selectionStart, selectionEnd - 1);
                    f.getText().scrollToSelection();
                    f.setSelected(true);
                } catch (PropertyVetoException e2) {
                    e2.printStackTrace();
                }
                boolean iconified=true;
                TextAutomatonFrame foo=UnitexFrame.getFrameManager().getTextAutomatonFrame();
                if (foo!=null) {
                	iconified=foo.isIcon();
                }
                UnitexFrame.getFrameManager().newTextAutomatonFrame(sentenceNumber,iconified);
                list.clearSelection();
            }
        });

	}
}
