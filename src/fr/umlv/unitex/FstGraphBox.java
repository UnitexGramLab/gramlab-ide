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

/**
 * This class describes a box of a sentence automaton. 
 * @author Sébastien Paumier
 *
 */
public class FstGraphBox extends GenericGraphBox {


   /**
    * Constructs a new box 
    * @param x X coordinate of the input point of the box
    * @param y Y coordinate of the input point of the box
    * @param type indicates if the box is initial, final or normal
    * @param p component on which the box will be drawn
    */
   public FstGraphBox(int x, int y, int type, FstGraphicalZone p) {
      super(x,y,type,p);
   }


   /**
    * Takes a <code>String</code> representing the box content and tokenizes it to divide it
    * into several lines  
    * @param s the box content
    */
   private void tokenizeText(String s) {
      int L= s.length(), i;
      String flechi;
      String canonique;
      String infos;
      char ligne[]= new char[10000];
      ligne= s.toCharArray();
      if (s.equals("<E>"))
         return;
      if (ligne[0] != '{') {
         n_lignes++;
         lines.add(s);
         greyed.add(new Boolean(false));
         return;
      }
      i= 1;
      flechi= "";
      while (i < L && ligne[i] != ',') {
         if (ligne[i] == '\\' && (i + 1) < L)
            i++;
         flechi= flechi.concat(String.valueOf(ligne[i]));
         i++;
      }
      i++;
      canonique= "";
      while (i < L && ligne[i] != '.') {
         if (ligne[i] == '\\' && (i + 1) < L)
            i++;
         canonique= canonique.concat(String.valueOf(ligne[i]));
         i++;
      }
      i++;
      infos= "";
      while (i < L && ligne[i] != '}') {
         if (ligne[i] == '\\' && (i + 1) < L)
            i++;
         infos= infos.concat(String.valueOf(ligne[i]));
         i++;
      }
      n_lignes++;
      lines.add(flechi);
      greyed.add(new Boolean(false));
      if (!(canonique.equals("") || canonique.equals(flechi))) {
         // if inflected form is equal to canonical, we don't insert it twice
         n_lignes++;
         lines.add(canonique);
         greyed.add(new Boolean(false));
      }
      transduction= infos;
   }


   /**
    * Sets the content of the box 
    * @param s the content
    */
   public void setContent(String s) {
      if (type == FINAL)
         return; // nothing to do if we consider the final state
      content= s;
      String tmp= "";
      n_lignes= 0;
      tmp= s;
      transduction= "";
      lines.clear();
      greyed.clear();
      tokenizeText(s);

      if (!tmp.equals("<E>")) {
         // dimensions of a full box
         Width= maxLineWidth() + 10;
         Height= n_lignes * get_h_ligne() + 6;
      } else {
         // dimensions of an empty box
         Height= 20;
         Width= 15;
      }
      Y1= Y - Height / 2;
      X_out= x + Width + 5;
   }

}
