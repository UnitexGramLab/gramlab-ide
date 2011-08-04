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

import java.awt.Component;
import java.awt.Font;
import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.config.Preferences;


/**
 * This class provides a text component that can display in read-only
 * large HTML concordance files.
 *
 * @author Sébastien Paumier
 */
public class BigConcordance extends JList {


    private BigConcordance(ConcordanceAsListModel m) {
        super(m);
        setFont(ConfigManager.getManager().getConcordanceFont(null));
        setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                StringBuilder builder = new StringBuilder();
                builder.append("<html><body>");
                String s = (String) value;
                builder.append(s);
                builder.append("</body></html>");
                setText(builder.toString());
                return this;
            }
        });
    }


    public BigConcordance() {
        this(new ConcordanceAsListModel());
    }

    public void load(File f) {
        ConcordanceAsListModel model = (ConcordanceAsListModel) getModel();
        model.load(f);
    }

    public void reset() {
        ConcordanceAsListModel model = (ConcordanceAsListModel) getModel();
        model.reset();
    }

}
