 /*
  * Unitex
  *
  * Copyright (C) 2001-2008 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

/**
 * This class describes a <code>JPanel</code> that allows the user to set left and right contexts 
 * for multiple word copies. This object is designed to be inserted into a dialog box. 
 * @author Sébastien Paumier
 *
 */
public class ListCopyDialog extends JPanel {

   JTextField left, right;

   /**
    * Constructs a new empty <code>ListCopyDialog</code> 
    *
    */
   public ListCopyDialog() {
      super();
      setBorder(new EmptyBorder(10, 10, 10, 10));
      setLayout(new GridLayout(2, 1));
      // placing the text
      add(new JLabel("Choose your left and right contexts:"));
      // placing the textfields
      left= new JTextField(5);
      left.setHorizontalAlignment(SwingConstants.RIGHT);
      right= new JTextField(5);
      JPanel inputPanel= new JPanel();
      inputPanel.setLayout(new FlowLayout());
      inputPanel.add(left);
      inputPanel.add(new JLabel("item"));
      inputPanel.add(right);
      add(inputPanel);
      // adding listeners for the text fields
      left.addKeyListener(new leftListener());
      right.addKeyListener(new rightListener());
   }

   /**
    * Constructs a new <code>ListCopyDialog</code> and sets
    * left and right contexts.
    * 
    * @param l left context
    * @param r right context
    */
   public ListCopyDialog(String l, String r) {
      this();
      left.setText(l);
      right.setText(r);
   }

   class leftListener implements KeyListener {
      public void keyTyped(KeyEvent e) {
         TextField.leftContext= left.getText();
      }
      public void keyReleased(KeyEvent e) {
         TextField.leftContext= left.getText();
      }
      public void keyPressed(KeyEvent e) {
         TextField.leftContext= left.getText();
      }
   }

   class rightListener implements KeyListener {
      public void keyTyped(KeyEvent e) {
         TextField.rightContext= right.getText();
      }
      public void keyReleased(KeyEvent e) {
         TextField.rightContext= right.getText();
      }
      public void keyPressed(KeyEvent e) {
         TextField.rightContext= right.getText();
      }
   }

}