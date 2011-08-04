/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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

package fr.umlv.unitex.process.commands;

import fr.umlv.unitex.config.Config;

public abstract class AbstractMethodCommand extends CommandBuilder {

    AbstractMethodCommand() {
        type = CommandBuilder.METHOD;
    }

    AbstractMethodCommand(String s) {
        /* We don't call super(s), because we want to see 'mkdir'
           * and not '.../Unitex/App/mkdir' */
        element(s);
        type = CommandBuilder.METHOD;
    }

    /**
     * The method to invoke to do the job.
     *
     * @return false if an error occurred; true otherwise
     */
    public abstract boolean execute();
    
}
