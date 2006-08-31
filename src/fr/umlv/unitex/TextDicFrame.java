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

import java.awt.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

import fr.umlv.unitex.io.*;

/**
 * This class describes a frame used to display current corpus's DLF, DLC and ERR files.
 * @author Sébastien Paumier
 *
 */
public class TextDicFrame extends JInternalFrame {
 
   JPanel panel= new JPanel();
   JPanel dicPanel= new JPanel();
   JPanel errPanel= new JPanel();
   MyTextArea dlf= new MyTextArea();
   JScrollPane dlfScroll;
   MyTextArea dlc= new MyTextArea();
   JScrollPane dlcScroll;
   MyTextArea err= new MyTextArea();
   JScrollPane errScroll;
   JLabel dlfLabel= new JLabel("");
   JLabel dlcLabel= new JLabel("");
   JLabel errLabel= new JLabel("");
   JPanel morphPanel = new JPanel();
   MyTextArea morphDic = new MyTextArea();
   JScrollPane morphScroll;

   static TextDicFrame frame;
   
   static boolean DLF_TOO_LARGE= false;
   static boolean DLC_TOO_LARGE= false;
   static boolean ERR_TOO_LARGE= false;


   private TextDicFrame() {
      super("", true, true, true, true);
      constructPanel();
      setContentPane(panel);
      pack();
      setBounds(250, 300, 500, 500);
      setVisible(false);
      setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
      addInternalFrameListener(new InternalFrameAdapter() {
        public void internalFrameClosing(InternalFrameEvent e) {
          try {
             setIcon(true);
          } catch (java.beans.PropertyVetoException e2) {
        	  e2.printStackTrace();
          }
       }
        });
   }

   private void constructPanel() {
      panel.setOpaque(true);
      panel.setLayout(new GridLayout(1, 2));
      constructDicPanel();
      constructErrPanel();
      panel.add(dicPanel);
      panel.add(errPanel);
   }

