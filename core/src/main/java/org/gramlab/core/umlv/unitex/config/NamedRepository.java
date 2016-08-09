package org.gramlab.core.umlv.unitex.config;

import java.io.File;
import java.util.regex.Pattern;

public class NamedRepository {
	private final String name;

	public String getName() {
		return name;
	}

	public File getFile() {
		return file;
	}

	private final File file;

	public NamedRepository(String n, File f) {
		if (!isValidName(n)) {
			throw new IllegalArgumentException("Invalid repository name: " + n);
		}
		this.name = n;
		this.file = f;
	}

	public static boolean isValidName(String s) {
		return Pattern.matches("^[a-zA-Z0-9_]+$", s);
	}

}
