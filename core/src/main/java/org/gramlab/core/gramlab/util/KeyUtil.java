package org.gramlab.core.gramlab.util;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;

public class KeyUtil {


	/**
	 * Pressing Enter on the given component will act as clicking 
	 * on the given button
	 */
	public static void addCRListener(JComponent c,final JButton b) {
		c.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar()==KeyEvent.VK_ENTER) {
					b.doClick();
				}
			}
		});
	}
	
	
	public static void addCRListener(JButton b) {
		addCRListener(b,b);
	}
}
