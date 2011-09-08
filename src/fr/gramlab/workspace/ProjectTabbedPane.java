package fr.gramlab.workspace;

import javax.swing.JDesktopPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.gramlab.frames.ProjectFrameManager;
import fr.umlv.unitex.frames.InternalFrameManager;

public class ProjectTabbedPane extends JTabbedPane {
	
	final static String DEFAULT_TAB_NAME="(default)";
	
	boolean selecting=false;
	
	public ProjectTabbedPane() {
		super();
		addTab(DEFAULT_TAB_NAME,ProjectFrameManager.getDefaultManager().getDesktop());
		ProjectManager.getManager().addProjectListener(new ProjectAdapter() {
			@Override
			public void projectOpened(Project p, int pos) {
				InternalFrameManager m=new InternalFrameManager(new JDesktopPane());
				p.setFrameManager(m);
				addTab(p.getName(),m.getDesktop());
			}
			
			@Override
			public void projectClosed(Project p, int pos) {
				int index=getTabIndex(p.getName());
				removeTabAt(index);
			}
			
			@Override
			public void currentProjectChanged(Project p, int pos) {
				int index;
				if (p==null) index=getTabIndex(DEFAULT_TAB_NAME);
				else index=getTabIndex(p.getName());
				if (!selecting) setSelectedIndex(index);
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
					if (index==-1) ProjectManager.getManager().setCurrentProject(null);
					else {
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
		throw new IllegalArgumentException("Unknown project name");
	}
	
}
