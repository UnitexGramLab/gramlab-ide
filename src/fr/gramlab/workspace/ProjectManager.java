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

	
	public void addProject(Project p) {
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
	}

	public void closeProject(Project p) {
		int pos=projects.indexOf(p);
		if (pos==-1) return;
		p.close();
		fireProjectClosed(p,pos);
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
}
