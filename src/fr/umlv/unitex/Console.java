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

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * This class describes a frame that shows all the command lines that have been launched. 
 * @author Sébastien Paumier
 *
 */
public class Console extends JInternalFrame {
   
   static Console console;
   private JTextArea text= new JTextArea();
   
   private Console() {
      super("Console", true, true);
      text= new JTextArea();
      JScrollPane scroll= new JScrollPane(text);
      JPanel middle= new JPanel();
      middle.setOpaque(true);
      middle.setLayout(new BorderLayout());
      middle.setBorder(BorderFactory.createLoweredBevelBorder());
      middle.add(scroll, BorderLayout.CENTER);
      JPanel top= new JPanel();
      top.setOpaque(true);
      top.setLayout(new BorderLayout());
      top.setBorder(new EmptyBorder(2, 2, 2, 2));
      top.add(middle, BorderLayout.CENTER);
      setContentPane(top);
      pack();
      setBounds(100, 100, 600, 400);
      setVisible(false);
      addInternalFrameListener(new InternalFrameAdapter() {
        public void internalFrameClosing(InternalFrameEvent e) {
          console.setVisible(false);
       }
        });
      setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
   }

   /**
    * Adds a <code>String</code> to the the command lines 
    * @param command the command line to be added
    */
   public static void addCommand(String command) {
      if (console==null) {
         init();
      }
      console.text.append(command + "\n");
   }

   /**
    * Initializes the console frame 
    *
    */
   private static void init() {
      console= new Console();
      UnitexFrame.addInternalFrame(console,false);
   }
   
   
   private static void showFrame() {
      if (console==null) {
         init();
      }
      console.setVisible(true);
   }


   public static void changeFrameVisibility() {
      if (console==null) {
         showFrame();
      }
      else console.setVisible(!console.isVisible());
   }
   
}
