package fr.gramlab.workspace;

import java.awt.BorderLayout;

import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.umlv.unitex.common.project.manager.GlobalProjectManager;
import fr.gramlab.frames.GramlabInternalFrameManager;
import fr.gramlab.project.GramlabProject;
import fr.gramlab.project.GramlabProjectManager;
import fr.umlv.unitex.frames.InternalFrameManager;

@SuppressWarnings("serial")
public class ProjectTabbedPane extends JTabbedPane {
	
	boolean selecting=false;
	
	public ProjectTabbedPane() {
		GlobalProjectManager.getAs(GramlabProjectManager.class)
			.addProjectListener(new ProjectAdapter() {
			@Override
			public void projectOpened(GramlabProject p, int pos) {
				GramlabInternalFrameManager m=new GramlabInternalFrameManager(p,new JDesktopPane());
				p.setFrameManager(m);
				JPanel tmp=new JPanel(new BorderLayout());
				tmp.add(m.getDesktop(),BorderLayout.CENTER);
				tmp.add(new FrameTabManager(m),BorderLayout.SOUTH);
				addTab(p.getName(),tmp);
				p.getSvnMonitor().monitor(true);
			}
			
			@Override
			public void projectClosing(GramlabProject p, int pos, boolean[] canClose) {
				InternalFrameManager m=GlobalProjectManager.search(p.getProjectDirectory(),false)
					.getFrameManagerAs(InternalFrameManager.class);
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
			public void projectClosed(GramlabProject p, int pos) {
				if (!selecting && GlobalProjectManager.getAs(GramlabProjectManager.class).getCurrentProject()==null) {
					setSelectedIndex(-1);
				}
			}
			
			@Override
			public void currentProjectChanged(GramlabProject p, int pos) {
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
						GlobalProjectManager.getAs(GramlabProjectManager.class).setCurrentProject(null);
					} else {
						String name=getTitleAt(index);
						GramlabProject p=GlobalProjectManager.getAs(GramlabProjectManager.class)
									.getProject(name);
						GlobalProjectManager.getAs(GramlabProjectManager.class).setCurrentProject(p);
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
