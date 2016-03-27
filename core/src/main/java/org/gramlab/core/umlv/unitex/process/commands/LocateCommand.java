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
package fr.umlv.unitex.process.commands;

import java.io.File;
import java.util.ArrayList;

import fr.umlv.unitex.config.InjectedVariable;

/**
 * @author Sébastien Paumier
 * 
 */
public class LocateCommand extends CommandBuilder {
	public LocateCommand() {
		super("Locate");
	}

	public LocateCommand snt(File s) {
		protectElement("-t" + s.getAbsolutePath());
		ultraSimplifiedList.add(s.getName());
		return this;
	}

	private File fst2Path=null;
	
	public LocateCommand fst2(File s) {
		protectElement(s.getAbsolutePath());
		ultraSimplifiedList.add(s.getName());
		fst2Path=s.getParentFile();
		return this;
	}

	public LocateCommand alphabet(File alphabet) {
		if (alphabet != null)
			protectElement("-a" + alphabet.getAbsolutePath());
		return this;
	}

	public LocateCommand shortestMatches() {
		element("-S");
		return this;
	}

	public LocateCommand arabic(File s) {
		element("-u");
		protectElement(s.getAbsolutePath());
		return this;
	}

	public LocateCommand longestMatches() {
		element("-L");
		return this;
	}

	public LocateCommand allMatches() {
		element("-A");
		return this;
	}

	public LocateCommand ignoreOutputs() {
		element("-I");
		return this;
	}

	public LocateCommand mergeOutputs() {
		element("-M");
		return this;
	}

	public LocateCommand replaceWithOutputs() {
		element("-R");
		return this;
	}

	public LocateCommand noLimit() {
		element("--all");
		return this;
	}

	public LocateCommand limit(int n) {
		element("-n" + n);
		return this;
	}

	public LocateCommand charByChar() {
		element("-c");
		return this;
	}

	public LocateCommand morphologicalDic(ArrayList<File> dicList) {
		if (dicList != null && !dicList.isEmpty()) {
			for (final File f : dicList) {
				protectElement("-m" + f.getAbsolutePath());
			}
		}
		return this;
	}

	public LocateCommand enableMorphologicalUseOfSpace() {
		element("-s");
		return this;
	}

	public LocateCommand allowAmbiguousOutputs() {
		element("-b");
		return this;
	}

	public LocateCommand forbidAmbiguousOutputs() {
		element("-z");
		return this;
	}

	public LocateCommand exitOnVariableErrors() {
		element("-X");
		return this;
	}

	public LocateCommand ignoreVariableErrors() {
		element("-Y");
		return this;
	}

	public LocateCommand backtrackOnVariableErrors() {
		element("-Z");
		return this;
	}

	public LocateCommand korean() {
		element("-K");
		return this;
	}

	public LocateCommand time() {
		element("--time");
		return this;
	}

	public LocateCommand maxExplorationSteps(int matches) {
		element("--stack_max=" + matches);
		return this;
	}
	
	public LocateCommand maxMatchesPerSubgraph(int matches) {
		element("--max_matches_per_subgraph=" + matches);
		return this;
	}
	
	public LocateCommand maxMatchesPerToken(int matches) {
		element("--max_matches_at_token_pos=" + matches);
		return this;
	}
	
	public LocateCommand maxErrors(int errors) {
		element("--max_errors=" + errors);
		return this;
	}
	
	public LocateCommand lessTolerant() {
		element("--less_tolerant");
		return this;
	}
	
	public LocateCommand lesserTolerant() {
		element("--lesser_tolerant");
		return this;
	}
	
	public LocateCommand leastTolerant() {
		element("--least_tolerant");
		return this;
	}
	
	public LocateCommand setInjectedVariables(ArrayList<InjectedVariable> vars) {
		if (vars == null)
			return this;
		for (final InjectedVariable v : vars) {
			element("-v");
			protectElement(v.getName() + "=" + v.getValue());
		}
		return this;
	}
	
	public File getFst2Path() {
		return fst2Path;
	}
}
