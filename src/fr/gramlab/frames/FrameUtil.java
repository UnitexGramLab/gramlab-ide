package fr.gramlab.frames;

import java.awt.Component;

public class FrameUtil {

	public static void center(Component parent,Component c) {
		int w=parent.getWidth();
		int h=parent.getHeight();
		c.setLocation((w-c.getWidth())/2,(h-c.getHeight())/2);
	}

}
