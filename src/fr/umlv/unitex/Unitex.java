/*
 * Unitex
 *
 * Copyright (C) 2001-2006 Université de Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.util.*;
import javax.swing.*;

import org.jvnet.substance.*;


/**
 * This is the main class of the Unitex system.
 * 
 * @author Sébastien Paumier
 */

public class Unitex {

	/**
	 * Starts Unitex. Shows a <code>SplashScreen</code> with the Unitex logo
	 * and then creates a <code>UnitexFrame</code>.
	 *  
	 */
	public static void main(String[] args) {
		Locale.setDefault(Locale.ENGLISH);
		try {
			UIManager.setLookAndFeel("org.jvnet.substance.SubstanceLookAndFeel");
			/* The following line seems to have no effect, so the search panel
			 * is removed by hand during the creation of the menu bar.
			 */
			SubstanceLookAndFeel.hideMenuSearchPanels();
		} catch (UnsupportedLookAndFeelException e) {
			System.out
					.println("Substance Look & Feel not supported on this platform. \nProgram Terminated");
			System.exit(0);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		new SplashScreen(new ImageIcon(Unitex.class.getResource("Unitex.jpg")),
				1500);
		Config.initConfig(args.length==1?args[0]:null);
		JFrame frame = new UnitexFrame();
		frame.setVisible(true);
	}
	
	
	
	
}