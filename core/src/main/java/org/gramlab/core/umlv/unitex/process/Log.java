/*
 * Unitex
 *
 * Copyright (C) 2001-2016 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

import javax.swing.JOptionPane;

import fr.umlv.unitex.config.ConfigManager;
import fr.umlv.unitex.files.FileUtil;

public class Log {
	/**
	 * Note: The log # cannot be stored in the Command object, since a failure
	 * can skip commands. So, we have to call this method just before invoking
	 * the command.
	 * 
	 * @return the log ID or null, in case of error or if logs are disabled
	 */
	public static String getCurrentLogID() {
		if (!ConfigManager.getManager().mustLog(null)) {
			return null;
		}
		final File logDir = ConfigManager.getManager().getLogDirectory(null);
		if (logDir == null) {
			throw new IllegalStateException(
					"Should not have a null logging directory when mustLog is true");
		}
		if (!logDir.exists()) {
			JOptionPane.showMessageDialog(
					null,
					"Log directory does not exist: \n\n"
							+ logDir.getAbsolutePath()
							+ "\n\nSet it properly or deactivate logging",
					"Log dir error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		final File count = new File(logDir,
				"unitex_logging_parameters_count.txt");
		if (!count.exists()) {
			/*
			 * If the configuration file does not exist, the first log will have
			 * #1 and we have to delete any preexisting .ulp files
			 */
			FileUtil.removeFile(new File(logDir, "*.ulp"));
			return "1";
		}
		try {
			final Scanner scanner = new Scanner(count, "UTF8");
			if (!scanner.hasNextInt()) {
				return null;
			}
			final int n = scanner.nextInt();
			if (n < 0)
				return null;
			scanner.close();
			/* +1 because the file contains the last log # */
			return "" + (n + 1);
		} catch (final FileNotFoundException e) {
			return null;
		}
	}
}
