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

package fr.umlv.unitex.io;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import fr.umlv.unitex.*;
import fr.umlv.unitex.exceptions.*;

/**
 * This class provides methods for loading and saving graphs.
 * @author Sébastien Paumier
 *
 */
public class GraphIO {

   /**
    * Boxes of a graph 
    */
   public ArrayList<GenericGraphBox> boxes;

   /**
    * Rendering properties of a graph 
    */
   public Preferences pref= new Preferences();

   /**
    * Width of a graph 
    */
   public int width;

   /**
    * Height of a graph 
    */
   public int height;

   /**
    * Number of boxes of a graph 
    */
   public int nBoxes;

   /**
    * This method loads a graph. 
    * @param grfFile name of the graph 
    * @return a <code>GraphIO</code> object describing the graph
    */
   public static GraphIO loadGraph(File grfFile) {
      GraphIO res= new GraphIO();
      FileInputStream source;
      if (!grfFile.exists()) {
         JOptionPane.showMessageDialog(
            null,
            "Cannot find " + grfFile.getAbsolutePath(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return null;
      }
      if (!grfFile.canRead()) {
         JOptionPane.showMessageDialog(
            null,
            "Cannot read " + grfFile.getAbsolutePath(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return null;
      }
      if (grfFile.length() <= 2) {
         JOptionPane.showMessageDialog(
            null,
            grfFile.getAbsolutePath() + " is empty",
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return null;
      }
      try {
         source= UnicodeIO.openUnicodeLittleEndianFileInputStream(grfFile);
         UnicodeIO.skipLine(source); // ignoring #...
         res.readSize(source);
         res.readInputFont(source);
         res.readOutputFont(source);
         res.readBackgroundColor(source);
         res.readForegroundColor(source);
         res.readSubgraphColor(source);
         res.readCommentColor(source);
         res.readSelectedColor(source);
         UnicodeIO.skipLine(source); // ignoring DBOXES
         res.readDrawFrame(source);
         res.readDate(source);
         res.readFile(source);
         res.readDirectory(source);
         res.readRightToLeft(source);
         UnicodeIO.skipLine(source); // ignoring DRST
         UnicodeIO.skipLine(source); // ignoring FITS
         UnicodeIO.skipLine(source); // ignoring PORIENT
         UnicodeIO.skipLine(source); // ignoring #
         res.readBoxNumber(source);
         res.boxes= new ArrayList<GenericGraphBox>();
         // adding initial state
         res.boxes.add(new GraphBox(0, 0, 0,null));
         // adding final state
         res.boxes.add(new GraphBox(0, 0, 1, null));
         // adding other states
         for (int i= 2; i < res.nBoxes; i++)
            res.boxes.add(new GraphBox(0, 0, 2, null));
         for (int i= 0; i < res.nBoxes; i++)
            res.readGraphLine(source, i);
         source.close();
      } catch (NotAUnicodeLittleEndianFileException e) {
         JOptionPane.showMessageDialog(
            null,
            grfFile.getAbsolutePath() + " is not a Unicode Little-Endian graph",
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return null;
      } catch (FileNotFoundException e) {
         e.printStackTrace();
         return null;
      } catch (IOException e) {
         e.printStackTrace();
         return null;
      }
      return res;
   }

   private void readSize(FileInputStream f) {
      // skipping the chars preceeding the width and height
      UnicodeIO.skipChars(f, 5);
      char c;
      // reading width
      width= 0;
      while (UnicodeIO.isDigit((c= (char)UnicodeIO.readChar(f))))
         width= width * 10 + (c - '0');
      // reading height
      while (UnicodeIO.isDigit((c= (char)UnicodeIO.readChar(f))))
         height= height * 10 + (c - '0');
   }

   private void readInputFont(FileInputStream f) {
      UnicodeIO.skipChars(f, 5);
      String s= "";
      char c;
      while ((c= (char)UnicodeIO.readChar(f)) != ':')
         s= s + c;
      boolean bold= (UnicodeIO.readChar(f) == 'B');
      boolean italic= (UnicodeIO.readChar(f) == 'I');
      int size= 0;
      while (UnicodeIO.isDigit((c= (char)UnicodeIO.readChar(f))))
         size= size * 10 + (c - '0');
      pref.inputSize= size;
      int style;
      if (bold && italic)
         style= Font.BOLD | Font.ITALIC;
      else
         if (bold)
            style= Font.BOLD;
         else
            if (italic)
               style= Font.ITALIC;
            else
               style= Font.PLAIN;
      pref.input= new Font(s, style, (int) (size / 0.72));
   }

   private void readOutputFont(FileInputStream f) {
      UnicodeIO.skipChars(f, 6);
      String s= "";
      char c;
      while ((c= (char)UnicodeIO.readChar(f)) != ':')
         s= s + c;
      boolean bold= (UnicodeIO.readChar(f) == 'B');
      boolean italic= (UnicodeIO.readChar(f) == 'I');
      int size= 0;
      while (UnicodeIO.isDigit((c= (char)UnicodeIO.readChar(f))))
         size= size * 10 + (c - '0');
      pref.outputSize= size;
      int style;
      if (bold && italic)
         style= Font.BOLD | Font.ITALIC;
      else
         if (bold)
            style= Font.BOLD;
         else
            if (italic)
               style= Font.ITALIC;
            else
               style= Font.PLAIN;
      pref.output= new Font(s, style, (int) (size / 0.72));
   }

   private void readBackgroundColor(FileInputStream f) {
      UnicodeIO.skipChars(f, 7);
      char c;
      int n= 0;
      while (UnicodeIO.isDigit((c= (char)UnicodeIO.readChar(f))))
         n= n * 10 + (c - '0');
      pref.backgroundColor= new Color(n);
   }

   private void readForegroundColor(FileInputStream f) {
      UnicodeIO.skipChars(f, 7);
      char c;
      int n= 0;
      while (UnicodeIO.isDigit((c= (char)UnicodeIO.readChar(f))))
         n= n * 10 + (c - '0');
      pref.foregroundColor= new Color(n);
   }

   private void readSubgraphColor(FileInputStream f) {
      UnicodeIO.skipChars(f, 7);
      char c;
      int n= 0;
      while (UnicodeIO.isDigit((c= (char)UnicodeIO.readChar(f))))
         n= n * 10 + (c - '0');
      pref.subgraphColor= new Color(n);
   }

   private void readSelectedColor(FileInputStream f) {
      UnicodeIO.skipChars(f, 7);
      char c;
      int n= 0;
      while (UnicodeIO.isDigit((c= (char)UnicodeIO.readChar(f))))
         n= n * 10 + (c - '0');
      pref.selectedColor= new Color(n);
   }

   private void readCommentColor(FileInputStream f) {
      UnicodeIO.skipChars(f, 7);
      char c;
      int n= 0;
      while (UnicodeIO.isDigit((c= (char)UnicodeIO.readChar(f))))
         n= n * 10 + (c - '0');
      pref.commentColor= new Color(n);
   }

   private void readDrawFrame(FileInputStream f) {
      UnicodeIO.skipChars(f, 7);
      pref.frame= (UnicodeIO.readChar(f) == 'y');
      UnicodeIO.readChar(f);
   }

   private void readDate(FileInputStream f) {
      UnicodeIO.skipChars(f, 6);
      pref.date= (UnicodeIO.readChar(f) == 'y');
      UnicodeIO.readChar(f);
   }

   private void readFile(FileInputStream f) {
      UnicodeIO.skipChars(f, 6);
      pref.filename= (UnicodeIO.readChar(f) == 'y');
      UnicodeIO.readChar(f);
   }

   private void readDirectory(FileInputStream f) {
      UnicodeIO.skipChars(f, 5);
      pref.pathname= (UnicodeIO.readChar(f) == 'y');
      UnicodeIO.readChar(f);
   }

   private void readRightToLeft(FileInputStream f) {
      UnicodeIO.skipChars(f, 5);
      pref.rightToLeft= (UnicodeIO.readChar(f) == 'y');
      UnicodeIO.readChar(f);
   }

   private void readBoxNumber(FileInputStream f) {
      char c;
      nBoxes= 0;
      while (UnicodeIO.isDigit((c= (char)UnicodeIO.readChar(f))))
         nBoxes= nBoxes * 10 + (c - '0');
   }

   private void readGraphLine(FileInputStream f, int n) {
      GenericGraphBox g= boxes.get(n);
      if (UnicodeIO.readChar(f) == 's') {
         // is a "s" was read, then we read the " char
         UnicodeIO.readChar(f);
      }
      String s= "";
      char c;
      while ((c= (char)UnicodeIO.readChar(f)) != '"') {
         if (c == '\\') {
            c= (char)UnicodeIO.readChar(f);
            if (c != '\\') {
               // case of \: \+ and \"
               if (c == '"')
                  s= s + c;
               else
                  s= s + "\\" + c;
            } else {
               // case of \\\" that must be transformed into \"
               c= (char)UnicodeIO.readChar(f);
               if (c == '\\') {
                  // we are in the case \\\" -> \"
                  c= (char)UnicodeIO.readChar(f);
                  s= s + "\\" + c;
               } else {
                  // we are in the case \\a -> \\a
                  s= s + "\\\\";
                  if (c!='"') s=s+c;
                  else break;
               }
            }
         } else
            s= s + c;
      }
      // skipping the space after "
      UnicodeIO.readChar(f);
      // reading the X coordinate
      int x= 0;
      int neg= 1;
      c= (char)UnicodeIO.readChar(f);
      if (c == '-') {
         neg= -1;
      } else
         if (UnicodeIO.isDigit(c)) {
            x= (c - '0');
         }
      while (UnicodeIO.isDigit((c= (char)UnicodeIO.readChar(f)))) {
         x= x * 10 + (c - '0');
      }
      x=x*neg;
      // reading the Y coordinate
      int y= 0;
      neg=1;
      c= (char)UnicodeIO.readChar(f);
            if (c == '-') {
               neg= -1;
            } else
               if (UnicodeIO.isDigit(c)) {
                  y= (c - '0');
               }
      while (UnicodeIO.isDigit((c= (char)UnicodeIO.readChar(f)))) {
         y= y * 10 + (c - '0');
      }
      y=y*neg;
      g.setX(x);
      g.setY(y);
      g.setX1(g.getX());
      g.setY1(g.getY());
      g.setX_in(g.getX());
      g.setY_in(g.getY());
      g.setX_out(g.getX() + g.getWidth() + 5);
      g.setY_out(g.getY_in());
      if (n != 1) {
         // 1 is the final state, which content is <E>
         g.setContent(s);
         // we will need to call g.update() to size the box according to the text
      } else {
         g.setContent("<E>");
         g.setX_in(g.getX());
         g.setY_in(g.getY());
         g.setX1(g.getX());
         g.setY1(g.getY() - 10);
         g.setY_out(g.getY_in());
         g.setX_out(g.getX_in() + 25);
      }
      int trans= 0;
      while (UnicodeIO.isDigit((c= (char)UnicodeIO.readChar(f))))
         trans= trans * 10 + (c - '0');
      for (int j= 0; j < trans; j++) {
         int dest= 0;
         while (UnicodeIO.isDigit((c= (char)UnicodeIO.readChar(f))))
            dest= dest * 10 + (c - '0');
         g.addTransitionTo(boxes.get(dest));
      }
      // skipping the end-of-line
      UnicodeIO.readChar(f);
   }

   /**
    * Saves the graph referenced by the field of this <code>GraphIO</code> object
    * @param grfFile graph file
    */
   public void saveGraph(File grfFile) {
      FileOutputStream dest;
      try {
         if (!grfFile.exists())
            grfFile.createNewFile();
      } catch (IOException e) {
         JOptionPane.showMessageDialog(
            null,
            "Cannot write " + grfFile.getAbsolutePath(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return;
      }
      if (!grfFile.canWrite()) {
         JOptionPane.showMessageDialog(
            null,
            "Cannot write " + grfFile.getAbsolutePath(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return;
      }
      try {
         dest= new FileOutputStream(grfFile);
         UnicodeIO.writeChar(dest, (char)0xFEFF);
         UnicodeIO.writeString(dest, "#Unigraph\n");
         UnicodeIO.writeString(
            dest,
            "SIZE "
               + String.valueOf(width)
               + " "
               + String.valueOf(height)
               + "\n");
         UnicodeIO.writeString(dest, "FONT " + pref.input.getName() + ":");
         if (pref.input.getStyle() == Font.PLAIN)
            UnicodeIO.writeString(dest, "  ");
         else
            if (pref.input.getStyle() == Font.BOLD)
               UnicodeIO.writeString(dest, "B ");
            else
               if (pref.input.getStyle() == Font.ITALIC)
                  UnicodeIO.writeString(dest, " I");
               else
                  UnicodeIO.writeString(dest, "BI");
         UnicodeIO.writeString(dest, String.valueOf(pref.inputSize) + "\n");
         UnicodeIO.writeString(dest, "OFONT " + pref.output.getName() + ":");
         if (pref.output.getStyle() == Font.PLAIN)
            UnicodeIO.writeString(dest, "  ");
         else
            if (pref.output.getStyle() == Font.BOLD)
               UnicodeIO.writeString(dest, "B ");
            else
               if (pref.output.getStyle() == Font.ITALIC)
                  UnicodeIO.writeString(dest, " I");
               else
                  UnicodeIO.writeString(dest, "BI");
         UnicodeIO.writeString(dest, String.valueOf(pref.outputSize) + "\n");
         UnicodeIO.writeString(
            dest,
            "BCOLOR "
               + String.valueOf(16777216 + pref.backgroundColor.getRGB())
               + "\n");
         UnicodeIO.writeString(
            dest,
            "FCOLOR "
               + String.valueOf(16777216 + pref.foregroundColor.getRGB())
               + "\n");
         UnicodeIO.writeString(
            dest,
            "ACOLOR "
               + String.valueOf(16777216 + pref.subgraphColor.getRGB())
               + "\n");
         UnicodeIO.writeString(
            dest,
            "SCOLOR "
               + String.valueOf(16777216 + pref.commentColor.getRGB())
               + "\n");
         UnicodeIO.writeString(
            dest,
            "CCOLOR "
               + String.valueOf(16777216 + pref.selectedColor.getRGB())
               + "\n");
         UnicodeIO.writeString(dest, "DBOXES y\n");
         if (pref.frame)
            UnicodeIO.writeString(dest, "DFRAME y\n");
         else
            UnicodeIO.writeString(dest, "DFRAME n\n");
         if (pref.date)
            UnicodeIO.writeString(dest, "DDATE y\n");
         else
            UnicodeIO.writeString(dest, "DDATE n\n");
         if (pref.filename)
            UnicodeIO.writeString(dest, "DFILE y\n");
         else
            UnicodeIO.writeString(dest, "DFILE n\n");
         if (pref.pathname)
            UnicodeIO.writeString(dest, "DDIR y\n");
         else
            UnicodeIO.writeString(dest, "DDIR n\n");
         if (pref.rightToLeft)
            UnicodeIO.writeString(dest, "DRIG y\n");
         else
            UnicodeIO.writeString(dest, "DRIG n\n");
         UnicodeIO.writeString(dest, "DRST n\n");
         UnicodeIO.writeString(dest, "FITS 100\n");
         UnicodeIO.writeString(dest, "PORIENT L\n");
         UnicodeIO.writeString(dest, "#\n");
         nBoxes= boxes.size();
         UnicodeIO.writeString(dest, String.valueOf(nBoxes) + "\n");
         for (int i= 0; i < nBoxes; i++) {
            GenericGraphBox g= boxes.get(i);
            UnicodeIO.writeChar(dest, '"');
            if (g.getType() != 1)
               write_content(dest, g.getContent());
            int N= g.getTransitions().size();
            UnicodeIO.writeString(
               dest,
               "\" "
                  + String.valueOf(g.getX())
                  + " "
                  + String.valueOf(g.getY())
                  + " "
                  + String.valueOf(N)
                  + " ");
            for (int j= 0; j < N; j++) {
               GenericGraphBox tmp= g.getTransitions().get(j);
               UnicodeIO.writeString(
                  dest,
                  String.valueOf(boxes.indexOf(tmp)) + " ");
            }
            UnicodeIO.writeChar(dest, '\n');
         }
         dest.close();
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private void write_content(FileOutputStream f, String s) {
      int L= s.length();
      char c;
      for (int i= 0; i < L; i++) {
         c= s.charAt(i);
         if (c == '"') {
            // case of char "
            if (i == 0 || s.charAt(i - 1) != '\\') {
               // the " is the "abc" one; it must be saved as \"
               UnicodeIO.writeChar(f, '\\');
               UnicodeIO.writeChar(f, '"');
            } else {
               // it is the \" char that must be saved as \\\"
               // we only write 2 \ because the third has been saved at the pos i-1
               UnicodeIO.writeChar(f, '\\');
               UnicodeIO.writeChar(f, '\\');
               UnicodeIO.writeChar(f, '"');
            }
         } else {
            UnicodeIO.writeChar(f, c);
         }
      }
   }

   /**
    * This method loads a sentence graph. 
    * @param file sentence graph file
    * @return a <code>GraphIO</code> object describing the sentence graph
    */
   public static GraphIO loadSentenceGraph(File file,TfstGraphicalZone tfstGraphicalZone) {
      GraphIO res= new GraphIO();
      FileInputStream source;
      if (!file.exists()) {
         JOptionPane.showMessageDialog(
            null,
            "Cannot find " + file.getAbsolutePath(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return null;
      }
      if (!file.canRead()) {
         JOptionPane.showMessageDialog(
            null,
            "Cannot read " + file.getAbsolutePath(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return null;
      }
      if (file.length() <= 2) {
         JOptionPane.showMessageDialog(
            null,
            file.getAbsolutePath() + " is empty",
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return null;
      }
      try {
         source= UnicodeIO.openUnicodeLittleEndianFileInputStream(file);
         UnicodeIO.skipLine(source); // ignoring #...
         res.readSize(source);
         res.readInputFont(source);
         res.readOutputFont(source);
         res.readBackgroundColor(source);
         res.readForegroundColor(source);
         res.readSubgraphColor(source);
         res.readCommentColor(source);
         res.readSelectedColor(source);
         UnicodeIO.skipLine(source); // ignoring DBOXES
         res.readDrawFrame(source);
         res.readDate(source);
         res.readFile(source);
         res.readDirectory(source);
         res.readRightToLeft(source);
         UnicodeIO.skipLine(source); // ignoring DRST
         UnicodeIO.skipLine(source); // ignoring FITS
         UnicodeIO.skipLine(source); // ignoring PORIENT
         UnicodeIO.skipLine(source); // ignoring #
         res.readBoxNumber(source);
         res.boxes= new ArrayList<GenericGraphBox>();
         // adding initial state
         res.boxes.add(new TfstGraphBox(0, 0, 0, tfstGraphicalZone));
         // adding final state
         res.boxes.add(new TfstGraphBox(0, 0, 1, tfstGraphicalZone));
         // adding other states
         for (int i= 2; i < res.nBoxes; i++)
            res.boxes.add(new TfstGraphBox(0, 0, 2, tfstGraphicalZone));
         for (int i= 0; i < res.nBoxes; i++)
            res.readSentenceGraphLine(source, i);
         source.close();
      } catch (NotAUnicodeLittleEndianFileException e) {
         JOptionPane.showMessageDialog(
            null,
            file.getAbsolutePath() + " is not a Unicode Little-Endian graph",
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return null;

      } catch (FileNotFoundException e) {
         e.printStackTrace();
         return null;
      } catch (IOException e) {
         e.printStackTrace();
         return null;
      }
      return res;
   }

   private void readSentenceGraphLine(FileInputStream f, int n) {
       TfstGraphBox g= (TfstGraphBox)boxes.get(n);
      if (UnicodeIO.readChar(f) == 's') {
         // is a "s" was read, then we read the " char
         UnicodeIO.readChar(f);
      }
      String s= "";
      char c;
      while ((c= (char)UnicodeIO.readChar(f)) != '"') {
         if (c == '\\') {
            c= (char)UnicodeIO.readChar(f);
            if (c != '\\') {
               // case of \: \+ and \"
               if (c == '"')
                  s= s + c;
               else
                  s= s + "\\" + c;
            } else {
               // case of \\\" that must must be transformed into \"
               c= (char)UnicodeIO.readChar(f);
               if (c == '\\') {
                  // we are in the case \\\" -> \"
                  c= (char)UnicodeIO.readChar(f);
                  s= s + "\\" + c;
               } else {
                  // we are in the case \\a -> \\a
                  s= s + "\\\\" + c;
               }
            }
         } else
            s= s + c;
      }
      // skipping the space after "
      UnicodeIO.readChar(f);
      // reading the X coordinate
      int x= 0;
      while (UnicodeIO.isDigit((c= (char)UnicodeIO.readChar(f))))
         x= x * 10 + (c - '0');
      // reading the Y coordinate
      int y= 0;
      while (UnicodeIO.isDigit((c= (char)UnicodeIO.readChar(f))))
         y= y * 10 + (c - '0');
      Preferences globPref=Preferences.getCloneOfPreferences();
      if (globPref.rightToLeft==true || pref.rightToLeft==true) {
    	  pref.rightToLeft=true;
          g.setX(width-x);
      } else {
          g.setX(x);
      }
      g.setY(y);
      g.setX1(g.getX());
      g.setY1(g.getY());
      g.setX_in(g.getX());
      g.setY_in(g.getY());
      g.setX_out(g.getX() + g.getWidth() + 5);
      g.setY_out(g.getY_in());
      if (n != 1) {
         // 1 is the final state, which content is <E>
         g.setContentWithBounds(s);
         // we will need to call g.update() to size the box according to the text
      } else {
         g.setContentWithBounds("<E>");
         g.setX_in(g.getX());
         g.setY_in(g.getY());
         g.setX1(g.getX());
         g.setY1(g.getY() - 10);
         g.setY_out(g.getY_in());
         g.setX_out(g.getX_in() + 25);
      }
      int trans= 0;
      while (UnicodeIO.isDigit((c= (char)UnicodeIO.readChar(f))))
         trans= trans * 10 + (c - '0');
      for (int j= 0; j < trans; j++) {
         int dest= 0;
         while (UnicodeIO.isDigit((c= (char)UnicodeIO.readChar(f))))
            dest= dest * 10 + (c - '0');
         g.addTransitionTo(boxes.get(dest));
      }
      // skipping the end-of-line
      UnicodeIO.readChar(f);
   }

   /**
    * Saves the sentence graph described by the fields of this <code>GraphIO</code> object. 
    * @param file sentence graph file
    */
   public void saveSentenceGraph(File file) {
      FileOutputStream dest;
      try {
         if (!file.exists())
            file.createNewFile();
      } catch (IOException e) {
         JOptionPane.showMessageDialog(
            null,
            "Cannot write " + file.getAbsolutePath(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return;
      }
      if (!file.canWrite()) {
         JOptionPane.showMessageDialog(
            null,
            "Cannot write " + file.getAbsolutePath(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return;
      }
      try {
         dest= new FileOutputStream(file);
         UnicodeIO.writeChar(dest, (char)0xFEFF);
         UnicodeIO.writeString(dest, "#Unigraph\n");
         UnicodeIO.writeString(
            dest,
            "SIZE "
               + String.valueOf(width)
               + " "
               + String.valueOf(height)
               + "\n");
         UnicodeIO.writeString(dest, "FONT " + pref.input.getName() + ":");
         if (pref.input.getStyle() == Font.PLAIN)
            UnicodeIO.writeString(dest, "  ");
         else
            if (pref.input.getStyle() == Font.BOLD)
               UnicodeIO.writeString(dest, "B ");
            else
               if (pref.input.getStyle() == Font.ITALIC)
                  UnicodeIO.writeString(dest, " I");
               else
                  UnicodeIO.writeString(dest, "BI");
         UnicodeIO.writeString(dest, String.valueOf(pref.inputSize) + "\n");
         UnicodeIO.writeString(dest, "OFONT " + pref.output.getName() + ":");
         if (pref.output.getStyle() == Font.PLAIN)
            UnicodeIO.writeString(dest, "  ");
         else
            if (pref.output.getStyle() == Font.BOLD)
               UnicodeIO.writeString(dest, "B ");
            else
               if (pref.output.getStyle() == Font.ITALIC)
                  UnicodeIO.writeString(dest, " I");
               else
                  UnicodeIO.writeString(dest, "BI");
         UnicodeIO.writeString(dest, String.valueOf(pref.outputSize) + "\n");
         UnicodeIO.writeString(
            dest,
            "BCOLOR "
               + String.valueOf(16777216 + pref.backgroundColor.getRGB())
               + "\n");
         UnicodeIO.writeString(
            dest,
            "FCOLOR "
               + String.valueOf(16777216 + pref.foregroundColor.getRGB())
               + "\n");
         UnicodeIO.writeString(
            dest,
            "ACOLOR "
               + String.valueOf(16777216 + pref.subgraphColor.getRGB())
               + "\n");
         UnicodeIO.writeString(
            dest,
            "SCOLOR "
               + String.valueOf(16777216 + pref.commentColor.getRGB())
               + "\n");
         UnicodeIO.writeString(
            dest,
            "CCOLOR "
               + String.valueOf(16777216 + pref.selectedColor.getRGB())
               + "\n");
         UnicodeIO.writeString(dest, "DBOXES y\n");
         if (pref.frame)
            UnicodeIO.writeString(dest, "DFRAME y\n");
         else
            UnicodeIO.writeString(dest, "DFRAME n\n");
         if (pref.date)
            UnicodeIO.writeString(dest, "DDATE y\n");
         else
            UnicodeIO.writeString(dest, "DDATE n\n");
         if (pref.filename)
            UnicodeIO.writeString(dest, "DFILE y\n");
         else
            UnicodeIO.writeString(dest, "DFILE n\n");
         if (pref.pathname)
            UnicodeIO.writeString(dest, "DDIR y\n");
         else
            UnicodeIO.writeString(dest, "DDIR n\n");
         if (pref.rightToLeft)
            UnicodeIO.writeString(dest, "DRIG y\n");
         else
            UnicodeIO.writeString(dest, "DRIG n\n");
         UnicodeIO.writeString(dest, "DRST n\n");
         UnicodeIO.writeString(dest, "FITS 100\n");
         UnicodeIO.writeString(dest, "PORIENT L\n");
         UnicodeIO.writeString(dest, "#\n");
         nBoxes= boxes.size();
         UnicodeIO.writeString(dest, String.valueOf(nBoxes) + "\n");
         for (int i= 0; i < nBoxes; i++) {
             TfstGraphBox g= (TfstGraphBox)boxes.get(i);
            UnicodeIO.writeChar(dest, '"');
            int N=g.getTransitions().size();
            if (g.getType() != GenericGraphBox.FINAL) {
                String foo=g.getContent();
                if (i==2 && foo.equals("THIS SENTENCE AUTOMATON HAS BEEN EMPTIED")) {
                    foo="<E>";
                    N=0;
                }
                if (!foo.equals("<E>")) {
                    if (g.getBounds()!=null) {
                        foo=foo+"/"+g.getBounds();
                    } else {
                        /* Should not happen */
                        throw new AssertionError("Bounds should not be null for a box content != <E>");
                    }
                }
               write_content(dest,foo);
            }
            UnicodeIO.writeString(
               dest,
               "\" "
                  + String.valueOf(g.getX())
                  + " "
                  + String.valueOf(g.getY())
                  + " "
                  + String.valueOf(N)
                  + " ");
            for (int j= 0; j < N; j++) {
                TfstGraphBox tmp= (TfstGraphBox)g.getTransitions().get(j);
               UnicodeIO.writeString(
                  dest,
                  String.valueOf(boxes.indexOf(tmp)) + " ");
            }
            UnicodeIO.writeChar(dest, '\n');
         }
         dest.close();
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

}