package fr.umlv.unitex.config;

import java.util.regex.Pattern;

public class InjectedVariable {
	
	private String name;
	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	private String value;
	
	public InjectedVariable(String n,String value) {
		if (!isValidName(n)) {
			throw new IllegalArgumentException("Invalid variable name: "+n);
		}
		this.name=n;
		this.value=value;
	}
	
	public static boolean isValidName(String s) {
		return Pattern.matches("^[a-zA-Z0-9_]+$",s);
	}
	
	public static boolean isValidValue(String s) {
		return Pattern.matches("^\\p{ASCII}*$",s);
	}

}
