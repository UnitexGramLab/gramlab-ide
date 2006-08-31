 /*
  * Unitex
  *
  * Copyright (C) 2001-2006 Université de Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.awt.ComponentOrientation;
import java.awt.event.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

import javax.swing.*;
import javax.swing.text.*;

/**
 * This class describes a <code>JTextArea</code> that can efficiently load Unicode text files.
 * @author Sébastien Paumier
 *
 */
public class MyTextArea extends JTextArea {

   Timer currentTimer= null;

   /**
    * Creates a new <code>MyTextArea</code> 
    *
    */
   public MyTextArea() {
    super();
    setLineWrap(true);
    setWrapStyleWord(true);
 }

  	/**
    * Loads a text file with a left to right presentation
    * @param file the text file
    * @throws IOException
    * @throws IllegalArgumentException
    */
   public void load(File file) throws IOException, IllegalArgumentException {
   	load(file,ComponentOrientation.LEFT_TO_RIGHT);
   }
   	/**
    * Loads a text file 
    * @param file the text file
    * @param orientation orientation of the text file
    * @throws IOException
    * @throws IllegalArgumentException
    */
   public void load(File file,ComponentOrientation orientation) throws IOException, IllegalArgumentException {
      try {
         FileInputStream stream= new FileInputStream(file);
         load(stream,orientation);
      } catch (FileNotFoundException e) {
         System.out.println("Cannot open file " + file.getPath());
         return;
      }
   }

   /**
    * @param stream text file stream to be loaded, with a left to right
    * orientation
    * @throws IllegalArgumentException
    * @throws IOException
    */
   public void load(final FileInputStream stream) throws IllegalArgumentException, IOException {
       load(stream,ComponentOrientation.LEFT_TO_RIGHT);
   }
   /**
    * Loads a text file 
    * @param stream the text file stream
    * @param orientation orientation of the text file 
    * @throws IOException
    * @throws IllegalArgumentException
    */
   public void load(final FileInputStream stream,ComponentOrientation orientation)
      throws IOException, IllegalArgumentException {
      final StringContent content= new StringContent();
      final FileChannel channel= stream.getChannel();
      final PlainDocument document= new PlainDocument(content);
      setDocument(document);
      setComponentOrientation(orientation);
      final Timer timer= new Timer(300, null);
      currentTimer= timer;
      final ByteBuffer buffer= ByteBuffer.allocateDirect(16000);
      buffer.order(ByteOrder.LITTLE_ENDIAN);
      final CharBuffer charBuffer= buffer.asCharBuffer();
      // remove unicode header
      buffer.limit(1);
      channel.read(buffer);
      buffer.position(0);
      channel.read(buffer);
      buffer.clear();
      // asynchronous update
      timer.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ev) {
            try {
               if (channel.read(buffer) > 0) {
                  int bufferPosition= buffer.position();
                  if ((bufferPosition & 1) == 1)
                     throw new InternalError("bad alignement: the text file is corrupted");

                  String s= charBuffer.limit(bufferPosition >> 1).toString();
                  int caretPosition= getCaretPosition();
                  try {
                     document.insertString(content.length() - 1, s, null);
                  } catch (BadLocationException e) {
                     throw new Error(e);
                  }
                  setCaretPosition(caretPosition);
                  buffer.clear();
               } else {
                  stream.close();
                  timer.stop();
                  currentTimer= null;
               }
            } catch (IOException e) {
               e.printStackTrace();
               try {
                  stream.close();
               } catch (IOException e1) {
                  e1.printStackTrace();
               }
               timer.stop();
               currentTimer= null;
            }
         }
      });
      timer.setCoalesce(true);
      timer.setRepeats(true);
      timer.start();
   }

   /**
    * Interrupts text loading 
    *
    */
   public void killTimer() {
      if (currentTimer != null) {
         currentTimer.stop();
         currentTimer= null;
      }
   }

}
