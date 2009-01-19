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
 * This class is used to display a frame that just contains a
 * <code>JTextField</code> to display messages.
 * 
 * @author Sébastien Paumier
 *  
 */
public class MessageWhileWorkingFrame extends JInternalFrame {

	static MessageWhileWorkingFrame frame;
	private static JLabel label = new JLabel();

	private MessageWhileWorkingFrame() {
		super("", true, true);
		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(true);
		top.setBorder(new EmptyBorder(2, 2, 2, 2));
		top.add(label);
		setContentPane(top);
		setBounds(100, 100, 450, 80);
		setVisible(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				GraphCollection.stop();
			}
		});
	}

	/**
	 * Initializes the frame
	 *  
	 */
	public static void init() {
		frame = new MessageWhileWorkingFrame();
		UnitexFrame.addInternalFrame(frame);
	}

	/**
	 * 
	 * @return the message label
	 */
	public static JLabel getLabel() {
		return label;
	}

	/**
	 * Shows the frame.
	 *  
	 */
	public static void showFrame() {
		showFrame("");
	}

	/**
	 * Shows the frame.
	 * 
	 * @param title
	 *            title of the frame
	 */
	public static void showFrame(String title) {
		if (frame == null) {
			init();
		}
		label.setText("");
		frame.setTitle(title);
		frame.setVisible(true);
	}

	/**
	 * Closes the frame.
	 *  
	 */
	public static void close() {
		frame.setVisible(false);
	}

}