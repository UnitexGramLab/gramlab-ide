package fr.gramlab.util;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * This class stores various key bindings.
 * 
 */

public class KeyUtil {


	/**
	 * Pressing Enter on the given component will act as clicking 
	 * on the given button.
	 * 
	 * @param c
	 * 		the given component
	 * @param b
	 * 		the given button
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
	 * This method implements hotkey  binding for [Esc] for currently focused JComponent with a "Cancel" Button.
	 * Pressing Esc, would mimic as clicking on cancel button.
	 * 
	 * @param b
	 * 		the cancel button(or exit or Back)
	 * @param c
	 * 		the focused JComponent
	 * @author Mukarram Tailor
	 */	
	public static void addEscListener(JComponent c, final JButton b) {
		c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "pressCancel");
		c.getActionMap().put("pressCancel", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent cancel) {
				b.doClick();
			}
		});
	}
	
	/**
	 * This method implements hotkey  binding for [Enter] for currently focused JComponent with a "Ok" Button.
	 * Pressing Enter, would mimic as clicking on Ok button.
	 * 
	 * @param b
	 * 		the Ok button(or Next or Done)
	 * @param c
	 * 		the focused Jcomponent
	 * @author Mukarram Tailor
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
	 * This method implements hotkey  binding for [Esc] for currently focused Dialog box.
	 * Pressing Esc, would close the dialog box.
	 * 
	 * @param c
	 * 		the focused component that can be a part of JDialog 
	 * @author Mukarram Tailor
	 */	
	public static void addCloseDialogListener(final JComponent c) {
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
