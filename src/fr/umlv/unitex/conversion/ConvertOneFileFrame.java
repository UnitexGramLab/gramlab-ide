 /*
  * Unitex
  *
  * Copyright (C) 2001-2007 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

package fr.umlv.unitex.conversion;

import java.awt.*;
import java.io.*;

import javax.swing.*;

import fr.umlv.unitex.*;
import fr.umlv.unitex.exceptions.*;
import fr.umlv.unitex.process.*;

/**
 * @author Sébastien Paumier
 *
 */
public class ConvertOneFileFrame {

   private static boolean transcodeAll= false;
   private static boolean ignoreAll= false;
   private static JRadioButton replace= new JRadioButton("Replace");
   private static JRadioButton renameSource= new JRadioButton("Rename source with suffix '.old'");
   private static JPanel panel= null;
   private static String[] options=
      { "Transcode", "Transcode all", "Ignore", "Ignore all" };
   private static JLabel line1=new JLabel();   
   private static JLabel line2=new JLabel();   
   

   /**
    * Initializes the frame. 
    *
    */
   private static void init() {
      ButtonGroup bg= new ButtonGroup();
      bg.add(replace);
      bg.add(renameSource);
      replace.setSelected(true);
      panel= new JPanel(new GridLayout(5,1));
      panel.add(line1);
      panel.add(new JLabel("is not a Unicode Little-Endian one. Do you want"));
      panel.add(line2);
      panel.add(replace);
      panel.add(renameSource);
   }

   /**
    * Takes a <code>String</code> that designates a non Unicode Little-Endian
    * file, and shows a dialog box asking what must be done with this file.  
    * 
    * @return <code>null</code> if the file must be ignored; a <code>String</code> that
    * contains the command line for the <code>Convert</code> program otherwise.
    */
   public static ConvertCommand getCommandLineForConversion(File file) {
      if (panel == null) {
         init();
      }
      line1.setText(file.getAbsolutePath());
      if (ignoreAll == true) {
         return null;
      }
      ConvertCommand cmd= new ConvertCommand();
	try {
		cmd=cmd.src(ConversionFrame.getEncodingForLanguage(Config.getCurrentLanguage()))
		        .dest("LITTLE-ENDIAN");
	} catch (InvalidDestinationEncodingException e) {
		e.printStackTrace();
	} catch (InvalidSourceEncodingException e) {
		e.printStackTrace();
	}
	if (transcodeAll == true) {
         if (replace.isSelected()) {
            cmd=cmd.replace();
         } else {
            cmd=cmd.renameSourceWithSuffix(".old");
         }
         cmd=cmd.file(file);
         return cmd;
      }
      int res=
      JOptionPane.showOptionDialog(
            null,
            panel,
            "",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            null);
            
      if (res==2 || res==JOptionPane.CLOSED_OPTION) {
         return null;
      }
      if (res==3) {
         ignoreAll=true;
         return null;
      }
      // here, we must transcode
      if (replace.isSelected()) {
        cmd=cmd.replace();
      } else {
         cmd=cmd.renameSourceWithSuffix(".old");
      }
      cmd.file(file);
      if (res==1) {
         transcodeAll=true;
      }
      return cmd;
   }


   public static void reset() {
      transcodeAll= false;
      ignoreAll= false;
      line2.setText("to transcode it from "+ConversionFrame.getEncodingForLanguage(Config.getCurrentLanguage())+
      " to Unicode Little-Endian ?");
   }

}
