package fr.gramlab.project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import fr.gramlab.GramlabConfigManager;
import fr.umlv.unitex.files.FileUtil;


public class ProjectManager {
	
	private ArrayList<ProjectListener> projectListeners=new ArrayList<ProjectListener>();
	private boolean firingProject=false;
	
	private ArrayList<Project> projects=new ArrayList<Project>();
	
	private static ProjectManager manager;
	
	private Project currentProject=null;
	
	
	public void addProjectListener(ProjectListener l) {
		projectListeners.add(l);
	}
	
	public void removeProjectListener(ProjectListener l) {
		if (firingProject) {
			throw new IllegalStateException("Cannot remove a listener while firing");
		}
		projectListeners.remove(l);
	}
	
	protected void fireProjectAdded(Project p,int pos) {
		firingProject=true;
		try {
			for (int i=0;i<projectListeners.size();i++) {
				projectListeners.get(i).projectAdded(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}

	protected void fireProjectRemoved(Project p,int pos) {
		firingProject=true;
		try {
			for (int i=0;i<projectListeners.size();i++) {
				projectListeners.get(i).projectRemoved(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}

	protected void fireProjectOpened(Project p,int pos) {
		firingProject=true;
		try {
			for (int i=0;i<projectListeners.size();i++) {
				projectListeners.get(i).projectOpened(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}

	protected void fireProjectSvnModified(Project p,int pos) {
		firingProject=true;
		try {
			for (int i=0;i<projectListeners.size();i++) {
				projectListeners.get(i).projectSVNModified(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}

	/**
	 * Evil hack: canClose is used as C-like pointer in order to avoid a complicated
	 *            VetoableProperty thing
	 */
	protected void fireProjectClosing(Project p,int pos,boolean[] canClose) {
		firingProject=true;
		try {
			canClose[0]=true;
			for (int i=0;canClose[0] && i<projectListeners.size();i++) {
				projectListeners.get(i).projectClosing(p,pos,canClose);
			}

		} finally {
			firingProject=false;
		}
	}

	protected void fireProjectClosed(Project p,int pos) {
		firingProject=true;
		try {
			for (int i=0;i<projectListeners.size();i++) {
				projectListeners.get(i).projectClosed(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}

	protected void fireCurrentProjectChanged(Project p,int pos) {
		firingProject=true;
		try {
			for (int i=0;i<projectListeners.size();i++) {
				projectListeners.get(i).currentProjectChanged(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}
	
	public void addProject(Project p) {
		if (p==null) {
			throw new IllegalArgumentException("Cannot add a null Project");
		}
		synchronized (p) {
			if (projects.contains(p)) return;
			projects.add(p);
			Collections.sort(projects);
			int pos=projects.indexOf(p);
			fireProjectAdded(p,pos);
		}
	}

	public void openProject(Project p) {
		synchronized (p) {
			int pos=projects.indexOf(p);
			if (pos==-1 || p.isOpen()) return;
			p.open();
			fireProjectOpened(p,pos);
			if (currentProject==null) {
				setCurrentProject(p);
			}
		}
	}

	/**
	 * Tries to close the project. The user can refuse it if there are some
	 * unsaved files. In that case, closeProject will return false; true otherwise.
	 */
	public boolean closeProject(Project p) {
		if (p==null) p=getCurrentProject();
		if (p==null) return true;
		synchronized (p) {
			int pos=projects.indexOf(p);
			if (pos==-1 || !p.isOpen()) return true;
			boolean[] canClose=new boolean[] {true};
			fireProjectClosing(p,pos,canClose);
			if (!canClose[0]) return false;
			p.close();
			fireProjectClosed(p,pos);
			if (currentProject==p) {
				setCurrentProject(null);
			}
			return true;		
		}
	}

	
	public void notifyProjectSvnChange(Project p) {
		if (p==null) {
			throw new IllegalArgumentException("Invalid null Project");
		}
		synchronized (p) {
			int pos=projects.indexOf(p);
			if (pos==-1) return;
			fireProjectSvnModified(p,pos);
		}
	}

	
	private void removeProject(Project p) {
		if (p==null) p=getCurrentProject();
		if (p==null) return;
		synchronized (p) {
			int pos=projects.indexOf(p);
			if (pos==-1) return;
			if (p.isOpen()) {
				closeProject(p);
			}
			projects.remove(p);
			fireProjectRemoved(p,pos);
		}
	}
	
	public boolean deleteProject(Project p,boolean askConfirmation) {
		if (p==null) p=getCurrentProject();
		if (p==null) return false;
		synchronized (p) {
			if (askConfirmation) {
				final String[] options = { "Yes", "No" };
				final int n = JOptionPane.showOptionDialog(null,
					"Do you really want to delete project '"+p.getName()+"' and all its data?", "", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if (n == 1) {
					return false;
				}
			}
			removeProject(p);
			FileUtil.setRecursivelyWritable(p.getProjectDirectory());
			final File dir=p.getProjectDirectory();
			if (!FileUtil.rm(p.getProjectDirectory())) {
				/* That may occur because of a svn lock. So we retry later */ 
				Timer t=new Timer(1000,new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (!FileUtil.rm(dir)) {
							JOptionPane
							.showMessageDialog(
									null,
									"Cannot delete project directory "+dir.getAbsolutePath()+
									",\nmaybe because of a svn lock. You have to remove it manually.",
									"Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
				t.setInitialDelay(4000);
				t.setRepeats(false);
				t.start();
			}
			return true;
		}
	}
	
	public boolean isProjectDirectory(File f) {
		if (!f.exists() || !f.isDirectory() 
				|| !f.getParentFile().equals(GramlabConfigManager.getWorkspaceDirectory())) return false;
		Project p=new Project(f.getName(),null,null,null);
		return p.getPom().getFile().exists();
	}

	public ArrayList<Project> getProjects() {
		return projects;
	}

	public void loadProjects() {
		File[] files=GramlabConfigManager.getWorkspaceDirectory().listFiles();
		if (files==null) {
			return;
		}
		for (File f:files) {
			if (isProjectDirectory(f)) {
				addProject(new Project(f.getName(),null,null,null));
			}
		}
		for (String s:GramlabConfigManager.getPreviousOpenProjects()) {
			Project p=getProject(s);
			if (p!=null) {
				openProject(p);
			}
		}
		String s=GramlabConfigManager.getPreviousCurrentProject();
		if (s!=null) {
			Project p=getProject(s);
			if (p!=null) {
				openProject(p);
				setCurrentProject(p);
			}
		}
	}

	public static void setManager(ProjectManager manager) {
		ProjectManager.manager = manager;
	}

	public static ProjectManager getManager() {
		return manager;
	}

	/**
	 * Looks for a project whose name is projectName
	 * @param projectName
	 * @return
	 */
	public Project getProject(String projectName) {
		if (projectName==null) {
			return getCurrentProject();
		}
		for (Project p:projects) {
			if (p.getName().equals(projectName)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Looks for a project whose directory contains the given file
	 */
	public Project getProject(File f) {
		for (Project p:projects) {
			if (null!=FileUtil.isAncestor(p.getProjectDirectory(),f)) {
				return p;
			}
		}
		return null;
	}

	public void setCurrentProject(Project p) {
		if (p!=null && !projects.contains(p)) {
			throw new IllegalArgumentException("The current project cannot be outside the workspace");
		}
		if (currentProject==p) {
			/* Nothing to do if the project does not actually change */
			return;
		}
		currentProject = p;
		fireCurrentProjectChanged(p,(p==null)?-1:projects.indexOf(p));
	}

	public Project getCurrentProject() {
		return currentProject;
	}

	public void changeWorkspace(File dir) {
		removeAllProjects();
		GramlabConfigManager.setWorkspaceDirectory(dir,true);
		loadProjects();
	}

	private void removeAllProjects() {
		for (int i=projects.size()-1;i>=0;i--) {
			Project p=projects.get(i);
			closeProject(p);
			removeProject(p);
		}
		
	}
	
	public boolean closeAllProjects() {
		for (int i=projects.size()-1;i>=0;i--) {
			Project p=projects.get(i);
			if (p.isOpen()) {
				if (!closeProject(p)) {
					return false;
				}
			}
		}
		return true;
	}

}
