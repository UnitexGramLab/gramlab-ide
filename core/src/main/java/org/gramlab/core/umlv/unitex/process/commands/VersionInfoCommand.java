/*
 * Unitex
 *
 * Copyright (C) 2001-2016 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package org.gramlab.core.umlv.unitex.process.commands;

import java.io.File;

import org.gramlab.core.umlv.unitex.config.Config;

/**
 * @author martinec
 */
public class VersionInfoCommand extends CommandBuilder {
  public VersionInfoCommand() {
    super("VersionInfo");
  }

  public VersionInfoCommand getCopyright() {
    element("--copyright");
    return this;
  }

  public VersionInfoCommand getVersion() {
    element("--version");
    return this;
  }

  public VersionInfoCommand getRevision() {
    element("--revision");
    return this;
  }

  public VersionInfoCommand getPlatform() {
    element("--platform");
    return this;
  }

  public VersionInfoCommand getCompiler() {
    element("--compiler");
    return this;
  }

  public VersionInfoCommand getJsonver() {
    element("--json");
    return this;
  }

  public VersionInfoCommand getXmlver() {
    element("--xml");
    return this;
  }

  public VersionInfoCommand getSemver() {
    element("--semver");
    return this;
  }
  
  public VersionInfoCommand output(File o) {
    protectElement("--output=" + o.getAbsolutePath());
    return this;
  }
}
