package fr.gramlab.project.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * This class describes a core property of a project, i.e. a property
 * that is neither a maven thing (which are dealt with by the pom.xml file),
 * nor a rendering thing like a font.
 * 
 * @author paumier
 *
 */
public interface ProjectCoreProperty<T> {
	
	public T load(BufferedReader s) throws IOException;
	
	public void save(OutputStreamWriter s) throws IOException;
	
}
