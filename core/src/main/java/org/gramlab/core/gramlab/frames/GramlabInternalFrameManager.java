package fr.gramlab.frames;

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.JDesktopPane;

import fr.gramlab.project.Project;
import fr.gramlab.project.ProjectManager;
import fr.umlv.unitex.DropTargetManager;
import fr.umlv.unitex.frames.InternalFrameManager;

public class GramlabInternalFrameManager extends InternalFrameManager {

	public GramlabInternalFrameManager(final Project p,JDesktopPane desktop) {
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
				ProjectManager.getManager().setCurrentProject(p);
			}
		});
		DropTargetManager.getDropTarget().newDropTarget(desktop);
	}

}
