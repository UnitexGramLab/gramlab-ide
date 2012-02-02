/*
 * Unitex
 *
 * Copyright (C) 2001-2012 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package fr.umlv.unitex.process.list;

import javax.swing.JList;
import javax.swing.ListModel;

import fr.umlv.unitex.console.Couple;

/**
 * This class describes a list to be used to display command outputs.
 * 
 * @author paumier
 *
 */
public class ProcessOutputList extends JList {

	private boolean autoscroll;
	
	/**
	 * If autoscroll is true, then the list will make sure to make every
	 * added element visible. This is deactivated when invoked from
	 * the HelpOnCommandFrame.
	 */
	public ProcessOutputList(ProcessOutputListModel model,boolean autoscroll) {
		super(model);
		this.autoscroll=autoscroll;
	}
	
	public ProcessOutputList(ProcessOutputListModel model) {
		this(model,true);
	}
	
	@Override
	public ProcessOutputListModel getModel() {
		return (ProcessOutputListModel) super.getModel();
	}
	
	@Override
	public void setModel(ListModel model) {
		throw new UnsupportedOperationException("Cannot set model on a ProcessOutputList");
	}
	
	public void addLine(Couple c) {
		ProcessOutputListModel model=getModel();
		model.addLine(c);
		if (autoscroll) {
			ensureIndexIsVisible(model.getSize() - 1);
		}
	}

	public void replaceLastLine(Couple c) {
		ProcessOutputListModel model=getModel();
		model.replaceLastLine(c);
		if (autoscroll) {
			ensureIndexIsVisible(model.getSize() - 1);
		}
	}

	public void empty() {
		getModel().removeAllElements();
	}
	
}
