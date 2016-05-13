package fr.umlv.unitex.utils;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * This is the file maintaining Key Bindings
 *
 * @author Mukarram Tailor
 */
public class KeyUtil {

	/**
	 * Pressing Enter on the given component will act as clicking on the given
	 * button
	 */
	public static void addCRListener(JComponent c, final JButton b) {
		c.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					b.doClick();
				}
			}
		});
	}

	public static void addCRListener(JButton b) {
		addCRListener(b, b);
	}

	/**
	 * 
	 * Pressing Esc on a focused dialog will act as clicking cancel
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
	 * 
	 * Pressing Enter on a focused dialog will act as clicking OK
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
	 * 
	 * Pressing Esc on a focused dialog will close
	 * 
	 */
	public static void addCloseListener(final JComponent c) {
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