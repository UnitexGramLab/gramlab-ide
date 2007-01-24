 /*
  * Unitex
  *
  * Copyright (C) 2001-2007 Université de Marne-la-Vallée <unitex@univ-mlv.fr>
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

import java.io.File;

/**
 * @author Sébastien Paumier
 *  
 */
public class InflectKrCommand extends CommandBuilder {

    public InflectKrCommand() {
        super("InflectKr");
    }

    public InflectKrCommand delas(File s) {
        protectElement(s.getAbsolutePath());
        return this;
    }

    public InflectKrCommand result(File s) {
      element("-o");
      protectElement(s.getAbsolutePath());
      return this;
    }

    public InflectKrCommand dir(File s) {
      protectElement(s.getAbsolutePath());
      return this;
    }
    public InflectKrCommand dirDerivation(File s) {
        element("-d");
        protectElement(s.getAbsolutePath());
        return this;
    }
    public InflectKrCommand dirVariation(File s) {
        element("-v");
        protectElement(s.getAbsolutePath());
        return this;
    }
    public InflectKrCommand convTable(File s) {
        element("-x");
        protectElement(s.getAbsolutePath());
        return this;
    }
    public InflectKrCommand racineContent() {
        element("-r");
        return this;
    }
    public InflectKrCommand suffixContent() {
        element("-s");
        return this;
    }
    public InflectKrCommand uneOption(String s) {
        element(s);
        return this;
    }

}