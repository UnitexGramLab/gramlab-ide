package fr.gramlab.util;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;

/**
 * This class stores various key bidings.
 * 
 * @author Mukarram Tailor
 */

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
	/**
	 * Pressing Esc on a focused dialog will act as clicking cancel button
	 * 
	 */	
	public static void addEscListener(JComponent c, final JButton b) {
		c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "closeTheDialog");
		c.getActionMap().put("closeTheDialog", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent cancel) {
				b.doClick();
			}
		});
	}
	
	/**
	 * Pressing Enter on a focused dialog will act as clicking OK button
	 * 
	 */	
	public static void addEnterListener(JComponent c, final JButton b) {
		c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "pressOK");
		c.getActionMap().put("pressOK", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent next) {
				b.doClick();
			}
		});
	}
	
	/** 
	 * Pressing Esc on a focused dialog will close it
	 * 
	 */	
	public static void addClosingListener(final JComponent c) {
		c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "closeTheDialog");
		c.getActionMap().put("closeTheDialog", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent close) {
				c.getFocusCycleRootAncestor().setVisible(false);
			}
		});
	}
}
