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

package fr.umlv.unitex.editor;

import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

import javax.swing.*;
import javax.swing.text.*;

import fr.umlv.unitex.*;
import fr.umlv.unitex.editor.ui.*;

/**
 * This class provides methods for loading corpora 
 */
public class FileManager {

   Timer currentTimer= null;
   FileEditionTextFrame fileEditionTextFrame;
   boolean FILE_TOO_LARGE= false;

   /**
    * loads file and checks encoding type
    * @param file the text file
    **/
   public void loadFile(File file) {
	   if (file==null) {
		 System.err.println("Internal error in FileManager.loadFile");
		 return;
	   }
      if (!file.exists()) {
         JOptionPane.showMessageDialog(
            null,
            "Cannot find " + file.getAbsolutePath(),
            "Error",
            ImageObserver.ERROR);
         return;
      }
      if (!file.canRead()) {
         JOptionPane.showMessageDialog(
            null,
            "Cannot read " + file.getAbsolutePath(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return;
      }
      if (file.length() <= 2) {
         JOptionPane.showMessageDialog(
            null,
            file.getAbsolutePath() + " is empty",
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return;
      }
         JOptionPane.showMessageDialog(null,"This is not necessarily the text being processed by Unitex","Warning",JOptionPane.WARNING_MESSAGE);
         load(file);
   }


   /**
    * loads a file from 
    * @param file file path 
    **/
   private void load(File file) {
      EditionTextArea text= new EditionTextArea();
      fileEditionTextFrame= new FileEditionTextFrame(text,file);
      if (file.length() <= 2) {
         FILE_TOO_LARGE= true;
         text.setDocument(new PlainDocument());
         text.setText(Config.EMPTY_FILE_MESSAGE);
      } else
         if (file.length() < Preferences.pref.MAX_TEXT_FILE_SIZE) {
            try {
               load(file, text);
            } catch (IOException E) {
               text.setDocument(new PlainDocument());
               text.setText(Config.ERROR_WHILE_READING_FILE_MESSAGE);
               FILE_TOO_LARGE= true;
            }
            FILE_TOO_LARGE= false;
         } else {
            FILE_TOO_LARGE= true;
            text.setDocument(new PlainDocument());
            text.setText(Config.FILE_TOO_LARGE_MESSAGE);
         }

      UnitexFrame.desktop.add(fileEditionTextFrame, UnitexFrame.DOCLAYER);

      try {
         fileEditionTextFrame.setSelected(true);
      } catch (java.beans.PropertyVetoException e) {
    	  e.printStackTrace();
      }

   }

   public void killTimer() {
      if (currentTimer != null) {
         currentTimer.stop();
         currentTimer= null;
      }
   }

   /**
    * saves a file
    * @param absolutePath the absolute path of the file
    */
   public void save(String absolutePath) {

      try {

         JDesktopPane jd= UnitexFrame.desktop;
         FileEditionTextFrame fetf= (FileEditionTextFrame)jd.getSelectedFrame();
         EditionTextArea t= fetf.getText();

         //save in unicode little indian UTF-16LE
         String s= "UTF-16LE";
         OutputStreamWriter osr=
            new OutputStreamWriter(
               new FileOutputStream(new File(absolutePath), false),s);
         String content=t.getText();
         int l=content.length();
         osr.write(0xfeff);
         for (int i=0;i<l;i++) {
            char c=content.charAt(i);
            if (c!='\n') {
            	osr.write(c);
            }
            else {
            	osr.write("\r\n");
            }
         }
         osr.flush();
         osr.close();

      } catch (IOException e) {
         JOptionPane.showMessageDialog(
            null,
            "Unable to save " + absolutePath,
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return;
      } catch (ClassCastException e) {
         JOptionPane.showMessageDialog(
            null,
            "please, select a text edition frame",
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return;
      } catch (NullPointerException e) {
         JOptionPane.showMessageDialog(
            null,
            "please, select a text edition frame",
            "Error",
            JOptionPane.ERROR_MESSAGE);
         return;
      }

   }


   /**
    * Load an little endian Unicode texte file put it in a document
    * @param file the text to load
    * @param textArea aera where put the text
    */
   public Document load(File file, final EditionTextArea textArea)
      throws IOException, IllegalArgumentException {

      final StringContent content= new StringContent();
      final FileChannel channel= new FileInputStream(file).getChannel();
      final PlainDocument document= new PlainDocument(content);
      final Timer timer= new Timer(300, null);
      currentTimer= timer;
      final ByteBuffer buffer= ByteBuffer.allocateDirect(16000);
      buffer.order(ByteOrder.LITTLE_ENDIAN);
      final CharBuffer charBuffer= buffer.asCharBuffer();

      textArea.setDocument(document);

      // remove unicode header
      // reading two firsts byte
      buffer.limit(1);
      channel.read(buffer);
      buffer.position(0);
      channel.read(buffer);
      // and clear it 
      buffer.clear();

      // asynchronous text update in the text aera 
      timer.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ev) {

            try {

               if (channel.read(buffer) > 0) {
                  int bufferPosition= buffer.position();
                  if ((bufferPosition & 1) == 1)
                     throw new InternalError("bad alignement: the text file is corrupted");

                  String s= charBuffer.limit(bufferPosition >> 1).toString();
                  int caretPosition= textArea.getCaretPosition();
                  try {
                     document.insertString(content.length() - 1, s, null);
                  } catch (BadLocationException e) {
                     throw new Error(e);
                  }
                  textArea.setCaretPosition(caretPosition);

                  buffer.clear();
               } else {
                  timer.stop();
                  currentTimer= null;
               }
            } catch (IOException e) {
               e.printStackTrace();
               timer.stop();
               currentTimer= null;
            }
         }
      });
      timer.setCoalesce(true);
      timer.setRepeats(true);
      timer.start();

      return document;
   }

   /**
    * load an empty text
    **/
   public void newFile() {
      fileEditionTextFrame= new FileEditionTextFrame();
      UnitexFrame.addInternalFrame(fileEditionTextFrame);

      try {
         fileEditionTextFrame.setSelected(true);
      } catch (java.beans.PropertyVetoException e) {
    	  e.printStackTrace();
      }

   }

   public void closeText() {
      fileEditionTextFrame.closeText();
   }


   /**
    * convert a file to an HTML file
    * @param fileDestinationpath the file destination path
    */
   public void convertToHtml(String fileDestinationpath) {
      String path= Util.getFilePathWithoutFileName(fileDestinationpath) + "\\";
      String fileName= Util.getFileNameWithoutFilePath(fileDestinationpath);

      try {

         String charset= "UTF-8";
         // Lecture du fichier
         File file= new File(fileDestinationpath);
         File HtmlFile= new File(path + fileName + ".html");

         InputStreamReader isr= new InputStreamReader(new FileInputStream(file));
         char[] tab= new char[(int)file.length()];
         isr.read(tab);
         isr.close();
         OutputStreamWriter osr=
            new OutputStreamWriter(new FileOutputStream(HtmlFile), charset);

         osr.write("<html><body>");

         // offset de 2 pour enlever l'entête unicode
         osr.write(tab, 2, tab.length - 2);

         osr.write("</html></body>");

         osr.flush();
         osr.close();
      } catch (IOException e) {
         JOptionPane.showMessageDialog(null, "Unable to save !!");
      }
   }

}
