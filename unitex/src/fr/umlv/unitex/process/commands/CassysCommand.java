package fr.umlv.unitex.process.commands;

import java.io.File;
import java.util.ArrayList;

import fr.umlv.unitex.config.ConfigManager;

public class CassysCommand extends CommandBuilder {
	public CassysCommand() {
		super("Cassys");
	}

	public CassysCommand alphabet(File s) {
		if (s != null)
			protectElement("-a" + s.getAbsolutePath());
		return this;
	}

	public CassysCommand targetText(File s) {
		protectElement("-t" + s.getAbsolutePath());
		return this;
	}

	public CassysCommand transducerList(File s) {
		protectElement("-l" + s.getAbsolutePath());
		return this;
	}
	
	public CassysCommand morphologicalDic(ArrayList<File> dicList) {
		if (dicList != null && !dicList.isEmpty()) {
			for (final File f : dicList) {
				protectElement("-w" + f.getAbsolutePath());
			}
		}
		return this;
	}
	
	public CassysCommand separatorsToSystem() {
	    element("-v");
	    return this;
	  }

	public CassysCommand transducerDir(File s) {
	    if (s != null) {
	      protectElement("-r" + s.getAbsolutePath() + File.separator );
	    } 
	    System.out.println(s.getAbsolutePath());
	    return this;
	  } 
        
        public CassysCommand inputOffset(File s) {
            if (s != null &&s.isFile()) {
                protectElement("--input_offsets=" + s.getAbsolutePath());
            }
            return this;
        }

	public CassysCommand cleanupIntermediateFiles() {
		element("-b");
		return this;
	}

	public CassysCommand maxMatchesPerSubgraph(String maxMatchesSubgraph) {
		protectElement("--locate_argument=--max_matches_per_subgraph=" + maxMatchesSubgraph);
		 return this;
	}
	public CassysCommand maxMatchesPerToken(String maxMatchesToken) {
		protectElement("--locate_argument=--max_matches_at_token_pos=" + maxMatchesToken);
		return this;
	}

}
