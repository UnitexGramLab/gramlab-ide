package fr.gramlab.frames;

import java.awt.GridLayout;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fr.gramlab.project.Project;
import fr.gramlab.project.ProjectManager;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.frames.InternalFrameManager;

public class ProjectFrameManager extends InternalFrameManager {

	public ProjectFrameManager() {
		super(null);
	}
	
	/**
	 * Here is the algorithm used to select the frame manager to use:
	 * 
	 * a) resource is null: use the current project's frame manager, or
	 *    null if there is no current project
	 * b) resource is not null: if resource can be related to a project,
	 *    we use this project's frame manager; otherwise, the current 
	 *    frame manager is used iff the resource is a .grf file. 
	 *    null is returned if there is no current project or if the file
	 *    is not a .grf file
	 */
    @Override
	public GramlabInternalFrameManager getSubManager(File resource,boolean weShallOpenTheProject) {
    	if (resource==null) {
    		Project p=ProjectManager.getManager().getCurrentProject();
    		if (p==null) {
    			return null;
    		}
    		return p.getFrameManager();
    	}
    	Project p=ProjectManager.getManager().getProject(resource);
    	if (p==null) {
    		p=ProjectManager.getManager().getCurrentProject();
    		if (p==null) return null;
    		if (FileUtil.getExtensionInLowerCase(resource).equals("grf")) {
    			JOptionPane.showMessageDialog(null, "Graph "
					+ resource.getAbsolutePath() + " does not belong\n"
					+"to the current project. You may not be able to use all the edition features.",
					"Warning", JOptionPane.WARNING_MESSAGE);
        		return p.getFrameManager();
    		} 
    		if (FileUtil.getExtensionInLowerCase(resource).equals("dic")) {
    			JOptionPane.showMessageDialog(null, "Dictionary "
					+ resource.getAbsolutePath() + " does not belong\n"
					+"to the current project. Some operations may fail.",
					"Warning", JOptionPane.WARNING_MESSAGE);
        		return p.getFrameManager();
    		} 
    		return null;
    	}
    	if (!p.isOpen() && weShallOpenTheProject) {
    		JPanel tmp=new JPanel(new GridLayout(4,1));
    		tmp.add(new JLabel("The file "+resource.getAbsolutePath()));
    		tmp.add(new JLabel("is associated to project "+p.getName()+" that is not currently open."));
    		tmp.add(new JLabel(""));
    		tmp.add(new JLabel("Do you want to open it now ?"));
    		if (JOptionPane.showConfirmDialog(null,tmp, "",
    				JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE)==0) {
    			ProjectManager.getManager().openProject(p);
    		} else return null;
    	}
    	return p.getFrameManager();
    }
    
    
}
