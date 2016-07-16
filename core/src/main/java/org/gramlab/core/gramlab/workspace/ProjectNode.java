package org.gramlab.core.gramlab.workspace;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Timer;

import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.gramlab.project.GramlabProjectManager;
import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;


public class ProjectNode extends WorkspaceTreeNode {

	GramlabProject project;
	private Timer timer=new Timer(3000,new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			project.asyncUpdateSvnInfo(null,false);
		}
	});
		
	
	public ProjectNode(final GramlabProject project,RootNode root) {
		super(project.getProjectDirectory(),root);
		this.project=project;
		timer.setCoalesce(false);
		timer.setInitialDelay(1000);
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				/* We have to use an invokeLater in order to avoid
				 * a concurrent exception with the ProjectManager's 
				 * projectListeners field
				 */
				GlobalProjectManager.getAs(GramlabProjectManager.class)
					.addProjectListener(new ProjectAdapter() {
					@Override
					public void projectOpened(GramlabProject p, int pos) {
						if (project.equals(p)) {
							timer.start();
						}
					}
					
					@Override
					public void projectClosed(GramlabProject p, int pos) {
						if (project.equals(p)) {
							timer.stop();
						}
					}
				});
				if (project.isOpen() && !timer.isRunning()) {
					timer.start();
				}
			}
		});
	}
	
	@Override
	public boolean isLeaf() {
		return !project.isOpen();
	}
	
	public GramlabProject getProject() {
		return project;
	}
	
	@Override
	public void refresh(ArrayList<File> removedFiles,
			ArrayList<File> forceRefresh,boolean forceAll) {
		/* In input, forceRefresh contains the list of all files that 
		 * must be refreshed. However, in order to force the recursive exploration
		 * of their parent directories that may not have been modified themselves,
		 * we add those parents to the refresh list
		 */
		if (forceRefresh==null) {
			super.refresh(removedFiles,null,forceAll);
			return;
		}
		ArrayList<File> list=new ArrayList<File>();
		for (File f:forceRefresh) {
			do {
				if (!list.contains(f)) list.add(f);
				f=f.getParentFile();
			} while (f!=null && !f.equals(project.getProjectDirectory()));
		}
		if (!list.contains(project.getProjectDirectory())) list.add(project.getProjectDirectory());
		super.refresh(removedFiles, list, forceAll);
	}
	
}