   private void constructDicPanel() {
      dicPanel.setLayout(new GridLayout(2, 1));
      dlfScroll= new JScrollPane(dlf);
      dlfScroll.setHorizontalScrollBarPolicy(
         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      dlcScroll= new JScrollPane(dlc);
      dlcScroll.setHorizontalScrollBarPolicy(
         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      JPanel up= new JPanel();
      up.setBorder(new EmptyBorder(5, 5, 5, 5));
      up.setLayout(new BorderLayout());
      up.add(dlfLabel, BorderLayout.NORTH);
      JPanel tmp= new JPanel();
      tmp.setOpaque(true);
      tmp.setLayout(new BorderLayout());
      tmp.setBorder(BorderFactory.createLoweredBevelBorder());
      tmp.add(dlfScroll, BorderLayout.CENTER);
      up.add(tmp, BorderLayout.CENTER);
      JPanel down= new JPanel();
      down.setBorder(new EmptyBorder(5, 5, 5, 5));
      down.setLayout(new BorderLayout());
      down.add(dlcLabel, BorderLayout.NORTH);
      JPanel tmp2= new JPanel();
      tmp2.setOpaque(true);
      tmp2.setLayout(new BorderLayout());
      tmp2.setBorder(BorderFactory.createLoweredBevelBorder());
      tmp2.add(dlcScroll, BorderLayout.CENTER);
      down.add(tmp2, BorderLayout.CENTER);
      dicPanel.add(up);
      dicPanel.add(down);
   }

   private void constructErrPanel() {
      errPanel.setLayout(new BorderLayout());
      errPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
      errScroll= new JScrollPane(err);
      errScroll.setHorizontalScrollBarPolicy(
         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      errPanel.add(errLabel, BorderLayout.NORTH);
      JPanel tmp= new JPanel();
      tmp.setOpaque(true);
      tmp.setLayout(new BorderLayout());
      tmp.setBorder(BorderFactory.createLoweredBevelBorder());
      tmp.add(errScroll, BorderLayout.CENTER);
      errPanel.add(tmp, BorderLayout.CENTER);
   }

   /**
    * Initializes the frame 
    *
    */
   private static void init() {
      frame= new TextDicFrame();
      UnitexFrame.addInternalFrame(frame);
   }

   /**
    * Loads "dlf", "dlc" and "err" files contained in a directory, and shows the frame
    * @param dir directory to look in
    */
   public static void loadTextDic(File dir,boolean iconify) {
      if (frame==null) {
         init();
      }
      frame.dlf.killTimer();
      frame.dlc.killTimer();
      frame.err.killTimer();
      int numberOfErrors= 0;
      try {
      	 File FILE = new File(dir,(Config.isAgglutinativeLanguage())? "mdlf":"dlf");
         frame.dlf.setFont(Config.getCurrentTextFont());
         frame.dlf.setLineWrap(false);
         frame.dlf.setEditable(false);
         String n= UnicodeIO.readFirstLine(new File(dir,(Config.isAgglutinativeLanguage())? "mdlf.n":"dlf.n"));
         String message= "DLF";
         if (n != null) {
            message= message + ": " + n + " simple-word lexical entr";
            if (new Integer(n).intValue() <= 1)
               message= message + "y";
            else
               message= message + "ies";
         }
         if (!FILE.exists() || FILE.length() <= 2) {
            numberOfErrors++;
            DLF_TOO_LARGE= true;
            frame.dlf.setDocument(new PlainDocument());
            frame.dlf.setText(Config.EMPTY_FILE_MESSAGE);
            frame.dlfLabel.setText("DLF: simple-word lexical entries");
         } else
            if (FILE.length() < Preferences.pref.MAX_TEXT_FILE_SIZE) {
               try {
                  frame.dlf.load(FILE);
               } catch (IOException E) {
                  frame.dlf.setDocument(new PlainDocument());
                  frame.dlf.setText(Config.ERROR_WHILE_READING_FILE_MESSAGE);
                  DLF_TOO_LARGE= true;
               }
               DLF_TOO_LARGE= false;
               frame.dlfLabel.setText(message);
            } else {
               frame.dlf.setDocument(new PlainDocument());
               frame.dlf.setText(Config.FILE_TOO_LARGE_MESSAGE);
               DLF_TOO_LARGE= true;
               frame.dlfLabel.setText(message);
            }
      } catch (Exception e) {
         DLF_TOO_LARGE= true;
      }
      try {
      	File FILE= new File(dir,
      			(Config.isAgglutinativeLanguage())? "mdlc":"dlc");
         frame.dlc.setFont(Config.getCurrentTextFont());
         frame.dlc.setWrapStyleWord(true);
         frame.dlc.setLineWrap(false);
         frame.dlc.setEditable(false);
         String n= UnicodeIO.readFirstLine(new File(dir,
         		(Config.isAgglutinativeLanguage())? "mdlc.n":"dlc.n"));
         String message= "DLC";
         if (n != null) {
            message= message + ": " + n + " compound lexical entr";
            if (new Integer(n).intValue() <= 1)
               message= message + "y";
            else
               message= message + "ies";
         }
         if (!FILE.exists() || FILE.length() <= 2) {
            numberOfErrors++;
            DLC_TOO_LARGE= true;
            frame.dlc.setDocument(new PlainDocument());
            frame.dlc.setText(Config.EMPTY_FILE_MESSAGE);
            frame.dlcLabel.setText("DLC: compound lexical entries");
         } else
            if (FILE.length() < Preferences.pref.MAX_TEXT_FILE_SIZE) {
               try {
                  frame.dlc.load(FILE);
               } catch (IOException E) {
                  frame.dlc.setDocument(new PlainDocument());
                  frame.dlc.setText(Config.ERROR_WHILE_READING_FILE_MESSAGE);
                  DLC_TOO_LARGE= true;
               }
               DLC_TOO_LARGE= false;
               frame.dlcLabel.setText(message);
            } else {
               frame.dlc.setDocument(new PlainDocument());
               frame.dlc.setText(Config.FILE_TOO_LARGE_MESSAGE);
               DLC_TOO_LARGE= true;
               frame.dlcLabel.setText(message);
            }
      } catch (Exception e) {
         DLC_TOO_LARGE= true;
      }
      try {
         File FILE= new File(dir,"err");
         frame.err.setFont(Config.getCurrentTextFont());
         frame.err.setWrapStyleWord(true);
         frame.err.setLineWrap(false);
         frame.err.setEditable(false);
         String n= UnicodeIO.readFirstLine(new File(dir,"err.n"));
         String message= "ERR";
         if (n != null) {
            message= message + ": " + n + " unknown simple word";
            if (new Integer(n).intValue() > 1)
               message= message + "s";
         }
         if (!FILE.exists() || FILE.length() <= 2) {
            numberOfErrors++;
            ERR_TOO_LARGE= true;
            frame.err.setDocument(new PlainDocument());
            frame.err.setText(Config.EMPTY_FILE_MESSAGE);
            frame.errLabel.setText("ERR: unknown simple words");
         } else
            if (FILE.length() < Preferences.pref.MAX_TEXT_FILE_SIZE) {
               try {
                  frame.err.load(FILE);
               } catch (IOException E) {
                  frame.err.setDocument(new PlainDocument());
                  frame.err.setText(Config.ERROR_WHILE_READING_FILE_MESSAGE);
                  ERR_TOO_LARGE= true;
               }
               ERR_TOO_LARGE= false;
               frame.errLabel.setText(message);
            } else {
               frame.err.setDocument(new PlainDocument());
               frame.err.setText(Config.FILE_TOO_LARGE_MESSAGE);
               ERR_TOO_LARGE= true;
               frame.errLabel.setText(message);
            }
      } catch (Exception e) {
         ERR_TOO_LARGE= true;
      }
      /*if (numberOfErrors == 3)
         return;
      */
      frame.setTitle("Word Lists in " + dir);
      frame.setVisible(true);
      try {
         frame.setIcon(iconify);
         frame.setSelected(true);
      } catch (java.beans.PropertyVetoException e2) {
    	  e2.printStackTrace();
      }
   }

   /**
    * Hides the frame 
    *
    */
   public static void hideFrame() {
      if (frame==null) {
         return;
      }
      frame.dlf.killTimer();
      frame.dlc.killTimer();
      frame.err.killTimer();
      frame.setVisible(false);
      try {
         frame.setIcon(false);
      } catch (java.beans.PropertyVetoException e2) {
    	  e2.printStackTrace();
      }
      frame.dlf.setDocument(new PlainDocument());
      frame.dlc.setDocument(new PlainDocument());
      frame.err.setDocument(new PlainDocument());
      System.gc();
   }

}
