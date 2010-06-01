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

package fr.umlv.unitex.process;

/**
 * This interface is used to define a method named <code>toDo()</code>. It is used to
 * define what must be done after a sequence of command lines in the 
 * <code>ProcessInfoFrame.launchCommand()</code> method: after all processes have been completed, 
 * a <code>toDo()</code> method is called to execute some actions.
 * 
 * All classes or methods that create <code>ProcessInfoFrame</code> objects should create
 * their own object, extending the <code>DoAbstract</code> class. 
 *  
 * @author Sébastien Paumier
 *
 */
public interface ToDo {
   public void toDo();
}