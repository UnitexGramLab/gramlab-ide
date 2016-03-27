package fr.gramlab.workspace;

import java.awt.Dimension;

import javax.swing.JComponent;

import fr.umlv.unitex.frames.TabbableInternalFrame;

@SuppressWarnings("serial")
public class FrameTab extends JComponent {
	
	private TabbableInternalFrame frame;

	public FrameTab(TabbableInternalFrame f) {
		this.frame=f;
		setMinimumSize(new Dimension(0,0));
		setMaximumSize(new Dimension(0,0));
		setPreferredSize(new Dimension(0,0));
	}

	public TabbableInternalFrame getFrame() {
		return frame;
	}
	
	public String getTabName() {
		return frame.getTabName(); 
	}
	
}
