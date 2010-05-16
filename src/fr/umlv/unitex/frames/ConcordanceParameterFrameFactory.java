/*
 * Unitex
 *
 * Copyright (C) 2001-2010 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 *
 */

package fr.umlv.unitex.frames;

import fr.umlv.unitex.Config;


public class ConcordanceParameterFrameFactory {

	private ConcordanceParameterFrame frame;
	
	/**
	 * Shows the frame. If the number of matches is bigger than a certain limit,
	 * an option is automatically selected to show the concordance with a web
	 * navigator.
	 * 
	 * @param matches
	 *            the number of matches
	 */
	ConcordanceParameterFrame newConcordanceParameterFrame(int matches) {
		if (frame==null) {
			frame=new ConcordanceParameterFrame();
		}
		frame.numberOfMatches = null;
		frame.updateDiffButton();
		/* TODO vérifier la limite sur les matches */
		if (matches >= Config.MAXIMUM_UTTERANCE_NUMBER_TO_DISPLAY_WITH_JAVA) {
			frame.checkBox.setSelected(true);
		}
		else {
			frame.checkBox.setSelected(frame.useWebBrowser);
		}
		return frame;
	}
	
	
	void closeConcordanceParameterFrame() {
		if (frame==null) {
			return;
		}
		frame.doDefaultCloseAction();
	}

}
