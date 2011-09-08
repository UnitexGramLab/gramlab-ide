package fr.gramlab.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import fr.gramlab.config.GramlabConfigManager;


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
			for (ProjectListener l:projectListeners) {
				l.projectAdded(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}

	protected void fireProjectRemoved(Project p,int pos) {
		firingProject=true;
		try {
			for (ProjectListener l:projectListeners) {
				l.projectRemoved(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}

	protected void fireProjectOpened(Project p,int pos) {
		firingProject=true;
		try {
			for (ProjectListener l:projectListeners) {
				l.projectOpened(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}

	protected void fireProjectClosed(Project p,int pos) {
		firingProject=true;
		try {
			for (ProjectListener l:projectListeners) {
				l.projectClosed(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}

	protected void fireCurrentProjectChanged(Project p,int pos) {
		firingProject=true;
		try {
			for (ProjectListener l:projectListeners) {
				l.currentProjectChanged(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}
	
	public void addProject(Project p) {
		if (p==null) {
			throw new IllegalArgumentException("Cannot add a null Project");
		}
		if (projects.contains(p)) return;
		projects.add(p);
		Collections.sort(projects);
		int pos=projects.indexOf(p);
		fireProjectAdded(p,pos);
	}

	public void openProject(Project p) {
		int pos=projects.indexOf(p);
		if (pos==-1) return;
		p.open();
		fireProjectOpened(p,pos);
		if (currentProject==null) {
			setCurrentProject(p);
		}
	}

	public void closeProject(Project p) {
		int pos=projects.indexOf(p);
		if (pos==-1) return;
		p.close();
		fireProjectClosed(p,pos);
		if (currentProject==p) {
			setCurrentProject(null);
		}
	}

	public void removeProject(Project p) {
		int pos=projects.indexOf(p);
		if (pos==-1) return;
		projects.remove(p);
		fireProjectRemoved(p,pos);
	}
	
	public boolean isProjectDirectory(File f) {
		if (!f.exists() || !f.isDirectory() 
				|| !f.getParentFile().equals(GramlabConfigManager.getWorkspaceDirectory())) return false;
		Project p=new Project(f.getName());
		return p.getConfigFile().exists();
	}

	public ArrayList<Project> getProjects() {
		return projects;
	}

	
	public void loadProjects() {
		File[] files=GramlabConfigManager.getWorkspaceDirectory().listFiles();
		for (File f:files) {
			if (isProjectDirectory(f)) {
				addProject(new Project(f.getName()));
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
		String fullname=f.getAbsolutePath();
		for (Project p:projects) {
			if (fullname.startsWith(p.getDirectory().getAbsolutePath())) {
				return p;
			}
		}
		return null;
	}

	public void setCurrentProject(Project p) {
		if (p!=null && !projects.contains(p)) {
			throw new IllegalArgumentException("The current project cannot be outside the workspace");
		}
		currentProject = p;
		fireCurrentProjectChanged(p,(p==null)?-1:projects.indexOf(p));
	}

	public Project getCurrentProject() {
		return currentProject;
	}
}
