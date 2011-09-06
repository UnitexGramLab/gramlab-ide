package fr.gramlab.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import fr.gramlab.config.GramlabConfigManager;


public class ProjectManager {
	
	private static ArrayList<ProjectListener> projectListeners=new ArrayList<ProjectListener>();
	private static boolean firingProject=false;
	
	private static ArrayList<Project> projects=new ArrayList<Project>();
	
	
	public static void addProjectListener(ProjectListener l) {
		projectListeners.add(l);
	}
	
	public static void removeProjectListener(ProjectListener l) {
		if (firingProject) {
			throw new IllegalStateException("Cannot remove a listener while firing");
		}
		projectListeners.remove(l);
	}
	
	protected static void fireProjectAdded(Project p,int pos) {
		firingProject=true;
		try {
			for (ProjectListener l:projectListeners) {
				l.projectAdded(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}

	protected static void fireProjectRemoved(Project p,int pos) {
		firingProject=true;
		try {
			for (ProjectListener l:projectListeners) {
				l.projectRemoved(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}

	protected static void fireProjectOpened(Project p,int pos) {
		firingProject=true;
		try {
			for (ProjectListener l:projectListeners) {
				l.projectOpened(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}

	protected static void fireProjectClosed(Project p,int pos) {
		firingProject=true;
		try {
			for (ProjectListener l:projectListeners) {
				l.projectClosed(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}

	
	public static void addProject(Project p) {
		if (projects.contains(p)) return;
		projects.add(p);
		Collections.sort(projects);
		int pos=projects.indexOf(p);
		fireProjectAdded(p,pos);
	}

	public static void openProject(Project p) {
		int pos=projects.indexOf(p);
		if (pos==-1) return;
		p.open();
		fireProjectOpened(p,pos);
	}

	public static void closeProject(Project p) {
		int pos=projects.indexOf(p);
		if (pos==-1) return;
		p.close();
		fireProjectClosed(p,pos);
	}

	public static void removeProject(Project p) {
		int pos=projects.indexOf(p);
		if (pos==-1) return;
		projects.remove(p);
		fireProjectRemoved(p,pos);
	}
	
	public static boolean isProjectDirectory(File f) {
		if (!f.exists() || !f.isDirectory() 
				|| !f.getParentFile().equals(GramlabConfigManager.getWorkspaceDirectory())) return false;
		Project p=new Project(f.getName());
		return p.getConfigFile().exists();
	}

	public static ArrayList<Project> getProjects() {
		return projects;
	}

	
	public static void loadProjects() {
		File[] files=GramlabConfigManager.getWorkspaceDirectory().listFiles();
		for (File f:files) {
			if (isProjectDirectory(f)) {
				addProject(new Project(f.getName()));
			}
		}
	}
}
