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
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
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
   BigTextList dlf= new BigTextList(true);
   JScrollPane dlfScroll;
   BigTextList dlc= new BigTextList(true);
   JScrollPane dlcScroll;
   BigTextList err= new BigTextList();
   JScrollPane errScroll;
   JLabel dlfLabel= new JLabel("");
   JLabel dlcLabel= new JLabel("");
   JLabel errLabel= new JLabel("");

   static TextDicFrame frame;
   
   
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
      GlobalPreferenceFrame.addTextFontListener(new FontListener() {

		public void fontChanged(Font font) {
			dlf.setFont(font);
			dlc.setFont(font);
			err.setFont(font);
		}});
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
      /********* Loading DLF file *********/
      File FILE = new File(dir,(Config.isAgglutinativeLanguage())? "mdlf":"dlf");
      frame.dlf.setFont(Config.getCurrentTextFont());
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
         frame.dlf.setText(Config.EMPTY_FILE_MESSAGE);
         frame.dlfLabel.setText("DLF: simple-word lexical entries");
      } else  {
    	  frame.dlf.load(FILE);
          frame.dlfLabel.setText(message);
      }
      /********* Loading DLC file *********/
      FILE=new File(dir,(Config.isAgglutinativeLanguage())? "mdlc":"dlc");
      frame.dlc.setFont(Config.getCurrentTextFont());
      n= UnicodeIO.readFirstLine(new File(dir,
         		(Config.isAgglutinativeLanguage())? "mdlc.n":"dlc.n"));
      message= "DLC";
      if (n != null) {
         message= message + ": " + n + " compound lexical entr";
         if (new Integer(n).intValue() <= 1)
            message= message + "y";
         else
            message= message + "ies";
      }
      if (!FILE.exists() || FILE.length() <= 2) {
         frame.dlc.setText(Config.EMPTY_FILE_MESSAGE);
         frame.dlcLabel.setText("DLC: compound lexical entries");
      } else {
    	  frame.dlc.load(FILE);
          frame.dlcLabel.setText(message);
      }
      /********* Loading ERR file *********/
      FILE=new File(dir,"err");
      frame.err.setFont(Config.getCurrentTextFont());
      n= UnicodeIO.readFirstLine(new File(dir,"err.n"));
      message= "ERR";
      if (n != null) {
         message= message + ": " + n + " unknown simple word";
         if (new Integer(n).intValue() > 1)
            message= message + "s";
      }
      if (!FILE.exists() || FILE.length() <= 2) {
         frame.err.setText(Config.EMPTY_FILE_MESSAGE);
         frame.errLabel.setText("ERR: unknown simple words");
      } else {
    	  frame.err.load(FILE);
          frame.errLabel.setText(message);
      }
      
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
      frame.dlf.reset();
      frame.dlc.reset();
      frame.err.reset();
      frame.setVisible(false);
      try {
         frame.setIcon(false);
      } catch (java.beans.PropertyVetoException e2) {
    	  e2.printStackTrace();
      }
      System.gc();
   }

}
