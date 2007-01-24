 /*
  * Unitex
  *
  * Copyright (C) 2001-2007 Université de Marne-la-Vallée <unitex@univ-mlv.fr>
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

import javax.swing.*;
import javax.swing.text.*;

/**
 * This class provides a <code>JTextField</code> that accepts only digits. It is useful to 
 * read integers.
 * @author Sébastien Paumier
 *
 */
public class NumericTextField extends JTextField {

   private class NumericTextDocument extends PlainDocument {
      public void insertString(int offs, String s, AttributeSet a)
         throws BadLocationException {
         int i;
         if (s == null)
            return;
         char c[]= s.toCharArray();
         for (i= 0; i < c.length; i++) {
            if ((c[i] != 10) && (c[i] < '0' || c[i] > '9'))
               return;
         }
         super.insertString(offs, s, a);
      }
   }

   /**
    * Creates a <code>NumericTextField</code> and sets its contents with a <code>String</code>.  
    * @param s the <code>String</code> that will appear in the component
    */
   public NumericTextField(String s) {
      super();
      setEditable(true);
      setText(s);
      setHorizontalAlignment(SwingConstants.RIGHT);
   }

   /**
    * Constructs a new <code>NumericTextField</code> with a specified number of columns and 
    * initializes it with a <code>String</code>
    * @param c the number of columns
    * @param s the <code>String</code> that will appear in the component
    */
   public NumericTextField(int c, String s) {
      super(c);
      setEditable(true);
      setText(s);
      setHorizontalAlignment(SwingConstants.RIGHT);
   }

   /**
    * Creates and returns a  <code>Document</code> designed to accept only digits.
    * @return the document
    */
   public Document createDefaultModel() {
      return new NumericTextDocument();
   }


   /**
    * Sets the content of the <code>NumericTextField</code>.
    * @param n the value
    */
   public void setText(int n) {
      setText(""+n);
   }

}
