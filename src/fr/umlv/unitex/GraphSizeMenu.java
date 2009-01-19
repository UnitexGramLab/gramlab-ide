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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

/**
 * This class describes a dialog box that allows the user to adjust the current
 * graph's size.
 * 
 * @author Sébastien Paumier
 *  
 */
public class GraphSizeMenu extends JDialog {

	SpecialNumericTextField Width = new SpecialNumericTextField(4, String
			.valueOf(0));
	SpecialNumericTextField Height = new SpecialNumericTextField(4, String
			.valueOf(0));
	static int resolutionDPI = Toolkit.getDefaultToolkit().getScreenResolution();

	static GraphSizeMenu pref;
	float X;
	float Y;
	static final int PIXELS = 1;
	static final int INCHES = 2;
	static final int CM = 3;
	static int unit = PIXELS;

	/**
	 * Constructs a new <code>GraphSizeMenu</code>
	 *  
	 */
	public GraphSizeMenu() {
		super(UnitexFrame.mainFrame, "Graph Size", true);
		pref = this;
		GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
		if (f == null) {
			return;
		}
		setContentPane(constructPanel());
		X = f.graphicalZone.Width;
		Y = f.graphicalZone.Height;
		unit = PIXELS;
		Width.setText(stringValueOfX());
		Height.setText(stringValueOfY());
		pack();
		setResizable(false);
		pref = this;
		showMenu();
	}

	private float getValueOfX() {
		if (unit == PIXELS)
			return X;
		else if (unit == INCHES)
			return (X / resolutionDPI);
		else
			return ((X * 24 / resolutionDPI) / 10);
	}

	private float getValueOfY() {
		if (unit == PIXELS)
			return Y;
		else if (unit == INCHES)
			return (Y / resolutionDPI);
		else
			return ((Y * 24 / resolutionDPI) / 10);
	}

	String stringValueOfX() {
		if (unit == PIXELS)
			return String.valueOf((int) getValueOfX());
		return String.valueOf((float) ((int) (getValueOfX() * 100)) / 100);
	}

	String stringValueOfY() {
		if (unit == PIXELS)
			return String.valueOf((int) getValueOfY());
		return String.valueOf((float) ((int) (getValueOfY() * 100)) / 100);
	}

	/**
	 * Shows the dialog box
	 *  
	 */
	public void showMenu() {
		setLocationRelativeTo(UnitexFrame.mainFrame);
		setVisible(true);
	}

