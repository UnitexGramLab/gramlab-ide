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

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 * This class describes a <code>JPanel</code> that allows the user to set a
 * variable name.
 *
 * @author Sébastien Paumier
 */
class VariableInsertionDialog extends JDialog {

	JTextField name;
	final static Pattern pattern=Pattern.compile("^[a-zA-Z_0-9]+$");

	
    public VariableInsertionDialog(boolean inputVar) {
        super(UnitexFrame.mainFrame, true);
        JPanel p = new JPanel(new GridLayout(3, 1));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(new JLabel("Choose your "+(inputVar?"input":"output")+" variable name:"));
        name = new JTextField(30);
        name.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				if (pattern.matcher(name.getText()).matches()) {
					name.setForeground(Color.BLACK);
				} else {
					name.setForeground(Color.RED);
				}
			}
		});
        p.add(name);
        JButton ok = new JButton("OK");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        JPanel down = new JPanel();
        down.add(ok);
        p.add(down);
        setContentPane(p);
        pack();
        setLocationRelativeTo(UnitexFrame.mainFrame);
    }

    public String getVariableName() {
		if (pattern.matcher(name.getText()).matches()) {
			return name.getText();
		}
		return null;
    }
    
}
