package org.gramlab.core.umlv.unitex.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.gramlab.core.umlv.unitex.frames.UnitexInternalFrameManager;
import org.gramlab.core.umlv.unitex.project.manager.UnitexProjectManager;

/**
 * 
 * @author mdamis
 *
 */
public class UnitexHelpMenuBuilder extends HelpMenuBuilder {

	private static JMenuItem buildCommandsMenuItem() {
		JMenuItem commands = new JMenuItem("Commands");
		commands.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UnitexProjectManager.search(null)
						.getFrameManagerAs(UnitexInternalFrameManager.class)
						.newHelpOnCommandFrame();
			}
		});
		return commands;
	}
	
	public static JMenu build(File appDir) {
		JMenu menu = HelpMenuBuilder.build(appDir);
		menu.addSeparator();
		menu.add(buildCommandsMenuItem());
		return menu;
	}
}