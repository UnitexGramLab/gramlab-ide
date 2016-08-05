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
package fr.umlv.unitex.exceptions;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * This class defines an <code>Exception</code> that is thrown when the user
 * wants to validate a box content with a backslash at the end of the line.
 * 
 * @author Sébastien Paumier
 * 
 */
public class UnitexUncaughtExceptionHandler implements UncaughtExceptionHandler {
	private static UnitexUncaughtExceptionHandler handler;

	private UnitexUncaughtExceptionHandler() {
		/* This should not be called from the outside */
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		final Box b = new Box(BoxLayout.Y_AXIS);
		b.add(new JLabel(e.toString()));
		String s = e.toString() + "\n";
		for (final StackTraceElement elem : e.getStackTrace()) {
			b.add(new JLabel("at " + elem.toString()));
			s = s + "at " + elem.toString() + "\n";
		}
		if (e.getCause() != null) {
			b.add(new JLabel("Caused by: " + e.getCause()));
			s = s + "Caused by: " + e.getCause() + "\n";
			for (final StackTraceElement elem : e.getCause().getStackTrace()) {
				b.add(new JLabel("at " + elem.toString()));
				s = s + "at " + elem.toString() + "\n";
			}
		}
		final JScrollPane scroll = new JScrollPane(b);
		scroll.setPreferredSize(new Dimension(b.getPreferredSize().width + 50, 400));
		scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
		final JPanel p = new JPanel(new BorderLayout());
		p.add(scroll, BorderLayout.CENTER);

		final JTextPane jTextPane = new JTextPane();
		jTextPane.setEditable(false);
		jTextPane.setOpaque(false);
		jTextPane.setContentType("text/html");
		jTextPane.setText("If you are having problems running Unitex/GramLab please"
				+ " feel free to post a support question<br> on the community support"
				+ " forum : <a href='http://forum.unitexgramlab.org'> "
				+ "http://forum.unitexgramlab.org</a>. <br>Some general advice "
				+ "about asking technical support questions can be found at <br>"
				+ "<a href='http://www.catb.org/esr/faqs/smart-questions.html'>"
				+ "http://www.catb.org/esr/faqs/smart-questions.html</a>.");
		jTextPane.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
				if (hyperlinkEvent.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
					try {
						Desktop.getDesktop().browse(hyperlinkEvent.getURL().toURI());
					} catch (URISyntaxException uriSyntaxException) {
						uriSyntaxException.printStackTrace();
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
				}
			}
		});

		p.add(jTextPane, BorderLayout.SOUTH);

		JOptionPane.showConfirmDialog(null, p, "Java Exception", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
	}

	public static UnitexUncaughtExceptionHandler getHandler() {
		if (handler == null) {
			handler = new UnitexUncaughtExceptionHandler();
		}
		return handler;
	}
}
