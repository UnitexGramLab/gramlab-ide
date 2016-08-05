package org.gramlab.core.gramlab.svn;

import java.io.File;
import java.util.ArrayList;

import org.gramlab.core.gramlab.project.GramlabProject;

public class SvnStatusInfo {

	private ArrayList<String> unversionedFiles=new ArrayList<String>();
	private ArrayList<String> addedFiles=new ArrayList<String>();
	private ArrayList<String> modifiedFiles=new ArrayList<String>();
	private ArrayList<String> removedFiles=new ArrayList<String>();
	private ArrayList<String> conflictFiles=new ArrayList<String>();

	private GramlabProject project=null;
	
	public SvnStatusInfo(GramlabProject p) {
		this.project=p;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> getUnversionedFiles() {
		return (ArrayList<String>) unversionedFiles.clone();
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> getAddedFiles() {
		return (ArrayList<String>) addedFiles.clone();
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getModifiedFiles() {
		return (ArrayList<String>) modifiedFiles.clone();
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getRemovedFiles() {
		return (ArrayList<String>) removedFiles.clone();
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getConflictFiles() {
		return (ArrayList<String>) conflictFiles.clone();
	}

	public void addUnversionedFile(String f) {
		unversionedFiles.add(f);
	}
	
	public void addAddedFile(String f) {
		addedFiles.add(f);
	}
	
	public void addModifiedFile(String f) {
		modifiedFiles.add(f);
	}

	public void addRemovedFile(String f) {
		removedFiles.add(f);
	}

	public void addConflictFile(String f) {
		conflictFiles.add(f);
	}

	public void addUnversionedFile(File f) {
		unversionedFiles.add(project.getRelativeFileName(f));
	}
	
	public void addAddedFile(File f) {
		addedFiles.add(project.getRelativeFileName(f));
	}
	
	public void addModifiedFile(File f) {
		modifiedFiles.add(project.getRelativeFileName(f));
	}

	public void addRemovedFile(File f) {
		removedFiles.add(project.getRelativeFileName(f));
	}

	public void addConflictFile(File f) {
		conflictFiles.add(project.getRelativeFileName(f));
	}

	public int getNumberOfCommittableFiles() {
		return unversionedFiles.size()+addedFiles.size()
			+modifiedFiles.size()+removedFiles.size();
	}
	


}
