package fr.gramlab.project;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import fr.gramlab.GramlabConfigManager;
import fr.umlv.unitex.common.project.manager.ProjectManager;
import fr.umlv.unitex.files.FileUtil;


public class GramlabProjectManager implements ProjectManager {
	
	private ArrayList<ProjectListener> projectListeners=new ArrayList<ProjectListener>();
	private boolean firingProject=false;
	
	private ArrayList<GramlabProject> projects=new ArrayList<GramlabProject>();
	
	private GramlabProject currentProject=null;
	
	
	public void addProjectListener(ProjectListener l) {
		projectListeners.add(l);
	}
	
	public void removeProjectListener(ProjectListener l) {
		if (firingProject) {
			throw new IllegalStateException("Cannot remove a listener while firing");
		}
		projectListeners.remove(l);
	}
	
	protected void fireProjectAdded(GramlabProject p,int pos) {
		firingProject=true;
		try {
			for (int i=0;i<projectListeners.size();i++) {
				projectListeners.get(i).projectAdded(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}

	protected void fireProjectRemoved(GramlabProject p,int pos) {
		firingProject=true;
		try {
			for (int i=0;i<projectListeners.size();i++) {
				projectListeners.get(i).projectRemoved(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}

	protected void fireProjectOpened(GramlabProject p,int pos) {
		firingProject=true;
		try {
			for (int i=0;i<projectListeners.size();i++) {
				projectListeners.get(i).projectOpened(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}

	protected void fireProjectSvnModified(GramlabProject p,int pos) {
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
	protected void fireProjectClosing(GramlabProject p,int pos,boolean[] canClose) {
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

	protected void fireProjectClosed(GramlabProject p,int pos) {
		firingProject=true;
		try {
			for (int i=0;i<projectListeners.size();i++) {
				projectListeners.get(i).projectClosed(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}

	protected void fireCurrentProjectChanged(GramlabProject p,int pos) {
		firingProject=true;
		try {
			for (int i=0;i<projectListeners.size();i++) {
				projectListeners.get(i).currentProjectChanged(p,pos);
			}
		} finally {
			firingProject=false;
		}
	}
	
	public void addProject(GramlabProject p) {
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

	public void openProject(GramlabProject p) {
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
	public boolean closeProject(GramlabProject p) {
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

	
	public void notifyProjectSvnChange(GramlabProject p) {
		if (p==null) {
			throw new IllegalArgumentException("Invalid null Project");
		}
		synchronized (p) {
			int pos=projects.indexOf(p);
			if (pos==-1) return;
			fireProjectSvnModified(p,pos);
		}
	}

	
	private void removeProject(GramlabProject p) {
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
	
	public boolean deleteProject(GramlabProject p,boolean askConfirmation) {
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
		GramlabProject p=new GramlabProject(f.getName(),null,null,null);
		return p.getPom().getFile().exists();
	}

	public ArrayList<GramlabProject> getProjects() {
		return projects;
	}

	public void loadProjects() {
		File[] files=GramlabConfigManager.getWorkspaceDirectory().listFiles();
		if (files==null) {
			return;
		}
		for (File f:files) {
			if (isProjectDirectory(f)) {
				addProject(new GramlabProject(f.getName(),null,null,null));
			}
		}
		for (String s:GramlabConfigManager.getPreviousOpenProjects()) {
			GramlabProject p=getProject(s);
			if (p!=null) {
				openProject(p);
			}
		}
		String s=GramlabConfigManager.getPreviousCurrentProject();
		if (s!=null) {
			GramlabProject p=getProject(s);
			if (p!=null) {
				openProject(p);
				setCurrentProject(p);
			}
		}
	}

	/**
	 * Looks for a project whose name is projectName
	 * @param projectName
	 * @return
	 */
	public GramlabProject getProject(String projectName) {
		if (projectName==null) {
			return getCurrentProject();
		}
		for (GramlabProject p:projects) {
			if (p.getName().equals(projectName)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Looks for a project whose directory contains the given file
	 */
	public GramlabProject getProject(File f) {
		for (GramlabProject p:projects) {
			if (null!=FileUtil.isAncestor(p.getProjectDirectory(),f)) {
				return p;
			}
		}
		return null;
	}

	public void setCurrentProject(GramlabProject p) {
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

	public GramlabProject getCurrentProject() {
		return currentProject;
	}

	public void changeWorkspace(File dir) {
		removeAllProjects();
		GramlabConfigManager.setWorkspaceDirectory(dir,true);
		loadProjects();
	}

	private void removeAllProjects() {
		for (int i=projects.size()-1;i>=0;i--) {
			GramlabProject p=projects.get(i);
			closeProject(p);
			removeProject(p);
		}
		
	}
	
	public boolean closeAllProjects() {
		for (int i=projects.size()-1;i>=0;i--) {
			GramlabProject p=projects.get(i);
			if (p.isOpen()) {
				if (!closeProject(p)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public GramlabProject search(File resource) {
		return search(resource, true);
	}
	
	//TODO Add support for weShallOpenTheProject
	@Override
	public GramlabProject search(File resource, boolean weShallOpenTheProject) {
		if (resource==null) {
			GramlabProject p=currentProject;
			if (p==null) {
				return null;
			}
			return p;
		}
		GramlabProject p=getProject(resource);
		if (p==null) {
			p=currentProject;
			if (p==null) return null;
			if (FileUtil.getExtensionInLowerCase(resource).equals("grf")) {
				JOptionPane.showMessageDialog(null, "Graph "
					+ resource.getAbsolutePath() + " does not belong\n"
					+"to the current project. You may not be able to use all the edition features.",
					"Warning", JOptionPane.WARNING_MESSAGE);
				return p;
			}
			if (FileUtil.getExtensionInLowerCase(resource).equals("dic")) {
				JOptionPane.showMessageDialog(null, "Dictionary "
					+ resource.getAbsolutePath() + " does not belong\n"
					+"to the current project. Some operations may fail.",
					"Warning", JOptionPane.WARNING_MESSAGE);
				return p;
			}
			return null;
		}
		if(!p.isOpen() && weShallOpenTheProject) {
			JPanel tmp=new JPanel(new GridLayout(4,1));
    		tmp.add(new JLabel("The file "+resource.getAbsolutePath()));
    		tmp.add(new JLabel("is associated to project "+p.getName()+" that is not currently open."));
    		tmp.add(new JLabel(""));
    		tmp.add(new JLabel("Do you want to open it now ?"));
    		if (JOptionPane.showConfirmDialog(null,tmp, "",
    				JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE)==0) {
    			this.openProject(p);
    		} else return null;
		}
		return p;
	}
}
