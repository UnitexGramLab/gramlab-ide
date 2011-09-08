package fr.gramlab.frames;

import java.awt.GridLayout;
import java.io.File;

import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fr.gramlab.workspace.Project;
import fr.gramlab.workspace.ProjectManager;
import fr.umlv.unitex.frames.InternalFrameManager;

public class ProjectFrameManager extends InternalFrameManager {

	private static InternalFrameManager defaultManager=new InternalFrameManager(new JDesktopPane());
	
	public ProjectFrameManager() {
		super(null);
	}
	
	/**
	 * Here is the algorithm used to select the frame manager to use:
	 * 
	 * a) resource is null: use the current project's frame manager, or
	 *    the default one if there is no current project
	 * b) resource is not null: if resource can be related to a project,
	 *    we use this project's frame manager; otherwise, the default 
	 *    frame manager is used.
	 */
    @Override
	public InternalFrameManager getSubManager(File resource) {
    	if (resource==null) {
    		Project p=ProjectManager.getManager().getCurrentProject();
    		if (p==null) return defaultManager;
    		return p.getFrameManager();
    	}
    	Project p=ProjectManager.getManager().getProject(resource);
    	if (p==null) return defaultManager;
    	if (!p.isOpen()) {
    		JPanel tmp=new JPanel(new GridLayout(3,1));
    		tmp.add(new JLabel("The file "+resource.getAbsolutePath()));
    		tmp.add(new JLabel("is associated to project "+p.getName()+" that is not currently open."));
    		tmp.add(new JLabel(""));
    		tmp.add(new JLabel("Do you want to open it now ?"));
    		if (JOptionPane.showConfirmDialog(null,tmp, "",
    				JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE)==0) {
    			ProjectManager.getManager().openProject(p);
    		} else return defaultManager;
    	}
    	return p.getFrameManager();
    }
    
    
    public static InternalFrameManager getDefaultManager() {
    	return defaultManager;
    }
	
}
