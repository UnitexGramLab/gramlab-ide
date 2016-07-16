package org.gramlab.core.umlv.unitex.project;

import org.gramlab.core.umlv.unitex.common.frames.manager.FrameManager;
import org.gramlab.core.umlv.unitex.common.project.Project;
import org.gramlab.core.umlv.unitex.frames.UnitexInternalFrameManager;

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