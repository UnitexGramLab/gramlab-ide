package fr.gramlab.util;

import fr.gramlab.Main;
import fr.umlv.unitex.utils.SingleInstanceMonitor;

/**
 * This class extends SingleInstanceMonitor to provide a method to maximise the GramlabFrame
 * 
 * @see fr.umlv.unitex.utils.SingleInstanceMonitor
 * @author markpower
 *
 */
public class GramlabSingleInstanceMonitor extends SingleInstanceMonitor {

	
	public void setFrameVisible() {
		if (Main.getMainFrame().isVisible()) {
			Main.getMainFrame().setVisible(true);
		}
	}
}
