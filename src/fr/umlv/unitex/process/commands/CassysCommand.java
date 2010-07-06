package fr.umlv.unitex.process.commands;

import java.io.File;

public class CassysCommand extends CommandBuilder {
	
	public CassysCommand(){
		super("Cassys");
	}
	
	public CassysCommand alphabet(File s){
		protectElement("-a"+s.getAbsolutePath());
		return this;
	}
	
	public CassysCommand targetText(File s){
		protectElement("-f"+s.getAbsolutePath());
		return this;
	}
	
	public CassysCommand transducerList(File s){
		protectElement("-t"+s.getAbsolutePath());
		return this;
	}
}
