/*
 * Unitex
 *
 * Copyright (C) 2001-2010 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.util.*;

import javax.swing.*;
import javax.swing.text.*;

/*
 * This class catches text and turns it into a list if it contains \n
 *
 */
/**
 * This class describes a <code>PlainDocument</code> object that detects multiple word copies.
 * If the text that must be added to the document contains carridge returns, a multiple word copy 
 * is done. If not, the normal paste operation is done to add this text to the document.
 * 
 * @author Sébastien Paumier
 *
 */
public class ListDocument extends PlainDocument {

   static int i= 0;

   /**
    * Tries to insert a string in the document.
    * @param offs offset to insert the string
    * @param s string to be inserted
    * @param a attribute set
    */
   public void insertString(int offs, String s, AttributeSet a)
      throws BadLocationException {
      if (s == null) {
         // exits if there is nothing to do
         return;
      }
      if (s.length() == 1) {
         super.insertString(offs, s, a);
         return;
      }
      if (s.startsWith("\n"))
         s= s.substring(1);
      if (s.endsWith("\n"))
         s= s.substring(0, s.length() - 1);
      if (s.indexOf("\n") == -1) {
         // if this is a single string, we return it
         super.insertString(offs, s, a);
         return;
      }

      // here we show the dialog box to choose contexts
      JOptionPane.showMessageDialog(
         null,
         new ListCopyDialog(TextField.leftContext, TextField.rightContext));

      // tokenizes the text
      StringTokenizer st= new StringTokenizer(s, "\n");
      String res= TextField.leftContext;
      res= res.concat(st.nextToken());
      res= res.concat(TextField.rightContext);
      while (st.hasMoreTokens()) {
         res= res.concat("+");
         res= res.concat(TextField.leftContext);
         res= res.concat(st.nextToken());
         res= res.concat(TextField.rightContext);
      }
      super.insertString(offs, res, a);
   }
}
