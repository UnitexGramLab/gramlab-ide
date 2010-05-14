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
 * This class defines a color rectangle.
 * 
 * @author S&bastien Paumier
 *  
 */
public class ColorRectangle extends JComponent {

	private ColorRectangle() {
		/* Just to prevent people from using this constructor
		 * instead of the factory method below
		 */
	}
	
	public static JPanel getColorRectangle() {
		JPanel p=new JPanel(null);
		p.setMinimumSize(new Dimension(70,22));
		p.setSize(new Dimension(70,22));
		p.setPreferredSize(new Dimension(70,22));
		return p;
	}
	
}
