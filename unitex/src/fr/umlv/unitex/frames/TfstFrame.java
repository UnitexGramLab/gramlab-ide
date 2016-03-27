package fr.umlv.unitex.frames;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;

import fr.umlv.unitex.graphrendering.TfstGraphicalZone;

public abstract class TfstFrame extends JInternalFrame {

	public TfstFrame(String a, boolean b, boolean c, boolean d, boolean e) {
		super(a,b,c,d,e);
	}
	
	public abstract JScrollPane getTfstScrollPane();
	public abstract TfstGraphicalZone getTfstGraphicalZone();
	
}
