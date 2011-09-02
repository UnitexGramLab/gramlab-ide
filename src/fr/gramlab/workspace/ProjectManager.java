package fr.gramlab.workspace;

import java.util.ArrayList;

import fr.gramlab.listeners.ProjectListener;

public class ProjectManager {
	
	private static ArrayList<ProjectListener> projectListeners=new ArrayList<ProjectListener>();
	private static boolean firingProject=false;
	public static void addProjectListener(ProjectListener l) {
		projectListeners.add(l);
	}
	
	public static void removeProjectListener(ProjectListener l) {
		if (firingProject) {
			throw new IllegalStateException("Cannot remove a listener while firing");
		}
		projectListeners.remove(l);
	}
	
	protected static void fireProjectAdded(Project p) {
		firingProject=true;
		try {
			for (ProjectListener l:projectListeners) {
				l.projectAdded(p);
			}
		} finally {
			firingProject=false;
		}
	}

	protected static void fireProjectRemoved(Project p) {
		firingProject=true;
		try {
			for (ProjectListener l:projectListeners) {
				l.projectRemoved(p);
			}
		} finally {
			firingProject=false;
		}
	}

	protected static void fireProjectOpened(Project p) {
		firingProject=true;
		try {
			for (ProjectListener l:projectListeners) {
				l.projectOpened(p);
			}
		} finally {
			firingProject=false;
		}
	}

	protected static void fireProjectClosed(Project p) {
		firingProject=true;
		try {
			for (ProjectListener l:projectListeners) {
				l.projectClosed(p);
			}
		} finally {
			firingProject=false;
		}
	}

	public static void addProject(Project p) {
		fireProjectAdded(p);
	}

}
