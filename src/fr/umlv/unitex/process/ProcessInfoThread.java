 /*
  * Unitex
  *
  * Copyright (C) 2001-2007 Université de Marne-la-Vallée <unitex@univ-mlv.fr>
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

package fr.umlv.unitex.process;

import java.io.*;

import javax.swing.*;

/**
 * This class is used to monitor stdout and stderr messages of external processes.
 * @author Sébastien Paumier
 *
 */
public class ProcessInfoThread extends Thread {

   JTextArea txt;
   BufferedReader stream;
   boolean close_on_finish;
   ProcessInfoFrame parent_frame;
   JScrollBar scroll_bar;
   JScrollPane scrollPane;

   /**
    * Creates a new <code>ProcessInfoThread</code> 
    * @param t the text area to display messages
    * @param s the stream to monitor
    * @param close indicate if the parent frame must be closed after the completion of the process
    * @param f parent frame
    * @param scroll scroll bar of the text area
    * @param scr scroll pane that contains the text area
    */
   public ProcessInfoThread(
      JTextArea t,
      InputStream s,
      boolean close,
      ProcessInfoFrame f,
      JScrollBar scroll,
      JScrollPane scr) {
      super();
      txt= t;
      close_on_finish= close;
      parent_frame= f;
      scroll_bar= scroll;
      scrollPane= scr;
      stream= new BufferedReader(new InputStreamReader(s));
   }

   /**
    * Runs the monitoring thread 
    */
   public void run() {
      String s;
      try {
         while ((s= stream.readLine()) != null) {
            if (!s.equals("")) {
               final String s2= s;
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     txt.append(s2 + "\n");
                     scroll_bar.setValue(scroll_bar.getMaximum());
                     scrollPane.repaint();
                  }
               });
            }
         }
      } catch (IOException e) {
    	  e.printStackTrace();
      }
      if (close_on_finish) {
         parent_frame.setVisible(false);
         parent_frame= null;
      } else {
         scroll_bar.setValue(scroll_bar.getMaximum());
      }
   }

}