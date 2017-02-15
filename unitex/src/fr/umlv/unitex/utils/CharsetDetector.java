package fr.umlv.unitex.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.mozilla.universalchardet.UniversalDetector;

public class CharsetDetector {

	public static String detect(File file) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(file);
		UniversalDetector detector = new UniversalDetector(null);

		byte[] buffer = new byte[4096];
		int bytesRead;

		while ((bytesRead = fileInputStream.read(buffer)) > 0 && !detector.isDone()) {
			detector.handleData(buffer, 0, bytesRead);
		}

		detector.dataEnd();
		fileInputStream.close();

		String encoding = detector.getDetectedCharset();
		if (encoding == null) {
			// default to UTF8
			encoding = "UTF8";
		}

		return encoding;
	}

}
