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

package fr.umlv.unitex;

import java.io.*;

/**
 * 
 * @author Olivier Blanc
 *
 */
public class EatStreamThread extends Thread {

   InputStream in;
   OutputStream out;

   EatStreamThread(InputStream _in) {
      in= _in;
      out= new NullOutputStream();
   }

   EatStreamThread(InputStream _in, OutputStream _out) {
      in= _in;
      out= _out;
   }

   public void run() {

      try {

         int c;
         while ((c= in.read()) != -1) {
            out.write(c);
         }
         out.close();
      } catch (IOException e) {
    	  e.printStackTrace();
      }
   }
}
