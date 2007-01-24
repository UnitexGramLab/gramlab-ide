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

import java.io.*;
import java.util.*;

import fr.umlv.unitex.conversion.*;
import fr.umlv.unitex.exceptions.*;
/**
 * @author Sébastien Paumier
 *  
 */
public class ConvertCommand extends CommandBuilder {

	public ConvertCommand() {
		super("Convert");
	}

    public ConvertCommand(ArrayList l) {
        super(l);
    }

    
	public ConvertCommand src(String s) throws InvalidSourceEncodingException {
		if (ConversionFrame.validSrcEncoding(s)) {
			element(s);
			return this;
		}
		throw new InvalidSourceEncodingException();
	}

	public ConvertCommand dest(String s)
			throws InvalidDestinationEncodingException {
		if (ConversionFrame.validDestEncoding(s)) {
			element(s);
			return this;
		}
		throw new InvalidDestinationEncodingException();
	}

	public ConvertCommand replace() {
		element("-r");
		return this;
	}

	public ConvertCommand rename(boolean addPrefix, boolean renameSource,
			String s) {
		String element = "-" + (addPrefix ? "p" : "s")
				+ (renameSource ? "s" : "d") + "=" + s;
		protectElement(element);
		return this;
	}

	public ConvertCommand renameSourceWithPrefix(String s) {
		String element = "-ps=" + s;
		protectElement(element);
		return this;
	}

	public ConvertCommand renameSourceWithSuffix(String s) {
		String element = "-ss=" + s;
		protectElement(element);
		return this;
	}

	public ConvertCommand renameDestWithPrefix(String s) {
		String element = "-pd=" + s;
		protectElement(element);
		return this;
	}

	public ConvertCommand renameDestWithSuffix(String s) {
		String element = "-sd=" + s;
		protectElement(element);
		return this;
	}

	public ConvertCommand file(File s) {
		protectElement(s.getAbsolutePath());
		return this;
	}

    public ConvertCommand copy() {
        return new ConvertCommand(getCopyOfList());
    }

}
