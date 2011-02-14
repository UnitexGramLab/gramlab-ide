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

package fr.umlv.unitex.concord;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import fr.umlv.unitex.Preferences;


/**
 * This class provides a text component that can display in read-only
 * large HTML concordance files.
 *
 * @author Sébastien Paumier
 */
public class BigConcordanceDiff extends JList {


    private BigConcordanceDiff(ConcordanceDiffAsListModel m) {
        super(m);
        setPrototypeCellValue(new ConcordanceDiffAsListModel.DiffLine("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm", Color.BLACK, "mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm", Color.BLACK));
        setFont(new Font(Preferences.getConcordanceFontName(), 0, Preferences.getConcordanceFontSize()));
        setCellRenderer(new DefaultListCellRenderer() {

            final JPanel panel = new JPanel(new GridLayout(1, 2));
            final JPanel panel1 = new  JPanel(new BorderLayout());
            final JPanel panel2 = new  JPanel(new BorderLayout());
            final JLabel label1 = new JLabel();
            final Border border1 = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
            final Border border2 = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
            final JLabel label2 = new JLabel();

            {
                panel.setBackground(Color.WHITE);
                panel1.setOpaque(false);
                panel2.setOpaque(false);
                label1.setFont(getFont());
                label1.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
                label2.setFont(getFont());
                label2.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
                panel1.add(label1);
                panel2.add(label2);
                panel.add(panel1);
                panel.add(panel2);
            }

            @Override
            public void setFont(Font font) {
                if (label1 != null) label1.setFont(font);
                if (label2 != null) label2.setFont(font);
            }

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                ConcordanceDiffAsListModel.DiffLine diffLine = (ConcordanceDiffAsListModel.DiffLine) value;
                if (diffLine == null) {
                    label1.setText(null);
                    label2.setText(null);
                    return panel;
                }
                StringBuilder builder = new StringBuilder();
                builder.append("<html><body>");
                builder.append(diffLine.line1);
                builder.append("</body></html>");
                label1.setText(builder.toString());
                label1.setForeground(diffLine.color1);
                if (diffLine.line1 == null) {
                    panel1.setBorder(null);
                    label1.setText("");
                } else {
                    panel1.setBorder(border1);
                }
                builder.setLength(0);
                builder.append("<html><body>");
                builder.append(diffLine.line2);
                builder.append("</body></html>");
                label2.setText(builder.toString());
                label2.setForeground(diffLine.color2);
                if (diffLine.line2 == null) {
                    panel2.setBorder(null);
                    label2.setText(null);
                } else {
                    panel2.setBorder(border2);
                }
                return panel;
            }
        });
    }


    public BigConcordanceDiff() {
        this(new ConcordanceDiffAsListModel());
    }

    public void load(File f) {
        ConcordanceDiffAsListModel model = (ConcordanceDiffAsListModel) getModel();
        model.load(f);
    }

    public void reset() {
        ConcordanceDiffAsListModel model = (ConcordanceDiffAsListModel) getModel();
        model.reset();
    }

}
