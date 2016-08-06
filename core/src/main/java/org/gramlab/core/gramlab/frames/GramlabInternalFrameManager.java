package org.gramlab.core.gramlab.frames;

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.JDesktopPane;

import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.gramlab.project.GramlabProjectManager;
import org.gramlab.core.umlv.unitex.DropTargetManager;
import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;
import org.gramlab.core.umlv.unitex.frames.InternalFrameManager;

public class GramlabInternalFrameManager extends InternalFrameManager {

	public GramlabInternalFrameManager(final GramlabProject p,JDesktopPane desktop) {
		super(desktop);
		desktop.addContainerListener(new ContainerListener() {
			@Override
			public void componentRemoved(ContainerEvent e) {
				/* */
			}
			
			@Override
			public void componentAdded(ContainerEvent e) {
				/* Everytime a frame is added to a gramlab frame manager's desktop,
				 * we ask that the corresponding project becomes the current one,
				 * so that the project's tab becomes visible */
				GlobalProjectManager.getAs(GramlabProjectManager.class).setCurrentProject(p);
			}
		});
		DropTargetManager.getDropTarget().newDropTarget(desktop);
	}

}
