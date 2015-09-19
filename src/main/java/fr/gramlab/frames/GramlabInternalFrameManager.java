package fr.gramlab.frames;

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.JDesktopPane;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.GramlabProjectManager;
import fr.umlv.unitex.DropTargetManager;
import fr.umlv.unitex.frames.InternalFrameManager;

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
