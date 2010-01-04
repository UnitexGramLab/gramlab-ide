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

package fr.umlv.unitex;

import java.awt.*;

import javax.swing.*;

/**
 * This class describes a splash screen window.
 * @author Sébastien Paumier
 *
 */
public class SplashScreen extends JWindow {
   
   /**
    * Creates and shows a new <code>SplashScreen</code>
    * @param icon the image to show
    * @param milliseconds the splashscreen lifetime in milliseconds
    */
   public SplashScreen(ImageIcon icon, int milliseconds) {
      super();
      JLabel splash= new JLabel(icon);
      getContentPane().add(splash);
      pack();
      Rectangle screenRect= getGraphicsConfiguration().getBounds();
      setLocation(
         screenRect.x + screenRect.width / 2 - getSize().width / 2,
         screenRect.y + screenRect.height / 2 - getSize().height / 2);
      setVisible(true);
      try {
         Thread.sleep(milliseconds);
      } catch (InterruptedException e) {
    	  e.printStackTrace();
      }
      dispose();
      /*
       // JDK1.5 use setAlwaysOnTop()
      Timer timer=new Timer(milliseconds,new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          dispose();
        }
      });
      timer.setRepeats(false);
      timer.start();*/
   }
}