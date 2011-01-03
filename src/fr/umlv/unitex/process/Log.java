/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.Preferences;

public class Log {

	/**
	 * Note: The log # cannot be stored in the Command object, since a failure can
	 * skip commands. So, we have to call this method just before invoking the command.
	 * 
	 * @return the log ID or null, in case of error or if logs are disabled
	 */
	public static String getCurrentLogID() {
		if (!Preferences.mustLog()) {
			return null;
		}
		if (Preferences.loggingDir()==null) {
			throw new IllegalStateException("Should not have a null logging directory when mustLog is true");
		}
		File count=new File(Preferences.loggingDir(),"unitex_logging_parameters_count.txt");
		if (!count.exists()) {
			/* If the configuration file does not exist, the first log will 
			 * have #1 and we have to delete any preexisting .ulp files */ 
			Config.removeFile(new File(Preferences.loggingDir(),"*.ulp"));
			return "1";
		}
		try {
			Scanner scanner=new Scanner(count,"UTF8");
			if (!scanner.hasNextInt()) {
				return null;
			}
			int n=scanner.nextInt();
			if (n<0) return null;
			scanner.close();
			/* +1 because the file contains the last log # */
			return ""+(n+1);
		} catch (FileNotFoundException e) {
			return null;
		}
		
	}
	
}
