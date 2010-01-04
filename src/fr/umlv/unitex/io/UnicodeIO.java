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

import java.io.*;

import fr.umlv.unitex.exceptions.*;

/**
 * This class provides methods for loading and saving UTF-16LE texts.
 * @author Sébastien Paumier
 *
 */
public class UnicodeIO {

   private static int toInt(byte b) {
      if (b >= 0)
         return b;
      return 256 + b;
   }

   /**
    * Reads a character from a file, considering <code>0x0D</code> and <code>0x0A</code>
    * as two distinct characters   
    * @param f the file input stream
    * @return a character 
    */
   public static int readCharRawly(FileInputStream f) {
      byte[] b= new byte[2];
      char c;
      try {
         if (f.read(b) == -1)
            return -1;
      } catch (IOException e) {
         e.printStackTrace();
      }
      c= (char) (toInt(b[1]) * 256 + toInt(b[0]));
      return c;
   }

   /**
    * Reads a character from a file. If the character is <code>0x0D</code>, then the following
    * <code>0x0A</code> character is skipped.
    * @param f the file input stream
    * @return a character 
    */
   public static int readChar(FileInputStream f) {
      int c= readCharRawly(f);
      if (c == 0x0d) {
         readCharRawly(f);
         c= '\n';
      }
      return c;
   }



   /**
    * Reads a line from a file 
    * @param f the file input stream
    * @return a string representing the line, without the carridge return character
    */
   public static String readLine(FileInputStream f) {
      int c;
      String s= "";
      while ((c= readChar(f)) != '\n' && c != -1) {
         s= s + (char)c;
      }
      return s;
   }

   /**
    * Reads the first line of a file 
    * @param f the file 
    * @return a string representing the first line without the carridge return character
    */
   public static String readFirstLine(File f) {
      if (!f.exists()) {
         return null;
      }
      if (!f.canRead()) {
         return null;
      }
      String res;
      FileInputStream source;
      try {
         source= UnicodeIO.openUnicodeLittleEndianFileInputStream(f);
         res= UnicodeIO.readLine(source);
         source.close();
      } catch (NotAUnicodeLittleEndianFileException e) {
         return null;
      } catch (FileNotFoundException e) {
         return null;
      } catch (IOException e) {
         return null;
      }
      return res;
   }

   /**
    * Skips characters from a file 
    * @param f the file input stream
    * @param n number of characters to skip
    */
   public static void skipChars(FileInputStream f, int n) {
      for (int i= 0; i < n; i++)
         readChar(f);
   }

   /**
    * Skips a line in a file
    * @param f the file input stream
    */
   public static void skipLine(FileInputStream f) {
      while (readChar(f) != '\n') {
        // do nothing
      }
   }

   /**
    * Tests if a character is a digit
    * @param c the character to test
    * @return <code>true</code> if <code>c</code> is a digit, <code>false</code> otherwise
    */
   public static boolean isDigit(char c) {
      return (c >= '0' && c <= '9');
   }

   /**
    * Writes a character to a file 
    * @param f the file output stream
    * @param c the character to write
    */
   public static void writeChar(FileOutputStream f, char c) {
      byte[] b= new byte[2];
      b[1]= (byte) (c / 256);
      b[0]= (byte) (c % 256);
      if (c == '\n') {
         writeChar(f, (char)0x0d);
      }
      try {
         f.write(b[0]);
         f.write(b[1]);
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   /**
    * Writes a string to a file
    * @param f the file output stream
    * @param s the string to write
    */
   public static void writeString(FileOutputStream f, String s) {
      int L= s.length();
      for (int i= 0; i < L; i++)
         writeChar(f, s.charAt(i));
   }

   /**
    * @param f a file 
    * @return true if f is a Unicode Little-Endian file
    * @throws FileNotFoundException if the file cannot be opened
    */
   public static boolean isAUnicodeLittleEndianFile(File f) 
      throws FileNotFoundException {
      FileInputStream stream= new FileInputStream(f);
      boolean res=(UnicodeIO.readChar(stream) == 0xFEFF);
      try {
         stream.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return res;      
   }


   /**
    * Opens and returns a file input stream. 
    * @param f the file 
    * @return the input stream
    * @throws NotAUnicodeLittleEndianFileException if the file is not a Unicode Little Endian one
    * @throws FileNotFoundException if the file cannot be opened
    */
   public static FileInputStream openUnicodeLittleEndianFileInputStream(File f)
      throws NotAUnicodeLittleEndianFileException, FileNotFoundException {
      FileInputStream stream= new FileInputStream(f);
      if (UnicodeIO.readChar(stream) != 0xFEFF) {
         try {
            stream.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
         throw new NotAUnicodeLittleEndianFileException(f);
      }
      return stream;
   }


   /**
    * Opens and returns a file output stream. 
    * @param f the file 
    * @return the output stream
    * @throws FileNotFoundException if the file cannot be opened
    */
   public static FileOutputStream openUnicodeLittleEndianFileOutputStream(File f)
      throws FileNotFoundException {
      FileOutputStream stream= new FileOutputStream(f);
      UnicodeIO.writeChar(stream,(char)0xFEFF);
      return stream;
   }
}