package org.gramlab.core.umlv.unitex.common.project;

import org.gramlab.core.umlv.unitex.common.frames.manager.FrameManager;

/**
 * 
 * This interface provides a common way to represent projects in Unitex and
 * GramLab.
 * 
 * Every Project should contain a FrameManager.
 * 
 * @author mdamis
 *
 */
public interface Project {
	/**
	 * Casts the frameManager of the Project to the specified type.
	 * 
	 * This method uses the Type Token Pattern.
	 * 
	 * @param type
	 *            the type used for the cast of the frameManager.
	 * @return the casted frameManager.
	 */
	<M extends FrameManager> M getFrameManagerAs(Class<M> type);
}
