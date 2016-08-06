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
package org.gramlab.core.umlv.unitex.cassys;

public class ConfigurationFileAnalyser {
	private String fileName;
	private boolean mergeMode;
	private boolean replaceMode;
	private boolean disabled;
	private boolean star;
	private boolean commentFound;
        private boolean generic;
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return the mergeMode
	 */
	public boolean isMergeMode() {
		return mergeMode;
	}

	/**
	 * @return the replaceMode
	 */
	public boolean isReplaceMode() {
		return replaceMode;
	}

	/**
	 * @return the commentFound
	 */
	public boolean isCommentFound() {
		return commentFound;
	}
        
	/**
         * @return is graph generic or not
         */
        public boolean isGeneric() {
		return generic;
	}
	
	/**
	 * 
	 * @return disabled
	 */
	public boolean isDisabled(){
		return disabled;
	}
	
	public boolean isStar(){
		return star;
	}
	

	public ConfigurationFileAnalyser(String line) throws EmptyLineException,
			InvalidLineException {
		// extract line comment
		final String lineSubstring[] = line.split("#", 2);
		if (lineSubstring.length > 1) {
			commentFound = true;
		}
		if (lineSubstring[0] == null || lineSubstring[0].equals("")) {
			throw new EmptyLineException();
		}
		final String lineCore[] = lineSubstring[0].split(" ");
		if (!lineCore[0].startsWith("\"") || !lineCore[0].endsWith("\"")) {
			throw new InvalidLineException(lineSubstring[0]
					+ " --> FileName must start and end with quote\n");
		}
		lineCore[0] = lineCore[0].substring(1, lineCore[0].length() - 1);
		if (lineCore.length > 1) {
			fileName = lineCore[0];
			if (lineCore[1].equals("M") || lineCore[1].equals("Merge")
					|| lineCore[1].equals("merge")) {
				mergeMode = true;
				replaceMode = false;
			} else if (lineCore[1].equals("R") || lineCore[1].equals("Replace")
					|| lineCore[1].equals("replace")) {
				mergeMode = false;
				replaceMode = true;
			} else {
				throw new InvalidLineException(lineSubstring[0]
						+ " --> Second argument should be Merge or Replace\n");
			}
		} else {
			throw new InvalidLineException(
					lineSubstring[0]
							+ " --> FileName should be followed by a white space and Merge or Replace\n");
		}
		if(lineCore.length>2){
			if(lineCore[2].equals("Disabled")||lineCore[2].equals("Disabled")){
				disabled = true;
			} else if(lineCore[2].equals("Enabled")){
				disabled = false;
			} else {
				throw new InvalidLineException(lineSubstring[0]
				                     						+ " --> Third argument should be Disabled or Enabled\n");
			}
			if(lineCore.length>3){
				if(lineCore[3].equals("*")){
					star = true;
				} else if(lineCore[3].equals("1")){
					star = false;
				} else {
					throw new InvalidLineException(lineSubstring[0]
						                     						+ " --> Fourth argument should be 1 or *\n");
				}
                                if(lineCore.length>4){
                                    if(lineCore[4].equals("@")){
                                        generic = true;
                                        mergeMode = true;
                                        replaceMode = false;
                                    }
                                    else {
                                        generic = false;
                                    }
                                }
                                else {
                                    generic = false;
                                }
			} else {
				star = false;
			}
		} else {
			disabled = false;
			star = false;
		}
	}

	public class InvalidLineException extends Exception {
		public InvalidLineException(String s) {
			super(s);
		}

		public InvalidLineException() {
			/* NOP */
		}
	}

	public class EmptyLineException extends Exception {
		public EmptyLineException() {
			/* NOP */
		}
	}
}
