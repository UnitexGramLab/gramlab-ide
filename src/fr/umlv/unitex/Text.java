 /*
  * Unitex
  *
  * Copyright (C) 2001-2007 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
  *
  * This library is free software; you can redistribute it and/or
  * modify it under the terms of the GNU Lesser General Public
  * License as published by the Free Software Foundation; either
  * version 2.1 of the License, or (at your option) any later version.
  *
  * This library is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  * Lesser General Public License for more details.
  * 
  * You should have received a copy of the GNU Lesser General Public
  * License along with this library; if not, write to the Free Software
  * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
  *
  */

package fr.umlv.unitex;

import java.io.*;

import javax.swing.*;

import fr.umlv.unitex.conversion.*;
import fr.umlv.unitex.exceptions.*;
import fr.umlv.unitex.io.*;
import fr.umlv.unitex.process.*;

/**
 * This class provides methods for loading corpora.
 * 
 * @author Sébastien Paumier
 *  
 */
public class Text {

	
	public static void loadCorpus(File name) {
		loadCorpus(name,false);
	}
	/**
	 * Loads a ".txt" or a ".snt" file
	 * 
	 * @param name
	 *            file name
	 */
	public static void loadCorpus(File name,boolean taggedText) {
		TextConversionDo toDo = new TextConversionDo(name,taggedText);
		try {
			if (!UnicodeIO.isAUnicodeLittleEndianFile(name)) {
        ConvertOneFileFrame.reset();
				ConvertCommand res = ConvertOneFileFrame
						.getCommandLineForConversion(name);
				if (res == null) {
					return;
				}
				new ProcessInfoFrame(res, true, toDo);
			} else {
				toDo.toDo();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads a ".txt" file. Asks for preprocessing the text.
	 * 
	 * @param file
	 *            file name
	 */
	static void loadTxt(File file,boolean taggedText) {
    String name=file.getAbsolutePath();
		FileInputStream source;
		if (!file.exists()) {
			JOptionPane.showMessageDialog(null, "Cannot find " + name, "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!file.canRead()) {
			JOptionPane.showMessageDialog(null, "Cannot read " + name, "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (file.length() <= 2) {
			JOptionPane.showMessageDialog(null, name + " is empty", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			source = UnicodeIO.openUnicodeLittleEndianFileInputStream(file);
			source.close();
		} catch (NotAUnicodeLittleEndianFileException e) {
			JOptionPane.showMessageDialog(null, name
					+ " is not a Unicode Little-Endian text", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		} catch (IOException e) {
			// do nothing
		}
		String nomSnt = Util.getFileNameWithoutExtension(name);
		nomSnt = nomSnt + ".snt";
		Config.setCurrentSnt(new File(nomSnt));
		Object[] options = {"Yes", "No"};
		if (0 == JOptionPane.showOptionDialog(UnitexFrame.mainFrame,
				"Do you want to preprocess the text ?", "",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				options, options[0])) {
			preprocessSnt(file, Config.getCurrentSnt(),taggedText);
		} else {
			preprocessLightSnt(file,taggedText);
		}
	}

	private static void preprocessSnt(File name, File snt,boolean taggedText) {
		new PreprocessFrame(name,snt,taggedText);
	}

	private static void preprocessLightSnt(File name,boolean taggedText) {
		File dir = Config.getCurrentSntDir();
		if (!dir.exists()) {
			// if the directory toto_snt does not exist, we create it
			dir.mkdir();
		}
		MultiCommands commands = new MultiCommands();
		// NORMALIZING TEXT...
		NormalizeCommand normalizeCmd = new NormalizeCommand().text(name);
		commands.addCommand(normalizeCmd);
		// TOKENIZING...
		TokenizeCommand tokenizeCmd = new TokenizeCommand().text(
				Config.getCurrentSnt()).alphabet();
		if (Config.getCurrentLanguage().equals("Thai")
				|| Config.getCurrentLanguage().equals("Chinese")) {
			tokenizeCmd = tokenizeCmd.tokenizeCharByChar();
		}
		commands.addCommand(tokenizeCmd);
		UnitexFrame.mainFrame.closeText();
		Text.removeSntFiles();
		new ProcessInfoFrame(commands, true, new TextDo(Config.getCurrentSnt(),taggedText));
	}

	/**
	 * Loads a ".snt" file and all related files (token lists, dictionaries and
	 * text automaton)
	 * 
	 * @param snt
	 *            file name
	 */
	static void loadSnt(File snt,boolean taggedText) {
		UnitexFrame.mainFrame.closeText();
		TextFrame.loadText(snt,taggedText);
		TokensFrame.loadTokens(new File(Config.getCurrentSntDir(),"tok_by_freq.txt"));
		TextDicFrame.loadTextDic(Config.getCurrentSntDir(),true);
		TextAutomatonFrame.showFrame();
		try {
			TextFrame.getFrame().setSelected(true);
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the text frame
	 *  
	 */
	public static void closeText() {
		TextFrame.hideFrame();
	}

	/**
	 * Remove all files related to the current ".snt" file
	 *  
	 */
	public static void removeSntFiles() {
		Config.removeFile(new File(Config.getCurrentSntDir(),"*"));
	}

	static class TextDo extends ToDoAbstract {
		File SNT;
		boolean b;

		public TextDo(File s,boolean taggedText) {
			SNT = s;
			b=taggedText;
		}
		public void toDo() {
			Text.loadSnt(SNT,b);
		}
	}

	static class TextConversionDo extends ToDoAbstract {
		File file;
		boolean b;

		public TextConversionDo(File s,boolean taggedText) {
			file = s;
			b=taggedText;
		}
		public void toDo() {
			if (Util.getFileNameExtension(file).equalsIgnoreCase("snt")) {
				Config.setCurrentSnt(file);
				loadSnt(file,b);
			} else {
				loadTxt(file,b);
			}
		}
	}

}