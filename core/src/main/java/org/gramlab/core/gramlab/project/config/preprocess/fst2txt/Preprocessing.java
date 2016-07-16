package org.gramlab.core.gramlab.project.config.preprocess.fst2txt;

import java.io.File;
import java.util.ArrayList;

import org.gramlab.core.gramlab.project.GramlabProject;
import org.gramlab.core.umlv.unitex.process.commands.MkdirCommand;
import org.gramlab.core.umlv.unitex.process.commands.MultiCommands;


/**
 * This class manages all the preprocessing steps of a project
 * 
 * @author paumier
 *
 */
public class Preprocessing {
	
	private ArrayList<PreprocessingStep> steps=new ArrayList<PreprocessingStep>();
	
	
	public void setPreprocessingSteps(ArrayList<PreprocessingStep> list) {
		steps.clear();
		int n=list.size();
		for (int i=0;i<n;i++) {
			steps.add(list.get(i));
		}
	}
	
	public MultiCommands getDeployCommands(GramlabProject p) {
		if (steps.size()==0) return null;
		MultiCommands c=new MultiCommands();
		for (PreprocessingStep s:steps) {
			if (!s.isSelected()) continue;
			/* We  must ensure to create the directories required by the output .fst2 */
			if (!s.getDestFst2().getParentFile().exists()) {
				c.addCommand(new MkdirCommand().name(s.getDestFst2().getParentFile()));
			}
			c.addCommand(s.getDeployCommand(p));
		}
		return c;
	}
	
	public MultiCommands getPreprocessCommands(GramlabProject p,File text) {
		if (steps.size()==0) return null;
		MultiCommands c=new MultiCommands();
		for (PreprocessingStep s:steps) {
			if (!s.isSelected()) continue;
			c.addCommand(s.getPreprocessCommand(p,text));
		}
		return c;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Preprocessing clone() {
		Preprocessing p=new Preprocessing();
		p.steps=(ArrayList<PreprocessingStep>) steps.clone();
		return p;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<PreprocessingStep> getPreprocessingSteps() {
		return (ArrayList<PreprocessingStep>) steps.clone();
	}

}
