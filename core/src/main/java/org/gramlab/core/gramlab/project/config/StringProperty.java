package org.gramlab.core.gramlab.project.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class StringProperty implements ProjectCoreProperty<String> {

	private String name,value;
	
	public StringProperty(String name,String value) {
		this.name=name;
		this.value=value;
	}
	
	@Override
	public String load(BufferedReader reader) throws IOException {
		String line=reader.readLine();
		if (line==null) {
			throw new IOException("Unexpected end of file");
		}
		Scanner scanner=new Scanner(line);
		scanner.useDelimiter("=");
		try {
			String n=scanner.next();
			if (!name.equals(n)) throw new IOException("Found '"+n+"' when expecting property named '"+name+"'");
			if (!scanner.hasNextLine()) return "";
			String res=scanner.nextLine();
			if (res.startsWith("=")) {
				res=res.substring(1);
			}
			return res;
		} catch (Exception e) {
			throw new IOException();
		}
	}

	@Override
	public void save(OutputStreamWriter s) throws IOException {
		s.write(name+"="+value+"\n");
	}
	
}
