package org.gramlab.core.umlv.unitex.frames;

import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public abstract class MenuAdapter implements MenuListener {

	/**
     * {@inheritDoc}
     */
	@Override
	public void menuSelected(MenuEvent e) {
		// do nothing if not overridden
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void menuDeselected(MenuEvent e) {
		// do nothing if not overridden
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void menuCanceled(MenuEvent e) {
		// do nothing if not overridden	
	}

}
