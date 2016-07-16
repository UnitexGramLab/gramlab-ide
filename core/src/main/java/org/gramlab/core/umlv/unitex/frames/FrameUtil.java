package org.gramlab.core.umlv.unitex.frames;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

public class FrameUtil {

	public static void center(Component parent,Component c) {
		int w,h;
		if (parent!=null) {
			w=parent.getWidth();
			h=parent.getHeight();
		} else {
			Dimension size=Toolkit.getDefaultToolkit().getScreenSize();
			w=size.width;
			h=size.height;
		}
		c.setLocation((w-c.getWidth())/2,(h-c.getHeight())/2);
	}

}