	private JPanel constructPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		panel.add(constructPanel1(), BorderLayout.NORTH);
		panel.add(constructDownPanel(), BorderLayout.CENTER);
		return panel;
	}

	private JPanel constructPanel1() {
		JPanel panel1 = new JPanel();
		panel1.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel1.add(new JLabel("Width : "));
		Width.setEditable(true);
		panel1.add(Width);
		panel1.add(new JLabel(" x Height : "));
		Height.setEditable(true);
		panel1.add(Height);
		return panel1;
	}

	private JPanel constructDownPanel() {
		JPanel downPanel = new JPanel(new BorderLayout());
		downPanel.add(constructPanel2(), BorderLayout.WEST);
		downPanel.add(constructPanel3(), BorderLayout.CENTER);
		return downPanel;
	}

	private JPanel constructPanel2() {
		JPanel panel2 = new JPanel(new GridLayout(3, 1));
		panel2.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel2.add(new JLabel("Unit :"));
    final Choice unitList = new Choice();
		unitList.addItem("Pixels");
		unitList.addItem("Inches");
		unitList.addItem("Cm");
		unitList.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED)
                    return;
                float x, y;
                try {
                    x = (new Float(Width.getText())).floatValue();
                    y = (new Float(Height.getText())).floatValue();
                } catch (NumberFormatException z) {
                    JOptionPane.showMessageDialog(null, "Invalid value", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (unit == PIXELS) {
                    X = x;
                    Y = y;
                } else if (unit == INCHES) {
                    X = (x * resolutionDPI);
                    Y = (y * resolutionDPI);
                } else {
                    X = (float) ((x * resolutionDPI) / 2.4);
                    Y = (float) ((y * resolutionDPI) / 2.4);
                }
                unit = unitList.getSelectedIndex() + 1;
                Width.setText(stringValueOfX());
                Height.setText(stringValueOfY());
                repaint();
            }
        });
		unitList.select(0);
		panel2.add(unitList);
    Action a4Action=new AbstractAction("Set to A4") {
		public void actionPerformed(ActionEvent arg0) {
            X = (float) ((29.7 * resolutionDPI) / 2.4);
            Y = (float) ((21 * resolutionDPI) / 2.4);
            Width.setText(stringValueOfX());
            Height.setText(stringValueOfY());
            repaint();
		}};
    JButton A4 = new JButton(a4Action);
		panel2.add(A4);
		return panel2;
	}

	private JPanel constructPanel3() {
		JPanel panel3 = new JPanel(new GridLayout(3, 1));
		panel3.setBorder(new EmptyBorder(5, 5, 5, 5));
		constructButtonPanel();
		panel3.add(new JLabel("Orientation :"));
    Action orientationAction=new AbstractAction("Portrait/Landscape") {
		public void actionPerformed(ActionEvent arg0) {
            float tmp;
            tmp = X;
            X = Y;
            Y = tmp;
            Width.setText(stringValueOfX());
            Height.setText(stringValueOfY());
            repaint();
		}};
    JButton orientation = new JButton(orientationAction);
    panel3.add(orientation);
		panel3.add(constructButtonPanel());
		return panel3;
	}

	private JPanel constructButtonPanel() {
		JPanel buttonPanel=new JPanel(new GridLayout(1, 2));
    Action okAction=new AbstractAction("OK") {
		public void actionPerformed(ActionEvent arg0) {
            float x, y;
            try {
                x = (new Float(Width.getText())).floatValue();
                y = (new Float(Height.getText())).floatValue();
            } catch (NumberFormatException z) {
                JOptionPane.showMessageDialog(null, "Invalid value", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (unit == PIXELS) {
                X = x;
                Y = y;
            } else if (unit == INCHES) {
                X = (x * resolutionDPI);
                Y = (y * resolutionDPI);
            } else {
                X = (float) ((x * resolutionDPI) / 2.4);
                Y = (float) ((y * resolutionDPI) / 2.4);
            }
            GraphFrame f = UnitexFrame.getCurrentFocusedGraphFrame();
            f.reSizeGraphicalZone((int) X, (int) Y);
            f.setModified(true);
            GraphSizeMenu.pref.setVisible(false);
		}};
    JButton OK=new JButton(okAction);
    Action cancelAction=new AbstractAction("Cancel") {
		public void actionPerformed(ActionEvent arg0) {
            GraphSizeMenu.pref.setVisible(false);
		}};
    JButton CANCEL=new JButton(cancelAction);
    buttonPanel.add(OK);
		buttonPanel.add(CANCEL);
    return buttonPanel;
	}

	class SpecialNumericTextField extends JTextField {

		SpecialNumericTextField(int c, String s) {
			super(c);
			setEditable(true);
			setText(s);
			setEditable(false);
		}

		public Document createDefaultModel() {
			return new SpecialNumericTextDocument();
		}
	}

	class SpecialNumericTextDocument extends PlainDocument {
		public void insertString(int offs, String s, AttributeSet a)
				throws BadLocationException {
			int i;
			if (s == null)
				return;
			char c[] = s.toCharArray();
			for (i = 0; i < c.length; i++) {
				if ((c[i] < '0' || c[i] > '9') && c[i] != '.')
					return;
				if ((GraphSizeMenu.unit == PIXELS) && (c[i] == '.'))
					return;
			}
			super.insertString(offs, s, a);
		}
	}

}