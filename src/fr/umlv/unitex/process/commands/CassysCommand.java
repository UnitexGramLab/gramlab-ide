package fr.umlv.unitex.process.commands;

import java.io.File;

public class CassysCommand extends CommandBuilder {
	
	public CassysCommand(){
		super("Cassys");
	}
	
	public CassysCommand alphabet(File s){
		if (s!=null) protectElement("-a"+s.getAbsolutePath());
		return this;
	}
	
	public CassysCommand targetText(File s){
		protectElement("-t"+s.getAbsolutePath());
		return this;
	}
	
	public CassysCommand transducerList(File s){
		protectElement("-l"+s.getAbsolutePath());
		return this;
	}
}
