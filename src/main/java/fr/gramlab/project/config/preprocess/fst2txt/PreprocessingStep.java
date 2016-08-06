package fr.gramlab.project.config.preprocess.fst2txt;

import java.io.File;

import fr.gramlab.project.GramlabProject;
import fr.umlv.unitex.files.FileUtil;
import fr.umlv.unitex.process.commands.CommandBuilder;
import fr.umlv.unitex.process.commands.CpCommand;
import fr.umlv.unitex.process.commands.Fst2TxtCommand;

/**
 * This class describes a preprocessing step, which means
 * a graph name and a mode (merge/replace). destFst2 is the name
 * of the compiled graph to be used. This should be located in the target
 * directory of the project.
 * 
 * If the given graph is a .grf, then it will be compiled to produce destFst2.
 * If it is a .fst2, it will just be copied to destFst2.
 * 
 * NOTE: those objects are immutable
 * 
 * @author paumier
 *
 */
public class PreprocessingStep {
	
	private boolean merge;
	private boolean isGrf;
	private File graph;
	
	private boolean selected;
	
	public boolean isMerge() {
		return merge;
	}

	public File getGraph() {
		return graph;
	}

	public File getDestFst2() {
		return destFst2;
	}

	public boolean isSelected() {
		return selected;
	}
	
	private File destFst2=null;
	
	public PreprocessingStep(File graph,File destFst2,boolean merge,boolean selected) {
		setGraph(graph);
		setDestFst2(destFst2);
		this.merge=merge;
		this.selected=selected;
	}

	private void setGraph(File f) {
		String s=FileUtil.getExtensionInLowerCase(f);
		if (!f.exists() || !f.isFile()) {
			throw new IllegalArgumentException("Expecting a .grf or .fst2 file");
		}
		graph=f;
		if (s.equals("grf")) {
			isGrf=true;
		} else if (s.equals("fst2")) {
			isGrf=false;
		} else {
			throw new IllegalArgumentException("Expecting a .grf or .fst2 file");
		}
	}
	
	
	public void setDestFst2(File f) {
		String s=FileUtil.getExtensionInLowerCase(f);
		if (!s.equals("fst2")) {
			throw new IllegalArgumentException("Expecting a .fst2 file");
		}
		destFst2=f;
	}

	public void setSelected(boolean b) {
		this.selected=b;
	}
	
	public void setMerge(boolean b) {
		this.merge=b;
	}
	
	/**
	 * @return the command to perform to install the .fst2 into the target
	 *         directory, either by compiling the .grf or copying the .fst2
	 */
	public CommandBuilder getDeployCommand(GramlabProject p) {
		if (destFst2==null) {
			throw new IllegalStateException("the dest .fst2 should have set before invoking this method");
		}
		if (isGrf) {
			/* If we have to compile a .grf */
			return p.createGrf2Fst2Command(graph,destFst2,false,true);
			
		}
		return new CpCommand().copy(graph,destFst2);
	}
	
	/**
	 * @return the Fst2Txt command to apply to the text.
	 */
	public CommandBuilder getPreprocessCommand(GramlabProject p,File text) {
		if (destFst2==null) {
			throw new IllegalStateException("the dest .fst2 should have set before invoking this method");
		}
		Fst2TxtCommand c=new Fst2TxtCommand()
				.text(text)
				.fst2(destFst2)
				.alphabet(p.getAlphabet())
				.mode(merge);
		c=c.charByChar(p.isCharByChar());
		c=c.morphologicalUseOfSpace(p.isMorphologicalUseOfSpace());
		p.monitor(c);
		if (isGrf) {
			/* If we have to compile a .grf */
			c.setSrcGrfPath(graph.getParentFile());
			
		}
		return c;
	}

	
	@Override
	protected PreprocessingStep clone() {
		return new PreprocessingStep(graph, destFst2, merge, selected);
	}
}
