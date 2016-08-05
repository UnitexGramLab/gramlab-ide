package org.gramlab.core.umlv.unitex.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;
import org.gramlab.core.umlv.unitex.frames.UnitexInternalFrameManager;

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
				GlobalProjectManager.search(null)
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