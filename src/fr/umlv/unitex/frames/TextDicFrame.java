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

package fr.umlv.unitex.frames;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import fr.umlv.unitex.BigTextList;
import fr.umlv.unitex.Config;
import fr.umlv.unitex.FontListener;
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


   TextDicFrame() {
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
      UnitexFrame.getFrameManager().getGlobalPreferencesFrame().addTextFontListener(new FontListener() {

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
    * Loads "dlf", "dlc" and "err" files contained in a directory, and shows the frame
    * @param dir directory to look in
    */
   void loadTextDic(File dir) {
      /********* Loading DLF file *********/
      File FILE = new File(dir,"dlf");
      dlf.setFont(Config.getCurrentTextFont());
      String n= UnicodeIO.readFirstLine(new File(dir,"dlf.n"));
      String message= "DLF";
      if (n != null) {
         message= message + ": " + n + " simple-word lexical entr";
         if (new Integer(n).intValue() <= 1)
            message= message + "y";
         else
            message= message + "ies";
      }
      if (!FILE.exists() || FILE.length() <= 2) {
         dlf.setText(Config.EMPTY_FILE_MESSAGE);
         dlfLabel.setText("DLF: simple-word lexical entries");
      } else  {
    	  dlf.load(FILE);
          dlfLabel.setText(message);
      }
      /********* Loading DLC file *********/
      FILE=new File(dir,"dlc");
      dlc.setFont(Config.getCurrentTextFont());
      n= UnicodeIO.readFirstLine(new File(dir,"dlc.n"));
      message= "DLC";
      if (n != null) {
         message= message + ": " + n + " compound lexical entr";
         if (new Integer(n).intValue() <= 1)
            message= message + "y";
         else
            message= message + "ies";
      }
      if (!FILE.exists() || FILE.length() <= 2) {
         dlc.setText(Config.EMPTY_FILE_MESSAGE);
         dlcLabel.setText("DLC: compound lexical entries");
      } else {
    	  dlc.load(FILE);
          dlcLabel.setText(message);
      }
      /********* Loading ERR file *********/
      FILE=new File(dir,"err");
      err.setFont(Config.getCurrentTextFont());
      n= UnicodeIO.readFirstLine(new File(dir,"err.n"));
      message= "ERR";
      if (n != null) {
         message= message + ": " + n + " unknown simple word";
         if (new Integer(n).intValue() > 1)
            message= message + "s";
      }
      if (!FILE.exists() || FILE.length() <= 2) {
         err.setText(Config.EMPTY_FILE_MESSAGE);
         errLabel.setText("ERR: unknown simple words");
      } else {
    	  err.load(FILE);
          errLabel.setText(message);
      }
      setTitle("Word Lists in " + dir);
   }

   /**
    * Hides the frame 
    *
    */
   void hideFrame() {
      dlf.reset();
      dlc.reset();
      err.reset();
      setVisible(false);
      System.gc();
   }

}
