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

import java.io.*;
import java.util.*;

/**
 * This class only contains a <code>String</code> that indicates version information.
 * The content of this string appears in the caption of the main frame.  
 * @author Sébastien Paumier
 *
 */
public class Version {
   /**
    * The string that contains the version number, and the date of the release.
    */
   public final static String version= "Unitex 2.1beta "+getRevisionDate();

   
   /**
    * @return a <code>String</code> representing the date of the .jar file that
    * contains Unitex's graphical interface in the form "(May 10, 2006)". 
    */
   private static String getJarDate() {
	   String date="(";
	   Calendar calendar=Calendar.getInstance();
	   calendar.setTimeInMillis(new File(Config.getApplicationDir(),"Unitex.jar").lastModified());
	   switch (calendar.get(Calendar.MONTH)) {
	   		case Calendar.JANUARY: date=date+"January "; break;
	   		case Calendar.FEBRUARY: date=date+"February "; break;
	   		case Calendar.MARCH: date=date+"March "; break;
	   		case Calendar.APRIL: date=date+"April "; break;
	   		case Calendar.MAY: date=date+"May "; break;
	   		case Calendar.JUNE: date=date+"June "; break;
	   		case Calendar.JULY: date=date+"July "; break;
	   		case Calendar.AUGUST: date=date+"August "; break;
	   		case Calendar.SEPTEMBER: date=date+"September "; break;
	   		case Calendar.OCTOBER: date=date+"October "; break;
	   		case Calendar.NOVEMBER: date=date+"November "; break;
	   		case Calendar.DECEMBER: date=date+"December "; break;
	   }
	   return date+calendar.get(Calendar.DAY_OF_MONTH)+", "+calendar.get(Calendar.YEAR)+")";
   }
   
   private static String getRevisionDate() {
	   File f=new File(Config.getApplicationDir(),"revision.date");
	   if (!f.exists() || f.length()==0) {
		   return getJarDate();
	   }
	   FileInputStream stream;
	try {
		stream = new FileInputStream(f);
	} catch (FileNotFoundException e) {
		e.printStackTrace();
		return getJarDate();
	}
	   byte[] buffer=new byte[64];
	   int n;
	   try {
		n=stream.read(buffer);
	} catch (IOException e) {
		e.printStackTrace();
		return getJarDate();
	}
	   try {
		return "("+new String(buffer,0,n-1,"UTF8")+")";
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
		return getJarDate();
	}
   }

}