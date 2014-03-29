package fr.umlv.unitex.graphrendering;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import fr.umlv.unitex.frames.GraphFrame;
import fr.umlv.unitex.frames.InternalFrameManager;

public class GraphMenuBuilder {

	public static JMenu createExportMenu() {
		return createExportMenu(null);
	}
	
	public static JMenu createExportMenu(final GraphicalZone grZone) {
		JMenu exportMenu = new JMenu("Export as Image");
		final Action exportPng = new AbstractAction("Export as PNG Image...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame frm = findFrame(grZone);
				if(frm!=null)
					frm.exportPng();
			}
		};
		
		exportMenu.add(new JMenuItem(exportPng));

		final Action exportJpeg = new AbstractAction("Export as JPEG Image...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame frm = findFrame(grZone);
				if(frm!=null)
					frm.exportJpeg();
			}
		};
		
		exportMenu.add(new JMenuItem(exportJpeg));
		
		final Action exportSvg = new AbstractAction("Export as SVG Image...") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final GraphFrame frm = findFrame(grZone);
				if(frm!=null)
					frm.exportSvg();
			}
		};
		
		exportMenu.add(new JMenuItem(exportSvg));
		
		return exportMenu;
	}
	
	protected static GraphFrame findFrame(GraphicalZone grZone) {
		GraphFrame frm = null;
		if(grZone != null) {
			frm = (GraphFrame) grZone.getParentFrame();
		}
		if(frm == null) {
			frm = InternalFrameManager.getManager(null).getCurrentFocusedGraphFrame();
		}
		if(frm == null) {
			JOptionPane.showMessageDialog(null,"No active graph frame!","Missing Graph Frame", JOptionPane.WARNING_MESSAGE);
		} 
		return frm;
	}
}
