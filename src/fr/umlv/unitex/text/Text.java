/*
 * Unitex
 *
 * Copyright (C) 2001-2011 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package fr.umlv.unitex.text;

import java.io.File;

import javax.swing.JOptionPane;

import fr.umlv.unitex.Config;
import fr.umlv.unitex.Util;
import fr.umlv.unitex.frames.UnitexFrame;
import fr.umlv.unitex.io.Encoding;
import fr.umlv.unitex.process.Launcher;
import fr.umlv.unitex.process.ToDo;
import fr.umlv.unitex.process.commands.MkdirCommand;
import fr.umlv.unitex.process.commands.MultiCommands;
import fr.umlv.unitex.process.commands.NormalizeCommand;
import fr.umlv.unitex.process.commands.TokenizeCommand;
import fr.umlv.unitex.process.commands.UnxmlizeCommand;

/**
 * This class provides methods for loading corpora.
 * 
 * @author Sébastien Paumier
 */
public class Text {
	public static void loadCorpus(File name) {
		loadCorpus(name, false);
	}

	/**
	 * Loads a ".txt" or a ".snt" file
	 * 
	 * @param name
	 *            file name
	 */
	public static void loadCorpus(File name, boolean taggedText) {
		TextConversionDo toDo = new TextConversionDo(name, taggedText);
		Encoding e = Encoding.getEncoding(name);
		if (e == null) {
			UnitexFrame.getFrameManager().newTranscodeOneFileDialog(name, toDo);
		} else {
			toDo.toDo();
		}
	}

	/**
	 * Loads a ".txt" file. Asks for preprocessing the text.
	 * 
	 * @param file
	 *            file name
	 */
	static void loadTxt(File file, boolean taggedText) {
		String name = file.getAbsolutePath();
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
		Encoding e=Encoding.getEncoding(file);
		if (e==null) {
		JOptionPane.showMessageDialog(null, name
					+ " is not a Unicode Little-Endian text", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		String nomSnt = Util.getFileNameWithoutExtension(name);
		nomSnt = nomSnt + ".snt";
		Config.setCurrentSnt(new File(nomSnt));
		Object[] options = { "Yes", "No" };
		if (0 == JOptionPane.showOptionDialog(UnitexFrame.mainFrame,
				"Do you want to preprocess the text ?", "",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				options, options[0])) {
			preprocessSnt(file, Config.getCurrentSnt(), taggedText);
		} else {
			preprocessLightSnt(file, taggedText);
		}
	}

	private static void preprocessSnt(File name, File snt, boolean taggedText) {
		UnitexFrame.getFrameManager()
				.newPreprocessDialog(name, snt, taggedText);
	}

	private static void preprocessLightSnt(File name, boolean taggedText) {
		File dir = Config.getCurrentSntDir();
		MultiCommands commands = new MultiCommands();
		// NORMALIZING TEXT...
		NormalizeCommand normalizeCmd = new NormalizeCommand()
				.textWithDefaultNormalization(name);
		commands.addCommand(normalizeCmd);
		// creating snt dir
		MkdirCommand mkdir = new MkdirCommand().name(dir);
		commands.addCommand(mkdir);
		// TOKENIZING...
		TokenizeCommand tokenizeCmd = new TokenizeCommand().text(
				Config.getCurrentSnt()).alphabet(Config.getAlphabet());
		if (Config.getCurrentLanguage().equals("Thai")
				|| Config.getCurrentLanguage().equals("Chinese")) {
			tokenizeCmd = tokenizeCmd.tokenizeCharByChar();
		}
		commands.addCommand(tokenizeCmd);
		/*
		 * We have to close the text frame there, because if not, we will have
		 * problem when trying to close the .snt file that is mapped
		 */
		UnitexFrame.getFrameManager().closeTextFrame();
		Text.removeSntFiles();
		Launcher.exec(commands, true, new TextDo(Config.getCurrentSnt(),
				taggedText));
	}

	/**
	 * Loads a ".snt" file and all related files (token lists, dictionaries and
	 * text automaton)
	 * 
	 * @param snt
	 *            file name
	 */
	public static void loadSnt(File snt, boolean taggedText) {
		UnitexFrame.getFrameManager().newTextFrame(snt, taggedText);
	}

	/**
	 * Remove all files related to the current ".snt" file
	 */
	public static void removeSntFiles() {
		if (Config.getCurrentSntDir().exists()) {
			Config.removeFile(new File(Config.getCurrentSntDir(), "*"));
		}
	}

	static class TextDo implements ToDo {
		File SNT;
		boolean b;

		public TextDo(File s, boolean taggedText) {
			SNT = s;
			b = taggedText;
		}

		public void toDo() {
			Text.loadSnt(SNT, b);
		}
	}

	static class TextConversionDo implements ToDo {
		File file;
		boolean b;

		public TextConversionDo(File s, boolean taggedText) {
			file = s;
			b = taggedText;
		}

		public void toDo() {
			if (Util.getFileNameExtension(file).equalsIgnoreCase("snt")) {
				Config.setCurrentSnt(file);
				loadSnt(file, b);
			} else if (Util.getFileNameExtension(file).equalsIgnoreCase("xml")
					|| Util.getFileNameExtension(file).equalsIgnoreCase("html")) {
				loadXmlOrHtml(file);
			} else {
				/* txt file */
				loadTxt(file, b);
			}
		}
	}

	public static void loadXmlOrHtml(File file) {
		String name = file.getAbsolutePath();
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
		Encoding e=Encoding.getEncoding(file);
		if (e==null) {
			JOptionPane.showMessageDialog(null, name
					+ " is not a Unicode Little-Endian file", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		UnxmlizeCommand cmd = new UnxmlizeCommand().text(file);
		String s = Util.getFileNameWithoutExtension(file) + ".txt";
		Launcher.exec(cmd, true, new LoadTxtDo(new File(s)));
	}

	static class LoadTxtDo implements ToDo {
		File txt;

		public LoadTxtDo(File txt) {
			this.txt = txt;
		}

		public void toDo() {
			loadTxt(txt, false);
		}
	}
}
