package fr.umlv.unitex.project;

import fr.umlv.unitex.common.project.Project;
import fr.umlv.unitex.frames.UnitexInternalFrameManager;
import fr.umlv.unitex.common.frames.manager.FrameManager;

/**
 * 
 * @author mdamis
 *
 */
public class UnitexProject implements Project {
	private UnitexInternalFrameManager frameManager;
	
	public UnitexProject(UnitexInternalFrameManager frameManager) {
		this.frameManager = frameManager;
	}

	public void setFrameManager(UnitexInternalFrameManager frameManager) {
		this.frameManager = frameManager;
	}

	@Override
	public <M extends FrameManager> M getFrameManagerAs(Class<M> type) {
		return type.cast(frameManager);
	}
	
}