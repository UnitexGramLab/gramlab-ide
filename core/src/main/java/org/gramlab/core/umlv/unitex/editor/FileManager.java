/*
 * Unitex
 *
 * Copyright (C) 2001-2016 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
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
package org.gramlab.core.umlv.unitex.editor;

import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.StringContent;

import org.gramlab.core.umlv.unitex.config.Config;
import org.gramlab.core.umlv.unitex.config.ConfigManager;
import org.gramlab.core.umlv.unitex.config.Preferences;
import org.gramlab.core.umlv.unitex.frames.FileEditionTextFrame;
import org.gramlab.core.umlv.unitex.frames.InternalFrameManager;
import org.gramlab.core.umlv.unitex.io.Encoding;
import org.gramlab.core.umlv.unitex.project.manager.UnitexProjectManager;

/**
 * This class provides methods for loading corpora
 */
public class FileManager {
	private Timer currentTimer = null;
	private FileEditionTextFrame fileEditionTextFrame;

	/**
	 * loads file and checks encoding type
	 * 
	 * @param file
	 *            the text file
	 */
	public void loadFile(File file) {
		if (file == null) {
			System.err.println("Internal error in FileManager.loadFile");
			return;
		}
		if (!file.exists()) {
			JOptionPane.showMessageDialog(null,
					"Cannot find " + file.getAbsolutePath(), "Error",
					ImageObserver.ERROR);
			return;
		}
		if (!file.canRead()) {
			JOptionPane.showMessageDialog(null,
					"Cannot read " + file.getAbsolutePath(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (file.length() <= 2) {
			JOptionPane.showMessageDialog(null, file.getAbsolutePath()
					+ " is empty", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		JOptionPane.showMessageDialog(null,
				"This is not necessarily the text being processed by Unitex",
				"Warning", JOptionPane.WARNING_MESSAGE);
		load(file);
	}

	/**
	 * loads a file from
	 * 
	 * @param file
	 *            file path
	 */
	private void load(File file) {
		fileEditionTextFrame = UnitexProjectManager.search(file)
				.getFrameManagerAs(InternalFrameManager.class)
				.newFileEditionTextFrame(file);
		final EditionTextArea text = fileEditionTextFrame.getText();
		if (file.length() <= 2) {
			text.setDocument(new PlainDocument());
			text.setText(Config.EMPTY_FILE_MESSAGE);
		} else if (file.length() < Preferences.MAX_TEXT_FILE_SIZE) {
			load(file, text);
			text.setCaretPosition(0);
		} else {
			text.setDocument(new PlainDocument());
			text.setText(Config.FILE_TOO_LARGE_MESSAGE);
		}
	}

	public void killTimer() {
		if (currentTimer != null) {
			currentTimer.stop();
			currentTimer = null;
		}
	}

	/**
	 * saves a file
	 * 
	 * @param absolutePath
	 *            the absolute path of the file
	 */
	public void save(String absolutePath) {
		final File file = new File(absolutePath);
		if (file.exists() && !file.canWrite()) {
			JOptionPane.showMessageDialog(null, "Unable to save "
					+ absolutePath + "\nbecause it is a read-only file! ",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			final FileEditionTextFrame fetf = UnitexProjectManager.search(null)
					.getFrameManagerAs(InternalFrameManager.class)
					.getSelectedFileEditionTextFrame();
			if (fetf == null)
				return;
			final EditionTextArea t = fetf.getText();
			final OutputStreamWriter osr = ConfigManager.getManager()
					.getEncoding(null).getOutputStreamWriter(file);
			final String content = t.getText();
			final int l = content.length();
			for (int i = 0; i < l; i++) {
				final char c = content.charAt(i);
				osr.write(c);
			}
			osr.flush();
			osr.close();
			t.setUnmodified();
		} catch (final IOException e) {
			JOptionPane.showMessageDialog(null, "Unable to save "
					+ absolutePath, "Error", JOptionPane.ERROR_MESSAGE);
		} catch (final ClassCastException e) {
			JOptionPane.showMessageDialog(null,
					"please, select a text edition frame", "Error",
					JOptionPane.ERROR_MESSAGE);
		} catch (final NullPointerException e) {
			JOptionPane.showMessageDialog(null,
					"please, select a text edition frame", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Load an little endian Unicode texte file put it in a document
	 * 
	 * @param file
	 *            the text to load
	 * @param textArea
	 *            aera where put the text
	 */
	public static Document load(File file, final EditionTextArea textArea)
			throws IllegalArgumentException {
		final StringContent content = new StringContent();
		final PlainDocument document = new PlainDocument(content);
		final String fileContent = Encoding.getContent(file);
		textArea.setDocument(document);
		try {
			document.insertString(0, fileContent, null);
		} catch (final BadLocationException e) {
			e.printStackTrace();
		}
		return document;
	}

	/**
	 * load an empty text
	 */
	public void newFile() {
		UnitexProjectManager.search(null)
				.getFrameManagerAs(InternalFrameManager.class)
				.newFileEditionTextFrame(null);
	}
}
