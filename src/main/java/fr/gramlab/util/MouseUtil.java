package fr.gramlab.util;

import java.awt.event.MouseEvent;

import fr.umlv.unitex.config.Config;

public class MouseUtil {
	
	public static boolean isPopupTrigger(MouseEvent e) {
		if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) return true;
		if (Config.getSystem()!=Config.MAC_OS_X_SYSTEM) {
			return e.getButton() == MouseEvent.BUTTON3;
		}
		/* Under MacOS, we also consider a Ctrl+click */
		return e.isControlDown() && e.getButton()==MouseEvent.BUTTON1;
	}
}
