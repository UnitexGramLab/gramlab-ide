package fr.gramlab.workspace;

import java.awt.BorderLayout;

import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.gramlab.frames.GramlabInternalFrameManager;
import fr.gramlab.project.Project;
import fr.gramlab.project.ProjectManager;
import fr.umlv.unitex.frames.InternalFrameManager;

@SuppressWarnings("serial")
public class ProjectTabbedPane extends JTabbedPane {
	
	boolean selecting=false;
	
	public ProjectTabbedPane() {
		ProjectManager.getManager().addProjectListener(new ProjectAdapter() {
			@Override
			public void projectOpened(Project p, int pos) {
				GramlabInternalFrameManager m=new GramlabInternalFrameManager(p,new JDesktopPane());
				p.setFrameManager(m);
				JPanel tmp=new JPanel(new BorderLayout());
				tmp.add(m.getDesktop(),BorderLayout.CENTER);
				tmp.add(new FrameTabManager(m),BorderLayout.SOUTH);
				addTab(p.getName(),tmp);
				p.getSvnMonitor().monitor(true);
			}
			
			@Override
			public void projectClosing(Project p, int pos, boolean[] canClose) {
				InternalFrameManager m=InternalFrameManager.getManager(p.getProjectDirectory(),false);
				if (m==null) return;
				p.saveOpenFrames(m.getDesktop().getAllFrames());
				if (!m.closeAllFrames()) {
					canClose[0]=false;
					return;
				}
				try {
					int index=getTabIndex(p.getName());
					removeTabAt(index);
				} catch (IllegalArgumentException e) {
					/* projectClosing may be invoked on a project that is not actually open */
				}
			}

			@Override
			public void projectClosed(Project p, int pos) {
				if (!selecting && ProjectManager.getManager().getCurrentProject()==null) {
					setSelectedIndex(-1);
				}
			}
			
			@Override
			public void currentProjectChanged(Project p, int pos) {
				int currentIndex=getSelectedIndex();
				int index;
				if (p==null) {
					index=-1;
				}
				else {
					try {
						index=getTabIndex(p.getName());
					} catch (IllegalArgumentException e) {
						index=-1;
					}
				}
				if (!selecting || (currentIndex!=index)) setSelectedIndex(index);
			}
		});
		getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				/* If the selected tab has changed, we must update
				 * the current project */
				if (selecting) {
					/* We have to avoid an infinite call loop */
					return;
				}
				selecting=true;
				try {
					int index=getSelectedIndex();
					if (index==-1) {
						/* There may be no selected index because we have removed
						 * the selected one. In that case, we look for an open project
						 * if any.
						 */
						if (getTabCount()>0) {
							index=0;
						}
					}
					if (index==-1) {
						ProjectManager.getManager().setCurrentProject(null);
					} else {
						String name=getTitleAt(index);
						Project p=ProjectManager.getManager().getProject(name);
						ProjectManager.getManager().setCurrentProject(p);
					}
				} finally {
					selecting=false;
				}
			}
		});
	}

	int getTabIndex(String name) {
		if (name==null) {
			throw new IllegalArgumentException("Unexpected null project name");
		}
		int n=getTabCount();
		for (int i=0;i<n;i++) {
			if (getTitleAt(i).equals(name)) return i;
		}
		throw new IllegalArgumentException("Unknown project: "+name);
	}
	
}
