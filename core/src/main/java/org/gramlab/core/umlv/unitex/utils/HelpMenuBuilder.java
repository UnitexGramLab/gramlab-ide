package org.gramlab.core.umlv.unitex.utils;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * 
 * @author mdamis
 *
 * @source http://stackoverflow.com/a/18509384 for opening a link in the default
 *         browser.
 */
public class HelpMenuBuilder {

	public static JMenu build(File appDir) {
		JMenu helpMenu = new JMenu("Help");

		JMenu manuals = buildManualsMenu(appDir);
		if (manuals.getItemCount() > 0) {
			helpMenu.add(manuals);
			helpMenu.addSeparator();
		}

		helpMenu.add(buildWebsiteMenuItem());
		helpMenu.add(buildForumMenuItem());

		return helpMenu;
	}

	static JMenu buildManualsMenu(File appDir) {
		JMenu manuals = new JMenu("Manuals");

		File manualsDir = new File(appDir.getPath() + File.separatorChar
				+ "manual");

		if (manualsDir.exists() && manualsDir.isDirectory()) {
			for (File manualDirContent : manualsDir.listFiles()) {
				if (manualDirContent.isDirectory()) {
					for (final File manual : manualDirContent.listFiles()) {
						if (manual.getName().contains(".pdf")) {
							JMenuItem manualAction = new JMenuItem(
									manualDirContent.getName());
							manualAction
									.addActionListener(new ActionListener() {
										@Override
										public void actionPerformed(
												ActionEvent e) {
											try {
												Desktop.getDesktop().open(
														manual);
											} catch (Exception exception) {
												exception.printStackTrace();
											}
										}
									});
							manuals.add(manualAction);
						}
					}
				}
			}
		}
		return manuals;
	}

	static JMenuItem buildWebsiteMenuItem() {
		JMenuItem website = new JMenuItem("Website");
		website.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Desktop.isDesktopSupported()) {
					Desktop desktop = Desktop.getDesktop();
					try {
						desktop.browse(new URI("http://unitexgramlab.org"));
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
		});
		return website;
	}

	static JMenuItem buildForumMenuItem() {
		JMenuItem forum = new JMenuItem("Forum");
		forum.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Desktop.isDesktopSupported()) {
					Desktop desktop = Desktop.getDesktop();
					try {
						desktop.browse(new URI("http://forum.unitexgramlab.org"));
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
		});
		return forum;
	}
}